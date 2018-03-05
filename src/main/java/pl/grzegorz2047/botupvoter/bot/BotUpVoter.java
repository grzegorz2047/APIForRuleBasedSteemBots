package pl.grzegorz2047.botupvoter.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.ExtendedAccount;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import pl.grzegorz2047.botupvoter.bot.argument.Argument;
import pl.grzegorz2047.botupvoter.user.User;

import java.util.*;

public class BotUpVoter implements Bot {

    private List<BotAction> botActions;
    private List<BotRule> botRules = new LinkedList<>();
    private boolean running = false;
    private boolean initialized;
    private HashMap<String, User> users;

    public BotUpVoter() {
    }

    @Override
    public boolean init(HashMap<String, User> users, List<BotRule> rules, List<BotAction> botActions, HashMap<String, Argument> arguments) {
        this.users = users;
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

    private SteemJConfig createSteemConfig(String botName, String postingKey) {
        SteemJConfig steemJConfig = SteemJConfig.getInstance();
        steemJConfig.setResponseTimeout(100000);
        AccountName botAccount = new AccountName(botName);
        steemJConfig.setDefaultAccount(botAccount);

        steemJConfig.setSteemJWeight((short) 0);//https://github.com/marvin-we/steem-java-api-wrapper/blob/126c907c4d136d38d4e805153aae1457f0a8f5e6/core/src/main/java/eu/bittrade/libs/steemj/SteemJ.java#L3018 ????
        List<ImmutablePair<PrivateKeyType, String>> privateKeys = new ArrayList<>();
        privateKeys.add(new ImmutablePair<>(PrivateKeyType.POSTING, postingKey));

        steemJConfig.getPrivateKeyStorage().addAccount(botAccount, privateKeys);
        return steemJConfig;
    }

    void runBot() throws SteemResponseException, SteemCommunicationException {//Async
        this.start();
        SteemJConfig steemConfig = createSteemConfig("botname", "posting-key");
        AccountName botAccount = steemConfig.getDefaultAccount();
        SteemJ steemJ = new SteemJ();

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
