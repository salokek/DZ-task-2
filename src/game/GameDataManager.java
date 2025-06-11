package game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.*;

public class GameDataManager {
    private static final String SAVES_DIR = "saves";
    private static final String RECORDS_FILE = "records.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .setPrettyPrinting()
            .create();

    private String currentPlayerName;

    public GameDataManager(String playerName) {
        this.currentPlayerName = playerName;
        new File(SAVES_DIR).mkdirs(); // Создаём папку saves, если её нет
    }

    public void viewJsonFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JTextArea textArea = new JTextArea(content, 20, 50);
            textArea.setEditable(false);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(null, scrollPane,
                    "Содержимое файла: " + filePath,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Ошибка чтения файла: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Сохранение и загрузка игры ---
    public void saveGame(MainLauncher game) {
        try (Writer writer = new FileWriter(getSaveFilePath())) {
            GameSaveState state = new GameSaveState(game);
            gson.toJson(state, writer);
            System.out.println("Игра сохранена для игрока: " + currentPlayerName);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    // Загрузка игры по имени игрока (новый метод)
    public GameSaveState loadGame(String playerName) {
        String savePath = SAVES_DIR + File.separator + playerName + "_save.json";
        if (!new File(savePath).exists()) {
            return null;
        }
        try (Reader reader = new FileReader(savePath)) {
            return gson.fromJson(reader, GameSaveState.class);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке: " + e.getMessage());
            return null;
        }
    }

    // Получить список всех сохранённых игроков (новый метод)
    public List<String> getSavedPlayers() {
        File savesFolder = new File(SAVES_DIR);
        File[] saveFiles = savesFolder.listFiles((dir, name) -> name.endsWith("_save.json"));
        if (saveFiles == null) return Collections.emptyList();

        List<String> players = new ArrayList<>();
        for (File file : saveFiles) {
            String name = file.getName().replace("_save.json", "");
            players.add(name);
        }
        return players;
    }

    // --- Работа с рекордами ---
    public void saveRecord(int score) {
        Map<String, Integer> records = loadRecords();
        records.put(currentPlayerName, score);
        try (Writer writer = new FileWriter(RECORDS_FILE)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении рекорда: " + e.getMessage());
        }
    }

    public Map<String, Integer> loadRecords() {
        try (Reader reader = new FileReader(RECORDS_FILE)) {
            return gson.fromJson(reader, new TypeToken<Map<String, Integer>>(){}.getType());
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    private String getSaveFilePath() {
        return SAVES_DIR + File.separator + currentPlayerName + "_save.json";
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }
}