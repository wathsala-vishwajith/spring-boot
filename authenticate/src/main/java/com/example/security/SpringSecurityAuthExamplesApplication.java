package com.example.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Security Authentication Examples Application
 *
 * This application demonstrates various Spring Security authentication mechanisms:
 * - Username/Password authentication
 * - OAuth 2.0 Login (GitHub, Google)
 * - SAML 2.0 Login
 * - CAS (Central Authentication Server)
 * - Remember Me
 * - JAAS Authentication
 * - Pre-Authentication Scenarios
 * - X509 Authentication
 */
@SpringBootApplication
public class SpringSecurityAuthExamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityAuthExamplesApplication.class, args);
    }
}
