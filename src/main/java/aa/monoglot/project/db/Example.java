package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS examples (
 *     id INT8 PRIMARY KEY,
 *     text VARCHAR NOT NULL,
 *     gloss VARCHAR NOT NULL,
 *     pronunciation VARCHAR NOT NULL,
 *     literal_translation VARCHAR NOT NULL,
 *     free_translation VARCHAR NOT NULL,
 *     explanation VARCHAR NOT NULL
 * );
 * </kbd>
 */
public class Example {
    private static final String INSERT_STR = "INSERT INTO examples VALUES (?, '', '', '', '', '', '')",
            UPDATE_TEXT_STR = "UPDATE examples SET text=? WHERE id=?",
            UPDATE_GLOSS_STR = "UPDATE examples SET gloss=? WHERE id=?",
            UPDATE_PRON_STR = "UPDATE examples SET pronunciation=? WHERE id=?",
            UPDATE_LIT_STR = "UPDATE examples SET literal_translation=? WHERE id=?",
            UPDATE_FREE_STR = "UPDATE examples SET free_translation=? WHERE id=?",
            UPDATE_EXPL_STR = "UPDATE examples SET explanation=? WHERE id=?",
            UPDATE_STR = "UPDATE examples SET text=?, gloss=?, pronunciation=?,literal_translation=?,free_translation=?,explanation=? WHERE id=?",
            SELECT_ONE_STR = "SELECT * FROM examples WHERE id=? LIMIT 1",
            SELECT_ALL_STR = "SELECT * FROM examples",
            DELETE_STR = "DELETE FROM examples WHERE id=?",
            SEARCH_STR = "SELECT * FROM examples WHERE (LOWER(text) LIKE ? OR LOWER(gloss) LIKE ? OR LOWER(pronunciation) LIKE ? OR "
                + "LOWER(literal_translation) LIKE ? OR LOWER(free_translation) LIKE ? OR LOWER(explanation) LIKE ?)";

    public final long ID;
    public final String text, gloss, pronunciation, literal_translation, free_translation, explanation;

    private Example(ResultSet resultSet) throws SQLException {
        ID = resultSet.getLong(1);
        text = resultSet.getString(2);
        gloss = resultSet.getString(3);
        pronunciation = resultSet.getString(4);
        literal_translation = resultSet.getString(5);
        free_translation = resultSet.getString(6);
        explanation = resultSet.getString(7);
    }

    public static Example create() throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        long id = Project.getProject().getDatabase().getNextID("examples");
        statement.setLong(1, id);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(id);
    }

    public Example updateText(String newText) throws SQLException {
        if(newText == null || text.equals(newText))
            return this;
        return updateStr(UPDATE_TEXT_STR, newText);
    }
    public Example updateGloss(String newGloss) throws SQLException {
        if(newGloss == null || gloss.equals(newGloss))
            return this;
        return updateStr(UPDATE_GLOSS_STR, newGloss);
    }
    public Example updatePronunciation(String newPronunciation) throws SQLException {
        if(newPronunciation == null || pronunciation.equals(newPronunciation))
            return this;
        return updateStr(UPDATE_PRON_STR, newPronunciation);
    }
    public Example updateLiteralTranslation(String newTranslation) throws SQLException {
        if(newTranslation == null || free_translation.equals(newTranslation))
            return this;
        return updateStr(UPDATE_LIT_STR, newTranslation);
    }
    public Example updateFreeTranslation(String newTranslation) throws SQLException {
        if(newTranslation == null || free_translation.equals(newTranslation))
            return this;
        return updateStr(UPDATE_FREE_STR, newTranslation);
    }
    public Example updateExplanation(String newExplanation) throws SQLException {
        if(newExplanation == null || explanation.equals(newExplanation))
            return this;
        return updateStr(UPDATE_EXPL_STR, newExplanation);
    }

    private Example updateStr(String sql, String newValue) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(sql);
        statement.setString(1, newValue);
        statement.setLong(2, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    @Override
    public String toString(){
        if(!text.isEmpty())
            return text;
        if(!gloss.isEmpty())
            return gloss;
        if(!free_translation.isEmpty())
            return free_translation;
        return String.format("Example #%d", ID);
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Example){
            Example other = (Example) o;
            if(ID == other.ID && text.equals(other.text) && gloss.equals(other.gloss)
                    && pronunciation.equals(other.pronunciation)
                    && literal_translation.equals(other.literal_translation)
                    && free_translation.equals(other.free_translation)
                    && explanation.equals(other.explanation))
                return true;
        }
        return false;
    }

    public static Example fetch(long id) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ONE_STR);
        statement.setLong(1, id);
        try(ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next())
                return new Example(resultSet);
        }
        return null;
    }
    public static void delete(Example example) throws SQLException {
        if(example != null) {
            PreparedStatement statement = Project.getProject().getDatabase().sql(DELETE_STR);
            statement.setLong(1, example.ID);
            statement.executeUpdate();
            Project.getProject().markSaveNeeded();
        }
    }

    public static ObservableList<Example> search(String text) throws SQLException {
        ObservableList<Example> list = FXCollections.observableArrayList();
        PreparedStatement statement = Project.getProject().getDatabase().sql(SEARCH_STR);
        text = "%" + text.toLowerCase() + "%";
        statement.setString(1, text);
        statement.setString(2, text);
        statement.setString(3, text);
        statement.setString(4, text);
        statement.setString(5, text);
        statement.setString(6, text);

        try(ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next())
                list.add(new Example(resultSet));
        }
        return list;
    }

    public void updateAll(String text, String gloss, String pronunciation, String literal_translation, String free_translation, String explanation) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_STR);
        statement.setString(1, text);
        statement.setString(2, gloss);
        statement.setString(3, pronunciation);
        statement.setString(4, literal_translation);
        statement.setString(5, free_translation);
        statement.setString(6, explanation);
        statement.setLong(7, ID);
        statement.executeUpdate();
    }
}
