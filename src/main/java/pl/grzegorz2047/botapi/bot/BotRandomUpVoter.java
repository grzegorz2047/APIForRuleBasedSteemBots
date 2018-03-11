package pl.grzegorz2047.botapi.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.ExtendedAccount;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.exceptions.BotNotInitialisedException;
import pl.grzegorz2047.botapi.bot.interfaces.Bot;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.user.User;

import java.util.*;

import static java.lang.Thread.sleep;

public class BotRandomUpVoter extends Thread implements Bot {
    private List<AccountName> usernames;//How to make it work on separate threads?

    private List<BotAction> botActions;
    private boolean running = false;
    private boolean initialized;
    private HashMap<String, User> users;
    private final SteemJ steemJ;
    private final AccountName botAccount;
    private HashMap<String, Argument> botArguments;

    public BotRandomUpVoter(SteemJ steemJ, AccountName botAccount) throws SteemResponseException, SteemCommunicationException {
        this.botAccount = botAccount;
        this.steemJ = steemJ;
    }

    @Override
    public boolean init(HashMap<String, User> users, List<BotAction> botActions, HashMap<String, Argument> arguments) {
        this.users = users;
        this.botActions = botActions;
        this.botArguments = arguments;
        this.usernames = addAccountsToProcess(users);

        if (users == null || botActions == null) {
            return false;
        }
        this.initialized = true;
        return initialized;
    }


    @Override
    public void run() {
        while (running) {
            doWork(botArguments, botAccount, steemJ, usernames);
            try {
                sleep(1000 * 120);
                System.out.println("Sleeping!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        running = true;
        //runBot()
    }

    @Override
    public boolean shutdown() {
        running = false;
        this.interrupt();
        return true;
    }

    @Override
    public boolean checkStatus() {
        return initialized && running;
    }

    @Override
    public void printAllActions() throws BotNotInitialisedException {
        if (!initialized) {
            throw new BotNotInitialisedException("Bot " + this.botAccount.getName() + " is not initialised!");
        }
        for (BotAction botAction : botActions) {
            System.out.println(botAction.toString());
        }
    }

    @Override
    public LinkedList<String> getAllRequiredKeyProperties() {
        LinkedList<String> requiredKeyProperties = new LinkedList<>();
        for (BotAction action : botActions) {
            requiredKeyProperties.addAll(action.getRequiredKeyProperties());
        }
        return requiredKeyProperties;
    }

    private void doWork(HashMap<String, Argument> botArguments, AccountName botAccount, SteemJ steemJ, List<AccountName> steemUsernames) {
        try {
            System.out.println("Starting to work!");
            List<ExtendedAccount> accounts = steemJ.getAccounts(Collections.singletonList(botAccount));
            if (accounts.size() == 0) {
                System.out.println("Bot account doesnt exist!?");
                System.exit(1);
            }
            ExtendedAccount extendedBotAccount = accounts.get(0);
            AccountName accountName = extendedBotAccount.getName();
            List<ExtendedAccount> filledAccounts = steemJ.getAccounts(steemUsernames);
            for (ExtendedAccount userActivityAccount : filledAccounts) {
                doForUser(botArguments, steemJ, userActivityAccount);
            }
            System.out.println("I looked at all accounts in this cycle!");
        } catch (SteemResponseException | SteemCommunicationException e) {
            e.printStackTrace();
        }
    }

    private void doForUser(HashMap<String, Argument> botArguments, SteemJ steemJ, ExtendedAccount userActivityAccount) throws SteemResponseException, SteemCommunicationException {
        HashMap<String, Argument> userArguments = new HashMap<>(botArguments);
        //permlinks and etc
        //finding what?
        //add arguments here and do something cool
        long reputation = userActivityAccount.getReputation();
        for (BotAction action : botActions) {
            try {
                LinkedList<String> requiredKeyProperties = action.getRequiredKeyProperties();
                System.out.println("Acting!");
                action.act(steemJ, userArguments);
            } catch (SteemInvalidTransactionException e) {
                System.out.println("exception " + e.getMessage());
                e.printStackTrace();//How to react to this placa? If one of the actions fails then what? Rollback?
            } catch (InsufficentArgumensToActException e) {
                System.out.println("exception " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private List<AccountName> addAccountsToProcess(HashMap<String, User> users) {
        List<AccountName> steemUsernames = new ArrayList<>();
        for (String username : users.keySet()) {
            System.out.println("Adding " + username + " to checkList");
            steemUsernames.add(new AccountName(username));
        }
        return steemUsernames;
    }

}
