# PostgreSQL Dialect Extension Guide

## Current State

Summary of what already exists (list the interfaces and their current methods briefly).

- **PostgresColumn\<T\>**: `similarTo`, `posixRegex`, `ilike`, `notIlike`, `fullTextSearch`
- **PostgresTable\<T\>**: `select()` (returning PostgresSelectQuery), `setUnlogged`, `setTablespace`, `partitionByRange`, `partitionByList`, `partitionByHash`, `enableRowLevelSecurity`
- **PostgresSelectQuery\<T\>**: `distinctOn`
- **SqlColumn\<T\>** (base): `equalTo`, `notEqualTo`, `greaterThan`, `greaterThanOrEqualTo`, `lessThan`, `lessThanOrEqualTo`, `between`, `isNull`, `isNotNull`, `like`, `startsWith`, `contains`, `endsWith`, `lengthGreaterThan`, `equalToIgnoreCase`, `withinLast`, `in`, `notIn`, `ifNull`, `as`, `asc`, `desc`, `nullsFirst`, `nullsLast`

Note: JSON operations are documented in `JSON_COMMON.md`. Array operations are documented in `ARRAY_COMMON.md`.

## PostgresColumn Extensions

### Pattern Matching

#### notSimilarTo
```java
@NonNull SqlCondition notSimilarTo(@NonNull String pattern);
```
Generates SQL: `column NOT SIMILAR TO pattern`

Negation of `SIMILAR TO`. Combines SQL `LIKE` syntax with POSIX regex. Supports `%`, `_`, `|`, `*`, `+`.

#### posixRegexInsensitive
```java
@NonNull SqlCondition posixRegexInsensitive(@NonNull String pattern);
```
Generates SQL: `column ~* pattern`

Case-insensitive POSIX regular expression match.

#### notPosixRegex
```java
@NonNull SqlCondition notPosixRegex(@NonNull String pattern);
```
Generates SQL: `column !~ pattern`

Negated case-sensitive POSIX regex match.

#### notPosixRegexInsensitive
```java
@NonNull SqlCondition notPosixRegexInsensitive(@NonNull String pattern);
```
Generates SQL: `column !~* pattern`

Negated case-insensitive POSIX regex match.

### Type Casting

#### cast
```java
@NonNull SqlExpression<?> cast(@NonNull SqlColumnType targetType);
```
Generates SQL: `column::targetType`

PostgreSQL-specific shorthand cast operator. More concise than standard `CAST(column AS type)`.

### Null Handling

#### isDistinctFrom
```java
@NonNull SqlCondition isDistinctFrom(@NonNull T value);
```
Generates SQL: `column IS DISTINCT FROM value`

Null-safe inequality comparison. Unlike `!=`, returns `true` when one operand is null and the other is not, and `false` when both are null.

#### isNotDistinctFrom
```java
@NonNull SqlCondition isNotDistinctFrom(@NonNull T value);
```
Generates SQL: `column IS NOT DISTINCT FROM value`

Null-safe equality comparison. Unlike `=`, returns `true` when both operands are null.

### String Aggregation

#### stringAgg
```java
@NonNull SqlExpression<String> stringAgg(@NonNull String separator);
```
Generates SQL: `STRING_AGG(column, 'separator')`

Concatenates values from grouped rows into a single string separated by the given separator. PostgreSQL equivalent of MySQL's `GROUP_CONCAT`.

### Regular Expression Functions

#### regexpReplace
```java
@NonNull SqlExpression<String> regexpReplace(@NonNull String pattern, @NonNull String replacement);
```
Generates SQL: `regexp_replace(column, 'pattern', 'replacement')`

Replaces the first substring matching the POSIX regex pattern with the replacement string.

#### regexpReplaceAll
```java
@NonNull SqlExpression<String> regexpReplaceAll(@NonNull String pattern, @NonNull String replacement);
```
Generates SQL: `regexp_replace(column, 'pattern', 'replacement', 'g')`

Replaces all substrings matching the POSIX regex pattern with the replacement string.

#### regexpMatch
```java
@NonNull SqlExpression<?> regexpMatch(@NonNull String pattern);
```
Generates SQL: `regexp_match(column, 'pattern')`

Returns the first match of a POSIX regex pattern as a text array of captured groups.

#### regexpMatches
```java
@NonNull SqlExpression<?> regexpMatches(@NonNull String pattern);
```
Generates SQL: `regexp_matches(column, 'pattern', 'g')`

Returns all matches of a POSIX regex pattern as a set of text arrays.

#### regexpSplitToArray
```java
@NonNull SqlExpression<?> regexpSplitToArray(@NonNull String pattern);
```
Generates SQL: `regexp_split_to_array(column, 'pattern')`

Splits the string using the POSIX regex pattern and returns the result as a text array.

#### regexpSplitToTable
```java
@NonNull SqlExpression<?> regexpSplitToTable(@NonNull String pattern);
```
Generates SQL: `regexp_split_to_table(column, 'pattern')`

Splits the string using the POSIX regex pattern and returns the result as a set of rows.

### Full-Text Search

#### toTsvector
```java
@NonNull SqlExpression<?> toTsvector();
```
Generates SQL: `to_tsvector(column)`

Converts the column value to a `tsvector` (text search vector) using the default text search configuration.

#### toTsvector (with config)
```java
@NonNull SqlExpression<?> toTsvector(@NonNull String config);
```
Generates SQL: `to_tsvector('config', column)`

Converts the column value to a `tsvector` using the specified text search configuration (e.g., `'english'`, `'simple'`).

#### tsRank
```java
@NonNull SqlExpression<Double> tsRank(@NonNull String query);
```
Generates SQL: `ts_rank(to_tsvector(column), to_tsquery('query'))`

Returns a relevance ranking score for full-text search results.

#### tsRankCd
```java
@NonNull SqlExpression<Double> tsRankCd(@NonNull String query);
```
Generates SQL: `ts_rank_cd(to_tsvector(column), to_tsquery('query'))`

Returns a cover density ranking score, which considers the proximity of matching lexemes.

#### tsHeadline
```java
@NonNull SqlExpression<String> tsHeadline(@NonNull String query);
```
Generates SQL: `ts_headline(column, to_tsquery('query'))`

Returns a text snippet with search terms highlighted using `<b>` tags.

#### phraseSearch
```java
@NonNull SqlCondition phraseSearch(@NonNull String phrase);
```
Generates SQL: `to_tsvector(column) @@ phraseto_tsquery('phrase')`

Full-text search that matches exact phrases (word order matters), unlike `fullTextSearch` which matches individual terms.

#### websearch
```java
@NonNull SqlCondition websearch(@NonNull String query);
```
Generates SQL: `to_tsvector(column) @@ websearch_to_tsquery('query')`

Full-text search using web-search-style syntax (e.g., `"exact phrase" -excluded +required`). Available since PostgreSQL 11.

### Range Operations

#### rangeContains
```java
@NonNull SqlCondition rangeContains(@NonNull T value);
```
Generates SQL: `column @> value`

Checks if a range column contains the given value.

#### rangeOverlaps
```java
@NonNull SqlCondition rangeOverlaps(@NonNull T start, @NonNull T end);
```
Generates SQL: `column && '[start, end]'`

Checks if a range column overlaps with the given range.

#### rangeIsEmpty
```java
@NonNull SqlCondition rangeIsEmpty();
```
Generates SQL: `isempty(column)`

Checks if the range column is empty.

#### lowerBound
```java
@NonNull SqlExpression<?> lowerBound();
```
Generates SQL: `lower(column)`

Returns the lower bound of a range column.

#### upperBound
```java
@NonNull SqlExpression<?> upperBound();
```
Generates SQL: `upper(column)`

Returns the upper bound of a range column.

### Network Address Operations

#### inetContains
```java
@NonNull SqlCondition inetContains(@NonNull String network);
```
Generates SQL: `column >> 'network'`

Checks if the inet/cidr column strictly contains the given network.

#### inetContainedBy
```java
@NonNull SqlCondition inetContainedBy(@NonNull String network);
```
Generates SQL: `column << 'network'`

Checks if the inet/cidr column is strictly contained by the given network.

#### inetContainsOrEquals
```java
@NonNull SqlCondition inetContainsOrEquals(@NonNull String network);
```
Generates SQL: `column >>= 'network'`

Checks if the inet/cidr column contains or equals the given network.

#### masklen
```java
@NonNull SqlExpression<Integer> masklen();
```
Generates SQL: `masklen(column)`

Returns the netmask length of an inet/cidr value.

#### hostAddress
```java
@NonNull SqlExpression<String> hostAddress();
```
Generates SQL: `host(column)`

Returns the IP address as text (without netmask) from an inet/cidr value.

---

## PostgresTable Extensions

### Table Inheritance

#### inherits
```java
void inherits(@NonNull SqlTable<?> parent);
```
Generates SQL: `CREATE TABLE ... INHERITS (parent)`

Sets up PostgreSQL table inheritance. The child table inherits all columns from the parent.

### Materialized Views

#### createMaterializedView
```java
void createMaterializedView(@NonNull String name, @NonNull String query);
```
Generates SQL: `CREATE MATERIALIZED VIEW name AS query`

Creates a materialized view that stores query results physically for faster access.

### Index Types

#### createGinIndex
```java
void createGinIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table USING GIN (columns)`

Creates a GIN (Generalized Inverted Index) index. Ideal for full-text search, JSONB, and array columns.

#### createGistIndex
```java
void createGistIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table USING GiST (columns)`

Creates a GiST (Generalized Search Tree) index. Supports range types, geometric types, and full-text search.

#### createBrinIndex
```java
void createBrinIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table USING BRIN (columns)`

Creates a BRIN (Block Range Index) index. Very compact, ideal for large tables with naturally ordered data (e.g., timestamps).

#### createHashIndex
```java
void createHashIndex(@NonNull String name, @NonNull SqlColumn<?> column);
```
Generates SQL: `CREATE INDEX name ON table USING HASH (column)`

Creates a hash index. Only supports equality comparisons but can be faster than B-tree for simple lookups.

#### createPartialIndex
```java
void createPartialIndex(@NonNull String name, @NonNull SqlCondition where, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table (columns) WHERE condition`

Creates a partial index that only indexes rows matching the given condition. Reduces index size and improves performance.

#### createExpressionIndex
```java
void createExpressionIndex(@NonNull String name, @NonNull String expression);
```
Generates SQL: `CREATE INDEX name ON table (expression)`

Creates an index on an expression (e.g., `lower(email)`, `(data->>'key')`).

---

## PostgresSelectQuery Extensions

#### forUpdate
```java
@NonNull PostgresSelectQuery<T> forUpdate();
```
Generates SQL: `SELECT ... FOR UPDATE`

Locks selected rows for update within the current transaction.

#### forNoKeyUpdate
```java
@NonNull PostgresSelectQuery<T> forNoKeyUpdate();
```
Generates SQL: `SELECT ... FOR NO KEY UPDATE`

Locks rows but allows concurrent `SELECT FOR KEY SHARE`. Weaker than `FOR UPDATE`.

#### forShare
```java
@NonNull PostgresSelectQuery<T> forShare();
```
Generates SQL: `SELECT ... FOR SHARE`

Locks rows to prevent concurrent updates while allowing other `FOR SHARE` locks.

#### forKeyShare
```java
@NonNull PostgresSelectQuery<T> forKeyShare();
```
Generates SQL: `SELECT ... FOR KEY SHARE`

The weakest row lock. Prevents deletion and key column updates.

#### skipLocked
```java
@NonNull PostgresSelectQuery<T> skipLocked();
```
Generates SQL: `SELECT ... FOR UPDATE SKIP LOCKED`

Skips rows that are already locked by other transactions. Useful for implementing job queues.

#### noWait
```java
@NonNull PostgresSelectQuery<T> noWait();
```
Generates SQL: `SELECT ... FOR UPDATE NOWAIT`

Fails immediately instead of waiting if a lock cannot be acquired.

---

## Supporting Types

### PostgresIndexMethod (new enum)
```java
public enum PostgresIndexMethod {
    BTREE,
    HASH,
    GIN,
    GIST,
    SPGIST,
    BRIN
}
```

### PostgresReplicaIdentity (new enum)
```java
public enum PostgresReplicaIdentity {
    DEFAULT,
    NOTHING,
    FULL,
    INDEX
}
```
