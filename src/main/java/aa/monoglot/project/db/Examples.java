package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Matt on 4/1/17.
 */
public class Examples {

//    CREATE TABLE IF NOT EXISTS examples (
//            id UUID PRIMARY KEY,
//            text VARCHAR NOT NULL,
//            gloss VARCHAR NOT NULL,
//            pronunciation VARCHAR NOT NULL,
//            literal_translation VARCHAR NOT NULL,
//            free_translation VARCHAR NOT NULL,
//            explanation VARCHAR NOT NULL

        static final String INSERT_STR = "INSERT INTO examples VALUES (?, ?, ?, ?, ?, ?, ?)";

        static final String UPDATE_STR = "UPDATE examples SET id=?, text=?, gloss=?, pronunciation=?, " +
                "literal_translation=?, free_translation=?, explanation=?";

        static final String SELECT_STR = "SELECT * FROM examples WHERE id = ?";

        private final static int ID_COL = 1, TEXT_COL = 2, GLOSS_COL = 3, PRONUN_COL = 4, LITERAL_COL = 5,
                                FREE_COL = 6, EXPLA_COL = 7;

        public UUID id = null;
        public String text;
        public String gloss;
        public String pronunciation;
        public String literal_translation;
        public String free_translation;
        public String explanation;

        Examples(ResultSet resultSet) throws SQLException {
            id = (UUID) resultSet.getObject(ID_COL);
            text = resultSet.getString(TEXT_COL);
            gloss = resultSet.getString(GLOSS_COL);
            pronunciation = resultSet.getString(PRONUN_COL);
            literal_translation = resultSet.getString(LITERAL_COL);
            free_translation = resultSet.getString(FREE_COL);
            explanation = resultSet.getString(EXPLA_COL);
        }


        Examples(UUID id, String text, String gloss, String pronunciation, String literal_translation,
                 String free_translation, String explanation){
            this.id = id;
            this.text = text;
            this.gloss = gloss;
            this.pronunciation = pronunciation;
            this.literal_translation = literal_translation;
            this.free_translation = free_translation;
            this.explanation = explanation;
        }

        public final Examples update(String newText, String newGloss, String newPronun, String newLiteral, String newFree,
                                 String newExplanation) throws SQLException{
            Project.getProject().markSaveNeeded();
            PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_STR);
            Examples example;
            if(newText.equals(null))
                throw new IllegalArgumentException("Text cannot be empty.");
            if(newGloss.equals(null))
                throw new IllegalArgumentException("Gloss cannot be empty.");
            if ( newPronun.equals(null))
                throw new IllegalArgumentException("Pronunciation cannot be empty.");
            if ( newLiteral.equals(null))
                throw new IllegalArgumentException("Literal translation cannot be empty.");
            if ( newFree.equals(null))
                throw new IllegalArgumentException("Free translation cannot be empty.");
            if ( newExplanation.equals(null))
                throw new IllegalArgumentException("Explanation cannot be empty.");

            statement.setObject(ID_COL, id);
            statement.setString(TEXT_COL, newText);
            statement.setString(GLOSS_COL, newGloss);
            statement.setString(PRONUN_COL, newPronun);
            statement.setString(LITERAL_COL, newLiteral);
            statement.setString(FREE_COL, newFree);
            statement.setString(EXPLA_COL, newExplanation);

            statement.executeUpdate();

            return new Examples(id, newText, newGloss, newPronun, newLiteral, newFree, newExplanation);
        }

        @Override
        public String toString(){
            return text;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean equals(Object o){
            if(o != null && o instanceof Examples){
                Examples other = (Examples) o;

                if(UT.nc(id , other.id)
                        && text.equals(other.text)
                        && gloss.equals(other.gloss)
                        && pronunciation.equals(other.pronunciation)
                        && literal_translation.equals(other.literal_translation)
                        && free_translation.equals(other.free_translation)
                        && explanation.equals(other.explanation))
                    return true;
            }
            return false;
        }

        public Examples createEmtpyExample(){
            return new Examples(null, null, null, null, null, null, null);
        }

        private PreparedStatement insert(PreparedStatement statement, UUID id, Examples example) throws SQLException {

            Project.getProject().markSaveNeeded();
            statement = Project.getProject().getDatabase().sql(INSERT_STR);


             /*ID   */statement.setObject(ID_COL, id);

            if(text.equals(null))
                throw new IllegalArgumentException("Text cannot be empty.");
            else if(gloss.equals(null))
                throw new IllegalArgumentException("Gloss cannot be empty.");
            else if ( pronunciation.equals(null))
                throw new IllegalArgumentException("Pronunciation cannot be empty.");
            else if ( literal_translation.equals(null))
                throw new IllegalArgumentException("Literal translation cannot be empty.");
            else if ( free_translation.equals(null))
                throw new IllegalArgumentException("Free translation cannot be empty.");
            else if ( explanation.equals(null))
                throw new IllegalArgumentException("Explanation cannot be empty.");
            else{
                statement.setString(TEXT_COL, text);
                statement.setString(GLOSS_COL, gloss);
                statement.setString(PRONUN_COL, pronunciation);
                statement.setString(LITERAL_COL, literal_translation);
                statement.setString(FREE_COL, free_translation);
                statement.setString(EXPLA_COL, explanation);
            }

            return statement;
        }

        public static Examples fetch(UUID id) throws SQLException {

            PreparedStatement stmt =  Project.getProject().getDatabase().sql(SELECT_STR);
            stmt.setObject(1, id);
            try(ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Examples(resultSet);
                } else {
                    return null;
                }
            }

        }
}
