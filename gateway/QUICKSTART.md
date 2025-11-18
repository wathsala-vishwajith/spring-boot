# Quick Start Guide

Get the Spring Cloud Gateway example running in 5 minutes!

## Prerequisites

- Java 17+
- Maven 3.6+

## Step 1: Start All Services

Open **three separate terminal windows** and run these commands:

### Terminal 1 - User Service
```bash
cd gateway/user-service
mvn spring-boot:run
```
Wait for: "Started UserServiceApplication"

### Terminal 2 - Product Service
```bash
cd gateway/product-service
mvn spring-boot:run
```
Wait for: "Started ProductServiceApplication"

### Terminal 3 - Gateway Service
```bash
cd gateway/gateway-service
mvn spring-boot:run
```
Wait for: "Started GatewayServiceApplication"

## Step 2: Verify Services

```bash
# Check Gateway
curl http://localhost:8080/actuator/health

# Check User Service
curl http://localhost:8081/health

# Check Product Service
curl http://localhost:8082/health
```

All should return healthy status!

## Step 3: Try the APIs

### Get Products (No Auth Required)

```bash
curl http://localhost:8080/api/products/products
```

You should see a list of sample products!

### Register a User

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123",
    "email": "demo@example.com",
    "firstName": "Demo",
    "lastName": "User"
  }'
```

### Login and Get Token

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123"
  }'
```

**Copy the token** from the response!

### Access Protected Endpoint

Replace `YOUR_TOKEN` with the token you copied:

```bash
curl http://localhost:8080/api/users/users \
  -H "Authorization: Bearer YOUR_TOKEN"
```

You should see a list of users!

## Step 4: Run Automated Tests

We've included a test script that verifies everything works:

```bash
cd gateway
./test-apis.sh
```

This script will:
- Check if all services are running
- Test public endpoints
- Register a user
- Login and get JWT token
- Test protected endpoints
- Verify authentication works

## What's Next?

1. Read the [full README](README.md) for detailed documentation
2. Explore the code - start with the controllers
3. Try the learning exercises in the README
4. Modify the code and experiment!

## Common Issues

**Port already in use?**
```bash
# Find process using port 8080, 8081, or 8082
lsof -i :8080
# Kill it
kill -9 <PID>
```

**Service won't start?**
- Check Java version: `java -version` (needs Java 17+)
- Check Maven: `mvn -version`
- Clear Maven cache: `rm -rf ~/.m2/repository`

**Need help?**
Check the Troubleshooting section in the [README](README.md)

---

**You're all set! Happy coding! 🚀**
