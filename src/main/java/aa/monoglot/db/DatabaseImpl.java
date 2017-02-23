package aa.monoglot.db;

import org.h2.tools.RunScript;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author cofl
 * @date 2/23/2017
 */
final class DatabaseImpl {
    private Connection connection;
    private Path workingDirectory;

    public DatabaseImpl(Path workingDirectory) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.h2.Driver");
        this.workingDirectory = workingDirectory;
        init();
    }

    void init() throws SQLException, IOException {
        try(Reader createTables = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sql/create-tables.sql"))) {
            RunScript.execute(getConnection(), createTables);
        }
    }

    Connection getConnection() throws SQLException {
        if(connection == null)
            connection = createConnection();
        return connection;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:" + workingDirectory.toString() + "/db");
    }

    void close() throws SQLException {
        if(connection != null) {
            connection.commit();
            connection.close();
        }
    }
}
