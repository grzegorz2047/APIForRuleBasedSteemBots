package pl.grzegorz2047.botapi.bot;

import org.junit.jupiter.api.Test;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.rules.NeverVotedBeforeRule;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class NeverVotedBeforeRuleTest {

    @Test
    void checkCondition() {
        NeverVotedBeforeRule neverVotedBeforeRule = new NeverVotedBeforeRule();
        HashMap<String, Argument> args = new HashMap<>();
        args.put("votedbefore", new Argument("true"));
        boolean b = neverVotedBeforeRule.checkCondition(args);
        assertThat(b).isTrue();
    }
}