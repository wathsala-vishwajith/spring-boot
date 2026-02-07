# SOLID Principles - Comprehensive Java Examples

This project demonstrates all five SOLID principles with real-world examples, including both "bad" (violating) and "good" (following) implementations, complete with unit tests.

## Table of Contents
- [Overview](#overview)
- [Project Structure](#project-structure)
- [SOLID Principles](#solid-principles)
  - [Single Responsibility Principle (SRP)](#1-single-responsibility-principle-srp)
  - [Open/Closed Principle (OCP)](#2-openclosed-principle-ocp)
  - [Liskov Substitution Principle (LSP)](#3-liskov-substitution-principle-lsp)
  - [Interface Segregation Principle (ISP)](#4-interface-segregation-principle-isp)
  - [Dependency Inversion Principle (DIP)](#5-dependency-inversion-principle-dip)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

## Overview

**SOLID** is an acronym for five design principles that help create more maintainable, flexible, and scalable object-oriented software:

- **S**: Single Responsibility Principle
- **O**: Open/Closed Principle
- **L**: Liskov Substitution Principle
- **I**: Interface Segregation Principle
- **D**: Dependency Inversion Principle

## Project Structure

```
solid-principles/
├── src/
│   ├── main/java/com/solid/
│   │   ├── srp/          # Single Responsibility Principle
│   │   │   ├── bad/      # Violating examples
│   │   │   └── good/     # Following examples
│   │   ├── ocp/          # Open/Closed Principle
│   │   ├── lsp/          # Liskov Substitution Principle
│   │   ├── isp/          # Interface Segregation Principle
│   │   └── dip/          # Dependency Inversion Principle
│   └── test/java/com/solid/
│       ├── srp/SRPTest.java
│       ├── ocp/OCPTest.java
│       ├── lsp/LSPTest.java
│       ├── isp/ISPTest.java
│       └── dip/DIPTest.java
├── pom.xml
└── README.md
```

## SOLID Principles

### 1. Single Responsibility Principle (SRP)

> **"A class should have one, and only one, reason to change."**

#### What it means:
Each class should have a single responsibility or purpose. If a class has multiple responsibilities, changes to one responsibility might affect others, making the code fragile and hard to maintain.

#### When to apply:
- When you find a class doing too many things (data management, business logic, persistence, notifications, etc.)
- When a class has methods that operate on completely different concerns
- When changes to one feature require modifying a class that handles unrelated features
- When writing new classes - always ask "What is the ONE thing this class is responsible for?"

#### Example in this project:

**Bad Example** (`srp/bad/Invoice.java`):
```java
// Invoice class does EVERYTHING:
// - Manages invoice data
// - Calculates totals
// - Saves to database
// - Sends emails
// - Generates reports
```

**Good Example** (`srp/good/`):
```java
// Separated into focused classes:
Invoice.java              // ONLY manages invoice data
InvoiceRepository.java    // ONLY handles persistence
InvoiceEmailService.java  // ONLY sends emails
InvoiceReportGenerator.java // ONLY generates reports
```

#### Benefits:
- **Easier to understand**: Each class has a clear, single purpose
- **Easier to test**: Can test each responsibility independently
- **Easier to maintain**: Changes to one responsibility don't affect others
- **Better reusability**: Focused classes can be reused in different contexts

#### Real-world scenarios:
- Separating UI logic from business logic
- Separating data access from business rules
- Separating validation from processing
- Separating logging/monitoring from core functionality

---

### 2. Open/Closed Principle (OCP)

> **"Software entities should be open for extension but closed for modification."**

#### What it means:
You should be able to add new functionality without changing existing code. Use abstractions (interfaces, abstract classes) to allow new implementations without modifying existing ones.

#### When to apply:
- When you find yourself adding if/else or switch statements for every new type
- When adding new features requires modifying existing, working code
- When you need to support multiple variants of similar behavior
- When designing plugin architectures or extensible frameworks
- When different implementations share a common interface but vary in details

#### Example in this project:

**Bad Example** (`ocp/bad/PaymentProcessor.java`):
```java
// Must modify this class to add new payment methods
public void processPayment(String paymentType, double amount) {
    if (paymentType.equals("CREDIT_CARD")) {
        // ...
    } else if (paymentType.equals("PAYPAL")) {
        // ...
    } else if (paymentType.equals("BITCOIN")) {
        // ... must add more if/else for new types!
    }
}
```

**Good Example** (`ocp/good/`):
```java
// PaymentMethod interface - closed for modification
public interface PaymentMethod {
    void processPayment(double amount);
}

// New payment methods extend without modifying existing code
public class CreditCardPayment implements PaymentMethod { ... }
public class PayPalPayment implements PaymentMethod { ... }
public class BitcoinPayment implements PaymentMethod { ... }
// Add new payment methods without changing PaymentProcessor!
```

#### Benefits:
- **Reduced risk**: Don't touch working code when adding features
- **Better scalability**: Easy to add new implementations
- **Improved testability**: Test new implementations independently
- **Cleaner code**: No sprawling if/else chains

#### Real-world scenarios:
- Payment gateways (as shown)
- File format handlers (PDF, Excel, CSV)
- Notification channels (Email, SMS, Push)
- Authentication strategies (OAuth, SAML, JWT)
- Discount/pricing strategies

---

### 3. Liskov Substitution Principle (LSP)

> **"Objects of a superclass should be replaceable with objects of a subclass without breaking the application."**

#### What it means:
Derived classes must be substitutable for their base classes. If class B is a subtype of class A, you should be able to replace A with B without disrupting the behavior of the program.

#### When to apply:
- When designing inheritance hierarchies
- When a subclass throws exceptions not thrown by the parent
- When a subclass has methods that don't make sense for the abstraction
- When you're tempted to throw `UnsupportedOperationException` in a subclass
- When overriding methods changes the expected behavior significantly

#### Example in this project:

**Bad Example** (`lsp/bad/`):
```java
// Bird class assumes all birds can fly
public abstract class Bird {
    public abstract void fly();
}

// Penguin violates LSP - cannot substitute for Bird!
public class Penguin extends Bird {
    public void fly() {
        throw new UnsupportedOperationException("Penguins cannot fly!");
    }
}

// This breaks when encountering a Penguin:
public void makeBirdsFly(List<Bird> birds) {
    for (Bird bird : birds) {
        bird.fly(); // Crashes on Penguin!
    }
}
```

**Good Example** (`lsp/good/`):
```java
// Base class with only common behavior
public abstract class Bird {
    public abstract void makeSound();
}

// Separate abstractions for different capabilities
public abstract class FlyingBird extends Bird {
    public abstract void fly();
}

public abstract class SwimmingBird extends Bird {
    public abstract void swim();
}

// Now each bird extends the appropriate abstraction
public class Sparrow extends FlyingBird { ... }
public class Penguin extends SwimmingBird { ... }

// Methods work with the appropriate level of abstraction
public void makeFlyingBirdsFly(List<FlyingBird> birds) {
    for (FlyingBird bird : birds) {
        bird.fly(); // Always works - LSP satisfied!
    }
}
```

#### Benefits:
- **Predictable behavior**: Subclasses behave as expected
- **Safer polymorphism**: Can use polymorphism without surprises
- **Better design**: Forces proper abstraction design
- **Compile-time safety**: Type system catches misuse

#### Real-world scenarios:
- Collection hierarchies (List, Set, Map)
- Shape hierarchies (don't force 3D methods on 2D shapes)
- User types (Employee, Contractor, Partner - different capabilities)
- Vehicle types (Car, Boat, Plane - different movement capabilities)

---

### 4. Interface Segregation Principle (ISP)

> **"No client should be forced to depend on methods it does not use."**

#### What it means:
Don't force classes to implement interfaces they don't need. Instead of one "fat" interface, create multiple smaller, specific interfaces so clients only implement what they need.

#### When to apply:
- When you have interfaces with many methods
- When implementing classes throw `UnsupportedOperationException` for some methods
- When different clients use different subsets of an interface
- When adding a method to an interface would break many implementations
- When creating APIs or libraries that others will implement

#### Example in this project:

**Bad Example** (`isp/bad/Worker.java`):
```java
// "Fat" interface forces all implementations to provide all methods
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void submitTimesheet();
    void getPaidVacation();
}

// Robot forced to implement methods it doesn't need!
public class Robot implements Worker {
    public void work() { ... }
    public void eat() { throw new UnsupportedOperationException(); }
    public void sleep() { throw new UnsupportedOperationException(); }
    public void getPaidVacation() { throw new UnsupportedOperationException(); }
    // ...
}
```

**Good Example** (`isp/good/`):
```java
// Segregated interfaces - small and focused
public interface Workable { void work(); }
public interface Eatable { void eat(); }
public interface Sleepable { void sleep(); }
public interface VacationEligible { void getPaidVacation(); }

// Each class implements only what it needs
public class Robot implements Workable { ... }
public class Contractor implements Workable, Eatable, Sleepable { ... }
public class FullTimeEmployee implements Workable, Eatable, Sleepable,
        VacationEligible { ... }

// Clients depend only on what they need
public void assignWork(List<Workable> workers) { ... }
public void scheduleVacations(List<VacationEligible> employees) { ... }
```

#### Benefits:
- **Flexibility**: Classes implement only relevant methods
- **Cleaner code**: No dummy/exception-throwing implementations
- **Better cohesion**: Interfaces are focused and coherent
- **Easier evolution**: Can add new interfaces without breaking existing code

#### Real-world scenarios:
- User capabilities (Readable, Writable, Deletable, Shareable)
- Database operations (Queryable, Insertable, Updatable, Deletable)
- UI components (Clickable, Draggable, Resizable, Focusable)
- Document operations (Printable, Saveable, Exportable)

---

### 5. Dependency Inversion Principle (DIP)

> **"High-level modules should not depend on low-level modules. Both should depend on abstractions."**

#### What it means:
Don't depend on concrete implementations; depend on interfaces or abstract classes. This inverts the traditional dependency direction and makes code more flexible and testable.

#### When to apply:
- When high-level business logic depends on low-level implementation details
- When you want to make code testable (inject mocks)
- When you need to swap implementations (different databases, services)
- When instantiating dependencies with `new` inside classes
- When building modular, plugin-based architectures
- Always in modern applications using dependency injection

#### Example in this project:

**Bad Example** (`dip/bad/NotificationService.java`):
```java
// High-level module depends directly on low-level concrete classes
public class NotificationService {
    private EmailService emailService;
    private SMSService smsService;

    public NotificationService() {
        // Directly instantiates concrete classes - tightly coupled!
        this.emailService = new EmailService();
        this.smsService = new SMSService();
    }
}

// Hard to test, hard to extend, tightly coupled
```

**Good Example** (`dip/good/`):
```java
// Abstraction
public interface MessageSender {
    void sendMessage(String recipient, String message);
}

// Low-level modules implement abstraction
public class EmailSender implements MessageSender { ... }
public class SMSSender implements MessageSender { ... }
public class SlackSender implements MessageSender { ... }

// High-level module depends on abstraction
public class NotificationService {
    private List<MessageSender> senders;

    // Dependency injection - receives abstraction, not concrete class
    public NotificationService(List<MessageSender> senders) {
        this.senders = senders;
    }

    public void sendNotification(String recipient, String message) {
        for (MessageSender sender : senders) {
            sender.sendMessage(recipient, message);
        }
    }
}

// Usage - inject dependencies from outside
NotificationService service = new NotificationService(
    Arrays.asList(new EmailSender(), new SMSSender())
);
```

#### Benefits:
- **Testability**: Easily inject mocks/stubs for testing
- **Flexibility**: Swap implementations without changing code
- **Loose coupling**: High and low-level modules are independent
- **Reusability**: Components can be reused with different dependencies

#### Real-world scenarios:
- Data repositories (inject different database implementations)
- External services (payment gateways, email providers)
- Logging frameworks
- Configuration providers
- Authentication providers
- All modern frameworks (Spring, Guice) use DI heavily

---

## Running the Project

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build the project
```bash
cd solid-principles
mvn clean compile
```

### Run all tests
```bash
mvn test
```

### Run tests for a specific principle
```bash
# Single Responsibility Principle
mvn test -Dtest=SRPTest

# Open/Closed Principle
mvn test -Dtest=OCPTest

# Liskov Substitution Principle
mvn test -Dtest=LSPTest

# Interface Segregation Principle
mvn test -Dtest=ISPTest

# Dependency Inversion Principle
mvn test -Dtest=DIPTest
```

### Generate test coverage report
```bash
mvn clean test
```

## Running Tests

All principles include comprehensive unit tests that demonstrate:
- ✅ Good examples work correctly
- ❌ Bad examples fail or throw exceptions
- 🔍 The benefits of following SOLID principles

Test files are located in `src/test/java/com/solid/`:
- `srp/SRPTest.java` - Tests for Single Responsibility Principle
- `ocp/OCPTest.java` - Tests for Open/Closed Principle
- `lsp/LSPTest.java` - Tests for Liskov Substitution Principle
- `isp/ISPTest.java` - Tests for Interface Segregation Principle
- `dip/DIPTest.java` - Tests for Dependency Inversion Principle

## Summary: When to Apply Each Principle

| Principle | Apply When | Red Flags |
|-----------|-----------|-----------|
| **SRP** | Creating any new class | Class has multiple unrelated methods; changes for different reasons |
| **OCP** | Designing extensible systems | Adding features requires modifying existing code; long if/else chains |
| **LSP** | Using inheritance | Subclass throws exceptions parent doesn't; `UnsupportedOperationException` |
| **ISP** | Designing interfaces | Large interfaces; clients implement methods they don't use |
| **DIP** | Managing dependencies | Using `new` for dependencies; hard to test; tightly coupled modules |

## Best Practices

1. **Start with SRP**: It's the foundation - get this right first
2. **Use OCP for variation points**: Identify where changes are likely and apply OCP there
3. **Be careful with inheritance**: LSP helps you design proper hierarchies
4. **Keep interfaces small**: ISP leads to more flexible, composable designs
5. **Inject dependencies**: DIP is essential for testability and flexibility
6. **Don't over-engineer**: Apply these principles pragmatically, not dogmatically
7. **Refactor gradually**: You don't need to follow all principles everywhere immediately

## Learning Resources

- **Books**:
  - "Clean Code" by Robert C. Martin
  - "Design Patterns: Elements of Reusable Object-Oriented Software" by Gang of Four
  - "Agile Software Development, Principles, Patterns, and Practices" by Robert C. Martin

- **Online**:
  - [SOLID Principles Explained](https://stackify.com/solid-design-principles/)
  - [The Principles of OOD](http://butunclebob.com/ArticleS.UncleBob.PrinciplesOfOod)

## Contributing

This is an educational project. Feel free to:
- Add more real-world examples
- Improve existing examples
- Add examples in other languages
- Enhance documentation

## License

This project is open source and available for educational purposes.

---

**Remember**: SOLID principles are guidelines, not absolute rules. Use them to improve code quality, but always consider the specific context and requirements of your project.
