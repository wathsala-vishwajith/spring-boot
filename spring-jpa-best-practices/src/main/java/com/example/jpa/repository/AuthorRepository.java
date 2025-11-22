package com.example.jpa.repository;

import com.example.jpa.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Author repository with optimized queries
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Find by email - uses unique index (idx_authors_email)
     */
    Optional<Author> findByEmail(String email);

    /**
     * Find by last name - uses index (idx_authors_last_name)
     */
    List<Author> findByLastName(String lastName);

    /**
     * Fetch author with all books (JOIN FETCH to prevent N+1)
     */
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);

    /**
     * Fetch all authors with their books
     */
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books")
    List<Author> findAllWithBooks();

    /**
     * Custom query using indexed column
     */
    @Query("SELECT a FROM Author a WHERE LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> searchByLastName(@Param("name") String name);
}
