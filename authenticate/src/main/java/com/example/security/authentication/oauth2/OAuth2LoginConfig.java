package com.example.security.authentication.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth 2.0 Login Configuration
 *
 * Demonstrates:
 * - OAuth 2.0 Login with GitHub
 * - OAuth 2.0 Login with Google (OpenID Connect)
 * - Custom OAuth2 User Service
 * - Handling OAuth2 login success
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html
 *
 * Setup Instructions:
 * 1. For GitHub:
 *    - Go to GitHub Settings > Developer settings > OAuth Apps
 *    - Create a new OAuth App
 *    - Set Authorization callback URL: http://localhost:8080/login/oauth2/code/github
 *    - Set GITHUB_CLIENT_ID and GITHUB_CLIENT_SECRET environment variables
 *
 * 2. For Google:
 *    - Go to Google Cloud Console > APIs & Services > Credentials
 *    - Create OAuth 2.0 Client ID
 *    - Set Authorized redirect URI: http://localhost:8080/login/oauth2/code/google
 *    - Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET environment variables
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.oauth2", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OAuth2LoginConfig {
    // OAuth2 configuration is primarily in application.yml
    // Additional customization can be added here if needed
}
