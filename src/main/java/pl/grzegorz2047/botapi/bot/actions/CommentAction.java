package pl.grzegorz2047.botapi.bot.actions;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.base.models.operations.CommentOperation;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.interfaces.BotRule;

import java.util.*;

public class CommentAction implements BotAction {

    private final SteemJ steemJ;
    private final AccountName botAccount;
    private HashMap<String, BotRule> botRules = new HashMap<>();

    public CommentAction(SteemJ steemJ, AccountName botAccount) {
        this.steemJ = steemJ;
        this.botAccount = botAccount;
    }


    @Override
    public boolean act(SteemJ steemJ, HashMap<String, Argument> arguments) throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException {
        System.out.println("Im about to comment!");
        String message = arguments.get("commentMessage").asString();
        String authorToCommentOn = arguments.get("authorToCommentOn").asString();
        if (message == null) {
            return false;
        }
        if (authorToCommentOn == null) {
            return false;
        }
        AccountName userAccount = new AccountName(authorToCommentOn);
        Permlink permlink = new Permlink(arguments.get("permlink").asString());
        String commentTagsArg = arguments.get("commentTags").asString();
        if (commentTagsArg == null) {
            return false;
        }

        String[] commentTags = commentTagsArg.split(",");
        System.out.println(message + ", " + authorToCommentOn + ", " + permlink.getLink() + ", " + commentTagsArg);
        CommentOperation comment = steemJ.createComment(botAccount, userAccount, permlink, message, commentTags);
        System.out.println("Successfuly commented!");
        return true;
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
        LinkedList<String> finalRequirements = new LinkedList<>(Arrays.asList("commentMessage", "permlink", "commentTags", "authorToCommentOn"));
        for (Map.Entry<String, BotRule> botRule : botRules.entrySet()) {
            String[] requiredKeyProperties = botRule.getValue().getRequiredKeyProperties();
            for (String requirement : requiredKeyProperties) {
                if (!finalRequirements.contains(requirement)) {
                    finalRequirements.add(requirement);
                }
            }
        }
        return finalRequirements;
    }

    @Override
    public String printAllRules() {
        return Arrays.toString(botRules.values().toArray());
    }

    @Override
    public String toString() {
        return "rules=" + this.printAllRules() + ", " + "botAccount=" + botAccount.getName() + ", " + "requiredKeyProperties=" + Arrays.toString(getRequiredKeyProperties().toArray());
    }

}
