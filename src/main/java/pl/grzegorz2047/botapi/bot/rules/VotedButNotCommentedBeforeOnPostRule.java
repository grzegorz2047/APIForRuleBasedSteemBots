package pl.grzegorz2047.botapi.bot.rules;

import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class VotedButNotCommentedBeforeOnPostRule implements BotRule {

    @Override
    public boolean breaks(HashMap<String, Argument> args) throws InsufficentArgumensToActException {
        Argument votedbefore = args.get("votedBefore");
        if (votedbefore == null) {
            throw new InsufficentArgumensToActException("votedBefore doesnt exist!");
        }
        System.out.println("VotedBefore is " + votedbefore.asString());
        boolean voted = votedbefore.asBoolean();
        if (voted) {
            Argument commentedOnPost = args.get("commentedOnPost");
            return commentedOnPost.asBoolean();
        }
        return false;
    }

    @Override
    public LinkedList<String> getRequiredKeyProperties() {
        return new LinkedList<>();
    }

    @Override
    public LinkedList<String> getRequiredRuntimeKeyProperties() {
        return new LinkedList<>(Arrays.asList("votedBefore", "commentedOnPost"));
    }

    @Override
    public String toString() {
        return "getRequiredRuntimeKeyProperties=" + Arrays.toString(getRequiredRuntimeKeyProperties().toArray());
    }

}
