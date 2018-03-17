package pl.grzegorz2047.botapi.bot.interfaces;

import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;

import java.util.HashMap;

public interface BotRule {

    boolean breaks(HashMap<String, Argument> args) throws InsufficentArgumensToActException;
    String[] getRequiredKeyProperties();
}
