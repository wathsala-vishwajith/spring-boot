package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Service interface for Product operations.
 */
public interface ProductService {

    /**
     * Create a new product
     */
    Product createProduct(ProductRequest request);

    /**
     * Get product by ID
     */
    Product getProductById(Long id);

    /**
     * Get all products with pagination
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * Update an existing product
     */
    Product updateProduct(Long id, ProductRequest request);

    /**
     * Delete a product (soft delete by setting active = false)
     */
    void deleteProduct(Long id);

    /**
     * Get products with filters and pagination
     */
    Page<Product> getProductsWithFilters(
            String category,
            Boolean active,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String searchTerm,
            Pageable pageable
    );

    /**
     * Search products by name or description
     */
    Page<Product> searchProducts(String searchTerm, Pageable pageable);
}
