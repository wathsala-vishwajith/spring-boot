# Iterator Pattern

## Overview
The Iterator pattern provides a way to access elements of a collection sequentially without exposing its underlying representation. It encapsulates the traversal logic and provides a uniform interface for iterating over different collections.

## When to Use
- Need to access collection elements without exposing internal structure
- Support multiple traversal methods for collections
- Provide uniform interface for traversing different collection types
- Implement lazy loading of collection elements

## Real-world Scenarios
- Java Collections (ArrayList, LinkedList, HashSet)
- Database result sets
- File system traversal
- Social network connections
- Menu navigation
- Playlist iteration

## Key Components
1. **Iterator**: Interface for accessing and traversing elements
2. **Concrete Iterator**: Implements Iterator interface
3. **Aggregate**: Interface for creating Iterator
4. **Concrete Aggregate**: Implements Aggregate interface

## Advantages
- Single Responsibility: Separate collection from traversal logic
- Open/Closed: Add new iterators without changing collection
- Parallel iteration: Multiple iterators on same collection
- Uniform interface: Same way to traverse different collections

## Running the Example
```bash
cd java/behavioral/iterator
javac IteratorDemo.java
java behavioral.iterator.IteratorDemo
```
