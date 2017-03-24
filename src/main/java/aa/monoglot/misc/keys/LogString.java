package aa.monoglot.misc.keys;

public enum LogString implements LocalizationKey {
    PROJECT_PATH("log.project.working-path"),
    PROJECT_OPENING("log.project.opening"),
    PROJECT_RECOVERING("log.project.recovering");

    private final String string;
    LogString(String str){
        string = str;
    }
    @Override public String getKey() {
        return string;
    }
}
