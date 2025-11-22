package com.example.authorization.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * User controller - accessible to authenticated users with USER role
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public Map<String, Object> getUserProfile(Authentication authentication) {
        return Map.of(
            "username", authentication.getName(),
            "roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()),
            "authenticated", authentication.isAuthenticated()
        );
    }

    @GetMapping("/dashboard")
    public Map<String, String> userDashboard(Authentication authentication) {
        return Map.of(
            "message", "Welcome to User Dashboard, " + authentication.getName(),
            "access", "All users with USER role can access this endpoint"
        );
    }
}
