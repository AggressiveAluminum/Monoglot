package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.*;
import java.time.Instant;
import java.util.*;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS definition (
 *    id UUID PRIMARY KEY,
 *    entry_id UUID,
 *    def_order INT NOT NULL,
 *    text VARCHAR NOT NULL,
 *    created TIMESTAMP NOT NULL,
 *    modified TIMESTAMP,
 *    FOREIGN KEY(entry_id) REFERENCES entry(id) ON DELETE CASCADE ON UPDATE CASCADE
 * );
 * </kbd>
 */
public class Definition {
    static final String INSERT_STR = "INSERT INTO definition VALUES (?, ?, ?, '', ?, NULL)";
    static final String UPDATE_TEXT_STR = "UPDATE definition SET text = ?, modified = ? WHERE id = ?";
    static final String UPDATE_ORDER_STR = "UPDATE definition SET def_order = ? WHERE def_order = ? AND entry_id = ?; UPDATE definition SET def_order = ? WHERE id = ?";
    static final String SELECT_SINGLE_STR = "SELECT * FROM definition WHERE id = ?";
    static final String SELECT_ALL_STR = "SELECT * FROM definition WHERE entry_id = ?";

    private static final int ID_COL = 1, ENTRY_ID_COL = 2, ORDER_COL = 3, TEXT_COL = 4,
        CREATED_COL = 5, MODIFIED_COL = 6;

    public final UUID ID, headwordID;
    public final int order;
    public final String text;
    public final Timestamp created, modified;

    public static Definition create(Headword word, Definition previous) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        UUID id = Project.getProject().getDatabase().getNextID();
        statement.setObject(1, id);
        statement.setObject(2, word.ID);
        statement.setInt(3, previous == null?1:previous.order + 1);
        statement.setTimestamp(4, Timestamp.from(Instant.now()));
        statement.execute();

        statement = Project.getProject().getDatabase().sql(SELECT_SINGLE_STR);
        statement.setObject(1, id);
        try(ResultSet resultSet = statement.executeQuery()){
            resultSet.next();
            return new Definition(resultSet);
        }
    }
    private Definition(UUID id, UUID headwordID, int order, String text, Timestamp created, Timestamp modified){
        this.ID = id;
        this.headwordID = headwordID;
        this.order = order;
        this.text = text;
        this.created = created;
        this.modified = modified;
    }
    private Definition(ResultSet resultSet) throws SQLException {
        ID = (UUID) resultSet.getObject(ID_COL);
        headwordID = (UUID) resultSet.getObject(ENTRY_ID_COL);
        order = resultSet.getInt(ORDER_COL);
        text = resultSet.getString(TEXT_COL);
        created = resultSet.getTimestamp(CREATED_COL);

        Timestamp temp = (Timestamp) resultSet.getObject(MODIFIED_COL);
        modified = resultSet.wasNull()?null:temp;
    }

    public Definition update(String newText) throws SQLException{
        if(text.equals(newText))
            return this;
        Project.getProject().markSaveNeeded();
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_TEXT_STR);
        statement.setString(1, newText);
        statement.setTimestamp(2, Timestamp.from(Instant.now()));
        statement.setObject(3, ID);
        statement.executeUpdate();
        return new Definition(ID, headwordID, order, UT.c(newText), created, Timestamp.from(Instant.now()));
    }
    public List<Definition> update(int newOrder) throws SQLException {
        if(newOrder < 1)
            throw new IllegalArgumentException();
        if(newOrder == order)
            return fetch(headwordID);
        Project.getProject().markSaveNeeded();
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_ORDER_STR);
        statement.setInt(1, order);
        statement.setInt(2, newOrder);
        statement.setObject(3, headwordID);
        statement.setInt(4, newOrder);
        statement.executeUpdate();
        return fetch(headwordID);
    }

    public static List<Definition> fetch(Headword word) throws SQLException {
        if(word.ID == null)
            throw new IllegalArgumentException("Headword must be saved in the DB before modifying its definitions.");
        return fetch(word.ID);
    }
    private static List<Definition> fetch(UUID wordID) throws SQLException {
        ArrayList<Definition> definitions = new ArrayList<>();
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ALL_STR);
        statement.setObject(1, wordID);
        try(ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next())
                definitions.add(new Definition(resultSet));
        }
        definitions.sort(Comparator.comparingInt(e->e.order));
        return definitions;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Definition){
            Definition other = (Definition) o;
            if(UT.nc(ID, other.ID)
                    && order == other.order
                    && headwordID.equals(other.headwordID)
                    && text.equals(other.text)
                    && created.equals(other.created))
                return true;
        }
        return false;
    }
}
