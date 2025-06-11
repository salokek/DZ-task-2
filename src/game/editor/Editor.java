package game.editor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;

public class Editor extends JFrame {
    private final EditorMap map;
    private final EditorPanel panel;
    final Color[] chosenColor = {Color.GRAY};
    private String currentType;
    private String currentSymbol;
    private Color currentColor = Color.GRAY;
    private int currentCost = 1;
    private boolean editing = false;
    private final JLabel typePanel;
    private final JLabel costPanel;

    public Editor() {
        this.typePanel = new JLabel("Тип:");
        this.costPanel = new JLabel("Стоимость:");
        this.map = new EditorMap(10);
        this.panel = new EditorPanel(this);

        this.add(this.panel, "Center");

        setTitle("Редактор карты");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        JPanel controls = new JPanel();


        String[] options = {"Path", "Obstacle", "Player Castle", "Computer Castle", "Custom"};
        JPanel typeButtons = new JPanel();
        controls.add(typePanel);
        controls.add(costPanel);

        for (String choice : options) {
            JButton button = new JButton(choice);
            button.addActionListener(e -> {
                this.currentType = choice;
                UpdateTypeDisplay(getTypePanel());
                UpdateCostDisplay(getCostPanel());
                switch (choice) {
                    case "Player Castle" -> {
                        currentColor = new Color(0, 102, 235);
                        currentSymbol = "X";
                    }
                    case "Computer Castle" -> {
                        currentColor = new Color(153, 0, 0);
                        currentSymbol = "Y";
                    }
                    case "Path" -> {
                        currentColor = new Color(252, 220, 94);
                        currentSymbol = "R";
                    }
                    case "Obstacle" -> {
                        currentColor = new Color(30, 40, 185);
                        currentSymbol = "#";
                    }
                    case "Custom" -> {
                        JTextField redField = new JTextField("128", 3);
                        JTextField greenField = new JTextField("128", 3);
                        JTextField blueField = new JTextField("128", 3);
                        JTextField costField = new JTextField("1", 5);


                        JPanel colorPanel = new JPanel();
                        colorPanel.add(new JLabel("RGB:"));
                        colorPanel.setVisible(false);
                        colorPanel.add(redField);
                        colorPanel.add(greenField);
                        colorPanel.add(blueField);
                        colorPanel.add(costField);
                        colorPanel.setVisible(true);
                        revalidate();
                        repaint();
                        try {

                            int r = Integer.parseInt(redField.getText());
                            int g = Integer.parseInt(greenField.getText());
                            int b = Integer.parseInt(blueField.getText());

                            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
                                throw new NumberFormatException("RGB out of range");

                            this.currentColor = new Color(r, g, b);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Некорректные значения RGB или стоимости");
                            return;
                        }

                    }
                    default -> {
                        currentColor = Color.LIGHT_GRAY;
                    }
                }
            });
            typeButtons.add(button);
        }
        controls.add(typeButtons);

        JButton saveButton = new JButton("Сохранить карту");
        saveButton.addActionListener(e -> {
            saveMapToFile(new File("ku/save.txt"));
        });


        JButton changeButton = new JButton("Сменить тип клетки");
        JButton applyButton = new JButton("Начать редактирование");


        applyButton.addActionListener(e -> {
            editing = true;
        });

        changeButton.addActionListener(e -> editing = false);



        controls.add(applyButton);
        controls.add(changeButton);
        controls.add(saveButton);

        add(controls, BorderLayout.NORTH);
    }


    public boolean isEditing() {
        return editing;
    }

    public String getCurrentSymbol() {
        return currentSymbol;
    }

    public int getCurrentCost() {
        return currentCost;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color newColor) {
        this.currentColor = newColor;
    }

    public void setCurrentSymbol(String newType) {
        this.currentSymbol = newType;
    }

    public void setCurrentCost(int newCost) {
        this.currentCost = newCost;
    }

    public JLabel getTypePanel() {
        return this.typePanel;
    }

    public JLabel getCostPanel() {
        return this.costPanel;
    }

    private void UpdateTypeDisplay(JLabel tp) {
        tp.setText("Тип: " + this.currentType);
    }

    private void UpdateCostDisplay(JLabel cp) {
        cp.setText("Стоимость: " + this.currentCost);
    }

    public void saveMapToFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (String[] row : this.panel.getMap().getGrid()) {
                for (String cell : row) {
                    writer.print(cell);
                    System.out.println(cell);
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
