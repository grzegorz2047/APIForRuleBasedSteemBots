package pl.grzegorz2047.botapi.bot.helpinformations;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.ExtendedAccount;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.helpinformations.exceptions.CantReceiveDataException;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;
import pl.grzegorz2047.botapi.interval.Interval;
import pl.grzegorz2047.botapi.interval.IntervalHandler;
import pl.grzegorz2047.botapi.interval.IntervalParser;

import java.util.*;

public class CurrentVotingBotCapabilitiesInformation implements HelpInformation {

    private IntervalParser intervalParser = new IntervalParser();

    @Override
    public HashMap<String, Argument> feedBot(SteemJ steemJ, HashMap<String, Argument> botArguments) throws SteemResponseException, CantReceiveDataException, SteemCommunicationException {
        HashMap<String, Argument> outputData = new HashMap<>();
        List<ExtendedAccount> accounts = steemJ.getAccounts(Collections.singletonList(SteemJConfig.getInstance().getDefaultAccount()));
        if (accounts.size() == 0) {
            throw new CantReceiveDataException("Bot account doesnt exist?!");
        }
        ExtendedAccount extendedBotAccount = accounts.get(0);
        float votingPower = extendedBotAccount.getVotingPower() / 100;
        String userIntervals = botArguments.get("intervals").asString();

        IntervalHandler globalIntervalHandler = getIntervalHandler(userIntervals);

        short votingStrength = getVotingStrength(globalIntervalHandler, votingPower);
        outputData.put("votingStrength", new Argument(votingStrength));
        return outputData;
    }

    @Override
    public Collection<? extends String> getRequiredKeyProperties() {
        return Arrays.asList("intervals");
    }

    @Override
    public String[] getRequiredRuntimeKeyProperties() {
        return new String[0];
    }

    private IntervalHandler getIntervalHandler(String userIntervals) {
        IntervalHandler globalIntervalHandler = new IntervalHandler(intervalParser.parse(userIntervals));
        System.out.println("parsuje " + userIntervals);
        float votingStrengthPercentageBasedOnCurrentVotingPower = globalIntervalHandler.getVotingStrengthPercentageBasedOnCurrentVotingPower(77);
        List<Interval> intervals = globalIntervalHandler.getIntervals();
        System.out.println("loaded intervals: " + intervals.size());
        System.out.println("Vote power for 77 is " + votingStrengthPercentageBasedOnCurrentVotingPower);
        return globalIntervalHandler;
    }

    private short getVotingStrength(IntervalHandler intervalHandler, float votingPower) {
        return (short) intervalHandler.getVotingStrengthPercentageBasedOnCurrentVotingPower(votingPower);
    }
}
