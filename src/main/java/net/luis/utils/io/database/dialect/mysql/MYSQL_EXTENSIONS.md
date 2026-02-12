# MySQL Dialect Extension Guide

## Current State

Summary of what already exists.

## MysqlColumn Extensions

### Pattern Matching & Regular Expressions

#### regexpReplace
```java
@NonNull SqlExpression<String> regexpReplace(@NonNull Pattern pattern, @NonNull String replacement);
```
Generates SQL: `REGEXP_REPLACE(column, 'pattern', 'replacement')`

Replaces all substrings matching the regular expression with the replacement string. Available since MySQL 8.0.

#### regexpInstr
```java
@NonNull SqlExpression<Integer> regexpInstr(@NonNull Pattern pattern);
```
Generates SQL: `REGEXP_INSTR(column, 'pattern')`

Returns the starting position of the first match of the pattern (1-based), or 0 if no match. Available since MySQL 8.0.

#### regexpSubstr
```java
@NonNull SqlExpression<String> regexpSubstr(@NonNull Pattern pattern);
```
Generates SQL: `REGEXP_SUBSTR(column, 'pattern')`

Returns the substring matching the regular expression, or null if no match. Available since MySQL 8.0.

#### regexpLike
```java
@NonNull SqlCondition regexpLike(@NonNull Pattern pattern);
```
Generates SQL: `REGEXP_LIKE(column, 'pattern')`

Function form of the REGEXP operator. Supports optional match type parameter. Available since MySQL 8.0.

#### soundsLike
```java
@NonNull SqlCondition soundsLike(@NonNull String value);
```
Generates SQL: `column SOUNDS LIKE 'value'`

Phonetic comparison using the SOUNDEX algorithm. Returns true if both sides produce the same SOUNDEX string.

### Full-Text Search

#### matchAgainstBoolean
```java
@NonNull SqlCondition matchAgainstBoolean(@NonNull String searchTerms);
```
Generates SQL: `MATCH(column) AGAINST('searchTerms' IN BOOLEAN MODE)`

Full-text search in boolean mode. Supports operators: `+` (must include), `-` (must exclude), `*` (wildcard), `"..."` (phrase), `>` / `<` (relevance modifier).

#### matchAgainstExpansion
```java
@NonNull SqlCondition matchAgainstExpansion(@NonNull String searchTerms);
```
Generates SQL: `MATCH(column) AGAINST('searchTerms' WITH QUERY EXPANSION)`

Full-text search with automatic query expansion. Performs the search twice: first normally, then again with the most relevant words from the first search added to the query.

#### matchAgainstNaturalLanguage
```java
@NonNull SqlExpression<Double> matchAgainstRelevance(@NonNull String searchTerms);
```
Generates SQL: `MATCH(column) AGAINST('searchTerms' IN NATURAL LANGUAGE MODE)`

Returns the relevance score of the full-text search match. Can be used in SELECT and ORDER BY.

### Conditional Functions

#### ifExpression
```java
@NonNull SqlExpression<?> ifExpression(@NonNull SqlCondition condition, @NonNull T ifTrue, @NonNull T ifFalse);
```
Generates SQL: `IF(condition, ifTrue, ifFalse)`

MySQL-specific conditional function. Returns `ifTrue` if the condition is true, otherwise `ifFalse`.

#### nullIf
```java
@NonNull SqlExpression<?> nullIf(@NonNull T value);
```
Generates SQL: `NULLIF(column, value)`

Returns null if the column equals the given value, otherwise returns the column value.

#### fieldIndex
```java
@NonNull SqlExpression<Integer> fieldIndex(@SuppressWarnings("unchecked") T @NonNull ... values);
```
Generates SQL: `FIELD(column, val1, val2, val3, ...)`

Returns the 1-based index of the column value in the list of values, or 0 if not found. Useful for custom ordering in `ORDER BY`.

### String Functions

#### groupConcatOrdered
```java
@NonNull SqlExpression<?> groupConcatOrdered(@NonNull String separator, @NonNull SqlColumn<?> orderBy);
```
Generates SQL: `GROUP_CONCAT(column ORDER BY orderBy SEPARATOR 'separator')`

Concatenates grouped values with ordering and a custom separator.

#### groupConcatDistinct
```java
@NonNull SqlExpression<?> groupConcatDistinct(@NonNull String separator);
```
Generates SQL: `GROUP_CONCAT(DISTINCT column SEPARATOR 'separator')`

Concatenates distinct grouped values with a custom separator.

#### elt
```java
@NonNull SqlExpression<?> elt(int index);
```
Generates SQL: `ELT(index, column)`

Returns the Nth element from a list of strings. Complementary to `FIELD`.

#### exportSet
```java
@NonNull SqlExpression<String> exportSet(int bits, @NonNull String on, @NonNull String off);
```
Generates SQL: `EXPORT_SET(bits, 'on', 'off')`

Returns a string representing the bit values, with custom strings for on/off bits.

### Type Conversion

#### convert
```java
@NonNull SqlExpression<?> convert(@NonNull String charset);
```
Generates SQL: `CONVERT(column USING charset)`

Converts the column value to a different character set.

#### castAsSigned
```java
@NonNull SqlExpression<Long> castAsSigned();
```
Generates SQL: `CAST(column AS SIGNED)`

Casts the column value to a signed integer.

#### castAsUnsigned
```java
@NonNull SqlExpression<Long> castAsUnsigned();
```
Generates SQL: `CAST(column AS UNSIGNED)`

Casts the column value to an unsigned integer.

#### castAsChar
```java
@NonNull SqlExpression<String> castAsChar();
```
Generates SQL: `CAST(column AS CHAR)`

Casts the column value to a character string.

### Bit Operations

#### bitAnd
```java
@NonNull SqlExpression<Long> bitAnd(long mask);
```
Generates SQL: `column & mask`

Bitwise AND operation.

#### bitOr
```java
@NonNull SqlExpression<Long> bitOr(long mask);
```
Generates SQL: `column | mask`

Bitwise OR operation.

#### bitXor
```java
@NonNull SqlExpression<Long> bitXor(long mask);
```
Generates SQL: `column ^ mask`

Bitwise XOR operation.

#### bitCount
```java
@NonNull SqlExpression<Integer> bitCount();
```
Generates SQL: `BIT_COUNT(column)`

Returns the number of set bits in the value.

### Encryption & Hashing

#### hex
```java
@NonNull SqlExpression<String> hex();
```
Generates SQL: `HEX(column)`

Returns the hexadecimal representation of the column value.

#### unhex
```java
@NonNull SqlExpression<?> unhex();
```
Generates SQL: `UNHEX(column)`

Converts a hexadecimal string back to binary.

#### md5
```java
@NonNull SqlExpression<String> md5();
```
Generates SQL: `MD5(column)`

Returns the MD5 hash of the column value as a 32-character hex string.

#### sha1
```java
@NonNull SqlExpression<String> sha1();
```
Generates SQL: `SHA1(column)`

Returns the SHA-1 hash of the column value as a 40-character hex string.

#### sha2
```java
@NonNull SqlExpression<String> sha2(int hashLength);
```
Generates SQL: `SHA2(column, hashLength)`

Returns the SHA-2 hash with the specified bit length (224, 256, 384, 512).

### Formatting

#### format
```java
@NonNull SqlExpression<String> format(int decimals);
```
Generates SQL: `FORMAT(column, decimals)`

Formats a number with the specified number of decimal places, adding thousand separators.

#### formatLocale
```java
@NonNull SqlExpression<String> formatLocale(int decimals, @NonNull String locale);
```
Generates SQL: `FORMAT(column, decimals, 'locale')`

Formats a number with locale-specific separators.

### Network Functions

#### inetAton
```java
@NonNull SqlExpression<Long> inetAton();
```
Generates SQL: `INET_ATON(column)`

Converts an IPv4 address string to a 32-bit unsigned integer.

#### inetNtoa
```java
@NonNull SqlExpression<String> inetNtoa();
```
Generates SQL: `INET_NTOA(column)`

Converts a 32-bit unsigned integer to an IPv4 address string.

#### inet6Aton
```java
@NonNull SqlExpression<?> inet6Aton();
```
Generates SQL: `INET6_ATON(column)`

Converts an IPv6 or IPv4 address string to a binary representation.

#### inet6Ntoa
```java
@NonNull SqlExpression<String> inet6Ntoa();
```
Generates SQL: `INET6_NTOA(column)`

Converts a binary representation back to an IP address string.

### JSON Aggregation

#### jsonArrayAgg
```java
@NonNull SqlExpression<?> jsonArrayAgg();
```
Generates SQL: `JSON_ARRAYAGG(column)`

Aggregates column values from grouped rows into a JSON array.

#### jsonObjectAgg
```java
@NonNull SqlExpression<?> jsonObjectAgg(@NonNull SqlColumn<String> keyColumn);
```
Generates SQL: `JSON_OBJECTAGG(keyColumn, column)`

Aggregates key-value pairs from grouped rows into a JSON object.

---

## MysqlJsonOps Extensions

### Mutation

#### jsonSet
```java
@NonNull SqlExpression<?> jsonSet(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_SET(column, 'path', 'value')`

Sets the value at the specified path. Creates the key if it doesn't exist, replaces if it does.

#### jsonInsert
```java
@NonNull SqlExpression<?> jsonInsert(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_INSERT(column, 'path', 'value')`

Inserts a value at the specified path. Does NOT replace existing values.

#### jsonReplace
```java
@NonNull SqlExpression<?> jsonReplace(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_REPLACE(column, 'path', 'value')`

Replaces the value at the specified path. Does nothing if the path doesn't exist.

#### jsonRemove
```java
@NonNull SqlExpression<?> jsonRemove(@NonNull String path);
```
Generates SQL: `JSON_REMOVE(column, 'path')`

Removes the value at the specified path from the JSON document.

#### jsonMergePatch
```java
@NonNull SqlExpression<?> jsonMergePatch(@NonNull String json);
```
Generates SQL: `JSON_MERGE_PATCH(column, 'json')`

Merges JSON documents using RFC 7396 merge-patch semantics. Null values in the patch remove keys.

#### jsonMergePreserve
```java
@NonNull SqlExpression<?> jsonMergePreserve(@NonNull String json);
```
Generates SQL: `JSON_MERGE_PRESERVE(column, 'json')`

Merges JSON documents, preserving all values (arrays are concatenated, objects are merged).

#### jsonArrayAppend
```java
@NonNull SqlExpression<?> jsonArrayAppend(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_ARRAY_APPEND(column, 'path', 'value')`

Appends a value to the end of the array at the specified path.

#### jsonArrayInsert
```java
@NonNull SqlExpression<?> jsonArrayInsert(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_ARRAY_INSERT(column, 'path', 'value')`

Inserts a value at the specified position in a JSON array.

### Introspection

#### jsonType
```java
@NonNull SqlExpression<String> jsonType();
```
Generates SQL: `JSON_TYPE(column)`

Returns the type of the JSON value (`OBJECT`, `ARRAY`, `STRING`, `INTEGER`, `DOUBLE`, `BOOLEAN`, `NULL`).

#### jsonLength
```java
@NonNull SqlExpression<Integer> jsonLength();
```
Generates SQL: `JSON_LENGTH(column)`

Returns the length of the JSON document (number of keys for objects, number of elements for arrays).

#### jsonLengthAtPath
```java
@NonNull SqlExpression<Integer> jsonLengthAtPath(@NonNull String path);
```
Generates SQL: `JSON_LENGTH(column, 'path')`

Returns the length of the JSON value at the specified path.

#### jsonDepth
```java
@NonNull SqlExpression<Integer> jsonDepth();
```
Generates SQL: `JSON_DEPTH(column)`

Returns the maximum nesting depth of the JSON document.

#### jsonKeys
```java
@NonNull SqlExpression<?> jsonKeys();
```
Generates SQL: `JSON_KEYS(column)`

Returns the keys of the top-level JSON object as a JSON array.

#### jsonKeysAtPath
```java
@NonNull SqlExpression<?> jsonKeysAtPath(@NonNull String path);
```
Generates SQL: `JSON_KEYS(column, 'path')`

Returns the keys of the JSON object at the specified path as a JSON array.

### Validation & Search

#### jsonValid
```java
@NonNull SqlCondition jsonValid();
```
Generates SQL: `JSON_VALID(column)`

Returns true if the column value is valid JSON.

#### jsonSearch
```java
@NonNull SqlExpression<?> jsonSearch(@NonNull String value);
```
Generates SQL: `JSON_SEARCH(column, 'one', 'value')`

Returns the path to the first occurrence of the given string value in the JSON document.

#### jsonSearchAll
```java
@NonNull SqlExpression<?> jsonSearchAll(@NonNull String value);
```
Generates SQL: `JSON_SEARCH(column, 'all', 'value')`

Returns the paths to all occurrences of the given string value in the JSON document.

#### jsonContainsPath
```java
@NonNull SqlCondition jsonContainsPath(String @NonNull ... paths);
```
Generates SQL: `JSON_CONTAINS_PATH(column, 'one', 'path1', 'path2', ...)`

Returns true if any of the given paths exist in the JSON document.

#### jsonContainsAllPaths
```java
@NonNull SqlCondition jsonContainsAllPaths(String @NonNull ... paths);
```
Generates SQL: `JSON_CONTAINS_PATH(column, 'all', 'path1', 'path2', ...)`

Returns true if all of the given paths exist in the JSON document.

### Table Functions

#### jsonTable
```java
@NonNull SqlExpression<?> jsonTable(@NonNull String path);
```
Generates SQL: `JSON_TABLE(column, 'path' COLUMNS(...))`

Converts JSON data to a relational table format. Available since MySQL 8.0.

---

## MysqlTable Extensions

### Partitioning

#### partitionByRange
```java
void partitionByRange(@NonNull SqlColumn<?> column);
```
Generates SQL: `PARTITION BY RANGE (column)`

Configures range partitioning on this table.

#### partitionByRangeColumns
```java
void partitionByRangeColumns(SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `PARTITION BY RANGE COLUMNS (columns)`

Configures multi-column range partitioning. Supports non-integer columns directly.

#### partitionByList
```java
void partitionByList(@NonNull SqlColumn<?> column);
```
Generates SQL: `PARTITION BY LIST (column)`

Configures list partitioning on this table.

#### partitionByListColumns
```java
void partitionByListColumns(SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `PARTITION BY LIST COLUMNS (columns)`

Configures multi-column list partitioning.

#### partitionByHash
```java
void partitionByHash(@NonNull SqlColumn<?> column, int partitions);
```
Generates SQL: `PARTITION BY HASH (column) PARTITIONS n`

Configures hash partitioning with the specified number of partitions.

#### partitionByKey
```java
void partitionByKey(int partitions, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `PARTITION BY KEY (columns) PARTITIONS n`

Configures key partitioning. Similar to hash but uses MySQL's internal hash function.

### Index Types

#### addSpatialIndex
```java
void addSpatialIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE SPATIAL INDEX name ON table (columns)`

Creates a spatial index for geometry columns. Requires MyISAM or InnoDB engine.

#### addDescendingIndex
```java
void addDescendingIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table (column1 DESC, column2 DESC, ...)`

Creates an index with descending key parts. Available since MySQL 8.0.

#### addFunctionalIndex
```java
void addFunctionalIndex(@NonNull String name, @NonNull String expression);
```
Generates SQL: `CREATE INDEX name ON table ((expression))`

Creates an index on an expression (e.g., `CAST(json->>'$.key' AS UNSIGNED)`). Available since MySQL 8.0.

#### addInvisibleIndex
```java
void addInvisibleIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table (columns) INVISIBLE`

Creates an invisible index. The optimizer ignores it, useful for testing index removal impact. Available since MySQL 8.0.

---

## Supporting Types

### MysqlRowFormat (new enum)
```java
public enum MysqlRowFormat {
    DEFAULT,
    DYNAMIC,
    FIXED,
    COMPRESSED,
    REDUNDANT,
    COMPACT
}
```

### MysqlCompression (new enum)
```java
public enum MysqlCompression {
    NONE,
    ZLIB,
    LZ4
}
```

### Additional MysqlEngine entries
```java
// Consider adding to existing MysqlEngine enum:
NDBCLUSTER,  // MySQL Cluster
FEDERATED,   // Federated storage
BLACKHOLE,   // /dev/null engine
MERGE,       // Merge tables
PERFORMANCE_SCHEMA
```
