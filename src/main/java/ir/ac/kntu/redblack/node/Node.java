package ir.ac.kntu.redblack.node;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Node {
    public static final int RED = 0, BLACK = 1, DOUBLEBLAK = 2;
    @JsonProperty("left")
    public Node left;

    @JsonProperty("right")
    public Node right;

    @JsonProperty("parent")
    public Node parent;

    @JsonProperty("data")
    public int data;

    @JsonProperty("color")
    public int color;

    @JsonProperty("draw_temp")
    public int draw_temp = 0;

    @JsonProperty("nodeID")
    public int nodeID = -1;

    @JsonProperty("nodeIDD")
    public int nodeIDD = 0;

    public static int cnt = 0;

    public Node() {
        left = right = parent = null;
        this.nodeIDD = ++cnt;
        this.color = RED;
    }

    public Node(int data) {
        left = right = parent = null;
        this.data = data;
        this.nodeIDD = ++cnt;
        this.color = RED;
    }

    public Node(int data, Node parent) {
        left = right = null;
        this.parent = parent;
        this.data = data;
        this.nodeIDD = ++cnt;
        this.color = RED;
    }
}

