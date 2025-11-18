package com.example.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller demonstrating different authorization levels configured in SecurityConfig.
 *
 * These endpoints show URL-based authorization patterns:
 * - Public access
 * - Role-based access (ADMIN, USER, MODERATOR)
 * - Authority-based access
 * - Custom authorization managers
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    /**
     * Public endpoint - no authentication required.
     * Configured with permitAll() in SecurityConfig.
     */
    @GetMapping("/public/hello")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint accessible to everyone!");
    }

    /**
     * Requires ADMIN role.
     * Configured with hasRole("ADMIN") in SecurityConfig.
     */
    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> adminDashboard(
        @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Admin Dashboard");
        response.put("user", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities());
        return ResponseEntity.ok(response);
    }

    /**
     * Requires USER role.
     * Configured with hasRole("USER") in SecurityConfig.
     */
    @GetMapping("/user/profile")
    public ResponseEntity<Map<String, Object>> userProfile(
        @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Profile");
        response.put("username", userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Requires ADMIN or MODERATOR role.
     * Configured with hasAnyRole("ADMIN", "MODERATOR") in SecurityConfig.
     */
    @GetMapping("/moderator/actions")
    public ResponseEntity<String> moderatorActions() {
        return ResponseEntity.ok("Moderator actions available");
    }

    /**
     * Requires READ_REPORTS or WRITE_REPORTS authority.
     * Configured with hasAnyAuthority() in SecurityConfig.
     */
    @GetMapping("/reports/view")
    public ResponseEntity<String> viewReports() {
        return ResponseEntity.ok("Reports are visible to users with appropriate authority");
    }

    /**
     * Uses custom authorization manager.
     * Requires CUSTOM_ACCESS authority as defined in CustomAuthorizationManager.
     */
    @GetMapping("/custom/resource")
    public ResponseEntity<String> customAuthorization() {
        return ResponseEntity.ok("Custom authorization successful!");
    }

    /**
     * Requires authentication but no specific role.
     * Configured with authenticated() in SecurityConfig.
     */
    @GetMapping("/authenticated/info")
    public ResponseEntity<Map<String, Object>> authenticatedInfo(
        @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "You are authenticated");
        response.put("username", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities());
        return ResponseEntity.ok(response);
    }
}
