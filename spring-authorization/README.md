# Spring Security Authorization Example

This project demonstrates comprehensive Spring Security authorization features including:

- **Request-level authorization** (URL-based security)
- **Method-level security** (@PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed)
- **Role-based access control (RBAC)**
- **Expression-based access control** (SpEL expressions)
- **Custom authorization logic**
- **HTTP method-specific authorization**

## Features Demonstrated

### 1. Request-Level Authorization
URL patterns secured in `SecurityConfig.java`:
- Public endpoints accessible to everyone
- Admin endpoints restricted to ADMIN role
- Manager endpoints accessible to MANAGER or ADMIN
- HTTP method-specific rules (GET, POST, PUT, DELETE)

### 2. Method-Level Security
Three types of method security annotations:

#### @PreAuthorize (Most Flexible)
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('ADMIN') or @documentSecurity.isOwner(#id, authentication.name)")
```

#### @Secured (Simple)
```java
@Secured("ROLE_ADMIN")
```

#### @RolesAllowed (JSR-250 Standard)
```java
@RolesAllowed("ADMIN")
```

### 3. Custom Security Components
`DocumentSecurity.java` - Custom bean for complex authorization logic:
```java
@documentSecurity.isOwner(#id, authentication.name)
@documentSecurity.canAccess(#classification, #role)
```

### 4. Post-Authorization Checks
`@PostAuthorize` in `DocumentService.java` - validates after method execution:
```java
@PostAuthorize("hasRole('ADMIN') or returnObject.get().owner == authentication.name")
```

## Project Structure

```
spring-authorization/
├── src/main/java/com/example/authorization/
│   ├── AuthorizationApplication.java          # Main application class
│   ├── config/
│   │   └── SecurityConfig.java                # Security configuration
│   ├── controller/
│   │   ├── AdminController.java               # Admin endpoints
│   │   ├── DocumentController.java            # Document CRUD operations
│   │   ├── ManagerController.java             # Manager endpoints
│   │   ├── PublicController.java              # Public endpoints
│   │   └── UserController.java                # User endpoints
│   ├── model/
│   │   ├── Document.java                      # Document entity
│   │   ├── DocumentRepository.java            # JPA repository
│   │   └── User.java                          # User model
│   ├── security/
│   │   ├── CustomUserDetailsService.java      # User authentication
│   │   └── DocumentSecurity.java              # Custom authorization logic
│   └── service/
│       ├── DataInitializer.java               # Sample data
│       └── DocumentService.java               # Business logic with security
└── src/main/resources/
    └── application.properties                 # Application configuration
```

## Available Users

The application includes pre-configured users with different roles:

| Username | Password    | Roles                                  |
|----------|-------------|----------------------------------------|
| admin    | admin123    | ROLE_ADMIN, ROLE_MANAGER, ROLE_USER   |
| manager  | manager123  | ROLE_MANAGER, ROLE_USER               |
| user     | user123     | ROLE_USER                             |
| guest    | guest123    | ROLE_GUEST                            |

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run
```bash
cd spring-authorization
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Public Endpoints (No Authentication Required)

```bash
# Public hello endpoint
curl http://localhost:8080/api/public/hello

# Public info endpoint
curl http://localhost:8080/api/public/info

# Public documents
curl http://localhost:8080/api/documents/public
```

### User Endpoints (Requires USER Role)

```bash
# Get user profile
curl -u user:user123 http://localhost:8080/api/user/profile

# User dashboard
curl -u user:user123 http://localhost:8080/api/user/dashboard

# Get my documents
curl -u user:user123 http://localhost:8080/api/documents/my-documents

# Create document
curl -u user:user123 -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"My Document","content":"Document content","classification":"PUBLIC"}'
```

### Manager Endpoints (Requires MANAGER or ADMIN Role)

```bash
# Manager dashboard
curl -u manager:manager123 http://localhost:8080/api/manager/dashboard

# Manager reports
curl -u manager:manager123 http://localhost:8080/api/manager/reports

# Get all documents
curl -u manager:manager123 http://localhost:8080/api/documents

# Get confidential documents
curl -u manager:manager123 http://localhost:8080/api/documents/confidential
```

### Admin Endpoints (Requires ADMIN Role)

```bash
# Admin dashboard
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard

# Admin settings
curl -u admin:admin123 http://localhost:8080/api/admin/settings

# Get all users
curl -u admin:admin123 http://localhost:8080/api/admin/users

# System info
curl -u admin:admin123 http://localhost:8080/api/admin/system-info

# Get secret documents
curl -u admin:admin123 http://localhost:8080/api/documents/secret

# Delete all documents (ADMIN only)
curl -u admin:admin123 -X DELETE http://localhost:8080/api/documents
```

### Document Operations

```bash
# Get document by ID (owner or admin)
curl -u user:user123 http://localhost:8080/api/documents/1

# Update document (owner or admin)
curl -u user:user123 -X PUT http://localhost:8080/api/documents/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Title","content":"Updated content","classification":"PUBLIC"}'

# Delete document (manager or admin)
curl -u manager:manager123 -X DELETE http://localhost:8080/api/documents/1
```

## Authorization Testing Examples

### Test 1: Access Denied - Guest trying to access user endpoint
```bash
curl -u guest:guest123 http://localhost:8080/api/user/dashboard
# Expected: 403 Forbidden
```

### Test 2: Access Denied - User trying to access admin endpoint
```bash
curl -u user:user123 http://localhost:8080/api/admin/dashboard
# Expected: 403 Forbidden
```

### Test 3: Access Denied - User trying to view secret documents
```bash
curl -u user:user123 http://localhost:8080/api/documents/secret
# Expected: 403 Forbidden
```

### Test 4: Access Granted - Admin can access everything
```bash
curl -u admin:admin123 http://localhost:8080/api/documents/secret
# Expected: 200 OK with secret documents
```

### Test 5: Owner-based authorization - Delete own document
```bash
# Create document as user
RESPONSE=$(curl -u user:user123 -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"My Doc","content":"Content","classification":"PUBLIC"}')

# Extract ID from response (assuming ID is in response)
# Delete as owner
curl -u user:user123 -X DELETE http://localhost:8080/api/documents/4
# Expected: 204 No Content (if user is owner)
```

## Key Security Concepts Demonstrated

### 1. URL-Based Authorization (SecurityConfig.java)
```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.DELETE, "/api/documents/**").hasAnyRole("MANAGER", "ADMIN")
```

### 2. Method-Level Authorization
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
@PreAuthorize("hasRole('ADMIN') or @documentSecurity.isOwner(#id, authentication.name)")
```

### 3. SpEL Expressions
```java
@PreAuthorize("hasRole('ADMIN') and isAuthenticated()")
@PostAuthorize("returnObject.get().owner == authentication.name")
```

### 4. Custom Authorization Logic
```java
@Component("documentSecurity")
public class DocumentSecurity {
    public boolean isOwner(Long documentId, String username) {
        // Custom logic
    }
}
```

## Database Access

H2 Console is available at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:authdb`
- Username: `sa`
- Password: (leave empty)

## Learning Resources

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/servlet/authorization/index.html)
- [Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [Expression-Based Access Control](https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html)

## Common Authorization Patterns

### Pattern 1: Role Hierarchy
Admin inherits all Manager and User permissions through role assignments.

### Pattern 2: Owner-Based Access
Users can access their own resources, admins can access everything.

### Pattern 3: Classification-Based Access
- PUBLIC: Everyone
- CONFIDENTIAL: Manager and Admin
- SECRET: Admin only

### Pattern 4: HTTP Method-Based
- GET: More permissive
- POST/PUT: Requires specific roles
- DELETE: Most restrictive

## Troubleshooting

### 403 Forbidden Error
- Check user credentials
- Verify user has required role
- Check SecurityConfig URL patterns
- Review method-level annotations

### Authentication Required
- Ensure you're providing credentials with `-u username:password`
- Check if endpoint requires authentication

### Debug Mode
Enable debug logging in `application.properties`:
```properties
logging.level.org.springframework.security=DEBUG
```

## Next Steps

To extend this example:
1. Add database-backed users instead of in-memory
2. Implement JWT-based authentication
3. Add permission-based authorization (beyond roles)
4. Implement audit logging for security events
5. Add integration tests with `@WithMockUser`

## License

This is an example project for educational purposes.
