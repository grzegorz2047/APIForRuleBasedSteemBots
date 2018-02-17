package pl.grzegorz2047.botupvoter.interval;

import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntervalHandlerTest {
    IntervalHandler intervalHandler;

    @BeforeEach
    void setUp() {
        IntervalParser intervalParser = new IntervalParser();
        List<Interval> intervals = intervalParser.parse("80-100,50;");
        intervalHandler = new IntervalHandler(intervals);
    }


    void getVotingPower() {
        float votingPower = 90;
        float votingStrengthPercentageBasedOnCurrentVotingPower = intervalHandler.getVotingStrengthPercentageBasedOnCurrentVotingPower(votingPower);
        assertEquals(50, votingStrengthPercentageBasedOnCurrentVotingPower);
    }

}