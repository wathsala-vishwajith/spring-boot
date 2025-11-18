# HTTP Request Authorization

## Overview

HTTP request authorization (also called URL-based security) protects web endpoints based on URL patterns and HTTP methods. This is configured in the `SecurityFilterChain`.

## Basic Configuration

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        // Configuration here
    );
    return http.build();
}
```

## Request Matchers

### 1. Pattern-Based Matching

```java
// Ant-style patterns
.requestMatchers("/api/public/**").permitAll()          // Matches all under /api/public
.requestMatchers("/admin/*").hasRole("ADMIN")           // Single level only
.requestMatchers("/**/*.jpg").permitAll()               // All JPG files

// Exact match
.requestMatchers("/login").permitAll()

// Multiple patterns
.requestMatchers("/api/v1/**", "/api/v2/**").authenticated()
```

### 2. HTTP Method-Based Matching

```java
.requestMatchers(HttpMethod.GET, "/api/documents/**").hasAuthority("READ_DOCUMENTS")
.requestMatchers(HttpMethod.POST, "/api/documents/**").hasAuthority("WRITE_DOCUMENTS")
.requestMatchers(HttpMethod.DELETE, "/api/documents/**").hasAuthority("DELETE_DOCUMENTS")
```

### 3. MVC Pattern Matching

```java
// More precise for Spring MVC
.requestMatchers(new MvcRequestMatcher.Builder(null)
    .servletPath("/api")
    .pattern("/documents/**"))
    .hasRole("USER")
```

## Authorization Rules

### 1. permitAll()

Allows access to everyone (authenticated or not).

```java
.requestMatchers("/", "/public/**", "/css/**", "/js/**").permitAll()
```

**Use Cases:**
- Public resources (CSS, JS, images)
- Landing pages
- Login/registration endpoints
- Health check endpoints

### 2. authenticated()

Requires authentication but no specific role/authority.

```java
.requestMatchers("/profile/**").authenticated()
```

### 3. hasRole() / hasAnyRole()

Requires specific role(s). Automatically adds "ROLE_" prefix.

```java
// Single role
.requestMatchers("/admin/**").hasRole("ADMIN")

// Multiple roles (OR condition)
.requestMatchers("/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
```

**Important:**
- `hasRole("ADMIN")` expects authority `ROLE_ADMIN`
- Don't include "ROLE_" prefix in the parameter

### 4. hasAuthority() / hasAnyAuthority()

Requires specific authority/permission. No automatic prefix.

```java
// Single authority
.requestMatchers("/documents/create").hasAuthority("WRITE_DOCUMENTS")

// Multiple authorities (OR condition)
.requestMatchers("/reports/**").hasAnyAuthority("READ_REPORTS", "WRITE_REPORTS")
```

### 5. access()

Uses SpEL expressions or custom `AuthorizationManager` for complex logic.

```java
// SpEL expression
.requestMatchers("/api/advanced/**")
    .access((authentication, context) ->
        new AuthorizationDecision(
            authentication.get().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                              a.getAuthority().equals("ADVANCED_ACCESS"))
        ))

// Custom AuthorizationManager
.requestMatchers("/api/custom/**").access(customAuthorizationManager)
```

### 6. denyAll()

Denies all access.

```java
.requestMatchers("/internal/**").denyAll()
```

## Order Matters!

Rules are evaluated in order. More specific patterns must come first.

```java
// ✅ Correct - specific to general
.requestMatchers("/admin/super").hasRole("SUPER_ADMIN")
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/api/**").authenticated()
.anyRequest().denyAll()

// ❌ Wrong - general pattern matches first
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/admin/super").hasRole("SUPER_ADMIN")  // Never reached!
```

## Complete Example

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public resources
                .requestMatchers("/", "/home", "/public/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // Authentication endpoints
                .requestMatchers("/login", "/register").permitAll()

                // API endpoints with method-specific security
                .requestMatchers(HttpMethod.GET, "/api/documents/**")
                    .hasAuthority("READ_DOCUMENTS")
                .requestMatchers(HttpMethod.POST, "/api/documents/**")
                    .hasAuthority("WRITE_DOCUMENTS")
                .requestMatchers(HttpMethod.DELETE, "/api/documents/**")
                    .hasAuthority("DELETE_DOCUMENTS")

                // Role-based endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/moderator/**").hasAnyRole("ADMIN", "MODERATOR")

                // Custom authorization
                .requestMatchers("/custom/**").access(customAuthorizationManager)

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }
}
```

## Common Patterns

### Pattern 1: RESTful API Security

```java
.authorizeHttpRequests(authorize -> authorize
    .requestMatchers(HttpMethod.GET, "/api/**").hasAuthority("READ")
    .requestMatchers(HttpMethod.POST, "/api/**").hasAuthority("CREATE")
    .requestMatchers(HttpMethod.PUT, "/api/**").hasAuthority("UPDATE")
    .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("DELETE")
)
```

### Pattern 2: Admin Section

```java
.authorizeHttpRequests(authorize -> authorize
    .requestMatchers("/admin/users/**").hasRole("USER_ADMIN")
    .requestMatchers("/admin/settings/**").hasRole("SETTINGS_ADMIN")
    .requestMatchers("/admin/**").hasRole("ADMIN")
)
```

### Pattern 3: Public API with Rate Limiting

```java
.authorizeHttpRequests(authorize -> authorize
    .requestMatchers("/api/public/**").access(rateLimitAuthorizationManager)
    .requestMatchers("/api/**").authenticated()
)
```

## Testing Authorization Rules

```java
@Test
@WithMockUser(roles = "USER")
void userCanAccessUserEndpoint() throws Exception {
    mockMvc.perform(get("/user/profile"))
        .andExpect(status().isOk());
}

@Test
@WithMockUser(roles = "USER")
void userCannotAccessAdminEndpoint() throws Exception {
    mockMvc.perform(get("/admin/dashboard"))
        .andExpect(status().isForbidden());
}

@Test
void unauthenticatedUserGets401() throws Exception {
    mockMvc.perform(get("/user/profile"))
        .andExpect(status().isUnauthorized());
}
```

## Best Practices

1. **Deny by Default**: Use `anyRequest().authenticated()` or `denyAll()`
2. **Specific First**: Order rules from specific to general
3. **Separate Public Resources**: Group public resources together
4. **Use Constants**: Define URL patterns as constants for reusability
5. **Document Decisions**: Comment complex authorization logic
6. **Test Thoroughly**: Test both positive and negative cases

## Common Mistakes

1. **Wrong Order**: General patterns before specific ones
2. **Missing anyRequest()**: Leaves some URLs unprotected
3. **Role Prefix**: Using `hasRole("ROLE_ADMIN")` instead of `hasRole("ADMIN")`
4. **HTTP Method Ignored**: Not considering that GET and POST have different security
5. **Overly Permissive**: Using `permitAll()` too liberally

## Performance Considerations

1. **Pattern Complexity**: Complex patterns are slower to match
2. **Number of Rules**: Many rules increase evaluation time
3. **Custom Managers**: Expensive operations in custom `AuthorizationManager`
4. **Database Queries**: Avoid database calls in authorization logic

## See Also

- [Authorization Architecture](01-AUTHORIZATION-ARCHITECTURE.md)
- [Method Security](03-METHOD-SECURITY.md)
- [Custom Authorization Managers](../src/main/java/com/example/security/authorization/)
