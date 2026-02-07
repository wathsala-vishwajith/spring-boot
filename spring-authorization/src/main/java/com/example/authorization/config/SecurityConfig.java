package com.example.authorization.config;

import com.example.authorization.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security Configuration demonstrating various authorization mechanisms:
 * 1. Request-level authorization (URL patterns)
 * 2. Role-based access control
 * 3. HTTP method-specific authorization
 * 4. Role hierarchy
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints - accessible to everyone
                .requestMatchers("/api/public/**").permitAll()

                // H2 Console access (for development only)
                .requestMatchers("/h2-console/**").permitAll()

                // Admin endpoints - only ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Manager endpoints - MANAGER or ADMIN roles
                .requestMatchers("/api/manager/**").hasAnyRole("MANAGER", "ADMIN")

                // User endpoints - authenticated users with USER role
                .requestMatchers("/api/user/**").hasRole("USER")

                // Documents endpoints with HTTP method-specific rules
                .requestMatchers(HttpMethod.GET, "/api/documents/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/documents/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/documents/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/documents/**").hasAnyRole("USER", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/documents/**").hasAnyRole("MANAGER", "ADMIN")

                // Info endpoint - authenticated users only
                .requestMatchers("/api/info").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            .formLogin(withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
