package game.buildings;

public enum NpcBuildingClass {

    PIGSTY(0, 8, "СВОнарник"),
    GUARD_TOWER(4, 1,"Домик для уточки"),
    VKUSNO(2, 5, "Вкусно и точка"),
    BARBER(6, 1, "Барбершоп");

    public final int px;
    public final int py;
    public final String name;

    NpcBuildingClass(int px, int py, String name) {
        this.px = px;
        this.py = py;
        this.name = name;
    }
}
