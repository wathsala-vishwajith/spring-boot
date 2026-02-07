package com.example.caching.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

/**
 * Configuration for Ehcache in-memory cache.
 *
 * Ehcache is a widely used open-source Java cache that can be used standalone
 * or integrated with popular frameworks.
 *
 * Use Ehcache when:
 * - You need a mature, enterprise-grade caching solution
 * - You want XML-based configuration
 * - You need tiered caching (on-heap, off-heap, disk)
 * - You want to persist cache to disk for restarts
 * - You need clustering capabilities (with Terracotta)
 *
 * Advantages:
 * - Mature and well-tested
 * - Supports disk persistence
 * - Can use off-heap memory
 * - JSR-107 (JCache) compliant
 * - Good documentation and community support
 *
 * Limitations:
 * - Slower than Caffeine for pure in-memory operations
 * - More complex configuration
 * - Requires separate clustering solution (Terracotta) for distribution
 */
@Configuration
public class EhcacheConfig {

    /**
     * Ehcache manager using JCache (JSR-107) specification.
     * Configuration is loaded from ehcache.xml in resources.
     */
    @Bean("ehcacheCacheManager")
    public CacheManager ehcacheCacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager(
                getClass().getResource("/ehcache.xml").toURI(),
                getClass().getClassLoader()
        );
        return new JCacheCacheManager(cacheManager);
    }
}
