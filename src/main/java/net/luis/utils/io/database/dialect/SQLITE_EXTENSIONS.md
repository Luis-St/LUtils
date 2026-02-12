# SQLite Dialect Extension Guide

## Current State

SQLite currently has no dialect-specific column or table interfaces. The `SqliteDialect` uses the base `SqlTable<?>` and `SqlColumn<?>` types. This guide proposes creating `SqliteColumn<T>` and `SqliteTable<T>` interfaces to expose SQLite-specific features, and updating `SqliteDialect` accordingly.

### Proposed Architecture
```java
// New interfaces
public interface SqliteColumn<T> extends SqlColumn<T> { ... }
public interface SqliteTable<T> extends SqlTable<T> { ... }

// Updated dialect
public final class SqliteDialect extends SqlDialect<SqliteTable<?>, SqliteColumn<?>> {}
```

## SqliteColumn Extensions

### Type Introspection

#### typeof
```java
@NonNull SqlExpression<String> typeof();
```
Generates SQL: `typeof(column)`

Returns the fundamental datatype of the value: `'null'`, `'integer'`, `'real'`, `'text'`, or `'blob'`. SQLite uses dynamic typing, so this can vary per row.

### String Functions

#### quote
```java
@NonNull SqlExpression<String> quote();
```
Generates SQL: `QUOTE(column)`

Returns a SQL literal representation of the value. Useful for debugging and constructing dynamic SQL.

Note: Math functions are documented in `MATH_COMMON.md`. String functions are documented in `STRING_COMMON.md`. Null handling operations are documented in `NULL_COMMON.md`. JSON operations are documented in `JSON_COMMON.md`. Array operations are documented in `ARRAY_COMMON.md`. Common regex operations are documented in `REGEX_COMMON.md`.

### Aggregate Extensions

#### groupConcat
```java
@NonNull SqlExpression<String> groupConcat(@NonNull String separator);
```
Generates SQL: `GROUP_CONCAT(column, 'separator')`

Concatenates values from grouped rows with the specified separator.

#### totalAgg
```java
@NonNull SqlExpression<Double> totalAgg();
```
Generates SQL: `TOTAL(column)`

Like `SUM()` but returns 0.0 instead of null when there are no rows. Always returns a float.

### Date/Time Functions

#### date
```java
@NonNull SqlExpression<String> date(String @NonNull ... modifiers);
```
Generates SQL: `DATE(column, 'modifier1', 'modifier2', ...)`

Returns the date in `YYYY-MM-DD` format, with optional modifiers like `'+1 day'`, `'start of month'`.

#### time
```java
@NonNull SqlExpression<String> time(String @NonNull ... modifiers);
```
Generates SQL: `TIME(column, 'modifier1', 'modifier2', ...)`

Returns the time in `HH:MM:SS` format.

#### datetime
```java
@NonNull SqlExpression<String> datetime(String @NonNull ... modifiers);
```
Generates SQL: `DATETIME(column, 'modifier1', 'modifier2', ...)`

Returns the datetime in `YYYY-MM-DD HH:MM:SS` format.

#### julianday
```java
@NonNull SqlExpression<Double> julianday();
```
Generates SQL: `JULIANDAY(column)`

Returns the Julian day number.

#### unixepoch
```java
@NonNull SqlExpression<Long> unixepoch();
```
Generates SQL: `UNIXEPOCH(column)`

Returns the Unix timestamp (seconds since 1970-01-01). Available since SQLite 3.38.0.

#### strftime
```java
@NonNull SqlExpression<String> strftime(@NonNull String format);
```
Generates SQL: `STRFTIME('format', column)`

Formats the date/time value according to the format string (e.g., `'%Y-%m-%d'`, `'%H:%M'`).

### Returning Clause (SQLite 3.35+)

#### returning
```java
@NonNull SqlExpression<?> returning();
```
Generates SQL: Used with `INSERT/UPDATE/DELETE ... RETURNING column`

Allows INSERT, UPDATE, and DELETE statements to return the affected rows' values.

---

### Full-Text Search (FTS5)

#### fts5Snippet
```java
@NonNull SqlExpression<String> fts5Snippet(int columnIndex, @NonNull String openTag, @NonNull String closeTag, @NonNull String ellipsis, int maxTokens);
```
Generates SQL: `snippet(table, columnIndex, 'openTag', 'closeTag', 'ellipsis', maxTokens)`

Returns a text snippet around matching terms.

#### fts5Bm25
```java
@NonNull SqlExpression<Double> fts5Bm25(double @NonNull ... weights);
```
Generates SQL: `bm25(table, weight1, weight2, ...)`

Returns the BM25 score with custom column weights.

## Supporting Types

### SqliteSynchronous (new enum)
```java
public enum SqliteSynchronous {
    OFF,      // No syncs at all (fastest, risk of corruption on OS crash)
    NORMAL,   // Sync at critical moments (good balance)
    FULL,     // Sync after every transaction (default, safe)
    EXTRA     // Even more syncing (safest)
}
```

### SqliteAutoVacuum (new enum)
```java
public enum SqliteAutoVacuum {
    NONE,         // No auto-vacuum (default)
    FULL,         // Reclaim space after every transaction
    INCREMENTAL   // Manual incremental vacuum
}
```

### SqliteJournalMode (new enum)
```java
public enum SqliteJournalMode {
    DELETE,    // Default rollback journal
    TRUNCATE,  // Truncate journal instead of delete
    PERSIST,   // Zero-fill journal header
    MEMORY,    // In-memory journal
    WAL,       // Write-Ahead Logging (recommended)
    OFF        // No journal (dangerous)
}
```

### SqliteConflictClause (new enum)
```java
public enum SqliteConflictClause {
    ABORT,    // Default - abort current statement
    ROLLBACK, // Roll back the entire transaction
    FAIL,     // Abort but keep prior changes
    IGNORE,   // Skip the conflicting row
    REPLACE   // Replace the conflicting row
}
```
