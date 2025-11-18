# Observer Pattern

## Overview
The Observer pattern defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically. Also known as Publish-Subscribe pattern.

## When to Use
- One object changes and many others need to update
- Object should notify others without knowing who they are
- Loosely coupled notification system needed
- Event-driven systems

## Real-world Scenarios
- Weather monitoring systems
- Stock market tickers
- News subscriptions
- Social media notifications
- Event listeners in GUI
- MVC pattern (Model notifies View)

## Key Components
1. **Subject**: Maintains observers, notifies them of changes
2. **Observer**: Interface for objects that should be notified
3. **Concrete Subject**: Stores state, sends notifications
4. **Concrete Observer**: Implements update interface

## Advantages
- Loose coupling between subject and observers
- Dynamic relationships (add/remove at runtime)
- Broadcast communication
- Supports Open/Closed Principle

## Running the Example
```bash
cd java/behavioral/observer
javac ObserverDemo.java
java behavioral.observer.ObserverDemo
```
