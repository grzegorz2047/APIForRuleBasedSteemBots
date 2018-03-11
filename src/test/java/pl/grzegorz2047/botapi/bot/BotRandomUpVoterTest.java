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
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.rules.NeverVotedBeforeRule;
import pl.grzegorz2047.botapi.interval.IntervalHandler;
import pl.grzegorz2047.botapi.user.User;

import java.util.*;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class BotRandomUpVoterTest {

    private final SteemConfigurer steemConfigurer = new SteemConfigurer();
    private BotRandomUpVoter botRandomUpVoter;
    private List<BotAction> botActions = new LinkedList<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Argument> arguments = new HashMap<>();
    private final SteemJConfig steemConfig = SteemConfigurer.configureSteemJ("fajter", "5JjRX3s46K8P7PYcqmcu9KCMjGQJcaaD6hAV9LYM2x36zVQPCY3");
    private final SteemJ steemJ = new SteemJ();

    public BotRandomUpVoterTest() throws SteemResponseException, SteemCommunicationException {
    }


    @Before
    public void setUp() throws Exception {
        botRandomUpVoter = new BotRandomUpVoter(steemJ, steemConfig.getDefaultAccount());
        users.put("fajter", new User("fajter", new IntervalHandler(new ArrayList<>()), Collections.singletonList("dontblameme")));
        boolean on = botRandomUpVoter.init(users, botActions, arguments);
        if (!on) {
            throw new Exception("error");
        }
        UpVoteAction upVoteAction = new UpVoteAction();
        botActions.add(upVoteAction);
        CommentAction commentAction = new CommentAction(steemJ, steemConfig.getDefaultAccount());
        commentAction.addRule("neverpostedbefore", new NeverVotedBeforeRule());
        botActions.add(commentAction);
    }

    @Test
    public void init() {
        boolean on = botRandomUpVoter.init(users, botActions, arguments);
        assertThat(on).isTrue();
    }

    @Test
    public void shutdown() {
        boolean off = botRandomUpVoter.shutdown();
        assertThat(off).isTrue();
    }

    @Test
    public void checkStatus() throws SteemResponseException, SteemCommunicationException {
        botRandomUpVoter = new BotRandomUpVoter(steemJ, steemConfig.getDefaultAccount());
        boolean off = botRandomUpVoter.checkStatus();
        assertThat(off).isFalse();
        boolean on = botRandomUpVoter.init(users, botActions, arguments);
        assertThat(on).isTrue();
    }

    @Test
    public void runBot() throws InterruptedException {
        System.out.println(Arrays.toString(botRandomUpVoter.getAllRequiredKeyProperties().toArray()));
        arguments.put("commentMessage", new Argument("YO MAN!"));
        arguments.put("permlink", new Argument("hello-world"));
        arguments.put("commentTags", new Argument("dontblameme"));
        arguments.put("authorToCommentOn", new Argument("fajter"));
        arguments.put("userAccount", new Argument("fajter"));
        arguments.put("botAccount", new Argument("fajter"));
        arguments.put("votingStrength", new Argument("100"));
        botRandomUpVoter.start();
        sleep(1000 * 30);
        //botRandomUpVoter.shutdown();
    }

    @Test
    public void printAllActions() throws BotNotInitialisedException {
        botRandomUpVoter.printAllActions();
        botActions.clear();
    }
}