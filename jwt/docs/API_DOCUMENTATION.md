# API Documentation

Complete API reference for the JWT Authentication Demo application.

## Base URL

```
http://localhost:8080/api
```

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### Authentication

#### 1. Register New User

Create a new user account.

**Endpoint:** `POST /api/auth/register`

**Authentication:** Not required

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "email": "string"
}
```

**Field Validations:**
- `username`: Required, 3-20 characters
- `password`: Required, minimum 6 characters
- `email`: Required, valid email format

**Success Response:**
- **Code:** 200 OK
- **Content:**
```json
{
  "message": "User registered successfully"
}
```

**Error Responses:**

- **Code:** 400 Bad Request
- **Content:**
```json
{
  "message": "Username already exists"
}
```
OR
```json
{
  "message": "Email already exists"
}
```

- **Code:** 400 Bad Request (Validation Error)
- **Content:**
```json
{
  "username": "Username must be between 3 and 20 characters",
  "password": "Password must be at least 6 characters",
  "email": "Email should be valid"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com"
  }'
```

---

#### 2. User Login

Authenticate user and receive JWT token.

**Endpoint:** `POST /api/auth/login`

**Authentication:** Not required

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Success Response:**
- **Code:** 200 OK
- **Content:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjMwMDAwMDAwLCJleHAiOjE2MzAwODY0MDB9.signature",
  "type": "Bearer",
  "username": "user",
  "message": "Authentication successful"
}
```

**Error Response:**
- **Code:** 400 Bad Request
- **Content:**
```json
{
  "message": "Invalid username or password"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "password"
  }'
```

**Response Example:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwODY0MDB9.1a2b3c4d5e6f7g8h9i0j",
  "type": "Bearer",
  "username": "user",
  "message": "Authentication successful"
}
```

---

### Public Endpoints

#### 3. Public Hello

Public endpoint accessible without authentication.

**Endpoint:** `GET /api/public/hello`

**Authentication:** Not required

**Request Headers:** None required

**Success Response:**
- **Code:** 200 OK
- **Content:**
```json
{
  "message": "Hello from public endpoint! No authentication required."
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/public/hello
```

---

### Protected Endpoints

All protected endpoints require a valid JWT token in the Authorization header.

#### 4. Private Hello

Protected endpoint requiring authentication.

**Endpoint:** `GET /api/private/hello`

**Authentication:** Required

**Request Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Success Response:**
- **Code:** 200 OK
- **Content:**
```json
{
  "message": "Hello user! You are authenticated.",
  "username": "user"
}
```

**Error Response:**
- **Code:** 403 Forbidden
- **Content:** (When token is missing or invalid)
```json
{
  "timestamp": "2024-01-01T12:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/private/hello"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

#### 5. User Profile

Get current authenticated user's profile information.

**Endpoint:** `GET /api/user/profile`

**Authentication:** Required (ROLE_USER)

**Request Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Success Response:**
- **Code:** 200 OK
- **Content:**
```json
{
  "username": "user",
  "authorities": [
    {
      "authority": "ROLE_USER"
    }
  ],
  "authenticated": true
}
```

**Error Response:**
- **Code:** 403 Forbidden (if not authenticated)

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

#### 6. Admin Dashboard

Admin-only endpoint demonstrating role-based access control.

**Endpoint:** `GET /api/admin/dashboard`

**Authentication:** Required (ROLE_ADMIN)

**Request Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Success Response:**
- **Code:** 200 OK
- **Content:**
```json
{
  "message": "Welcome to admin dashboard, admin!",
  "role": "ADMIN"
}
```

**Error Responses:**

- **Code:** 403 Forbidden (if not authenticated)
- **Code:** 403 Forbidden (if user doesn't have ROLE_ADMIN)

**cURL Example:**
```bash
# Using admin credentials
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## HTTP Status Codes

The API uses standard HTTP status codes:

| Code | Description |
|------|-------------|
| 200  | Success - Request completed successfully |
| 201  | Created - Resource created successfully |
| 400  | Bad Request - Invalid request data or validation error |
| 401  | Unauthorized - Authentication required |
| 403  | Forbidden - Insufficient permissions |
| 404  | Not Found - Resource not found |
| 500  | Internal Server Error - Server error |

## Error Response Format

All error responses follow this format:

```json
{
  "message": "Error description"
}
```

Or for validation errors:

```json
{
  "field1": "Error message for field1",
  "field2": "Error message for field2"
}
```

## JWT Token Details

### Token Structure

```
Header.Payload.Signature
```

**Example Token:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwODY0MDB9.signature
```

### Decoded Header
```json
{
  "alg": "HS256"
}
```

### Decoded Payload
```json
{
  "sub": "user",
  "iat": 1700000000,
  "exp": 1700086400
}
```

### Token Claims

| Claim | Description |
|-------|-------------|
| sub   | Subject (username) |
| iat   | Issued at (timestamp) |
| exp   | Expiration time (timestamp) |

### Token Expiration

- **Duration:** 24 hours (86400000 milliseconds)
- **After Expiration:** Token becomes invalid and authentication fails
- **Refresh:** No automatic refresh; user must login again

## Postman Collection

### Quick Setup

1. **Create Environment Variables:**
   - `baseUrl`: `http://localhost:8080/api`
   - `token`: (will be set automatically after login)

2. **Login Request:**
   ```
   POST {{baseUrl}}/auth/login
   ```

   **Tests Script:**
   ```javascript
   var jsonData = pm.response.json();
   pm.environment.set("token", jsonData.token);
   ```

3. **Protected Endpoint Request:**
   ```
   GET {{baseUrl}}/private/hello
   ```

   **Authorization Tab:**
   - Type: Bearer Token
   - Token: `{{token}}`

### Sample Collection

```json
{
  "info": {
    "name": "JWT Demo API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"password\": \"password123\",\n  \"email\": \"test@example.com\"\n}"
            },
            "url": "{{baseUrl}}/auth/register"
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"user\",\n  \"password\": \"password\"\n}"
            },
            "url": "{{baseUrl}}/auth/login"
          }
        }
      ]
    }
  ]
}
```

## Rate Limiting (Future Enhancement)

Currently not implemented. Recommended for production:

- Login endpoint: 5 requests per minute per IP
- Registration: 3 requests per hour per IP
- Protected endpoints: 100 requests per minute per user

## Versioning

Currently: v1 (implicit)

For future versions, consider:
```
/api/v2/auth/login
```

## CORS Configuration

Default configuration allows all origins for development.

**Production Recommendation:**
```java
@CrossOrigin(origins = "https://yourdomain.com")
```

## Best Practices

### 1. Token Storage (Client-Side)

**Recommended:**
- Use `httpOnly` cookies for web applications
- Use secure storage for mobile apps (Keychain/Keystore)

**Avoid:**
- localStorage (vulnerable to XSS)
- sessionStorage (still vulnerable to XSS)

### 2. Token Transmission

- Always use HTTPS in production
- Never send tokens in URL parameters
- Always use Authorization header

### 3. Token Validation

- Validate token on every request
- Check expiration time
- Verify signature
- Validate claims

### 4. Error Handling

- Don't expose sensitive error details
- Log errors server-side
- Return generic error messages to client

## Testing Scenarios

### 1. Successful Authentication Flow
```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123","email":"test@test.com"}'

# 2. Login
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}' \
  | jq -r '.token')

# 3. Access protected endpoint
curl -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer $TOKEN"
```

### 2. Invalid Token
```bash
curl -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer invalid.token.here"
# Expected: 403 Forbidden
```

### 3. Missing Token
```bash
curl -X GET http://localhost:8080/api/private/hello
# Expected: 403 Forbidden
```

### 4. Expired Token
- Wait 24 hours after login
- Try to access protected endpoint
- Expected: 403 Forbidden

### 5. Role-Based Access
```bash
# Login as regular user
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.token')

# Try to access admin endpoint
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer $TOKEN"
# Expected: 403 Forbidden
```

## Troubleshooting

### Common Issues

**1. 403 Forbidden on valid token**
- Check token expiration
- Verify token format (Bearer prefix)
- Check role requirements

**2. "Invalid username or password"**
- Verify username exists
- Check password is correct
- Ensure user is enabled

**3. Token not accepted**
- Verify Authorization header format
- Check for whitespace in token
- Ensure complete token is sent

## Additional Resources

- JWT.io - Token debugger: https://jwt.io/
- Postman - API testing: https://www.postman.com/
- Spring Security Docs: https://spring.io/projects/spring-security
