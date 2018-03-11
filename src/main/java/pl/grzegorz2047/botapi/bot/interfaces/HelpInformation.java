package pl.grzegorz2047.botapi.bot.interfaces;

import pl.grzegorz2047.botapi.bot.argument.Argument;

import java.util.HashMap;

public interface HelpInformation {
    HashMap<String, Argument> feedBot(HashMap<String, Argument> botArguments);

}
