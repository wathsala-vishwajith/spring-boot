# JWT Authentication Demo - Quick Start Guide

Get up and running with the JWT Authentication Demo in 5 minutes!

## Prerequisites

- Java 17+
- Maven 3.6+

## Quick Start

### 1. Run the Application

```bash
cd jwt
mvn spring-boot:run
```

Wait for:
```
Started JwtDemoApplication in X.XXX seconds
```

### 2. Test Authentication

#### Get a JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "user",
  "message": "Authentication successful"
}
```

#### Use the Token

```bash
# Save token
TOKEN="your-token-here"

# Access protected endpoint
curl -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer $TOKEN"
```

**Response:**
```json
{
  "message": "Hello user! You are authenticated.",
  "username": "user"
}
```

## Default Credentials

| Username | Password  | Roles               |
|----------|-----------|---------------------|
| user     | password  | ROLE_USER           |
| admin    | admin123  | ROLE_USER, ROLE_ADMIN |

## Quick Test Script

Save as `quick_test.sh`:

```bash
#!/bin/bash

echo "Testing JWT Authentication Demo"
echo "================================"

# Login
echo -e "\n1. Logging in..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}')

TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ Login failed"
  exit 1
fi

echo "✅ Login successful"
echo "Token: ${TOKEN:0:50}..."

# Test protected endpoint
echo -e "\n2. Testing protected endpoint..."
RESPONSE=$(curl -s -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer $TOKEN")

if echo "$RESPONSE" | grep -q "authenticated"; then
  echo "✅ Protected endpoint accessible"
  echo "Response: $RESPONSE"
else
  echo "❌ Failed to access protected endpoint"
  exit 1
fi

echo -e "\n✅ All tests passed!"
```

Run it:
```bash
chmod +x quick_test.sh
./quick_test.sh
```

## API Endpoints

### Public

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get token
- `GET /api/public/hello` - Public test endpoint

### Protected (Requires Token)

- `GET /api/private/hello` - Protected test endpoint
- `GET /api/user/profile` - Get user profile
- `GET /api/admin/dashboard` - Admin only (ROLE_ADMIN required)

## Next Steps

1. **Read the full documentation:** [README.md](./README.md)
2. **Understand the architecture:** [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md)
3. **Explore API endpoints:** [docs/API_DOCUMENTATION.md](./docs/API_DOCUMENTATION.md)
4. **Run comprehensive tests:** [docs/TESTING_GUIDE.md](./docs/TESTING_GUIDE.md)
5. **Review security:** [docs/SECURITY.md](./docs/SECURITY.md)

## Troubleshooting

**Port 8080 already in use?**
```bash
# Change port in application.properties
server.port=8081
```

**Application won't start?**
```bash
# Clean and rebuild
mvn clean install
mvn spring-boot:run
```

**Token not working?**
- Check token format: `Bearer <token>`
- Verify token hasn't expired (24 hours)
- Ensure you're using the correct endpoint

## H2 Database Console

Access the H2 console for development:

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:jwtdb`
- Username: `sa`
- Password: (leave empty)

## Development Tools

**Recommended Extensions:**
- Postman (API testing)
- JWT.io (Token debugging)
- IntelliJ IDEA or VS Code

## Get Help

- Check [README.md](./README.md) for detailed information
- Review [docs/](./docs/) for comprehensive guides
- Examine code comments for implementation details

---

**Happy Coding! 🚀**
