package com.example.caching.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for Caffeine in-memory cache.
 *
 * Caffeine is a high-performance, near-optimal caching library for Java.
 * It provides an in-memory cache with a rich feature set.
 *
 * Use Caffeine when:
 * - You need high-performance local caching
 * - Your application runs on a single instance or doesn't require cache sharing
 * - You want automatic eviction policies (size-based, time-based)
 * - You need cache statistics and monitoring
 *
 * Advantages:
 * - Extremely fast (one of the fastest Java caching libraries)
 * - Low memory footprint
 * - Rich eviction policies
 * - Built-in statistics
 * - Thread-safe
 *
 * Limitations:
 * - Not suitable for distributed systems
 * - Cache is lost on application restart
 * - Each instance has its own cache (no sharing)
 */
@Configuration
public class CaffeineConfig {

    /**
     * Primary cache manager using Caffeine.
     * Configured with:
     * - Initial capacity of 100 entries
     * - Maximum size of 500 entries
     * - Expire after write: 10 minutes
     * - Expire after access: 5 minutes
     * - Record statistics for monitoring
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "products",
                "users",
                "productsByCategory"
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .recordStats()
        );

        return cacheManager;
    }

    /**
     * Cache manager with shorter TTL for frequently changing data.
     */
    @Bean("shortLivedCacheManager")
    public CacheManager shortLivedCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("shortLived");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .recordStats()
        );

        return cacheManager;
    }
}
