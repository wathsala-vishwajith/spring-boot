package com.example.jpa.config;

import org.hibernate.stat.Statistics;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA configuration for monitoring and performance
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class JpaConfig {

    private final EntityManagerFactory entityManagerFactory;

    /**
     * Log Hibernate statistics periodically
     * Useful for detecting N+1 queries and performance issues
     */
    @Scheduled(fixedDelay = 120000, initialDelay = 15000) // Every 2 minutes
    public void logHibernateStatistics() {
        Statistics stats = entityManagerFactory.unwrap(org.hibernate.SessionFactory.class)
            .getStatistics();

        if (stats.isStatisticsEnabled()) {
            log.info("==== Hibernate Statistics ====");
            log.info("Queries executed: {}", stats.getQueryExecutionCount());
            log.info("Query cache hits: {}", stats.getQueryCacheHitCount());
            log.info("Query cache misses: {}", stats.getQueryCacheMissCount());
            log.info("Entities loaded: {}", stats.getEntityLoadCount());
            log.info("Entities updated: {}", stats.getEntityUpdateCount());
            log.info("Entities inserted: {}", stats.getEntityInsertCount());
            log.info("Entities deleted: {}", stats.getEntityDeleteCount());
            log.info("Collections loaded: {}", stats.getCollectionLoadCount());
            log.info("Collections updated: {}", stats.getCollectionUpdateCount());
            log.info("Transactions: {}", stats.getTransactionCount());
            log.info("Successful transactions: {}", stats.getSuccessfulTransactionCount());
            log.info("Optimistic lock failures: {}", stats.getOptimisticFailureCount());

            // Warning for potential N+1 queries
            long queryCount = stats.getQueryExecutionCount();
            long entityLoadCount = stats.getEntityLoadCount();
            if (queryCount > 0 && entityLoadCount / queryCount > 10) {
                log.warn("WARNING: High entity loads per query! Possible N+1 query issue. " +
                         "Entities loaded: {}, Queries executed: {}",
                         entityLoadCount, queryCount);
            }

            log.info("==============================");

            // Optional: clear statistics for next period
            // stats.clear();
        }
    }
}
