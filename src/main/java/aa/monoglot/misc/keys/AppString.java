package aa.monoglot.misc.keys;

public enum AppString implements LocalizationKey {
    ALL_FILES("dialog.file.all-files"),
    APP_NAME("app.title"),
    MGLT_FILE("dialog.file.mglt-file"),
    CHECK_SAVE("dialog.save.check"),
    LOADED_RESOURCE_BUNDLE("log.load.loaded-resource-bundle"),
    LOADED_UI_FILES("log.load.loaded-ui-files"),
    LOADED_ICONS("log.load.loaded-icons"),
    PRELOADER_DONE("log.load.preloader-done");

    private final String key;
    AppString(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
