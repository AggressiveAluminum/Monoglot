package aa.monoglot.project.db;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS entry (
 *    id UUID PRIMARY KEY,
 *    word VARCHAR NOT NULL,
 *    romanization VARCHAR NOT NULL,
 *    pronunciation VARCHAR NOT NULL,
 *    stem VARCHAR NOT NULL,
 *    type INT,
 *    category INT,
 *    created TIMESTAMP NOT NULL,
 *    modified TIMESTAMP
 * );
 * </kbd>
 */
public final class Headword {
    static final String INSERT_STR = "INSERT INTO entry VALUES (?, '', '', '', '', '', NULL, NULL, ?, NULL)";
    static final String UPDATE_STR = "UPDATE entry SET ";
    static final String SELECT_STR = "SELECT * FROM entry WHERE id = ?";

    private final static String EMPTY_STRING = "";
    private final static int ID_COL = 0, WORD_COL = 1, ROMAN_COL = 2, PRONUN_COL = 3,
        STEM_COL = 4, TYPE_COL = 5, CAT_COL = 6, CREATED_COL = 7, MODIFIED_COL = 8;

    public final UUID ID, type, category;
    public final String word, romanization, pronunciation, stem;
    public final Timestamp created, modified;

    Headword(ResultSet resultSet) throws SQLException {
        ID = (UUID) resultSet.getObject(ID_COL);
        word = resultSet.getString(WORD_COL);
        romanization = resultSet.getString(ROMAN_COL);
        pronunciation = resultSet.getString(PRONUN_COL);
        stem = resultSet.getString(STEM_COL);
        created = resultSet.getTimestamp(CREATED_COL);

        Object temp;
        temp = resultSet.getObject(TYPE_COL);
        if(resultSet.wasNull())
            type = null;
        else type = (UUID) temp;

        temp = resultSet.getObject(CAT_COL);
        if(resultSet.wasNull())
            category = null;
        else category = (UUID) temp;

        temp = resultSet.getTimestamp(MODIFIED_COL);
        if(resultSet.wasNull())
            modified = null;
        else modified = (Timestamp) temp;

        resultSet.close();
    }

    public static Headword create() {
        return new Headword(Timestamp.from(Instant.now()));
    }
    Headword(Timestamp created){
        this(null, null, null, null, null, null, null, created, null);
    }
    Headword(UUID id, String word, String romanization, String pronunciation, String stem, UUID type, UUID category, Timestamp created, Timestamp modified){
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

    public final Headword update(String newWord, String newRomanization, String newPronunciation, String newStem, UUID newType, UUID newCategory){
        if(newWord == null)
            throw new IllegalArgumentException();
        if(newRomanization != null && newRomanization.equals(EMPTY_STRING))
            newRomanization = null;
        if(newPronunciation != null && newPronunciation.equals(EMPTY_STRING))
            newPronunciation = null;
        if(newStem != null && newStem.equals(EMPTY_STRING))
            newStem = null;
        Headword newHeadword = new Headword(this.ID, newWord, newRomanization, newPronunciation,
                newStem, newType, newCategory, this.created, Timestamp.from(Instant.now()));
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
            if(nullCompare(ID , other.ID) && word.equals(other.word) && created.equals(other.created) && romanization.equals(other.romanization)
                    && pronunciation.equals(other.pronunciation) && stem.equals(other.stem) && nullCompare(type, other.type)
                    && nullCompare(category, other.category))
                return true;
        }
        return false;
    }

    private static <T> boolean nullCompare(T a, T b){
        if(a == null)
            return b == null;
        return a.equals(b);
    }

    static PreparedStatement insert(PreparedStatement statement, UUID id, Headword headword) throws SQLException {
        int order = 0;
        return null;
    }
    static PreparedStatement update(PreparedStatement statement, Headword headword) throws SQLException {
        return update(statement, headword.ID, headword);
    }
    static PreparedStatement update(PreparedStatement statement, UUID id, Headword headword) throws SQLException {
        int order = 0;
        statement.setString(order++, headword.word);
        statement.setString(order++, headword.romanization);
        statement.setString(order++, headword.pronunciation);
        statement.setString(order++, headword.stem);
        if(headword.type == null)
            statement.setNull(order++, Types.OTHER);
        else statement.setObject(order++, headword.type);
        if(headword.category == null)
            statement.setNull(order++, Types.OTHER);
        else statement.setObject(order++, headword.category);
        if(headword.modified == null)
            statement.setNull(order++, Types.TIMESTAMP);
        else statement.setTimestamp(order++, headword.modified);

        // WHERE
        statement.setObject(order, headword.ID);
        return statement;
    }

    public static Headword select(PreparedStatement statement, UUID id) throws SQLException {
        statement.setObject(0, id);
        try(ResultSet resultSet = statement.executeQuery()) {
            return new Headword(resultSet);
        }
    }
}
