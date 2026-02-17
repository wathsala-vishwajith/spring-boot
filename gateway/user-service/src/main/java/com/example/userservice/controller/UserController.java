package com.example.userservice.controller;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller
 *
 * REST API endpoints for user management and authentication.
 *
 * Endpoints:
 * - POST /register - Register new user (public)
 * - POST /login - Authenticate and get JWT token (public)
 * - GET /users - List all users (protected)
 * - GET /users/{username} - Get specific user (protected)
 *
 * REST API Best Practices Demonstrated:
 * - Use appropriate HTTP methods (POST for creation, GET for retrieval)
 * - Return appropriate status codes (201 for creation, 200 for success, 400 for bad request)
 * - Use @Valid for request validation
 * - Consistent response format
 * - Meaningful endpoint names
 *
 * Note: Protected endpoints are accessed through the Gateway,
 * which validates JWT tokens before routing requests here.
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Register a new user
     *
     * Public endpoint - no authentication required
     * Validates request body and creates new user account
     *
     * Example request:
     * POST /register
     * {
     *   "username": "john",
     *   "password": "password123",
     *   "email": "john@example.com",
     *   "firstName": "John",
     *   "lastName": "Doe"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Registration request for username: {}", request.getUsername());
            UserResponse user = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Login and receive JWT token
     *
     * Public endpoint - no authentication required
     * Validates credentials and returns JWT token
     *
     * Example request:
     * POST /login
     * {
     *   "username": "john",
     *   "password": "password123"
     * }
     *
     * Example response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "type": "Bearer",
     *   "username": "john",
     *   "message": "Authentication successful"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Login request for username: {}", request.getUsername());
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all users
     *
     * Protected endpoint - requires valid JWT token
     * Token must be included in Authorization header: "Bearer <token>"
     *
     * Gateway validates token and adds X-User-Id header before routing here
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("Get all users request from user: {}", userId);
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by username
     *
     * Protected endpoint - requires valid JWT token
     *
     * Example: GET /users/john
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(
            @PathVariable String username,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            log.info("Get user request for: {} from user: {}", username, userId);
            UserResponse user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.error("Get user failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running");
    }

    /**
     * Error response DTO
     */
    record ErrorResponse(String message) {}
}
