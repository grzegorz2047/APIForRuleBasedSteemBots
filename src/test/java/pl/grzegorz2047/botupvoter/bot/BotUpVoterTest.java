package pl.grzegorz2047.botupvoter.bot;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import pl.grzegorz2047.botupvoter.bot.argument.Argument;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BotUpVoterTest {

    private BotUpVoter botUpVoter;

    private List<BotRule> botRules = new LinkedList<>();
    private List<BotAction> botActions = new LinkedList<>();
    private HashMap<String, Argument> arguments = new HashMap<>();

    @Before
    void setUp() {
        botUpVoter = new BotUpVoter();
    }

    @Test
    void init() {
        boolean on = botUpVoter.init(botRules, botActions, arguments);
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
        boolean on = botUpVoter.init(botRules, botActions, arguments);
        assertThat(on).isTrue();
    }

}