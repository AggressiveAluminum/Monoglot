package aa.monoglot.misc.keys;

public enum AppWarning implements LocalizationKey {
    ICON_LOAD("log.warning.icon-load");

    private final String key;
    AppWarning(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
