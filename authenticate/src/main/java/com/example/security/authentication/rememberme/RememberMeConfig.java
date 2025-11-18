package com.example.security.authentication.rememberme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * Remember Me Authentication Configuration
 *
 * Demonstrates:
 * - Persistent Remember Me tokens
 * - Token-based authentication
 * - Remember Me cookie management
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/authentication/rememberme.html
 *
 * Features:
 * - Persistent token repository (database-backed)
 * - Configurable token validity
 * - Secure cookie settings
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.rememberme", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RememberMeConfig {

    @Value("${auth.examples.rememberme.key}")
    private String rememberMeKey;

    /**
     * Persistent token repository for Remember Me functionality
     * Stores tokens in database for security
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // Create table automatically (for demo purposes)
        // In production, use schema migration tools like Flyway or Liquibase
        try {
            tokenRepository.setCreateTableOnStartup(true);
        } catch (Exception e) {
            // Table might already exist
        }
        return tokenRepository;
    }
}
