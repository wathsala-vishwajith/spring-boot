# API Testing Examples

This document contains cURL examples for testing all microservices endpoints.

## Base URLs

- API Gateway: `http://localhost:8080`
- User Service (Direct): `http://localhost:8081`
- Order Service (Direct): `http://localhost:8082`
- Product Service (Direct): `http://localhost:8083`

**Note**: Use API Gateway URLs in production. Direct service URLs are for testing only.

---

## User Service APIs

### Create User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_smith",
    "email": "alice@example.com",
    "firstName": "Alice",
    "lastName": "Smith"
  }'
```

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob_jones",
    "email": "bob@example.com",
    "firstName": "Bob",
    "lastName": "Jones"
  }'
```

### Get All Users

```bash
curl http://localhost:8080/api/users
```

### Get User by ID

```bash
curl http://localhost:8080/api/users/1
```

### Update User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_updated",
    "email": "alice.new@example.com",
    "firstName": "Alice",
    "lastName": "Smith-Johnson"
  }'
```

### Delete User

```bash
curl -X DELETE http://localhost:8080/api/users/2
```

---

## Product Service APIs

### Create Products

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro",
    "description": "16-inch laptop with M3 chip",
    "price": 2499.99,
    "stock": 25,
    "category": "Electronics"
  }'
```

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "description": "Latest smartphone",
    "price": 999.99,
    "stock": 100,
    "category": "Electronics"
  }'
```

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Office Chair",
    "description": "Ergonomic office chair",
    "price": 299.99,
    "stock": 50,
    "category": "Furniture"
  }'
```

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Standing Desk",
    "description": "Adjustable height desk",
    "price": 599.99,
    "stock": 30,
    "category": "Furniture"
  }'
```

### Get All Products

```bash
curl http://localhost:8080/api/products
```

### Get Product by ID

```bash
curl http://localhost:8080/api/products/1
```

### Get Products by Category

```bash
curl http://localhost:8080/api/products/category/Electronics
```

```bash
curl http://localhost:8080/api/products/category/Furniture
```

### Update Product

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro (Updated)",
    "description": "16-inch laptop with M3 Pro chip",
    "price": 2699.99,
    "stock": 25,
    "category": "Electronics"
  }'
```

### Update Product Stock

```bash
# Add 10 items to stock
curl -X PATCH "http://localhost:8080/api/products/1/stock?quantity=10"
```

```bash
# Decrease stock by 5 items
curl -X PATCH "http://localhost:8080/api/products/1/stock?quantity=-5"
```

### Delete Product

```bash
curl -X DELETE http://localhost:8080/api/products/4
```

---

## Order Service APIs

### Create Orders

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }'
```

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 2,
    "quantity": 1
  }'
```

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "productId": 3,
    "quantity": 3
  }'
```

### Get All Orders

```bash
curl http://localhost:8080/api/orders
```

This will return orders with full user and product details populated via REST calls.

### Get Order by ID

```bash
curl http://localhost:8080/api/orders/1
```

### Update Order Status

```bash
# Confirm order
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=CONFIRMED"
```

```bash
# Ship order
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=SHIPPED"
```

```bash
# Deliver order
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=DELIVERED"
```

```bash
# Cancel order
curl -X PATCH "http://localhost:8080/api/orders/2/status?status=CANCELLED"
```

**Available Order Status Values:**
- PENDING
- CONFIRMED
- SHIPPED
- DELIVERED
- CANCELLED

### Delete Order

```bash
curl -X DELETE http://localhost:8080/api/orders/3
```

---

## Complete Test Scenario

Here's a complete workflow to test the entire system:

```bash
# 1. Create users
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "email": "john@example.com", "firstName": "John", "lastName": "Doe"}'

# 2. Create products
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop", "description": "Gaming laptop", "price": 1499.99, "stock": 20, "category": "Electronics"}'

# 3. Verify user exists
curl http://localhost:8080/api/users/1

# 4. Verify product exists
curl http://localhost:8080/api/products/1

# 5. Create an order (this will trigger REST calls to User and Product services)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "productId": 1, "quantity": 2}'

# 6. Get order details (will show enriched data with user and product info)
curl http://localhost:8080/api/orders/1

# 7. Update order status
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=CONFIRMED"
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=SHIPPED"
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=DELIVERED"

# 8. Check final order state
curl http://localhost:8080/api/orders/1
```

---

## Monitoring Endpoints

### Service Health Checks

```bash
# Eureka Server
curl http://localhost:8761/actuator/health

# Config Server
curl http://localhost:8888/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health

# User Service
curl http://localhost:8081/actuator/health

# Product Service
curl http://localhost:8083/actuator/health

# Order Service
curl http://localhost:8082/actuator/health
```

### Service Discovery

```bash
# View all registered services
curl http://localhost:8761/eureka/apps

# View specific service instances
curl http://localhost:8761/eureka/apps/USER-SERVICE
curl http://localhost:8761/eureka/apps/PRODUCT-SERVICE
curl http://localhost:8761/eureka/apps/ORDER-SERVICE
```

### Gateway Routes

```bash
# View configured routes
curl http://localhost:8080/actuator/gateway/routes
```

---

## Event Monitoring

After each operation, check RabbitMQ Management Console to see published events:

**RabbitMQ Management**: http://localhost:15672
- Username: guest
- Password: guest

Navigate to:
1. **Queues** tab to see message counts
2. **Exchanges** tab to see the `microservices.exchange`
3. Click on queues to get/purge messages

---

## Error Scenarios

### Non-existent User

```bash
# Try to create order with non-existent user
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 999, "productId": 1, "quantity": 2}'
```

This should fail with a Feign client error.

### Non-existent Product

```bash
# Try to create order with non-existent product
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "productId": 999, "quantity": 2}'
```

This should also fail with a Feign client error.

---

## Tips

1. Use `jq` for pretty JSON output:
```bash
curl http://localhost:8080/api/users | jq
```

2. Save response to variable:
```bash
USER_ID=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "email": "test@example.com", "firstName": "Test", "lastName": "User"}' \
  | jq -r '.id')

echo "Created user with ID: $USER_ID"
```

3. Monitor logs in real-time:
```bash
tail -f logs/user-service.log
tail -f logs/order-service.log
tail -f logs/product-service.log
```
