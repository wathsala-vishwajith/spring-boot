package com.solid.dip.bad;

/**
 * Another high-level module that depends on concrete NotificationService
 */
public class UserRegistration {

    private NotificationService notificationService;

    public UserRegistration() {
        this.notificationService = new NotificationService();
    }

    public void registerUser(String email, String username) {
        System.out.println("Registering user: " + username);
        // ... registration logic ...

        notificationService.sendEmailNotification(
            email,
            "Welcome " + username + "! Your registration is complete."
        );
    }
}
