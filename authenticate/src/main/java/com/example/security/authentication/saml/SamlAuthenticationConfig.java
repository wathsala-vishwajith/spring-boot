package com.example.security.authentication.saml;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * SAML 2.0 Authentication Configuration
 *
 * Demonstrates:
 * - SAML 2.0 Service Provider configuration
 * - Integration with SAML Identity Providers
 * - SAML assertion processing
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/saml2/login/index.html
 *
 * Setup Instructions:
 * 1. Generate SP (Service Provider) certificates:
 *    keytool -genkeypair -alias spring-saml -keyalg RSA -keysize 2048 \
 *            -storetype PKCS12 -keystore saml-keystore.p12 -validity 3650
 *
 * 2. Configure SAML IdP metadata URL in application.yml
 *
 * 3. Register your SP with the IdP using metadata endpoint:
 *    http://localhost:8080/saml2/service-provider-metadata/example-saml
 *
 * Note: This is disabled by default as it requires external SAML IdP configuration
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.saml", name = "enabled", havingValue = "true")
public class SamlAuthenticationConfig {
    // SAML configuration is primarily in application.yml
    // Additional customization can be added here if needed
}
