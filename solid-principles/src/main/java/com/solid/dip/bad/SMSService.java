package com.solid.dip.bad;

/**
 * Low-level module - concrete implementation
 */
public class SMSService {

    public void sendSMS(String phoneNumber, String message) {
        System.out.println("Sending SMS to: " + phoneNumber);
        System.out.println("Message: " + message);
        System.out.println("SMS sent successfully");
    }
}
