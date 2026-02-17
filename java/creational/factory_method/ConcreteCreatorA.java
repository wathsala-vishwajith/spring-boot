package creational.factory_method;

/**
 * Concrete Creator A - creates Product A instances
 */
public class ConcreteCreatorA extends Creator {
    @Override
    public Product factoryMethod() {
        return new ConcreteProductA();
    }
}
