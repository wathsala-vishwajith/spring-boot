package structural.facade;

/**
 * Demo class showing Facade pattern in action
 *
 * The Facade pattern provides a unified interface to a set of interfaces in a subsystem.
 * It defines a higher-level interface that makes the subsystem easier to use.
 */
public class FacadeDemo {
    public static void main(String[] args) {
        System.out.println("=== Facade Pattern Demo ===\n");

        System.out.println("--- Home Theater Example ---");
        demonstrateHomeTheater();

        System.out.println("\n--- Computer System Example ---");
        demonstrateComputer();

        System.out.println("\n--- Online Shopping Example ---");
        demonstrateOnlineShopping();

        System.out.println("\n--- Restaurant Example ---");
        demonstrateRestaurant();
    }

    /**
     * Classic example - Home theater system with multiple components
     */
    private static void demonstrateHomeTheater() {
        // Without facade - complex
        System.out.println("Without Facade (Complex):");
        Amplifier amp = new Amplifier();
        DVDPlayer dvd = new DVDPlayer();
        Projector projector = new Projector();
        Screen screen = new Screen();
        Lights lights = new Lights();

        // Client has to manage all components
        lights.dim(10);
        screen.down();
        projector.on();
        amp.on();
        amp.setVolume(5);
        dvd.on();
        dvd.play("Inception");

        System.out.println();

        // With facade - simple
        System.out.println("With Facade (Simple):");
        HomeTheaterFacade homeTheater = new HomeTheaterFacade();
        homeTheater.watchMovie("Inception");

        System.out.println();
        homeTheater.endMovie();
    }

    /**
     * Real-world example - Computer system startup
     */
    private static void demonstrateComputer() {
        ComputerFacade computer = new ComputerFacade();

        System.out.println("Starting computer:");
        computer.start();

        System.out.println("\nShutting down computer:");
        computer.shutdown();
    }

    /**
     * Real-world example - Online shopping checkout process
     */
    private static void demonstrateOnlineShopping() {
        ShoppingFacade shopping = new ShoppingFacade();

        System.out.println("Completing order:");
        shopping.completeOrder("user123", "product456", 99.99);
    }

    /**
     * Real-world example - Restaurant order system
     */
    private static void demonstrateRestaurant() {
        RestaurantFacade restaurant = new RestaurantFacade();

        System.out.println("Ordering meal:");
        restaurant.orderMeal("Burger", "Coke");

        System.out.println("\nOrdering takeaway:");
        restaurant.orderTakeaway("Pizza", "123 Main St");
    }
}

// ============================================
// Home Theater Example - Complex Subsystem
// ============================================

/**
 * Subsystem class - Amplifier
 */
class Amplifier {
    public void on() {
        System.out.println("Amplifier: Turning on");
    }

    public void off() {
        System.out.println("Amplifier: Turning off");
    }

    public void setVolume(int level) {
        System.out.println("Amplifier: Setting volume to " + level);
    }
}

/**
 * Subsystem class - DVD Player
 */
class DVDPlayer {
    public void on() {
        System.out.println("DVD Player: Turning on");
    }

    public void off() {
        System.out.println("DVD Player: Turning off");
    }

    public void play(String movie) {
        System.out.println("DVD Player: Playing '" + movie + "'");
    }

    public void stop() {
        System.out.println("DVD Player: Stopping");
    }
}

/**
 * Subsystem class - Projector
 */
class Projector {
    public void on() {
        System.out.println("Projector: Turning on");
    }

    public void off() {
        System.out.println("Projector: Turning off");
    }

    public void wideScreenMode() {
        System.out.println("Projector: Setting to wide screen mode");
    }
}

/**
 * Subsystem class - Screen
 */
class Screen {
    public void down() {
        System.out.println("Screen: Going down");
    }

    public void up() {
        System.out.println("Screen: Going up");
    }
}

/**
 * Subsystem class - Lights
 */
class Lights {
    public void dim(int level) {
        System.out.println("Lights: Dimming to " + level + "%");
    }

    public void on() {
        System.out.println("Lights: Turning on");
    }
}

/**
 * Facade - Simplifies home theater operations
 */
class HomeTheaterFacade {
    private Amplifier amp;
    private DVDPlayer dvd;
    private Projector projector;
    private Screen screen;
    private Lights lights;

    public HomeTheaterFacade() {
        this.amp = new Amplifier();
        this.dvd = new DVDPlayer();
        this.projector = new Projector();
        this.screen = new Screen();
        this.lights = new Lights();
    }

    /**
     * Simplified method to watch a movie
     */
    public void watchMovie(String movie) {
        System.out.println("Get ready to watch a movie...");
        lights.dim(10);
        screen.down();
        projector.on();
        projector.wideScreenMode();
        amp.on();
        amp.setVolume(5);
        dvd.on();
        dvd.play(movie);
    }

    /**
     * Simplified method to end movie
     */
    public void endMovie() {
        System.out.println("Shutting down movie theater...");
        dvd.stop();
        dvd.off();
        amp.off();
        projector.off();
        screen.up();
        lights.on();
    }
}

// ============================================
// Computer System Example
// ============================================

/**
 * Subsystem - CPU
 */
class CPU {
    public void freeze() {
        System.out.println("CPU: Freezing");
    }

    public void jump(long position) {
        System.out.println("CPU: Jumping to position " + position);
    }

    public void execute() {
        System.out.println("CPU: Executing");
    }
}

/**
 * Subsystem - Memory
 */
class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Memory: Loading data at position " + position);
    }
}

/**
 * Subsystem - Hard Drive
 */
class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("HardDrive: Reading " + size + " bytes from LBA " + lba);
        return new byte[size];
    }
}

/**
 * Facade - Computer system
 */
class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;

    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }

    /**
     * Simplified startup process
     */
    public void start() {
        System.out.println("Starting computer...");
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
        System.out.println("Computer started successfully!");
    }

    /**
     * Simplified shutdown process
     */
    public void shutdown() {
        System.out.println("Shutting down computer...");
        System.out.println("Saving state...");
        System.out.println("Powering off...");
        System.out.println("Computer shut down successfully!");
    }
}

// ============================================
// Online Shopping Example
// ============================================

/**
 * Subsystem - Inventory
 */
class Inventory {
    public boolean checkAvailability(String productId) {
        System.out.println("Inventory: Checking availability of " + productId);
        return true;
    }

    public void updateStock(String productId) {
        System.out.println("Inventory: Updating stock for " + productId);
    }
}

/**
 * Subsystem - Payment
 */
class Payment {
    public boolean processPayment(String userId, double amount) {
        System.out.println("Payment: Processing $" + amount + " for user " + userId);
        return true;
    }
}

/**
 * Subsystem - Shipping
 */
class Shipping {
    public void arrangeShipping(String userId, String productId) {
        System.out.println("Shipping: Arranging shipment of " + productId + " to " + userId);
    }
}

/**
 * Subsystem - Notification
 */
class Notification {
    public void sendConfirmation(String userId) {
        System.out.println("Notification: Sending confirmation email to " + userId);
    }
}

/**
 * Facade - Shopping system
 */
class ShoppingFacade {
    private Inventory inventory;
    private Payment payment;
    private Shipping shipping;
    private Notification notification;

    public ShoppingFacade() {
        this.inventory = new Inventory();
        this.payment = new Payment();
        this.shipping = new Shipping();
        this.notification = new Notification();
    }

    /**
     * Simplified order completion process
     */
    public void completeOrder(String userId, String productId, double amount) {
        System.out.println("Processing order...");

        if (!inventory.checkAvailability(productId)) {
            System.out.println("Product not available!");
            return;
        }

        if (!payment.processPayment(userId, amount)) {
            System.out.println("Payment failed!");
            return;
        }

        inventory.updateStock(productId);
        shipping.arrangeShipping(userId, productId);
        notification.sendConfirmation(userId);

        System.out.println("Order completed successfully!");
    }
}

// ============================================
// Restaurant Example
// ============================================

/**
 * Subsystem - Kitchen
 */
class Kitchen {
    public void prepareFood(String dish) {
        System.out.println("Kitchen: Preparing " + dish);
    }
}

/**
 * Subsystem - Waiter
 */
class Waiter {
    public void takeOrder(String dish) {
        System.out.println("Waiter: Taking order for " + dish);
    }

    public void serveFood() {
        System.out.println("Waiter: Serving food");
    }
}

/**
 * Subsystem - Cashier
 */
class Cashier {
    public void processPayment() {
        System.out.println("Cashier: Processing payment");
    }

    public void printReceipt() {
        System.out.println("Cashier: Printing receipt");
    }
}

/**
 * Subsystem - Delivery
 */
class Delivery {
    public void packageFood(String dish) {
        System.out.println("Delivery: Packaging " + dish);
    }

    public void deliverTo(String address) {
        System.out.println("Delivery: Delivering to " + address);
    }
}

/**
 * Facade - Restaurant operations
 */
class RestaurantFacade {
    private Kitchen kitchen;
    private Waiter waiter;
    private Cashier cashier;
    private Delivery delivery;

    public RestaurantFacade() {
        this.kitchen = new Kitchen();
        this.waiter = new Waiter();
        this.cashier = new Cashier();
        this.delivery = new Delivery();
    }

    /**
     * Simplified dine-in order
     */
    public void orderMeal(String dish, String drink) {
        System.out.println("Processing dine-in order...");
        waiter.takeOrder(dish + " and " + drink);
        kitchen.prepareFood(dish);
        waiter.serveFood();
        cashier.processPayment();
        cashier.printReceipt();
        System.out.println("Enjoy your meal!");
    }

    /**
     * Simplified takeaway order
     */
    public void orderTakeaway(String dish, String address) {
        System.out.println("Processing takeaway order...");
        kitchen.prepareFood(dish);
        delivery.packageFood(dish);
        cashier.processPayment();
        delivery.deliverTo(address);
        System.out.println("Order on the way!");
    }
}
