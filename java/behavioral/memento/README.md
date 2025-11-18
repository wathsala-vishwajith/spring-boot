# Memento Pattern

## Overview
The Memento pattern captures and externalizes an object's internal state without violating encapsulation, so the object can be restored to this state later. It's like a save point in a video game.

## When to Use
- Need to save and restore object state
- Direct access to state would expose implementation
- Support undo/redo operations
- Need snapshots of object state

## Real-world Scenarios
- Text editor undo/redo
- Game save systems
- Database transactions
- Version control systems
- Browser history

## Key Components
1. **Memento**: Stores internal state of Originator
2. **Originator**: Creates memento and restores from it
3. **Caretaker**: Manages mementos (doesn't modify them)

## Advantages
- Preserves encapsulation
- Simplifies Originator
- Supports undo/redo
- Can save object history

## Running the Example
```bash
cd java/behavioral/memento
javac MementoDemo.java
java behavioral.memento.MementoDemo
```
