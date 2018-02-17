package pl.grzegorz2047.botupvoter;

import java.util.Properties;

public class GlobalConfigData {
    private final String votingTags;
    private final String botName;
    private final String postingKey;
    private final boolean commentingEnabled;
    private final String commentMessage;
    private final String commentTagsString;
    private final boolean debugMode;
    private final boolean votingEnabled;
    private final Integer votingPower;
    private final Long frequenceCheckInMilliseconds;
    private final Integer howDeepToCheckIfFirstPost;
    private final boolean reblogEnabled;
    private final boolean intervalEnabled;
    private final int votingPowerLimit;
    private final String globalIntervals;
    private final boolean checkTags;
    private final boolean maxOneVote;

    public GlobalConfigData(Properties globalProperties) {
        this.votingTags = globalProperties.getProperty("votingTags");
        this.botName = globalProperties.getProperty("botName");
        this.postingKey = globalProperties.getProperty("postingKey");
        this.commentingEnabled = Boolean.parseBoolean(globalProperties.getProperty("commentingEnabled"));
        this.commentMessage = globalProperties.getProperty("message");
        this.commentTagsString = globalProperties.getProperty("votingTags");
        this.debugMode = Boolean.parseBoolean(globalProperties.getProperty("debug"));
        this.votingEnabled = Boolean.parseBoolean(globalProperties.getProperty("votingEnabled"));
        this.votingPower = Integer.valueOf(globalProperties.getProperty("votingPower"));
        this.frequenceCheckInMilliseconds = Long.valueOf(globalProperties.getProperty("frequenceCheckInMilliseconds"));
        this.howDeepToCheckIfFirstPost = Integer.valueOf(globalProperties.getProperty("howDeepToCheckIfFirstPost"));
        this.checkTags = Boolean.parseBoolean(globalProperties.getProperty("checkTags"));
        this.reblogEnabled = Boolean.parseBoolean(globalProperties.getProperty("reblogEnabled"));
        this.intervalEnabled = Boolean.parseBoolean(globalProperties.getProperty("intervalsEnabled"));
        this.votingPowerLimit = Integer.parseInt(globalProperties.getProperty("votingPowerLimit"));
        this.globalIntervals = globalProperties.getProperty("intervals");
        this.maxOneVote = Boolean.parseBoolean(globalProperties.getProperty("maxOneVoteForUserPerDay"));
    }

    public String getGlobalIntervals() {
        return globalIntervals;
    }

    public String getCommentTags() {
        return commentTagsString;
    }

    public Long getFrequenceCheckInMilliseconds() {
        return frequenceCheckInMilliseconds;
    }

    public String getPostingKey() {
        return postingKey;
    }

    public String getBotName() {
        return botName;
    }

    public String getVotingTags() {
        return votingTags;
    }

    public boolean isCheckTagsEnabled() {
        return checkTags;
    }

    public boolean isMaxOneVote() {
        return maxOneVote;
    }

    public String getCommentMessage() {
        return commentMessage;
    }
}
