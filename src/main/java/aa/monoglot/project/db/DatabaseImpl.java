package aa.monoglot.project.db;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

final class DatabaseImpl {
    private Connection connection;
    private Path workingDirectory;
    private final HashMap<String, PreparedStatement> SQL_STATEMENTS = new HashMap<>();

    public DatabaseImpl(Path workingDirectory) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.h2.Driver");
        this.workingDirectory = workingDirectory;
        connection = createConnection();
        connection.commit();
    }

    Connection getConnection() throws SQLException {
        if(connection == null)
            connection = createConnection();
        return connection;
    }

    private Connection createConnection() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:h2:file:" + workingDirectory.toString() + "/db;INIT=RUNSCRIPT FROM 'classpath:sql/create-tables.sql'");
        c.setAutoCommit(false);
        return c;
    }

    void close() throws SQLException {
        if(connection != null) {
            connection.commit();

            // close all open prepared statements.
            for(String sql: SQL_STATEMENTS.keySet())
                SQL_STATEMENTS.get(sql).close();
            SQL_STATEMENTS.clear();

            // finish up
            connection.close();
            connection = null;
        }
    }

    void flush() throws SQLException {
        connection.commit();
    }

    PreparedStatement getStatement(String sql) throws SQLException {
        PreparedStatement p = SQL_STATEMENTS.get(sql);
        if(p == null)
            SQL_STATEMENTS.put(sql, p = getConnection().prepareStatement(sql));
        return p;
    }
}
