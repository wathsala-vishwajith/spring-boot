package com.solid.ocp.good;

public class PayPalPayment implements PaymentMethod {

    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment of $" + amount);
        System.out.println("PayPal account: " + email);
        System.out.println("Redirecting to PayPal...");
        System.out.println("Payment authorized");
        System.out.println("Payment successful");
    }

    @Override
    public String getPaymentType() {
        return "PayPal";
    }
}
