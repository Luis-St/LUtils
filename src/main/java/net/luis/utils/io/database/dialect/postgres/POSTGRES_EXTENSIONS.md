# PostgreSQL Dialect Extension Guide

## Current State

Summary of what already exists (list the interfaces and their current methods briefly).

- **PostgresColumn\<T\>**: `similarTo`, `posixRegex`, `array()`, `json()`, `ilike`, `notIlike`, `arrayContains`, `arrayOverlaps`, `arrayAgg`, `jsonExtract`, `jsonExists`, `fullTextSearch`
- **PostgresTable\<T\>**: `select()` (returning PostgresSelectQuery), `setUnlogged`, `setTablespace`, `partitionByRange`, `partitionByList`, `partitionByHash`, `enableRowLevelSecurity`
- **PostgresSelectQuery\<T\>**: `distinctOn`
- **PostgresJsonOps** (extends SqlJsonOps which has `get`, `hasKey`, `getAsText`): `containsJson`, `containedBy`, `getPath`
- **SqlArrayOps\<T\>** (accessed via `PostgresColumn.array()`): `contains`, `overlaps`, `length`
- **SqlColumn\<T\>** (base): `equalTo`, `notEqualTo`, `greaterThan`, `greaterThanOrEqualTo`, `lessThan`, `lessThanOrEqualTo`, `between`, `isNull`, `isNotNull`, `like`, `startsWith`, `contains`, `endsWith`, `lengthGreaterThan`, `equalToIgnoreCase`, `withinLast`, `in`, `notIn`, `ifNull`, `as`, `asc`, `desc`, `nullsFirst`, `nullsLast`

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

### Array Operations

#### arrayAppend
```java
@NonNull SqlExpression<?> arrayAppend(@NonNull T element);
```
Generates SQL: `array_append(column, element)`

Appends an element to the end of an array.

#### arrayPrepend
```java
@NonNull SqlExpression<?> arrayPrepend(@NonNull T element);
```
Generates SQL: `array_prepend(element, column)`

Prepends an element to the beginning of an array.

#### arrayRemove
```java
@NonNull SqlExpression<?> arrayRemove(@NonNull T element);
```
Generates SQL: `array_remove(column, element)`

Removes all occurrences of the specified element from the array.

#### arrayPosition
```java
@NonNull SqlExpression<Integer> arrayPosition(@NonNull T element);
```
Generates SQL: `array_position(column, element)`

Returns the index of the first occurrence of the element in the array (1-based), or null if not found.

#### arrayCat
```java
@NonNull SqlExpression<?> arrayCat(@NonNull SqlColumn<?> other);
```
Generates SQL: `array_cat(column, other)`

Concatenates two arrays.

#### arrayReplace
```java
@NonNull SqlExpression<?> arrayReplace(@NonNull T oldElement, @NonNull T newElement);
```
Generates SQL: `array_replace(column, oldElement, newElement)`

Replaces all occurrences of `oldElement` with `newElement` in the array.

#### unnest
```java
@NonNull SqlExpression<?> unnest();
```
Generates SQL: `unnest(column)`

Expands an array column into a set of rows (one row per array element). Commonly used in `FROM` clause or `LATERAL` joins.

#### arrayDims
```java
@NonNull SqlExpression<String> arrayDims();
```
Generates SQL: `array_dims(column)`

Returns a text representation of the array's dimensions.

#### arrayNdims
```java
@NonNull SqlExpression<Integer> arrayNdims();
```
Generates SQL: `array_ndims(column)`

Returns the number of dimensions of the array.

#### arraySlice
```java
@NonNull SqlExpression<?> arraySlice(int from, int to);
```
Generates SQL: `column[from:to]`

Returns a sub-array from the specified range (1-based inclusive bounds).

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

## PostgresJsonOps Extensions

### Mutation

#### jsonbSet
```java
@NonNull SqlExpression<?> jsonbSet(@NonNull String path, @NonNull String value);
```
Generates SQL: `jsonb_set(column, '{path}', 'value')`

Sets the value at the specified path in a JSONB document. Creates the key if it does not exist.

#### jsonbInsert
```java
@NonNull SqlExpression<?> jsonbInsert(@NonNull String path, @NonNull String value);
```
Generates SQL: `jsonb_insert(column, '{path}', 'value')`

Inserts a value at the specified path. Unlike `jsonb_set`, does not replace existing values.

#### jsonbDeleteKey
```java
@NonNull SqlExpression<?> jsonbDeleteKey(@NonNull String key);
```
Generates SQL: `column - 'key'`

Deletes a top-level key from a JSONB document.

#### jsonbDeletePath
```java
@NonNull SqlExpression<?> jsonbDeletePath(@NonNull String path);
```
Generates SQL: `column #- '{path}'`

Deletes the value at the specified nested path from a JSONB document.

#### jsonbConcat
```java
@NonNull SqlExpression<?> jsonbConcat(@NonNull String json);
```
Generates SQL: `column || 'json'::jsonb`

Concatenates two JSONB values. If both are objects, the right-hand side keys take precedence.

### Introspection

#### jsonbTypeof
```java
@NonNull SqlExpression<String> jsonbTypeof();
```
Generates SQL: `jsonb_typeof(column)`

Returns the type of the top-level JSONB value as a string (`object`, `array`, `string`, `number`, `boolean`, `null`).

#### jsonbObjectKeys
```java
@NonNull SqlExpression<?> jsonbObjectKeys();
```
Generates SQL: `jsonb_object_keys(column)`

Returns the set of keys in the top-level JSONB object as a set of text values.

#### jsonbArrayLength
```java
@NonNull SqlExpression<Integer> jsonbArrayLength();
```
Generates SQL: `jsonb_array_length(column)`

Returns the number of elements in a JSONB array.

#### jsonbArrayElements
```java
@NonNull SqlExpression<?> jsonbArrayElements();
```
Generates SQL: `jsonb_array_elements(column)`

Expands a JSONB array into a set of JSONB values (one per row).

#### jsonbArrayElementsText
```java
@NonNull SqlExpression<?> jsonbArrayElementsText();
```
Generates SQL: `jsonb_array_elements_text(column)`

Expands a JSONB array into a set of text values.

### Formatting & Validation

#### jsonbPretty
```java
@NonNull SqlExpression<String> jsonbPretty();
```
Generates SQL: `jsonb_pretty(column)`

Returns the JSONB value as indented, human-readable text.

#### jsonbStripNulls
```java
@NonNull SqlExpression<?> jsonbStripNulls();
```
Generates SQL: `jsonb_strip_nulls(column)`

Recursively removes all object keys with null values from a JSONB document.

### Key Existence

#### hasAnyKey
```java
@NonNull SqlCondition hasAnyKey(String @NonNull ... keys);
```
Generates SQL: `column ?| array['key1', 'key2']`

Checks if the JSONB document contains any of the given keys.

#### hasAllKeys
```java
@NonNull SqlCondition hasAllKeys(String @NonNull ... keys);
```
Generates SQL: `column ?& array['key1', 'key2']`

Checks if the JSONB document contains all of the given keys.

### Aggregation

#### jsonbAgg
```java
@NonNull SqlExpression<?> jsonbAgg();
```
Generates SQL: `jsonb_agg(column)`

Aggregates values from grouped rows into a JSONB array.

#### jsonbObjectAgg
```java
@NonNull SqlExpression<?> jsonbObjectAgg(@NonNull SqlColumn<String> keyColumn);
```
Generates SQL: `jsonb_object_agg(keyColumn, column)`

Aggregates key-value pairs from grouped rows into a JSONB object.

---

## SqlArrayOps Extensions

### Mutation

#### append
```java
@NonNull SqlExpression<?> append(@NonNull T element);
```
Generates SQL: `array_append(column, element)`

Appends an element to the end of the array.

#### prepend
```java
@NonNull SqlExpression<?> prepend(@NonNull T element);
```
Generates SQL: `array_prepend(element, column)`

Prepends an element to the beginning of the array.

#### remove
```java
@NonNull SqlExpression<?> remove(@NonNull T element);
```
Generates SQL: `array_remove(column, element)`

Removes all occurrences of the specified element.

#### replace
```java
@NonNull SqlExpression<?> replace(@NonNull T oldElement, @NonNull T newElement);
```
Generates SQL: `array_replace(column, oldElement, newElement)`

Replaces all occurrences of `oldElement` with `newElement`.

#### cat
```java
@NonNull SqlExpression<?> cat(@NonNull SqlColumn<?> other);
```
Generates SQL: `array_cat(column, other)`

Concatenates this array with another.

### Inspection

#### position
```java
@NonNull SqlExpression<Integer> position(@NonNull T element);
```
Generates SQL: `array_position(column, element)`

Returns the 1-based index of the first occurrence, or null if not found.

#### dims
```java
@NonNull SqlExpression<String> dims();
```
Generates SQL: `array_dims(column)`

Returns a text representation of the array dimensions.

#### ndims
```java
@NonNull SqlExpression<Integer> ndims();
```
Generates SQL: `array_ndims(column)`

Returns the number of dimensions.

### Transformation

#### unnest
```java
@NonNull SqlExpression<?> unnest();
```
Generates SQL: `unnest(column)`

Expands the array into a set of rows.

#### slice
```java
@NonNull SqlExpression<?> slice(int from, int to);
```
Generates SQL: `column[from:to]`

Returns a sub-array (1-based inclusive bounds).

#### distinct
```java
@NonNull SqlExpression<?> distinct();
```
Generates SQL: `ARRAY(SELECT DISTINCT unnest(column))`

Returns the array with duplicate elements removed.

#### sort
```java
@NonNull SqlExpression<?> sort();
```
Generates SQL: `ARRAY(SELECT unnest(column) ORDER BY 1)`

Returns the array with elements sorted.

### Aggregation

#### arrayAgg
```java
@NonNull SqlExpression<?> arrayAgg();
```
Generates SQL: `array_agg(column)`

Aggregates values from grouped rows into an array.

### Containment

#### containsAll
```java
@NonNull SqlCondition containsAll(@NonNull List<T> elements);
```
Generates SQL: `column @> ARRAY[...]`

Checks if the array contains all of the given elements.

#### isContainedBy
```java
@NonNull SqlCondition isContainedBy(@NonNull List<T> elements);
```
Generates SQL: `column <@ ARRAY[...]`

Checks if the array is contained by the given array.

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

#### refreshMaterializedView
```java
void refreshMaterializedView(@NonNull String name);
```
Generates SQL: `REFRESH MATERIALIZED VIEW name`

Refreshes the data in a materialized view.

#### refreshMaterializedViewConcurrently
```java
void refreshMaterializedViewConcurrently(@NonNull String name);
```
Generates SQL: `REFRESH MATERIALIZED VIEW CONCURRENTLY name`

Refreshes the materialized view without locking out reads. Requires a unique index on the view.

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

### Constraints

#### addExclusionConstraint
```java
void addExclusionConstraint(@NonNull String name, @NonNull String exclusionElements);
```
Generates SQL: `ALTER TABLE ... ADD CONSTRAINT name EXCLUDE USING gist (exclusionElements)`

Adds an exclusion constraint. Ensures that no two rows satisfy a given predicate simultaneously. Commonly used for range non-overlap constraints.

#### addCheckConstraint
```java
void addCheckConstraint(@NonNull String name, @NonNull SqlCondition condition);
```
Generates SQL: `ALTER TABLE ... ADD CONSTRAINT name CHECK (condition)`

Adds a named check constraint.

### Row-Level Security

#### addPolicy
```java
void addPolicy(@NonNull String name, @NonNull String using);
```
Generates SQL: `CREATE POLICY name ON table USING (using)`

Creates a row-level security policy. Requires `enableRowLevelSecurity()` to be called first.

#### addPolicy (with check)
```java
void addPolicy(@NonNull String name, @NonNull String using, @NonNull String withCheck);
```
Generates SQL: `CREATE POLICY name ON table USING (using) WITH CHECK (withCheck)`

Creates a row-level security policy with separate read and write conditions.

#### dropPolicy
```java
void dropPolicy(@NonNull String name);
```
Generates SQL: `DROP POLICY name ON table`

Drops a row-level security policy.

### Partitioning

#### attachPartition
```java
void attachPartition(@NonNull String partitionName, @NonNull String bound);
```
Generates SQL: `ALTER TABLE ... ATTACH PARTITION partitionName bound`

Attaches an existing table as a partition with the given bound (e.g., `FOR VALUES FROM ('2024-01-01') TO ('2025-01-01')`).

#### detachPartition
```java
void detachPartition(@NonNull String partitionName);
```
Generates SQL: `ALTER TABLE ... DETACH PARTITION partitionName`

Detaches a partition from the partitioned table.

#### detachPartitionConcurrently
```java
void detachPartitionConcurrently(@NonNull String partitionName);
```
Generates SQL: `ALTER TABLE ... DETACH PARTITION partitionName CONCURRENTLY`

Detaches a partition without blocking queries on the parent table. Available since PostgreSQL 14.

#### setDefaultPartition
```java
void setDefaultPartition(@NonNull String partitionName);
```
Generates SQL: `ALTER TABLE ... ATTACH PARTITION partitionName DEFAULT`

Designates a partition as the default partition for rows not matching any other partition bounds.

### Storage & Maintenance

#### setFillFactor
```java
void setFillFactor(int percent);
```
Generates SQL: `ALTER TABLE ... SET (fillfactor = percent)`

Sets the fill factor (10-100). Lower values leave more free space per page for updates, reducing page splits.

#### setAutovacuumEnabled
```java
void setAutovacuumEnabled(boolean enabled);
```
Generates SQL: `ALTER TABLE ... SET (autovacuum_enabled = enabled)`

Enables or disables autovacuum for this table.

#### cluster
```java
void cluster(@NonNull String indexName);
```
Generates SQL: `CLUSTER table USING indexName`

Physically reorders the table data according to the specified index. Improves range scan performance.

#### vacuum
```java
void vacuum();
```
Generates SQL: `VACUUM table`

Reclaims storage occupied by dead tuples.

#### vacuumFull
```java
void vacuumFull();
```
Generates SQL: `VACUUM FULL table`

Compacts the table by rewriting it entirely. Requires exclusive lock.

#### analyze
```java
void analyze();
```
Generates SQL: `ANALYZE table`

Updates planner statistics for better query planning.

### Schema & Replication

#### setSchema
```java
void setSchema(@NonNull String schema);
```
Generates SQL: `ALTER TABLE ... SET SCHEMA schema`

Moves this table to a different schema.

#### enableReplicaIdentityFull
```java
void enableReplicaIdentityFull();
```
Generates SQL: `ALTER TABLE ... REPLICA IDENTITY FULL`

Sets replica identity to `FULL`, sending the entire old row for logical replication `UPDATE` and `DELETE` operations.

#### enableReplicaIdentityIndex
```java
void enableReplicaIdentityIndex(@NonNull String indexName);
```
Generates SQL: `ALTER TABLE ... REPLICA IDENTITY USING INDEX indexName`

Sets replica identity to use the specified unique index.

#### setComment
```java
void setComment(@NonNull String comment);
```
Generates SQL: `COMMENT ON TABLE table IS 'comment'`

Sets a descriptive comment on the table.

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
