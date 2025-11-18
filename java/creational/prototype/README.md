# Prototype Pattern

## Overview
The Prototype pattern is a creational design pattern that lets you copy existing objects without making your code dependent on their classes. It enables object creation by cloning a prototypical instance.

## When to Use

### Use Prototype when:

1. **Creating objects is expensive or complex**
   - Database queries to populate objects
   - Network calls to fetch data
   - Complex initialization logic
   - Resource-intensive computations

2. **You want to avoid subclasses of object creators (like Factory Method)**
   - Cloning is simpler than creating new instances
   - Reduces number of classes needed

3. **Objects differ only in state, not behavior**
   - Many similar objects with different configurations
   - Example: Game characters with different attributes

4. **You need to preserve object state**
   - Creating snapshots (Memento pattern uses this)
   - Undo/Redo functionality
   - Saving game states

5. **Runtime object creation based on dynamic classes**
   - Don't know concrete classes at compile time
   - Classes loaded dynamically

### Real-world scenarios:

- **Graphics editors**: Copying shapes, images, or design elements
- **Game development**: Spawning enemies, items, or NPCs from templates
- **Configuration management**: Cloning configuration objects
- **Database records**: Creating similar records with slight modifications
- **Document templates**: Creating documents from predefined templates
- **Cell division simulation**: Biological simulations

## Implementation Details

### Key Components:

1. **Prototype Interface**: Declares cloning method (usually `clone()`)
2. **Concrete Prototype**: Implements cloning method
3. **Client**: Creates new objects by asking prototype to clone itself
4. **Prototype Registry** (optional): Stores frequently used prototypes

### Code Example:
```java
// Prototype interface
public abstract class Shape implements Cloneable {
    protected String type;

    public abstract void draw();

    @Override
    public Shape clone() {
        try {
            return (Shape) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

// Concrete prototype
public class Circle extends Shape {
    private int radius;

    @Override
    public Circle clone() {
        return (Circle) super.clone();
    }
}

// Registry
public class ShapeRegistry {
    private Map<String, Shape> shapes = new HashMap<>();

    public void register(String key, Shape shape) {
        shapes.put(key, shape);
    }

    public Shape getShape(String key) {
        return shapes.get(key).clone();
    }
}

// Usage
Shape circle = registry.getShape("circle");
```

## Caveats and Considerations

### Disadvantages:

1. **Cloning Complex Objects Can Be Difficult**
   - Deep copy vs shallow copy issues
   - Circular references can cause problems
   - Need to handle all object dependencies

2. **Cloneable Interface Issues in Java**
   - `Cloneable` is a marker interface (no methods)
   - `clone()` is in `Object` class, not `Cloneable`
   - Protected access modifier on `clone()`
   - Throws `CloneNotSupportedException` (checked exception)

3. **Deep Copy Complexity**
   - Default `clone()` creates shallow copy
   - Must manually implement deep copy for nested objects
   - Can be error-prone

4. **Breaking Encapsulation**
   - Clone has access to private fields
   - Bypasses constructors
   - Can violate invariants if not careful

### Common Pitfalls:

1. **Shallow Copy Problem**
   ```java
   // Shallow copy - both objects share same list!
   public class Person implements Cloneable {
       private List<String> hobbies;

       public Person clone() {
           return (Person) super.clone();  // Shallow copy
       }
   }

   // Deep copy - each object has own list
   public Person clone() {
       Person cloned = (Person) super.clone();
       cloned.hobbies = new ArrayList<>(this.hobbies);
       return cloned;
   }
   ```

2. **Forgetting to Override clone()**
   - Must override in each subclass
   - Otherwise returns wrong type

3. **Not Handling CloneNotSupportedException**
   ```java
   // Bad
   public Shape clone() throws CloneNotSupportedException {
       return (Shape) super.clone();
   }

   // Good
   public Shape clone() {
       try {
           return (Shape) super.clone();
       } catch (CloneNotSupportedException e) {
           throw new AssertionError();  // Can't happen
       }
   }
   ```

4. **Cloning Singleton**
   - Don't clone singletons!
   - Prevents creating multiple instances

5. **Circular References**
   ```java
   class Node {
       Node next;
       public Node clone() {
           // Be careful with circular references!
       }
   }
   ```

### Best Practices:

1. **Choose Right Copy Type**
   - **Shallow copy**: When object contains only primitives/immutables
   - **Deep copy**: When object contains mutable references
   - **Custom copy**: Mix of shallow and deep based on needs

2. **Use Copy Constructor Alternative**
   ```java
   public class Person {
       private String name;
       private List<String> hobbies;

       // Copy constructor
       public Person(Person other) {
           this.name = other.name;
           this.hobbies = new ArrayList<>(other.hobbies);
       }
   }
   ```

3. **Use Static Factory Method**
   ```java
   public class Shape {
       public static Shape copy(Shape shape) {
           return shape.clone();
       }
   }
   ```

4. **Implement Copy Method Instead of Clone**
   ```java
   public interface Copyable<T> {
       T copy();
   }

   public class Circle implements Copyable<Circle> {
       public Circle copy() {
           Circle circle = new Circle();
           circle.radius = this.radius;
           return circle;
       }
   }
   ```

5. **Use Serialization for Deep Copy**
   ```java
   public static <T extends Serializable> T deepCopy(T object) {
       try {
           ByteArrayOutputStream bos = new ByteArrayOutputStream();
           ObjectOutputStream out = new ObjectOutputStream(bos);
           out.writeObject(object);

           ByteArrayInputStream bis =
               new ByteArrayInputStream(bos.toByteArray());
           ObjectInputStream in = new ObjectInputStream(bis);
           return (T) in.readObject();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }
   ```

6. **Document Cloning Behavior**
   ```java
   /**
    * Creates a deep copy of this person, including
    * a new copy of the hobbies list.
    */
   public Person clone() { ... }
   ```

7. **Consider Immutability**
   - Immutable objects don't need cloning
   - Can return `this` for immutable objects

8. **Use Prototype Registry**
   - Store commonly used prototypes
   - Reduces duplicate code
   - Centralized prototype management

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Factory Method** | Creates new objects vs. clones existing ones |
| **Abstract Factory** | Creates families of objects vs. clones single objects |
| **Builder** | Constructs step-by-step vs. instant clone |
| **Singleton** | Ensures one instance vs. creates many copies |

## Relationship with Other Patterns

- **Often used with Composite** to clone complex tree structures
- **Can be used with Memento** to save object states
- **Alternative to Abstract Factory** when product hierarchies are complex
- **Can complement Command pattern** for undo/redo functionality

## Alternatives to Cloneable

Many developers avoid `Cloneable` due to its issues:

1. **Copy Constructor**
   ```java
   public Person(Person other) { ... }
   ```

2. **Copy Factory Method**
   ```java
   public static Person newInstance(Person other) { ... }
   ```

3. **Custom Copy Interface**
   ```java
   interface Copyable<T> { T copy(); }
   ```

4. **Builder Pattern**
   - For complex objects with many fields

## Running the Example

```bash
cd java/creational/prototype
javac PrototypeDemo.java
java creational.prototype.PrototypeDemo
```

## Expected Output

The demo will show:
1. Cloning shapes from registry
2. Modifying cloned objects independently
3. Performance comparison between cloning and creating new objects
4. Difference between shallow copy and deep copy
5. Solutions to common copying problems
