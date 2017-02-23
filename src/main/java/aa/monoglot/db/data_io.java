package aa.monoglot.db;

import java.sql.*;
import java.util.ArrayDeque;
import java.util.Properties;

/**
 * Created by Matt on 2/20/17.
 */
public class data_io {

    boolean paused;
    private ArrayDeque queue;
    Connection conn = null;

    public data_io() {
        queue = new ArrayDeque();
        paused = false;
    }

    public static Connection dbOpenConn(){
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
             conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void dbCloseConn(Connection conn){
        try {
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pauseDB(){
        if ( conn != null ){
            dbCloseConn(conn);
        }
    }

    public void resumeDB(){
        conn = dbOpenConn();
    }

    public void addSQLAction(String query){
        queue.add(query);
    }

    public ResultSet doSQLAction(String action){

        Connection conn = dbOpenConn();
        Statement query = null;
        ResultSet results = null;

        try{

            conn.setAutoCommit(false);
            query = conn.createStatement();
            results = query.executeQuery(action);

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return results;
    }
}
