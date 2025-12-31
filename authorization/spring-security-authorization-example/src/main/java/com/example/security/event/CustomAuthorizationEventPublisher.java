package com.example.security.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Custom component to publish authorization events.
 *
 * While Spring Security automatically publishes events for many authorization
 * decisions, you can manually publish events for custom authorization logic.
 *
 * This demonstrates how to integrate custom authorization with the event system.
 */
@Component
@Slf4j
public class CustomAuthorizationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public CustomAuthorizationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publish a custom authorization event.
     *
     * @param authentication The authentication object
     * @param object The secured object
     * @param decision The authorization decision
     */
    public void publishAuthorizationEvent(
        Authentication authentication,
        Object object,
        AuthorizationDecision decision) {

        if (decision.isGranted()) {
            publishGrantedEvent(authentication, object, decision);
        } else {
            publishDeniedEvent(authentication, object, decision);
        }
    }

    /**
     * Publish an authorization granted event.
     */
    private void publishGrantedEvent(
        Authentication authentication,
        Object object,
        AuthorizationResult result) {

        AuthorizationGrantedEvent<?> event =
            new AuthorizationGrantedEvent<>(() -> authentication, object, result);

        eventPublisher.publishEvent(event);
        log.debug("Published AuthorizationGrantedEvent for user: {}",
            authentication.getName());
    }

    /**
     * Publish an authorization denied event.
     */
    private void publishDeniedEvent(
        Authentication authentication,
        Object object,
        AuthorizationResult result) {

        AuthorizationDeniedEvent<?> event =
            new AuthorizationDeniedEvent<>(() -> authentication, object, result);

        eventPublisher.publishEvent(event);
        log.debug("Published AuthorizationDeniedEvent for user: {}",
            authentication.getName());
    }
}
