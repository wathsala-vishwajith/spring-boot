package com.example.security.config;

import com.example.security.authorization.CustomAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Main Security Configuration demonstrating various HTTP request authorization patterns.
 *
 * This configuration shows:
 * - Public endpoints
 * - Role-based authorization (hasRole)
 * - Authority-based authorization (hasAuthority)
 * - Multiple roles/authorities (hasAnyRole, hasAnyAuthority)
 * - Custom authorization managers
 * - SpEL expressions with access()
 * - Pattern-based matching (requestMatchers)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthorizationManager customAuthorizationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints - no authentication required
                .requestMatchers("/api/public/**").permitAll()

                // Role-based authorization
                // hasRole() automatically adds "ROLE_" prefix
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasRole("USER")

                // Multiple roles - user must have at least one
                .requestMatchers("/api/moderator/**").hasAnyRole("ADMIN", "MODERATOR")

                // Authority-based authorization (no automatic prefix)
                .requestMatchers("/api/documents/create").hasAuthority("WRITE_DOCUMENTS")
                .requestMatchers("/api/documents/delete/**").hasAuthority("DELETE_DOCUMENTS")

                // Multiple authorities
                .requestMatchers("/api/reports/**")
                    .hasAnyAuthority("READ_REPORTS", "WRITE_REPORTS")

                // Custom authorization manager
                .requestMatchers("/api/custom/**").access(customAuthorizationManager)

                // SpEL expression-based authorization
                .requestMatchers("/api/advanced/**")
                    .access((authentication, context) ->
                        new org.springframework.security.authorization.AuthorizationDecision(
                            authentication.get().getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                                              a.getAuthority().equals("ADVANCED_ACCESS"))
                        ))

                // Authenticated users only
                .requestMatchers("/api/authenticated/**").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})  // Enable HTTP Basic authentication
            .csrf(csrf -> csrf.disable());  // Disable CSRF for simplicity in this example

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
