package ru.tastika.collections;

/**
 *
 * @author hobal
 */
public class BinaryTree {


    private BinaryTree parentBinaryTree;
    private BinaryTree leftBinaryTree;
    private BinaryTree rightBinaryTree;
    private Object data;


    public BinaryTree() {
        parentBinaryTree = null;
        leftBinaryTree = null;
        rightBinaryTree = null;
        data = null;
    }


    public void setLeftBinaryTree(BinaryTree leftBinaryTree) {
        this.leftBinaryTree = leftBinaryTree;
        if (leftBinaryTree != null) {
            leftBinaryTree.setParentBinaryTree(this);
        }
    }


    public void setRightBinaryTree(BinaryTree rightBinaryTree) {
        this.rightBinaryTree = rightBinaryTree;
        if (rightBinaryTree != null) {
            rightBinaryTree.setParentBinaryTree(this);
        }
    }


    public void setData(Object data) {
        this.data = data;
    }


    /**
     * @return the leftBinaryTree
     */
    public BinaryTree getLeftBinaryTree() {
        return leftBinaryTree;
    }


    public boolean hasLeftBinaryTree() {
        return leftBinaryTree != null;
    }


    /**
     * @return the rightBinaryTree
     */
    public BinaryTree getRightBinaryTree() {
        return rightBinaryTree;
    }


    public boolean hasRightBinaryTree() {
        return rightBinaryTree != null;
    }


    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" [");
        sb.append(getData());
        sb.append("]");
        return sb.toString();
    }


    /**
     * @return the parentBinaryTree
     */
    public BinaryTree getParentBinaryTree() {
        return parentBinaryTree;
    }


    /**
     * @param parentBinaryTree the parentBinaryTree to set
     */
    private void setParentBinaryTree(BinaryTree parentBinaryTree) {
        this.parentBinaryTree = parentBinaryTree;
    }

}
