package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Instant;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS entry (
 *    ID INT8 PRIMARY KEY,
 *    word VARCHAR NOT NULL,
 *    romanization VARCHAR NOT NULL,
 *    pronunciation VARCHAR NOT NULL,
 *    stem VARCHAR NOT NULL,
 *    type INT8,
 *    category INT8,
 *    created TIMESTAMP NOT NULL,
 *    modified TIMESTAMP
 * );
 * </kbd>
 */
public final class Headword {
    private static final String INSERT_STR = "INSERT INTO entry VALUES (?, ?, '', '', '', NULL, NULL, ?, NULL)",
        WORD_UPDATE_STR = "UPDATE entry SET word = ?, modified = ? WHERE ID = ?",
        PRON_UPDATE_STR = "UPDATE entry SET pronunciation = ?, modified = ? WHERE ID = ?",
        ROMA_UPDATE_STR = "UPDATE entry SET romanization = ?, modified = ? WHERE ID = ?",
        STEM_UPDATE_STR = "UPDATE entry SET stem = ?, modified = ? WHERE ID = ?",
        TYPE_UPDATE_STR = "UPDATE entry SET type = ?, modified = ? WHERE ID = ?",
        CATEGORY_UPDATE_STR = "UPDATE entry SET category = ?, modified = ? WHERE ID = ?",
        ALL_UPDATE_STR = "UPDATE entry SET word=?, romanization=?, pronunciation=?, stem=?,type=?,category=?,modified=? where ID=?",
        SELECT_STR = "SELECT * FROM ENTRY WHERE ID = ? LIMIT 1",
        DELETE_STR = "DELETE FROM entry WHERE ID = ?";

    public final long ID;
    private final Long type, category;
    public final String word, romanization, pronunciation, stem;
    public final Timestamp created, modified;

    public static Headword create(String word) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        long id = Project.getProject().getDatabase().getNextID("entry");
        statement.setLong(1, id);
        statement.setString(2, word);
        statement.setTimestamp(3, Timestamp.from(Instant.now()));
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(id);
    }
    public static Headword fetch(long id) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_STR);
        statement.setLong(1, id);
        try(ResultSet resultSet = statement.executeQuery()){
            if(resultSet.next())
                return new Headword(resultSet);
        }
        return null;
    }
    public static void delete(Headword word) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(DELETE_STR);
        statement.setLong(1, word.ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
    }
    Headword(ResultSet resultSet) throws SQLException {
        ID = resultSet.getLong(1);
        word = resultSet.getString(2);
        romanization = resultSet.getString(3);
        pronunciation = resultSet.getString(4);
        stem = resultSet.getString(5);
        created = resultSet.getTimestamp(8);

        Long temp = resultSet.getLong(6);
            type = resultSet.wasNull()?null:temp;
        temp = resultSet.getLong(7);
            category = resultSet.wasNull()?null:temp;
        Timestamp tempTime = resultSet.getTimestamp(9);
            modified = resultSet.wasNull()?null:tempTime;
    }

    @Override
    public String toString(){
        return word;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Headword){
            Headword other = (Headword) o;
            if(ID == other.ID && word.equals(other.word)
                    && created.equals(other.created)
                    && romanization.equals(other.romanization)
                    && pronunciation.equals(other.pronunciation)
                    && stem.equals(other.stem)
                    && UT.nc(type, other.type)
                    && UT.nc(category, other.category)
                    && UT.nc(modified, other.modified))
                return true;
        }
        return false;
    }

    public Headword updateWord(String newWord) throws SQLException {
        if(word.equals(newWord))
            return this;
        return updateStr(WORD_UPDATE_STR, newWord);
    }

    public Headword updateStem(String newStem) throws SQLException {
        if(stem.equals(newStem))
            return this;
        return updateStr(STEM_UPDATE_STR, newStem);
    }

    public Headword updatePronunciation(String newPronunciation) throws SQLException {
        if(pronunciation.equals(newPronunciation))
            return this;
        return updateStr(PRON_UPDATE_STR, newPronunciation);
    }

    public Headword updateRomanization(String newRomanization) throws SQLException {
        if(romanization.equals(newRomanization))
            return this;
        return updateStr(ROMA_UPDATE_STR, newRomanization);
    }

    /**
     * Common method for the string update methods.
     */
    private Headword updateStr(String sql, String newValue) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(sql);
        statement.setString(1, newValue);
        statement.setTimestamp(2, Timestamp.from(Instant.now()));
        statement.setLong(3, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    public Headword updateType(final Type newType) throws SQLException {
        if((newType == null && type == null) || (newType != null && type != null && newType.ID == type))
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(TYPE_UPDATE_STR);
        if(newType == null)
            statement.setNull(1, Types.OTHER);
        else statement.setLong(1, newType.ID);
        statement.setTimestamp(2, Timestamp.from(Instant.now()));
        statement.setLong(3, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    public Headword updateCategory(Category newCategory) throws SQLException {
        if((newCategory == null && category == null) || (newCategory != null && category != null && newCategory.ID == category))
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(CATEGORY_UPDATE_STR);
        if(newCategory == null)
            statement.setNull(1, Types.OTHER);
        else statement.setLong(1, newCategory.ID);
        statement.setTimestamp(2, Timestamp.from(Instant.now()));
        statement.setLong(3, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    public Headword updateAll(String newWord, String newRomanization, String newPronunciation, String newStem, Type newType, Category newCategory) throws SQLException {
        if(word.equals(newWord)
                && romanization.equals(newRomanization)
                && pronunciation.equals(newPronunciation)
                && stem.equals(newStem)
                && ((newCategory == null && category == null) || (newCategory != null && category != null && newCategory.ID == category))
                && ((newType == null && type == null) || (newType != null && type != null && newType.ID == type)))
                return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(ALL_UPDATE_STR);
        statement.setObject(1, newWord);
        statement.setObject(2, newRomanization);
        statement.setObject(3, newPronunciation);
        statement.setObject(4, newStem);
        if(newType == null)
            statement.setNull(5, Types.OTHER);
        else statement.setLong(5, newType.ID);
        if(newCategory == null)
            statement.setNull(6, Types.OTHER);
        else statement.setLong(6, newCategory.ID);
        statement.setTimestamp(7, Timestamp.from(Instant.now()));
        statement.setObject(8, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    public Headword updateTags(ObservableList<Tag> checkedItems) throws SQLException {
        //TODO
        return this;
    }

    public Type getType() throws SQLException {
        if(type == null)
            return null;
        return Type.fetch(type);
    }

    public Category getCategory() throws SQLException {
        if(category == null)
            return null;
        return Category.fetch(category);
    }
}
