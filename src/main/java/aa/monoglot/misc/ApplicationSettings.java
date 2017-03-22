package aa.monoglot.misc;

import aa.monoglot.misc.ApplicationSettings.Setting;
import aa.monoglot.util.BackedSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ApplicationSettings extends BackedSettings<ApplicationSettings.Setting> {
    private enum OSType {
        WINDOWS, OSX, OTHER
    }

    public enum Setting implements BackedSettings.KeyType {
        ;

        private final String string;
        Setting(String string){
            this.string = string;
        }
        public String getString() {
            return string;
        }
    }

    private final OSType osType = findOSType();
    private final Path settingsPath;

    public ApplicationSettings() throws IOException {
        super(verifySettingsPath(findOSType()));
        settingsPath = verifySettingsPath(osType);
    }

    private static OSType findOSType(){
        String os = System.getProperty("os.name").toUpperCase();
        if(os.contains("WIN"))
            return OSType.WINDOWS;
        if(os.contains("MAC"))
            return OSType.OSX;
        return OSType.OTHER;
    }

    private static Path verifySettingsPath(OSType osType) throws IOException {
        Path p;
        switch (osType){
            case WINDOWS:
                p = Paths.get(System.getenv("APPDATA"), "Monoglot");
            case OSX:
                p = Paths.get("~/Library/Preferences/Monoglot");
            default:
                p = Paths.get("~/.monoglot");
        }

        if(!Files.exists(p))
            Files.createDirectories(p);
        else if(Files.isRegularFile(p)){
            Files.move(p, p.resolveSibling(".Monoglot.file"));
            Files.createDirectory(p);
        }

        return p;
    }

    public Path resolve(String location){
        return settingsPath.resolve(location);
    }
}
