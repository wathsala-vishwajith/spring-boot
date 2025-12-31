# Spring Security Authorization Architecture

## Overview

Spring Security's authorization architecture determines whether an authenticated user can access a specific resource or perform a particular action. This document explains the core components and concepts.

## Core Components

### 1. AuthorizationManager

The `AuthorizationManager` is the central interface for making authorization decisions in Spring Security 6.x+.

```java
public interface AuthorizationManager<T> {
    AuthorizationDecision check(Supplier<Authentication> authentication, T object);
}
```

**Key Points:**
- Replaces the deprecated `AccessDecisionManager` from earlier versions
- Takes an `Authentication` and a secured object
- Returns an `AuthorizationDecision` (granted or denied)
- Can be customized for complex authorization logic

**Example Implementation:**

```java
@Component
public class CustomAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(
        Supplier<Authentication> authentication,
        RequestAuthorizationContext context) {

        Authentication auth = authentication.get();

        // Custom authorization logic
        boolean hasAuthority = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("CUSTOM_ACCESS"));

        return new AuthorizationDecision(hasAuthority);
    }
}
```

### 2. AuthorizationDecision

Represents the result of an authorization check.

```java
public class AuthorizationDecision {
    private final boolean granted;

    public AuthorizationDecision(boolean granted) {
        this.granted = granted;
    }

    public boolean isGranted() {
        return this.granted;
    }
}
```

**Usage:**
- `new AuthorizationDecision(true)` - Access granted
- `new AuthorizationDecision(false)` - Access denied

### 3. Authentication

Contains information about the authenticated principal:
- Username/Principal
- Credentials
- Authorities (roles and permissions)
- Additional details

### 4. GrantedAuthority

Represents a permission granted to the principal.

```java
public interface GrantedAuthority {
    String getAuthority();
}
```

**Common Implementations:**
- `SimpleGrantedAuthority` - Basic string-based authority
- Role-based: `ROLE_ADMIN`, `ROLE_USER`
- Permission-based: `READ_DOCUMENTS`, `WRITE_DOCUMENTS`

## Authorization Flow

```
1. Request arrives
   ↓
2. Authentication Filter authenticates user
   ↓
3. SecurityContext stores Authentication
   ↓
4. AuthorizationManager.check() is invoked
   ↓
5. Decision made based on:
   - User's authorities
   - Request attributes
   - Custom logic
   ↓
6. If granted → Continue to controller
   If denied → AccessDeniedException → 403 Forbidden
```

## Types of Authorization

### 1. URL-Based Authorization

Secures HTTP endpoints based on URL patterns.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/public/**").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/**").authenticated()
        .anyRequest().denyAll()
    );
    return http.build();
}
```

### 2. Method-Level Authorization

Secures individual methods using annotations.

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
    // Method implementation
}
```

### 3. Domain Object Security (ACLs)

Secures individual instances of domain objects.

```java
@PreAuthorize("hasPermission(#id, 'Document', 'READ')")
public Document getDocument(Long id) {
    return documentRepository.findById(id);
}
```

## Authorization Patterns

### Pattern 1: Role-Based Access Control (RBAC)

Users are assigned roles, and roles determine access.

```java
// Configuration
.requestMatchers("/admin/**").hasRole("ADMIN")

// Method security
@Secured("ROLE_ADMIN")
public void adminMethod() { }
```

### Pattern 2: Authority-Based Access Control

More fine-grained than roles, using specific permissions.

```java
// Configuration
.requestMatchers("/documents/create").hasAuthority("WRITE_DOCUMENTS")

// Method security
@PreAuthorize("hasAuthority('DELETE_DOCUMENTS')")
public void deleteDocument(Long id) { }
```

### Pattern 3: Expression-Based Access Control

Uses SpEL expressions for complex logic.

```java
@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
public void updateProfile(String username) { }
```

### Pattern 4: Custom Authorization Logic

Implement custom `AuthorizationManager` for complex scenarios.

```java
.requestMatchers("/api/custom/**").access(customAuthorizationManager)
```

## Best Practices

1. **Principle of Least Privilege**: Grant minimum necessary permissions
2. **Defense in Depth**: Use multiple layers (URL + method security)
3. **Deny by Default**: Explicitly permit rather than explicitly deny
4. **Separate Concerns**: Keep authorization logic separate from business logic
5. **Audit and Monitor**: Log authorization decisions for security analysis

## Common Pitfalls

1. **Forgetting @EnableMethodSecurity**: Method annotations won't work
2. **Role Prefix Confusion**: `hasRole("ADMIN")` expects `ROLE_ADMIN`
3. **Order Matters**: More specific patterns must come before general ones
4. **Caching Issues**: Be aware of caching when checking permissions
5. **Performance**: Complex authorization logic can impact performance

## Security Considerations

1. **Never trust client-side authorization**: Always enforce server-side
2. **Sensitive data in logs**: Be careful with authorization event logging
3. **Session fixation**: Ensure proper session management
4. **Time-based access**: Consider time-sensitive permissions
5. **Delegation**: Be cautious with user impersonation features

## See Also

- [HTTP Request Authorization](02-HTTP-REQUEST-AUTHORIZATION.md)
- [Method Security](03-METHOD-SECURITY.md)
- [Access Control Lists](04-ACCESS-CONTROL-LISTS.md)
- [Authorization Events](05-AUTHORIZATION-EVENTS.md)
