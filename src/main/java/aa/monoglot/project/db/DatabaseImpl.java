package aa.monoglot.project.db;

import org.h2.tools.RunScript;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        Connection c = DriverManager.getConnection("jdbc:h2:" + workingDirectory.toString() + "/db");
        c.setAutoCommit(false);
        return c;
    }

    void close() throws SQLException {
        if(connection != null) {
            connection.commit();
            connection.close();
        }
    }

    void flush() throws SQLException {
        connection.commit();
    }

    void doSQLAction(SQLAction action) throws SQLException {
        action.execute();
    }
}
