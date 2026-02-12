# Common Conditional & Null Handling Functions Across Dialects

Conditional expressions and null handling functions shared across PostgreSQL, MySQL, MariaDB, and SQLite.
These follow the SQL standard and are available in all dialects, though some conditional functions and null-safe comparison operators differ in syntax.

The base `SqlColumn<T>` interface already provides `isNull()`, `isNotNull()`, and `ifNull(value)` (two-argument null substitution). The functions below cover conditional branching, multi-value null handling, null-safe comparisons, and conditional min/max expressions.

---

## Conditional Expressions

### If Expression

Evaluates a condition and returns one of two values depending on whether the condition is true or false.

| Dialect    | Method Name                              | Generated SQL                                       |
|------------|------------------------------------------|-----------------------------------------------------|
| PostgreSQL | `ifExpression(condition, ifTrue, ifFalse)` | `CASE WHEN condition THEN ifTrue ELSE ifFalse END` |
| MySQL      | `ifExpression(condition, ifTrue, ifFalse)` | `IF(condition, ifTrue, ifFalse)`                    |
| MariaDB    | `ifExpression(condition, ifTrue, ifFalse)` | `IF(condition, ifTrue, ifFalse)`                    |
| SQLite     | `ifExpression(condition, ifTrue, ifFalse)` | `IIF(condition, ifTrue, ifFalse)`                   |

Note: MySQL and MariaDB have a built-in `IF(condition, true_value, false_value)` function. SQLite 3.32.0+ provides `IIF(condition, true_value, false_value)` modeled after the same concept. PostgreSQL has no `IF` or `IIF` function in SQL (only in PL/pgSQL); it uses the SQL standard `CASE WHEN ... THEN ... ELSE ... END` expression instead. All four dialects support the `CASE WHEN` form, but `IF`/`IIF` are more concise where available. The condition must evaluate to a boolean; the `ifTrue` and `ifFalse` values must be of compatible types.

---

## Null Substitution

### Coalesce

Returns the first non-null value among the column and the given fallback values.

| Dialect    | Method Name             | Generated SQL                          |
|------------|-------------------------|----------------------------------------|
| PostgreSQL | `coalesce(val1, val2…)` | `COALESCE(column, val1, val2, ...)`    |
| MySQL      | `coalesce(val1, val2…)` | `COALESCE(column, val1, val2, ...)`    |
| MariaDB    | `coalesce(val1, val2…)` | `COALESCE(column, val1, val2, ...)`    |
| SQLite     | `coalesce(val1, val2…)` | `COALESCE(column, val1, val2, ...)`    |

Note: Unlike `ifNull(value)` (which is equivalent to `COALESCE` with exactly two arguments), `coalesce` accepts an arbitrary number of fallback values and returns the first non-null result. All dialects follow the SQL standard: if all arguments are `NULL`, the result is `NULL`.

### Null If

Returns `NULL` if the column value equals the given value, otherwise returns the column value.

| Dialect    | Method Name      | Generated SQL              |
|------------|------------------|----------------------------|
| PostgreSQL | `nullIf(value)`  | `NULLIF(column, value)`    |
| MySQL      | `nullIf(value)`  | `NULLIF(column, value)`    |
| MariaDB    | `nullIf(value)`  | `NULLIF(column, value)`    |
| SQLite     | `nullIf(value)`  | `NULLIF(column, value)`    |

Note: Commonly used to convert sentinel values (e.g., empty strings, zeros) to `NULL` for proper null handling in aggregations. For example, `NULLIF(column, '')` turns empty strings into `NULL` so that `COALESCE` or `COUNT` behave correctly.

---

## Null-Safe Comparison

### Is Distinct From

Null-safe inequality comparison. Returns `true` when one operand is `NULL` and the other is not, and `false` when both are `NULL`. Unlike `!=`, never returns `NULL`.

| Dialect    | Method Name               | Generated SQL                         |
|------------|---------------------------|---------------------------------------|
| PostgreSQL | `isDistinctFrom(value)`   | `column IS DISTINCT FROM value`       |
| MySQL      | `isDistinctFrom(value)`   | `NOT (column <=> value)`              |
| MariaDB    | `isDistinctFrom(value)`   | `NOT (column <=> value)`              |
| SQLite     | `isDistinctFrom(value)`   | `column IS NOT value`                 |

Note: PostgreSQL uses the SQL standard syntax. MySQL and MariaDB do not support `IS DISTINCT FROM` syntax directly; they negate the null-safe equality operator `<=>`. SQLite supports `IS NOT` for null-safe inequality (since 3.39.0, SQLite also accepts the standard `IS DISTINCT FROM` syntax). The result is always `true` or `false`, never `NULL`.

### Is Not Distinct From

Null-safe equality comparison. Returns `true` when both operands are `NULL`. Unlike `=`, never returns `NULL`.

| Dialect    | Method Name                  | Generated SQL                            |
|------------|------------------------------|------------------------------------------|
| PostgreSQL | `isNotDistinctFrom(value)`   | `column IS NOT DISTINCT FROM value`      |
| MySQL      | `isNotDistinctFrom(value)`   | `column <=> value`                       |
| MariaDB    | `isNotDistinctFrom(value)`   | `column <=> value`                       |
| SQLite     | `isNotDistinctFrom(value)`   | `column IS value`                        |

Note: MySQL and MariaDB's `<=>` operator is the "null-safe equal" operator, which is semantically identical to `IS NOT DISTINCT FROM`. SQLite's `IS` operator performs null-safe equality (since 3.39.0, SQLite also accepts `IS NOT DISTINCT FROM` syntax). This is useful for comparing values where either side might be `NULL` without the three-valued logic of standard `=`.

### Comparison Summary

| Expression           | `a = NULL` | `a IS NOT DISTINCT FROM NULL` | `a IS DISTINCT FROM NULL` |
|----------------------|------------|-------------------------------|---------------------------|
| `a` is `NULL`        | `NULL`     | `true`                        | `false`                   |
| `a` is not `NULL`    | `NULL`     | `false`                       | `true`                    |

---

## Conditional Min / Max

### Greatest

Returns the largest value from a list of expressions. Equivalent to a multi-argument `MAX` that operates on columns within a single row (not across rows).

| Dialect    | Method Name            | Generated SQL                     |
|------------|------------------------|-----------------------------------|
| PostgreSQL | `greatest(val1, val2…)` | `GREATEST(column, val1, val2)`   |
| MySQL      | `greatest(val1, val2…)` | `GREATEST(column, val1, val2)`   |
| MariaDB    | `greatest(val1, val2…)` | `GREATEST(column, val1, val2)`   |
| SQLite     | `greatest(val1, val2…)` | `MAX(column, val1, val2)`        |

Note: SQLite uses the multi-argument `MAX()` function (not the aggregate `MAX()`) for this purpose. The multi-argument form is selected automatically when `MAX` appears with 2+ arguments outside of an aggregate context. SQLite requires version 3.34.0+ for reliable multi-argument `MAX` behavior.

### Least

Returns the smallest value from a list of expressions. Equivalent to a multi-argument `MIN` that operates on columns within a single row (not across rows).

| Dialect    | Method Name         | Generated SQL                     |
|------------|---------------------|-----------------------------------|
| PostgreSQL | `least(val1, val2…)` | `LEAST(column, val1, val2)`      |
| MySQL      | `least(val1, val2…)` | `LEAST(column, val1, val2)`      |
| MariaDB    | `least(val1, val2…)` | `LEAST(column, val1, val2)`      |
| SQLite     | `least(val1, val2…)` | `MIN(column, val1, val2)`        |

Note: SQLite uses the multi-argument `MIN()` function for this purpose.

---

## Key Differences & Limitations

### Null Handling in GREATEST / LEAST

The handling of `NULL` arguments in `GREATEST` and `LEAST` differs significantly:

| Dialect           | `GREATEST(1, NULL, 3)` | Behavior                                     |
|-------------------|------------------------|----------------------------------------------|
| PostgreSQL        | `NULL`                 | Returns `NULL` if any argument is `NULL`     |
| MySQL / MariaDB   | `NULL`                 | Returns `NULL` if any argument is `NULL`     |
| SQLite            | `3`                    | Ignores `NULL` arguments; returns `NULL` only if all are `NULL` |

This is an important behavioral difference. If null safety is required, wrap arguments in `COALESCE` before passing them to `GREATEST` / `LEAST`:
```sql
GREATEST(COALESCE(a, 0), COALESCE(b, 0), COALESCE(c, 0))
```

### Version Requirements

| Dialect    | Feature                    | Minimum Version      |
|------------|----------------------------|-----------------------|
| PostgreSQL | `CASE WHEN`                | 7.0+ (all versions)   |
| PostgreSQL | `IS DISTINCT FROM`         | 8.4+                  |
| PostgreSQL | `GREATEST` / `LEAST`       | 8.1+                  |
| MySQL      | `IF()`                     | 3.23+                 |
| MySQL      | `<=>` operator             | 3.23+                 |
| MariaDB    | `IF()`                     | 5.1+                  |
| MariaDB    | `<=>` operator             | 5.1+                  |
| SQLite     | `IIF()`                    | 3.32.0 (2020)         |
| SQLite     | `IS` / `IS NOT` for values | 3.39.0 (2022)         |
| SQLite     | Multi-argument `MAX`/`MIN` | 3.34.0 (2020)         |

### Type Coercion in GREATEST / LEAST

- **PostgreSQL**: All arguments must be of compatible types. Mismatched types (e.g., integer vs text) produce a type error.
- **MySQL / MariaDB**: Arguments are coerced using implicit conversion rules. Mixed types may produce unexpected results (e.g., `GREATEST('10', 9)` returns `'9'` due to string comparison).
- **SQLite**: Uses dynamic typing with affinity rules. Numeric values are compared numerically; text values are compared as text.

### Standard SQL Conformance

| Function               | SQL Standard | PostgreSQL | MySQL | MariaDB | SQLite |
|------------------------|-------------|------------|-------|---------|--------|
| `COALESCE`             | SQL:1992    | Yes        | Yes   | Yes     | Yes    |
| `NULLIF`               | SQL:1992    | Yes        | Yes   | Yes     | Yes    |
| `IS DISTINCT FROM`     | SQL:1999    | Yes        | No    | No      | Yes*   |
| `IS NOT DISTINCT FROM` | SQL:1999    | Yes        | No    | No      | Yes*   |
| `GREATEST` / `LEAST`   | No          | Yes        | Yes   | Yes     | No**   |
| `CASE WHEN`            | SQL:1992    | Yes        | Yes   | Yes     | Yes    |
| `IF()`                 | No          | No         | Yes   | Yes     | No     |
| `IIF()`                | No          | No         | No    | No      | Yes*** |

\* SQLite supports the standard syntax since 3.39.0. Earlier versions use `IS` / `IS NOT`.
\** SQLite uses multi-argument `MAX()` / `MIN()` instead.
\*** SQLite's `IIF()` is modeled after the same function in Microsoft SQL Server and was added in 3.32.0.
