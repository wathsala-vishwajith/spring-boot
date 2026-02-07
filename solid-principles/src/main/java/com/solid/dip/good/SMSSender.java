package com.solid.dip.good;

/**
 * Low-level module implementing the abstraction
 */
public class SMSSender implements MessageSender {

    @Override
    public void sendMessage(String recipient, String message) {
        System.out.println("Sending SMS to: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("SMS sent successfully");
    }

    @Override
    public String getType() {
        return "SMS";
    }
}
