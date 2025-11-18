# Security Best Practices and Considerations

Comprehensive security guide for the JWT Authentication Demo application.

## Table of Contents

1. [Security Overview](#security-overview)
2. [JWT Token Security](#jwt-token-security)
3. [Authentication Security](#authentication-security)
4. [Authorization Security](#authorization-security)
5. [Database Security](#database-security)
6. [Network Security](#network-security)
7. [Common Vulnerabilities](#common-vulnerabilities)
8. [Production Hardening](#production-hardening)
9. [Security Checklist](#security-checklist)

## Security Overview

This application implements JWT-based authentication with Spring Security. While the implementation follows best practices, additional hardening is required for production use.

### Current Security Features

✅ **Implemented:**
- BCrypt password hashing
- JWT token-based authentication
- Stateless session management
- Role-based access control (RBAC)
- Input validation
- CSRF disabled (appropriate for stateless API)

⚠️ **Needs Enhancement for Production:**
- Token refresh mechanism
- Token revocation/blacklisting
- Rate limiting
- Account lockout after failed attempts
- Password complexity requirements
- HTTPS enforcement
- Security headers
- Audit logging

## JWT Token Security

### Token Generation

**Current Implementation:**
```java
jwt.secret=your-secret-key-here
jwt.expiration=86400000  // 24 hours
```

### 🔒 Production Requirements

#### 1. Secret Key Security

**❌ BAD:**
```properties
jwt.secret=mysecret
```

**✅ GOOD:**
```properties
jwt.secret=${JWT_SECRET}  # From environment variable
```

**Generate a strong secret:**
```bash
# Generate 256-bit secret
openssl rand -base64 32

# Or use Java
import java.security.SecureRandom;
import java.util.Base64;

byte[] secret = new byte[32];
new SecureRandom().nextBytes(secret);
String encodedSecret = Base64.getEncoder().encodeToString(secret);
```

**Requirements:**
- Minimum 256 bits (32 bytes)
- Cryptographically random
- Never committed to version control
- Rotated periodically
- Different per environment

#### 2. Token Expiration

**Current:** 24 hours

**Recommendations:**
- **Access Token:** 15-30 minutes
- **Refresh Token:** 7-30 days
- **Remember Me Token:** 30-90 days

**Implementation:**
```java
// Short-lived access token
jwt.access-token-expiration=900000  // 15 minutes

// Long-lived refresh token
jwt.refresh-token-expiration=2592000000  // 30 days
```

#### 3. Token Claims

**Minimal Claims:**
```json
{
  "sub": "username",
  "iat": 1700000000,
  "exp": 1700086400
}
```

**Enhanced Claims:**
```json
{
  "sub": "username",
  "iat": 1700000000,
  "exp": 1700086400,
  "jti": "unique-token-id",
  "iss": "your-app-name",
  "aud": "your-client-app",
  "roles": ["ROLE_USER"]
}
```

#### 4. Token Storage (Client-Side)

**Security Matrix:**

| Storage Method | XSS Vulnerable | CSRF Vulnerable | Cross-Domain | Recommended |
|----------------|----------------|-----------------|--------------|-------------|
| localStorage   | ✅ YES         | ❌ NO          | ❌ NO        | ❌ NO       |
| sessionStorage | ✅ YES         | ❌ NO          | ❌ NO        | ❌ NO       |
| Cookie (httpOnly) | ❌ NO       | ⚠️ YES         | ✅ YES       | ✅ YES      |
| Memory only    | ❌ NO          | ❌ NO          | ❌ NO        | ⚠️ ACCEPTABLE |

**Best Practice: httpOnly Cookies**

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
    AuthResponse authResponse = authService.authenticate(request);

    // Set token in httpOnly cookie
    Cookie cookie = new Cookie("jwt", authResponse.getToken());
    cookie.setHttpOnly(true);
    cookie.setSecure(true);  // HTTPS only
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60);  // 24 hours
    response.addCookie(cookie);

    return ResponseEntity.ok(authResponse);
}
```

#### 5. Token Revocation

**Current Implementation:** ❌ None

**Production Implementation Options:**

**Option A: Token Blacklist (Redis)**
```java
@Service
public class TokenBlacklistService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void blacklistToken(String token, long expirationTime) {
        String key = "blacklist:" + token;
        redisTemplate.opsForValue().set(key, "revoked", expirationTime, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }
}
```

**Option B: Token Version in Database**
```java
@Entity
public class User {
    private Long tokenVersion;  // Increment on logout/password change
}

// Validate token version matches user's current version
```

## Authentication Security

### Password Security

#### Current Implementation
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**BCrypt Parameters:**
- Default strength: 10
- Time to hash: ~100ms
- Includes automatic salting

#### Enhanced Password Requirements

```java
public class PasswordValidator {

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public static boolean isStrong(String password) {
        if (password.length() < 12) return false;
        if (!UPPERCASE.matcher(password).find()) return false;
        if (!LOWERCASE.matcher(password).find()) return false;
        if (!DIGIT.matcher(password).find()) return false;
        if (!SPECIAL.matcher(password).find()) return false;
        return true;
    }
}
```

### Failed Login Protection

**Implementation: Account Lockout**

```java
@Entity
public class User {
    private int failedLoginAttempts = 0;
    private LocalDateTime lockoutUntil;

    public boolean isAccountLocked() {
        return lockoutUntil != null && LocalDateTime.now().isBefore(lockoutUntil);
    }
}

@Service
public class AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    public void handleFailedLogin(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= MAX_ATTEMPTS) {
                user.setLockoutUntil(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            }

            userRepository.save(user);
        }
    }

    public void handleSuccessfulLogin(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);
        }
    }
}
```

### Multi-Factor Authentication (2FA)

**Implementation Example:**

```java
@Entity
public class User {
    private String totpSecret;  // For TOTP-based 2FA
    private boolean mfaEnabled = false;
}

// Use library like "aerogear-otp-java"
```

## Authorization Security

### Role-Based Access Control (RBAC)

**Current Implementation:**
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/dashboard")
public ResponseEntity<?> adminDashboard() {
    // ...
}
```

### Enhanced Authorization

**Method-Level Security:**
```java
@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
@GetMapping("/users/{username}")
public ResponseEntity<?> getUserProfile(@PathVariable String username) {
    // Users can access own profile, admins can access any
}
```

**Custom Security Expressions:**
```java
@PreAuthorize("@securityService.canAccessResource(#resourceId, authentication)")
@GetMapping("/resources/{resourceId}")
public ResponseEntity<?> getResource(@PathVariable Long resourceId) {
    // Custom authorization logic
}
```

## Database Security

### SQL Injection Prevention

**✅ Current Implementation (Safe):**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

Spring Data JPA uses parameterized queries automatically.

**❌ NEVER DO THIS:**
```java
// DON'T USE RAW QUERIES WITH STRING CONCATENATION
String query = "SELECT * FROM users WHERE username = '" + username + "'";
```

### Data Encryption

**Sensitive Data Encryption:**
```java
@Convert(converter = EmailEncryptionConverter.class)
private String email;

public class EmailEncryptionConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // Encrypt before saving
        return encryptionService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Decrypt when reading
        return encryptionService.decrypt(dbData);
    }
}
```

### Database Credentials

**❌ BAD:**
```properties
spring.datasource.username=root
spring.datasource.password=admin123
```

**✅ GOOD:**
```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

## Network Security

### HTTPS/TLS

**⚠️ CRITICAL for Production**

**Enable HTTPS:**
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat

# Force HTTPS
security.require-ssl=true
```

**Redirect HTTP to HTTPS:**
```java
@Configuration
public class HttpsRedirectConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        return tomcat;
    }
}
```

### CORS Configuration

**Current (Development):**
```java
// Allows all origins - NOT FOR PRODUCTION
```

**Production Configuration:**
```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### Security Headers

**Add Security Headers:**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'"))
            .frameOptions(frame -> frame.deny())
            .xssProtection(xss -> xss.block(true))
            .contentTypeOptions(Customizer.withDefaults())
            .referrerPolicy(referrer -> referrer
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
        );

    return http.build();
}
```

## Common Vulnerabilities

### OWASP Top 10 for APIs

#### 1. Broken Object Level Authorization (BOLA)

**Vulnerable:**
```java
@GetMapping("/users/{userId}")
public User getUser(@PathVariable Long userId) {
    return userRepository.findById(userId).orElseThrow();
    // Any authenticated user can access any user!
}
```

**Secure:**
```java
@GetMapping("/users/{userId}")
public User getUser(@PathVariable Long userId, Authentication auth) {
    User user = userRepository.findById(userId).orElseThrow();
    if (!user.getUsername().equals(auth.getName()) && !hasRole(auth, "ADMIN")) {
        throw new AccessDeniedException("Cannot access other users");
    }
    return user;
}
```

#### 2. Broken Authentication

**Secure Implementation Checklist:**
- ✅ Strong password requirements
- ✅ Account lockout after failed attempts
- ✅ Secure password reset flow
- ✅ Multi-factor authentication
- ✅ Session timeout
- ✅ Secure token generation

#### 3. Excessive Data Exposure

**Vulnerable:**
```java
@GetMapping("/profile")
public User getProfile(Authentication auth) {
    return userRepository.findByUsername(auth.getName()).orElseThrow();
    // Returns password hash, internal IDs, etc.
}
```

**Secure:**
```java
@GetMapping("/profile")
public UserProfileDTO getProfile(Authentication auth) {
    User user = userRepository.findByUsername(auth.getName()).orElseThrow();
    return new UserProfileDTO(user);  // Only expose necessary fields
}
```

#### 4. Lack of Resources & Rate Limiting

**Implementation:**
```java
@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimiter loginRateLimiter() {
        return RateLimiter.create(5.0);  // 5 requests per second
    }
}

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/auth/")) {
            if (!rateLimiter.tryAcquire()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
```

#### 5. Mass Assignment

**Vulnerable:**
```java
@PostMapping("/users")
public User createUser(@RequestBody User user) {
    return userRepository.save(user);
    // Attacker could set isAdmin=true!
}
```

**Secure:**
```java
@PostMapping("/users")
public User createUser(@RequestBody UserCreationDTO dto) {
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRoles(Set.of("ROLE_USER"));  // Default role
    return userRepository.save(user);
}
```

## Production Hardening

### Environment Configuration

```bash
# .env file (NEVER commit this)
JWT_SECRET=your-very-long-and-random-secret-key-here
DB_URL=jdbc:postgresql://localhost:5432/proddb
DB_USERNAME=app_user
DB_PASSWORD=strong-password-here
```

### Application Properties

```properties
# application-prod.properties

# Disable development features
spring.h2.console.enabled=false
spring.jpa.show-sql=false
spring.devtools.restart.enabled=false

# Security
server.ssl.enabled=true
security.require-ssl=true

# Logging
logging.level.root=WARN
logging.level.com.example.jwtdemo=INFO
logging.level.org.springframework.security=WARN

# Hide implementation details
server.error.include-stacktrace=never
server.error.include-message=never

# Production database
spring.jpa.hibernate.ddl-auto=validate
```

### Docker Security

```dockerfile
FROM openjdk:17-jdk-slim

# Don't run as root
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app
COPY target/jwt-demo.jar app.jar

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Kubernetes Security

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: jwt-demo
spec:
  securityContext:
    runAsNonRoot: true
    runAsUser: 1000
  containers:
  - name: jwt-demo
    image: jwt-demo:latest
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
    resources:
      limits:
        memory: "512Mi"
        cpu: "500m"
```

## Security Checklist

### Development
- [ ] Use strong JWT secret (256+ bits)
- [ ] Implement input validation
- [ ] Hash passwords with BCrypt
- [ ] Disable CSRF for stateless API
- [ ] Use DTOs to prevent mass assignment
- [ ] Implement proper error handling

### Testing
- [ ] Test authentication flows
- [ ] Test authorization checks
- [ ] Test token expiration
- [ ] Test rate limiting
- [ ] Security scan (OWASP ZAP)
- [ ] Dependency vulnerability scan

### Production
- [ ] Use HTTPS/TLS
- [ ] Rotate JWT secrets regularly
- [ ] Implement token refresh
- [ ] Add rate limiting
- [ ] Enable audit logging
- [ ] Set security headers
- [ ] Use environment variables for secrets
- [ ] Implement account lockout
- [ ] Add monitoring/alerting
- [ ] Regular security updates
- [ ] Backup and disaster recovery
- [ ] Penetration testing

## Security Monitoring

### Audit Logging

```java
@Aspect
@Component
public class SecurityAuditAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("SECURITY_AUDIT");

    @AfterReturning("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void auditAuthentication(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogger.info("Action: {}, User: {}, Time: {}",
            joinPoint.getSignature().getName(),
            auth != null ? auth.getName() : "anonymous",
            LocalDateTime.now()
        );
    }
}
```

### Security Events to Log

- ✅ Login attempts (success/failure)
- ✅ Account lockouts
- ✅ Password changes
- ✅ Role changes
- ✅ Token generation
- ✅ Access denied events
- ✅ Suspicious activity patterns

## Conclusion

Security is an ongoing process. Regularly review and update security measures, stay informed about new vulnerabilities, and follow the principle of defense in depth.

### Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [CWE Top 25](https://cwe.mitre.org/top25/)
