# MySQL Dialect Extension Guide

## Current State

Summary of what already exists.

## MysqlColumn Extensions

### Pattern Matching & Regular Expressions

### Full-Text Search

#### matchAgainstExpansion
```java
@NonNull SqlCondition matchAgainstExpansion(@NonNull String searchTerms);
```
Generates SQL: `MATCH(column) AGAINST('searchTerms' WITH QUERY EXPANSION)`

Full-text search with automatic query expansion. Performs the search twice: first normally, then again with the most relevant words from the first search added to the query.

### Conditional Functions

#### ifExpression
```java
@NonNull SqlExpression<?> ifExpression(@NonNull SqlCondition condition, @NonNull T ifTrue, @NonNull T ifFalse);
```
Generates SQL: `IF(condition, ifTrue, ifFalse)`

MySQL-specific conditional function. Returns `ifTrue` if the condition is true, otherwise `ifFalse`.

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

### Hashing

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
