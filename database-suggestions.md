# Database Concept — Suggestions & Improvements

## 1. Connection Pool Management (High)

There is no `SqlConnectionPool` or `SqlDataSource` abstraction. `SqlDatabaseConfig.asyncConnectionPoolSize()` exists but there's no actual pool interface. Before transactions or queries can work, you need:

- A connection pool interface (acquire/release connections)
- Health checks / connection validation (idle test queries)
- Idle timeout and max lifetime settings
- Min/max pool size configuration
- Connection leak detection (connections not returned within a threshold)

Consider:
```java
SqlDatabaseConfig.builder()
    .connectionPool(SqlConnectionPool.builder()
        .jdbcUrl("jdbc:postgresql://localhost/mydb")
        .username("user")
        .password("pass")
        .minIdle(5)
        .maxSize(20)
        .idleTimeout(Duration.ofMinutes(10))
        .maxLifetime(Duration.ofMinutes(30))
        .connectionTestQuery("SELECT 1")
        .leakDetectionThreshold(Duration.ofSeconds(30))
        .build())
    .build();
```

Alternatively, accept a `javax.sql.DataSource` to allow users to bring their own pool (HikariCP, etc.):
```java
SqlDatabaseConfig.builder()
    .dataSource(hikariDataSource)
    .build();
```

## 2. Ad-Hoc JOIN Support (High)

Joins are pre-generated from YAML relationships only. There's no way to do:

- Joins between entities without a declared relationship
- Self-joins (e.g., employee → manager on the same table)
- Joins on non-FK conditions (e.g., range-based: `ON a.date BETWEEN b.start AND b.end`)
- Cross joins

The concept says "Many-to-Many: Not supported — Use manual join table." But there's no manual join API. Consider a low-level `join()` alongside the generated ones:
```java
UserTable.TABLE.select()
    .join(AddressTable.TABLE, UserTable.ADDRESS_ID.equalTo(AddressTable.ID))
    .fetch();
```

## 3. Soft Delete Specification (High)

Mentioned in `02-query-api.md` (`hardDelete()`) but never fully defined:

- How is soft delete configured in YAML? (e.g., `softDelete: { column: deleted_at, type: Instant }`)
- Are soft-deleted rows automatically excluded from `select()`? (global filter)
- Can you query soft-deleted rows? (e.g., `withDeleted()` or `onlyDeleted()`)
- How does soft delete interact with unique constraints? (deleted + active row with same email)

This needs a dedicated section in the schema doc.

## 4. Optimistic Locking / Version Column (High)

`SqlStaleEntityException` exists in the exception hierarchy, but the concept never explains how it's triggered. Missing:

- Version field in YAML schema (e.g., `version: { type: Long, optimisticLock: true }`)
- Automatic `WHERE version = ?` on updates
- Automatic version increment on write

Without this, `SqlStaleEntityException` can never actually be thrown.

## 5. CTE (WITH Clause) Support (High)

`SqlDialectFeatures.supportsCte()` exists and the feature matrix shows all dialects support CTEs, but there's no API to build them. CTEs are essential for recursive queries (tree structures, hierarchies) and complex multi-step queries:
```java
UserTable.TABLE.select()
    .with("active_users", UserTable.TABLE.subquery()
        .where(UserTable.STATUS.equalTo(ACTIVE)))
    .where(UserTable.ID.in(cte("active_users")))
    .fetch();
```

## 6. INSERT ... SELECT (Medium)

The insert API only accepts entity instances. There's no way to insert from a query:
```java
ArchiveTable.TABLE.insert()
    .fromSelect(UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(DELETED)))
    .execute();
```
Common for archival, data migration, and bulk operations.

## 7. Expression-Based SET in Updates (Medium)

`set(column, value)` and `increment()` exist, but there's no way to:

- Set a column from another column: `SET email = backup_email`
- Set from an expression: `SET name = UPPER(name)`
- Set from a subquery: `SET status = (SELECT ...)`

Consider: `set(column, expression)` alongside `set(column, value)`.

## 8. Type-Specific Column Classes (Medium)

`SqlColumn` exposes `like()`, `startsWith()`, `contains()`, `lengthGreaterThan()` on all columns including numeric and date ones. With code generation, you could generate type-specific column classes:

- `SqlStringColumn<T>` with string operations
- `SqlNumericColumn<T>` with numeric operations
- `SqlTemporalColumn<T>` with `withinLast()`, temporal comparisons

This gives compile-time safety — `UserTable.AGE.startsWith("x")` would be a compile error instead of a runtime SQL error.

## 9. Batch Insert Controls (Medium)

`UserTable.TABLE.insert(user1, user2, user3)` exists but there's no control over batch behavior:

- Chunk size for large collections (inserting 100K rows)
- Whether to use multi-value INSERT or JDBC batch
- Progress/error reporting for partial failures

Consider:
```java
UserTable.TABLE.insert(largeList)
    .batchSize(1000)
    .onError(BatchErrorStrategy.CONTINUE) // or ABORT
    .execute();
```

## 10. SqlDatabase Lifecycle Methods (Medium)

Even with generated table classes, `SqlDatabase` should expose more:

- `close()` / `AutoCloseable` — resource cleanup for the connection pool
- `getDialect()` — expose the active dialect so user code can branch on capabilities
- `health()` / `ping()` — connection validation for health checks
- `raw(String sql)` — escape hatch for edge cases the API can't express

## 11. UNION / INTERSECT / EXCEPT (Low)

No way to combine query results. These are standard SQL operations:
```java
query1.union(query2).fetch();
query1.unionAll(query2).fetch();
query1.intersect(query2).fetch();
```

## 12. SqlPage Navigation (Low)

`SqlPage` has `hasNext()` / `hasPrevious()` but no way to actually fetch the next page. Consider:
```java
SqlPage<User> next = currentPage.fetchNext();
SqlPage<User> prev = currentPage.fetchPrevious();
```

Also, offset-based pagination degrades on large datasets. Consider offering keyset (cursor) pagination:
```java
SqlCursorPage<User> page = query.fetchCursorPage(pageSize, afterCursor);
```

## 13. Event / Listener Hooks (Low)

No way to hook into entity lifecycle events:

- `beforeInsert`, `afterInsert`, `beforeUpdate`, `afterUpdate`, `beforeDelete`
- Useful for: validation beyond constraints, computed fields, cache invalidation, custom audit logging

The audit system handles `createdAt`/`updatedBy` automatically, but custom logic (e.g., "send email on status change") has no hook point.

## 14. Dialect Access Verbosity (Low)

The `.dialect(SqlDialect.POSTGRES)` pattern is functional but verbose:
```java
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.EMAIL.dialect(SqlDialect.POSTGRES).ilike("%@GMAIL.COM"))
    .fetch();
```

Two `.dialect()` calls in one query. Consider:

- A scoped dialect context: `database.withDialect(POSTGRES, () -> { ... })`
- Generating dialect-specific table classes: `UserTable.POSTGRES.select()...` (since the dialect is known at config/generation time)

## 15. DISTINCT ON — PostgreSQL (Low)

The concept has `distinct()` but no `DISTINCT ON(columns)`, which is a powerful PostgreSQL feature for getting the first row per group:
```java
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .distinctOn(UserTable.STATUS)
    .orderBy(UserTable.STATUS.asc(), UserTable.CREATED_AT.desc())
    .fetch();
```

## 16. LATERAL JOIN Support (Low)

`SqlDialectFeatures.supportsLateral()` exists but there's no API. Lateral joins are powerful for "top-N per group" queries and are supported by PostgreSQL and MySQL 8.0+.

## 17. Schema Validation at Startup (Low)

The Gradle plugin has `validateSchema`, but there's no runtime validation. Consider validating the YAML-defined schema against the actual database on startup:
```java
SqlDatabaseConfig.builder()
    .validateSchemaOnConnect(true)
    .build();
```

## 18. EXPLAIN ANALYZE (Low)

`query.explain()` exists, but there's no `query.explainAnalyze()` which actually executes the query and returns real execution stats — essential for debugging performance beyond estimated plans.

## 19. SqlCondition Composition Ergonomics (Low)

Static methods accept multiple conditions: `SqlCondition.and(c1, c2, c3)`. But instance methods only chain one at a time: `c1.and(c2).and(c3)`. For consistency, instance methods could accept varargs too.

---

## Priority Summary

| Priority | # | Suggestion |
|----------|---|-----------|
| **High** | 1 | Connection pool management |
| **High** | 2 | Ad-hoc JOIN support |
| **High** | 3 | Soft delete specification |
| **High** | 4 | Optimistic locking / version column |
| **High** | 5 | CTE support |
| **Medium** | 6 | INSERT ... SELECT |
| **Medium** | 7 | Expression-based SET in updates |
| **Medium** | 8 | Type-specific column classes |
| **Medium** | 9 | Batch insert controls |
| **Medium** | 10 | SqlDatabase lifecycle methods |
| **Low** | 11 | UNION / INTERSECT / EXCEPT |
| **Low** | 12 | SqlPage navigation |
| **Low** | 13 | Event / listener hooks |
| **Low** | 14 | Dialect access verbosity |
| **Low** | 15 | DISTINCT ON (PostgreSQL) |
| **Low** | 16 | LATERAL JOIN support |
| **Low** | 17 | Schema validation at startup |
| **Low** | 18 | EXPLAIN ANALYZE |
| **Low** | 19 | SqlCondition composition ergonomics |
