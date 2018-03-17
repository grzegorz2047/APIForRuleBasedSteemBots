package pl.grzegorz2047.botapi.bot.interfaces;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.exceptions.CantGetFeedException;
import pl.grzegorz2047.botapi.bot.helpinformations.exceptions.CantReceiveDataException;
import pl.grzegorz2047.botapi.user.User;

import java.util.HashMap;

public interface HelpInformation {
    HashMap<String, Argument> feedBot(SteemJ steemJ, User user, HashMap<String, Argument> botArguments) throws SteemResponseException, CantReceiveDataException, SteemCommunicationException;

}
