package behavioral.strategy;

/**
 * Demo class showing Strategy pattern in action
 */
public class StrategyDemo {
    public static void main(String[] args) {
        System.out.println("=== Strategy Pattern Demo ===\n");

        System.out.println("--- Payment Processing ---");
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(100);
        cart.addItem(50);

        System.out.println("Pay with Credit Card:");
        cart.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
        cart.checkout();

        System.out.println("\nPay with PayPal:");
        cart.setPaymentStrategy(new PayPalPayment("user@example.com"));
        cart.checkout();

        System.out.println("\nPay with Bitcoin:");
        cart.setPaymentStrategy(new BitcoinPayment("1A2B3C4D5E6F"));
        cart.checkout();

        System.out.println("\n--- Navigation System ---");
        Navigator navigator = new Navigator();

        navigator.setStrategy(new CarStrategy());
        navigator.navigate("Home", "Office");

        navigator.setStrategy(new WalkingStrategy());
        navigator.navigate("Home", "Office");

        navigator.setStrategy(new PublicTransportStrategy());
        navigator.navigate("Home", "Office");

        System.out.println("\n--- Sorting Algorithms ---");
        DataProcessor processor = new DataProcessor();
        int[] data = {5, 2, 8, 1, 9};

        processor.setStrategy(new BubbleSortStrategy());
        processor.sort(data);

        processor.setStrategy(new QuickSortStrategy());
        processor.sort(data);
    }
}

// Strategy interface
interface PaymentStrategy {
    void pay(int amount);
}

// Concrete Strategies
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using Credit Card " + cardNumber);
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using PayPal account " + email);
    }
}

class BitcoinPayment implements PaymentStrategy {
    private String walletAddress;

    public BitcoinPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using Bitcoin wallet " + walletAddress);
    }
}

// Context
class ShoppingCart {
    private int total = 0;
    private PaymentStrategy paymentStrategy;

    public void addItem(int price) {
        total += price;
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public void checkout() {
        if (paymentStrategy == null) {
            System.out.println("Please select a payment method");
            return;
        }
        paymentStrategy.pay(total);
    }
}

// Navigation Example
interface NavigationStrategy {
    void navigate(String from, String to);
}

class CarStrategy implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        System.out.println("Driving from " + from + " to " + to + " (25 min via highway)");
    }
}

class WalkingStrategy implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        System.out.println("Walking from " + from + " to " + to + " (45 min via park)");
    }
}

class PublicTransportStrategy implements NavigationStrategy {
    @Override
    public void navigate(String from, String to) {
        System.out.println("Taking bus from " + from + " to " + to + " (35 min, $2.50)");
    }
}

class Navigator {
    private NavigationStrategy strategy;

    public void setStrategy(NavigationStrategy strategy) {
        this.strategy = strategy;
    }

    public void navigate(String from, String to) {
        if (strategy == null) {
            System.out.println("Please select navigation mode");
            return;
        }
        strategy.navigate(from, to);
    }
}

// Sorting Example
interface SortStrategy {
    void sort(int[] array);
}

class BubbleSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] array) {
        System.out.println("Sorting using Bubble Sort algorithm");
    }
}

class QuickSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] array) {
        System.out.println("Sorting using Quick Sort algorithm");
    }
}

class DataProcessor {
    private SortStrategy strategy;

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void sort(int[] data) {
        if (strategy == null) {
            System.out.println("Please select sorting algorithm");
            return;
        }
        strategy.sort(data);
    }
}
