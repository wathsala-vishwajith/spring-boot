package com.solid.dip.bad;

/**
 * Low-level module - concrete implementation
 */
public class EmailService {

    public void sendEmail(String recipient, String message) {
        System.out.println("Sending email to: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("Email sent successfully");
    }
}
