package com.example.security.config;

import com.example.security.authentication.x509.X509AuthenticationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * Main Security Configuration
 *
 * Configures security filter chains for different authentication scenarios.
 * Each authentication method can be enabled/disabled via application.yml
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${auth.examples.rememberme.enabled:true}")
    private boolean rememberMeEnabled;

    @Value("${auth.examples.rememberme.key}")
    private String rememberMeKey;

    @Value("${auth.examples.rememberme.token-validity-seconds:86400}")
    private int tokenValiditySeconds;

    @Value("${auth.examples.x509.enabled:false}")
    private boolean x509Enabled;

    @Autowired(required = false)
    private PersistentTokenRepository persistentTokenRepository;

    @Autowired(required = false)
    private UserDetailsService userDetailsService;

    @Autowired(required = false)
    private X509AuthenticationConfig x509Config;

    /**
     * Main security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/", "/index", "/h2-console/**", "/css/**", "/js/**").permitAll()
                // Admin endpoints
                .requestMatchers("/basic/admin/**").hasRole("ADMIN")
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Form login configuration
            .formLogin(form -> form
                .loginPage("/basic/login")
                .defaultSuccessUrl("/basic/home", true)
                .permitAll()
            )
            // Logout configuration
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // OAuth2 Login configuration
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/basic/login")
                .defaultSuccessUrl("/oauth2/home", true)
            );

        // Remember Me configuration
        if (rememberMeEnabled && persistentTokenRepository != null && userDetailsService != null) {
            http.rememberMe(rememberMe -> rememberMe
                .key(rememberMeKey)
                .tokenRepository(persistentTokenRepository)
                .tokenValiditySeconds(tokenValiditySeconds)
                .userDetailsService(userDetailsService)
            );
        }

        // X.509 Client Certificate Authentication
        if (x509Enabled && x509Config != null) {
            http.x509(x509 -> x509
                .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                .userDetailsService(x509Config.x509UserDetailsService())
            );
        }

        // H2 Console configuration (development only)
        http.csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**")
        );
        http.headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())
        );

        return http.build();
    }
}
