# Decorator Pattern

## Overview
The Decorator pattern is a structural design pattern that allows you to attach new behaviors to objects dynamically by placing these objects inside special wrapper objects called decorators. Decorators provide a flexible alternative to subclassing for extending functionality.

Think of it like adding toppings to a pizza or wrapping a gift - you start with a base object and wrap it with additional features layer by layer.

## When to Use

### Use Decorator when:

1. **You want to add responsibilities to objects dynamically and transparently**
   - Add behavior without affecting other objects
   - Add behavior at runtime, not compile time
   - Make changes reversible

2. **Extension by subclassing is impractical**
   - Class definition is hidden or unavailable
   - Large number of independent extensions would produce explosion of subclasses
   - Subclassing would be inflexible or cumbersome

3. **You want to add/remove responsibilities from an object at runtime**
   - Stack multiple decorators
   - Mix and match behaviors
   - Dynamic composition

4. **You want to combine several behaviors flexibly**
   - Multiple optional features
   - Features that can be combined in various ways
   - Avoid class explosion from all combinations

### Real-world scenarios:

- **Coffee shop**: Coffee with milk, sugar, whipped cream, caramel (add-ons)
- **Text editors**: Text with bold, italic, underline, color formatting
- **I/O streams**: BufferedReader wrapping FileReader wrapping InputStreamReader
- **GUI components**: Adding scrollbars, borders, shadows to windows
- **Notifications**: Email, SMS, Slack notifications with logging, filtering, formatting
- **Web requests**: Adding authentication, caching, logging to HTTP requests
- **Pizza orders**: Base pizza with various toppings
- **Game characters**: Adding power-ups, abilities, equipment to characters

## Implementation Details

### Key Components:

1. **Component**: Interface for objects that can have responsibilities added
2. **Concrete Component**: Base object to which additional responsibilities can be attached
3. **Decorator**: Maintains reference to Component and defines interface that conforms to Component
4. **Concrete Decorator**: Adds responsibilities to the component

### Structure:

```java
// Component interface
interface Component {
    void operation();
}

// Concrete component
class ConcreteComponent implements Component {
    public void operation() {
        // Base behavior
    }
}

// Base decorator
abstract class Decorator implements Component {
    protected Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    public void operation() {
        component.operation();
    }
}

// Concrete decorators
class ConcreteDecoratorA extends Decorator {
    public ConcreteDecoratorA(Component component) {
        super(component);
    }

    public void operation() {
        super.operation();
        addedBehavior();
    }

    private void addedBehavior() {
        // Additional behavior
    }
}
```

### Classic Example - Coffee Shop:

```java
interface Coffee {
    String getDescription();
    double getCost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() {
        return "Simple Coffee";
    }

    public double getCost() {
        return 2.0;
    }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    public String getDescription() {
        return coffee.getDescription();
    }

    public double getCost() {
        return coffee.getCost();
    }
}

class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }

    public double getCost() {
        return coffee.getCost() + 0.5;
    }
}

// Usage
Coffee coffee = new MilkDecorator(
                    new SugarDecorator(
                        new SimpleCoffee()));
System.out.println(coffee.getDescription() + " - $" + coffee.getCost());
// Output: Simple Coffee, Sugar, Milk - $2.7
```

## Caveats and Considerations

### Disadvantages:

1. **Can Result in Many Small Objects**
   - Lots of small decorator classes
   - Can be hard to learn and debug
   ```java
   // Many decorator classes
   class MilkDecorator extends CoffeeDecorator { }
   class SugarDecorator extends CoffeeDecorator { }
   class WhipDecorator extends CoffeeDecorator { }
   class CaramelDecorator extends CoffeeDecorator { }
   // ... and many more
   ```

2. **Hard to Remove Specific Decorator from Stack**
   - Decorators are wrapped, not easily unwrapped
   - Removing middle decorator is difficult
   ```java
   // Hard to remove MilkDecorator without rebuilding
   Coffee coffee = new CaramelDecorator(
                       new MilkDecorator(
                           new SimpleCoffee()));
   ```

3. **Decorators Aren't Identical to Components**
   - Decorated object isn't identical to original
   - Can't rely on object identity
   ```java
   Component c = new ConcreteComponent();
   Component d = new Decorator(c);
   // c != d (different objects)
   ```

4. **Order of Decorators Can Matter**
   - Different order can give different results
   - Need to document correct order
   ```java
   // Encrypt then compress
   DataSource ds1 = new CompressionDecorator(
                        new EncryptionDecorator(source));

   // Compress then encrypt (different result!)
   DataSource ds2 = new EncryptionDecorator(
                        new CompressionDecorator(source));
   ```

5. **Complexity in Debugging**
   - Multiple layers of wrapping
   - Stack traces can be deep and confusing
   - Hard to understand what's happening

### Common Pitfalls:

1. **Confusing with Adapter Pattern**
   ```java
   // Adapter - changes interface
   class Adapter implements NewInterface {
       private OldInterface old;
       public void newMethod() {
           old.oldMethod();
       }
   }

   // Decorator - keeps same interface, adds behavior
   class Decorator implements Interface {
       private Interface component;
       public void method() {
           component.method(); // Same interface
           // Add extra behavior
       }
   }
   ```

2. **Not Delegating Properly**
   ```java
   // Bad - not delegating to wrapped component
   class BadDecorator extends Decorator {
       public void operation() {
           // Added behavior only, forgot to call super!
           addedBehavior();
       }
   }

   // Good - delegates properly
   class GoodDecorator extends Decorator {
       public void operation() {
           super.operation(); // Delegate first
           addedBehavior();
       }
   }
   ```

3. **Breaking Component Interface**
   ```java
   // Bad - adding methods that aren't in Component interface
   class BadDecorator implements Component {
       public void operation() { }
       public void extraMethod() { } // Not in Component!
   }

   // Good - stick to Component interface
   class GoodDecorator implements Component {
       public void operation() {
           // Only methods from Component
       }
   }
   ```

4. **Not Making Decorator Abstract Enough**
   ```java
   // Bad - concrete decorator doing too much
   class MilkAndSugarDecorator extends Decorator {
       // Combines two responsibilities
   }

   // Good - single responsibility per decorator
   class MilkDecorator extends Decorator { }
   class SugarDecorator extends Decorator { }
   ```

5. **Unnecessary Decorators**
   ```java
   // Bad - using decorator when simple inheritance would work
   class SpecialCoffee extends SimpleCoffee {
       // If behavior is fixed and never changes, inheritance is simpler
   }

   // Good - use decorator when behavior is dynamic
   Coffee coffee = new MilkDecorator(base); // Can add/remove at runtime
   ```

### Best Practices:

1. **Keep Decorators Lightweight**
   ```java
   // Good - focused decorator
   class BoldDecorator extends TextDecorator {
       public String render() {
           return "<b>" + text.render() + "</b>";
       }
   }

   // Bad - decorator doing too much
   class FormattingDecorator extends TextDecorator {
       // Don't combine multiple formatting types
   }
   ```

2. **Use Base Decorator Class**
   ```java
   // Good - provides default implementation
   abstract class CoffeeDecorator implements Coffee {
       protected Coffee coffee;

       public CoffeeDecorator(Coffee coffee) {
           this.coffee = coffee;
       }

       public String getDescription() {
           return coffee.getDescription();
       }

       public double getCost() {
           return coffee.getCost();
       }
   }
   ```

3. **Make Component Interface Complete**
   ```java
   // Good - complete interface
   interface Component {
       void operation1();
       void operation2();
       String getProperty();
   }

   // All decorators can delegate all methods
   ```

4. **Consider Decorator Factory**
   ```java
   public class CoffeeFactory {
       public static Coffee createCoffee(List<String> addons) {
           Coffee coffee = new SimpleCoffee();
           for (String addon : addons) {
               coffee = decorateWith(coffee, addon);
           }
           return coffee;
       }

       private static Coffee decorateWith(Coffee coffee, String addon) {
           switch(addon) {
               case "milk": return new MilkDecorator(coffee);
               case "sugar": return new SugarDecorator(coffee);
               default: return coffee;
           }
       }
   }
   ```

5. **Document Decorator Order**
   ```java
   /**
    * DataSource with compression and encryption.
    *
    * IMPORTANT: Decorators must be applied in this order:
    * 1. CompressionDecorator (compress first)
    * 2. EncryptionDecorator (then encrypt)
    *
    * This ensures data is compressed before encryption.
    */
   ```

6. **Consider Immutability**
   ```java
   // Good - decorators are immutable
   class Decorator implements Component {
       private final Component component;

       public Decorator(Component component) {
           this.component = component;
       }
   }
   ```

7. **Use Builder for Complex Decoration**
   ```java
   Coffee coffee = new CoffeeBuilder()
       .base(new SimpleCoffee())
       .addMilk()
       .addSugar()
       .addWhip()
       .build();
   ```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Adapter** | Changes interface vs. adds behavior with same interface |
| **Proxy** | Controls access vs. adds responsibilities |
| **Composite** | Tree structure vs. recursive wrapping |
| **Strategy** | Changes algorithm vs. adds responsibilities |

### Decorator vs Adapter:
- **Decorator**: Same interface, adds behavior
- **Adapter**: Different interface, makes things compatible
- **Decorator**: Can stack multiple decorators
- **Adapter**: Usually single wrapper

### Decorator vs Proxy:
- **Decorator**: Adds responsibilities
- **Proxy**: Controls access to object
- Both use same interface and delegation

### Decorator vs Composite:
- **Decorator**: Linear chain of wrappers
- **Composite**: Tree structure of components
- **Decorator**: Adds behavior
- **Composite**: Represents hierarchies

## Relationship with Other Patterns

- **Adapter** changes interface; Decorator keeps same interface
- **Composite** and Decorator have similar structures (recursive composition)
- **Strategy** changes internals; Decorator changes externals
- **Prototype** can help save state of complex decorated objects
- Often combined with **Factory** and **Builder** patterns

## Real-world Examples in Java

1. **Java I/O Streams**
   ```java
   // Classic decorator example in Java
   BufferedReader reader = new BufferedReader(
       new InputStreamReader(
           new FileInputStream("file.txt")));
   ```

2. **Collections**
   ```java
   List<String> list = new ArrayList<>();
   List<String> synchronized = Collections.synchronizedList(list);
   List<String> unmodifiable = Collections.unmodifiableList(list);
   ```

3. **Servlet Filters**
   ```java
   // Request/Response wrappers in servlets
   HttpServletRequestWrapper
   HttpServletResponseWrapper
   ```

4. **Swing Components**
   ```java
   JScrollPane scrollPane = new JScrollPane(textArea);
   ```

## Advantages

1. **More Flexible Than Static Inheritance**
   - Add/remove responsibilities at runtime
   - Mix and match decorators

2. **Avoids Feature-Laden Classes**
   - Pay-as-you-go approach
   - Add functionality incrementally

3. **Responsibilities Can Be Added/Removed at Runtime**
   - Dynamic composition
   - Configurable behavior

4. **Follows Open/Closed Principle**
   - Open for extension (new decorators)
   - Closed for modification (existing code unchanged)

5. **Single Responsibility Principle**
   - Each decorator has one job
   - Monolithic class split into smaller classes

## When NOT to Use

1. **Small number of fixed combinations**
   - Simple inheritance might be clearer
   - Decorator adds unnecessary complexity

2. **Object identity is important**
   - Decorated object is different from original
   - Can't use == for comparison

3. **Order of decorators doesn't matter conceptually**
   - But might matter technically
   - Can lead to confusion

4. **Performance is critical**
   - Each decorator adds method call overhead
   - Can be significant with many layers

## Running the Example

```bash
cd java/structural/decorator
javac DecoratorDemo.java
java structural.decorator.DecoratorDemo
```

## Expected Output

The demo will show:
1. Coffee with various add-ons (milk, sugar, whipped cream, caramel)
2. Text with different formatting (bold, italic, underline, color)
3. Stream processing with compression and encryption decorators
4. Pizza with various toppings
5. How decorators can be stacked to add multiple behaviors dynamically
