# Proxy Pattern

## Overview
The Proxy pattern is a structural design pattern that provides a substitute or placeholder for another object. A proxy controls access to the original object, allowing you to perform something either before or after the request gets through to the original object. The proxy has the same interface as the real object, making it transparent to clients.

Think of it like a credit card acting as a proxy for your bank account - it provides the same interface (making payments) but adds additional control, security, and features.

## When to Use

### Use Proxy when:

1. **You need lazy initialization (virtual proxy)**
   - Expensive object creation
   - Delay initialization until needed
   - Save resources

2. **You need access control (protection proxy)**
   - Control access based on permissions
   - Authentication/authorization
   - Security requirements

3. **You need a local representative for remote object (remote proxy)**
   - Object lives in different address space
   - Network communication needed
   - Hide network complexity

4. **You need logging, caching, or other cross-cutting concerns**
   - Log requests
   - Cache results
   - Count references

5. **You need smart reference**
   - Additional actions when object is accessed
   - Reference counting
   - Loading persistent objects into memory

### Real-world scenarios:

- **Virtual Proxy**: Lazy loading of large images, videos, or documents
- **Protection Proxy**: Access control for documents, files, or resources
- **Remote Proxy**: RMI stubs, web service clients, distributed objects
- **Caching Proxy**: Database query caching, web proxy caching
- **Logging Proxy**: Method call logging, audit trails
- **Smart Reference**: Reference counting, copy-on-write, lazy loading from database
- **Firewall Proxy**: Network access control
- **Synchronization Proxy**: Thread-safe access to objects

## Types of Proxies

1. **Virtual Proxy**: Controls access to expensive-to-create objects (lazy loading)
2. **Protection Proxy**: Controls access based on access rights
3. **Remote Proxy**: Provides local representative for remote object
4. **Caching Proxy**: Caches results of expensive operations
5. **Logging Proxy**: Logs method calls
6. **Smart Reference Proxy**: Performs additional actions (reference counting, etc.)

## Implementation Details

### Key Components:

1. **Subject**: Common interface for RealSubject and Proxy
2. **RealSubject**: The real object that the proxy represents
3. **Proxy**: Maintains reference to RealSubject and controls access to it
4. **Client**: Works with Subject interface

### Structure:

```java
// Subject interface
interface Subject {
    void request();
}

// Real Subject
class RealSubject implements Subject {
    public void request() {
        System.out.println("RealSubject: Handling request");
    }
}

// Proxy
class Proxy implements Subject {
    private RealSubject realSubject;

    public void request() {
        if (realSubject == null) {
            realSubject = new RealSubject();
        }
        // Additional logic before/after
        System.out.println("Proxy: Pre-processing");
        realSubject.request();
        System.out.println("Proxy: Post-processing");
    }
}
```

### Classic Example - Virtual Proxy (Lazy Loading):

```java
// Subject
interface Image {
    void display();
}

// Real Subject - Expensive to create
class RealImage implements Image {
    private String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk(); // Expensive operation
    }

    private void loadFromDisk() {
        System.out.println("Loading " + filename);
    }

    public void display() {
        System.out.println("Displaying " + filename);
    }
}

// Proxy - Delays loading until needed
class ProxyImage implements Image {
    private String filename;
    private RealImage realImage;

    public ProxyImage(String filename) {
        this.filename = filename;
    }

    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename); // Lazy loading
        }
        realImage.display();
    }
}

// Usage
Image image = new ProxyImage("large_photo.jpg");
// Image not loaded yet
image.display(); // Loads now, then displays
image.display(); // Just displays (already loaded)
```

## Caveats and Considerations

### Disadvantages:

1. **Increased Complexity**
   - Additional layer of indirection
   - More classes to maintain
   - Can make code harder to understand

2. **Performance Overhead**
   - Extra method calls
   - May slow down response time
   - Not suitable for performance-critical paths

3. **Potential for Subtle Bugs**
   - Proxy behavior might not perfectly match real object
   - Thread safety issues
   - State synchronization problems

4. **Transparency Issues**
   - Sometimes clients need to know about proxy
   - Identity comparison (==) fails
   - Type checking can be problematic

### Common Pitfalls:

1. **Not Implementing All Methods**
   ```java
   // Bad - incomplete proxy
   class Proxy implements Subject {
       private RealSubject real;

       public void method1() {
           real.method1();
       }
       // Forgot to implement method2()!
   }

   // Good - implement all interface methods
   class Proxy implements Subject {
       public void method1() { real.method1(); }
       public void method2() { real.method2(); }
   }
   ```

2. **Forgetting to Delegate**
   ```java
   // Bad - not delegating to real object
   class Proxy implements Subject {
       public void request() {
           System.out.println("Logging...");
           // Forgot to call real.request()!
       }
   }

   // Good - always delegate
   class Proxy implements Subject {
       public void request() {
           System.out.println("Logging...");
           real.request(); // Delegate
       }
   }
   ```

3. **Creating Real Object Too Early**
   ```java
   // Bad - defeats purpose of virtual proxy
   class Proxy implements Subject {
       private RealSubject real = new RealSubject(); // Created immediately!
   }

   // Good - lazy initialization
   class Proxy implements Subject {
       private RealSubject real;

       public void request() {
           if (real == null) {
               real = new RealSubject(); // Create when needed
           }
           real.request();
       }
   }
   ```

4. **Not Handling Thread Safety**
   ```java
   // Bad - not thread-safe
   class Proxy implements Subject {
       private RealSubject real;

       public void request() {
           if (real == null) {
               real = new RealSubject(); // Race condition!
           }
           real.request();
       }
   }

   // Good - thread-safe
   class Proxy implements Subject {
       private volatile RealSubject real;

       public void request() {
           if (real == null) {
               synchronized(this) {
                   if (real == null) {
                       real = new RealSubject();
                   }
               }
           }
           real.request();
       }
   }
   ```

5. **Confusing with Similar Patterns**
   ```java
   // Proxy - same interface, controls access
   class Proxy implements Subject {
       private Subject real;
   }

   // Decorator - same interface, adds behavior
   class Decorator implements Subject {
       private Subject component;
       // Adds new behavior
   }

   // Adapter - different interface, makes compatible
   class Adapter implements TargetInterface {
       private Adaptee adaptee;
   }
   ```

### Best Practices:

1. **Use Same Interface**
   ```java
   // Good - proxy and real subject share interface
   interface Subject {
       void operation();
   }

   class RealSubject implements Subject {
       public void operation() { }
   }

   class Proxy implements Subject {
       private Subject real;
       public void operation() {
           real.operation();
       }
   }
   ```

2. **Initialize Lazily When Appropriate**
   ```java
   class VirtualProxy implements Subject {
       private RealSubject real;

       public void operation() {
           if (real == null) {
               real = new RealSubject();
           }
           real.operation();
       }
   }
   ```

3. **Implement Thread Safety**
   ```java
   class ThreadSafeProxy implements Subject {
       private volatile RealSubject real;
       private final Object lock = new Object();

       public void operation() {
           if (real == null) {
               synchronized(lock) {
                   if (real == null) {
                       real = new RealSubject();
                   }
               }
           }
           real.operation();
       }
   }
   ```

4. **Document Proxy Behavior**
   ```java
   /**
    * ProxyImage provides lazy loading for large images.
    *
    * Image is not loaded from disk until display() is called.
    * Subsequent calls to display() use cached image.
    *
    * Thread-safe for concurrent access.
    */
   class ProxyImage implements Image {
       // ...
   }
   ```

5. **Consider Using Composition**
   ```java
   // Good - composition
   class Proxy implements Subject {
       private final Subject real;

       public Proxy(Subject real) {
           this.real = real;
       }
   }
   ```

6. **Handle Cleanup Properly**
   ```java
   class Proxy implements Subject, AutoCloseable {
       private RealSubject real;

       public void close() {
           if (real != null) {
               real.cleanup();
           }
       }
   }
   ```

7. **Use Dynamic Proxy for Multiple Proxies**
   ```java
   // Java dynamic proxy
   Subject proxy = (Subject) Proxy.newProxyInstance(
       Subject.class.getClassLoader(),
       new Class<?>[] { Subject.class },
       new InvocationHandler() {
           public Object invoke(Object proxy, Method method, Object[] args) {
               // Proxy logic here
               return method.invoke(realSubject, args);
           }
       }
   );
   ```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Adapter** | Changes interface vs. keeps same interface |
| **Decorator** | Adds behavior vs. controls access |
| **Facade** | Simplifies interface vs. same interface with control |
| **Bridge** | Separates abstraction from implementation vs. controls access |

### Proxy vs Adapter:
- **Proxy**: Same interface as real subject
- **Adapter**: Different interface (makes incompatible interfaces compatible)
- **Proxy**: Controls access
- **Adapter**: Translates interface

### Proxy vs Decorator:
- **Proxy**: Controls access to object
- **Decorator**: Adds responsibilities to object
- **Proxy**: Usually manages object lifecycle
- **Decorator**: Usually just adds behavior
- Both use same interface and delegation

### Proxy vs Facade:
- **Proxy**: One-to-one relationship with real object
- **Facade**: Simplifies many objects behind simple interface
- **Proxy**: Same interface
- **Facade**: New, simpler interface

## Relationship with Other Patterns

- **Adapter** changes interface; Proxy keeps same interface
- **Decorator** and Proxy have similar structures but different intents
- **Facade** simplifies interface; Proxy keeps same interface
- Can use **Factory Method** to decide whether to return real object or proxy
- **Singleton** can be implemented using Proxy

## Real-world Examples in Java

1. **Java RMI (Remote Method Invocation)**
   ```java
   // Client uses stub (proxy) to call remote object
   MyService service = (MyService) Naming.lookup("rmi://server/MyService");
   service.doSomething(); // Calls through proxy
   ```

2. **Dynamic Proxy**
   ```java
   Subject proxy = (Subject) Proxy.newProxyInstance(
       Subject.class.getClassLoader(),
       new Class<?>[] { Subject.class },
       invocationHandler
   );
   ```

3. **Hibernate Lazy Loading**
   ```java
   // Returns proxy that loads data when accessed
   User user = session.load(User.class, 1);
   ```

4. **Spring AOP Proxies**
   ```java
   @Transactional
   public void saveUser(User user) {
       // Spring creates proxy to handle transaction
   }
   ```

5. **Collections Wrappers**
   ```java
   List<String> list = new ArrayList<>();
   List<String> synchronized = Collections.synchronizedList(list);
   ```

## Advantages

1. **Controls Access to Object**
   - Can add security, logging, caching
   - Transparent to clients

2. **Lazy Initialization**
   - Create expensive objects only when needed
   - Saves memory and startup time

3. **Additional Functionality**
   - Add features without changing real object
   - Reference counting, caching, logging

4. **Protects Real Object**
   - Can validate requests before forwarding
   - Can handle errors

5. **Open/Closed Principle**
   - Add new proxies without changing real object
   - Extend functionality without modification

## When NOT to Use

1. **Simple objects**
   - Proxy adds unnecessary complexity
   - Direct access is simpler

2. **Performance is critical**
   - Extra indirection adds overhead
   - Direct access is faster

3. **Object doesn't need control**
   - No lazy loading needed
   - No access control needed
   - No additional processing needed

4. **Tight coupling is acceptable**
   - Don't need abstraction
   - Simple direct access is clearer

## Running the Example

```bash
cd java/structural/proxy
javac ProxyDemo.java
java structural.proxy.ProxyDemo
```

## Expected Output

The demo will show:
1. Virtual Proxy: Lazy loading of expensive images
2. Protection Proxy: Access control based on user roles
3. Caching Proxy: Caching expensive database queries
4. Remote Proxy: Remote service access with connection management
5. Logging Proxy: Automatic logging of method calls
6. How proxy provides transparent control over object access
