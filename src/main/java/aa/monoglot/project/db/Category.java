package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.*;
import java.util.UUID;

/**
 * Created by Darren on 3/13/17.
 */
public class Category {

    static final String INSERT_STR = "INSERT INTO tags VALUES (?, ?, ?, ?, ?)";
    static final String UPDATE_STR = "UPDATE tags SET id=?, name=?, full_name=?, parent_category=?, description=?";
    static final String SELECT_STR = "SELECT * FROM categories WHERE id = ?";

    private final static int ID_COL = 1, NAME_COL = 2, FNAME_COL = 3, PARCAT_COL = 4, DESC_COL = 5;

    public static  UUID id = null, parent_category = null;
    public static String name = null, full_name = null, description = null;


    Category(ResultSet resultSet) throws SQLException {
        id = (UUID) resultSet.getObject(ID_COL);
        name = resultSet.getString(NAME_COL);
        full_name = resultSet.getString(FNAME_COL);
        parent_category = (UUID) resultSet.getObject(PARCAT_COL);
        description = resultSet.getString(DESC_COL);
    }


    Category(UUID id, String name, String full_name, UUID parent_category, String description){
        this.id = id;
        this.name = name;
        this.full_name = full_name;
        this.parent_category = parent_category;
        this.description = description;
    }

    @Override
    public String toString() {
        return parent_category + "<" + name;
    }

    public String shortName() {
        return name;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Category){
            Category other = (Category) o;

            if(UT.nc(id , other.id)
                    && name.equals(other.name)
                    && full_name.equals(other.full_name)
                    && UT.nc(parent_category, other.parent_category)
                    && description.equals(other.description))
                return true;
        }
        return false;
    }

    public Category createEmtpyCategory(){
        return new Category(null, null, null, null, null);
    }

    public Category insert(UUID id, Category category) throws SQLException {

        Project.getProject().markSaveNeeded();
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        //ID
        if (id == null)
            throw new IllegalArgumentException("ID cannot be empty.");
        else statement.setObject(ID_COL, id);
        //name
        if(category.name.equals(null))
            throw new IllegalArgumentException("Name cannot be empty.");
        else statement.setString(NAME_COL, category.name);
        //Full name
        if(category.full_name.equals(null))
            throw new IllegalArgumentException("Full Name cannont be empty");
        else statement.setString(FNAME_COL, category.full_name);
        //Parent category can be null

        //Description
        if(category.description.equals(null))
            throw new IllegalArgumentException("Description cannot be empty.");
        else statement.setString(DESC_COL, category.description);



        return new Category(statement.executeQuery());
    }

    public final Category update(UUID id, String newName, String newFullName, UUID parID, String newDescription) throws SQLException{

        if (id == null)
            throw new IllegalArgumentException("ID cannot be empty.");
        if(newName.equals(null))
            throw new IllegalArgumentException("Name cannot be empty.");
        if(newFullName.equals(null))
            throw new IllegalArgumentException("Full Name cannot be empty");
        if(newDescription.equals(null))
            throw new IllegalArgumentException("A description cannot be empty.");

        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_STR);
        statement.setObject(ID_COL, id);
        statement.setString(NAME_COL, newName);
        statement.setString(FNAME_COL, newFullName);
        statement.setObject(PARCAT_COL, parID);
        statement.setString(DESC_COL, newDescription);
        statement.executeUpdate();

        return new Category(id, newName, newFullName, parID ,newDescription);
    }


    public static Category fetch(UUID id) throws SQLException {

        PreparedStatement stmt =  Project.getProject().getDatabase().sql(SELECT_STR);
        stmt.setObject(1, id);
        try(ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return new Category(resultSet);
            } else {
                return null;
            }
        }
    }
}