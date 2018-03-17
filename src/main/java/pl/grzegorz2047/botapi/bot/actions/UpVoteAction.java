package pl.grzegorz2047.botapi.bot.actions;

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

import java.util.*;

public class UpVoteAction implements BotAction {

    private final ActionProcessor actionProcessor = new ActionProcessor();
    private HashMap<String, BotRule> botRules = new HashMap<>();

    @Override
    public boolean act(SteemJ steemJ, HashMap<String, Argument> arguments) throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException, InsufficentArgumensToActException {
        HashMap<String, Argument> actionArguments = new HashMap<>(arguments);
        if (!arguments.keySet().containsAll(getRequiredKeyProperties())) {
            throw new InsufficentArgumensToActException("You dont have all required arguments to act! Current keys: " + Arrays.toString(arguments.keySet().toArray()) + " requirements are " + Arrays.toString(getRequiredKeyProperties().toArray()));
        }

        if (!arguments.keySet().containsAll(getRequiredRuntimeKeyProperties())) {
            throw new InsufficentArgumensToActException("You dont have all required runtime arguments to act! (bot still hungry of knowledge!) Current keys: " + Arrays.toString(arguments.keySet().toArray()) + " requirements are " + Arrays.toString(getRequiredRuntimeKeyProperties().toArray()));
        }
        Argument permlinkArg = arguments.get("permlink");
        Argument botAccountArg = arguments.get("botName");
        Argument userAccountArg = arguments.get("userAccount");
        Argument votingStrengthArg = arguments.get("votingStrength");

        if (permlinkArg == null || userAccountArg == null || votingStrengthArg == null) {
            throw new InsufficentArgumensToActException("Arguments contain null!");
        }

        Discussion content = steemJ.getContent(new AccountName(userAccountArg.asString()), new Permlink(permlinkArg.asString()));

        List<VoteState> activeVotes = content.getActiveVotes();

        final AccountName botAccount = new AccountName(botAccountArg.asString());
        boolean hasBotVotedOnThisPost = actionProcessor.isBotOnVoterList(activeVotes, botAccount.getName());
        actionArguments.put("votedbefore", new Argument(hasBotVotedOnThisPost));
        //Check if voted before
        if (!actionProcessor.canProceed(actionArguments, botRules.values())) {
            System.out.println("Rules broken. Cant proceed!");
            return false;
        }
        return vote(steemJ, new AccountName(botAccountArg.asString()), new AccountName(userAccountArg.asString()), new Permlink(permlinkArg.asString()), votingStrengthArg.asShort());
    }


    private boolean vote(SteemJ steemJ, AccountName botAccount, AccountName userAccount, Permlink newestPermlink, short votingStrength) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {

        System.out.println("Im voting on " + newestPermlink.getLink() + " with power " + votingStrength);
        steemJ.vote(botAccount, userAccount, newestPermlink, votingStrength);
        //currentSeenUsersPosts.put(userAccountName, permlinkText);
        //Put data to database? To be able to check it later when next check ocurrs or sth.

        String votedMsg = "Successfully voted on " + userAccount.getName() + " post " + newestPermlink.getLink();
        System.out.println(votedMsg);
        return true;
        //OldMain.writeLog("bot.log", votedMsg);
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
        LinkedList<String> finalRequirements = new LinkedList<>(Arrays.asList("botName"));
        for (Map.Entry<String, BotRule> botRule : botRules.entrySet()) {
            LinkedList<String> requiredKeyProperties = botRule.getValue().getRequiredKeyProperties();
            finalRequirements.removeAll(requiredKeyProperties);
            finalRequirements.addAll(requiredKeyProperties);
        }

        return finalRequirements;
    }

    @Override
    public LinkedList<String> getRequiredRuntimeKeyProperties() {
        LinkedList<String> finalRequirements = new LinkedList<>(Arrays.asList("permlink", "userAccount", "votingStrength"));
        for (Map.Entry<String, BotRule> botRule : botRules.entrySet()) {
            LinkedList<String> requiredKeyProperties = botRule.getValue().getRequiredRuntimeKeyProperties();
            finalRequirements.removeAll(requiredKeyProperties);
            finalRequirements.addAll(requiredKeyProperties);
        }
        return finalRequirements;
    }


    @Override
    public Set<String> printAllRules() {

        return botRules.keySet();
    }
}
