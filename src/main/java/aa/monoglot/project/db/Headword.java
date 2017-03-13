package aa.monoglot.project.db;

import aa.monoglot.util.UT;

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
 *    type UUID,
 *    category UUID,
 *    created TIMESTAMP NOT NULL,
 *    modified TIMESTAMP
 * );
 * </kbd>
 */
public final class Headword {
    static final String INSERT_STR = "INSERT INTO entry VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL)";
    static final String UPDATE_STR = "UPDATE entry SET word=?, romanization=?, pronunciation=?, stem=?,type=?,category=?,modified=? where id=?";
    static final String SELECT_STR = "SELECT * FROM entry WHERE id = ?";

    private final static int ID_COL = 1, WORD_COL = 2, ROMAN_COL = 3, PRONUN_COL = 4,
        STEM_COL = 5, TYPE_COL = 6, CAT_COL = 7, CREATED_COL = 8, MODIFIED_COL = 9;

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
        type = resultSet.wasNull()?null:(UUID) temp;

        temp = resultSet.getObject(CAT_COL);
        category = resultSet.wasNull()?null:(UUID) temp;

        temp = resultSet.getTimestamp(MODIFIED_COL);
        modified = resultSet.wasNull()?null:(Timestamp) temp;
    }

    public static Headword create() {
        return new Headword(Timestamp.from(Instant.now()));
    }
    Headword(Timestamp created){
        this(null, "", "", "", "", null, null, created, null);
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
            throw new IllegalArgumentException("A word cannot be empty.");
        Headword newHeadword = new Headword(this.ID, newWord, UT.c(newRomanization), UT.c(newPronunciation),
                UT.c(newStem), newType, newCategory, this.created, Timestamp.from(Instant.now()));
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
        if(o != null && o instanceof Headword){
            Headword other = (Headword) o;
            if(UT.nc(ID , other.ID)
                    && word.equals(other.word)
                    && created.equals(other.created)
                    && romanization.equals(other.romanization)
                    && pronunciation.equals(other.pronunciation)
                    && stem.equals(other.stem)
                    && UT.nc(type, other.type)
                    && UT.nc(category, other.category))
                return true;
        }
        return false;
    }

    static PreparedStatement insert(PreparedStatement statement, UUID id, Headword headword) throws SQLException {
        /*ID   */statement.setObject(ID_COL, id);
        /*WORD */statement.setString(WORD_COL, headword.word);
        /*ROMAN*/statement.setString(ROMAN_COL, headword.romanization);
        /*PRON */statement.setString(PRONUN_COL, headword.pronunciation);
        /*STEM */statement.setString(STEM_COL, headword.stem);
        /*TYPE */
        if(headword.type == null)
            statement.setNull(TYPE_COL, Types.OTHER);
        else statement.setObject(TYPE_COL, headword.type);
        /*CAT  */
        if(headword.category == null)
            statement.setNull(CAT_COL, Types.OTHER);
        else statement.setObject(CAT_COL, headword.category);
        /*CREAT*/statement.setTimestamp(CREATED_COL, headword.created);
        return statement;
    }
    static PreparedStatement update(PreparedStatement statement, Headword headword) throws SQLException {
        return update(statement, headword.ID, headword);
    }
    static PreparedStatement update(PreparedStatement statement, UUID id, Headword headword) throws SQLException {
        int order = 1;
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
        statement.setObject(1, id);
        try(ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return new Headword(resultSet);
        }
    }
}
