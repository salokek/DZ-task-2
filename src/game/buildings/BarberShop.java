package game.buildings;

import game.GameClock;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.*;

public class BarberShop {
    private transient final Semaphore chairs = new Semaphore(2);

    public void getHaircut(String who, GameClock clock) {
        if (!chairs.tryAcquire()) {
            System.out.println(who + " не может войти в парикмахерскую – всё занято.");
            return;
        }

        int duration;
        if (who.equalsIgnoreCase("hero")) {
            String[] options = {"Обычная (10)", "Модная (30)"};
            int choice = JOptionPane.showOptionDialog(null, "Выберите стрижку:",
                    "Фембойная", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            duration = (choice == 1) ? 30 : 10;
        } else {
            duration = new Random().nextBoolean() ? 10 : 30;
        }

        long startTime = clock.getTime();
        long endTime = startTime + duration;

        System.out.println(who + " начал стрижку на " + duration + " мин. [" + startTime + " → " + endTime + "]");

        new Thread(() -> {
            try {
                while (clock.getTime() < endTime) {
                    Thread.sleep(50);
                }
                System.out.println(who + " завершил стрижку на " + clock.getTime() + "-й минуте.");
            } catch (InterruptedException ignored) {}
            finally {
                chairs.release();
            }
        }).start();
    }

}
