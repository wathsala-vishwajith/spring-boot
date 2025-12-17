package com.example.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Performance Optimization Demo.
 *
 * This application demonstrates various performance optimization techniques:
 * - Profiling with JProfiler/VisualVM
 * - Memory leak detection
 * - GC tuning
 * - Connection pool optimization
 * - Async processing with CompletableFuture and Virtual Threads
 *
 * JVM Arguments for profiling and GC tuning:
 *
 * For JProfiler:
 * -agentpath:/path/to/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849
 *
 * For VisualVM (JMX enabled):
 * -Dcom.sun.management.jmxremote
 * -Dcom.sun.management.jmxremote.port=9010
 * -Dcom.sun.management.jmxremote.local.only=false
 * -Dcom.sun.management.jmxremote.authenticate=false
 * -Dcom.sun.management.jmxremote.ssl=false
 *
 * GC Tuning Options:
 *
 * G1GC (Recommended for most applications):
 * -XX:+UseG1GC
 * -XX:MaxGCPauseMillis=200
 * -XX:G1HeapRegionSize=16M
 * -XX:InitiatingHeapOccupancyPercent=45
 *
 * ZGC (Low latency):
 * -XX:+UseZGC
 * -XX:ZCollectionInterval=5
 *
 * Shenandoah GC:
 * -XX:+UseShenandoahGC
 *
 * Memory settings:
 * -Xms512m
 * -Xmx2g
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=/tmp/heapdump.hprof
 *
 * GC Logging:
 * -Xlog:gc*:file=/tmp/gc.log:time,uptime:filecount=5,filesize=10M
 */
@SpringBootApplication
@EnableAsync
public class PerformanceOptimizationApplication {

    public static void main(String[] args) {
        // Print JVM information for profiling
        System.out.println("=== JVM Information ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("JVM Name: " + System.getProperty("java.vm.name"));
        System.out.println("JVM Version: " + System.getProperty("java.vm.version"));

        Runtime runtime = Runtime.getRuntime();
        System.out.println("Available Processors: " + runtime.availableProcessors());
        System.out.println("Max Memory: " + (runtime.maxMemory() / 1024 / 1024) + " MB");
        System.out.println("Total Memory: " + (runtime.totalMemory() / 1024 / 1024) + " MB");
        System.out.println("Free Memory: " + (runtime.freeMemory() / 1024 / 1024) + " MB");
        System.out.println("======================\n");

        SpringApplication.run(PerformanceOptimizationApplication.class, args);
    }
}
