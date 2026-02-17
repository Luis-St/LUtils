# Database Concept Review

## Overall Impression

A comprehensive, type-safe SQL DSL with 82 interfaces/classes across 15 packages. The API surface is large but logically organized. The concept covers everything from basic CRUD to window functions, CTEs, JSON/array operations, migrations, auditing, pagination, retry logic, and entity lifecycle hooks.

## Strengths

### 1. Clean type hierarchy
`SqlRenderable` as the root for all SQL-generating elements is a strong foundation. The expression hierarchy (`SqlRenderable -> SqlOrderable -> SqlExpression -> SqlColumn`) gives compile-time safety at every level.

### 2. Dialect abstraction
The `SqlDialect` + `SqlDialectFeatures` split is well thought out. `SqlDialectFeatures` provides 22 granular capability flags (from `supportsReturning()` to `supportsIdentityColumns()`), giving query builders fine-grained control over what to emit natively vs. emulate. The `SqlDialectRegistry` auto-detecting dialect from JDBC URL prefix is a nice convenience. The `distinctOn()` method documenting that it falls back to window functions on non-PostgreSQL databases is a good example of thoughtful cross-dialect support.

### 3. Exception hierarchy
Very granular. `SqlStaleEntityException` carrying `entityType`, `entityId`, and `expectedVersion` is exactly the right level of detail. The separation of constraint violations into subtypes (unique, foreign key, not-null, check) makes catch blocks meaningful. All exceptions are unchecked (`RuntimeException`), keeping query code clean.

### 4. Fluent query API
The `SqlSelectQueryBase` -> `SqlSelectQuery` / `SqlSelectProjectionQuery` split is a good choice. Entity queries get locking (`forUpdate`, `skipLocked`, `noWait`), projection queries get mapping (`fetchAs`). The self-referencing generic `Q extends SqlSelectQueryBase<T, Q>` ensures the full API is preserved after each chained call. No unnecessary methods on either.

### 5. Transaction design
Thread-local `isInTransaction()` / `current()` / `requireActive()` statics, savepoints, isolation levels, read-only mode, timeouts, and auto-managed `inTransaction(action)` scopes — this covers real-world needs well.

### 6. Function library
The static utility classes (`SqlAgg`, `SqlString`, `SqlMath`, `SqlDate`, `SqlHash`, `SqlJson`, `SqlArray`, `SqlRegex`, `SqlWindow`) are well-categorized and extensive (~100+ functions).

### 7. Pagination
`SqlPage<T>` is well-designed with `content()`, `totalElements()`, `totalPages()`, `currentPage()`, `hasNext()`, `hasPrevious()`, and navigable `fetchNext()` / `fetchPrevious()` methods. Integrates cleanly into the query API via `fetchPage(page, pageSize)`.

### 8. Retry mechanism
`SqlRetry` provides a clean, composable API for resilient operations: `SqlRetry.withBackoff(maxRetries, initialDelay).retryOn(SqlDeadlockException.class).execute(action)`. Exponential backoff with configurable exception types is the right abstraction for database retry logic.

### 9. Entity lifecycle listeners
`SqlEntityListener<T>` implements the Observer pattern with six hooks (`before/afterInsert`, `before/afterUpdate`, `before/afterDelete`). Default empty implementations allow listeners to selectively override only the events they care about. Tables manage listener registration (`addListener` / `removeListener`).

### 10. Audit infrastructure
The audit system is well-structured across three types: `SqlAuditContext` provides scoped user tracking with `withUser(user, action)` that properly restores previous state, `SqlAuditEntry` offers `forCreate` / `forUpdate` factory methods, and `SqlTimestampSource` (APPLICATION vs DATABASE) controls where timestamps originate. `SqlDatabaseConfig` exposes `auditTimestampSource()` and `auditClock()` for testability.

### 11. Optimistic locking
The versioning system is now clearly documented: `SqlVersioned<T>` entities get automatic `WHERE version = ?` and `SET version = version + 1` injected by query builders that detect `SqlVersionColumn`. The `skipVersionCheck()` decorator on `SqlQueryProvider` provides a clean escape hatch. `SqlStaleEntityException` is thrown on version mismatch.

### 12. Immutable entity protocol
Both `SqlVersioned<T>` and `SqlAuditable<T>` use `withXxx()` methods returning new instances. This signals an immutable/copy-on-modify entity design (compatible with Java records), which is a strong choice for a database framework — it makes entities safe to pass around and avoids mutation-related bugs.

### 13. Index and sequence management
`SqlIndexDefinition` supports partial indexes (with optional `WHERE` condition), 10 index methods (BTREE, HASH, GIN, GIST, BRIN, etc.), and uniqueness constraints. `SqlSequenceDefinition` covers `startWith`, `incrementBy`, and `cache`. Both are managed directly through `SqlTable`.

### 14. Scoped view pattern
`SqlQueryProvider` supports composable scoping via the Decorator pattern: `table.from(db).withIn(tx).skipVersionCheck()` returns decorated views without mutating the original table reference. The `dialect()` method also enables dialect-specific extensions beyond the common API.

## Concerns & Suggestions

### 1. API Surface Size vs. Implementation Complexity

82 files of interfaces is a massive contract to fulfill. Each dialect needs to support every function in `SqlString`, `SqlMath`, `SqlDate`, etc. That's potentially hundreds of dialect-specific SQL translations.

**Suggestion:** Consider which functions are truly cross-dialect vs. which are dialect-specific (e.g., `SqlArray`, many `SqlJson` operations). You could tier the functions: core (all dialects must support), extended (some dialects), and dialect-specific. The `SqlDialectFeatures` mechanism has 22 flags which is good for structural features, but doesn't cover individual function support — a dialect might support JSON but not every operation in `SqlJson`.

### 2. SqlTable as SqlQueryProvider

`SqlTable<T>` extends `SqlQueryProvider<T>`, meaning tables are both DDL objects (create, drop, truncate, index/sequence management, listener registration) and query factories (select, insert, update, delete). This coupling means:

- A table always needs a database binding to do anything useful
- The `from(database)` / `withIn(transaction)` methods on `SqlQueryProvider` feel like contextual state that shouldn't live on a table definition

**Suggestion:** Consider separating table definition (schema, columns, indexes) from query building. Something like `database.from(table).select()` instead of `table.from(database).select()`. The current approach inverts the natural ownership — a database contains tables, not the other way around.

### 3. Async API Duplication

Every query type has sync + async variants (`fetch()` / `fetchAsync()`, `fetchOne()` / `fetchOneAsync()`, `count()` / `countAsync()`, etc.). On `SqlSelectQueryBase` alone this doubles 8 fetch methods to 16.

**Suggestion:** Consider a pattern like `query.async().fetch()` that returns an async wrapper, or just rely on `CompletableFuture.supplyAsync(() -> query.fetch(), executor)` at the call site. This would halve the query interface sizes.

### 4. Migration System — Thin on Detail

`SqlMigration`, `SqlSchema`, `SqlSchemaDiff`, `SqlTableDiff` are defined but remain underdeveloped relative to the rest. `SqlMigration` has `diff()`, `pending()`, `version()`, `description()`, and extends `SqlRenderable`, but there's no rollback support, no dry-run mode, no migration tracking table concept, and schema diffing (column renames vs. drop+add, data-dependent migrations) is extremely hard to get right.

**Suggestion:** Either flesh this out significantly (versioned migration tracking table, rollback support, dry-run mode, migration ordering/dependency graph) or consider dropping it from v1 and recommending Flyway/Liquibase integration instead. Half-implemented migration tooling is dangerous.

### 5. Connection Pooling

`SqlPooledDataSource` and `SqlSingleConnectionDataSource` are provided. Connection pooling is notoriously difficult (leak detection, connection validation, eviction, metrics, idle timeout, max lifetime).

**Suggestion:** Unless there is a strong reason to build your own, consider using HikariCP as the default pool and providing a thin adapter. `SqlSingleConnectionDataSource` makes sense for testing/simple use cases.

### 6. SqlAuditable Design

`SqlAuditable<T>` has getters returning `null` by default and `withXxx` methods throwing `UnsupportedOperationException` by default. This means an entity can claim to be auditable but silently ignore or crash on audit operations. The surrounding audit infrastructure (`SqlAuditContext`, `SqlAuditEntry`, `SqlTimestampSource`) is well-designed, but the entity-side contract is weak.

**Suggestion:** Make `SqlAuditable` a stronger contract. If an entity implements it, it should be required to support all operations. Consider splitting into `SqlCreationAuditable` and `SqlUpdateAuditable` if partial auditing is needed. An entity that only tracks `createdAt` but not `updatedBy` should express that at the type level, not via runtime exceptions.

### 7. Missing: Entity Mapping

The concept has queries returning `T` (entity type) but there's no visible ORM/mapping layer — no annotations like `@Column`, `@Table`, no `ResultSet -> Entity` mapping interface. How does `select().fetch()` know how to turn rows into `T`? `SqlSelectProjectionQuery.fetchAs(Class<R>)` implies some kind of constructor/factory matching, but this isn't defined.

**Suggestion:** This is a critical gap. You'll need either:
- Annotation-based mapping (like JPA)
- A `SqlRowMapper<T>` functional interface
- Convention-based mapping (column names -> field names, possibly leveraging record component matching)

This decision will significantly affect the rest of the API, so it's worth deciding early.

### 8. SqlRenderable & Parameter Binding

Everything implements `toSql(SqlDialect)`, and `SqlSelectQueryBase.getParameters()` exposes bound parameters. However, only select queries expose parameters — `SqlInsertQuery`, `SqlUpdateQuery`, and `SqlDeleteQuery` don't have a visible `getParameters()`. There's also no `SqlRendered` type that bundles SQL + parameters together, meaning callers must coordinate two separate calls.

**Suggestion:** Define a `SqlRendered` record (e.g., `record SqlRendered(String sql, List<Object> parameters)`) and add `toRendered(SqlDialect)` to `SqlRenderable` or at least to all query types. Ensure all DML queries (not just SELECT) expose their parameters.

### 9. SqlDatabaseConfig.build() Return Type

`SqlDatabaseConfig.build()` returns `SqlDatabaseConfig` instead of `SqlDatabase`. A builder's `build()` method should typically return the thing being built, not the builder itself.

**Suggestion:** Change the return type to `SqlDatabase`, or rename `build()` to something that clarifies it finalizes the config (e.g., `freeze()`) if the intent is to produce an immutable config object that's then passed to a `SqlDatabase` factory.

### 10. Optimistic Locking with Bulk Operations

Version management is clearly automatic for entity-level operations. However, the interaction with bulk `update().set(...).where(...)` is undefined. If a bulk update modifies versioned entities, does the version get incremented? Is `SqlStaleEntityException` thrown if some rows have unexpected versions?

**Suggestion:** Document the expected behavior for bulk operations on versioned entities. Common approaches: (a) bulk updates bypass versioning entirely (with a clear warning), (b) bulk updates increment versions but don't check them, or (c) bulk updates are disallowed on versioned tables without `skipVersionCheck()`.

## Architecture Summary

| Aspect | Assessment |
|---|---|
| Package organization | Excellent — logical, discoverable |
| Type safety | Strong — generics used well |
| Query DSL | Very complete — CTEs, window functions, set operations |
| Dialect system | Well-designed — 22 feature flags, auto-detection |
| Exception handling | Excellent — specific, context-rich |
| Function coverage | Extensive, possibly too broad for v1 |
| Pagination | Well-designed — navigable, metadata-rich |
| Retry logic | Clean — composable, backoff-based |
| Entity listeners | Good — Observer pattern, selective override |
| Audit system | Infrastructure strong, entity contract weak |
| Optimistic locking | Well-documented, automatic, with escape hatch |
| Entity mapping | Missing — needs definition |
| Migration tooling | Underdeveloped — consider deferring |
| Async story | Works but duplicates API surface |
| Index/sequence management | Complete — partial indexes, multiple methods |

## Conclusion

This is a solid conceptual foundation that has matured since the initial review. The addition of pagination, retry logic, entity listeners, and a well-structured audit infrastructure fills important operational gaps. Optimistic locking is now clearly documented with automatic version management. The biggest remaining risks are (1) the entity mapping gap — without this, the framework can't actually function end-to-end, (2) the sheer implementation effort across dialects, (3) the migration system being neither simple enough to ignore nor complete enough to trust, and (4) the `SqlAuditable` weak contract undermining the otherwise solid audit infrastructure. Prioritizing the entity mapping design and then implementing one dialect end-to-end before expanding is recommended.
