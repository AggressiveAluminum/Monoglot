package aa.monoglot.project.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Database {
    private final static String SIMPLE_SEARCH_SQL = "SELECT * FROM entry WHERE (word LIKE ? OR stem LIKE ? OR romanization LIKE ? OR pronunciation like ?) %s %s ORDER BY word ASC",
            SEARCH_TYPE_PART = "AND type = ?",
            SEARCH_CATEGORY_PART = "AND category = ?";

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

    PreparedStatement sql(String sql) throws SQLException {
        return db.getStatement(sql);
    }

    Long getNextID(String table) throws SQLException {
        PreparedStatement statement = sql("SELECT MAX(ID) FROM " + table);
        try(ResultSet resultSet = statement.executeQuery()){
            if(resultSet.next())
                return resultSet.getLong(1) + 1;
        }
        return 0L;
    }

    public ObservableList<Headword> simpleSearch(String searchText, Type type, Category category, List<Tag> tags) throws SQLException {
        ObservableList<Headword> list = FXCollections.observableArrayList();
        PreparedStatement statement = db.getStatement(String.format(SIMPLE_SEARCH_SQL, type == null?"":SEARCH_TYPE_PART, category == null?"":SEARCH_CATEGORY_PART));
        searchText = "%" + searchText + "%";
        int order = 1;
        statement.setString(order++, searchText);
        statement.setString(order++, searchText);
        statement.setString(order++, searchText);
        statement.setString(order++, searchText);
        if(type != null)
            statement.setObject(order++, type.ID);
        if(category != null)
            statement.setObject(order, category.ID);

        try(ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next())
                list.add(new Headword(resultSet));
        }
        return list;
    }
}
