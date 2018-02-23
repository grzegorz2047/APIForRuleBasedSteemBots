package pl.grzegorz2047.botupvoter.bot;

public interface BotAction {

    boolean act();

    boolean addRule(String name, BotRule rule);

    boolean removeRule(String name);

    String toString();

    void printAllRules();
}
