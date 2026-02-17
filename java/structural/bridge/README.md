# Bridge Pattern

## Overview
The Bridge pattern is a structural design pattern that separates an object's abstraction from its implementation so that the two can vary independently. It uses composition over inheritance to decouple abstraction and implementation, allowing both to be developed and extended separately.

Think of it as a bridge connecting two class hierarchies - one for abstractions and one for implementations.

## When to Use

### Use Bridge when:

1. **You want to avoid permanent binding between abstraction and implementation**
   - Need to select implementation at runtime
   - Want to switch implementations dynamically
   - Both abstraction and implementation should be extensible

2. **Both abstractions and implementations should be extensible by subclassing**
   - Need to extend both hierarchies independently
   - Changes in implementation shouldn't affect abstraction
   - Multiple orthogonal dimensions of variation

3. **Changes in implementation should not impact clients**
   - Hide implementation details from clients
   - Implementation changes shouldn't require recompilation
   - Provide stable interface while varying implementation

4. **You have a proliferation of classes from combining abstractions and implementations**
   - Instead of n×m classes, you get n+m classes
   - Reduces class explosion from inheritance
   - More maintainable class hierarchy

### Real-world scenarios:

- **GUI frameworks**: Separating widgets from platform-specific implementations (Windows, Mac, Linux)
- **Database drivers**: JDBC separates database API from vendor-specific implementations
- **Graphics systems**: Shapes (abstraction) rendered using different APIs (OpenGL, DirectX, Vulkan)
- **Remote controls**: Different remote types (basic, advanced) controlling different devices (TV, radio, AC)
- **Message systems**: Different message types sent via different platforms (email, SMS, push)
- **Payment systems**: Payment methods (credit card, debit, digital wallet) via different processors
- **Logging frameworks**: Different log levels using different outputs (file, console, network)
- **Device drivers**: Abstract device operations implemented for specific hardware

## Implementation Details

### Key Components:

1. **Abstraction**: High-level control layer (contains reference to implementation)
2. **Refined Abstraction**: Extends abstraction with additional features
3. **Implementation**: Interface for implementation classes
4. **Concrete Implementation**: Actual implementation of the interface

### Structure:

```java
// Implementation interface
interface Implementation {
    void operationImpl();
}

// Concrete implementations
class ConcreteImplementationA implements Implementation {
    public void operationImpl() {
        // Implementation A
    }
}

class ConcreteImplementationB implements Implementation {
    public void operationImpl() {
        // Implementation B
    }
}

// Abstraction
abstract class Abstraction {
    protected Implementation impl;

    protected Abstraction(Implementation impl) {
        this.impl = impl;
    }

    abstract void operation();
}

// Refined abstraction
class RefinedAbstraction extends Abstraction {
    public RefinedAbstraction(Implementation impl) {
        super(impl);
    }

    public void operation() {
        impl.operationImpl();
    }
}
```

### Classic Example - Shapes and Colors:

```java
// Implementation
interface Color {
    void applyColor();
}

class RedColor implements Color {
    public void applyColor() {
        System.out.println("Red");
    }
}

// Abstraction
abstract class Shape {
    protected Color color;

    protected Shape(Color color) {
        this.color = color;
    }

    abstract void draw();
}

class Circle extends Shape {
    public Circle(Color color) {
        super(color);
    }

    public void draw() {
        System.out.print("Circle - ");
        color.applyColor();
    }
}

// Usage
Shape redCircle = new Circle(new RedColor());
redCircle.draw();
```

## Caveats and Considerations

### Disadvantages:

1. **Increased Complexity**
   - More classes and interfaces to maintain
   - Additional level of indirection
   - Can be overkill for simple scenarios
   ```java
   // Simple case - Bridge might be overkill
   class RedCircle { } // Simpler without Bridge

   // Complex case - Bridge is beneficial
   // 5 shapes × 5 colors = 25 classes WITHOUT Bridge
   // 5 shapes + 5 colors = 10 classes WITH Bridge
   ```

2. **Overhead for Simple Cases**
   - Not suitable when you have only one implementation
   - Additional abstraction layer adds complexity
   - Use only when you truly need flexibility

3. **Design Upfront Required**
   - Need to identify abstraction and implementation hierarchies
   - Requires good understanding of domain
   - Hard to retrofit into existing code

4. **Learning Curve**
   - Developers need to understand two hierarchies
   - More difficult to understand than simple inheritance
   - Requires careful documentation

### Common Pitfalls:

1. **Confusing with Adapter Pattern**
   ```java
   // Adapter - makes incompatible interfaces work together (retrofitted)
   class LegacyAdapter implements NewInterface {
       private LegacyClass legacy;
   }

   // Bridge - designed upfront to keep abstraction and implementation separate
   abstract class Abstraction {
       protected Implementation impl;
   }
   ```
   - **Bridge** is designed upfront; **Adapter** is retrofitted
   - **Bridge** keeps hierarchies separate; **Adapter** makes existing things work

2. **Overusing the Pattern**
   ```java
   // Bad - unnecessary Bridge
   class SimpleButton {
       private ButtonRenderer renderer; // Overkill if only one renderer
   }

   // Good - Bridge when multiple implementations exist
   class Button {
       private Renderer renderer; // Windows, Mac, Linux renderers
   }
   ```

3. **Not Identifying Correct Abstraction**
   - Choosing wrong dimension for abstraction vs implementation
   - Leads to awkward design and forced patterns
   ```java
   // Wrong split
   abstract class Shape {
       protected Size size; // Size shouldn't be implementation
   }

   // Correct split
   abstract class Shape {
       protected Color color; // Color is good implementation choice
   }
   ```

4. **Tight Coupling Between Hierarchies**
   ```java
   // Bad - abstraction depends on concrete implementation
   class Circle {
       private RedColor color; // Should depend on interface
   }

   // Good - depends on interface
   class Circle {
       private Color color;
   }
   ```

5. **Exposing Implementation Details**
   ```java
   // Bad - exposing implementation
   public Implementation getImplementation() {
       return impl; // Breaks encapsulation
   }

   // Good - keep implementation hidden
   private Implementation impl; // No public access
   ```

### Best Practices:

1. **Identify Orthogonal Dimensions**
   ```java
   // Good - independent dimensions
   // Dimension 1: Shape (Circle, Square, Triangle)
   // Dimension 2: Color (Red, Green, Blue)
   // Result: 3 + 3 = 6 classes instead of 3 × 3 = 9
   ```

2. **Use Dependency Injection**
   ```java
   // Good - inject implementation
   public Abstraction(Implementation impl) {
       this.impl = impl;
   }

   // Also good - factory pattern
   public static Abstraction create(String type) {
       Implementation impl = ImplementationFactory.create(type);
       return new ConcreteAbstraction(impl);
   }
   ```

3. **Keep Implementation Interface Focused**
   ```java
   // Good - focused interface
   interface Renderer {
       void renderCircle(int x, int y, int radius);
       void renderSquare(int x, int y, int side);
   }

   // Bad - too many responsibilities
   interface Renderer {
       void render();
       void animate();
       void playSound(); // Not related to rendering
   }
   ```

4. **Document the Bridge Structure**
   ```java
   /**
    * Shape represents the abstraction hierarchy.
    * Delegates color-related operations to Color implementation.
    *
    * Abstraction: Shape hierarchy (Circle, Square, etc.)
    * Implementation: Color hierarchy (Red, Green, Blue, etc.)
    */
   abstract class Shape {
       protected Color color;
   }
   ```

5. **Consider Thread Safety**
   ```java
   // If implementation is shared or mutable
   class ThreadSafeAbstraction {
       private final Implementation impl; // Immutable reference

       public synchronized void operation() {
           impl.operationImpl();
       }
   }
   ```

6. **Use Builder for Complex Abstractions**
   ```java
   public class ShapeBuilder {
       private Color color;
       private int size;

       public ShapeBuilder withColor(Color color) {
           this.color = color;
           return this;
       }

       public Shape build() {
           return new Circle(color, size);
       }
   }
   ```

## Comparison with Other Patterns

| Pattern | Purpose | Key Difference |
|---------|---------|----------------|
| **Adapter** | Make incompatible interfaces work together | Retrofitted vs. designed upfront (Bridge) |
| **Strategy** | Encapsulate algorithms | Strategy changes algorithm; Bridge separates abstraction from implementation |
| **State** | Change behavior based on state | State changes behavior; Bridge separates hierarchies |
| **Abstract Factory** | Create families of objects | Factory creates objects; Bridge separates abstraction/implementation |

### Bridge vs Adapter:
- **Bridge**: Designed upfront to separate abstraction and implementation
- **Adapter**: Retrofitted to make existing incompatible interfaces work together
- **Bridge**: Both hierarchies can be extended independently
- **Adapter**: Usually wraps a single class

### Bridge vs Strategy:
- **Bridge**: Separates abstraction from implementation (structural)
- **Strategy**: Encapsulates interchangeable algorithms (behavioral)
- **Bridge**: Two hierarchies that vary independently
- **Strategy**: One interface with multiple implementations

## Relationship with Other Patterns

- **Abstract Factory** can create and configure Bridges
- **Adapter** makes things work after they're designed; Bridge makes them work before
- **State** and **Strategy** can be implemented using Bridge
- Often used with **Composite** for tree structures
- Can use **Singleton** for implementation objects if they're stateless

## Real-world Examples in Java

1. **JDBC API**
   ```java
   // Abstraction: JDBC API
   Connection conn = DriverManager.getConnection(url);
   // Implementation: MySQL, PostgreSQL, Oracle drivers
   ```

2. **Collections Framework**
   ```java
   // Abstraction: List, Set, Map
   List<String> list = new ArrayList<>();
   // Implementation: ArrayList, LinkedList
   ```

3. **AWT/Swing**
   ```java
   // Abstraction: Component hierarchy
   Button button = new Button("Click");
   // Implementation: Platform-specific peers (Windows, Mac, Linux)
   ```

4. **Logging Frameworks**
   ```java
   // Abstraction: Logger interface
   Logger logger = LoggerFactory.getLogger(MyClass.class);
   // Implementation: Log4j, Logback, JUL
   ```

## Advantages

1. **Platform Independence**
   - Separate platform-specific code from platform-independent code
   - Easy to add new platforms

2. **Reduces Class Explosion**
   - n + m classes instead of n × m classes
   - More maintainable codebase

3. **Improved Extensibility**
   - Extend abstraction and implementation independently
   - Add new implementations without changing abstractions

4. **Hides Implementation from Clients**
   - Clients only see abstraction
   - Can swap implementations at runtime

5. **Single Responsibility Principle**
   - Abstraction focuses on high-level logic
   - Implementation focuses on platform details

6. **Open/Closed Principle**
   - Open for extension (new abstractions/implementations)
   - Closed for modification (existing code unchanged)

## When NOT to Use

1. **Single implementation scenario**
   - No need for Bridge if only one implementation exists
   - Use simple composition instead

2. **Tightly coupled dimensions**
   - If abstraction and implementation aren't truly independent
   - Bridge adds unnecessary complexity

3. **Simple, small applications**
   - Overhead not justified for simple cases
   - Direct implementation is clearer

4. **Performance-critical code**
   - Extra indirection may impact performance
   - Measure before optimizing

## Running the Example

```bash
cd java/structural/bridge
javac BridgeDemo.java
java structural.bridge.BridgeDemo
```

## Expected Output

The demo will show:
1. Shapes drawn with different colors (classic Bridge example)
2. Remote controls operating different devices (TV, Radio)
3. Different message types sent via different platforms (Email, SMS, Push)
4. How abstraction and implementation can vary independently
