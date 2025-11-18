# Flyweight Pattern

## Overview
The Flyweight pattern is a structural design pattern that minimizes memory usage by sharing as much data as possible with similar objects. It achieves this by storing common state (intrinsic state) externally and sharing it among multiple objects, while keeping unique state (extrinsic state) separate.

Think of it like a text editor where instead of storing full formatting information for each character, you share character styles among all characters that use the same style.

## When to Use

### Use Flyweight when:

1. **Your application uses a large number of objects**
   - Creating many similar objects
   - Memory consumption is a concern
   - Most object state can be made extrinsic

2. **Most object state can be made extrinsic (external)**
   - Objects have many shared properties
   - Unique state can be computed or passed in
   - Shared state is immutable

3. **Many groups of objects can be replaced by relatively few shared objects**
   - Once extrinsic state is removed, many objects can share the same intrinsic state
   - Identity of objects isn't important
   - Objects are interchangeable

4. **The application doesn't depend on object identity**
   - Can't use == to compare flyweights
   - Objects are value objects, not entities
   - Shared instances are acceptable

### Real-world scenarios:

- **Text editors**: Share character formatting (font, size, style) among thousands of characters
- **Game development**: Share tree/building models in a forest or city (millions of trees, few types)
- **Graphics systems**: Share icons, sprites, textures across UI elements
- **Particle systems**: Share particle types (fire, smoke, water) among thousands of particles
- **Chess/Board games**: Share piece types (pawn, rook, knight) at different positions
- **String interning**: Java String pool shares identical strings
- **Database connection pools**: Share expensive database connections
- **Font rendering**: Share glyph shapes, vary only position and size

## Implementation Details

### Key Components:

1. **Flyweight**: Interface for flyweights that can receive and act on extrinsic state
2. **Concrete Flyweight**: Implements Flyweight, stores intrinsic (shared) state
3. **Flyweight Factory**: Creates and manages flyweight objects, ensures sharing
4. **Client**: Maintains references to flyweights and computes/stores extrinsic state

### Key Concepts:

- **Intrinsic State**: Shared, context-independent, stored in flyweight
- **Extrinsic State**: Unique, context-dependent, stored by client or passed as parameters

### Structure:

```java
// Flyweight interface
interface Flyweight {
    void operation(ExtrinsicState state);
}

// Concrete Flyweight - stores intrinsic state
class ConcreteFlyweight implements Flyweight {
    private final String intrinsicState;

    public ConcreteFlyweight(String intrinsicState) {
        this.intrinsicState = intrinsicState;
    }

    public void operation(ExtrinsicState state) {
        // Use both intrinsic and extrinsic state
    }
}

// Flyweight Factory
class FlyweightFactory {
    private static Map<String, Flyweight> flyweights = new HashMap<>();

    public static Flyweight getFlyweight(String key) {
        if (!flyweights.containsKey(key)) {
            flyweights.put(key, new ConcreteFlyweight(key));
        }
        return flyweights.get(key);
    }
}
```

### Classic Example - Forest:

```java
// Flyweight - TreeType (intrinsic state: name, color, texture)
class TreeType {
    private final String name;
    private final String color;
    private final String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    public void draw(int x, int y) {
        // x, y are extrinsic state
        System.out.println("Drawing " + name + " at (" + x + "," + y + ")");
    }
}

// Factory
class TreeFactory {
    private static Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + color + texture;
        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(name, color, texture));
        }
        return treeTypes.get(key);
    }
}

// Context - Tree (stores extrinsic state: x, y)
class Tree {
    private int x, y;
    private TreeType type;

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        type.draw(x, y);
    }
}

// Usage - Plant 1000 trees but only create 3 TreeType objects
Forest forest = new Forest();
for (int i = 0; i < 1000; i++) {
    TreeType type = TreeFactory.getTreeType("Oak", "Green", "Oak texture");
    forest.plantTree(new Tree(randomX(), randomY(), type));
}
```

## Caveats and Considerations

### Disadvantages:

1. **Increased Complexity**
   - Need to separate intrinsic and extrinsic state
   - More classes and indirection
   - Harder to understand and maintain
   ```java
   // Without Flyweight - simple but memory hungry
   class Tree {
       int x, y;
       String name, color, texture; // Duplicated for each tree
   }

   // With Flyweight - complex but memory efficient
   class Tree {
       int x, y;
       TreeType type; // Shared
   }
   class TreeType {
       String name, color, texture; // Shared
   }
   ```

2. **Runtime Costs**
   - Computing extrinsic state
   - Looking up flyweights from factory
   - May be slower than non-shared objects
   ```java
   // Lookup overhead
   TreeType type = TreeFactory.getTreeType("Oak", "Green", "texture");
   ```

3. **Not Always Effective**
   - If objects don't have much shared state
   - If number of objects is small
   - Overhead > memory savings

4. **Immutability Requirement**
   - Shared state must be immutable
   - Can't modify intrinsic state
   - Reduces flexibility

### Common Pitfalls:

1. **Putting Extrinsic State in Flyweight**
   ```java
   // Bad - storing position in flyweight (should be extrinsic)
   class TreeType {
       private int x, y; // Don't store in flyweight!
       private String name;
   }

   // Good - position is extrinsic
   class TreeType {
       private final String name; // Only intrinsic state
   }
   class Tree {
       private int x, y; // Extrinsic state in context
       private TreeType type;
   }
   ```

2. **Making Flyweights Mutable**
   ```java
   // Bad - mutable flyweight
   class TreeType {
       private String color; // Not final

       public void setColor(String color) {
           this.color = color; // Affects all trees!
       }
   }

   // Good - immutable flyweight
   class TreeType {
       private final String color;

       public TreeType(String color) {
           this.color = color;
       }
       // No setters
   }
   ```

3. **Not Using Factory**
   ```java
   // Bad - creating flyweights directly
   TreeType type = new TreeType("Oak"); // Creates duplicates!

   // Good - using factory ensures sharing
   TreeType type = TreeFactory.getTreeType("Oak");
   ```

4. **Premature Optimization**
   ```java
   // Bad - using Flyweight when not needed
   class Button {
       ButtonStyle style; // Only 5 buttons, no need for Flyweight
   }

   // Good - simple approach when appropriate
   class Button {
       String color, font; // Simple is better here
   }
   ```

5. **Relying on Object Identity**
   ```java
   // Bad - comparing flyweights by identity
   if (tree1.getType() == tree2.getType()) { // May fail

   // Good - compare by value
   if (tree1.getType().equals(tree2.getType())) {
   ```

### Best Practices:

1. **Clearly Separate Intrinsic and Extrinsic State**
   ```java
   // Intrinsic (shared): Type information
   class TreeType {
       private final String name;
       private final String color;
       private final String texture;
   }

   // Extrinsic (unique): Position information
   class Tree {
       private int x, y;
       private TreeType type;
   }
   ```

2. **Make Flyweights Immutable**
   ```java
   class TreeType {
       private final String name;
       private final String color;

       public TreeType(String name, String color) {
           this.name = name;
           this.color = color;
       }

       // Only getters, no setters
       public String getName() { return name; }
   }
   ```

3. **Use Factory Pattern**
   ```java
   class FlyweightFactory {
       private static final Map<String, Flyweight> pool = new HashMap<>();

       public static Flyweight getFlyweight(String key) {
           if (!pool.containsKey(key)) {
               pool.put(key, new ConcreteFlyweight(key));
           }
           return pool.get(key);
       }
   }
   ```

4. **Consider Thread Safety**
   ```java
   class FlyweightFactory {
       private static final ConcurrentHashMap<String, Flyweight> pool =
           new ConcurrentHashMap<>();

       public static Flyweight getFlyweight(String key) {
           return pool.computeIfAbsent(key, k -> new ConcreteFlyweight(k));
       }
   }
   ```

5. **Monitor Memory Usage**
   ```java
   class FlyweightFactory {
       public static int getFlyweightCount() {
           return pool.size();
       }

       public static void printStatistics() {
           System.out.println("Flyweights created: " + pool.size());
       }
   }
   ```

6. **Document State Separation**
   ```java
   /**
    * TreeType is a flyweight storing intrinsic state.
    *
    * Intrinsic state (shared):
    * - name: Type of tree (Oak, Pine, etc.)
    * - color: Tree color
    * - texture: Visual texture
    *
    * Extrinsic state (stored in Tree):
    * - x, y: Position in forest
    */
   class TreeType {
       // ...
   }
   ```

7. **Use Weak References for Cache Management**
   ```java
   class FlyweightFactory {
       private static final Map<String, WeakReference<Flyweight>> pool =
           new HashMap<>();

       public static Flyweight getFlyweight(String key) {
           WeakReference<Flyweight> ref = pool.get(key);
           Flyweight flyweight = (ref != null) ? ref.get() : null;

           if (flyweight == null) {
               flyweight = new ConcreteFlyweight(key);
               pool.put(key, new WeakReference<>(flyweight));
           }

           return flyweight;
       }
   }
   ```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Singleton** | One instance total vs. one instance per intrinsic state |
| **Prototype** | Creates copies vs. shares instances |
| **Object Pool** | Reuses objects vs. shares objects |
| **Facade** | Simplifies interface vs. reduces memory |

### Flyweight vs Singleton:
- **Flyweight**: Multiple shared instances (one per intrinsic state)
- **Singleton**: Single instance for entire class
- **Flyweight**: About memory optimization
- **Singleton**: About controlled access

### Flyweight vs Object Pool:
- **Flyweight**: Objects are logically distinct but share state
- **Object Pool**: Objects are reused (same physical object)
- **Flyweight**: Objects exist simultaneously
- **Object Pool**: Objects used one at a time

### Flyweight vs Prototype:
- **Flyweight**: Shares instances
- **Prototype**: Clones instances
- **Flyweight**: Reduces memory by sharing
- **Prototype**: Reduces creation cost by cloning

## Relationship with Other Patterns

- Often combined with **Composite** to implement shared leaf nodes
- **Factory** is essential for managing flyweight instances
- **State** and **Strategy** objects can be flyweights
- Can use **Singleton** to implement flyweight factory
- **Prototype** is opposite approach (copy vs share)

## Real-world Examples in Java

1. **String Pool (Interning)**
   ```java
   String s1 = "Hello";  // Stored in string pool
   String s2 = "Hello";  // Reuses same object
   // s1 == s2 is true (same object)

   String s3 = new String("Hello").intern(); // Add to pool
   ```

2. **Integer Cache**
   ```java
   Integer a = 127;
   Integer b = 127;
   // a == b is true (cached for -128 to 127)

   Integer c = 128;
   Integer d = 128;
   // c == d is false (not cached)
   ```

3. **Boolean Constants**
   ```java
   Boolean.TRUE  // Shared instance
   Boolean.FALSE // Shared instance
   ```

4. **Enum Constants**
   ```java
   enum Color { RED, GREEN, BLUE }
   // Only one instance of each constant
   ```

## Advantages

1. **Reduces Memory Consumption**
   - Share common data
   - Fewer objects created
   - Significant savings with many similar objects

2. **Improves Performance**
   - Less garbage collection
   - Better cache locality
   - Faster object access

3. **Centralizes State Management**
   - Shared state in one place
   - Easier to update common properties
   - Better consistency

## When NOT to Use

1. **Few objects**
   - Overhead not justified
   - Simple approach is clearer

2. **Objects have little shared state**
   - No memory benefit
   - Added complexity for nothing

3. **Objects need to be mutable**
   - Flyweights must be immutable
   - Changing shared state affects all objects

4. **Object identity is important**
   - Can't use == for comparison
   - Need unique object instances

## Performance Considerations

```java
// Without Flyweight
// 1,000,000 trees × 100 bytes = 100 MB

// With Flyweight
// 1,000,000 trees × 20 bytes (x,y,reference) +
// 10 tree types × 100 bytes = 20 MB + 1 KB
// Memory saved: ~80%
```

## Running the Example

```bash
cd java/structural/flyweight
javac FlyweightDemo.java
java structural.flyweight.FlyweightDemo
```

## Expected Output

The demo will show:
1. Text editor sharing character styles among many characters
2. Forest with 1000 trees sharing only 3 tree types
3. Particle system with 200 particles sharing only 2 particle types
4. Chess game showing how piece types are shared
5. Memory usage statistics showing the efficiency gains
