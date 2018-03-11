package pl.grzegorz2047.botapi.bot.interfaces;

import pl.grzegorz2047.botapi.bot.argument.Argument;

import java.util.HashMap;

public interface BotRule {

    boolean checkCondition(HashMap<String, Argument> args);
    String[] getRequiredKeyProperties();
}
