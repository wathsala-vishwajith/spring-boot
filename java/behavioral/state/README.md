# State Pattern

## Overview
The State pattern allows an object to alter its behavior when its internal state changes. The object will appear to change its class. It's like a finite state machine implemented in OOP.

## When to Use
- Object behavior depends on its state
- Operations have large conditional statements
- State transitions are well-defined
- Avoid monolithic conditional logic

## Real-world Scenarios
- Document workflow (draft, review, published)
- Vending machines (idle, selected, dispensing)
- TCP connections (established, listening, closed)
- Player states in games (standing, jumping, running)
- Order processing (new, paid, shipped, delivered)

## Key Components
1. **Context**: Maintains current state, delegates requests
2. **State**: Interface for state-specific behavior
3. **Concrete States**: Implement behavior for specific states

## Advantages
- Organizes state-specific code
- Makes state transitions explicit
- Eliminates large conditionals
- Easy to add new states

## Running the Example
```bash
cd java/behavioral/state
javac StateDemo.java
java behavioral.state.StateDemo
```
