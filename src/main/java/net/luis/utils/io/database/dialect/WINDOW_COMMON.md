# Common Window Functions Across Dialects

All standard SQL window functions are supported by every dialect: PostgreSQL (8.4+), MySQL (8.0+), MariaDB (10.2+), and SQLite (3.25+).
The generated SQL is identical across all dialects since these follow the SQL:2003 standard.

Every window function requires an `OVER (...)` clause that specifies partitioning and ordering.
The window specification syntax (`PARTITION BY`, `ORDER BY`, frame clauses) is the same across all dialects.

---

## Ranking Functions

### Row Number

Assigns a sequential integer to each row within a partition, starting at 1. No gaps, no ties.

| Dialect    | Method Name    | Generated SQL               |
|------------|----------------|-----------------------------|
| PostgreSQL | `rowNumber()`  | `ROW_NUMBER() OVER (...)`   |
| MySQL      | `rowNumber()`  | `ROW_NUMBER() OVER (...)`   |
| MariaDB    | `rowNumber()`  | `ROW_NUMBER() OVER (...)`   |
| SQLite     | `rowNumber()`  | `ROW_NUMBER() OVER (...)`   |

### Rank

Assigns a rank to each row within a partition. Rows with equal values receive the same rank, and the next rank is skipped (gaps).

| Dialect    | Method Name | Generated SQL          |
|------------|-------------|------------------------|
| PostgreSQL | `rank()`    | `RANK() OVER (...)`    |
| MySQL      | `rank()`    | `RANK() OVER (...)`    |
| MariaDB    | `rank()`    | `RANK() OVER (...)`    |
| SQLite     | `rank()`    | `RANK() OVER (...)`    |

Note: With values `[10, 10, 30]`, ranks are `[1, 1, 3]` (rank 2 is skipped).

### Dense Rank

Like `RANK()` but without gaps. Rows with equal values receive the same rank, and the next rank is not skipped.

| Dialect    | Method Name   | Generated SQL               |
|------------|---------------|-----------------------------|
| PostgreSQL | `denseRank()` | `DENSE_RANK() OVER (...)`   |
| MySQL      | `denseRank()` | `DENSE_RANK() OVER (...)`   |
| MariaDB    | `denseRank()` | `DENSE_RANK() OVER (...)`   |
| SQLite     | `denseRank()` | `DENSE_RANK() OVER (...)`   |

Note: With values `[10, 10, 30]`, dense ranks are `[1, 1, 2]`.

### Ntile

Distributes rows into the specified number of approximately equal-sized buckets, numbered from 1 to `buckets`.

| Dialect    | Method Name       | Generated SQL                |
|------------|-------------------|------------------------------|
| PostgreSQL | `ntile(buckets)`  | `NTILE(buckets) OVER (...)`  |
| MySQL      | `ntile(buckets)`  | `NTILE(buckets) OVER (...)`  |
| MariaDB    | `ntile(buckets)`  | `NTILE(buckets) OVER (...)`  |
| SQLite     | `ntile(buckets)`  | `NTILE(buckets) OVER (...)`  |

Note: If the number of rows is not evenly divisible by the number of buckets, earlier buckets receive one extra row.

### Percent Rank

Returns the relative rank of the current row as a value between 0 and 1: `(rank - 1) / (total_rows - 1)`.

| Dialect    | Method Name     | Generated SQL                  |
|------------|-----------------|--------------------------------|
| PostgreSQL | `percentRank()` | `PERCENT_RANK() OVER (...)`    |
| MySQL      | `percentRank()` | `PERCENT_RANK() OVER (...)`    |
| MariaDB    | `percentRank()` | `PERCENT_RANK() OVER (...)`    |
| SQLite     | `percentRank()` | `PERCENT_RANK() OVER (...)`    |

Note: The first row in a partition always has a `PERCENT_RANK` of 0. A partition with a single row also returns 0.

### Cumulative Distribution

Returns the fraction of rows with values less than or equal to the current row's value: `(rows_preceding_or_tied) / (total_rows)`.

| Dialect    | Method Name  | Generated SQL              |
|------------|--------------|----------------------------|
| PostgreSQL | `cumeDist()` | `CUME_DIST() OVER (...)`   |
| MySQL      | `cumeDist()` | `CUME_DIST() OVER (...)`   |
| MariaDB    | `cumeDist()` | `CUME_DIST() OVER (...)`   |
| SQLite     | `cumeDist()` | `CUME_DIST() OVER (...)`   |

Note: The result range is `(0, 1]` (never 0, always includes 1 for the last row or rows tied at the maximum).

---

## Value Functions

### Lag

Returns the value from the row `offset` rows before the current row within the partition. Returns null if no such row exists.

| Dialect    | Method Name    | Generated SQL                    |
|------------|----------------|----------------------------------|
| PostgreSQL | `lag(offset)`  | `LAG(column, offset) OVER (...)` |
| MySQL      | `lag(offset)`  | `LAG(column, offset) OVER (...)` |
| MariaDB    | `lag(offset)`  | `LAG(column, offset) OVER (...)` |
| SQLite     | `lag(offset)`  | `LAG(column, offset) OVER (...)` |

Note: `LAG(column, 1)` returns the previous row's value. All dialects also support an optional third argument for a default value when the offset goes beyond the partition boundary: `LAG(column, offset, default)`.

### Lead

Returns the value from the row `offset` rows after the current row within the partition. Returns null if no such row exists.

| Dialect    | Method Name     | Generated SQL                     |
|------------|-----------------|-----------------------------------|
| PostgreSQL | `lead(offset)`  | `LEAD(column, offset) OVER (...)` |
| MySQL      | `lead(offset)`  | `LEAD(column, offset) OVER (...)` |
| MariaDB    | `lead(offset)`  | `LEAD(column, offset) OVER (...)` |
| SQLite     | `lead(offset)`  | `LEAD(column, offset) OVER (...)` |

Note: `LEAD(column, 1)` returns the next row's value. Like `LAG`, all dialects support an optional default value: `LEAD(column, offset, default)`.

### First Value

Returns the first value in the window frame.

| Dialect    | Method Name    | Generated SQL                         |
|------------|----------------|---------------------------------------|
| PostgreSQL | `firstValue()` | `FIRST_VALUE(column) OVER (...)`      |
| MySQL      | `firstValue()` | `FIRST_VALUE(column) OVER (...)`      |
| MariaDB    | `firstValue()` | `FIRST_VALUE(column) OVER (...)`      |
| SQLite     | `firstValue()` | `FIRST_VALUE(column) OVER (...)`      |

Note: The result depends on the window frame. With the default frame (`RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW`), `FIRST_VALUE` returns the first value of the partition.

### Last Value

Returns the last value in the window frame.

| Dialect    | Method Name   | Generated SQL                        |
|------------|---------------|--------------------------------------|
| PostgreSQL | `lastValue()` | `LAST_VALUE(column) OVER (...)`      |
| MySQL      | `lastValue()` | `LAST_VALUE(column) OVER (...)`      |
| MariaDB    | `lastValue()` | `LAST_VALUE(column) OVER (...)`      |
| SQLite     | `lastValue()` | `LAST_VALUE(column) OVER (...)`      |

Note: With the default frame (`RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW`), `LAST_VALUE` returns the current row's value, not the partition's last value. To get the true last value, use `ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING`.

### Nth Value

Returns the value from the Nth row in the window frame (1-based).

| Dialect    | Method Name    | Generated SQL                        |
|------------|----------------|--------------------------------------|
| PostgreSQL | `nthValue(n)`  | `NTH_VALUE(column, n) OVER (...)`    |
| MySQL      | `nthValue(n)`  | `NTH_VALUE(column, n) OVER (...)`    |
| MariaDB    | `nthValue(n)`  | `NTH_VALUE(column, n) OVER (...)`    |
| SQLite     | `nthValue(n)`  | `NTH_VALUE(column, n) OVER (...)`    |

Note: Returns null if the Nth row does not exist within the window frame. Like `LAST_VALUE`, the result depends on the frame specification.

---

## Window Specification

All dialects use the same `OVER (...)` syntax for defining the window:

```sql
function() OVER (
    [PARTITION BY column1, column2, ...]
    [ORDER BY column1 [ASC|DESC], ...]
    [frame_clause]
)
```

### Frame Clause

The frame clause defines which rows are included in the window frame relative to the current row:

```sql
{ ROWS | RANGE | GROUPS } BETWEEN frame_start AND frame_end
```

| Frame Boundary               | Description                                              |
|------------------------------|----------------------------------------------------------|
| `UNBOUNDED PRECEDING`        | Start of the partition                                   |
| `N PRECEDING`                | N rows/range before the current row                      |
| `CURRENT ROW`                | The current row                                          |
| `N FOLLOWING`                | N rows/range after the current row                       |
| `UNBOUNDED FOLLOWING`        | End of the partition                                     |

### Frame Type Support

| Frame Type | PostgreSQL | MySQL | MariaDB | SQLite |
|------------|------------|-------|---------|--------|
| `ROWS`     | Yes        | Yes   | Yes     | Yes    |
| `RANGE`    | Yes        | Yes   | Yes     | Yes    |
| `GROUPS`   | Yes (11+)  | Yes   | Yes     | Yes    |

Note: `ROWS` counts individual rows. `RANGE` groups rows with the same `ORDER BY` value together. `GROUPS` counts peer groups (rows with the same `ORDER BY` value form one group). The default frame when `ORDER BY` is specified is `RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW`.

### Named Windows

All dialects support defining reusable window specifications with `WINDOW`:

```sql
SELECT
    ROW_NUMBER() OVER w,
    RANK() OVER w
FROM table
WINDOW w AS (PARTITION BY category ORDER BY price)
```

---

## Key Differences & Limitations

### Version Requirements

| Dialect    | Minimum Version    | Notes                                           |
|------------|--------------------|-------------------------------------------------|
| PostgreSQL | 8.4 (2009)         | Full support since initial implementation        |
| MySQL      | 8.0 (2018)         | Not available in MySQL 5.x                       |
| MariaDB    | 10.2 (2017)        | Full standard support                            |
| SQLite     | 3.25.0 (2018)      | Full standard support                            |

### Null Handling

All dialects follow the SQL standard for null handling in window functions:
- `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `NTILE`: not affected by nulls (they operate on row position)
- `LAG`, `LEAD`: return null when the offset exceeds the partition boundary (unless a default is specified)
- `FIRST_VALUE`, `LAST_VALUE`, `NTH_VALUE`: return null if the referenced row's value is null
- `PERCENT_RANK`, `CUME_DIST`: nulls are treated as equal to each other for ranking purposes

### Aggregate Functions as Window Functions

All dialects support using standard aggregate functions (`SUM`, `AVG`, `COUNT`, `MIN`, `MAX`) as window functions by adding an `OVER` clause:

```sql
SUM(column) OVER (PARTITION BY category ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)
```

This produces a running total. Any aggregate function can be used this way across all dialects.
