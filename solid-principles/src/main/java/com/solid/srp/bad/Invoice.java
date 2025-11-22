package com.solid.srp.bad;

import java.util.ArrayList;
import java.util.List;

/**
 * BAD EXAMPLE: Violates Single Responsibility Principle
 * This class has multiple responsibilities:
 * 1. Managing invoice data
 * 2. Calculating totals
 * 3. Saving to database
 * 4. Sending emails
 */
public class Invoice {
    private String invoiceNumber;
    private List<LineItem> items;
    private String customerEmail;

    public Invoice(String invoiceNumber, String customerEmail) {
        this.invoiceNumber = invoiceNumber;
        this.customerEmail = customerEmail;
        this.items = new ArrayList<>();
    }

    public void addItem(LineItem item) {
        items.add(item);
    }

    // Responsibility 1: Business logic
    public double calculateTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    // Responsibility 2: Data persistence
    public void saveToDatabase() {
        System.out.println("Connecting to database...");
        System.out.println("Saving invoice " + invoiceNumber + " to database");
        System.out.println("Total: $" + calculateTotal());
    }

    // Responsibility 3: Email notification
    public void sendEmail() {
        System.out.println("Connecting to SMTP server...");
        System.out.println("Sending invoice to: " + customerEmail);
        System.out.println("Email sent successfully");
    }

    // Responsibility 4: Report generation
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Invoice: ").append(invoiceNumber).append("\n");
        report.append("Customer: ").append(customerEmail).append("\n");
        report.append("Items:\n");
        for (LineItem item : items) {
            report.append("  - ").append(item.getName())
                  .append(": $").append(item.getPrice())
                  .append(" x ").append(item.getQuantity())
                  .append("\n");
        }
        report.append("Total: $").append(calculateTotal());
        return report.toString();
    }

    // Getters
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
}
