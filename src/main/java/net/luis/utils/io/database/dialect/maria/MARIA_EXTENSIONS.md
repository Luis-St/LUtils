# MariaDB Dialect Extension Guide

## Current State

Summary of current interfaces including inherited MySQL methods.

## MariaColumn Extensions (beyond MySQL)

### Dynamic Columns

#### columnAdd
```java
@NonNull SqlExpression<?> columnAdd(@NonNull String name, @NonNull String type, @NonNull Object value);
```
Generates SQL: `COLUMN_ADD(column, 'name', value AS type)`

Adds or updates a key-value pair in a dynamic column blob. Dynamic columns allow storing different sets of columns per row without schema changes.

#### columnGet
```java
@NonNull SqlExpression<?> columnGet(@NonNull String name, @NonNull String type);
```
Generates SQL: `COLUMN_GET(column, 'name' AS type)`

Retrieves a value from a dynamic column blob, casting it to the specified type.

#### columnDelete
```java
@NonNull SqlExpression<?> columnDelete(@NonNull String name);
```
Generates SQL: `COLUMN_DELETE(column, 'name')`

Removes a key from a dynamic column blob.

#### columnExists
```java
@NonNull SqlCondition columnExists(@NonNull String name);
```
Generates SQL: `COLUMN_EXISTS(column, 'name')`

Checks if a key exists in a dynamic column blob.

#### columnList
```java
@NonNull SqlExpression<?> columnList();
```
Generates SQL: `COLUMN_LIST(column)`

Returns a comma-separated list of all column names stored in the dynamic column blob.

#### columnJson
```java
@NonNull SqlExpression<String> columnJson();
```
Generates SQL: `COLUMN_JSON(column)`

Converts a dynamic column blob to a JSON string representation.

### JSON Functions (MariaDB-specific)

#### jsonQuery
```java
@NonNull SqlExpression<?> jsonQuery(@NonNull String path);
```
Generates SQL: `JSON_QUERY(column, 'path')`

Extracts a JSON object or array at the specified path. Unlike `JSON_VALUE` which returns a scalar, `JSON_QUERY` returns structured JSON.

#### jsonExists
```java
@NonNull SqlCondition jsonExists(@NonNull String path);
```
Generates SQL: `JSON_EXISTS(column, 'path')`

Returns true if the specified path exists in the JSON document.

#### jsonInsert
```java
@NonNull SqlExpression<?> jsonInsert(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_INSERT(column, 'path', 'value')`

Inserts a value at the path if it does not already exist. Does not replace existing values.

#### jsonReplace
```java
@NonNull SqlExpression<?> jsonReplace(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_REPLACE(column, 'path', 'value')`

Replaces the value at the path if it exists. Does nothing if the path doesn't exist.

#### jsonSet
```java
@NonNull SqlExpression<?> jsonSet(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_SET(column, 'path', 'value')`

Sets the value at the path. Creates the key if it doesn't exist, replaces if it does.

#### jsonRemove
```java
@NonNull SqlExpression<?> jsonRemove(@NonNull String path);
```
Generates SQL: `JSON_REMOVE(column, 'path')`

Removes the value at the specified path.

#### jsonArrayAppend
```java
@NonNull SqlExpression<?> jsonArrayAppend(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_ARRAY_APPEND(column, 'path', 'value')`

Appends a value to the array at the specified path.

#### jsonArrayInsert
```java
@NonNull SqlExpression<?> jsonArrayInsert(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_ARRAY_INSERT(column, 'path', 'value')`

Inserts a value at the specified position in a JSON array.

#### jsonMergePatch
```java
@NonNull SqlExpression<?> jsonMergePatch(@NonNull String json);
```
Generates SQL: `JSON_MERGE_PATCH(column, 'json')`

Merges using RFC 7396 semantics. Null values in the patch remove keys.

#### jsonMergePreserve
```java
@NonNull SqlExpression<?> jsonMergePreserve(@NonNull String json);
```
Generates SQL: `JSON_MERGE_PRESERVE(column, 'json')`

Merges preserving all values. Arrays are concatenated.

#### jsonType
```java
@NonNull SqlExpression<String> jsonType();
```
Generates SQL: `JSON_TYPE(column)`

Returns the type of the JSON value.

#### jsonLength
```java
@NonNull SqlExpression<Integer> jsonLength();
```
Generates SQL: `JSON_LENGTH(column)`

Returns the length of the JSON document.

#### jsonDepth
```java
@NonNull SqlExpression<Integer> jsonDepth();
```
Generates SQL: `JSON_DEPTH(column)`

Returns the maximum nesting depth.

#### jsonKeys
```java
@NonNull SqlExpression<?> jsonKeys();
```
Generates SQL: `JSON_KEYS(column)`

Returns the keys of the top-level JSON object.

#### jsonValid
```java
@NonNull SqlCondition jsonValid();
```
Generates SQL: `JSON_VALID(column)`

Returns true if the column contains valid JSON.

#### jsonSearch
```java
@NonNull SqlExpression<?> jsonSearch(@NonNull String value);
```
Generates SQL: `JSON_SEARCH(column, 'one', 'value')`

Returns the path to the first occurrence of the string.

#### jsonContainsPath
```java
@NonNull SqlCondition jsonContainsPath(String @NonNull ... paths);
```
Generates SQL: `JSON_CONTAINS_PATH(column, 'one', 'path1', ...)`

Returns true if any of the paths exist.

#### jsonDetailed
```java
@NonNull SqlExpression<String> jsonDetailed();
```
Generates SQL: `JSON_DETAILED(column)`

MariaDB-specific function that returns a pretty-printed version of a JSON document with type annotations.

#### jsonLoose
```java
@NonNull SqlExpression<String> jsonLoose();
```
Generates SQL: `JSON_LOOSE(column)`

MariaDB-specific function that adds spaces to a compact JSON representation for readability.

#### jsonCompact
```java
@NonNull SqlExpression<String> jsonCompact();
```
Generates SQL: `JSON_COMPACT(column)`

MariaDB-specific function that removes all unnecessary spaces from a JSON document.

### Oracle Compatibility

#### oracleDecode
```java
@NonNull SqlExpression<?> oracleDecode(Object @NonNull ... searchResults);
```
Generates SQL: `DECODE(column, search1, result1, search2, result2, ..., default)`

Oracle-compatible DECODE function. Available in MariaDB's Oracle compatibility mode.

#### rownum
```java
@NonNull SqlExpression<Long> rownum();
```
Generates SQL: `ROWNUM()`

Returns the current row number. MariaDB-specific, inspired by Oracle's ROWNUM.

### Sequence Operations

#### previousSequenceValue
```java
@NonNull SqlExpression<?> previousSequenceValue(@NonNull String sequence);
```
Generates SQL: `PREVIOUS VALUE FOR sequence`

Returns the last value generated by the specified sequence in the current session.

#### setSequenceValue
```java
@NonNull SqlExpression<?> setSequenceValue(@NonNull String sequence, long value);
```
Generates SQL: `SETVAL(sequence, value)`

Sets the current value of the sequence.

### Temporal Queries (on system-versioned columns)

#### asOf
```java
@NonNull SqlCondition asOf(@NonNull String timestamp);
```
Generates SQL: `FOR SYSTEM_TIME AS OF 'timestamp'`

Queries the system-versioned table as it existed at the specified point in time.

#### fromTo
```java
@NonNull SqlCondition fromTo(@NonNull String start, @NonNull String end);
```
Generates SQL: `FOR SYSTEM_TIME FROM 'start' TO 'end'`

Queries all versions of rows that were active during the specified period.

#### betweenSystemTime
```java
@NonNull SqlCondition betweenSystemTime(@NonNull String start, @NonNull String end);
```
Generates SQL: `FOR SYSTEM_TIME BETWEEN 'start' AND 'end'`

Queries all versions of rows that overlapped with the specified period (inclusive on both ends).

### Window Functions (MariaDB additions)

#### percentRank
```java
@NonNull SqlExpression<Double> percentRank();
```
Generates SQL: `PERCENT_RANK() OVER (...)`

Returns the relative rank as a percentage (0 to 1).

#### cumeDist
```java
@NonNull SqlExpression<Double> cumeDist();
```
Generates SQL: `CUME_DIST() OVER (...)`

Returns the cumulative distribution (fraction of rows with values less than or equal to the current row).

#### ntile
```java
@NonNull SqlExpression<Integer> ntile(int buckets);
```
Generates SQL: `NTILE(buckets) OVER (...)`

Divides rows into the specified number of approximately equal-sized buckets.

### Invisible Columns

#### setInvisible
```java
void setInvisible(boolean invisible);
```
Generates SQL: `ALTER TABLE ... ALTER COLUMN column SET INVISIBLE` or `SET VISIBLE`

Makes a column invisible (excluded from `SELECT *`) or visible. MariaDB 10.3+.

---

## MariaTable Extensions (beyond MySQL)

### System Versioning

#### setSystemVersionedWithPeriod
```java
void setSystemVersionedWithPeriod(@NonNull SqlColumn<?> startColumn, @NonNull SqlColumn<?> endColumn);
```
Generates SQL: `ALTER TABLE ... ADD PERIOD FOR SYSTEM_TIME (start, end), ADD SYSTEM VERSIONING`

Enables system versioning with explicit period columns for tracking row validity.

#### addHistoryPartition
```java
void addHistoryPartition(@NonNull String partitionName, @NonNull String until);
```
Generates SQL: `ALTER TABLE ... ADD PARTITION (PARTITION partitionName HISTORY BEFORE 'until')`

Adds a history partition for system-versioned tables. Allows partitioning historical data by time.

#### dropHistoryBefore
```java
void dropHistoryBefore(@NonNull String timestamp);
```
Generates SQL: `DELETE HISTORY FROM table BEFORE SYSTEM_TIME 'timestamp'`

Deletes historical rows older than the specified timestamp. Only works on system-versioned tables.

### Application-Time Periods

#### addPeriod
```java
void addPeriod(@NonNull String name, @NonNull SqlColumn<?> startColumn, @NonNull SqlColumn<?> endColumn);
```
Generates SQL: `ALTER TABLE ... ADD PERIOD FOR name (start, end)`

Defines an application-time period. Allows temporal queries based on application-defined time ranges.

#### addWithoutOverlaps
```java
void addUniqueWithoutOverlaps(@NonNull String name, @NonNull SqlColumn<?> column, @NonNull String periodName);
```
Generates SQL: `ALTER TABLE ... ADD UNIQUE (column, periodName WITHOUT OVERLAPS)`

Adds a unique constraint that prevents overlapping periods for the same key value.

### Storage Options

#### setPageCompressed
```java
void setPageCompressed(boolean enabled);
```
Generates SQL: `ALTER TABLE ... PAGE_COMPRESSED=1` or `PAGE_COMPRESSED=0`

Enables or disables page-level compression. MariaDB-specific alternative to InnoDB compression.

#### setPageCompressionLevel
```java
void setPageCompressionLevel(int level);
```
Generates SQL: `ALTER TABLE ... PAGE_COMPRESSION_LEVEL=level`

Sets the page compression level (1-9). Higher values give better compression but use more CPU.

#### setTransactional
```java
void setTransactional(boolean transactional);
```
Generates SQL: `ALTER TABLE ... TRANSACTIONAL=1` or `TRANSACTIONAL=0`

Enables transactional support for Aria engine tables.

### Sequences

#### createOrReplaceSequence
```java
void createOrReplaceSequence(@NonNull SqlSequenceDefinition definition);
```
Generates SQL: `CREATE OR REPLACE SEQUENCE ...`

Creates a sequence, replacing it if it already exists. MariaDB supports this atomic operation.

#### alterSequenceRestart
```java
void alterSequenceRestart(@NonNull String sequenceName, long value);
```
Generates SQL: `ALTER SEQUENCE sequenceName RESTART WITH value`

Restarts a sequence at the specified value.

### Table Operations

#### createOrReplace
```java
void createOrReplace();
```
Generates SQL: `CREATE OR REPLACE TABLE ...`

Creates the table, atomically dropping and recreating it if it already exists.

#### addCheckConstraint
```java
void addCheckConstraint(@NonNull String name, @NonNull SqlCondition condition);
```
Generates SQL: `ALTER TABLE ... ADD CONSTRAINT name CHECK (condition)`

Adds a named check constraint. MariaDB has supported named check constraints since 10.2.

#### setTableComment
```java
void setTableComment(@NonNull String comment);
```
Generates SQL: `ALTER TABLE ... COMMENT='comment'`

Sets or updates the table comment.

### Partitioning (MariaDB-specific extensions)

#### partitionBySystemTime
```java
void partitionBySystemTime(int partitions);
```
Generates SQL: `PARTITION BY SYSTEM_TIME PARTITIONS n`

Partitions a system-versioned table by system time. History is split across partitions.

#### partitionBySystemTimeInterval
```java
void partitionBySystemTimeInterval(@NonNull String interval);
```
Generates SQL: `PARTITION BY SYSTEM_TIME INTERVAL interval`

Partitions by system time using automatic time-based intervals.

#### addPartition
```java
void addPartition(@NonNull String name, @NonNull String definition);
```
Generates SQL: `ALTER TABLE ... ADD PARTITION (PARTITION name definition)`

Adds a new partition to the table.

#### dropPartition
```java
void dropPartition(@NonNull String name);
```
Generates SQL: `ALTER TABLE ... DROP PARTITION name`

Drops a partition and its data.

#### reorganizePartition
```java
void reorganizePartition(@NonNull String name, @NonNull String newDefinition);
```
Generates SQL: `ALTER TABLE ... REORGANIZE PARTITION name INTO (newDefinition)`

Reorganizes an existing partition into a new layout.

---

## Supporting Types

### MariaSystemTimeMode (new enum)
```java
public enum MariaSystemTimeMode {
    AS_OF,
    FROM_TO,
    BETWEEN,
    ALL
}
```

### Note on MariaDB-specific engines
MariaDB supports additional engines beyond MySQL:
- **Aria** - crash-safe MyISAM replacement
- **ColumnStore** - columnar analytical engine
- **S3** - read-only tables in Amazon S3
- **Spider** - distributed/sharded engine
- **CONNECT** - access external data sources
- **Mroonga** - full-text search engine

Consider extending `MysqlEngine` or creating a `MariaEngine` enum:
```java
public enum MariaEngine {
    INNODB,
    ARIA,
    MYISAM,
    MEMORY,
    CSV,
    ARCHIVE,
    COLUMNSTORE,
    S3,
    SPIDER,
    CONNECT,
    MROONGA,
    BLACKHOLE,
    FEDERATED,
    OQGRAPH
}
```
