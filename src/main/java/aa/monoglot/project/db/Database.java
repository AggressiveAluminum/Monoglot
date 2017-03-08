package aa.monoglot.project.db;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Database {
    private final DatabaseImpl db;

    public Database(Path workingDirectory) throws ClassNotFoundException, IOException, SQLException {
        db = new DatabaseImpl(workingDirectory);
    }

    public void close() throws SQLException {
        //TODO: ???
        db.close();
    }

    public void flush() throws SQLException {
        db.flush();
    }

    public Headword put(Headword headword) throws SQLException {
        if(headword.ID == null){
            UUID id = db.getNextID();
            Headword.insert(db.getStatement(Headword.INSERT_STR), id, headword).execute();
            Headword.update(db.getStatement(Headword.UPDATE_STR), id, headword).executeUpdate();
            return Headword.select(db.getStatement(Headword.SELECT_STR), id);
        } else {
            Headword.update(db.getStatement(Headword.UPDATE_STR), headword).executeUpdate();
            return headword;
        }
    }
}
