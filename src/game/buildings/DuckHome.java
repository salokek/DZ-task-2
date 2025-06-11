package game.buildings;

import game.GameClock;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.*;

public class DuckHome {
    private transient final Semaphore rooms = new Semaphore(5);

    public void rest(String who, GameClock clock) {
        if (!rooms.tryAcquire()) {
            System.out.println(who + " не может войти в отель – нет свободных комнат.");
            return;
        }

        int days;
        if (who.equalsIgnoreCase("hero")) {
            String[] options = {"Короткий (1)", "Долгий (3)"};
            int choice = JOptionPane.showOptionDialog(null, "Сколько отдохнуть?",
                    "Домик для уточки", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            days = (choice == 1) ? 3 : 1;
        } else {
            days = new Random().nextBoolean() ? 1 : 3;
        }

        long startDay = clock.getTime();
        long endDay = startDay + days * 100;

        System.out.println(who + " отдыхает " + days + " дн. [" + startDay + " → " + endDay + "]");

        new Thread(() -> {
            try {
                while (clock.getTime() < endDay) {
                    Thread.sleep(50);
                }
            } catch (InterruptedException ignored) {}
            finally {
                rooms.release();
                System.out.println(who + " покинул отель.");
            }
        }).start();
    }
}


