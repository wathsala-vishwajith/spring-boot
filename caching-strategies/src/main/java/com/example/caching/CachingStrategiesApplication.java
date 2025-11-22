package com.example.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for demonstrating various caching strategies in Spring Boot.
 *
 * This application showcases:
 * - In-memory caching (Caffeine, Ehcache)
 * - Distributed caching (Redis)
 * - Cache invalidation patterns
 * - Best practices for each approach
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class CachingStrategiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CachingStrategiesApplication.class, args);
    }
}
