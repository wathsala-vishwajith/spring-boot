package com.solid.dip.good;

/**
 * New low-level module - can be added without modifying existing code!
 */
public class PushNotificationSender implements MessageSender {

    @Override
    public void sendMessage(String recipient, String message) {
        System.out.println("Sending push notification to device: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("Push notification sent successfully");
    }

    @Override
    public String getType() {
        return "Push Notification";
    }
}
