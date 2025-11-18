# Spring Cloud Gateway Example Project

A comprehensive microservices architecture example demonstrating Spring Cloud Gateway, JWT authentication, and RESTful API design. This project serves as a learning resource for developers looking to understand microservices patterns and Spring Boot best practices.

## 📚 Project Overview

This project demonstrates a complete microservices architecture with:

- **Gateway Service**: Central entry point using Spring Cloud Gateway
- **User Service**: Authenticated microservice with JWT token generation
- **Product Service**: Public REST API for product catalog

### Architecture Diagram

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────────┐
│      Gateway Service (Port 8080)        │
│  - Routing                               │
│  - JWT Validation                        │
│  - Load Balancing                        │
└────┬────────────────────┬────────────────┘
     │                    │
     ▼                    ▼
┌─────────────┐    ┌─────────────┐
│ User Service│    │Product Service│
│ (Port 8081) │    │ (Port 8082) │
│ Authenticated│    │   Public    │
└─────────────┘    └─────────────┘
```

## 🎯 Learning Objectives

This project teaches:

1. **Microservices Architecture**: Separating concerns into independent services
2. **API Gateway Pattern**: Single entry point for all services
3. **JWT Authentication**: Stateless authentication for microservices
4. **Spring Security**: Securing endpoints and validating credentials
5. **RESTful API Design**: Best practices for REST API development
6. **Spring Data JPA**: Database operations with repositories
7. **DTO Pattern**: Separating internal models from API contracts
8. **Exception Handling**: Proper error responses
9. **Logging**: Tracking application flow and debugging

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

### Running the Services

You need to start all three services in order:

#### 1. Start User Service

```bash
cd user-service
mvn spring-boot:run
```

User Service will start on port **8081**

#### 2. Start Product Service

```bash
cd product-service
mvn spring-boot:run
```

Product Service will start on port **8082**

#### 3. Start Gateway Service

```bash
cd gateway-service
mvn spring-boot:run
```

Gateway Service will start on port **8080**

### Verifying Services are Running

Check health endpoints:

```bash
# Gateway health
curl http://localhost:8080/actuator/health

# User Service health
curl http://localhost:8081/health

# Product Service health
curl http://localhost:8082/health
```

## 📖 API Documentation

All API requests should go through the Gateway at `http://localhost:8080`

### User Service APIs

#### 1. Register a New User

**Endpoint**: `POST /api/users/register`
**Authentication**: Not required

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Response**:
```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "active": true,
  "createdAt": "2025-11-18T10:30:00"
}
```

#### 2. Login and Get JWT Token

**Endpoint**: `POST /api/users/login`
**Authentication**: Not required

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNzAwMzE2MDAwLCJleHAiOjE3MDA0MDI0MDB9.abc123...",
  "type": "Bearer",
  "username": "john",
  "message": "Authentication successful"
}
```

**Important**: Save the token! You'll need it for protected endpoints.

#### 3. Get All Users (Protected)

**Endpoint**: `GET /api/users/users`
**Authentication**: Required (JWT token)

```bash
curl -X GET http://localhost:8080/api/users/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

#### 4. Get Specific User (Protected)

**Endpoint**: `GET /api/users/users/{username}`
**Authentication**: Required (JWT token)

```bash
curl -X GET http://localhost:8080/api/users/users/john \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### Product Service APIs

All product endpoints are **public** (no authentication required).

#### 1. Get All Products

```bash
curl http://localhost:8080/api/products/products
```

#### 2. Get Active Products Only

```bash
curl http://localhost:8080/api/products/products/active
```

#### 3. Get Product by ID

```bash
curl http://localhost:8080/api/products/products/1
```

#### 4. Get Products by Category

```bash
curl http://localhost:8080/api/products/products/category/electronics
```

Available categories: `electronics`, `books`, `clothing`, `home`

#### 5. Search Products

```bash
curl "http://localhost:8080/api/products/products/search?q=laptop"
```

#### 6. Create Product

```bash
curl -X POST http://localhost:8080/api/products/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tablet",
    "description": "10-inch tablet with high-resolution display",
    "price": 399.99,
    "stockQuantity": 30,
    "category": "electronics",
    "imageUrl": "https://example.com/tablet.jpg"
  }'
```

#### 7. Update Product

```bash
curl -X PUT http://localhost:8080/api/products/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "description": "Updated description",
    "price": 899.99,
    "stockQuantity": 45,
    "category": "electronics",
    "imageUrl": "https://example.com/laptop-new.jpg"
  }'
```

#### 8. Delete Product

```bash
curl -X DELETE http://localhost:8080/api/products/products/1
```

**Note**: This is a soft delete - the product is marked as inactive, not removed from the database.

## 🔐 Authentication Flow

### Understanding JWT Authentication

1. **User registers** → Password is hashed with BCrypt and stored
2. **User logs in** → Server validates credentials and generates JWT token
3. **Client stores token** → Typically in localStorage or httpOnly cookie
4. **Client includes token** → In Authorization header: `Bearer <token>`
5. **Gateway validates token** → Checks signature and expiration
6. **Gateway adds user context** → Adds `X-User-Id` header for downstream services
7. **Service processes request** → Trusts the gateway's validation

### JWT Token Structure

A JWT token has three parts separated by dots:

```
eyJhbGci...  .  eyJzdWIi...  .  abc123...
   Header         Payload       Signature
```

- **Header**: Algorithm and token type
- **Payload**: User data (username, expiration)
- **Signature**: Verifies token hasn't been tampered with

### Testing Authentication

**Step 1**: Register a user
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123","email":"test@example.com"}'
```

**Step 2**: Login and get token
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

**Step 3**: Copy the token from the response

**Step 4**: Use token for protected endpoints
```bash
curl -X GET http://localhost:8080/api/users/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🏗️ Project Structure

### Gateway Service

```
gateway-service/
├── src/main/java/com/example/gateway/
│   ├── GatewayServiceApplication.java      # Main application
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java    # Custom JWT validation filter
│   └── util/
│       └── JwtUtil.java                     # JWT parsing and validation
└── src/main/resources/
    └── application.yml                      # Gateway routes and configuration
```

**Key Concepts**:
- **Gateway Filters**: Intercept and modify requests/responses
- **Route Predicates**: Match requests to specific routes
- **JWT Validation**: Verify token signature and expiration

### User Service

```
user-service/
├── src/main/java/com/example/userservice/
│   ├── UserServiceApplication.java
│   ├── controller/
│   │   └── UserController.java              # REST endpoints
│   ├── service/
│   │   └── UserService.java                 # Business logic
│   ├── repository/
│   │   └── UserRepository.java              # Data access
│   ├── model/
│   │   └── User.java                        # JPA entity
│   ├── dto/
│   │   ├── LoginRequest.java                # Request DTOs
│   │   ├── RegisterRequest.java
│   │   ├── AuthResponse.java                # Response DTOs
│   │   └── UserResponse.java
│   ├── security/
│   │   └── CustomUserDetailsService.java    # Load user for authentication
│   ├── config/
│   │   └── SecurityConfig.java              # Spring Security configuration
│   └── util/
│       └── JwtUtil.java                     # JWT token generation
└── src/main/resources/
    └── application.yml
```

**Key Concepts**:
- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separate API contracts from internal models
- **Spring Security**: Authentication and authorization
- **BCrypt**: Secure password hashing
- **JWT Generation**: Creating tokens for authenticated users

### Product Service

```
product-service/
├── src/main/java/com/example/productservice/
│   ├── ProductServiceApplication.java
│   ├── controller/
│   │   └── ProductController.java           # REST endpoints
│   ├── service/
│   │   └── ProductService.java              # Business logic
│   ├── repository/
│   │   └── ProductRepository.java           # Data access
│   ├── model/
│   │   └── Product.java                     # JPA entity
│   ├── dto/
│   │   ├── ProductRequest.java              # Request DTO
│   │   └── ProductResponse.java             # Response DTO
│   └── config/
│       └── DataInitializer.java             # Sample data
└── src/main/resources/
    └── application.yml
```

**Key Concepts**:
- **RESTful Design**: Resource-based URLs, appropriate HTTP methods
- **JPA Queries**: Derived query methods
- **Data Initialization**: CommandLineRunner for sample data
- **Soft Delete**: Mark records as inactive instead of deleting

## 🔍 Deep Dive: How It All Works Together

### Request Flow Example

Let's trace a request to get all users (protected endpoint):

1. **Client sends request**:
   ```
   GET http://localhost:8080/api/users/users
   Authorization: Bearer eyJhbGci...
   ```

2. **Gateway receives request**:
   - Matches route: `/api/users/**`
   - Applies `JwtAuthenticationFilter`

3. **JwtAuthenticationFilter validates token**:
   - Extracts token from Authorization header
   - Validates signature using secret key
   - Checks expiration date
   - Extracts username from token
   - Adds `X-User-Id` header with username

4. **Gateway routes to User Service**:
   - Rewrites path: `/api/users/users` → `/users`
   - Forwards to: `http://localhost:8081/users`
   - Includes `X-User-Id` header

5. **User Service processes request**:
   - Controller receives request
   - Service layer fetches users from repository
   - Converts entities to DTOs
   - Returns response

6. **Gateway returns response to client**

### Database Schema

#### User Service (H2 Database)

**users** table:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- BCrypt hash
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

#### Product Service (H2 Database)

**products** table:
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(19,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    category VARCHAR(255),
    image_url VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## 🎓 Learning Exercises

### Beginner Level

1. **Add a new field to User**:
   - Add `phoneNumber` field to User entity
   - Update DTOs and service methods
   - Test with registration endpoint

2. **Create a new product category**:
   - Add products in a new category
   - Test the category filter endpoint

3. **Change JWT expiration**:
   - Modify `jwt.expiration` in application.yml
   - Test token expiration

### Intermediate Level

1. **Add role-based authorization**:
   - Add `roles` field to User (ADMIN, USER)
   - Modify JWT to include roles
   - Protect certain endpoints for ADMIN only

2. **Add pagination to product list**:
   - Use Spring Data's Pageable
   - Add page and size query parameters
   - Return paginated response

3. **Add product categories endpoint**:
   - Create `/api/products/categories` endpoint
   - Return list of unique categories
   - Update controller and service

### Advanced Level

1. **Add refresh token mechanism**:
   - Generate refresh token on login
   - Create `/api/users/refresh` endpoint
   - Allow token renewal without re-login

2. **Add distributed tracing**:
   - Integrate Spring Cloud Sleuth
   - Add trace IDs to logs
   - Track requests across services

3. **Add rate limiting**:
   - Implement rate limiting filter in Gateway
   - Limit requests per user/IP
   - Return 429 Too Many Requests when exceeded

4. **Switch to real database**:
   - Replace H2 with PostgreSQL
   - Add Docker Compose configuration
   - Create database migration with Flyway

## 🔧 Configuration

### Important Configuration Files

#### Gateway: application.yml

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service-protected
          uri: http://localhost:8081
          predicates:
            - Path=/api/users/**
          filters:
            - JwtAuthentication  # Custom filter
```

**Key Settings**:
- `routes`: Define how requests are routed
- `predicates`: Match conditions (path, method, headers)
- `filters`: Modify requests/responses
- `jwt.secret`: Must match across Gateway and User Service

#### User Service: application.yml

```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation1234567890
  expiration: 86400000  # 24 hours

spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Recreate tables on restart
```

**Security Note**: In production:
- Use environment variables for secrets
- Use strong, random secret keys (at least 256 bits)
- Store secrets in a secure vault (AWS Secrets Manager, HashiCorp Vault)

## 🐛 Troubleshooting

### Common Issues

#### 1. "401 Unauthorized" on protected endpoints

**Cause**: Missing or invalid JWT token

**Solution**:
- Ensure you've logged in and received a token
- Include token in Authorization header: `Bearer <token>`
- Check token hasn't expired (valid for 24 hours)

#### 2. Services can't connect

**Cause**: Services not running or wrong ports

**Solution**:
- Verify all services are running: `jps` or `ps aux | grep java`
- Check ports: User Service (8081), Product Service (8082), Gateway (8080)
- Check firewall settings

#### 3. "User already exists" error

**Cause**: Username or email already registered

**Solution**:
- Use a different username/email
- Database is recreated on restart (H2 in-memory)
- Restart the User Service to clear the database

#### 4. Gateway returns 503 Service Unavailable

**Cause**: Backend service is down

**Solution**:
- Verify the target service is running
- Check service URLs in gateway application.yml
- Review gateway logs: `tail -f gateway-service/logs/application.log`

## 📚 Additional Resources

### Spring Boot Documentation
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

### JWT Resources
- [JWT.io](https://jwt.io/) - Decode and verify JWT tokens
- [RFC 7519](https://tools.ietf.org/html/rfc7519) - JWT specification

### Microservices Patterns
- [Microservices.io](https://microservices.io/)
- [12 Factor App](https://12factor.net/)

## 🎯 Best Practices Demonstrated

1. **Separation of Concerns**: Controllers, Services, Repositories
2. **DTO Pattern**: Don't expose internal models
3. **Password Security**: BCrypt hashing, never store plain text
4. **Stateless Authentication**: JWT tokens, no server-side sessions
5. **RESTful Design**: Resource-based URLs, appropriate HTTP methods
6. **Error Handling**: Meaningful error messages and status codes
7. **Logging**: Track application flow and errors
8. **Configuration**: Externalized configuration with profiles
9. **Documentation**: Comprehensive API documentation
10. **Code Comments**: Explain why, not what

## 🚀 Next Steps

1. **Explore the code**: Read through each class and understand its purpose
2. **Run the examples**: Try all the API endpoints
3. **Modify and experiment**: Add new features or change existing ones
4. **Break things**: Learn by fixing errors
5. **Build your own**: Create a new microservice based on these patterns

## 📝 License

This is a learning project. Feel free to use it for educational purposes.

## 🤝 Contributing

This is a learning resource. Suggestions for improvements are welcome!

---

**Happy Learning! 🎉**

Questions? Review the code comments - they contain detailed explanations of concepts and best practices.
