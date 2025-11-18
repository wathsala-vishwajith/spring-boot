#!/bin/bash

# OAuth2 with JWT - cURL Examples
# This script provides examples of all API endpoints

BASE_URL="http://localhost:9000"

echo "======================================"
echo "OAuth2 JWT - cURL Examples"
echo "======================================"
echo

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print section headers
print_header() {
    echo -e "\n${BLUE}==== $1 ====${NC}\n"
}

# Function to print commands
print_command() {
    echo -e "${GREEN}$ $1${NC}"
}

# 1. Public Endpoints
print_header "1. Public Endpoints (No Authentication)"

print_command "curl $BASE_URL/api/public/hello"
curl -s $BASE_URL/api/public/hello | jq
echo

print_command "curl $BASE_URL/api/public/info"
curl -s $BASE_URL/api/public/info | jq
echo

# 2. Get Token - Client Credentials
print_header "2. Get Access Token (Client Credentials)"

print_command "curl -X POST $BASE_URL/oauth2/token -u service-client:service-secret -d \"grant_type=client_credentials&scope=api.read api.write\""
TOKEN_RESPONSE=$(curl -s -X POST $BASE_URL/oauth2/token \
  -u service-client:service-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=api.read api.write")

echo $TOKEN_RESPONSE | jq
ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')
echo -e "\nExtracted Access Token: ${ACCESS_TOKEN:0:50}...\n"

# 3. Product Endpoints - Read
print_header "3. Product Endpoints - Read (api.read scope)"

print_command "curl -H \"Authorization: Bearer \$ACCESS_TOKEN\" $BASE_URL/api/products"
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" $BASE_URL/api/products | jq
echo

print_command "curl -H \"Authorization: Bearer \$ACCESS_TOKEN\" $BASE_URL/api/products/1"
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" $BASE_URL/api/products/1 | jq
echo

# 4. Product Endpoints - Write
print_header "4. Product Endpoints - Write (api.write scope)"

print_command "curl -X POST $BASE_URL/api/products -H \"Authorization: Bearer \$ACCESS_TOKEN\" -d '{...}'"
curl -s -X POST $BASE_URL/api/products \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Headset",
    "description": "7.1 Surround Sound Gaming Headset",
    "price": 79.99
  }' | jq
echo

print_command "curl -X PUT $BASE_URL/api/products/1 -H \"Authorization: Bearer \$ACCESS_TOKEN\" -d '{...}'"
curl -s -X PUT $BASE_URL/api/products/1 \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ultra Laptop",
    "description": "High-performance laptop with 32GB RAM",
    "price": 1599.99
  }' | jq
echo

# 5. JWT Inspection
print_header "5. Inspect JWT Token"

echo "Token Header:"
print_command "echo \$ACCESS_TOKEN | cut -d. -f1 | base64 -d"
echo $ACCESS_TOKEN | cut -d. -f1 | base64 -d 2>/dev/null | jq
echo

echo "Token Payload (Claims):"
print_command "echo \$ACCESS_TOKEN | cut -d. -f2 | base64 -d"
echo $ACCESS_TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq
echo

# 6. OpenID Connect Endpoints
print_header "6. OpenID Connect Endpoints"

print_command "curl $BASE_URL/.well-known/openid-configuration"
curl -s $BASE_URL/.well-known/openid-configuration | jq
echo

print_command "curl $BASE_URL/.well-known/jwks.json"
curl -s $BASE_URL/.well-known/jwks.json | jq
echo

# 7. Authorization Code Flow (Manual Steps)
print_header "7. Authorization Code Flow (Manual)"

echo "Step 1: Open in browser:"
echo "  $BASE_URL/oauth2/authorize?response_type=code&client_id=client&scope=openid%20profile%20read%20write&redirect_uri=http://127.0.0.1:8080/authorized"
echo
echo "Step 2: Login with user/password or admin/admin"
echo
echo "Step 3: After redirect, extract the 'code' parameter"
echo
echo "Step 4: Exchange code for token:"
echo "  curl -X POST $BASE_URL/oauth2/token \\"
echo "    -u client:secret \\"
echo "    -d \"grant_type=authorization_code&code=CODE_HERE&redirect_uri=http://127.0.0.1:8080/authorized\""
echo

# 8. Testing Error Cases
print_header "8. Testing Error Cases"

echo "Test 1: Missing Token (Should return 401)"
print_command "curl -i $BASE_URL/api/products"
curl -i -s $BASE_URL/api/products | head -n 1
echo

echo "Test 2: Invalid Token (Should return 401)"
print_command "curl -i -H \"Authorization: Bearer INVALID_TOKEN\" $BASE_URL/api/products"
curl -i -s -H "Authorization: Bearer INVALID_TOKEN" $BASE_URL/api/products | head -n 1
echo

echo "Test 3: Insufficient Scope (with read-only token trying to write)"
echo "  (Would need a read-only token to demonstrate)"
echo

print_header "Examples Complete!"

echo "Saved token for reuse:"
echo "  ACCESS_TOKEN=$ACCESS_TOKEN"
echo
echo "Try these commands yourself:"
echo "  export ACCESS_TOKEN='$ACCESS_TOKEN'"
echo "  curl -H \"Authorization: Bearer \$ACCESS_TOKEN\" $BASE_URL/api/products"
echo
