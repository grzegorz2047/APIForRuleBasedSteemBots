package pl.grzegorz2047.botapi.bot;

import org.junit.jupiter.api.Test;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.rules.NeverVotedBeforeOnPostRule;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class NeverVotedBeforeOnPostRuleTest {

    @Test
    void checkCondition() throws InsufficentArgumensToActException {
        NeverVotedBeforeOnPostRule neverVotedBeforeOnPostRule = new NeverVotedBeforeOnPostRule();
        HashMap<String, Argument> args = new HashMap<>();
        args.put("votedbefore", new Argument("true"));
        boolean b = neverVotedBeforeOnPostRule.breaks(args);
        assertThat(b).isTrue();
    }
}