package com.example.caching.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Service demonstrating various cache invalidation patterns.
 *
 * Cache invalidation is one of the hardest problems in computer science.
 * This service shows different strategies:
 *
 * 1. Time-based invalidation (TTL)
 * 2. Event-based invalidation
 * 3. Manual invalidation
 * 4. Scheduled cache warming
 * 5. Conditional eviction
 * 6. Write-through cache invalidation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationService {

    private final CacheManager caffeineCacheManager;

    /**
     * Pattern 1: Time-based invalidation (TTL).
     *
     * Caches automatically expire after a configured time.
     * - Configured in CaffeineConfig (expireAfterWrite, expireAfterAccess)
     * - Configured in RedisConfig (entryTtl)
     *
     * Use when:
     * - Data has a predictable freshness window
     * - You can tolerate slightly stale data
     * - You want simple, automatic invalidation
     */
    public void demonstrateTTLInvalidation() {
        log.info("TTL invalidation is configured in cache managers");
        log.info("Caffeine: 10 min write, 5 min access");
        log.info("Redis: 10-30 min depending on cache");
    }

    /**
     * Pattern 2: Event-based invalidation.
     *
     * Cache is invalidated when specific events occur (create, update, delete).
     * Uses @CacheEvict on write operations.
     *
     * Use when:
     * - You need immediate consistency
     * - Changes are infrequent
     * - You can identify all write operations
     */
    @CacheEvict(value = {"products", "users"}, allEntries = true)
    public void invalidateOnEvent(String event) {
        log.info("Event-based invalidation triggered by event: {}", event);
    }

    /**
     * Pattern 3: Manual invalidation.
     *
     * Programmatically clear caches using CacheManager.
     *
     * Use when:
     * - You need fine-grained control
     * - Invalidation logic is complex
     * - You want to invalidate based on runtime conditions
     */
    public void manualInvalidation(String cacheName, String key) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            if (key != null) {
                cache.evict(key);
                log.info("Manually evicted key '{}' from cache '{}'", key, cacheName);
            } else {
                cache.clear();
                log.info("Manually cleared all entries from cache '{}'", cacheName);
            }
        }
    }

    /**
     * Pattern 4: Scheduled cache warming.
     *
     * Proactively refresh cache before it expires.
     * Prevents cache stampede (thundering herd problem).
     *
     * Use when:
     * - Cache misses are expensive
     * - You can predict which data will be accessed
     * - You want consistent response times
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void scheduledCacheWarming() {
        log.info("Scheduled cache warming started");
        // In a real scenario, you would:
        // 1. Fetch frequently accessed data
        // 2. Populate caches proactively
        // 3. Do this during off-peak hours
    }

    /**
     * Pattern 5: Conditional eviction.
     *
     * Evict cache only if certain conditions are met.
     *
     * Use when:
     * - Invalidation depends on business logic
     * - You want to avoid unnecessary cache evictions
     */
    @CacheEvict(value = "products", key = "#id", condition = "#invalidate == true")
    public void conditionalEviction(Long id, boolean invalidate) {
        log.info("Conditional eviction for product {} (invalidate: {})", id, invalidate);
    }

    /**
     * Pattern 6: Write-through cache invalidation.
     *
     * Update both database and cache in the same operation.
     * Uses @CachePut to update cache without evicting.
     *
     * Use when:
     * - You want cache to stay fresh
     * - Write operations are not too frequent
     * - You can afford the overhead of updating cache
     */
    public void demonstrateWriteThrough() {
        log.info("Write-through uses @CachePut to update cache on write");
        log.info("See ProductService.updateProduct() for example");
    }

    /**
     * Gets cache statistics for monitoring and debugging.
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();

        Collection<String> cacheNames = caffeineCacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = caffeineCacheManager.getCache(cacheName);
            if (cache != null) {
                Object nativeCache = cache.getNativeCache();
                stats.put(cacheName, nativeCache.toString());
            }
        }

        return stats;
    }

    /**
     * Clears all caches across all cache managers.
     *
     * Use with caution in production!
     */
    public void clearAllCaches() {
        log.warn("Clearing ALL caches - this will impact performance!");

        caffeineCacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = caffeineCacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cleared cache: {}", cacheName);
            }
        });
    }
}
