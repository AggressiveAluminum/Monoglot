package aa.monoglot.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Represents a settings file.
 * @param <KT> The type of key this BackedSettings uses.
 */
public class BackedSettings<KT extends BackedSettings.KeyType> {
    public interface KeyType {
        String getString();
    }
    private final Path path;
    private final Properties properties = new Properties();

    public BackedSettings(Path path) throws IOException {
        this.path = path;
        if (!Files.exists(path))
            Files.createFile(path);
        else if (Files.isDirectory(path))
            throw new FileNotFoundException(path.toString());
        load();
    }

    public void load() throws IOException {
        try(InputStream stream = Files.newInputStream(path)){
            properties.load(stream);
        }
    }

    public void store(String comments) throws IOException {
        try(OutputStream stream = Files.newOutputStream(path)){
            properties.store(stream, comments);
        }
    }

    public String get(KT key){
        return properties.getProperty(key.getString());
    }

    public String get(KT key, String _default){
        return properties.getProperty(key.getString(), _default);
    }

    public void put(KT key, String value){
        properties.setProperty(key.getString(), value);
    }
}
