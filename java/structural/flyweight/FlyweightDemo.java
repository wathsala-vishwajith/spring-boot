package structural.flyweight;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Demo class showing Flyweight pattern in action
 *
 * The Flyweight pattern minimizes memory usage by sharing as much data as possible
 * with similar objects. It's useful when you need to create a large number of similar objects.
 */
public class FlyweightDemo {
    public static void main(String[] args) {
        System.out.println("=== Flyweight Pattern Demo ===\n");

        System.out.println("--- Text Editor Example ---");
        demonstrateTextEditor();

        System.out.println("\n--- Forest Simulation Example ---");
        demonstrateForest();

        System.out.println("\n--- Particle System Example ---");
        demonstrateParticles();

        System.out.println("\n--- Chess Game Example ---");
        demonstrateChess();
    }

    /**
     * Classic example - Text editor with shared character objects
     */
    private static void demonstrateTextEditor() {
        TextEditor editor = new TextEditor();

        // Type some text
        String text = "Hello World! Hello Java!";
        int x = 0, y = 0;

        for (char c : text.toCharArray()) {
            if (c == ' ') {
                x += 5;
            } else {
                editor.addCharacter(c, "Arial", 12, "Black", x, y);
                x += 10;
            }
        }

        editor.display();
        editor.printMemoryUsage();
    }

    /**
     * Real-world example - Forest with shared tree types
     */
    private static void demonstrateForest() {
        Forest forest = new Forest();
        Random random = new Random(42);

        // Plant 1000 trees, but only a few types
        System.out.println("Planting 1000 trees...");
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(1000);
            int y = random.nextInt(1000);

            if (i % 3 == 0) {
                forest.plantTree(x, y, "Oak", "Green", "Oak texture");
            } else if (i % 3 == 1) {
                forest.plantTree(x, y, "Pine", "Dark Green", "Pine texture");
            } else {
                forest.plantTree(x, y, "Birch", "Light Green", "Birch texture");
            }
        }

        forest.display();
        forest.printMemoryUsage();
    }

    /**
     * Real-world example - Particle system with shared particle types
     */
    private static void demonstrateParticles() {
        ParticleSystem particleSystem = new ParticleSystem();
        Random random = new Random(42);

        System.out.println("Creating particle effects...");

        // Create fire effect
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(100);
            int y = random.nextInt(100);
            int velocityX = random.nextInt(10) - 5;
            int velocityY = random.nextInt(10) - 5;
            particleSystem.createParticle("Fire", "Red", x, y, velocityX, velocityY);
        }

        // Create smoke effect
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(100);
            int y = random.nextInt(100);
            int velocityX = random.nextInt(10) - 5;
            int velocityY = random.nextInt(10) - 5;
            particleSystem.createParticle("Smoke", "Gray", x, y, velocityX, velocityY);
        }

        particleSystem.display();
        particleSystem.printMemoryUsage();
    }

    /**
     * Real-world example - Chess game with shared piece types
     */
    private static void demonstrateChess() {
        ChessBoard board = new ChessBoard();

        // Setup chess pieces
        board.placePiece("Pawn", "White", 0, 1);
        board.placePiece("Pawn", "White", 1, 1);
        board.placePiece("Pawn", "Black", 0, 6);
        board.placePiece("Pawn", "Black", 1, 6);
        board.placePiece("Rook", "White", 0, 0);
        board.placePiece("Knight", "White", 1, 0);
        board.placePiece("Rook", "Black", 0, 7);
        board.placePiece("Knight", "Black", 1, 7);

        board.display();
        board.printMemoryUsage();
    }
}

// ============================================
// Text Editor Example
// ============================================

/**
 * Flyweight interface - Character
 */
interface Character {
    void display(int x, int y);
}

/**
 * Concrete Flyweight - Represents a character with intrinsic state
 * Intrinsic state: character, font, size, color (shared)
 */
class CharacterStyle implements Character {
    private final char symbol;      // Intrinsic
    private final String font;      // Intrinsic
    private final int size;         // Intrinsic
    private final String color;     // Intrinsic

    public CharacterStyle(char symbol, String font, int size, String color) {
        this.symbol = symbol;
        this.font = font;
        this.size = size;
        this.color = color;
    }

    @Override
    public void display(int x, int y) {
        // x, y are extrinsic state (not stored in flyweight)
        // System.out.println("Character '" + symbol + "' at (" + x + "," + y + ") font=" + font);
    }

    public char getSymbol() {
        return symbol;
    }
}

/**
 * Flyweight Factory - Manages flyweight objects
 */
class CharacterFactory {
    private static final Map<String, Character> characters = new HashMap<>();

    public static Character getCharacter(char symbol, String font, int size, String color) {
        String key = symbol + font + size + color;

        if (!characters.containsKey(key)) {
            characters.put(key, new CharacterStyle(symbol, font, size, color));
            System.out.println("Creating new character: " + symbol);
        }

        return characters.get(key);
    }

    public static int getTotalCharacters() {
        return characters.size();
    }
}

/**
 * Client - Text Editor
 */
class TextEditor {
    private java.util.List<Character> characters = new java.util.ArrayList<>();
    private java.util.List<Integer> xPositions = new java.util.ArrayList<>();
    private java.util.List<Integer> yPositions = new java.util.ArrayList<>();

    public void addCharacter(char symbol, String font, int size, String color, int x, int y) {
        Character character = CharacterFactory.getCharacter(symbol, font, size, color);
        characters.add(character);
        xPositions.add(x);
        yPositions.add(y);
    }

    public void display() {
        System.out.println("Displaying " + characters.size() + " characters");
        for (int i = 0; i < characters.size(); i++) {
            characters.get(i).display(xPositions.get(i), yPositions.get(i));
        }
    }

    public void printMemoryUsage() {
        System.out.println("Total characters displayed: " + characters.size());
        System.out.println("Unique character objects: " + CharacterFactory.getTotalCharacters());
        System.out.println("Memory saved: " + (characters.size() - CharacterFactory.getTotalCharacters()) + " objects");
    }
}

// ============================================
// Forest Example
// ============================================

/**
 * Flyweight - Tree Type (intrinsic state)
 */
class TreeType {
    private final String name;      // Intrinsic
    private final String color;     // Intrinsic
    private final String texture;   // Intrinsic

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    public void draw(int x, int y) {
        // x, y are extrinsic state
        // System.out.println("Drawing " + name + " tree at (" + x + "," + y + ")");
    }

    public String getName() {
        return name;
    }
}

/**
 * Flyweight Factory - Tree Type Factory
 */
class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + color + texture;

        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(name, color, texture));
            System.out.println("Creating new tree type: " + name);
        }

        return treeTypes.get(key);
    }

    public static int getTotalTreeTypes() {
        return treeTypes.size();
    }
}

/**
 * Context object - Tree (contains extrinsic state)
 */
class Tree {
    private final int x;            // Extrinsic
    private final int y;            // Extrinsic
    private final TreeType type;    // Reference to flyweight

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        type.draw(x, y);
    }
}

/**
 * Client - Forest
 */
class Forest {
    private java.util.List<Tree> trees = new java.util.ArrayList<>();

    public void plantTree(int x, int y, String name, String color, String texture) {
        TreeType type = TreeFactory.getTreeType(name, color, texture);
        Tree tree = new Tree(x, y, type);
        trees.add(tree);
    }

    public void display() {
        System.out.println("Drawing " + trees.size() + " trees");
        for (Tree tree : trees) {
            tree.draw();
        }
    }

    public void printMemoryUsage() {
        System.out.println("Total trees: " + trees.size());
        System.out.println("Unique tree types: " + TreeFactory.getTotalTreeTypes());
        System.out.println("Memory efficiency: " + trees.size() / TreeFactory.getTotalTreeTypes() + "x");
    }
}

// ============================================
// Particle System Example
// ============================================

/**
 * Flyweight - Particle Type
 */
class ParticleType {
    private final String name;      // Intrinsic
    private final String color;     // Intrinsic

    public ParticleType(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void render(int x, int y, int velocityX, int velocityY) {
        // x, y, velocityX, velocityY are extrinsic
        // System.out.println("Rendering " + name + " particle at (" + x + "," + y + ")");
    }
}

/**
 * Flyweight Factory - Particle Factory
 */
class ParticleFactory {
    private static final Map<String, ParticleType> particleTypes = new HashMap<>();

    public static ParticleType getParticleType(String name, String color) {
        String key = name + color;

        if (!particleTypes.containsKey(key)) {
            particleTypes.put(key, new ParticleType(name, color));
            System.out.println("Creating new particle type: " + name);
        }

        return particleTypes.get(key);
    }

    public static int getTotalParticleTypes() {
        return particleTypes.size();
    }
}

/**
 * Context - Particle
 */
class Particle {
    private final int x, y;                 // Extrinsic
    private final int velocityX, velocityY; // Extrinsic
    private final ParticleType type;        // Reference to flyweight

    public Particle(int x, int y, int velocityX, int velocityY, ParticleType type) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.type = type;
    }

    public void render() {
        type.render(x, y, velocityX, velocityY);
    }
}

/**
 * Client - Particle System
 */
class ParticleSystem {
    private java.util.List<Particle> particles = new java.util.ArrayList<>();

    public void createParticle(String name, String color, int x, int y, int velocityX, int velocityY) {
        ParticleType type = ParticleFactory.getParticleType(name, color);
        Particle particle = new Particle(x, y, velocityX, velocityY, type);
        particles.add(particle);
    }

    public void display() {
        System.out.println("Rendering " + particles.size() + " particles");
        for (Particle particle : particles) {
            particle.render();
        }
    }

    public void printMemoryUsage() {
        System.out.println("Total particles: " + particles.size());
        System.out.println("Unique particle types: " + ParticleFactory.getTotalParticleTypes());
        System.out.println("Memory efficiency: " + particles.size() / ParticleFactory.getTotalParticleTypes() + "x");
    }
}

// ============================================
// Chess Example
// ============================================

/**
 * Flyweight - Chess Piece Type
 */
class ChessPieceType {
    private final String name;      // Intrinsic
    private final String color;     // Intrinsic

    public ChessPieceType(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void display(int x, int y) {
        // x, y are extrinsic
        // System.out.println(color + " " + name + " at (" + x + "," + y + ")");
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}

/**
 * Flyweight Factory - Chess Piece Factory
 */
class ChessPieceFactory {
    private static final Map<String, ChessPieceType> pieceTypes = new HashMap<>();

    public static ChessPieceType getPieceType(String name, String color) {
        String key = name + color;

        if (!pieceTypes.containsKey(key)) {
            pieceTypes.put(key, new ChessPieceType(name, color));
            System.out.println("Creating new piece type: " + color + " " + name);
        }

        return pieceTypes.get(key);
    }

    public static int getTotalPieceTypes() {
        return pieceTypes.size();
    }
}

/**
 * Context - Chess Piece
 */
class ChessPiece {
    private final int x, y;                 // Extrinsic
    private final ChessPieceType type;      // Reference to flyweight

    public ChessPiece(int x, int y, ChessPieceType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void display() {
        type.display(x, y);
    }
}

/**
 * Client - Chess Board
 */
class ChessBoard {
    private java.util.List<ChessPiece> pieces = new java.util.ArrayList<>();

    public void placePiece(String name, String color, int x, int y) {
        ChessPieceType type = ChessPieceFactory.getPieceType(name, color);
        ChessPiece piece = new ChessPiece(x, y, type);
        pieces.add(piece);
    }

    public void display() {
        System.out.println("Chess board with " + pieces.size() + " pieces:");
        for (ChessPiece piece : pieces) {
            piece.display();
        }
    }

    public void printMemoryUsage() {
        System.out.println("Total pieces: " + pieces.size());
        System.out.println("Unique piece types: " + ChessPieceFactory.getTotalPieceTypes());
        System.out.println("Memory saved by sharing types");
    }
}
