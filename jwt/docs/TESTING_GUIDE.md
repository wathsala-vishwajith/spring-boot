# Testing Guide

Comprehensive guide for testing the JWT Authentication Demo application.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Manual Testing](#manual-testing)
3. [Automated Testing](#automated-testing)
4. [Integration Testing](#integration-testing)
5. [Security Testing](#security-testing)
6. [Performance Testing](#performance-testing)

## Prerequisites

### Required Tools

- **cURL**: Command-line HTTP client
- **Postman**: API testing tool (optional)
- **jq**: JSON processor for command line (optional)

### Installation

**cURL** (Usually pre-installed on Linux/Mac):
```bash
# Ubuntu/Debian
sudo apt-get install curl

# Mac
brew install curl
```

**jq** (For parsing JSON responses):
```bash
# Ubuntu/Debian
sudo apt-get install jq

# Mac
brew install jq
```

### Start the Application

```bash
cd jwt
mvn spring-boot:run
```

Wait for the message:
```
Started JwtDemoApplication in X.XXX seconds
```

## Manual Testing

### Test 1: Public Endpoint Access

**Objective:** Verify public endpoints are accessible without authentication.

```bash
curl -X GET http://localhost:8080/api/public/hello
```

**Expected Response:**
```json
{
  "message": "Hello from public endpoint! No authentication required."
}
```

**Status Code:** 200 OK

---

### Test 2: User Registration

**Objective:** Register a new user account.

#### Test 2.1: Successful Registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com"
  }'
```

**Expected Response:**
```json
{
  "message": "User registered successfully"
}
```

**Status Code:** 200 OK

#### Test 2.2: Duplicate Username

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password456",
    "email": "another@example.com"
  }'
```

**Expected Response:**
```json
{
  "message": "Username already exists"
}
```

**Status Code:** 400 Bad Request

#### Test 2.3: Invalid Email Format

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "invalid-email"
  }'
```

**Expected:** Validation error

#### Test 2.4: Short Password

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "12345",
    "email": "user@example.com"
  }'
```

**Expected:** Validation error (password must be at least 6 characters)

---

### Test 3: User Authentication

**Objective:** Login and receive JWT token.

#### Test 3.1: Successful Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "password"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "user",
  "message": "Authentication successful"
}
```

**Status Code:** 200 OK

**Save the token for next tests:**
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.token')

echo "Token: $TOKEN"
```

#### Test 3.2: Invalid Credentials

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "wrongpassword"
  }'
```

**Expected Response:**
```json
{
  "message": "Invalid username or password"
}
```

**Status Code:** 400 Bad Request

---

### Test 4: Protected Endpoint Access

**Objective:** Access protected endpoints with JWT token.

#### Test 4.1: Access with Valid Token

```bash
# First, get the token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.token')

# Access protected endpoint
curl -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
```json
{
  "message": "Hello user! You are authenticated.",
  "username": "user"
}
```

**Status Code:** 200 OK

#### Test 4.2: Access without Token

```bash
curl -X GET http://localhost:8080/api/private/hello
```

**Expected:** 403 Forbidden

#### Test 4.3: Access with Invalid Token

```bash
curl -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer invalid.token.here"
```

**Expected:** 403 Forbidden

---

### Test 5: User Profile Endpoint

**Objective:** Retrieve current user's profile information.

```bash
# Get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.token')

# Get profile
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
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

---

### Test 6: Role-Based Access Control

**Objective:** Test admin-only endpoints.

#### Test 6.1: Admin Access with Admin Token

```bash
# Login as admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.token')

# Access admin endpoint
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Expected Response:**
```json
{
  "message": "Welcome to admin dashboard, admin!",
  "role": "ADMIN"
}
```

**Status Code:** 200 OK

#### Test 6.2: Regular User Access to Admin Endpoint

```bash
# Login as regular user
USER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.token')

# Try to access admin endpoint
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Expected:** 403 Forbidden

---

## Complete Test Script

Save this as `test_all.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api"
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "======================================"
echo "JWT Authentication API Test Suite"
echo "======================================"

# Test 1: Public Endpoint
echo -e "\n${GREEN}Test 1: Public Endpoint${NC}"
curl -s -X GET $BASE_URL/public/hello | jq

# Test 2: Register New User
echo -e "\n${GREEN}Test 2: Register New User${NC}"
curl -s -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"testuser_$(date +%s)\",
    \"password\": \"password123\",
    \"email\": \"test_$(date +%s)@example.com\"
  }" | jq

# Test 3: Login
echo -e "\n${GREEN}Test 3: User Login${NC}"
TOKEN=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.token')

if [ -n "$TOKEN" ]; then
  echo "Token received: ${TOKEN:0:50}..."
else
  echo -e "${RED}Failed to get token${NC}"
  exit 1
fi

# Test 4: Protected Endpoint
echo -e "\n${GREEN}Test 4: Protected Endpoint with Token${NC}"
curl -s -X GET $BASE_URL/private/hello \
  -H "Authorization: Bearer $TOKEN" | jq

# Test 5: User Profile
echo -e "\n${GREEN}Test 5: User Profile${NC}"
curl -s -X GET $BASE_URL/user/profile \
  -H "Authorization: Bearer $TOKEN" | jq

# Test 6: Admin Login
echo -e "\n${GREEN}Test 6: Admin Login${NC}"
ADMIN_TOKEN=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.token')

echo "Admin token received: ${ADMIN_TOKEN:0:50}..."

# Test 7: Admin Dashboard
echo -e "\n${GREEN}Test 7: Admin Dashboard${NC}"
curl -s -X GET $BASE_URL/admin/dashboard \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq

# Test 8: Invalid Token
echo -e "\n${GREEN}Test 8: Invalid Token (Should Fail)${NC}"
curl -s -X GET $BASE_URL/private/hello \
  -H "Authorization: Bearer invalid.token"

# Test 9: No Token
echo -e "\n${GREEN}Test 9: No Token (Should Fail)${NC}"
curl -s -X GET $BASE_URL/private/hello

echo -e "\n${GREEN}======================================"
echo "All tests completed!"
echo "======================================${NC}"
```

**Run the script:**
```bash
chmod +x test_all.sh
./test_all.sh
```

---

## Postman Testing

### Setup Postman Collection

1. **Create a new collection** named "JWT Demo"

2. **Add environment variables:**
   - Variable: `baseUrl`
   - Value: `http://localhost:8080/api`

3. **Create requests as documented in API_DOCUMENTATION.md**

4. **Add test scripts:**

**For Login Request:**
```javascript
// Save token to environment
var jsonData = pm.response.json();
pm.environment.set("token", jsonData.token);

// Test response
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Token is present", function () {
    pm.expect(jsonData.token).to.exist;
});
```

**For Protected Endpoints:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has message", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.message).to.exist;
});
```

---

## Automated Testing

### Unit Tests

Create test classes in `src/test/java/com/example/jwtdemo/`:

#### JwtUtilTest.java

```java
package com.example.jwtdemo;

import com.example.jwtdemo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testInvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtUtil.extractUsername("invalid.token.here");
        });
    }
}
```

#### AuthServiceTest.java

```java
package com.example.jwtdemo;

import com.example.jwtdemo.dto.RegisterRequest;
import com.example.jwtdemo.repository.UserRepository;
import com.example.jwtdemo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest(
            "newuser",
            "password123",
            "newuser@test.com"
        );

        String result = authService.register(request);
        assertEquals("User registered successfully", result);
        assertTrue(userRepository.existsByUsername("newuser"));
    }

    @Test
    void testRegisterDuplicateUsername() {
        RegisterRequest request = new RegisterRequest(
            "user",
            "password123",
            "unique@test.com"
        );

        assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });
    }
}
```

**Run unit tests:**
```bash
mvn test
```

---

## Integration Testing

### Controller Integration Tests

```java
package com.example.jwtdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/private/hello"))
                .andExpect(status().isForbidden());
    }
}
```

---

## Security Testing

### Test Scenarios

1. **SQL Injection Prevention**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin'\'' OR '\''1'\''='\''1","password":"anything"}'
```
**Expected:** Authentication failure

2. **XSS Prevention**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"<script>alert(1)</script>","password":"password","email":"test@test.com"}'
```
**Expected:** Username properly escaped/rejected

3. **Token Tampering**
- Decode a valid token
- Modify claims
- Re-encode
- Try to use modified token
**Expected:** Validation failure

---

## Performance Testing

### Using Apache Bench

```bash
# Install Apache Bench
sudo apt-get install apache2-utils

# Test public endpoint
ab -n 1000 -c 10 http://localhost:8080/api/public/hello

# Test login endpoint
ab -n 100 -c 5 -p login.json -T application/json \
  http://localhost:8080/api/auth/login
```

### Expected Performance

- Public endpoints: > 1000 req/sec
- Login endpoint: > 100 req/sec
- Protected endpoints: > 500 req/sec

---

## Troubleshooting Tests

### Common Issues

**1. Connection Refused**
- Check if application is running
- Verify port 8080 is not in use

**2. 403 Forbidden on all requests**
- Check SecurityConfig
- Verify filter chain configuration

**3. Token Validation Fails**
- Check JWT secret matches
- Verify token hasn't expired
- Check system clock

### Debug Mode

Enable debug logging:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.com.example.jwtdemo=DEBUG
```

---

## Test Coverage Goals

- **Unit Tests:** > 80% code coverage
- **Integration Tests:** All critical paths
- **Security Tests:** OWASP Top 10
- **Performance Tests:** All endpoints

**Run coverage report:**
```bash
mvn clean test jacoco:report
```

View report: `target/site/jacoco/index.html`

---

## Continuous Testing

### GitHub Actions Example

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: mvn clean test
```

---

## Conclusion

Regular testing ensures the JWT authentication system works correctly and securely. Use this guide to verify all functionality before deploying to production.
