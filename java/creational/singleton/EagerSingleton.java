package creational.singleton;

/**
 * Eager Initialization Singleton
 * Instance is created at class loading time
 */
public class EagerSingleton {
    // Instance created at class loading
    private static final EagerSingleton instance = new EagerSingleton();

    // Private constructor prevents instantiation
    private EagerSingleton() {
        System.out.println("EagerSingleton instance created");
    }

    public static EagerSingleton getInstance() {
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from EagerSingleton!");
    }
}
