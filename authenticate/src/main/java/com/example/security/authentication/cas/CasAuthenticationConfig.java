package com.example.security.authentication.cas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * CAS (Central Authentication Server) Configuration
 *
 * Demonstrates:
 * - CAS authentication integration
 * - Single Sign-On (SSO) with CAS
 * - CAS ticket validation
 *
 * Reference: https://docs.spring.io/spring-security/reference/servlet/authentication/cas.html
 *
 * Setup Instructions:
 * 1. Install and configure CAS server (https://apereo.github.io/cas/)
 * 2. Set CAS_SERVER_URL environment variable
 * 3. Set CAS_SERVICE_URL environment variable
 *
 * Note: This is disabled by default as it requires external CAS server
 */
@Configuration
@ConditionalOnProperty(prefix = "auth.examples.cas", name = "enabled", havingValue = "true")
public class CasAuthenticationConfig {

    @Value("${cas.server.url}")
    private String casServerUrl;

    @Value("${cas.service.url}")
    private String casServiceUrl;

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casServiceUrl + "/login/cas");
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casServerUrl + "/login");
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties());
        provider.setKey("casAuthProviderKey");
        provider.setAuthenticationUserDetailsService(token ->
            new User(token.getName(), "",
                    AuthorityUtils.createAuthorityList("ROLE_USER"))
        );
        return provider;
    }
}
