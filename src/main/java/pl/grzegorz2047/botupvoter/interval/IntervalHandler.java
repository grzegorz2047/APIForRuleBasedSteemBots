package pl.grzegorz2047.botupvoter.interval;

import java.util.List;

public class IntervalHandler {

    private final List<Interval> intervals;
    private static final float DEFAULT_MAX_VOTING_POWER = 100;

    public IntervalHandler(List<Interval> intervals) {
        this.intervals = intervals;
        if (intervals.isEmpty()) {
            this.intervals.add(new Interval(1, 100, 100));
        }
    }


    public float getVotingStrengthPercentageBasedOnCurrentVotingPower(float votingPower) {
        for (Interval interval : intervals) {
            if (interval.isBetween(votingPower)) {
                System.out.println("Debuge interval: " + interval.toString());
                return interval.getVotingPower();
            }
        }
        return DEFAULT_MAX_VOTING_POWER;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }
}
