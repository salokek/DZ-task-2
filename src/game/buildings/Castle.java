package game.buildings;


import java.util.ArrayList;
import java.util.Iterator;

public class Castle {
    private final String symbol;
    private final int x;
    private final int y;
    private final ArrayList<Building> buildings;

    public Castle(String symbol, int x, int y) {
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.buildings = new ArrayList<>();
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public ArrayList<Building> getBuildings() {
        return this.buildings;
    }

    public void build(BuildingClass type) {
        this.buildings.add(new Building(type));
        System.out.println(type.name + " построено!");
    }

    public boolean hasBuilding(BuildingClass type) {
        Iterator<Building> buildingIterator = this.buildings.iterator();

        Building b;
        do {
            if (!buildingIterator.hasNext()) {
                return false;
            }

            b = buildingIterator.next();
        } while(b.getType() != type);

        return true;
    }
}

