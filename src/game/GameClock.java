package game;

import java.util.concurrent.atomic.AtomicInteger;

public class GameClock extends Thread {
    AtomicInteger currentTime = new AtomicInteger(0);
    private boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(100);
                currentTime.incrementAndGet();
            } catch (InterruptedException ignored) {}
        }
    }

    public long getTime() {
        return currentTime.get();
    }

    public void stopClock() {
        running = false;
    }

    public int getCurrentHour() {
        return (currentTime.get() / 60) % 24;
    }
}
