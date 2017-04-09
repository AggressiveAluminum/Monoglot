package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS categories (
 *   -- part-of-speech.
 *   -- for noun classes/verb conjugation paradigms/etc, have the subcategory
 *   --   with a "pointer" to its parent
 *   -- it may be worth it to keep this as a tree in-memory?
 *   --  as strings for the UI: (examples)
 *   --    `noun` name: noun
 *   --    `noun:G1` or `noun:neuter`; name: G1 or neuter, NOT the UI string
 *   --    `noun:G1:from greek`; name: "from greek"
 *   ID INT8 PRIMARY KEY,
 *   name VARCHAR NOT NULL,
 *   full_name VARCHAR NOT NULL,
 *   parent_category INT8,
 *   description VARCHAR NOT NULL
 * );
 * </kbd>
 */
public class Category {
    private static final String INSERT_STR = "INSERT INTO categories VALUES (?, ?, ?, ?, '')";
    private static final String UPDATE_NAME_STR = "UPDATE categories SET name=?,full_name=? WHERE ID=?";
    private static final String UPDATE_DESC_STR = "UPDATE categories SET description=? WHERE ID=?";
    private static final String SELECT_STR = "SELECT * FROM categories WHERE ID = ?";
    private static final String DELETE_STR = "DELETE FROM categories WHERE ID = ?";
    private static final String SELECT_ALL_STR = "SELECT * FROM categories";

    public final long ID;
    public final Long parent_category;
    public final String name, full_name, description;

    private Category(ResultSet resultSet) throws SQLException {
        ID = resultSet.getLong(1);
        name = resultSet.getString(2);
        full_name = resultSet.getString(3);
        description = resultSet.getString(5);
        Long temp = resultSet.getLong(4);
        if(resultSet.wasNull())
            parent_category = null;
        else parent_category = temp;
    }

    @Override
    public boolean equals(Object o){
        if(o == null || !(o instanceof Category))
            return false;
        return ID == ((Category) o).ID;
    }

    public static Category create(String name, Category parent) throws SQLException {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        long id = Project.getProject().getDatabase().getNextID("categories");
        statement.setLong(1, id);
        statement.setString(2, name);
        if(parent != null) {
            statement.setString(3, parent.full_name + ":" + name);
            statement.setLong(4, parent.ID);
        } else {
            statement.setString(3, name);
            statement.setNull(4, Types.OTHER);
        }
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(id);
    }

    public void delete(Category category) throws SQLException {
        if(category != null){
            PreparedStatement statement = Project.getProject().getDatabase().sql(DELETE_STR);
            statement.setLong(1, category.ID);
            statement.executeUpdate();
            Project.getProject().markSaveNeeded();
        }
    }

    public Category updateName(String newName) throws SQLException {
        if(newName == null || newName.equals(name))
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_NAME_STR);
        statement.setString(1, newName);
        if(parent_category != null) {
            //noinspection ConstantConditions
            statement.setString(2, fetch(parent_category).full_name + ":" + newName);
        } else {
            statement.setString(2, newName);
        }
        statement.setLong(3, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }
    public Category updateDescription(String newDescription) throws SQLException {
        if(newDescription == null || newDescription.isEmpty())
            return this;
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_DESC_STR);
        statement.setString(1, newDescription);
        statement.setLong(2, ID);
        statement.executeUpdate();
        Project.getProject().markSaveNeeded();
        return fetch(ID);
    }

    public static Category fetch(long id) throws SQLException {
        PreparedStatement statement =  Project.getProject().getDatabase().sql(SELECT_STR);
        statement.setLong(1, id);
        try(ResultSet resultSet = statement.executeQuery()){
            if (resultSet.next())
                return new Category(resultSet);
        }
        return null;
    }

    public static List<Category> fetchAll() throws SQLException {
        List<Category> categories = new ArrayList<>();
        if(Project.isOpen()) {
            PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ALL_STR);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next())
                    categories.add(new Category(resultSet));
            }
        }
        return categories;
    }
}