package game.buildings;

public enum BuildingClass {

    TAVERN(30, "Таверна", "Найм героев"),
    GUARD_TOWER(20, "Сторожевой пост", "Найм копейщиков"),
    ARCHERY(30, "Башня арбалетчиков", "Найм арбалетчиков"),
    ARMORY(40, "Оружейная", "Найм мечников"),
    STABLE(50, "Конюшня", "Увеличивает перемещение героев"),
    ARENA(60, "Арена", "Найм кавалеристов"),
    CATHEDRAL(80, "Собор", "Найм паладинов");

    public final int cost;
    public final String name;
    public final String description;

    BuildingClass(int cost, String name, String description) {
        this.cost = cost;
        this.name = name;
        this.description = description;
    }
}
