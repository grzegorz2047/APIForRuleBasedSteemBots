package pl.grzegorz2047.botapi.configuration;

import pl.grzegorz2047.botapi.user.PropertiesLoader;
import pl.grzegorz2047.botapi.user.exception.PropertiesNotFound;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class GlobalConfigurationLoader {

    private Properties properties;
    private String filePath = "global.properties";

    public GlobalConfigurationLoader() {
    }

    public GlobalConfigurationLoader(String filePath) {
        this.filePath = filePath;
    }


    void createGlobalProperties() throws IOException {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            File targetFile = new File(filePath);
            File parent = targetFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Couldn't create dir: " + parent);
            }
            output = new FileOutputStream(filePath);
            prop.setProperty("votingTags", "test");
            prop.setProperty("botName", "yourbotAccountName");
            prop.setProperty("postingKey", "enteryourprivatepostingkey");
            prop.setProperty("commentingEnabled", "false");
            prop.setProperty("message", "Welcome on this tag!");
            prop.setProperty("frequenceCheckInMilliseconds", "1000");
            prop.setProperty("votingEnabled", "true");
            prop.setProperty("votingPower", "100");
            prop.setProperty("howDeepToCheckIfFirstPost", "100");
            prop.setProperty("reblogEnabled", "true");
            prop.setProperty("debug", "true");
            prop.setProperty("votingPowerLimit", "81");
            prop.setProperty("intervalsEnabled", "true");
            prop.setProperty("intervals", "0-80,10;81-90,50;91-100,100");
            prop.setProperty("maxOneVoteForUserPerDay", "true");

            // save properties to project root folder
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean prepareGlobalProperties() throws IOException {
        Path pathToFile = Paths.get(filePath);
        if (!fileExists(pathToFile)) {
            createGlobalProperties();
            return false;
        }
        return true;
    }

    private boolean fileExists(Path pathToFile) {
        return Files.exists(pathToFile);
    }


    public Properties getGlobalProperties() throws PropertiesNotFound {
        if (properties != null) {
            return properties;
        }
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        properties = propertiesLoader.loadPropertiesFromFile(filePath);
        return properties;
    }
}
