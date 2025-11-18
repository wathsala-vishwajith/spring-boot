package behavioral.memento;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo class showing Memento pattern in action
 */
public class MementoDemo {
    public static void main(String[] args) {
        System.out.println("=== Memento Pattern Demo ===\n");

        System.out.println("--- Text Editor with Undo ---");
        TextEditor editor = new TextEditor();
        EditorHistory history = new EditorHistory();

        editor.setText("Version 1");
        history.save(editor.save());
        System.out.println("Current: " + editor.getText());

        editor.setText("Version 2");
        history.save(editor.save());
        System.out.println("Current: " + editor.getText());

        editor.setText("Version 3");
        System.out.println("Current: " + editor.getText());

        System.out.println("\nUndoing...");
        editor.restore(history.undo());
        System.out.println("After undo: " + editor.getText());

        editor.restore(history.undo());
        System.out.println("After undo: " + editor.getText());

        System.out.println("\n--- Game Save System ---");
        Game game = new Game(100, 1, "Forest");
        GameCaretaker caretaker = new GameCaretaker();

        System.out.println("Initial state: " + game);
        caretaker.saveGame(game.save());

        game.setHealth(50);
        game.setLevel(2);
        game.setLocation("Cave");
        System.out.println("After playing: " + game);
        caretaker.saveGame(game.save());

        game.setHealth(10);
        game.setLevel(2);
        game.setLocation("Boss Room");
        System.out.println("In danger: " + game);

        System.out.println("\nLoading previous save...");
        game.restore(caretaker.loadGame(1));
        System.out.println("Restored: " + game);
    }
}

// Memento
class TextMemento {
    private final String text;

    public TextMemento(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

// Originator
class TextEditor {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public TextMemento save() {
        return new TextMemento(text);
    }

    public void restore(TextMemento memento) {
        text = memento.getText();
    }
}

// Caretaker
class EditorHistory {
    private List<TextMemento> history = new ArrayList<>();

    public void save(TextMemento memento) {
        history.add(memento);
    }

    public TextMemento undo() {
        if (!history.isEmpty()) {
            return history.remove(history.size() - 1);
        }
        return null;
    }
}

// Game Example
class GameMemento {
    private final int health;
    private final int level;
    private final String location;

    public GameMemento(int health, int level, String location) {
        this.health = health;
        this.level = level;
        this.location = location;
    }

    public int getHealth() { return health; }
    public int getLevel() { return level; }
    public String getLocation() { return location; }
}

class Game {
    private int health;
    private int level;
    private String location;

    public Game(int health, int level, String location) {
        this.health = health;
        this.level = level;
        this.location = location;
    }

    public void setHealth(int health) { this.health = health; }
    public void setLevel(int level) { this.level = level; }
    public void setLocation(String location) { this.location = location; }

    public GameMemento save() {
        return new GameMemento(health, level, location);
    }

    public void restore(GameMemento memento) {
        this.health = memento.getHealth();
        this.level = memento.getLevel();
        this.location = memento.getLocation();
    }

    @Override
    public String toString() {
        return "Game[health=" + health + ", level=" + level + ", location=" + location + "]";
    }
}

class GameCaretaker {
    private List<GameMemento> saves = new ArrayList<>();

    public void saveGame(GameMemento memento) {
        saves.add(memento);
        System.out.println("Game saved (Save #" + saves.size() + ")");
    }

    public GameMemento loadGame(int index) {
        if (index >= 0 && index < saves.size()) {
            return saves.get(index);
        }
        return null;
    }
}
