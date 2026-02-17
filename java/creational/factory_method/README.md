# Factory Method Pattern

## Overview
The Factory Method pattern is a creational design pattern that provides an interface for creating objects in a superclass, but allows subclasses to alter the type of objects that will be created.

## When to Use

### Use Factory Method when:

1. **You don't know the exact types and dependencies of objects beforehand**
   - The exact type of object to be created is determined at runtime based on configuration or user input

2. **You want to provide users with a way to extend internal components**
   - Framework users can extend default components by subclassing

3. **You want to save system resources by reusing existing objects**
   - Instead of creating new objects each time, you can reuse existing ones

4. **You want to organize code better**
   - Separates product construction code from the code that uses the product
   - Easier to introduce new product types without breaking existing code (Open/Closed Principle)

### Real-world scenarios:

- **GUI frameworks**: Creating different types of UI elements (Windows, Linux, macOS buttons)
- **Document management**: Creating different document types (PDF, Word, Excel)
- **Logistics systems**: Different transportation methods (truck, ship, plane)
- **Payment processing**: Different payment methods (credit card, PayPal, crypto)
- **Database connections**: Different database drivers (MySQL, PostgreSQL, MongoDB)

## Implementation Details

### Key Components:

1. **Product Interface**: Common interface for all objects the factory can produce
2. **Concrete Products**: Different implementations of the Product interface
3. **Creator (Factory)**: Declares the factory method returning Product objects
4. **Concrete Creators**: Override the factory method to create specific products

### Code Example:
```java
// Product interface
interface Product {
    void operation();
}

// Concrete products
class ConcreteProductA implements Product {
    public void operation() { /* ... */ }
}

// Creator with factory method
abstract class Creator {
    public abstract Product factoryMethod();

    public void someOperation() {
        Product product = factoryMethod();
        // work with product
    }
}

// Concrete creator
class ConcreteCreatorA extends Creator {
    public Product factoryMethod() {
        return new ConcreteProductA();
    }
}
```

## Caveats and Considerations

### Disadvantages:

1. **Increased Complexity**
   - The code can become more complicated since you need to introduce many new subclasses
   - Each product type requires a new creator subclass

2. **Overkill for Simple Cases**
   - If you only have one or two product types, this pattern might be unnecessary
   - Simple object instantiation might be sufficient

3. **Parallel Class Hierarchy**
   - You might end up with parallel hierarchies: one for products, one for creators
   - Can be difficult to maintain as the system grows

4. **Testing Complexity**
   - More classes mean more unit tests to write and maintain
   - Mocking can become complex with multiple levels of abstraction

### Common Pitfalls:

1. **Overuse**
   - Don't use Factory Method just because you're creating objects
   - Use it when you need the flexibility it provides

2. **Confusion with Abstract Factory**
   - Factory Method uses inheritance (subclasses decide which class to instantiate)
   - Abstract Factory uses composition (objects delegate creation to factory object)

3. **Breaking the Single Responsibility Principle**
   - Don't add too much logic to the factory method
   - Keep it focused on object creation

### Best Practices:

1. **Use when subclasses need to specify object types**
   - Perfect for frameworks where you can't anticipate all object types

2. **Consider alternatives**
   - Simple Factory (not a GoF pattern) for simpler cases
   - Abstract Factory for families of related objects
   - Builder for complex object construction

3. **Combine with other patterns**
   - Often used with Template Method
   - Can be evolved into Abstract Factory, Prototype, or Builder

4. **Thread Safety**
   - If using in multi-threaded environment, ensure thread-safety
   - Especially important for singleton products

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Abstract Factory** | Creates families of related objects vs. single product |
| **Builder** | Focuses on step-by-step construction vs. one-step creation |
| **Prototype** | Creates objects by cloning vs. from scratch |
| **Simple Factory** | Uses a single factory class vs. subclasses |

## Running the Example

```bash
cd java/creational/factory_method
javac FactoryMethodDemo.java
java creational.factory_method.FactoryMethodDemo
```

## Expected Output

The demo will show:
1. Basic factory method usage with Product A and B
2. Real-world logistics example with truck and ship delivery
