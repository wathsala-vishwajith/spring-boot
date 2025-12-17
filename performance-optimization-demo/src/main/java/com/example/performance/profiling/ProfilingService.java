package com.example.performance.profiling;

import org.springframework.stereotype.Service;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Service demonstrating profiling and monitoring capabilities.
 * Can be monitored using JProfiler, VisualVM, or JConsole.
 *
 * Key Profiling Metrics:
 * - CPU usage
 * - Memory usage (heap and non-heap)
 * - Thread information
 * - GC statistics
 * - Class loading information
 */
@Service
public class ProfilingService {

    private final MemoryMXBean memoryMXBean;
    private final ThreadMXBean threadMXBean;
    private final RuntimeMXBean runtimeMXBean;
    private final OperatingSystemMXBean osMXBean;

    public ProfilingService() {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.osMXBean = ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * Get comprehensive JVM profiling information.
     * This method is useful for monitoring in VisualVM/JProfiler.
     */
    public Map<String, Object> getProfilingInfo() {
        Map<String, Object> info = new HashMap<>();

        // Memory Information
        info.put("memory", getMemoryInfo());

        // Thread Information
        info.put("threads", getThreadInfo());

        // GC Information
        info.put("garbageCollection", getGCInfo());

        // Runtime Information
        info.put("runtime", getRuntimeInfo());

        // Operating System Information
        info.put("operatingSystem", getOSInfo());

        // Class Loading Information
        info.put("classLoading", getClassLoadingInfo());

        return info;
    }

    /**
     * Get detailed memory information for profiling.
     */
    public Map<String, Object> getMemoryInfo() {
        Map<String, Object> memoryInfo = new HashMap<>();

        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();

        Map<String, Long> heap = new HashMap<>();
        heap.put("init", heapUsage.getInit());
        heap.put("used", heapUsage.getUsed());
        heap.put("committed", heapUsage.getCommitted());
        heap.put("max", heapUsage.getMax());

        Map<String, Long> nonHeap = new HashMap<>();
        nonHeap.put("init", nonHeapUsage.getInit());
        nonHeap.put("used", nonHeapUsage.getUsed());
        nonHeap.put("committed", nonHeapUsage.getCommitted());
        nonHeap.put("max", nonHeapUsage.getMax());

        memoryInfo.put("heap", heap);
        memoryInfo.put("nonHeap", nonHeap);
        memoryInfo.put("objectPendingFinalization", memoryMXBean.getObjectPendingFinalizationCount());

        return memoryInfo;
    }

    /**
     * Get thread information for profiling.
     * Useful for detecting thread leaks and deadlocks.
     */
    public Map<String, Object> getThreadInfo() {
        Map<String, Object> threadInfo = new HashMap<>();

        threadInfo.put("threadCount", threadMXBean.getThreadCount());
        threadInfo.put("peakThreadCount", threadMXBean.getPeakThreadCount());
        threadInfo.put("totalStartedThreadCount", threadMXBean.getTotalStartedThreadCount());
        threadInfo.put("daemonThreadCount", threadMXBean.getDaemonThreadCount());

        // Check for deadlocks
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        threadInfo.put("deadlockedThreads", deadlockedThreads != null ? deadlockedThreads.length : 0);

        return threadInfo;
    }

    /**
     * Get Garbage Collection statistics.
     * Important for GC tuning.
     */
    public Map<String, Object> getGCInfo() {
        Map<String, Object> gcInfo = new HashMap<>();

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            Map<String, Object> collectorInfo = new HashMap<>();
            collectorInfo.put("collectionCount", gc.getCollectionCount());
            collectorInfo.put("collectionTime", gc.getCollectionTime());
            collectorInfo.put("memoryPoolNames", gc.getMemoryPoolNames());

            gcInfo.put(gc.getName(), collectorInfo);
        }

        return gcInfo;
    }

    /**
     * Get runtime information.
     */
    public Map<String, Object> getRuntimeInfo() {
        Map<String, Object> runtimeInfo = new HashMap<>();

        runtimeInfo.put("vmName", runtimeMXBean.getVmName());
        runtimeInfo.put("vmVendor", runtimeMXBean.getVmVendor());
        runtimeInfo.put("vmVersion", runtimeMXBean.getVmVersion());
        runtimeInfo.put("uptime", runtimeMXBean.getUptime());
        runtimeInfo.put("startTime", runtimeMXBean.getStartTime());

        return runtimeInfo;
    }

    /**
     * Get operating system information.
     */
    public Map<String, Object> getOSInfo() {
        Map<String, Object> osInfo = new HashMap<>();

        osInfo.put("name", osMXBean.getName());
        osInfo.put("arch", osMXBean.getArch());
        osInfo.put("version", osMXBean.getVersion());
        osInfo.put("availableProcessors", osMXBean.getAvailableProcessors());
        osInfo.put("systemLoadAverage", osMXBean.getSystemLoadAverage());

        return osInfo;
    }

    /**
     * Get class loading information.
     */
    public Map<String, Object> getClassLoadingInfo() {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        Map<String, Object> classInfo = new HashMap<>();

        classInfo.put("loadedClassCount", classLoadingMXBean.getLoadedClassCount());
        classInfo.put("totalLoadedClassCount", classLoadingMXBean.getTotalLoadedClassCount());
        classInfo.put("unloadedClassCount", classLoadingMXBean.getUnloadedClassCount());

        return classInfo;
    }

    /**
     * CPU-intensive operation for profiling.
     * Use this to generate CPU samples in profilers.
     */
    public long cpuIntensiveOperation(int iterations) {
        long start = System.currentTimeMillis();
        long result = 0;

        for (int i = 0; i < iterations; i++) {
            result += fibonacci(30);
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("CPU intensive operation completed in " + duration + "ms");

        return result;
    }

    /**
     * Recursive Fibonacci for CPU profiling.
     */
    private long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    /**
     * Memory allocation operation for profiling.
     * Use this to generate memory allocation samples.
     */
    public void memoryAllocationOperation(int objectCount) {
        long start = System.currentTimeMillis();

        // Allocate objects to generate memory allocation samples
        Object[] objects = new Object[objectCount];
        for (int i = 0; i < objectCount; i++) {
            objects[i] = new byte[1024]; // 1KB per object
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("Memory allocation operation completed in " + duration + "ms. " +
                "Allocated " + (objectCount * 1024 / 1024) + "MB");

        // Force GC to see GC behavior in profilers
        System.gc();
    }
}
