package aa.monoglot;

/**
 * Control codes for {@linkplain Monoglot#showError}.
 */
public enum ApplicationErrorCode {
    LOCALIZATION_FAILURE(true, null),
    UNUSUAL_FAILURE(true, "dialog.error.fatalError"),
    APPLICATION_LOAD_FAILURE(true, "dialog.error.fatalError"),
    RECOVERABLE_ERROR(false, "dialog.error.normal");


    private final boolean isFatal;
    private final String localizationPrefix;
    ApplicationErrorCode(){
        this(false, null);
    }

    ApplicationErrorCode(boolean isFatal, String prefix){
        this.isFatal = isFatal;
        this.localizationPrefix = prefix;
    }

    public boolean isFatal(){
        return isFatal;
    }

    public String getPrefix(){
        return localizationPrefix;
    }
}
