package pl.grzegorz2047.botapi.bot.interfaces;

import eu.bittrade.libs.steemj.SteemJ;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.exceptions.CantGetFeedException;
import pl.grzegorz2047.botapi.user.User;

import java.util.HashMap;

public interface HelpInformation {
    HashMap<String, Argument> feedBot(SteemJ steemJ, User user, HashMap<String, Argument> botArguments) throws CantGetFeedException;

}
