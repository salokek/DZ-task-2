package game.buildings;

import game.GameClock;
import game.Hero;
import game.Pig;

import java.util.*;
import java.io.*;

public class Hutor implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Pig> pigs = new ArrayList<>();
    private static final int MAX_WEIGHT = 500;
    private boolean exploded = false;

    public void update(GameClock clock, Pigsty pigsty) {
        int hour = clock.getCurrentHour();
        if (hour >= 1 && hour <= 10 && !exploded) {
            Random rand = new Random();
            List<Pig> candidates = pigsty.getPigs();
            if (candidates.isEmpty()) {
                System.out.println("NO");
                return;
            }
            for (Pig pig : candidates) {
                System.out.println(pig.getName());
                if (rand.nextBoolean()) {
                    if (getTotalWeight() + pig.getWeight() <= MAX_WEIGHT) {
                        System.out.println("Svin prishel v hutor, ves:" + getTotalWeight());
                        pigs.add(pig);
                    } else {
                        explode();
                        return;
                    }
                }
            }
        }
        if (hour > 10) {
            pigs.clear();
        }
    }

    public double getTotalWeight() {
        double ans = 0;
        for (Pig pig : pigs) {
            ans += pig.getWeight();
        }
        return ans;
    }

    public void heroEatsPigs(Hero hero) {
        if (!exploded) {
            double eatenWeight = 0;
            System.out.println("Производится попытка съесть свиней");
            Iterator<Pig> iter = pigs.iterator();
            while (iter.hasNext()) {
                Pig pig = iter.next();
                if (eatenWeight + pig.getWeight() <= MAX_WEIGHT) {
                    eatenWeight += pig.getWeight();
                    iter.remove();
                } else {
                    break;
                }
            }
            hero.setHealth(100);
        } else {
            System.out.println("Хутор взорван :(");
        }

    }

    public void explode() {
        exploded = true;
        pigs.clear();
        System.out.println("Хутор взорвался! Все в радиусе 10 блоков получают урон.");

    }

    public void saveState(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    public static Hutor loadState(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Hutor) in.readObject();
        }
    }
}

