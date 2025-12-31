# Spring Security Authorization Example

A comprehensive learning resource demonstrating all aspects of authorization in Spring Security, including HTTP request authorization, method security, Access Control Lists (ACLs), custom authorization managers, and authorization events.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Authorization Concepts Covered](#authorization-concepts-covered)
- [Test Users](#test-users)
- [API Endpoints](#api-endpoints)
- [Testing the Application](#testing-the-application)
- [Documentation](#documentation)
- [Learning Path](#learning-path)

## Overview

This project provides a complete, runnable example of Spring Security authorization features. It's designed as learning material to help developers understand and implement authorization in their Spring Boot applications.

**Based on Spring Security Reference Documentation:**
- [Authorization Architecture](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html)
- [Authorize HTTP Requests](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html)
- [Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [Access Control Lists](https://docs.spring.io/spring-security/reference/servlet/authorization/acls.html)
- [Authorization Events](https://docs.spring.io/spring-security/reference/servlet/authorization/events.html)

## Features

### 1. HTTP Request Authorization
- URL-based security with pattern matching
- Role-based access control (RBAC)
- Authority-based access control
- Custom authorization managers
- SpEL expression-based authorization

### 2. Method Security
- `@PreAuthorize` - Pre-invocation authorization
- `@PostAuthorize` - Post-invocation authorization
- `@PreFilter` - Filter method parameters
- `@PostFilter` - Filter method results
- `@Secured` - Simple role-based security
- `@RolesAllowed` - JSR-250 standard
- Custom security expression handlers

### 3. Access Control Lists (ACLs)
- Instance-level security
- Fine-grained permissions (READ, WRITE, DELETE, ADMIN)
- Domain object security
- Permission inheritance
- ACL caching with EhCache

### 4. Authorization Events
- Event listeners for granted/denied access
- Audit logging
- Security monitoring
- Custom event publishing

### 5. Custom Authorization
- Custom AuthorizationManager implementations
- Owner-based authorization
- Complex authorization logic
- Security evaluators

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## Getting Started

### 1. Clone and Navigate

```bash
cd authorization/spring-security-authorization-example
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Optional)

Visit `http://localhost:8080/h2-console`

**Connection details:**
- JDBC URL: `jdbc:h2:mem:securitydb`
- Username: `sa`
- Password: (leave blank)

## Project Structure

```
src/main/java/com/example/security/
├── SecurityAuthorizationApplication.java    # Main application
├── config/
│   ├── SecurityConfig.java                  # HTTP security configuration
│   ├── CustomUserDetailsService.java        # User authentication
│   └── DataInitializer.java                 # Sample data
├── authorization/
│   ├── CustomAuthorizationManager.java      # Custom authorization
│   └── OwnerAuthorizationManager.java       # Owner-based authorization
├── acl/
│   └── AclConfig.java                       # ACL configuration
├── controller/
│   ├── DemoController.java                  # HTTP authorization examples
│   ├── DocumentController.java              # Method security examples
│   └── AclController.java                   # ACL examples
├── service/
│   ├── DocumentService.java                 # Method security
│   ├── AclDocumentService.java              # ACL service
│   └── DocumentSecurityEvaluator.java       # Custom evaluator
├── event/
│   ├── AuthorizationEventListener.java      # Event listener
│   └── CustomAuthorizationEventPublisher.java
├── model/
│   ├── User.java                            # User entity
│   ├── Role.java                            # Role entity
│   └── Document.java                        # Document entity
└── repository/
    ├── UserRepository.java
    ├── RoleRepository.java
    └── DocumentRepository.java

docs/
├── 01-AUTHORIZATION-ARCHITECTURE.md         # Core concepts
├── 02-HTTP-REQUEST-AUTHORIZATION.md         # URL-based security
├── 03-METHOD-SECURITY.md                    # Method annotations
├── 04-ACCESS-CONTROL-LISTS.md               # ACL details
└── 05-AUTHORIZATION-EVENTS.md               # Event handling
```

## Authorization Concepts Covered

### 1. Authorization Architecture
- `AuthorizationManager` interface
- `AuthorizationDecision`
- Custom authorization logic
- See: [docs/01-AUTHORIZATION-ARCHITECTURE.md](docs/01-AUTHORIZATION-ARCHITECTURE.md)

### 2. HTTP Request Authorization
- Pattern matching with `requestMatchers()`
- `hasRole()`, `hasAuthority()`
- `permitAll()`, `authenticated()`, `denyAll()`
- Custom authorization managers
- See: [docs/02-HTTP-REQUEST-AUTHORIZATION.md](docs/02-HTTP-REQUEST-AUTHORIZATION.md)

### 3. Method Security
- Pre/Post authorization
- Pre/Post filtering
- SpEL expressions
- Custom security evaluators
- See: [docs/03-METHOD-SECURITY.md](docs/03-METHOD-SECURITY.md)

### 4. Access Control Lists (ACLs)
- Instance-level permissions
- ACL management
- `hasPermission()` expressions
- Permission inheritance
- See: [docs/04-ACCESS-CONTROL-LISTS.md](docs/04-ACCESS-CONTROL-LISTS.md)

### 5. Authorization Events
- Event listeners
- Audit logging
- Security monitoring
- Custom event publishing
- See: [docs/05-AUTHORIZATION-EVENTS.md](docs/05-AUTHORIZATION-EVENTS.md)

## Test Users

The application comes with pre-configured users for testing:

| Username   | Password   | Role      | Authorities |
|------------|------------|-----------|-------------|
| admin      | admin123   | ADMIN     | All authorities including DELETE_DOCUMENTS, READ_REPORTS, CUSTOM_ACCESS |
| user1      | password1  | USER      | READ_DOCUMENTS, WRITE_DOCUMENTS |
| user2      | password2  | USER      | READ_DOCUMENTS, WRITE_DOCUMENTS |
| moderator  | mod123     | MODERATOR | READ_DOCUMENTS, WRITE_DOCUMENTS, READ_REPORTS |
| viewer     | view123    | VIEWER    | READ_DOCUMENTS only |

## API Endpoints

### Public Endpoints (No Authentication)

```bash
# Public endpoint
curl http://localhost:8080/api/public/hello
```

### Role-Based Endpoints

```bash
# Admin only
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard

# User role required
curl -u user1:password1 http://localhost:8080/api/user/profile

# Admin or Moderator
curl -u moderator:mod123 http://localhost:8080/api/moderator/actions
```

### Authority-Based Endpoints

```bash
# Requires WRITE_DOCUMENTS authority
curl -u user1:password1 -X POST http://localhost:8080/api/documents/create \
  -H "Content-Type: application/json" \
  -d '{"title":"My Document","content":"Content here","owner":"user1"}'

# Requires DELETE_DOCUMENTS authority (admin only)
curl -u admin:admin123 -X DELETE http://localhost:8080/api/documents/delete/1
```

### Custom Authorization

```bash
# Requires CUSTOM_ACCESS authority (admin only)
curl -u admin:admin123 http://localhost:8080/api/custom/resource
```

### Method Security Examples

```bash
# Get document (ownership checked via @PostAuthorize)
curl -u user1:password1 http://localhost:8080/api/documents/1

# Get all documents (requires ADMIN or MODERATOR via @RolesAllowed)
curl -u admin:admin123 http://localhost:8080/api/documents/all

# Get my documents (filtered via @PostFilter)
curl -u user1:password1 http://localhost:8080/api/documents/my-documents
```

### ACL Examples

```bash
# Create document with ACL
curl -u user1:password1 -X POST http://localhost:8080/api/acl/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"ACL Document","content":"Protected content","owner":"user1"}'

# Grant READ permission to another user
curl -u user1:password1 -X POST http://localhost:8080/api/acl/documents/1/grant-read \
  -H "Content-Type: application/json" \
  -d '{"username":"user2"}'

# Access document (permission checked via ACL)
curl -u user2:password2 http://localhost:8080/api/acl/documents/1
```

## Testing the Application

### Using cURL

**Basic Authentication:**
```bash
curl -u username:password http://localhost:8080/api/endpoint
```

**POST Request:**
```bash
curl -u user1:password1 -X POST http://localhost:8080/api/documents/create \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","content":"Content","owner":"user1"}'
```

### Using Postman

1. Import the endpoints
2. Set Authorization Type: Basic Auth
3. Enter username and password
4. Send requests

### Expected Behaviors

**403 Forbidden:**
- User lacks required role/authority
- Failed ACL permission check
- Custom authorization denied

**401 Unauthorized:**
- No credentials provided
- Invalid credentials

**200 OK:**
- Authorization successful
- User has required permissions

## Documentation

Comprehensive documentation is available in the `docs/` directory:

1. **[Authorization Architecture](docs/01-AUTHORIZATION-ARCHITECTURE.md)**
   - Core concepts and components
   - Authorization flow
   - Best practices

2. **[HTTP Request Authorization](docs/02-HTTP-REQUEST-AUTHORIZATION.md)**
   - URL pattern matching
   - Role and authority-based security
   - Configuration examples

3. **[Method Security](docs/03-METHOD-SECURITY.md)**
   - All method security annotations
   - SpEL expressions
   - Custom security evaluators

4. **[Access Control Lists](docs/04-ACCESS-CONTROL-LISTS.md)**
   - ACL concepts and architecture
   - Configuration and usage
   - Permission management

5. **[Authorization Events](docs/05-AUTHORIZATION-EVENTS.md)**
   - Event types and handling
   - Audit logging
   - Security monitoring

## Learning Path

### Beginner

1. Start with HTTP Request Authorization
   - Read [docs/02-HTTP-REQUEST-AUTHORIZATION.md](docs/02-HTTP-REQUEST-AUTHORIZATION.md)
   - Explore `SecurityConfig.java`
   - Test with `DemoController.java` endpoints

2. Understand basic authentication
   - Review `CustomUserDetailsService.java`
   - Check `DataInitializer.java` for test users
   - Test different user roles

### Intermediate

3. Learn Method Security
   - Read [docs/03-METHOD-SECURITY.md](docs/03-METHOD-SECURITY.md)
   - Study `DocumentService.java`
   - Test `DocumentController.java` endpoints

4. Custom Authorization Logic
   - Review `CustomAuthorizationManager.java`
   - Understand `DocumentSecurityEvaluator.java`
   - Implement your own evaluator

### Advanced

5. Master Access Control Lists
   - Read [docs/04-ACCESS-CONTROL-LISTS.md](docs/04-ACCESS-CONTROL-LISTS.md)
   - Study `AclConfig.java`
   - Explore `AclDocumentService.java`
   - Test ACL endpoints

6. Authorization Events
   - Read [docs/05-AUTHORIZATION-EVENTS.md](docs/05-AUTHORIZATION-EVENTS.md)
   - Review `AuthorizationEventListener.java`
   - Implement custom event handlers

## Common Use Cases

### Use Case 1: Simple Web Application
- Use HTTP request authorization with roles
- Apply method security for service layer
- Implement authorization event logging

### Use Case 2: Multi-Tenant Application
- Use ACLs for tenant isolation
- Custom authorization managers for tenant checks
- Method security with tenant context

### Use Case 3: Document Management System
- ACLs for document permissions
- Owner-based authorization
- Sharing with specific permissions

### Use Case 4: Enterprise Application
- Role-based access control
- Authority-based permissions
- Audit logging with events
- Custom authorization logic

## Troubleshooting

### Authorization Not Working

1. Check `@EnableMethodSecurity` is present
2. Verify user has required roles/authorities
3. Check role prefix (`ROLE_` vs no prefix)
4. Enable debug logging

### ACLs Not Working

1. Verify ACL tables are created (check schema-acl.sql)
2. Check `MethodSecurityExpressionHandler` configuration
3. Ensure ACL permissions are granted
4. Check ACL cache configuration

### Events Not Firing

1. Verify event listener component is scanned
2. Check event publishing is enabled
3. Confirm correct event type is used
4. Enable event debug logging

## Additional Resources

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Security GitHub](https://github.com/spring-projects/spring-security)
- [Baeldung Spring Security Tutorials](https://www.baeldung.com/spring-security)

## Contributing

This is a learning resource. Feel free to:
- Add more examples
- Improve documentation
- Fix bugs
- Add test cases

## License

This project is created for educational purposes.

## Questions?

Review the comprehensive documentation in the `docs/` directory. Each document includes:
- Detailed explanations
- Code examples
- Best practices
- Common pitfalls
- Testing strategies

---

**Happy Learning!**

Start with the basics and progressively explore more advanced topics. Each feature is demonstrated with working code that you can test immediately.
