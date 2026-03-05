# Analysis Report: `net.luis.utils.io.database` SQL Database Concept

---

## 1. Overview

The database package is a type-safe, synchronous SQL abstraction library built on JDBC. It follows a clean conceptual model: **table definitions are pure schema descriptors** (`SqlTable`/`SqlColumn`) decoupled from execution, and **query providers** are obtained from a database or transaction at runtime. The library covers DDL, DML, transactions, migrations, auditing, locking, and dialect-specific extensions. It is not an ORM — it is a structured query builder over records.

All interfaces are stubs (`throw new UnsupportedOperationException()`), confirming this is a design concept awaiting implementation.

---

## 2. Features Present and Well-Designed

### 2.1 Table Definition System
- `SqlTable<T>` — pure schema descriptor, factory methods for all column types
- Rich column hierarchy: `SqlColumn`, `SqlPrimaryKeyColumn`, `SqlForeignColumn`, `SqlVersionColumn`, `SqlCreationColumn`, `SqlUpdateColumn`
- `SqlCompositePrimaryKey` for multi-column primary keys
- Column constraints encoded at definition time via `SqlColumnBuilder` (not null, unique, default, auto-increment, check, FK reference)

### 2.2 Query System
- Full CRUD + DDL on `SqlQueryProvider`: `create`, `createIfNotExists`, `drop`, `dropIfExists`, `truncate`, `exists`, index management
- `SqlSelectQueryBase` with a complete fluent API: `WHERE`, `WHERE EXISTS`/`NOT EXISTS`, all four JOIN types, `GROUP BY`, `HAVING`, `ORDER BY`, `LIMIT`, `OFFSET`, `DISTINCT`, `UNION`/`UNION ALL`/`INTERSECT`/`EXCEPT`, `WITH` (CTEs), subquery support
- `SqlSelectQuery<T>` — full-entity select with pessimistic locking (`FOR UPDATE`, `SKIP LOCKED`, `NOWAIT`)
- `SqlSelectProjectionQuery<T>` — typed projections for 1–16 columns returning `SqlRow2`–`SqlRow16` tuples, plus `fetchAs(Class<R>)` / `fetchOneAs` / `streamAs` / `fetchPageAs` for DTO mapping
- `SqlUpdateQuery` — `set()`, `set(expression)`, `increment()`, `setNow()`, `execute()`, `returning()`
- `SqlInsertQuery` — `execute()`, `returning()`, `fetchInserted()`; batch insert with configurable `batchSize`; `upsert()`, `insertOrIgnore()`, `insertFromSelect()`
- `SqlDeleteQuery` — `where()`, `execute()`, `returning()`
- `skipVersionCheck()` on `SqlQueryProvider` to suppress automatic optimistic-lock clauses for bulk operations

### 2.3 Transaction Management
- Full ACID support: `beginTransaction()`, `commit()`, `rollback()`, `rollbackTo(savepoint)`, `savepoint()`
- Auto-rollback on `close()` (try-with-resources safe)
- `readOnly()`, `timeout()`, `isolation()`, `propagation()` on `SqlTransaction`
- All seven Spring-style `SqlPropagation` behaviors (REQUIRED, REQUIRES_NEW, NESTED, SUPPORTS, NOT_SUPPORTED, MANDATORY, NEVER)
- Four standard `SqlIsolationLevel`s
- `inTransaction(Function)` helper on `SqlDatabase` for callback-style transactions

### 2.4 Migration System
- DSL-only migrations — no raw SQL allowed in `SqlMigration.up()`/`down()`
- `SqlMigrationBuilder` covers: create/drop/rename table, add/drop/rename/alter column, create/drop/rename index, add unique/FK/check/composite-PK constraint, drop constraint
- Version-ordered execution via `Version`
- Checksum computed at registration time (dry-run hash)
- `SqlMigrationRunner`: `migrate()`, `migrateTo()`, `rollback()`, `rollback(n)`, `rollbackTo()`, `status()`, `dryRun()`, `dryRunRollback()`
- `SqlMigrationInfo` exposes version, description, status, applied timestamp, checksum

### 2.5 Optimistic & Pessimistic Locking
- Optimistic: `SqlVersionColumn` causes automatic `WHERE version = ?` and `SET version = version + 1`; bypassed via `skipVersionCheck()`
- Pessimistic: `FOR UPDATE`, `SKIP LOCKED`, `NOWAIT` via `SqlLockableQuery` (on both entity and projection selects)
- Dedicated `SqlDeadlockException` and `SqlLockNotAvailableException`

### 2.6 Audit Framework
- `SqlAuditContext` (immutable, user identity) at database and transaction level
- `SqlCreationColumn` — automatically filled on INSERT
- `SqlUpdateColumn` — automatically filled on INSERT and UPDATE
- `SqlTimestampSource.APPLICATION` / `DATABASE`
- `SqlAuditEntry<T>` — tracks entity, operation (CREATE/UPDATE/DELETE), timestamp, user

### 2.7 Dialect System
- `SqlDialect` abstract base + `SqlDialectFeatures` for capability detection
- `SqlFeature` enum covers: RETURNING, SKIP_LOCKED, ARRAYS, JSONB, PARTIAL_INDEXES, CTE, WINDOW_FUNCTIONS, BOOLEAN_TYPE, UPSERT, SCHEMAS, GENERATED_COLUMNS, MERGE, LATERAL, JSON, TRUNCATE_CASCADE, TABLE_INHERITANCE, IDENTITY_COLUMNS
- `SqlColumnType` enum — 20 dialect-agnostic types (VARCHAR, TEXT, CHAR, BOOLEAN, SMALLINT, INTEGER, BIGINT, REAL, DOUBLE, DECIMAL, UUID, DATE, TIME, TIMESTAMP, TIMESTAMP_TZ, BLOB, BINARY, JSON, ARRAY, XML)

### 2.8 PostgreSQL Extensions
- `PostgresQueryProvider` with `truncateCascade()`
- `PostgresInsertQuery` with `onConflictDoNothing()`, `onConflictDoNothing(columns...)`, `onConflictDoUpdate(Consumer, columns...)`
- `PostgresSelectQuery` with `distinctOn(column)`
- PostgreSQL-specific operators: `PostgresStringOps`, `PostgresJsonOps`, `PostgresRegexOps`, `PostgresArrayOps`, `PostgresHashOps`

### 2.9 Type-Safe Column Operations
- `SqlStringOps`: `startsWith`, `contains`, `endsWith`, `like`, `equalToIgnoreCase`, `lengthGreaterThan`
- `SqlNumericOps`: `isPositive`, `isNegative`, `isZero`, `moduloEquals`
- `SqlTemporalOps`: `withinLast`, `withinNext`, `before`, `after`, `onDate`

### 2.10 Supporting Infrastructure
- `SqlCondition`: `allOf`/`anyOf`/`always`/`none`, `and`/`or`/`not` fluent chaining
- `SqlPage<T>`: bidirectional pagination with `fetchNext()`/`fetchPrevious()`
- `SqlRetry`: exponential backoff, configurable exception types, `execute(Supplier)` + `run(Action)`
- `SqlEntityListener<T>`: before/after INSERT/UPDATE/DELETE lifecycle hooks
- `SqlNamingStrategy`: Java↔SQL name conversion
- `SqlTypeConverter<J, D>`: pluggable Java↔JDBC type conversion
- `SqlSingleConnectionDataSource` + `SqlPooledDataSource`
- `SqlRenderable` / `SqlRendered` / `SqlRenderer` for SQL generation and inspection
- Complete exception hierarchy with SQL state, vendor code, bound SQL statement, and bound parameters

---

## 3. Missing or Incomplete Features

### 3.2 Notable Gaps

**Raw SQL escape hatch**
Neither the query DSL nor migrations provide any fallback to raw SQL. While this enforces safety and dialect portability, some legitimate operations cannot be expressed through the DSL (complex CTEs, stored procedure calls, generated column expressions, dialect-specific functions). A controlled escape hatch (e.g., `nativeQuery(String sql, Object... params)`) is conventional in such libraries.

**`FOR SHARE` locking**
`SqlLockableQuery` only defines `forUpdate()`. `FOR SHARE` (shared/read lock) is a standard SQL locking mode useful when multiple readers need protection against concurrent writes without blocking each other. It is absent despite `FOR UPDATE` being present.

**CROSS JOIN**
`SqlSelectQueryBase` defines `innerJoin`, `leftJoin`, `rightJoin`, `fullJoin` but not `crossJoin`. CROSS JOIN is standard SQL and occasionally needed.

**LATERAL JOIN**
`SqlFeature.LATERAL` is defined in the feature enum, signaling intent, but no `lateralJoin()` method exists in `SqlSelectQueryBase`. The feature flag is currently a dead declaration.

**Cursor/keyset pagination**
`SqlPage` and `fetchPage(page, pageSize)` implement offset-based pagination only. Keyset (cursor) pagination — where the next page is fetched using the last seen value of an ordered column — is significantly more efficient for large datasets and deep pages. It is absent.

**Generated columns in `SqlColumnBuilder`**
`SqlFeature.GENERATED_COLUMNS` is declared but `SqlColumnBuilder` has no `generated(expression)` method. There is no way to define a generated/computed column through the DSL.

**`SqlTable.getColumns()` — column enumeration**
`SqlTable` provides factory methods to create columns (`column()`, `primaryKeyColumn()`, etc.) but has no accessor to enumerate the columns that belong to a table. This prevents any generic, reflection-free operation over a table's schema (e.g., auto-generating INSERT projections, schema validation, documentation generation).

**`SqlColumn.getType()` method**
`SqlColumn<T>` is generic over the Java type `T`, but there is no `Class<T> getType()` method on the interface. Runtime type inspection of a column's Java class requires unchecked reflection or external bookkeeping.

**`SqlTableDiff` not connected to any API**
`SqlTableDiff` (and the referenced `SqlColumnChange`) is a well-structured interface tracking added, removed, and modified columns. However, no method in `SqlMigrationRunner` or `SqlDatabase` computes or returns a `SqlTableDiff`. The type exists in isolation and cannot be used. A `SqlMigrationRunner.diff()` method that compares the Java `SqlTable` definition against the live database schema would complete the picture.

**Audit log persistence**
`SqlAuditEntry<T>` models audit events (CREATE/UPDATE/DELETE) with entity, operation, timestamp, and user. However, there is no `SqlAuditTable`, `SqlAuditQueryProvider`, or any mechanism to persist these entries to a dedicated audit log table. The audit framework records what happened but provides no built-in way to store it.

---

### 3.3 Minor Issues

**`SqlDatabaseConfig` reference in `SqlTypeConverter` javadoc**
The Javadoc for `SqlTypeConverter` says: *"Register converters via `SqlDatabaseConfig`..."*. No `SqlDatabaseConfig` class exists; the actual registration API is `SqlDatabaseBuilder.registerConverter()`. This is a documentation inconsistency that will confuse implementors.

**`SqlPage` missing `pageSize()` accessor**
`SqlPage<T>` exposes `totalElements()`, `totalPages()`, `currentPage()`, `hasNext()`, `hasPrevious()` but not the page size. Consumers who need to re-issue a page request or display page size information must track the page size externally.

**`SqlRetry` not integrated with `SqlDatabase`**
`SqlRetry` is a standalone utility with no connection to `SqlDatabase` or `SqlTransaction`. There is no `db.withRetry(retry)` decorator or `tx.withRetry(retry)` integration. Users must wire retry logic manually around database calls.

**`SqlMigrationRunner` missing `validate()`**
There is no method to validate that all previously applied migrations still have matching checksums — only `status()` which returns info. A `validate()` method that throws on checksum mismatch would surface tampered or accidentally modified migrations explicitly.

**`SqlStringOps` asymmetry**
`SqlStringOps` has `lengthGreaterThan(int)` but no `lengthLessThan(int)` or `lengthEquals(int)`. These would be symmetric and equally useful.

**Typed projection `select()` overloads do not accept `SqlExpression`**
The 1–16 column typed `select(col1, col2, ...)` overloads are declared to accept `SqlColumn<?>` parameters only. The variadic `select(SqlExpression<?> ...)` overload that accepts aggregates and window function results loses type safety (returns `SqlSelectProjectionQuery<?>`). Aggregate or function results cannot be used with typed tuple returns.

**`SqlCondition.always()` / `none()` dialect dependency**
`SqlCondition.always()` and `none()` are static factory methods on the condition interface, but as stubs with no dialect context, it is unclear how they will be rendered in a dialect-agnostic way (e.g., `TRUE` vs `1=1`). This may be an implementation concern, but the concept provides no indication of how dialect rendering is invoked from condition factories.

---

## 4. Summary Table

| Category | Status |
|---|---|
| Schema definition (table/column types) | Complete |
| CRUD queries (SELECT, INSERT, UPDATE, DELETE) | Complete |
| SELECT clauses (WHERE, JOIN, GROUP BY, ORDER BY, etc.) | Complete |
| Set operations (UNION, INTERSECT, EXCEPT) | Complete |
| CTEs and subqueries | Complete |
| Optimistic locking (version columns) | Complete |
| Pessimistic locking (FOR UPDATE, SKIP LOCKED, NOWAIT) | Complete — missing `FOR SHARE` |
| Transaction management (savepoints, isolation, propagation) | Complete |
| Migrations (DDL DSL, versioning, checksums, dry run) | Complete — missing data migrations |
| Dialect abstraction + PostgreSQL extensions | Complete |
| Audit (context, columns, entries) | Partial — no persistence mechanism |
| Exception hierarchy | Complete |
| Pagination | Partial — offset only, no keyset |
| Entity lifecycle listeners | Complete |
| Type conversion and naming strategy | Complete |
| Column operations (string/numeric/temporal) | Mostly complete — minor asymmetries |
| Batch operations | Complete — INSERT, UPDATE, DELETE all support batching |
| Column-to-column conditions | Complete — expression-to-expression overloads on `SqlExpression<T>` |
| JOIN in UPDATE/DELETE | Complete — `SqlJoinable<Q>` shared interface; all four join types |
| Data migrations (DML in builder) | Complete — `queryProvider()` on `SqlMigrationBuilder` |
| Raw SQL escape hatch | **Missing** |
| `FOR SHARE` locking | **Missing** |
| CROSS JOIN / LATERAL JOIN | **Missing** |
| `SqlTableDiff` API connection | **Missing** |
| `SqlTable.getColumns()` | **Missing** |
| Generated column definition | **Missing** |
| `SqlColumn.getType()` accessor | **Missing** |

---

## 5. Conclusion

The concept is architecturally sound and substantially complete for the common case. The separation of schema definition from query execution, the dialect abstraction, the type-safe projection tuples, and the DSL-only migration system are all strong design choices. The previously critical gaps — **data migrations**, **batch UPDATE/DELETE**, **column-to-column conditions**, and **JOIN-based mutations** — have been addressed. The remaining issues are refinements rather than blockers.
