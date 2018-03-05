package pl.grzegorz2047.botupvoter.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.base.models.operations.CommentOperation;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

import java.lang.reflect.Array;
import java.util.*;

public class BotCommentAction implements BotAction {

    private final SteemJ steemJ;
    private final AccountName botAccount;
    private HashMap<String, BotRule> botRules = new HashMap<>();

    public BotCommentAction(SteemJ steemJ, AccountName botAccount) {
        this.steemJ = steemJ;
        this.botAccount = botAccount;
    }


    @Override
    public boolean act(HashMap<String, String> arguments) throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException {
        String message = arguments.get("commentMessage");
        String authorToCommentOn = arguments.get("authorToCommentOn");
        if (message == null) {
            return false;
        }
        if (authorToCommentOn == null) {
            return false;
        }
        AccountName userAccount = new AccountName(authorToCommentOn);
        Permlink permlink = new Permlink(arguments.get("permlink"));
        String commentTagsArg = arguments.get("commentTags");
        if (commentTagsArg == null) {
            return false;
        }
        String[] commentTags = commentTagsArg.split(",");

        CommentOperation comment = steemJ.createComment(botAccount, userAccount, permlink, message, commentTags);
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
        System.out.println("TEST");
        return finalRequirements;
    }

    @Override
    public void printAllRules() {

    }

}
