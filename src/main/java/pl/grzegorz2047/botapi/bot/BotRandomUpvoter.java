package pl.grzegorz2047.botapi.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.actions.exceptions.InsufficentArgumensToActException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.exceptions.BotNotInitialisedException;
import pl.grzegorz2047.botapi.bot.exceptions.CantGetFeedException;
import pl.grzegorz2047.botapi.bot.helpinformations.exceptions.CantReceiveDataException;
import pl.grzegorz2047.botapi.bot.interfaces.Bot;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;
import pl.grzegorz2047.botapi.user.User;

import java.util.*;

public class BotRandomUpvoter extends Thread implements Bot {
    private List<AccountName> usernames;//How to make it work on separate threads?

    private List<BotAction> botActions;
    private List<HelpInformation> botFeed;
    private boolean running = false;
    private boolean initialized;
    private HashMap<String, User> users;
    private final SteemJ steemJ;
    private final AccountName botAccount;
    private HashMap<String, Argument> botArguments;

    public BotRandomUpvoter(SteemJ steemJ, AccountName botAccount) {
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
        Argument millisecondsArgument = botArguments.get("frequenceCheckInMilliseconds");
        long frequenceCheckInMilliseconds = millisecondsArgument.asLong();
        while (running) {
            voteOnLuckyHuman();
            try {
                sleep(frequenceCheckInMilliseconds);
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
        LinkedList<String> requiredKeyProperties = new LinkedList<>(Arrays.asList("botName", "frequenceCheckInMilliseconds"));
        for (BotAction action : botActions) {
            requiredKeyProperties.addAll(action.getRequiredKeyProperties());
        }
        for (HelpInformation feed : botFeed) {
            requiredKeyProperties.addAll(feed.getRequiredKeyProperties());
        }
        return requiredKeyProperties;
    }

    private void voteOnLuckyHuman() {
        try {
            System.out.println("Starting to work!");
            try {
                HashMap<String, Argument> newFeed = new HashMap<>(botArguments);
                newFeed.putAll(getFeedForBot(steemJ));
                takeProperActions(newFeed);
            } catch (CantReceiveDataException | CantGetFeedException | InsufficentArgumensToActException | SteemInvalidTransactionException e) {
                System.out.println("Msg: " + e.getMessage());
                System.out.println("Cause: " + Arrays.toString(e.getStackTrace()));
            }

            System.out.println("I looked at all accounts in this cycle!");
        } catch (SteemResponseException | SteemCommunicationException e) {
            e.printStackTrace();
        }

    }

    private void takeProperActions(HashMap<String, Argument> arguments) throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException, InsufficentArgumensToActException {
        for (BotAction action : botActions) {
            System.out.println("Acting!");
            action.act(steemJ, arguments);
        }
    }


    private HashMap<String, Argument> getFeedForBot(SteemJ steemJ) throws CantGetFeedException, SteemResponseException, CantReceiveDataException, SteemCommunicationException {
        HashMap<String, Argument> newFeed = new HashMap<>();
        for (HelpInformation information : botFeed) {
            //User user = users.get(username);
            HashMap<String, Argument> feed = information.feedBot(steemJ, botArguments);
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
