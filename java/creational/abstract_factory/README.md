# Abstract Factory Pattern

## Overview
The Abstract Factory pattern is a creational design pattern that lets you produce families of related objects without specifying their concrete classes. It provides an interface for creating families of related or dependent objects.

## When to Use

### Use Abstract Factory when:

1. **Your code needs to work with various families of related products**
   - You have multiple variants of products, and they need to work together
   - Example: UI components for different operating systems (Windows, Mac, Linux)

2. **You want to ensure products from the same family are used together**
   - Prevents mixing incompatible products (e.g., Windows button with Mac checkbox)
   - Enforces consistency across product families

3. **You want to provide a library of products, revealing only interfaces**
   - Hide implementation details from clients
   - Only expose product interfaces, not concrete classes

4. **You anticipate future expansion with new product families**
   - Easy to add new families without changing existing code
   - Each family is encapsulated in its own factory

### Real-world scenarios:

- **Cross-platform UI**: Different look-and-feel for Windows, Mac, Linux
- **Theme systems**: Light theme, dark theme, high-contrast theme
- **Document converters**: Different output formats (PDF, HTML, XML) with related components
- **Game development**: Different terrain types with matching vegetation, animals, structures
- **Database abstraction**: Different database families (MySQL, PostgreSQL) with related components

## Implementation Details

### Key Components:

1. **Abstract Products**: Interfaces for each distinct product type (e.g., Button, Checkbox)
2. **Concrete Products**: Specific implementations of abstract products (e.g., WindowsButton, MacButton)
3. **Abstract Factory**: Interface declaring creation methods for all abstract products
4. **Concrete Factories**: Implement creation methods to produce concrete products
5. **Client**: Works with factories and products through abstract interfaces only

### Code Example:
```java
// Abstract products
interface Button { void render(); }
interface Checkbox { void render(); }

// Concrete products for Windows
class WindowsButton implements Button { /* ... */ }
class WindowsCheckbox implements Checkbox { /* ... */ }

// Concrete products for Mac
class MacButton implements Button { /* ... */ }
class MacCheckbox implements Checkbox { /* ... */ }

// Abstract factory
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Concrete factories
class WindowsFactory implements GUIFactory {
    public Button createButton() { return new WindowsButton(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

class MacFactory implements GUIFactory {
    public Button createButton() { return new MacButton(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}

// Client code
class Application {
    private Button button;
    private Checkbox checkbox;

    public Application(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }
}
```

## Caveats and Considerations

### Disadvantages:

1. **Complexity Overhead**
   - Introduces many new interfaces and classes
   - Can be overkill for simple applications with few product types
   - More difficult to understand and maintain initially

2. **Difficulty Adding New Products**
   - Adding a new product type requires changing the abstract factory interface
   - All concrete factories must be updated
   - Violates Open/Closed Principle when adding product types (but not when adding families)

3. **Increased Number of Classes**
   - Each product variant requires its own class
   - Each family requires its own factory class
   - Can lead to "class explosion"

4. **Parallel Hierarchies**
   - Product hierarchies must be maintained in parallel
   - Can be challenging to keep synchronized

### Common Pitfalls:

1. **Using When Not Needed**
   - Don't use if you only have one family of products
   - Don't use if products don't need to be used together
   - Simple Factory or Factory Method might be sufficient

2. **Mixing Product Families**
   - The pattern helps prevent this, but incorrect factory selection can still cause issues
   - Always validate factory selection logic

3. **Tight Coupling Between Products**
   - Products from the same family shouldn't depend on each other's implementation
   - They should only rely on interfaces

4. **God Factory**
   - Avoid creating factories that produce too many unrelated products
   - Keep product families cohesive and focused

### Best Practices:

1. **Start Simple**
   - Begin with Factory Method
   - Evolve to Abstract Factory when you have multiple related products

2. **Use Dependency Injection**
   - Inject factories into clients rather than having clients create them
   - Makes testing easier

3. **Consider Configuration**
   - Use configuration files or environment variables to select factories
   - Avoid hardcoding factory selection

4. **Combine with Singleton**
   - Factories are often implemented as Singletons
   - One factory instance per family is usually sufficient

5. **Keep Products in Same Package**
   - Organize related products together
   - Makes the family structure clear

6. **Use Enums for Factory Selection**
   ```java
   enum UITheme { WINDOWS, MAC, LINUX }

   GUIFactory getFactory(UITheme theme) {
       switch(theme) {
           case WINDOWS: return new WindowsFactory();
           case MAC: return new MacFactory();
           default: throw new IllegalArgumentException();
       }
   }
   ```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Factory Method** | Creates one product vs. family of products |
| **Builder** | Focuses on step-by-step construction vs. instant creation |
| **Prototype** | Clones existing objects vs. creates new ones |
| **Singleton** | Often used together - factories are often singletons |

## Relationship with Other Patterns

- **Can start as Factory Method** and evolve to Abstract Factory
- **Often implemented using Factory Methods** (one per product type)
- **Can use Prototype** inside factory methods instead of `new`
- **Can serve as Facade** to hide complex object creation
- **Often combined with Bridge** to hide platform-specific implementations

## Running the Example

```bash
cd java/creational/abstract_factory
javac AbstractFactoryDemo.java
java creational.abstract_factory.AbstractFactoryDemo
```

## Expected Output

The demo will show:
1. OS detection and appropriate factory selection
2. Rendering and interaction with OS-specific UI components
3. Furniture factory example showing product family consistency

## Thread Safety

When using factories in multi-threaded applications:
- Factories themselves should be stateless
- Consider using Singleton pattern with thread-safe initialization
- Product creation should be thread-safe if products are shared
