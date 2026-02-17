package creational.abstract_factory;

/**
 * Demo class showing Abstract Factory pattern in action
 */
public class AbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("=== Abstract Factory Pattern Demo ===\n");

        // Determine OS (simulated with command-line argument or system property)
        String osName = System.getProperty("os.name").toLowerCase();
        GUIFactory factory;

        System.out.println("Detected OS: " + osName + "\n");

        // Select factory based on OS
        if (osName.contains("win")) {
            factory = new WindowsFactory();
            System.out.println("Using Windows Factory\n");
        } else {
            factory = new MacFactory();
            System.out.println("Using Mac Factory\n");
        }

        // Client code works with factory through abstract interface
        Application app = new Application(factory);

        System.out.println("--- Rendering UI Components ---");
        app.render();

        System.out.println("\n--- Interacting with UI Components ---");
        app.interact();

        System.out.println("\n=== Furniture Factory Example ===\n");
        demonstrateFurnitureFactory();
    }

    private static void demonstrateFurnitureFactory() {
        // Modern furniture
        FurnitureFactory modernFactory = new ModernFurnitureFactory();
        Client client1 = new Client(modernFactory);
        System.out.println("Modern Furniture:");
        client1.describeFurniture();

        System.out.println();

        // Victorian furniture
        FurnitureFactory victorianFactory = new VictorianFurnitureFactory();
        Client client2 = new Client(victorianFactory);
        System.out.println("Victorian Furniture:");
        client2.describeFurniture();
    }
}

// Additional real-world example: Furniture Store

interface Chair {
    void sitOn();
}

interface Sofa {
    void lieOn();
}

class ModernChair implements Chair {
    @Override
    public void sitOn() {
        System.out.println("Sitting on a modern minimalist chair");
    }
}

class VictorianChair implements Chair {
    @Override
    public void sitOn() {
        System.out.println("Sitting on a Victorian chair with ornate details");
    }
}

class ModernSofa implements Sofa {
    @Override
    public void lieOn() {
        System.out.println("Lying on a sleek modern sofa");
    }
}

class VictorianSofa implements Sofa {
    @Override
    public void lieOn() {
        System.out.println("Lying on a classic Victorian sofa with velvet upholstery");
    }
}

interface FurnitureFactory {
    Chair createChair();
    Sofa createSofa();
}

class ModernFurnitureFactory implements FurnitureFactory {
    @Override
    public Chair createChair() {
        return new ModernChair();
    }

    @Override
    public Sofa createSofa() {
        return new ModernSofa();
    }
}

class VictorianFurnitureFactory implements FurnitureFactory {
    @Override
    public Chair createChair() {
        return new VictorianChair();
    }

    @Override
    public Sofa createSofa() {
        return new VictorianSofa();
    }
}

class Client {
    private Chair chair;
    private Sofa sofa;

    public Client(FurnitureFactory factory) {
        this.chair = factory.createChair();
        this.sofa = factory.createSofa();
    }

    public void describeFurniture() {
        chair.sitOn();
        sofa.lieOn();
    }
}
