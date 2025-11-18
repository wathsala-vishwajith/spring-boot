package creational.singleton;

/**
 * Lazy Initialization Singleton
 * Instance is created only when first requested
 * NOT thread-safe!
 */
public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {
        System.out.println("LazySingleton instance created");
    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from LazySingleton!");
    }
}
