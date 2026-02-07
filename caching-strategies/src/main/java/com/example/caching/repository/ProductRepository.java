package com.example.caching.repository;

import com.example.caching.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simulated repository for Product entities.
 * In a real application, this would interact with a database.
 * Uses ConcurrentHashMap to simulate database storage.
 */
@Repository
public class ProductRepository {

    private final Map<Long, Product> database = new ConcurrentHashMap<>();
    private Long idCounter = 1L;

    public ProductRepository() {
        initializeData();
    }

    private void initializeData() {
        // Initialize with sample data
        saveProduct(Product.builder()
                .name("Laptop Pro 15")
                .description("High-performance laptop with 16GB RAM")
                .price(new BigDecimal("1299.99"))
                .category("Electronics")
                .stockQuantity(50)
                .build());

        saveProduct(Product.builder()
                .name("Wireless Mouse")
                .description("Ergonomic wireless mouse")
                .price(new BigDecimal("29.99"))
                .category("Electronics")
                .stockQuantity(200)
                .build());

        saveProduct(Product.builder()
                .name("Office Chair")
                .description("Comfortable ergonomic office chair")
                .price(new BigDecimal("249.99"))
                .category("Furniture")
                .stockQuantity(30)
                .build());

        saveProduct(Product.builder()
                .name("Mechanical Keyboard")
                .description("RGB mechanical keyboard with blue switches")
                .price(new BigDecimal("89.99"))
                .category("Electronics")
                .stockQuantity(75)
                .build());

        saveProduct(Product.builder()
                .name("Desk Lamp")
                .description("LED desk lamp with adjustable brightness")
                .price(new BigDecimal("39.99"))
                .category("Furniture")
                .stockQuantity(100)
                .build());
    }

    public Product findById(Long id) {
        // Simulate database latency
        simulateLatency();
        return database.get(id);
    }

    public List<Product> findAll() {
        simulateLatency();
        return new ArrayList<>(database.values());
    }

    public List<Product> findByCategory(String category) {
        simulateLatency();
        return database.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public Product save(Product product) {
        simulateLatency();
        return saveProduct(product);
    }

    private Product saveProduct(Product product) {
        if (product.getId() == null) {
            product.setId(idCounter++);
            product.setCreatedAt(LocalDateTime.now());
        }
        product.setUpdatedAt(LocalDateTime.now());
        database.put(product.getId(), product);
        return product;
    }

    public void deleteById(Long id) {
        simulateLatency();
        database.remove(id);
    }

    public boolean existsById(Long id) {
        return database.containsKey(id);
    }

    /**
     * Simulates database query latency (500ms)
     */
    private void simulateLatency() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
