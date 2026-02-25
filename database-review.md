# Database Concept Review — `net.luis.utils.io.database`

## Overview

This is a comprehensive **type-safe SQL query builder and database abstraction layer** for Java, designed as a library-level alternative to frameworks like jOOQ or Exposed. The concept spans **~90+ files** across 15 subpackages covering queries, dialects, migrations, window functions, auditing, async operations, and more.

The overall architectural vision is **ambitious and well-structured**. Below is a detailed review with ratings, strengths, and suggestions organized by subsystem.

---

## 1. Architecture & Design — ⭐⭐⭐⭐ (4/5)

### Strengths
- **Clean separation of concerns**: Dialect vs. Renderer vs. Query vs. Table is well-partitioned. The `SqlDialect` handles type mappings and feature discovery, while `SqlRenderer` handles syntactic output — this is a mature design decision.
- **Fluent builder pattern** is consistently applied across queries, migrations, conditions, and window clauses.
- **Generics are used extensively and correctly** — `SqlColumn<T>`, `SqlExpression<T>`, `SqlTable<T>`, `SqlQueryProvider<T>` maintain compile-time type safety throughout the chain.
- **The `SqlRenderable` / `SqlRendered` abstraction** cleanly separates SQL construction from execution, supporting prepared statements with bound parameters.
- **Checked exceptions** (`SqlException extends Exception`) force callers to handle database errors — a deliberate and defensible choice for a database library.

### Suggestions
1. **`SqlDatabase.of(SqlDatabaseConfig)` is a static interface method that throws** — this is a code smell for a factory. Consider making `SqlDatabaseConfig.build()` the sole entry point (which it already is), and **remove `SqlDatabase.of()`** to avoid two ways to create a database. Alternatively, make `of()` delegate to a config builder internally.

2. **`SqlDialect` is an `abstract class` while nearly everything else is an `interface`** — consider making it an interface with default methods, or document the rationale. An abstract class limits users to single-inheritance when creating custom dialects.

3. **Missing `SqlDatabase.from()` for async** — the database provides `SqlQueryProvider` via `from(table)`, but there's no direct async entry point at the database level. Users must call `.async()` on individual queries, which is fine, but consider whether a `SqlDatabase.asyncFrom()` or similar would reduce boilerplate for fully-async codebases.

---

## 2. Query System — ⭐⭐⭐⭐⭐ (5/5)

### Strengths
- **Full CRUD coverage** with `SqlSelectQuery`, `SqlInsertQuery`, `SqlUpdateQuery`, `SqlDeleteQuery`.
- **Type-safe projections** via `SqlRow2` through `SqlRow16` records — ensures compile-time checking of multi-column selects.
- **Rich SELECT API**: `WHERE`, `JOIN` (all types), `GROUP BY`, `HAVING`, `ORDER BY`, `LIMIT/OFFSET`, `DISTINCT`, `UNION/INTERSECT/EXCEPT`, `CTE`, `EXISTS/NOT EXISTS` subqueries.
- **Pagination** is built-in via `SqlPage<T>` with `fetchNext()`/`fetchPrevious()` navigation — excellent UX.
- **Pessimistic locking** (`FOR UPDATE`, `SKIP LOCKED`, `NOWAIT`) is thoughtfully scoped to entity queries only, not projections.
- **Upsert, INSERT OR IGNORE, INSERT FROM SELECT, batch inserts** — covers advanced insert patterns.
- **`skipVersionCheck()`** on `SqlQueryProvider` is a smart escape hatch for optimistic locking.

### Suggestions
1. **The 16 overloaded `select()` methods on `SqlQueryProvider`** (one per arity) are a maintenance burden. Consider:
    - A code-generation step to produce these.
    - Alternatively, a `select(SqlColumn<?>...)` varargs fallback that returns `SqlSelectProjectionQuery<SqlRow>` with runtime-typed access (already partially present as `select(SqlExpression<?>...)`). Document the tradeoff clearly.

2. **`SqlDeleteQuery` lacks `returning()`** — `SqlInsertQuery` and `SqlUpdateQuery` both have `returning()`, but `SqlDeleteQuery` does not. PostgreSQL supports `DELETE ... RETURNING`, and this would be a natural addition.

3. **`SqlUpdateQuery.setNow()` accepts `SqlColumn<?>`** — the wildcard type here is too loose. Consider constraining it to temporal types (`SqlColumn<? extends Temporal>` or similar) for type safety.

4. **Missing `SqlSelectQueryBase.forUpdate()`** — it's only on `SqlSelectQuery`, which means projection queries can't lock rows. While the Javadoc explains projection queries are "typically read-only," there are valid use cases (e.g., `SELECT id FROM ... FOR UPDATE`). Consider either adding it to the base or documenting the limitation more prominently.

5. **`CommonTableExpression` is a concrete class, not an interface** — inconsistent with the rest of the API. Consider making it an interface or a sealed abstract class.

---

## 3. Dialect & Renderer System — ⭐⭐⭐⭐ (4/5)

### Strengths
- **`SqlDialectFeatures` is comprehensive** — 16 feature flags covering RETURNING, SKIP LOCKED, arrays, JSONB, CTEs, window functions, schemas, MERGE, LATERAL, identity columns, etc.
- **`SqlColumnType` enum covers modern types** — UUID, JSONB, ARRAY, XML, BYTEA, TIMESTAMP_TZ.
- **`SqlRenderer` cleanly captures syntactic differences** — identifier quoting, boolean literals, parameter placeholders, limit/offset, upsert syntax, RETURNING.
- **`SqlDefaultRenderer`** provides sensible ANSI SQL defaults.
- **PostgreSQL is a first-class dialect** with dedicated `PostgresTable`, `PostgresColumn`, query types, and ops (array, JSON, string).

### Suggestions
1. **`PostgresDialect.createRenderer()` returns `SqlDefaultRenderer`** — this should return a `PostgresSqlRenderer`. This appears to be a placeholder, but it's worth flagging: the Postgres-specific renderer already exists in `dialect.postgres.PostgresSqlRenderer`.

2. **No MySQL/MariaDB, SQLite, or SQL Server dialect** — even as stubs, having at least a MySQL dialect would validate the abstraction. MySQL has significantly different syntax (backtick quoting, `AUTO_INCREMENT` vs `GENERATED ALWAYS AS IDENTITY`, `LIMIT` without `OFFSET` keyword, etc.).

3. **`SqlColumnType` mixes abstract and concrete types** — `BYTEA` is PostgreSQL-specific, `BLOB` is more generic. Consider whether `BYTEA` should be in the generic enum or only mapped via `PostgresDialect.mapColumnType()`. Similarly, `JSONB` is Postgres-specific.

4. **`SqlRenderer.parameterPlaceholder(int index)`** — the index parameter suggests support for positional placeholders (`$1`, `$2` in PostgreSQL), but `SqlDefaultRenderer` always returns `?`. Document whether this is for future positional parameter support or for dialect-specific behavior.

5. **Missing dialect auto-detection** — consider a `SqlDialect.fromUrl(String jdbcUrl)` that infers the dialect from the JDBC URL prefix (`jdbc:postgresql:`, `jdbc:mysql:`, etc.).

---

## 4. Expression & Function System — ⭐⭐⭐⭐⭐ (5/5)

### Strengths
- **Extremely comprehensive scalar function coverage**: `SqlString` (20+ functions), `SqlMath` (15+ functions), `SqlDate` (15+ functions), `SqlJson`, `SqlArray`, `SqlHash`, `SqlRegex`.
- **`SqlExpression<T>` unifies columns, functions, and aliases** under a common type that supports conditions and ordering.
- **`SqlWindowExpression<T>`** correctly extends `SqlExpression<T>` with `over()` / `over(clause)` — aggregates can be used both as plain aggregates and as window functions.
- **`SqlWindowClause`** with `partitionBy()`, `orderBy()`, `frame()`, `rows()`, `range()` — and full frame bound support (`UNBOUNDED PRECEDING`, `n PRECEDING`, `CURRENT ROW`, `n FOLLOWING`, `UNBOUNDED FOLLOWING`).
- **Type-specific ops** on columns (`string()`, `numeric()`, `temporal()`) — avoids polluting the base column interface.

### Suggestions
1. **`SqlFunction.cast()` takes `Class<T>` but needs dialect mapping** — the implementation will need to convert `Class<T>` to a SQL type name. Consider accepting `SqlColumnType` as an alternative overload: `cast(expr, SqlColumnType.INTEGER)`.

2. **Static utility classes (e.g., `SqlMath`, `SqlString`) have no access to the dialect/renderer** — since functions like `RANDOM()` vs `RAND()`, `TRUNCATE()` vs `TRUNC()`, `LENGTH()` vs `CHAR_LENGTH()` are dialect-dependent, how will the static methods know which SQL to generate? These methods should either:
    - Return an `SqlExpression` implementation that defers rendering to `toSql(SqlRenderer)`, or
    - Accept a renderer parameter.

   This is likely already planned (since they return `SqlExpression` which has `toSql(renderer)`), but it should be documented explicitly as an architecture note.

3. **`SqlNumericOps<T>` is very thin** — only has `between()`, which already exists on `SqlExpression`. Consider adding: `isPositive()`, `isNegative()`, `isZero()`, `moduloEquals(divisor, remainder)`.

4. **`SqlTemporalOps` is also very thin** — only `withinLast(Duration)`. Consider adding: `before(Instant)`, `after(Instant)`, `withinNext(Duration)`, `onDate(LocalDate)`.

---

## 5. Table & Column System — ⭐⭐⭐⭐ (4/5)

### Strengths
- **`SqlTable<T>` is a pure schema definition** — no data access on the table itself, which is clean.
- **Specialized column types**: `SqlForeignColumn`, `SqlVersionColumn`, `SqlCreationColumn`, `SqlUpdateColumn` — each carries semantic meaning that the framework can act on automatically.
- **Optimistic locking** via `SqlVersioned<T>` + `SqlVersionColumn` with auto-increment on UPDATE is well-designed.
- **Audit columns** (`SqlCreationColumn`, `SqlUpdateColumn`) auto-populated from `SqlAuditContext` — smart.
- **Entity lifecycle listeners** on `SqlTable` — clean observer pattern.

### Suggestions
1. **`SqlTable<T>` lacks a way to define composite primary keys** at the API level. The migration `SqlTableBuilder.primaryKey(String...)` supports composites, but the query-side `SqlTable` doesn't expose a primary key definition. This could matter for `findById()` if added later.

2. **`SqlTable.column()` creates columns on-the-fly** — there's no compile-time guarantee that column definitions are consistent across call sites. Consider encouraging a pattern where columns are declared as `static final` fields:
   ```java
   SqlTable<User> USERS = SqlTable.of("users", User.class);
   SqlColumn<String> EMAIL = USERS.column("email", String.class);
   ```
   This is likely the intended usage pattern; adding an example to the Javadoc would help.

3. **`SqlForeignColumn` doesn't expose ON DELETE/ON UPDATE actions** (CASCADE, SET NULL, RESTRICT, etc.). These are critical for foreign key relationships and should be part of the column definition or at least configurable.

4. **No `SqlColumn.primaryKey()` marker** — unlike `SqlVersionColumn` or `SqlCreationColumn`, there's no way to mark a column as the primary key in the query-side API. This would be useful for auto-generating `findById()` or identity-based upsert logic.

---

## 6. Transaction System — ⭐⭐⭐⭐ (4/5)

### Strengths
- **Full transaction lifecycle**: `beginTransaction()`, `commit()`, `rollback()`, savepoints, read-only mode, timeout, isolation levels.
- **Audit context integration** — `beginTransaction(SqlAuditContext)`.
- **`SqlSavepoint`** with `release()` and `rollbackTo()` — proper savepoint semantics.
- **Isolation levels** as a clean enum.

### Suggestions
1. **`SqlTransaction` doesn't implement `AutoCloseable`** — this is a significant omission. Transactions should be usable in try-with-resources to ensure rollback on exceptions:
   ```java
   try (var tx = db.beginTransaction()) {
       // ...
       tx.commit();
   } // auto-rollback if not committed
   ```

2. **Missing `SqlTransaction.isActive()` / `isCommitted()` / `isRolledBack()`** — state introspection is important for error handling and retry logic.

3. **No transactional `execute()` helper** — consider a convenience method like:
   ```java
   db.inTransaction(tx -> {
       tx.from(users).insert(user);
       return result;
   });
   ```
   This would auto-commit on success and auto-rollback on exception, reducing boilerplate.

---

## 7. Migration System — ⭐⭐⭐⭐ (4/5)

### Strengths
- **Programmatic migrations** with `up()` / `down()` — avoids raw SQL files.
- **Rich DSL**: `createTable`, `dropTable`, `renameTable`, `addColumn`, `dropColumn`, `renameColumn`, `alterColumn`, indexes, constraints, sequences.
- **`SqlMigrationRunner`** with `migrate()`, `migrateTo()`, `rollback()`, `rollbackTo()`, `status()`, `dryRun()`.
- **Schema introspection** via `SqlSchema.fromDatabase()` and **schema diffing** via `SqlSchemaDiff.between()`.
- **Checksums** on `SqlMigrationInfo` for drift detection.
- **`SqlMigrationStatus`** enum (PENDING, APPLIED, ROLLED_BACK) — clean state tracking.

### Suggestions
1. **No migration ordering mechanism beyond `version()` string** — how are versions compared? Lexicographic? Semantic versioning? Consider requiring a numeric or timestamp-based version, or documenting the comparison strategy.

2. **`SqlMigration` lacks a `checksum()` method** — `SqlMigrationInfo` has `checksum()` but it's not clear how the checksum is computed. If it's derived from the migration's operations, consider making this explicit.

3. **Missing data migrations** — the DSL only supports DDL (schema changes). Consider adding `execute(SqlDatabase db)` or similar for data migrations (e.g., backfilling a new column).

4. **No migration validation/lint** — consider a `validate()` method on `SqlMigrationRunner` that checks for:
    - Duplicate versions
    - Checksum mismatches with applied migrations
    - Orphaned applied migrations (applied but not registered)

5. **`SqlTableBuilder` doesn't support composite foreign keys** — `SqlColumnBuilder.references(table, column)` handles single-column FKs, but multi-column FKs are not supported in the migration DSL.

---

## 8. Connection Management — ⭐⭐⭐ (3/5)

### Strengths
- **Two connection strategies**: `SqlPooledDataSource` (pooled) and `SqlSingleConnectionDataSource` (simple) — good flexibility.
- **Standard `javax.sql.DataSource` interface** — compatible with existing JDBC infrastructure.

### Suggestions
1. **Custom connection pooling is risky** — implementing a production-grade connection pool is extremely complex (connection validation, leak detection, idle timeout, max lifetime, etc.). **Strongly consider depending on HikariCP** or similar instead of rolling your own `SqlPooledDataSource`. If the goal is zero dependencies, document the limitations clearly.

2. **Neither DataSource implements `Closeable` / `AutoCloseable`** — pooled connections need lifecycle management. `SqlPooledDataSource` should be closeable to shut down the pool.

3. **No connection validation/health-check configuration** — `SqlDatabase.health()` and `ping()` exist but there's no way to configure validation queries, test-on-borrow, or idle connection eviction on the DataSource.

4. **Credentials are passed as constructor parameters** — consider supporting `Properties` or a builder pattern for connection configuration (SSL, timeout, application name, etc.).

---

## 9. Async Query System — ⭐⭐⭐⭐ (4/5)

### Strengths
- **Mirror async API** for every query type — `SqlAsyncSelectQuery`, `SqlAsyncInsertQuery`, `SqlAsyncUpdateQuery`, `SqlAsyncDeleteQuery`, `SqlAsyncSelectProjectionQuery`.
- **Clean opt-in via `.async()`** — synchronous by default, async when needed.
- **Configurable executor** via `SqlDatabaseConfig.asyncExecutor()` and `asyncConnectionPoolSize()`.

### Suggestions
1. **Async queries should return `CompletableFuture<T>`** — verify this is the case in all async terminal methods. The Javadoc mentions it but the interfaces weren't fully read.

2. **Missing reactive/streaming support** — for large result sets, consider `Flow.Publisher<T>` (Java 9+) or at minimum `CompletableFuture<Stream<T>>` with backpressure awareness.

3. **No async transaction support** — `SqlTransaction` is synchronous only. Consider whether async transactions are needed, or document that transactions must be synchronous.

---

## 10. Exception Hierarchy — ⭐⭐⭐⭐⭐ (5/5)

### Strengths
- **Rich, well-organized exception hierarchy**:
    - `SqlException` (base)
        - `SqlConnectionException`
        - `SqlDatabaseException`
        - `SqlTransactionException`
        - `SqlMappingException`
        - **Constraint violations**: `SqlConstraintViolationException` → `SqlUniqueConstraintViolationException`, `SqlForeignKeyViolationException`, `SqlNotNullViolationException`, `SqlCheckConstraintViolationException`
        - **Entity exceptions**: `SqlEntityException` → `SqlEntityNotFoundException`, `SqlStaleEntityException`, `SqlRelationshipNotLoadedException`
        - **Locking exceptions**: `SqlLockingException` → `SqlDeadlockException`, `SqlLockNotAvailableException`
        - **Query exceptions**: `SqlQueryException` → `SqlQueryTimeoutException`
- **Semantic exception types** enable precise catch blocks and retry logic.
- **`SqlStaleEntityException`** for optimistic locking failures — correct design.

### Suggestions
1. **Consider adding `SqlException.getSqlState()`** and `getSqlErrorCode()` — these are available from `java.sql.SQLException` and critical for programmatic error handling.

---

## 11. Audit System — ⭐⭐⭐⭐ (4/5)

### Strengths
- **`SqlAuditContext`** with `getCurrentUser()`, `setCurrentUser()`, `clear()`, and `withUser()` scoping.
- **`SqlTimestampSource`** (APPLICATION vs DATABASE) — lets users choose between app-level `Clock` and DB-level `NOW()`.
- **`SqlAuditEntry`** for tracking INSERT/UPDATE operations.

### Suggestions
1. **`SqlAuditContext` is mutable** — `setCurrentUser()` is inherently thread-unsafe if shared. Consider making it `ThreadLocal`-based internally, or documenting thread-safety requirements.

2. **`SqlAuditEntry` lacks a `forDelete()` factory** — DELETE operations should also be auditable.

3. **`SqlAuditEntry` interface is very sparse** — it has no accessors beyond the factory methods. It should expose: `getEntity()`, `getOperation()` (CREATE/UPDATE/DELETE), `getTimestamp()`, `getUser()`.

---

## 12. Mapping System — ⭐⭐⭐ (3/5)

### Strengths
- **`SqlColumnMapping`** for bidirectional snake_case ↔ camelCase conversion — essential for Java record mapping.
- **`SqlRecordMapper<T>`** for ResultSet → record conversion.

### Suggestions
1. **Very thin for the scope of the project** — no annotation-based mapping (`@Column`, `@Table`), no relationship mapping (OneToMany, ManyToOne), no lazy loading, no embeddable types. While keeping it simple is valid, the audit/version columns already imply a form of entity mapping. Consider at minimum:
    - Auto-discovery of record components → column mappings
    - `@SqlColumn("custom_name")` annotation for overrides

2. **`SqlRecordMapper.map(Object[])` is stringly-typed** — using `Object[]` loses all type safety. Consider a `SqlResultRow` abstraction that provides typed access: `row.get(column)`.

3. **No custom type converters** — how are `UUID`, `Instant`, `enum` types, or JSON columns mapped to/from Java objects? A `SqlTypeConverter<J, D>` interface would be valuable.

---

## 13. PostgreSQL Extensions — ⭐⭐⭐⭐ (4/5)

### Strengths
- **Dedicated Postgres package** with `PostgresTable`, `PostgresColumn`, `PostgresQueryProvider`, and all CRUD query variants.
- **`PostgresArrayOps`**, **`PostgresJsonOps`**, **`PostgresStringOps`** — dialect-specific operator support.
- **Proper OO extension** — `PostgresTable extends SqlTable`, `PostgresColumn extends SqlColumn`.

### Suggestions
1. **Consider `PostgresInsertQuery.onConflictDoNothing()`** — PostgreSQL's `ON CONFLICT DO NOTHING` is a common pattern not clearly exposed.

2. **Missing PostgreSQL-specific features**: `LISTEN/NOTIFY`, `COPY`, `EXPLAIN ANALYZE`, advisory locks, `pg_advisory_lock()`. Not all need to be included, but `LISTEN/NOTIFY` is commonly needed.

---

## Summary

| Subsystem | Rating | Key Strength | Top Suggestion |
|-----------|--------|-------------|----------------|
| Architecture | ⭐⭐⭐⭐ | Clean separation of concerns | Remove duplicate factory method |
| Query System | ⭐⭐⭐⭐⭐ | Comprehensive type-safe API | Add `DELETE ... RETURNING` |
| Dialect/Renderer | ⭐⭐⭐⭐ | Feature flags + syntax delegation | Fix Postgres renderer, add MySQL |
| Expressions/Functions | ⭐⭐⭐⭐⭐ | Massive function coverage | Document dialect rendering strategy |
| Table/Column | ⭐⭐⭐⭐ | Semantic column types | Add ON DELETE/UPDATE to FK columns |
| Transactions | ⭐⭐⭐⭐ | Full lifecycle + savepoints | Implement `AutoCloseable` |
| Migrations | ⭐⭐⭐⭐ | Programmatic DSL + schema diff | Define version ordering strategy |
| Connections | ⭐⭐⭐ | Standard DataSource | Use HikariCP instead of custom pool |
| Async | ⭐⭐⭐⭐ | Clean opt-in via `.async()` | Consider reactive streams |
| Exceptions | ⭐⭐⭐⭐⭐ | Precise semantic hierarchy | Add SQL state/error code |
| Audit | ⭐⭐⭐⭐ | Context-scoped user tracking | Thread-safety + forDelete() |
| Mapping | ⭐⭐⭐ | Record-based approach | Type converters + auto-mapping |
| Postgres | ⭐⭐⭐⭐ | Proper dialect extensions | ON CONFLICT DO NOTHING |

### Overall Rating: ⭐⭐⭐⭐ (4/5) — Very Strong Concept

**Top 5 Priority Suggestions:**
1. **Make `SqlTransaction` implement `AutoCloseable`** — this is a correctness issue; transactions that aren't closed leak connections.
2. **Don't build a custom connection pool** — use HikariCP or document severe limitations.
3. **Fix `PostgresDialect` to use `PostgresSqlRenderer`** — currently returns the default renderer.
4. **Add `DELETE ... RETURNING` to `SqlDeleteQuery`** — inconsistent with INSERT and UPDATE.
5. **Expand the mapping system** — type converters and auto-mapping from records are essential for practical use.

The concept demonstrates deep knowledge of SQL semantics, strong API design instincts, and excellent documentation discipline. The Javadoc quality across all files is exceptional — every method is documented with the generated SQL, which is rare and valuable. This is a well-thought-out foundation ready for implementation.
