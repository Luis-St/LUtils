# Window Function Analysis

## Current State

The `SqlWindow` class (`function/SqlWindow.java`) defines 18 static methods covering ranking, navigation, and value functions - all stubs throwing `UnsupportedOperationException`. The concept doc (`v4/02-query-api.md:122-134`) shows this intended usage:

```java
SqlWindow.rowNumber()
    .over(SqlWindow.partitionBy(UserTable.STATUS)
                    .orderBy(UserTable.CREATED_AT.desc()))
    .as("rank")
```

## Fundamental Problems with the Current Design

### 1. Static method composition doesn't work

`over()`, `partitionBy()`, and `orderBy()` are all independent static methods returning `SqlExpression<?>`. But the concept example calls `.over()` as an **instance method** on the result of `rowNumber()`. This contradicts the actual API - `SqlExpression<T>` has no `over()` method, and `SqlWindow.over()` is static.

### 2. No window specification abstraction

In SQL, the `OVER(...)` clause is a single composite specification:

```sql
OVER (
    PARTITION BY col1, col2
    ORDER BY col3 DESC
    ROWS BETWEEN 3 PRECEDING AND CURRENT ROW
)
```

Currently, `partitionBy()` and `orderBy()` each return standalone `SqlExpression<?>` values. There's no object that holds the full window specification (partition + order + frame) together.

### 3. Type information is lost

- `rowNumber()` returns `SqlExpression<Long>`
- `over()` returns `SqlExpression<?>` (wildcard)

After chaining through `over()`, the `Long` type is erased. The result can no longer be used in type-safe comparisons or ordering.

### 4. Frame specifications are completely absent

There is zero support for:

- `ROWS BETWEEN ... AND ...`
- `RANGE BETWEEN ... AND ...`
- `GROUPS BETWEEN ... AND ...`
- `UNBOUNDED PRECEDING` / `N PRECEDING`
- `CURRENT ROW`
- `N FOLLOWING` / `UNBOUNDED FOLLOWING`

### 5. Aggregate functions can't be windowed

In SQL, any aggregate can be used as a window function: `SUM(col) OVER (...)`, `AVG(col) OVER (...)`. But `SqlAgg` returns `SqlExpression<>` which has no `over()` method, so there's no way to attach a window spec to an aggregate.

## What Would Be Needed

To support complex window functions like:

```sql
SUM(amount) OVER (PARTITION BY dept ORDER BY hire_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)
```

The system needs three new abstractions:

### A) `SqlWindowSpec` - The full window definition

```
partitionBy(columns...) + orderBy(orderables...) + frame(SqlWindowFrame)
```

### B) `SqlWindowFrame` - Frame boundary specification

```
ROWS/RANGE/GROUPS BETWEEN <start> AND <end>
```

Where start/end can be `UNBOUNDED PRECEDING`, `N PRECEDING`, `CURRENT ROW`, `N FOLLOWING`, `UNBOUNDED FOLLOWING`.

### C) `SqlWindowExpression<T>` - A typed window expression

An expression type that preserves the return type and has an `.over(SqlWindowSpec)` method, so:

- Window functions (`rowNumber()`, `rank()`, etc.) return `SqlWindowExpression<Long>`
- Aggregates get a window-able variant
- `.over()` returns `SqlExpression<T>` (type preserved)

## Verdict

**The current `SqlWindow` structure cannot support complex window functions.** The static-method-returning-`SqlExpression<?>` approach is fundamentally the wrong abstraction. However, the **surrounding infrastructure is solid** - `SqlExpression<T>`, `SqlOrderable`, `SqlDialect`, and the query builder pattern are all good foundations. The fix requires:

1. Replacing `SqlWindow`'s static `over()`/`partitionBy()`/`orderBy()` with a proper `SqlWindowSpec` builder
2. Adding a `SqlWindowFrame` type for frame boundaries
3. Making window functions return a type that accepts `.over(spec)` while preserving `<T>`
4. Enabling aggregate expressions to also accept `.over(spec)`

The rest of the system (dialect-aware `toSql()`, expression composition, query builders) can accommodate this without changes.
