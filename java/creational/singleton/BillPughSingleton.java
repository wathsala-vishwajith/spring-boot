package creational.singleton;

/**
 * Bill Pugh Singleton (Initialization-on-demand holder idiom)
 * Thread-safe, lazy initialization without synchronization
 * Considered the best approach by many experts
 */
public class BillPughSingleton {

    private BillPughSingleton() {
        System.out.println("BillPughSingleton instance created");
    }

    // Static inner class - loaded only when getInstance() is called
    private static class SingletonHolder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void showMessage() {
        System.out.println("Hello from BillPughSingleton!");
    }
}
