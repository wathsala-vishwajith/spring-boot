package com.example.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product Service Application
 *
 * This microservice provides a product catalog API.
 *
 * Key Features:
 * - Public REST API (no authentication required)
 * - CRUD operations for products
 * - In-memory H2 database with sample data
 * - Clean REST API design
 *
 * Architecture Pattern:
 * - This is a stateless microservice
 * - Accessed through the Gateway (but doesn't require authentication)
 * - Demonstrates building a simple REST API with Spring Boot
 *
 * Learning Points:
 * - Building a REST API without authentication
 * - JPA entity relationships
 * - Repository pattern
 * - Service layer pattern
 * - DTO pattern for API responses
 * - Data initialization with CommandLineRunner
 */
@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
