package com.solid.dip.bad;

/**
 * BAD EXAMPLE: Violates Dependency Inversion Principle
 * High-level module (NotificationService) depends directly on low-level modules
 * (EmailService, SMSService) - concrete implementations
 *
 * Problems:
 * 1. Tightly coupled to specific implementations
 * 2. Hard to test (can't mock dependencies)
 * 3. Hard to extend (adding new notification types requires modifying this class)
 */
public class NotificationService {

    private EmailService emailService;
    private SMSService smsService;

    public NotificationService() {
        // Direct dependency on concrete classes
        this.emailService = new EmailService();
        this.smsService = new SMSService();
    }

    public void sendEmailNotification(String recipient, String message) {
        emailService.sendEmail(recipient, message);
    }

    public void sendSMSNotification(String phoneNumber, String message) {
        smsService.sendSMS(phoneNumber, message);
    }

    // Problem: To add push notifications, we'd need to:
    // 1. Create PushNotificationService
    // 2. Add it as a field here
    // 3. Initialize it in constructor
    // 4. Add a new method sendPushNotification
    // This violates Open/Closed Principle too!
}
