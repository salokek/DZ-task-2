package game;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;

import game.buildings.Castle;
import game.buildings.Hutor;
import game.logic.GameMap;
import game.logic.GamePanel;
import game.buildings.BuildingClass;

public class MainLauncher extends JFrame {
    private final GamePanel panel;
    private final GameMap map;
    private Hero player;
    private Hero computer;
    private boolean playerTurn = true;
    private final JPanel goldPanel;
    public boolean eclipseActive = false;
    private final GameWorld world;
    private GameDataManager dataManager;
    private Hutor hutor = new Hutor();


    public void setPlayer(Hero player) {
        this.player = player;
    }


    private void initGame(Castle playerCastle, Castle computerCastle, Hero upHero, Hero upAI) {
        if (upHero == null && upAI == null) {
            this.player = new Hero("Брух", "H", playerCastle, this.map, this);
            this.computer = new Hero("ТупаAI", "AI", computerCastle, this.map, this);
        } else {
            this.player = upHero;
            this.player.setMap(this.map);
            this.player.setGame(this);
            this.computer = upAI;
            this.computer.setMap(this.map);
            this.computer.setGame(this);
        }

        this.computer.setGold(200);
        this.map.setPlayer(this.player);
        this.map.setComputer(this.computer);
        this.map.getGrid()[this.player.getX()][this.player.getY()] = "H";
        this.map.getGrid()[this.computer.getX()][this.computer.getY()] = "AI";
        this.player.getCastle().build(BuildingClass.GUARD_TOWER);
        this.computer.getCastle().build(BuildingClass.GUARD_TOWER);
        Unit pu = new Unit("MiniPeka", "p", 80, 15, 2, 0, 1, Color.BLUE);
        pu.setX(this.player.getX());
        pu.setY(this.player.getY());
        this.player.getUnits().add(pu);
        Unit cu = new Unit("MiniPeka", "p", 80, 15, 2, 0, 1, Color.CYAN);
        cu.setX(this.computer.getX());
        cu.setY(this.computer.getY());
        this.computer.getUnits().add(cu);
        this.map.getGrid()[pu.getX()][pu.getY()] = pu.getSymbol();
        this.map.getGrid()[cu.getX()][cu.getY()] = cu.getSymbol();
        System.out.println("Игра запущена. Начальное состояние карты:");
        this.map.printToConsole();
    }

    private void StyleAllButtons(JPanel p) {
        for (Component comp : p.getComponents()) {
            if (comp instanceof JButton button) {
                button.setBackground(new Color(240, 255, 240));
                button.setForeground(Color.BLACK);
                button.setFont(new Font("Arial", Font.BOLD, 14));
                button.setFocusPainted(false);
                button.setBorderPainted(false);
            } else if (comp instanceof JPanel) {
                StyleAllButtons((JPanel) comp);
            }
        }
    }


    public MainLauncher(int sw, String playerName) {
        this.dataManager = new GameDataManager(playerName);
        this.setTitle("Console Strategy");
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.setSize(1920, 1080);
        Castle playerCastle = new Castle("Castle_P", 1, 1);
        Castle computerCastle = new Castle("Castle_C", 8, 8);
        this.map = new GameMap(10, playerCastle, computerCastle, sw, this.dataManager, playerName);
        this.world = new GameWorld();
        this.world.startWorld();

        this.panel = new GamePanel(world, this.map, this);
        this.add(this.panel, "Center");
        JPanel left = new JPanel(new GridLayout(4, 1));

        JPanel buildsPanel = new JPanel(new GridLayout(3, 3));
        buildsPanel.setBorder(BorderFactory.createTitledBorder("Строительства"));
        BuildingClass[] builds = BuildingClass.values();
        int buildsLength = builds.length;

        JButton cavernBuild;
        for(int i = 0; i < buildsLength; ++i) {
            BuildingClass bt = builds[i];
            cavernBuild = new JButton(bt.name + " (" + bt.cost + ")");
            cavernBuild.addActionListener((event) -> {
                this.buildBT(bt);
            });
            buildsPanel.add(cavernBuild);
        }
        left.add(buildsPanel);

        goldPanel = new JPanel();
        goldPanel.setBorder(BorderFactory.createTitledBorder("Золото"));
        JLabel goldButton;
        if (this.player != null) {
            goldButton = new JLabel("Золото: " + this.player.getGold());
        }
        else {
            goldButton = new JLabel("Золото: " + 100);
        }
        goldButton.setFont(new Font("Arial", Font.BOLD, 16));
        goldPanel.add(goldButton);
        left.add(goldPanel);

        JPanel army = new JPanel(new GridLayout(3, 2));
        army.setBorder(BorderFactory.createTitledBorder("Найм юнитов"));
        JButton MiniPekaB = new JButton("МиниПека (10)");
        MiniPekaB.addActionListener((ev) -> {
            this.hireUnit("MiniPeka", 10);
        });
        army.add(MiniPekaB);
        JButton arbB = new JButton("Арбалетчик (20)");
        arbB.addActionListener((ev) -> {
            this.hireUnit("Arbalet", 20);
        });
        army.add(arbB);
        JButton swB = new JButton("Мечник (30)");
        swB.addActionListener((ev) -> {
            this.hireUnit("Sword", 30);
        });
        army.add(swB);
        cavernBuild = new JButton("Кавалерист (40)");
        cavernBuild.addActionListener((ev) -> {
            this.hireUnit("Cavalry", 40);
        });
        army.add(cavernBuild);
        JButton palB = new JButton("Маг (50)");
        palB.addActionListener((ev) -> {
            this.hireUnit("Mage", 50);
        });
        army.add(palB);
        left.add(army);

        JPanel tavernPanel = new JPanel();
        tavernPanel.setBorder(BorderFactory.createTitledBorder("Таверна"));
        JButton tavernHire = new JButton("Найм героя (50)");
        tavernHire.addActionListener((ev) -> {
            this.hireHeroFromTavern(50);

        });
        tavernPanel.add(tavernHire);
        left.add(tavernPanel);

        this.add(left, "West");

        JButton endTurn = new JButton("End Turn");
        endTurn.addActionListener((ev) -> {
            this.endPlayerTurn();
        });

        StyleAllButtons(left);
        this.add(endTurn, "South");


        Hero newHero = null;
        Hero newAI = null;
        if (sw == 2) {
            GameSaveState newGame = dataManager.loadGame(playerName);
            newHero = newGame.getPlayer();
            newAI = newGame.getComputer();
        }
        this.initGame(playerCastle, computerCastle, newHero, newAI);


        System.out.println("Игра инициализирована.");
        this.setLocationRelativeTo((Component)null);
    }

    public void checkWin() {

        if (!this.player.hasArmy() && this.player.getUnits().isEmpty()) {
            System.out.println("Победа компьютера: у игрока нет армии.");
            JOptionPane.showMessageDialog(this, "Игрок проиграл (нет армии)!");
            System.exit(0);
        }

        if (!this.computer.hasArmy() && this.computer.getUnits().isEmpty()) {
            System.out.println("Победа игрока: у компьютера нет армии.");
            JOptionPane.showMessageDialog(this, "Компьютер проиграл (нет армии)!");
            dataManager.saveRecord(this.player.getGold());
            System.exit(0);
        }

        if (this.map.checkVictory(this.player.getX(), this.player.getY(), true) && !this.computer.hasArmy() && this.computer.getUnits().isEmpty()) {
            System.out.println("Победа игрока: герой игрока достиг вражеского замка.");
            JOptionPane.showMessageDialog(this, "Игрок вошёл в замок противника и победил!");
            dataManager.saveRecord(this.player.getGold());
            System.exit(0);
        }

        if (this.map.checkVictory(this.computer.getX(), this.computer.getY(), false) && !this.player.hasArmy() && this.player.getUnits().isEmpty()) {
            System.out.println("Победа компьютера: герой компьютера достиг вражеского замка.");
            JOptionPane.showMessageDialog(this, "Компьютер вошёл в замок противника и победил!");
            System.exit(0);
        }

        for (Unit u : this.map.getPlayer().getUnits()) {
            if (this.map.checkVictory(u.getX(), u.getY(), true) && !this.computer.hasArmy() && this.computer.getUnits().isEmpty()) {
                System.out.println("Победа игрока: один из его юнитов достиг вражеского замка.");
                JOptionPane.showMessageDialog(this, "Игрок вошёл в ваш замок и победил!");
                dataManager.saveRecord(this.player.getGold());
                System.exit(0);
            }
        }

        for (Unit u : this.map.getComputer().getUnits()) {
            if (this.map.checkVictory(u.getX(), u.getY(), false) && !this.player.hasArmy() && this.player.getUnits().isEmpty()) {
                System.out.println("Победа компьютера: один из его юнитов достиг вражеского замка.");
                JOptionPane.showMessageDialog(this, "Компьютер вошёл в ваш замок и победил!");
                System.exit(0);
            }
        }

    }

    void buildBT(BuildingClass bt) {
        if (this.player.getGold() < bt.cost) {
            System.out.println("Строительство не выполнено: недостаточно золота.");
        } else {
            if (bt == BuildingClass.STABLE) {
                this.player.setGold(this.player.getGold() - bt.cost);

                this.player.getCastle().build(bt);
                this.player.changeMoveRange(2);
                System.out.println("Конюшня построена, дальность увеличена.");
            } else {
                this.player.setGold(this.player.getGold() - bt.cost);
                this.player.getCastle().build(bt);
            }
            UpdateGoldDisplay(getGoldPanel());
            this.map.printToConsole();
        }
    }

    void hireUnit(String type, int cost) {
        if (this.player.getGold() < cost) {
            System.out.println("Найм не выполнен: недостаточно золота.");
        } else {
            Unit newUnit = null;
            switch (type) {
                case "MiniPeka":
                    if (this.player.getCastle().hasBuilding(BuildingClass.GUARD_TOWER)) {
                        newUnit = new Unit("MiniPeka", "e", 80, 15, 2, cost, 1, new Color(3, 53, 118));
                    }
                    break;
                case "Arbalet":
                    if (this.player.getCastle().hasBuilding(BuildingClass.ARCHERY)) {
                        newUnit = new Unit("Arbalet", "a", 60, 25, 2, cost, 3, new Color(255, 128, 0));
                    }
                    break;
                case "Sword":
                    if (this.player.getCastle().hasBuilding(BuildingClass.ARMORY)) {
                        newUnit = new Unit("SwordMan", "m", 100, 20, 3, cost, 1, new Color(150, 183, 226));
                    }
                    break;
                case "Cavalry":
                    if (this.player.getCastle().hasBuilding(BuildingClass.ARENA)) {
                        newUnit = new Unit("Cavalry", "c", 90, 30, 4, cost, 2, new Color(204, 0, 204));
                    }
                    break;
                case "Mage":
                    if (this.player.getCastle().hasBuilding(BuildingClass.CATHEDRAL)) {
                        newUnit = new Unit("Mage", "k", 120, 35, 3, cost, 1, new Color(255, 0, 127));
                    }
            }

            if (newUnit == null) {
                System.out.println("Найм не выполнен: нет нужного здания.");
            } else {
                this.player.getUnits().add(newUnit);
                this.player.setGold(this.player.getGold() - cost);
                UpdateGoldDisplay(getGoldPanel());
                newUnit.setX(this.player.getCastle().getX());
                newUnit.setY(this.player.getCastle().getY());
                this.map.getGrid()[newUnit.getX()][newUnit.getY()] = newUnit.getSymbol();
                System.out.println("Найм юнита " + newUnit.getName() + " выполнен.");
                this.map.printToConsole();
            }
        }
    }

    private void hireHeroFromTavern(int cost) {
        if (this.player.getGold() < cost) {
            System.out.println("Найм героя не выполнен: недостаточно золота.");
        } else if (!this.player.getCastle().hasBuilding(BuildingClass.TAVERN)) {
            System.out.println("Найм героя не выполнен: нужна Таверна.");
        } else {
            int cX = this.player.getCastle().getX();
            int cY = this.player.getCastle().getY();
            boolean anyUnitsThere = false;

            for (Unit un : this.player.getUnits()) {
                if (un.getX() == cX && un.getY() == cY) {
                    anyUnitsThere = true;
                    break;
                }
            }

            if (anyUnitsThere) {
                System.out.println("Найм героя не выполнен: в замке уже есть юниты.");
            } else {
                Hero newHero = new Hero("Новень", "H", this.player.getCastle(), this.map, this);
                newHero.setGold(0);
                newHero.setHealth(50);
                this.player.setGold(this.player.getGold() - cost);
                UpdateGoldDisplay(getGoldPanel());
                System.out.println("Найм героя из Таверны выполнен.");
                this.map.getGrid()[cX][cY] = "H";
                this.map.printToConsole();
            }
        }
    }

    private void UpdateGoldDisplay(JPanel gp) {
        for (Component comp : gp.getComponents()) {
            if (comp instanceof JLabel goldLabel) {
                goldLabel.setText("Золото: " + this.player.getGold());
            }
        }
    }

    public GamePanel getPanel() {
        return this.panel;
    }

    public GameMap getMap() {
        return this.map;
    }

    public JPanel getGoldPanel() {
        return this.goldPanel;
    }

    public boolean isPlayerTurn() {
        return this.playerTurn;
    }

    public Hero getPlayer() {
        return this.player;
    }

    public Hero getComputer() {
        return this.computer;
    }

    private void endPlayerTurn() {
        if (this.playerTurn) {

            this.player.attackAllUnits();
            this.player.moveAllUnits();
            this.playerTurn = false;
            System.out.println("Ход завершён. Ход компьютера начинается.");
            this.player.resetTurn();

            for (Unit u : this.player.getUnits()) {
                u.resetMovement();
            }

            this.computer.resetTurn();
            this.aiTurn();
            if (eclipseActive && Math.random() < 0.5) {
                endLunarEclipse();
            }
            dataManager.saveGame(this);
        }

    }

    private void aiTurn() {
        this.computer.resetTurn();
        (new SwingWorker<Void, Void>() {
            protected Void doInBackground() {
                while(true) {
                    if (MainLauncher.this.computer.getMovesLeft() > 0 && MainLauncher.this.computer.getTotalDistance() < MainLauncher.this.computer.getMoveRange()) {
                        int oldMoves = MainLauncher.this.computer.getMovesLeft();
                        int oldDist = MainLauncher.this.computer.getTotalDistance();
                        MainLauncher.this.computer.aiLogic();
                        this.publish(new Void[0]);
                        if (MainLauncher.this.computer.getMovesLeft() != oldMoves || MainLauncher.this.computer.getTotalDistance() != oldDist) {
                            try {
                                Thread.sleep(300L);
                            } catch (Exception ex) {
                                System.out.println("Ошибка ожидания: " + ex.getMessage());
                            }
                            continue;
                        }

                        System.out.println("Компьютер не может выполнить ход, завершаем его ход.");
                    }

                    return null;
                }
            }

            protected void done() {
                MainLauncher.this.playerTurn = true;
                System.out.println("Ход компьютера завершён. Ход игрока.");
                MainLauncher.this.checkWin();
                MainLauncher.this.panel.repaint();
            }
        }).execute();
    }

    public void startLunarEclipse() {
        eclipseActive = true;
        System.out.println("🌑 Лунное затмение! Юниты превращаются в оборотней...");

        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(player.getUnits());
        allUnits.addAll(computer.getUnits());

        for (Unit u : allUnits) {
            if (Math.random() < 0.9) {

                u.setSymbol("W");
                u.setImmortal(true);
                this.map.getGrid()[u.getX()][u.getY()] = u.getSymbol();
            }
        }
        this.map.printToConsole();
        MainLauncher.this.panel.repaint();
    }

    private void endLunarEclipse() {
        eclipseActive = false;
        System.out.println("🌕 Затмение завершено. Оборотни возвращаются в нормальное состояние...");

        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(player.getUnits());
        allUnits.addAll(computer.getUnits());

        for (Unit u : allUnits) {
            if (u.getImmortality()) {
                u.setHealth(10);
                u.restoreOriginalStats();
                this.map.getGrid()[u.getX()][u.getY()] = u.getSymbol();
            }
        }

        this.map.printToConsole();
        MainLauncher.this.panel.repaint();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainLauncher ml = new MainLauncher(0, "Test");
            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                ml.player.setTestMode(true);
                ml.computer.setTestMode(true);
                System.out.println("Запущен тестовый режим.");
            }

            ml.setVisible(true);
        });
    }
}

