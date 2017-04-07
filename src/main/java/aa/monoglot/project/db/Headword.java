package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;
import javafx.collections.ObservableList;

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
    private static final String INSERT_STR = "INSERT INTO entry VALUES (?, ?, '', '', '', NULL, NULL, ?, NULL)",
        WORD_UPDATE_STR = "UPDATE entry SET word = ?, modified = ? WHERE id = ?",
        PRON_UPDATE_STR = "UPDATE entry SET pronunciation = ?, modified = ? WHERE id = ?",
        ROMA_UPDATE_STR = "UPDATE entry SET romanization = ?, modified = ? WHERE id = ?",
        STEM_UPDATE_STR = "UPDATE entry SET stem = ?, modified = ? WHERE id = ?",
        ALL_UPDATE_STR = "UPDATE entry SET word=?, romanization=?, pronunciation=?, stem=?,type=?,category=?,modified=? where id=?",
        SELECT_STR = "SELECT * FROM ENTRY WHERE id = ? LIMIT 1",
        DELETE_STR = "DELETE FROM entry WHERE id = ?";

    public final UUID ID, type, category;
    public final String word, romanization, pronunciation, stem;
    public final Timestamp created, modified;

    public static Headword create(String word) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        UUID id = Project.getProject().getDatabase().getNextID();
        statement.setObject(1, id);
        statement.setString(2, word);
        statement.setTimestamp(3, Timestamp.from(Instant.now()));
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(id);
    }
    public static Headword fetch(UUID id) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_STR);
        statement.setObject(1, id);
        try(ResultSet resultSet = statement.executeQuery()){
            if(resultSet.next())
                return new Headword(resultSet);
        }
        return null;
    }
    public static void delete(Headword word) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(DELETE_STR);
        statement.setObject(1, word.ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
    }
    Headword(ResultSet resultSet) throws SQLException {
        ID = (UUID) resultSet.getObject(1);
        word = resultSet.getString(2);
        romanization = resultSet.getString(3);
        pronunciation = resultSet.getString(4);
        stem = resultSet.getString(5);
        created = resultSet.getTimestamp(8);

        Object temp = resultSet.getObject(6);
            type = resultSet.wasNull()?null:(UUID) temp;
        temp = resultSet.getObject(7);
            category = resultSet.wasNull()?null:(UUID) temp;
        temp = resultSet.getTimestamp(9);
            modified = resultSet.wasNull()?null:(Timestamp) temp;
    }

    @Override
    public String toString(){
        return word;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Headword){
            Headword other = (Headword) o;
            if(ID.equals(other.ID)
                    && word.equals(other.word)
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
        updateStr(WORD_UPDATE_STR, newWord);
        return fetch(ID);
    }

    public Headword updateStem(String newStem) throws SQLException {
        if(stem.equals(newStem))
            return this;
        updateStr(STEM_UPDATE_STR, newStem);
        return fetch(ID);
    }

    public Headword updatePronunciation(String newPronunciation) throws SQLException {
        if(pronunciation.equals(newPronunciation))
            return this;
        updateStr(PRON_UPDATE_STR, newPronunciation);
        return fetch(ID);
    }

    public Headword updateRomanization(String newRomanization) throws SQLException {
        if(romanization.equals(newRomanization))
            return this;
        updateStr(ROMA_UPDATE_STR, newRomanization);
        return fetch(ID);
    }

    private void updateStr(String sql, String newValue) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(sql);
        statement.setString(1, newValue);
        statement.setTimestamp(2, Timestamp.from(Instant.now()));
        statement.setObject(3, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
    }

    public Headword updateType(Type newType) throws SQLException {
        /*if(type.equals(newType.ID))
            return this;*///TODO
        return null;
    }

    public Headword updateCategory(Category newCategory) throws SQLException {
        /*if(category.equals(newCategory.ID))
            return this;*///TODO
        return null;
    }

    public Headword updateAll(String newWord, String newRomanization, String newPronunciation, String newStem, Type newType, Category newCategory) throws SQLException {
        if(word.equals(newWord)
                && romanization.equals(newRomanization)
                && pronunciation.equals(newPronunciation)
                && stem.equals(newStem)){
            //if((newType == null && type == null) || (type != null && newType != null && type.equals(newType.ID)))
                //return this;
            //if((newCategory == null && category == null) || (category != null && newCategory != null && category.equals(newCategory.ID)))
                //return this;
        }
        PreparedStatement statement = Project.getProject().getDatabase().sql(ALL_UPDATE_STR);
        statement.setObject(1, newWord);
        statement.setObject(2, newRomanization);
        statement.setObject(3, newPronunciation);
        statement.setObject(4, newStem);
        if(newType == null || (Math.random() < Double.POSITIVE_INFINITY))//TODO
            statement.setNull(5, Types.OTHER);
        //else statement.setObject(5, newType.ID);
        if(newCategory == null || (Math.random() < Double.POSITIVE_INFINITY))//TODO
            statement.setNull(6, Types.OTHER);
        //else statement.setObject(6, newType.ID);
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
}
