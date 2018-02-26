package pl.grzegorz2047.botupvoter.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.ExtendedAccount;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botupvoter.bot.argument.Argument;
import pl.grzegorz2047.botupvoter.user.User;

import java.util.*;

public class BotUpVoter implements Bot {

    private List<BotAction> botActions;
    private List<BotRule> botRules = new LinkedList<>();
    private boolean running = false;
    private boolean initialized;

    public BotUpVoter() {
    }

    @Override
    public boolean init(List<BotRule> rules, List<BotAction> botActions, HashMap<String, Argument> arguments) {
        this.botRules = rules;
        this.botActions = botActions;

        this.initialized = true;
        return initialized;
    }

    @Override
    public boolean start() {
        if (!initialized) {
            return false;
        }
        running = true;
        //runBot()
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
    void runBot(SteemJ steemJ, HashMap<String, User> users,AccountName botAccount) {
        List<AccountName> steemUsernames = addAccountsToProcess(users);
        BotActions bot = new BotActions();
        while (running) {
            try {
                List<ExtendedAccount> accounts = steemJ.getAccounts(Collections.singletonList(botAccount));
                if (accounts.size() == 0) {
                    System.out.println("Bot account doesnt exist!?");
                    System.exit(1);
                    return;
                }
                ExtendedAccount extendedBotAccount = accounts.get(0);
                AccountName accountName = extendedBotAccount.getName();
                List<ExtendedAccount> filledAccounts = steemJ.getAccounts(steemUsernames);
                for (ExtendedAccount userActivityAccount : filledAccounts) {

                }
            } catch (SteemResponseException e) {
                e.printStackTrace();
            } catch (SteemCommunicationException e) {
                e.printStackTrace();
            }
        }
    }
    List<AccountName> addAccountsToProcess(HashMap<String, User> users) {
        List<AccountName> steemUsernames = new ArrayList<>();
        for (String username : users.keySet()) {
            System.out.println("Adding " + username + " to checkList");
            steemUsernames.add(new AccountName(username));
        }
        return steemUsernames;
    }

}
