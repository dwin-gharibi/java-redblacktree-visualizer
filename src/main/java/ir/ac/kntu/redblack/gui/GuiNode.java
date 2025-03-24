package ir.ac.kntu.redblack.gui;

class GuiNode {
    public int x;
    public int y;
    public int data;
    public int color;

    public GuiNode(int data, int color, int GuiX, int GuiY) {
        x = GuiX;
        y = GuiY;
        this.color = color;
        this.data = data;
    }
}