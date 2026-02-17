package creational.singleton;

/**
 * Enum Singleton
 * Thread-safe, prevents reflection and serialization attacks
 * Recommended by Joshua Bloch in "Effective Java"
 */
public enum EnumSingleton {
    INSTANCE;

    private String value;

    EnumSingleton() {
        System.out.println("EnumSingleton instance created");
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void showMessage() {
        System.out.println("Hello from EnumSingleton! Value: " + value);
    }
}
