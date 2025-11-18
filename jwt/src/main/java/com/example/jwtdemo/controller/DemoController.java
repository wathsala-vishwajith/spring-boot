package com.example.jwtdemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for demonstrating protected and public endpoints.
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    /**
     * Public endpoint accessible without authentication
     *
     * @return welcome message
     */
    @GetMapping("/public/hello")
    public Map<String, String> publicHello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from public endpoint! No authentication required.");
        return response;
    }

    /**
     * Protected endpoint requiring authentication
     *
     * @return personalized welcome message
     */
    @GetMapping("/private/hello")
    public Map<String, String> privateHello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello " + username + "! You are authenticated.");
        response.put("username", username);
        return response;
    }

    /**
     * Admin-only endpoint
     *
     * @return admin message
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to admin dashboard, " + username + "!");
        response.put("role", "ADMIN");
        return response;
    }

    /**
     * User profile endpoint
     *
     * @return user information
     */
    @GetMapping("/user/profile")
    public Map<String, Object> userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("authorities", authentication.getAuthorities());
        response.put("authenticated", authentication.isAuthenticated());
        return response;
    }
}
