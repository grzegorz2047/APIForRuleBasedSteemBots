package pl.grzegorz2047.botapi.bot.interfaces;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public interface BotAction {

    boolean act(SteemJ steemJ, HashMap<String, Argument> arguments) throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException, InsufficentArgumensToActException;

    boolean addRule(String name, BotRule rule);

    boolean removeRule(String name);

    String toString();

    LinkedList<String> getRequiredKeyProperties();
    LinkedList<String> getRequiredRuntimeKeyProperties();

    Set<String> printAllRules();
}
