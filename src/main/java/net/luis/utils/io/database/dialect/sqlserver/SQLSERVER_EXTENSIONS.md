# SQL Server Dialect Extension Guide

## Current State

Summary of current interfaces.

## SqlServerColumn Extensions

### JSON Operations

#### jsonQuery
```java
@NonNull SqlExpression<?> jsonQuery(@NonNull String path);
```
Generates SQL: `JSON_QUERY(column, 'path')`

Extracts a JSON object or array from a JSON string. Unlike `JSON_VALUE` which returns a scalar, `JSON_QUERY` returns structured JSON.

#### jsonModify
```java
@NonNull SqlExpression<?> jsonModify(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_MODIFY(column, 'path', 'value')`

Modifies the value at the specified path in a JSON string. Can insert, update, or delete values.

#### jsonModifyAppend
```java
@NonNull SqlExpression<?> jsonModifyAppend(@NonNull String path, @NonNull String value);
```
Generates SQL: `JSON_MODIFY(column, 'append path', 'value')`

Appends a value to a JSON array at the specified path.

#### isJson
```java
@NonNull SqlCondition isJson();
```
Generates SQL: `ISJSON(column) = 1`

Returns true if the column value is valid JSON.

### Type Conversion

#### tryCast
```java
@NonNull SqlExpression<?> tryCast(@NonNull Class<?> targetType);
```
Generates SQL: `TRY_CAST(column AS targetType)`

Attempts to cast the value to the target type. Returns null instead of raising an error on failure.

#### tryParse
```java
@NonNull SqlExpression<?> tryParse(@NonNull Class<?> targetType);
```
Generates SQL: `TRY_PARSE(column AS targetType)`

Attempts to parse the string value to the target type using .NET culture-specific formatting. Returns null on failure.

#### tryParseWithCulture
```java
@NonNull SqlExpression<?> tryParseWithCulture(@NonNull Class<?> targetType, @NonNull String culture);
```
Generates SQL: `TRY_PARSE(column AS targetType USING 'culture')`

Attempts to parse using a specific culture (e.g., `'en-US'`, `'de-DE'`).

### Conditional Functions

#### iif
```java
@NonNull SqlExpression<?> iif(@NonNull SqlCondition condition, @NonNull T ifTrue, @NonNull T ifFalse);
```
Generates SQL: `IIF(condition, ifTrue, ifFalse)`

SQL Server's inline IF function. Returns `ifTrue` if condition is true, `ifFalse` otherwise.

#### choose
```java
@NonNull SqlExpression<?> choose(int index, Object @NonNull ... values);
```
Generates SQL: `CHOOSE(index, val1, val2, val3, ...)`

Returns the value at the specified 1-based index from the list of values.

### String Functions

#### stringAggOrdered
```java
@NonNull SqlExpression<?> stringAggOrdered(@NonNull String separator, @NonNull SqlColumn<?> orderBy);
```
Generates SQL: `STRING_AGG(column, 'separator') WITHIN GROUP (ORDER BY orderBy)`

Concatenates string values from grouped rows with ordering.

#### translate
```java
@NonNull SqlExpression<String> translate(@NonNull String inputChars, @NonNull String outputChars);
```
Generates SQL: `TRANSLATE(column, 'inputChars', 'outputChars')`

Replaces each character in `inputChars` with the corresponding character in `outputChars`.

#### quoteName
```java
@NonNull SqlExpression<String> quoteName();
```
Generates SQL: `QUOTENAME(column)`

Returns the string with delimiters added to make it a valid SQL Server delimited identifier.

#### quoteNameWith
```java
@NonNull SqlExpression<String> quoteNameWith(@NonNull String quoteChar);
```
Generates SQL: `QUOTENAME(column, 'quoteChar')`

Returns the string with the specified delimiter characters.

#### parseName
```java
@NonNull SqlExpression<String> parseName(int part);
```
Generates SQL: `PARSENAME(column, part)`

Returns the specified part (1-4) of a dot-separated object name. Part 1 is rightmost.

#### stuff
```java
@NonNull SqlExpression<String> stuff(int start, int length, @NonNull String replacement);
```
Generates SQL: `STUFF(column, start, length, 'replacement')`

Deletes characters from `start` for `length` and inserts the replacement string at that position.

### Date/Time Functions

#### dateFromParts
```java
@NonNull SqlExpression<?> dateFromParts(int year, int month, int day);
```
Generates SQL: `DATEFROMPARTS(year, month, day)`

Constructs a date value from individual year, month, and day components.

#### eoMonth
```java
@NonNull SqlExpression<?> eoMonth();
```
Generates SQL: `EOMONTH(column)`

Returns the last day of the month containing the date.

#### eoMonthOffset
```java
@NonNull SqlExpression<?> eoMonthOffset(int monthOffset);
```
Generates SQL: `EOMONTH(column, monthOffset)`

Returns the last day of the month that is `monthOffset` months from the column date.

#### dateDiff
```java
@NonNull SqlExpression<Integer> dateDiff(@NonNull String datePart, @NonNull SqlColumn<?> endDate);
```
Generates SQL: `DATEDIFF(datePart, column, endDate)`

Returns the count of the specified datepart boundaries crossed between two dates.

#### dateDiffBig
```java
@NonNull SqlExpression<Long> dateDiffBig(@NonNull String datePart, @NonNull SqlColumn<?> endDate);
```
Generates SQL: `DATEDIFF_BIG(datePart, column, endDate)`

Same as `DATEDIFF` but returns `bigint`, avoiding overflow for large differences.

#### dateAdd
```java
@NonNull SqlExpression<?> dateAdd(@NonNull String datePart, int number);
```
Generates SQL: `DATEADD(datePart, number, column)`

Adds the specified number of datepart intervals to the date.

#### dateName
```java
@NonNull SqlExpression<String> dateName(@NonNull String datePart);
```
Generates SQL: `DATENAME(datePart, column)`

Returns the name of the specified datepart (e.g., month name, day of week name) as a string.

#### datePart
```java
@NonNull SqlExpression<Integer> datePart(@NonNull String datePart);
```
Generates SQL: `DATEPART(datePart, column)`

Returns the specified datepart as an integer.

#### isDate
```java
@NonNull SqlCondition isDate();
```
Generates SQL: `ISDATE(column) = 1`

Returns true if the column value can be converted to a date.

#### switchOffset
```java
@NonNull SqlExpression<?> switchOffset(@NonNull String timezone);
```
Generates SQL: `SWITCHOFFSET(column, 'timezone')`

Converts a `datetimeoffset` value to a different time zone.

#### atTimeZone
```java
@NonNull SqlExpression<?> atTimeZone(@NonNull String timezone);
```
Generates SQL: `column AT TIME ZONE 'timezone'`

Converts a datetime to the specified time zone. Returns `datetimeoffset`.

### Mathematical Functions

#### greatest
```java
@NonNull SqlExpression<?> greatest(Object @NonNull ... values);
```
Generates SQL: `GREATEST(column, val1, val2, ...)`

Returns the maximum value among the arguments. Available since SQL Server 2022.

#### least
```java
@NonNull SqlExpression<?> least(Object @NonNull ... values);
```
Generates SQL: `LEAST(column, val1, val2, ...)`

Returns the minimum value among the arguments. Available since SQL Server 2022.

### Hashing & Compression

#### hashBytes
```java
@NonNull SqlExpression<?> hashBytes(@NonNull String algorithm);
```
Generates SQL: `HASHBYTES('algorithm', column)`

Returns the hash of the column value using the specified algorithm (`MD2`, `MD4`, `MD5`, `SHA`, `SHA1`, `SHA2_256`, `SHA2_512`).

#### compress
```java
@NonNull SqlExpression<?> compress();
```
Generates SQL: `COMPRESS(column)`

Compresses the column value using the GZIP algorithm.

#### decompress
```java
@NonNull SqlExpression<?> decompress();
```
Generates SQL: `DECOMPRESS(column)`

Decompresses a GZIP-compressed value.

### Format Functions

#### formatNumber
```java
@NonNull SqlExpression<String> formatNumber(@NonNull String format);
```
Generates SQL: `FORMAT(column, 'format')`

Formats a numeric value using .NET format strings (e.g., `'N2'`, `'C'`, `'P'`).

#### formatWithCulture
```java
@NonNull SqlExpression<String> formatWithCulture(@NonNull String format, @NonNull String culture);
```
Generates SQL: `FORMAT(column, 'format', 'culture')`

Formats a value using a .NET format string and specific culture.

### Analytic Functions

#### lag
```java
@NonNull SqlExpression<?> lag(int offset);
```
Generates SQL: `LAG(column, offset) OVER (...)`

Returns the value from the row that is `offset` rows before the current row.

#### lagWithDefault
```java
@NonNull SqlExpression<?> lagWithDefault(int offset, @NonNull T defaultValue);
```
Generates SQL: `LAG(column, offset, defaultValue) OVER (...)`

Returns the value from the row `offset` rows before, or `defaultValue` if no row exists.

#### lead
```java
@NonNull SqlExpression<?> lead(int offset);
```
Generates SQL: `LEAD(column, offset) OVER (...)`

Returns the value from the row that is `offset` rows after the current row.

#### leadWithDefault
```java
@NonNull SqlExpression<?> leadWithDefault(int offset, @NonNull T defaultValue);
```
Generates SQL: `LEAD(column, offset, defaultValue) OVER (...)`

Returns the value from the row `offset` rows after, or `defaultValue` if no row exists.

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

---

## SqlServerTable Extensions

### Data Compression

#### enableRowCompression
```java
void enableRowCompression();
```
Generates SQL: `ALTER TABLE ... REBUILD WITH (DATA_COMPRESSION = ROW)`

Enables row-level compression. Optimizes storage for fixed-length types.

#### enablePageCompression
```java
void enablePageCompression();
```
Generates SQL: `ALTER TABLE ... REBUILD WITH (DATA_COMPRESSION = PAGE)`

Enables page-level compression. Combines row compression with prefix and dictionary compression.

#### enableColumnstoreArchiveCompression
```java
void enableColumnstoreArchiveCompression();
```
Generates SQL: `ALTER TABLE ... REBUILD WITH (DATA_COMPRESSION = COLUMNSTORE_ARCHIVE)`

Maximum compression for columnstore indexes. Ideal for infrequently accessed data.

### Memory-Optimized Tables

#### setMemoryOptimized
```java
void setMemoryOptimized(boolean enabled);
```
Generates SQL: `CREATE TABLE ... WITH (MEMORY_OPTIMIZED = ON)` or alter equivalent

Enables In-Memory OLTP. Table data resides entirely in memory for extreme performance.

#### setDurability
```java
void setDurability(@NonNull SqlServerDurability durability);
```
Generates SQL: `CREATE TABLE ... WITH (DURABILITY = durability)`

Sets the durability for memory-optimized tables. `SCHEMA_AND_DATA` persists everything, `SCHEMA_ONLY` discards data on restart.

### Indexes

#### addFilteredIndex
```java
void addFilteredIndex(@NonNull String name, @NonNull SqlCondition where, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table (columns) WHERE condition`

Creates a filtered (partial) index that only includes rows matching the condition.

#### addIncludeIndex
```java
void addIncludeIndex(@NonNull String name, SqlColumn<?>[] keyColumns, SqlColumn<?> @NonNull ... includeColumns);
```
Generates SQL: `CREATE INDEX name ON table (keyColumns) INCLUDE (includeColumns)`

Creates a covering index with included (non-key) columns. Avoids bookmark lookups.

#### addFullTextIndex
```java
void addFullTextIndex(@NonNull String catalogName, @NonNull SqlColumn<?> keyColumn, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE FULLTEXT INDEX ON table (columns) KEY INDEX keyColumn ON catalogName`

Creates a full-text index on the specified columns using the given catalog.

#### rebuildIndex
```java
void rebuildIndex(@NonNull String name);
```
Generates SQL: `ALTER INDEX name ON table REBUILD`

Rebuilds the index, removing fragmentation and updating statistics.

#### reorganizeIndex
```java
void reorganizeIndex(@NonNull String name);
```
Generates SQL: `ALTER INDEX name ON table REORGANIZE`

Reorganizes the leaf level of the index. Less resource-intensive than rebuild.

#### rebuildAllIndexes
```java
void rebuildAllIndexes();
```
Generates SQL: `ALTER INDEX ALL ON table REBUILD`

Rebuilds all indexes on the table.

### Partitioning

#### createPartitionFunction
```java
void createPartitionFunction(@NonNull String name, @NonNull SqlColumnType type, Object @NonNull ... boundaryValues);
```
Generates SQL: `CREATE PARTITION FUNCTION name (type) AS RANGE RIGHT FOR VALUES (values)`

Creates a partition function that defines how rows map to partitions based on boundary values.

#### createPartitionScheme
```java
void createPartitionScheme(@NonNull String name, @NonNull String functionName, String @NonNull ... filegroups);
```
Generates SQL: `CREATE PARTITION SCHEME name AS PARTITION functionName TO (filegroups)`

Creates a partition scheme that maps partitions to filegroups.

#### splitPartition
```java
void splitPartition(@NonNull String functionName, @NonNull Object boundaryValue);
```
Generates SQL: `ALTER PARTITION FUNCTION functionName() SPLIT RANGE (value)`

Splits an existing partition at the specified boundary value.

#### mergePartition
```java
void mergePartition(@NonNull String functionName, @NonNull Object boundaryValue);
```
Generates SQL: `ALTER PARTITION FUNCTION functionName() MERGE RANGE (value)`

Merges two partitions by removing a boundary value.

#### switchPartition
```java
void switchPartition(int partitionNumber, @NonNull SqlTable<?> targetTable);
```
Generates SQL: `ALTER TABLE ... SWITCH PARTITION partitionNumber TO targetTable`

Transfers a partition's data to another table without moving data. Nearly instantaneous.

### Change Data Capture

#### enableCdc
```java
void enableCdc();
```
Generates SQL: `EXEC sys.sp_cdc_enable_table @source_schema = ..., @source_name = ...`

Enables Change Data Capture on this table. Records all DML changes in change tables.

#### disableCdc
```java
void disableCdc();
```
Generates SQL: `EXEC sys.sp_cdc_disable_table @source_schema = ..., @source_name = ...`

Disables Change Data Capture on this table.

### Security

#### addRowLevelSecurityPolicy
```java
void addRowLevelSecurityPolicy(@NonNull String policyName, @NonNull String predicateFunction);
```
Generates SQL: `CREATE SECURITY POLICY policyName ADD FILTER PREDICATE predicateFunction ON table`

Creates a row-level security policy using the specified predicate function.

#### enableDynamicDataMasking
```java
void enableDynamicDataMasking(@NonNull SqlColumn<?> column, @NonNull String maskFunction);
```
Generates SQL: `ALTER TABLE ... ALTER COLUMN column ADD MASKED WITH (FUNCTION = 'maskFunction')`

Adds dynamic data masking to a column. Mask functions: `default()`, `email()`, `partial(prefix, padding, suffix)`, `random(start, end)`.

### Computed Columns

#### addComputedColumn
```java
void addComputedColumn(@NonNull String name, @NonNull String expression);
```
Generates SQL: `ALTER TABLE ... ADD name AS (expression)`

Adds a virtual computed column derived from an expression.

#### addPersistedComputedColumn
```java
void addPersistedComputedColumn(@NonNull String name, @NonNull String expression);
```
Generates SQL: `ALTER TABLE ... ADD name AS (expression) PERSISTED`

Adds a physically stored computed column. Can be indexed.

### Statistics

#### updateStatistics
```java
void updateStatistics();
```
Generates SQL: `UPDATE STATISTICS table`

Updates query optimization statistics for the table.

#### updateStatisticsFullScan
```java
void updateStatisticsFullScan();
```
Generates SQL: `UPDATE STATISTICS table WITH FULLSCAN`

Updates statistics by scanning the entire table.

### Table Hints

#### withReadUncommitted
```java
void withReadUncommitted();
```
Generates SQL: `SELECT ... FROM table WITH (READUNCOMMITTED)`

Equivalent to `NOLOCK`. Allows dirty reads.

#### withReadCommittedLock
```java
void withReadCommittedLock();
```
Generates SQL: `SELECT ... FROM table WITH (READCOMMITTEDLOCK)`

Forces read committed isolation with shared locks.

#### withHoldLock
```java
void withHoldLock();
```
Generates SQL: `SELECT ... FROM table WITH (HOLDLOCK)`

Holds shared locks until the transaction completes (serializable isolation).

#### withTableLock
```java
void withTableLock();
```
Generates SQL: `SELECT ... FROM table WITH (TABLOCK)`

Acquires a table-level lock instead of row or page locks.

#### withTableLockExclusive
```java
void withTableLockExclusive();
```
Generates SQL: `SELECT ... FROM table WITH (TABLOCKX)`

Acquires an exclusive table-level lock.

### Graph Tables

#### createAsNodeTable
```java
void createAsNodeTable();
```
Generates SQL: `CREATE TABLE ... AS NODE`

Creates a graph node table.

#### createAsEdgeTable
```java
void createAsEdgeTable();
```
Generates SQL: `CREATE TABLE ... AS EDGE`

Creates a graph edge table.

#### addEdgeConstraint
```java
void addEdgeConstraint(@NonNull String name, @NonNull SqlTable<?> fromNode, @NonNull SqlTable<?> toNode);
```
Generates SQL: `ALTER TABLE ... ADD CONSTRAINT name CONNECTION (fromNode TO toNode)`

Adds an edge constraint limiting which node types can be connected.

---

## Supporting Types

### SqlServerDurability (new enum)
```java
public enum SqlServerDurability {
    SCHEMA_AND_DATA,
    SCHEMA_ONLY
}
```

### SqlServerCompression (new enum)
```java
public enum SqlServerCompression {
    NONE,
    ROW,
    PAGE,
    COLUMNSTORE,
    COLUMNSTORE_ARCHIVE
}
```

### SqlServerMaskFunction (new enum)
```java
public enum SqlServerMaskFunction {
    DEFAULT,
    EMAIL,
    RANDOM,
    PARTIAL
}
```
