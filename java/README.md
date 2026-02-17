# Design Patterns in Core Java

A comprehensive collection of **22 classic design patterns** from the Gang of Four (GoF) catalog, implemented in pure Java with real-world examples and detailed documentation.

## 📚 Table of Contents

- [Overview](#overview)
- [Pattern Categories](#pattern-categories)
- [Quick Reference](#quick-reference)
- [How to Use](#how-to-use)
- [Pattern Comparison](#pattern-comparison)
- [Resources](#resources)

## Overview

This repository contains complete implementations of all design patterns from the refactoring.guru catalog. Each pattern includes:

✅ **Complete Java implementations** with multiple examples
✅ **Comprehensive README** with usage guidelines and caveats
✅ **Real-world scenarios** demonstrating practical applications
✅ **Best practices** and common pitfalls
✅ **Comparison with related patterns**
✅ **Thread safety considerations** where applicable

## Pattern Categories

Design patterns are organized into three main categories based on their purpose:

### 🏗️ Creational Patterns (5)
Deal with object creation mechanisms, trying to create objects in a manner suitable to the situation.

### 🔧 Structural Patterns (7)
Deal with object composition, creating relationships between objects to form larger structures.

### 🎯 Behavioral Patterns (10)
Deal with object collaboration and the delegation of responsibilities between objects.

---

## Quick Reference

### 🏗️ Creational Patterns

| Pattern | Purpose | When to Use | Real-World Example |
|---------|---------|-------------|-------------------|
| **[Factory Method](creational/factory_method/)** | Creates objects without specifying exact class | When exact type is determined at runtime | Logistics (truck/ship delivery) |
| **[Abstract Factory](creational/abstract_factory/)** | Creates families of related objects | When you need consistent product families | UI themes (Windows/Mac/Linux) |
| **[Builder](creational/builder/)** | Constructs complex objects step-by-step | Objects with many optional parameters | House construction, SQL queries |
| **[Prototype](creational/prototype/)** | Creates objects by cloning existing ones | When object creation is expensive | Game character spawning |
| **[Singleton](creational/singleton/)** | Ensures only one instance exists | Global access to shared resource | Database connection, Logger |

### 🔧 Structural Patterns

| Pattern | Purpose | When to Use | Real-World Example |
|---------|---------|-------------|-------------------|
| **[Adapter](structural/adapter/)** | Makes incompatible interfaces work together | Integrating third-party libraries | Payment gateways, Media players |
| **[Bridge](structural/bridge/)** | Separates abstraction from implementation | When both can vary independently | Remote controls for devices |
| **[Composite](structural/composite/)** | Treats individual & composite objects uniformly | Tree structures, part-whole hierarchies | File systems, Organization charts |
| **[Decorator](structural/decorator/)** | Adds responsibilities dynamically | Flexible alternative to subclassing | Coffee with add-ons, Text formatting |
| **[Facade](structural/facade/)** | Provides simplified interface to complex system | Simplify complex subsystem | Home theater system |
| **[Flyweight](structural/flyweight/)** | Minimizes memory by sharing data | Many similar objects | Text editor characters, Particle systems |
| **[Proxy](structural/proxy/)** | Controls access to another object | Lazy loading, access control, caching | Virtual proxy, Protection proxy |

### 🎯 Behavioral Patterns

| Pattern | Purpose | When to Use | Real-World Example |
|---------|---------|-------------|-------------------|
| **[Chain of Responsibility](behavioral/chain_of_responsibility/)** | Passes requests along handler chain | Multiple objects can handle request | Support ticket escalation |
| **[Command](behavioral/command/)** | Encapsulates requests as objects | Undo/redo, queuing operations | Text editor operations |
| **[Iterator](behavioral/iterator/)** | Sequential access without exposing internals | Traverse collections uniformly | Collection traversal |
| **[Mediator](behavioral/mediator/)** | Centralizes complex communications | Reduce coupling between objects | Chat room, Air traffic control |
| **[Memento](behavioral/memento/)** | Captures and restores object state | Undo functionality, snapshots | Text editor undo, Game saves |
| **[Observer](behavioral/observer/)** | Notifies dependents of state changes | One-to-many dependency | Weather station, Event system |
| **[State](behavioral/state/)** | Changes behavior based on internal state | Object behavior varies by state | Document workflow, Vending machine |
| **[Strategy](behavioral/strategy/)** | Encapsulates interchangeable algorithms | Runtime algorithm selection | Payment methods, Sorting algorithms |
| **[Template Method](behavioral/template_method/)** | Defines algorithm skeleton | Let subclasses override specific steps | Data processing pipelines |
| **[Visitor](behavioral/visitor/)** | Separates algorithms from object structure | Add operations without modifying classes | Compiler AST operations |

---

## How to Use

### Running Individual Patterns

Each pattern has a demo class that can be run independently:

```bash
# Navigate to specific pattern directory
cd java/creational/singleton

# Compile the demo
javac SingletonDemo.java

# Run the demo
java creational.singleton.SingletonDemo
```

### Compile All Patterns

```bash
# From the java directory
find . -name "*.java" -type f > sources.txt
javac @sources.txt
```

### Pattern Selection Guide

#### Choose Based on Your Problem:

**Object Creation Problems → Creational Patterns**
- Too many constructor parameters? → **Builder**
- Need only one instance? → **Singleton**
- Don't know exact class at runtime? → **Factory Method**
- Need families of related objects? → **Abstract Factory**
- Object creation is expensive? → **Prototype**

**Interface/Structure Problems → Structural Patterns**
- Incompatible interfaces? → **Adapter**
- Need to add functionality dynamically? → **Decorator**
- Complex subsystem? → **Facade**
- Tree-like structure? → **Composite**
- Too many similar objects? → **Flyweight**
- Abstraction and implementation vary independently? → **Bridge**
- Need controlled access? → **Proxy**

**Behavior/Communication Problems → Behavioral Patterns**
- Multiple handlers for request? → **Chain of Responsibility**
- Need undo/redo? → **Command** or **Memento**
- Behavior changes with state? → **State**
- Need to swap algorithms? → **Strategy**
- One-to-many notification? → **Observer**
- Reduce coupling between communicating objects? → **Mediator**
- Traverse collection without exposing internals? → **Iterator**
- Define algorithm skeleton? → **Template Method**
- Add operations without modifying classes? → **Visitor**

---

## Pattern Comparison

### Common Confusions

#### **Factory Method vs Abstract Factory**
- **Factory Method**: Creates ONE product type using subclasses
- **Abstract Factory**: Creates FAMILIES of related products using composition

#### **Decorator vs Proxy**
- **Decorator**: Adds new functionality (same interface)
- **Proxy**: Controls access to object (same interface but different purpose)

#### **Adapter vs Bridge**
- **Adapter**: Retrofitted to make incompatible interfaces work
- **Bridge**: Designed upfront to separate abstraction from implementation

#### **Strategy vs State**
- **Strategy**: Client chooses algorithm explicitly
- **State**: Object changes behavior automatically based on state

#### **Composite vs Decorator**
- **Composite**: Part-whole hierarchy (tree structure)
- **Decorator**: Linear chain of responsibility for adding features

#### **Chain of Responsibility vs Decorator**
- **Chain of Responsibility**: Request flows through chain, one handler processes
- **Decorator**: All decorators process the request (wrapping)

---

## Pattern Relationships

### Patterns That Work Well Together

1. **Abstract Factory + Singleton**
   - Factory instances are often singletons

2. **Factory Method + Template Method**
   - Factory method often called within template method

3. **Builder + Composite**
   - Builder can construct complex composite trees

4. **Command + Memento**
   - Commands save state using mementos for undo

5. **Observer + Mediator**
   - Mediator can use observer to notify colleagues

6. **Decorator + Strategy**
   - Decorator for structure, Strategy for algorithms

7. **Proxy + Decorator**
   - Can be used together (e.g., caching proxy + logging decorator)

8. **Iterator + Composite**
   - Iterate over composite structures

9. **Visitor + Composite**
   - Perform operations on composite structures

---

## Best Practices

### 1. **Don't Overuse Patterns**
```java
// Bad - unnecessary pattern
class MyArrayList extends ArrayList {}  // Just use ArrayList!

// Good - pattern solves real problem
class DatabaseConnection {  // Singleton for shared resource
    private static DatabaseConnection instance;
}
```

### 2. **Understand the Trade-offs**
Every pattern has costs:
- More classes to maintain
- Increased complexity
- Potential performance overhead

Use patterns when benefits outweigh costs.

### 3. **Prefer Composition Over Inheritance**
Most patterns favor composition:
- Abstract Factory (composition)
- Strategy (composition)
- Decorator (composition)
- Bridge (composition)

### 4. **Keep It Simple (KISS)**
Start with simplest solution, refactor to pattern when needed.

### 5. **Consider Modern Alternatives**
- **Singleton** → Dependency Injection (Spring, Guice)
- **Strategy** → Lambda expressions (Java 8+)
- **Observer** → Reactive Streams (RxJava, Project Reactor)
- **Builder** → Lombok `@Builder` annotation

---

## Thread Safety

Patterns requiring thread-safety considerations:

| Pattern | Thread Safety Concern |
|---------|---------------------|
| **Singleton** | Critical - multiple threads can create multiple instances |
| **Object Pool** | Critical - concurrent access to pool |
| **Flyweight** | Important - shared state must be immutable |
| **Prototype** | Consider if cloning is thread-safe |
| **Observer** | Important if notifications cross threads |
| **Memento** | Important if state changes during save/restore |

---

## Performance Considerations

| Pattern | Performance Impact | Mitigation |
|---------|-------------------|------------|
| **Flyweight** | Reduces memory ✅ | Lookup overhead |
| **Proxy** | Adds indirection ⚠️ | Keep proxy lightweight |
| **Decorator** | Multiple wrapper layers ⚠️ | Limit decorator depth |
| **Chain of Responsibility** | May traverse entire chain ⚠️ | Order handlers by frequency |
| **Visitor** | Double dispatch ⚠️ | Acceptable for tree traversal |
| **Prototype** | Faster than new ✅ | Deep copy can be expensive |

---

## Testing Strategies

### Singleton Testing
```java
// Problem: Hard to test due to global state
public class UserService {
    private DatabaseConnection db = DatabaseConnection.getInstance();
}

// Solution: Dependency injection
public class UserService {
    private DatabaseConnection db;

    public UserService(DatabaseConnection db) {
        this.db = db;  // Can inject mock in tests
    }
}
```

### Strategy Testing
```java
// Easy to test - just test each strategy independently
@Test
public void testCreditCardPayment() {
    PaymentStrategy strategy = new CreditCardPayment();
    assertTrue(strategy.pay(100.0));
}
```

### Decorator Testing
```java
// Test decorators independently and in combination
@Test
public void testDecoratorChain() {
    Component plain = new PlainComponent();
    Component decorated = new DecoratorB(new DecoratorA(plain));
    assertEquals(expectedBehavior, decorated.operation());
}
```

---

## Resources

### Books
- **"Design Patterns: Elements of Reusable Object-Oriented Software"** - Gang of Four (GoF)
- **"Head First Design Patterns"** - Freeman & Freeman
- **"Effective Java"** - Joshua Bloch
- **"Refactoring to Patterns"** - Joshua Kerievsky

### Online Resources
- [Refactoring.Guru](https://refactoring.guru/design-patterns) - Interactive catalog
- [SourceMaking](https://sourcemaking.com/design_patterns) - Design patterns reference
- [Java Design Patterns](https://java-design-patterns.com/) - Java-specific implementations

### Video Courses
- **"Design Patterns in Java"** - Pluralsight
- **"Java Design Patterns"** - Udemy
- **"Design Patterns"** - LinkedIn Learning

---

## Contributing

Each pattern implementation includes:
- Source code with detailed comments
- Demo class showing usage
- README with comprehensive documentation
- Real-world examples
- Best practices and pitfalls

Feel free to:
- Add more real-world examples
- Improve documentation
- Add unit tests
- Suggest optimizations

---

## Summary

### Key Takeaways

1. **Design patterns are tools, not rules**
   - Use when they solve real problems
   - Don't force patterns into your code

2. **Understand the intent**
   - Know WHY a pattern exists
   - Know WHEN to apply it
   - Know the TRADE-OFFS

3. **Patterns work together**
   - Often combined to solve complex problems
   - Each has its place in your toolkit

4. **Keep learning**
   - Recognize patterns in existing code
   - Study open-source projects
   - Practice refactoring to patterns

5. **Modern Java features**
   - Lambdas can replace some patterns (Strategy, Command)
   - Streams use Iterator internally
   - Optional can replace Null Object pattern

---

## Pattern Statistics

- **Total Patterns**: 22
- **Creational**: 5 (23%)
- **Structural**: 7 (32%)
- **Behavioral**: 10 (45%)

**Most Commonly Used** (in modern Java applications):
1. Singleton (though DI is preferred)
2. Factory Method
3. Strategy
4. Observer
5. Decorator

**Most Underutilized** (but valuable):
1. Flyweight
2. Visitor
3. Memento
4. Mediator
5. Bridge

---

## License

These implementations are provided for educational purposes. Feel free to use, modify, and distribute as needed.

---

**Happy Learning! 🚀**

Remember: Patterns are meant to be understood and adapted, not blindly followed. Use them wisely!
