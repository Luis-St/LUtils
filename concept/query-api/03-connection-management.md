# Connection Management

## Overview

Database connections are managed through a connection pool. The pool handles connection lifecycle, health checking, and provides connections to transactions and queries.

## Database Configuration

```java
DatabaseConfig config = DatabaseConfig.builder()
    // Connection URL
    .url("jdbc:postgresql://localhost:5432/myapp")

    // Credentials
    .username("myapp")
    .password("secret")

    // Or use a DataSource
    .dataSource(existingDataSource)

    // Pool settings
    .poolSize(10)                    // Fixed pool size
    .minPoolSize(5)                  // Or: min connections
    .maxPoolSize(20)                 // Or: max connections

    // Connection lifecycle
    .connectionTimeout(30_000)       // Max wait for connection (ms)
    .idleTimeout(600_000)            // Close idle connections after (ms)
    .maxLifetime(1_800_000)          // Max connection age (ms)
    .keepaliveTime(30_000)           // Keepalive interval (ms)

    // Validation
    .validationQuery("SELECT 1")     // Connection health check query
    .validationTimeout(5_000)        // Validation query timeout (ms)
    .testOnBorrow(true)              // Validate before use
    .testWhileIdle(true)             // Validate idle connections

    // Statement settings
    .defaultQueryTimeout(30_000)     // Default query timeout (ms)
    .preparedStatementCacheSize(250) // Per-connection PS cache

    // Dialect
    .dialect(SqlDialect.POSTGRESQL)  // Or auto-detect from URL

    .build();
```

## Initializing the Database

```java
// Initialize at application startup
Database.initialize(config);

// Or with multiple data sources
Database.initialize("primary", primaryConfig);
Database.initialize("reporting", reportingConfig);

// Shutdown at application end
Database.shutdown();
```

## Connection Pool Interface

```java
public interface ConnectionPool {

    /**
     * Gets a connection from the pool.
     * Blocks until a connection is available or timeout is reached.
     */
    Connection getConnection() throws SQLException;

    /**
     * Gets a connection with a specific timeout.
     */
    Connection getConnection(long timeout, TimeUnit unit) throws SQLException;

    /**
     * Returns a connection to the pool.
     * The connection should not be used after returning.
     */
    void returnConnection(Connection connection);

    /**
     * Returns pool statistics.
     */
    PoolStats getStats();

    /**
     * Evicts all idle connections from the pool.
     */
    void evictIdleConnections();

    /**
     * Shuts down the pool and closes all connections.
     */
    void shutdown();

    /**
     * Shuts down gracefully, waiting for active connections.
     */
    void shutdown(long timeout, TimeUnit unit);
}
```

## Pool Statistics

```java
public interface PoolStats {

    /** Total connections in pool (active + idle) */
    int getTotalConnections();

    /** Connections currently in use */
    int getActiveConnections();

    /** Connections available for use */
    int getIdleConnections();

    /** Threads waiting for a connection */
    int getPendingThreads();

    /** Total connections created since startup */
    long getTotalCreatedConnections();

    /** Total connection acquire requests */
    long getTotalAcquireCount();

    /** Average time to acquire a connection (ms) */
    double getAverageAcquireTime();

    /** Maximum time to acquire a connection (ms) */
    long getMaxAcquireTime();

    /** Number of connection timeouts */
    long getTimeoutCount();
}

// Usage
PoolStats stats = Database.getPool().getStats();
log.info("Pool: {}/{} active, {} idle",
    stats.getActiveConnections(),
    stats.getTotalConnections(),
    stats.getIdleConnections());
```

## Multiple Data Sources

```java
// Register multiple data sources
Database.initialize("primary", primaryConfig);
Database.initialize("replica", replicaConfig);
Database.initialize("analytics", analyticsConfig);

// Use specific data source
try (Transaction tx = Database.using("primary").beginTransaction()) {
    UserTable.TABLE.insert(user);
    tx.commit();
}

// Query from replica
List<User> users = Database.using("replica")
    .query(UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE)))
    .fetch();

// Or set context for a block
Database.withDataSource("analytics", () -> {
    // All queries in this block use the analytics data source
    return ReportTable.TABLE.select().fetch();
});
```

## Read Replicas

```java
// Configure primary + replicas
DatabaseConfig config = DatabaseConfig.builder()
    .url("jdbc:postgresql://primary:5432/myapp")
    .readReplicaUrls(
        "jdbc:postgresql://replica1:5432/myapp",
        "jdbc:postgresql://replica2:5432/myapp"
    )
    .readReplicaLoadBalancing(LoadBalancing.ROUND_ROBIN)
    .build();

// Writes go to primary, reads go to replicas
UserTable.TABLE.insert(user);  // -> primary

List<User> users = UserTable.TABLE.select().fetch();  // -> replica

// Force read from primary (for consistency after write)
List<User> users = UserTable.TABLE.select()
    .fromPrimary()
    .fetch();
```

## Connection Events

```java
// Listen for connection events
Database.getPool().addListener(new ConnectionPoolListener() {

    @Override
    public void onConnectionCreated(Connection connection) {
        log.debug("Connection created");
    }

    @Override
    public void onConnectionAcquired(Connection connection) {
        // Set connection-level settings
        connection.setAutoCommit(false);
    }

    @Override
    public void onConnectionReturned(Connection connection) {
        // Reset connection state
    }

    @Override
    public void onConnectionValidationFailed(Connection connection, SQLException e) {
        log.warn("Connection validation failed", e);
    }

    @Override
    public void onConnectionEvicted(Connection connection) {
        log.debug("Connection evicted");
    }
});
```

## Health Checks

```java
// Check database connectivity
boolean healthy = Database.isHealthy();

// Detailed health check
HealthStatus status = Database.healthCheck();
if (!status.isHealthy()) {
    log.error("Database unhealthy: {}", status.getMessage());
}

public interface HealthStatus {
    boolean isHealthy();
    String getMessage();
    Duration getResponseTime();
    Map<String, Object> getDetails();
}
```

## Connection Leak Detection

```java
DatabaseConfig config = DatabaseConfig.builder()
    .url("...")
    // Log warning if connection held longer than this
    .leakDetectionThreshold(60_000)  // 60 seconds
    .build();

// When a leak is detected, logs stack trace of where connection was acquired
// WARN: Connection leak detected. Acquired at:
//   com.myapp.UserService.findUsers(UserService.java:42)
//   ...
```

## Prepared Statement Caching

```java
DatabaseConfig config = DatabaseConfig.builder()
    .url("...")
    // Per-connection prepared statement cache
    .preparedStatementCacheSize(250)
    // SQL limit for caching (longer queries not cached)
    .preparedStatementCacheSqlLimit(2048)
    .build();

// Statements are automatically cached and reused
// SELECT * FROM users WHERE id = ?  -- cached
// SELECT * FROM users WHERE id = ?  -- reused from cache
```

## Open Questions

- **HikariCP integration**: Should we integrate with HikariCP or build our own pool?
- **Connection per thread**: Should there be thread-local connection binding for transaction context?
- **Sharding**: How should database sharding be handled?
- **Metrics integration**: Should we integrate with Micrometer for metrics export?
- **SSL/TLS**: How should SSL certificate configuration work?
