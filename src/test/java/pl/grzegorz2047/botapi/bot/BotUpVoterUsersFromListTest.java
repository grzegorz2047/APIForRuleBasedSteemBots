package pl.grzegorz2047.botapi.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.junit.Before;
import org.junit.Test;
import pl.grzegorz2047.botapi.bot.actions.CommentAction;
import pl.grzegorz2047.botapi.bot.actions.UpVoteAction;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.exceptions.BotNotInitialisedException;
import pl.grzegorz2047.botapi.bot.helpinformations.NewestUserPostInformation;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;
import pl.grzegorz2047.botapi.bot.rules.NeverVotedBeforeOnPostRule;
import pl.grzegorz2047.botapi.interval.IntervalHandler;
import pl.grzegorz2047.botapi.user.User;

import java.util.*;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class BotUpVoterUsersFromListTest {

    private final SteemConfigurer steemConfigurer = new SteemConfigurer();
    private BotUpVoterUsersFromList botUpVoterUsersFromList;
    private List<BotAction> botActions = new LinkedList<>();
    private List<HelpInformation> botFeed = new LinkedList<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Argument> arguments = new HashMap<>();
    private final SteemJConfig steemConfig = SteemConfigurer.configureSteemJ("fajter", "5JjRX3s46K8P7PYcqmcu9KCMjGQJcaaD6hAV9LYM2x36zVQPCY3");
    private final SteemJ steemJ = new SteemJ();

    public BotUpVoterUsersFromListTest() throws SteemResponseException, SteemCommunicationException {
    }


    @Before
    public void setUp() throws Exception {
        botUpVoterUsersFromList = new BotUpVoterUsersFromList(steemJ, steemConfig.getDefaultAccount());
        users.put("fajter", new User("fajter", new IntervalHandler(new ArrayList<>()), Collections.singletonList("dontblameme")));
        botFeed.add(new NewestUserPostInformation());
        UpVoteAction upVoteAction = new UpVoteAction();
        upVoteAction.addRule("didvotebefore", new NeverVotedBeforeOnPostRule());

        botActions.add(upVoteAction);
        CommentAction commentAction = new CommentAction();
        commentAction.addRule("didpostbefore", new NeverVotedBeforeOnPostRule());
        botActions.add(commentAction);

        boolean on = botUpVoterUsersFromList.init(users, botFeed, botActions, arguments);
        if (!on) {
            throw new Exception("error");
        }
    }

    @Test
    public void init() {
        boolean on = botUpVoterUsersFromList.init(users, botFeed, botActions, arguments);
        assertThat(on).isTrue();
    }

    @Test
    public void shutdown() {
        boolean off = botUpVoterUsersFromList.shutdown();
        assertThat(off).isTrue();
    }

    @Test
    public void checkStatus() throws SteemResponseException, SteemCommunicationException {
        botUpVoterUsersFromList = new BotUpVoterUsersFromList(steemJ, steemConfig.getDefaultAccount());
        boolean off = botUpVoterUsersFromList.checkStatus();
        assertThat(off).isFalse();
        boolean on = botUpVoterUsersFromList.init(users, botFeed, botActions, arguments);
        assertThat(on).isTrue();
    }

    @Test
    public void runBot() throws InterruptedException {
        System.out.println(Arrays.toString(botUpVoterUsersFromList.getAllRequiredKeyProperties().toArray()));
        arguments.put("commentMessage", new Argument("YO MAN!"));
        arguments.put("permlink", new Argument("hello-world"));
        arguments.put("votingTags", new Argument("dontblameme"));
        arguments.put("authorToCommentOn", new Argument("fajter"));
        arguments.put("userAccount", new Argument("fajter"));
        arguments.put("botName", new Argument("fajter"));
        arguments.put("votingStrength", new Argument("100"));
        arguments.put("votedBefore", new Argument(true));
        botUpVoterUsersFromList.start();
        sleep(1000 * 20);
        //botUpVoterUsersFromList.shutdown();
    }

    @Test
    public void printAllActions() throws BotNotInitialisedException {
        botUpVoterUsersFromList.printAllActions();
        botActions.clear();
    }
}