package aa.monoglot.misc.keys;

public enum AppString implements LocalizationKey {
    ALL_FILES("dialog.file.all-files"),
    APP_NAME("app.title"),
    MGLT_FILE("dialog.file.mglt-file"),
    CHECK_SAVE("dialog.save.check");//TODO

    private final String key;
    AppString(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
