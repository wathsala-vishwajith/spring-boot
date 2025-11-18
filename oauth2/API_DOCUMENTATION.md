# API Documentation

Complete API reference for the OAuth2 JWT Example Application.

## Base URL

```
http://localhost:9000
```

## Authentication

All protected endpoints require a valid JWT Bearer token in the Authorization header:

```
Authorization: Bearer <access_token>
```

## OAuth2 Endpoints

### Get Authorization Code

**Endpoint:** `GET /oauth2/authorize`

**Description:** Initiates the authorization code flow.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| response_type | string | Yes | Must be "code" |
| client_id | string | Yes | OAuth2 client ID |
| redirect_uri | string | Yes | Callback URL |
| scope | string | Yes | Space-separated list of scopes |
| state | string | No | Opaque value for CSRF protection |

**Example:**
```
GET /oauth2/authorize?response_type=code&client_id=client&redirect_uri=http://127.0.0.1:8080/authorized&scope=openid%20profile%20read%20write
```

**Response:** Redirects to login page, then to consent page, then to redirect_uri with code parameter.

---

### Get Access Token

**Endpoint:** `POST /oauth2/token`

**Description:** Exchange authorization code or credentials for access token.

**Authentication:** Basic Auth (client_id:client_secret)

**Content-Type:** `application/x-www-form-urlencoded`

#### Client Credentials Grant

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| grant_type | string | Yes | "client_credentials" |
| scope | string | No | Space-separated list of scopes |

**Example:**
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -d "grant_type=client_credentials&scope=api.read api.write"
```

**Response:**
```json
{
  "access_token": "eyJraWQiOiI...",
  "token_type": "Bearer",
  "expires_in": 899,
  "scope": "api.read api.write"
}
```

#### Authorization Code Grant

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| grant_type | string | Yes | "authorization_code" |
| code | string | Yes | Authorization code from /authorize |
| redirect_uri | string | Yes | Same redirect_uri used in /authorize |

**Example:**
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u client:secret \
  -d "grant_type=authorization_code&code=ABC123&redirect_uri=http://127.0.0.1:8080/authorized"
```

**Response:**
```json
{
  "access_token": "eyJraWQiOiI...",
  "refresh_token": "eyJraWQiOiI...",
  "token_type": "Bearer",
  "expires_in": 1799,
  "scope": "openid profile read write"
}
```

#### Refresh Token Grant

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| grant_type | string | Yes | "refresh_token" |
| refresh_token | string | Yes | Refresh token from previous response |

**Example:**
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u client:secret \
  -d "grant_type=refresh_token&refresh_token=eyJraWQiOiI..."
```

---

### Get JWK Set

**Endpoint:** `GET /.well-known/jwks.json`

**Description:** Returns the public keys used to verify JWT signatures.

**Authentication:** None required

**Response:**
```json
{
  "keys": [
    {
      "kty": "RSA",
      "e": "AQAB",
      "kid": "abc-123",
      "n": "xGOr_..."
    }
  ]
}
```

---

### Get OpenID Configuration

**Endpoint:** `GET /.well-known/openid-configuration`

**Description:** Returns OpenID Connect discovery metadata.

**Authentication:** None required

**Response:**
```json
{
  "issuer": "http://localhost:9000",
  "authorization_endpoint": "http://localhost:9000/oauth2/authorize",
  "token_endpoint": "http://localhost:9000/oauth2/token",
  "jwks_uri": "http://localhost:9000/.well-known/jwks.json",
  "response_types_supported": ["code"],
  "grant_types_supported": ["authorization_code", "client_credentials", "refresh_token"],
  ...
}
```

---

## Public API Endpoints

### Get Public Hello Message

**Endpoint:** `GET /api/public/hello`

**Description:** Returns a public welcome message.

**Authentication:** None required

**Response:**
```json
{
  "message": "Hello! This is a public endpoint.",
  "info": "No authentication required to access this endpoint."
}
```

---

### Get Server Information

**Endpoint:** `GET /api/public/info`

**Description:** Returns server information and available endpoints.

**Authentication:** None required

**Response:**
```json
{
  "name": "Spring Boot OAuth2 JWT Example",
  "version": "1.0.0",
  "description": "Example demonstrating OAuth2 with JWT tokens",
  "endpoints": {
    "token": "/oauth2/token",
    "authorize": "/oauth2/authorize",
    "jwks": "/.well-known/jwks.json",
    "openid-configuration": "/.well-known/openid-configuration"
  }
}
```

---

## User API Endpoints

All user endpoints require a valid access token.

### Get Current User

**Endpoint:** `GET /api/user/me`

**Description:** Returns information about the currently authenticated user.

**Authentication:** Required (any authenticated user)

**Required Scopes:** Any

**Example:**
```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/user/me
```

**Response:**
```json
{
  "username": "user",
  "authorities": ["SCOPE_read", "SCOPE_write"],
  "subject": "user",
  "issuedAt": "2024-01-15T10:30:00Z",
  "expiresAt": "2024-01-15T11:00:00Z",
  "claims": {
    "sub": "user",
    "aud": ["client"],
    "scope": ["read", "write"],
    ...
  }
}
```

---

### Get User Hello

**Endpoint:** `GET /api/user/hello`

**Description:** Returns a personalized greeting.

**Authentication:** Required

**Response:**
```json
{
  "message": "Hello, user!",
  "info": "This endpoint requires authentication."
}
```

---

## Admin API Endpoints

All admin endpoints require ADMIN role.

### Get Admin Dashboard

**Endpoint:** `GET /api/admin/dashboard`

**Description:** Returns admin dashboard data.

**Authentication:** Required

**Required Role:** ROLE_ADMIN

**Example:**
```bash
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:9000/api/admin/dashboard
```

**Response:**
```json
{
  "message": "Welcome to the admin dashboard!",
  "user": "admin",
  "stats": {
    "totalUsers": 42,
    "activeTokens": 15,
    "requestsToday": 1337
  }
}
```

**Error Response (403):**
```json
{
  "error": "Forbidden",
  "message": "Access Denied"
}
```

---

### List Users

**Endpoint:** `GET /api/admin/users`

**Description:** Returns list of all users.

**Authentication:** Required

**Required Role:** ROLE_ADMIN

**Response:**
```json
{
  "users": [
    {
      "id": 1,
      "username": "user",
      "role": "USER"
    },
    {
      "id": 2,
      "username": "admin",
      "role": "ADMIN"
    }
  ]
}
```

---

### Update Settings

**Endpoint:** `POST /api/admin/settings`

**Description:** Updates system settings.

**Authentication:** Required

**Required Role:** ROLE_ADMIN

**Required Scope:** write

**Request Body:**
```json
{
  "setting1": "value1",
  "setting2": "value2"
}
```

**Response:**
```json
{
  "message": "Settings updated successfully",
  "status": "success"
}
```

---

## Product API Endpoints

### List All Products

**Endpoint:** `GET /api/products`

**Description:** Returns list of all products.

**Authentication:** Required

**Required Scope:** read OR api.read

**Example:**
```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/products
```

**Response:**
```json
{
  "products": [
    {
      "id": 1,
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 1299.99
    },
    {
      "id": 2,
      "name": "Mouse",
      "description": "Wireless mouse",
      "price": 29.99
    }
  ],
  "count": 2
}
```

---

### Get Product by ID

**Endpoint:** `GET /api/products/{id}`

**Description:** Returns a specific product by ID.

**Authentication:** Required

**Required Scope:** read OR api.read

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Product ID |

**Example:**
```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/products/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1299.99
}
```

**Error Response (404):**
```json
{
  "error": "Not Found",
  "message": "Product not found"
}
```

---

### Create Product

**Endpoint:** `POST /api/products`

**Description:** Creates a new product.

**Authentication:** Required

**Required Scope:** write OR api.write

**Request Body:**
```json
{
  "name": "New Product",
  "description": "Product description",
  "price": 99.99
}
```

**Example:**
```bash
curl -X POST http://localhost:9000/api/products \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Keyboard","description":"Mechanical keyboard","price":89.99}'
```

**Response:**
```json
{
  "message": "Product created successfully",
  "product": {
    "id": 4,
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 89.99
  }
}
```

---

### Update Product

**Endpoint:** `PUT /api/products/{id}`

**Description:** Updates an existing product.

**Authentication:** Required

**Required Scope:** write OR api.write

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Product ID |

**Request Body:**
```json
{
  "name": "Updated Name",
  "description": "Updated description",
  "price": 149.99
}
```

**Example:**
```bash
curl -X PUT http://localhost:9000/api/products/1 \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Premium Laptop","description":"Updated laptop","price":1499.99}'
```

**Response:**
```json
{
  "message": "Product updated successfully",
  "product": {
    "id": 1,
    "name": "Premium Laptop",
    "description": "Updated laptop",
    "price": 1499.99
  }
}
```

---

### Delete Product

**Endpoint:** `DELETE /api/products/{id}`

**Description:** Deletes a product.

**Authentication:** Required

**Required Scope:** write OR api.write

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Product ID |

**Example:**
```bash
curl -X DELETE http://localhost:9000/api/products/1 \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Response:**
```json
{
  "message": "Product deleted successfully"
}
```

---

## Error Responses

### Common HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (invalid parameters) |
| 401 | Unauthorized (invalid or missing token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 500 | Internal Server Error |

### Error Response Format

```json
{
  "error": "Error Type",
  "message": "Detailed error message",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/endpoint"
}
```

## OAuth2 Scopes

| Scope | Description |
|-------|-------------|
| openid | OpenID Connect authentication |
| profile | Access to user profile |
| read | Read access to user resources |
| write | Write access to user resources |
| api.read | Read access to API resources |
| api.write | Write access to API resources |

## Rate Limiting

Currently, no rate limiting is implemented. In production, you should add rate limiting to protect your endpoints.

## Versioning

This API is version 1.0.0. Future versions may be released with different base paths (e.g., `/api/v2/`).
