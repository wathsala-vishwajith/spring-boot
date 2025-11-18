package creational.singleton;

/**
 * Double-Checked Locking Singleton
 * Best performance with thread safety
 * Recommended approach for most cases
 */
public class DoubleCheckedLockingSingleton {
    // volatile keyword ensures visibility across threads
    private static volatile DoubleCheckedLockingSingleton instance;

    private DoubleCheckedLockingSingleton() {
        System.out.println("DoubleCheckedLockingSingleton instance created");
    }

    public static DoubleCheckedLockingSingleton getInstance() {
        if (instance == null) {  // First check (no locking)
            synchronized (DoubleCheckedLockingSingleton.class) {
                if (instance == null) {  // Second check (with locking)
                    instance = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from DoubleCheckedLockingSingleton!");
    }
}
