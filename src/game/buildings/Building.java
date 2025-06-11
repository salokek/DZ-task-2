package game.buildings;

public class Building {
    private final BuildingClass type;

    public Building(BuildingClass type) {
        this.type = type;
    }

    public BuildingClass getType() {
        return this.type;
    }
}

