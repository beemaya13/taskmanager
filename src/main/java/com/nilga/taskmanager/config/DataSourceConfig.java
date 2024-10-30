package com.nilga.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuration class for setting up the data source with automatic failover
 * between primary and failover databases.
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.hikari.primary.jdbc-url}")
    private String primaryUrl;

    @Value("${spring.datasource.hikari.primary.username}")
    private String primaryUsername;

    @Value("${spring.datasource.hikari.primary.password}")
    private String primaryPassword;

    @Value("${spring.datasource.hikari.primary.driver-class-name}")
    private String primaryDriverClassName;

    @Value("${spring.datasource.hikari.failover.jdbc-url}")
    private String failoverUrl;

    @Value("${spring.datasource.hikari.failover.username}")
    private String failoverUsername;

    @Value("${spring.datasource.hikari.failover.password}")
    private String failoverPassword;

    @Value("${spring.datasource.hikari.failover.driver-class-name}")
    private String failoverDriverClassName;

    /**
     * Creates a {@link DataSource} bean that attempts to connect to the primary database.
     * If the primary database is unavailable, it switches to the configured failover database.
     *
     * @return a configured {@link DataSource} instance
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        try {
            // Attempt to connect to the primary database
            config.setJdbcUrl(primaryUrl);
            config.setUsername(primaryUsername);
            config.setPassword(primaryPassword);
            config.setDriverClassName(primaryDriverClassName);
            return new HikariDataSource(config);

        } catch (Exception e) {
            // If primary database is unavailable, switch to failover database
            config.setJdbcUrl(failoverUrl);
            config.setUsername(failoverUsername);
            config.setPassword(failoverPassword);
            config.setDriverClassName(failoverDriverClassName);
            return new HikariDataSource(config);
        }
    }
}
