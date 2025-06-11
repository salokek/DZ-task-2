package game.editor;

public class EditorMap {
    private int size;
    private String[][] grid;
    private String[][] baseGrid;

    public EditorMap(int size) {
        this.size = size;
        this.grid = new String[size][size];
        this.baseGrid = new String[size][size];
        this.initMap();
    }

    private void initMap() {
        int px;
        int py;
        for(px = 0; px < this.size; ++px) {
            for(py = 0; py < this.size; ++py) {
                this.grid[px][py] = ".";

                this.baseGrid[px][py] = this.grid[px][py];
            }
        }

        System.out.println("Карта инициализирована. Размер: " + this.size + "x" + this.size);
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


    public void printToConsole() {
        System.out.println("Текущее состояние редактируемой карты:");

        for(int j = 0; j < this.size; ++j) {
            for(int i = 0; i < this.size; ++i) {
                System.out.print(this.grid[i][j] + " ");
            }

            System.out.println();
        }

        System.out.println();
    }
}


