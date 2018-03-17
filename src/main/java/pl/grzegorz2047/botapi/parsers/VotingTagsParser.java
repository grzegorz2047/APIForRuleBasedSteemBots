package pl.grzegorz2047.botapi.parsers;

import java.util.*;

public class VotingTagsParser {
     public List<String> getVotingTags(String votingTagsString) {
        List<String> votingTags = new ArrayList<String>();
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