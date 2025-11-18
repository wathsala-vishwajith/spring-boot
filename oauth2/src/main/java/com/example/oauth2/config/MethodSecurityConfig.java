package com.example.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enable method-level security annotations like @PreAuthorize.
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
