# Product REST API - Spring Boot Best Practices

A comprehensive RESTful API built with Java Spring Boot that demonstrates industry best practices for API development.

## Features

### ✅ RESTful Best Practices

- **Proper HTTP Methods**: GET, POST, PUT, DELETE
- **Appropriate Status Codes**: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 404 Not Found, 409 Conflict, 500 Internal Server Error
- **Resource-oriented URLs**: `/api/v1/products`, `/api/v1/products/{id}`
- **Consistent Response Format**: Standardized error responses with detailed information

### ✅ API Versioning

- **URI Versioning**: `/api/v1/products`
- **Future-proof**: Easy to add v2, v3, etc. while maintaining backward compatibility

### ✅ Pagination & Filtering

- **Page-based Pagination**: Configurable page size and number
- **Sorting**: Sort by any field in ascending or descending order
- **Multiple Filters**:
  - Category filtering
  - Price range filtering (min/max)
  - Active status filtering
  - Full-text search (name and description)
  - Combined filters

### ✅ HATEOAS (Hypermedia)

- **Discoverability**: Each resource includes links to related actions
- **Self-documentation**: Links guide clients on available operations
- **Example Links**:
  - `self`: Link to the resource itself
  - `all-products`: Link to the product collection
  - `update`: Link to update the resource
  - `delete`: Link to delete the resource

### ✅ OpenAPI/Swagger Documentation

- **Interactive API Documentation**: Swagger UI at `/swagger-ui.html`
- **OpenAPI Specification**: JSON spec at `/v3/api-docs`
- **Try it out**: Test endpoints directly from the browser

### ✅ Additional Features

- **Input Validation**: Request validation with detailed error messages
- **Global Exception Handling**: Consistent error responses
- **Soft Delete**: Products are marked inactive instead of being permanently deleted
- **Timestamps**: Automatic creation and update timestamps
- **CORS Support**: Configured for cross-origin requests

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: For database operations
- **Spring HATEOAS**: For hypermedia support
- **SpringDoc OpenAPI**: For API documentation (Swagger)
- **H2 Database**: In-memory database for demo purposes
- **Lombok**: To reduce boilerplate code
- **Maven**: Build tool

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**
   ```bash
   cd product-api
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api/v1`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:productdb`
     - Username: `sa`
     - Password: (leave empty)

## API Endpoints

### Product Management

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/api/v1/products` | Get all products (paginated) | 200 |
| GET | `/api/v1/products/{id}` | Get product by ID | 200, 404 |
| POST | `/api/v1/products` | Create a new product | 201, 400, 409 |
| PUT | `/api/v1/products/{id}` | Update a product | 200, 400, 404 |
| DELETE | `/api/v1/products/{id}` | Delete a product (soft) | 204, 404 |

## Usage Examples

### 1. Get All Products (with pagination)

```bash
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=10&sort=name,asc"
```

### 2. Get All Products (with filtering)

```bash
# Filter by category
curl -X GET "http://localhost:8080/api/v1/products?category=Electronics"

# Filter by price range
curl -X GET "http://localhost:8080/api/v1/products?minPrice=20&maxPrice=100"

# Search by name/description
curl -X GET "http://localhost:8080/api/v1/products?search=mouse"

# Combined filters
curl -X GET "http://localhost:8080/api/v1/products?category=Electronics&minPrice=20&maxPrice=100&active=true&search=wireless"
```

### 3. Get Product by ID

```bash
curl -X GET "http://localhost:8080/api/v1/products/1"
```

**Response with HATEOAS links:**
```json
{
  "id": 1,
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse with 6 programmable buttons",
  "price": 29.99,
  "category": "Electronics",
  "stockQuantity": 150,
  "sku": "WM-001",
  "active": true,
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/products/1"
    },
    "all-products": {
      "href": "http://localhost:8080/api/v1/products"
    },
    "update": {
      "href": "http://localhost:8080/api/v1/products/1"
    },
    "delete": {
      "href": "http://localhost:8080/api/v1/products/1"
    }
  }
}
```

### 4. Create a New Product

```bash
curl -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse with 6 buttons",
    "price": 29.99,
    "category": "Electronics",
    "stockQuantity": 100,
    "sku": "WM-002",
    "active": true
  }'
```

**Response:** `201 Created` with `Location` header

### 5. Update a Product

```bash
curl -X PUT "http://localhost:8080/api/v1/products/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse Pro",
    "description": "Updated description",
    "price": 34.99,
    "category": "Electronics",
    "stockQuantity": 120,
    "sku": "WM-001",
    "active": true
  }'
```

**Response:** `200 OK` with updated product

### 6. Delete a Product

```bash
curl -X DELETE "http://localhost:8080/api/v1/products/1"
```

**Response:** `204 No Content`

## Validation Rules

### Product Request Validation

- **name**: Required, 2-100 characters
- **description**: Optional, max 1000 characters
- **price**: Required, must be > 0, max 10 digits + 2 decimals
- **category**: Required
- **stockQuantity**: Required, must be >= 0
- **sku**: Required, alphanumeric with hyphens only (e.g., WM-001)
- **active**: Optional, defaults to true

### Error Response Example

```json
{
  "status": 400,
  "message": "Validation Failed",
  "details": "Input validation failed. Please check the errors.",
  "timestamp": "2025-01-01T10:00:00",
  "path": "/api/v1/products",
  "validationErrors": [
    {
      "field": "price",
      "message": "Price must be greater than 0"
    },
    {
      "field": "sku",
      "message": "SKU must contain only uppercase letters, numbers, and hyphens"
    }
  ]
}
```

## Query Parameters

### Pagination Parameters

- `page`: Page number (0-indexed, default: 0)
- `size`: Page size (default: 10)
- `sort`: Sort field and direction (format: `field,direction`, e.g., `name,asc`)

### Filter Parameters

- `category`: Filter by category (exact match)
- `active`: Filter by active status (true/false)
- `minPrice`: Minimum price (inclusive)
- `maxPrice`: Maximum price (inclusive)
- `search`: Search term (searches name and description, case-insensitive)

## Project Structure

```
product-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/productapi/
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java       # Swagger/OpenAPI configuration
│   │   │   │   └── WebConfig.java           # Web and CORS configuration
│   │   │   ├── controller/
│   │   │   │   └── v1/
│   │   │   │       └── ProductController.java  # REST endpoints
│   │   │   ├── dto/
│   │   │   │   ├── ProductRequest.java      # Request DTO
│   │   │   │   └── ProductResponse.java     # Response DTO with HATEOAS
│   │   │   ├── exception/
│   │   │   │   ├── ErrorResponse.java       # Error response structure
│   │   │   │   ├── GlobalExceptionHandler.java  # Global error handling
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   └── Product.java             # JPA entity
│   │   │   ├── repository/
│   │   │   │   └── ProductRepository.java   # Data access layer
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java      # Service interface
│   │   │   │   └── ProductServiceImpl.java  # Service implementation
│   │   │   └── ProductApiApplication.java   # Main application class
│   │   └── resources/
│   │       ├── application.yml              # Application configuration
│   │       └── data.sql                     # Sample data
│   └── test/
│       └── java/com/example/productapi/
│           └── ProductApiApplicationTests.java
└── pom.xml                                  # Maven dependencies
```

## Best Practices Demonstrated

1. **Separation of Concerns**: Clear separation between controller, service, and repository layers
2. **DTO Pattern**: Separate request/response DTOs from domain entities
3. **Exception Handling**: Centralized exception handling with meaningful error responses
4. **Validation**: Input validation at the controller level
5. **Transactions**: Proper transaction management in the service layer
6. **Soft Delete**: Products are deactivated instead of permanently deleted
7. **Timestamps**: Automatic tracking of creation and update times
8. **API Documentation**: Comprehensive OpenAPI/Swagger documentation
9. **HATEOAS**: Hypermedia links for API discoverability
10. **RESTful Design**: Resource-oriented URLs and proper HTTP method usage

## Testing the API

### Using Swagger UI

1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Explore all available endpoints
3. Click "Try it out" on any endpoint
4. Fill in the parameters
5. Click "Execute" to test the endpoint

### Using cURL

See the "Usage Examples" section above for cURL commands.

### Using Postman

1. Import the OpenAPI specification from `http://localhost:8080/v3/api-docs`
2. Postman will automatically create a collection with all endpoints
3. Test the endpoints using the Postman interface

## Database

The application uses an in-memory H2 database that is initialized with sample data on startup.

**Access H2 Console:**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:productdb`
- Username: `sa`
- Password: (leave empty)

**Sample Data:**
The application comes pre-loaded with 15 sample products across different categories:
- Electronics (Mouse, Keyboard, Webcam, etc.)
- Accessories (Laptop Stand, Monitor Arm, etc.)
- Office Supplies (Notebooks, Pens, Desk Lamp, etc.)

## Future Enhancements

- Add authentication and authorization (Spring Security, JWT)
- Implement rate limiting
- Add caching (Redis)
- Add monitoring (Spring Boot Actuator, Prometheus)
- Add API versioning through headers
- Implement GraphQL endpoint
- Add comprehensive unit and integration tests
- Add Docker support
- Add CI/CD pipeline

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support

For issues and questions, please refer to the Swagger documentation at `/swagger-ui.html` or check the OpenAPI specification at `/v3/api-docs`.
