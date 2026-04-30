# Layer 6: Migrations

## Overview
Design and implement the schema migration system. This is the most design-heavy layer and depends on all previous layers being functional. The three interfaces (`SqlMigration`, `SqlTableMigration`, `SqlColumnMigration`) are currently empty and need their contracts defined, plus a migration runner to orchestrate execution.

## Files
- `src/main/java/net/luis/utils/io/database/migration/SqlMigration.java`
- `src/main/java/net/luis/utils/io/database/migration/SqlTableMigration.java`
- `src/main/java/net/luis/utils/io/database/migration/SqlColumnMigration.java`
- New file: migration runner (e.g. `SqlMigrationRunner.java` or `SqlMigrationManager.java`)

## Dependencies
- All previous layers:
  - Layer 2: DDL execution for applying migrations
  - Layer 3: Queries for reading/writing migration history
  - Layer 4: Transactions for atomic migration execution

---

## 6a. `SqlMigration` — Base Interface

### Contract
```java
public interface SqlMigration {
    int version();
    String description();
    void migrate(SqlProvider provider) throws SqlException;
}
```

| Method | Purpose |
|--------|---------|
| `version()` | Unique, monotonically increasing version number for ordering |
| `description()` | Human-readable description of what this migration does |
| `migrate(SqlProvider)` | Execute the migration using the provider (which gives access to DDL + queries) |

### Design Notes
- `version()` determines execution order — migrations run in ascending version order
- `SqlProvider` is the execution context — when run inside a transaction, the provider is the transaction's provider
- The migration receives `SqlProvider` (not raw `Connection`) to keep it at the abstraction level of this library

---

## 6b. `SqlTableMigration` — Table-Level DDL

### Contract
```java
public interface SqlTableMigration extends SqlMigration {
    // Table-level operations available during migration
    void renameTable(SqlProvider provider, String oldName, String newName) throws SqlException;
    void addColumn(SqlProvider provider, SqlColumn<?, ?> column) throws SqlException;
    void dropColumn(SqlProvider provider, String tableName, String columnName) throws SqlException;
    void addIndex(SqlProvider provider, SqlIndex index) throws SqlException;
    void dropIndex(SqlProvider provider, String indexName) throws SqlException;
    void addConstraint(SqlProvider provider, String tableName, SqlCondition constraint) throws SqlException;
    void dropConstraint(SqlProvider provider, String tableName, String constraintName) throws SqlException;
}
```

### Alternative: Builder-Style API
Instead of requiring users to implement all methods, provide a builder:

```java
public interface SqlTableMigration extends SqlMigration {
    // Marker interface — implementations use SqlProvider directly in migrate()
    // Utility methods could be provided as default methods or via a helper class
}
```

With static helpers:
```java
SqlTableMigration.renameTable(provider, "old_name", "new_name");
SqlTableMigration.addColumn(provider, table, newColumn);
```

### SQL Generation
Each operation generates ALTER TABLE statements:
- `renameTable` → `ALTER TABLE "old" RENAME TO "new"`
- `addColumn` → `ALTER TABLE "table" ADD COLUMN <columnDefinition>` (use `dialect.renderColumnDefinition()`)
- `dropColumn` → `ALTER TABLE "table" DROP COLUMN "column"`
- etc.

---

## 6c. `SqlColumnMigration` — Column-Level ALTER

### Contract
```java
public interface SqlColumnMigration extends SqlMigration {
    // Column-level operations
    void renameColumn(SqlProvider provider, String tableName, String oldName, String newName) throws SqlException;
    void changeType(SqlProvider provider, String tableName, String columnName, SqlType<?> newType) throws SqlException;
    void setDefault(SqlProvider provider, String tableName, String columnName, Object value) throws SqlException;
    void dropDefault(SqlProvider provider, String tableName, String columnName) throws SqlException;
    void setNullable(SqlProvider provider, String tableName, String columnName, boolean nullable) throws SqlException;
}
```

### SQL Generation
- `renameColumn` → `ALTER TABLE "table" RENAME COLUMN "old" TO "new"`
- `changeType` → `ALTER TABLE "table" ALTER COLUMN "col" TYPE <newType>` (dialect-specific — MySQL uses `MODIFY COLUMN`)
- `setDefault` → `ALTER TABLE "table" ALTER COLUMN "col" SET DEFAULT ?`
- `dropDefault` → `ALTER TABLE "table" ALTER COLUMN "col" DROP DEFAULT`
- `setNullable(true)` → `ALTER TABLE "table" ALTER COLUMN "col" DROP NOT NULL`
- `setNullable(false)` → `ALTER TABLE "table" ALTER COLUMN "col" SET NOT NULL`

### Dialect Differences
ALTER COLUMN syntax varies significantly across databases:
- PostgreSQL: `ALTER TABLE t ALTER COLUMN c TYPE new_type`
- MySQL: `ALTER TABLE t MODIFY COLUMN c new_type [constraints]`
- SQLite: Does not support ALTER COLUMN at all (requires table recreation)
- SQL Server: `ALTER TABLE t ALTER COLUMN c new_type`

This may require new render methods in `SqlDialect` (e.g. `renderAlterColumn()`).

---

## 6d. Migration Runner

### New Class: `SqlMigrationRunner`

**Responsibilities:**
1. Maintain a migration history table
2. Discover and order pending migrations
3. Execute migrations atomically (within transactions)
4. Record successful migrations

### Migration History Table
Auto-created table `_sql_migrations` (or configurable name):

| Column | Type | Description |
|--------|------|-------------|
| `version` | INTEGER, PRIMARY KEY | Migration version number |
| `description` | VARCHAR(255) | Migration description |
| `applied_at` | TIMESTAMP | When the migration was applied |
| `checksum` | VARCHAR(64) | Optional hash for drift detection |

### API
```java
public class SqlMigrationRunner {
    SqlMigrationRunner(SqlDatabase database);

    // Register migrations
    void addMigration(SqlMigration migration);
    void addMigrations(Collection<SqlMigration> migrations);

    // Execute pending migrations
    void migrate() throws SqlException;

    // Query state
    int getCurrentVersion() throws SqlException;
    List<SqlMigration> getPendingMigrations() throws SqlException;
    boolean isUpToDate() throws SqlException;

    // Validation
    void validate() throws SqlException;  // check for version gaps, conflicts
}
```

### Execution Flow
```
migrate():
1. Create history table if not exists (using SqlTableProvider)
2. Read applied versions from history table (using SqlSelectQuery)
3. Sort registered migrations by version
4. For each pending migration (version not in history):
   a. Begin transaction
   b. Execute migration.migrate(transaction)
   c. Insert record into history table
   d. Commit transaction
   e. On failure: rollback, throw exception with migration context
```

### Configuration
- History table name (default `_sql_migrations`)
- Whether to run in individual transactions per migration vs. single transaction for all
- Whether to validate on startup
- Whether to allow out-of-order execution

---

## Design Decisions to Make

1. **Interface design**: Should `SqlTableMigration`/`SqlColumnMigration` expose fine-grained methods, or should they be marker interfaces where users write raw migration logic in `migrate()`?
2. **ALTER rendering**: Need to decide whether to add `renderAlterTable()` / `renderAlterColumn()` methods to `SqlDialect`, or handle ALTER generation in the migration layer directly.
3. **Rollback support**: Should migrations support a `rollback()` method for downgrade? Adds complexity but is standard in migration tools.
4. **Discovery**: Should migrations be discovered via classpath scanning, manual registration, or both?

---

## Estimated Effort
- Interface design + migration runner: ~2-3 days
- ALTER rendering in dialects (if needed): ~1 day
- Testing with multiple dialects: ~1 day
