package game;

import game.GameWorld;
import java.util.Random;
import java.util.concurrent.*;

public class NpcManager extends Thread {
    private final GameWorld gameWorld;
    private final Random random = new Random();

    public NpcManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            new Thread(() -> {
                String name = "NPC-" + Thread.currentThread().threadId();
                gameWorld.simulateNPC(name);
            }).start();

            try {
                Thread.sleep(7000);
            } catch (InterruptedException ignored) {}
        }
    }
}

