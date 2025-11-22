package com.solid.dip;

import com.solid.dip.good.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Dependency Inversion Principle
 */
class DIPTest {

    @Test
    void testEmailSender() {
        MessageSender emailSender = new EmailSender();

        assertDoesNotThrow(() -> emailSender.sendMessage("user@example.com", "Hello!"));
        assertEquals("Email", emailSender.getType());
    }

    @Test
    void testSMSSender() {
        MessageSender smsSender = new SMSSender();

        assertDoesNotThrow(() -> smsSender.sendMessage("+1234567890", "Hello!"));
        assertEquals("SMS", smsSender.getType());
    }

    @Test
    void testPushNotificationSender() {
        MessageSender pushSender = new PushNotificationSender();

        assertDoesNotThrow(() -> pushSender.sendMessage("device-token-123", "Hello!"));
        assertEquals("Push Notification", pushSender.getType());
    }

    @Test
    void testSlackSender() {
        MessageSender slackSender = new SlackSender();

        assertDoesNotThrow(() -> slackSender.sendMessage("#general", "Hello!"));
        assertEquals("Slack", slackSender.getType());
    }

    @Test
    void testNotificationServiceWithSingleSender() {
        MessageSender emailSender = new EmailSender();
        NotificationService service = new NotificationService(emailSender);

        assertDoesNotThrow(() -> service.sendNotification("user@example.com", "Welcome!"));
    }

    @Test
    void testNotificationServiceWithMultipleSenders() {
        List<MessageSender> senders = Arrays.asList(
            new EmailSender(),
            new SMSSender(),
            new PushNotificationSender()
        );

        NotificationService service = new NotificationService(senders);

        // Sends via all configured senders
        assertDoesNotThrow(() -> service.sendNotification("recipient", "Test message"));
    }

    @Test
    void testNotificationServiceIsExtensible() {
        // Can add new senders without modifying NotificationService
        MessageSender emailSender = new EmailSender();
        NotificationService service = new NotificationService(emailSender);

        // Add new sender dynamically
        MessageSender slackSender = new SlackSender();
        service.addMessageSender(slackSender);

        assertDoesNotThrow(() -> service.sendNotification("recipient", "Test"));
    }

    @Test
    void testDependencyInjection() {
        // Different senders can be injected
        MessageSender sender1 = new EmailSender();
        NotificationService service1 = new NotificationService(sender1);

        MessageSender sender2 = new SMSSender();
        NotificationService service2 = new NotificationService(sender2);

        // Same high-level class, different low-level implementations
        assertNotSame(service1, service2);
        assertDoesNotThrow(() -> service1.sendNotification("user", "msg1"));
        assertDoesNotThrow(() -> service2.sendNotification("user", "msg2"));
    }

    @Test
    void testUserRegistrationWithDependencyInjection() {
        // Can configure UserRegistration with different notification strategies
        MessageSender emailSender = new EmailSender();
        NotificationService emailNotificationService = new NotificationService(emailSender);
        UserRegistration registration1 = new UserRegistration(emailNotificationService);

        assertDoesNotThrow(() -> registration1.registerUser("user@example.com", "John"));

        // Different configuration
        List<MessageSender> multiSenders = Arrays.asList(
            new EmailSender(),
            new SMSSender()
        );
        NotificationService multiNotificationService = new NotificationService(multiSenders);
        UserRegistration registration2 = new UserRegistration(multiNotificationService);

        assertDoesNotThrow(() -> registration2.registerUser("user@example.com", "Jane"));
    }

    @Test
    void testPolymorphicBehavior() {
        // All senders can be treated uniformly through the MessageSender interface
        MessageSender[] senders = {
            new EmailSender(),
            new SMSSender(),
            new PushNotificationSender(),
            new SlackSender()
        };

        for (MessageSender sender : senders) {
            assertDoesNotThrow(() -> sender.sendMessage("recipient", "Test"));
            assertNotNull(sender.getType());
        }
    }

    @Test
    void testHighLevelModuleIndependence() {
        // NotificationService doesn't depend on concrete implementations
        // It depends on the MessageSender abstraction

        // Can use any implementation
        NotificationService service1 = new NotificationService(new EmailSender());
        NotificationService service2 = new NotificationService(new SMSSender());
        NotificationService service3 = new NotificationService(new SlackSender());

        // All work the same way
        assertDoesNotThrow(() -> service1.sendNotification("r1", "m1"));
        assertDoesNotThrow(() -> service2.sendNotification("r2", "m2"));
        assertDoesNotThrow(() -> service3.sendNotification("r3", "m3"));
    }

    @Test
    void testSpecificSenderMethod() {
        List<MessageSender> senders = Arrays.asList(
            new EmailSender(),
            new SMSSender()
        );
        NotificationService service = new NotificationService(senders);

        // Can also send via a specific sender
        MessageSender slackSender = new SlackSender();
        assertDoesNotThrow(() ->
            service.sendViaSpecificSender(slackSender, "#channel", "Urgent message")
        );
    }
}
