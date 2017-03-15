/**
 * Created by Darren on 3/13/17.
 */
public class Categories {
    private class TreeNode {
        String value = null;
        //TreeNode parent = null;
        TreeNode leftChild = null;
        TreeNode rightChild = null;

        public TreeNode(String newVal){
            value = newVal;
        }

        boolean isLeaf(){
            return (leftChild == null && rightChild == null);
        }
        boolean isParent(){
            return (leftChild != null || rightChild != null);
        }
    }

    private TreeNode root = null;
    public TreeNode getRoot(){
        return root;
    }

    public TreeNode getLeftChild(TreeNode parent){
        return parent.leftChild;
    }

    public TreeNode getRightChild(TreeNode parent){
        return parent.rightChild;
    }

    public Boolean isLeaf(TreeNode node){
        return node.isLeaf();
    }

    public Boolean isRoot(TreeNode node){
        return node == root;
    }

    public Boolean isParent(TreeNode node){
        return node.isParent();
    }

    public void add(String value){
        TreeNode node = new TreeNode(value);
        if(root == null){
            root = node;
        }
        TreeNode current = root;
        while(true){
            int compare = current.value.compareTo(value);
            if(compare > 0){
                if(current.rightChild == null){
                    current.rightChild = node;
                    break;
                } else {
                    current = current.rightChild;
                }
            } else {
                if(current.leftChild == null){
                    current.leftChild = node;
                    break;
                } else {
                    current = current.leftChild;
                }
            }
        }
    }
}
