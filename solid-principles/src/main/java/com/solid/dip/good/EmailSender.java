package com.solid.dip.good;

/**
 * Low-level module implementing the abstraction
 */
public class EmailSender implements MessageSender {

    @Override
    public void sendMessage(String recipient, String message) {
        System.out.println("Sending email to: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("Email sent successfully");
    }

    @Override
    public String getType() {
        return "Email";
    }
}
