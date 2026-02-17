package com.example.jwtdemo.repository;

import com.example.jwtdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     *
     * @param username username to search for
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if username exists
     *
     * @param username username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     *
     * @param email email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
