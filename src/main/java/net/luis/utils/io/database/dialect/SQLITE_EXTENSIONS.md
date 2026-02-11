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

### Pattern Matching

#### glob
```java
@NonNull SqlCondition glob(@NonNull String pattern);
```
Generates SQL: `column GLOB 'pattern'`

SQLite-specific case-sensitive pattern matching using Unix-style wildcards: `*` (any sequence), `?` (any single character), `[...]` (character class). Unlike `LIKE` which is case-insensitive by default, `GLOB` is always case-sensitive.

#### notGlob
```java
@NonNull SqlCondition notGlob(@NonNull String pattern);
```
Generates SQL: `column NOT GLOB 'pattern'`

Negation of `GLOB` pattern matching.

#### regexp
```java
@NonNull SqlCondition regexp(@NonNull String pattern);
```
Generates SQL: `column REGEXP 'pattern'`

Regular expression matching. Requires the `REGEXP` user function to be loaded (not built-in, but commonly provided via extensions).

### Type Introspection

#### typeof
```java
@NonNull SqlExpression<String> typeof();
```
Generates SQL: `typeof(column)`

Returns the fundamental datatype of the value: `'null'`, `'integer'`, `'real'`, `'text'`, or `'blob'`. SQLite uses dynamic typing, so this can vary per row.

### Null Handling

#### nullIf
```java
@NonNull SqlExpression<?> nullIf(@NonNull T value);
```
Generates SQL: `NULLIF(column, value)`

Returns null if the column equals the given value, otherwise returns the column value.

#### coalesce
```java
@SuppressWarnings("unchecked")
@NonNull SqlExpression<?> coalesce(T @NonNull ... values);
```
Generates SQL: `COALESCE(column, val1, val2, ...)`

Returns the first non-null value among the column and the given values.

### String Functions

#### instr
```java
@NonNull SqlExpression<Integer> instr(@NonNull String substring);
```
Generates SQL: `INSTR(column, 'substring')`

Returns the 1-based position of the first occurrence of the substring, or 0 if not found.

#### unicode
```java
@NonNull SqlExpression<Integer> unicode();
```
Generates SQL: `UNICODE(column)`

Returns the Unicode code point of the first character of the string.

#### charFunc
```java
@NonNull SqlExpression<String> charFunc(int @NonNull ... codePoints);
```
Generates SQL: `CHAR(codePoint1, codePoint2, ...)`

Returns a string composed of characters from the given Unicode code points.

#### replace
```java
@NonNull SqlExpression<String> replace(@NonNull String search, @NonNull String replacement);
```
Generates SQL: `REPLACE(column, 'search', 'replacement')`

Replaces all occurrences of the search string with the replacement.

#### substr
```java
@NonNull SqlExpression<String> substr(int start, int length);
```
Generates SQL: `SUBSTR(column, start, length)`

Returns a substring starting at position `start` (1-based) with the given length.

#### ltrim
```java
@NonNull SqlExpression<String> ltrim();
```
Generates SQL: `LTRIM(column)`

Removes leading whitespace.

#### ltrimChars
```java
@NonNull SqlExpression<String> ltrimChars(@NonNull String chars);
```
Generates SQL: `LTRIM(column, 'chars')`

Removes leading characters that appear in the given string.

#### rtrim
```java
@NonNull SqlExpression<String> rtrim();
```
Generates SQL: `RTRIM(column)`

Removes trailing whitespace.

#### rtrimChars
```java
@NonNull SqlExpression<String> rtrimChars(@NonNull String chars);
```
Generates SQL: `RTRIM(column, 'chars')`

Removes trailing characters that appear in the given string.

#### trim
```java
@NonNull SqlExpression<String> trim();
```
Generates SQL: `TRIM(column)`

Removes leading and trailing whitespace.

#### trimChars
```java
@NonNull SqlExpression<String> trimChars(@NonNull String chars);
```
Generates SQL: `TRIM(column, 'chars')`

Removes leading and trailing characters that appear in the given string.

#### hex
```java
@NonNull SqlExpression<String> hex();
```
Generates SQL: `HEX(column)`

Returns the hexadecimal representation of a blob or the UTF-8 encoding of a string.

#### unhex
```java
@NonNull SqlExpression<?> unhex();
```
Generates SQL: `UNHEX(column)`

Converts a hexadecimal string to a blob. Available since SQLite 3.38.0.

#### quote
```java
@NonNull SqlExpression<String> quote();
```
Generates SQL: `QUOTE(column)`

Returns a SQL literal representation of the value. Useful for debugging and constructing dynamic SQL.

### Math Functions (SQLite 3.35+)

#### abs
```java
@NonNull SqlExpression<?> abs();
```
Generates SQL: `ABS(column)`

Returns the absolute value.

#### sign
```java
@NonNull SqlExpression<Integer> sign();
```
Generates SQL: `SIGN(column)`

Returns -1, 0, or 1 depending on the sign of the value. Available since SQLite 3.39.0.

#### ceil
```java
@NonNull SqlExpression<?> ceil();
```
Generates SQL: `CEIL(column)`

Returns the smallest integer not less than the value. Available since SQLite 3.35.0.

#### floor
```java
@NonNull SqlExpression<?> floor();
```
Generates SQL: `FLOOR(column)`

Returns the largest integer not greater than the value. Available since SQLite 3.35.0.

#### round
```java
@NonNull SqlExpression<?> round(int decimals);
```
Generates SQL: `ROUND(column, decimals)`

Rounds to the specified number of decimal places.

#### ln
```java
@NonNull SqlExpression<Double> ln();
```
Generates SQL: `LN(column)`

Returns the natural logarithm. Available since SQLite 3.35.0.

#### log10
```java
@NonNull SqlExpression<Double> log10();
```
Generates SQL: `LOG10(column)`

Returns the base-10 logarithm. Available since SQLite 3.35.0.

#### log2
```java
@NonNull SqlExpression<Double> log2();
```
Generates SQL: `LOG2(column)`

Returns the base-2 logarithm. Available since SQLite 3.35.0.

#### power
```java
@NonNull SqlExpression<Double> power(double exponent);
```
Generates SQL: `POWER(column, exponent)`

Returns the value raised to the given exponent. Available since SQLite 3.35.0.

#### sqrt
```java
@NonNull SqlExpression<Double> sqrt();
```
Generates SQL: `SQRT(column)`

Returns the square root. Available since SQLite 3.35.0.

#### mod
```java
@NonNull SqlExpression<?> mod(@NonNull T divisor);
```
Generates SQL: `MOD(column, divisor)` or `column % divisor`

Returns the remainder after division. Available since SQLite 3.35.0.

### JSON Functions (SQLite 3.38+)

#### jsonExtract
```java
@NonNull SqlExpression<?> jsonExtract(@NonNull String path);
```
Generates SQL: `json_extract(column, 'path')`

Extracts a value from a JSON document at the specified path (e.g., `'$.name'`).

#### jsonExtractAs
```java
<R> @NonNull SqlExpression<R> jsonExtractAs(@NonNull String path, @NonNull Class<R> type);
```
Generates SQL: `json_extract(column, 'path')`

Extracts a JSON value and casts it to the specified Java type.

#### jsonType
```java
@NonNull SqlExpression<String> jsonType();
```
Generates SQL: `json_type(column)`

Returns the JSON type of the top-level value: `'null'`, `'true'`, `'false'`, `'integer'`, `'real'`, `'text'`, `'array'`, `'object'`.

#### jsonTypeAtPath
```java
@NonNull SqlExpression<String> jsonTypeAtPath(@NonNull String path);
```
Generates SQL: `json_type(column, 'path')`

Returns the JSON type of the value at the specified path.

#### jsonValid
```java
@NonNull SqlCondition jsonValid();
```
Generates SQL: `json_valid(column)`

Returns true if the column contains valid JSON.

#### jsonArrayLength
```java
@NonNull SqlExpression<Integer> jsonArrayLength();
```
Generates SQL: `json_array_length(column)`

Returns the number of elements in a JSON array.

#### jsonArrayLengthAtPath
```java
@NonNull SqlExpression<Integer> jsonArrayLengthAtPath(@NonNull String path);
```
Generates SQL: `json_array_length(column, 'path')`

Returns the length of the JSON array at the specified path.

#### jsonInsert
```java
@NonNull SqlExpression<?> jsonInsert(@NonNull String path, @NonNull String value);
```
Generates SQL: `json_insert(column, 'path', 'value')`

Inserts a value at the path if it does not already exist. Does not replace existing values.

#### jsonReplace
```java
@NonNull SqlExpression<?> jsonReplace(@NonNull String path, @NonNull String value);
```
Generates SQL: `json_replace(column, 'path', 'value')`

Replaces the value at the path if it exists. Does nothing if the path doesn't exist.

#### jsonSet
```java
@NonNull SqlExpression<?> jsonSet(@NonNull String path, @NonNull String value);
```
Generates SQL: `json_set(column, 'path', 'value')`

Sets the value at the path. Creates if it doesn't exist, replaces if it does.

#### jsonRemove
```java
@NonNull SqlExpression<?> jsonRemove(@NonNull String path);
```
Generates SQL: `json_remove(column, 'path')`

Removes the value at the specified path.

#### jsonPatch
```java
@NonNull SqlExpression<?> jsonPatch(@NonNull String patch);
```
Generates SQL: `json_patch(column, 'patch')`

Applies an RFC 7396 merge patch to the JSON document.

#### jsonGroupArray
```java
@NonNull SqlExpression<?> jsonGroupArray();
```
Generates SQL: `json_group_array(column)`

Aggregate function that collects values into a JSON array.

#### jsonGroupObject
```java
@NonNull SqlExpression<?> jsonGroupObject(@NonNull SqlColumn<String> keyColumn);
```
Generates SQL: `json_group_object(keyColumn, column)`

Aggregate function that collects key-value pairs into a JSON object.

#### jsonEach
```java
@NonNull SqlExpression<?> jsonEach();
```
Generates SQL: `json_each(column)`

Table-valued function that expands a JSON array into rows with columns: `key`, `value`, `type`, `atom`, `id`, `parent`, `fullkey`, `path`.

#### jsonTree
```java
@NonNull SqlExpression<?> jsonTree();
```
Generates SQL: `json_tree(column)`

Table-valued function that recursively walks the JSON structure, returning one row per JSON element.

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

### Window Functions (SQLite 3.25+)

#### rowNumber
```java
@NonNull SqlExpression<Integer> rowNumber();
```
Generates SQL: `ROW_NUMBER() OVER (...)`

Returns the sequential row number within a partition.

#### rank
```java
@NonNull SqlExpression<Integer> rank();
```
Generates SQL: `RANK() OVER (...)`

Returns the rank with gaps.

#### denseRank
```java
@NonNull SqlExpression<Integer> denseRank();
```
Generates SQL: `DENSE_RANK() OVER (...)`

Returns the rank without gaps.

#### ntile
```java
@NonNull SqlExpression<Integer> ntile(int buckets);
```
Generates SQL: `NTILE(buckets) OVER (...)`

Distributes rows into the specified number of buckets.

#### lag
```java
@NonNull SqlExpression<?> lag(int offset);
```
Generates SQL: `LAG(column, offset) OVER (...)`

Returns the value from the row `offset` rows before.

#### lead
```java
@NonNull SqlExpression<?> lead(int offset);
```
Generates SQL: `LEAD(column, offset) OVER (...)`

Returns the value from the row `offset` rows after.

#### firstValue
```java
@NonNull SqlExpression<?> firstValue();
```
Generates SQL: `FIRST_VALUE(column) OVER (...)`

Returns the first value in the window frame.

#### lastValue
```java
@NonNull SqlExpression<?> lastValue();
```
Generates SQL: `LAST_VALUE(column) OVER (...)`

Returns the last value in the window frame.

#### nthValue
```java
@NonNull SqlExpression<?> nthValue(int n);
```
Generates SQL: `NTH_VALUE(column, n) OVER (...)`

Returns the Nth value in the window frame.

### Returning Clause (SQLite 3.35+)

#### returning
```java
@NonNull SqlExpression<?> returning();
```
Generates SQL: Used with `INSERT/UPDATE/DELETE ... RETURNING column`

Allows INSERT, UPDATE, and DELETE statements to return the affected rows' values.

---

## SqliteTable Extensions

### Table Configuration

#### setStrict
```java
void setStrict();
```
Generates SQL: `CREATE TABLE ... STRICT`

Creates a strict table that enforces type checking. Columns can only hold values of the declared type. Available since SQLite 3.37.0.

#### setWithoutRowid
```java
void setWithoutRowid();
```
Generates SQL: `CREATE TABLE ... WITHOUT ROWID`

Creates a table without the implicit `rowid` column. Requires an explicit `INTEGER PRIMARY KEY`. Reduces storage overhead for tables with non-integer primary keys.

#### setStrictWithoutRowid
```java
void setStrictWithoutRowid();
```
Generates SQL: `CREATE TABLE ... STRICT, WITHOUT ROWID`

Combines both STRICT mode and WITHOUT ROWID.

### Virtual Tables

#### createVirtualTable
```java
void createVirtualTable(@NonNull String module, String @NonNull ... args);
```
Generates SQL: `CREATE VIRTUAL TABLE table USING module(args)`

Creates a virtual table using the specified module (e.g., `fts5`, `rtree`, `csv`).

#### createFts5
```java
void createFts5(SqlColumn<?> @NonNull ... contentColumns);
```
Generates SQL: `CREATE VIRTUAL TABLE table USING fts5(column1, column2, ...)`

Creates an FTS5 (Full-Text Search 5) virtual table for efficient text search.

#### createFts5External
```java
void createFts5External(@NonNull SqlTable<?> contentTable, SqlColumn<?> @NonNull ... contentColumns);
```
Generates SQL: `CREATE VIRTUAL TABLE table USING fts5(column1, column2, ..., content='contentTable', content_rowid='rowid')`

Creates an external-content FTS5 table backed by an existing table. Avoids data duplication.

#### createRtree
```java
void createRtree(@NonNull String idColumn, String @NonNull ... dimensionColumns);
```
Generates SQL: `CREATE VIRTUAL TABLE table USING rtree(idColumn, minX, maxX, minY, maxY, ...)`

Creates an R-tree virtual table for efficient spatial queries. Dimension columns must come in min/max pairs.

### Full-Text Search (FTS5)

#### fts5Match
```java
@NonNull SqlCondition fts5Match(@NonNull String query);
```
Generates SQL: `table MATCH 'query'`

Full-text search query on an FTS5 table. Supports prefix queries (`prefix*`), phrase queries (`"exact phrase"`), boolean operators (`AND`, `OR`, `NOT`), and column filters (`column:term`).

#### fts5Rank
```java
@NonNull SqlExpression<Double> fts5Rank();
```
Generates SQL: `rank`

Returns the BM25 relevance score. Only available on FTS5 tables.

#### fts5Highlight
```java
@NonNull SqlExpression<String> fts5Highlight(int columnIndex, @NonNull String openTag, @NonNull String closeTag);
```
Generates SQL: `highlight(table, columnIndex, 'openTag', 'closeTag')`

Returns the column text with matching terms wrapped in the specified tags.

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

### Pragma Operations

#### enableWalMode
```java
void enableWalMode();
```
Generates SQL: `PRAGMA journal_mode=WAL`

Enables Write-Ahead Logging. Allows concurrent reads during writes. Recommended for most applications.

#### enableDeleteMode
```java
void enableDeleteMode();
```
Generates SQL: `PRAGMA journal_mode=DELETE`

Uses the default rollback journal mode.

#### setPageSize
```java
void setPageSize(int size);
```
Generates SQL: `PRAGMA page_size=size`

Sets the page size in bytes (512, 1024, 2048, 4096, 8192, 16384, 32768, 65536). Must be set before creating any tables.

#### setCacheSize
```java
void setCacheSize(int pages);
```
Generates SQL: `PRAGMA cache_size=pages`

Sets the number of pages in the page cache. Negative values set size in kibibytes.

#### setSynchronous
```java
void setSynchronous(@NonNull SqliteSynchronous mode);
```
Generates SQL: `PRAGMA synchronous=mode`

Controls how aggressively SQLite syncs data to disk.

#### setAutoVacuum
```java
void setAutoVacuum(@NonNull SqliteAutoVacuum mode);
```
Generates SQL: `PRAGMA auto_vacuum=mode`

Controls automatic vacuum behavior.

#### setMmapSize
```java
void setMmapSize(long bytes);
```
Generates SQL: `PRAGMA mmap_size=bytes`

Sets the maximum number of bytes to memory-map. Can significantly improve read performance.

#### setBusyTimeout
```java
void setBusyTimeout(int milliseconds);
```
Generates SQL: `PRAGMA busy_timeout=milliseconds`

Sets how long to wait when a table is locked before returning SQLITE_BUSY.

#### enableForeignKeys
```java
void enableForeignKeys();
```
Generates SQL: `PRAGMA foreign_keys=ON`

Enables foreign key constraint enforcement (disabled by default in SQLite).

### Maintenance

#### vacuum
```java
void vacuum();
```
Generates SQL: `VACUUM`

Rebuilds the database file, reclaiming unused space and defragmenting.

#### vacuumInto
```java
void vacuumInto(@NonNull String filepath);
```
Generates SQL: `VACUUM INTO 'filepath'`

Creates a vacuumed copy of the database at the specified path. Available since SQLite 3.27.0.

#### analyze
```java
void analyze();
```
Generates SQL: `ANALYZE table`

Updates the statistics used by the query planner.

#### reindex
```java
void reindex();
```
Generates SQL: `REINDEX table`

Rebuilds all indexes on the table.

#### integrityCheck
```java
@NonNull String integrityCheck();
```
Generates SQL: `PRAGMA integrity_check`

Performs a comprehensive check of the database structure. Returns `'ok'` or a description of problems.

#### foreignKeyCheck
```java
@NonNull List<?> foreignKeyCheck();
```
Generates SQL: `PRAGMA foreign_key_check(table)`

Checks for foreign key violations in this table.

### Database Management

#### attachDatabase
```java
void attachDatabase(@NonNull String filepath, @NonNull String alias);
```
Generates SQL: `ATTACH DATABASE 'filepath' AS alias`

Attaches another SQLite database file, allowing cross-database queries.

#### detachDatabase
```java
void detachDatabase(@NonNull String alias);
```
Generates SQL: `DETACH DATABASE alias`

Detaches a previously attached database.

### Triggers

#### createTrigger
```java
void createTrigger(@NonNull String name, @NonNull String timing, @NonNull String event, @NonNull String body);
```
Generates SQL: `CREATE TRIGGER name timing event ON table BEGIN body; END`

Creates a trigger. `timing` is `BEFORE`, `AFTER`, or `INSTEAD OF`. `event` is `INSERT`, `UPDATE`, or `DELETE`.

#### dropTrigger
```java
void dropTrigger(@NonNull String name);
```
Generates SQL: `DROP TRIGGER IF EXISTS name`

Drops a trigger.

---

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
