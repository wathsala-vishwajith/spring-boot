package creational.singleton;

/**
 * Thread-Safe Singleton using synchronized method
 * Thread-safe but poor performance due to synchronization overhead
 */
public class ThreadSafeSingleton {
    private static ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {
        System.out.println("ThreadSafeSingleton instance created");
    }

    // Synchronized method - thread-safe but slow
    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from ThreadSafeSingleton!");
    }
}
