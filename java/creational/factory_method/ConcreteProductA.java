package creational.factory_method;

/**
 * Concrete implementation of Product A - represents a specific product type
 */
public class ConcreteProductA implements Product {
    @Override
    public void operation() {
        System.out.println("ConcreteProductA: Performing operation A");
    }

    @Override
    public String getDescription() {
        return "Product A: High-performance variant";
    }
}
