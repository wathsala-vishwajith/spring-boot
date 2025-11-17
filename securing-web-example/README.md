# Spring Security Web Application - Complete Beginner's Guide

A comprehensive, beginner-friendly example demonstrating how to secure a Spring Boot web application using Spring Security. This project is designed as a learning resource with extensive inline documentation and detailed explanations.

## Table of Contents

- [What You'll Learn](#what-youll-learn)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Understanding the Architecture](#understanding-the-architecture)
- [Step-by-Step Walkthrough](#step-by-step-walkthrough)
- [Key Concepts Explained](#key-concepts-explained)
- [Common Issues and Solutions](#common-issues-and-solutions)
- [Testing the Application](#testing-the-application)
- [Next Steps](#next-steps)
- [Additional Resources](#additional-resources)

## What You'll Learn

By studying this example, you will understand:

1. **Spring Security Fundamentals**
   - How to add Spring Security to a Spring Boot application
   - Authentication vs Authorization
   - Security filter chain and how it works

2. **Configuration**
   - Configuring URL-based security rules
   - Creating custom login pages
   - Setting up in-memory authentication
   - Password encoding with BCrypt

3. **Spring MVC Integration**
   - View controllers for simple pages
   - Thymeleaf template integration
   - Security-aware templates

4. **Security Best Practices**
   - CSRF protection
   - Secure password storage
   - HttpOnly and Secure cookies
   - Session management

5. **Practical Implementation**
   - Public vs protected pages
   - Login/logout functionality
   - Saved request handling (redirect after login)
   - Displaying user information

## Prerequisites

Before you begin, ensure you have:

- **Java 17 or higher** installed ([Download here](https://adoptium.net/))
- **Maven 3.6+** installed ([Download here](https://maven.apache.org/download.cgi))
- Basic understanding of:
  - Java programming
  - Web applications (HTTP, cookies, sessions)
  - HTML and basic CSS
- An IDE or text editor (IntelliJ IDEA, Eclipse, VS Code recommended)

**Check your installations:**
```bash
java -version   # Should show Java 17 or higher
mvn -version    # Should show Maven 3.6 or higher
```

## Project Structure

```
securing-web-example/
├── pom.xml                                    # Maven configuration and dependencies
├── README.md                                  # This file
└── src/
    ├── main/
    │   ├── java/com/example/securingweb/
    │   │   ├── SecuringWebApplication.java   # Main application entry point
    │   │   ├── MvcConfig.java                # Spring MVC configuration
    │   │   └── WebSecurityConfig.java        # Spring Security configuration
    │   └── resources/
    │       ├── application.properties         # Application configuration
    │       └── templates/                     # Thymeleaf HTML templates
    │           ├── home.html                  # Public home page
    │           ├── hello.html                 # Protected page
    │           └── login.html                 # Custom login page
    └── test/
        └── java/com/example/securingweb/     # Test classes (future expansion)
```

## Quick Start

### 1. Clone and Navigate to Project

```bash
cd securing-web-example
```

### 2. Build the Project

```bash
mvn clean package
```

This will:
- Download all dependencies
- Compile the Java code
- Run tests (if any)
- Package the application as a JAR file

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/securing-web-0.0.1-SNAPSHOT.jar
```

### 4. Access the Application

Open your browser and navigate to:
- **Home Page (Public):** http://localhost:8080/
- **Protected Page:** http://localhost:8080/hello (will redirect to login)
- **Login Page:** http://localhost:8080/login

### 5. Test Login

Use these credentials:
- **Username:** `user`
- **Password:** `password`

### 6. Stop the Application

Press `Ctrl+C` in the terminal where the application is running.

## Understanding the Architecture

### How Spring Security Works

```
┌─────────────────────────────────────────────────────────────────┐
│                         HTTP Request                             │
│                    (e.g., GET /hello)                            │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                   Security Filter Chain                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 1. SecurityContextPersistenceFilter                      │   │
│  │    → Loads SecurityContext from session                  │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 2. CsrfFilter                                            │   │
│  │    → Validates CSRF tokens on state-changing requests    │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 3. UsernamePasswordAuthenticationFilter                  │   │
│  │    → Processes login form submissions                    │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 4. AuthorizationFilter                                   │   │
│  │    → Checks if user has permission to access URL         │   │
│  └─────────────────────────────────────────────────────────┘   │
│  └── ... and more filters                                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ↓
                  Is user authenticated?
                            │
              ┌─────────────┴─────────────┐
              NO                          YES
              │                            │
              ↓                            ↓
    Redirect to /login         Does user have permission?
                                           │
                             ┌─────────────┴─────────────┐
                            YES                          NO
                             │                            │
                             ↓                            ↓
                    Continue to Controller       Return 403 Forbidden
                             │
                             ↓
                    ┌────────────────┐
                    │   Controller   │
                    │  (if mapped)   │
                    └────────┬───────┘
                             │
                             ↓
                    ┌────────────────┐
                    │  Thymeleaf     │
                    │  Processes     │
                    │  Template      │
                    └────────┬───────┘
                             │
                             ↓
                    ┌────────────────┐
                    │  HTML Response │
                    └────────────────┘
```

### Authentication Flow

When you submit the login form, here's what happens:

```
1. Browser sends POST to /login with username and password
                    ↓
2. UsernamePasswordAuthenticationFilter intercepts
                    ↓
3. Creates Authentication object (unauthenticated)
                    ↓
4. AuthenticationManager processes authentication
                    ↓
5. Delegates to AuthenticationProvider (DaoAuthenticationProvider)
                    ↓
6. AuthenticationProvider calls UserDetailsService.loadUserByUsername()
                    ↓
7. UserDetailsService returns UserDetails from InMemoryUserDetailsManager
                    ↓
8. AuthenticationProvider verifies password using PasswordEncoder
                    ↓
9. BCryptPasswordEncoder.matches(rawPassword, encodedPassword)
                    ↓
          ┌─────────┴─────────┐
         NO                   YES
          │                    │
          ↓                    ↓
   Authentication Failed   Authentication Successful
   Redirect to             Store in SecurityContext
   /login?error            Redirect to original URL
                           or default success URL
```

## Step-by-Step Walkthrough

### Step 1: Understanding Dependencies (pom.xml)

Open `pom.xml` and examine the key dependencies:

```xml
<!-- Web application support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Thymeleaf template engine -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Spring Security - THE KEY DEPENDENCY -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Important:** Just by adding `spring-boot-starter-security`, Spring Security automatically:
- Requires authentication for ALL endpoints
- Generates a default login page
- Creates a default user (username: "user", password: printed in console)
- Enables CSRF protection
- Adds security headers
- Sets up session management

### Step 2: Main Application Class (SecuringWebApplication.java)

This is the entry point of your Spring Boot application:

```java
@SpringBootApplication  // Combines @Configuration, @EnableAutoConfiguration, @ComponentScan
public class SecuringWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecuringWebApplication.class, args);
    }
}
```

**What @SpringBootApplication does:**
1. **@Configuration**: Marks class as a source of bean definitions
2. **@EnableAutoConfiguration**: Tells Spring Boot to auto-configure based on dependencies
3. **@ComponentScan**: Scans for components, services, controllers in current package and sub-packages

### Step 3: MVC Configuration (MvcConfig.java)

Configures view controllers for simple page mappings:

```java
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }
}
```

**When to use View Controllers:**
- ✅ Just displaying a static page
- ✅ No business logic needed
- ✅ No model data to prepare
- ❌ Need to process forms → Use @Controller
- ❌ Need to fetch database data → Use @Controller
- ❌ Need to manipulate model → Use @Controller

### Step 4: Security Configuration (WebSecurityConfig.java)

This is where the magic happens! The security configuration defines:

#### A. URL Authorization Rules

```java
.authorizeHttpRequests(authorize -> authorize
    .requestMatchers("/", "/home").permitAll()  // Public pages
    .anyRequest().authenticated()                // Everything else requires login
)
```

**Key Points:**
- Order matters! Rules are evaluated top-to-bottom
- First match wins
- Use whitelist approach (explicitly allow, secure everything else)

#### B. Form Login Configuration

```java
.formLogin(form -> form
    .loginPage("/login")    // Custom login page
    .permitAll()            // Allow everyone to access login page
)
```

**What this enables:**
- Custom login page at `/login`
- Automatic redirect to login when accessing protected resources
- Saved request handling (redirect back after login)

#### C. User Details Service

```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();
    return new InMemoryUserDetailsManager(user);
}
```

**In Real Applications:**
- Replace InMemoryUserDetailsManager with database storage
- Use JdbcUserDetailsManager or custom implementation
- Load users from database tables
- Support user registration and management

#### D. Password Encoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Why BCrypt:**
- Industry standard for password hashing
- Automatically salted (prevents rainbow table attacks)
- Adaptive (can increase cost factor as hardware improves)
- Intentionally slow (makes brute-force attacks impractical)

### Step 5: Templates

#### home.html - Public Page
- Accessible without login
- Shows different content for authenticated vs unauthenticated users
- Uses `sec:authorize="isAuthenticated()"` for conditional display

#### hello.html - Protected Page
- Requires authentication
- Displays user information (`sec:authentication="name"`)
- Shows logout button
- Demonstrates saved request handling

#### login.html - Custom Login Page
- Custom branded login form
- Shows error messages (`th:if="${param.error}"`)
- Shows logout success message (`th:if="${param.logout}"`)
- CSRF token automatically included by Thymeleaf

## Key Concepts Explained

### 1. Authentication vs Authorization

- **Authentication**: *"Who are you?"*
  - Verifying identity (username/password, tokens, biometrics)
  - Proving you are who you claim to be
  - Example: Logging in with username and password

- **Authorization**: *"What are you allowed to do?"*
  - Checking permissions
  - Determining access to resources
  - Example: Only admins can access /admin pages

### 2. Security Filter Chain

Spring Security uses a chain of filters that intercept HTTP requests:

1. **SecurityContextPersistenceFilter**: Loads/stores SecurityContext from session
2. **CsrfFilter**: Validates CSRF tokens
3. **UsernamePasswordAuthenticationFilter**: Processes login forms
4. **BasicAuthenticationFilter**: Processes HTTP Basic authentication
5. **AuthorizationFilter**: Checks access permissions
6. **ExceptionTranslationFilter**: Handles security exceptions
7. And more...

Each filter performs a specific security task and can allow or block the request.

### 3. SecurityContext and SecurityContextHolder

- **SecurityContext**: Holds authentication information for current thread
- **SecurityContextHolder**: ThreadLocal that stores SecurityContext

```java
// Getting current authenticated user
SecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();
String username = authentication.getName();
```

### 4. CSRF Protection

**What is CSRF?**
Cross-Site Request Forgery is an attack where a malicious website tricks your browser into making unwanted requests to another site where you're authenticated.

**Example Attack (without CSRF protection):**
```html
<!-- Malicious website -->
<img src="https://yourbank.com/transfer?to=attacker&amount=1000">
```
If you're logged into your bank, this request would execute!

**Spring Security's Protection:**
- Requires a CSRF token for state-changing requests (POST, PUT, DELETE)
- Token is generated server-side and stored in session
- Forms must include the token
- Thymeleaf automatically adds tokens to forms

### 5. Password Encoding

**Never store plain text passwords!**

```java
// BAD - NEVER DO THIS
String password = "password123";

// GOOD - Always encode
PasswordEncoder encoder = new BCryptPasswordEncoder();
String encoded = encoder.encode("password123");
// Result: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

**BCrypt Hash Format:**
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
│││ ││ │                      ││
│││ ││ └─ Salt (22 chars)     └─ Hash (31 chars)
│││ │└─ Cost factor (10 = 2^10 = 1024 rounds)
│││ └─ Delimiter
││└─ BCrypt version
│└─ Prefix
```

### 6. Session Management

**How Sessions Work:**
1. User logs in successfully
2. Server creates HttpSession
3. Stores SecurityContext in session
4. Sends session ID to browser as cookie (JSESSIONID)
5. Browser sends cookie with each request
6. Server retrieves SecurityContext from session
7. User remains authenticated until:
   - Explicit logout
   - Session timeout (default: 30 minutes)
   - Server restart (in-memory sessions)

## Common Issues and Solutions

### Issue 1: Access Denied (403 Forbidden)

**Symptom:** Getting 403 error when accessing pages

**Possible Causes:**
1. **CSRF token missing** - Make sure forms use `th:action` and POST method
2. **Not authenticated** - User needs to log in first
3. **Insufficient permissions** - User doesn't have required role

**Solution:**
```java
// Check authorization rules in WebSecurityConfig.java
.authorizeHttpRequests(authorize -> authorize
    .requestMatchers("/public/**").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

### Issue 2: Redirect Loop on Login Page

**Symptom:** Endless redirects to /login

**Cause:** Login page URL requires authentication

**Solution:**
```java
.formLogin(form -> form
    .loginPage("/login")
    .permitAll()  // ← This is crucial!
)
```

### Issue 3: "Invalid CSRF Token"

**Symptom:** Form submission fails with CSRF error

**Solutions:**
1. Ensure form uses POST method (not GET)
2. Use Thymeleaf's `th:action` attribute (auto-adds token)
3. If using AJAX, include token in headers:
```javascript
// Get CSRF token from meta tag
const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

// Include in AJAX request
fetch('/api/endpoint', {
    method: 'POST',
    headers: {
        [header]: token,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
});
```

### Issue 4: Password Not Matching

**Symptom:** Login fails even with correct password

**Cause:** Password not encoded or wrong encoder used

**Solution:**
```java
// Make sure to encode passwords
UserDetails user = User.withUsername("user")
    .password(passwordEncoder().encode("password"))  // ← Must encode!
    .roles("USER")
    .build();
```

### Issue 5: Static Resources Not Loading

**Symptom:** CSS, JS, images not loading

**Solution:** Add static resources to public access:
```java
.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
```

## Testing the Application

### Manual Testing Steps

1. **Test Public Access**
   - Navigate to http://localhost:8080/
   - Verify you can see the home page without logging in

2. **Test Protected Resource**
   - Click "Try to Access Protected Content"
   - Verify redirect to login page
   - Note the URL includes saved request: `/login`

3. **Test Login with Wrong Credentials**
   - Enter wrong username/password
   - Verify error message appears
   - Verify redirect to `/login?error`

4. **Test Successful Login**
   - Enter correct credentials (user/password)
   - Verify redirect to originally requested page (/hello)
   - Verify username is displayed

5. **Test Authenticated Access**
   - Navigate to home page while logged in
   - Verify you see "You are logged in as: user"

6. **Test Logout**
   - Click logout button
   - Verify redirect to `/login?logout`
   - Verify success message
   - Try accessing /hello
   - Verify redirect to login (session cleared)

### Automated Testing (Future Enhancement)

```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void publicPageAccessibleWithoutLogin() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    @Test
    public void protectedPageRequiresLogin() throws Exception {
        mockMvc.perform(get("/hello"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    public void protectedPageAccessibleWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk());
    }
}
```

## Next Steps

After mastering this example, explore these topics:

### 1. Database Authentication
Replace InMemoryUserDetailsManager with JPA:
```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    // ... getters, setters
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Load from database
    }
}
```

### 2. Method-Level Security
Secure individual methods:
```java
@EnableMethodSecurity
@Configuration
public class MethodSecurityConfig {}

@Service
public class BankService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(Long id) {
        // Only admins can call this
    }

    @PostAuthorize("returnObject.owner == authentication.name")
    public Account getAccount(Long id) {
        // User can only see their own account
    }
}
```

### 3. OAuth2 / Social Login
Add Google/GitHub login:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### 4. JWT Authentication
Stateless authentication for REST APIs:
```java
@Configuration
public class JwtSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtAuthenticationFilter());
        return http.build();
    }
}
```

### 5. Remember-Me Functionality
```java
.rememberMe()
    .key("uniqueAndSecret")
    .tokenValiditySeconds(86400) // 24 hours
```

### 6. Custom Authentication Provider
```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) {
        // Custom authentication logic
        // Could integrate with LDAP, external API, etc.
    }
}
```

## Additional Resources

### Official Documentation
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Boot Security Auto-Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.security)
- [Thymeleaf + Spring Security](https://www.thymeleaf.org/doc/articles/springsecurity.html)

### Tutorials
- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture)
- [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)

### Books
- "Spring Security in Action" by Laurentiu Spilca
- "Spring Boot in Action" by Craig Walls

### Video Courses
- Java Brains - Spring Security (YouTube)
- Baeldung Spring Security Series

## Contributing

Found an issue or have a suggestion? Please open an issue or submit a pull request!

## License

This project is released under the MIT License. See LICENSE file for details.

---

**Happy Learning! Remember: Security is not optional, it's essential.**

If you have questions or run into issues, review the extensive inline comments in the source code. Every file is thoroughly documented to help you understand Spring Security concepts.
