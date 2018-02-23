package pl.grzegorz2047.botupvoter.bot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BotUpVoter implements Bot {

    private final List<BotAction> botActions;
    private List<BotRule> botRules = new LinkedList<>();
    private boolean running = false;

    public BotUpVoter(List<BotRule> rules, List<BotAction> botActions) {
        this.botRules = rules;
        this.botActions = botActions;
    }

    @Override
    public boolean init() {
        running = true;
        return running;
    }

    @Override
    public boolean shutdown() {
        running = false;
        return running;
    }

    @Override
    public boolean checkStatus() {
        return running;
    }

    @Override
    public void printAllActions() {
        for (BotAction botAction : botActions) {
            System.out.println(botAction.toString());
        }
    }
}
