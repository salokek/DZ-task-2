package game;

import game.buildings.Castle;
import game.logic.GameMap;
import game.Hero;
import java.util.List;

public class GameSaveState {
    private Hero player;
    private Hero computer;
    private boolean playerTurn;
    private GameMap map;
    private Castle playerCastle;
    private Castle computerCastle;

    public GameSaveState(MainLauncher game) {
        this.player = game.getPlayer();
        this.computer = game.getComputer();
        this.playerTurn = game.isPlayerTurn();
        this.map = game.getMap();
        this.playerCastle = game.getPlayer().getCastle();
        this.computerCastle = game.getComputer().getCastle();
    }

    public Hero getPlayer() { return player; }
    public Hero getComputer() { return computer; }
    public boolean isPlayerTurn() { return playerTurn; }
    public GameMap getMap() { return map; }
    public Castle getPlayerCastle() { return playerCastle; }
    public Castle getComputerCastle() { return computerCastle; }
}