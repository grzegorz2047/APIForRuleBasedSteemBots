package pl.grzegorz2047.botapi;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.SimpleFileCreator;
import pl.grzegorz2047.botapi.bot.SteemConfigurer;
import pl.grzegorz2047.botapi.bot.BotUpVoterUsersFromList;
import pl.grzegorz2047.botapi.bot.UsersLoader;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;
import pl.grzegorz2047.botapi.configuration.GlobalConfigurationLoader;
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
    }

    void prepareUpVoterBot(SteemJ steemJ) {
        String botDataPath = "UpVoter";
        String globalFilePath = botDataPath + File.separator + "global.properties";
        Properties globalProperties = getGlobalProperties(globalFilePath);
        String usersFilePath = botDataPath + File.separator + "users.txt";
        createTxtFiles(usersFilePath);
        GlobalConfigData globalConfigData = new GlobalConfigData(globalProperties);
        IntervalParser intervalParser = new IntervalParser();

        IntervalHandler globalIntervalHandler = getIntervalHandler(globalConfigData, intervalParser);

        HashMap<String, User> users = new HashMap<String, User>();
        try {
            List<String> votingTags = new VotingTagsParser().getVotingTags(globalProperties);
            users = new UsersLoader().getUsers(globalIntervalHandler, votingTags, usersFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);

        }

        if (users.isEmpty()) {
            System.out.println("No users added! Bot cannot continue working! Turning off!");
            System.exit(0);
        }

        SteemJConfig steemConfig = SteemConfigurer.configureSteemJ(globalConfigData.getBotName(), globalConfigData.getPostingKey());
        AccountName botAccount = steemConfig.getDefaultAccount();

        boolean isMaxOneVotePerCertainTime = globalConfigData.isMaxOneVote();
        String commentTags = globalConfigData.getCommentTags();
        String[] listOfCommentTags = commentTags.split(",");

        BotUpVoterUsersFromList botUpVoterUsersFromList = new BotUpVoterUsersFromList(steemJ, botAccount);
        List<BotAction> botActions = new LinkedList<>();
        List<HelpInformation> botFeed = new LinkedList<>();
        HashMap<String, Argument> arguments = new HashMap<>();

        botUpVoterUsersFromList.init(users, botFeed, botActions, arguments);
        LinkedList<String> allRequiredKeyProperties = botUpVoterUsersFromList.getAllRequiredKeyProperties();
        System.out.println("All required keyProperties: " + Arrays.toString(allRequiredKeyProperties.toArray()));
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

    private IntervalHandler getIntervalHandler(GlobalConfigData globalConfigData, IntervalParser intervalParser) {
        IntervalHandler globalIntervalHandler = new IntervalHandler(intervalParser.parse(globalConfigData.getGlobalIntervals()));
        System.out.println("parsuje " + globalConfigData.getGlobalIntervals());
        float votingStrengthPercentageBasedOnCurrentVotingPower = globalIntervalHandler.getVotingStrengthPercentageBasedOnCurrentVotingPower(77);
        System.out.println("loaded intervals: " + globalIntervalHandler.getIntervals().size());
        System.out.println("Vote power for 77 is " + votingStrengthPercentageBasedOnCurrentVotingPower);
        return globalIntervalHandler;
    }
}