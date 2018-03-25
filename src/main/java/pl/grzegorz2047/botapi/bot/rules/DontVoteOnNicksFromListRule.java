package pl.grzegorz2047.botapi.bot.rules;

import org.spongycastle.util.Arrays;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.*;

import static java.util.Arrays.asList;

public class DontVoteOnNicksFromListRule implements BotRule {


    List<String> blacklist = new ArrayList<>();

    public DontVoteOnNicksFromListRule() {
        blacklist.add("noisy");//load data from file?
    }

    @Override
    public boolean breaks(HashMap<String, Argument> args) throws InsufficentArgumensToActException {
        Argument userAccount = args.get("userAccount");
        return blacklist.contains(userAccount.asString());
    }

    @Override
    public LinkedList<String> getRequiredKeyProperties() {
        return new LinkedList<>();
    }

    @Override
    public LinkedList<String> getRequiredRuntimeKeyProperties() {
        return new LinkedList<>(Collections.singletonList("userAccount"));
    }
}
