# Visitor Pattern

## Overview
The Visitor pattern lets you separate algorithms from the objects on which they operate. You can add new operations to existing object structures without modifying them. It's like a plugin architecture for operations.

## When to Use
- Need to perform operations on elements of complex structure
- Many distinct operations on objects
- Object structure rarely changes but operations often change
- Avoid polluting classes with unrelated operations

## Real-world Scenarios
- Compiler operations (type checking, code generation, optimization)
- Document processing (rendering, exporting, spell checking)
- Shopping cart calculations (tax, shipping, discounts)
- File system operations (search, size calculation, permissions)
- Shape operations (area, perimeter, rendering)

## Key Components
1. **Visitor**: Interface with visit methods for each element type
2. **Concrete Visitor**: Implements operations for each element
3. **Element**: Interface with accept method
4. **Concrete Element**: Implements accept to call visitor

## Advantages
- Open/Closed: Add new operations without modifying elements
- Single Responsibility: Related operations grouped in visitor
- Can accumulate state while visiting
- Can visit elements of different types

## Disadvantages
- Hard to add new element types
- Breaks encapsulation (visitor needs access to elements)
- Double dispatch complexity

## Running the Example
```bash
cd java/behavioral/visitor
javac VisitorDemo.java
java behavioral.visitor.VisitorDemo
```
