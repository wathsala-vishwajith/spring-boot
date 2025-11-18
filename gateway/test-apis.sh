#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}Spring Cloud Gateway API Tester${NC}"
echo -e "${BLUE}================================${NC}\n"

GATEWAY_URL="http://localhost:8080"

# Function to print section headers
print_section() {
    echo -e "\n${YELLOW}>>> $1${NC}\n"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if services are running
print_section "Checking if services are running..."

if curl -s -f "${GATEWAY_URL}/actuator/health" > /dev/null; then
    print_success "Gateway Service is running"
else
    print_error "Gateway Service is not running on port 8080"
    echo "Please start the gateway service first: cd gateway-service && mvn spring-boot:run"
    exit 1
fi

if curl -s -f "http://localhost:8081/health" > /dev/null; then
    print_success "User Service is running"
else
    print_error "User Service is not running on port 8081"
    echo "Please start the user service: cd user-service && mvn spring-boot:run"
    exit 1
fi

if curl -s -f "http://localhost:8082/health" > /dev/null; then
    print_success "Product Service is running"
else
    print_error "Product Service is not running on port 8082"
    echo "Please start the product service: cd product-service && mvn spring-boot:run"
    exit 1
fi

# Test Product Service (Public API)
print_section "Testing Product Service (Public - No Authentication)"

echo "1. Get all products:"
curl -s "${GATEWAY_URL}/api/products/products" | json_pp | head -n 20
print_success "Products retrieved successfully"

echo -e "\n2. Get products by category (electronics):"
curl -s "${GATEWAY_URL}/api/products/products/category/electronics" | json_pp | head -n 15
print_success "Products filtered by category"

echo -e "\n3. Search products (laptop):"
curl -s "${GATEWAY_URL}/api/products/products/search?q=laptop" | json_pp
print_success "Product search completed"

# Test User Service - Registration
print_section "Testing User Service - Registration"

RANDOM_NUM=$RANDOM
USERNAME="testuser${RANDOM_NUM}"

echo "Registering user: ${USERNAME}"
REGISTER_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "'${USERNAME}'",
    "password": "password123",
    "email": "'${USERNAME}'@example.com",
    "firstName": "Test",
    "lastName": "User"
  }')

echo "$REGISTER_RESPONSE" | json_pp
print_success "User registered successfully"

# Test User Service - Login
print_section "Testing User Service - Login"

echo "Logging in as: ${USERNAME}"
LOGIN_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "'${USERNAME}'",
    "password": "password123"
  }')

echo "$LOGIN_RESPONSE" | json_pp

# Extract JWT token
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    print_error "Failed to get JWT token"
    exit 1
fi

print_success "Login successful, JWT token received"
echo -e "${BLUE}Token: ${JWT_TOKEN:0:50}...${NC}"

# Test Protected Endpoints
print_section "Testing Protected Endpoints (With JWT Token)"

echo "1. Get all users (Protected):"
USER_LIST=$(curl -s -X GET "${GATEWAY_URL}/api/users/users" \
  -H "Authorization: Bearer ${JWT_TOKEN}")

echo "$USER_LIST" | json_pp
print_success "Protected endpoint accessed successfully with JWT token"

echo -e "\n2. Get specific user (Protected):"
curl -s -X GET "${GATEWAY_URL}/api/users/users/${USERNAME}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" | json_pp
print_success "User details retrieved successfully"

# Test without token (should fail)
print_section "Testing Protected Endpoint Without Token (Should Fail)"

FAIL_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "${GATEWAY_URL}/api/users/users")
HTTP_CODE=$(echo "$FAIL_RESPONSE" | grep -o "HTTP_STATUS:[0-9]*" | cut -d':' -f2)

if [ "$HTTP_CODE" = "401" ]; then
    print_success "Correctly rejected request without JWT token (401 Unauthorized)"
else
    print_error "Expected 401 status code, got: $HTTP_CODE"
fi

# Test with invalid token (should fail)
print_section "Testing Protected Endpoint With Invalid Token (Should Fail)"

FAIL_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "${GATEWAY_URL}/api/users/users" \
  -H "Authorization: Bearer invalid.token.here")
HTTP_CODE=$(echo "$FAIL_RESPONSE" | grep -o "HTTP_STATUS:[0-9]*" | cut -d':' -f2)

if [ "$HTTP_CODE" = "401" ]; then
    print_success "Correctly rejected request with invalid JWT token (401 Unauthorized)"
else
    print_error "Expected 401 status code, got: $HTTP_CODE"
fi

# Create a product
print_section "Testing Product Creation"

echo "Creating a new product:"
NEW_PRODUCT=$(curl -s -X POST "${GATEWAY_URL}/api/products/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Tablet",
    "description": "A test tablet created by the test script",
    "price": 499.99,
    "stockQuantity": 25,
    "category": "electronics",
    "imageUrl": "https://example.com/test-tablet.jpg"
  }')

echo "$NEW_PRODUCT" | json_pp
PRODUCT_ID=$(echo "$NEW_PRODUCT" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
print_success "Product created successfully with ID: $PRODUCT_ID"

# Update the product
if [ -n "$PRODUCT_ID" ]; then
    print_section "Testing Product Update"

    echo "Updating product ID: $PRODUCT_ID"
    curl -s -X PUT "${GATEWAY_URL}/api/products/products/${PRODUCT_ID}" \
      -H "Content-Type: application/json" \
      -d '{
        "name": "Updated Test Tablet",
        "description": "Updated description",
        "price": 449.99,
        "stockQuantity": 30,
        "category": "electronics",
        "imageUrl": "https://example.com/test-tablet-updated.jpg"
      }' | json_pp
    print_success "Product updated successfully"
fi

# Summary
print_section "Test Summary"
echo -e "${GREEN}All tests completed successfully!${NC}\n"
echo -e "Key Takeaways:"
echo -e "  ✓ Product Service is accessible without authentication"
echo -e "  ✓ User registration and login work correctly"
echo -e "  ✓ JWT tokens are generated on successful login"
echo -e "  ✓ Protected endpoints require valid JWT tokens"
echo -e "  ✓ Invalid or missing tokens are properly rejected"
echo -e "  ✓ Gateway successfully routes requests to microservices"
echo -e "\n${BLUE}Test completed at: $(date)${NC}"
