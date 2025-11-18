# Command Pattern

## Overview
The Command pattern is a behavioral design pattern that encapsulates a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations. It decouples the object that invokes the operation from the one that knows how to perform it.

Think of it like a restaurant order - the waiter (invoker) takes your order (command) and passes it to the kitchen (receiver), without needing to know how to cook.

## When to Use

### Use Command when:

1. **You want to parameterize objects with operations**
   - Callbacks in procedural languages
   - Configure objects with actions
   - Different objects, same interface

2. **You want to queue, specify, and execute requests at different times**
   - Request queue for later execution
   - Schedule commands
   - Batch processing

3. **You want to support undo/redo operations**
   - Store command history
   - Reverse operations
   - Transaction management

4. **You want to log changes for crash recovery**
   - Record operations
   - Replay operations
   - Audit trail

5. **You want to structure a system around high-level operations**
   - Built on primitive operations
   - Macro commands
   - Transaction-based systems

### Real-world scenarios:

- **Text editors**: Undo/redo operations (type, delete, format)
- **GUI buttons**: Encapsulate button actions
- **Database transactions**: Support rollback
- **Task scheduling**: Job queues, background tasks
- **Remote control**: Commands for different devices
- **Restaurant orders**: Waiter takes orders to kitchen
- **Smart home**: Macro commands (evening routine, morning routine)
- **Version control**: Commits, reverts
- **Game actions**: Move, attack, use item (with undo)

## Implementation Details

### Key Components:

1. **Command**: Interface for executing operations
2. **Concrete Command**: Binds receiver with action
3. **Receiver**: Knows how to perform the operation
4. **Invoker**: Asks command to carry out request
5. **Client**: Creates commands and sets receivers

### Structure:

```java
// Command interface
interface Command {
    void execute();
    void undo();
}

// Receiver
class Receiver {
    public void action() {
        System.out.println("Receiver: performing action");
    }
}

// Concrete Command
class ConcreteCommand implements Command {
    private Receiver receiver;

    public ConcreteCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    public void execute() {
        receiver.action();
    }

    public void undo() {
        // Reverse the action
    }
}

// Invoker
class Invoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        command.execute();
    }
}
```

## Advantages

1. **Decouples invoker from receiver**: Sender doesn't need to know about receiver
2. **Supports undo/redo**: Store command history
3. **Supports command queuing**: Execute commands later
4. **Supports logging**: Record all operations
5. **Composable**: Create macro commands from simple commands
6. **Open/Closed Principle**: Add new commands without changing existing code

## Running the Example

```bash
cd java/behavioral/command
javac CommandDemo.java
java behavioral.command.CommandDemo
```

## Expected Output

The demo shows:
1. Text editor with undo/redo functionality
2. Remote control operating different devices
3. Restaurant order system with command queuing
4. Smart home with macro commands
