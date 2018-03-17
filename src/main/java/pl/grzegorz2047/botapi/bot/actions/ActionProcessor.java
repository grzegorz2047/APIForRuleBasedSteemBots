package pl.grzegorz2047.botapi.bot.actions;

import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.VoteState;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ActionProcessor {
    public ActionProcessor() {
    }

    boolean canProceed(HashMap<String, Argument> arguments, Collection<BotRule> values) throws InsufficentArgumensToActException {
        for (BotRule rule : values) {
            boolean breaks = rule.breaks(arguments);
            if (breaks) {
                return false;
            }
        }
        return true;
    }

    public boolean isBotOnVoterList(List<VoteState> activeVotes, String botName) {
        boolean botVotedOnThisPost = false;
        for (VoteState vote : activeVotes) {
            AccountName voterAccountName = vote.getVoter();
            if (botName.equals(voterAccountName.getName())) {
                botVotedOnThisPost = true;
            }
        }
        return botVotedOnThisPost;
    }


}