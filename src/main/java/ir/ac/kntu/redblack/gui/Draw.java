package ir.ac.kntu.redblack.gui;

import ir.ac.kntu.redblack.node.Node;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

class Draw {
    private final int SCREEN_WIDTH = 1024;
    private final int SCREEN_HEIGHT = 666;
    private int cnt = -1;

    private void setNodeID(Node node) {
        if (node.nodeID == -1) node.nodeID = ++cnt;
    }

    public void bfs(Node node, Gui frame) {
        int level = 100, prev = SCREEN_WIDTH / 3;
        int relation = SCREEN_WIDTH / 6;

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Boolean> Drawn = new HashMap<>();
        queue.add(node);
        
        while (!queue.isEmpty()) {
            int queue_size = queue.size();
            while (queue_size > 0) {
                Node currNode = queue.poll();
                if (!Drawn.containsKey(currNode)) {
                    currNode.draw_temp = prev;
                    frame.addNode(currNode.data, currNode.color, prev, level);
                    Drawn.put(currNode, true);
                }
                setNodeID(currNode);
                Node child = currNode.left;
                if (child != null) {
                    frame.addNode(child.data, child.color, child.parent.draw_temp - relation, level + 100);
                    child.draw_temp = child.parent.draw_temp - relation;
                    Drawn.put(child, true);
                    setNodeID(child);
                    queue.add(child);
                    frame.addEdge(child.nodeID, child.parent.nodeID);
                }
                child = currNode.right;
                if (child != null) {
                    setNodeID(child);
                    frame.addNode(child.data, child.color, child.parent.draw_temp + relation, level + 100);
                    child.draw_temp = child.parent.draw_temp + relation;
                    Drawn.put(child, true);
                    queue.add(child);
                    frame.addEdge(child.nodeID, child.parent.nodeID);
                }
                queue_size--;
            }
            relation /= 2;
            level += 100;
        }
    }
}
