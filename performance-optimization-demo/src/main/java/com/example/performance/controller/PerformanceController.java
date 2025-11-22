package com.example.performance.controller;

import com.example.performance.async.AsyncProcessingService;
import com.example.performance.async.VirtualThreadsService;
import com.example.performance.connectionpool.ConnectionPoolService;
import com.example.performance.memoryleak.MemoryLeakDemonstration;
import com.example.performance.profiling.ProfilingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller exposing all performance optimization demonstrations.
 *
 * Endpoints are organized by category:
 * - /api/profiling - JVM profiling and monitoring
 * - /api/memory - Memory leak detection
 * - /api/async - CompletableFuture examples
 * - /api/virtual-threads - Virtual threads examples
 * - /api/connection-pool - Connection pool optimization
 */
@RestController
@RequestMapping("/api")
public class PerformanceController {

    @Autowired
    private ProfilingService profilingService;

    @Autowired
    private MemoryLeakDemonstration memoryLeakDemonstration;

    @Autowired
    private AsyncProcessingService asyncProcessingService;

    @Autowired
    private VirtualThreadsService virtualThreadsService;

    @Autowired
    private ConnectionPoolService connectionPoolService;

    // ========== Profiling Endpoints ==========

    @GetMapping("/profiling/info")
    public ResponseEntity<Map<String, Object>> getProfilingInfo() {
        return ResponseEntity.ok(profilingService.getProfilingInfo());
    }

    @GetMapping("/profiling/memory")
    public ResponseEntity<Map<String, Object>> getMemoryInfo() {
        return ResponseEntity.ok(profilingService.getMemoryInfo());
    }

    @GetMapping("/profiling/threads")
    public ResponseEntity<Map<String, Object>> getThreadInfo() {
        return ResponseEntity.ok(profilingService.getThreadInfo());
    }

    @GetMapping("/profiling/gc")
    public ResponseEntity<Map<String, Object>> getGCInfo() {
        return ResponseEntity.ok(profilingService.getGCInfo());
    }

    @PostMapping("/profiling/cpu-test")
    public ResponseEntity<String> runCpuTest(@RequestParam(defaultValue = "10") int iterations) {
        long result = profilingService.cpuIntensiveOperation(iterations);
        return ResponseEntity.ok("CPU test completed. Result: " + result);
    }

    @PostMapping("/profiling/memory-test")
    public ResponseEntity<String> runMemoryTest(@RequestParam(defaultValue = "1000") int objectCount) {
        profilingService.memoryAllocationOperation(objectCount);
        return ResponseEntity.ok("Memory allocation test completed");
    }

    // ========== Memory Leak Endpoints ==========

    @GetMapping("/memory/leak-detection-tips")
    public ResponseEntity<Map<String, String>> getMemoryLeakDetectionTips() {
        return ResponseEntity.ok(memoryLeakDemonstration.getMemoryLeakDetectionTips());
    }

    @PostMapping("/memory/demonstrate-static-leak")
    public ResponseEntity<String> demonstrateStaticLeak(@RequestParam(defaultValue = "100") int objectCount) {
        memoryLeakDemonstration.demonstrateStaticCollectionLeak(objectCount);
        return ResponseEntity.ok("Static collection leak demonstrated. Check memory usage in profiler.");
    }

    @PostMapping("/memory/cleanup-static")
    public ResponseEntity<String> cleanupStatic() {
        memoryLeakDemonstration.cleanupStaticCollection();
        return ResponseEntity.ok("Static collection cleaned up");
    }

    @PostMapping("/memory/demonstrate-cache-leak")
    public ResponseEntity<String> demonstrateCacheLeak(@RequestParam(defaultValue = "100") int entries) {
        memoryLeakDemonstration.demonstrateUnboundedCacheLeak(entries);
        return ResponseEntity.ok("Unbounded cache leak demonstrated");
    }

    @PostMapping("/memory/demonstrate-threadlocal-leak")
    public ResponseEntity<String> demonstrateThreadLocalLeak(@RequestParam(defaultValue = "100") int dataSize) {
        memoryLeakDemonstration.demonstrateThreadLocalLeak(dataSize);
        return ResponseEntity.ok("ThreadLocal leak demonstrated");
    }

    @PostMapping("/memory/cleanup-threadlocal")
    public ResponseEntity<String> cleanupThreadLocal() {
        memoryLeakDemonstration.cleanupThreadLocal();
        return ResponseEntity.ok("ThreadLocal cleaned up");
    }

    // ========== Async Processing Endpoints ==========

    @GetMapping("/async/simple")
    public CompletableFuture<String> simpleAsync(@RequestParam String input) {
        return asyncProcessingService.asyncOperation(input);
    }

    @PostMapping("/async/parallel")
    public CompletableFuture<List<String>> parallelProcessing(@RequestBody List<String> inputs) {
        return asyncProcessingService.processMultipleTasksInParallel(inputs);
    }

    @GetMapping("/async/chained")
    public CompletableFuture<String> chainedAsync(@RequestParam String input) {
        return asyncProcessingService.chainedAsyncOperations(input);
    }

    @GetMapping("/async/combined")
    public CompletableFuture<String> combinedAsync(@RequestParam String userId) {
        return asyncProcessingService.combineAsyncOperations(userId);
    }

    @GetMapping("/async/race")
    public CompletableFuture<String> raceAsync() {
        return asyncProcessingService.raceAsyncOperations();
    }

    @GetMapping("/async/timeout")
    public CompletableFuture<String> asyncWithTimeout(@RequestParam String input) {
        return asyncProcessingService.asyncWithTimeout(input);
    }

    @PostMapping("/async/batch")
    public CompletableFuture<List<String>> batchAsync(
            @RequestBody List<String> items,
            @RequestParam(defaultValue = "10") int batchSize) {
        return asyncProcessingService.processBatchAsync(items, batchSize);
    }

    @GetMapping("/async/cpu-bound")
    public CompletableFuture<Long> cpuBoundAsync(@RequestParam(defaultValue = "10") int iterations) {
        return asyncProcessingService.cpuBoundAsyncProcessing(iterations);
    }

    // ========== Virtual Threads Endpoints ==========

    @GetMapping("/virtual-threads/simple")
    public ResponseEntity<String> simpleVirtualThread() {
        String result = virtualThreadsService.runSimpleVirtualThread();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/virtual-threads/massive-concurrency")
    public ResponseEntity<String> massiveConcurrency(
            @RequestParam(defaultValue = "10000") int threadCount) {
        String result = virtualThreadsService.demonstrateMassiveConcurrency(threadCount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/virtual-threads/compare")
    public ResponseEntity<String> compareThreadTypes(
            @RequestParam(defaultValue = "1000") int threadCount) {
        String result = virtualThreadsService.compareVirtualVsPlatformThreads(threadCount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/virtual-threads/io-bound")
    public ResponseEntity<String> ioBoundOperations(
            @RequestParam(defaultValue = "100") int operationCount) {
        String result = virtualThreadsService.simulateIoBoundOperations(operationCount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/virtual-threads/structured-concurrency")
    public ResponseEntity<String> structuredConcurrency() {
        String result = virtualThreadsService.demonstrateStructuredConcurrency();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/virtual-threads/async")
    public CompletableFuture<String> asyncVirtualThread(@RequestParam String input) {
        return virtualThreadsService.asyncVirtualThreadOperation(input);
    }

    @GetMapping("/virtual-threads/best-practices")
    public ResponseEntity<List<String>> virtualThreadBestPractices() {
        return ResponseEntity.ok(virtualThreadsService.getVirtualThreadBestPractices());
    }

    // ========== Connection Pool Endpoints ==========

    @GetMapping("/connection-pool/statistics")
    public ResponseEntity<Map<String, Object>> getPoolStatistics() {
        return ResponseEntity.ok(connectionPoolService.getPoolStatistics());
    }

    @GetMapping("/connection-pool/optimal-size")
    public ResponseEntity<Map<String, Object>> calculateOptimalPoolSize() {
        return ResponseEntity.ok(connectionPoolService.calculateOptimalPoolSize());
    }

    @PostMapping("/connection-pool/stress-test")
    public ResponseEntity<Map<String, Object>> stressTestPool(
            @RequestParam(defaultValue = "50") int concurrentRequests) {
        return ResponseEntity.ok(connectionPoolService.stressTestConnectionPool(concurrentRequests));
    }

    @PostMapping("/connection-pool/batch-demo")
    public ResponseEntity<String> demonstrateBatch(
            @RequestParam(defaultValue = "100") int recordCount) {
        connectionPoolService.demonstrateBatchOperations(recordCount);
        return ResponseEntity.ok("Batch operation completed for " + recordCount + " records");
    }

    @GetMapping("/connection-pool/leak-detection")
    public ResponseEntity<String> leakDetectionInfo() {
        return ResponseEntity.ok(connectionPoolService.demonstrateConnectionLeakDetection());
    }

    @GetMapping("/connection-pool/best-practices")
    public ResponseEntity<List<String>> connectionPoolBestPractices() {
        return ResponseEntity.ok(connectionPoolService.getConnectionPoolBestPractices());
    }

    @GetMapping("/connection-pool/monitoring")
    public ResponseEntity<Map<String, String>> getMonitoringInfo() {
        return ResponseEntity.ok(connectionPoolService.getMonitoringQueries());
    }

    @PostMapping("/connection-pool/compare-patterns")
    public ResponseEntity<Map<String, Object>> compareConnectionPatterns(
            @RequestParam(defaultValue = "100") int operations) {
        return ResponseEntity.ok(connectionPoolService.compareConnectionUsagePatterns(operations));
    }

    // ========== General Information ==========

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "application", "Performance Optimization Demo",
                "version", "1.0.0",
                "javaVersion", System.getProperty("java.version"),
                "availableEndpoints", Map.of(
                        "profiling", "/api/profiling/*",
                        "memory", "/api/memory/*",
                        "async", "/api/async/*",
                        "virtualThreads", "/api/virtual-threads/*",
                        "connectionPool", "/api/connection-pool/*"
                ),
                "monitoring", Map.of(
                        "actuator", "/actuator",
                        "health", "/actuator/health",
                        "metrics", "/actuator/metrics",
                        "prometheus", "/actuator/prometheus"
                )
        ));
    }
}
