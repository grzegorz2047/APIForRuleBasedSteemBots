package pl.grzegorz2047.botapi.interval;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntervalParserTest {
    @Test
    void parse() {
        IntervalParser parser = new IntervalParser();
        List<Interval> intervals = parser.parse("0-80,5;81-90,25;91-100,50");
        float votingPower = 1;
        for (Interval interval : intervals) {
            if (interval.isBetween(77)) {
                votingPower = interval.getVotingPower();
            }
        }
        assertEquals(5, votingPower);

        assertEquals(3, intervals.size());
        List<Interval> singletonList = parser.parse("0-80,10;");
        assertEquals(1, singletonList.size());
        Interval interval = singletonList.get(0);
        boolean between = interval.isBetween(50);
        assertTrue(between);
        assertEquals(10, interval.getVotingPower());
    }
}