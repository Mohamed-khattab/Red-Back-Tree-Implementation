import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Node{
    int data ;
    Node left ;
    Node right ;
    Node parent ;
    boolean color ;
    public Node(int data) {
        this.data = data;
    }
}
 class RedBlackTree{
    private final boolean RED = false ;
    private final boolean Black = true ;
    private Node root ;
    private  int size;
     public Node search(int key){
        Node node = root ;
        while( node != null ){
            if( key == node.data){
                return node ;
            } else if (key < node.data ) {
                node = node.left ;
            }else {
                node = node.right ;
            }
        }
        return null ;
    }
    public void  insert( int key ){
        Node node = root;
        Node parent = null ;
        // Traverse the Node to reach the right place
        while( node != null ){
            parent = node ;
            if(key < node.data){
                node = node.left;
            } else if (key > node.data) {
                node = node.right ;
            }else {
               throw new IllegalArgumentException("TREE ALREADY CONTAINS A NODE WITH KEY  :" + key);
            }
        }
        // insert a new node
        Node newNode = new Node(key) ;
        newNode.color = RED ;
        if(parent == null ){
            root = newNode ;
        } else if (key <parent.data) {
            parent.left = newNode ;
        } else {
            parent.right = newNode ;
        }
        newNode.parent= parent ;
        insert_and_balance(newNode) ;
        size ++ ;
    }
    private void insert_and_balance(Node node) {
        Node parent = node.parent ;
        // if the tree was empty before insertion
        if(parent == null ){
            node.color = Black ;
            return ;
        }
        // if the parent is black
        if( parent.color == Black){
            return ;
        }
        // here we have two consequtitive red nodes
        // Case one : it the parent is the root
        Node grandParent = parent.parent ;
        if(grandParent == null ){
            parent.color =Black ;
            return ;
        }
        // Case two  it the node in the middle of the tree ,uncle node will bew needed
        Node uncle = getUncle(parent) ;
        // if the uncle is red
        if(uncle !=null && uncle.color ==RED){
            parent.color= Black ;
            uncle.color = Black ;
            grandParent.color= RED ;
            // call recursively  for the grandparents to check for two consecutive red nodes cuz it is red right now
            insert_and_balance(grandParent);
        }  // if the parent is the left child of the grandparent and uncle color is black
        else if(parent == grandParent.left ){
            // if the node is the
            if(node == parent.right){
                leftRotate(parent);
                parent = node ;
            }
            rightRotate(grandParent);
            parent.color = Black ;  // colorize the parent
            grandParent.color = RED ;
        }
        else {    // if the parent is the right child of the grandparent and uncle color is black
        if(node == parent.left){
                rightRotate(parent);
                parent=  node ;
            }
            leftRotate(grandParent);
            parent.color = Black ;  // colorize the parent
            grandParent.color = RED ;
        }
    }
    private void rightRotate(Node node) {
        Node parent = node.parent ;
        Node leftChild = node.left ;
        node.left = leftChild.right ;
        if(leftChild.right != null ){
            leftChild.right.parent = node  ;
        }
        leftChild.right = node;
        node.parent = leftChild;
        updateParentChild(parent, node, leftChild);
    }
    private void leftRotate(Node node) {
        Node parent = node.parent;
        Node rightChild = node.right;

        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }
        rightChild.left = node;
        node.parent = rightChild;
        updateParentChild(parent, node, rightChild);
    }
    private void updateParentChild(Node parent, Node oldChild, Node newChild) {
        if (parent == null) {
            root = newChild;
        } else if (parent.left == oldChild) {
            parent.left = newChild;
        } else if (parent.right == oldChild) {
            parent.right = newChild;
        } else {
            throw new IllegalStateException("Node is not a child of its parent");
        }

        if (newChild != null) {
            newChild.parent = parent;
        }
    }
    private Node getUncle(Node parent) {
        Node grandParent = parent.parent ;
        if(grandParent.left == parent){
            return grandParent.right ;
        } else if (grandParent.right == parent) {
            return grandParent.left ;
        }else{
            throw new IllegalArgumentException("The parent is not a child of his grandparent") ;
        }
    }

    public void delete(int key ){
        Node node = root  ;
        while(node != null && node.data != key){
            if(key < node.data ){
                node = node.left ;
            } else {
                node = node.right ;
            }
        }
        if(node == null){ // here  found the node to be deleted
            throw new IllegalArgumentException("NODE NOT FOUND") ;
        }
        Node movedUpNode  ;
        boolean deletedNodeColor ;
        if (node.left != null && node.right != null) {
            Node successor = getSuccessor(node.right);
            node.data = successor.data;
        }
        movedUpNode = deleteNodeWithZeroOrOneChild(node) ;
        deletedNodeColor = node.color;
        if(deletedNodeColor ==Black){
            delete_and_balance(movedUpNode) ;
            if(movedUpNode.getClass() == NilNode.class){
                updateParentChild(movedUpNode.parent , movedUpNode , null);
            }
        }
        size -- ;
    }
    private Node getSuccessor(Node node) {
        while( node.left != null ){
            node = node.left ;
        }
        return node;
    }
    private Node deleteNodeWithZeroOrOneChild(Node node) {
        if(node.left != null ){
            updateParentChild(node.parent , node , node.left);
            return node.left ;
        }else if(node.right != null ){
            updateParentChild(node.parent , node , node.right);
            return node.right ;
        }
        else{ // no child found if red jst remove , if not replace with a temp NTl node
            Node newChild = node.color == Black ? new NilNode() :null ;
            updateParentChild(node.parent , node , newChild);
            return newChild ;
        }

    }
    private class NilNode extends Node {
        private NilNode() {
            super(0);
            this.color = Black;
        }
    }
    private void delete_and_balance(Node node)  {
        if(node == root){
            node.color = Black ;
            return ;
        }
        Node sibling = getSibling(node) ;
        if (sibling.color == RED) {
            handleRedSibling(node, sibling);
            sibling = getSibling(node); // Get new sibling for fall-through to cases 3-6
        }

        // Cases 3+4: Black sibling with two black children
        if (isBlack(sibling.left) && isBlack(sibling.right)) {
            sibling.color = RED;

            // Case 3: Black sibling with two black children + red parent
            if (node.parent.color == RED) {
                node.parent.color = Black;
            }
            // Case 4: Black sibling with two black children + black parent
            else {
                delete_and_balance(node.parent);
            }
        }
        // Case 5+6: Black sibling with at least one red child
        else {
            handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
        }
    }

    private boolean isBlack(Node node) {
        return  node == null || node.color == Black ;
    }

    private Node getSibling(Node node) {
        Node parent = node.parent;
        if (node == parent.left) {
            return parent.right;
        } else if (node == parent.right) {
            return parent.left;
        } else {
            throw new IllegalStateException("Parent is not a child of its grandparent");
        }
    }
    private void handleRedSibling(Node node, Node sibling) {
        // Recolor...
        sibling.color = Black;
        node.parent.color = RED;

        // ... and rotate
        if (node == node.parent.left) {
            leftRotate(node.parent);
        } else {
            rightRotate(node.parent);
        }
    }
    private void handleBlackSiblingWithAtLeastOneRedChild(Node node, Node sibling) {
        boolean nodeIsLeftChild = node == node.parent.left;

        // Case 5: Black sibling with at least one red child + "outer nephew" is black
        if (nodeIsLeftChild && isBlack(sibling.right)) {
            sibling.left.color = Black;
            sibling.color = RED;
            rightRotate(sibling);
            sibling = node.parent.right;
        } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
            sibling.right.color = Black;
            sibling.color = RED;
            leftRotate(sibling);
            sibling = node.parent.left;
        }
        // Case 6: Black sibling with at least one red child + "outer nephew" is red
        sibling.color = node.parent.color;
        node.parent.color = Black;
        if (nodeIsLeftChild) {
            sibling.right.color = Black;
            leftRotate(node.parent);
        } else {
            sibling.left.color = Black;
            rightRotate(node.parent);
        }
    }
    public int  getTreeSize(){
         return size ;
    }
    public boolean isEmpty(){
         return size == 0;
    }
     public void displayTree(){   /* display  nodes in level way BFS  */
         if(isEmpty()){
             System.out.println("\n Tree Is Empty \n ");
             return ;
         }
         Queue<Node> queue = new LinkedList<>();
         queue.add(root);
         while (!queue.isEmpty()) {
             Node tempNode = queue.poll();
             System.out.print(tempNode.data + " ");
             /*add left child to the queue */
             if (tempNode.left != null) {
                 queue.add(tempNode.left);
             }
             /*add  right child to the queue */
             if (tempNode.right != null) {
                 queue.add(tempNode.right);
             }
         }
     }
}
public class RedBlackTreeApp {
    public static void  main(String[] args){
        RedBlackTree  RbTree  = new RedBlackTree() ;
        Scanner sc = new Scanner(System.in) ;
        char choice ;
        do
        {
            System.out.println("\nSelect an operation:\n");
            System.out.println("1. Insert a node ");
            System.out.println("2. Search a node");
            System.out.println("3. Get total number of nodes in Red Black Tree");
            System.out.println("4. Is Red Black Tree empty?");
            System.out.println("5. Remove node from Red Black Tree");
            System.out.println("6. Display Red Black Tree in BFS Traversing");
            //get choice from user
            int operation  = sc.nextInt();
            switch (operation) {
                case 1 -> {
                    System.out.println("Please enter an element to insert in Red Black Tree");
                    RbTree.insert(sc.nextInt());
                }
                case 2 -> {
                    System.out.println("Enter integer element to search");
                    System.out.println(RbTree.search(sc.nextInt()));
                }
                case 3 -> System.out.println("Total number of nodes is : " + RbTree.getTreeSize());
                case 4 -> System.out.println(RbTree.isEmpty());
                case 5 -> {
                    System.out.println("Enter The node key to be deleted  ");
                    RbTree.delete(sc.nextInt());
                    System.out.println("\nNode Removed successfully");
                }
                case 6 -> {
                    System.out.println("\nDisplay Red Black Tree in BFS Traversing ");
                    RbTree.displayTree();
                }
                default -> System.out.println("\n ");
            }
            System.out.println("\nPress 'y' or 'Y' to continue \n");
            choice = sc.next().charAt(0);
        } while (choice == 'Y'|| choice == 'y');
    }
}
