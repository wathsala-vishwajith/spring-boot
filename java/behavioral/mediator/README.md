# Mediator Pattern

## Overview
The Mediator pattern defines an object that encapsulates how a set of objects interact. It promotes loose coupling by keeping objects from referring to each other explicitly, and lets you vary their interaction independently.

## When to Use
- Set of objects communicate in complex ways
- Reusing objects difficult due to dependencies
- Behavior distributed between classes should be customizable
- Too many relationships between objects

## Real-world Scenarios
- Chat rooms (users communicate through mediator)
- Air traffic control (coordinates aircraft)
- GUI dialogs (widgets communicate through dialog)
- MVC controller (mediates model and view)

## Key Components
1. **Mediator**: Interface for communicating with colleagues
2. **Concrete Mediator**: Coordinates colleague objects
3. **Colleague**: Objects that communicate through mediator

## Advantages
- Reduces coupling between colleagues
- Centralizes control logic
- Simplifies object protocols
- Easier to understand object relationships

## Running the Example
```bash
cd java/behavioral/mediator
javac MediatorDemo.java
java behavioral.mediator.MediatorDemo
```
