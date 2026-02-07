package com.example.caching.service;

import com.example.caching.model.Product;
import com.example.caching.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service demonstrating in-memory caching with Caffeine.
 *
 * This service uses Spring's declarative caching annotations:
 * - @Cacheable: Caches the method result
 * - @CachePut: Updates the cache
 * - @CacheEvict: Removes entries from cache
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Retrieves a product by ID with caching.
     *
     * First call: Fetches from repository (slow - 500ms)
     * Subsequent calls: Returns from cache (fast - <1ms)
     *
     * The cache key is the product ID.
     * Cache expires after 10 minutes of write or 5 minutes of no access.
     */
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        log.info("Fetching product {} from database (cache miss)", id);
        return productRepository.findById(id);
    }

    /**
     * Retrieves all products with caching.
     *
     * Uses a constant key since the method has no parameters.
     */
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        log.info("Fetching all products from database (cache miss)");
        return productRepository.findAll();
    }

    /**
     * Retrieves products by category with caching.
     *
     * Each category is cached separately using the category as the key.
     */
    @Cacheable(value = "productsByCategory", key = "#category")
    public List<Product> getProductsByCategory(String category) {
        log.info("Fetching products for category '{}' from database (cache miss)", category);
        return productRepository.findByCategory(category);
    }

    /**
     * Creates a new product and invalidates the "all products" cache.
     *
     * @CacheEvict removes specific entries from the cache.
     * allEntries = false means only specific keys are evicted.
     */
    @CacheEvict(value = "products", key = "'all'")
    public Product createProduct(Product product) {
        log.info("Creating new product and evicting 'all' products cache");
        return productRepository.save(product);
    }

    /**
     * Updates a product and refreshes its cache entry.
     *
     * @CachePut updates the cache without interfering with method execution.
     * The method always executes, and the result is placed in the cache.
     */
    @CachePut(value = "products", key = "#product.id")
    @CacheEvict(value = "products", key = "'all'")
    public Product updateProduct(Product product) {
        log.info("Updating product {} and refreshing cache", product.getId());
        return productRepository.save(product);
    }

    /**
     * Deletes a product and removes it from cache.
     *
     * Evicts both the specific product and the "all products" cache.
     */
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        log.info("Deleting product {} and evicting from cache", id);
        productRepository.deleteById(id);
    }

    /**
     * Clears all product caches.
     *
     * allEntries = true removes all entries from the cache.
     * Useful for bulk operations or cache warming.
     */
    @CacheEvict(value = {"products", "productsByCategory"}, allEntries = true)
    public void clearAllCaches() {
        log.info("Clearing all product caches");
    }
}
