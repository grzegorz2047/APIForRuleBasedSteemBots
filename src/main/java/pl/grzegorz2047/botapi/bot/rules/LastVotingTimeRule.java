package pl.grzegorz2047.botapi.bot.rules;

import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class LastVotingTimeRule implements BotRule {
    @Override
    public boolean breaks(HashMap<String, Argument> args) throws InsufficentArgumensToActException {
        return false;
    }

    @Override
    public LinkedList<String> getRequiredKeyProperties() {
        return new LinkedList<String>(Arrays.asList("limitedVotingIntervalsEnabled"));
    }

    @Override
    public LinkedList<String> getRequiredRuntimeKeyProperties() {
        return new LinkedList<String>(Arrays.asList("limitedVotingIntervalsEnabled"));
    }
}
