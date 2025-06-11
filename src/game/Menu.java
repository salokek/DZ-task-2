package game;

import java.awt.*;
import javax.swing.*;

import game.editor.Editor;

public class Menu extends JFrame {

    public Menu() {
        this.setTitle("Menu");
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.setSize(1000, 1000);

        JPanel choice = new JPanel(new GridLayout(4, 1)); // Увеличили на 1 строку
        choice.setBorder(BorderFactory.createTitledBorder("Выбор режима"));

        JPanel namePanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Ваше имя:");
        JTextField nameField = new JTextField(15);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        choice.add(namePanel);

        JButton newGameB = new JButton("Новая игра");
        newGameB.addActionListener((ev) -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, введите ваше имя!");
                return;
            }
            MainLauncher newGame = new MainLauncher(0, playerName);
            newGame.setVisible(true);
            this.setVisible(false);
        });
        choice.add(newGameB);

        JButton UploadB = new JButton("Загрузить карту");
        UploadB.addActionListener((ev) -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, введите ваше имя!");
                return;
            }
            Object[] options = {"Из редактора карт", "Из предыдущего сохранения"};
            int choice2 = JOptionPane.showOptionDialog(this,
                    "Выберите тип загрузки:",
                    "Загрузка игры",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            MainLauncher newGame;
            if (choice2 == 0) {
                newGame = new MainLauncher(1, playerName);
            } else if (choice2 == 1) {
                newGame = new MainLauncher(2, playerName);
            } else {
                return;
            }

            newGame.setVisible(true);
            this.setVisible(false);
        });
        choice.add(UploadB);

        JButton EditorB = new JButton("Открыть редактор карт");
        EditorB.addActionListener((ev) -> {
            Editor newCart = new Editor();
            newCart.setVisible(true);
            this.setVisible(false);
        });
        choice.add(EditorB);

        this.add(choice, "Center");
        StyleAllButtons(choice);
    }

    private void StyleAllButtons(JPanel p) {
        for (Component comp : p.getComponents()) {
            if (comp instanceof JButton button) {
                button.setBackground(new Color(240, 255, 240));
                button.setForeground(Color.BLACK);
                button.setFont(new Font("Arial", Font.BOLD, 14));
                button.setFocusPainted(false);
                button.setBorderPainted(false);
            } else if (comp instanceof JPanel) {
                StyleAllButtons((JPanel) comp);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Menu m = new Menu();

            m.setVisible(true);
        });
    }
}

