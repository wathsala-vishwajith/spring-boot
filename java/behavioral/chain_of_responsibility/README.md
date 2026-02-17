# Chain of Responsibility Pattern

## Overview
The Chain of Responsibility pattern is a behavioral design pattern that lets you pass requests along a chain of handlers. Upon receiving a request, each handler decides either to process the request or to pass it to the next handler in the chain. This pattern decouples senders and receivers of requests.

Think of it like a support escalation system - a basic support agent handles simple questions, escalates medium issues to technical support, and critical issues go to management.

## When to Use

### Use Chain of Responsibility when:

1. **More than one object can handle a request, and the handler isn't known in advance**
   - Multiple potential handlers
   - Handler determined at runtime
   - Dynamic chain configuration

2. **You want to issue a request to one of several objects without specifying the receiver explicitly**
   - Loosely coupled senders and receivers
   - Sender doesn't know which receiver will handle it
   - Flexibility in assignment

3. **The set of objects that can handle a request should be specified dynamically**
   - Can modify chain at runtime
   - Add/remove handlers dynamically
   - Reconfigure processing order

4. **You want to avoid coupling the sender to the receiver**
   - Sender only knows about handler interface
   - No knowledge of concrete handlers
   - Better flexibility and maintainability

### Real-world scenarios:

- **Support ticket systems**: Basic → Technical → Management escalation
- **Logging frameworks**: Console → File → Email based on severity
- **Authentication/Authorization**: Multiple validation steps in sequence
- **Purchase approval workflow**: Team Lead → Manager → Director → CEO
- **Event handling**: UI event propagation (button → panel → window → application)
- **Middleware chains**: HTTP request processing (auth → logging → caching → handler)
- **Exception handling**: Try multiple handlers until one succeeds
- **Validation chains**: Multiple validators checking different aspects

## Implementation Details

### Key Components:

1. **Handler**: Interface for handling requests and accessing successor
2. **Concrete Handler**: Handles requests it's responsible for, forwards others
3. **Client**: Initiates request to a handler in the chain

### Structure:

```java
// Handler
abstract class Handler {
    protected Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    public abstract void handleRequest(Request request);
}

// Concrete Handlers
class ConcreteHandler1 extends Handler {
    public void handleRequest(Request request) {
        if (canHandle(request)) {
            // Handle it
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
}

// Usage
Handler h1 = new ConcreteHandler1();
Handler h2 = new ConcreteHandler2();
h1.setNext(h2);
h1.handleRequest(request);
```

### Classic Example - Support System:

```java
abstract class SupportHandler {
    protected SupportHandler next;

    public void setNext(SupportHandler next) {
        this.next = next;
    }

    public abstract void handleRequest(SupportRequest request);
}

class BasicSupport extends SupportHandler {
    public void handleRequest(SupportRequest request) {
        if (request.getPriority() == Priority.LOW) {
            System.out.println("Basic Support handling");
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
}

// Build chain
SupportHandler basic = new BasicSupport();
SupportHandler technical = new TechnicalSupport();
basic.setNext(technical);

// Use chain
basic.handleRequest(new SupportRequest("Help", Priority.LOW));
```

## Caveats and Considerations

### Disadvantages:

1. **Request might not be handled**
   - If no handler can process it
   - End of chain reached
   - Need to handle this case

2. **Debugging can be difficult**
   - Request path through chain not obvious
   - Hard to trace which handler processed request
   - Complex chains hard to understand

3. **Performance concerns**
   - Request might traverse entire chain
   - Each handler adds overhead
   - Can be slow for long chains

4. **Chain configuration complexity**
   - Need to ensure chain is properly set up
   - Easy to make mistakes in chain order
   - Broken chains cause issues

### Best Practices:

1. **Handle end-of-chain case**
   ```java
   public void handleRequest(Request request) {
       if (canHandle(request)) {
           process(request);
       } else if (next != null) {
           next.handleRequest(request);
       } else {
           throw new RuntimeException("No handler found");
       }
   }
   ```

2. **Make handlers immutable**
   ```java
   class Handler {
       private final Handler next;

       public Handler(Handler next) {
           this.next = next;
       }
   }
   ```

3. **Consider using functional style**
   ```java
   interface Handler {
       boolean handle(Request request);
   }

   // Chain using composition
   Handler chain = request -> {
       return handler1.handle(request) ||
              handler2.handle(request) ||
              handler3.handle(request);
   };
   ```

## Advantages

1. **Reduced coupling**: Sender doesn't know receiver
2. **Flexibility**: Easy to add/remove/reorder handlers
3. **Single Responsibility**: Each handler has one job
4. **Open/Closed Principle**: Add new handlers without changing existing code

## Running the Example

```bash
cd java/behavioral/chain_of_responsibility
javac ChainOfResponsibilityDemo.java
java behavioral.chain_of_responsibility.ChainOfResponsibilityDemo
```

## Expected Output

The demo shows:
1. Support system escalating requests based on priority
2. Logging system with different severity levels
3. Authentication chain with multiple validation steps
4. Purchase approval workflow with different authority levels
