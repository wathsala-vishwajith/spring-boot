package com.example.productservice.service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Service
 *
 * Business logic layer for product management.
 *
 * Responsibilities:
 * - Validate business rules
 * - Coordinate repository operations
 * - Transform between entities and DTOs
 * - Handle business exceptions
 *
 * @Transactional ensures database consistency:
 * - If any exception occurs, all changes are rolled back
 * - Multiple database operations execute as a single unit
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Get all products
     */
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get active products only
     */
    public List<ProductResponse> getActiveProducts() {
        log.info("Fetching active products");
        return productRepository.findByActiveTrue()
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return ProductResponse.from(product);
    }

    /**
     * Get products by category
     */
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products in category: {}", category);
        return productRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Search products by name
     */
    public List<ProductResponse> searchProducts(String query) {
        log.info("Searching products with query: {}", query);
        return productRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Create new product
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setActive(true);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        return ProductResponse.from(savedProduct);
    }

    /**
     * Update existing product
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", id);

        return ProductResponse.from(updatedProduct);
    }

    /**
     * Delete product (soft delete - marks as inactive)
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setActive(false);
        productRepository.save(product);

        log.info("Product deleted successfully: {}", id);
    }
}
