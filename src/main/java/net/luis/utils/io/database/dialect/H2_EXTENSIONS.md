# H2 Dialect Extension Guide

## Current State

H2 currently has no dialect-specific column or table interfaces. The `H2Dialect` uses the base `SqlTable<?>` and `SqlColumn<?>` types. This guide proposes creating `H2Column<T>` and `H2Table<T>` interfaces to expose H2-specific features, and updating `H2Dialect` accordingly.

### Proposed Architecture
```java
// New interfaces
public interface H2Column<T> extends SqlColumn<T> { ... }
public interface H2Table<T> extends SqlTable<T> { ... }

// Updated dialect
public final class H2Dialect extends SqlDialect<H2Table<?>, H2Column<?>> {}
```

## H2Column Extensions

### Pattern Matching

#### regexp
```java
@NonNull SqlCondition regexp(@NonNull String pattern);
```
Generates SQL: `column REGEXP 'pattern'`

Regular expression matching using Java regex syntax.

#### regexpReplace
```java
@NonNull SqlExpression<String> regexpReplace(@NonNull String pattern, @NonNull String replacement);
```
Generates SQL: `REGEXP_REPLACE(column, 'pattern', 'replacement')`

Replaces substrings matching the regular expression.

#### regexpSubstr
```java
@NonNull SqlExpression<String> regexpSubstr(@NonNull String pattern);
```
Generates SQL: `REGEXP_SUBSTR(column, 'pattern')`

Returns the first substring matching the regular expression.

#### regexpLike
```java
@NonNull SqlCondition regexpLike(@NonNull String pattern);
```
Generates SQL: `REGEXP_LIKE(column, 'pattern')`

Function-based regular expression matching.

### Array Operations

#### arrayGet
```java
@NonNull SqlExpression<?> arrayGet(int index);
```
Generates SQL: `ARRAY_GET(column, index)`

Returns the element at the specified 1-based index from an array.

#### arrayLength
```java
@NonNull SqlExpression<Integer> arrayLength();
```
Generates SQL: `CARDINALITY(column)`

Returns the number of elements in the array.

#### arrayContains
```java
@NonNull SqlCondition arrayContains(@NonNull T element);
```
Generates SQL: `ARRAY_CONTAINS(column, element)`

Returns true if the array contains the specified element.

#### arrayCat
```java
@NonNull SqlExpression<?> arrayCat(@NonNull SqlColumn<?> other);
```
Generates SQL: `ARRAY_CAT(column, other)`

Concatenates two arrays.

#### arrayAppend
```java
@NonNull SqlExpression<?> arrayAppend(@NonNull T element);
```
Generates SQL: `ARRAY_APPEND(column, element)`

Appends an element to the array.

#### arraySlice
```java
@NonNull SqlExpression<?> arraySlice(int from, int to);
```
Generates SQL: `ARRAY_SLICE(column, from, to)`

Returns a sub-array from the specified range.

#### arrayToString
```java
@NonNull SqlExpression<String> arrayToString(@NonNull String delimiter);
```
Generates SQL: `ARRAY_TO_STRING(column, 'delimiter')`

Converts an array to a delimited string.

#### stringToArray
```java
@NonNull SqlExpression<?> stringToArray(@NonNull String delimiter);
```
Generates SQL: `STRING_TO_ARRAY(column, 'delimiter')`

Splits a string into an array using the delimiter.

#### unnest
```java
@NonNull SqlExpression<?> unnest();
```
Generates SQL: `UNNEST(column)`

Expands an array into a set of rows.

### Conditional Functions

#### decode
```java
@NonNull SqlExpression<?> decode(Object @NonNull ... searchResults);
```
Generates SQL: `DECODE(column, search1, result1, search2, result2, ..., default)`

Oracle-compatible DECODE function. Compares the column to each search value and returns the corresponding result.

#### casewhen
```java
@NonNull SqlExpression<?> casewhen(@NonNull SqlCondition condition, @NonNull T ifTrue, @NonNull T ifFalse);
```
Generates SQL: `CASEWHEN(condition, ifTrue, ifFalse)`

H2-specific inline conditional function.

#### nvl2
```java
@NonNull SqlExpression<?> nvl2(@NonNull T ifNotNull, @NonNull T ifNull);
```
Generates SQL: `NVL2(column, ifNotNull, ifNull)`

Returns `ifNotNull` when the column is not null, otherwise `ifNull`. Oracle-compatible function.

#### nullIf
```java
@NonNull SqlExpression<?> nullIf(@NonNull T value);
```
Generates SQL: `NULLIF(column, value)`

Returns null if the column equals the value.

#### greatest
```java
@NonNull SqlExpression<?> greatest(Object @NonNull ... values);
```
Generates SQL: `GREATEST(column, val1, val2, ...)`

Returns the greatest value among the arguments.

#### least
```java
@NonNull SqlExpression<?> least(Object @NonNull ... values);
```
Generates SQL: `LEAST(column, val1, val2, ...)`

Returns the least value among the arguments.

### String Functions

#### stringAgg
```java
@NonNull SqlExpression<String> stringAgg(@NonNull String separator);
```
Generates SQL: `STRING_AGG(column, 'separator')`

Concatenates grouped values with a separator. H2 supports the standard SQL function.

#### groupConcat
```java
@NonNull SqlExpression<String> groupConcat(@NonNull String separator);
```
Generates SQL: `GROUP_CONCAT(column SEPARATOR 'separator')`

MySQL-compatible grouped string concatenation.

#### listagg
```java
@NonNull SqlExpression<String> listagg(@NonNull String separator);
```
Generates SQL: `LISTAGG(column, 'separator')`

Oracle-compatible list aggregation.

#### translate
```java
@NonNull SqlExpression<String> translate(@NonNull String from, @NonNull String to);
```
Generates SQL: `TRANSLATE(column, 'from', 'to')`

Character-by-character translation.

#### repeat
```java
@NonNull SqlExpression<String> repeat(int count);
```
Generates SQL: `REPEAT(column, count)`

Repeats the string the specified number of times.

#### space
```java
@NonNull SqlExpression<String> space(int count);
```
Generates SQL: `SPACE(count)`

Returns a string of the specified number of spaces.

#### soundex
```java
@NonNull SqlExpression<String> soundex();
```
Generates SQL: `SOUNDEX(column)`

Returns the SOUNDEX phonetic code.

#### difference
```java
@NonNull SqlExpression<Integer> difference(@NonNull String other);
```
Generates SQL: `DIFFERENCE(column, 'other')`

Returns the difference between SOUNDEX values (0-4, where 4 is most similar).

### Date/Time Functions

#### formatDatetime
```java
@NonNull SqlExpression<String> formatDatetime(@NonNull String format);
```
Generates SQL: `FORMATDATETIME(column, 'format')`

Formats a date/time value using Java SimpleDateFormat patterns.

#### parseDatetime
```java
@NonNull SqlExpression<?> parseDatetime(@NonNull String format);
```
Generates SQL: `PARSEDATETIME(column, 'format')`

Parses a string into a timestamp using the specified format.

#### dateAdd
```java
@NonNull SqlExpression<?> dateAdd(@NonNull String unit, int value);
```
Generates SQL: `DATEADD('unit', value, column)`

Adds the specified amount to a date. Units: `YEAR`, `MONTH`, `DAY`, `HOUR`, `MINUTE`, `SECOND`.

#### dateDiff
```java
@NonNull SqlExpression<Long> dateDiff(@NonNull String unit, @NonNull SqlColumn<?> other);
```
Generates SQL: `DATEDIFF('unit', column, other)`

Returns the difference between two dates in the specified unit.

#### extract
```java
@NonNull SqlExpression<Integer> extract(@NonNull String field);
```
Generates SQL: `EXTRACT(field FROM column)`

Extracts a component from a date/time value.

#### dayOfWeek
```java
@NonNull SqlExpression<Integer> dayOfWeek();
```
Generates SQL: `DAY_OF_WEEK(column)`

Returns the day of the week (1=Sunday).

#### dayOfYear
```java
@NonNull SqlExpression<Integer> dayOfYear();
```
Generates SQL: `DAY_OF_YEAR(column)`

Returns the day of the year (1-366).

#### isoWeek
```java
@NonNull SqlExpression<Integer> isoWeek();
```
Generates SQL: `ISO_WEEK(column)`

Returns the ISO week number.

#### isoYear
```java
@NonNull SqlExpression<Integer> isoYear();
```
Generates SQL: `ISO_YEAR(column)`

Returns the ISO year number.

#### truncate
```java
@NonNull SqlExpression<?> truncate(@NonNull String unit);
```
Generates SQL: `DATE_TRUNC('unit', column)`

Truncates the timestamp to the specified precision.

### JSON Functions (H2 1.4.200+)

#### jsonValue
```java
@NonNull SqlExpression<?> jsonValue(@NonNull String path);
```
Generates SQL: `column FORMAT JSON` or JSON path extraction

Extracts a scalar value from JSON.

#### jsonArray
```java
@NonNull SqlExpression<?> jsonArrayAgg();
```
Generates SQL: `JSON_ARRAYAGG(column)`

Aggregates values into a JSON array.

#### jsonObject
```java
@NonNull SqlExpression<?> jsonObjectAgg(@NonNull SqlColumn<String> keyColumn);
```
Generates SQL: `JSON_OBJECTAGG(keyColumn: column)`

Aggregates key-value pairs into a JSON object.

### Bit Operations

#### bitAnd
```java
@NonNull SqlExpression<Long> bitAnd(long mask);
```
Generates SQL: `BITAND(column, mask)`

Bitwise AND operation.

#### bitOr
```java
@NonNull SqlExpression<Long> bitOr(long mask);
```
Generates SQL: `BITOR(column, mask)`

Bitwise OR operation.

#### bitXor
```java
@NonNull SqlExpression<Long> bitXor(long mask);
```
Generates SQL: `BITXOR(column, mask)`

Bitwise XOR operation.

#### bitNot
```java
@NonNull SqlExpression<Long> bitNot();
```
Generates SQL: `BITNOT(column)`

Bitwise NOT operation.

#### bitGet
```java
@NonNull SqlCondition bitGet(int position);
```
Generates SQL: `BIT_GET(column, position)`

Returns true if the bit at the specified position is set.

#### bitCount
```java
@NonNull SqlExpression<Integer> bitCount();
```
Generates SQL: `BIT_COUNT(column)`

Returns the number of set bits.

#### lshift
```java
@NonNull SqlExpression<Long> lshift(int positions);
```
Generates SQL: `LSHIFT(column, positions)`

Left shift operation.

#### rshift
```java
@NonNull SqlExpression<Long> rshift(int positions);
```
Generates SQL: `RSHIFT(column, positions)`

Right shift operation.

### Security & Hashing

#### hash
```java
@NonNull SqlExpression<?> hash(@NonNull String algorithm);
```
Generates SQL: `HASH('algorithm', column)`

Computes a hash using the specified algorithm (`SHA-256`, `SHA-384`, `SHA-512`, `SHA3-256`).

#### encrypt
```java
@NonNull SqlExpression<?> encrypt(@NonNull String algorithm, @NonNull byte[] key);
```
Generates SQL: `ENCRYPT('algorithm', key, column)`

Encrypts data using the specified algorithm (`AES`, `XTEA`).

#### decrypt
```java
@NonNull SqlExpression<?> decrypt(@NonNull String algorithm, @NonNull byte[] key);
```
Generates SQL: `DECRYPT('algorithm', key, column)`

Decrypts previously encrypted data.

#### compress
```java
@NonNull SqlExpression<?> compress(@NonNull String algorithm);
```
Generates SQL: `COMPRESS(column, 'algorithm')`

Compresses data using the specified algorithm (`LZF`, `DEFLATE`).

#### expand
```java
@NonNull SqlExpression<?> expand(@NonNull String algorithm);
```
Generates SQL: `EXPAND(column, 'algorithm')`

Decompresses previously compressed data.

### Conversion & Utility

#### castAsVarchar
```java
@NonNull SqlExpression<String> castAsVarchar();
```
Generates SQL: `CAST(column AS VARCHAR)`

Casts to VARCHAR type.

#### castAsInteger
```java
@NonNull SqlExpression<Integer> castAsInteger();
```
Generates SQL: `CAST(column AS INT)`

Casts to integer.

#### castAsBigint
```java
@NonNull SqlExpression<Long> castAsBigint();
```
Generates SQL: `CAST(column AS BIGINT)`

Casts to bigint.

#### castAsDouble
```java
@NonNull SqlExpression<Double> castAsDouble();
```
Generates SQL: `CAST(column AS DOUBLE)`

Casts to double.

#### castAsTimestamp
```java
@NonNull SqlExpression<?> castAsTimestamp();
```
Generates SQL: `CAST(column AS TIMESTAMP)`

Casts to timestamp.

#### toChar
```java
@NonNull SqlExpression<String> toChar(@NonNull String format);
```
Generates SQL: `TO_CHAR(column, 'format')`

Oracle-compatible format conversion.

---

## H2Table Extensions

### Table Types

#### setTableType
```java
void setTableType(@NonNull H2TableType type);
```
Generates SQL: `CREATE type TABLE ...`

Creates a table of the specified type. H2 supports different storage modes.

#### createTemporary
```java
void createTemporary();
```
Generates SQL: `CREATE LOCAL TEMPORARY TABLE ...`

Creates a temporary table that exists only for the current session.

#### createGlobalTemporary
```java
void createGlobalTemporary();
```
Generates SQL: `CREATE GLOBAL TEMPORARY TABLE ...`

Creates a global temporary table visible to all sessions but with session-specific data.

### Linked Tables

#### createLinkedTable
```java
void createLinkedTable(@NonNull String driverClass, @NonNull String url, @NonNull String user, @NonNull String password, @NonNull String originalTable);
```
Generates SQL: `CREATE LINKED TABLE table('driverClass', 'url', 'user', 'password', 'originalTable')`

Creates a linked table that connects to an external database table. Queries are forwarded to the remote database.

#### createLinkedTableReadOnly
```java
void createLinkedTableReadOnly(@NonNull String driverClass, @NonNull String url, @NonNull String user, @NonNull String password, @NonNull String originalTable);
```
Generates SQL: `CREATE LINKED TABLE table('driverClass', 'url', 'user', 'password', 'originalTable') READONLY`

Creates a read-only linked table.

### User-Defined Functions

#### createAlias
```java
void createAlias(@NonNull String name, @NonNull String javaMethod);
```
Generates SQL: `CREATE ALIAS name FOR 'javaMethod'`

Creates a user-defined function backed by a Java method (e.g., `'com.example.MyFunctions.compute'`).

#### createAliasFromSource
```java
void createAliasFromSource(@NonNull String name, @NonNull String source);
```
Generates SQL: `CREATE ALIAS name AS $$ ... $$`

Creates a user-defined function from inline Java source code.

#### dropAlias
```java
void dropAlias(@NonNull String name);
```
Generates SQL: `DROP ALIAS IF EXISTS name`

Drops a user-defined function.

### Domains

#### createDomain
```java
void createDomain(@NonNull String name, @NonNull SqlColumnType baseType);
```
Generates SQL: `CREATE DOMAIN name AS baseType`

Creates a user-defined domain type based on a built-in type.

#### createDomainWithConstraint
```java
void createDomainWithConstraint(@NonNull String name, @NonNull SqlColumnType baseType, @NonNull String checkConstraint);
```
Generates SQL: `CREATE DOMAIN name AS baseType CHECK (checkConstraint)`

Creates a domain with a check constraint.

#### dropDomain
```java
void dropDomain(@NonNull String name);
```
Generates SQL: `DROP DOMAIN IF EXISTS name`

Drops a user-defined domain.

### Triggers

#### createTrigger
```java
void createTrigger(@NonNull String name, @NonNull String timing, @NonNull String event, @NonNull String javaClass);
```
Generates SQL: `CREATE TRIGGER name timing event ON table CALL 'javaClass'`

Creates a trigger that calls a Java class implementing `org.h2.api.Trigger`. `timing` is `BEFORE` or `AFTER`. `event` is `INSERT`, `UPDATE`, or `DELETE`.

#### dropTrigger
```java
void dropTrigger(@NonNull String name);
```
Generates SQL: `DROP TRIGGER IF EXISTS name`

Drops a trigger.

### Views

#### createView
```java
void createView(@NonNull String name, @NonNull String query);
```
Generates SQL: `CREATE VIEW name AS query`

Creates a view.

#### createOrReplaceView
```java
void createOrReplaceView(@NonNull String name, @NonNull String query);
```
Generates SQL: `CREATE OR REPLACE VIEW name AS query`

Creates or replaces a view.

#### dropView
```java
void dropView(@NonNull String name);
```
Generates SQL: `DROP VIEW IF EXISTS name`

Drops a view.

### Constraints

#### addCheckConstraint
```java
void addCheckConstraint(@NonNull String name, @NonNull SqlCondition condition);
```
Generates SQL: `ALTER TABLE ... ADD CONSTRAINT name CHECK (condition)`

Adds a named check constraint.

### Computed Columns

#### addGeneratedColumn
```java
void addGeneratedColumn(@NonNull String name, @NonNull SqlColumnType type, @NonNull String expression);
```
Generates SQL: `ALTER TABLE ... ADD name type GENERATED ALWAYS AS (expression)`

Adds a generated (computed) column.

### Table Maintenance

#### analyze
```java
void analyze();
```
Generates SQL: `ANALYZE TABLE table`

Updates table statistics for the query optimizer.

#### setReadOnly
```java
void setReadOnly(boolean readOnly);
```
Generates SQL: `ALTER TABLE ... SET REFERENTIAL_INTEGRITY readOnly`

Controls read-only mode.

### Database Settings

#### setMode
```java
void setMode(@NonNull H2CompatibilityMode mode);
```
Generates SQL: `SET MODE mode`

Sets the SQL compatibility mode. H2 can emulate the syntax of other databases.

#### setTrace
```java
void setTrace(int level);
```
Generates SQL: `SET TRACE_LEVEL_SYSTEM_OUT level`

Sets the trace level (0=OFF, 1=ERROR, 2=INFO, 3=DEBUG).

#### setCacheSize
```java
void setCacheSize(int kb);
```
Generates SQL: `SET CACHE_SIZE kb`

Sets the cache size in kilobytes.

#### setMaxMemoryRows
```java
void setMaxMemoryRows(int rows);
```
Generates SQL: `SET MAX_MEMORY_ROWS rows`

Sets the maximum number of rows kept in memory for large result sets and temporary tables before spilling to disk.

#### setUndoLogEnabled
```java
void setUndoLogEnabled(boolean enabled);
```
Generates SQL: `SET UNDO_LOG enabled`

Controls whether the undo log is maintained.

#### setLockTimeout
```java
void setLockTimeout(int milliseconds);
```
Generates SQL: `SET LOCK_TIMEOUT milliseconds`

Sets the lock timeout in milliseconds.

### Backup & Export

#### backup
```java
void backup(@NonNull String filepath);
```
Generates SQL: `BACKUP TO 'filepath'`

Creates a database backup as a ZIP file.

#### script
```java
void script(@NonNull String filepath);
```
Generates SQL: `SCRIPT TO 'filepath'`

Exports the database as a SQL script file.

#### scriptNoData
```java
void scriptNoData(@NonNull String filepath);
```
Generates SQL: `SCRIPT NODATA TO 'filepath'`

Exports only the schema (DDL) without data.

#### runScript
```java
void runScript(@NonNull String filepath);
```
Generates SQL: `RUNSCRIPT FROM 'filepath'`

Executes a SQL script from a file.

---

## Supporting Types

### H2TableType (new enum)
```java
public enum H2TableType {
    CACHED,        // Default, data stored on disk
    MEMORY,        // Data stored in memory only
    TEMPORARY,     // Session-scoped temporary
    GLOBAL_TEMPORARY // Global temporary with per-session data
}
```

### H2CompatibilityMode (new enum)
```java
public enum H2CompatibilityMode {
    REGULAR,       // Default H2 mode
    DB2,
    DERBY,
    HSQLDB,
    MSSQL,
    MYSQL,
    ORACLE,
    POSTGRESQL,
    IGNITE
}
```

### H2CompressionAlgorithm (new enum)
```java
public enum H2CompressionAlgorithm {
    LZF,
    DEFLATE
}
```

### H2EncryptionAlgorithm (new enum)
```java
public enum H2EncryptionAlgorithm {
    AES,
    XTEA
}
```

### H2HashAlgorithm (new enum)
```java
public enum H2HashAlgorithm {
    SHA_256,
    SHA_384,
    SHA_512,
    SHA3_256
}
```
