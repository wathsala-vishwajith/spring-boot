package com.example.performance.memoryleak;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates common memory leak patterns and how to detect them.
 *
 * Memory leaks in Java typically occur when:
 * 1. Objects are stored in static collections and never removed
 * 2. Listeners/callbacks are registered but never unregistered
 * 3. ThreadLocal variables are not cleaned up
 * 4. Unclosed resources (streams, connections)
 * 5. Custom cache implementations that grow unbounded
 *
 * Detection Tools:
 * - VisualVM: Monitor heap usage, take heap dumps, analyze retained objects
 * - JProfiler: Memory leak analysis, allocation hot spots
 * - Eclipse MAT (Memory Analyzer Tool): Heap dump analysis
 * - Java Flight Recorder: Continuous monitoring
 *
 * Symptoms:
 * - Increasing heap usage over time
 * - Frequent GC cycles
 * - OutOfMemoryError
 */
@Service
public class MemoryLeakDemonstration {

    // Example 1: Static collection memory leak
    private static final List<Object> STATIC_COLLECTION = new ArrayList<>();

    // Example 2: Cache without eviction policy
    private static final Map<String, Object> UNBOUNDED_CACHE = new ConcurrentHashMap<>();

    // Example 3: ThreadLocal not cleaned up
    private static final ThreadLocal<List<byte[]>> THREAD_LOCAL_CACHE = ThreadLocal.withInitial(ArrayList::new);

    // Example 4: Event listeners not removed
    private final List<EventListener> listeners = new ArrayList<>();

    /**
     * Demonstrates a static collection memory leak.
     * Objects added to this collection are never garbage collected.
     */
    public void demonstrateStaticCollectionLeak(int objectCount) {
        System.out.println("Creating " + objectCount + " objects in static collection (MEMORY LEAK)");

        for (int i = 0; i < objectCount; i++) {
            // These objects will never be garbage collected
            STATIC_COLLECTION.add(new byte[1024 * 10]); // 10KB per object
        }

        System.out.println("Static collection size: " + STATIC_COLLECTION.size());
        System.out.println("Memory used by static collection: " +
                (STATIC_COLLECTION.size() * 10) + "KB");
    }

    /**
     * Fix for static collection leak: provide a cleanup method.
     */
    public void cleanupStaticCollection() {
        System.out.println("Cleaning up static collection");
        STATIC_COLLECTION.clear();
    }

    /**
     * Demonstrates an unbounded cache memory leak.
     */
    public void demonstrateUnboundedCacheLeak(int entries) {
        System.out.println("Adding " + entries + " entries to unbounded cache (MEMORY LEAK)");

        for (int i = 0; i < entries; i++) {
            UNBOUNDED_CACHE.put("key_" + i, new byte[1024 * 10]); // 10KB per entry
        }

        System.out.println("Cache size: " + UNBOUNDED_CACHE.size());
    }

    /**
     * Fixed version using bounded cache with LRU eviction.
     */
    public Map<String, Object> createBoundedCache(int maxSize) {
        return new LinkedHashMap<String, Object>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() > maxSize;
            }
        };
    }

    /**
     * Demonstrates ThreadLocal memory leak.
     * ThreadLocal variables persist for the lifetime of the thread.
     */
    public void demonstrateThreadLocalLeak(int dataSize) {
        System.out.println("Adding data to ThreadLocal (potential MEMORY LEAK)");

        List<byte[]> threadData = THREAD_LOCAL_CACHE.get();
        for (int i = 0; i < dataSize; i++) {
            threadData.add(new byte[1024]); // 1KB per item
        }

        System.out.println("ThreadLocal data size: " + threadData.size() + " items");
    }

    /**
     * Fix for ThreadLocal leak: always clean up.
     */
    public void cleanupThreadLocal() {
        System.out.println("Cleaning up ThreadLocal");
        THREAD_LOCAL_CACHE.remove();
    }

    /**
     * Demonstrates listener/callback memory leak.
     */
    public void addListener(EventListener listener) {
        listeners.add(listener);
        System.out.println("Listener added. Total listeners: " + listeners.size());
    }

    /**
     * Fix: provide method to remove listeners.
     */
    public void removeListener(EventListener listener) {
        listeners.remove(listener);
        System.out.println("Listener removed. Total listeners: " + listeners.size());
    }

    /**
     * Demonstrates proper resource management (no leak).
     */
    public void demonstrateProperResourceManagement() {
        // Using try-with-resources ensures proper cleanup
        System.out.println("Demonstrating proper resource management with try-with-resources");

        // This is the correct way - resources are automatically closed
        try (Scanner scanner = new Scanner("test data")) {
            while (scanner.hasNext()) {
                scanner.next();
            }
        }
        // Scanner is automatically closed here
    }

    /**
     * Get memory leak detection tips.
     */
    public Map<String, String> getMemoryLeakDetectionTips() {
        Map<String, String> tips = new LinkedHashMap<>();

        tips.put("VisualVM", "Monitor heap usage over time. Look for increasing trend. " +
                "Take heap dumps and analyze dominator tree.");

        tips.put("JProfiler", "Use Memory Leak Analysis view. Track object allocation hot spots. " +
                "Monitor live objects and their references.");

        tips.put("Heap Dump Analysis", "Take heap dumps at different times. Compare them to find " +
                "objects that are growing. Look for unexpected collections or caches.");

        tips.put("Telemetry", "Monitor GC frequency and duration. Increasing GC activity with " +
                "growing heap indicates a leak.");

        tips.put("Prevention", "Use try-with-resources. Clean up listeners. Bound all caches. " +
                "Clean ThreadLocal variables. Avoid static collections.");

        tips.put("Java Flight Recorder", "Enable JFR for continuous monitoring: " +
                "-XX:StartFlightRecording=duration=60s,filename=/tmp/recording.jfr");

        return tips;
    }

    /**
     * Simulate a realistic memory leak scenario.
     */
    public void simulateRealisticMemoryLeak(int sessionCount) {
        System.out.println("Simulating realistic memory leak: user sessions not cleaned up");

        // Simulating user sessions that are never cleaned up
        for (int i = 0; i < sessionCount; i++) {
            UserSession session = new UserSession("user_" + i);
            session.addData(new byte[1024 * 50]); // 50KB per session
            STATIC_COLLECTION.add(session);
        }

        System.out.println("Created " + sessionCount + " user sessions that are never cleaned up");
    }

    /**
     * Inner class representing a user session.
     */
    private static class UserSession {
        private final String userId;
        private final List<Object> sessionData = new ArrayList<>();
        private final long createdAt;

        public UserSession(String userId) {
            this.userId = userId;
            this.createdAt = System.currentTimeMillis();
        }

        public void addData(Object data) {
            sessionData.add(data);
        }
    }

    /**
     * Interface for event listeners.
     */
    public interface EventListener {
        void onEvent(String event);
    }
}
