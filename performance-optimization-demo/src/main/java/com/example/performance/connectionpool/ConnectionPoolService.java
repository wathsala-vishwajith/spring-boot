package com.example.performance.connectionpool;

import com.example.performance.model.User;
import com.example.performance.repository.UserRepository;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Demonstrates HikariCP connection pool optimization.
 *
 * HikariCP is the fastest connection pool and is the default in Spring Boot.
 *
 * Key Configuration Parameters:
 * - maximumPoolSize: Maximum number of connections (formula: cores * 2 + effective_spindle_count)
 * - minimumIdle: Minimum idle connections in the pool
 * - connectionTimeout: Maximum time to wait for a connection from pool
 * - idleTimeout: Maximum time a connection can sit idle in pool
 * - maxLifetime: Maximum lifetime of a connection in the pool
 * - leakDetectionThreshold: Time before connection leak is suspected
 *
 * Monitoring:
 * - Use HikariCP MBeans for JMX monitoring
 * - Monitor active connections, idle connections, waiting threads
 * - Track connection acquisition time
 * - Watch for connection leaks
 */
@Service
public class ConnectionPoolService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get HikariCP pool statistics for monitoring.
     */
    public Map<String, Object> getPoolStatistics() {
        if (!(dataSource instanceof HikariDataSource)) {
            return Map.of("error", "DataSource is not HikariCP");
        }

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("poolName", hikariDataSource.getPoolName());
        stats.put("activeConnections", poolMXBean.getActiveConnections());
        stats.put("idleConnections", poolMXBean.getIdleConnections());
        stats.put("totalConnections", poolMXBean.getTotalConnections());
        stats.put("threadsAwaitingConnection", poolMXBean.getThreadsAwaitingConnection());

        // Configuration
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("maximumPoolSize", hikariDataSource.getMaximumPoolSize());
        config.put("minimumIdle", hikariDataSource.getMinimumIdle());
        config.put("connectionTimeout", hikariDataSource.getConnectionTimeout());
        config.put("idleTimeout", hikariDataSource.getIdleTimeout());
        config.put("maxLifetime", hikariDataSource.getMaxLifetime());
        config.put("leakDetectionThreshold", hikariDataSource.getLeakDetectionThreshold());

        stats.put("configuration", config);

        return stats;
    }

    /**
     * Demonstrates optimal pool sizing calculation.
     */
    public Map<String, Object> calculateOptimalPoolSize() {
        int cores = Runtime.getRuntime().availableProcessors();

        // Formula from HikariCP documentation
        // pool size = cores * 2 + effective_spindle_count
        // For SSDs, effective_spindle_count ≈ 1
        // For HDDs, it's the number of physical disks
        int effectiveSpindleCount = 1; // Assuming SSD

        int optimalPoolSize = cores * 2 + effectiveSpindleCount;

        Map<String, Object> calculation = new LinkedHashMap<>();
        calculation.put("availableCores", cores);
        calculation.put("effectiveSpindleCount", effectiveSpindleCount);
        calculation.put("recommendedPoolSize", optimalPoolSize);
        calculation.put("formula", "cores * 2 + effective_spindle_count");
        calculation.put("notes", "This is a starting point. Monitor and adjust based on actual workload.");

        return calculation;
    }

    /**
     * Demonstrates connection pool stress testing.
     * Helps identify optimal pool size for your workload.
     */
    public Map<String, Object> stressTestConnectionPool(int concurrentRequests) {
        System.out.println("Stress testing connection pool with " + concurrentRequests + " concurrent requests");

        long startTime = System.currentTimeMillis();
        Map<String, Object> results = new LinkedHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);

        try {
            List<CompletableFuture<Void>> futures = IntStream.range(0, concurrentRequests)
                    .mapToObj(i -> CompletableFuture.runAsync(() -> {
                        performDatabaseOperation(i);
                    }, executor))
                    .collect(Collectors.toList());

            // Wait for all operations to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            long duration = System.currentTimeMillis() - startTime;

            results.put("concurrentRequests", concurrentRequests);
            results.put("totalDurationMs", duration);
            results.put("avgRequestTimeMs", duration / (double) concurrentRequests);
            results.put("requestsPerSecond", (concurrentRequests * 1000.0) / duration);
            results.put("poolStatistics", getPoolStatistics());

        } finally {
            executor.shutdown();
        }

        return results;
    }

    /**
     * Demonstrates batch operations for better connection utilization.
     */
    @Transactional
    public void demonstrateBatchOperations(int recordCount) {
        System.out.println("Performing batch insert of " + recordCount + " records");
        long startTime = System.currentTimeMillis();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < recordCount; i++) {
            users.add(new User(
                    "user_" + i,
                    "user" + i + "@example.com",
                    "First" + i,
                    "Last" + i
            ));
        }

        // Batch save - more efficient than individual saves
        userRepository.saveAll(users);

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Batch insert completed in " + duration + "ms");
        System.out.println("Average time per record: " + (duration / (double) recordCount) + "ms");
    }

    /**
     * Demonstrates connection leak detection.
     */
    public String demonstrateConnectionLeakDetection() {
        if (!(dataSource instanceof HikariDataSource)) {
            return "DataSource is not HikariCP";
        }

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        long leakThreshold = hikariDataSource.getLeakDetectionThreshold();

        String info = String.format(
                "Connection Leak Detection Threshold: %d ms\n" +
                "If a connection is not returned to the pool within %d ms, " +
                "HikariCP will log a warning with a stack trace.\n" +
                "Monitor logs for 'Connection leak detection' messages.",
                leakThreshold, leakThreshold
        );

        return info;
    }

    /**
     * Get connection pool best practices.
     */
    public List<String> getConnectionPoolBestPractices() {
        return List.of(
                "Size pool based on formula: cores * 2 + effective_spindle_count",
                "Set connectionTimeout to prevent indefinite waiting (default: 30s)",
                "Set maxLifetime to less than database wait_timeout (default: 30min)",
                "Enable leakDetectionThreshold in development (2000ms is good)",
                "Use batch operations to reduce connection usage",
                "Always use try-with-resources or Spring's transaction management",
                "Monitor pool metrics via JMX or Actuator endpoints",
                "Don't create new pools - share a single pool across application",
                "Set minimumIdle = maximumPoolSize for stable performance (pre-allocated pool)",
                "Use connection validation to detect stale connections",
                "Enable JMX monitoring: spring.datasource.hikari.register-mbeans=true",
                "Test pool size under production-like load",
                "Watch for threads waiting for connections - indicates pool is too small",
                "More connections != better performance (can cause contention)",
                "For microservices, smaller pools work better (5-10 connections)"
        );
    }

    /**
     * Get monitoring queries for HikariCP.
     */
    public Map<String, String> getMonitoringQueries() {
        Map<String, String> queries = new LinkedHashMap<>();

        queries.put("JMX MBean", "com.zaxxer.hikari:type=Pool (HikariPool-1)");
        queries.put("Active Connections", "activeConnections attribute");
        queries.put("Idle Connections", "idleConnections attribute");
        queries.put("Total Connections", "totalConnections attribute");
        queries.put("Threads Waiting", "threadsAwaitingConnection attribute");

        queries.put("Actuator Endpoint", "/actuator/metrics/hikaricp.connections.active");
        queries.put("Prometheus Metrics", "hikaricp_connections_active, hikaricp_connections_idle, hikaricp_connections_pending");

        return queries;
    }

    /**
     * Demonstrates optimal vs suboptimal connection usage.
     */
    public Map<String, Object> compareConnectionUsagePatterns(int operations) {
        Map<String, Object> comparison = new LinkedHashMap<>();

        // Suboptimal: Many small transactions
        long suboptimalStart = System.currentTimeMillis();
        for (int i = 0; i < operations; i++) {
            performSmallTransaction(i);
        }
        long suboptimalDuration = System.currentTimeMillis() - suboptimalStart;

        // Optimal: Batch operations
        long optimalStart = System.currentTimeMillis();
        performBatchTransaction(operations);
        long optimalDuration = System.currentTimeMillis() - optimalStart;

        comparison.put("operations", operations);
        comparison.put("suboptimalDurationMs", suboptimalDuration);
        comparison.put("optimalDurationMs", optimalDuration);
        comparison.put("improvement", String.format("%.2fx faster", (double) suboptimalDuration / optimalDuration));

        return comparison;
    }

    // Helper methods

    private void performDatabaseOperation(int id) {
        User user = new User("stresstest_" + id, "test" + id + "@example.com", "Stress", "Test");
        userRepository.save(user);
    }

    @Transactional
    private void performSmallTransaction(int id) {
        User user = new User("small_" + id, "small" + id + "@example.com", "Small", "Tx");
        userRepository.save(user);
    }

    @Transactional
    private void performBatchTransaction(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(new User("batch_" + i, "batch" + i + "@example.com", "Batch", "Tx"));
        }
        userRepository.saveAll(users);
    }
}
