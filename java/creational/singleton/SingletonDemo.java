package creational.singleton;

/**
 * Demo class showing different Singleton implementations
 */
public class SingletonDemo {
    public static void main(String[] args) {
        System.out.println("=== Singleton Pattern Demo ===\n");

        // Test Eager Singleton
        System.out.println("--- Eager Singleton ---");
        EagerSingleton eager1 = EagerSingleton.getInstance();
        EagerSingleton eager2 = EagerSingleton.getInstance();
        System.out.println("Same instance? " + (eager1 == eager2));
        eager1.showMessage();

        // Test Lazy Singleton
        System.out.println("\n--- Lazy Singleton ---");
        LazySingleton lazy1 = LazySingleton.getInstance();
        LazySingleton lazy2 = LazySingleton.getInstance();
        System.out.println("Same instance? " + (lazy1 == lazy2));
        lazy1.showMessage();

        // Test Thread-Safe Singleton
        System.out.println("\n--- Thread-Safe Singleton ---");
        ThreadSafeSingleton ts1 = ThreadSafeSingleton.getInstance();
        ThreadSafeSingleton ts2 = ThreadSafeSingleton.getInstance();
        System.out.println("Same instance? " + (ts1 == ts2));
        ts1.showMessage();

        // Test Double-Checked Locking Singleton
        System.out.println("\n--- Double-Checked Locking Singleton ---");
        DoubleCheckedLockingSingleton dcl1 = DoubleCheckedLockingSingleton.getInstance();
        DoubleCheckedLockingSingleton dcl2 = DoubleCheckedLockingSingleton.getInstance();
        System.out.println("Same instance? " + (dcl1 == dcl2));
        dcl1.showMessage();

        // Test Bill Pugh Singleton
        System.out.println("\n--- Bill Pugh Singleton ---");
        BillPughSingleton bp1 = BillPughSingleton.getInstance();
        BillPughSingleton bp2 = BillPughSingleton.getInstance();
        System.out.println("Same instance? " + (bp1 == bp2));
        bp1.showMessage();

        // Test Enum Singleton
        System.out.println("\n--- Enum Singleton ---");
        EnumSingleton enum1 = EnumSingleton.INSTANCE;
        EnumSingleton enum2 = EnumSingleton.INSTANCE;
        System.out.println("Same instance? " + (enum1 == enum2));
        enum1.setValue("Test Value");
        enum2.showMessage();

        // Test multi-threading scenario
        System.out.println("\n--- Thread Safety Test ---");
        testThreadSafety();

        // Real-world example
        System.out.println("\n--- Real-world Example: Database Connection ---");
        demonstrateDatabaseConnection();
    }

    private static void testThreadSafety() {
        System.out.println("Creating 5 threads to access Singleton...");

        for (int i = 0; i < 5; i++) {
            final int threadNum = i + 1;
            new Thread(() -> {
                DoubleCheckedLockingSingleton instance =
                    DoubleCheckedLockingSingleton.getInstance();
                System.out.println("Thread " + threadNum + ": " +
                    instance.hashCode());
            }).start();
        }

        // Wait for threads to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateDatabaseConnection() {
        // First access
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        db1.connect("mydb", "admin:password");
        db1.executeQuery("SELECT * FROM users");

        System.out.println();

        // Second access from different part of code
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        System.out.println("Same instance? " + (db1 == db2));
        System.out.println("Already connected? " + db2.isConnected());
        db2.executeQuery("INSERT INTO users VALUES (1, 'John')");

        db2.disconnect();
    }
}
