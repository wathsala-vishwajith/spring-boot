package com.example.oauth2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User controller with endpoints that require authentication.
 * These endpoints are protected by OAuth2 JWT tokens.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * Get current user information from the JWT token.
     *
     * @param authentication The authentication object containing JWT details
     * @return User information extracted from the token
     */
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication != null) {
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

            // If the authentication principal is a JWT, extract additional claims
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                response.put("subject", jwt.getSubject());
                response.put("issuedAt", jwt.getIssuedAt());
                response.put("expiresAt", jwt.getExpiresAt());
                response.put("claims", jwt.getClaims());
            }
        }

        return response;
    }

    /**
     * Endpoint accessible by any authenticated user.
     *
     * @param authentication The authentication object
     * @return A message for authenticated users
     */
    @GetMapping("/hello")
    public Map<String, String> hello(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, " + authentication.getName() + "!");
        response.put("info", "This endpoint requires authentication.");
        return response;
    }
}
