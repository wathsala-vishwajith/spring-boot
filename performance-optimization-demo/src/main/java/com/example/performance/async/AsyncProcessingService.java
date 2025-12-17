package com.example.performance.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Demonstrates asynchronous processing patterns using CompletableFuture.
 *
 * CompletableFuture Benefits:
 * - Non-blocking operations
 * - Composable asynchronous pipelines
 * - Exception handling in async context
 * - Combining multiple async operations
 *
 * Use Cases:
 * - API calls to multiple services
 * - Database queries that can run in parallel
 * - File I/O operations
 * - Email sending, notifications
 */
@Service
public class AsyncProcessingService {

    /**
     * Simple async method using Spring's @Async annotation.
     * Runs in a separate thread from the configured thread pool.
     */
    @Async("taskExecutor")
    public CompletableFuture<String> asyncOperation(String input) {
        System.out.println("Executing async operation on thread: " + Thread.currentThread().getName());

        try {
            // Simulate some processing
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture("Processed: " + input);
    }

    /**
     * Demonstrates parallel processing of multiple tasks using CompletableFuture.
     */
    public CompletableFuture<List<String>> processMultipleTasksInParallel(List<String> inputs) {
        System.out.println("Processing " + inputs.size() + " tasks in parallel");

        List<CompletableFuture<String>> futures = inputs.stream()
                .map(this::asyncOperation)
                .collect(Collectors.toList());

        // Wait for all futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // When all complete, collect results
        return allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Demonstrates chaining async operations.
     */
    public CompletableFuture<String> chainedAsyncOperations(String input) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Step 1: Fetching data on " + Thread.currentThread().getName());
            return "Data for " + input;
        }).thenApplyAsync(data -> {
            System.out.println("Step 2: Processing data on " + Thread.currentThread().getName());
            return data.toUpperCase();
        }).thenApplyAsync(processed -> {
            System.out.println("Step 3: Enriching data on " + Thread.currentThread().getName());
            return processed + " - ENRICHED";
        }).exceptionally(ex -> {
            System.err.println("Error in async chain: " + ex.getMessage());
            return "ERROR";
        });
    }

    /**
     * Demonstrates combining multiple async operations.
     */
    public CompletableFuture<String> combineAsyncOperations(String userId) {
        CompletableFuture<String> userDataFuture = fetchUserData(userId);
        CompletableFuture<String> orderDataFuture = fetchOrderData(userId);
        CompletableFuture<String> preferenceFuture = fetchUserPreferences(userId);

        // Combine all three results
        return userDataFuture.thenCombine(orderDataFuture, (user, orders) -> user + ", " + orders)
                .thenCombine(preferenceFuture, (combined, prefs) -> combined + ", " + prefs);
    }

    /**
     * Demonstrates racing multiple async operations (returns first completed).
     */
    public CompletableFuture<String> raceAsyncOperations() {
        CompletableFuture<String> service1 = fetchFromService1();
        CompletableFuture<String> service2 = fetchFromService2();
        CompletableFuture<String> service3 = fetchFromService3();

        // Returns the result of whichever completes first
        return CompletableFuture.anyOf(service1, service2, service3)
                .thenApply(result -> (String) result);
    }

    /**
     * Demonstrates async processing with timeout handling.
     */
    public CompletableFuture<String> asyncWithTimeout(String input) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate long-running operation
                Thread.sleep(5000);
                return "Completed: " + input;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted", e);
            }
        }).orTimeout(3, java.util.concurrent.TimeUnit.SECONDS)
          .exceptionally(ex -> "Timeout or error: " + ex.getMessage());
    }

    /**
     * Demonstrates batch processing with async operations.
     */
    public CompletableFuture<List<String>> processBatchAsync(List<String> items, int batchSize) {
        System.out.println("Processing " + items.size() + " items in batches of " + batchSize);

        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < items.size(); i += batchSize) {
            int end = Math.min(i + batchSize, items.size());
            List<String> batch = items.subList(i, end);

            CompletableFuture<String> batchFuture = processBatch(batch);
            futures.add(batchFuture);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /**
     * Demonstrates CPU-bound async processing.
     * For CPU-bound tasks, limit parallelism to available processors.
     */
    public CompletableFuture<Long> cpuBoundAsyncProcessing(int iterations) {
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU-bound processing with " + processors + " processors");

        return CompletableFuture.supplyAsync(() -> {
            long result = 0;
            for (int i = 0; i < iterations; i++) {
                result += fibonacci(35);
            }
            return result;
        });
    }

    /**
     * Demonstrates I/O-bound async processing.
     * For I/O-bound tasks, can use higher parallelism.
     */
    public CompletableFuture<List<String>> ioBoundAsyncProcessing(List<String> urls) {
        System.out.println("I/O-bound processing for " + urls.size() + " URLs");

        // For I/O operations, we can have more threads than CPU cores
        ExecutorService ioExecutor = Executors.newFixedThreadPool(20);

        List<CompletableFuture<String>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> simulateHttpCall(url), ioExecutor))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .whenComplete((result, ex) -> ioExecutor.shutdown());
    }

    // Helper methods for demonstrations

    private CompletableFuture<String> fetchUserData(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(100);
            return "User:" + userId;
        });
    }

    private CompletableFuture<String> fetchOrderData(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(150);
            return "Orders:10";
        });
    }

    private CompletableFuture<String> fetchUserPreferences(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(80);
            return "Prefs:enabled";
        });
    }

    private CompletableFuture<String> fetchFromService1() {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(200);
            return "Service1 response";
        });
    }

    private CompletableFuture<String> fetchFromService2() {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(150);
            return "Service2 response";
        });
    }

    private CompletableFuture<String> fetchFromService3() {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(300);
            return "Service3 response";
        });
    }

    private CompletableFuture<String> processBatch(List<String> batch) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(500);
            return "Processed batch of " + batch.size() + " items";
        });
    }

    private String simulateHttpCall(String url) {
        simulateDelay(200);
        return "Response from " + url;
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
