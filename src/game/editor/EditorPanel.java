package game.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class EditorPanel extends JPanel {
    private final EditorMap map;
    private final Editor editor;


    public EditorPanel(Editor editor) {
        this.editor = editor;
        this.map = new EditorMap(10);

        this.setFocusable(true);
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                EditorPanel.this.handleClick(event);
            }
        });
    }

    private void handleClick(MouseEvent event) {
        int cellSize = Math.min(this.getWidth() / this.map.getSize(), this.getHeight() / this.map.getSize());
        int xx = event.getX() / cellSize;
        int yy = event.getY() / cellSize;
        System.out.println("Обнаружен клик по координате (" + xx + "," + yy + ")");
        if (this.editor.isEditing()) {
            this.map.getGrid()[xx][yy] = editor.getCurrentSymbol();
        }

        repaint();
    }


    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.map != null) {
            int cellSize = Math.min(this.getWidth() / this.map.getSize(), this.getHeight() / this.map.getSize());

            for(int i = 0; i < this.map.getSize(); ++i) {
                for(int j = 0; j < this.map.getSize(); ++j) {
                    String symbol = this.map.getGrid()[i][j];
                    Color color = this.pickColor(symbol, i, j);
                    graphics.setColor(color);
                    graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    graphics.setColor(Color.BLACK);
                    graphics.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);


                    if (this.map.getBaseGrid()[i][j].equals("P_Castle")) {
                        graphics.setColor(new Color(0, 102, 235));
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    } else if (this.map.getBaseGrid()[i][j].equals("C_Castle")) {
                        graphics.setColor(new Color(153, 0, 0));
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    }

                    if (symbol.equals("H")) {
                        graphics.setColor(new Color(0, 102, 235));
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    }

                    if (symbol.equals("AI")) {
                        graphics.setColor(new Color(153, 0, 0));
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    }
                    if (symbol.equals("W")) {
                        graphics.setColor(new Color(104, 18, 123));
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    }
                }
            }

        }
    }

    private Color pickColor(String symbol, int px, int py) {
        switch (symbol) {
            case "X" -> {
                return new Color(0, 102, 235);
            }
            case "Y" -> {
                return new Color(153, 0, 0);
            }
            case "P" -> {
                return new Color(129, 255, 214);
            }
            case "C" -> {
                return new Color(255, 51, 51);
            }
            case "AI" -> {
                return new Color(153, 0, 0);
            }
            case "R" -> {
                return new Color(252, 220, 94);
            }
            case "W" -> {
                return new Color(104, 18, 123);
            }
            case "#" -> {
                return new Color(30, 40, 185);
            }
            case "." -> {
                if (px + py < 9) {
                    return new Color(113, 253, 113);
                } else if (px + py > 9) {
                    return new Color(229, 255, 220);
                }
                else {
                    return Color.LIGHT_GRAY;
                }
            }
            default -> {
                return Color.LIGHT_GRAY;
            }
        }
    }

    public EditorMap getMap() {
        return this.map;
    }
}


