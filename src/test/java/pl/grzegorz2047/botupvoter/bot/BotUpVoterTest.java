package pl.grzegorz2047.botupvoter.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import pl.grzegorz2047.botupvoter.bot.argument.Argument;
import pl.grzegorz2047.botupvoter.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BotUpVoterTest {

    private BotUpVoter botUpVoter;

    private List<BotRule> botRules = new LinkedList<>();
    private List<BotAction> botActions = new LinkedList<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Argument> arguments = new HashMap<>();

    private SteemJConfig createSteemConfig(String botName, String postingKey) {
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

    @Before
    void setUp() throws Exception {
        botUpVoter = new BotUpVoter();
        boolean on = botUpVoter.init(users, botRules, botActions, arguments);
        if (!on) {
            throw new Exception("error");
        }
        SteemJ steemJ = new SteemJ();
        SteemJConfig steemConfig = createSteemConfig("kumpel", "posting key");

        BotCommentAction commentAction = new BotCommentAction(steemJ, steemConfig.getDefaultAccount());
        commentAction.addRule("neverpostedbefore", new NeverVotedBeforeRule());
        botActions.add(commentAction);
    }

    @Test
    void init() {
        boolean on = botUpVoter.init(users, botRules, botActions, arguments);
        assertThat(on).isTrue();
    }

    @Test
    void shutdown() {
        boolean off = botUpVoter.shutdown();
        assertThat(off).isTrue();
    }

    @Test
    void checkStatus() {
        botUpVoter = new BotUpVoter();
        boolean off = botUpVoter.checkStatus();
        assertThat(off).isFalse();
        boolean on = botUpVoter.init(users, botRules, botActions, arguments);
        assertThat(on).isTrue();
    }

    @Test
    void runBot() throws SteemResponseException, SteemCommunicationException {
        botUpVoter.runBot();
        botUpVoter.shutdown();
    }
}