package structural.decorator;

/**
 * Demo class showing Decorator pattern in action
 *
 * The Decorator pattern attaches additional responsibilities to an object dynamically.
 * Decorators provide a flexible alternative to subclassing for extending functionality.
 */
public class DecoratorDemo {
    public static void main(String[] args) {
        System.out.println("=== Decorator Pattern Demo ===\n");

        System.out.println("--- Coffee Shop Example ---");
        demonstrateCoffeeShop();

        System.out.println("\n--- Text Formatting Example ---");
        demonstrateTextFormatting();

        System.out.println("\n--- Stream Processing Example ---");
        demonstrateStreamProcessing();

        System.out.println("\n--- Pizza Example ---");
        demonstratePizza();
    }

    /**
     * Classic example - Coffee with various add-ons
     */
    private static void demonstrateCoffeeShop() {
        // Simple coffee
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());

        // Coffee with milk
        coffee = new MilkDecorator(new SimpleCoffee());
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());

        // Coffee with milk and sugar
        coffee = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());

        // Deluxe coffee with everything
        coffee = new CaramelDecorator(
                    new WhipDecorator(
                        new SugarDecorator(
                            new MilkDecorator(
                                new SimpleCoffee()))));
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());

        // Espresso with double shot
        coffee = new MilkDecorator(new MilkDecorator(new Espresso()));
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());
    }

    /**
     * Real-world example - Text formatting
     */
    private static void demonstrateTextFormatting() {
        // Plain text
        Text text = new PlainText("Hello World");
        System.out.println(text.render());

        // Bold text
        text = new BoldDecorator(new PlainText("Hello World"));
        System.out.println(text.render());

        // Italic and underlined text
        text = new UnderlineDecorator(new ItalicDecorator(new PlainText("Hello World")));
        System.out.println(text.render());

        // All formatting
        text = new ColorDecorator(
                new UnderlineDecorator(
                    new ItalicDecorator(
                        new BoldDecorator(
                            new PlainText("Hello World")))), "red");
        System.out.println(text.render());
    }

    /**
     * Real-world example - Stream processing with compression and encryption
     */
    private static void demonstrateStreamProcessing() {
        String data = "Sensitive data";

        // Plain data
        DataSource source = new FileDataSource("data.txt");
        source.writeData(data);
        System.out.println("Plain: " + source.readData());

        // Encrypted data
        source = new EncryptionDecorator(new FileDataSource("encrypted.txt"));
        source.writeData(data);
        System.out.println("Encrypted: " + source.readData());

        // Compressed and encrypted data
        source = new CompressionDecorator(
                    new EncryptionDecorator(
                        new FileDataSource("compressed_encrypted.txt")));
        source.writeData(data);
        System.out.println("Compressed & Encrypted: " + source.readData());
    }

    /**
     * Another example - Pizza with toppings
     */
    private static void demonstratePizza() {
        // Plain pizza
        Pizza pizza = new PlainPizza();
        System.out.println(pizza.getDescription() + " - $" + pizza.getCost());

        // Pizza with cheese
        pizza = new CheeseTopping(new PlainPizza());
        System.out.println(pizza.getDescription() + " - $" + pizza.getCost());

        // Supreme pizza
        pizza = new PepperoniTopping(
                    new MushroomTopping(
                        new CheeseTopping(
                            new PlainPizza())));
        System.out.println(pizza.getDescription() + " - $" + pizza.getCost());
    }
}

// ============================================
// Coffee Shop Example
// ============================================

/**
 * Component interface - defines the interface for objects that can have
 * responsibilities added to them dynamically
 */
interface Coffee {
    String getDescription();
    double getCost();
}

/**
 * Concrete component - Simple coffee
 */
class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }

    @Override
    public double getCost() {
        return 2.0;
    }
}

/**
 * Concrete component - Espresso
 */
class Espresso implements Coffee {
    @Override
    public String getDescription() {
        return "Espresso";
    }

    @Override
    public double getCost() {
        return 3.0;
    }
}

/**
 * Base decorator class - implements the component interface and has a reference
 * to a component object
 */
abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
}

/**
 * Concrete decorator - Milk
 */
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.5;
    }
}

/**
 * Concrete decorator - Sugar
 */
class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.2;
    }
}

/**
 * Concrete decorator - Whipped cream
 */
class WhipDecorator extends CoffeeDecorator {
    public WhipDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Whipped Cream";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.7;
    }
}

/**
 * Concrete decorator - Caramel
 */
class CaramelDecorator extends CoffeeDecorator {
    public CaramelDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Caramel";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.6;
    }
}

// ============================================
// Text Formatting Example
// ============================================

/**
 * Component interface for text
 */
interface Text {
    String render();
}

/**
 * Concrete component - Plain text
 */
class PlainText implements Text {
    private String content;

    public PlainText(String content) {
        this.content = content;
    }

    @Override
    public String render() {
        return content;
    }
}

/**
 * Base decorator for text
 */
abstract class TextDecorator implements Text {
    protected Text decoratedText;

    public TextDecorator(Text text) {
        this.decoratedText = text;
    }

    @Override
    public String render() {
        return decoratedText.render();
    }
}

/**
 * Concrete decorator - Bold
 */
class BoldDecorator extends TextDecorator {
    public BoldDecorator(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<b>" + decoratedText.render() + "</b>";
    }
}

/**
 * Concrete decorator - Italic
 */
class ItalicDecorator extends TextDecorator {
    public ItalicDecorator(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<i>" + decoratedText.render() + "</i>";
    }
}

/**
 * Concrete decorator - Underline
 */
class UnderlineDecorator extends TextDecorator {
    public UnderlineDecorator(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<u>" + decoratedText.render() + "</u>";
    }
}

/**
 * Concrete decorator - Color
 */
class ColorDecorator extends TextDecorator {
    private String color;

    public ColorDecorator(Text text, String color) {
        super(text);
        this.color = color;
    }

    @Override
    public String render() {
        return "<color=" + color + ">" + decoratedText.render() + "</color>";
    }
}

// ============================================
// Stream Processing Example
// ============================================

/**
 * Component interface for data source
 */
interface DataSource {
    void writeData(String data);
    String readData();
}

/**
 * Concrete component - File data source
 */
class FileDataSource implements DataSource {
    private String filename;
    private String data;

    public FileDataSource(String filename) {
        this.filename = filename;
    }

    @Override
    public void writeData(String data) {
        this.data = data;
        System.out.println("Writing to " + filename + ": " + data);
    }

    @Override
    public String readData() {
        System.out.println("Reading from " + filename);
        return data;
    }
}

/**
 * Base decorator for data source
 */
abstract class DataSourceDecorator implements DataSource {
    protected DataSource wrappee;

    public DataSourceDecorator(DataSource source) {
        this.wrappee = source;
    }

    @Override
    public void writeData(String data) {
        wrappee.writeData(data);
    }

    @Override
    public String readData() {
        return wrappee.readData();
    }
}

/**
 * Concrete decorator - Encryption
 */
class EncryptionDecorator extends DataSourceDecorator {
    public EncryptionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        String encrypted = encrypt(data);
        super.writeData(encrypted);
    }

    @Override
    public String readData() {
        String data = super.readData();
        return decrypt(data);
    }

    private String encrypt(String data) {
        // Simple "encryption" for demo
        return "[ENCRYPTED:" + data + "]";
    }

    private String decrypt(String data) {
        // Simple "decryption" for demo
        return data.replace("[ENCRYPTED:", "").replace("]", "");
    }
}

/**
 * Concrete decorator - Compression
 */
class CompressionDecorator extends DataSourceDecorator {
    public CompressionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        String compressed = compress(data);
        super.writeData(compressed);
    }

    @Override
    public String readData() {
        String data = super.readData();
        return decompress(data);
    }

    private String compress(String data) {
        // Simple "compression" for demo
        return "[COMPRESSED:" + data + "]";
    }

    private String decompress(String data) {
        // Simple "decompression" for demo
        return data.replace("[COMPRESSED:", "").replace("]", "");
    }
}

// ============================================
// Pizza Example
// ============================================

/**
 * Component interface for pizza
 */
interface Pizza {
    String getDescription();
    double getCost();
}

/**
 * Concrete component - Plain pizza
 */
class PlainPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Plain Pizza";
    }

    @Override
    public double getCost() {
        return 8.0;
    }
}

/**
 * Base decorator for pizza
 */
abstract class ToppingDecorator implements Pizza {
    protected Pizza pizza;

    public ToppingDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription();
    }

    @Override
    public double getCost() {
        return pizza.getCost();
    }
}

/**
 * Concrete decorator - Cheese topping
 */
class CheeseTopping extends ToppingDecorator {
    public CheeseTopping(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Cheese";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 1.5;
    }
}

/**
 * Concrete decorator - Pepperoni topping
 */
class PepperoniTopping extends ToppingDecorator {
    public PepperoniTopping(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Pepperoni";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 2.0;
    }
}

/**
 * Concrete decorator - Mushroom topping
 */
class MushroomTopping extends ToppingDecorator {
    public MushroomTopping(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Mushroom";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 1.0;
    }
}
