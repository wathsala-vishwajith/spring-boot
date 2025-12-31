package com.example.security.authorization;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Custom AuthorizationManager demonstrating how to create custom authorization logic.
 *
 * This example checks if the user has a specific authority and if the request
 * comes from a certain IP range (in production, you would implement real IP checks).
 *
 * AuthorizationManager is the core interface for making authorization decisions
 * in Spring Security 6.x+.
 */
@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext context) {
        Authentication auth = authentication.get();

        // Check if user is authenticated
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // Custom logic: check for specific authority
        boolean hasAuthority = auth.getAuthorities().stream()
            .anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("CUSTOM_ACCESS"));

        // You could also check request attributes, headers, IP addresses, etc.
        // String remoteAddr = context.getRequest().getRemoteAddr();

        return new AuthorizationDecision(hasAuthority);
    }
}
