package creational.factory_method;

/**
 * Demo class showing Factory Method pattern in action
 */
public class FactoryMethodDemo {
    public static void main(String[] args) {
        System.out.println("=== Factory Method Pattern Demo ===\n");

        // Client code works with creators through the base Creator interface
        Creator creatorA = new ConcreteCreatorA();
        System.out.println("Using Creator A:");
        creatorA.someOperation();

        System.out.println();

        Creator creatorB = new ConcreteCreatorB();
        System.out.println("Using Creator B:");
        creatorB.someOperation();

        System.out.println("\n=== Real-world example: Logistics ===\n");
        demonstrateLogistics();
    }

    private static void demonstrateLogistics() {
        // Road logistics
        Logistics roadLogistics = new RoadLogistics();
        roadLogistics.planDelivery();

        System.out.println();

        // Sea logistics
        Logistics seaLogistics = new SeaLogistics();
        seaLogistics.planDelivery();
    }
}

// Real-world example: Logistics system

interface Transport {
    void deliver();
}

class Truck implements Transport {
    @Override
    public void deliver() {
        System.out.println("Deliver by land in a truck");
    }
}

class Ship implements Transport {
    @Override
    public void deliver() {
        System.out.println("Deliver by sea in a ship");
    }
}

abstract class Logistics {
    public abstract Transport createTransport();

    public void planDelivery() {
        Transport transport = createTransport();
        System.out.println("Planning delivery...");
        transport.deliver();
    }
}

class RoadLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        return new Truck();
    }
}

class SeaLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        return new Ship();
    }
}
