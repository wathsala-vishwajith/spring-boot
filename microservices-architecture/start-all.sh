#!/bin/bash

echo "Starting Microservices Architecture..."
echo "========================================"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Start RabbitMQ
echo -e "${YELLOW}Starting RabbitMQ...${NC}"
docker-compose up -d
sleep 5

# Start Eureka Server
echo -e "${YELLOW}Starting Eureka Server...${NC}"
cd eureka-server
mvn spring-boot:run > ../logs/eureka-server.log 2>&1 &
EUREKA_PID=$!
echo "Eureka Server PID: $EUREKA_PID"
cd ..
sleep 15

# Start Config Server
echo -e "${YELLOW}Starting Config Server...${NC}"
cd config-server
mvn spring-boot:run > ../logs/config-server.log 2>&1 &
CONFIG_PID=$!
echo "Config Server PID: $CONFIG_PID"
cd ..
sleep 10

# Start Product Service
echo -e "${YELLOW}Starting Product Service...${NC}"
cd product-service
mvn spring-boot:run > ../logs/product-service.log 2>&1 &
PRODUCT_PID=$!
echo "Product Service PID: $PRODUCT_PID"
cd ..
sleep 10

# Start Order Service
echo -e "${YELLOW}Starting Order Service...${NC}"
cd order-service
mvn spring-boot:run > ../logs/order-service.log 2>&1 &
ORDER_PID=$!
echo "Order Service PID: $ORDER_PID"
cd ..
sleep 10

# Start Notification Service
echo -e "${YELLOW}Starting Notification Service...${NC}"
cd notification-service
mvn spring-boot:run > ../logs/notification-service.log 2>&1 &
NOTIFICATION_PID=$!
echo "Notification Service PID: $NOTIFICATION_PID"
cd ..
sleep 10

# Start API Gateway
echo -e "${YELLOW}Starting API Gateway...${NC}"
cd api-gateway
mvn spring-boot:run > ../logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!
echo "API Gateway PID: $GATEWAY_PID"
cd ..
sleep 10

echo ""
echo -e "${GREEN}========================================"
echo "All services started successfully!"
echo "========================================${NC}"
echo ""
echo "Service URLs:"
echo "  - Eureka Server: http://localhost:8761"
echo "  - Config Server: http://localhost:8888"
echo "  - API Gateway: http://localhost:8080"
echo "  - Order Service: http://localhost:8081"
echo "  - Product Service: http://localhost:8082"
echo "  - Notification Service: http://localhost:8083"
echo "  - RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo ""
echo "Process IDs:"
echo "  - Eureka Server: $EUREKA_PID"
echo "  - Config Server: $CONFIG_PID"
echo "  - Product Service: $PRODUCT_PID"
echo "  - Order Service: $ORDER_PID"
echo "  - Notification Service: $NOTIFICATION_PID"
echo "  - API Gateway: $GATEWAY_PID"
echo ""
echo "To stop all services, run: ./stop-all.sh"
echo ""

# Save PIDs to file for stop script
cat > .service-pids << EOF
EUREKA_PID=$EUREKA_PID
CONFIG_PID=$CONFIG_PID
PRODUCT_PID=$PRODUCT_PID
ORDER_PID=$ORDER_PID
NOTIFICATION_PID=$NOTIFICATION_PID
GATEWAY_PID=$GATEWAY_PID
EOF
