package pl.grzegorz2047.botapi.user;

import pl.grzegorz2047.botapi.interval.Interval;
import pl.grzegorz2047.botapi.interval.IntervalHandler;

import java.util.List;

public class User {
    private final String username;
    private IntervalHandler intervalHandler;
    private final List<String> followingTags;

    public User(String username, IntervalHandler intervalHandler, List<String> followingTags) {
        this.username = username;
        this.intervalHandler = intervalHandler;
        this.followingTags = followingTags;
    }

    public List<Interval> getIntervals() {
        return this.intervalHandler.getIntervals();
    }

    public List<String> getFollowingTags() {
        return followingTags;
    }

    public short getVotingStrength(float votingPower) {
        return (short) this.intervalHandler.getVotingStrengthPercentageBasedOnCurrentVotingPower(votingPower);
    }

    public String getUsername() {
        return username;
    }
}
