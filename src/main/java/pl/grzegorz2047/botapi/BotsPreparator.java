package pl.grzegorz2047.botapi;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import pl.grzegorz2047.botapi.bot.*;
import pl.grzegorz2047.botapi.bot.actions.CommentAction;
import pl.grzegorz2047.botapi.bot.actions.UpVoteAction;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.helpinformations.CurrentVotingBotCapabilitiesInformation;
import pl.grzegorz2047.botapi.bot.helpinformations.NewestPostInTagInformation;
import pl.grzegorz2047.botapi.bot.helpinformations.NewestUserPostInformation;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;
import pl.grzegorz2047.botapi.bot.rules.NeverVotedBeforeOnPostRule;
import pl.grzegorz2047.botapi.configuration.GlobalConfigurationLoader;
import pl.grzegorz2047.botapi.interval.Interval;
import pl.grzegorz2047.botapi.interval.IntervalHandler;
import pl.grzegorz2047.botapi.interval.IntervalParser;
import pl.grzegorz2047.botapi.parsers.VotingTagsParser;
import pl.grzegorz2047.botapi.user.User;
import pl.grzegorz2047.botapi.user.exception.PropertiesNotFound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BotsPreparator {

    void runAllBots() throws SteemResponseException, SteemCommunicationException {
        prepareUpVoterBot(new SteemJ());
        prepareRandomUpVoterBot(new SteemJ());
    }

    private void prepareUpVoterBot(SteemJ steemJ) {
        String botDataPath = "UpVoter";
        String globalFilePath = botDataPath + File.separator + "global.properties";
        Properties globalProperties = getGlobalProperties(globalFilePath);
        String usersFilePath = botDataPath + File.separator + "users.txt";
        createTxtFiles(usersFilePath);
        IntervalParser intervalParser = new IntervalParser();


        String votingTagsStr = globalProperties.getProperty("votingTags");
        String botName = globalProperties.getProperty("botName");
        String postingKey = globalProperties.getProperty("postingKey");
        boolean commentingEnabled = Boolean.parseBoolean(globalProperties.getProperty("commentingEnabled"));
        String commentMessage = globalProperties.getProperty("message");
        String commentTagsString = globalProperties.getProperty("votingTags");
        boolean debugMode = Boolean.parseBoolean(globalProperties.getProperty("debug"));
        boolean votingEnabled = Boolean.parseBoolean(globalProperties.getProperty("votingEnabled"));
        Integer votingPower = Integer.valueOf(globalProperties.getProperty("votingPower"));
        Long frequenceCheckInMilliseconds = Long.valueOf(globalProperties.getProperty("frequenceCheckInMilliseconds"));
        Integer howDeepToCheckIfFirstPost = Integer.valueOf(globalProperties.getProperty("howDeepToCheckIfFirstPost"));
        boolean checkTags = Boolean.parseBoolean(globalProperties.getProperty("checkTags"));
        boolean reblogEnabled = Boolean.parseBoolean(globalProperties.getProperty("reblogEnabled"));
        boolean intervalEnabled = Boolean.parseBoolean(globalProperties.getProperty("intervalsEnabled"));
        int votingPowerLimit = Integer.parseInt(globalProperties.getProperty("votingPowerLimit"));
        String globalIntervals = globalProperties.getProperty("intervals");
        boolean maxOneVote = Boolean.parseBoolean(globalProperties.getProperty("maxOneVoteForUserPerDay"));


        IntervalHandler globalIntervalHandler = getIntervalHandler(intervalParser, globalIntervals);

        HashMap<String, User> users = new HashMap<String, User>();
        try {
            List<String> votingTags = new VotingTagsParser().getVotingTags(globalProperties.getProperty("votingTags"));
            users = new UsersLoader().getUsers(globalIntervalHandler, votingTags, usersFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);

        }

        if (users.isEmpty()) {
            System.out.println("No users added! Bot cannot continue working! Turning off!");
            System.exit(0);
        }

        SteemJConfig steemConfig = SteemConfigurer.configureSteemJ(botName, postingKey);
        AccountName botAccount = steemConfig.getDefaultAccount();

        String[] listOfCommentTags = commentTagsString.split(",");

        BotUpVoterUsersFromList botUpVoterUsersFromList = new BotUpVoterUsersFromList(steemJ, botAccount);
        List<BotAction> botActions = new LinkedList<>();
        UpVoteAction upVoteAction = new UpVoteAction();
        upVoteAction.addRule("votedbefore", new NeverVotedBeforeOnPostRule());
        botActions.add(upVoteAction);
        List<HelpInformation> botFeed = new LinkedList<>();
        botFeed.add(new NewestUserPostInformation());
        HashMap<String, Argument> arguments = new HashMap<>();

        botUpVoterUsersFromList.init(users, botFeed, botActions, arguments);
        LinkedList<String> allRequiredKeyProperties = botUpVoterUsersFromList.getAllRequiredKeyProperties();
        System.out.println("All required keyProperties: " + Arrays.toString(allRequiredKeyProperties.toArray()));
    }

    private void prepareRandomUpVoterBot(SteemJ steemJ) {
        String botDataPath = "RandomCurator";
        String globalFilePath = botDataPath + File.separator + "global.properties";
        Properties globalProperties = getGlobalProperties(globalFilePath);
        String usersFilePath = botDataPath + File.separator + "users.txt";
        createTxtFiles(usersFilePath);
        LinkedList<String> allRequiredKeyProperties = new LinkedList<>(Arrays.asList("botName", "postingKey", "intervals"));

        String votingTagsStr = globalProperties.getProperty("votingTags");
        String botName = globalProperties.getProperty("botName");
        String postingKey = globalProperties.getProperty("postingKey");
        String frequenceCheckInMilliseconds = globalProperties.getProperty("frequenceCheckInMilliseconds");
        String globalIntervals = globalProperties.getProperty("intervals");


        IntervalParser intervalParser = new IntervalParser();

        IntervalHandler globalIntervalHandler = getIntervalHandler(intervalParser, globalIntervals);

        HashMap<String, User> users = new HashMap<String, User>();
        try {
            List<String> votingTags = new VotingTagsParser().getVotingTags(votingTagsStr);
            users = new UsersLoader().getUsers(globalIntervalHandler, votingTags, usersFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);

        }

        if (users.isEmpty()) {
            System.out.println("No users added! Bot cannot continue working! Turning off!");
            System.exit(0);
        }

        SteemJConfig steemConfig = SteemConfigurer.configureSteemJ(botName, postingKey);
        AccountName botAccount = steemConfig.getDefaultAccount();

        List<BotAction> botActions = new LinkedList<>();
        List<HelpInformation> botFeed = new LinkedList<>();
        BotRandomUpvoter botRandomUpvoter = new BotRandomUpvoter(steemJ, botAccount);
        UpVoteAction upVoteAction = new UpVoteAction();

        HashMap<String, Argument> arguments = loadArgumentsFromProperties(globalProperties);

        upVoteAction.addRule("votedbefore", new NeverVotedBeforeOnPostRule());
        botActions.add(upVoteAction);
        botActions.add(new CommentAction());

        botFeed.add(new NewestPostInTagInformation());
        botFeed.add(new CurrentVotingBotCapabilitiesInformation());

        botRandomUpvoter.init(users, botFeed, botActions, arguments);
        allRequiredKeyProperties.removeAll(botRandomUpvoter.getAllRequiredKeyProperties());
        allRequiredKeyProperties.addAll(botRandomUpvoter.getAllRequiredKeyProperties());
        Set<Object> loadedKeyProperties = globalProperties.keySet();
        LinkedList<String> loadedKeys = getGlobalPropertiesKeys(loadedKeyProperties);
        if (!loadedKeys.containsAll(allRequiredKeyProperties)) {
            System.out.println("All required keyProperties not fullfilled! Your loaded properties: " + Arrays.toString(loadedKeyProperties.toArray()) + " To run this bot you need this data: " + Arrays.toString(allRequiredKeyProperties.toArray()));
            return;
        }
        System.out.println("Loaded keys in global properties" + Arrays.toString(loadedKeys.toArray()));

        botRandomUpvoter.start();
    }

    private HashMap<String, Argument> loadArgumentsFromProperties(Properties globalProperties) {
        HashMap<String, Argument> arguments = new HashMap<>();

        for (Object objKey : globalProperties.keySet()) {
            String key = (String) objKey;
            System.out.println("loading property " + key + " as argument");
            arguments.put(key, new Argument(globalProperties.getProperty(key)));
        }
        return arguments;
    }

    private LinkedList<String> getGlobalPropertiesKeys(Set<Object> loadedKeyProperties) {
        LinkedList<String> loadedKeys = new LinkedList<>();
        for (Object k : loadedKeyProperties) {
            String key = (String) k;
            loadedKeys.add(key);
        }
        return loadedKeys;
    }

    private Properties getGlobalProperties(String globalFilePath) {
        Properties globalProperties = null;
        GlobalConfigurationLoader propertiesLoader = new GlobalConfigurationLoader(globalFilePath);
        try {
            if (!propertiesLoader.prepareGlobalProperties()) {
                System.out.println("File global.properties generated! configure bot before you run a bot!");
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("There is an error while creating global.properties file! Check you permissions!");
            System.exit(1);
        }
        try {
            globalProperties = propertiesLoader.getGlobalProperties();
        } catch (PropertiesNotFound propertiesNotFound) {
            System.out.println("There is an error while getting informations from global.properties file! Check you permissions!");
            propertiesNotFound.printStackTrace();
            System.exit(1);
        }
        return globalProperties;
    }

    private void createTxtFiles(String usersFilePath) {
        SimpleFileCreator simpleFileCreator = new SimpleFileCreator();
        Path usersPath = Paths.get(usersFilePath);
        try {
            if (simpleFileCreator.createFile(usersPath)) {
                System.out.println(usersFilePath + " generated! Add users before you run a bot!");
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println(usersPath + " cannot be created! Check if you have proper permissions!");
            System.exit(1);
        }
    }

    private IntervalHandler getIntervalHandler(IntervalParser intervalParser, String globalIntervals) {
        IntervalHandler globalIntervalHandler = new IntervalHandler(intervalParser.parse(globalIntervals));
        System.out.println("parsuje " + globalIntervals);
        float votingStrengthPercentageBasedOnCurrentVotingPower = globalIntervalHandler.getVotingStrengthPercentageBasedOnCurrentVotingPower(77);
        List<Interval> intervals = globalIntervalHandler.getIntervals();
        System.out.println("loaded intervals: " + intervals.size());
        System.out.println("Vote power for 77 is " + votingStrengthPercentageBasedOnCurrentVotingPower);
        return globalIntervalHandler;
    }

    public SteemJConfig createSteemConfig(String botName, String postingKey) {
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
}