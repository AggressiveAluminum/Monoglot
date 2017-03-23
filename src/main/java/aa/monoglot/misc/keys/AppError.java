package aa.monoglot.misc.keys;

public enum AppError implements LocalizationKey{
    LOAD_ERROR("log.error.load"),
    UNCAUGHT("log.error.uncaught"),
    FATAL_ERROR_TITLE("dialog.error.fatal-error.title"),
    FATAL_ERROR_HEADER("dialog.error.fatal-error.header"),
    FATAL_ERROR_TEXT("dialog.error.fatal-error.text"),
    LOAD_ERROR_TITLE("dialog.error.load-error.title"),
    LOAD_ERROR_HEADER("dialog.error.load-error.header"),
    LOAD_ERROR_TEXT("dialog.error.load-error.text");

    private final String key;
    AppError(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
