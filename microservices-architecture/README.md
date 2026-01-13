# Spring Boot Microservices Architecture

A comprehensive microservices architecture implementation using Java Spring Boot, featuring API Gateway, Service Discovery, Config Server, and three sample microservices with REST and message queue communication.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        API Gateway                          │
│                       (Port: 8080)                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Eureka Server                            │
│                  (Service Discovery)                        │
│                       (Port: 8761)                          │
└─────────────────────────────────────────────────────────────┘
                              │
           ┌──────────────────┼──────────────────┐
           ▼                  ▼                  ▼
    ┌──────────┐      ┌──────────┐      ┌──────────┐
    │  Order   │      │ Product  │      │Notification│
    │ Service  │◄────►│ Service  │      │  Service  │
    │(8081)    │      │(8082)    │      │(8083)     │
    └──────────┘      └──────────┘      └──────────┘
         │                                     ▲
         │            RabbitMQ                 │
         └─────────────────────────────────────┘
```

## Components

### Infrastructure Services

1. **Eureka Server** (Port: 8761)
   - Service Discovery and Registry
   - All microservices register here
   - Dashboard: http://localhost:8761

2. **Config Server** (Port: 8888)
   - Centralized configuration management
   - Native profile using local filesystem
   - Configurations in `/config` directory

3. **API Gateway** (Port: 8080)
   - Single entry point for all microservices
   - Load balancing with Eureka
   - Route mapping:
     - `/api/orders/**` → Order Service
     - `/api/products/**` → Product Service
     - `/api/notifications/**` → Notification Service

### Business Microservices

1. **Order Service** (Port: 8081)
   - Manages customer orders
   - REST API for order operations
   - Communicates with Product Service via Feign Client
   - Publishes order events to RabbitMQ
   - H2 in-memory database

2. **Product Service** (Port: 8082)
   - Manages product catalog
   - REST API for product operations
   - Pre-loaded with sample products
   - H2 in-memory database

3. **Notification Service** (Port: 8083)
   - Handles notifications
   - Listens to RabbitMQ for order events
   - Sends notifications for order creation/updates
   - REST API to view notification history
   - H2 in-memory database

## Technologies Used

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Config Management**: Spring Cloud Config
- **REST Communication**: OpenFeign
- **Message Queue**: RabbitMQ with AMQP
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **Containerization**: Docker Compose

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for RabbitMQ)

## Getting Started

### 1. Start Infrastructure Services

First, start RabbitMQ using Docker Compose:

```bash
cd microservices-architecture
docker-compose up -d
```

This will start:
- RabbitMQ on port 5672
- RabbitMQ Management Console on http://localhost:15672 (guest/guest)

### 2. Start Services in Order

Start each service in separate terminal windows in the following order:

#### Step 1: Start Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
Wait until Eureka dashboard is available at http://localhost:8761

#### Step 2: Start Config Server
```bash
cd config-server
mvn spring-boot:run
```

#### Step 3: Start Microservices (can be started in parallel)

**Terminal 1 - Product Service:**
```bash
cd product-service
mvn spring-boot:run
```

**Terminal 2 - Order Service:**
```bash
cd order-service
mvn spring-boot:run
```

**Terminal 3 - Notification Service:**
```bash
cd notification-service
mvn spring-boot:run
```

#### Step 4: Start API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

### 3. Verify All Services

Check Eureka Dashboard at http://localhost:8761 to ensure all services are registered:
- CONFIG-SERVER
- API-GATEWAY
- ORDER-SERVICE
- PRODUCT-SERVICE
- NOTIFICATION-SERVICE

## API Endpoints

### Through API Gateway (Recommended)

Base URL: `http://localhost:8080/api`

#### Product Service

```bash
# Get all products
curl http://localhost:8080/api/products/

# Get product by ID
curl http://localhost:8080/api/products/1

# Create a product
curl -X POST http://localhost:8080/api/products/ \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Mouse",
    "description": "High-performance gaming mouse",
    "price": 79.99,
    "stock": 150,
    "category": "Electronics"
  }'

# Search products
curl http://localhost:8080/api/products/search?name=laptop

# Get products by category
curl http://localhost:8080/api/products/category/Electronics
```

#### Order Service

```bash
# Get all orders
curl http://localhost:8080/api/orders/

# Create an order (triggers notification via RabbitMQ)
curl -X POST http://localhost:8080/api/orders/ \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2,
    "customerEmail": "customer@example.com"
  }'

# Get order by ID
curl http://localhost:8080/api/orders/1

# Update order status (triggers notification)
curl -X PUT "http://localhost:8080/api/orders/1/status?status=SHIPPED"

# Get orders by status
curl http://localhost:8080/api/orders/status/PENDING

# Get orders by customer
curl http://localhost:8080/api/orders/customer/customer@example.com
```

#### Notification Service

```bash
# Get all notifications
curl http://localhost:8080/api/notifications/

# Get notification by ID
curl http://localhost:8080/api/notifications/1

# Get notifications by recipient
curl http://localhost:8080/api/notifications/recipient/customer@example.com

# Get notifications by type
curl http://localhost:8080/api/notifications/type/EMAIL
```

### Direct Service Access (for testing)

You can also access services directly:
- Order Service: http://localhost:8081
- Product Service: http://localhost:8082
- Notification Service: http://localhost:8083

## Testing the Complete Flow

### 1. Check Available Products
```bash
curl http://localhost:8080/api/products/
```

### 2. Create an Order
```bash
curl -X POST http://localhost:8080/api/orders/ \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2,
    "customerEmail": "john.doe@example.com"
  }'
```

This will:
- Call Product Service to get product details (REST communication)
- Create the order in Order Service
- Publish an order event to RabbitMQ
- Notification Service receives the event and creates a notification

### 3. Verify Notification
```bash
curl http://localhost:8080/api/notifications/recipient/john.doe@example.com
```

### 4. Update Order Status
```bash
curl -X PUT "http://localhost:8080/api/orders/1/status?status=SHIPPED"
```

This triggers another notification about the status change.

## Database Access

Each service has an H2 console available:

- Order Service: http://localhost:8081/h2-console
- Product Service: http://localhost:8082/h2-console
- Notification Service: http://localhost:8083/h2-console

**Connection Details:**
- JDBC URL: (check application.yml for each service)
- Username: `sa`
- Password: (empty)

## Monitoring Endpoints

Each service exposes actuator endpoints:

```bash
# Health check
curl http://localhost:8081/actuator/health

# Service info
curl http://localhost:8081/actuator/info

# All endpoints
curl http://localhost:8081/actuator
```

## Communication Patterns Demonstrated

1. **REST Communication**
   - Order Service → Product Service (via Feign Client)
   - Client → Services (via API Gateway)

2. **Message Queue Communication**
   - Order Service → RabbitMQ → Notification Service
   - Asynchronous event-driven communication
   - Decoupled services

3. **Service Discovery**
   - All services register with Eureka
   - Dynamic service lookup
   - Load balancing

## Stopping the Application

1. Stop all Spring Boot applications (Ctrl+C in each terminal)
2. Stop Docker containers:
```bash
docker-compose down
```

To remove volumes as well:
```bash
docker-compose down -v
```

## Project Structure

```
microservices-architecture/
├── eureka-server/              # Service Discovery
├── config-server/              # Configuration Server
├── api-gateway/                # API Gateway
├── order-service/              # Order Management Service
├── product-service/            # Product Catalog Service
├── notification-service/       # Notification Service
├── docker-compose.yml          # Infrastructure services
└── README.md                   # This file
```

## Architecture Benefits

1. **Scalability**: Services can be scaled independently
2. **Resilience**: Failure in one service doesn't affect others
3. **Technology Diversity**: Each service can use different technologies
4. **Independent Deployment**: Services can be deployed separately
5. **Organized Codebase**: Clear separation of concerns
6. **Loose Coupling**: Services communicate via well-defined APIs

## Future Enhancements

- Add authentication and authorization (Spring Security + OAuth2)
- Implement distributed tracing (Spring Cloud Sleuth + Zipkin)
- Add circuit breakers (Resilience4j)
- Implement API rate limiting
- Add Redis for caching
- Implement proper database per service
- Add Kubernetes deployment manifests
- Implement saga pattern for distributed transactions

## License

This project is for educational purposes.
