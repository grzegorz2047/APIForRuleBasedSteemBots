package pl.grzegorz2047.botapi.bot.rules;

import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.Arrays;
import java.util.HashMap;

public class NeverVotedBeforeRule implements BotRule {

    @Override
    public boolean checkCondition(HashMap<String, Argument> args) {
        String votedbeforeArg = args.get("votedbefore").asString();
        return votedbeforeArg != null && Boolean.parseBoolean(votedbeforeArg);
    }

    @Override
    public String[] getRequiredKeyProperties() {
        return new String[]{"votedbefore"};
    }

    @Override
    public String toString() {
        return "requiredKeyProperties=" + Arrays.toString(getRequiredKeyProperties());
    }

}
