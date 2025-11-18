# Quick Start Guide

## 5-Minute Setup

### 1. Build and Run

```bash
cd authorization/spring-security-authorization-example
mvn clean install
mvn spring-boot:run
```

Application starts at: `http://localhost:8080`

### 2. Test Authentication

```bash
# Public endpoint (no auth required)
curl http://localhost:8080/api/public/hello

# Admin endpoint
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard

# User endpoint
curl -u user1:password1 http://localhost:8080/api/user/profile
```

### 3. Test Authorization Patterns

#### Role-Based Access
```bash
# Success - admin has ADMIN role
curl -u admin:admin123 http://localhost:8080/api/admin/dashboard

# Failure - user1 doesn't have ADMIN role (gets 403)
curl -u user1:password1 http://localhost:8080/api/admin/dashboard
```

#### Authority-Based Access
```bash
# Create document - requires WRITE_DOCUMENTS authority
curl -u user1:password1 -X POST http://localhost:8080/api/documents/create \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Doc","content":"Content","owner":"user1"}'

# Delete document - requires DELETE_DOCUMENTS authority (admin only)
curl -u admin:admin123 -X DELETE http://localhost:8080/api/documents/delete/1
```

#### Method Security
```bash
# Get document - ownership checked via @PostAuthorize
curl -u user1:password1 http://localhost:8080/api/documents/1

# Get all - requires ADMIN or MODERATOR via @RolesAllowed
curl -u admin:admin123 http://localhost:8080/api/documents/all
```

#### ACL (Access Control Lists)
```bash
# 1. Create document with ACL (user1)
curl -u user1:password1 -X POST http://localhost:8080/api/acl/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"Secret Doc","content":"Private","owner":"user1"}'

# 2. Try to access as user2 (fails - no permission)
curl -u user2:password2 http://localhost:8080/api/acl/documents/1

# 3. Grant READ permission to user2
curl -u user1:password1 -X POST http://localhost:8080/api/acl/documents/1/grant-read \
  -H "Content-Type: application/json" \
  -d '{"username":"user2"}'

# 4. Access as user2 (succeeds now)
curl -u user2:password2 http://localhost:8080/api/acl/documents/1
```

## Test Users Reference

| Username | Password | Role | Key Authorities |
|----------|----------|------|-----------------|
| admin | admin123 | ADMIN | All (including DELETE_DOCUMENTS) |
| user1 | password1 | USER | READ_DOCUMENTS, WRITE_DOCUMENTS |
| user2 | password2 | USER | READ_DOCUMENTS, WRITE_DOCUMENTS |
| moderator | mod123 | MODERATOR | READ_DOCUMENTS, WRITE_DOCUMENTS, READ_REPORTS |
| viewer | view123 | VIEWER | READ_DOCUMENTS only |

## Common Scenarios

### Scenario 1: User Can Only Access Their Own Documents

```bash
# user1 creates a document
curl -u user1:password1 -X POST http://localhost:8080/api/documents/create \
  -H "Content-Type: application/json" \
  -d '{"title":"User1 Doc","content":"Private to user1","owner":"user1"}'

# user1 can access (owner)
curl -u user1:password1 http://localhost:8080/api/documents/1

# user2 cannot access (403 Forbidden)
curl -u user2:password2 http://localhost:8080/api/documents/1

# admin can access (has admin role)
curl -u admin:admin123 http://localhost:8080/api/documents/1
```

### Scenario 2: Role-Based Moderation

```bash
# Moderator actions - requires ADMIN or MODERATOR role
curl -u moderator:mod123 http://localhost:8080/api/moderator/actions  # ✓
curl -u user1:password1 http://localhost:8080/api/moderator/actions   # ✗ 403
```

### Scenario 3: Fine-Grained Document Sharing (ACL)

```bash
# 1. user1 creates a document
RESPONSE=$(curl -s -u user1:password1 -X POST http://localhost:8080/api/acl/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"Shared Doc","content":"Content","owner":"user1"}')

# 2. user1 grants READ to user2
curl -u user1:password1 -X POST http://localhost:8080/api/acl/documents/1/grant-read \
  -H "Content-Type: application/json" \
  -d '{"username":"user2"}'

# 3. user1 grants WRITE to moderator
curl -u user1:password1 -X POST http://localhost:8080/api/acl/documents/1/grant-write \
  -H "Content-Type: application/json" \
  -d '{"username":"moderator"}'

# Now:
# - user1: Full access (owner)
# - user2: Can READ only
# - moderator: Can READ and WRITE
# - viewer: No access
```

## Viewing Authorization Events

Check the application logs to see authorization events:

```bash
# Terminal with application running will show:
# INFO  - Authorization GRANTED - User: admin, Resource: HTTP Request
# WARN  - Authorization DENIED - User: user1, Resource: HTTP Request
```

## H2 Database Console

View the database: `http://localhost:8080/h2-console`

**Settings:**
- JDBC URL: `jdbc:h2:mem:securitydb`
- Username: `sa`
- Password: (blank)

**Useful Queries:**

```sql
-- View all users and roles
SELECT u.username, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;

-- View role authorities
SELECT r.name as role, ra.authority
FROM roles r
JOIN role_authorities ra ON r.id = ra.role_id;

-- View ACL entries
SELECT
  ac.class,
  aoi.object_id_identity,
  asid.sid,
  ae.mask,
  ae.granting
FROM acl_entry ae
JOIN acl_object_identity aoi ON ae.acl_object_identity = aoi.id
JOIN acl_class ac ON aoi.object_id_class = ac.id
JOIN acl_sid asid ON ae.sid = asid.id;
```

## Debugging Tips

### Enable Debug Logging

In `application.properties`:

```properties
logging.level.org.springframework.security=DEBUG
logging.level.com.example.security=DEBUG
```

### Common Issues

**403 Forbidden:**
- User lacks required role/authority
- Check user's roles in database
- Verify spelling of role names (ROLE_ prefix for roles)

**401 Unauthorized:**
- Wrong username/password
- User not found in database

**ACL Not Working:**
- Check ACL tables exist
- Verify MethodSecurityExpressionHandler configured
- Ensure permissions were granted

## Next Steps

1. **Understand Basics**: Review [README.md](../README.md)
2. **Deep Dive**: Read documentation in `docs/` folder
3. **Experiment**: Modify the code and test different scenarios
4. **Create Your Own**: Use this as a template for your application

## API Reference Quick Guide

### Public APIs
- `GET /api/public/hello` - No auth required

### Role-Based APIs
- `GET /api/admin/**` - Requires ROLE_ADMIN
- `GET /api/user/**` - Requires ROLE_USER
- `GET /api/moderator/**` - Requires ROLE_ADMIN or ROLE_MODERATOR

### Document APIs (Method Security)
- `POST /api/documents/create` - Requires WRITE_DOCUMENTS
- `GET /api/documents/{id}` - Owner or returns empty
- `PUT /api/documents/{id}` - Owner or ADMIN
- `DELETE /api/documents/delete/{id}` - Requires ADMIN
- `GET /api/documents/all` - Requires ADMIN or MODERATOR
- `GET /api/documents/my-documents` - Filtered by ownership

### ACL APIs
- `POST /api/acl/documents` - Create with ACL
- `GET /api/acl/documents/{id}` - Requires READ permission
- `PUT /api/acl/documents/{id}` - Requires WRITE permission
- `DELETE /api/acl/documents/{id}` - Requires DELETE permission
- `POST /api/acl/documents/{id}/grant-read` - Requires ADMIN permission
- `POST /api/acl/documents/{id}/grant-write` - Requires ADMIN permission
- `POST /api/acl/documents/{id}/revoke` - Requires ADMIN permission

Happy coding!
