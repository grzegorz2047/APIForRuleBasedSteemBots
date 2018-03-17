package pl.grzegorz2047.botapi.bot.helpinformations;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.apis.follow.model.BlogEntry;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.grzegorz2047.botapi.bot.argument.Argument;
import pl.grzegorz2047.botapi.bot.helpinformations.exceptions.CantReceiveDataException;
import pl.grzegorz2047.botapi.bot.interfaces.HelpInformation;
import pl.grzegorz2047.botapi.user.User;

import java.util.HashMap;
import java.util.List;

public class NewestUserPostInformation implements HelpInformation {
    @Override
    public HashMap<String, Argument> feedBot(SteemJ steemJ, User user, HashMap<String, Argument> botArguments) throws SteemResponseException, CantReceiveDataException, SteemCommunicationException {
        HashMap<String, Argument> outputData = new HashMap<>();
        Permlink newestPermlink;
        newestPermlink = getNewestDiscusion(steemJ, user);
        //System.out.println("received newest discussion from " + userAccountName);
        //System.out.println("permlink " + newestPermlink);

        outputData.put("permlink", new Argument(newestPermlink.getLink()));
        return outputData;
    }

    private Permlink getNewestDiscusion(SteemJ steemJ, User user) throws CantReceiveDataException, SteemResponseException, SteemCommunicationException {
        AccountName author = user.getAccountNameObj();
        List<BlogEntry> blogEntries = steemJ.getBlogEntries(author, 0, (short) 50);
        if (blogEntries.size() == 0) {
            throw new CantReceiveDataException("");
        }
        for (BlogEntry entry : blogEntries) {
            if (entry.getAuthor().equals(author)) {
                Discussion content = steemJ.getContent(author, entry.getPermlink());
                String jsonMetadata = content.getJsonMetadata();
                boolean validTag = hasPostValidTag(user.getFollowingTags(), jsonMetadata);
                if (validTag) {
                    return entry.getPermlink();
                }
            }
        }
        throw new CantReceiveDataException("");
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
