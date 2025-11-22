package com.example.jpa.repository;

import com.example.jpa.domain.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Publisher repository
 */
@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    /**
     * Find by name - uses index
     */
    List<Publisher> findByName(String name);

    /**
     * Fetch publisher with books
     */
    @Query("SELECT DISTINCT p FROM Publisher p LEFT JOIN FETCH p.books WHERE p.id = :id")
    Optional<Publisher> findByIdWithBooks(@Param("id") Long id);

    /**
     * Find publishers by country - uses index
     */
    List<Publisher> findByCountry(String country);
}
