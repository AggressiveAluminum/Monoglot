package aa.monoglot.project.db;

import aa.monoglot.project.Project;

import java.awt.print.PrinterJob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                SELECT_ONE_STR = "SELECT * FROM examples WHERE id=? LIMIT 1",
                SELECT_ALL_STR = "SELECT * FROM examples",
                DELETE_STR = "DELETE FROM examples WHERE id=?";

        public final long ID;
        public final String text, gloss, pronunciation, literal_translation, free_translation, explanation;

        Example(ResultSet resultSet) throws SQLException {
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
            return text;
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

        public static List<Example> fetchAll() throws SQLException{
            PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ALL_STR);
            try(ResultSet resultSet = statement.executeQuery()){
                resultSet.next();
                List<Example> examples = new ArrayList<Example>();
                do{
                    examples.add(new Example(resultSet));
                } while (resultSet.next());
                return examples;
            }
        }

        public static void delete(Example example) throws SQLException {
            if(example != null) {
                PreparedStatement statement = Project.getProject().getDatabase().sql(DELETE_STR);
                statement.setLong(1, example.ID);
                statement.executeUpdate();
                Project.getProject().markSaveNeeded();
            }
        }
}
