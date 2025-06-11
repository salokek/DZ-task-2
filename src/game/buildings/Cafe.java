package game.buildings;

import game.GameClock;
import game.Hero;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.*;

public class Cafe {
    private transient final Semaphore waiters = new Semaphore(3);

    public void eat(String who, Hero hero, GameClock clock) {
        if (!waiters.tryAcquire()) {
            System.out.println(who + " не может войти в кафе – всё занято.");
            return;
        }

        int time;
        if (who.equalsIgnoreCase("hero")) {
            String[] options = {"Перекус (15)", "Обед (30)"};
            int choice = JOptionPane.showOptionDialog(null, "Выберите тип еды:",
                    "Вкусно и точка", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            time = (choice == 1) ? 30 : 15;

        } else {
            time = new Random().nextBoolean() ? 15 : 30;
        }

        long startTime = clock.getTime();
        long endTime = startTime + time;

        System.out.println(who + " ест в кафе на " + time + " мин. [" + startTime + " → " + endTime + "]");

        new Thread(() -> {
            try {
                while (clock.getTime() < endTime) {
                    Thread.sleep(50); // проверка каждые 5 игровых минут
                }
            } catch (InterruptedException ignored) {}
            finally {
                waiters.release();
                if (who.equalsIgnoreCase("hero") && hero != null) {
                    if (time == 15) {
                        hero.setHealth(115);
                        System.out.println("Герой получил прибавку к здоровью: 15");
                    } else {
                        hero.setHealth(130);
                        System.out.println("Герой получил прибавку к здоровью: 30");
                    }
                }
                System.out.println(who + " покинул кафе.");
            }
        }).start();
    }
}

