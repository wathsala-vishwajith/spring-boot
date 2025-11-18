package structural.adapter;

/**
 * Demo class showing Adapter pattern in action
 */
public class AdapterDemo {
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern Demo ===\n");

        AudioPlayer audioPlayer = new AudioPlayer();

        System.out.println("--- Playing different formats ---");
        audioPlayer.play("mp3", "song.mp3");
        audioPlayer.play("mp4", "video.mp4");
        audioPlayer.play("vlc", "movie.vlc");
        audioPlayer.play("avi", "clip.avi");

        System.out.println("\n--- Real-world Example: Payment Processing ---");
        demonstratePaymentAdapter();
    }

    private static void demonstratePaymentAdapter() {
        PaymentProcessor processor = new PaymentProcessor();

        // Process different payment methods
        processor.processPayment("creditcard", 100.0);
        processor.processPayment("paypal", 50.0);
        processor.processPayment("bitcoin", 200.0);
        processor.processPayment("cash", 25.0);
    }
}

// Real-world example: Payment system adapter

interface PaymentGateway {
    void pay(double amount);
}

class CreditCardPayment {
    public void makePayment(double amount) {
        System.out.println("Processing credit card payment: $" + amount);
    }
}

class PayPalPayment {
    public void sendMoney(double amount) {
        System.out.println("Sending money via PayPal: $" + amount);
    }
}

class BitcoinPayment {
    public void transfer(double amount) {
        System.out.println("Transferring Bitcoin: $" + amount);
    }
}

class CreditCardAdapter implements PaymentGateway {
    private CreditCardPayment creditCard = new CreditCardPayment();

    @Override
    public void pay(double amount) {
        creditCard.makePayment(amount);
    }
}

class PayPalAdapter implements PaymentGateway {
    private PayPalPayment paypal = new PayPalPayment();

    @Override
    public void pay(double amount) {
        paypal.sendMoney(amount);
    }
}

class BitcoinAdapter implements PaymentGateway {
    private BitcoinPayment bitcoin = new BitcoinPayment();

    @Override
    public void pay(double amount) {
        bitcoin.transfer(amount);
    }
}

class PaymentProcessor {
    public void processPayment(String method, double amount) {
        PaymentGateway gateway = null;

        if (method.equalsIgnoreCase("creditcard")) {
            gateway = new CreditCardAdapter();
        } else if (method.equalsIgnoreCase("paypal")) {
            gateway = new PayPalAdapter();
        } else if (method.equalsIgnoreCase("bitcoin")) {
            gateway = new BitcoinAdapter();
        }

        if (gateway != null) {
            gateway.pay(amount);
        } else {
            System.out.println("Unsupported payment method: " + method);
        }
    }
}
