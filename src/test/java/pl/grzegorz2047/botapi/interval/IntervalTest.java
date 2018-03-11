package pl.grzegorz2047.botapi.interval;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntervalTest {


    @Test
    void isBetween() {
        Interval interval = new Interval(5, 10, 15);
        boolean between = interval.isBetween(7);
        assertTrue(between);
    }

    @Test
    void getVotingPower() {
        Interval interval = new Interval(5, 10, 15);
        assertEquals(15, interval.getVotingPower());
    }

}