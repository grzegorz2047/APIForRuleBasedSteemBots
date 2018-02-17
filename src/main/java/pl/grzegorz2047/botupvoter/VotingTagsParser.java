package pl.grzegorz2047.botupvoter;

import java.util.*;

public class VotingTagsParser {
    static List<String> getVotingTags(Properties userProperties) {
        List<String> votingTags = new ArrayList<String>();
        String votingTagsString = userProperties.getProperty("votingTags");
        if (votingTagsString == null) {
            return votingTags;
        }
        if (!votingTagsString.isEmpty()) {
            String[] splittedTags = votingTagsString.split(",");
            if (splittedTags.length == 0) {
                votingTags = Collections.singletonList(votingTagsString);
            } else {
                votingTags = Arrays.asList(splittedTags);
            }
        }
        return votingTags;
    }
}