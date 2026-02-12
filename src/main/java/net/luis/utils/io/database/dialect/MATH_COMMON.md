# Common Math Functions Across Dialects

Standard SQL mathematical functions supported by all dialects: PostgreSQL, MySQL, MariaDB, and SQLite (3.35+).
The generated SQL is identical across all dialects for most functions, since these follow standard SQL conventions.

SQLite added built-in math functions in version 3.35.0 (2021-03). Before that version, math functions required loadable extensions.
PostgreSQL, MySQL, and MariaDB have supported these functions natively since their earliest widely-used versions.

---

## Basic Arithmetic

### Absolute Value

Returns the absolute value of a numeric column.

| Dialect    | Method Name | Generated SQL   |
|------------|-------------|-----------------|
| PostgreSQL | `abs()`     | `ABS(column)`   |
| MySQL      | `abs()`     | `ABS(column)`   |
| MariaDB    | `abs()`     | `ABS(column)`   |
| SQLite     | `abs()`     | `ABS(column)`   |

### Sign

Returns -1, 0, or 1 depending on whether the value is negative, zero, or positive.

| Dialect    | Method Name | Generated SQL    |
|------------|-------------|------------------|
| PostgreSQL | `sign()`    | `SIGN(column)`   |
| MySQL      | `sign()`    | `SIGN(column)`   |
| MariaDB    | `sign()`    | `SIGN(column)`   |
| SQLite     | `sign()`    | `SIGN(column)`   |

Note: SQLite requires version 3.39.0+ for `SIGN`. Earlier 3.35+ versions do not include it.

### Modulo

Returns the remainder after division.

| Dialect    | Method Name    | Generated SQL          |
|------------|----------------|------------------------|
| PostgreSQL | `mod(divisor)` | `MOD(column, divisor)` |
| MySQL      | `mod(divisor)` | `MOD(column, divisor)` |
| MariaDB    | `mod(divisor)` | `MOD(column, divisor)` |
| SQLite     | `mod(divisor)` | `MOD(column, divisor)` |

Note: All dialects also support the `%` operator as an alternative (`column % divisor`).

---

## Rounding

### Ceiling

Returns the smallest integer not less than the value.

| Dialect    | Method Name | Generated SQL    |
|------------|-------------|------------------|
| PostgreSQL | `ceil()`    | `CEIL(column)`   |
| MySQL      | `ceil()`    | `CEIL(column)`   |
| MariaDB    | `ceil()`    | `CEIL(column)`   |
| SQLite     | `ceil()`    | `CEIL(column)`   |

Note: PostgreSQL and MySQL also accept `CEILING(column)` as an alias.

### Floor

Returns the largest integer not greater than the value.

| Dialect    | Method Name | Generated SQL     |
|------------|-------------|-------------------|
| PostgreSQL | `floor()`   | `FLOOR(column)`   |
| MySQL      | `floor()`   | `FLOOR(column)`   |
| MariaDB    | `floor()`   | `FLOOR(column)`   |
| SQLite     | `floor()`   | `FLOOR(column)`   |

### Round

Rounds to the specified number of decimal places.

| Dialect    | Method Name       | Generated SQL             |
|------------|-------------------|---------------------------|
| PostgreSQL | `round(decimals)` | `ROUND(column, decimals)` |
| MySQL      | `round(decimals)` | `ROUND(column, decimals)` |
| MariaDB    | `round(decimals)` | `ROUND(column, decimals)` |
| SQLite     | `round(decimals)` | `ROUND(column, decimals)` |

Note: When `decimals` is 0, rounds to the nearest integer. PostgreSQL uses "round half to even" (banker's rounding) for midpoint values, while MySQL, MariaDB, and SQLite use "round half away from zero". For example, `ROUND(2.5)` returns 2 in PostgreSQL but 3 in the other dialects.

### Truncate

Truncates a number to the specified number of decimal places (rounds toward zero).

| Dialect    | Method Name          | Generated SQL                  |
|------------|----------------------|--------------------------------|
| PostgreSQL | `truncate(decimals)` | `TRUNC(column, decimals)`      |
| MySQL      | `truncate(decimals)` | `TRUNCATE(column, decimals)`   |
| MariaDB    | `truncate(decimals)` | `TRUNCATE(column, decimals)`   |
| SQLite     | `truncate(decimals)` | `TRUNC(column)`                |

Note: PostgreSQL and SQLite use `TRUNC`, while MySQL and MariaDB use `TRUNCATE`. SQLite's `TRUNC` does not accept a decimal-places argument; it always truncates to an integer. To emulate decimal truncation in SQLite, use `ROUND(column * POWER(10, decimals)) / POWER(10, decimals)` or similar arithmetic.

---

## Exponential & Logarithmic

### Exponential

Returns Euler's number (e ≈ 2.71828) raised to the power of the column value.

| Dialect    | Method Name | Generated SQL   |
|------------|-------------|-----------------|
| PostgreSQL | `exp()`     | `EXP(column)`   |
| MySQL      | `exp()`     | `EXP(column)`   |
| MariaDB    | `exp()`     | `EXP(column)`   |
| SQLite     | `exp()`     | `EXP(column)`   |

### Natural Logarithm

Returns the natural (base-e) logarithm of the value.

| Dialect    | Method Name | Generated SQL   |
|------------|-------------|-----------------|
| PostgreSQL | `ln()`      | `LN(column)`    |
| MySQL      | `ln()`      | `LN(column)`    |
| MariaDB    | `ln()`      | `LN(column)`    |
| SQLite     | `ln()`      | `LN(column)`    |

### Base-10 Logarithm

Returns the base-10 logarithm of the value.

| Dialect    | Method Name | Generated SQL      |
|------------|-------------|--------------------|
| PostgreSQL | `log10()`   | `LOG(column)`      |
| MySQL      | `log10()`   | `LOG10(column)`    |
| MariaDB    | `log10()`   | `LOG10(column)`    |
| SQLite     | `log10()`   | `LOG10(column)`    |

Note: PostgreSQL's single-argument `LOG(x)` returns the base-10 logarithm, which differs from MySQL, MariaDB, and SQLite where `LOG(x)` returns the natural logarithm. To avoid ambiguity, prefer `LN(x)` for natural logarithm and `LOG10(x)` for base-10 logarithm. PostgreSQL 12+ also accepts `LOG10(x)` as an alias.

### Base-2 Logarithm

Returns the base-2 logarithm of the value.

| Dialect    | Method Name | Generated SQL     |
|------------|-------------|-------------------|
| PostgreSQL | `log2()`    | `LOG(2, column)`  |
| MySQL      | `log2()`    | `LOG2(column)`    |
| MariaDB    | `log2()`    | `LOG2(column)`    |
| SQLite     | `log2()`    | `LOG2(column)`    |

Note: PostgreSQL does not have a dedicated `LOG2` function; it uses the two-argument form `LOG(base, value)` instead.

### Power

Returns the value raised to the given exponent.

| Dialect    | Method Name        | Generated SQL             |
|------------|--------------------|---------------------------|
| PostgreSQL | `power(exponent)`  | `POWER(column, exponent)` |
| MySQL      | `power(exponent)`  | `POWER(column, exponent)` |
| MariaDB    | `power(exponent)`  | `POWER(column, exponent)` |
| SQLite     | `power(exponent)`  | `POWER(column, exponent)` |

Note: MySQL and MariaDB also accept `POW(column, exponent)` as an alias.

### Square Root

Returns the square root of the value.

| Dialect    | Method Name | Generated SQL    |
|------------|-------------|------------------|
| PostgreSQL | `sqrt()`    | `SQRT(column)`   |
| MySQL      | `sqrt()`    | `SQRT(column)`   |
| MariaDB    | `sqrt()`    | `SQRT(column)`   |
| SQLite     | `sqrt()`    | `SQRT(column)`   |

---

## Trigonometric Functions

All standard trigonometric functions are supported across all dialects with identical SQL syntax.
SQLite requires version 3.35.0+ for these functions.

| Method Name  | Generated SQL          | Description                    |
|--------------|------------------------|--------------------------------|
| `sin()`      | `SIN(column)`          | Sine (input in radians)        |
| `cos()`      | `COS(column)`          | Cosine (input in radians)      |
| `tan()`      | `TAN(column)`          | Tangent (input in radians)     |
| `asin()`     | `ASIN(column)`         | Arc sine (returns radians)     |
| `acos()`     | `ACOS(column)`         | Arc cosine (returns radians)   |
| `atan()`     | `ATAN(column)`         | Arc tangent (returns radians)  |
| `atan2(x)`   | `ATAN2(column, x)`     | Two-argument arc tangent       |

### Angle Conversion

| Method Name  | Generated SQL          | Description                    |
|--------------|------------------------|--------------------------------|
| `radians()`  | `RADIANS(column)`      | Converts degrees to radians    |
| `degrees()`  | `DEGREES(column)`      | Converts radians to degrees    |

Note: All dialects also support `PI()` as a constant (no column argument). SQLite requires 3.35.0+ for `PI()`.

---

## Key Differences & Limitations

### Version Requirements

| Dialect    | Minimum Version | Notes                                                              |
|------------|-----------------|--------------------------------------------------------------------|
| PostgreSQL | 7.1+            | Full math function support since early versions                    |
| MySQL      | 4.0+            | Full math function support since early versions                    |
| MariaDB    | 5.1+            | Full math function support since early versions                    |
| SQLite     | 3.35.0 (2021)   | Math functions added as built-ins; `SIGN` requires 3.39.0 (2022)  |

### LOG Function Ambiguity

The single-argument `LOG()` function has different semantics across dialects:

| Dialect           | `LOG(x)` Returns   | `LOG(b, x)` Returns |
|-------------------|--------------------|-----------------------|
| PostgreSQL        | Base-10 logarithm  | Log base b of x      |
| MySQL / MariaDB   | Natural logarithm  | Log base b of x      |
| SQLite            | Natural logarithm  | Log base b of x      |

To avoid confusion, always use `LN(x)` for natural logarithm and `LOG10(x)` for base-10 logarithm instead of the ambiguous `LOG(x)`.

### Rounding Behavior

PostgreSQL uses "round half to even" (banker's rounding) for `ROUND()`, while MySQL, MariaDB, and SQLite use "round half away from zero":

| Expression    | PostgreSQL | MySQL / MariaDB / SQLite |
|---------------|------------|--------------------------|
| `ROUND(2.5)`  | 2          | 3                        |
| `ROUND(3.5)`  | 4          | 4                        |
| `ROUND(-2.5)` | -2         | -3                       |

### Null Handling

All dialects return `NULL` if the input column value is `NULL` for all math functions. No special null handling is needed.

### Integer vs Float Behavior

- **PostgreSQL**: `ROUND(x, n)` and `TRUNC(x, n)` return `numeric` type. Integer arguments to `CEIL`/`FLOOR` return integers.
- **MySQL / MariaDB**: Return type depends on the input type (integer input → integer result for `CEIL`/`FLOOR`, float input → float result).
- **SQLite**: Math functions always return a floating-point result, except `ABS` which preserves the input type.
