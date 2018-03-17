package pl.grzegorz2047.botapi;

import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class Main {

    public static void main(String[] args) throws SteemResponseException, SteemCommunicationException {
        BotsPreparator botsPreparator = new BotsPreparator();
        botsPreparator.runAllBots();
    }

}
