# Composite Pattern

## Overview
The Composite pattern is a structural design pattern that lets you compose objects into tree structures to represent part-whole hierarchies. It allows clients to treat individual objects and compositions of objects uniformly through a common interface.

Think of it like a file system where both files and folders implement the same interface, allowing you to perform operations on both without knowing which one you're dealing with.

## When to Use

### Use Composite when:

1. **You want to represent part-whole hierarchies of objects**
   - Tree-like structures
   - Nested components
   - Recursive composition

2. **You want clients to ignore the difference between individual and composite objects**
   - Treat leaves and composites uniformly
   - Simplify client code
   - Polymorphic handling of components

3. **You want to implement tree structures**
   - File systems (files and folders)
   - Organization charts (employees and departments)
   - GUI components (widgets and containers)
   - Graphics (shapes and groups)

4. **Operations need to be performed recursively on a hierarchy**
   - Calculate total size of folders
   - Draw nested graphics
   - Execute menu commands at any level

### Real-world scenarios:

- **File systems**: Files and directories (folders contain files and other folders)
- **Organization charts**: Employees and departments (departments contain employees and sub-departments)
- **GUI frameworks**: Swing/AWT containers and components
- **Graphics editors**: Individual shapes and groups of shapes
- **Menu systems**: Menu items and sub-menus
- **Document structures**: Sections, paragraphs, and text (Word, HTML DOM)
- **E-commerce**: Products and product bundles
- **Task management**: Tasks and sub-tasks in project management

## Implementation Details

### Key Components:

1. **Component**: Common interface for both leaf and composite objects
2. **Leaf**: Represents leaf objects (no children)
3. **Composite**: Stores child components and implements child-related operations
4. **Client**: Manipulates objects through the Component interface

### Structure:

```java
// Component interface
interface Component {
    void operation();
}

// Leaf - no children
class Leaf implements Component {
    public void operation() {
        // Leaf-specific behavior
    }
}

// Composite - has children
class Composite implements Component {
    private List<Component> children = new ArrayList<>();

    public void add(Component component) {
        children.add(component);
    }

    public void remove(Component component) {
        children.remove(component);
    }

    public void operation() {
        // Delegate to children
        for (Component child : children) {
            child.operation();
        }
    }
}
```

### Classic Example - File System:

```java
interface FileSystemComponent {
    void display();
    int getSize();
}

class File implements FileSystemComponent {
    private String name;
    private int size;

    public void display() {
        System.out.println("File: " + name);
    }

    public int getSize() {
        return size;
    }
}

class Folder implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> children = new ArrayList<>();

    public void add(FileSystemComponent component) {
        children.add(component);
    }

    public void display() {
        System.out.println("Folder: " + name);
        for (FileSystemComponent child : children) {
            child.display();
        }
    }

    public int getSize() {
        int total = 0;
        for (FileSystemComponent child : children) {
            total += child.getSize();
        }
        return total;
    }
}

// Usage
Folder root = new Folder("root");
root.add(new File("file1.txt", 100));
root.add(new File("file2.txt", 200));
System.out.println("Total size: " + root.getSize()); // 300
```

## Caveats and Considerations

### Disadvantages:

1. **Can Make Design Overly General**
   - Common interface might not fit all components
   - Forced to implement methods that don't make sense
   ```java
   // Bad - File shouldn't have add/remove
   interface Component {
       void add(Component c);    // Doesn't make sense for files
       void remove(Component c); // Doesn't make sense for files
   }

   // Better - Use separate interfaces or throw exceptions
   class File implements Component {
       public void add(Component c) {
           throw new UnsupportedOperationException();
       }
   }
   ```

2. **Difficult to Restrict Component Types**
   - Hard to enforce that composite only contains specific types
   - Type safety issues
   ```java
   // Can't easily restrict Folder to only contain Files
   Folder folder = new Folder("docs");
   folder.add(new File("doc.txt", 100));     // OK
   folder.add(new AudioFile("song.mp3", 500)); // Also OK, but maybe not wanted
   ```

3. **May Complicate Simple Designs**
   - Overkill for flat structures
   - Adds unnecessary complexity when hierarchy isn't needed

4. **Performance Overhead**
   - Recursive operations can be expensive
   - Deep hierarchies may cause stack overflow
   - Consider iterative alternatives for deep trees

### Common Pitfalls:

1. **Violating Leaf-Composite Distinction**
   ```java
   // Bad - Leaf trying to act as composite
   class File implements FileSystemComponent {
       private List<FileSystemComponent> children; // Files shouldn't have children!
   }

   // Good - Clear distinction
   class File implements FileSystemComponent {
       // No children-related code
   }
   ```

2. **Not Handling Cycles**
   ```java
   // Bad - allows cycles
   Folder parent = new Folder("parent");
   Folder child = new Folder("child");
   parent.add(child);
   child.add(parent); // Cycle! Will cause infinite recursion

   // Good - Check for cycles
   public void add(Component component) {
       if (isAncestor(component)) {
           throw new IllegalArgumentException("Cannot add ancestor");
       }
       children.add(component);
   }
   ```

3. **Inefficient Parent References**
   ```java
   // Sometimes you need parent references
   interface Component {
       void setParent(Component parent);
       Component getParent();
   }

   // Update when adding/removing
   public void add(Component component) {
       component.setParent(this);
       children.add(component);
   }
   ```

4. **Not Using Design for Type Safety**
   ```java
   // Bad - runtime checks
   public void addImage(Component c) {
       if (!(c instanceof Image)) {
           throw new IllegalArgumentException();
       }
       children.add(c);
   }

   // Better - use generics
   class ImageComposite implements Component {
       private List<Image> images = new ArrayList<>();
       public void add(Image image) {
           images.add(image);
       }
   }
   ```

5. **Forgetting to Delegate in Composite**
   ```java
   // Bad - not delegating to children
   class Folder implements FileSystemComponent {
       public void display() {
           System.out.println("Folder");
           // Forgot to display children!
       }
   }

   // Good - delegate to all children
   class Folder implements FileSystemComponent {
       public void display() {
           System.out.println("Folder");
           for (Component child : children) {
               child.display(); // Delegate
           }
       }
   }
   ```

### Best Practices:

1. **Define Clear Component Interface**
   ```java
   // Good - focused interface
   interface Component {
       void operation();
       String getName();
   }

   // Composite-specific operations in separate interface
   interface Composite extends Component {
       void add(Component c);
       void remove(Component c);
       List<Component> getChildren();
   }
   ```

2. **Use Type-Safe Collections When Possible**
   ```java
   // If composites should only contain specific types
   class DocumentSection {
       private List<Paragraph> paragraphs = new ArrayList<>();
       // Type-safe: can only add Paragraphs
   }
   ```

3. **Implement Child Management Carefully**
   ```java
   public void add(Component component) {
       if (component == null) {
           throw new IllegalArgumentException("Cannot add null");
       }
       if (component == this) {
           throw new IllegalArgumentException("Cannot add self");
       }
       children.add(component);
   }

   public void remove(Component component) {
       children.remove(component);
   }
   ```

4. **Consider Caching for Expensive Operations**
   ```java
   class Folder implements FileSystemComponent {
       private Integer cachedSize = null;

       public int getSize() {
           if (cachedSize == null) {
               cachedSize = calculateSize();
           }
           return cachedSize;
       }

       public void add(Component c) {
           children.add(c);
           cachedSize = null; // Invalidate cache
       }
   }
   ```

5. **Use Iterator for Traversal**
   ```java
   interface Component {
       Iterator<Component> createIterator();
   }

   class Composite implements Component {
       public Iterator<Component> createIterator() {
           return new CompositeIterator(children.iterator());
       }
   }
   ```

6. **Handle Operations That Don't Apply**
   ```java
   // Leaf that doesn't support child operations
   class File implements FileSystemComponent {
       public void add(FileSystemComponent c) {
           throw new UnsupportedOperationException(
               "Files cannot contain children");
       }
   }
   ```

7. **Document Composite Structure**
   ```java
   /**
    * FileSystemComponent represents both files and folders.
    *
    * Leaf: File - represents individual files
    * Composite: Folder - can contain files and other folders
    *
    * Clients can treat files and folders uniformly.
    */
   interface FileSystemComponent {
       void display();
   }
   ```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Decorator** | Adds responsibilities vs. represents hierarchies |
| **Flyweight** | Shares objects to save memory vs. composes tree structures |
| **Chain of Responsibility** | Linear chain vs. tree structure |
| **Iterator** | Traverses structure vs. defines structure |

### Composite vs Decorator:
- **Composite**: Focuses on representing tree hierarchies (structural)
- **Decorator**: Focuses on adding responsibilities dynamically (behavioral)
- **Composite**: Many children, recursive composition
- **Decorator**: Typically one component being wrapped

### Composite vs Flyweight:
- **Composite**: Creates tree structures
- **Flyweight**: Shares leaf nodes to reduce memory
- Can be combined: Use Flyweight for shared leaf nodes in Composite tree

## Relationship with Other Patterns

- Often used with **Iterator** to traverse composite structures
- **Visitor** can be used to perform operations on Composite structures
- **Decorator** is often used with Composite
- Leaf nodes can be **Flyweights** to save memory
- **Chain of Responsibility** can use Composite to organize chains
- **Builder** can construct complex Composite trees

## Real-world Examples in Java

1. **Swing/AWT Components**
   ```java
   // Container is composite, Button is leaf
   JPanel panel = new JPanel();
   panel.add(new JButton("OK"));
   panel.add(new JLabel("Text"));
   // Both treated uniformly as Component
   ```

2. **DOM (Document Object Model)**
   ```java
   // Element nodes can contain other elements (composite)
   // Text nodes are leaves
   Element div = document.createElement("div");
   div.appendChild(document.createTextNode("Hello"));
   ```

3. **Java Collections**
   ```java
   // Collections can contain other collections
   List<Object> composite = new ArrayList<>();
   composite.add("string"); // Leaf
   composite.add(new ArrayList<>()); // Composite
   ```

4. **Graphics2D**
   ```java
   // Can group shapes together
   Shape shape1 = new Rectangle(...);
   Shape shape2 = new Ellipse(...);
   Area area = new Area(shape1);
   area.add(new Area(shape2)); // Composite
   ```

## Advantages

1. **Simplified Client Code**
   - Treat individual objects and compositions uniformly
   - No need for type checking

2. **Easy to Add New Components**
   - Open/Closed Principle
   - New leaf or composite types easily added

3. **Flexible Structure**
   - Can create complex tree structures
   - Easy to add nodes at any level

4. **Recursive Composition**
   - Natural representation of hierarchical structures
   - Operations propagate through tree automatically

## When NOT to Use

1. **Flat structures**
   - No hierarchy needed
   - Simple collection sufficient

2. **Components are too different**
   - Can't define meaningful common interface
   - Forced to implement inapplicable methods

3. **Type restrictions needed**
   - Need strict control over what goes where
   - Hard to enforce with Composite

4. **Performance critical with deep trees**
   - Recursive operations can be slow
   - Consider alternative data structures

## Running the Example

```bash
cd java/structural/composite
javac CompositeDemo.java
java structural.composite.CompositeDemo
```

## Expected Output

The demo will show:
1. File system with files and folders (calculating sizes, displaying hierarchy)
2. Organization structure with employees and departments (salary budgets)
3. Graphics system with individual shapes and groups
4. Menu system with nested menus and menu items
5. How individual objects and compositions are treated uniformly
