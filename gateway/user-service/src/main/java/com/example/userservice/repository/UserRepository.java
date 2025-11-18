package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 *
 * Spring Data JPA repository for User entity.
 * Provides CRUD operations and custom query methods.
 *
 * Learning Points:
 * - Spring Data JPA auto-implements common database operations
 * - Method names are parsed to generate queries (findByUsername)
 * - No need to write SQL for simple queries
 * - Returns Optional to handle null cases gracefully
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * Used for authentication
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if username already exists
     * Used for registration validation
     */
    boolean existsByUsername(String username);

    /**
     * Check if email already exists
     * Used for registration validation
     */
    boolean existsByEmail(String email);
}
