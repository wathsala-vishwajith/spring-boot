package creational.factory_method;

/**
 * Concrete Creator B - creates Product B instances
 */
public class ConcreteCreatorB extends Creator {
    @Override
    public Product factoryMethod() {
        return new ConcreteProductB();
    }
}
