package game;

import java.awt.Color;

public class Unit {
    private String name;
    private String symbol;
    private int health;
    private int damage;
    private int moveRange;
    private int cost;
    private int attackRange;
    private Color color;
    private int movesLeft;
    private int x;
    private int y;

    private boolean immortality;
    private int originalAttack;
    private String originalSymbol;

    public Unit(String name, String symbol, int health, int damage, int moveRange, int cost, int attackRange, Color color) {
        this.name = name;
        this.symbol = symbol;
        this.health = health;
        this.damage = damage;
        this.moveRange = moveRange;
        this.cost = cost;
        this.attackRange = attackRange;
        this.color = color;
        this.movesLeft = moveRange;
        this.x = -1;
        this.y = -1;
        this.originalAttack = attackRange;
        this.originalSymbol = symbol;
        boolean immortality = false;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String newSymbol) {
        this.symbol = newSymbol;
    }

    public int getHealth() {
        return this.health;
    }

    public int getDamage() {
        return this.damage;
    }

    public int getMoveRange() {
        return this.moveRange;
    }

    public boolean getImmortality() {
        return this.immortality;
    }

    public int getAttackRange() {
        return this.attackRange;
    }

    public Color getColor() {
        return this.color;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getMovesLeft() {
        return this.movesLeft;
    }

    public void setX(int xx) {
        this.x = xx;
    }

    public void setY(int yy) {
        this.y = yy;
    }

    public void setMovesLeft(int m) {
        this.movesLeft = m;
    }

    public void setHealth(int h) {
        this.health = h;
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public void resetMovement() {
        this.movesLeft = this.moveRange;
    }

    public void restoreOriginalStats() {
        this.attackRange = this.originalAttack;
        this.symbol = this.originalSymbol;
        this.immortality = false;
    }
    public void setImmortal(boolean newImmortality) {
        this.immortality = newImmortality;
    }

    public void attack(Unit target) {
        target.health -= this.damage;
        System.out.println(this.name + " наносит " + this.damage + " урона " + target.name);
        if (target.health <= 0) {
            target.health = 0;
            System.out.println(target.name + " уничтожен!");
        }

    }

    public void setAttackRange(int newAttackRange) {
        this.attackRange = newAttackRange;
    }
}

