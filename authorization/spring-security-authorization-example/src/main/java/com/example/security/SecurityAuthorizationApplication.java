package com.example.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Main application class for Spring Security Authorization Examples.
 *
 * This application demonstrates comprehensive authorization features including:
 * - HTTP Request Authorization (URL-based security)
 * - Method Security (@PreAuthorize, @PostAuthorize, @Secured)
 * - Custom Authorization Managers
 * - Access Control Lists (ACLs)
 * - Authorization Events and Monitoring
 */
@SpringBootApplication
@EnableMethodSecurity(
    prePostEnabled = true,    // Enable @PreAuthorize and @PostAuthorize
    securedEnabled = true,    // Enable @Secured
    jsr250Enabled = true      // Enable @RolesAllowed
)
@EnableCaching
public class SecurityAuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityAuthorizationApplication.class, args);
    }
}
