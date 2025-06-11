package game;

import com.google.gson.annotations.Expose;
import game.buildings.Castle;
import game.logic.GameMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Hero {
    @Expose
    private String name;
    @Expose
    private String symbol;
    @Expose
    private Castle castle;
    @Expose
    private ArrayList<Unit> units;
    @Expose
    private int gold;
    @Expose
    private int x;
    @Expose
    private int y;
    @Expose
    private int health;
    @Expose
    private int moveRange;
    @Expose
    private int movesLeft;
    @Expose
    private int totalDistance;
    @Expose(serialize = false)
    private transient GameMap map;
    @Expose(serialize = false)
    private transient MainLauncher game;
    private transient boolean testMode;

    public Hero(String name, String symbol, Castle castle, GameMap map, MainLauncher game) {
        this.name = name;
        this.symbol = symbol;
        this.castle = castle;
        this.x = castle.getX();
        this.y = castle.getY();
        this.map = map;
        this.game = game;
        this.units = new ArrayList<>();
        this.gold = 100;
        this.health = 100;
        this.moveRange = 4;
        this.movesLeft = this.moveRange;
        this.totalDistance = 0;
        this.testMode = false;
    }
    public Hero() {
        this.gold = 100;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Castle getCastle() {
        return this.castle;
    }

    public ArrayList<Unit> getUnits() {
        return this.units;
    }

    public int getGold() {
        return this.gold;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHealth() {
        return this.health;
    }

    public int getMoveRange() {
        return this.moveRange;
    }

    public int getMovesLeft() {
        return this.movesLeft;
    }

    public int getTotalDistance() {
        return this.totalDistance;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setX(int xx) {
        this.x = xx;
    }

    public void setY(int yy) {
        this.y = yy;
    }

    public void setHealth(int hh) {
        this.health = hh;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public void setGame(MainLauncher game) {
        this.game = game;
    }

    public boolean isTestMode() {
        return this.testMode;
    }

    public void setTestMode(boolean tm) {
        this.testMode = tm;
        if (tm) {
            this.health = 999;
            this.gold = 999;
            this.moveRange = 10;
            this.movesLeft = 10;
            System.out.println(this.name + " в тестовом режиме: health=" + this.health + ", gold=" + this.gold);
        }

    }

    public boolean hasArmy() {
        return !this.units.isEmpty();
    }

    public void resetTurn() {
        this.movesLeft = this.moveRange;
        this.totalDistance = 0;

        for (Unit u : this.units) {
            u.resetMovement();
        }

        System.out.println(this.name + " начинает новый ход. Осталось ходов: " + this.movesLeft);
    }

    public void changeMoveRange(int delta) {
        this.moveRange += delta;
        this.movesLeft += delta;
        System.out.println(this.name + " увеличил дальность на " + delta + ". Новая дальность: " + this.moveRange);
    }

    private static int calcMoveCost(int distance, int newX, int newY) {
        int coefficient = 1;
        return distance * coefficient;
    }

    public void moveUnit(Unit u, int nx, int ny) {
        int distance = Math.abs(u.getX() - nx) + Math.abs(u.getY() - ny);
        if (distance != 0) {
            if (this.map.getBaseGrid()[nx][ny].equals("#")) {
                System.out.println("Клетка (" + nx + "," + ny + ") заблокирована препятствием.");
            } else {
                int finalCost = calcMoveCost(distance, nx, ny);
                String currentName = this.name;
                System.out.println(currentName + " пытается переместить юнита " + u.getName() + " с (" + u.getX() + "," + u.getY() + ") на (" + nx + "," + ny + "). Стоимость: " + finalCost);
                if (u.getMovesLeft() >= finalCost) {
                    if (this.map.isWalkable(nx, ny, false) && this.map.positionFreeForHero(nx, ny)) {
                        this.map.getGrid()[u.getX()][u.getY()] = this.map.getBaseGrid()[u.getX()][u.getY()];
                        u.setX(nx);
                        u.setY(ny);
                        this.map.getGrid()[nx][ny] = u.getSymbol();
                        u.setMovesLeft(u.getMovesLeft() - finalCost);
                        if (this == this.map.getComputer() && this.map.checkVictory(nx, ny, false)) {
                            System.out.println(this.name + " юнит достиг зоны врага!");
                            this.game.checkWin();
                        }

                        currentName = this.name;
                        System.out.println(currentName + " переместил юнита " + u.getName() + ". Осталось ходов: " + u.getMovesLeft());
                        this.map.printToConsole();
                    } else {
                        System.out.println("Перемещение юнита " + u.getName() + " невозможно (целая клетка занята).");
                    }
                } else {
                    System.out.println("Недостаточно ходов для перемещения юнита " + u.getName());
                }

            }
        }
    }

    private void moveUnitOneStepToward(Unit u, int tx, int ty) {
        int nx = u.getX() + Integer.compare(tx, u.getX());
        int ny = u.getY() + Integer.compare(ty, u.getY());
        if (this.map.getBaseGrid()[nx][ny].equals("#")) {
            System.out.println("Клетка (" + nx + "," + ny + ") заблокирована препятствием.");
        } else {
            if (this.map.isWalkable(nx, ny, false)) {
                this.moveUnit(u, nx, ny);
            } else {
                boolean success = false;
                for(int tries = 0; tries < 4; ++tries) {
                    Random random = new Random();
                    int direction = random.nextInt(4);
                    int altx = u.getX();
                    int alty = u.getY();
                    switch (direction) {
                        case 0 -> alty = u.getY() + 1;
                        case 1 -> altx = u.getX() + 1;
                    }

                    if (!this.map.getBaseGrid()[altx][alty].equals("#") && this.map.isWalkable(altx, alty, false)) {
                        this.moveUnit(u, altx, alty);
                        success = true;
                        break;
                    }
                }

                if (!success) {
                    System.out.println("Юнит " + u.getName() + " не нашёл обхода.");
                }
            }

        }
    }

    public void moveHero(int nx, int ny) {
        int dist = Math.abs(this.x - nx) + Math.abs(this.y - ny);
        if (dist == 0) {
            System.out.println("Герой " + this.name + " уже находится на целевой клетке (" + nx + "," + ny + ").");
        } else if (this.map.getBaseGrid()[nx][ny].equals("#")) {
            System.out.println("Герой " + this.name + " не может переместиться на (" + nx + "," + ny + ") – препятствие.");
        } else {
            int finalCost = this.calcMoveCost(dist, nx, ny);
            System.out.println(this.name + " пытается переместить героя с (" + this.x + "," + this.y + ") на (" + nx + "," + ny + "). Стоимость: " + finalCost);
            if (this.movesLeft >= finalCost) {
                if (this.map.isWalkable(nx, ny, false)) {
                    this.map.getGrid()[this.x][this.y] = this.map.getBaseGrid()[this.x][this.y];
                    this.x = nx;
                    this.y = ny;
                    this.map.getGrid()[nx][ny] = this.symbol;
                    this.movesLeft -= finalCost;
                    this.totalDistance += finalCost;
                    boolean iAmPlayer = this == this.map.getPlayer();
                    if (this.map.checkVictory(nx, ny, iAmPlayer)) {
                        System.out.println(this.name + " герой достиг зоны врага!");
                        this.game.checkWin();
                    }

                    System.out.println(this.name + " переместил героя. Осталось ходов: " + this.movesLeft);
                    this.map.printToConsole();
                } else {
                    System.out.println("Герой " + this.name + " не может переместиться на (" + nx + "," + ny + ")");
                }
            } else {
                System.out.println("Герою " + this.name + " недостаточно ходов для перемещения.");
            }

        }
    }

    private void moveHeroStep(int tx, int ty) {
        int nx = this.x + Integer.compare(tx, this.x);
        int ny = this.y + Integer.compare(ty, this.y);
        if (this.map.getBaseGrid()[nx][ny].equals("#")) {
            System.out.println("Клетка (" + nx + "," + ny + ") заблокирована препятствием.");
        } else {
            if (this.map.isWalkable(nx, ny, false)) {
                this.moveHero(nx, ny);
            } else {
                boolean success = false;

                for(int tries = 0; tries < 4; ++tries) {
                    Random rr = new Random();
                    int dir = rr.nextInt(4);
                    int altx = this.x;
                    int alty = this.y;
                    switch (dir) {
                        case 0 -> alty = this.y - 1;
                        case 1 -> alty = this.y + 1;
                        case 2 -> altx = this.x - 1;
                        case 3 -> altx = this.x + 1;
                    }

                    if (!this.map.getBaseGrid()[altx][alty].equals("#") && this.map.isWalkable(altx, alty, false)) {
                        this.moveHero(altx, alty);
                        success = true;
                        break;
                    }
                }

                if (!success) {
                    System.out.println("Герой " + this.name + ": Путь к замку заблокирован, альтернативы не найдены!");
                }
            }

        }
    }

    public void moveAllUnits() {
        Hero enemyHero = this == this.map.getPlayer() ? this.map.getComputer() : this.map.getPlayer();
        int px = enemyHero.getCastle().getX();
        int py = enemyHero.getCastle().getY();

        for (Unit u : this.units) {
            System.out.println("Количество оставших ходов: " + u.getMovesLeft());
            if (u.getMovesLeft() > 0) {
                String currentName = this.name;
                System.out.println(currentName + " перемещает юнита " + u.getName() + " к замку противника (" + px + "," + py + ")");
                this.moveUnitOneStepToward(u, px, py);
            }
        }

    }

    public void attackAllUnits() {
        System.out.println(this.name + " все юниты пытаются атаковать вражеских.");
        ArrayList<Unit> enemyUnits = this == this.map.getPlayer() ? this.map.getComputer().getUnits() : this.map.getPlayer().getUnits();
        if (enemyUnits.isEmpty()) {
            System.out.println("Нет вражеских юнитов для атаки.");
        }


        for (Unit u : units) {
            if (u.getMovesLeft() > 0) {
                Unit closest = null;
                int minDamage = Integer.MAX_VALUE;

                for (Unit enemy : enemyUnits) {
                    int damageDistance = Math.abs(u.getX() - enemy.getX()) + Math.abs(u.getY() - enemy.getY());
                    if (damageDistance <= u.getAttackRange() && damageDistance < minDamage  && !enemy.getImmortality()) {
                        closest = enemy;
                        minDamage = damageDistance;
                    }
                    if (enemy.getImmortality()) {
                        System.out.println("Враг оборотень, он бессертен.");
                    }
                }
                if (closest == null) {
                    System.out.println("Вражеские юниты далеко.");
                }
                if (closest != null) {
                    System.out.println(this.name + " юнит " + u.getName() + " атакует " + closest.getName() + " на расстоянии " + minDamage);
                    u.attack(closest);

                    if (!closest.isAlive()) {
                        System.out.println(this.name + " уничтожил юнита " + closest.getName());
                        enemyUnits.remove(closest);
                        this.map.getGrid()[closest.getX()][closest.getY()] = this.map.getBaseGrid()[closest.getX()][closest.getY()];
                        this.gold += 100;
                        System.out.println(this.name + " получает +100 золота за убийство " + closest.getName());
                    }

                    u.setMovesLeft(0);
                    this.map.printToConsole();
                }
            }
        }

        Hero enemyHero = (this == this.map.getPlayer()) ? this.map.getComputer() : this.map.getPlayer();
        int distance = Math.abs(this.x - enemyHero.getX()) + Math.abs(this.y - enemyHero.getY());

        if (distance <= 1) {
            attackHero();
        }

    }

    private void attackHero() {
        if (this.movesLeft > 0) {
            System.out.println(this.name + " герой пытается атаковать ближайшего врага.");
            Hero enemyHero = this == this.map.getPlayer() ? this.map.getComputer() : this.map.getPlayer();
            int distance = Math.abs(this.x - enemyHero.getX()) + Math.abs(this.y - enemyHero.getY());
            if (distance <= 1) {
                String playerName = this.name;
                System.out.println(playerName + " герой атакует врага " + enemyHero.getName());
                int heroDamage = 20;
                enemyHero.setHealth(enemyHero.getHealth() - heroDamage);
                if (enemyHero.getHealth() <= 0) {
                    playerName = this.name;
                    System.out.println(playerName + " герой уничтожил врага " + enemyHero.getName());
                    enemyHero.setHealth(0);
                    this.gold += 10;
                    playerName = this.name;
                    System.out.println(playerName + " получает +10 золота за убийство героя " + enemyHero.getName());
                }

                this.movesLeft = 0;
                this.map.printToConsole();
            } else {
                System.out.println("Враг не в пределах атаки героя (расстояние " + distance + ").");
            }

        }
    }

    private void moveHeroToEnemyCastle() {
        int px = this.map.getPlayer().getCastle().getX();
        int py = this.map.getPlayer().getCastle().getY();
        System.out.println(this.name + " герой двигается к замку врага (" + px + "," + py + ")");
        this.moveHeroStep(px, py);
    }

    public void aiLogic() {
        System.out.println("Компьютер принимает решение...");
        this.attackAllUnits();
        this.moveAllUnits();
        this.moveHeroToEnemyCastle();
    }

    private boolean isEnemyNear() {
        ArrayList<Unit> enemyUnits = this == this.map.getPlayer() ? this.map.getComputer().getUnits() : this.map.getPlayer().getUnits();
        Iterator<Unit> enemyIterator = enemyUnits.iterator();
        int distance;
        do {
            if (!enemyIterator.hasNext()) {
                return false;
            }

            Unit enemy = (Unit) enemyIterator.next();
            distance = Math.abs(this.x - enemy.getX()) + Math.abs(this.y - enemy.getY());
        } while(distance > 3);

        return true;
    }

    private boolean isWeaker() {
        int myp = this.calcPower(this.units);
        ArrayList<Unit> enemyUnits = this == this.map.getPlayer() ? this.map.getComputer().getUnits() : this.map.getPlayer().getUnits();
        int enp = this.calcPower(enemyUnits);
        return myp < enp;
    }

    private int calcPower(ArrayList<Unit> unitsList) {
        int totalPower = 0;

        for (Unit u : unitsList) {
            totalPower += u.getHealth() + u.getDamage() * 5;
        }

        return totalPower;
    }

}

