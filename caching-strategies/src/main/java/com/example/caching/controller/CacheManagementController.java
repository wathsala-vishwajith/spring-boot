package com.example.caching.controller;

import com.example.caching.service.CacheInvalidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for cache management and monitoring.
 *
 * Demonstrates various cache invalidation patterns and provides
 * cache statistics for monitoring.
 */
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheManagementController {

    private final CacheInvalidationService cacheInvalidationService;

    /**
     * Get cache statistics.
     *
     * Returns information about cache size, hit rates, etc.
     *
     * Example: GET http://localhost:8080/api/cache/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = cacheInvalidationService.getCacheStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Manually invalidate a specific cache entry.
     *
     * Example: DELETE http://localhost:8080/api/cache/products/1
     */
    @DeleteMapping("/{cacheName}/{key}")
    public ResponseEntity<String> invalidateCache(
            @PathVariable String cacheName,
            @PathVariable String key) {
        cacheInvalidationService.manualInvalidation(cacheName, key);
        return ResponseEntity.ok("Cache entry invalidated: " + cacheName + "/" + key);
    }

    /**
     * Clear all entries from a specific cache.
     *
     * Example: DELETE http://localhost:8080/api/cache/products
     */
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        cacheInvalidationService.manualInvalidation(cacheName, null);
        return ResponseEntity.ok("Cache cleared: " + cacheName);
    }

    /**
     * Clear ALL caches.
     *
     * Use with caution in production!
     *
     * Example: DELETE http://localhost:8080/api/cache/all
     */
    @DeleteMapping("/all")
    public ResponseEntity<String> clearAllCaches() {
        cacheInvalidationService.clearAllCaches();
        return ResponseEntity.ok("All caches cleared");
    }

    /**
     * Trigger event-based invalidation.
     *
     * Example: POST http://localhost:8080/api/cache/invalidate
     * Body: "data_updated"
     */
    @PostMapping("/invalidate")
    public ResponseEntity<String> triggerInvalidation(@RequestBody String event) {
        cacheInvalidationService.invalidateOnEvent(event);
        return ResponseEntity.ok("Event-based invalidation triggered: " + event);
    }

    /**
     * Demonstrate conditional eviction.
     *
     * Example: POST http://localhost:8080/api/cache/conditional/1?invalidate=true
     */
    @PostMapping("/conditional/{id}")
    public ResponseEntity<String> conditionalEviction(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean invalidate) {
        cacheInvalidationService.conditionalEviction(id, invalidate);
        return ResponseEntity.ok("Conditional eviction executed for ID: " + id);
    }

    /**
     * Get information about cache invalidation patterns.
     *
     * Example: GET http://localhost:8080/api/cache/patterns
     */
    @GetMapping("/patterns")
    public ResponseEntity<Map<String, String>> getCachePatterns() {
        Map<String, String> patterns = Map.of(
            "TTL", "Time-based automatic expiration (configured in cache managers)",
            "Event-based", "Invalidate on create/update/delete operations",
            "Manual", "Programmatic invalidation using CacheManager",
            "Scheduled", "Proactive cache warming at scheduled intervals",
            "Conditional", "Invalidate only if certain conditions are met",
            "Write-through", "Update cache on write operations using @CachePut"
        );
        return ResponseEntity.ok(patterns);
    }
}
