package com.solid.dip.good;

import java.util.ArrayList;
import java.util.List;

/**
 * GOOD EXAMPLE: High-level module depends on abstraction (MessageSender)
 * Not on concrete implementations
 *
 * Benefits:
 * 1. Loosely coupled - can work with any MessageSender implementation
 * 2. Easy to test - can inject mock MessageSender
 * 3. Easy to extend - new message types don't require changes here
 * 4. Follows Open/Closed Principle too!
 */
public class NotificationService {

    private List<MessageSender> messageSenders;

    // Dependency injection via constructor
    public NotificationService(List<MessageSender> messageSenders) {
        this.messageSenders = new ArrayList<>(messageSenders);
    }

    // Alternative: single sender
    public NotificationService(MessageSender messageSender) {
        this.messageSenders = new ArrayList<>();
        this.messageSenders.add(messageSender);
    }

    public void sendNotification(String recipient, String message) {
        for (MessageSender sender : messageSenders) {
            System.out.println("--- Using " + sender.getType() + " ---");
            sender.sendMessage(recipient, message);
        }
    }

    public void addMessageSender(MessageSender sender) {
        this.messageSenders.add(sender);
    }

    // Can still send via specific sender if needed
    public void sendViaSpecificSender(MessageSender sender, String recipient, String message) {
        sender.sendMessage(recipient, message);
    }
}
