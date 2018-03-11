package pl.grzegorz2047.botapi.bot.interfaces;

import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.exceptions.BotNotInitialisedException;
import pl.grzegorz2047.botapi.user.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public interface Bot {
    boolean init(HashMap<String, User> users, List<BotAction> botActions, HashMap<String, Argument> arguments);

    void start();

    boolean shutdown();

    boolean checkStatus();

    void printAllActions() throws BotNotInitialisedException;

    LinkedList<String> getAllRequiredKeyProperties();
}
