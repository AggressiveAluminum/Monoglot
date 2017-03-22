package aa.monoglot.project;

import aa.monoglot.util.BackedSettings;

/**
 * Settings key for Project's BackedSettings.
 */
public enum ProjectKey implements BackedSettings.KeyType {
    K_NOTES("notes"), K_NAME("name");

    private final String string;
    ProjectKey(String string){
        this.string = string;
    }
    public String getString() {
        return string;
    }
}
