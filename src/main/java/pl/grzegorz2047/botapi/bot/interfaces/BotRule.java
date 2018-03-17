package pl.grzegorz2047.botapi.bot.interfaces;

import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;

import java.util.HashMap;
import java.util.LinkedList;

public interface BotRule {

    boolean breaks(HashMap<String, Argument> args) throws InsufficentArgumensToActException;
    LinkedList<String> getRequiredKeyProperties();
    LinkedList<String> getRequiredRuntimeKeyProperties();
}
