# Template Method Pattern

## Overview
The Template Method pattern defines the skeleton of an algorithm in a base class, letting subclasses override specific steps without changing the algorithm's structure. It's one of the most common patterns in frameworks.

## When to Use
- Common algorithm structure with varying details
- Avoid code duplication in similar classes
- Control extension points in frameworks
- Implement invariant parts once

## Real-world Scenarios
- Beverage preparation (tea, coffee)
- Data parsing (CSV, XML, JSON)
- Game frameworks (initialize, play, end)
- Testing frameworks (setup, test, teardown)
- Build systems (compile, link, package)

## Key Components
1. **Abstract Class**: Defines template method and abstract steps
2. **Concrete Class**: Implements abstract steps
3. **Template Method**: Defines algorithm skeleton (usually final)
4. **Hook Methods**: Optional steps that can be overridden

## Advantages
- Reuses common code
- Controls algorithm structure
- Easy to add new variants
- Follows Hollywood Principle ("Don't call us, we'll call you")

## Running the Example
```bash
cd java/behavioral/template_method
javac TemplateMethodDemo.java
java behavioral.template_method.TemplateMethodDemo
```
