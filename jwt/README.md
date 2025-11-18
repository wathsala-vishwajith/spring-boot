# JWT Authentication in Spring Boot Microservices

A comprehensive example project demonstrating JWT (JSON Web Token) authentication implementation in Spring Boot microservices. This project is based on the tutorial from [Spring Framework Guru](https://springframework.guru/jwt-authentication-in-spring-microservices-jwt-token/).

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Testing the Application](#testing-the-application)
- [Security Configuration](#security-configuration)
- [JWT Token Flow](#jwt-token-flow)
- [Additional Documentation](#additional-documentation)

## 🎯 Overview

JWT (JSON Web Token) is an open standard (RFC 7519) that defines a compact and self-contained way for securely transmitting information between parties as a JSON object. This information can be verified and trusted because it is digitally signed.

This project demonstrates:
- User registration and authentication
- JWT token generation and validation
- Protected and public REST endpoints
- Role-based access control
- Spring Security integration

## ✨ Features

- **User Registration**: Create new user accounts with validation
- **User Authentication**: Login with username/password and receive JWT token
- **Token-based Authorization**: Protect endpoints using JWT tokens
- **Role-based Access Control**: Different access levels (USER, ADMIN)
- **Stateless Authentication**: No server-side session management
- **H2 Database**: In-memory database for easy testing
- **RESTful API**: Clean and well-documented endpoints
- **Comprehensive Error Handling**: Proper error responses

## 🛠 Technologies Used

- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Web
  - Spring Security
  - Spring Data JPA
- **JJWT 0.12.3** - JWT library for Java
- **H2 Database** - In-memory database
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

## 📁 Project Structure

```
jwt/
├── src/
│   ├── main/
│   │   ├── java/com/example/jwtdemo/
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java           # Spring Security configuration
│   │   │   │   └── DataInitializer.java          # Demo data loader
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java           # Authentication endpoints
│   │   │   │   └── DemoController.java           # Protected/public demo endpoints
│   │   │   ├── dto/
│   │   │   │   ├── AuthRequest.java              # Login request DTO
│   │   │   │   ├── AuthResponse.java             # Login response DTO
│   │   │   │   ├── RegisterRequest.java          # Registration request DTO
│   │   │   │   └── MessageResponse.java          # Generic message DTO
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthenticationFilter.java  # JWT filter for requests
│   │   │   ├── model/
│   │   │   │   └── User.java                     # User entity
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java           # User data repository
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java              # Authentication service
│   │   │   │   └── CustomUserDetailsService.java # User details service
│   │   │   ├── util/
│   │   │   │   └── JwtUtil.java                  # JWT utility class
│   │   │   └── JwtDemoApplication.java           # Main application class
│   │   └── resources/
│   │       └── application.properties            # Application configuration
│   └── test/
│       └── java/com/example/jwtdemo/            # Test classes
├── pom.xml                                       # Maven configuration
└── README.md                                     # This file
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation & Running

1. **Clone the repository** (if not already done)
   ```bash
   cd jwt
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR file:
   ```bash
   java -jar target/jwt-demo-1.0.0.jar
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:jwtdb`
     - Username: `sa`
     - Password: (leave empty)

### Default Users

The application creates two demo users on startup:

| Username | Password  | Roles               |
|----------|-----------|---------------------|
| user     | password  | ROLE_USER           |
| admin    | admin123  | ROLE_USER, ROLE_ADMIN |

## 📡 API Endpoints

### Authentication Endpoints (Public)

#### Register New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "password123",
  "email": "john@example.com"
}
```

**Response:**
```json
{
  "message": "User registered successfully"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "password"
}
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

### Public Endpoints

#### Public Hello
```http
GET /api/public/hello
```

**Response:**
```json
{
  "message": "Hello from public endpoint! No authentication required."
}
```

### Protected Endpoints (Requires Authentication)

For protected endpoints, include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### Private Hello
```http
GET /api/private/hello
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "message": "Hello user! You are authenticated.",
  "username": "user"
}
```

#### User Profile
```http
GET /api/user/profile
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "username": "user",
  "authorities": [{"authority": "ROLE_USER"}],
  "authenticated": true
}
```

#### Admin Dashboard (Admin Only)
```http
GET /api/admin/dashboard
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "message": "Welcome to admin dashboard, admin!",
  "role": "ADMIN"
}
```

## 🧪 Testing the Application

### Using cURL

1. **Register a new user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123",
    "email": "test@example.com"
  }'
```

2. **Login and get JWT token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "password"
  }'
```

3. **Access protected endpoint:**
```bash
curl -X GET http://localhost:8080/api/private/hello \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using Postman

1. **Import the collection** (if provided) or create requests manually
2. **Login** to get JWT token
3. **Set Authorization** header: `Bearer <token>`
4. **Test endpoints** as needed

See [TESTING_GUIDE.md](./docs/TESTING_GUIDE.md) for detailed testing scenarios.

## 🔒 Security Configuration

The application uses Spring Security with the following configuration:

- **Stateless Session Management**: No server-side sessions
- **CSRF Disabled**: Not needed for stateless JWT authentication
- **Public Endpoints**: `/api/auth/**`, `/api/public/**`, `/h2-console/**`
- **Protected Endpoints**: All other `/api/**` endpoints
- **Password Encoding**: BCrypt with strength 10
- **JWT Token**: HS256 algorithm, 24-hour expiration

### Key Security Components

1. **JwtAuthenticationFilter**: Intercepts requests and validates JWT tokens
2. **SecurityConfig**: Configures Spring Security filter chain
3. **JwtUtil**: Handles token generation and validation
4. **CustomUserDetailsService**: Loads user details for authentication

## 🔄 JWT Token Flow

### Authentication Flow

```
1. Client sends credentials (username/password) to /api/auth/login
2. AuthController receives request
3. AuthenticationManager validates credentials
4. On success, JwtUtil generates JWT token
5. Token is returned to client in response
6. Client stores token (localStorage, sessionStorage, etc.)
```

### Authorization Flow

```
1. Client sends request with Authorization header: Bearer <token>
2. JwtAuthenticationFilter intercepts request
3. Filter extracts and validates JWT token
4. If valid, user is authenticated in SecurityContext
5. Request proceeds to controller
6. Controller processes request and returns response
```

### Token Structure

A JWT token consists of three parts separated by dots:
```
header.payload.signature
```

Example decoded token:
```json
{
  "sub": "user",
  "iat": 1234567890,
  "exp": 1234654290
}
```

## 📚 Additional Documentation

- [ARCHITECTURE.md](./docs/ARCHITECTURE.md) - Detailed architecture and design decisions
- [API_DOCUMENTATION.md](./docs/API_DOCUMENTATION.md) - Complete API reference
- [TESTING_GUIDE.md](./docs/TESTING_GUIDE.md) - Comprehensive testing guide
- [SECURITY.md](./docs/SECURITY.md) - Security best practices and considerations

## 🔧 Configuration

Key configuration properties in `application.properties`:

```properties
# JWT Configuration
jwt.secret=your-secret-key-here  # Change in production!
jwt.expiration=86400000          # 24 hours in milliseconds

# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:jwtdb
```

### Important Notes

⚠️ **For Production:**
- Change the JWT secret to a strong, random value (at least 256 bits)
- Use a persistent database (PostgreSQL, MySQL, etc.) instead of H2
- Disable H2 console
- Configure proper CORS settings
- Use HTTPS
- Implement token refresh mechanism
- Add rate limiting
- Configure proper logging

## 🤝 Contributing

This is a learning/demo project. Feel free to fork and modify as needed.

## 📝 License

This project is created for educational purposes.

## 🙏 Acknowledgments

- Based on the tutorial from [Spring Framework Guru](https://springframework.guru/jwt-authentication-in-spring-microservices-jwt-token/)
- Spring Boot and Spring Security documentation
- JJWT library documentation

## 📞 Support

For questions or issues, please refer to:
- Spring Security documentation: https://spring.io/projects/spring-security
- JJWT GitHub: https://github.com/jwtk/jjwt
- Spring Boot documentation: https://spring.io/projects/spring-boot

---

**Happy Learning! 🚀**
