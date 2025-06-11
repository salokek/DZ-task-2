package game;

import game.buildings.BuildingClass;
import game.buildings.Castle;
import game.logic.GameMap;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainLauncherTest {
    private MainLauncher launcher;
    private Hero player;
    private Castle castle;

    static Logger log = Logger.getLogger(MainLauncherTest.class.getName());

    @BeforeAll
    public static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("ku/log.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            log.addHandler(fileHandler);
            log.setLevel(Level.ALL);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void closeHandler(){
        log.getHandlers()[0].close();
    }

    @BeforeEach
    public void setup() throws IOException, ClassNotFoundException {

        castle = new Castle("TestCastle", 1, 1);
        GameMap map = new GameMap(10, castle, new Castle("EnemyCastle", 8, 8), 0, new GameDataManager("Test"), "Test");
        launcher = new MainLauncher(0, "Test");
        player = new Hero("TestHero", "H", castle, map, launcher);
        map.setPlayer(player);
        launcher.setPlayer(player);

        launcher.getGoldPanel().removeAll();
        launcher.getGoldPanel().add(new JLabel("Золото: " + player.getGold()));
        launcher.eclipseActive = false;

        launcher.getPanel().revalidate();

    }

    @Test
    public void testBuildBuildingWithEnoughGold() {
        player.setGold(100);
        launcher.buildBT(BuildingClass.GUARD_TOWER);

        assertTrue(player.getCastle().hasBuilding(BuildingClass.GUARD_TOWER));
        assertEquals(100 - BuildingClass.GUARD_TOWER.cost, player.getGold());

        log.info("TestBuildingEnoughGoldSuccess");
    }

    @Test
    public void testBuildBuildingWithoutEnoughGold() {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(stream);
        PrintStream oldOutput = System.out;
        System.setOut(printStream);

        player.setGold(100);
        launcher.buildBT(BuildingClass.ARCHERY);

        System.setOut(oldOutput);
        String act = stream.toString();
        String exp = ("Башня арбалетчиков построено!");

        assertEquals(exp, act.substring(0, 29));

        log.warning("Это лог-сообщение с уровнем WARNING внутри теста.");
    }

    @Test
    public void testHireMiniPekaWithBuilding() {
        player.setGold(100);
        player.getCastle().build(BuildingClass.GUARD_TOWER);
        launcher.hireUnit("MiniPeka", 10);

        assertFalse(player.getUnits().isEmpty());
        assertEquals("MiniPeka", player.getUnits().get(0).getName());
        assertEquals(90, player.getGold());
    }

    @Test
    public void testHireMiniPekaWithoutBuilding() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        player.setGold(100);
        launcher.hireUnit("MiniPeka", 10);

        Method method = Hero.class.getDeclaredMethod("calcMoveCost", int.class, int.class, int.class);
        method.setAccessible(true);
        int temp = (int) method.invoke(Hero.class, 10, 2, 3);
        assertEquals(10, temp);

        log.severe("Нарушение шкебеде");

        assertTrue(player.getUnits().isEmpty());
    }
}

