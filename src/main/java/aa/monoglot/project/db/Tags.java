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
     CREATE TABLE IF NOT EXISTS tags (
     -- so people can say stuff like "TODO"
     id UUID PRIMARY KEY,
     name VARCHAR NOT NULL,
     description VARCHAR NOT NULL
     );
     */

    static final String INSERT_STR = "INSERT INTO tags VALUES (?, ?, ?)";
    static final String UPDATE_STR = "UPDATE tags SET id=?, name=?, description=?";
    static final String SELECT_STR = "SELECT * FROM tags WHERE id = ?";

    private final static int ID_COL = 1, NAME_COL = 2, DESC_COL = 3;

    public  UUID id = null;
    public String name;
    public String description;

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

    public Tags createEmtpyTag(){
        return new Tags(null, null, null);
    }

    public Tags insert( UUID id, Tags tag) throws SQLException {

        Project.getProject().markSaveNeeded();
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        /*ID   */
        if ( id == null)
            throw new IllegalArgumentException("ID cannot be empty.");
        else statement.setObject(ID_COL, id);
        /*name */
        if(tag.name.equals(null))
            throw new IllegalArgumentException("Name cannot be empty.");
        else statement.setString(NAME_COL, tag.name);
        /*Description  */
        if(tag.description.equals(null))
            throw new IllegalArgumentException("Description cannot be empty.");
        else statement.setString(DESC_COL, tag.description);



        return new Tags(statement.executeQuery());
    }

    public final Tags update(UUID id, String newName, String newDescription) throws SQLException{

        if ( id == null)
            throw new IllegalArgumentException("ID cannot be empty.");
        if(newName.equals(null))
            throw new IllegalArgumentException("Name cannot be empty.");
        if(newDescription.equals(null))
            throw new IllegalArgumentException("A description cannot be empty.");

        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_STR);
        statement.setObject(ID_COL, id);
        statement.setString(NAME_COL, newName);
        statement.setString(DESC_COL, newDescription);
        statement.executeUpdate();

        return new Tags(id, newName ,newDescription);
    }

    public static Tags fetch(UUID id) throws SQLException {

        PreparedStatement stmt =  Project.getProject().getDatabase().sql(SELECT_STR);
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