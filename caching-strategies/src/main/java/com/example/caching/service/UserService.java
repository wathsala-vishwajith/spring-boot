package com.example.caching.service;

import com.example.caching.model.User;
import com.example.caching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service demonstrating distributed caching with Redis.
 *
 * This service uses the same caching annotations as ProductService,
 * but the cache manager is configured to use Redis.
 *
 * Key differences from in-memory caching:
 * - Cache is shared across all application instances
 * - Survives application restarts (if Redis persistence is enabled)
 * - Slower than in-memory (network latency)
 * - Requires serialization/deserialization
 *
 * To use Redis caching, specify cacheManager in annotations or
 * configure it as the default cache manager.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retrieves a user by ID using Redis cache.
     *
     * The cache is shared across all application instances.
     * If Instance A caches a user, Instance B can retrieve it from Redis.
     */
    @Cacheable(value = "redis:users", key = "#id", cacheManager = "redisCacheManager")
    public User getUserById(Long id) {
        log.info("Fetching user {} from database (Redis cache miss)", id);
        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by username using Redis cache.
     */
    @Cacheable(value = "redis:users", key = "'username:' + #username", cacheManager = "redisCacheManager")
    public User getUserByUsername(String username) {
        log.info("Fetching user by username '{}' from database (Redis cache miss)", username);
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Retrieves all users using Redis cache.
     */
    @Cacheable(value = "redis:users", key = "'all'", cacheManager = "redisCacheManager")
    public List<User> getAllUsers() {
        log.info("Fetching all users from database (Redis cache miss)");
        return userRepository.findAll();
    }

    /**
     * Creates a new user and invalidates the "all users" cache.
     */
    @CacheEvict(value = "redis:users", key = "'all'", cacheManager = "redisCacheManager")
    public User createUser(User user) {
        log.info("Creating new user and evicting 'all' users from Redis cache");
        return userRepository.save(user);
    }

    /**
     * Updates a user and refreshes the Redis cache.
     *
     * Note: We evict both the ID-based and username-based cache entries.
     */
    @CachePut(value = "redis:users", key = "#user.id", cacheManager = "redisCacheManager")
    @CacheEvict(value = "redis:users", key = "'username:' + #user.username", cacheManager = "redisCacheManager")
    public User updateUser(User user) {
        log.info("Updating user {} and refreshing Redis cache", user.getId());
        return userRepository.save(user);
    }

    /**
     * Deletes a user and removes it from Redis cache.
     */
    @CacheEvict(value = "redis:users", allEntries = true, cacheManager = "redisCacheManager")
    public void deleteUser(Long id) {
        log.info("Deleting user {} and evicting from Redis cache", id);
        userRepository.deleteById(id);
    }

    /**
     * Clears all user caches in Redis.
     */
    @CacheEvict(value = "redis:users", allEntries = true, cacheManager = "redisCacheManager")
    public void clearAllCaches() {
        log.info("Clearing all user caches in Redis");
    }
}
