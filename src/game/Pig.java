package game;

import java.io.Serializable;

public class Pig implements Serializable {
    private final double weight;
    private final String name;

    public Pig(double weight, String name) {
        this.weight = weight;
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }
}
