package com.example.jpa.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Database configuration demonstrating:
 * 1. HikariCP configuration
 * 2. Connection pool monitoring
 * 3. Performance tuning
 */
@Configuration
@Slf4j
public class DatabaseConfig {

    /**
     * DataSource configuration with HikariCP
     * HikariCP is the default connection pool in Spring Boot
     * This configuration makes the settings explicit
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * HikariCP DataSource with custom configuration
     *
     * Best Practices:
     * 1. Set maximum-pool-size based on your database limits and application needs
     * 2. Keep minimum-idle >= half of maximum-pool-size for responsiveness
     * 3. Enable leak-detection-threshold in development/testing
     * 4. Set appropriate connection-timeout (don't set too low)
     * 5. Set max-lifetime less than database connection timeout
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();

        log.info("HikariCP Configuration:");
        log.info("  Pool Name: {}", dataSource.getPoolName());
        log.info("  Maximum Pool Size: {}", dataSource.getMaximumPoolSize());
        log.info("  Minimum Idle: {}", dataSource.getMinimumIdle());
        log.info("  Connection Timeout: {}ms", dataSource.getConnectionTimeout());
        log.info("  Idle Timeout: {}ms", dataSource.getIdleTimeout());
        log.info("  Max Lifetime: {}ms", dataSource.getMaxLifetime());
        log.info("  Leak Detection Threshold: {}ms", dataSource.getLeakDetectionThreshold());

        return dataSource;
    }

    /**
     * Connection pool metrics logging
     * In production, integrate with monitoring tools like Micrometer/Prometheus
     */
    @Bean
    public HikariPoolMonitor hikariPoolMonitor(HikariDataSource dataSource) {
        return new HikariPoolMonitor(dataSource);
    }
}
