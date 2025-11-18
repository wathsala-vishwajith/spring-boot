package com.example.oauth2.controller;

import com.example.oauth2.model.Product;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource controller demonstrating protected resources with different scopes.
 */
@RestController
@RequestMapping("/api/products")
public class ResourceController {

    private final List<Product> products = new ArrayList<>();

    public ResourceController() {
        // Initialize with some sample products
        products.add(new Product(1L, "Laptop", "High-performance laptop", 1299.99));
        products.add(new Product(2L, "Mouse", "Wireless mouse", 29.99));
        products.add(new Product(3L, "Keyboard", "Mechanical keyboard", 89.99));
    }

    /**
     * Get all products - requires 'read' scope.
     *
     * @return List of all products
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read') or hasAuthority('SCOPE_api.read')")
    public Map<String, Object> getAllProducts() {
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("count", products.size());
        return response;
    }

    /**
     * Get a specific product by ID - requires 'read' scope.
     *
     * @param id The product ID
     * @return The product
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_read') or hasAuthority('SCOPE_api.read')")
    public Product getProduct(@PathVariable Long id) {
        return products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    /**
     * Create a new product - requires 'write' scope.
     *
     * @param product The product to create
     * @return The created product
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_write') or hasAuthority('SCOPE_api.write')")
    public Map<String, Object> createProduct(@RequestBody Product product) {
        product.setId((long) (products.size() + 1));
        products.add(product);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product created successfully");
        response.put("product", product);
        return response;
    }

    /**
     * Update a product - requires 'write' scope.
     *
     * @param id The product ID
     * @param updatedProduct The updated product data
     * @return The updated product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write') or hasAuthority('SCOPE_api.write')")
    public Map<String, Object> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product product = products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product updated successfully");
        response.put("product", product);
        return response;
    }

    /**
     * Delete a product - requires 'write' scope.
     *
     * @param id The product ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write') or hasAuthority('SCOPE_api.write')")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        products.removeIf(p -> p.getId().equals(id));

        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        return response;
    }
}
