package pl.grzegorz2047.botupvoter.bot;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.apis.follow.model.BlogEntry;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.base.models.VoteState;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import pl.grzegorz2047.botupvoter.Main;

import java.util.HashMap;
import java.util.List;

public class BotActions {


    private static HashMap<String, String> currentSeenUsersPosts = new HashMap<>();
    private static HashMap<String, Long> lastVotedUser = new HashMap<>();



    public static boolean alreadyPosted(String lastUserPost, String permlinkText) {
        return lastUserPost != null && !lastUserPost.isEmpty() && permlinkText.equals(lastUserPost);
    }

    public static void comment(SteemJ steemJ, AccountName botAccount, String message, String[] commentTags, AccountName userAccount, Permlink newestPermlink) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        steemJ.createComment(botAccount, userAccount, newestPermlink, message, commentTags);
    }

    public static void vote(SteemJ steemJ, AccountName botAccount, AccountName userAccount, String userAccountName, Permlink newestPermlink, String permlinkText, short votingStrength) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        System.out.println("Im voting on " + permlinkText + " with power " + votingStrength);
        steemJ.vote(botAccount, userAccount, newestPermlink, votingStrength);
        currentSeenUsersPosts.put(userAccountName, permlinkText);
        String votedMsg = "Successfully voted on " + userAccountName + " post " + permlinkText;
        System.out.println(votedMsg);
        Main.writeLog("bot.log", votedMsg);
    }

    static Discussion getlatestPost(AccountName accountName, List<Discussion> newestDiscusions) throws Exception {
        for (Discussion interaterdDiscussion : newestDiscusions) {
            System.out.println("Looking at " + interaterdDiscussion.getPermlink().getLink());
            if (interaterdDiscussion.getAuthor().equals(accountName)) {
                return interaterdDiscussion;
            }
        }
        throw new Exception("No post found");
    }

    public static boolean hasBotVotedOnThisPost(AccountName botAccount, List<VoteState> activeVotes) {
        boolean botVotedOnThisPost = false;
        for (VoteState vote : activeVotes) {
            AccountName voterAccountName = vote.getVoter();
            if (botAccount.getName().equals(voterAccountName.getName())) {
                botVotedOnThisPost = true;
            }
        }
        return botVotedOnThisPost;
    }

    public static Permlink getNewestDiscusion(SteemJ steemJ, AccountName author, List<String> tags) throws Exception {
        List<BlogEntry> blogEntries = steemJ.getBlogEntries(author, 0, (short) 50);
        if (blogEntries.size() == 0) {
            throw new Exception("");
        }
        for (BlogEntry entry : blogEntries) {
            if (entry.getAuthor().equals(author)) {
                return entry.getPermlink();
            }
        }
        throw new Exception("");
    }
    public static HashMap<String, String> getCurrentSeenUsersPosts() {
        return currentSeenUsersPosts;
    }

    public static HashMap<String, Long> getLastVotedUser() {
        return lastVotedUser;
    }
}