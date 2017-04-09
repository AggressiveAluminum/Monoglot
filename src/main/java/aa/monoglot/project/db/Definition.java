package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS definition (
 *    ID UUID PRIMARY KEY,
 *    entry_id UUID,
 *    def_order INT NOT NULL,
 *    text VARCHAR NOT NULL,
 *    created TIMESTAMP NOT NULL,
 *    modified TIMESTAMP,
 *    FOREIGN KEY(entry_id) REFERENCES entry(ID) ON DELETE CASCADE ON UPDATE CASCADE
 * );
 * </kbd>
 */
public class Definition {
    private static final String INSERT_STR = "INSERT INTO definition VALUES (?, ?, ?, '', ?, NULL)",
            UPDATE_TEXT_STR = "UPDATE definition SET text = ?, modified = ? WHERE ID = ?",
            UPDATE_ORDER_STR1 = "UPDATE definition SET def_order = ? WHERE def_order = ? AND entry_id = ?",
            UPDATE_ORDER_STR2 = "UPDATE definition SET def_order = ? WHERE ID = ?",
            SELECT_SINGLE_STR = "SELECT * FROM definition WHERE ID = ?",
            SELECT_ALL_STR = "SELECT * FROM definition WHERE entry_id = ? ORDER BY def_order ASC",
            DELETE_STR = "DELETE FROM definition WHERE ID = ?",
            DELETE_STR2 = "UPDATE definition AS defs SET def_order=((SELECT def_order FROM definition WHERE definition.ID=defs.ID) - 1) WHERE def_order > ? AND entry_id = ?";

    final long ID, headwordID;
    public final int order;
    public final String text;
    public final Timestamp created, modified;

    public static Definition create(Headword word, Definition previous) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        Project.getProject().markSaveNeeded();
        long id = Project.getProject().getDatabase().getNextID("definition");
        statement.setLong(1, id);
        statement.setLong(2, word.ID);
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
    public static void delete(Definition definition) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(DELETE_STR);
        Project.getProject().markSaveNeeded();
        statement.setLong(1, definition.ID);
        statement.executeUpdate();

        statement = Project.getProject().getDatabase().sql(DELETE_STR2);
        statement.setInt(1, definition.order);
        statement.setLong(2, definition.headwordID);
        statement.executeUpdate();
    }
    private Definition(ResultSet resultSet) throws SQLException {
        ID = resultSet.getLong(1);
        headwordID = resultSet.getLong(2);
        order = resultSet.getInt(3);
        text = resultSet.getString(4);
        created = resultSet.getTimestamp(5);

        Timestamp temp = (Timestamp) resultSet.getObject(6);
        modified = resultSet.wasNull()?null:temp;
    }

    public Definition updateText(String newText) throws SQLException{
        if(newText == null || text.equals(newText))
            return this;
        Project.getProject().markSaveNeeded();
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_TEXT_STR);
        statement.setString(1, newText);
        statement.setTimestamp(2, Timestamp.from(Instant.now()));
        statement.setObject(3, ID);
        statement.executeUpdate();
        return fetch(ID);
    }
    public List<Definition> update(int newOrder) throws SQLException {
        if(newOrder < 1)
            throw new IllegalArgumentException();
        if(newOrder == order)
            return fetchFor(headwordID);
        Project.getProject().markSaveNeeded();
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_ORDER_STR1);
        statement.setInt(1, order);
        statement.setInt(2, newOrder);
        statement.setLong(3, headwordID);
        statement.executeUpdate();
        statement = Project.getProject().getDatabase().sql(UPDATE_ORDER_STR2);
        statement.setInt(1, newOrder);
        statement.setLong(2, ID);
        statement.executeUpdate();
        return fetchFor(headwordID);
    }

    private static Definition fetch(long id) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_SINGLE_STR);
        statement.setLong(1, id);
        try(ResultSet resultSet = statement.executeQuery()){
            if(resultSet.next())
                return new Definition(resultSet);
        }
        return null;
    }
    public static List<Definition> fetch(Headword word) throws SQLException {
        if(word == null)
            return Collections.emptyList();
        return fetchFor(word.ID);
    }
    private static List<Definition> fetchFor(long wordID) throws SQLException {
        List<Definition> definitions = new ArrayList<>();
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ALL_STR);
        statement.setLong(1, wordID);
        try(ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next())
                definitions.add(new Definition(resultSet));
        }
        return definitions;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Definition){
            Definition other = (Definition) o;
            if(ID == other.ID && order == other.order && headwordID == other.headwordID
                    && text.equals(other.text) && created.equals(other.created))
                return true;
        }
        return false;
    }
}
