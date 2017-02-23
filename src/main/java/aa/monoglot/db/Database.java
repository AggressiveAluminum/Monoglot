package aa.monoglot.db;

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

    public Database(Path workingDirectory) throws ClassNotFoundException {
        db = new DatabaseImpl(workingDirectory);
    }

    public void close() throws SQLException {
        //TODO: finalize
        db.close();
    }

    public void pause() {
        //TODO
    }

    public void resume(){
        //TODO
    }
}
