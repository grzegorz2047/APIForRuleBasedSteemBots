package pl.grzegorz2047.botapi.interval;

public class Interval {


    private float min;
    private float max;
    private float votingPower;

    public Interval(float min, float max, float votingPower) {
        this.min = min;
        this.max = max;
        this.votingPower = votingPower;
    }

    public boolean isBetween(float value) {
        return value >= min && value <= max;
    }

    public float getVotingPower() {
        return votingPower;
    }

    @Override
    public String toString() {
        return "min=" + min + ", " + "max=" + max + ", " + "voting power=" + votingPower;
    }
}
