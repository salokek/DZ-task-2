package game.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.google.gson.annotations.Expose;
import game.GameDataManager;
import game.GameSaveState;
import game.Hero;
import game.buildings.Castle;
import game.Unit;

public class GameMap {
    @Expose
    private int size;
    @Expose
    private String[][] grid;
    @Expose
    private String[][] baseGrid;
    @Expose(serialize = false)
    private transient Hero player;
    @Expose(serialize = false)
    private transient Hero computer;
    private transient int mapStatus;

    public GameMap(int size, Castle playerC, Castle compC, int sw, GameDataManager manager, String playerName) {
        this.size = size;
        this.grid = new String[size][size];
        this.baseGrid = new String[size][size];
        this.mapStatus = sw;
        if (sw == 1) {
            getNewGrid(new File("ku/save.txt"));
        }
        else if (sw == 2) {
            GameSaveState newGame = manager.loadGame(playerName);
            GameMap newMap = newGame.getMap();
            this.size = newMap.getSize();
            for (int x = 0; x < this.size; ++x) {
                for (int y = 0; y < this.size; ++y) {
                    this.grid[x][y] = newMap.getGrid()[x][y];
                    this.baseGrid[x][y] = newMap.getBaseGrid()[x][y];
                }
            }
        } else {
            this.initMap(playerC, compC);
        }
    }

    public void getNewGrid(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[][] grid = new String[this.size][this.size];
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split("");
                for (int col = 0; col < cells.length; col++) {
                    grid[row][col] = cells[col];
                }
                row++;
            }
            setGrid(grid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setGrid(String[][] newGrid) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.grid[i][j] = newGrid[i][j];
                this.baseGrid[i][j] = newGrid[i][j];
            }
        }
    }

    private void initMap(Castle playerCastle, Castle computerCastle) {
        int px;
        int py;
        for(px = 0; px < this.size; ++px) {
            for(py = 0; py < this.size; ++py) {
                if (px < this.size / 3 && py < this.size / 3) {
                    this.grid[px][py] = "P";
                } else if (px >= 2 * this.size / 3 + 1 && py >= 2 * this.size / 3 + 1) {
                    this.grid[px][py] = "C";
                } else {
                    this.grid[px][py] = ".";
                }
                this.baseGrid[px][py] = this.grid[px][py];
            }
        }

        this.grid[0][8] = "I";
        this.baseGrid[0][8] = "I";

        this.grid[0][7] = "L";
        this.baseGrid[0][7] = "L";

        this.grid[4][1] = "U";
        this.baseGrid[4][1] = "U";

        this.grid[2][5] = "Z";
        this.baseGrid[2][5] = "Z";

        this.grid[6][1] = "V";
        this.baseGrid[6][1] = "V";


        this.grid[playerCastle.getX()][playerCastle.getY()] = "P_Castle";
        this.baseGrid[playerCastle.getX()][playerCastle.getY()] = "P_Castle";
        this.grid[computerCastle.getX()][computerCastle.getY()] = "C_Castle";
        this.baseGrid[computerCastle.getX()][computerCastle.getY()] = "C_Castle";

        px = playerCastle.getX();
        py = playerCastle.getY();
        int cx = computerCastle.getX();
        int cy = computerCastle.getY();

        while(px != cx || py != cy) {
            if ((px != playerCastle.getX() || py != playerCastle.getY()) && (px != computerCastle.getX() || py != computerCastle.getY())) {
                this.grid[px][py] = "R";
                this.baseGrid[px][py] = "R";
            }

            if (px < cx) {
                ++px;
            } else if (px > cx) {
                --px;
            }

            if (py < cy) {
                ++py;
            } else if (py > cy) {
                --py;
            }
        }

        this.addDiagonalObstacles();
        System.out.println("Карта инициализирована. Размер: " + this.size + "x" + this.size);
    }

    private void addDiagonalObstacles() {
        int half = this.size / 2;

        for(int i = 2; i < half; ++i) {
            this.grid[i][this.size - 1 - i] = "#";
            this.baseGrid[i][this.size - 1 - i] = "#";
            this.grid[this.size - 1 - i][i] = "#";
            this.baseGrid[this.size - 1 - i][i] = "#";
        }

        System.out.println("Диагональные препятствия размещены.");
    }

    public int getSize() {
        return this.size;
    }

    public String[][] getGrid() {
        return this.grid;
    }

    public String[][] getBaseGrid() {
        return this.baseGrid;
    }

    public Hero getPlayer() {
        return this.player;
    }

    public Hero getComputer() {
        return this.computer;
    }

    public void setPlayer(Hero h) {
        this.player = h;
    }

    public void setComputer(Hero c) {
        this.computer = c;
    }

    public boolean checkVictory(int x, int y, boolean isPlayer) {
        String zone = this.baseGrid[x][y];
        if (isPlayer) {
            if (zone.equals("C") || zone.equals("C_Castle")) {
                System.out.println("Победа игрока: вошёл в замок " + zone + " (" + x + "," + y + ")");
                return true;
            }
        } else if ((zone.equals("P") || zone.equals("P_Castle"))) {
            System.out.println("Победа компьютера: вошёл в замок " + zone + " (" + x + "," + y + ")");
            return true;
        }

        return false;
    }

    public boolean isWalkable(int x, int y, boolean isPlayer) {
        if (x >= 0 && x < this.size && y >= 0 && y < this.size) {
            if (this.baseGrid[x][y].equals("#")) {
                System.out.println("Клетка заблокирована препятствием: (" + x + "," + y + ")");
                return false;
            } else if (this.cellOccupied(x, y)) {
                System.out.println("Клетка занята: (" + x + "," + y + ")");
                return false;
            } else {
                return true;
            }
        } else {
            System.out.println("Координаты вне карты: (" + x + "," + y + ")");
            return false;
        }
    }

    private boolean cellOccupied(int x, int y) {
        Iterator<Unit> unitsIterator = this.player.getUnits().iterator();

        Unit u;
        while(unitsIterator.hasNext()) {
            u = (Unit) unitsIterator.next();
            if (u.getX() == x && u.getY() == y) {
                return true;
            }
        }

        unitsIterator = this.computer.getUnits().iterator();

        while(unitsIterator.hasNext()) {
            u = (Unit) unitsIterator.next();
            if (u.getX() == x && u.getY() == y) {
                return true;
            }
        }

        return this.player.getX() == x && this.player.getY() == y || this.computer.getX() == x && this.computer.getY() == y;
    }

    public boolean positionFreeForHero(int x, int y) {
        return (this.player.getX() != x || this.player.getY() != y) && (this.computer.getX() != x || this.computer.getY() != y);
    }

    public void printToConsole() {
        System.out.println("Текущее состояние карты:");

        for(int j = 0; j < this.size; ++j) {
            for(int i = 0; i < this.size; ++i) {
                System.out.print(this.grid[i][j] + " ");
            }

            System.out.println();
        }

        System.out.println();
    }
}

