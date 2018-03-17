package pl.grzegorz2047.botapi.bot.rules;

import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class NeverVotedBeforeOnPostRule implements BotRule {

    @Override
    public boolean breaks(HashMap<String, Argument> args) throws InsufficentArgumensToActException {
        Argument votedbefore = args.get("votedBefore");
        if (votedbefore == null) {
            throw new InsufficentArgumensToActException("votedBefore doesnt exist!");
        }
        System.out.println("VotedBefore is " + votedbefore.asString());
        return votedbefore.asBoolean();
    }

    @Override
    public LinkedList<String> getRequiredKeyProperties() {
        return new LinkedList<>();
    }

    @Override
    public LinkedList<String> getRequiredRuntimeKeyProperties() {
        return new LinkedList<>(Arrays.asList("votedBefore"));
    }

    @Override
    public String toString() {
        return "getRequiredRuntimeKeyProperties=" + Arrays.toString(getRequiredRuntimeKeyProperties().toArray());
    }

}
