package game.logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Iterator;
import javax.swing.JPanel;

import game.GameWorld;
import game.Unit;
import game.MainLauncher;

public class GamePanel extends JPanel {
    private final GameWorld world;
    private final GameMap map;
    private final MainLauncher game;
    private Unit selectedUnit;

    public GamePanel(GameWorld world, GameMap map, MainLauncher game) {
        this.world = world;
        this.map = map;
        this.game = game;
        this.setFocusable(true);
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                GamePanel.this.handleClick(event);
            }
        });
    }

    private void handleClick(MouseEvent event) {
        int cellSize = Math.min(this.getWidth() / this.map.getSize(), this.getHeight() / this.map.getSize());
        int xx = event.getX() / cellSize;
        int yy = event.getY() / cellSize;
        System.out.println("Обнаружен клик по координате (" + xx + "," + yy + ")");
        if (this.game.isPlayerTurn()) {
            if (this.selectedUnit == null) {
                if (this.map.getPlayer().getX() == xx && this.map.getPlayer().getY() == yy) {
                    this.selectedUnit = new Unit("Hero", "H", 100, 20, 4, 0, 1, new Color(0, 102, 235));
                    this.selectedUnit.setX(xx);
                    this.selectedUnit.setY(yy);
                    System.out.println("Выбран герой игрока.");
                } else {
                    for (Unit u : this.map.getPlayer().getUnits()) {
                        if (u.getX() == xx && u.getY() == yy) {
                            this.selectedUnit = u;
                            System.out.println("Выбран юнит " + u.getName() + ".");
                            break;
                        }
                    }
                }
            } else {
                if (this.selectedUnit.getName().equals("Hero")) {
                    System.out.println("Герой перемещается на (" + xx + "," + yy + ")");
                    this.map.getPlayer().moveHero(xx, yy);
                    if (!this.game.eclipseActive && Math.random() < 0.5) {
                        this.game.startLunarEclipse();
                    }

                    String sym = this.map.getBaseGrid()[xx][yy];

                    switch (sym) {
                        case "L" -> {
                            System.out.println("Игрок вошел в хутор");
                            this.world.eatHutor(this.game.getPlayer());
                        }
                        case "I" -> {
                            System.out.println("Игрок вошел в свинарник");
                            this.world.playerEnterPigsty();
                        }
                        case "U" -> {
                            System.out.println("Игрок вошел в домик для уточки");
                            this.world.playerGoToHotel();
                        }
                        case "Z" -> {
                            System.out.println("Игрок вошел в VKUSNO I TOCHKA");
                            this.world.playerGoToCafe(this.game.getPlayer());
                        }
                        case "V" -> {
                            System.out.println("Игрок вошел в барбершоп...");
                            this.world.playerGoToBarber();
                        }
                        default -> {
                        }
                    }

                } else {
                    int distance = Math.abs(this.selectedUnit.getX() - xx) + Math.abs(this.selectedUnit.getY() - yy);
                    System.out.println("Юнит " + this.selectedUnit.getName() + " пытается переместиться на (" + xx + "," + yy + ") на расстояние " + distance);
                    if (distance <= this.selectedUnit.getMoveRange() && this.selectedUnit.getMovesLeft() >= distance) {
                        if (this.map.isWalkable(xx, yy, true)) {
                            this.map.getPlayer().moveUnit(this.selectedUnit, xx, yy);
                            this.selectedUnit.setMovesLeft(this.selectedUnit.getMovesLeft() - distance);
                        }
                    } else if (distance <= this.selectedUnit.getAttackRange()) {
                        for (Unit en : this.map.getComputer().getUnits()) {
                            if (en.getX() == xx && en.getY() == yy) {
                                PrintStream stream = System.out;
                                String unitName = this.selectedUnit.getName();
                                stream.println("Юнит " + unitName + " атакует юнита " + en.getName());
                                this.selectedUnit.attack(en);
                                if (!en.isAlive()) {
                                    System.out.println("Юнит " + en.getName() + " уничтожен.");
                                    this.map.getComputer().getUnits().remove(en);
                                    this.map.getGrid()[xx][yy] = this.map.getBaseGrid()[xx][yy];
                                    this.map.getPlayer().setGold(this.map.getPlayer().getGold() + 5);
                                    System.out.println("Игрок получил +5 золота.");
                                }

                                this.map.printToConsole();
                                break;
                            }
                        }
                    }
                }

                this.selectedUnit = null;
                this.repaint();
            }
        }


    }

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
                    Iterator<Unit> unitsIterator = this.map.getPlayer().getUnits().iterator();

                    Unit nextUnit;
                    while(unitsIterator.hasNext()) {
                        nextUnit = (Unit) unitsIterator.next();
                        if (nextUnit.getX() == i && nextUnit.getY() == j) {
                            graphics.setColor(nextUnit.getColor());
                            graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                        }
                    }

                    unitsIterator = this.map.getComputer().getUnits().iterator();

                    while(unitsIterator.hasNext()) {
                        nextUnit = (Unit) unitsIterator.next();
                        if (nextUnit.getX() == i && nextUnit.getY() == j) {
                            graphics.setColor(nextUnit.getColor().darker());
                            graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                        }
                    }

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
            case "L" -> {
                return new Color(225, 145, 50);
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
            case "I" -> {
                return new Color(210, 100, 120);
            }
            case "U" -> {
                return new Color(200, 200, 120);
            }
            case "Z" -> {
                return new Color(30, 250, 100);
            }
            case "V" -> {
                return new Color(200, 200, 185);
            }
            case "#" -> {
                return new Color(30, 40, 185);
            }
            case "." -> {
                if (px + py < 9) {
                    return new Color(113, 253, 170);
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
}

