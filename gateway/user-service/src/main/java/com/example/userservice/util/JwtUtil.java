package com.example.userservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Utility Class
 *
 * Handles JWT token generation for authenticated users.
 *
 * JWT Structure:
 * - Header: Algorithm and token type
 * - Payload: Claims (user data, expiration, etc.)
 * - Signature: Verifies token hasn't been tampered with
 *
 * Security Considerations:
 * - Secret key must be kept secure (use environment variables in production)
 * - Token expiration prevents indefinite access
 * - Signature ensures integrity
 *
 * Important: The secret must match the one in Gateway service for validation
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationAndValidation1234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    /**
     * Generate JWT token for a user
     *
     * @param username The username to include in the token
     * @return JWT token string
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)                    // Username as subject
                .issuedAt(now)                        // Token creation time
                .expiration(expiryDate)               // Token expiration time
                .signWith(key)                        // Sign with secret key
                .compact();
    }

    /**
     * Get token expiration time in milliseconds
     */
    public Long getExpirationTime() {
        return expiration;
    }
}
