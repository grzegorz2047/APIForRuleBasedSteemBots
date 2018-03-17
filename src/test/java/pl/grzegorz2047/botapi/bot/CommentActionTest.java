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
import pl.grzegorz2047.botapi.BotsPreparator;
import pl.grzegorz2047.botapi.bot.actions.CommentAction;
import pl.grzegorz2047.botapi.bot.interfaces.BotAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentActionTest {

    private BotAction commentAction;


    @Before
    void setup() {
        this.commentAction = new CommentAction();
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
    void getRequiredKeyProperties() {
        CommentAction commentAction = new CommentAction();
        LinkedList<String> requiredKeyProperties =
                commentAction.
                        getRequiredKeyProperties();
        for (String requiredKey : requiredKeyProperties) {
            System.out.println("Required key is " + requiredKey);
        }
    }
}