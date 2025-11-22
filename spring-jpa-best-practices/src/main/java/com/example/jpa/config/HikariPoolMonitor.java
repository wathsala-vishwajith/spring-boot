package com.example.jpa.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Monitors HikariCP connection pool metrics
 * Useful for detecting connection leaks and pool exhaustion
 */
@Component
@Slf4j
public class HikariPoolMonitor {

    private final HikariDataSource dataSource;

    public HikariPoolMonitor(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Log pool statistics every 60 seconds
     * In production, send these metrics to monitoring systems
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    public void logPoolStatistics() {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        if (poolMXBean != null) {
            log.info("==== HikariCP Pool Statistics ====");
            log.info("Active Connections: {}", poolMXBean.getActiveConnections());
            log.info("Idle Connections: {}", poolMXBean.getIdleConnections());
            log.info("Total Connections: {}", poolMXBean.getTotalConnections());
            log.info("Threads Awaiting Connection: {}", poolMXBean.getThreadsAwaitingConnection());
            log.info("==================================");

            // Alert if pool is under pressure
            if (poolMXBean.getThreadsAwaitingConnection() > 0) {
                log.warn("WARNING: {} threads are waiting for database connections! " +
                         "Consider increasing pool size or investigating slow queries.",
                         poolMXBean.getThreadsAwaitingConnection());
            }

            // Alert if too many active connections
            int activePercentage = (poolMXBean.getActiveConnections() * 100) / dataSource.getMaximumPoolSize();
            if (activePercentage > 80) {
                log.warn("WARNING: {}% of connection pool is active. Pool may be exhausted soon.",
                         activePercentage);
            }
        }
    }

    /**
     * Get current pool metrics (useful for health checks)
     */
    public PoolMetrics getPoolMetrics() {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        if (poolMXBean != null) {
            return new PoolMetrics(
                poolMXBean.getActiveConnections(),
                poolMXBean.getIdleConnections(),
                poolMXBean.getTotalConnections(),
                poolMXBean.getThreadsAwaitingConnection(),
                dataSource.getMaximumPoolSize()
            );
        }

        return null;
    }

    public record PoolMetrics(
        int activeConnections,
        int idleConnections,
        int totalConnections,
        int threadsAwaiting,
        int maxPoolSize
    ) {
        public int getActivePercentage() {
            return (activeConnections * 100) / maxPoolSize;
        }

        public boolean isHealthy() {
            return threadsAwaiting == 0 && getActivePercentage() < 80;
        }
    }
}
