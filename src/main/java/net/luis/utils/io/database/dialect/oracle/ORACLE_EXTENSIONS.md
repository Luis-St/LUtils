# Oracle Dialect Extension Guide

## Current State

Summary of current interfaces.

## OracleColumn Extensions

### Regular Expression Functions

#### regexpReplace
```java
@NonNull SqlExpression<String> regexpReplace(@NonNull String pattern, @NonNull String replacement);
```
Generates SQL: `REGEXP_REPLACE(column, 'pattern', 'replacement')`

Replaces all occurrences of the pattern with the replacement string.

#### regexpReplaceAt
```java
@NonNull SqlExpression<String> regexpReplaceAt(@NonNull String pattern, @NonNull String replacement, int position, int occurrence);
```
Generates SQL: `REGEXP_REPLACE(column, 'pattern', 'replacement', position, occurrence)`

Replaces a specific occurrence of the pattern starting from the given position.

#### regexpSubstr
```java
@NonNull SqlExpression<String> regexpSubstr(@NonNull String pattern);
```
Generates SQL: `REGEXP_SUBSTR(column, 'pattern')`

Returns the first substring matching the pattern.

#### regexpSubstrAt
```java
@NonNull SqlExpression<String> regexpSubstrAt(@NonNull String pattern, int position, int occurrence);
```
Generates SQL: `REGEXP_SUBSTR(column, 'pattern', position, occurrence)`

Returns the Nth occurrence of the pattern starting from the given position.

#### regexpInstr
```java
@NonNull SqlExpression<Integer> regexpInstr(@NonNull String pattern);
```
Generates SQL: `REGEXP_INSTR(column, 'pattern')`

Returns the position of the first occurrence of the pattern.

#### regexpCount
```java
@NonNull SqlExpression<Integer> regexpCount(@NonNull String pattern);
```
Generates SQL: `REGEXP_COUNT(column, 'pattern')`

Returns the number of times the pattern matches.

### Type Conversion

#### toNumber
```java
@NonNull SqlExpression<?> toNumber(@NonNull String format);
```
Generates SQL: `TO_NUMBER(column, 'format')`

Converts a string to a number using the specified format model (e.g., `'999.99'`, `'$999,999.00'`).

#### toTimestamp
```java
@NonNull SqlExpression<?> toTimestamp(@NonNull String format);
```
Generates SQL: `TO_TIMESTAMP(column, 'format')`

Converts a string to a timestamp using the specified format model.

#### toTimestampTz
```java
@NonNull SqlExpression<?> toTimestampTz(@NonNull String format);
```
Generates SQL: `TO_TIMESTAMP_TZ(column, 'format')`

Converts a string to a timestamp with time zone.

#### toClob
```java
@NonNull SqlExpression<?> toClob();
```
Generates SQL: `TO_CLOB(column)`

Converts the value to a CLOB.

#### toNchar
```java
@NonNull SqlExpression<?> toNchar();
```
Generates SQL: `TO_NCHAR(column)`

Converts to national character set.

#### cast
```java
@NonNull SqlExpression<?> cast(@NonNull SqlColumnType targetType);
```
Generates SQL: `CAST(column AS targetType)`

Standard SQL cast.

### Date/Time Functions

#### extract
```java
@NonNull SqlExpression<Integer> extract(@NonNull DatePart part);
```
Generates SQL: `EXTRACT(part FROM column)`

Extracts a component (YEAR, MONTH, DAY, HOUR, MINUTE, SECOND) from a date or timestamp.

#### trunc
```java
@NonNull SqlExpression<?> trunc();
```
Generates SQL: `TRUNC(column)`

Truncates a date to midnight or a number to an integer.

#### truncTo
```java
@NonNull SqlExpression<?> truncTo(@NonNull String format);
```
Generates SQL: `TRUNC(column, 'format')`

Truncates to the specified unit (e.g., `'MM'` for month, `'YYYY'` for year, `'HH'` for hour).

#### round
```java
@NonNull SqlExpression<?> round(int precision);
```
Generates SQL: `ROUND(column, precision)`

Rounds a number to the specified number of decimal places, or a date to the specified unit.

#### addMonths
```java
@NonNull SqlExpression<?> addMonths(int months);
```
Generates SQL: `ADD_MONTHS(column, months)`

Adds the specified number of months to a date.

#### monthsBetween
```java
@NonNull SqlExpression<Double> monthsBetween(@NonNull SqlColumn<?> other);
```
Generates SQL: `MONTHS_BETWEEN(column, other)`

Returns the number of months between two dates.

#### lastDay
```java
@NonNull SqlExpression<?> lastDay();
```
Generates SQL: `LAST_DAY(column)`

Returns the last day of the month containing the date.

#### nextDay
```java
@NonNull SqlExpression<?> nextDay(@NonNull String dayOfWeek);
```
Generates SQL: `NEXT_DAY(column, 'dayOfWeek')`

Returns the next occurrence of the specified day of the week after the given date.

#### sysdate
```java
@NonNull SqlExpression<?> sysdate();
```
Generates SQL: `SYSDATE`

Returns the current date and time of the database server.

#### systimestamp
```java
@NonNull SqlExpression<?> systimestamp();
```
Generates SQL: `SYSTIMESTAMP`

Returns the current timestamp with time zone of the database server.

### String Functions

#### translate
```java
@NonNull SqlExpression<String> translate(@NonNull String from, @NonNull String to);
```
Generates SQL: `TRANSLATE(column, 'from', 'to')`

Replaces each character in `from` with the corresponding character in `to`. Characters in `from` without a match in `to` are removed.

#### replace
```java
@NonNull SqlExpression<String> replace(@NonNull String search, @NonNull String replacement);
```
Generates SQL: `REPLACE(column, 'search', 'replacement')`

Replaces all occurrences of `search` with `replacement`.

#### lpad
```java
@NonNull SqlExpression<String> lpad(int length, @NonNull String padChar);
```
Generates SQL: `LPAD(column, length, 'padChar')`

Left-pads the string to the specified length with the given character.

#### rpad
```java
@NonNull SqlExpression<String> rpad(int length, @NonNull String padChar);
```
Generates SQL: `RPAD(column, length, 'padChar')`

Right-pads the string to the specified length with the given character.

#### initcap
```java
@NonNull SqlExpression<String> initcap();
```
Generates SQL: `INITCAP(column)`

Converts the first letter of each word to uppercase and the rest to lowercase.

#### soundex
```java
@NonNull SqlExpression<String> soundex();
```
Generates SQL: `SOUNDEX(column)`

Returns the SOUNDEX phonetic code for the string.

#### instr
```java
@NonNull SqlExpression<Integer> instr(@NonNull String substring);
```
Generates SQL: `INSTR(column, 'substring')`

Returns the position of the first occurrence of the substring (1-based).

#### instrAt
```java
@NonNull SqlExpression<Integer> instrAt(@NonNull String substring, int start, int occurrence);
```
Generates SQL: `INSTR(column, 'substring', start, occurrence)`

Returns the position of the Nth occurrence of the substring starting from the given position.

### JSON Functions

#### jsonExists
```java
@NonNull SqlCondition jsonExists(@NonNull String path);
```
Generates SQL: `JSON_EXISTS(column, 'path')`

Returns true if the specified JSON path exists in the column.

#### jsonQuery
```java
@NonNull SqlExpression<?> jsonQuery(@NonNull String path);
```
Generates SQL: `JSON_QUERY(column, 'path')`

Returns a JSON object or array from the specified path.

#### jsonTable
```java
@NonNull SqlExpression<?> jsonTable(@NonNull String path);
```
Generates SQL: `JSON_TABLE(column, 'path' COLUMNS (...))`

Converts JSON data to a relational table format. Used in FROM clause.

#### jsonMergePatch
```java
@NonNull SqlExpression<?> jsonMergePatch(@NonNull String json);
```
Generates SQL: `JSON_MERGEPATCH(column, 'json')`

Merges JSON documents using RFC 7396 merge-patch semantics. Available since Oracle 19c.

#### jsonSerialize
```java
@NonNull SqlExpression<String> jsonSerialize();
```
Generates SQL: `JSON_SERIALIZE(column)`

Converts a JSON value to a string representation. Available since Oracle 21c.

#### jsonArrayAgg
```java
@NonNull SqlExpression<?> jsonArrayAgg();
```
Generates SQL: `JSON_ARRAYAGG(column)`

Aggregates values into a JSON array.

#### jsonObjectAgg
```java
@NonNull SqlExpression<?> jsonObjectAgg(@NonNull SqlColumn<String> keyColumn);
```
Generates SQL: `JSON_OBJECTAGG(KEY keyColumn VALUE column)`

Aggregates key-value pairs into a JSON object.

### XML Functions

#### xmlElement
```java
@NonNull SqlExpression<?> xmlElement(@NonNull String name);
```
Generates SQL: `XMLELEMENT(NAME name, column)`

Creates an XML element with the column value as content.

#### xmlForest
```java
@NonNull SqlExpression<?> xmlForest(SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `XMLFOREST(column1, column2, ...)`

Creates a sequence of XML elements from the specified columns.

#### xmlAgg
```java
@NonNull SqlExpression<?> xmlAgg();
```
Generates SQL: `XMLAGG(column)`

Aggregates XML fragments from grouped rows into a single XML document.

#### extractXml
```java
@NonNull SqlExpression<?> extractXml(@NonNull String xpath);
```
Generates SQL: `EXTRACT(column, 'xpath')`

Extracts an XML fragment using the specified XPath expression.

#### xmlExists
```java
@NonNull SqlCondition xmlExists(@NonNull String xpath);
```
Generates SQL: `XMLEXISTS('xpath' PASSING column)`

Returns true if the XPath expression matches something in the XML document.

### Hierarchical Query Functions

#### level
```java
@NonNull SqlExpression<Integer> level();
```
Generates SQL: `LEVEL`

Returns the depth level of the current row in a hierarchical query (root = 1).

#### isLeaf
```java
@NonNull SqlCondition isLeaf();
```
Generates SQL: `CONNECT_BY_ISLEAF = 1`

Returns true if the current row is a leaf node (no children) in the hierarchy.

#### isCycle
```java
@NonNull SqlCondition isCycle();
```
Generates SQL: `CONNECT_BY_ISCYCLE = 1`

Returns true if the current row has a cycle in the hierarchy.

#### connectByRoot
```java
@NonNull SqlExpression<?> connectByRoot();
```
Generates SQL: `CONNECT_BY_ROOT column`

Returns the value of the column from the root row of the hierarchy.

### Analytic Functions

#### lag
```java
@NonNull SqlExpression<?> lag(int offset);
```
Generates SQL: `LAG(column, offset) OVER (...)`

Returns the value from a row that is `offset` rows before the current row.

#### lead
```java
@NonNull SqlExpression<?> lead(int offset);
```
Generates SQL: `LEAD(column, offset) OVER (...)`

Returns the value from a row that is `offset` rows after the current row.

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

#### keepDenseRankFirst
```java
@NonNull SqlExpression<?> keepDenseRankFirst(@NonNull SqlColumn<?> orderBy);
```
Generates SQL: `column KEEP (DENSE_RANK FIRST ORDER BY orderBy)`

Returns the value from the row that ranks first in the dense ranking. Used with aggregate functions.

#### keepDenseRankLast
```java
@NonNull SqlExpression<?> keepDenseRankLast(@NonNull SqlColumn<?> orderBy);
```
Generates SQL: `column KEEP (DENSE_RANK LAST ORDER BY orderBy)`

Returns the value from the row that ranks last in the dense ranking.

#### withinGroup
```java
@NonNull SqlExpression<?> withinGroup(@NonNull SqlColumn<?> orderBy);
```
Generates SQL: `aggregate(column) WITHIN GROUP (ORDER BY orderBy)`

Specifies the ordering for ordered-set aggregate functions like `LISTAGG`, `PERCENTILE_CONT`, `PERCENTILE_DISC`.

### Pivot/Unpivot

#### pivot
```java
@NonNull SqlExpression<?> pivot(@NonNull String aggregateFunc, @NonNull SqlColumn<?> forColumn, Object @NonNull ... values);
```
Generates SQL: `PIVOT (aggregateFunc(column) FOR forColumn IN (values))`

Rotates rows into columns. Converts unique values of the `forColumn` into separate columns.

#### unpivot
```java
@NonNull SqlExpression<?> unpivot(@NonNull String valueColumn, @NonNull String nameColumn, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `UNPIVOT (valueColumn FOR nameColumn IN (columns))`

Rotates columns into rows. Inverse of pivot.

### Miscellaneous

#### rownum
```java
@NonNull SqlExpression<Integer> rownum();
```
Generates SQL: `ROWNUM`

Returns the sequential row number of the current row in the result set.

#### rowid
```java
@NonNull SqlExpression<String> rowid();
```
Generates SQL: `ROWID`

Returns the physical address of the row.

#### greatest
```java
@NonNull SqlExpression<?> greatest(Object @NonNull ... values);
```
Generates SQL: `GREATEST(column, val1, val2, ...)`

Returns the greatest value among the arguments.

#### leastValue
```java
@NonNull SqlExpression<?> leastValue(Object @NonNull ... values);
```
Generates SQL: `LEAST(column, val1, val2, ...)`

Returns the least value among the arguments.

---

## OracleTable Extensions

### Partitioning

#### partitionByRangeInterval
```java
void partitionByRangeInterval(@NonNull SqlColumn<?> column, @NonNull String interval);
```
Generates SQL: `PARTITION BY RANGE (column) INTERVAL (interval)`

Configures interval partitioning. Oracle automatically creates partitions as data arrives. The interval specifies the width of each partition (e.g., `NUMTOYMINTERVAL(1, 'MONTH')`).

#### subpartitionByHash
```java
void subpartitionByHash(@NonNull SqlColumn<?> column, int subpartitions);
```
Generates SQL: `SUBPARTITION BY HASH (column) SUBPARTITIONS n`

Adds hash subpartitioning to a partitioned table (composite partitioning).

#### subpartitionByList
```java
void subpartitionByList(@NonNull SqlColumn<?> column);
```
Generates SQL: `SUBPARTITION BY LIST (column)`

Adds list subpartitioning to a partitioned table.

#### subpartitionByRange
```java
void subpartitionByRange(@NonNull SqlColumn<?> column);
```
Generates SQL: `SUBPARTITION BY RANGE (column)`

Adds range subpartitioning to a partitioned table.

#### addPartition
```java
void addPartition(@NonNull String name, @NonNull String bound);
```
Generates SQL: `ALTER TABLE ... ADD PARTITION name VALUES ... (bound)`

Adds a new partition to the table.

#### dropPartition
```java
void dropPartition(@NonNull String name);
```
Generates SQL: `ALTER TABLE ... DROP PARTITION name`

Drops a partition and its data.

#### truncatePartition
```java
void truncatePartition(@NonNull String name);
```
Generates SQL: `ALTER TABLE ... TRUNCATE PARTITION name`

Removes all rows from a partition without dropping it.

#### splitPartition
```java
void splitPartition(@NonNull String name, @NonNull String bound, @NonNull String part1, @NonNull String part2);
```
Generates SQL: `ALTER TABLE ... SPLIT PARTITION name AT (bound) INTO (PARTITION part1, PARTITION part2)`

Splits a partition into two at the specified boundary.

#### mergePartitions
```java
void mergePartitions(@NonNull String part1, @NonNull String part2, @NonNull String into);
```
Generates SQL: `ALTER TABLE ... MERGE PARTITIONS part1, part2 INTO PARTITION into`

Merges two adjacent partitions into one.

#### exchangePartition
```java
void exchangePartition(@NonNull String partitionName, @NonNull SqlTable<?> targetTable);
```
Generates SQL: `ALTER TABLE ... EXCHANGE PARTITION partitionName WITH TABLE targetTable`

Exchanges a partition with a non-partitioned table. Near-instantaneous for bulk loading.

### In-Memory Column Store

#### enableInMemory
```java
void enableInMemory();
```
Generates SQL: `ALTER TABLE ... INMEMORY`

Enables the In-Memory Column Store for this table. Data is stored in a columnar format in memory.

#### setInMemoryPriority
```java
void setInMemoryPriority(@NonNull OracleInMemoryPriority priority);
```
Generates SQL: `ALTER TABLE ... INMEMORY PRIORITY priority`

Sets the priority for populating the table into the In-Memory Column Store.

#### setInMemoryCompression
```java
void setInMemoryCompression(@NonNull OracleInMemoryCompression compression);
```
Generates SQL: `ALTER TABLE ... INMEMORY compression`

Sets the compression level for in-memory data.

#### disableInMemory
```java
void disableInMemory();
```
Generates SQL: `ALTER TABLE ... NO INMEMORY`

Removes the table from the In-Memory Column Store.

### Virtual & Invisible Columns

#### addVirtualColumn
```java
void addVirtualColumn(@NonNull String name, @NonNull String expression);
```
Generates SQL: `ALTER TABLE ... ADD name AS (expression) VIRTUAL`

Adds a virtual computed column. Values are computed on access, not stored.

#### addInvisibleColumn
```java
void addInvisibleColumn(@NonNull String name, @NonNull SqlColumnType type);
```
Generates SQL: `ALTER TABLE ... ADD name type INVISIBLE`

Adds an invisible column. Excluded from `SELECT *` but can be queried explicitly.

### Materialized Views

#### refreshMaterializedView
```java
void refreshMaterializedView(@NonNull String name);
```
Generates SQL: `BEGIN DBMS_MVIEW.REFRESH('name'); END;`

Manually refreshes a materialized view.

#### refreshMaterializedViewFast
```java
void refreshMaterializedViewFast(@NonNull String name);
```
Generates SQL: `BEGIN DBMS_MVIEW.REFRESH('name', 'F'); END;`

Performs a fast (incremental) refresh using materialized view logs.

#### refreshMaterializedViewComplete
```java
void refreshMaterializedViewComplete(@NonNull String name);
```
Generates SQL: `BEGIN DBMS_MVIEW.REFRESH('name', 'C'); END;`

Performs a complete refresh by re-executing the defining query.

#### createMaterializedViewLog
```java
void createMaterializedViewLog(SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE MATERIALIZED VIEW LOG ON table WITH ROWID, SEQUENCE (columns) INCLUDING NEW VALUES`

Creates a materialized view log to support fast refreshes.

### Performance & Storage

#### setParallel
```java
void setParallel(int degree);
```
Generates SQL: `ALTER TABLE ... PARALLEL degree`

Sets the degree of parallelism for queries on this table.

#### setNoParallel
```java
void setNoParallel();
```
Generates SQL: `ALTER TABLE ... NOPARALLEL`

Disables parallel execution for this table.

#### enableResultCache
```java
void enableResultCache();
```
Generates SQL: `ALTER TABLE ... RESULT_CACHE (MODE FORCE)`

Enables result caching for queries on this table.

#### setLogging
```java
void setLogging(boolean enabled);
```
Generates SQL: `ALTER TABLE ... LOGGING` or `NOLOGGING`

Controls whether DML operations generate redo log entries. `NOLOGGING` speeds up bulk operations but data is not recoverable.

#### shrinkSpace
```java
void shrinkSpace();
```
Generates SQL: `ALTER TABLE ... SHRINK SPACE`

Compacts the table by moving rows to reclaim unused space. Requires row movement to be enabled.

#### shrinkSpaceCascade
```java
void shrinkSpaceCascade();
```
Generates SQL: `ALTER TABLE ... SHRINK SPACE CASCADE`

Compacts the table and all dependent objects (indexes, LOB segments).

#### moveOnline
```java
void moveOnline();
```
Generates SQL: `ALTER TABLE ... MOVE ONLINE`

Moves/reorganizes the table while allowing concurrent DML. Available since Oracle 12c.

#### moveToTablespace
```java
void moveToTablespace(@NonNull String tablespace);
```
Generates SQL: `ALTER TABLE ... MOVE TABLESPACE tablespace`

Moves the table to a different tablespace.

### Index Types

#### createBitmapIndex
```java
void createBitmapIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE BITMAP INDEX name ON table (columns)`

Creates a bitmap index. Efficient for columns with low cardinality (few distinct values).

#### createReverseKeyIndex
```java
void createReverseKeyIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `CREATE INDEX name ON table (columns) REVERSE`

Creates a reverse key index. Distributes sequential values across the index to reduce contention.

#### createFunctionIndex
```java
void createFunctionIndex(@NonNull String name, @NonNull String expression);
```
Generates SQL: `CREATE INDEX name ON table (expression)`

Creates a function-based index on an expression (e.g., `UPPER(name)`, `NVL(status, 'UNKNOWN')`).

#### rebuildIndex
```java
void rebuildIndex(@NonNull String name);
```
Generates SQL: `ALTER INDEX name REBUILD`

Rebuilds an index to remove fragmentation.

#### rebuildIndexOnline
```java
void rebuildIndexOnline(@NonNull String name);
```
Generates SQL: `ALTER INDEX name REBUILD ONLINE`

Rebuilds an index while allowing concurrent DML.

### Flashback & Recovery

#### flashbackTo
```java
void flashbackTo(@NonNull String timestamp);
```
Generates SQL: `FLASHBACK TABLE table TO TIMESTAMP TO_TIMESTAMP('timestamp')`

Restores the table to its state at the specified timestamp. Requires `enableFlashback()`.

#### flashbackToScn
```java
void flashbackToScn(long scn);
```
Generates SQL: `FLASHBACK TABLE table TO SCN scn`

Restores the table to its state at the specified System Change Number.

#### flashbackDrop
```java
void flashbackDrop();
```
Generates SQL: `FLASHBACK TABLE table TO BEFORE DROP`

Recovers a dropped table from the recycle bin.

### Redefinition

#### startRedefinition
```java
void startRedefinition(@NonNull SqlTable<?> interimTable);
```
Generates SQL: `BEGIN DBMS_REDEFINITION.START_REDEF_TABLE(...); END;`

Starts online table redefinition. Allows restructuring a table while it remains accessible.

#### finishRedefinition
```java
void finishRedefinition(@NonNull SqlTable<?> interimTable);
```
Generates SQL: `BEGIN DBMS_REDEFINITION.FINISH_REDEF_TABLE(...); END;`

Completes online table redefinition and switches the table definition.

### Table Comments

#### setComment
```java
void setComment(@NonNull String comment);
```
Generates SQL: `COMMENT ON TABLE table IS 'comment'`

Sets a comment on the table.

#### setColumnComment
```java
void setColumnComment(@NonNull SqlColumn<?> column, @NonNull String comment);
```
Generates SQL: `COMMENT ON COLUMN table.column IS 'comment'`

Sets a comment on a specific column.

---

## Supporting Types

### OracleInMemoryPriority (new enum)
```java
public enum OracleInMemoryPriority {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
```

### OracleInMemoryCompression (new enum)
```java
public enum OracleInMemoryCompression {
    NO_MEMCOMPRESS,
    MEMCOMPRESS_FOR_DML,
    MEMCOMPRESS_FOR_QUERY_LOW,
    MEMCOMPRESS_FOR_QUERY_HIGH,
    MEMCOMPRESS_FOR_CAPACITY_LOW,
    MEMCOMPRESS_FOR_CAPACITY_HIGH
}
```

### DatePart extensions
The existing `DatePart` enum should include Oracle-specific parts:
```java
// Consider adding to DatePart:
TIMEZONE_HOUR,
TIMEZONE_MINUTE,
TIMEZONE_REGION,
TIMEZONE_ABBR
```
