package ir.ac.kntu.redblack.utils;

import ir.ac.kntu.redblack.node.Node;
import ir.ac.kntu.redblack.tree.RedBlackTree;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import java.util.*;

public class Neo4jUtil {

    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "password";

    private Driver driver;

    public Neo4jUtil() {
        driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
    }

    public void close() {
        driver.close();
    }

    public void saveRedBlackTree(RedBlackTree tree) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                saveNode(tx, tree.getRoot(), null, true);
                return null;
            });
        }
    }

    public void clearDatabase() {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (n) DETACH DELETE n");
                return null;
            });
        }
    }

    private void saveNode(Transaction tx, Node node, Node parent, boolean isRoot) {
        if (node == null) return;

        Map<String, Object> properties = new HashMap<>();
        properties.put("NodeIDD", node.nodeIDD);
        properties.put("data", node.data);
        properties.put("color", node.color);
        properties.put("isRoot", isRoot);

        String createNodeQuery = "CREATE (n:Node {NodeIDD: $NodeIDD, data: $data, color: $color, isRoot: $isRoot})";
        tx.run(createNodeQuery, properties);

        if (parent != null) {
            if (parent.left == node) {
                tx.run("MATCH (parent:Node {NodeIDD: $parentIDD}), (child:Node {NodeIDD: $childIDD}) " +
                                "CREATE (parent)-[:LEFT_CHILD]->(child)",
                        Map.of("parentIDD", parent.nodeIDD, "childIDD", node.nodeIDD));
            } else if (parent.right == node) {
                tx.run("MATCH (parent:Node {NodeIDD: $parentIDD}), (child:Node {NodeIDD: $childIDD}) " +
                                "CREATE (parent)-[:RIGHT_CHILD]->(child)",
                        Map.of("parentIDD", parent.nodeIDD, "childIDD", node.nodeIDD));
            }
        }

        saveNode(tx, node.left, node, false);
        saveNode(tx, node.right, node, false);
    }

    public RedBlackTree loadRedBlackTree() {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Node root = findRootNode(tx);

                if (root == null) throw new RuntimeException("Root node not found in the database.");

                RedBlackTree tree = new RedBlackTree(root);
                bfsLoad(tx, root, tree);
                return tree;
            });
        }
    }

    private Node findRootNode(Transaction tx) {
        String findRootQuery = "MATCH (n:Node) WHERE n.isRoot = true RETURN n.NodeIDD AS NodeIDD, n.data AS data, n.color AS color";
        Result result = tx.run(findRootQuery);

        if (result.hasNext()) {
            Record record = result.next();
            Node rootNode = new Node(record.get("data").asInt());
            rootNode.color = record.get("color").asInt();
            rootNode.nodeIDD = record.get("NodeIDD").asInt();
            return rootNode;
        }

        return null;
    }

    private void bfsLoad(Transaction tx, Node root, RedBlackTree tree) {
        Queue<Node> queue = new LinkedList<>();
        Map<Integer, Node> idToNodeMap = new HashMap<>();

        queue.add(root);
        idToNodeMap.put(root.nodeIDD, root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            String query = "MATCH (n:Node {NodeIDD: $NodeIDD}) " +
                    "OPTIONAL MATCH (n)-[:LEFT_CHILD]->(left:Node) " +
                    "OPTIONAL MATCH (n)-[:RIGHT_CHILD]->(right:Node) " +
                    "RETURN left.NodeIDD AS leftID, right.NodeIDD AS rightID, " +
                    "left.data AS leftData, left.color AS leftColor, " +
                    "right.data AS rightData, right.color AS rightColor";

            Result result = tx.run(query, Map.of("NodeIDD", current.nodeIDD));

            if (result.hasNext()) {
                Record record = result.next();

                Integer leftID = record.get("leftID").isNull() ? null : record.get("leftID").asInt();
                if (leftID != null) {
                    Node leftNode = idToNodeMap.computeIfAbsent(leftID, id -> new Node(record.get("leftData").asInt()));
                    leftNode.color = record.get("leftColor").asInt();
                    leftNode.nodeIDD = leftID;
                    leftNode.parent = current;
                    current.left = leftNode;
                    queue.add(leftNode);
                }

                Integer rightID = record.get("rightID").isNull() ? null : record.get("rightID").asInt();
                if (rightID != null) {
                    Node rightNode = idToNodeMap.computeIfAbsent(rightID, id -> new Node(record.get("rightData").asInt()));
                    rightNode.color = record.get("rightColor").asInt();
                    rightNode.nodeIDD = rightID;
                    rightNode.parent = current;
                    current.right = rightNode;
                    queue.add(rightNode);
                }
            }
        }
    }
}
