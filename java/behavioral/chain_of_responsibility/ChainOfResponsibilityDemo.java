package behavioral.chain_of_responsibility;

/**
 * Demo class showing Chain of Responsibility pattern in action
 *
 * The Chain of Responsibility pattern passes requests along a chain of handlers.
 * Each handler decides either to process the request or to pass it to the next handler.
 */
public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility Pattern Demo ===\n");

        System.out.println("--- Support System Example ---");
        demonstrateSupportSystem();

        System.out.println("\n--- Logging System Example ---");
        demonstrateLoggingSystem();

        System.out.println("\n--- Authentication Chain Example ---");
        demonstrateAuthenticationChain();

        System.out.println("\n--- Purchase Approval Example ---");
        demonstratePurchaseApproval();
    }

    /**
     * Classic example - Customer support request handling
     */
    private static void demonstrateSupportSystem() {
        // Build the chain
        SupportHandler basic = new BasicSupport();
        SupportHandler technical = new TechnicalSupport();
        SupportHandler manager = new ManagerSupport();

        basic.setNext(technical);
        technical.setNext(manager);

        // Send requests through the chain
        System.out.println("Request 1: Simple question");
        basic.handleRequest(new SupportRequest("How do I reset my password?", Priority.LOW));

        System.out.println("\nRequest 2: Technical issue");
        basic.handleRequest(new SupportRequest("Server is not responding", Priority.MEDIUM));

        System.out.println("\nRequest 3: Critical issue");
        basic.handleRequest(new SupportRequest("Data breach detected!", Priority.HIGH));
    }

    /**
     * Real-world example - Logging with different levels
     */
    private static void demonstrateLoggingSystem() {
        // Build logging chain
        Logger consoleLogger = new ConsoleLogger(LogLevel.INFO);
        Logger fileLogger = new FileLogger(LogLevel.WARNING);
        Logger emailLogger = new EmailLogger(LogLevel.ERROR);

        consoleLogger.setNext(fileLogger);
        fileLogger.setNext(emailLogger);

        // Log messages at different levels
        System.out.println("Logging INFO message:");
        consoleLogger.log(LogLevel.INFO, "Application started");

        System.out.println("\nLogging WARNING message:");
        consoleLogger.log(LogLevel.WARNING, "High memory usage detected");

        System.out.println("\nLogging ERROR message:");
        consoleLogger.log(LogLevel.ERROR, "Database connection failed");
    }

    /**
     * Real-world example - Authentication middleware chain
     */
    private static void demonstrateAuthenticationChain() {
        // Build authentication chain
        AuthHandler userExists = new UserExistsHandler();
        AuthHandler validPassword = new ValidPasswordHandler();
        AuthHandler roleCheck = new RoleCheckHandler();

        userExists.setNext(validPassword);
        validPassword.setNext(roleCheck);

        // Test authentication
        System.out.println("Test 1: Valid user");
        boolean result1 = userExists.handle("john@example.com", "password123", "ADMIN");
        System.out.println("Authentication result: " + result1);

        System.out.println("\nTest 2: Invalid user");
        boolean result2 = userExists.handle("unknown@example.com", "password", "USER");
        System.out.println("Authentication result: " + result2);
    }

    /**
     * Real-world example - Purchase approval workflow
     */
    private static void demonstratePurchaseApproval() {
        // Build approval chain
        Approver teamLead = new TeamLead();
        Approver manager = new Manager();
        Approver director = new Director();
        Approver ceo = new CEO();

        teamLead.setNext(manager);
        manager.setNext(director);
        director.setNext(ceo);

        // Submit purchase requests
        System.out.println("Request 1: $500");
        teamLead.approve(new PurchaseRequest("Laptop accessories", 500));

        System.out.println("\nRequest 2: $5000");
        teamLead.approve(new PurchaseRequest("New workstations", 5000));

        System.out.println("\nRequest 3: $50000");
        teamLead.approve(new PurchaseRequest("Office renovation", 50000));

        System.out.println("\nRequest 4: $500000");
        teamLead.approve(new PurchaseRequest("Building expansion", 500000));
    }
}

// ============================================
// Support System Example
// ============================================

enum Priority {
    LOW, MEDIUM, HIGH
}

/**
 * Request object
 */
class SupportRequest {
    private String issue;
    private Priority priority;

    public SupportRequest(String issue, Priority priority) {
        this.issue = issue;
        this.priority = priority;
    }

    public String getIssue() {
        return issue;
    }

    public Priority getPriority() {
        return priority;
    }
}

/**
 * Handler interface
 */
abstract class SupportHandler {
    protected SupportHandler next;

    public void setNext(SupportHandler next) {
        this.next = next;
    }

    public abstract void handleRequest(SupportRequest request);
}

/**
 * Concrete Handler - Basic Support
 */
class BasicSupport extends SupportHandler {
    @Override
    public void handleRequest(SupportRequest request) {
        if (request.getPriority() == Priority.LOW) {
            System.out.println("Basic Support: Handling - " + request.getIssue());
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
}

/**
 * Concrete Handler - Technical Support
 */
class TechnicalSupport extends SupportHandler {
    @Override
    public void handleRequest(SupportRequest request) {
        if (request.getPriority() == Priority.MEDIUM) {
            System.out.println("Technical Support: Handling - " + request.getIssue());
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
}

/**
 * Concrete Handler - Manager Support
 */
class ManagerSupport extends SupportHandler {
    @Override
    public void handleRequest(SupportRequest request) {
        if (request.getPriority() == Priority.HIGH) {
            System.out.println("Manager Support: Handling - " + request.getIssue());
        } else if (next != null) {
            next.handleRequest(request);
        } else {
            System.out.println("No handler available for this request");
        }
    }
}

// ============================================
// Logging System Example
// ============================================

enum LogLevel {
    INFO(1), WARNING(2), ERROR(3);

    private int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

/**
 * Abstract Logger Handler
 */
abstract class Logger {
    protected LogLevel level;
    protected Logger next;

    public Logger(LogLevel level) {
        this.level = level;
    }

    public void setNext(Logger next) {
        this.next = next;
    }

    public void log(LogLevel level, String message) {
        if (level.getLevel() >= this.level.getLevel()) {
            write(message);
        }
        if (next != null) {
            next.log(level, message);
        }
    }

    protected abstract void write(String message);
}

/**
 * Concrete Logger - Console
 */
class ConsoleLogger extends Logger {
    public ConsoleLogger(LogLevel level) {
        super(level);
    }

    @Override
    protected void write(String message) {
        System.out.println("Console Logger: " + message);
    }
}

/**
 * Concrete Logger - File
 */
class FileLogger extends Logger {
    public FileLogger(LogLevel level) {
        super(level);
    }

    @Override
    protected void write(String message) {
        System.out.println("File Logger: " + message);
    }
}

/**
 * Concrete Logger - Email
 */
class EmailLogger extends Logger {
    public EmailLogger(LogLevel level) {
        super(level);
    }

    @Override
    protected void write(String message) {
        System.out.println("Email Logger: " + message + " (sent to admin@example.com)");
    }
}

// ============================================
// Authentication Chain Example
// ============================================

/**
 * Abstract Authentication Handler
 */
abstract class AuthHandler {
    protected AuthHandler next;

    public void setNext(AuthHandler next) {
        this.next = next;
    }

    public abstract boolean handle(String username, String password, String role);
}

/**
 * Concrete Handler - Check if user exists
 */
class UserExistsHandler extends AuthHandler {
    @Override
    public boolean handle(String username, String password, String role) {
        if (!username.contains("@")) {
            System.out.println("❌ Invalid username format");
            return false;
        }

        if (username.equals("unknown@example.com")) {
            System.out.println("❌ User does not exist");
            return false;
        }

        System.out.println("✓ User exists");
        if (next != null) {
            return next.handle(username, password, role);
        }
        return true;
    }
}

/**
 * Concrete Handler - Validate password
 */
class ValidPasswordHandler extends AuthHandler {
    @Override
    public boolean handle(String username, String password, String role) {
        if (password.length() < 8) {
            System.out.println("❌ Password too short");
            return false;
        }

        System.out.println("✓ Password valid");
        if (next != null) {
            return next.handle(username, password, role);
        }
        return true;
    }
}

/**
 * Concrete Handler - Check user role
 */
class RoleCheckHandler extends AuthHandler {
    @Override
    public boolean handle(String username, String password, String role) {
        if (role.equals("ADMIN") || role.equals("USER")) {
            System.out.println("✓ Role authorized: " + role);
            return true;
        }

        System.out.println("❌ Invalid role");
        return false;
    }
}

// ============================================
// Purchase Approval Example
// ============================================

/**
 * Purchase request
 */
class PurchaseRequest {
    private String description;
    private double amount;

    public PurchaseRequest(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}

/**
 * Abstract Approver
 */
abstract class Approver {
    protected Approver next;

    public void setNext(Approver next) {
        this.next = next;
    }

    public abstract void approve(PurchaseRequest request);
}

/**
 * Concrete Approver - Team Lead (up to $1000)
 */
class TeamLead extends Approver {
    @Override
    public void approve(PurchaseRequest request) {
        if (request.getAmount() <= 1000) {
            System.out.println("Team Lead approved: " + request.getDescription() +
                " ($" + request.getAmount() + ")");
        } else if (next != null) {
            next.approve(request);
        }
    }
}

/**
 * Concrete Approver - Manager (up to $10000)
 */
class Manager extends Approver {
    @Override
    public void approve(PurchaseRequest request) {
        if (request.getAmount() <= 10000) {
            System.out.println("Manager approved: " + request.getDescription() +
                " ($" + request.getAmount() + ")");
        } else if (next != null) {
            next.approve(request);
        }
    }
}

/**
 * Concrete Approver - Director (up to $100000)
 */
class Director extends Approver {
    @Override
    public void approve(PurchaseRequest request) {
        if (request.getAmount() <= 100000) {
            System.out.println("Director approved: " + request.getDescription() +
                " ($" + request.getAmount() + ")");
        } else if (next != null) {
            next.approve(request);
        }
    }
}

/**
 * Concrete Approver - CEO (any amount)
 */
class CEO extends Approver {
    @Override
    public void approve(PurchaseRequest request) {
        System.out.println("CEO approved: " + request.getDescription() +
            " ($" + request.getAmount() + ")");
    }
}
