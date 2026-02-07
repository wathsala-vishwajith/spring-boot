package com.solid.ocp.good;

/**
 * GOOD EXAMPLE: Follows Open/Closed Principle
 * This class is closed for modification but open for extension
 * New payment methods can be added without modifying this class
 */
public class PaymentProcessor {

    public void processPayment(PaymentMethod paymentMethod, double amount) {
        System.out.println("=== Payment Processing ===");
        System.out.println("Payment type: " + paymentMethod.getPaymentType());
        paymentMethod.processPayment(amount);
        System.out.println("=========================");
    }

    // Benefit: To add a new payment method (e.g., Apple Pay),
    // we just create a new class implementing PaymentMethod
    // No need to modify this PaymentProcessor class!
}
