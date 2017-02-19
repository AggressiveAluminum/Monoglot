package aa.monoglot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Matt
 */
public class Database {
    private Connection dbConnection;

    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        dbConnection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
    }

    public void close() throws SQLException {
        dbConnection.close();
    }
}
