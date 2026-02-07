package com.solid.ocp.good;

public class BankTransferPayment implements PaymentMethod {

    private String accountNumber;
    private String bankCode;

    public BankTransferPayment(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing bank transfer of $" + amount);
        System.out.println("Account: " + maskAccountNumber(accountNumber));
        System.out.println("Bank code: " + bankCode);
        System.out.println("Validating bank account...");
        System.out.println("Initiating transfer...");
        System.out.println("Payment successful");
    }

    @Override
    public String getPaymentType() {
        return "Bank Transfer";
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber.length() < 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
