package com.solid.ocp.good;

public class CreditCardPayment implements PaymentMethod {

    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
        System.out.println("Card: " + maskCardNumber(cardNumber));
        System.out.println("Validating card number...");
        System.out.println("Checking available credit...");
        System.out.println("Charging card...");
        System.out.println("Payment successful");
    }

    @Override
    public String getPaymentType() {
        return "Credit Card";
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
