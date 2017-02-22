package aa.monoglot.db;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Alex on 2/18/17.
 */
public class Database {
    public static void main(String... args) throws ClassNotFoundException {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(getConnString(Paths.get("~/Documents")));
//
//            Statement stmt = null;
//            String query = "create table headword()";
//

            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * creating a path string for connection
     * @param dir
     */
    private static String getConnString(Path dir) {
        return "jdbc:h2:file:" + dir.toAbsolutePath().toString().replaceAll("\\\\", "/") + "/db";
    }
}


