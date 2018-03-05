package pl.grzegorz2047.botupvoter.bot;

import java.util.HashMap;

public class NeverVotedBeforeRule implements BotRule {


    @Override
    public boolean checkCondition(HashMap<String, String> args) {
        String votedbeforeArg = args.get("votedbefore");
        return votedbeforeArg != null && Boolean.parseBoolean(votedbeforeArg);
    }

    @Override
    public String[] getRequiredKeyProperties() {
        return new String[] {"votedbefore"};
    }

}
