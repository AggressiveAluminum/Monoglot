package aa.monoglot.misc.keys;

public enum LogString implements LocalizationKey {
    PROJECT_PATH("log.project.working-path"),
    PROJECT_OPENING("log.project.opening"),
    PROJECT_RECOVERING("log.project.recovering"),
    LEXICON_SWITCH_FAILED("log.lexicon.switch-failed"),
    PROJECT_NUKING("log.project.nuking-working-dir"),
    LEXICON_NEW_WORD("log.lexicon.new-word"),
    LEXICON_WORD_DELETED("log.lexicon.deleted-word"),
    LEXICON_DEF_ADDED("log.lexicon.added-definition"),
    LEXICON_DEF_DELETED("log.lexicon.deleted-definition"),
    EXAMPLE_CREATED("log.example.new-example"),
    EXAMPLE_DELETED("log.example.deleted");

    private final String string;
    LogString(String str){
        string = str;
    }
    @Override public String getKey() {
        return string;
    }
}
