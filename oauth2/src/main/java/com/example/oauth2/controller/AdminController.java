package com.example.oauth2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin controller with endpoints that require ADMIN role.
 * These endpoints demonstrate role-based access control with OAuth2 JWT.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * Admin-only endpoint.
     * Requires ADMIN role to access.
     *
     * @param authentication The authentication object
     * @return Admin dashboard information
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> dashboard(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to the admin dashboard!");
        response.put("user", authentication.getName());
        response.put("stats", Map.of(
            "totalUsers", 42,
            "activeTokens", 15,
            "requestsToday", 1337
        ));
        return response;
    }

    /**
     * Endpoint to manage users (admin only).
     *
     * @return List of users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("users", java.util.List.of(
            Map.of("id", 1, "username", "user", "role", "USER"),
            Map.of("id", 2, "username", "admin", "role", "ADMIN")
        ));
        return response;
    }

    /**
     * Protected endpoint that requires specific scope.
     *
     * @return A message
     */
    @PostMapping("/settings")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('SCOPE_write')")
    public Map<String, String> updateSettings(@RequestBody Map<String, Object> settings) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Settings updated successfully");
        response.put("status", "success");
        return response;
    }
}
