package game.buildings;

import game.Pig;

import java.util.ArrayList;
import java.util.List;

public class Pigsty {
    private double totalWeight = 0;
    private final double maxWeight = 1000.0;
    private int numberOfPigs = 0;
    private List<Pig> pigs = new ArrayList<>();

    public synchronized boolean enter(String who, double weight) {
        if (totalWeight + weight > maxWeight) {
            System.out.println(who + " не может войти в свинарник – перегруз.");
            return false;
        }
        totalWeight += weight;
        numberOfPigs += 1;
        Pig newPig = new Pig(weight, who);
        pigs.add(newPig);
        System.out.println(who + " вошёл в свинарник. Текущий вес: " + totalWeight);

        return true;
    }

    public synchronized void exit(String who) {
        if (who.equalsIgnoreCase("hero")) {
            double weight = who.equals("hero") ? 65 : 50;
            totalWeight -= weight;
            numberOfPigs -= 1;
            for (int i = 0; i < numberOfPigs; ++i) {
                if (pigs.get(i).getName().equals(who)) {
                    pigs.remove(i);
                    return;
                }
            }
            System.out.println(who + " вышел из свинарника. Остаток веса: " + totalWeight);
        }
    }

    public synchronized void leave(String who, double weight) {
        totalWeight -= weight;
        numberOfPigs -= 1;
        for (int i = 0; i < numberOfPigs; ++i) {
            if (pigs.get(i).getName().equals(who)) {
                pigs.remove(i);
                return;
            }
        }
        System.out.println(who + " покинул свинарник. Общий вес: " + totalWeight);
    }

    public List<Pig> getPigs() {
        return pigs;
    }
}
