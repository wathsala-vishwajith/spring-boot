package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User Service Application
 *
 * This microservice handles user management and authentication.
 *
 * Key Features:
 * - User registration with password hashing (BCrypt)
 * - User authentication with JWT token generation
 * - Protected endpoints requiring valid JWT tokens
 * - In-memory H2 database for demo purposes
 *
 * Security Architecture:
 * - Passwords are hashed using BCrypt before storage
 * - JWT tokens are generated upon successful login
 * - Tokens contain username and expiration time
 * - Gateway validates tokens before routing requests
 *
 * Learning Points:
 * - Stateless authentication using JWT
 * - Spring Security configuration
 * - Password encoding best practices
 * - REST API design for authentication
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
