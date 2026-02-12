# PostgreSQL Dialect Extension Guide

## Current State

Summary of what already exists (list the interfaces and their current methods briefly).

- **PostgresColumn\<T\>**: `similarTo`, `posixRegex`, `ilike`, `notIlike`, `fullTextSearch`
- **PostgresTable\<T\>**: `select()` (returning PostgresSelectQuery), `setUnlogged`, `setTablespace`, `enableRowLevelSecurity`
- **PostgresSelectQuery\<T\>**: `distinctOn`
- **SqlColumn\<T\>** (base): `equalTo`, `notEqualTo`, `greaterThan`, `greaterThanOrEqualTo`, `lessThan`, `lessThanOrEqualTo`, `between`, `isNull`, `isNotNull`, `like`, `startsWith`, `contains`, `endsWith`, `lengthGreaterThan`, `equalToIgnoreCase`, `withinLast`, `in`, `notIn`, `ifNull`, `as`, `asc`, `desc`, `nullsFirst`, `nullsLast`

## PostgresColumn Extensions

### Type Casting

#### cast
```java
@NonNull SqlExpression<?> cast(@NonNull SqlColumnType targetType);
```
Generates SQL: `column::targetType`

PostgreSQL-specific shorthand cast operator. More concise than standard `CAST(column AS type)`.

### String Aggregation

#### stringAgg
```java
@NonNull SqlExpression<String> stringAgg(@NonNull String separator);
```
Generates SQL: `STRING_AGG(column, 'separator')`

Concatenates values from grouped rows into a single string separated by the given separator. PostgreSQL equivalent of MySQL's `GROUP_CONCAT`.

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
