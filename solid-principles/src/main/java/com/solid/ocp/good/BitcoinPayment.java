package com.solid.ocp.good;

public class BitcoinPayment implements PaymentMethod {

    private String walletAddress;

    public BitcoinPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing Bitcoin payment of $" + amount);
        System.out.println("Wallet: " + walletAddress);
        System.out.println("Generating transaction...");
        System.out.println("Waiting for blockchain confirmation...");
        System.out.println("Payment successful");
    }

    @Override
    public String getPaymentType() {
        return "Bitcoin";
    }
}
