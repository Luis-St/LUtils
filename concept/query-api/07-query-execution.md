# Query Execution

## Overview

This document covers the internal mechanics of query execution: prepared statements, batching, streaming, timeouts, and query lifecycle.

## Query Lifecycle

```
1. Build Query (type-safe builder API)
         ↓
2. Generate SQL (dialect-specific)
         ↓
3. Get Connection (from pool)
         ↓
4. Prepare Statement (cached)
         ↓
5. Bind Parameters
         ↓
6. Execute Query
         ↓
7. Map Results
         ↓
8. Return Connection (to pool)
```

## Prepared Statement Caching

Prepared statements are cached per-connection to avoid repeated parsing:

```java
// Configuration
DatabaseConfig.builder()
    .preparedStatementCacheSize(250)      // Max statements per connection
    .preparedStatementCacheSqlLimit(2048) // Don't cache SQL longer than this
    .build();
```

### Cache Behavior

```java
// First execution: prepares statement
List<User> users1 = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();
// SQL: "SELECT * FROM users WHERE status = ?"
// → PreparedStatement created and cached

// Second execution: reuses cached statement
List<User> users2 = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();
// → PreparedStatement reused from cache, only parameters bound

// Different parameter, same SQL: still reuses statement
List<User> users3 = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .fetch();
// → Same PreparedStatement, different parameter binding
```

### Cache Key

The cache key is the SQL string. Structurally identical queries share a statement:

```java
// These share the same cached statement:
UserTable.TABLE.select().where(UserTable.ID.equalTo(uuid1)).fetchFirst();
UserTable.TABLE.select().where(UserTable.ID.equalTo(uuid2)).fetchFirst();
// Both use: "SELECT * FROM users WHERE id = ?"

// This is a different statement:
UserTable.TABLE.select().where(UserTable.EMAIL.equalTo(email)).fetchFirst();
// Uses: "SELECT * FROM users WHERE email = ?"
```

## Query Timeouts

### Per-Query Timeout

```java
// Set timeout on specific query
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .timeout(Duration.ofSeconds(5))
    .fetch();

// Throws QueryTimeoutException if exceeded
```

### Default Timeout

```java
// Configure default for all queries
DatabaseConfig.builder()
    .defaultQueryTimeout(Duration.ofSeconds(30))
    .build();

// Individual queries can override
List<Report> reports = ReportTable.TABLE.select()
    .timeout(Duration.ofMinutes(5))  // Override for this slow query
    .fetch();
```

### Statement Timeout vs Transaction Timeout

```java
// Statement timeout: per-query
UserTable.TABLE.select()
    .timeout(Duration.ofSeconds(5))  // This query only
    .fetch();

// Transaction timeout: entire transaction
try (Transaction tx = Database.beginTransaction().timeout(30)) {
    // All operations must complete within 30 seconds total
    UserTable.TABLE.select().fetch();
    OrderTable.TABLE.select().fetch();
    tx.commit();
}
```

## Streaming Large Result Sets

For large result sets that don't fit in memory:

```java
// Stream results (cursor-based, low memory)
try (Stream<User> stream = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .stream()) {

    stream
        .filter(user -> user.age() > 18)
        .map(this::processUser)
        .forEach(this::saveResult);
}

// IMPORTANT: Stream must be closed (use try-with-resources)
// Stream holds the connection open until closed
```

### Fetch Size

Control how many rows are fetched per round-trip:

```java
// Default fetch size (typically 100-1000)
DatabaseConfig.builder()
    .defaultFetchSize(500)
    .build();

// Per-query fetch size
Stream<User> stream = UserTable.TABLE.select()
    .fetchSize(100)  // Fetch 100 rows at a time
    .stream();
```

### Streaming with Async

```java
// Async streaming
CompletableFuture<Void> processing = UserTable.TABLE.select()
    .forEachAsync(user -> {
        // Called for each row
        processUser(user);
    });

// Wait for completion
processing.join();
```

## Batch Operations

### Batch Insert

```java
// Simple batch insert
List<User> users = List.of(user1, user2, user3);
List<User> saved = UserTable.TABLE.insert(users);

// Large batch with explicit batch size
int inserted = UserTable.TABLE.bulkInsert(largeUserList, 1000);
// Executes in batches of 1000, returns total count
```

### Batch Update

```java
// Update multiple entities
List<User> users = loadAndModifyUsers();
int updated = UserTable.TABLE.update(users);

// Batch statement update (same SQL, different params)
List<UUID> userIds = List.of(id1, id2, id3);
int updated = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.ID.equalTo(Param.of(UUID.class)))
    .executeBatch(userIds.stream().map(List::of).toList());
```

### Batch Delete

```java
// Delete multiple by ID
List<UUID> idsToDelete = List.of(id1, id2, id3);
int deleted = UserTable.TABLE.delete()
    .where(UserTable.ID.in(idsToDelete))
    .execute();

// Or batch individual deletes
int deleted = UserTable.TABLE.delete(usersToDelete);
```

### Batch Execution Interface

```java
public interface BatchExecutor<E> {

    /**
     * Executes the query for each parameter set.
     * @param parameterSets List of parameter values for each execution
     * @return Array of update counts
     */
    int[] executeBatch(List<List<Object>> parameterSets);

    /**
     * Executes in batches of the specified size.
     */
    int[] executeBatch(List<List<Object>> parameterSets, int batchSize);

    /**
     * Returns total affected rows across all batches.
     */
    int executeBatchSum(List<List<Object>> parameterSets);
}
```

## Query Hints

Database-specific hints for query optimization:

```java
// PostgreSQL: Set work_mem for this query
List<User> users = UserTable.TABLE.select()
    .hint("SET LOCAL work_mem = '256MB'")
    .where(complexCondition)
    .fetch();

// MySQL: Force index
List<User> users = UserTable.TABLE.select()
    .hint("USE INDEX (idx_users_email)")
    .where(UserTable.EMAIL.like("%@example.com"))
    .fetch();

// Oracle: Parallel hint
List<Order> orders = OrderTable.TABLE.select()
    .hint("/*+ PARALLEL(orders, 4) */")
    .fetch();
```

## Execution Statistics

```java
// Enable query statistics
DatabaseConfig.builder()
    .queryStatisticsEnabled(true)
    .build();

// Execute query
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Get execution stats (if enabled)
QueryStats stats = QueryStats.last();
System.out.println("Rows fetched: " + stats.getRowCount());
System.out.println("Execution time: " + stats.getExecutionTime().toMillis() + "ms");
System.out.println("Fetch time: " + stats.getFetchTime().toMillis() + "ms");

// Or capture inline
QueryExecution<List<User>> execution = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetchWithStats();

List<User> users = execution.getResult();
QueryStats stats = execution.getStats();
```

### QueryStats Interface

```java
public interface QueryStats {

    /** The executed SQL */
    String getSql();

    /** Number of rows affected or returned */
    long getRowCount();

    /** Time spent in query execution */
    Duration getExecutionTime();

    /** Time spent fetching and mapping results */
    Duration getFetchTime();

    /** Total time (execution + fetch) */
    Duration getTotalTime();

    /** Whether the query hit the prepared statement cache */
    boolean wasCached();

    /** Connection acquisition time */
    Duration getConnectionAcquireTime();
}
```

## Read-Only Optimization

```java
// Mark query as read-only for potential optimization
List<User> users = UserTable.TABLE.select()
    .readOnly()  // Hint to the database
    .fetch();

// Some databases can:
// - Skip transaction logging
// - Use read replicas
// - Avoid locking overhead
```

## Open Questions

- **Statement pooling**: Should we pool PreparedStatements across connections (like PgBouncer)?
- **Query plan caching**: Should we expose the query plan cache for inspection?
- **Parallel query hints**: How to expose parallel query configuration (PostgreSQL parallel workers)?
- **Server-side cursors**: When should we automatically use server-side cursors?
- **Result set scrolling**: Should we support scrollable result sets for random access?
