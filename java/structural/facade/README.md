# Facade Pattern

## Overview
The Facade pattern is a structural design pattern that provides a simplified, unified interface to a complex subsystem. It defines a higher-level interface that makes the subsystem easier to use by hiding its complexity behind a simpler interface.

Think of it like a universal remote control that simplifies operating your entire home theater system, or a receptionist at a hospital who guides you through complex procedures.

## When to Use

### Use Facade when:

1. **You want to provide a simple interface to a complex subsystem**
   - Subsystem has many components
   - Components are complex and interconnected
   - Most clients only need simple operations

2. **You want to layer your subsystem**
   - Define entry points to each subsystem level
   - Decouple subsystems from clients and other subsystems
   - Promote weak coupling

3. **There are many dependencies between clients and implementation classes**
   - Reduce coupling by having clients communicate with facade
   - Subsystem changes don't affect clients
   - Easier to maintain and modify

4. **You want to wrap a poorly designed collection of APIs**
   - Legacy code with complex interface
   - Third-party libraries with difficult APIs
   - Simplify for your specific use case

### Real-world scenarios:

- **Home theater systems**: One button to watch movie (controls projector, lights, sound, DVD player)
- **Computer startup**: Simple power button (initializes CPU, memory, hard drive, OS)
- **Online shopping**: Single checkout button (handles inventory, payment, shipping, notification)
- **Database access**: Simple query methods (manage connections, transactions, error handling)
- **Compiler**: Simple compile command (lexical analysis, parsing, optimization, code generation)
- **Banking**: Account facade (manages checking, savings, loans, investments)
- **Restaurant ordering**: Simple order (coordinates kitchen, waiter, cashier, delivery)
- **Framework libraries**: Simplified APIs wrapping complex implementations

## Implementation Details

### Key Components:

1. **Facade**: Knows which subsystem classes are responsible for a request and delegates client requests to appropriate subsystem objects
2. **Subsystem Classes**: Implement subsystem functionality, handle work assigned by facade, have no knowledge of facade
3. **Client**: Uses facade instead of calling subsystem objects directly

### Structure:

```java
// Subsystem classes
class SubsystemA {
    public void operationA() { }
}

class SubsystemB {
    public void operationB() { }
}

class SubsystemC {
    public void operationC() { }
}

// Facade
class Facade {
    private SubsystemA subsystemA;
    private SubsystemB subsystemB;
    private SubsystemC subsystemC;

    public Facade() {
        this.subsystemA = new SubsystemA();
        this.subsystemB = new SubsystemB();
        this.subsystemC = new SubsystemC();
    }

    public void operation() {
        // Simplified interface combining multiple subsystem operations
        subsystemA.operationA();
        subsystemB.operationB();
        subsystemC.operationC();
    }
}
```

### Classic Example - Home Theater:

```java
// Without Facade - Client deals with complexity
Amplifier amp = new Amplifier();
DVDPlayer dvd = new DVDPlayer();
Projector projector = new Projector();
Screen screen = new Screen();
Lights lights = new Lights();

lights.dim(10);
screen.down();
projector.on();
amp.on();
amp.setVolume(5);
dvd.on();
dvd.play("Movie");

// With Facade - Simplified
class HomeTheaterFacade {
    private Amplifier amp;
    private DVDPlayer dvd;
    private Projector projector;
    private Screen screen;
    private Lights lights;

    public void watchMovie(String movie) {
        System.out.println("Get ready to watch a movie...");
        lights.dim(10);
        screen.down();
        projector.on();
        amp.on();
        amp.setVolume(5);
        dvd.on();
        dvd.play(movie);
    }
}

// Usage
HomeTheaterFacade homeTheater = new HomeTheaterFacade();
homeTheater.watchMovie("Inception"); // Simple!
```

## Caveats and Considerations

### Disadvantages:

1. **Can Become a God Object**
   - Facade can become too large and complex
   - Couples to all subsystem classes
   - Becomes monolithic if not careful
   ```java
   // Bad - facade doing too much
   class ApplicationFacade {
       public void handleDatabase() { }
       public void handleUI() { }
       public void handleNetwork() { }
       public void handleFiles() { }
       public void handleSecurity() { }
       // ... 50+ more methods
   }
   ```

2. **May Not Provide Enough Flexibility**
   - Simplified interface may be too limiting
   - Advanced users may need direct subsystem access
   - One size fits all might not work
   ```java
   // Facade only provides basic operations
   facade.watchMovie("Title");
   // What if I want custom volume? Custom screen size?
   ```

3. **Can Violate Single Responsibility Principle**
   - Facade knows about many subsystems
   - Changes in any subsystem may require facade changes
   - Hard to maintain if too many responsibilities

4. **Doesn't Prevent Direct Subsystem Access**
   - Clients can still access subsystems directly
   - No enforcement of using facade
   - Can lead to inconsistent usage

### Common Pitfalls:

1. **Making Facade Too Complex**
   ```java
   // Bad - facade with complex logic
   class Facade {
       public void operation(Config config) {
           if (config.mode == Mode.A) {
               // Complex logic here
           } else if (config.mode == Mode.B) {
               // More complex logic
           }
           // Too much business logic in facade
       }
   }

   // Good - facade delegates, keeps it simple
   class Facade {
       public void operation() {
           subsystemA.doA();
           subsystemB.doB();
       }
   }
   ```

2. **Not Allowing Subsystem Access**
   ```java
   // Bad - completely hiding subsystems
   class Facade {
       private SubsystemA a = new SubsystemA(); // Private, no access
   }

   // Better - allow direct access when needed
   class Facade {
       private SubsystemA a = new SubsystemA();

       public SubsystemA getSubsystemA() {
           return a; // For advanced users
       }
   }
   ```

3. **Creating Too Many Facades**
   ```java
   // Bad - facade for everything
   class ButtonFacade { } // Overkill for simple component
   class LabelFacade { }
   class TextBoxFacade { }

   // Good - facade for complex subsystems only
   class UIFrameworkFacade { } // Manages many components
   ```

4. **Confusing with Adapter**
   ```java
   // Adapter - makes incompatible interface compatible
   class Adapter implements Target {
       private Adaptee adaptee;
       public void request() {
           adaptee.specificRequest();
       }
   }

   // Facade - simplifies complex subsystem
   class Facade {
       private SubsystemA a;
       private SubsystemB b;
       public void simpleOperation() {
           a.complexOperationA();
           b.complexOperationB();
       }
   }
   ```

5. **Tight Coupling to Concrete Classes**
   ```java
   // Bad - tight coupling
   class Facade {
       private ConcreteSubsystemA a = new ConcreteSubsystemA();
   }

   // Better - depend on abstractions
   class Facade {
       private SubsystemA a;

       public Facade(SubsystemA a) {
           this.a = a;
       }
   }
   ```

### Best Practices:

1. **Keep Facade Simple and Focused**
   ```java
   // Good - simple, focused facade
   class HomeTheaterFacade {
       public void watchMovie(String movie) { }
       public void endMovie() { }
       public void listenToRadio() { }
       public void endRadio() { }
   }
   ```

2. **Provide Multiple Facades for Different Use Cases**
   ```java
   // Different facades for different client needs
   class BasicUserFacade {
       public void doBasicOperation() { }
   }

   class AdvancedUserFacade {
       public void doBasicOperation() { }
       public void doAdvancedOperation() { }
       public void configure() { }
   }
   ```

3. **Allow Direct Subsystem Access**
   ```java
   class Facade {
       private SubsystemA subsystemA;

       // Simple operations through facade
       public void simpleOperation() {
           subsystemA.operation();
       }

       // Advanced users can access subsystem directly
       public SubsystemA getSubsystemA() {
           return subsystemA;
       }
   }
   ```

4. **Use Dependency Injection**
   ```java
   // Good - testable and flexible
   class Facade {
       private final SubsystemA a;
       private final SubsystemB b;

       public Facade(SubsystemA a, SubsystemB b) {
           this.a = a;
           this.b = b;
       }
   }
   ```

5. **Document What Facade Hides**
   ```java
   /**
    * HomeTheaterFacade simplifies home theater operations.
    *
    * Subsystems managed:
    * - Amplifier (volume, mode)
    * - DVD Player (play, stop, eject)
    * - Projector (on/off, mode)
    * - Screen (up/down)
    * - Lights (on/off/dim)
    *
    * For advanced control, access subsystems directly via getters.
    */
   class HomeTheaterFacade {
       // ...
   }
   ```

6. **Consider Facades at Different Layers**
   ```java
   // Layer 1: Low-level subsystems
   class DatabaseConnection { }
   class QueryExecutor { }

   // Layer 2: Facade for database operations
   class DatabaseFacade {
       public void saveUser(User user) { }
   }

   // Layer 3: Facade for business operations
   class BusinessFacade {
       public void registerUser(String username) { }
   }
   ```

7. **Use Factory Pattern with Facade**
   ```java
   class FacadeFactory {
       public static Facade createFacade(Config config) {
           SubsystemA a = new SubsystemA(config);
           SubsystemB b = new SubsystemB(config);
           return new Facade(a, b);
       }
   }
   ```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Adapter** | Makes interface compatible vs. simplifies interface |
| **Proxy** | Same interface, controls access vs. simplified interface |
| **Mediator** | Centralizes communication vs. simplifies interface |
| **Decorator** | Adds responsibility vs. simplifies interface |

### Facade vs Adapter:
- **Facade**: Simplifies complex subsystem (many classes)
- **Adapter**: Makes incompatible interface compatible (one class)
- **Facade**: Creates new simplified interface
- **Adapter**: Converts existing interface

### Facade vs Mediator:
- **Facade**: Unidirectional (client to subsystem)
- **Mediator**: Bidirectional (components communicate through mediator)
- **Facade**: Simplifies interface
- **Mediator**: Reduces coupling between components

### Facade vs Proxy:
- **Facade**: Simplified interface to complex subsystem
- **Proxy**: Same interface, controls access
- **Facade**: Works with multiple subsystem objects
- **Proxy**: Works with single object

## Relationship with Other Patterns

- **Abstract Factory** can be used with Facade to create subsystem objects
- **Singleton** is often used for Facade objects
- **Mediator** is similar but focuses on peer communication
- **Adapter** makes things work after they're designed; Facade makes them easier to use
- Facade can be used with **Flyweight** to reduce subsystem objects

## Real-world Examples in Java

1. **JDBC API**
   ```java
   // JDBC is a facade over complex database operations
   Connection conn = DriverManager.getConnection(url);
   Statement stmt = conn.createStatement();
   ResultSet rs = stmt.executeQuery("SELECT * FROM users");
   ```

2. **SLF4J (Simple Logging Facade for Java)**
   ```java
   // Facade for various logging frameworks
   Logger logger = LoggerFactory.getLogger(MyClass.class);
   logger.info("Message"); // Simple interface
   ```

3. **Spring Framework**
   ```java
   // JdbcTemplate is a facade over JDBC
   JdbcTemplate template = new JdbcTemplate(dataSource);
   template.query("SELECT * FROM users", rowMapper);
   ```

4. **Java Mail API**
   ```java
   // Facade for email operations
   Session session = Session.getInstance(props);
   MimeMessage message = new MimeMessage(session);
   Transport.send(message);
   ```

## Advantages

1. **Isolates Clients from Subsystem Components**
   - Reduces dependencies
   - Easier to change subsystem

2. **Promotes Weak Coupling**
   - Clients don't depend on subsystem classes
   - Subsystem can change without affecting clients

3. **Simplifies Complex Systems**
   - Easy-to-use interface
   - Hides complexity

4. **Layered Architecture**
   - Define entry points to subsystem layers
   - Better organization

5. **Easier to Use, Test, and Understand**
   - Simple API for common use cases
   - Can mock facade for testing

## When NOT to Use

1. **Simple subsystem**
   - If subsystem is already simple, facade adds overhead
   - Direct usage is clearer

2. **Clients need fine-grained control**
   - Facade's simplification is too limiting
   - Direct subsystem access is better

3. **Subsystem changes frequently**
   - Facade needs constant updates
   - Creates maintenance burden

4. **Performance is critical**
   - Extra layer adds overhead
   - Direct access is faster

## Running the Example

```bash
cd java/structural/facade
javac FacadeDemo.java
java structural.facade.FacadeDemo
```

## Expected Output

The demo will show:
1. Home theater system (with and without facade)
2. Computer startup/shutdown (simplified by facade)
3. Online shopping checkout process (complex workflow simplified)
4. Restaurant ordering (dine-in and takeaway simplified)
5. How facade provides simple interface to complex subsystems
