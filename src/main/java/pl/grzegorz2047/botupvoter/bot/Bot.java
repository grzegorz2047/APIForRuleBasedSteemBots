package pl.grzegorz2047.botupvoter.bot;

import pl.grzegorz2047.botupvoter.bot.argument.Argument;

import java.util.HashMap;
import java.util.List;

public interface Bot {
    boolean init(List<BotRule> rules, List<BotAction> botActions, HashMap<String, Argument> arguments);

    boolean start();

    boolean shutdown();

    boolean checkStatus();

    void printAllActions();
}
