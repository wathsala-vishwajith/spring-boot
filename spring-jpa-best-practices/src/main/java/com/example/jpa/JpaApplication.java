package com.example.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application demonstrating JPA best practices:
 *
 * Key Features:
 * 1. Proper database indexing on entities
 * 2. N+1 query prevention using:
 *    - Named Entity Graphs
 *    - @EntityGraph annotations
 *    - JPQL JOIN FETCH queries
 * 3. HikariCP connection pooling with monitoring
 * 4. Transaction management best practices
 * 5. Flyway database migrations
 * 6. Optimistic locking for concurrent updates
 * 7. Hibernate statistics monitoring
 *
 * @author Spring Data JPA Best Practices Demo
 */
@SpringBootApplication
public class JpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
    }
}
