package com.example.performance.async;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Demonstrates Java 21 Virtual Threads for massive concurrency.
 *
 * Virtual Threads (Project Loom):
 * - Lightweight threads (1-2KB vs 1-2MB for platform threads)
 * - Can create millions of virtual threads
 * - Perfect for I/O-bound tasks
 * - Simplifies concurrent programming (no need for reactive programming)
 * - Managed by JVM, not OS
 *
 * When to use Virtual Threads:
 * - High concurrency I/O operations (database, network, file)
 * - Request handling in web servers
 * - Microservices communication
 * - Replacing reactive programming for simplicity
 *
 * When NOT to use Virtual Threads:
 * - CPU-bound tasks (use platform threads or ForkJoinPool)
 * - When using synchronized blocks extensively (can pin virtual threads)
 */
@Service
public class VirtualThreadsService {

    private final Executor virtualThreadExecutor;

    public VirtualThreadsService(@Qualifier("virtualThreadExecutor") Executor virtualThreadExecutor) {
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    /**
     * Demonstrates creating and running a simple virtual thread.
     */
    public String runSimpleVirtualThread() {
        System.out.println("Running simple virtual thread");

        try {
            Thread virtualThread = Thread.ofVirtual().start(() -> {
                System.out.println("Hello from virtual thread: " + Thread.currentThread());
                System.out.println("Is virtual: " + Thread.currentThread().isVirtual());
            });

            virtualThread.join();
            return "Virtual thread completed";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        }
    }

    /**
     * Demonstrates massive concurrency with virtual threads.
     * Creates thousands of virtual threads simultaneously.
     */
    public String demonstrateMassiveConcurrency(int threadCount) {
        System.out.println("Creating " + threadCount + " virtual threads");
        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                int taskId = i;
                Future<String> future = executor.submit(() -> {
                    // Simulate I/O operation
                    Thread.sleep(1000);
                    return "Task " + taskId + " completed on " + Thread.currentThread();
                });
                futures.add(future);
            }

            // Wait for all tasks to complete
            int completed = 0;
            for (Future<String> future : futures) {
                future.get();
                completed++;
            }

            Duration duration = Duration.between(start, Instant.now());
            String result = String.format("Completed %d virtual threads in %d ms",
                    completed, duration.toMillis());
            System.out.println(result);
            return result;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Compares performance between platform threads and virtual threads.
     */
    public String compareVirtualVsPlatformThreads(int threadCount) {
        System.out.println("\n=== Comparing Virtual Threads vs Platform Threads ===");

        // Test with platform threads
        Instant platformStart = Instant.now();
        try (ExecutorService platformExecutor = Executors.newFixedThreadPool(
                Math.min(threadCount, 100))) { // Limit platform threads

            List<Future<Void>> futures = IntStream.range(0, threadCount)
                    .mapToObj(i -> platformExecutor.submit(() -> {
                        Thread.sleep(100);
                        return null;
                    }))
                    .toList();

            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        Duration platformDuration = Duration.between(platformStart, Instant.now());

        // Test with virtual threads
        Instant virtualStart = Instant.now();
        try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Void>> futures = IntStream.range(0, threadCount)
                    .mapToObj(i -> virtualExecutor.submit(() -> {
                        Thread.sleep(100);
                        return null;
                    }))
                    .toList();

            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        Duration virtualDuration = Duration.between(virtualStart, Instant.now());

        String result = String.format(
                "Platform Threads (%d max): %d ms\nVirtual Threads: %d ms\nSpeedup: %.2fx",
                Math.min(threadCount, 100),
                platformDuration.toMillis(),
                virtualDuration.toMillis(),
                (double) platformDuration.toMillis() / virtualDuration.toMillis()
        );

        System.out.println(result);
        return result;
    }

    /**
     * Demonstrates I/O-bound operations with virtual threads.
     * Perfect use case for virtual threads.
     */
    public String simulateIoBoundOperations(int operationCount) {
        System.out.println("Simulating " + operationCount + " I/O-bound operations");
        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, operationCount)
                    .mapToObj(i -> executor.submit(() -> simulateApiCall(i)))
                    .toList();

            List<String> results = new ArrayList<>();
            for (Future<String> future : futures) {
                results.add(future.get());
            }

            Duration duration = Duration.between(start, Instant.now());
            String result = String.format("Completed %d I/O operations in %d ms",
                    results.size(), duration.toMillis());
            System.out.println(result);
            return result;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Demonstrates structured concurrency with virtual threads.
     * Structured concurrency ensures that all subtasks complete before the main task.
     */
    public String demonstrateStructuredConcurrency() {
        System.out.println("Demonstrating structured concurrency");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> user = executor.submit(() -> fetchUserData("user123"));
            Future<String> orders = executor.submit(() -> fetchOrders("user123"));
            Future<String> preferences = executor.submit(() -> fetchPreferences("user123"));

            // All tasks must complete within this scope
            String result = String.format("User: %s, Orders: %s, Preferences: %s",
                    user.get(), orders.get(), preferences.get());

            System.out.println(result);
            return result;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Async method using virtual threads executor.
     */
    @Async("virtualThreadExecutor")
    public CompletableFuture<String> asyncVirtualThreadOperation(String input) {
        System.out.println("Executing on virtual thread: " + Thread.currentThread());
        System.out.println("Is virtual: " + Thread.currentThread().isVirtual());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return CompletableFuture.completedFuture("Processed with virtual thread: " + input);
    }

    /**
     * Demonstrates handling thread pinning issues.
     * Virtual threads can be "pinned" to platform threads when using synchronized blocks.
     */
    public String demonstrateThreadPinning() {
        System.out.println("Demonstrating thread pinning with synchronized");

        // Bad: synchronized can pin virtual threads
        synchronized (this) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Good: use ReentrantLock instead
        java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock();
        lock.lock();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

        return "Use ReentrantLock instead of synchronized for virtual threads";
    }

    /**
     * Best practices for virtual threads.
     */
    public List<String> getVirtualThreadBestPractices() {
        return List.of(
                "Use virtual threads for I/O-bound tasks, not CPU-bound tasks",
                "Avoid synchronized blocks - use ReentrantLock instead",
                "Don't pool virtual threads - create them on demand",
                "Use try-with-resources with ExecutorService for automatic cleanup",
                "Monitor for thread pinning using -Djdk.tracePinnedThreads=full",
                "Virtual threads are cheap - create one per task",
                "Use ExecutorService.newVirtualThreadPerTaskExecutor() for task execution",
                "Combine with structured concurrency for better error handling",
                "Virtual threads are great for replacing reactive programming",
                "Use Thread.ofVirtual() for one-off virtual threads"
        );
    }

    // Helper methods

    private String simulateApiCall(int id) {
        try {
            Thread.sleep(100 + ThreadLocalRandom.current().nextInt(100));
            return "API call " + id + " completed";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        }
    }

    private String fetchUserData(String userId) throws InterruptedException {
        Thread.sleep(100);
        return "UserData for " + userId;
    }

    private String fetchOrders(String userId) throws InterruptedException {
        Thread.sleep(150);
        return "Orders for " + userId;
    }

    private String fetchPreferences(String userId) throws InterruptedException {
        Thread.sleep(80);
        return "Preferences for " + userId;
    }
}
