# Quick Start Guide

Get up and running with the OAuth2 JWT example in 5 minutes!

## Prerequisites

- Java 17+
- Maven 3.6+
- cURL or Postman

## Step 1: Build and Run

```bash
cd oauth2
mvn clean install
mvn spring-boot:run
```

Wait for: `Started OAuth2JwtApplication in X seconds`

## Step 2: Test Public Endpoint

```bash
curl http://localhost:9000/api/public/hello
```

Expected output:
```json
{
  "message": "Hello! This is a public endpoint.",
  "info": "No authentication required to access this endpoint."
}
```

## Step 3: Get Access Token

```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=api.read api.write"
```

Copy the `access_token` from the response.

## Step 4: Access Protected Endpoint

Replace `YOUR_TOKEN` with the actual token:

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:9000/api/products
```

Expected output: List of products

## Step 5: Create a Product

```bash
curl -X POST http://localhost:9000/api/products \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"My Product","description":"Test product","price":99.99}'
```

## Automated Quick Start Script

Save this as `quick-start.sh`:

```bash
#!/bin/bash

echo "=== OAuth2 JWT Quick Start ==="
echo

# Start the application in background
echo "Starting application..."
mvn spring-boot:run &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 15

# Test public endpoint
echo -e "\n1. Testing public endpoint:"
curl -s http://localhost:9000/api/public/hello | jq

# Get token
echo -e "\n2. Getting access token:"
TOKEN_RESPONSE=$(curl -s -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-secret \
  -d "grant_type=client_credentials&scope=api.read api.write")
ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')
echo "Token: ${ACCESS_TOKEN:0:50}..."

# Get products
echo -e "\n3. Getting products:"
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:9000/api/products | jq

echo -e "\n=== Quick Start Complete! ==="
echo "Application is running. Press Ctrl+C to stop."

# Keep script running
wait $APP_PID
```

Run it:
```bash
chmod +x quick-start.sh
./quick-start.sh
```

## Next Steps

- Read [USAGE_GUIDE.md](USAGE_GUIDE.md) for detailed examples
- Check [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for complete API reference
- See [README.md](README.md) for architecture and concepts

## Common Issues

**Port already in use:**
```bash
# Find process using port 9000
lsof -i :9000
# Kill it
kill -9 PID
```

**Java version:**
```bash
java -version  # Should be 17+
```

**Maven not found:**
```bash
# Install Maven
# Ubuntu/Debian: sudo apt install maven
# MacOS: brew install maven
```

## Available Credentials

**Users:**
- Username: `user` / Password: `password` (USER role)
- Username: `admin` / Password: `admin` (ADMIN role)

**OAuth2 Clients:**
- Client: `client` / Secret: `secret`
- Service Client: `service-client` / Secret: `service-secret`

Happy coding!
