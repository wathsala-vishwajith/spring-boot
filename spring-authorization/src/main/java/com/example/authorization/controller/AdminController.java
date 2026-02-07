package com.example.authorization.controller;

import com.example.authorization.model.User;
import com.example.authorization.security.CustomUserDetailsService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin controller demonstrating different method security annotations:
 * - @PreAuthorize (Spring Security)
 * - @Secured (Spring Security)
 * - @RolesAllowed (JSR-250)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CustomUserDetailsService userDetailsService;

    public AdminController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Using @PreAuthorize - most flexible, supports SpEL expressions
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminDashboard() {
        return Map.of(
            "message", "Welcome to Admin Dashboard",
            "access", "Only ADMIN role can access this endpoint"
        );
    }

    /**
     * Using @Secured - simpler, no SpEL support
     * Note: Requires ROLE_ prefix
     */
    @GetMapping("/settings")
    @Secured("ROLE_ADMIN")
    public Map<String, String> adminSettings() {
        return Map.of(
            "message", "Admin Settings",
            "annotation", "@Secured annotation example"
        );
    }

    /**
     * Using @RolesAllowed - JSR-250 standard
     * Note: Does NOT require ROLE_ prefix
     */
    @GetMapping("/users")
    @RolesAllowed("ADMIN")
    public Map<String, Object> getAllUsers() {
        Map<String, User> users = userDetailsService.getAllUsers();
        return Map.of(
            "message", "All system users",
            "count", users.size(),
            "users", users.keySet()
        );
    }

    /**
     * Complex authorization with SpEL expression
     */
    @GetMapping("/system-info")
    @PreAuthorize("hasRole('ADMIN') and isAuthenticated()")
    public Map<String, Object> getSystemInfo() {
        return Map.of(
            "javaVersion", System.getProperty("java.version"),
            "osName", System.getProperty("os.name"),
            "availableProcessors", Runtime.getRuntime().availableProcessors(),
            "totalMemory", Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB"
        );
    }
}
