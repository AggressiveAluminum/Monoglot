package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import aa.monoglot.util.UT;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * <kbd>
 * CREATE TABLE IF NOT EXISTS definition (
 *    id UUID PRIMARY KEY,
 *    entry_id UUID NOT NULL,
 *    prev_definition UUID,
 *    text VARCHAR NOT NULL,
 *    created TIMESTAMP NOT NULL,
 *    modified TIMESTAMP
 * );
 * </kbd>
 */
public class Definition {
    static final String INSERT_STR = "INSERT INTO definition VALUES (?, ?, ?, \"\", ?, NULL)";
    static final String UPDATE_STR = "";
    static final String SELECT_SINGLE_STR = "SELECT * FROM defintion WHERE id = ?";
    static final String SELECT_ALL_STR = "SELECT * FROM definition WHERE entry_id = ?";
    static final String UPDATE_ORDER = "UPDATE definition SET prev_definition = ?, modified = ? WHERE id = ?;" +
            "UPDATE definition SET prev_definition = ?, modified = ? WHERE id = ?;" +
            "UPDATE definition SET prev_definition = ?, modified = ? WHERE id = ?";

    private static final int ID_COL = 1, ENTRY_ID_COL = 2, NEXT_ID_COL = 3, TEXT_COL = 4,
        CREATED_COL = 5, MODIFIED_COL = 6;

    public final UUID ID, headwordID, prevID;
    public final String text;
    public final Timestamp created, modified;

    public static Definition create(Headword word, Definition previous) throws SQLException {
        PreparedStatement statement = Project.getProject().getDatabase().sql(INSERT_STR);
        UUID id = Project.getProject().getDatabase().getNextID();
        statement.setObject(1, id);
        statement.setObject(2, word.ID);
        statement.setObject(3, previous.ID);
        statement.setTimestamp(4, Timestamp.from(Instant.now()));
        statement.execute();

        statement = Project.getProject().getDatabase().sql(SELECT_SINGLE_STR);
        statement.setObject(1, id);
        try(ResultSet resultSet = statement.executeQuery()){
            resultSet.next();
            return new Definition(resultSet);
        }
    }
    Definition(UUID id, UUID headwordID, UUID prevID, String text, Timestamp created, Timestamp modified){
        this.ID = id;
        this.headwordID = headwordID;
        this.prevID = prevID;
        this.text = text;
        this.created = created;
        this.modified = modified;
    }
    Definition(ResultSet resultSet) throws SQLException {
        ID = (UUID) resultSet.getObject(ID_COL);
        headwordID = (UUID) resultSet.getObject(ENTRY_ID_COL);
        text = resultSet.getString(TEXT_COL);
        created = resultSet.getTimestamp(CREATED_COL);

        Object temp;

        temp = resultSet.getObject(NEXT_ID_COL);
        prevID = resultSet.wasNull()?null:(UUID) temp;

        temp = resultSet.getObject(MODIFIED_COL);
        modified = resultSet.wasNull()?null:(Timestamp) temp;
    }

    public final Definition update(String newText){
        Definition newDefinition = new Definition(ID, headwordID, prevID, UT.c(newText), created, Timestamp.from(Instant.now()));
        if(this.equals(newDefinition))
            return this;
        return newDefinition;
    }

    /**
     * Shifts a definition up one position in the list.
     */
    public static void reorder(Definition before, Definition after) throws SQLException {
        if(before == null && after == null)
            return;
        // Started with: w <- x <- y <- z; swapping x and y
        PreparedStatement statement = Project.getProject().getDatabase().sql(UPDATE_ORDER);
        Timestamp updatedTime = Timestamp.from(Instant.now());
        // y -> w
        if(before == null || before.prevID == null)
            statement.setNull(1, Types.OTHER);
        else statement.setObject(1, before.prevID);
        statement.setObject(2, ID);
        statement.setTimestamp(3, updatedTime);
        // x -> y
        if(before != null) {
            statement.setObject(4, ID);
            statement.setObject(5, before.ID);
            statement.setTimestamp(6, updatedTime);
            // z -> x
            if(after != null) {
                statement.setObject(7, before.ID);
                statement.setObject(8, after.ID);
                statement.setTimestamp(9, updatedTime);
            }
        }

        statement.execute();
    }

    public static List<Definition> fetch(Headword word) throws SQLException {
        if(word.ID == null)
            throw new IllegalArgumentException("Headword must be saved in the DB before modifying its definitions.");
        ArrayList<Definition> definitions = new ArrayList<>();
        LinkedList<Definition> temp = new LinkedList<>();
        PreparedStatement statement = Project.getProject().getDatabase().sql(SELECT_ALL_STR);
        statement.setObject(1, word.ID);
        try(ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next())
                temp.add(new Definition(resultSet));
        }
        int size = temp.size();
        UUID previous = null;
        for(int i = 0; i < size; i++) for(Definition d: temp)
            if(d.prevID == previous){
                definitions.add(d);
                previous = d.ID;
                temp.remove(d);
            }
        return definitions;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Definition){
            Definition other = (Definition) o;
            if(UT.nc(ID, other.ID)
                    && headwordID.equals(other.headwordID)
                    && UT.nc(prevID, other.prevID)
                    && text.equals(other.text)
                    && created.equals(other.created))
                return true;
        }
        return false;
    }
}
