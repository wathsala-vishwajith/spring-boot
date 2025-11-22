package com.solid.srp.good;

/**
 * Separate class responsible ONLY for data persistence
 */
public class InvoiceRepository {

    public void save(Invoice invoice) {
        System.out.println("Connecting to database...");
        System.out.println("Saving invoice " + invoice.getInvoiceNumber() + " to database");
        System.out.println("Total: $" + invoice.calculateTotal());
        System.out.println("Invoice saved successfully");
    }

    public Invoice findByNumber(String invoiceNumber) {
        System.out.println("Fetching invoice " + invoiceNumber + " from database");
        // In real implementation, would fetch from database
        return null;
    }
}
