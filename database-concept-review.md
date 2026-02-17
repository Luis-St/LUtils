# Database Concept Review

## Overall Impression

A comprehensive, type-safe SQL DSL with 78 interfaces/classes across 15 packages. The API surface is large but logically organized. The concept covers everything from basic CRUD to window functions, CTEs, JSON/array operations, migrations, and auditing.

## Strengths

### 1. Clean type hierarchy
`SqlRenderable` as the root for all SQL-generating elements is a strong foundation. The expression hierarchy (`SqlRenderable -> SqlOrderable -> SqlExpression -> SqlColumn`) gives compile-time safety at every level.

### 2. Dialect abstraction
The `SqlDialect` + `SqlDialectFeatures` split is well thought out. Feature flags let you guard against unsupported operations at runtime rather than silently generating bad SQL.

### 3. Exception hierarchy
Very granular. `SqlStaleEntityException` carrying `entityType`, `entityId`, and `expectedVersion` is exactly the right level of detail. The separation of constraint violations into subtypes (unique, foreign key, not-null, check) makes catch blocks meaningful.

### 4. Fluent query API
The `SqlSelectQueryBase` → `SqlSelectQuery` / `SqlSelectProjectionQuery` split is a good choice. Entity queries get locking (`forUpdate`, `skipLocked`), projection queries get mapping (`fetchAs`). No unnecessary methods on either.

### 5. Transaction design
Thread-local `isInTransaction()` / `current()` / `requireActive()` statics, savepoints, isolation levels, read-only mode — this covers real-world needs well.

### 6. Function library
The static utility classes (`SqlAgg`, `SqlString`, `SqlMath`, `SqlDate`, `SqlHash`, `SqlJson`, `SqlArray`, `SqlRegex`, `SqlWindow`) are well-categorized and extensive (~100+ functions).

## Concerns & Suggestions

### 1. API Surface Size vs. Implementation Complexity

78 files of interfaces is a massive contract to fulfill. Each dialect needs to support every function in `SqlString` (29 methods), `SqlMath` (30+), `SqlDate` (35+), etc. That's potentially hundreds of dialect-specific SQL translations.

**Suggestion:** Consider which functions are truly cross-dialect vs. which are PostgreSQL-specific (e.g., `SqlArray`, many `SqlJson` operations). You could tier the functions: core (all dialects must support), extended (some dialects), and dialect-specific. The `SqlDialectFeatures` mechanism exists but doesn't seem granular enough to cover individual function support.

### 2. SqlTable as SqlQueryProvider

`SqlTable<T>` extends `SqlQueryProvider<T>`, meaning tables are both DDL objects (create, drop, truncate) and query factories (select, insert, update, delete). This coupling means:

- A table always needs a database binding to do anything useful
- The `from(database)` / `withIn(transaction)` methods on `SqlQueryProvider` feel like contextual state that shouldn't live on a table definition

**Suggestion:** Consider separating table definition (schema, columns, indexes) from query building. Something like `database.from(table).select()` instead of `table.from(database).select()`. The current approach inverts the natural ownership — a database contains tables, not the other way around.

### 3. Optimistic Locking & Versioning

The `SqlVersioned<T>` interface requires entities to implement `version()` and `withVersion()`, and `SqlVersionColumn` marks the version column. The `skipVersionCheck()` on `SqlQueryProvider` is a nice escape hatch. However:

- How does the version get incremented? Is it automatic on update, or does the caller manage it?
- What happens with batch updates (`update().set(...).where(...)`)? Version checks on bulk updates can be tricky.

**Suggestion:** Document clearly in the interface contracts whether version management is automatic or manual, and how it interacts with bulk operations.

### 4. Async API Duplication

Every query type has sync + async variants (`fetch()` / `fetchAsync()`, `execute()` / `executeAsync()`, etc.). This doubles the method count on already-large interfaces.

**Suggestion:** Consider a pattern like `query.async().fetch()` that returns an async wrapper, or just rely on `CompletableFuture.supplyAsync(() -> query.fetch(), executor)` at the call site. This would halve the query interface sizes.

### 5. Migration System — Thin on Detail

`SqlMigration`, `SqlSchema`, `SqlSchemaDiff`, `SqlTableDiff` are defined but feel underdeveloped relative to the rest. Schema diffing is extremely hard to get right (column renames vs. drop+add, data-dependent migrations, etc.).

**Suggestion:** Either flesh this out significantly (versioned migration tracking table, rollback support, dry-run mode) or consider dropping it from v1 and recommending Flyway/Liquibase integration instead. Half-implemented migration tooling is dangerous.

### 6. Connection Pooling

`SqlPooledDataSource` and `SqlSingleConnectionDataSource` are provided. Connection pooling is notoriously difficult (leak detection, connection validation, eviction, metrics, idle timeout, max lifetime).

**Suggestion:** Unless there is a strong reason to build your own, consider using HikariCP as the default pool and providing a thin adapter. `SqlSingleConnectionDataSource` makes sense for testing/simple use cases.

### 7. SqlAuditable Design

`SqlAuditable<T>` has getters returning `null` by default and setters throwing `UnsupportedOperationException` by default. This means an entity can claim to be auditable but silently ignore or crash on audit operations.

**Suggestion:** Make `SqlAuditable` a stronger contract. If an entity implements it, it should be required to support all operations. Consider splitting into `SqlCreationAuditable` and `SqlUpdateAuditable` if partial auditing is needed.

### 8. Missing: Entity Mapping

The concept has queries returning `T` (entity type) but there's no visible ORM/mapping layer — no annotations like `@Column`, `@Table`, no `ResultSet -> Entity` mapping interface. How does `select().fetch()` know how to turn rows into `T`?

**Suggestion:** This is a critical gap. You'll need either:
- Annotation-based mapping (like JPA)
- A `SqlRowMapper<T>` functional interface
- Convention-based mapping (column names → field names)

This decision will significantly affect the rest of the API, so it's worth deciding early.

### 9. SqlRenderable Responsibility

Everything implements `toSql(SqlDialect)`, but who handles parameter binding? The queries expose `getParameters()`, but there's no visible `SqlParameterBinder` or `PreparedStatement` integration concept.

**Suggestion:** Define how parameterized SQL is generated. A `SqlRendered` type holding both the SQL string and the parameter list (like `record SqlRendered(String sql, List<Object> parameters)`) would make the boundary explicit.

## Architecture Summary

| Aspect | Assessment |
|---|---|
| Package organization | Excellent — logical, discoverable |
| Type safety | Strong — generics used well |
| Query DSL | Very complete — CTEs, window functions, set operations |
| Dialect system | Well-designed — feature flags are smart |
| Exception handling | Excellent — specific, context-rich |
| Function coverage | Extensive, possibly too broad for v1 |
| Entity mapping | Missing — needs definition |
| Migration tooling | Underdeveloped — consider deferring |
| Async story | Works but duplicates API surface |

## Conclusion

This is a solid conceptual foundation. The biggest risks are (1) the mapping gap, (2) the sheer implementation effort across dialects, and (3) the migration system being neither simple enough to ignore nor complete enough to trust. Prioritizing the entity mapping design and then implementing one dialect end-to-end before expanding is recommended.
