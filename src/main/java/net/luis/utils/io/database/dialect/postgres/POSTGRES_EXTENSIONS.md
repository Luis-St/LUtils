## PostgresColumn Extensions

### Type Casting

#### cast
```java
@NonNull SqlExpression<?> cast(@NonNull SqlColumnType targetType);
```
Generates SQL: `column::targetType`

PostgreSQL-specific shorthand cast operator. More concise than standard `CAST(column AS type)`.

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

