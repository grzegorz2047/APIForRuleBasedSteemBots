package pl.grzegorz2047.botapi.bot.actions;

import com.google.common.collect.Lists;
import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.base.models.VoteState;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UpVoteAction implements BotAction {

    private HashMap<String, BotRule> botRules = new HashMap<>();

    @Override
    public boolean act(SteemJ steemJ, HashMap<String, Argument> arguments) throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException, InsufficentArgumensToActException {
        if (!arguments.keySet().containsAll(getRequiredKeyProperties())) {
            throw new InsufficentArgumensToActException("You dont have all required arguments to act! requirements are " + Arrays.toString(getRequiredKeyProperties().toArray()));
        }
        Argument permlinkArg = arguments.get("permlink");
        Argument botAccountArg = arguments.get("botAccount");
        Argument userAccountArg = arguments.get("userAccount");
        Argument votingStrengthArg = arguments.get("votingStrength");

        if (permlinkArg == null || userAccountArg == null || votingStrengthArg == null) {
            System.out.println("Arguments contain null!");
            return false;
        }

        Discussion content = steemJ.getContent(new AccountName(userAccountArg.asString()), new Permlink(permlinkArg.asString()));

        List<VoteState> activeVotes = content.getActiveVotes();

        hasBotVotedOnThisPost(new AccountName(botAccountArg.asString()), activeVotes);
        //Check if voted before
        return canProceed(arguments) && vote(steemJ, new AccountName(botAccountArg.asString()), new AccountName(userAccountArg.asString()), new Permlink(permlinkArg.asString()), votingStrengthArg.asShort());
    }

    private boolean hasBotVotedOnThisPost(AccountName botAccount, List<VoteState> activeVotes) {
        boolean botVotedOnThisPost = false;
        for (VoteState vote : activeVotes) {
            AccountName voterAccountName = vote.getVoter();
            if (botAccount.getName().equals(voterAccountName.getName())) {
                botVotedOnThisPost = true;
            }
        }
        return botVotedOnThisPost;
    }

    private boolean canProceed(HashMap<String, Argument> arguments) {
        for (BotRule rule : botRules.values()) {
            boolean ok = rule.checkCondition(arguments);
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    private boolean vote(SteemJ steemJ, AccountName botAccount, AccountName userAccount, Permlink newestPermlink, short votingStrength) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {

        System.out.println("Im voting on " + newestPermlink.getLink() + " with power " + votingStrength);
        steemJ.vote(botAccount, userAccount, newestPermlink, votingStrength);
        //currentSeenUsersPosts.put(userAccountName, permlinkText);
        //Put data to database? To be able to check it later when next check ocurrs or sth.

        String votedMsg = "Successfully voted on " + userAccount.getName() + " post " + newestPermlink.getLink();
        System.out.println(votedMsg);
        return true;
        //Main.writeLog("bot.log", votedMsg);
    }


    @Override
    public boolean addRule(String name, BotRule rule) {
        if (botRules.containsKey(name)) {
            return false;
        }
        botRules.put(name, rule);
        return true;
    }

    @Override
    public boolean removeRule(String name) {
        if (botRules.containsKey(name)) {
            botRules.remove(name);
            return true;
        }
        return false;
    }


    @Override
    public LinkedList<String> getRequiredKeyProperties() {
        LinkedList<String> requiredKeys = new LinkedList<>();
        requiredKeys.add("botAccount");
        requiredKeys.add("userAccount");
        requiredKeys.add("votingStrength");
        requiredKeys.add("permlink");
        for (BotRule rule : botRules.values()) {
            String[] requiredKeyProperties = rule.getRequiredKeyProperties();
            requiredKeys.addAll(Lists.newArrayList(requiredKeyProperties));
        }
        return requiredKeys;
    }

    @Override
    public String printAllRules() {

        return null;
    }
}
