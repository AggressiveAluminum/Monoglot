import java.util.ArrayList;

/**
 * Created by Darren on 3/13/17.
 */
public class Categories {

    private static class Node {

        public ArrayList<Node> children;

        private Node parent;
        private String name;

        public Node(String n) {
            parent = null;
            name = n;
            children = null;
        }

        public void setParent(Node p) {
            this.parent = p;
        }

        public Node getParent() {
            return this.parent;
        }

        public void addChild(Node c) {
            children.add(c);
        }

        public ArrayList<Node> getChildren() {
            return children;
        }

        public void setName(String n) {
            this.name = n;
        }

        public String getName() {
            return name;
        }
    }

    public static void add(String n) {
        Node node = new Node(n);
    }

    @Override
    public String toString() {
        //tree
    }

    @Override
    public String shortName() {
        return name;
    }
}