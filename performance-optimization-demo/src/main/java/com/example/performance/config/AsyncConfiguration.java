package com.example.performance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration for asynchronous processing.
 * Demonstrates both traditional thread pools and Java 21 virtual threads.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Value("${app.async.core-pool-size:5}")
    private int corePoolSize;

    @Value("${app.async.max-pool-size:10}")
    private int maxPoolSize;

    @Value("${app.async.queue-capacity:100}")
    private int queueCapacity;

    @Value("${app.async.thread-name-prefix:async-executor-}")
    private String threadNamePrefix;

    @Value("${app.virtual-threads.enabled:false}")
    private boolean virtualThreadsEnabled;

    /**
     * Traditional Thread Pool Executor for async operations.
     * Use for CPU-bound tasks.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        System.out.println("Initialized TaskExecutor with core pool size: " + corePoolSize +
                ", max pool size: " + maxPoolSize);

        return executor;
    }

    /**
     * Virtual Thread Executor (Java 21+).
     * Perfect for I/O-bound tasks with high concurrency.
     * Virtual threads are lightweight and can handle millions of concurrent tasks.
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        if (virtualThreadsEnabled) {
            System.out.println("Virtual Threads enabled - using virtual thread executor");
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            System.out.println("Virtual Threads not enabled - falling back to thread pool");
            return taskExecutor();
        }
    }
}
