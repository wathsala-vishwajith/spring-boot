package com.solid.ocp.good;

/**
 * GOOD EXAMPLE: Follows Open/Closed Principle
 * Payment methods are open for extension but closed for modification
 */
public interface PaymentMethod {
    void processPayment(double amount);
    String getPaymentType();
}
