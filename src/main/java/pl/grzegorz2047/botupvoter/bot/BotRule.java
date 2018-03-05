package pl.grzegorz2047.botupvoter.bot;

import java.util.HashMap;

public interface BotRule {

    boolean checkCondition(HashMap<String, String> args);
    String[] getRequiredKeyProperties();
}
