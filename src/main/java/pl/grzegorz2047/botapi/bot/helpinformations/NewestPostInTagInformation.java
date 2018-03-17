package pl.grzegorz2047.botapi.bot.helpinformations;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.DiscussionQuery;
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.DiscussionSortType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.grzegorz2047.botapi.bot.actions.ActionProcessor;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.helpinformations.exceptions.CantReceiveDataException;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;

import java.util.*;

public class NewestPostInTagInformation implements HelpInformation {

    private static Random random = new Random();

    @Override
    public HashMap<String, Argument> feedBot(SteemJ steemJ, HashMap<String, Argument> botArguments) throws SteemResponseException, CantReceiveDataException, SteemCommunicationException {
        HashMap<String, Argument> outputData = new HashMap<>();
        String[] commentTags = botArguments.get("votingTags").asArray(",");
        //System.out.println("received newest discussion from " + userAccountName);
        //System.out.println("permlink " + newestPermlink);
        List<Discussion> discussions = new ArrayList<>();
        for (String tag : commentTags) {
            discussions.addAll(getNewestDiscusions(tag, steemJ));
        }
        int numberOfPosts = discussions.size();
        if (numberOfPosts == 0) {
            throw new CantReceiveDataException("No post found!");
        }
        int randomNumber = random.nextInt(numberOfPosts);
        Discussion fairRandomPost = discussions.get(randomNumber);


        AccountName author = fairRandomPost.getAuthor();
        String botName = botArguments.get("botName").asString();
        boolean botOnVoterList = new ActionProcessor().isBotOnVoterList(fairRandomPost.getActiveVotes(), botName);
        outputData.put("votedBefore", new Argument(new Argument(botOnVoterList)));
        outputData.put("userAccount", new Argument(author.getName()));
        Permlink permlink = fairRandomPost.getPermlink();
        outputData.put("permlink", new Argument(permlink.getLink()));
        return outputData;
    }

    @Override
    public Collection<? extends String> getRequiredKeyProperties() {
        return Arrays.asList("votingTags");
    }

    @Override
    public String[] getRequiredRuntimeKeyProperties() {
        return new String[0];
    }


    private List<Discussion> getNewestDiscusions(String tag, SteemJ steemJ) throws SteemCommunicationException, SteemResponseException {
        DiscussionQuery discussionQuery = new DiscussionQuery();
        discussionQuery.setTag(tag);
        discussionQuery.setLimit(30);
        return steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_CREATED);
    }


    private boolean hasPostValidTag(List<String> followingTags, String jsonMetadata) {
        //System.out.println("Discussion " + content.getTitle());
        JSONObject jsonObject = new JSONObject(jsonMetadata);
        JSONArray jsonArray = (JSONArray) jsonObject.get("tags");
        return isEligiblePost(jsonArray, followingTags);
    }

    private boolean isEligiblePost(JSONArray tags, List<String> followingTags) {
        boolean isEligiblePost = false;
        for (int i = 0; i < tags.length(); i++) {
            String tagsString = tags.getString(i);
            if (followingTags.contains(tagsString)) {
                isEligiblePost = true;
            }
        }
        return isEligiblePost;
    }

}
