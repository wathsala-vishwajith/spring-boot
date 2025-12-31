package com.example.security.authentication.preauth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * Pre-Authentication Scenario Configuration
 *
 * Demonstrates:
 * - Pre-authentication with external systems (SiteMinder, Java EE Security)
 * - Request header-based authentication
 * - Trust external authentication while handling authorization
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/authentication/preauth.html
 *
 * Use Cases:
 * - Integration with enterprise SSO systems
 * - Java EE container-managed security
 * - Reverse proxy authentication (e.g., Apache, Nginx)
 *
 * Setup:
 * - External system sets authentication header (e.g., SM_USER, REMOTE_USER)
 * - Spring Security trusts the header and handles authorization
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.preauth", name = "enabled", havingValue = "true")
public class PreAuthenticationConfig {

    /**
     * Request Header Authentication Filter
     * Extracts username from request header set by external authentication system
     */
    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader("SM_USER"); // SiteMinder header
        filter.setExceptionIfHeaderMissing(false);
        return filter;
    }

    /**
     * Pre-Authentication Provider
     */
    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(
            new PreAuthenticatedGrantedAuthoritiesUserDetailsService()
        );
        return provider;
    }
}
