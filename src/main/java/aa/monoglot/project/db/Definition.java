package aa.monoglot.project.db;

import aa.monoglot.util.UT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
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
    static final String INSERT_STR = "";
    static final String UPDATE_STR = "";
    static final String SELECT_ALL_STR = "";

    private static final int ID_COL = 1, ENTRY_ID_COL = 2, NEXT_ID_COL = 3, TEXT_COL = 4,
        CREATED_COL = 5, MODIFIED_COL = 6;

    public final UUID ID, headwordID, prevID;
    public final String text;
    public final Timestamp created, modified;

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

    public static Definition create(Headword word, Definition previous){
        return new Definition(word, previous, Timestamp.from(Instant.now()));
    }
    Definition(Headword word, Definition previous, Timestamp created){
        this(null, word.ID, previous == null?null:previous.ID, null, created, null);
    }
    Definition(UUID id, UUID headwordID, UUID prevID, String text, Timestamp created, Timestamp modified){
        this.ID = id;
        this.headwordID = headwordID;
        this.prevID = prevID;
        this.text = text;
        this.created = created;
        this.modified = modified;
    }

    public final Definition update(String newText){
        Definition newDefinition = new Definition(ID, headwordID, prevID, UT.c(newText), created, Timestamp.from(Instant.now()));
        if(this.equals(newDefinition))
            return this;
        return newDefinition;
    }
    public final Definition reorder(UUID newPrevious){
        Definition newDefinition = new Definition(ID, headwordID, newPrevious, text, created, Timestamp.from(Instant.now()));
        if(this.equals(newDefinition))
            return this;
        return newDefinition;
    }

    @SuppressWarnings("ConstantConditions")
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
