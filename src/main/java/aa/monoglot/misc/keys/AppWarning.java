package aa.monoglot.misc.keys;

public enum AppWarning implements LocalizationKey {
    ICON_LOAD("log.warning.icon-load"),
    NO_SUCH_PROJECT_TITLE("dialog.warning.no-such-project.title"),
    NO_SUCH_PROJECT_HEADER("dialog.warning.no-such-project.header"),
    NO_SUCH_PROJECT_TEXT("dialog.warning.no-such-project.text");

    private final String key;
    AppWarning(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
