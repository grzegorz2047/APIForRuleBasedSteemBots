package pl.grzegorz2047.botupvoter.bot;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BotUpVoterTest {

    private BotUpVoter botUpVoter;

    private List<BotRule> botRules = new LinkedList<>();
    private List<BotAction> botActions = new LinkedList<>();

    @Before
    void setUp() {
        botUpVoter = new BotUpVoter(botRules, botActions);
    }

    @Test
    void init() {
        boolean on = botUpVoter.init();
        assertThat(on).isTrue();
    }

    @Test
    void shutdown() {
        boolean off = botUpVoter.shutdown();
        assertThat(off).isTrue();
    }

    @Test
    void checkStatus() {
        botUpVoter = new BotUpVoter(botRules, botActions);
        boolean off = botUpVoter.checkStatus();
        assertThat(off).isFalse();
        boolean on = botUpVoter.init();
        assertThat(on).isTrue();
    }

}