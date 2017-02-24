package aa.monoglot.db;

import javafx.util.Callback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author cofl
 * @date 2/23/2017
 */
class SQLAction {
    private final PreparedStatement statement;
    private final Callback<ResultSet, Void> callback;

    SQLAction(PreparedStatement statement, Callback<ResultSet, Void> callback){
        this.statement = statement;
        this.callback = callback;
    }

    void execute() throws SQLException {
        if(statement.execute() && callback != null)
            callback.call(statement.getResultSet());
        statement.getConnection().commit();
    }
}
