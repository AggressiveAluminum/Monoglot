package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by Matt on 3/26/17.
 */
public class Tags{

        /**
         * CREATE TABLE IF NOT EXISTS types (
         -- stem/root/affix/etc
         -- should we consider making this a fixed set?
         id UUID PRIMARY KEY,
         name VARCHAR NOT NULL,
         description VARCHAR NOT NULL
         );
         */

        static final String INSERT_STR = "INSERT INTO tags VALUES (?, ?, ?)";
        static final String UPDATE_STR = "UPDATE tags SET id=?, name=?, description=?";
        static final String SELECT_STR = "SELECT * FROM tags WHERE id = ?";

        private final static int ID_COL = 1, NAME_COL = 2, DESC_COL = 3;

        public static  UUID id = null;
        public static String name = null, description = null;

        Tags(ResultSet resultSet) throws SQLException {
            id = (UUID) resultSet.getObject(ID_COL);
            name = resultSet.getString(NAME_COL);
            description = resultSet.getString(DESC_COL);
        }


        Tags(UUID id, String name, String description){
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public final Tags update(String newName, String newDescription){
            if(newName.equals(null))
                throw new IllegalArgumentException("A name cannot be empty.");
            if(newDescription.equals(null))
                throw new IllegalArgumentException("A description cannot be empty.");
            Tags tag = new Tags(this.id, newName, newDescription);

            return tag;
        }

        @Override
        public String toString(){
            return name;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean equals(Object o){
            if(o != null && o instanceof Tags){
                Tags other = (Tags) o;

                if(UT.nc(id , other.id)
                        && name.equals(other.name)
                        && description.equals(other.description))
                    return true;
            }
            return false;
        }

        static PreparedStatement insert(PreparedStatement statement, UUID id, Tags tag) throws SQLException {
        /*ID   */statement.setObject(ID_COL, id);
        /*name */statement.setString(NAME_COL, tag.name);
        /*description*/statement.setString(DESC_COL, tag.description);

        /*name */
            if(Tags.name == null)
                statement.setNull(NAME_COL, Types.OTHER);
            else statement.setObject(NAME_COL, Tags.name);
        /*CAT  */
            if(Tags.description == null)
                statement.setNull(DESC_COL, Types.OTHER);
            else statement.setObject(DESC_COL, Tags.description);

            return statement;
        }


        static PreparedStatement update(PreparedStatement statement, Tags tag) throws SQLException {
            return update(statement,Tags.id, tag);
        }


        static PreparedStatement update(PreparedStatement statement, UUID id, Tags Tag) throws SQLException {
            int order = 1;
            statement.setString(order++, Tags.name);
            statement.setString(order++, Tags.description);

            if(Tags.name == null)
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

        public static Tags select(PreparedStatement statement, UUID id) throws SQLException {
            statement.setObject(1, id);
            try(ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return new Tags(resultSet);
            }
        }

        public static Tags fetch(UUID id) throws SQLException {

            PreparedStatement stmt =  Project.getProject().getDatabase().sql("SELECT * FROM tags where id=?");
            stmt.setObject(1, id);
            try(ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Tags(resultSet);
                } else {
                    return null;
                }
            }

        }
    }