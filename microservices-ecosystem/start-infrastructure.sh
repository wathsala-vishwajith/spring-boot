#!/bin/bash

echo "Starting infrastructure services..."
docker-compose up -d

echo "Waiting for RabbitMQ to be ready..."
sleep 10

echo "Infrastructure services started successfully!"
echo "RabbitMQ Management UI: http://localhost:15672 (guest/guest)"
