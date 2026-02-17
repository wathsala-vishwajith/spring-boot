package structural.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo class showing Proxy pattern in action
 *
 * The Proxy pattern provides a substitute or placeholder for another object.
 * A proxy controls access to the original object, allowing you to perform
 * something either before or after the request gets through to the original object.
 */
public class ProxyDemo {
    public static void main(String[] args) {
        System.out.println("=== Proxy Pattern Demo ===\n");

        System.out.println("--- Virtual Proxy Example (Lazy Loading) ---");
        demonstrateVirtualProxy();

        System.out.println("\n--- Protection Proxy Example (Access Control) ---");
        demonstrateProtectionProxy();

        System.out.println("\n--- Caching Proxy Example ---");
        demonstrateCachingProxy();

        System.out.println("\n--- Remote Proxy Example ---");
        demonstrateRemoteProxy();

        System.out.println("\n--- Logging Proxy Example ---");
        demonstrateLoggingProxy();
    }

    /**
     * Virtual Proxy - Delays object creation until it's actually needed
     */
    private static void demonstrateVirtualProxy() {
        System.out.println("Creating proxies for large images...");
        Image image1 = new ProxyImage("photo1.jpg");
        Image image2 = new ProxyImage("photo2.jpg");

        System.out.println("\nDisplaying image1 (first time - loads from disk):");
        image1.display();

        System.out.println("\nDisplaying image1 again (cached - no loading):");
        image1.display();

        System.out.println("\nDisplaying image2 (first time - loads from disk):");
        image2.display();
    }

    /**
     * Protection Proxy - Controls access based on permissions
     */
    private static void demonstrateProtectionProxy() {
        User admin = new User("Admin", "ADMIN");
        User guest = new User("Guest", "GUEST");

        Document document = new ProtectedDocument("secret.txt");

        System.out.println("Admin trying to read:");
        document.read(admin);

        System.out.println("\nAdmin trying to write:");
        document.write(admin, "Confidential content");

        System.out.println("\nGuest trying to read:");
        document.read(guest);

        System.out.println("\nGuest trying to write:");
        document.write(guest, "Some content");
    }

    /**
     * Caching Proxy - Caches results to avoid expensive operations
     */
    private static void demonstrateCachingProxy() {
        DatabaseQuery database = new CachingDatabaseProxy();

        System.out.println("First query (hits database):");
        String result1 = database.query("SELECT * FROM users WHERE id=1");
        System.out.println("Result: " + result1);

        System.out.println("\nSame query again (returns from cache):");
        String result2 = database.query("SELECT * FROM users WHERE id=1");
        System.out.println("Result: " + result2);

        System.out.println("\nDifferent query (hits database):");
        String result3 = database.query("SELECT * FROM users WHERE id=2");
        System.out.println("Result: " + result3);
    }

    /**
     * Remote Proxy - Represents an object in different address space
     */
    private static void demonstrateRemoteProxy() {
        PaymentService service = new RemotePaymentProxy();

        System.out.println("Processing payment through remote service:");
        service.processPayment(100.0, "1234-5678-9012-3456");

        System.out.println("\nRefunding payment:");
        service.refund("TX123456");
    }

    /**
     * Logging Proxy - Adds logging before/after method calls
     */
    private static void demonstrateLoggingProxy() {
        Calculator calculator = new LoggingCalculatorProxy();

        System.out.println("Performing calculations with logging:");
        int sum = calculator.add(10, 5);
        System.out.println("Result: " + sum);

        int product = calculator.multiply(4, 3);
        System.out.println("Result: " + product);
    }
}

// ============================================
// Virtual Proxy Example - Lazy Loading Images
// ============================================

/**
 * Subject interface
 */
interface Image {
    void display();
}

/**
 * Real Subject - Expensive object to create
 */
class RealImage implements Image {
    private String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("Loading image from disk: " + filename);
        // Simulate expensive operation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display() {
        System.out.println("Displaying image: " + filename);
    }
}

/**
 * Proxy - Controls access to RealImage with lazy loading
 */
class ProxyImage implements Image {
    private String filename;
    private RealImage realImage;

    public ProxyImage(String filename) {
        this.filename = filename;
    }

    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);
        }
        realImage.display();
    }
}

// ============================================
// Protection Proxy Example - Access Control
// ============================================

/**
 * User class with role
 */
class User {
    private String name;
    private String role;

    public User(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}

/**
 * Subject interface
 */
interface Document {
    void read(User user);
    void write(User user, String content);
}

/**
 * Real Subject - Actual document
 */
class RealDocument implements Document {
    private String filename;
    private String content;

    public RealDocument(String filename) {
        this.filename = filename;
        this.content = "Original content";
    }

    @Override
    public void read(User user) {
        System.out.println(user.getName() + " reading document: " + filename);
        System.out.println("Content: " + content);
    }

    @Override
    public void write(User user, String content) {
        System.out.println(user.getName() + " writing to document: " + filename);
        this.content = content;
    }
}

/**
 * Protection Proxy - Controls access based on user role
 */
class ProtectedDocument implements Document {
    private RealDocument realDocument;
    private String filename;

    public ProtectedDocument(String filename) {
        this.filename = filename;
        this.realDocument = new RealDocument(filename);
    }

    @Override
    public void read(User user) {
        if (hasReadPermission(user)) {
            realDocument.read(user);
        } else {
            System.out.println("Access denied: " + user.getName() + " cannot read " + filename);
        }
    }

    @Override
    public void write(User user, String content) {
        if (hasWritePermission(user)) {
            realDocument.write(user, content);
        } else {
            System.out.println("Access denied: " + user.getName() + " cannot write to " + filename);
        }
    }

    private boolean hasReadPermission(User user) {
        return user.getRole().equals("ADMIN") || user.getRole().equals("USER");
    }

    private boolean hasWritePermission(User user) {
        return user.getRole().equals("ADMIN");
    }
}

// ============================================
// Caching Proxy Example
// ============================================

/**
 * Subject interface
 */
interface DatabaseQuery {
    String query(String sql);
}

/**
 * Real Subject - Expensive database operations
 */
class RealDatabase implements DatabaseQuery {
    @Override
    public String query(String sql) {
        System.out.println("Executing query on database: " + sql);
        // Simulate expensive database query
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Result for: " + sql;
    }
}

/**
 * Caching Proxy - Caches database query results
 */
class CachingDatabaseProxy implements DatabaseQuery {
    private RealDatabase realDatabase;
    private Map<String, String> cache;

    public CachingDatabaseProxy() {
        this.realDatabase = new RealDatabase();
        this.cache = new HashMap<>();
    }

    @Override
    public String query(String sql) {
        if (cache.containsKey(sql)) {
            System.out.println("Returning cached result for: " + sql);
            return cache.get(sql);
        }

        String result = realDatabase.query(sql);
        cache.put(sql, result);
        return result;
    }
}

// ============================================
// Remote Proxy Example
// ============================================

/**
 * Subject interface
 */
interface PaymentService {
    void processPayment(double amount, String cardNumber);
    void refund(String transactionId);
}

/**
 * Real Subject - Would be on remote server
 */
class RealPaymentService implements PaymentService {
    @Override
    public void processPayment(double amount, String cardNumber) {
        System.out.println("Processing payment of $" + amount + " with card " + cardNumber);
    }

    @Override
    public void refund(String transactionId) {
        System.out.println("Refunding transaction: " + transactionId);
    }
}

/**
 * Remote Proxy - Handles communication with remote service
 */
class RemotePaymentProxy implements PaymentService {
    private RealPaymentService realService;

    public RemotePaymentProxy() {
        // In real scenario, this would connect to remote service
        this.realService = new RealPaymentService();
    }

    @Override
    public void processPayment(double amount, String cardNumber) {
        System.out.println("Remote Proxy: Establishing connection...");
        System.out.println("Remote Proxy: Encrypting payment data...");
        realService.processPayment(amount, cardNumber);
        System.out.println("Remote Proxy: Closing connection...");
    }

    @Override
    public void refund(String transactionId) {
        System.out.println("Remote Proxy: Establishing connection...");
        realService.refund(transactionId);
        System.out.println("Remote Proxy: Closing connection...");
    }
}

// ============================================
// Logging Proxy Example
// ============================================

/**
 * Subject interface
 */
interface Calculator {
    int add(int a, int b);
    int multiply(int a, int b);
}

/**
 * Real Subject - Actual calculator
 */
class RealCalculator implements Calculator {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int multiply(int a, int b) {
        return a * b;
    }
}

/**
 * Logging Proxy - Adds logging around operations
 */
class LoggingCalculatorProxy implements Calculator {
    private RealCalculator realCalculator;

    public LoggingCalculatorProxy() {
        this.realCalculator = new RealCalculator();
    }

    @Override
    public int add(int a, int b) {
        System.out.println("LOG: Calling add(" + a + ", " + b + ")");
        long startTime = System.currentTimeMillis();

        int result = realCalculator.add(a, b);

        long endTime = System.currentTimeMillis();
        System.out.println("LOG: add() completed in " + (endTime - startTime) + "ms");

        return result;
    }

    @Override
    public int multiply(int a, int b) {
        System.out.println("LOG: Calling multiply(" + a + ", " + b + ")");
        long startTime = System.currentTimeMillis();

        int result = realCalculator.multiply(a, b);

        long endTime = System.currentTimeMillis();
        System.out.println("LOG: multiply() completed in " + (endTime - startTime) + "ms");

        return result;
    }
}
