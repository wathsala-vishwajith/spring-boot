package com.solid.srp;

import com.solid.srp.good.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Single Responsibility Principle
 */
class SRPTest {

    private Invoice invoice;
    private InvoiceRepository repository;
    private InvoiceEmailService emailService;
    private InvoiceReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        invoice = new Invoice("INV-001", "customer@example.com");
        invoice.addItem(new LineItem("Laptop", 999.99, 1));
        invoice.addItem(new LineItem("Mouse", 29.99, 2));

        repository = new InvoiceRepository();
        emailService = new InvoiceEmailService();
        reportGenerator = new InvoiceReportGenerator();
    }

    @Test
    void testInvoiceCalculatesTotal() {
        double expectedTotal = 999.99 + (29.99 * 2);
        assertEquals(expectedTotal, invoice.calculateTotal(), 0.01);
    }

    @Test
    void testInvoiceManagesItems() {
        assertEquals(2, invoice.getItems().size());
        assertEquals("INV-001", invoice.getInvoiceNumber());
        assertEquals("customer@example.com", invoice.getCustomerEmail());
    }

    @Test
    void testRepositorySavesInvoice() {
        // Should execute without exception
        assertDoesNotThrow(() -> repository.save(invoice));
    }

    @Test
    void testEmailServiceSendsEmail() {
        // Should execute without exception
        assertDoesNotThrow(() -> emailService.sendInvoiceEmail(invoice));
    }

    @Test
    void testReportGeneratorCreatesTextReport() {
        String report = reportGenerator.generateTextReport(invoice);

        assertNotNull(report);
        assertTrue(report.contains("INV-001"));
        assertTrue(report.contains("Laptop"));
        assertTrue(report.contains("Mouse"));
        assertTrue(report.contains("1059.97"));
    }

    @Test
    void testReportGeneratorCreatesHtmlReport() {
        String htmlReport = reportGenerator.generateHtmlReport(invoice);

        assertNotNull(htmlReport);
        assertTrue(htmlReport.contains("<html>"));
        assertTrue(htmlReport.contains("INV-001"));
        assertTrue(htmlReport.contains("Laptop"));
        assertTrue(htmlReport.contains("1059.97"));
    }

    @Test
    void testSeparationOfConcerns() {
        // Each class has one responsibility and can be used independently
        InvoiceRepository repo1 = new InvoiceRepository();
        InvoiceRepository repo2 = new InvoiceRepository();

        // Different repositories can be used for different storage strategies
        assertNotSame(repo1, repo2);

        // Email service is independent
        InvoiceEmailService emailService1 = new InvoiceEmailService();
        assertDoesNotThrow(() -> emailService1.sendPaymentReminder(invoice));
    }
}
