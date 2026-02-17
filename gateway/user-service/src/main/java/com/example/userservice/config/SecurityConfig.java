package com.example.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 *
 * Configures Spring Security for the User Service.
 *
 * Key Concepts:
 * 1. Stateless Authentication: No server-side sessions (uses JWT)
 * 2. Password Encoding: BCrypt for secure password hashing
 * 3. Authentication Provider: How users are authenticated
 * 4. Security Filter Chain: Which endpoints require authentication
 *
 * Learning Points:
 * - CSRF protection disabled (not needed for stateless JWT APIs)
 * - Sessions disabled (STATELESS) - JWT provides authentication
 * - Public endpoints: /register and /login
 * - All other endpoints protected by Gateway JWT filter
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    /**
     * Password Encoder Bean
     *
     * BCrypt is a strong, adaptive hashing algorithm.
     * Benefits:
     * - Includes salt automatically (prevents rainbow table attacks)
     * - Computationally expensive (slows down brute force attacks)
     * - Adaptive: can increase iterations as hardware improves
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Manager Bean
     *
     * Used by the service to authenticate users during login.
     * Validates username/password against database.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Authentication Provider
     *
     * Configures how authentication is performed:
     * - Uses custom UserDetailsService to load user from database
     * - Uses BCrypt to verify passwords
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Security Filter Chain
     *
     * Defines security rules for HTTP requests.
     *
     * Configuration:
     * - Disable CSRF (not needed for stateless APIs)
     * - Disable sessions (STATELESS mode)
     * - Public access to /register and /login
     * - All other endpoints authenticated at Gateway level
     *
     * Note: In this architecture, the Gateway handles JWT validation.
     * The user service trusts requests from the Gateway (no JWT validation here).
     * In production, consider mutual TLS between services.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
