package com.example.jpa.repository;

import com.example.jpa.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Category repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find by name - uses unique index
     */
    Optional<Category> findByName(String name);

    /**
     * Fetch category with books (prevent N+1)
     */
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.books WHERE c.id = :id")
    Optional<Category> findByIdWithBooks(@Param("id") Long id);

    /**
     * Find all categories with their books
     */
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.books")
    List<Category> findAllWithBooks();
}
