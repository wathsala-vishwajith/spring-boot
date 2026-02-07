package com.solid.ocp.bad;

/**
 * BAD EXAMPLE: Violates Open/Closed Principle
 * This class needs to be modified every time a new payment method is added
 * It's open for modification instead of extension
 */
public class PaymentProcessor {

    public void processPayment(String paymentType, double amount) {
        if (paymentType.equals("CREDIT_CARD")) {
            processCreditCard(amount);
        } else if (paymentType.equals("PAYPAL")) {
            processPayPal(amount);
        } else if (paymentType.equals("BITCOIN")) {
            processBitcoin(amount);
        } else if (paymentType.equals("BANK_TRANSFER")) {
            processBankTransfer(amount);
        } else {
            throw new IllegalArgumentException("Unknown payment type: " + paymentType);
        }
    }

    private void processCreditCard(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
        System.out.println("Validating card number...");
        System.out.println("Checking available credit...");
        System.out.println("Charging card...");
    }

    private void processPayPal(double amount) {
        System.out.println("Processing PayPal payment of $" + amount);
        System.out.println("Redirecting to PayPal...");
        System.out.println("Payment authorized");
    }

    private void processBitcoin(double amount) {
        System.out.println("Processing Bitcoin payment of $" + amount);
        System.out.println("Generating wallet address...");
        System.out.println("Waiting for confirmation...");
    }

    private void processBankTransfer(double amount) {
        System.out.println("Processing bank transfer of $" + amount);
        System.out.println("Validating bank account...");
        System.out.println("Initiating transfer...");
    }

    // Problem: To add a new payment method (e.g., Apple Pay),
    // we must MODIFY this class by adding another if/else branch
}
