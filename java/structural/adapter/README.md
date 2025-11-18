# Adapter Pattern

## Overview
The Adapter pattern (also known as Wrapper) is a structural design pattern that allows objects with incompatible interfaces to collaborate. It acts as a bridge between two incompatible interfaces by wrapping an existing class with a new interface.

## When to Use

### Use Adapter when:

1. **You want to use an existing class with an incompatible interface**
   - Third-party library with different interface
   - Legacy code that can't be modified
   - External API with incompatible signatures

2. **You want to create a reusable class that cooperates with unrelated classes**
   - Classes that don't have compatible interfaces
   - Need to work with classes you can't modify

3. **You need to use several existing subclasses**
   - But it's impractical to adapt their interface by subclassing each one
   - Adapter can adapt the parent class interface

4. **Integration of new systems with legacy systems**
   - New system expects different interface than legacy provides
   - Adapter provides translation layer

### Real-world scenarios:

- **Media players**: Playing different file formats (VLC, MP4, MP3)
- **Payment gateways**: Integrating multiple payment providers (PayPal, Stripe, Square)
- **Database drivers**: JDBC adapts different database vendors
- **Logging frameworks**: SLF4J adapts various logging implementations
- **Power adapters**: Physical adapters for electrical plugs (real-world metaphor)
- **Legacy system integration**: Modern API wrapping old systems
- **XML to JSON converters**: Adapting different data formats

## Implementation Details

### Key Components:

1. **Target**: Interface that client expects
2. **Adaptee**: Existing class with incompatible interface
3. **Adapter**: Converts Adaptee interface to Target interface
4. **Client**: Works with Target interface

### Types of Adapters:

#### 1. Object Adapter (Composition)
```java
public class Adapter implements Target {
    private Adaptee adaptee;

    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void request() {
        adaptee.specificRequest();
    }
}
```
**Uses composition** - Adapter holds reference to Adaptee

#### 2. Class Adapter (Inheritance)
```java
public class Adapter extends Adaptee implements Target {
    @Override
    public void request() {
        specificRequest();
    }
}
```
**Uses inheritance** - Adapter inherits from Adaptee (requires multiple inheritance, limited in Java)

## Caveats and Considerations

### Disadvantages:

1. **Increased Complexity**
   - Additional layer of abstraction
   - More classes to maintain
   - Can make code harder to understand

2. **Performance Overhead**
   - Extra method calls through adapter
   - Usually negligible but consider for high-performance scenarios

3. **Limited Two-Way Adaptation**
   - Object adapter only adapts Adaptee, not subclasses
   - May need multiple adapters for class hierarchies

4. **Maintenance Burden**
   - Changes to Adaptee interface require adapter updates
   - Multiple adapters multiply maintenance

### Common Pitfalls:

1. **Overusing Adapters**
   - Don't use adapter when you can modify the interface directly
   - Don't use adapter to fix poor design
   ```java
   // Bad - unnecessary adapter
   class MyList extends ArrayList {}  // Just use ArrayList directly

   // Good - adapting incompatible interface
   class LegacyListAdapter implements ModernList {
       private LegacyList legacy;
   }
   ```

2. **Not Handling All Methods**
   - Adapter must implement all Target interface methods
   - Empty implementations can cause runtime issues
   ```java
   // Bad
   public void unsupportedMethod() {
       // Do nothing - client might expect behavior!
   }

   // Good
   public void unsupportedMethod() {
       throw new UnsupportedOperationException(
           "This operation is not supported");
   }
   ```

3. **Confusing with Decorator**
   - Adapter changes interface
   - Decorator enhances functionality with same interface

4. **Bidirectional Adaptation**
   - Be careful when adapter needs to work both ways
   - Can become complex quickly

5. **State Management**
   - If adapter maintains state, ensure thread safety
   - Be clear about where state lives (adapter vs adaptee)

### Best Practices:

1. **Prefer Object Adapter (Composition)**
   - More flexible than class adapter
   - Can adapt multiple adaptees
   - Follows composition over inheritance principle
   ```java
   // Good - flexible
   class Adapter implements Target {
       private Adaptee adaptee;
   }

   // Less flexible - limited by single inheritance
   class Adapter extends Adaptee implements Target {
   }
   ```

2. **Use Meaningful Names**
   ```java
   // Clear naming
   class PayPalPaymentAdapter implements PaymentGateway
   class LegacyDatabaseAdapter implements ModernDatabase
   ```

3. **Handle Incompatible Methods Explicitly**
   ```java
   @Override
   public void modernMethod() {
       throw new UnsupportedOperationException(
           "Legacy system doesn't support this operation");
   }
   ```

4. **Document Adaptation Strategy**
   ```java
   /**
    * Adapts LegacyPayment to PaymentGateway interface.
    * Note: refund() is not supported by legacy system.
    */
   public class LegacyPaymentAdapter implements PaymentGateway {
   }
   ```

5. **Consider Two-Way Adapters**
   ```java
   public class TwoWayAdapter implements TargetA, TargetB {
       // Can work as both interfaces
   }
   ```

6. **Use Factory for Adapter Selection**
   ```java
   public class AdapterFactory {
       public static PaymentGateway getAdapter(String type) {
           switch(type) {
               case "paypal": return new PayPalAdapter();
               case "stripe": return new StripeAdapter();
               default: throw new IllegalArgumentException();
           }
       }
   }
   ```

7. **Keep Adapter Lightweight**
   - Don't add business logic to adapter
   - Only translate between interfaces
   - Keep it simple and focused

8. **Test Thoroughly**
   - Test all method mappings
   - Test edge cases and error conditions
   - Ensure proper exception translation

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Decorator** | Adds functionality vs. changes interface |
| **Proxy** | Same interface vs. different interface |
| **Facade** | Simplifies complex subsystem vs. makes incompatible interfaces compatible |
| **Bridge** | Designed upfront for flexibility vs. retrofitted for compatibility |

## Relationship with Other Patterns

- **Bridge** is usually designed up-front; Adapter is retrofitted
- **Decorator** enhances without changing interface
- **Proxy** provides same interface; Adapter provides different one
- **Facade** defines new interface; Adapter reuses old interface
- Can use **Factory Method** to create adapters
- Often used with **Strategy** for algorithm selection

## Real-world Examples in Java

1. **Arrays.asList()**
   ```java
   Integer[] arr = {1, 2, 3};
   List<Integer> list = Arrays.asList(arr);  // Adapts array to List
   ```

2. **InputStreamReader**
   ```java
   InputStream is = new FileInputStream("file.txt");
   Reader reader = new InputStreamReader(is);  // Adapts InputStream to Reader
   ```

3. **Collections Wrappers**
   ```java
   List<String> list = new ArrayList<>();
   List<String> synced = Collections.synchronizedList(list);
   ```

4. **JDBC-ODBC Bridge**
   - Adapts JDBC interface to ODBC drivers

## Advantages

1. **Single Responsibility Principle**
   - Separates interface conversion from business logic

2. **Open/Closed Principle**
   - Can introduce new adapters without changing existing code

3. **Flexibility**
   - Can adapt multiple incompatible interfaces
   - Can swap adapters at runtime

4. **Code Reuse**
   - Reuse existing code without modification
   - Integrate third-party libraries easily

## Running the Example

```bash
cd java/structural/adapter
javac AdapterDemo.java
java structural.adapter.AdapterDemo
```

## Expected Output

The demo will show:
1. Media player playing different file formats using adapters
2. Payment processing system adapting multiple payment gateways
3. How adapter makes incompatible interfaces work together
4. Real-world payment gateway integration example
