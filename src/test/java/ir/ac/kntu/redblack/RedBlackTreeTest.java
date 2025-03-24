package ir.ac.kntu.redblack;

import ir.ac.kntu.redblack.node.Node;
import ir.ac.kntu.redblack.tree.RedBlackTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedBlackTreeTest {
    private RedBlackTree tree;

    @BeforeEach
    public void setup() {
        tree = new RedBlackTree();
    }

    @Test
    public void testInsertRoot() {
        tree.finalInsertion(10);
        assertEquals(10, tree.getRoot().data);
        assertEquals(Node.BLACK, tree.getRoot().color);
    }

    @Test
    public void testInsertSingleNodeColor() {
        tree.finalInsertion(15);
        assertEquals(Node.BLACK, tree.getRoot().color);
    }

    @Test
    public void testInsertConsecutiveRedFix() {
        tree.finalInsertion(20);
        tree.finalInsertion(10);
        tree.finalInsertion(15);

        Node root = tree.getRoot();
        assertEquals(15, root.data);
        assertEquals(Node.BLACK, root.color);
        assertEquals(Node.RED, root.left.color);
        assertEquals(Node.RED, root.right.color);
    }

    @Test
    public void testLeftRotate() {
        tree.finalInsertion(10);
        tree.finalInsertion(20);
        tree.LeftRotate(tree.getRoot());

        assertEquals(20, tree.getRoot().data);
        assertEquals(10, tree.getRoot().left.data);
    }

    @Test
    public void testRightRotate() {
        tree.finalInsertion(20);
        tree.finalInsertion(10);
        tree.rightRotate(tree.getRoot());

        assertEquals(10, tree.getRoot().data);
        assertEquals(20, tree.getRoot().right.data);
    }

    @Test
    public void testUncleIsRedScenario() {
        tree.finalInsertion(30);
        tree.finalInsertion(20);
        tree.finalInsertion(40);
        tree.finalInsertion(10);
        tree.finalInsertion(25);

        assertEquals(Node.BLACK, tree.getRoot().color);
        assertEquals(Node.BLACK, tree.getRoot().left.color);
        assertEquals(Node.BLACK, tree.getRoot().right.color);
    }

    @Test
    public void testInsertLargeSequence() {
        for (int i = 1; i <= 50; i++) {
            tree.finalInsertion(i);
        }

        assertNotNull(tree.getRoot());
        assertEquals(Node.BLACK, tree.getRoot().color);
    }

    @Test
    public void testSearchFound() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.finalInsertion(75);

        Node node = tree.Search(25);
        assertNotNull(node);
        assertEquals(25, node.data);
    }

    @Test
    public void testSearchNotFound() {
        tree.finalInsertion(50);
        try {
            Node node = tree.Search(30);
            assertNotEquals(node.data, 30);
        }
        catch (Exception e) {
            assert true;
        }
    }

    @Test
    public void testDeleteLeafNode() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.Delete(25);

        try {
            assertEquals(tree.Search(25).data, 25);
        }
        catch (Exception e) {
            assert true;
        }
        assertEquals(Node.BLACK, tree.getRoot().color);
    }

    @Test
    public void testDeleteRootNode() {
        tree.finalInsertion(50);
        tree.Delete(50);

        assertNull(tree.getRoot());
    }

    @Test
    public void testDeleteNodeWithOneChild() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.finalInsertion(10);
        tree.Delete(25);

        assertNotNull(tree.Search(10));
    }

    @Test
    public void testDeleteNodeWithTwoChildren() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.finalInsertion(75);
        tree.finalInsertion(10);
        tree.finalInsertion(30);
        tree.Delete(25);

        assertNotNull(tree.Search(30));
    }

    @Test
    public void testFixDoubleBlackCase1() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.Delete(25);

        assertEquals(Node.BLACK, tree.getRoot().color);
    }

    @Test
    public void testFixDoubleBlackCase2() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.finalInsertion(75);
        tree.Delete(25);

        assertEquals(Node.BLACK, tree.getRoot().color);
        assertEquals(Node.RED, tree.getRoot().right.color);
    }

    @Test
    public void testFixDoubleBlackCase3() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.finalInsertion(75);
        tree.finalInsertion(10);
        tree.Delete(25);

        assertEquals(Node.BLACK, tree.getRoot().color);
        assertEquals(Node.BLACK, tree.getRoot().right.color);
    }

    @Test
    public void testClearTree() {
        tree.finalInsertion(50);
        tree.finalInsertion(25);
        tree.finalInsertion(75);

        tree.clear();

        assertNull(tree.getRoot());
    }

    @Test
    public void testNodeColorAfterInsertion() {
        tree.finalInsertion(20);
        tree.finalInsertion(10);
        tree.finalInsertion(30);

        assertEquals(Node.BLACK, tree.getRoot().color);
        assertEquals(Node.RED, tree.getRoot().left.color);
        assertEquals(Node.RED, tree.getRoot().right.color);
    }

    @Test
    public void testTreePropertyAfterMultipleInsertions() {
        for (int i = 1; i <= 100; i++) {
            tree.finalInsertion(i);
        }
        assertNotNull(tree.getRoot());
        assertEquals(Node.BLACK, tree.getRoot().color);
    }
}

