package pl.grzegorz2047.botupvoter.bot;

import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public interface BotAction {

    boolean act(HashMap<String, String> arguments) throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException;

    boolean addRule(String name, BotRule rule);

    boolean removeRule(String name);

    String toString();

    LinkedList<String> getRequiredKeyProperties();

    void printAllRules();
}
