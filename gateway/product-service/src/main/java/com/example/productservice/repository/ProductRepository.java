package com.example.productservice.repository;

import com.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Repository
 *
 * Spring Data JPA repository for Product entity.
 *
 * Learning Points:
 * - JpaRepository provides basic CRUD operations automatically
 * - Query methods derived from method names
 * - Spring Data JPA generates implementation at runtime
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find products by category
     * Spring Data JPA auto-generates: SELECT * FROM products WHERE category = ?
     */
    List<Product> findByCategory(String category);

    /**
     * Find active products
     */
    List<Product> findByActiveTrue();

    /**
     * Find products by category and active status
     */
    List<Product> findByCategoryAndActiveTrue(String category);

    /**
     * Search products by name (case-insensitive, partial match)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
