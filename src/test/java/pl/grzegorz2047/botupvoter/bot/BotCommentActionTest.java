package pl.grzegorz2047.botupvoter.bot;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BotCommentActionTest {

    private BotAction commentAction;

    @Before
    void setup() {
        commentAction = new BotCommentAction();
    }

    @Test
    void act() {
        boolean acted = commentAction.act();
        assertThat(acted).isTrue();
    }

    @Test
    void printAllRules() {
    }

}