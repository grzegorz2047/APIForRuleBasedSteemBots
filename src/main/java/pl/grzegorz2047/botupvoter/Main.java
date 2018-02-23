package pl.grzegorz2047.botupvoter;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.base.models.*;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.grzegorz2047.botupvoter.bot.BotActions;
import pl.grzegorz2047.botupvoter.configuration.GlobalConfigurationLoader;
import pl.grzegorz2047.botupvoter.interval.Interval;
import pl.grzegorz2047.botupvoter.interval.IntervalHandler;
import pl.grzegorz2047.botupvoter.interval.IntervalParser;
import pl.grzegorz2047.botupvoter.user.User;
import pl.grzegorz2047.botupvoter.user.UserDataLoader;
import pl.grzegorz2047.botupvoter.user.exception.PropertiesNotFound;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) throws SteemResponseException, SteemCommunicationException {
        Properties globalProperties;
        GlobalConfigurationLoader properties = new GlobalConfigurationLoader();
        try {
            if (!properties.prepareGlobalProperties()) {
                System.out.println("File global.properties generated! configure bot before you run a bot!");
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("There is an erro while creating global.properties file! Check you permissions!");
            System.exit(1);
            return;
        }
        try {
            globalProperties = properties.getGlobalProperties();
        } catch (PropertiesNotFound propertiesNotFound) {
            System.out.println("There is an error while getting informations from global.properties file! Check you permissions!");
            propertiesNotFound.printStackTrace();
            System.exit(1);
            return;
        }
        GlobalConfigData globalConfigData = new GlobalConfigData(globalProperties);
        IntervalParser intervalParser = new IntervalParser();
        Path usersPath = Paths.get("users.txt");
        if (createFile(usersPath)) return;
        Path logsPath = Paths.get("bot.log");
        if (createFile(logsPath)) return;
        IntervalHandler globalIntervalHandler = new IntervalHandler(intervalParser.parse(globalConfigData.getGlobalIntervals()));
        System.out.println("parsuje " + globalConfigData.getGlobalIntervals());
        float votingStrengthPercentageBasedOnCurrentVotingPower = globalIntervalHandler.getVotingStrengthPercentageBasedOnCurrentVotingPower(77);
        System.out.println("Liczba " + globalIntervalHandler.getIntervals().size());
        System.out.println("Moc glosu dla 77 to " + votingStrengthPercentageBasedOnCurrentVotingPower);

        HashMap<String, User> users = new HashMap<>();
        try {
            users = getUsers(globalIntervalHandler, VotingTagsParser.getVotingTags(globalProperties), usersPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        if (users.isEmpty()) {
            System.out.println("No users added! Bot cannot continue working! Turning off!");
            System.exit(0);
            return;
        }

        SteemJConfig steemConfig = createSteemConfig(globalConfigData.getBotName(), globalConfigData.getPostingKey());
        AccountName botAccount = steemConfig.getDefaultAccount();
        SteemJ steemJ;
        try {
            steemJ = new SteemJ();
        } catch (SteemCommunicationException e) {
            System.out.println("Connection to STEEM refused!");
            e.printStackTrace();
            System.exit(1);
            return;
        } catch (SteemResponseException e) {
            System.out.println("STEEM doesnt respond");
            e.printStackTrace();
            System.exit(1);
            return;
        }
        boolean isMaxOneVotePerCertainTime = globalConfigData.isMaxOneVote();
        String commentTags = globalConfigData.getCommentTags();
        String[] listOfCommentTags = commentTags.split(",");
        runBot(steemJ, users, globalConfigData.getFrequenceCheckInMilliseconds(), botAccount, globalConfigData.isCheckTagsEnabled(), isMaxOneVotePerCertainTime, globalConfigData.getCommentMessage(), listOfCommentTags, globalConfigData.isCOmmentingEnabled());
    }

    private static boolean createFile(Path usersPath) {
        if (!Files.exists(usersPath)) {
            try {
                Files.createFile(usersPath);
            } catch (IOException e) {
                System.out.println("File users.txt cannot be created! Check you persmissions!");
                e.printStackTrace();
                System.exit(1);
                return true;
            }
            System.out.println("File users.txt generated! add users before you run a bot!");
            System.exit(0);
            return true;
        }
        return false;
    }


    private static SteemJConfig createSteemConfig(String botName, String postingKey) {
        SteemJConfig steemJConfig = SteemJConfig.getInstance();
        steemJConfig.setResponseTimeout(100000);
        AccountName botAccount = new AccountName(botName);
        steemJConfig.setDefaultAccount(botAccount);

        steemJConfig.setSteemJWeight((short) 0);//https://github.com/marvin-we/steem-java-api-wrapper/blob/126c907c4d136d38d4e805153aae1457f0a8f5e6/core/src/main/java/eu/bittrade/libs/steemj/SteemJ.java#L3018 ????
        List<ImmutablePair<PrivateKeyType, String>> privateKeys = new ArrayList<>();
        privateKeys.add(new ImmutablePair<>(PrivateKeyType.POSTING, postingKey));

        steemJConfig.getPrivateKeyStorage().addAccount(botAccount, privateKeys);
        return steemJConfig;
    }


    private static void runBot(SteemJ steemJ, HashMap<String, User> users, Long frequenceCheckInMilliseconds, AccountName botAccount, boolean checkTags, boolean isMaxOneVotePerCertainTime, String message, String[] commentTags, boolean commentEnabled) {
        List<AccountName> steemUsernames = addAccountsToProcess(users);
        BotActions bot = new BotActions();
        while (true) {
            try {
                List<ExtendedAccount> accounts = steemJ.getAccounts(Collections.singletonList(botAccount));
                if (accounts.size() == 0) {
                    System.out.println("Bot accound doesnt exist!?");
                    System.exit(1);
                    return;
                }
                ExtendedAccount extendedBotAccount = accounts.get(0);
                AccountName accountName = extendedBotAccount.getName();
                List<ExtendedAccount> filledAccounts = steemJ.getAccounts(steemUsernames);
                for (ExtendedAccount userActivityAccount : filledAccounts) {
                    AccountName userAccount = userActivityAccount.getName();
                    String userAccountName = userAccount.getName();
                    User user = users.get(userAccountName);
                    Permlink newestPermlink;
                    try {
                        newestPermlink = BotActions.getNewestDiscusion(steemJ, userAccount, user.getFollowingTags());
                        //System.out.println("received newest discussion from " + userAccountName);
                        //System.out.println("permlink " + newestPermlink);
                    } catch (Exception ex) {
                        System.out.println("Cant find post for " + userAccountName);
                        continue;
                    }
                    Discussion content = steemJ.getContent(userAccount, newestPermlink);

                    List<VoteState> activeVotes = content.getActiveVotes();
                    if (checkTags) {
                        if (!hasPostValidTag(user, content)) continue;
                    }
//                    List<VoteState> activeVotes = steemJ.getActiveVotes(userAccount, newestPermlink);
                    boolean botVotedOnThisPost = BotActions.hasBotVotedOnThisPost(accountName, activeVotes);
                    if (botVotedOnThisPost) {
                        continue;
                    }

                    System.out.println("Checking account " + userAccountName);
                    String lastUserPost = BotActions.getCurrentSeenUsersPosts().get(userAccountName);
                    String permlinkText = newestPermlink.getLink();
                    if (BotActions.alreadyPosted(lastUserPost, permlinkText)) continue;
                    if (isMaxOneVotePerCertainTime) {
                        if (!canBeVoted(userAccountName)) continue;
                    }

                    BotActions.getLastVotedUser().put(userAccountName, System.currentTimeMillis());
                    System.out.println("Voting on " + userAccountName);

                    float votingPower = extendedBotAccount.getVotingPower() / 100;
                    System.out.println("What votingStrength will be for " + votingPower + "?");
                    short votingStrength = user.getVotingStrength(votingPower);
                    System.out.println("I received " + votingStrength + " voting strength!");
                    try {
                        BotActions.vote(steemJ, botAccount, userAccount, userAccountName, newestPermlink, permlinkText, votingStrength);
                    } catch (SteemInvalidTransactionException e) {
                        System.out.println("Errow while responding action toward " + userAccountName + " on steem waiting!");
                        continue;
                    }
                    if (commentEnabled) {
                        message = message.replaceAll("<author>", userAccountName);
                        try {
                            BotActions.comment(steemJ, botAccount, message, commentTags, userAccount, newestPermlink);
                        } catch (SteemInvalidTransactionException e) {
                            System.out.println("Errow while responding action toward " + userAccountName + " on steem waiting!");
                            continue;
                        }
                    }
                 }
                Thread.sleep(frequenceCheckInMilliseconds);
            } catch (InterruptedException ex) {
                try {
                    System.out.println("Errow while responding to steem waiting!");
                    Thread.sleep(frequenceCheckInMilliseconds);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (SteemResponseException | SteemCommunicationException e) {
                try {
                    System.out.println("Errow while communicationg with steem@ waiting!");
                    Thread.sleep(frequenceCheckInMilliseconds);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static boolean canBeVoted(String userAccountName) {
        long lastVoteTime = BotActions.getLastVotedUser().get(userAccountName);
        int h24 = 1000 * 60 * 60 * 24;
        return !isAfterCertainTime(lastVoteTime, h24);
    }

    private static List<AccountName> addAccountsToProcess(HashMap<String, User> users) {
        List<AccountName> steemUsernames = new ArrayList<>();
        for (String username : users.keySet()) {
            System.out.println("Adding " + username + " to checkList");
            steemUsernames.add(new AccountName(username));
        }
        return steemUsernames;
    }

    private static boolean hasPostValidTag(User user, Discussion content) {
        String jsonMetadata = content.getJsonMetadata();
        //System.out.println("Discussion " + content.getTitle());
        JSONObject jsonObject = new JSONObject(jsonMetadata);
        JSONArray jsonArray = (JSONArray) jsonObject.get("tags");
        boolean isEligiblePost = isEligiblePost(user, jsonArray);
        if (isEligiblePost) {
            return false;
        }
        return true;
    }

    private static boolean isAfterCertainTime(Long lastVoteTime, int h24) {
        if (lastVoteTime != null) {
            long now = System.currentTimeMillis();
            long diff = now - lastVoteTime;
            if (diff < h24) {
                System.out.println("less than 24h@");
                return true;
            }
        }
        return false;
    }

    public static void writeLog(String fileName, String textToWrite) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            out.println(textToWrite);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private static boolean isEligiblePost(User user, JSONArray tags) {
        boolean isEligiblePost = false;
        for (int i = 0; i < tags.length(); i++) {
            if (user.getFollowingTags().contains(tags.getString(i))) {
                isEligiblePost = true;
            }
        }
        return isEligiblePost;
    }


    private static HashMap<String, User> getUsers(IntervalHandler globalIntervals, List<String> globalVotingTags, Path usersPath) throws Exception {
        HashMap<String, User> userList = new HashMap<String, User>();
        Stream<String> lines = getUsersListStream(usersPath);
        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            String username = iterator.next();
            System.out.println("Loading " + username);
            Properties userProperties = null;
            List<String> votingTags;
            try {
                UserDataLoader userDataLoader = new UserDataLoader(username);
                userProperties = userDataLoader.getUserProperties();
                votingTags = VotingTagsParser.getVotingTags(userProperties);
            } catch (PropertiesNotFound propertiesNotFound) {
                votingTags = new ArrayList<>();
            }
            IntervalHandler userIntervalHandler;
            if (userProperties != null) {
                String intervalsString = userProperties.getProperty("intervals");
                IntervalParser intervalParser = new IntervalParser();
                List<Interval> parsedIntervalsList = intervalParser.parse(intervalsString);
                if (parsedIntervalsList.isEmpty()) {
                    userIntervalHandler = globalIntervals;
                    System.out.println("User " + username + " is using global intervals!");
                } else {
                    userIntervalHandler = new IntervalHandler(parsedIntervalsList);
                }
                if (votingTags.isEmpty()) {
                    votingTags = globalVotingTags;
                    if (votingTags.isEmpty()) {
                        throw new Exception("No voting tags define in global properties and in user properties");
                    } else {
                        System.out.println("User " + username + " is using global voting tags!");
                    }
                }
            } else {
                userIntervalHandler = globalIntervals;
                votingTags = globalVotingTags;
                System.out.println("User " + username + " is using global intervals!");
                System.out.println("User " + username + " is using global votingTags!");
            }

            User user = new User(username, userIntervalHandler, votingTags);
            userList.put(username, user);
        }
        return userList;
    }

    private static Stream<String> getUsersListStream(Path usersPath) throws Exception {
        Stream<String> lines;
        try {
            lines = Files.lines(usersPath);
        } catch (IOException e) {
            String exceptionMessage = "There is an error while getting informations from users.txt file! Check you permissions!";
            System.out.println(exceptionMessage);
            System.exit(1);
            throw new Exception(exceptionMessage);
        }
        return lines;
    }

    private static boolean fileExists(Path pathToFile) {
        return Files.exists(pathToFile);
    }


}
