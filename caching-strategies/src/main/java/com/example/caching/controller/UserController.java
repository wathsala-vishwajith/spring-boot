package com.example.caching.controller;

import com.example.caching.model.User;
import com.example.caching.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller demonstrating distributed caching with Redis.
 *
 * Test the caching behavior:
 * 1. Call GET /api/users/{id} twice - first call is slow, second is fast
 * 2. If you have multiple instances, the cache is shared
 * 3. Restart the application - cache persists in Redis (if persistence enabled)
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get user by ID using Redis cache.
     *
     * First call: ~500ms (database query)
     * Subsequent calls: ~10-50ms (from Redis - network latency)
     *
     * Note: Slower than in-memory cache due to network overhead,
     * but shared across all application instances.
     *
     * Example: GET http://localhost:8080/api/users/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        User user = userService.getUserById(id);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Request took " + duration + "ms (Redis cache)");

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by username using Redis cache.
     *
     * Example: GET http://localhost:8080/api/users/username/john.doe
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        long startTime = System.currentTimeMillis();
        User user = userService.getUserByUsername(username);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Request took " + duration + "ms (Redis cache)");

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users using Redis cache.
     *
     * Example: GET http://localhost:8080/api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        long startTime = System.currentTimeMillis();
        List<User> users = userService.getAllUsers();
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Request took " + duration + "ms (Redis cache)");

        return ResponseEntity.ok(users);
    }

    /**
     * Create a new user.
     *
     * This invalidates the "all users" cache in Redis.
     *
     * Example: POST http://localhost:8080/api/users
     * Body: {"username": "newuser", "email": "new@example.com", "firstName": "New", "lastName": "User"}
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update a user.
     *
     * This updates the Redis cache using @CachePut.
     *
     * Example: PUT http://localhost:8080/api/users/1
     * Body: {"id": 1, "username": "john.doe", "email": "updated@example.com"}
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a user.
     *
     * This removes the user from Redis cache.
     *
     * Example: DELETE http://localhost:8080/api/users/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clear all user caches in Redis.
     *
     * Example: POST http://localhost:8080/api/users/cache/clear
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        userService.clearAllCaches();
        return ResponseEntity.ok("All user caches cleared from Redis");
    }
}
