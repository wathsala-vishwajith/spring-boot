# Microservices Architecture with Spring Boot

A complete microservices architecture implementation using Java Spring Boot, featuring service discovery, API gateway, centralized configuration, and inter-service communication via REST and message queues.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         API Gateway (8080)                       │
│                     Spring Cloud Gateway                         │
└────────────┬────────────────────────────────────────────────────┘
             │
             ├─────────────┬─────────────┬─────────────┐
             │             │             │             │
    ┌────────▼────────┐   │   ┌────────▼────────┐   │
    │  User Service   │   │   │ Product Service │   │
    │    (8081)       │◄──┼──►│     (8083)      │   │
    └────────┬────────┘   │   └────────┬────────┘   │
             │            │            │            │
             │      ┌─────▼─────┐      │            │
             │      │   Order   │      │            │
             └─────►│  Service  │◄─────┘            │
                    │  (8082)   │                   │
                    └─────┬─────┘                   │
                          │                         │
          ┌───────────────┴───────────────┐         │
          │                               │         │
    ┌─────▼──────┐              ┌────────▼────────┐│
    │  RabbitMQ  │              │ Eureka Server   ││
    │  (5672)    │              │    (8761)       ││
    │  Message   │              │Service Discovery││
    │   Queue    │              └────────┬────────┘│
    └────────────┘                       │         │
                                ┌────────▼────────┐│
                                │  Config Server  ││
                                │     (8888)      ││
                                │  Centralized    ││
                                │ Configuration   ││
                                └─────────────────┘│
```

## Components

### 1. Eureka Server (Service Discovery)
- **Port**: 8761
- **Purpose**: Service registry for all microservices
- **URL**: http://localhost:8761

All microservices register themselves with Eureka, enabling dynamic service discovery and load balancing.

### 2. Config Server (Centralized Configuration)
- **Port**: 8888
- **Purpose**: Centralized external configuration management
- **URL**: http://localhost:8888

Stores and serves configuration for all microservices from a central location.

### 3. API Gateway
- **Port**: 8080
- **Purpose**: Single entry point for all client requests
- **Technology**: Spring Cloud Gateway

Routes:
- `/api/users/**` → User Service
- `/api/products/**` → Product Service
- `/api/orders/**` → Order Service

### 4. User Service
- **Port**: 8081
- **Database**: H2 (in-memory)
- **Purpose**: Manages user information

**Endpoints:**
- `GET /users` - Get all users
- `GET /users/{id}` - Get user by ID
- `POST /users` - Create new user
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Delete user

**Events Published:**
- `user.created`
- `user.updated`
- `user.deleted`

### 5. Product Service
- **Port**: 8083
- **Database**: H2 (in-memory)
- **Purpose**: Manages product catalog

**Endpoints:**
- `GET /products` - Get all products
- `GET /products/{id}` - Get product by ID
- `GET /products/category/{category}` - Get products by category
- `POST /products` - Create new product
- `PUT /products/{id}` - Update product
- `PATCH /products/{id}/stock?quantity={qty}` - Update stock
- `DELETE /products/{id}` - Delete product

**Events Published:**
- `product.created`
- `product.updated`
- `product.stock.updated`
- `product.deleted`

### 6. Order Service
- **Port**: 8082
- **Database**: H2 (in-memory)
- **Purpose**: Manages orders and orchestrates user and product services

**Endpoints:**
- `GET /orders` - Get all orders
- `GET /orders/{id}` - Get order by ID
- `POST /orders` - Create new order
- `PATCH /orders/{id}/status?status={status}` - Update order status
- `DELETE /orders/{id}` - Delete order

**Events Published:**
- `order.created`
- `order.updated`
- `order.deleted`

**REST Communication:**
- Calls User Service via Feign Client to fetch user details
- Calls Product Service via Feign Client to fetch product details

## Communication Patterns

### 1. Synchronous Communication (REST)
The Order Service communicates with User and Product services using **OpenFeign** clients:

```java
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
```

### 2. Asynchronous Communication (RabbitMQ)
All services publish events to RabbitMQ when entities are created, updated, or deleted:

**Exchange**: `microservices.exchange` (Topic Exchange)

**Queues and Routing Keys:**
- `user.queue` → Listens to `user.*`
- `product.queue` → Listens to `product.*`
- `order.queue` → Listens to `order.*`

**Example Message Flow:**
1. User creates an order → Order Service publishes `order.created` event
2. Product Service listens to order events and can update stock accordingly
3. Services react to events asynchronously without tight coupling

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for RabbitMQ)

## Getting Started

### Step 1: Start Infrastructure Services

```bash
cd microservices-ecosystem
./start-infrastructure.sh
```

This starts:
- RabbitMQ (ports 5672, 15672)

### Step 2: Build All Services

```bash
# Build each service
cd eureka-server && mvn clean install && cd ..
cd config-server && mvn clean install && cd ..
cd api-gateway && mvn clean install && cd ..
cd user-service && mvn clean install && cd ..
cd product-service && mvn clean install && cd ..
cd order-service && mvn clean install && cd ..
```

### Step 3: Start Services in Order

**Option A: Manual Start (Recommended for development)**

1. Start Eureka Server:
```bash
cd eureka-server
mvn spring-boot:run
```

2. Wait 30 seconds, then start Config Server:
```bash
cd config-server
mvn spring-boot:run
```

3. Wait 15 seconds, then start API Gateway:
```bash
cd api-gateway
mvn spring-boot:run
```

4. Start the microservices (can be started in parallel):
```bash
# Terminal 1
cd user-service
mvn spring-boot:run

# Terminal 2
cd product-service
mvn spring-boot:run

# Terminal 3
cd order-service
mvn spring-boot:run
```

**Option B: Automated Start (Background)**

```bash
./start-all-services.sh
```

### Step 4: Verify Services

Check Eureka Dashboard to ensure all services are registered:
- http://localhost:8761

You should see:
- API-GATEWAY
- USER-SERVICE
- PRODUCT-SERVICE
- ORDER-SERVICE
- CONFIG-SERVER

## Testing the System

### 1. Create a User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### 2. Create a Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "stock": 50,
    "category": "Electronics"
  }'
```

### 3. Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }'
```

This will:
- Call User Service to validate user
- Call Product Service to get product price
- Calculate total price
- Create order
- Publish `order.created` event to RabbitMQ

### 4. Get All Orders (with enriched data)

```bash
curl http://localhost:8080/api/orders
```

Response includes full user and product details fetched via REST calls.

### 5. Update Order Status

```bash
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=SHIPPED"
```

## Monitoring

### Service Health Endpoints

All services expose actuator endpoints:

```bash
# User Service
curl http://localhost:8081/actuator/health

# Product Service
curl http://localhost:8083/actuator/health

# Order Service
curl http://localhost:8082/actuator/health
```

### RabbitMQ Management Console

Access the RabbitMQ management interface:
- **URL**: http://localhost:15672
- **Username**: guest
- **Password**: guest

Here you can:
- Monitor queues and exchanges
- See message rates
- View active connections
- Debug message flow

### H2 Database Consoles

Each service has an H2 console:

```bash
# User Service
http://localhost:8081/h2-console
JDBC URL: jdbc:h2:mem:userdb

# Product Service
http://localhost:8083/h2-console
JDBC URL: jdbc:h2:mem:productdb

# Order Service
http://localhost:8082/h2-console
JDBC URL: jdbc:h2:mem:orderdb
```

## Project Structure

```
microservices-ecosystem/
├── eureka-server/          # Service Discovery
├── config-server/          # Configuration Server
├── api-gateway/            # API Gateway
├── user-service/           # User Microservice
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── messaging/
│   └── config/
├── product-service/        # Product Microservice
│   └── (same structure)
├── order-service/          # Order Microservice
│   ├── client/            # Feign clients for REST communication
│   └── (same structure)
├── docker-compose.yml      # Infrastructure services
├── start-infrastructure.sh # Start RabbitMQ
├── start-all-services.sh   # Start all services
└── logs/                   # Application logs
```

## Key Technologies

- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Cloud Netflix Eureka** - Service Discovery
- **Spring Cloud Config** - Centralized Configuration
- **Spring Cloud Gateway** - API Gateway
- **Spring Cloud OpenFeign** - REST Client
- **Spring AMQP (RabbitMQ)** - Async Messaging
- **Spring Data JPA** - Data Persistence
- **H2 Database** - In-memory Database
- **Lombok** - Boilerplate Reduction
- **Maven** - Build Tool

## Design Patterns Used

1. **Service Registry Pattern** - Eureka Server
2. **API Gateway Pattern** - Spring Cloud Gateway
3. **Circuit Breaker Pattern** - Built into Feign (can be enhanced with Resilience4j)
4. **Event-Driven Architecture** - RabbitMQ messaging
5. **Database per Service** - Each service has its own database
6. **Externalized Configuration** - Config Server

## Stopping Services

To stop all services:

```bash
# If started manually, press Ctrl+C in each terminal

# If started via script, find and kill processes
ps aux | grep spring-boot
kill <PID>

# Stop infrastructure
docker-compose down
```

## Future Enhancements

- Add distributed tracing (Zipkin/Sleuth)
- Implement circuit breakers (Resilience4j)
- Add API authentication/authorization (Spring Security + JWT)
- Implement distributed logging (ELK stack)
- Add monitoring (Prometheus + Grafana)
- Implement API rate limiting
- Add caching layer (Redis)
- Database migration from H2 to PostgreSQL/MySQL
- Containerize all services with Docker
- Add Kubernetes deployment manifests
- Implement saga pattern for distributed transactions

## Troubleshooting

### Services not registering with Eureka
- Ensure Eureka Server is running first
- Check network connectivity
- Verify `eureka.client.service-url.defaultZone` in application.yml

### RabbitMQ connection errors
- Ensure RabbitMQ is running: `docker ps`
- Check RabbitMQ logs: `docker logs rabbitmq`
- Verify port 5672 is accessible

### Feign Client errors
- Ensure target service is registered with Eureka
- Check service name in `@FeignClient` annotation matches registered name
- Verify target service endpoints are accessible

## License

This project is for educational purposes.
