package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.*;
import java.util.UUID;

/**
 * Created by Darren on 3/13/17.
 */
public class Type {

    static final String INSERT_STR = "INSERT INTO types VALUES (?, ?, ?)";
    static final String UPDATE_STR = "UPDATE types SET id=?, name=?, description=?";
    static final String SELECT_STR = "SELECT * FROM types WHERE id = ?";

    private final static int ID_COL = 1, NAME_COL = 2, DESC_COL = 3;

    public static  UUID id = null;
    public static String name = null, description = null;

    Type(ResultSet resultSet) throws SQLException {

    }

    Type(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public final Type update(String newName, String newDescription) {
        if(newName.equals(null))
            throw new IllegalArgumentException("A name cannot be empty.");
        if(newDescription.equals(null))
            throw new IllegalArgumentException("A description cannot be empty.");
        Type type = new Type(this.id, newName, newDescription);

        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Type){
            Type other = (Type) o;

            if(UT.nc(id , other.id)
                    && name.equals(other.name)
                    && description.equals(other.description))
                return true;
        }
        return false;
    }

    static PreparedStatement insert(PreparedStatement statement, UUID id, Type type) throws SQLException {
        /*ID   */statement.setObject(ID_COL, id);
        /*name */statement.setString(NAME_COL, type.name);
        /*description*/statement.setString(DESC_COL, type.description);

        //name
        if(Type.name == null)
            statement.setNull(NAME_COL, java.sql.Types.OTHER);
        else statement.setObject(NAME_COL, Type.name);
        //description
        if(Type.description == null)
            statement.setNull(DESC_COL, java.sql.Types.OTHER);
        else statement.setObject(DESC_COL, Type.description);

        return statement;
    }


    static PreparedStatement update(PreparedStatement statement, Type type) throws SQLException {
        return update(statement, Type.id, type);
    }

    static PreparedStatement update(PreparedStatement statement, UUID id, Type type) throws SQLException {
        int order = 1;
        statement.setString(order++, Type.name);
        statement.setString(order++, Type.description);

        if(Type.name == null)
            statement.setNull(order++, java.sql.Types.OTHER);
        else statement.setObject(order++, type.name);
        if(Type.description == null)
            statement.setNull(order++, java.sql.Types.OTHER);
        else statement.setObject(order++, type.description);

        //UUID?
        statement.setObject(order, type.id);
        return statement;
    }

    public static Type select(PreparedStatement statement, UUID id) throws SQLException {
        statement.setObject(1, id);
        try(ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return new Type(resultSet);
        }
    }

    public static Type fetch(UUID id) throws SQLException {

        PreparedStatement stmt =  Project.getProject().getDatabase().sql("SELECT * FROM types where id=?");
        stmt.setObject(1, id);
        try(ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return new Type(resultSet);
            } else {
                return null;
            }
        }
    }
}


