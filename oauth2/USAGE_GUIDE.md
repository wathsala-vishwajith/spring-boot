# OAuth2 with JWT - Detailed Usage Guide

This guide provides step-by-step instructions for using the OAuth2 JWT example application.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Understanding OAuth2 Flows](#understanding-oauth2-flows)
3. [Practical Examples](#practical-examples)
4. [Using Postman](#using-postman)
5. [Using cURL](#using-curl)
6. [Common Scenarios](#common-scenarios)

## Quick Start

### Start the Server

```bash
cd oauth2
mvn spring-boot:run
```

Wait for the message: "Started OAuth2JwtApplication"

### Verify Server is Running

```bash
curl http://localhost:9000/api/public/info | jq
```

## Understanding OAuth2 Flows

### Flow 1: Client Credentials (Service-to-Service)

**Use Case**: Your backend service needs to access the API without user interaction.

**Diagram:**
```
Service Client ──────► Authorization Server
       │                       │
       │   1. Request Token    │
       │──────────────────────►│
       │                       │
       │   2. Return JWT Token │
       │◄──────────────────────│
       │                       │
       │                       │
       ▼                       ▼
Resource Server ◄─── 3. Access Protected Resource (with token)
```

### Flow 2: Authorization Code (User-Based)

**Use Case**: A web application needs to access the API on behalf of a user.

**Diagram:**
```
User ──► Client App ──► Authorization Server
                              │
                              ▼
                        Login & Consent
                              │
                              ▼
                     Return Auth Code ──► Exchange for Token
                                                  │
                                                  ▼
                                          Access Resources
```

## Practical Examples

### Example 1: Service-to-Service API Access

**Scenario**: A backend service needs to read and write products.

**Step 1: Obtain Access Token**

```bash
# Store the response
TOKEN_RESPONSE=$(curl -s -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=api.read api.write")

# Extract the access token
ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')

# Display the token (optional)
echo "Access Token: $ACCESS_TOKEN"

# Decode and view token claims (optional)
echo $ACCESS_TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq
```

**Step 2: Read Products**

```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/products | jq
```

**Step 3: Create a Product**

```bash
curl -X POST http://localhost:9000/api/products \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Monitor",
    "description": "27-inch 144Hz gaming monitor",
    "price": 399.99
  }' | jq
```

**Step 4: Update a Product**

```bash
curl -X PUT http://localhost:9000/api/products/1 \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Laptop",
    "description": "Updated high-performance laptop with 32GB RAM",
    "price": 1499.99
  }' | jq
```

### Example 2: User Authentication Flow

**Scenario**: A web application authenticating a user.

**Step 1: Direct User to Authorization Endpoint**

Open in browser:
```
http://localhost:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid%20profile%20read%20write&redirect_uri=http://127.0.0.1:8080/authorized
```

**Step 2: User Logs In**

Use these credentials:
- Regular User: `user` / `password`
- Admin User: `admin` / `admin`

**Step 3: User Grants Consent**

After login, you'll see a consent screen. Approve the requested scopes.

**Step 4: Extract Authorization Code**

After consent, you'll be redirected to:
```
http://127.0.0.1:8080/authorized?code=AUTHORIZATION_CODE
```

Copy the `code` parameter value.

**Step 5: Exchange Code for Token**

```bash
# Replace AUTHORIZATION_CODE with the actual code
AUTH_CODE="PASTE_YOUR_CODE_HERE"

TOKEN_RESPONSE=$(curl -s -X POST http://localhost:9000/oauth2/token \
  -u client:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code&code=$AUTH_CODE&redirect_uri=http://127.0.0.1:8080/authorized")

ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')
REFRESH_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.refresh_token')

echo "Access Token: $ACCESS_TOKEN"
echo "Refresh Token: $REFRESH_TOKEN"
```

**Step 6: Access User Information**

```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/user/me | jq
```

**Step 7: Access Protected Resources**

```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/products | jq
```

### Example 3: Using Refresh Tokens

**Scenario**: Your access token expired, use refresh token to get a new one.

```bash
# Use the refresh token from Example 2
NEW_TOKEN_RESPONSE=$(curl -s -X POST http://localhost:9000/oauth2/token \
  -u client:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token&refresh_token=$REFRESH_TOKEN")

NEW_ACCESS_TOKEN=$(echo $NEW_TOKEN_RESPONSE | jq -r '.access_token')
echo "New Access Token: $NEW_ACCESS_TOKEN"

# Use the new token
curl -H "Authorization: Bearer $NEW_ACCESS_TOKEN" \
  http://localhost:9000/api/user/me | jq
```

### Example 4: Role-Based Access (Admin)

**Scenario**: Access admin-only endpoints.

**Step 1: Get Token with Admin User**

Follow Example 2, but login with `admin` / `admin`.

**Step 2: Access Admin Dashboard**

```bash
curl -H "Authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  http://localhost:9000/api/admin/dashboard | jq
```

**Step 3: List Users (Admin Only)**

```bash
curl -H "Authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  http://localhost:9000/api/admin/users | jq
```

**Step 4: Try with Regular User (Should Fail)**

```bash
# This should return 403 Forbidden
curl -H "Authorization: Bearer $USER_ACCESS_TOKEN" \
  http://localhost:9000/api/admin/dashboard
```

## Using Postman

### Setup

1. Open Postman
2. Create a new request collection named "OAuth2 JWT Example"

### Configure Authorization (Client Credentials)

1. Create a new request
2. Set method to GET
3. URL: `http://localhost:9000/api/products`
4. Go to **Authorization** tab
5. Type: **OAuth 2.0**
6. Configure:
   - Grant Type: `Client Credentials`
   - Access Token URL: `http://localhost:9000/oauth2/token`
   - Client ID: `service-client`
   - Client Secret: `service-secret`
   - Scope: `api.read api.write`
7. Click **Get New Access Token**
8. Click **Use Token**
9. Send the request

### Configure Authorization (Authorization Code)

1. Create a new request
2. Go to **Authorization** tab
3. Type: **OAuth 2.0**
4. Configure:
   - Grant Type: `Authorization Code`
   - Callback URL: `http://127.0.0.1:8080/authorized`
   - Auth URL: `http://localhost:9000/oauth2/authorize`
   - Access Token URL: `http://localhost:9000/oauth2/token`
   - Client ID: `client`
   - Client Secret: `secret`
   - Scope: `openid profile read write`
5. Click **Get New Access Token**
6. Login when prompted
7. Grant consent
8. Click **Use Token**

## Using cURL

### Complete Script for Testing All Endpoints

Save this as `test-oauth2.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:9000"

echo "=== Testing OAuth2 with JWT ==="
echo

# Test public endpoint
echo "1. Testing public endpoint..."
curl -s $BASE_URL/api/public/hello | jq
echo

# Get service client token
echo "2. Getting service client token..."
TOKEN_RESPONSE=$(curl -s -X POST $BASE_URL/oauth2/token \
  -u service-client:service-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=api.read api.write")

ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')
echo "Access Token obtained: ${ACCESS_TOKEN:0:20}..."
echo

# List products
echo "3. Listing products..."
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" \
  $BASE_URL/api/products | jq
echo

# Create product
echo "4. Creating new product..."
curl -s -X POST $BASE_URL/api/products \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","description":"Created via script","price":49.99}' | jq
echo

# List products again
echo "5. Listing products after creation..."
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" \
  $BASE_URL/api/products | jq
echo

echo "=== Testing complete ==="
```

Make it executable and run:
```bash
chmod +x test-oauth2.sh
./test-oauth2.sh
```

## Common Scenarios

### Scenario 1: Token Expired

**Problem**: Getting 401 Unauthorized error.

**Solution**:
```bash
# Check token expiration
echo $ACCESS_TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq '.exp'

# Compare with current time
date +%s

# If expired, get a new token
# For client credentials:
curl -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -d "grant_type=client_credentials&scope=api.read api.write"

# For authorization code (use refresh token):
curl -X POST http://localhost:9000/oauth2/token \
  -u client:secret \
  -d "grant_type=refresh_token&refresh_token=$REFRESH_TOKEN"
```

### Scenario 2: Insufficient Permissions

**Problem**: Getting 403 Forbidden error.

**Solution**: Check token scopes and user roles.

```bash
# Decode token to see scopes
echo $ACCESS_TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq '.scope'

# Request token with required scopes
curl -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -d "grant_type=client_credentials&scope=api.read api.write"
```

### Scenario 3: Testing Different Users

**Regular User Access:**
```bash
# Login with user/password via authorization code flow
# Then test user endpoints:
curl -H "Authorization: Bearer $USER_TOKEN" \
  http://localhost:9000/api/user/hello
```

**Admin Access:**
```bash
# Login with admin/admin via authorization code flow
# Then test admin endpoints:
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:9000/api/admin/dashboard
```

### Scenario 4: Inspecting JWT Tokens

**View Token Structure:**
```bash
# Full token
echo $ACCESS_TOKEN

# Header
echo $ACCESS_TOKEN | cut -d. -f1 | base64 -d 2>/dev/null | jq

# Payload (claims)
echo $ACCESS_TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq

# Signature (binary)
echo $ACCESS_TOKEN | cut -d. -f3
```

**Verify Token Online:**
- Go to https://jwt.io
- Paste your token
- View decoded claims
- Note: Don't paste production tokens on public websites!

## Tips and Best Practices

1. **Always use HTTPS in production** - Never transmit tokens over HTTP
2. **Store tokens securely** - Use secure storage mechanisms
3. **Use short-lived tokens** - Minimize damage if token is compromised
4. **Validate tokens properly** - Always verify signature and expiration
5. **Use appropriate grant types** - Match grant type to your use case
6. **Scope appropriately** - Request only needed permissions
7. **Handle token refresh** - Implement proper refresh token flow
8. **Log security events** - Monitor for suspicious activity

## Troubleshooting

### Enable Debug Output in cURL

```bash
curl -v -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/products
```

### Check Server Logs

The application logs OAuth2 flow details. Look for:
- Token requests
- Authorization decisions
- Scope validations
- Access denials

### Common Error Messages

| Error | Meaning | Solution |
|-------|---------|----------|
| 401 Unauthorized | Invalid or missing token | Get a new token |
| 403 Forbidden | Insufficient permissions | Request token with required scopes/roles |
| invalid_grant | Invalid authorization code | Get a new authorization code |
| invalid_client | Wrong client credentials | Check client ID and secret |
| invalid_scope | Requested scope not allowed | Check client's allowed scopes |

## Next Steps

- Implement a real client application
- Add database-backed user storage
- Implement custom token claims
- Add refresh token rotation
- Implement token revocation
- Add PKCE for mobile apps
- Integrate with external IdPs
