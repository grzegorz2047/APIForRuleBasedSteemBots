package pl.grzegorz2047.botapi.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleFileCreator {
    public boolean createFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
            return true;
        }
        return false;
    }
}