# Database Concept — Suggestions & Improvements

## 1. Connection Pool Management (High)

Support `javax.sql.DataSource` with two built-in implementations:
- A single connection implementation (for simple use cases / testing)
- A pool connection implementation (for production use)

This allows users to bring their own pool (HikariCP, Apache DBCP, etc.) while also working without external dependencies.

```java
// Built-in single connection
SqlDatabaseConfig.builder()
    .dataSource(new SqlSingleConnectionDataSource("jdbc:..."))
    .build();

// Built-in pooled connection
SqlDatabaseConfig.builder()
    .dataSource(new SqlPooledDataSource(...))
    .build();

// User-provided pool (e.g., HikariCP)
SqlDatabaseConfig.builder()
    .dataSource(hikariDataSource)
    .build();
```

## 2. Database Context & Connection Wiring (High)

The API currently has no mechanism for tables, queries, and operations to obtain a JDBC connection. 
`SqlTable.TABLE` instances are static constants, but terminal operations (`fetch()`, `execute()`, `insert()`, etc.) need a connection. 
This applies to all operations: queries, inserts, updates, deletes, DDL, page navigation, and transactions.

**Approach: Hybrid — thread-local default + explicit `from()` override.**

### Connection Resolution Order

When a terminal operation needs a connection, it resolves in this order:

1. Explicit `from()` override (if called on the table/query)
2. Thread-local `SqlTransaction` (if inside `inTransaction()`)
3. Thread-local `SqlDatabase` (if `setDefault()` was called)
4. Throw `SqlConnectionException` (no context available)

### Setup & Default Database

```java
// Create database and set as thread-local default
SqlDatabase db = SqlDatabase.create(config);
SqlDatabase.setDefault(db);
// or in one call:
SqlDatabase.createAndSetDefault(config);

// All operations implicitly use the default database
List<User> users = UserTable.TABLE.select().fetch();
UserTable.TABLE.insert(user);
UserTable.TABLE.update().set(UserTable.STATUS, ACTIVE).execute();
UserTable.TABLE.delete().where(UserTable.STATUS.equalTo(DELETED)).execute();
```

### Explicit Override via `from()`

`from()` is available on `SqlTable` and overrides the thread-local for that operation chain:

```java
SqlDatabase analyticsDb = SqlDatabase.create(analyticsConfig);

// Explicit database — ignores thread-local
List<User> users = UserTable.TABLE.from(analyticsDb).select().fetch();
UserTable.TABLE.from(analyticsDb).insert(user);
```

### Transaction Context

Transactions follow the same hybrid pattern. `inTransaction()` sets a thread-local transaction — all operations within the lambda automatically use the transaction's connection:

```java
// Thread-local transaction (default)
User saved = db.inTransaction(tx -> {
    User user = UserTable.TABLE.insert(newUser);     // uses tx's connection
    OrderTable.TABLE.insert(order);                  // uses tx's connection
    return user;
});
// Auto-commit on success, auto-rollback on exception (ThrowableFunction/ThrowableConsumer)

// Explicit transaction override via from()
SqlTransaction tx = db.beginTransaction();
UserTable.TABLE.withIn(tx).insert(user);               // explicit
tx.commit();
```

### Multi-Database Support

Multiple databases are supported. One can be the default (thread-local), others are accessed via `from()`:

```java
SqlDatabase primary = SqlDatabase.createAndSetDefault(primaryConfig);
SqlDatabase analytics = SqlDatabase.create(analyticsConfig);

// Uses primary (thread-local default)
UserTable.TABLE.select().fetch();

// Uses analytics (explicit override)
AnalyticsTable.TABLE.from(analytics).select().fetch();

// Cross-database in one transaction is NOT supported — each from() uses its own database's connection
```

### Async Context Propagation

The `asyncExecutor` configured in `SqlDatabaseConfig` is automatically wrapped to capture and propagate the thread-local context (both database and transaction) to async threads:

```java
// Context propagates to async thread automatically
CompletableFuture<List<User>> future = UserTable.TABLE.select().fetchAsync();

// Explicit override also works with async
CompletableFuture<List<User>> future = UserTable.TABLE.from(analyticsDb).select().fetchAsync();

// Inside inTransaction — async operations share the transaction context
db.inTransaction(tx -> {
    CompletableFuture<List<User>> users = UserTable.TABLE.select().fetchAsync(); // uses tx
    return users.join();
});
```

### Affected Operations

This wiring applies to all terminal operations across the system:

- **Queries:** `fetch()`, `fetchOne()`, `count()`, `exists()`, `stream()`, `fetchPage()`, and all async variants
- **Inserts:** `insert(entity)`, `insert(entities...)`, `insert(Collection)`, `SqlInsertQuery.execute()`
- **Updates:** `update(entity)`, `SqlUpdateQuery.execute()`, `SqlUpdateQuery.returning()`
- **Deletes:** `delete(entity)`, `SqlDeleteQuery.execute()`
- **DDL:** `create()`, `drop()`, `truncate()`, `exists()`, `createIndex()`, `dropIndex()`
- **Sequences:** `nextSequenceValue()`
- **Page navigation:** `SqlPage.fetchNext()`, `SqlPage.fetchPrevious()`

## 3. Ad-Hoc JOIN Support (High)

Add manual join methods to allow joins between entities without declared YAML relationships, self-joins, and many-to-many joins. 
Provides `innerJoin()`, `leftJoin()`, `rightJoin()`, and `fullJoin()` methods that take a table and a join condition as parameters and return a new query object:

```java
// Ad-hoc join
UserTable.TABLE.select()
    .innerJoin(AddressTable.TABLE, UserTable.ADDRESS_ID.equalTo(AddressTable.ID))
    .fetch();

// Self-join (employee -> manager)
UserTable.TABLE.select()
    .innerJoin(UserTable.TABLE, UserTable.MANAGER_ID.equalTo(UserTable.ID))
    .fetch();

// Many-to-many via join table
UserTable.TABLE.select()
    .innerJoin(UserRoleTable.TABLE, UserTable.ID.equalTo(UserRoleTable.USER_ID))
    .innerJoin(RoleTable.TABLE, UserRoleTable.ROLE_ID.equalTo(RoleTable.ID))
    .fetch();
```

These complement the generated relationship joins (e.g., `UserTable.TABLE.joinAddress()`).

## 4. Optimistic Locking / Version Column (High)

Standalone feature, separate from audit. Configurable similarly to the audit feature.

- Version field in YAML schema (e.g., `version: { type: Long, optimisticLock: true }`)
- Automatic `WHERE version = ?` on updates
- Automatic version increment on write
- Throws `SqlStaleEntityException` on version mismatch

```yaml
columns:
  version: { type: Long, optimisticLock: true }
```

## 5. CTE (WITH Clause) Support (High)

Add `CommonTableExpression` class and `with()` method on select queries. The CTE expression can be set either in the factory method or in the `with()` call:

```java
// Option 1: Expression set in with()
CommonTableExpression activeUsers = CommonTableExpression.of("active_users");
UserTable.TABLE.select()
    .with(activeUsers, UserTable.TABLE.subquery()
        .where(UserTable.STATUS.equalTo(ACTIVE)))
    .where(UserTable.ID.in(activeUsers))
    .fetch();

// Option 2: Expression set in of()
CommonTableExpression activeUsers = CommonTableExpression.of("active_users",
    UserTable.TABLE.subquery().where(UserTable.STATUS.equalTo(ACTIVE)));
UserTable.TABLE.select()
    .with(activeUsers)
    .where(UserTable.ID.in(activeUsers))
    .fetch();
```

The `with(cte)` method must validate that the `CommonTableExpression` has its expression set and throw an exception if it is not. 
Supports recursive CTEs for tree/hierarchy traversal.

## 6. INSERT ... SELECT (Medium)

Add support for inserting from a query result:

```java
ArchiveTable.TABLE.insert()
    .fromSelect(UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(DELETED)))
    .execute();
```

Useful for archival, data migration, and bulk operations.

## 7. Expression-Based SET in Updates (Medium)

Add `set(SqlColumn<V>, SqlExpression<V>)` overload alongside existing `set(SqlColumn<V>, V)` to support setting columns from other columns or function expressions:

```java
// Set from another column: SET email = backup_email
UserTable.TABLE.update()
    .set(UserTable.EMAIL, UserTable.BACKUP_EMAIL)
    .where(UserTable.EMAIL.isNull())
    .execute();

// Set from expression: SET name = UPPER(name)
UserTable.TABLE.update()
    .set(UserTable.NAME, SqlString.upper(UserTable.NAME))
    .where(UserTable.ID.equalTo(userId))
    .execute();
```

The existing `increment()` and `setNow()` are special cases of this pattern.

## 8. Type-Specific Column Operations (Medium)

Use composition to provide type-safe column operations without class explosion across dialects. Instead of typed column subclasses, provide accessor objects:

```java
// String operations - only available via .string()
UserTable.NAME.string().startsWith("John")
UserTable.NAME.string().contains("doe")
UserTable.NAME.string().like("%@gmail.com")
UserTable.NAME.string().lengthGreaterThan(5)

// Numeric operations - only available via .numeric()
UserTable.AGE.numeric().between(18, 65)

// Temporal operations - only available via .temporal()
UserTable.CREATED_AT.temporal().withinLast(Duration.ofDays(7))
```

This gives compile-time safety (`UserTable.AGE.string().startsWith("x")` would not compile) without needing type-specific column subclasses for every dialect. 
Remove `like()`, `startsWith()`, `contains()`, `endsWith()`, `lengthGreaterThan()`, `withinLast()`, and `equalToIgnoreCase()` from the base `SqlColumn` interface.

## 9. Batch Insert Controls (Medium)

Add collection-based insert overloads with optional batch size control:

```java
// Insert a collection
UserTable.TABLE.insert(userList);

// Insert with batch size control
UserTable.TABLE.insert(userList, 1000); // process in batches of 1000
```

Adds `insert(Collection<T>)` and `insert(Collection<T>, int batchSize)` to `SqlTable`.

## 10. SqlDatabase Lifecycle Methods (Medium)

Add lifecycle methods to `SqlDatabase`:

- `close()` / `AutoCloseable` — resource cleanup for the connection pool
- `getDialect()` — expose the active dialect so user code can branch on capabilities
- `health()` / `ping()` — connection validation for health checks

## 11. UNION / INTERSECT / EXCEPT (Low)

Add set operations to combine query results:

```java
query1.union(query2).fetch();
query1.unionAll(query2).fetch();
query1.intersect(query2).fetch();
query1.except(query2).fetch();
```

## 12. SqlPage Navigation (Low)

Add navigation methods to `SqlPage`:

```java
SqlPage<User> next = currentPage.fetchNext();
SqlPage<User> prev = currentPage.fetchPrevious();
```

Offset-based pagination only, no cursor-based pagination.

## 13. Event / Listener Hooks (Low)

Add entity lifecycle hooks:

- `beforeInsert()`, `afterInsert()`
- `beforeUpdate()`, `afterUpdate()`
- `beforeDelete()`, `afterDelete()`

Useful for validation beyond constraints, computed fields, cache invalidation, and custom logic (e.g., "send email on status change").

## 14. Dialect Access Verbosity (Low)

Keep the `dialect(SqlDialect)` method but reduce verbosity by propagating the dialect context within a query. When a query is started via a dialect-specific table, columns within that query should not require their own `.dialect()` call:

```java
// Current (verbose) - two .dialect() calls
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.EMAIL.dialect(SqlDialect.POSTGRES).ilike("%@GMAIL.COM"))
    .fetch();

// Improved - dialect propagates from table to columns in the query
// The dialect is know in generation, so no need to specify it on every column
// The columns are defined as PostgresColumns, so they know how to generate the correct SQL for the dialect without needing a hint
// The table is defined as a PostgresTable, so it knows how to generate the correct SQL for the dialect without needing a hint
// The .dialect() method is still available for explicit overrides, but in most cases the dialect will be known from the table and does not need to be specified on every column
UserTable.TABLE.select()
    .where(UserTable.EMAIL.ilike("%@GMAIL.COM"))
    .fetch();
```

## 15. DISTINCT ON (Low)

Implement `DISTINCT ON` as a PostgreSQL-specific extension of the select query, only available through PostgreSQL table classes:

```java
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .distinctOn(UserTable.STATUS)
    .orderBy(UserTable.STATUS.asc(), UserTable.CREATED_AT.desc())
    .fetch();
```

Not available on the base `SqlSelectQuery` interface — only on the PostgreSQL-specific select query type.

## 16. Dialect-Specific Feature Migration Strategy (Low)

General strategy: move features that are specific to a single dialect into dialect-specific interfaces/classes rather than the base API. Candidates:

- `DISTINCT ON` — PostgreSQL select query
- `RETURNING` clause differences — PostgreSQL vs SQL Server `OUTPUT`
- `MERGE` statement — SQL Server / Oracle
- Array operations — PostgreSQL
- JSON operators — PostgreSQL (`->`, `->>`) vs MySQL (`JSON_EXTRACT`)
- `CONNECT BY` — Oracle
- Table inheritance — PostgreSQL

This keeps the base API clean and ensures dialect-specific features are only available when targeting the correct dialect.

## 17. Remove EXPLAIN / Query Plan Methods (Low)

Remove `explain()` from `SqlSelectQueryBase` and do not add `explainAnalyze()`. Query plan analysis should be done outside the library using database-specific tooling or directly via `SqlDatabase.getConnection()`.

## 18. SqlCondition Composition Ergonomics (Low)

Add varargs overloads to instance methods for consistency with static methods:

```java
// Static (already exists)
SqlCondition.and(c1, c2, c3)
SqlCondition.or(c1, c2, c3)

// Instance (new - varargs)
c1.and(c2, c3)
c1.or(c2, c3)
```
