package pl.grzegorz2047.botapi.bot;

import pl.grzegorz2047.botapi.interval.Interval;
import pl.grzegorz2047.botapi.interval.IntervalHandler;
import pl.grzegorz2047.botapi.interval.IntervalParser;
import pl.grzegorz2047.botapi.parsers.VotingTagsParser;
import pl.grzegorz2047.botapi.user.User;
import pl.grzegorz2047.botapi.user.UserDataLoader;
import pl.grzegorz2047.botapi.user.exception.PropertiesNotFound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class UsersLoader {

    public HashMap<String, User> getUsers(IntervalHandler globalIntervals, List<String> globalVotingTags, String path) throws Exception {
        Path usersPath = Paths.get(path);
        HashMap<String, User> userList = new HashMap<String, User>();
        Stream<String> lines = getUsersListStream(usersPath);
        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            String username = iterator.next();
            System.out.println("Loading " + username);
            Properties userProperties = null;
            List<String> votingTags;
            try {
                UserDataLoader userDataLoader = new UserDataLoader(username);
                userProperties = userDataLoader.getUserProperties();
                votingTags = new VotingTagsParser().getVotingTags(userProperties);
            } catch (PropertiesNotFound propertiesNotFound) {
                votingTags = new ArrayList<String>();
            }
            IntervalHandler userIntervalHandler;
            if (userProperties != null) {
                String intervalsString = userProperties.getProperty("intervals");
                IntervalParser intervalParser = new IntervalParser();
                List<Interval> parsedIntervalsList = intervalParser.parse(intervalsString);
                if (parsedIntervalsList.isEmpty()) {
                    userIntervalHandler = globalIntervals;
                    System.out.println("User " + username + " is using global intervals!");
                } else {
                    userIntervalHandler = new IntervalHandler(parsedIntervalsList);
                }
                if (votingTags.isEmpty()) {
                    votingTags = globalVotingTags;
                    if (votingTags.isEmpty()) {
                        throw new Exception("No voting tags define in global properties and in user properties");
                    } else {
                        System.out.println("User " + username + " is using global voting tags!");
                    }
                }
            } else {
                userIntervalHandler = globalIntervals;
                votingTags = globalVotingTags;
                System.out.println("User " + username + " is using global intervals!");
                System.out.println("User " + username + " is using global votingTags!");
            }

            User user = new User(username, userIntervalHandler, votingTags);
            userList.put(username, user);
        }
        return userList;
    }

    private Stream<String> getUsersListStream(Path usersPath) throws Exception {
        Stream<String> lines;
        try {
            lines = Files.lines(usersPath);
        } catch (IOException e) {
            String exceptionMessage = "There is an error while getting informations from users.txt file! Check you permissions!";
            System.out.println(exceptionMessage);
            System.exit(1);
            throw new Exception(exceptionMessage);
        }
        return lines;
    }


}