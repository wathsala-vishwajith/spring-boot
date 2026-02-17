# Architecture Documentation

## Overview

This document provides a detailed explanation of the JWT authentication system architecture, design decisions, and component interactions.

## System Architecture

### High-Level Architecture

```
┌─────────────┐        ┌──────────────────┐        ┌─────────────┐
│   Client    │◄──────►│  Spring Boot     │◄──────►│  Database   │
│  (Browser,  │  HTTP  │  Application     │  JPA   │    (H2)     │
│   Mobile)   │        │                  │        │             │
└─────────────┘        └──────────────────┘        └─────────────┘
                              │
                              │
                    ┌─────────┴─────────┐
                    │                   │
              ┌─────▼──────┐    ┌──────▼─────┐
              │  Security  │    │    JWT     │
              │   Filter   │    │   Utility  │
              │   Chain    │    │            │
              └────────────┘    └────────────┘
```

## Component Architecture

### 1. Presentation Layer

#### Controllers

**AuthController** (`controller/AuthController.java`)
- Handles authentication-related requests
- Endpoints:
  - `POST /api/auth/register` - User registration
  - `POST /api/auth/login` - User authentication
- Validates input using `@Valid` annotation
- Returns appropriate HTTP responses

**DemoController** (`controller/DemoController.java`)
- Demonstrates protected and public endpoints
- Endpoints:
  - `GET /api/public/hello` - Public endpoint
  - `GET /api/private/hello` - Protected endpoint
  - `GET /api/user/profile` - User-specific endpoint
  - `GET /api/admin/dashboard` - Admin-only endpoint
- Uses `@PreAuthorize` for role-based access control

### 2. Service Layer

#### AuthService (`service/AuthService.java`)

**Responsibilities:**
- User registration logic
- User authentication
- JWT token generation orchestration
- Business rule validation

**Key Methods:**
```java
public String register(RegisterRequest request)
public AuthResponse authenticate(AuthRequest request)
```

#### CustomUserDetailsService (`service/CustomUserDetailsService.java`)

**Responsibilities:**
- Implements Spring Security's `UserDetailsService`
- Loads user details from database
- Converts User entity to Spring Security UserDetails

**Integration:**
- Called by Spring Security during authentication
- Provides user information for JWT generation
- Handles user not found scenarios

### 3. Security Layer

#### SecurityConfig (`config/SecurityConfig.java`)

**Configuration:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
```

**Key Configurations:**
1. **CSRF Protection**: Disabled for stateless API
2. **Session Management**: Stateless
3. **Authorization Rules**:
   - Public: `/api/auth/**`, `/api/public/**`
   - Protected: All other `/api/**` endpoints
4. **Password Encoder**: BCryptPasswordEncoder
5. **Authentication Manager**: DaoAuthenticationProvider

#### JwtAuthenticationFilter (`filter/JwtAuthenticationFilter.java`)

**Flow:**
```
1. Extract Authorization header
2. Check for "Bearer " prefix
3. Extract JWT token
4. Validate token
5. Load user details
6. Set authentication in SecurityContext
7. Continue filter chain
```

**Key Features:**
- Extends `OncePerRequestFilter`
- Executes once per request
- Non-blocking error handling
- Integrates with Spring Security context

### 4. Utility Layer

#### JwtUtil (`util/JwtUtil.java`)

**Core Functions:**

1. **Token Generation:**
```java
public String generateToken(UserDetails userDetails)
public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails)
```

2. **Token Validation:**
```java
public Boolean validateToken(String token, UserDetails userDetails)
```

3. **Claim Extraction:**
```java
public String extractUsername(String token)
public Date extractExpiration(String token)
public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
```

**Token Structure:**
- **Algorithm**: HS256 (HMAC with SHA-256)
- **Claims**:
  - `sub`: Username (subject)
  - `iat`: Issued at timestamp
  - `exp`: Expiration timestamp
- **Signature**: HMAC SHA-256

### 5. Data Layer

#### Model

**User Entity** (`model/User.java`)
```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String username;
    private String password;  // BCrypt hashed
    private String email;
    private Set<String> roles;
    private boolean enabled;
}
```

**Relationships:**
- User 1:N Roles (via @ElementCollection)

#### Repository

**UserRepository** (`repository/UserRepository.java`)
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

### 6. DTOs (Data Transfer Objects)

**Purpose:** Decouple API contracts from internal models

**AuthRequest**
```json
{
  "username": "string",
  "password": "string"
}
```

**RegisterRequest**
```json
{
  "username": "string (3-20 chars)",
  "password": "string (min 6 chars)",
  "email": "string (valid email)"
}
```

**AuthResponse**
```json
{
  "token": "JWT token",
  "type": "Bearer",
  "username": "string",
  "message": "string"
}
```

## Request Flow Diagrams

### Authentication Flow

```
┌──────┐                   ┌────────────┐                 ┌──────────┐
│Client│                   │AuthController              │AuthService│
└──┬───┘                   └─────┬──────┘                └────┬─────┘
   │ POST /api/auth/login        │                            │
   │────────────────────────────►│                            │
   │  {username, password}       │                            │
   │                             │ authenticate(request)       │
   │                             │───────────────────────────►│
   │                             │                            │
   │                             │        ┌──────────────┐    │
   │                             │        │Authentication│    │
   │                             │        │   Manager    │    │
   │                             │        └──────┬───────┘    │
   │                             │               │            │
   │                             │        validate credentials│
   │                             │               │            │
   │                             │        ┌──────▼───────┐    │
   │                             │        │CustomUser    │    │
   │                             │        │DetailsService│    │
   │                             │        └──────┬───────┘    │
   │                             │               │            │
   │                             │        ┌──────▼───────┐    │
   │                             │        │UserRepository│    │
   │                             │        └──────┬───────┘    │
   │                             │               │            │
   │                             │        load user from DB   │
   │                             │               │            │
   │                             │        ┌──────▼───────┐    │
   │                             │        │   JwtUtil    │    │
   │                             │        └──────┬───────┘    │
   │                             │               │            │
   │                             │        generate JWT token  │
   │                             │               │            │
   │                             │◄──────────────┘            │
   │                             │◄───────────────────────────┘
   │◄────────────────────────────│
   │  {token, username}          │
   │                             │
```

### Authorization Flow

```
┌──────┐              ┌─────────────┐        ┌──────────┐         ┌──────────┐
│Client│              │JwtAuthFilter│        │ JwtUtil  │         │Controller│
└──┬───┘              └──────┬──────┘        └────┬─────┘         └────┬─────┘
   │ GET /api/private/hello  │                    │                    │
   │ Authorization: Bearer..│                    │                    │
   │────────────────────────►│                    │                    │
   │                         │                    │                    │
   │                         │ extract token      │                    │
   │                         │                    │                    │
   │                         │ validateToken()    │                    │
   │                         │───────────────────►│                    │
   │                         │                    │                    │
   │                         │ extractUsername()  │                    │
   │                         │───────────────────►│                    │
   │                         │                    │                    │
   │                         │ extractExpiration()│                    │
   │                         │───────────────────►│                    │
   │                         │                    │                    │
   │                         │◄───────────────────┤                    │
   │                         │    valid           │                    │
   │                         │                    │                    │
   │                         │ set SecurityContext│                    │
   │                         │                    │                    │
   │                         │ proceed            │                    │
   │                         │───────────────────────────────────────►│
   │                         │                    │                    │
   │                         │                    │     process request│
   │                         │                    │                    │
   │◄────────────────────────┴────────────────────┴────────────────────┤
   │         {message, username}                  │                    │
   │                         │                    │                    │
```

## Security Design

### Token-Based Authentication

**Benefits:**
1. **Stateless**: No server-side session storage
2. **Scalable**: Easy horizontal scaling
3. **Cross-Domain**: Works across different domains
4. **Mobile-Friendly**: Easy to use in mobile apps

**Considerations:**
1. **Token Storage**: Client must store securely
2. **Token Expiration**: Tokens expire after 24 hours
3. **Token Revocation**: No built-in revocation (consider Redis)
4. **Token Size**: Larger than session IDs

### Password Security

- **Algorithm**: BCrypt
- **Strength**: 10 rounds (default)
- **Salting**: Automatic per password
- **No Plain Text**: Passwords never stored in plain text

### HTTPS Requirements

⚠️ **Production Requirement**: Always use HTTPS in production!

Reasons:
- JWT tokens in Authorization header
- Credentials in login requests
- Man-in-the-middle attack prevention

## Database Design

### Entity-Relationship Diagram

```
┌─────────────────────────────┐
│          User               │
├─────────────────────────────┤
│ id: Long (PK)               │
│ username: String (UNIQUE)   │
│ password: String (BCrypt)   │
│ email: String (UNIQUE)      │
│ enabled: Boolean            │
└─────────────┬───────────────┘
              │
              │ 1:N
              │
              ▼
┌─────────────────────────────┐
│       UserRoles             │
├─────────────────────────────┤
│ user_id: Long (FK)          │
│ role: String                │
└─────────────────────────────┘
```

## Configuration Management

### Application Properties

**Development:**
```properties
spring.jpa.show-sql=true
logging.level.org.springframework.security=DEBUG
spring.h2.console.enabled=true
```

**Production Recommendations:**
```properties
spring.jpa.show-sql=false
logging.level.org.springframework.security=INFO
spring.h2.console.enabled=false
jwt.secret=${JWT_SECRET}  # Environment variable
```

## Design Patterns Used

### 1. Dependency Injection
- Constructor injection for all dependencies
- `@Autowired` on constructors
- Promotes testability and loose coupling

### 2. Repository Pattern
- `UserRepository` extends `JpaRepository`
- Abstracts data access logic
- Easy to mock for testing

### 3. Filter Chain Pattern
- `JwtAuthenticationFilter` in security filter chain
- Sequential processing of requests
- Separation of concerns

### 4. DTO Pattern
- Separate DTOs for requests and responses
- Validation at DTO level
- API version independence

### 5. Service Layer Pattern
- Business logic in service classes
- Controllers delegate to services
- Reusable business operations

## Error Handling Strategy

### Authentication Errors
```java
try {
    authService.authenticate(request);
} catch (Exception e) {
    return ResponseEntity.badRequest()
        .body(new MessageResponse("Invalid username or password"));
}
```

### Validation Errors
- Handled by `@Valid` annotation
- Returns 400 Bad Request
- Includes validation messages

### JWT Token Errors
- Invalid token: 403 Forbidden
- Expired token: 403 Forbidden
- Missing token: 403 Forbidden

## Performance Considerations

### Token Validation
- O(1) token parsing
- Cached signing key
- Minimal database access

### Database Queries
- Indexed username lookup
- Eager fetch for roles
- Connection pooling

### Caching Opportunities
1. User details (after authentication)
2. JWT signing key
3. Role-permission mappings

## Testing Strategy

### Unit Tests
- Service layer logic
- JWT utility methods
- Custom validators

### Integration Tests
- Controller endpoints
- Security configuration
- Database operations

### Security Tests
- Invalid token handling
- Expired token handling
- Unauthorized access attempts

## Deployment Considerations

### Environment Variables
```bash
export JWT_SECRET="your-production-secret-key"
export DB_URL="jdbc:postgresql://localhost:5432/mydb"
export DB_USERNAME="dbuser"
export DB_PASSWORD="dbpass"
```

### Health Checks
- Endpoint: `/actuator/health`
- Database connectivity
- Application status

### Monitoring
- Request/response logging
- Security event logging
- Performance metrics

## Future Enhancements

1. **Token Refresh**: Implement refresh token mechanism
2. **Token Blacklist**: Redis-based token revocation
3. **OAuth2**: Add OAuth2 support
4. **Multi-factor Auth**: Add 2FA support
5. **Rate Limiting**: Prevent brute force attacks
6. **Audit Logging**: Track security events
7. **Role Management**: Dynamic role assignment
8. **Password Policy**: Enforce strong passwords

## Conclusion

This architecture provides a solid foundation for JWT authentication in Spring Boot microservices. It follows best practices and can be extended to meet specific requirements.
