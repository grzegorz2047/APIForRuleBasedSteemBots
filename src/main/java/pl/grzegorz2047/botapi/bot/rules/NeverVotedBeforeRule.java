package pl.grzegorz2047.botapi.bot.rules;

import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.Arrays;
import java.util.HashMap;

public class NeverVotedBeforeRule implements BotRule {

    @Override
    public boolean breaks(HashMap<String, Argument> args) throws InsufficentArgumensToActException {
        Argument votedbefore = args.get("votedbefore");
        if (votedbefore == null) {
            throw new InsufficentArgumensToActException("votedbefore doesnt exist!");
        }
        System.out.println("VotedBefore is " + votedbefore.asString());
        return votedbefore.asBoolean();
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
