# Singleton Pattern

## Overview
The Singleton pattern is a creational design pattern that ensures a class has only one instance and provides a global point of access to that instance. It restricts the instantiation of a class to a single object.

## When to Use

### Use Singleton when:

1. **Exactly one instance of a class is required**
   - Global state management
   - Shared resource access
   - Configuration settings

2. **Global access point is needed**
   - Need to access the instance from anywhere in code
   - Consistent state across application

3. **Resource must be shared**
   - Database connections
   - File system access
   - Logging
   - Caching
   - Thread pools

4. **Lazy initialization is beneficial**
   - Resource-intensive object
   - May not always be needed
   - Delay creation until first use

### Real-world scenarios:

- **Database connection pool**: Single pool managing all connections
- **Logger**: Centralized logging system
- **Configuration manager**: Application settings and properties
- **Cache manager**: Shared cache across application
- **Hardware interface access**: Printer, scanner access
- **Runtime environment**: Application context, service locator
- **Thread pool executor**: Managing worker threads

## Implementation Details

### Key Components:

1. **Private Constructor**: Prevents external instantiation
2. **Private Static Instance**: Holds the single instance
3. **Public Static Method**: Provides global access point
4. **Thread Safety** (optional): Ensures single instance in multi-threaded environment

### Implementation Approaches:

#### 1. Eager Initialization
```java
public class Singleton {
    private static final Singleton instance = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return instance;
    }
}
```
**Pros**: Simple, thread-safe
**Cons**: Instance created even if never used

#### 2. Lazy Initialization
```java
public class Singleton {
    private static Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```
**Pros**: Lazy initialization
**Cons**: Not thread-safe

#### 3. Thread-Safe (Synchronized Method)
```java
public class Singleton {
    private static Singleton instance;

    private Singleton() {}

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```
**Pros**: Thread-safe
**Cons**: Performance overhead due to synchronization

#### 4. Double-Checked Locking
```java
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```
**Pros**: Thread-safe, good performance, lazy initialization
**Cons**: Requires Java 5+ for volatile keyword

#### 5. Bill Pugh (Initialization-on-demand)
```java
public class Singleton {
    private Singleton() {}

    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```
**Pros**: Thread-safe, lazy initialization, no synchronization overhead
**Cons**: Slightly complex

#### 6. Enum Singleton
```java
public enum Singleton {
    INSTANCE;

    public void doSomething() {
        // ...
    }
}
```
**Pros**: Thread-safe, prevents reflection and serialization attacks, simplest
**Cons**: Not lazy, less flexible

## Caveats and Considerations

### Disadvantages:

1. **Violates Single Responsibility Principle**
   - Class manages its own lifecycle AND business logic
   - Does two things: ensure single instance + actual functionality

2. **Global State Issues**
   - Hidden dependencies (not visible in constructor/method signatures)
   - Tight coupling across application
   - Makes unit testing difficult

3. **Concurrency Issues**
   - Proper thread-safety implementation is crucial
   - Easy to get wrong
   - Can cause race conditions if not careful

4. **Difficult to Test**
   - Hard to mock or stub
   - Maintains state between tests
   - Can cause test interdependencies

5. **Can Mask Bad Design**
   - Often overused
   - May indicate poor dependency management
   - Alternative: Dependency Injection

### Common Pitfalls:

1. **Multiple Instances via Reflection**
   ```java
   Constructor<Singleton> constructor =
       Singleton.class.getDeclaredConstructor();
   constructor.setAccessible(true);
   Singleton instance2 = constructor.newInstance();  // Creates second instance!
   ```
   **Solution**: Throw exception in constructor if instance exists, or use enum

2. **Serialization/Deserialization Creates New Instance**
   ```java
   // Prevents multiple instances during deserialization
   protected Object readResolve() {
       return getInstance();
   }
   ```

3. **ClassLoader Issues**
   - Different classloaders can create different instances
   - Rare but possible in complex applications

4. **Cloning**
   ```java
   @Override
   protected Object clone() throws CloneNotSupportedException {
       throw new CloneNotSupportedException();
   }
   ```

5. **Not Using volatile in Double-Checked Locking**
   ```java
   // Wrong - may not work correctly
   private static Singleton instance;

   // Correct - ensures visibility
   private static volatile Singleton instance;
   ```

6. **Using in Multi-Module Applications**
   - Each module might have its own "singleton"
   - Be careful with OSGi, microservices

### Best Practices:

1. **Prefer Dependency Injection**
   - Instead of Singleton, use DI framework (Spring, Guice)
   - Makes testing easier
   - More flexible

2. **Use Enum for Simple Cases**
   - Best protection against reflection and serialization
   - Simplest implementation

3. **Use Bill Pugh for Complex Cases**
   - When you need lazy initialization
   - When enum is too restrictive

4. **Make Constructor Throw Exception**
   ```java
   private Singleton() {
       if (instance != null) {
           throw new IllegalStateException("Already instantiated");
       }
   }
   ```

5. **Document Thread Safety**
   ```java
   /**
    * Thread-safe singleton using double-checked locking.
    */
   ```

6. **Consider Alternatives**
   - Dependency Injection
   - Service Locator (with caution)
   - Monostate pattern
   - Factory pattern

7. **Stateless When Possible**
   - Minimize mutable state
   - Makes thread-safety easier

8. **Interface for Testing**
   ```java
   public interface DatabaseConnection {
       void connect();
   }

   public class DatabaseConnectionImpl implements DatabaseConnection {
       // Singleton implementation
   }
   ```

## Testing Challenges and Solutions

### Problem: Hard to Mock
```java
// Bad - tightly coupled
public class UserService {
    public void saveUser() {
        DatabaseConnection.getInstance().connect();
    }
}
```

### Solution 1: Dependency Injection
```java
// Good - testable
public class UserService {
    private DatabaseConnection db;

    public UserService(DatabaseConnection db) {
        this.db = db;
    }

    public void saveUser() {
        db.connect();
    }
}

// In test
UserService service = new UserService(mockDatabase);
```

### Solution 2: Reset Method (for testing only)
```java
public class Singleton {
    // Only for testing!
    public static void reset() {
        instance = null;
    }
}
```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Factory Method** | Creates different objects vs. one instance of same class |
| **Abstract Factory** | Creates families of objects vs. single instance |
| **Prototype** | Creates copies vs. single instance |
| **Multiton** | Multiple named instances vs. single instance |

## Relationship with Other Patterns

- **Facade** often implemented as Singleton
- **Abstract Factory** can be Singleton
- **Builder** can be Singleton
- **Prototype Registry** can be Singleton
- **State** objects often Singletons (if stateless)

## When NOT to Use

1. **When state varies by context** (use Prototype or Factory)
2. **When testing is important** (use Dependency Injection)
3. **When scalability is critical** (global state is problematic)
4. **In microservices** (each instance should be independent)
5. **When you need multiple instances** (obviously!)

## Modern Java Alternatives

### Spring Framework
```java
@Component
@Scope("singleton")  // Default scope
public class MyService {
    // Spring manages lifecycle
}
```

### CDI (Contexts and Dependency Injection)
```java
@ApplicationScoped
public class MyService {
    // CDI manages lifecycle
}
```

## Running the Example

```bash
cd java/creational/singleton
javac SingletonDemo.java
java creational.singleton.SingletonDemo
```

## Expected Output

The demo will show:
1. Different Singleton implementations
2. Verification that only one instance is created
3. Thread safety demonstration
4. Real-world database connection example
5. Hash codes showing same instance across threads
