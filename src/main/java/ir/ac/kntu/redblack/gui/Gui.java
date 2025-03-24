package ir.ac.kntu.redblack.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.ac.kntu.redblack.node.Node;
import ir.ac.kntu.redblack.tree.RedBlackTree;
import ir.ac.kntu.redblack.utils.JsonExporter;
import ir.ac.kntu.redblack.utils.Neo4jUtil;

public class Gui extends JFrame {
    private final int SCREEN_WIDTH = 1024;
    private final int SCREEN_HEIGHT = 666;

    int width;
    int height;

    public ArrayList<GuiNode> GuiNodes;
    public ArrayList<GuiEdge> guiEdges;
    RedBlackTree redBlackTree;

    public Gui() {
        super();
        this.setTitle("درخت قرمز-سیاه               |               ساختمان‌داده");
        this.setLocationRelativeTo(null);

        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.getContentPane().setBackground(Color.lightGray);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GuiNodes = new ArrayList<>();
        guiEdges = new ArrayList<>();
        width = 40;
        height = 40;
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Font font = new Font("Vazirmatn", Font.BOLD, 16);

        redBlackTree = new RedBlackTree();

        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(new Color(230, 230, 230));

        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        ImageIcon originalIcon = new ImageIcon("neo4j_logo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(resizedIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel includedText = new JLabel("+ ذخیره‌سازی گراف در neo4j");
        includedText.setFont(new Font("Vazirmatn", Font.BOLD, 14));
        includedText.setHorizontalAlignment(SwingConstants.CENTER);
        includedText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.add(logoLabel);
        logoPanel.add(includedText);

        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        rightPanel.add(logoPanel, gbc);

        JTextField insertText = new JTextField(2);
        insertText.setFont(font);
        JButton insert = createStyledButton("افزودن گره جدید", new Color(76, 175, 80), Color.white, font);
        insert.addActionListener(itemEvent -> {
            Listener(insertText, 0);
        });
        addRowToPanel(rightPanel, "افزودن گره:", insertText, insert, font, gbc);

        JTextField deleteText = new JTextField(2);
        deleteText.setFont(font);
        JButton delete = createStyledButton("حذف گره از درخت", new Color(244, 67, 54), Color.white, font);
        delete.addActionListener(itemEvent -> {
            Listener(deleteText, 1);
        });
        addRowToPanel(rightPanel, "حذف گره:", deleteText, delete, font, gbc);

        JTextField searchText = new JTextField(2);
        searchText.setFont(font);
        JButton search = createStyledButton("جستجوی گره", new Color(33, 150, 243), Color.white, font);
        addRowToPanel(rightPanel, "جستجوی گره:", searchText, search, font, gbc);
        search.addActionListener(itemEvent -> {
            Listener(searchText, 2);
        });

        JButton exportBtn = createStyledButton("خروجی گرفتن (json)", new Color(156, 39, 176), Color.white, font);
        exportBtn.addActionListener((ActionEvent e) -> {
            try {
                exportTreeToJson();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        rightPanel.add(exportBtn, gbc);

        JButton loadBtn = createStyledButton("بارگذاری درخت (json)", new Color(3, 169, 244), Color.white, font);
        loadBtn.addActionListener((ActionEvent e) -> {
            loadTreeFromJson();
        });
        rightPanel.add(loadBtn, gbc);

        JButton saveBtn = createStyledButton("ذخیره درخت (neo4j)", new Color(63, 81, 181), Color.white, font);
        saveBtn.addActionListener((ActionEvent e) -> {
            saveTreeToDatabase();
        });
        rightPanel.add(saveBtn, gbc);

        JButton clear = createStyledButton("حذف کل درخت", new Color(121, 85, 72), Color.white, font);
        clear.addActionListener(itemEvent -> {
            redBlackTree.clear();
            this.guiEdges.clear();
            this.GuiNodes.clear();
            this.repaint();
        });
        rightPanel.add(clear, gbc);

        add(rightPanel, BorderLayout.EAST);
        loadTreeFromDatabase();
    }

    private void addRowToPanel(JPanel panel, String labelText, JTextField textField, JButton button, Font font, GridBagConstraints gbc) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JLabel label = new JLabel(labelText);
        label.setFont(font);

        rowPanel.add(button);
        rowPanel.add(textField);
        rowPanel.add(label);
        panel.add(rowPanel, gbc);
    }

    private void exportTreeToJson() throws IOException {
        JsonExporter jsonExporter = new JsonExporter();
        jsonExporter.exportToJson(redBlackTree.getRoot(), "red_black_tree.json");
        JOptionPane.showMessageDialog(this, "درخت با موفقیت در فایل JSON ذخیره شد.");
    }

    private void loadTreeFromJson() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("انتخاب فایل JSON");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            JsonExporter jsonExporter = new JsonExporter();
            try {
                Node loadedRoot = jsonExporter.importFromJson(selectedFile.getAbsolutePath());

                redBlackTree.clear();
                this.GuiNodes.clear();
                this.guiEdges.clear();

                this.redBlackTree.setRoot(loadedRoot);

                Draw draw = new Draw();
                draw.bfs(loadedRoot, this);
                this.repaint();

                JOptionPane.showMessageDialog(this, "درخت با موفقیت از فایل JSON بارگذاری شد.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "خطا در بارگذاری فایل JSON.");
                e.printStackTrace();
            }
        }
    }

    private void saveTreeToDatabase() {
        Neo4jUtil neo4jUtil = new Neo4jUtil();
        try {
            neo4jUtil.clearDatabase();
            neo4jUtil.saveRedBlackTree(redBlackTree);
            JOptionPane.showMessageDialog(this, "درخت با موفقیت به دیتابیس Neo4j اضافه شد.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطا در اضافه کردن درخت به دیتابیس Neo4j.");
            e.printStackTrace();
        } finally {
            neo4jUtil.close();
        }
    }

    private void loadTreeFromDatabase() {
        Neo4jUtil neo4jUtil = new Neo4jUtil();
        try {
            RedBlackTree loadedTree = neo4jUtil.loadRedBlackTree();

            redBlackTree.clear();
            this.GuiNodes.clear();
            this.guiEdges.clear();

            this.redBlackTree.setRoot(loadedTree.getRoot());

            Draw draw = new Draw();
            draw.bfs(loadedTree.getRoot(), this);

            this.repaint();

            JOptionPane.showMessageDialog(this, "درخت با موفقیت از دیتابیس Neo4j بارگذاری شد.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطا در بارگذاری درخت از دیتابیس Neo4j.");
            e.printStackTrace();
        } finally {
            neo4jUtil.close();
        }
    }

    private JButton createStyledButton(String text, Color background, Color foreground, Font font) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(background.darker(), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    public void addNode(int data, int color, int x, int y) {
        GuiNodes.add(new GuiNode(data, color, x, y));
        this.repaint();
    }

    public void addEdge(int i, int j) {
        guiEdges.add(new GuiEdge(i, j));
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Font font = new Font("Vazirmatnٰ", Font.BOLD, 12);
        g.setFont(font);

        FontMetrics f = g.getFontMetrics();
        int nodeHeight = Math.max(height, f.getHeight());

        for (GuiEdge e : guiEdges) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(0));
            g2.draw(new Line2D.Float(GuiNodes.get(e.i).x, GuiNodes.get(e.i).y, GuiNodes.get(e.j).x, GuiNodes.get(e.j).y));

        }

        for (GuiNode n : GuiNodes) {
            String Data = String.valueOf(n.data);
            int nodeWidth = Math.max(width, f.stringWidth(Data) + width / 2);
            if (n.color == 0) g.setColor(Color.red);
            else g.setColor(Color.black);


            g.fillOval(n.x - nodeWidth / 2, n.y - nodeHeight / 2, nodeWidth, nodeHeight);
            g.setColor(Color.white);
            g.drawOval(n.x - nodeWidth / 2, n.y - nodeHeight / 2, nodeWidth, nodeHeight);


            g.drawString(Data, n.x - f.stringWidth(Data) / 2, n.y + f.getHeight() / 2);
        }
    }

    void Listener(JTextField textField, int operation) {
        String input = textField.getText();
        textField.setText("");
        int x = Integer.parseInt(input);
        textField.setText("");

        this.guiEdges.clear();
        this.GuiNodes.clear();

        if (operation == 0) redBlackTree.finalInsertion(x);
        else if(operation == 2) {
            try {
                Node node = redBlackTree.Search(x);
                JOptionPane.showMessageDialog(this, "راسی با مقدار " + node.data + " در درخت یافت شد");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "راسی با مقدار " + x + " در درخت یافت نشد!!!");
            }

        }
        else redBlackTree.Delete(x);

        new Draw().bfs(redBlackTree.getRoot(), this);
        Node node = redBlackTree.getRoot();
        redBlackTree.reset(node);
    }
}
