package com.example.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Spring Boot OAuth2 with JWT example.
 *
 * This application demonstrates:
 * - OAuth2 Authorization Server with JWT token support
 * - OAuth2 Resource Server with JWT validation
 * - Secure REST API endpoints
 * - Token-based authentication and authorization
 */
@SpringBootApplication
public class OAuth2JwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2JwtApplication.class, args);
    }
}
