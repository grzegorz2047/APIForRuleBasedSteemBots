package pl.grzegorz2047.botupvoter.user;

import org.junit.jupiter.api.Test;
import pl.grzegorz2047.botupvoter.interval.Interval;
import pl.grzegorz2047.botupvoter.interval.IntervalHandler;
import pl.grzegorz2047.botupvoter.interval.IntervalParser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {


    @Test
    void getIntervals() {
        IntervalParser intervalParser = new IntervalParser();
        List<Interval> intervals = intervalParser.parse("1-100,10;");
        IntervalHandler intervalHandler = new IntervalHandler(intervals);
        List<String> followingTags = Arrays.asList("Polish", "pl");
        String username = "grzegorz2047";
        User user = new User(username, intervalHandler, followingTags);
        List<Interval> userIntervals = user.getIntervals();
        assertEquals(intervals.size(), userIntervals.size());
        Interval interval = userIntervals.get(0);
        assertTrue(interval.isBetween(50));
        String userUsername = user.getUsername();
        assertTrue(userUsername.equals(username));
    }

}
