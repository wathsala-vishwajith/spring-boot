package com.solid.ocp;

import com.solid.ocp.good.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Open/Closed Principle
 */
class OCPTest {

    @Test
    void testCreditCardPayment() {
        PaymentMethod creditCard = new CreditCardPayment("1234567890123456");
        PaymentProcessor processor = new PaymentProcessor();

        assertDoesNotThrow(() -> processor.processPayment(creditCard, 100.00));
        assertEquals("Credit Card", creditCard.getPaymentType());
    }

    @Test
    void testPayPalPayment() {
        PaymentMethod paypal = new PayPalPayment("user@example.com");
        PaymentProcessor processor = new PaymentProcessor();

        assertDoesNotThrow(() -> processor.processPayment(paypal, 50.00));
        assertEquals("PayPal", paypal.getPaymentType());
    }

    @Test
    void testBitcoinPayment() {
        PaymentMethod bitcoin = new BitcoinPayment("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        PaymentProcessor processor = new PaymentProcessor();

        assertDoesNotThrow(() -> processor.processPayment(bitcoin, 200.00));
        assertEquals("Bitcoin", bitcoin.getPaymentType());
    }

    @Test
    void testBankTransferPayment() {
        PaymentMethod bankTransfer = new BankTransferPayment("9876543210", "SWIFT123");
        PaymentProcessor processor = new PaymentProcessor();

        assertDoesNotThrow(() -> processor.processPayment(bankTransfer, 1000.00));
        assertEquals("Bank Transfer", bankTransfer.getPaymentType());
    }

    @Test
    void testExtensibilityWithoutModification() {
        // PaymentProcessor doesn't need to be modified to support new payment types
        PaymentProcessor processor = new PaymentProcessor();

        // Original payment methods
        processor.processPayment(new CreditCardPayment("1234"), 100);
        processor.processPayment(new PayPalPayment("test@test.com"), 50);

        // New payment methods can be added without modifying PaymentProcessor
        processor.processPayment(new BitcoinPayment("wallet123"), 200);
        processor.processPayment(new BankTransferPayment("acc123", "bank456"), 300);

        // All work seamlessly - Open/Closed Principle satisfied
    }

    @Test
    void testPolymorphism() {
        // All payment methods can be treated uniformly through the interface
        PaymentMethod[] methods = {
            new CreditCardPayment("1234"),
            new PayPalPayment("test@test.com"),
            new BitcoinPayment("wallet"),
            new BankTransferPayment("acc", "bank")
        };

        PaymentProcessor processor = new PaymentProcessor();

        for (PaymentMethod method : methods) {
            assertDoesNotThrow(() -> processor.processPayment(method, 100.00));
            assertNotNull(method.getPaymentType());
        }
    }
}
