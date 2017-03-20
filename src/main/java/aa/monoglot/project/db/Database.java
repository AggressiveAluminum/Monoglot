package aa.monoglot.project.db;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    private final static String SIMPLE_SEARCH_SQL = "SELECT * FROM entry WHERE word LIKE ? OR stem LIKE ? OR romanization LIKE ? OR pronunciation like ? ORDER BY word ASC";

    private final DatabaseImpl db;

    public Database(Path workingDirectory) throws ClassNotFoundException, IOException, SQLException {
        db = new DatabaseImpl(workingDirectory);
    }

    public void close() throws SQLException {
        db.flush();
        db.close();
    }

    public void flush() throws SQLException {
        db.flush();
    }

    public void open() throws  SQLException {
        db.getConnection();
    }

    public Headword put(Headword headword) throws SQLException {
        if(headword.ID == null){
            UUID id = db.getNextID();
            Headword.insert(db.getStatement(Headword.INSERT_STR), id, headword).execute();
            db.flush();
            return Headword.select(db.getStatement(Headword.SELECT_STR), id);
        } else {
            Headword.update(db.getStatement(Headword.UPDATE_STR), headword).executeUpdate();
            return headword;
        }
    }

    public List<Headword> simpleSearch(String searchText, Object type, Object category, Object[] tags) throws SQLException {
        ArrayList<Headword> list = new ArrayList<>();
        PreparedStatement statement = db.getStatement(SIMPLE_SEARCH_SQL);
        statement.setString(1, searchText);
        statement.setString(2, searchText);
        statement.setString(3, searchText);
        statement.setString(4, searchText);

        try(ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next())
                list.add(new Headword(resultSet));
        }
        return list;
    }

    Headword selectHeadword(UUID id) throws SQLException {
        return Headword.select(db.getStatement(Headword.SELECT_STR), id);
    }
}