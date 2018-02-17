package pl.grzegorz2047.botupvoter.user;

import pl.grzegorz2047.botupvoter.Main;
import pl.grzegorz2047.botupvoter.user.exception.PropertiesNotFound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class UserDataLoader {
    private final String username;
    private final PropertiesLoader propertiesLoader = new PropertiesLoader();
    private Properties userProperties;
    private final static String ROOT_FOLDER = "users";

    public UserDataLoader(String username) {
        this.username = username;
    }

    public Properties getUserProperties() throws PropertiesNotFound {
        if (userProperties != null) {
            return userProperties;
        }
        Path directoryPath = Paths.get(ROOT_FOLDER);
        boolean usersDirectory = Files.isDirectory(directoryPath);
        if (!usersDirectory) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                System.out.println("Cant create directory! Check permission!");
                System.exit(1);
                throw new PropertiesNotFound();
            }
        }
        String userFilename = this.username + ".properties";
        String userFilePath = ROOT_FOLDER + File.separator + userFilename;
        userProperties = propertiesLoader.loadPropertiesFromFile(userFilePath);
        return userProperties;
    }

}
