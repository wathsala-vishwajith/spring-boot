package com.solid.dip.good;

/**
 * Another implementation - demonstrating extensibility
 */
public class SlackSender implements MessageSender {

    @Override
    public void sendMessage(String recipient, String message) {
        System.out.println("Sending Slack message to channel: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("Slack message sent successfully");
    }

    @Override
    public String getType() {
        return "Slack";
    }
}
