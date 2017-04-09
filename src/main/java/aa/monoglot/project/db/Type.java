package aa.monoglot.project.db;

import aa.monoglot.project.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 *<kbd>
 * CREATE TABLE IF NOT EXISTS types (
 *     -- stem/root/affix/etc
 *     -- should we consider making this a fixed set?
 *     ID INT8 PRIMARY KEY,
 *     name VARCHAR NOT NULL,
 *     description VARCHAR NOT NULL
 * );
 * </kbd>
 */
public class Type {
    private static final String INSERT_STR = "INSERT INTO types VALUES (?, ?, '')";
    private static final String UPDATE_NAME_STR = "UPDATE types SET name=? WHERE ID=?";
    private static final String UPDATE_DESC_STR = "UPDATE types SET name=? WHERE ID=?";
    private static final String SELECT_ONE_STR = "SELECT * FROM types WHERE ID=?";
    private static final String SELECT_ALL_STR = "SELECT * FROM types";

    public final long ID;
    public final String name, description;

    private Type(){ID = 0;name = "";description = null;}
    private Type(ResultSet resultSet) throws SQLException {
        ID = resultSet.getLong(1);
        name = resultSet.getString(2);
        description = resultSet.getString(3);
    }
    public static Type create(String name) throws SQLException {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        long id = Project.getProject().getDatabase().getNextID("types");
        statement.setLong(1, id);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(id);
    }

    public Type updateName(String newName) throws SQLException {
        if(newName == null || newName.equals(name))
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_NAME_STR);
        statement.setString(1, newName);
        statement.setLong(2, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }
    public Type updateDescription(String newDescription) throws SQLException {
        if(newDescription == null || newDescription.isEmpty())
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_DESC_STR);
        statement.setString(1, newDescription);
        statement.setLong(2, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o){
        if(o == null || !(o instanceof Type))
            return false;
        return ID == ((Type) o).ID;
    }

    public static Type fetch(long id) throws SQLException {
        PreparedStatement statement =  Project.getProject().getDatabase().sql(SELECT_ONE_STR);
        statement.setLong(1, id);
        try(ResultSet resultSet = statement.executeQuery()){
            if (resultSet.next())
                return new Type(resultSet);
        }
        return null;
    }

    public static List<Type> fetchAll() throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ALL_STR);
        List<Type> types = new ArrayList<>();
        try(ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next())
                types.add(new Type(resultSet));
        }
        return types;
    }

    public static void populateDefaults(final Database database) throws SQLException {
        database.sql("INSERT INTO types VALUES (0,'root',''), (1,'stem',''), (2,'affix',''), (3, 'bound morpheme',''), (4, 'clitic', '')").executeUpdate();
    }
}


