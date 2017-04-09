package aa.monoglot.project.db;

import aa.monoglot.project.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS tags (
 *     -- so people can say stuff like "TODO"
 *     id INT8 PRIMARY KEY,
 *     name VARCHAR NOT NULL,
 *     description VARCHAR NOT NULL
 * );
 * </kbd>
 */
public class Tag {
    private static final String INSERT_STR = "INSERT INTO tags VALUES (?, ?, '')",
            UPDATE_NAME_STR = "UPDATE tags SET name=? WHERE id=?",
            UPDATE_DESC_STR = "UPDATE tags SET description=? WHERE id=?",
            SELECT_ONE_STR = "SELECT * FROM tags WHERE id=? LIMIT 1",
            SELECT_ALL_STR = "SELECT * FROM tags",
            DELETE_STR = "DELETE FROM tags WHERE id=?";
    public final long ID;
    public final String name, description;

    private Tag(ResultSet resultSet) throws SQLException {
        ID = resultSet.getLong(1);
        name = resultSet.getString(2);
        description = resultSet.getString(3);
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Tag){
            Tag other = (Tag) o;
            if(ID == other.ID && name.equals(other.name) && description.equals(other.description))
                return true;
        }
        return false;
    }

    public static Tag create(String name) throws SQLException {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        long id = Project.getProject().getDatabase().getNextID("tags");
        statement.setLong(1, id);
        statement.setString(2, name);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(id);
    }

    public Tag updateName(String newName) throws SQLException {
        if(newName == null || newName.equals(name))
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_NAME_STR);
        statement.setString(1, newName);
        statement.setLong(2, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }
    public Tag updateDescription(String newDescription) throws SQLException {
        if(newDescription == null || newDescription.isEmpty())
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_DESC_STR);
        statement.setString(1, newDescription);
        statement.setLong(2, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    public static Tag fetch(long id) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ONE_STR);
        statement.setLong(1, id);
        try(ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next())
                return new Tag(resultSet);
        }
        return null;
    }

    public static void delete(Tag tag) throws SQLException {
        if(tag != null) {
            PreparedStatement statement = Project.getProject().getDatabase().sql(DELETE_STR);
            statement.setLong(1, tag.ID);
            statement.executeUpdate();
            Project.getProject().markSaveNeeded();
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

    public static List<Tag> fetchFor(Headword word) {
        //TODO
        return Collections.emptyList();
    }
}