package com.universite.courses.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    private DatabaseConfig() {}

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseConfig.class) {
                if (dataSource == null) {
                    initializeDataSource();
                }
            }
        }
        return dataSource;
    }

    private static void initializeDataSource() {
        try {
            Properties props = loadProperties();
            
            HikariConfig config = new HikariConfig();
            
            // Check if running in Docker/Spring environment
            String springProfile = System.getenv("SPRING_PROFILES_ACTIVE");
            String dbHost = System.getenv("DB_HOST");
            String dbPort = System.getenv("DB_PORT");
            String dbName = System.getenv("DB_NAME");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            if ("docker".equalsIgnoreCase(springProfile) && dbHost != null) {
                // Use environment variables for Docker
                String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", 
                    dbHost, 
                    dbPort != null ? dbPort : "5432", 
                    dbName != null ? dbName : "courses_db");
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(dbUser != null ? dbUser : props.getProperty("db.username"));
                config.setPassword(dbPassword != null ? dbPassword : props.getProperty("db.password"));
                logger.info("Using Docker database configuration: " + jdbcUrl);
            } else {
                // Use properties file for local development
                config.setJdbcUrl(props.getProperty("db.url"));
                config.setUsername(props.getProperty("db.username"));
                config.setPassword(props.getProperty("db.password"));
                logger.info("Using local database configuration");
            }
            
            config.setDriverClassName(props.getProperty("db.driver"));
            
            // Connection pool settings
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimumIdle", "5")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));
            config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "600000")));
            config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.maxLifetime", "1800000")));
            
            // Performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");
            
            // Initialize schema
            initializeSchema();
            
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Unable to find database.properties");
            }
            props.load(input);
        }
        return props;
    }

    private static void initializeSchema() {
        try (Connection conn = dataSource.getConnection();
             InputStream schemaStream = DatabaseConfig.class.getClassLoader()
                     .getResourceAsStream("schema.sql")) {
            
            if (schemaStream != null) {
                String schema = new String(schemaStream.readAllBytes());
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(schema);
                    logger.info("Database schema initialized successfully");
                }
            } else {
                logger.warn("schema.sql not found, skipping schema initialization");
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to initialize database schema", e);
        }
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}
