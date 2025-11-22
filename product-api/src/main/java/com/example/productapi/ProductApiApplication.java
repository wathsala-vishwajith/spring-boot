package com.example.productapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Product REST API.
 * Demonstrates RESTful API best practices including:
 * - Proper HTTP methods and status codes
 * - API versioning
 * - Pagination and filtering
 * - HATEOAS
 * - OpenAPI/Swagger documentation
 */
@SpringBootApplication
public class ProductApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApiApplication.class, args);
    }
}
