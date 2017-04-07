package aa.monoglot.project.db;

import java.util.UUID;

/**
 * Created by Darren on 3/13/17.
 */
public class Category {
    private static class Node {

        private Node parent;
        private String name;

        public Node(String n) {
            parent = null;
            name = n;
        }

        public void setParent(Node p) {
            this.parent = p;
        }

        public Node getParent() {
            return this.parent;
        }

        public void setName(String n) {
            this.name = n;
        }

        public String getName() {
            return name;
        }
    }

    static final String INSERT_STR = "INSERT INTO tags VALUES (?, ?, ?, ?, ?)";
    static final String UPDATE_STR = "UPDATE tags SET id=?, name=?, full_name=?, parent_category=?, description=?";
    static final String SELECT_STR = "SELECT * FROM categories WHERE id = ?";

    private final static int ID_COL = 1, NAME_COL = 2, FNAME_COL = 3, PARCAT_COL = 4, DESC_COL = 5;

    public static  UUID id = null;
    public static String name = null, full_name = null, parent_category = null, description = null;

    @Override
    public String toString() {
        return parent_category + "<" + name;
    }

    public String shortName() {
        return name;
    }
}