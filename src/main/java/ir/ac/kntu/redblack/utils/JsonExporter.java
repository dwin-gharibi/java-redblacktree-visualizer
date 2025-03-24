package ir.ac.kntu.redblack.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.kntu.redblack.node.Node;
import java.io.File;
import java.io.IOException;

public class JsonExporter {

    public void exportToJson(Node root, String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filePath), nodeToJson(root));
    }

    public Node importFromJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Node root = objectMapper.readValue(new File(filePath), Node.class);
        rebuildParents(root, null);
        return root;
    }

    private Node nodeToJson(Node node) {
        if (node == null) {
            return null;
        }
        Node newNode = new Node();

        newNode.data = node.data;
        newNode.color = node.color;
        newNode.nodeID = node.nodeID;
        newNode.draw_temp = node.draw_temp;
        newNode.left = nodeToJson(node.left);
        newNode.right = nodeToJson(node.right);
        newNode.parent = null;

        return newNode;
    }

    private void rebuildParents(Node node, Node parent) {
        if (node == null) {
            return;
        }
        node.parent = parent;
        rebuildParents(node.left, node);
        rebuildParents(node.right, node);
    }
}
