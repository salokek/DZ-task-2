package game;


import game.buildings.*;

import java.io.IOException;
import java.util.Random;

public class GameWorld {
    private final Pigsty pigsty = new Pigsty();
    private final Cafe cafe = new Cafe();
    private final DuckHome hotel = new DuckHome();
    private final BarberShop barbershop = new BarberShop();
    private transient final Random random = new Random();
    private final GameClock clock = new GameClock();
    private Hutor hutor;

    public void startWorld() {
        try {
            hutor = Hutor.loadState("ku/hamlet.dat");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            hutor = new Hutor();
        }
        clock.start();
        Thread npcManager = new Thread(() -> {
            long nextNPCSpawn = clock.getTime();
            while (true) {
                long now = clock.getTime();
                if (now >= nextNPCSpawn) {
                    Thread npc = new Thread(() -> simulateNPC("NPC-" + Thread.currentThread().threadId()));
                    npc.start();
                    nextNPCSpawn = now + 10;
                }
                if (clock.getCurrentHour() <= 10 && clock.getCurrentHour() >= 1) {
                    hutor.update(this.clock, this.pigsty);
                    System.out.println(pigsty.getPigs().size());
                    try {
                        hutor.saveState("ku/hamlet.dat");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }
        });

        npcManager.setDaemon(true);
        npcManager.start();
    }

    public void simulateNPC(String name) {
        int choice = random.nextInt(4);
        switch (choice) {
            case 0 -> {
                double w = 50 + random.nextDouble() * 150;
                if (pigsty.enter(name, w)) {
                    long enterTime = clock.getTime();
                    long leaveTime = enterTime + 20;

                    new Thread(() -> {
                        while (clock.getTime() < leaveTime) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ignored) {}
                        }
                        //pigsty.leave(name, w);
                    }).start();
                }
            }
            case 1 -> hotel.rest(name, clock);
            case 2 -> cafe.eat(name, null, clock);
            case 3 -> barbershop.getHaircut(name, clock);
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public boolean playerEnterPigsty() {
        return pigsty.enter("Hero", 65);
    }

    public void playerGoToCafe(Hero hero) {
        cafe.eat("Hero", hero, clock);
    }

    public void playerGoToBarber() {
        barbershop.getHaircut("Hero", clock);
    }

    public void playerGoToHotel() {
        hotel.rest("Hero", clock);
    }

    public void eatHutor(Hero player) {
        this.hutor.heroEatsPigs(player);
    }
}
