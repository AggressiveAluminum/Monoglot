package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 CREATE TABLE IF NOT EXISTS tags (
 -- so people can say stuff like "TODO"
 id UUID PRIMARY KEY,
 name VARCHAR NOT NULL,
 description VARCHAR NOT NULL
 );
 */
public class Tag {
    static final String INSERT_STR = "INSERT INTO tags VALUES (?, ?, ?)";
    static final String UPDATE_STR = "UPDATE tags SET id=?, name=?, description=?";
    static final String SELECT_STR = "SELECT * FROM tags WHERE id = ?";
    static final String SELECT_ALL_STR = "SELECT * FROM tags";

    private final static int ID_COL = 1, NAME_COL = 2, DESC_COL = 3;

    public  UUID id = null;
    public String name;
    public String description;

    Tag(ResultSet resultSet) throws SQLException {
        id = (UUID) resultSet.getObject(ID_COL);
        name = resultSet.getString(NAME_COL);
        description = resultSet.getString(DESC_COL);
    }


    Tag(UUID id, String name, String description){
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
        if(o != null && o instanceof Tag){
            Tag other = (Tag) o;

            if(UT.nc(id , other.id)
                    && name.equals(other.name)
                    && description.equals(other.description))
                return true;
        }
        return false;
    }

    public Tag createEmtpyTag(){
        return new Tag(null, null, null);
    }

    public Tag insert(UUID id, Tag tag) throws SQLException {

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



        return new Tag(statement.executeQuery());
    }

    public final Tag update(UUID id, String newName, String newDescription) throws SQLException{

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

        return new Tag(id, newName ,newDescription);
    }

    public static Tag fetch(UUID id) throws SQLException {

        PreparedStatement stmt = Project.getProject().getDatabase().sql(SELECT_STR);
        stmt.setObject(1, id);
        try(ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return new Tag(resultSet);
            } else {
                return null;
            }
        }

    }

    public static List<Tag> fetchAll() throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ALL_STR);
        List<Tag> tags = new ArrayList<>();
        try(ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next())
                tags.add(new Tag(resultSet));
        }
        return tags;
    }

    public static List<Tag> fetchFor(Headword activeWord) {
        //TODO
        return null;
    }
}