package pl.grzegorz2047.botapi.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import pl.grzegorz2047.botapi.bot.actions.CommentAction;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentActionTest {

    private BotAction commentAction;

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

    @Before
    void setup() throws SteemResponseException, SteemCommunicationException {
        SteemJ steemJ = new SteemJ();
        SteemJConfig steemConfig = createSteemConfig("kumpel", "posting key");
        CommentAction commentAction = new CommentAction(steemJ, steemConfig.getDefaultAccount());

        //commentAction.setPostData();
        this.commentAction = commentAction;
    }

    @Test
    void act() throws SteemResponseException, SteemCommunicationException, SteemInvalidTransactionException {
        /*HashMap<String, String> arguments = new HashMap<>();
        arguments.put("permlink", "linkToPost");
        arguments.put("authorToCommentOn", "coolAuthor");
        arguments.put("message", "Good work! Keep it up!");
        boolean acted = commentAction.act(arguments);
        assertThat(acted).isTrue();
    */
    }

    @Test
    void printAllRules() {

    }

    @Test
    void getRequiredKeyProperties() throws SteemResponseException, SteemCommunicationException {
        SteemJ steemJ = new SteemJ();
        SteemJConfig steemConfig = createSteemConfig("kumpel", "posting key");
        CommentAction commentAction = new CommentAction(steemJ, steemConfig.getDefaultAccount());
        LinkedList<String> requiredKeyProperties =
                commentAction.
                        getRequiredKeyProperties();
        for (String requiredKey : requiredKeyProperties) {
            System.out.println("Required key is " + requiredKey);
        }
    }
}