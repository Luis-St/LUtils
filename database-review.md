# Database Concept Review

## Overview

The `net.luis.utils.io.database` package defines a **conceptual SQL database abstraction layer**.
It is currently a pure API design — nearly all types are interfaces or abstract classes, and methods with bodies mostly throw `UnsupportedOperationException` as placeholders.
This review evaluates the feature set and API design of the concept.

---

## Package Structure

```
net.luis.utils.io.database/
├── audit/              – Audit trail (context, entries, operations)
├── condition/          – SQL conditions (WHERE clauses)
├── connection/         – Connection management
├── dialect/            – Dialect abstraction + PostgreSQL stub
│   └── postgres/ops/
├── exception/          – Rich exception hierarchy
│   ├── constraint/     – Constraint violation exceptions
│   ├── entity/         – Entity-related exceptions
│   ├── locking/        – Lock/deadlock exceptions
│   └── query/          – Query execution exceptions
├── function/           – SQL function wrappers
│   ├── scalar/         – Scalar functions (string, math, date, json, array, hash, regex)
│   └── window/         – Window functions & frame bounds
├── index/              – Index definitions
├── listener/           – Entity lifecycle listeners
├── mapping/            – Result mapping, type conversion, column naming
├── migration/          – Schema migration support
├── query/              – Query builders (select, insert, update, delete)
│   ├── async/          – Async query equivalents
│   └── row/            – Typed tuple rows (SqlRow2–SqlRow16)
├── renderer/           – Dialect-specific SQL rendering
├── sequence/           – Sequence definitions
├── table/              – Table, column, and key definitions
│   └── ops/            – Type-specific column operations (string, numeric, temporal)
└── transaction/        – Transactions, async transactions, savepoints
```

---

## Core Design

### Entry Points

| Type | Role |
|------|------|
| `SqlDatabaseConfig` | Builder for configuring and creating a database instance |
| `SqlDatabase` | Main facade — health checks, query providers, transactions, audit |
| `SqlTable<T>` | Schema definition — columns, keys, listeners |
| `SqlQueryProvider<T>` | DDL + DML operations on a table |

### Type Hierarchy

```
SqlRenderable
  ├── SqlCondition
  ├── SqlOrderable
  │   └── SqlExpression<T>
  │       ├── SqlColumn<T>
  │       │   ├── SqlPrimaryKeyColumn<T, V>
  │       │   ├── SqlVersionColumn<T, V>
  │       │   ├── SqlForeignColumn<T, R>
  │       │   ├── SqlCreationColumn<T, V>
  │       │   └── SqlUpdateColumn<T, V>
  │       └── SqlWindowExpression<T>
  ├── SqlWindowFrame
  ├── SqlFrameBound
  └── SqlWindowClause

SqlException
  ├── SqlConnectionException
  ├── SqlTransactionException
  ├── SqlDatabaseException
  ├── SqlMappingException
  ├── SqlQueryException
  │   └── SqlQueryTimeoutException
  ├── SqlConstraintViolationException
  │   ├── SqlCheckConstraintViolationException
  │   ├── SqlNotNullViolationException
  │   ├── SqlUniqueConstraintViolationException
  │   └── SqlForeignKeyViolationException
  ├── SqlLockingException
  │   ├── SqlDeadlockException
  │   └── SqlLockNotAvailableException
  └── SqlEntityException
      ├── SqlEntityNotFoundException
      ├── SqlStaleEntityException
      └── SqlRelationshipNotLoadedException

AutoCloseable
  ├── SqlDatabase
  ├── SqlTransaction
  └── SqlAsyncTransaction
```

---

## Strengths

### 1. Comprehensive Type-Safe API
The generic type system is used consistently throughout. `SqlColumn<T>`, `SqlExpression<T>`, and the typed tuple rows (`SqlRow2<T1,T2>` through `SqlRow16`) provide compile-time type safety for query results. This prevents common errors like accessing columns with the wrong type.

### 2. Clean Separation of Concerns
Each sub-package has a clear, focused responsibility:
- Schema definition (`table/`) is separate from query building (`query/`)
- SQL rendering (`renderer/`) is separate from dialect capabilities (`dialect/`)
- Audit concerns (`audit/`) are isolated from core query logic

### 3. Rich Exception Hierarchy
The exception hierarchy is well-structured with specific exception types for different failure modes (constraint violations, locking issues, entity problems, timeouts). This allows callers to handle errors granularly — e.g., catching `SqlUniqueConstraintViolationException` specifically for upsert-like logic.

### 4. Dialect Abstraction
The `SqlDialect` + `SqlRenderer` + `SqlDialectFeatures` trio is a solid pattern:
- `SqlDialect` identifies and configures the database
- `SqlRenderer` handles dialect-specific SQL generation
- `SqlDialectFeatures` advertises capabilities, enabling the query layer to adapt

### 5. Fluent Builder APIs
Query builders, conditions, and transactions use fluent method chaining consistently, making the intended usage readable:
```java
db.from(users).select()
  .where(users.column("age", Integer.class).greaterThan(18))
  .orderBy(users.column("name", String.class).asc())
  .limit(10)
```

### 6. Async Support
Every synchronous query operation has an async counterpart returning `CompletableFuture`. The async transaction API mirrors the sync one. This is a pragmatic design that avoids forcing all consumers into one paradigm.

### 7. Extensive SQL Function Coverage
The scalar function library is impressively comprehensive — string, math, date/time, JSON, array, hash, and regex operations are all covered with type-safe wrappers. Window functions with full frame specification support (`ROWS`, `RANGE`, `GROUPS`, bounds) are equally thorough.

### 8. Audit Trail Built In
Audit support (context, entries, timestamp sources) is integrated at the database and transaction level rather than bolted on as an afterthought. This is the right architectural choice.

---

## Feature Review

### Query System

The query API is the centerpiece of the concept. `SqlQueryProvider<T>` exposes a complete set of operations — DDL (create, drop, truncate), DML (select, insert, update, delete), index management, and sequence operations — all accessed through `SqlDatabase.from(table)`.

**Select queries** are well-designed with typed projections. The overloaded `select()` methods returning `SqlRow2<T1,T2>` through `SqlRow16` provide compile-time type safety for multi-column results, while the varargs `select(SqlExpression<?>...)` fallback handles dynamic cases. The `SqlSelectQuery` adds pessimistic locking support (`forUpdate()`, `skipLocked()`, `noWait()`), which is essential for real-world concurrent access patterns.

**Insert queries** cover all common patterns: single insert, batch insert with configurable batch size, upsert with conflict resolution, insert-or-ignore, and insert-from-select. This is a complete set that should handle most use cases without forcing users to drop down to raw SQL.

**Suggestion:** Consider adding a `returning()` clause to insert/update/delete queries (the renderer already has `returningSyntax()`), which is increasingly common and avoids a separate select after mutation.

### Condition & Expression System

The `SqlCondition` / `SqlExpression<T>` hierarchy is clean and expressive. Conditions compose via `and()`, `or()`, and `not()` with static `allOf()` / `anyOf()` combinators. Expressions provide all standard comparisons (`equalTo`, `greaterThan`, `between`, `isNull`, etc.) and flow naturally into ordering (`asc()`, `desc()`, `nullsFirst()`, `nullsLast()`).

The type-specific operations on columns (`SqlStringOps`, `SqlNumericOps`, `SqlTemporalOps`) are a particularly nice touch — `column.string().startsWith("foo")` is more discoverable than a flat API with dozens of methods.

**Suggestion:** Consider adding `SqlCondition.none()` and `SqlCondition.always()` as identity elements for dynamic condition building (e.g., starting with `always()` and chaining `.and(...)` conditionally).

### Scalar Functions

The scalar function library is comprehensive and well-organized across focused utility classes:

| Class | Coverage |
|-------|----------|
| `SqlFunction` | General: `coalesce`, `nullif`, `cast`, `greatest`, `least`, `caseWhen` |
| `SqlString` | 20+ string operations including `groupConcat` variants |
| `SqlMath` | Full math suite including trigonometry and bitwise operations |
| `SqlDate` | 30+ date/time operations including extraction, arithmetic, formatting |
| `SqlJson` | JSON manipulation: set, insert, replace, remove, merge, type inspection |
| `SqlArray` | Array operations: append, contains, sort, overlap checks |
| `SqlHash` | Cryptographic hashes: MD5, SHA family, CRC32 |
| `SqlRegex` | Pattern matching and replacement |

All functions return `SqlExpression<T>` or `SqlWindowExpression<T>`, so they compose naturally with conditions and ordering. This is the right design — functions are first-class expressions, not a separate subsystem.

**Suggestion:** `SqlJson` could benefit from a path-based extraction method (e.g., `extract(SqlExpression<?> json, String path)`) since JSON path access is the most common JSON operation in queries.

### Window Functions

Window function support is thorough. `SqlWindow` provides all standard window functions (`rowNumber`, `rank`, `denseRank`, `ntile`, `lag`, `lead`, `percentRank`, `cumeDist`, `firstValue`, `lastValue`, `nthValue`), and the framing API is complete:

- `SqlWindowClause` — partition by, order by, frame specification
- `SqlWindowFrame` — `ROWS`, `RANGE`, `GROUPS` frame types
- `SqlFrameBound` — `UNBOUNDED PRECEDING`, `n PRECEDING`, `CURRENT ROW`, `n FOLLOWING`, `UNBOUNDED FOLLOWING`

The `SqlWindowExpression<T>` extending `SqlExpression<T>` with an `over()` method is elegant — it means aggregate functions can be used as both regular aggregates and window functions through the same type.

### Transaction Management

Transactions support isolation levels, timeouts, read-only mode, and savepoints — covering the features most applications need. The fluent configuration pattern is clean:
```java
db.beginTransaction()
  .isolation(SqlIsolationLevel.REPEATABLE_READ)
  .readOnly()
  .timeout(Duration.ofSeconds(30))
```

The `SqlDatabase.inTransaction()` convenience method with auto-commit/rollback semantics is practical for simple cases. Savepoints with named `rollbackTo()` enable partial rollback patterns.

**Suggestion:** Consider whether transaction propagation semantics (REQUIRED, REQUIRES_NEW, NESTED) are needed. If the design intentionally avoids propagation to keep things simple, that's a valid choice — but it should be documented as a deliberate decision.

### Async Support

Every synchronous operation has an async counterpart returning `CompletableFuture`:
- `SqlAsyncQueryProvider<T>` mirrors `SqlQueryProvider<T>`
- `SqlAsyncTransaction` mirrors `SqlTransaction`
- `SqlAsyncSelectQuery<T>`, `SqlAsyncInsertQuery<T>`, etc.

The config accepts an `Executor` and connection pool size for async operations, giving the caller control over thread management. Mirroring the sync API 1:1 means there's no conceptual overhead when switching between sync and async — users learn one API shape.

### Dialect & Rendering

The three-part dialect system is well-structured:

- **`SqlDialect`** — identity, type mapping (Java ↔ SQL), renderer creation
- **`SqlRenderer`** — dialect-specific SQL syntax (quoting, placeholders, limit/offset, upsert, returning, etc.)
- **`SqlDialectFeatures`** — capability flags (18 boolean methods covering arrays, JSON, CTEs, window functions, etc.)

`SqlRenderable.toSql(SqlRenderer)` threads the renderer through the entire expression tree, so every element produces dialect-correct SQL. The `SqlRendered` record bundling SQL with bound parameters is a clean abstraction for prepared statement execution.

**Suggestion:** `SqlDialectFeatures` uses individual boolean methods, which requires interface changes when adding new features. An enum-based approach (`boolean supports(SqlFeature feature)`) would be more extensible. Alternatively, default methods returning `false` would allow adding features without breaking existing dialect implementations.

### Table & Column Model

`SqlTable<T>` provides a complete schema definition API with specialized column types:

| Column Type | Purpose |
|-------------|---------|
| `SqlColumn<T>` | Standard typed column |
| `SqlPrimaryKeyColumn<T, V>` | Primary key |
| `SqlCompositePrimaryKey<T>` | Composite primary key |
| `SqlForeignColumn<T, R>` | Foreign key with referential actions |
| `SqlVersionColumn<T, V>` | Optimistic locking version |
| `SqlCreationColumn<T, V>` | Audit creation timestamp |
| `SqlUpdateColumn<T, V>` | Audit update timestamp |

Foreign keys include `onDelete` / `onUpdate` actions (`CASCADE`, `RESTRICT`, `SET_NULL`, `SET_DEFAULT`, `NO_ACTION`). The `SqlVersioned<T>` interface for entities with `version()` / `withVersion()` integrates cleanly with `SqlVersionColumn` and `skipVersionCheck()` on the query provider.

**Suggestion:** `SqlForeignColumn` defines relationships, and `SqlRelationshipNotLoadedException` exists in the exception hierarchy, which implies some form of lazy loading is planned. Consider defining the loading strategy (eager vs. lazy, join fetch API) as part of the concept — it has significant implications for how entities are modeled and how queries are built.

### Entity Mapping

`SqlRecordMapper<T>` and `SqlResultRow` handle the mapping between SQL results and Java objects. `SqlResultRow` provides access by column reference, index, or name — covering all common patterns. `SqlTypeConverter<J,D>` enables custom type conversions registered through the database config.

`SqlColumnMapping` handles `snake_case` ↔ `camelCase` naming conversion, following the common convention for Java-to-SQL name mapping.

**Suggestion:** Consider making the naming strategy pluggable (not all projects use `snake_case` columns). Also, clarify whether `SqlRecordMapper<T>` requires `T` to be a Java record — if so, this could be enforced with a type bound.

### Audit System

Audit support is integrated at the infrastructure level:

- `SqlAuditContext` — carries user identity, set on the database or per-transaction
- `SqlAuditEntry<T>` — records entity, operation (CREATE/UPDATE/DELETE), timestamp, and user
- `SqlTimestampSource` — application-side vs. database-side timestamps
- `SqlCreationColumn` / `SqlUpdateColumn` — automatic timestamp columns
- `SqlEntityListener<T>` — lifecycle hooks (before/after insert, update, delete)

This is well-designed. Audit context flows through transactions, timestamps are configurable, and entity listeners provide extensibility points without polluting the core API.

### Exception Hierarchy

The exception hierarchy is granular and well-organized:

- **Connection** — `SqlConnectionException`
- **Queries** — `SqlQueryException`, `SqlQueryTimeoutException`
- **Constraints** — `SqlConstraintViolationException` with subtypes for check, not-null, unique, and foreign key violations
- **Locking** — `SqlLockingException` with deadlock and lock-unavailable subtypes
- **Entities** — `SqlEntityNotFoundException`, `SqlStaleEntityException` (version mismatch), `SqlRelationshipNotLoadedException`
- **Mapping** — `SqlMappingException`
- **Transactions** — `SqlTransactionException`

This granularity enables precise error handling — e.g., catching `SqlUniqueConstraintViolationException` for upsert logic, or `SqlStaleEntityException` for optimistic lock retries. The `SqlRetry` interface with `retryOn()` filtering complements this nicely.

### Pagination

`SqlPage<T>` provides a complete pagination model: content, total elements/pages, current page, and navigation (`hasNext`, `hasPrevious`, `fetchNext`, `fetchPrevious`). This is a self-contained cursor that handles the common pagination pattern without requiring the caller to manage offsets manually.

### Index & Sequence Support

Index operations (create, drop, list) and sequence operations (create, next value) are accessible through the query provider. `SqlIndexDefinition` and `SqlSequenceDefinition` provide the configuration, keeping these DDL features discoverable alongside the DML operations rather than hidden in a separate management API.

### Retry Mechanism

`SqlRetry` with configurable max retries, exponential backoff, and exception filtering (`retryOn(...)`) is a useful infrastructure feature. It integrates naturally with the exception hierarchy — e.g., retrying on `SqlDeadlockException` or `SqlConnectionException` while failing fast on constraint violations.

### Common Table Expressions

`CommonTableExpression` supports both regular and recursive CTEs with named queries. This enables complex query composition (hierarchical data traversal, multi-step aggregations) within the type-safe query builder framework.

---

## Architecture Assessment

| Aspect | Rating | Notes |
|--------|--------|-------|
| API Consistency | Strong | Naming, generics, and patterns are applied uniformly |
| Type Safety | Strong | Generics used well throughout; typed tuples and expressions compose cleanly |
| Separation of Concerns | Strong | Clean package boundaries with focused responsibilities |
| Feature Completeness | Strong | Covers CRUD, conditions, functions, window functions, transactions, async, audit, pagination, CTEs, retry |
| Extensibility | Moderate | Dialect/renderer pattern is good; `SqlDialectFeatures` boolean methods are somewhat rigid |
| Composability | Strong | Expressions, conditions, and functions share a common type hierarchy and compose naturally |

---

## Summary

The database concept presents a well-designed, feature-rich SQL abstraction layer. The feature set covers the full spectrum of SQL operations — from basic CRUD through advanced window functions, JSON/array operations, and CTEs — with a consistent, type-safe API throughout.

Key design strengths:
- **Unified expression model** — columns, functions, and aggregates all flow through `SqlExpression<T>`, making them composable in conditions, ordering, and projections
- **Complete async mirroring** — every sync operation has an async counterpart with no conceptual overhead
- **Integrated audit trail** — audit context flows through the database and transaction layers
- **Granular exception hierarchy** — enables precise error handling and clean retry logic
- **Dialect independence** — the renderer/dialect/features trio cleanly separates SQL generation from query building

The suggestions above are refinements to an already solid design — adding identity conditions, a `returning()` clause, JSON path extraction, pluggable naming strategies, and more extensible dialect features would further strengthen the concept.
