package com.example.security.authentication.x509;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * X.509 Certificate Authentication Configuration
 *
 * Demonstrates:
 * - Client certificate authentication
 * - SSL/TLS mutual authentication
 * - Certificate-based user lookup
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/authentication/x509.html
 *
 * Setup Instructions:
 * 1. Generate server keystore:
 *    keytool -genkeypair -alias server -keyalg RSA -keysize 2048 \
 *            -storetype PKCS12 -keystore server-keystore.p12 -validity 3650
 *
 * 2. Generate client certificate:
 *    keytool -genkeypair -alias client -keyalg RSA -keysize 2048 \
 *            -storetype PKCS12 -keystore client-keystore.p12 -validity 3650
 *
 * 3. Export client certificate:
 *    keytool -exportcert -alias client -keystore client-keystore.p12 \
 *            -storetype PKCS12 -file client.cer
 *
 * 4. Import client certificate into server truststore:
 *    keytool -importcert -alias client -file client.cer \
 *            -keystore server-truststore.p12 -storetype PKCS12
 *
 * 5. Configure SSL in application.yml with client-auth: need
 *
 * Note: Requires SSL to be enabled in server configuration
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.x509", name = "enabled", havingValue = "true")
public class X509AuthenticationConfig {

    /**
     * UserDetailsService for X.509 authentication
     * Maps certificate subject DN to user details
     */
    @Bean
    public UserDetailsService x509UserDetailsService() {
        return username -> {
            // Extract CN from certificate DN
            // Example DN: CN=John Doe,OU=Engineering,O=Example Corp,C=US
            String commonName = extractCommonName(username);

            // In production, look up user in database based on certificate
            if (commonName != null && !commonName.isEmpty()) {
                return new User(commonName, "",
                    AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_X509"));
            }

            throw new UsernameNotFoundException("Certificate user not found: " + username);
        };
    }

    /**
     * Extract Common Name (CN) from Distinguished Name (DN)
     */
    private String extractCommonName(String dn) {
        if (dn == null) return null;

        String[] parts = dn.split(",");
        for (String part : parts) {
            if (part.trim().toUpperCase().startsWith("CN=")) {
                return part.trim().substring(3);
            }
        }
        return dn;
    }
}
