package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Gateway Service Application
 *
 * This is the main entry point for the Spring Cloud Gateway service.
 * The gateway acts as a single entry point for all microservices,
 * handling routing, load balancing, and security concerns.
 *
 * Key Features:
 * - Routes requests to appropriate microservices
 * - Validates JWT tokens for protected routes
 * - Provides centralized authentication/authorization
 * - Enables cross-cutting concerns (logging, rate limiting, etc.)
 */
@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
