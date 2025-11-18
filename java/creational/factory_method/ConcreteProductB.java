package creational.factory_method;

/**
 * Concrete implementation of Product B - represents another specific product type
 */
public class ConcreteProductB implements Product {
    @Override
    public void operation() {
        System.out.println("ConcreteProductB: Performing operation B");
    }

    @Override
    public String getDescription() {
        return "Product B: Budget-friendly variant";
    }
}
