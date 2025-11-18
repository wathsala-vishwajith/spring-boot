package creational.factory_method;

/**
 * Creator abstract class - declares the factory method that returns Product objects
 * Also contains core business logic that relies on Product objects
 */
public abstract class Creator {
    /**
     * Factory method - subclasses override this to create specific product types
     */
    public abstract Product factoryMethod();

    /**
     * Core business logic that uses the factory method
     */
    public void someOperation() {
        Product product = factoryMethod();
        System.out.println("Creator: Working with " + product.getDescription());
        product.operation();
    }
}
