package com.example.productservice.controller;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product Controller
 *
 * REST API endpoints for product catalog.
 * All endpoints are PUBLIC (no authentication required).
 *
 * Endpoints:
 * - GET /products - List all products
 * - GET /products/active - List active products
 * - GET /products/{id} - Get specific product
 * - GET /products/category/{category} - Get products by category
 * - GET /products/search?q={query} - Search products
 * - POST /products - Create new product
 * - PUT /products/{id} - Update product
 * - DELETE /products/{id} - Delete product (soft delete)
 *
 * REST API Design Principles:
 * - Resource-based URLs (nouns, not verbs)
 * - Appropriate HTTP methods (GET, POST, PUT, DELETE)
 * - Meaningful status codes (200, 201, 404, 400)
 * - Consistent response format
 * - Query parameters for filtering/searching
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Get all products
     *
     * Example: GET /products
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("GET /products - Fetching all products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get active products only
     *
     * Example: GET /products/active
     */
    @GetMapping("/products/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        log.info("GET /products/active - Fetching active products");
        List<ProductResponse> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get product by ID
     *
     * Example: GET /products/1
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            log.info("GET /products/{} - Fetching product", id);
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            log.error("Product not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get products by category
     *
     * Example: GET /products/category/electronics
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        log.info("GET /products/category/{} - Fetching products", category);
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Search products by name
     *
     * Example: GET /products/search?q=laptop
     */
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String q) {
        log.info("GET /products/search?q={} - Searching products", q);
        List<ProductResponse> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    /**
     * Create new product
     *
     * Example:
     * POST /products
     * {
     *   "name": "Laptop",
     *   "description": "High-performance laptop",
     *   "price": 999.99,
     *   "stockQuantity": 50,
     *   "category": "electronics",
     *   "imageUrl": "https://example.com/laptop.jpg"
     * }
     */
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            log.info("POST /products - Creating product: {}", request.getName());
            ProductResponse product = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (RuntimeException e) {
            log.error("Product creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update product
     *
     * Example: PUT /products/1
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        try {
            log.info("PUT /products/{} - Updating product", id);
            ProductResponse product = productService.updateProduct(id, request);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            log.error("Product update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete product (soft delete)
     *
     * Example: DELETE /products/1
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            log.info("DELETE /products/{} - Deleting product", id);
            productService.deleteProduct(id);
            return ResponseEntity.ok(new SuccessResponse("Product deleted successfully"));
        } catch (RuntimeException e) {
            log.error("Product deletion failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Product Service is running");
    }

    /**
     * Response DTOs
     */
    record ErrorResponse(String message) {}
    record SuccessResponse(String message) {}
}
