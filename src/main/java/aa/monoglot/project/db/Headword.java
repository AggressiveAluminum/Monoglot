package aa.monoglot.project.db;

import java.sql.Timestamp;

public final class Headword {
    private final static String EMPTY_STRING = "";

    public final int ID;
    public final String word, romanization, pronunciation, stem;
    public final Integer type, category;
    public final Timestamp created, modified;

    Headword(int id, String word, Timestamp created){
        this(id, word, null, null, null, null, null, created, null);
    }
    Headword(int id, String word, String romanization, String pronunciation, String stem, Integer type, Integer category, Timestamp created, Timestamp modified){
        this.ID = id;
        this.word = word;
        this.romanization = romanization;
        this.pronunciation = pronunciation;
        this.stem = stem;
        this.type = type;
        this.category = category;
        this.created = created;
        this.modified = modified;
    }

    public final Headword update(String newWord, String newRomanization, String newPronunciation, String newStem, Integer newType, Integer newCategory, Timestamp newModifiedTime){
        if(newWord == null)
            throw new IllegalArgumentException();
        if(newRomanization != null && newRomanization.equals(EMPTY_STRING))
            newRomanization = null;
        if(newPronunciation != null && newPronunciation.equals(EMPTY_STRING))
            newPronunciation = null;
        if(newStem != null && newStem.equals(EMPTY_STRING))
            newStem = null;
        Headword newHeadword = new Headword(this.ID, newWord, newRomanization, newPronunciation, newStem, newType, newCategory, this.created, newModifiedTime);
        if(this.equals(newHeadword))
            return this;
        return newHeadword;
    }

    @Override
    public String toString(){
        return word;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean equals(Object o){
        if(o instanceof Headword){
            Headword other = (Headword) o;
            if(ID == other.ID && word.equals(other.word) && created.equals(other.created) && nullCompare(romanization, other.romanization)
                    && nullCompare(pronunciation, other.pronunciation) && nullCompare(stem, other.stem) && nullCompare(type, other.type)
                    && nullCompare(category, other.category) && nullCompare(modified, other.modified))
                return true;
        }
        return false;
    }

    private static <T> boolean nullCompare(T a, T b){
        if(a == null)
            return b == null;
        return a.equals(b);
    }
}
