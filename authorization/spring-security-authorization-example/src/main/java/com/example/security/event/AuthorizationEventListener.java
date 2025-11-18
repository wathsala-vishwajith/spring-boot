package com.example.security.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.stereotype.Component;

/**
 * Event listener for authorization events.
 *
 * Spring Security publishes authorization events that you can listen to for:
 * - Auditing and logging
 * - Security monitoring
 * - Custom business logic based on authorization decisions
 * - Alerting on suspicious activity
 *
 * Two main event types:
 * - AuthorizationGrantedEvent: When access is granted
 * - AuthorizationDeniedEvent: When access is denied
 */
@Component
@Slf4j
public class AuthorizationEventListener {

    /**
     * Listen for successful authorization events.
     * Useful for audit logging and analytics.
     */
    @EventListener
    public void onAuthorizationGranted(AuthorizationGrantedEvent<?> event) {
        log.info("Authorization GRANTED - User: {}, Resource: {}, Decision: {}",
            event.getAuthentication().get().getName(),
            getResourceInfo(event.getSource()),
            event.getAuthorizationDecision());

        // You can add custom logic here:
        // - Log to audit database
        // - Update analytics
        // - Trigger business workflows
    }

    /**
     * Listen for failed authorization attempts.
     * Important for security monitoring and detecting potential attacks.
     */
    @EventListener
    public void onAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
        log.warn("Authorization DENIED - User: {}, Resource: {}, Decision: {}",
            event.getAuthentication().get().getName(),
            getResourceInfo(event.getSource()),
            event.getAuthorizationDecision());

        // Custom logic for denied access:
        // - Log security incidents
        // - Alert on repeated failures
        // - Implement rate limiting
        // - Trigger account lockout mechanisms
    }

    /**
     * Extract resource information from the event source.
     */
    private String getResourceInfo(Object source) {
        if (source == null) {
            return "Unknown";
        }

        // Handle different types of authorization contexts
        String sourceStr = source.toString();

        // Extract meaningful information
        if (sourceStr.contains("RequestAuthorizationContext")) {
            return "HTTP Request";
        } else if (sourceStr.contains("MethodInvocation")) {
            return "Method Invocation";
        }

        return source.getClass().getSimpleName();
    }
}
