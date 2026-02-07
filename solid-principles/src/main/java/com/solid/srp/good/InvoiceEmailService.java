package com.solid.srp.good;

/**
 * Separate class responsible ONLY for sending email notifications
 */
public class InvoiceEmailService {

    public void sendInvoiceEmail(Invoice invoice) {
        System.out.println("Connecting to SMTP server...");
        System.out.println("Sending invoice to: " + invoice.getCustomerEmail());
        System.out.println("Subject: Invoice " + invoice.getInvoiceNumber());
        System.out.println("Email sent successfully");
    }

    public void sendPaymentReminder(Invoice invoice) {
        System.out.println("Sending payment reminder to: " + invoice.getCustomerEmail());
    }
}
