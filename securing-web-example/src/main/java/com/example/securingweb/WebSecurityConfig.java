package com.example.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration - The Heart of Application Security
 *
 * LESSON 3: Understanding Spring Security Configuration
 * ======================================================
 *
 * @Configuration: Indicates that this class declares one or more @Bean methods and may be processed
 *                 by the Spring container to generate bean definitions.
 *
 * @EnableWebSecurity: This annotation:
 *                     1. Enables Spring Security's web security support
 *                     2. Provides Spring MVC integration
 *                     3. Creates the Spring Security filter chain
 *
 * WHAT IS A SECURITY FILTER CHAIN?
 * =================================
 * When a request comes to your application, it passes through a chain of filters before reaching
 * your controller. These filters handle:
 * - Authentication (checking credentials)
 * - Authorization (checking permissions)
 * - CSRF protection
 * - Session management
 * - Security headers
 * - And more...
 *
 * SPRING SECURITY 6 CHANGES (IMPORTANT FOR BEGINNERS)
 * ====================================================
 * In older Spring Security versions (pre-6), we would extend WebSecurityConfigurerAdapter.
 * In Spring Security 6+, we use a component-based approach with @Bean methods.
 *
 * Old way (deprecated):
 *     public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 *         @Override
 *         protected void configure(HttpSecurity http) { ... }
 *     }
 *
 * New way (current):
 *     @Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) { ... }
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * BEAN 1: Security Filter Chain Configuration
     *
     * This is the main security configuration where we define:
     * - Which URLs require authentication
     * - Which URLs are public
     * - Login page configuration
     * - Logout configuration
     * - Other security settings
     *
     * @param http HttpSecurity object that allows configuring web-based security
     * @return SecurityFilterChain the configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            /*
             * AUTHORIZATION CONFIGURATION
             * ===========================
             * Here we define which URLs require authentication and which don't.
             *
             * The order of rules matters! Spring Security processes them top-to-bottom
             * and stops at the first match.
             */
            .authorizeHttpRequests(authorize -> authorize
                /*
                 * Public URLs - No authentication required
                 * =========================================
                 * requestMatchers("/", "/home"): Matches these exact URLs
                 * permitAll(): Allows anyone to access these URLs without logging in
                 *
                 * Why make "/" and "/home" public?
                 * - Home page is typically the landing page
                 * - Users should be able to see it before deciding to log in
                 * - It often contains information about the application
                 */
                .requestMatchers("/", "/home").permitAll()

                /*
                 * Protected URLs - Authentication required
                 * =========================================
                 * anyRequest(): Matches any request not matched by previous rules
                 * authenticated(): Requires the user to be authenticated (logged in)
                 *
                 * This is a security best practice:
                 * - Explicitly allow certain URLs (whitelist approach)
                 * - Secure everything else by default
                 * - Better than blacklist approach where you might forget to secure something
                 */
                .anyRequest().authenticated()
            )
            /*
             * FORM LOGIN CONFIGURATION
             * ========================
             * Configures form-based authentication (username/password login page)
             */
            .formLogin(form -> form
                /*
                 * loginPage("/login"): Specifies the URL of our custom login page
                 *
                 * What happens without this?
                 * - Spring Security provides a default auto-generated login page
                 * - It's functional but not pretty
                 *
                 * What happens with this?
                 * - When user tries to access a protected page without being logged in
                 * - They are redirected to "/login"
                 * - Our custom login page is shown (login.html)
                 *
                 * IMPORTANT: We must also permit access to the login page itself,
                 * or we'll get a redirect loop!
                 */
                .loginPage("/login")

                /*
                 * permitAll(): Allows everyone to access the login page
                 *
                 * This also implicitly permits:
                 * - The login processing URL (POST to /login)
                 * - Login error handling
                 * - Logout functionality
                 *
                 * Without this, users couldn't access the login page to authenticate!
                 */
                .permitAll()
            )
            /*
             * LOGOUT CONFIGURATION
             * ====================
             * Configures logout functionality
             */
            .logout(logout -> logout
                /*
                 * permitAll(): Allows anyone to trigger logout
                 *
                 * Default logout URL: /logout (POST request for CSRF protection)
                 *
                 * What happens when user logs out?
                 * 1. Invalidates the user's session
                 * 2. Clears authentication information
                 * 3. Redirects to login page with ?logout parameter
                 * 4. Clears remember-me cookies (if enabled)
                 *
                 * You can customize logout with:
                 * - .logoutUrl("/custom-logout") - Change logout URL
                 * - .logoutSuccessUrl("/") - Change redirect after logout
                 * - .deleteCookies("JSESSIONID") - Delete specific cookies
                 * - .invalidateHttpSession(true) - Invalidate session (default)
                 */
                .permitAll()
            );

        /*
         * Build and return the configured SecurityFilterChain
         *
         * The build() method:
         * 1. Validates the configuration
         * 2. Creates the filter chain with all configured filters
         * 3. Returns a SecurityFilterChain that Spring will use to filter requests
         */
        return http.build();
    }

    /**
     * BEAN 2: User Details Service
     *
     * UserDetailsService is an interface used by Spring Security to load user-specific data.
     * It has one method: loadUserByUsername(String username)
     *
     * WHERE DOES USER DATA COME FROM?
     * ================================
     * In real applications, user data typically comes from:
     * - Database (most common) - using JdbcUserDetailsManager or custom implementation
     * - LDAP server - using LdapUserDetailsService
     * - External authentication provider - OAuth2, SAML, etc.
     *
     * For this example, we use InMemoryUserDetailsManager:
     * - Stores users in memory (RAM)
     * - Perfect for demos, testing, and learning
     * - NOT suitable for production (data lost on restart)
     *
     * @return UserDetailsService configured with in-memory users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        /*
         * Creating a User
         * ===============
         * Spring Security provides a User builder for creating UserDetails objects.
         *
         * User.withUsername("user"):
         * - Static factory method that returns a UserBuilder
         * - Sets the username to "user"
         *
         * .password(...):
         * - Sets the password
         * - MUST be encoded (never store plain text passwords!)
         * - We use passwordEncoder().encode() to encode it with BCrypt
         *
         * .roles("USER"):
         * - Assigns roles to the user
         * - Roles are used for authorization (what can this user do?)
         * - Internally, Spring prefixes this with "ROLE_" (becomes "ROLE_USER")
         * - You can assign multiple roles: .roles("USER", "ADMIN")
         *
         * .build():
         * - Builds the UserDetails object
         *
         * ROLES VS AUTHORITIES
         * ====================
         * - Roles are a special type of authority
         * - Role "USER" automatically becomes authority "ROLE_USER"
         * - Use roles for coarse-grained access control (admin, user, moderator)
         * - Use authorities for fine-grained permissions (read:messages, write:messages)
         */
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        /*
         * InMemoryUserDetailsManager
         * ==========================
         * A UserDetailsService implementation that stores users in memory.
         *
         * You can add multiple users:
         *     UserDetails admin = User.withUsername("admin")
         *         .password(passwordEncoder().encode("admin123"))
         *         .roles("ADMIN", "USER")
         *         .build();
         *
         *     return new InMemoryUserDetailsManager(user, admin);
         *
         * Then you can use role-based authorization:
         *     .requestMatchers("/admin/**").hasRole("ADMIN")
         *     .requestMatchers("/user/**").hasRole("USER")
         */
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * BEAN 3: Password Encoder
     *
     * A PasswordEncoder is responsible for encoding passwords and verifying encoded passwords.
     *
     * WHY DO WE NEED PASSWORD ENCODING?
     * ==================================
     * 1. Security: Never store passwords in plain text
     * 2. If your database is compromised, attackers can't see actual passwords
     * 3. Legal/Compliance: Many regulations require password encryption
     *
     * WHAT IS BCrypt?
     * ===============
     * BCrypt is a password hashing function designed by Niels Provos and David Mazières.
     *
     * Advantages:
     * - Adaptive: You can increase rounds as computers get faster
     * - Salted: Automatically includes a random salt to prevent rainbow table attacks
     * - Slow: Intentionally slow to make brute-force attacks impractical
     *
     * How it works:
     * - Takes your password: "password"
     * - Generates a random salt: "29 random characters"
     * - Hashes the password with the salt multiple times (default: 10 rounds = 2^10 = 1024 iterations)
     * - Result: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     *
     * Format of BCrypt hash:
     * $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
     * ││  ││ │                     ││
     * ││  ││ └─ Salt (22 chars)    └─ Hash (31 chars)
     * ││  │└─ Cost factor (10 = 2^10 rounds)
     * ││  └─ Delimiter
     * │└─ BCrypt version identifier
     * └─ Prefix
     *
     * OTHER PASSWORD ENCODERS AVAILABLE
     * ==================================
     * - NoOpPasswordEncoder (DEPRECATED - stores plain text, never use!)
     * - Pbkdf2PasswordEncoder (PBKDF2 algorithm)
     * - SCryptPasswordEncoder (SCrypt algorithm)
     * - Argon2PasswordEncoder (Argon2 algorithm - winner of Password Hashing Competition)
     *
     * For new applications, BCrypt or Argon2 are recommended.
     *
     * @return PasswordEncoder instance for password encoding/verification
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * TEACHING NOTE: How Authentication Works in Spring Security
     * ===========================================================
     *
     * 1. User submits username and password via login form (POST to /login)
     *
     * 2. UsernamePasswordAuthenticationFilter intercepts the request
     *
     * 3. Filter creates an Authentication object with the provided credentials
     *
     * 4. Authentication object is passed to AuthenticationManager
     *
     * 5. AuthenticationManager delegates to AuthenticationProvider
     *
     * 6. AuthenticationProvider uses UserDetailsService to load user by username
     *    - Calls: userDetailsService().loadUserByUsername("user")
     *    - Returns: UserDetails object with username, encoded password, and roles
     *
     * 7. AuthenticationProvider uses PasswordEncoder to verify password
     *    - Calls: passwordEncoder().matches(rawPassword, encodedPassword)
     *    - Returns: true if passwords match, false otherwise
     *
     * 8. If authentication succeeds:
     *    - Creates fully authenticated Authentication object
     *    - Stores it in SecurityContext
     *    - SecurityContext is stored in SecurityContextHolder (ThreadLocal)
     *    - Redirects to originally requested URL (or default success URL)
     *
     * 9. If authentication fails:
     *    - Redirects to /login?error
     *    - Clears SecurityContext
     *
     * 10. For subsequent requests:
     *     - Session ID is sent via cookie
     *     - SessionManagementFilter retrieves SecurityContext from session
     *     - SecurityContext is loaded into SecurityContextHolder
     *     - Request proceeds with authenticated user
     *
     * ACCESSING THE AUTHENTICATED USER
     * =================================
     * In controllers:
     *     @GetMapping("/hello")
     *     public String hello(Authentication authentication) {
     *         String username = authentication.getName();
     *         return "hello";
     *     }
     *
     * Or:
     *     SecurityContext context = SecurityContextHolder.getContext();
     *     Authentication auth = context.getAuthentication();
     *     String username = auth.getName();
     *
     * In Thymeleaf templates:
     *     <p th:text="${#authentication.name}">Username</p>
     *     <div sec:authorize="isAuthenticated()">
     *         This content is only visible to authenticated users
     *     </div>
     */
}
