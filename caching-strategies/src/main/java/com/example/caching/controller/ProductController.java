package com.example.caching.controller;

import com.example.caching.model.Product;
import com.example.caching.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller demonstrating in-memory caching with Caffeine.
 *
 * Test the caching behavior:
 * 1. Call GET /api/products/{id} twice - first call is slow, second is fast
 * 2. Update the product - cache is refreshed
 * 3. Call GET again - fast response from updated cache
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Get product by ID.
     *
     * First call: ~500ms (database query)
     * Subsequent calls: <1ms (from Caffeine cache)
     *
     * Example: GET http://localhost:8080/api/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        Product product = productService.getProductById(id);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Request took " + duration + "ms");

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Get all products.
     *
     * Example: GET http://localhost:8080/api/products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        long startTime = System.currentTimeMillis();
        List<Product> products = productService.getAllProducts();
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Request took " + duration + "ms");

        return ResponseEntity.ok(products);
    }

    /**
     * Get products by category.
     *
     * Example: GET http://localhost:8080/api/products/category/Electronics
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        long startTime = System.currentTimeMillis();
        List<Product> products = productService.getProductsByCategory(category);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Request took " + duration + "ms");

        return ResponseEntity.ok(products);
    }

    /**
     * Create a new product.
     *
     * This invalidates the "all products" cache.
     *
     * Example: POST http://localhost:8080/api/products
     * Body: {"name": "New Product", "price": 99.99, "category": "Electronics"}
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update a product.
     *
     * This updates the cache using @CachePut.
     *
     * Example: PUT http://localhost:8080/api/products/1
     * Body: {"id": 1, "name": "Updated Product", "price": 149.99}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        Product updated = productService.updateProduct(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a product.
     *
     * This removes the product from cache.
     *
     * Example: DELETE http://localhost:8080/api/products/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clear all product caches.
     *
     * Example: POST http://localhost:8080/api/products/cache/clear
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        productService.clearAllCaches();
        return ResponseEntity.ok("All product caches cleared");
    }
}
