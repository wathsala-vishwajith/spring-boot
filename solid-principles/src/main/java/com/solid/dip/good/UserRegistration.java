package com.solid.dip.good;

/**
 * High-level module depends on abstraction
 * Can be configured with any NotificationService implementation
 */
public class UserRegistration {

    private NotificationService notificationService;

    // Dependency injection via constructor
    public UserRegistration(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void registerUser(String email, String username) {
        System.out.println("Registering user: " + username);
        // ... registration logic ...

        notificationService.sendNotification(
            email,
            "Welcome " + username + "! Your registration is complete."
        );
    }
}
