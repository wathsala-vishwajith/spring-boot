package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication Response DTO
 *
 * Returned after successful login.
 * Contains the JWT token that clients must include in subsequent requests.
 *
 * Token Usage:
 * - Client stores token (typically in localStorage or httpOnly cookie)
 * - Client includes token in Authorization header: "Bearer <token>"
 * - Gateway validates token before routing to protected services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private String message;

    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
        this.message = "Authentication successful";
    }
}
