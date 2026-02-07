package com.solid.srp.good;

import java.util.ArrayList;
import java.util.List;

/**
 * GOOD EXAMPLE: Follows Single Responsibility Principle
 * This class has only ONE responsibility: Managing invoice data
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

    public double calculateTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
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
