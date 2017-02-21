package aa.monoglot.db;

import java.nio.file.Path;
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
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
            //application code here
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getConnString(Path dir) {
        //creating a path string for connection
        String thing = dir.toString();
        thing.replaceAll("\\\\", "/");
        thing = thing + "/db";
        return "jdbc:h2:file:" + thing;
    }
}


