package aa.monoglot.db;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * @author cofl
 * @date 2/22/2017
 *
 * TODO: integrate the changes from the DB team to work with this setup.
 */
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

    public Headword newHeadword(String word){
        //TODO
        return null;
    }

    public void put(final Headword headword) throws SQLException {
        //TODO
    }

    /*public void put(final Definition definition) throws SQLException {
        //TODO
    }
     */
}
