package pl.grzegorz2047.botupvoter.user;

import pl.grzegorz2047.botupvoter.user.exception.PropertiesNotFound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class PropertiesLoader {


    public Properties loadPropertiesFromFile(String filePath) throws PropertiesNotFound {
        Properties prop = new Properties();
        FileInputStream input = null;
        try {
            File propertiesfile = new File(filePath);
            input = new FileInputStream(propertiesfile);
            prop.load(new InputStreamReader(input, Charset.forName("UTF-8")));
            return prop;
        } catch (IOException ex) {
            throw new PropertiesNotFound();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new PropertiesNotFound();
                }
            }
        }
    }
}