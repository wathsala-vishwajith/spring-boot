#!/bin/bash

echo "Stopping Microservices Architecture..."
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Load PIDs if file exists
if [ -f .service-pids ]; then
    source .service-pids

    echo -e "${RED}Stopping all services...${NC}"

    # Kill all processes
    [ ! -z "$GATEWAY_PID" ] && kill $GATEWAY_PID 2>/dev/null && echo "Stopped API Gateway (PID: $GATEWAY_PID)"
    [ ! -z "$NOTIFICATION_PID" ] && kill $NOTIFICATION_PID 2>/dev/null && echo "Stopped Notification Service (PID: $NOTIFICATION_PID)"
    [ ! -z "$ORDER_PID" ] && kill $ORDER_PID 2>/dev/null && echo "Stopped Order Service (PID: $ORDER_PID)"
    [ ! -z "$PRODUCT_PID" ] && kill $PRODUCT_PID 2>/dev/null && echo "Stopped Product Service (PID: $PRODUCT_PID)"
    [ ! -z "$CONFIG_PID" ] && kill $CONFIG_PID 2>/dev/null && echo "Stopped Config Server (PID: $CONFIG_PID)"
    [ ! -z "$EUREKA_PID" ] && kill $EUREKA_PID 2>/dev/null && echo "Stopped Eureka Server (PID: $EUREKA_PID)"

    rm .service-pids
else
    echo "No PID file found. Killing all java processes running Spring Boot..."
    pkill -f "spring-boot:run"
fi

# Stop Docker containers
echo -e "${RED}Stopping RabbitMQ...${NC}"
docker-compose down

echo ""
echo -e "${GREEN}All services stopped successfully!${NC}"
