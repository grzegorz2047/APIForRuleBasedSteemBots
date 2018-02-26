package pl.grzegorz2047.botupvoter.bot.argument;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ArgumentTest {

    @Test
    void asString() {
        Argument argument = new Argument("150");
        assertThat(argument.asString()).isEqualTo("150");
    }

    @Test
    void asInt() {
        Argument argument = new Argument("150");
        assertThat(argument.asInt()).isEqualTo(150);
    }

    @Test
    void asFloat() {
        Argument argument = new Argument("150.5");
        assertThat(argument.asFloat()).isEqualTo(150.5f);
    }
}