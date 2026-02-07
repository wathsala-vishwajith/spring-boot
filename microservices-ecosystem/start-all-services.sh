#!/bin/bash

echo "========================================="
echo "Starting Microservices Architecture"
echo "========================================="

# Start Eureka Server
echo "Starting Eureka Server..."
cd eureka-server
mvn spring-boot:run > ../logs/eureka-server.log 2>&1 &
EUREKA_PID=$!
cd ..

sleep 20

# Start Config Server
echo "Starting Config Server..."
cd config-server
mvn spring-boot:run > ../logs/config-server.log 2>&1 &
CONFIG_PID=$!
cd ..

sleep 15

# Start API Gateway
echo "Starting API Gateway..."
cd api-gateway
mvn spring-boot:run > ../logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..

sleep 15

# Start Microservices
echo "Starting User Service..."
cd user-service
mvn spring-boot:run > ../logs/user-service.log 2>&1 &
USER_PID=$!
cd ..

echo "Starting Product Service..."
cd product-service
mvn spring-boot:run > ../logs/product-service.log 2>&1 &
PRODUCT_PID=$!
cd ..

echo "Starting Order Service..."
cd order-service
mvn spring-boot:run > ../logs/order-service.log 2>&1 &
ORDER_PID=$!
cd ..

echo "========================================="
echo "All services started!"
echo "========================================="
echo "Eureka Server: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo "User Service: http://localhost:8081"
echo "Order Service: http://localhost:8082"
echo "Product Service: http://localhost:8083"
echo "Config Server: http://localhost:8888"
echo "RabbitMQ Management: http://localhost:15672"
echo "========================================="
echo "Process IDs:"
echo "Eureka: $EUREKA_PID"
echo "Config: $CONFIG_PID"
echo "Gateway: $GATEWAY_PID"
echo "User: $USER_PID"
echo "Product: $PRODUCT_PID"
echo "Order: $ORDER_PID"
echo "========================================="
