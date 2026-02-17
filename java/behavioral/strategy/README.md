# Strategy Pattern

## Overview
The Strategy pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

## When to Use
- Multiple related classes differ only in behavior
- Need different variants of an algorithm
- Algorithm uses data clients shouldn't know about
- Class has many conditional statements

## Real-world Scenarios
- Payment methods (credit card, PayPal, Bitcoin)
- Navigation routes (car, walking, public transport)
- Sorting algorithms (bubble, quick, merge)
- Compression algorithms (ZIP, RAR, 7Z)
- Discount calculations
- File export formats (PDF, Excel, CSV)

## Key Components
1. **Strategy**: Interface for algorithm
2. **Concrete Strategies**: Different algorithm implementations
3. **Context**: Uses a Strategy

## Advantages
- Eliminates conditional statements
- Provides choice of implementations
- Easy to add new strategies
- Encapsulates algorithms

## Running the Example
```bash
cd java/behavioral/strategy
javac StrategyDemo.java
java behavioral.strategy.StrategyDemo
```
