package com.example.security.authentication.basic;

import com.example.security.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;

/**
 * Basic Username/Password Authentication Configuration
 *
 * Demonstrates:
 * - UserDetailsService implementation
 * - Password encoding with BCrypt
 * - Loading users from database
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.basic", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BasicAuthenticationConfig {

    /**
     * Password encoder bean - BCrypt is recommended for production
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Custom UserDetailsService that loads users from database
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()))
                        .disabled(!user.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
