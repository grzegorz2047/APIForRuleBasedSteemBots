package pl.grzegorz2047.botapi.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.ExtendedAccount;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.GlobalConfigData;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.exceptions.BotNotInitialisedException;
import pl.grzegorz2047.botapi.bot.exceptions.CantGetFeedException;
import pl.grzegorz2047.botapi.bot.interfaces.Bot;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;
import pl.grzegorz2047.botapi.configuration.GlobalConfigurationLoader;
import pl.grzegorz2047.botapi.interval.IntervalHandler;
import pl.grzegorz2047.botapi.interval.IntervalParser;
import pl.grzegorz2047.botapi.user.User;
import pl.grzegorz2047.botapi.user.exception.PropertiesNotFound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class UsersFromListUpVoter extends Thread implements Bot {
    private List<AccountName> usernames;//How to make it work on separate threads?

    private List<BotAction> botActions;
    private List<HelpInformation> botFeed;
    private boolean running = false;
    private boolean initialized;
    private HashMap<String, User> users;
    private final SteemJ steemJ;
    private final AccountName botAccount;
    private HashMap<String, Argument> botArguments;

    public UsersFromListUpVoter(SteemJ steemJ, AccountName botAccount) {
        this.botAccount = botAccount;
        this.steemJ = steemJ;
    }

    @Override
    public boolean init(HashMap<String, User> users, List<HelpInformation> botFeed, List<BotAction> botActions, HashMap<String, Argument> arguments) {
        this.users = users;
        this.botFeed = botFeed;
        this.botActions = botActions;
        this.botArguments = arguments;
        this.usernames = addAccountsToProcess(users);

        if (users == null || botActions == null) {
            return false;
        }


        this.initialized = true;
        return true;
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
                try {
                    doForUser(botArguments, steemJ, userActivityAccount);
                } catch (CantGetFeedException | InsufficentArgumensToActException | SteemInvalidTransactionException e) {
                    System.out.println("Msg: " + e.getMessage());
                    System.out.println("Cause: " + e.getCause());
                }
            }
            System.out.println("I looked at all accounts in this cycle!");
        } catch (SteemResponseException | SteemCommunicationException e) {
            e.printStackTrace();
        }
    }

    private void doForUser(HashMap<String, Argument> botArguments, SteemJ steemJ, ExtendedAccount userActivityAccount) throws SteemResponseException, SteemCommunicationException, CantGetFeedException, InsufficentArgumensToActException, SteemInvalidTransactionException {
        HashMap<String, Argument> userArguments = new HashMap<>(botArguments);
        //permlinks and etc
        //finding what?
        //add arguments here and do something cool
        String username = userActivityAccount.getName().getName();

        HashMap<String, Argument> newFeed = getFeedForBot(botArguments, steemJ, username);
        userArguments.putAll(newFeed);

        for (BotAction action : botActions) {
            System.out.println("Acting!");
            action.act(steemJ, userArguments);
        }
    }

    private HashMap<String, Argument> getFeedForBot(HashMap<String, Argument> botArguments, SteemJ steemJ, String username) throws CantGetFeedException {
        HashMap<String, Argument> newFeed = new HashMap<>();
        for (HelpInformation information : botFeed) {
            HashMap<String, Argument> feed = information.feedBot(steemJ, users.get(username), botArguments);
            newFeed.putAll(feed);

        }
        return newFeed;
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
