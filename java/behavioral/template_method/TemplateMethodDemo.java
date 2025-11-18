package behavioral.template_method;

/**
 * Demo class showing Template Method pattern in action
 */
public class TemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println("=== Template Method Pattern Demo ===\n");

        System.out.println("--- Making Beverages ---");
        Beverage tea = new Tea();
        Beverage coffee = new Coffee();

        System.out.println("Preparing Tea:");
        tea.prepareRecipe();

        System.out.println("\nPreparing Coffee:");
        coffee.prepareRecipe();

        System.out.println("\n--- Data Processing ---");
        DataProcessor csvProcessor = new CSVProcessor();
        DataProcessor xmlProcessor = new XMLProcessor();

        System.out.println("Processing CSV:");
        csvProcessor.process("data.csv");

        System.out.println("\nProcessing XML:");
        xmlProcessor.process("data.xml");

        System.out.println("\n--- Game Framework ---");
        Game chess = new Chess();
        Game soccer = new Soccer();

        System.out.println("Playing Chess:");
        chess.play();

        System.out.println("\nPlaying Soccer:");
        soccer.play();
    }
}

// Abstract class with template method
abstract class Beverage {
    // Template method - defines the algorithm skeleton
    public final void prepareRecipe() {
        boilWater();
        brew();
        pourInCup();
        if (customerWantsCondiments()) {
            addCondiments();
        }
    }

    // Common steps
    private void boilWater() {
        System.out.println("Boiling water");
    }

    private void pourInCup() {
        System.out.println("Pouring into cup");
    }

    // Abstract steps - must be implemented by subclasses
    protected abstract void brew();
    protected abstract void addCondiments();

    // Hook method - can be overridden
    protected boolean customerWantsCondiments() {
        return true;
    }
}

// Concrete classes
class Tea extends Beverage {
    @Override
    protected void brew() {
        System.out.println("Steeping the tea");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding lemon");
    }
}

class Coffee extends Beverage {
    @Override
    protected void brew() {
        System.out.println("Dripping coffee through filter");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding sugar and milk");
    }
}

// Data Processing Example
abstract class DataProcessor {
    // Template method
    public final void process(String filename) {
        openFile(filename);
        readData();
        parseData();
        closeFile();
    }

    private void openFile(String filename) {
        System.out.println("Opening file: " + filename);
    }

    protected abstract void readData();
    protected abstract void parseData();

    private void closeFile() {
        System.out.println("Closing file");
    }
}

class CSVProcessor extends DataProcessor {
    @Override
    protected void readData() {
        System.out.println("Reading CSV data");
    }

    @Override
    protected void parseData() {
        System.out.println("Parsing comma-separated values");
    }
}

class XMLProcessor extends DataProcessor {
    @Override
    protected void readData() {
        System.out.println("Reading XML data");
    }

    @Override
    protected void parseData() {
        System.out.println("Parsing XML tags");
    }
}

// Game Framework Example
abstract class Game {
    // Template method
    public final void play() {
        initialize();
        startPlay();
        endPlay();
    }

    protected abstract void initialize();
    protected abstract void startPlay();
    protected abstract void endPlay();
}

class Chess extends Game {
    @Override
    protected void initialize() {
        System.out.println("Chess Game Initialized. Start playing.");
    }

    @Override
    protected void startPlay() {
        System.out.println("Chess Game Started. Enjoy the game!");
    }

    @Override
    protected void endPlay() {
        System.out.println("Chess Game Finished!");
    }
}

class Soccer extends Game {
    @Override
    protected void initialize() {
        System.out.println("Soccer Game Initialized. Start playing.");
    }

    @Override
    protected void startPlay() {
        System.out.println("Soccer Game Started. Enjoy the game!");
    }

    @Override
    protected void endPlay() {
        System.out.println("Soccer Game Finished!");
    }
}
