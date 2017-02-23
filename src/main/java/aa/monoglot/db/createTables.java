package aa.monoglot.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Matt on 2/22/17.
 */
public class createTables {

    public void main(String [] args){
        String createTableEntry = "CREATE TABLE IF NOT EXISTS entry (\n" +
                "  id INT PRIMARY KEY\n" +
                "  word VARCHAR NOT NULL,\n" +
                "  romanization VARCHAR,\n" +
                "  pronunciation VARCHAR,\n" +
                "  stem VARCHAR,\n" +
                "  type INT,\n" +
                "  category INT,\n" +
                "  created TIMESTAMP NOT NULL,\n" +
                "  modified TIMESTAMP\n" +
                ");";

        String createTableDefinition = "CREATE TABLE IF NOT EXISTS definition (\n" +
                "  id INT PRIMARY KEY,\n" +
                "  entry_id INT NOT NULL,\n" +
                "  previous_definition INT,\n" +
                "  next_definition INT,\n" +
                "  text TEXT,\n" +
                "  created TIMESTAMP NOT NULL,\n" +
                "  modified TIMESTAMP\n" +
                ");";
        String createTableTypes = "CREATE TABLE IF NOT EXISTS types (\n" +
                "  -- stem/root/affix/etc\n" +
                "  -- should we consider making this a fixed set?\n" +
                "  id INT PRIMARY KEY,\n" +
                "  name VARCHAR NOT NULL,\n" +
                "  description VARCHAR\n" +
                ");";

        String createTableCategories = "CREATE TABLE IF NOT EXISTS categories (\n" +
                "  -- part-of-speech.\n" +
                "  -- for noun classes/verb conjugation paradigms/etc, have the subcategory\n" +
                "  --   with a \"pointer\" to its parent\n" +
                "  -- it may be worth it to keep this as a tree in-memory?\n" +
                "  --  as strings for the UI: (examples)\n" +
                "  --    `noun` name: noun\n" +
                "  --    `noun:G1` or `noun:neuter`; name: G1 or neuter, NOT the UI string\n" +
                "  --    `noun:G1:from greek`; name: \"from greek\"\n" +
                "  id INT PRIMARY KEY,\n" +
                "  name VARCHAR NOT NULL,\n" +
                "  parent_category INT,\n" +
                "  description VARCHAR\n" +
                ");";

        String createTableTags = "CREATE TABLE IF NOT EXISTS tags (\n" +
                "  -- so people can say stuff like \"TODO\"\n" +
                "  id INT PRIMARY KEY,\n" +
                "  name VARCHAR NOT NULL,\n" +
                "  description VARCHAR\n" +
                ");";

        String createTableRel_tag_entry = "CREATE TABLE IF NOT EXISTS rel_tag_entry (\n" +
                "  -- yes, I'm pretty sure this is the best way to do it in SQL.\n" +
                "  -- variable arrays != performant, extra tables w/ relations == performant\n" +
                "  row_id INT PRIMARY KEY,\n" +
                "  tag_id INT,\n" +
                "  entry_id INT\n" +
                ");";

        String createTableRel_tag_definition = "CREATE TABLE IF NOT EXISTS rel_tag_definition (\n" +
                "  row_id INT PRIMARY KEY,\n" +
                "  tag_id INT,\n" +
                "  definition_id INT\n" +
                ");";

        createTables(createTableEntry);
        createTables(createTableDefinition);
        createTables(createTableTypes);
        createTables(createTableCategories);
        createTables(createTableTags);
        createTables(createTableRel_tag_entry);
        createTables(createTableRel_tag_definition);

    }

    private void createTables(String query) {
        Connection conn = data_io.dbOpenConn();
        Statement queryString = null;
        try {
            conn.setAutoCommit(false);
            queryString = conn.createStatement();
            queryString.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (queryString != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                }
            }
        }
    }

