package com.example.security.authentication.jaas;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.HashMap;
import java.util.Map;

/**
 * JAAS (Java Authentication and Authorization Service) Configuration
 *
 * Demonstrates:
 * - JAAS integration with Spring Security
 * - Custom LoginModule implementation
 * - JAAS authentication provider
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/authentication/jaas.html
 *
 * Note: JAAS is a legacy authentication mechanism.
 * Modern applications should prefer other authentication methods.
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.jaas", name = "enabled", havingValue = "true")
public class JaasAuthenticationConfig {

    /**
     * JAAS Authentication Provider with in-memory configuration
     */
    @Bean
    public AbstractJaasAuthenticationProvider jaasAuthenticationProvider() {
        DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();
        provider.setConfiguration(new InMemoryConfiguration(getConfigurationEntries()));
        provider.setAuthorityGranters(new String[0]);
        return provider;
    }

    /**
     * JAAS configuration entries
     */
    private Map<String, AppConfigurationEntry[]> getConfigurationEntries() {
        Map<String, AppConfigurationEntry[]> entries = new HashMap<>();

        // Example JAAS configuration
        // In production, this would be loaded from jaas.config file
        AppConfigurationEntry[] configEntries = new AppConfigurationEntry[] {
            new AppConfigurationEntry(
                "com.sun.security.auth.module.JndiLoginModule",
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                new HashMap<>()
            )
        };

        entries.put("jaas-example", configEntries);
        return entries;
    }
}
