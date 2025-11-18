package com.example.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Public controller with endpoints that don't require authentication.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    /**
     * Public endpoint accessible without authentication.
     *
     * @return A welcome message
     */
    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello! This is a public endpoint.");
        response.put("info", "No authentication required to access this endpoint.");
        return response;
    }

    /**
     * Get information about the OAuth2 server.
     *
     * @return Server information
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Spring Boot OAuth2 JWT Example");
        response.put("version", "1.0.0");
        response.put("description", "Example demonstrating OAuth2 with JWT tokens");
        response.put("endpoints", Map.of(
            "token", "/oauth2/token",
            "authorize", "/oauth2/authorize",
            "jwks", "/.well-known/jwks.json",
            "openid-configuration", "/.well-known/openid-configuration"
        ));
        return response;
    }
}
