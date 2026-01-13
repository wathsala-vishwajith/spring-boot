# Spring Boot OAuth2 with JWT - Example Project

A comprehensive example project demonstrating OAuth2 Authorization Server and Resource Server implementation with JWT (JSON Web Tokens) in Spring Boot 3.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [OAuth2 Grant Types](#oauth2-grant-types)
- [API Endpoints](#api-endpoints)
- [Testing the Application](#testing-the-application)
- [Security Configuration](#security-configuration)
- [JWT Token Details](#jwt-token-details)
- [Troubleshooting](#troubleshooting)
- [Learning Resources](#learning-resources)

## Overview

This project demonstrates a complete OAuth2 implementation using Spring Boot 3 and Spring Security 6. It includes:

- **OAuth2 Authorization Server**: Issues JWT access tokens
- **OAuth2 Resource Server**: Validates JWT tokens and protects REST API endpoints
- **Multiple Grant Types**: Support for Authorization Code, Client Credentials, and Refresh Token
- **Role-Based Access Control (RBAC)**: Different user roles with specific permissions
- **Scope-Based Authorization**: Fine-grained access control using OAuth2 scopes

## Features

- **JWT Token Generation**: Uses RSA asymmetric encryption for token signing
- **Multiple OAuth2 Clients**: Pre-configured clients for different use cases
- **OpenID Connect Support**: OIDC endpoints for authentication
- **Secure REST APIs**: Protected endpoints with various authorization requirements
- **Method-Level Security**: Using `@PreAuthorize` annotations
- **In-Memory User Store**: Easy setup with predefined users (production should use database)
- **Comprehensive Logging**: Debug logging for OAuth2 flow understanding

## Architecture

```
┌─────────────┐                  ┌──────────────────────┐
│   Client    │                  │  Authorization       │
│ Application │◄────────────────►│  Server (Port 9000)  │
└─────────────┘                  │  - Issues JWT tokens │
                                 │  - Validates users   │
       │                         └──────────────────────┘
       │                                    │
       │                                    │ JWT Token
       │                                    ▼
       │                         ┌──────────────────────┐
       └────────────────────────►│  Resource Server     │
                                 │  - Validates tokens  │
                                 │  - Protects APIs     │
                                 └──────────────────────┘
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **cURL** or **Postman** for testing API endpoints
- Basic understanding of OAuth2 and JWT concepts

## Project Structure

```
oauth2/
├── src/
│   ├── main/
│   │   ├── java/com/example/oauth2/
│   │   │   ├── OAuth2JwtApplication.java          # Main application class
│   │   │   ├── config/
│   │   │   │   ├── AuthorizationServerConfig.java # OAuth2 server configuration
│   │   │   │   ├── SecurityConfig.java            # Security configuration
│   │   │   │   └── MethodSecurityConfig.java      # Method security
│   │   │   ├── controller/
│   │   │   │   ├── PublicController.java          # Public endpoints
│   │   │   │   ├── UserController.java            # User endpoints
│   │   │   │   ├── AdminController.java           # Admin endpoints
│   │   │   │   └── ResourceController.java        # Product CRUD endpoints
│   │   │   └── model/
│   │   │       └── Product.java                   # Product model
│   │   └── resources/
│   │       ├── application.properties              # Application configuration
│   │       └── application.yml                     # Alternative YAML config
│   └── test/
│       └── java/com/example/oauth2/               # Test classes
├── pom.xml                                         # Maven dependencies
├── README.md                                       # This file
├── USAGE_GUIDE.md                                  # Detailed usage examples
└── API_DOCUMENTATION.md                            # API reference
```

## Getting Started

### 1. Clone and Build

```bash
cd oauth2
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The server will start on **port 9000**.

### 3. Verify Server is Running

```bash
curl http://localhost:9000/api/public/info
```

You should see server information including available OAuth2 endpoints.

## OAuth2 Grant Types

### 1. Client Credentials Grant

Used for machine-to-machine communication (no user involved).

**Client Configuration:**
- Client ID: `service-client`
- Client Secret: `service-secret`
- Scopes: `api.read`, `api.write`

**Request Token:**
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=api.read api.write"
```

### 2. Authorization Code Grant

Used for user-based authentication (typical web application flow).

**Client Configuration:**
- Client ID: `client`
- Client Secret: `secret`
- Scopes: `openid`, `profile`, `read`, `write`
- Redirect URIs: `http://127.0.0.1:8080/authorized`

**Step 1: Get Authorization Code**

Open in browser:
```
http://localhost:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid%20profile%20read%20write&redirect_uri=http://127.0.0.1:8080/authorized
```

Login with:
- Username: `user` / Password: `password` (USER role)
- Username: `admin` / Password: `admin` (ADMIN role)

**Step 2: Exchange Code for Token**

```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u client:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code&code=AUTHORIZATION_CODE&redirect_uri=http://127.0.0.1:8080/authorized"
```

### 3. Refresh Token Grant

Used to obtain a new access token using a refresh token.

```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u client:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token&refresh_token=REFRESH_TOKEN"
```

## API Endpoints

### Public Endpoints (No Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/public/hello` | Public hello message |
| GET | `/api/public/info` | Server information |

### User Endpoints (Requires Authentication)

| Method | Endpoint | Description | Required Scope |
|--------|----------|-------------|----------------|
| GET | `/api/user/me` | Get current user info | Any authenticated |
| GET | `/api/user/hello` | User greeting | Any authenticated |

### Admin Endpoints (Requires ADMIN Role)

| Method | Endpoint | Description | Required Scope |
|--------|----------|-------------|----------------|
| GET | `/api/admin/dashboard` | Admin dashboard | ROLE_ADMIN |
| GET | `/api/admin/users` | List users | ROLE_ADMIN |
| POST | `/api/admin/settings` | Update settings | ROLE_ADMIN + write scope |

### Product Endpoints (Scope-Based)

| Method | Endpoint | Description | Required Scope |
|--------|----------|-------------|----------------|
| GET | `/api/products` | List all products | read or api.read |
| GET | `/api/products/{id}` | Get product by ID | read or api.read |
| POST | `/api/products` | Create product | write or api.write |
| PUT | `/api/products/{id}` | Update product | write or api.write |
| DELETE | `/api/products/{id}` | Delete product | write or api.write |

### OAuth2 Endpoints

| Endpoint | Description |
|----------|-------------|
| `/.well-known/openid-configuration` | OpenID Connect discovery |
| `/.well-known/jwks.json` | JSON Web Key Set (public keys) |
| `/oauth2/authorize` | Authorization endpoint |
| `/oauth2/token` | Token endpoint |
| `/oauth2/introspect` | Token introspection |
| `/oauth2/revoke` | Token revocation |

## Testing the Application

### 1. Test Public Endpoint

```bash
curl http://localhost:9000/api/public/hello
```

### 2. Get Access Token (Client Credentials)

```bash
TOKEN_RESPONSE=$(curl -s -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=api.read api.write")

ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')
echo "Access Token: $ACCESS_TOKEN"
```

### 3. Access Protected Endpoint

```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/products
```

### 4. Create a Product (Requires write scope)

```bash
curl -X POST http://localhost:9000/api/products \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"New Product","description":"A new product","price":99.99}'
```

### 5. Test User Endpoint (Authorization Code)

First, get a token using authorization code flow (as shown above), then:

```bash
curl -H "Authorization: Bearer $USER_ACCESS_TOKEN" \
  http://localhost:9000/api/user/me
```

## Security Configuration

### Users and Roles

The application comes with two predefined users:

| Username | Password | Roles |
|----------|----------|-------|
| user | password | USER |
| admin | admin | USER, ADMIN |

**Note:** In production, use a database-backed user service with properly hashed passwords.

### OAuth2 Clients

| Client ID | Client Secret | Grant Types | Scopes |
|-----------|---------------|-------------|--------|
| client | secret | authorization_code, refresh_token, client_credentials | openid, profile, read, write |
| service-client | service-secret | client_credentials | api.read, api.write |

### Token Expiration

- **Access Token**: 30 minutes (for authorization code flow), 15 minutes (for client credentials)
- **Refresh Token**: 1 hour

These can be configured in `AuthorizationServerConfig.java`.

## JWT Token Details

### Token Structure

A JWT token consists of three parts: Header, Payload, and Signature.

**Example Token Payload:**
```json
{
  "sub": "user",
  "aud": ["client"],
  "nbf": 1234567890,
  "scope": ["openid", "profile", "read"],
  "iss": "http://localhost:9000",
  "exp": 1234569690,
  "iat": 1234567890,
  "jti": "unique-token-id"
}
```

### Decoding JWT Tokens

You can decode JWT tokens at [jwt.io](https://jwt.io) to inspect claims.

### JWT Signing

This application uses **RSA-2048** asymmetric encryption:
- **Private Key**: Used to sign tokens (kept secret by authorization server)
- **Public Key**: Used to validate tokens (available at `/.well-known/jwks.json`)

## Troubleshooting

### Common Issues

**1. "Invalid token" error**
- Ensure the token hasn't expired
- Verify you're using the correct token
- Check that the Authorization header format is: `Bearer <token>`

**2. "Insufficient scope" error**
- The token doesn't have the required scope
- Request a token with appropriate scopes

**3. "Access Denied" error**
- User doesn't have the required role
- Try with admin credentials for admin endpoints

**4. Connection refused**
- Ensure the application is running on port 9000
- Check firewall settings

### Enable Debug Logging

The application already has debug logging enabled for OAuth2. Check console output for detailed flow information.

## Learning Resources

### Key Concepts

1. **OAuth2**: An authorization framework that enables applications to obtain limited access to user accounts
2. **JWT**: A compact, URL-safe means of representing claims between two parties
3. **Authorization Server**: Issues access tokens after successfully authenticating the client
4. **Resource Server**: Hosts protected resources and validates access tokens
5. **Scopes**: Define the specific permissions requested/granted
6. **Grant Types**: Different ways to obtain access tokens

### Further Reading

- [OAuth 2.0 RFC](https://tools.ietf.org/html/rfc6749)
- [JWT RFC](https://tools.ietf.org/html/rfc7519)
- [Spring Security OAuth2](https://spring.io/projects/spring-security-oauth)
- [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)

## Next Steps

1. **Database Integration**: Replace in-memory user/client stores with database
2. **Custom Claims**: Add custom claims to JWT tokens
3. **Consent Page**: Implement a custom consent page
4. **External IdP**: Integrate with external identity providers
5. **Token Revocation**: Implement token blacklisting
6. **Rate Limiting**: Add rate limiting to protect endpoints
7. **HTTPS**: Configure SSL/TLS for production

## License

This is an educational example project for learning OAuth2 and JWT in Spring Boot.
