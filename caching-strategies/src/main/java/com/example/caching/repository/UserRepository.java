package com.example.caching.repository;

import com.example.caching.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulated repository for User entities.
 * Uses ConcurrentHashMap to simulate database storage.
 */
@Repository
public class UserRepository {

    private final Map<Long, User> database = new ConcurrentHashMap<>();
    private Long idCounter = 1L;

    public UserRepository() {
        initializeData();
    }

    private void initializeData() {
        // Initialize with sample data
        saveUser(User.builder()
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .lastLogin(LocalDateTime.now().minusDays(1))
                .active(true)
                .build());

        saveUser(User.builder()
                .username("jane.smith")
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .lastLogin(LocalDateTime.now().minusHours(3))
                .active(true)
                .build());

        saveUser(User.builder()
                .username("bob.wilson")
                .email("bob.wilson@example.com")
                .firstName("Bob")
                .lastName("Wilson")
                .lastLogin(LocalDateTime.now().minusWeeks(2))
                .active(false)
                .build());
    }

    public User findById(Long id) {
        simulateLatency();
        return database.get(id);
    }

    public Optional<User> findByUsername(String username) {
        simulateLatency();
        return database.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public List<User> findAll() {
        simulateLatency();
        return new ArrayList<>(database.values());
    }

    public User save(User user) {
        simulateLatency();
        return saveUser(user);
    }

    private User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        database.put(user.getId(), user);
        return user;
    }

    public void deleteById(Long id) {
        simulateLatency();
        database.remove(id);
    }

    /**
     * Simulates database query latency (500ms)
     */
    private void simulateLatency() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
