package com.solid.dip.good;

/**
 * GOOD EXAMPLE: Follows Dependency Inversion Principle
 * High-level abstraction that both high-level and low-level modules depend on
 */
public interface MessageSender {
    void sendMessage(String recipient, String message);
    String getType();
}
