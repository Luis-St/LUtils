# Common Date/Time Functions Across Dialects

Date and time functions shared across PostgreSQL, MySQL, MariaDB, and SQLite.
While all four dialects support date/time operations, the underlying SQL varies significantly due to different function names, interval syntax, and type systems.

PostgreSQL, MySQL, and MariaDB have native `DATE`, `TIME`, `DATETIME`/`TIMESTAMP` types.
SQLite has no dedicated date/time types; it stores dates as `TEXT` (ISO 8601 strings), `REAL` (Julian day numbers), or `INTEGER` (Unix timestamps) and relies on built-in date/time functions to interpret them.

---

## Current Date & Time

### Current Date

Returns the current date (without time component).

| Dialect    | Method Name     | Generated SQL    |
|------------|-----------------|------------------|
| PostgreSQL | `currentDate()` | `CURRENT_DATE`   |
| MySQL      | `currentDate()` | `CURRENT_DATE`   |
| MariaDB    | `currentDate()` | `CURRENT_DATE`   |
| SQLite     | `currentDate()` | `DATE('now')`    |

Note: `CURRENT_DATE` is SQL standard and supported natively by PostgreSQL, MySQL, and MariaDB. SQLite uses its `DATE()` function with `'now'` as the time value. MySQL and MariaDB also accept `CURDATE()` as an alias.

### Current Time

Returns the current time (without date component).

| Dialect    | Method Name     | Generated SQL    |
|------------|-----------------|------------------|
| PostgreSQL | `currentTime()` | `CURRENT_TIME`   |
| MySQL      | `currentTime()` | `CURRENT_TIME`   |
| MariaDB    | `currentTime()` | `CURRENT_TIME`   |
| SQLite     | `currentTime()` | `TIME('now')`    |

Note: PostgreSQL's `CURRENT_TIME` includes a time zone offset. MySQL and MariaDB also accept `CURTIME()` as an alias. SQLite returns time as a `TEXT` string in `HH:MM:SS` format.

### Current Timestamp

Returns the current date and time.

| Dialect    | Method Name          | Generated SQL        |
|------------|----------------------|----------------------|
| PostgreSQL | `currentTimestamp()` | `CURRENT_TIMESTAMP`  |
| MySQL      | `currentTimestamp()` | `CURRENT_TIMESTAMP`  |
| MariaDB    | `currentTimestamp()` | `CURRENT_TIMESTAMP`  |
| SQLite     | `currentTimestamp()` | `DATETIME('now')`    |

Note: `CURRENT_TIMESTAMP` is SQL standard. PostgreSQL returns a `TIMESTAMP WITH TIME ZONE`. MySQL and MariaDB return `DATETIME`. All three also accept `NOW()` as an alias. SQLite returns a `TEXT` string in `YYYY-MM-DD HH:MM:SS` format. An important difference: PostgreSQL's `CURRENT_TIMESTAMP` returns the transaction start time (constant within a transaction), while `CLOCK_TIMESTAMP()` returns the actual current time. MySQL's `NOW()` behaves similarly (constant within a statement), while `SYSDATE()` returns the execution time.

---

## Date/Time Part Extraction

### Extract Year

Returns the year from a date or timestamp column.

| Dialect    | Method Name      | Generated SQL                                |
|------------|------------------|----------------------------------------------|
| PostgreSQL | `extractYear()`  | `EXTRACT(YEAR FROM column)`                  |
| MySQL      | `extractYear()`  | `EXTRACT(YEAR FROM column)`                  |
| MariaDB    | `extractYear()`  | `EXTRACT(YEAR FROM column)`                  |
| SQLite     | `extractYear()`  | `CAST(STRFTIME('%Y', column) AS INTEGER)`    |

Note: PostgreSQL 14+ returns `numeric` type; earlier versions return `double precision`. MySQL and MariaDB also accept the shorthand `YEAR(column)`. SQLite requires `STRFTIME` with explicit casting since it returns text.

### Extract Month

Returns the month (1–12) from a date or timestamp column.

| Dialect    | Method Name       | Generated SQL                                |
|------------|-------------------|----------------------------------------------|
| PostgreSQL | `extractMonth()`  | `EXTRACT(MONTH FROM column)`                 |
| MySQL      | `extractMonth()`  | `EXTRACT(MONTH FROM column)`                 |
| MariaDB    | `extractMonth()`  | `EXTRACT(MONTH FROM column)`                 |
| SQLite     | `extractMonth()`  | `CAST(STRFTIME('%m', column) AS INTEGER)`    |

Note: MySQL and MariaDB also accept `MONTH(column)`.

### Extract Day

Returns the day of the month (1–31) from a date or timestamp column.

| Dialect    | Method Name     | Generated SQL                                |
|------------|-----------------|----------------------------------------------|
| PostgreSQL | `extractDay()`  | `EXTRACT(DAY FROM column)`                   |
| MySQL      | `extractDay()`  | `EXTRACT(DAY FROM column)`                   |
| MariaDB    | `extractDay()`  | `EXTRACT(DAY FROM column)`                   |
| SQLite     | `extractDay()`  | `CAST(STRFTIME('%d', column) AS INTEGER)`    |

Note: MySQL and MariaDB also accept `DAY(column)` and `DAYOFMONTH(column)`.

### Extract Hour

Returns the hour (0–23) from a time or timestamp column.

| Dialect    | Method Name      | Generated SQL                                |
|------------|------------------|----------------------------------------------|
| PostgreSQL | `extractHour()`  | `EXTRACT(HOUR FROM column)`                  |
| MySQL      | `extractHour()`  | `EXTRACT(HOUR FROM column)`                  |
| MariaDB    | `extractHour()`  | `EXTRACT(HOUR FROM column)`                  |
| SQLite     | `extractHour()`  | `CAST(STRFTIME('%H', column) AS INTEGER)`    |

Note: MySQL and MariaDB also accept `HOUR(column)`.

### Extract Minute

Returns the minute (0–59) from a time or timestamp column.

| Dialect    | Method Name        | Generated SQL                                |
|------------|--------------------|----------------------------------------------|
| PostgreSQL | `extractMinute()`  | `EXTRACT(MINUTE FROM column)`                |
| MySQL      | `extractMinute()`  | `EXTRACT(MINUTE FROM column)`                |
| MariaDB    | `extractMinute()`  | `EXTRACT(MINUTE FROM column)`                |
| SQLite     | `extractMinute()`  | `CAST(STRFTIME('%M', column) AS INTEGER)`    |

Note: MySQL and MariaDB also accept `MINUTE(column)`.

### Extract Second

Returns the second (0–59) from a time or timestamp column.

| Dialect    | Method Name        | Generated SQL                                |
|------------|--------------------|----------------------------------------------|
| PostgreSQL | `extractSecond()`  | `EXTRACT(SECOND FROM column)`                |
| MySQL      | `extractSecond()`  | `EXTRACT(SECOND FROM column)`                |
| MariaDB    | `extractSecond()`  | `EXTRACT(SECOND FROM column)`                |
| SQLite     | `extractSecond()`  | `CAST(STRFTIME('%S', column) AS INTEGER)`    |

Note: PostgreSQL's `EXTRACT(SECOND ...)` includes fractional seconds (e.g., `30.5`). MySQL, MariaDB, and SQLite return only whole seconds from `EXTRACT`/`STRFTIME`. MySQL and MariaDB also accept `SECOND(column)`. To get fractional seconds in MySQL/MariaDB, use `EXTRACT(MICROSECOND FROM column)`.

### Extract Day of Week

Returns the day of the week from a date or timestamp column.

| Dialect    | Method Name          | Generated SQL                                |
|------------|----------------------|----------------------------------------------|
| PostgreSQL | `extractDayOfWeek()` | `EXTRACT(DOW FROM column)`                   |
| MySQL      | `extractDayOfWeek()` | `DAYOFWEEK(column)`                          |
| MariaDB    | `extractDayOfWeek()` | `DAYOFWEEK(column)`                          |
| SQLite     | `extractDayOfWeek()` | `CAST(STRFTIME('%w', column) AS INTEGER)`    |

Note: The numbering differs across dialects. PostgreSQL and SQLite use 0 = Sunday through 6 = Saturday. MySQL and MariaDB use 1 = Sunday through 7 = Saturday (ODBC standard). To get ISO day-of-week (1 = Monday through 7 = Sunday), use `EXTRACT(ISODOW FROM column)` in PostgreSQL or `WEEKDAY(column) + 1` in MySQL/MariaDB.

### Extract Day of Year

Returns the day of the year (1–366) from a date or timestamp column.

| Dialect    | Method Name           | Generated SQL                                |
|------------|-----------------------|----------------------------------------------|
| PostgreSQL | `extractDayOfYear()`  | `EXTRACT(DOY FROM column)`                   |
| MySQL      | `extractDayOfYear()`  | `DAYOFYEAR(column)`                          |
| MariaDB    | `extractDayOfYear()`  | `DAYOFYEAR(column)`                          |
| SQLite     | `extractDayOfYear()`  | `CAST(STRFTIME('%j', column) AS INTEGER)`    |

### Extract ISO Week

Returns the ISO 8601 week number (1–53) from a date or timestamp column.

| Dialect    | Method Name      | Generated SQL                                |
|------------|------------------|----------------------------------------------|
| PostgreSQL | `extractWeek()`  | `EXTRACT(WEEK FROM column)`                  |
| MySQL      | `extractWeek()`  | `WEEKOFYEAR(column)`                         |
| MariaDB    | `extractWeek()`  | `WEEKOFYEAR(column)`                         |
| SQLite     | `extractWeek()`  | `CAST(STRFTIME('%W', column) AS INTEGER)`    |

Note: PostgreSQL's `EXTRACT(WEEK ...)` returns the ISO 8601 week number (weeks start on Monday, week 1 contains January 4th). MySQL's `WEEKOFYEAR(column)` is equivalent to `WEEK(column, 3)` and follows ISO 8601. SQLite's `%W` returns the week number with Monday as the first day (00–53), which is close to but not identical to ISO 8601 (it does not handle the year-boundary rule where the first days of January may belong to the previous year's last week).

### Extract Quarter

Returns the quarter (1–4) from a date or timestamp column.

| Dialect    | Method Name         | Generated SQL                                            |
|------------|---------------------|----------------------------------------------------------|
| PostgreSQL | `extractQuarter()`  | `EXTRACT(QUARTER FROM column)`                           |
| MySQL      | `extractQuarter()`  | `QUARTER(column)`                                        |
| MariaDB    | `extractQuarter()`  | `QUARTER(column)`                                        |
| SQLite     | `extractQuarter()`  | `((CAST(STRFTIME('%m', column) AS INTEGER) + 2) / 3)`   |

Note: SQLite has no built-in quarter function. The formula `(month + 2) / 3` with integer division maps months 1–3 to Q1, 4–6 to Q2, 7–9 to Q3, and 10–12 to Q4.

---

## Date/Time Arithmetic

### Add Years

Adds the specified number of years to a date or timestamp column.

| Dialect    | Method Name    | Generated SQL                              |
|------------|----------------|--------------------------------------------|
| PostgreSQL | `addYears(n)`  | `column + INTERVAL 'n years'`              |
| MySQL      | `addYears(n)`  | `DATE_ADD(column, INTERVAL n YEAR)`        |
| MariaDB    | `addYears(n)`  | `DATE_ADD(column, INTERVAL n YEAR)`        |
| SQLite     | `addYears(n)`  | `DATETIME(column, '+n years')`             |

Note: Use negative values to subtract years. PostgreSQL also supports `column - INTERVAL 'n years'`. MySQL and MariaDB also accept `DATE_SUB(column, INTERVAL n YEAR)`. SQLite uses `DATETIME()` for timestamp values and `DATE()` for date-only values.

### Add Months

Adds the specified number of months to a date or timestamp column.

| Dialect    | Method Name     | Generated SQL                              |
|------------|-----------------|---------------------------------------------|
| PostgreSQL | `addMonths(n)`  | `column + INTERVAL 'n months'`             |
| MySQL      | `addMonths(n)`  | `DATE_ADD(column, INTERVAL n MONTH)`       |
| MariaDB    | `addMonths(n)`  | `DATE_ADD(column, INTERVAL n MONTH)`       |
| SQLite     | `addMonths(n)`  | `DATETIME(column, '+n months')`            |

Note: End-of-month handling varies. Adding 1 month to January 31st: PostgreSQL returns February 28th (or 29th in leap years), MySQL and MariaDB return February 28th/29th, and SQLite returns March 3rd (or 2nd in leap years) because it simply increments the month number and overflows. This is an important behavioral difference for end-of-month dates.

### Add Days

Adds the specified number of days to a date or timestamp column.

| Dialect    | Method Name   | Generated SQL                              |
|------------|---------------|---------------------------------------------|
| PostgreSQL | `addDays(n)`  | `column + INTERVAL 'n days'`               |
| MySQL      | `addDays(n)`  | `DATE_ADD(column, INTERVAL n DAY)`         |
| MariaDB    | `addDays(n)`  | `DATE_ADD(column, INTERVAL n DAY)`         |
| SQLite     | `addDays(n)`  | `DATETIME(column, '+n days')`              |

Note: For PostgreSQL `DATE` columns, integer addition also works directly: `column + n` adds `n` days. This does not work for `TIMESTAMP` columns.

### Add Hours

Adds the specified number of hours to a timestamp column.

| Dialect    | Method Name    | Generated SQL                              |
|------------|----------------|--------------------------------------------|
| PostgreSQL | `addHours(n)`  | `column + INTERVAL 'n hours'`              |
| MySQL      | `addHours(n)`  | `DATE_ADD(column, INTERVAL n HOUR)`        |
| MariaDB    | `addHours(n)`  | `DATE_ADD(column, INTERVAL n HOUR)`        |
| SQLite     | `addHours(n)`  | `DATETIME(column, '+n hours')`             |

### Add Minutes

Adds the specified number of minutes to a timestamp column.

| Dialect    | Method Name      | Generated SQL                                |
|------------|------------------|----------------------------------------------|
| PostgreSQL | `addMinutes(n)`  | `column + INTERVAL 'n minutes'`              |
| MySQL      | `addMinutes(n)`  | `DATE_ADD(column, INTERVAL n MINUTE)`        |
| MariaDB    | `addMinutes(n)`  | `DATE_ADD(column, INTERVAL n MINUTE)`        |
| SQLite     | `addMinutes(n)`  | `DATETIME(column, '+n minutes')`             |

### Add Seconds

Adds the specified number of seconds to a timestamp column.

| Dialect    | Method Name      | Generated SQL                                |
|------------|------------------|----------------------------------------------|
| PostgreSQL | `addSeconds(n)`  | `column + INTERVAL 'n seconds'`              |
| MySQL      | `addSeconds(n)`  | `DATE_ADD(column, INTERVAL n SECOND)`        |
| MariaDB    | `addSeconds(n)`  | `DATE_ADD(column, INTERVAL n SECOND)`        |
| SQLite     | `addSeconds(n)`  | `DATETIME(column, '+n seconds')`             |

### Difference in Days

Returns the number of days between the column value and another date (column - other).

| Dialect    | Method Name        | Generated SQL                                             |
|------------|--------------------|-----------------------------------------------------------|
| PostgreSQL | `diffInDays(other)` | `CAST(column AS DATE) - CAST(other AS DATE)`             |
| MySQL      | `diffInDays(other)` | `DATEDIFF(column, other)`                                |
| MariaDB    | `diffInDays(other)` | `DATEDIFF(column, other)`                                |
| SQLite     | `diffInDays(other)` | `CAST(JULIANDAY(column) - JULIANDAY(other) AS INTEGER)`  |

Note: A positive result means the column date is after the other date. PostgreSQL's date subtraction returns an exact integer. MySQL/MariaDB's `DATEDIFF` ignores the time portion and computes the difference based on date values only. SQLite's `JULIANDAY` subtraction returns a fractional number; the `CAST` rounds toward zero to produce whole days.

### Difference in Seconds

Returns the number of seconds between the column value and another timestamp (column - other).

| Dialect    | Method Name           | Generated SQL                                                   |
|------------|-----------------------|-----------------------------------------------------------------|
| PostgreSQL | `diffInSeconds(other)` | `EXTRACT(EPOCH FROM (column - other))`                         |
| MySQL      | `diffInSeconds(other)` | `TIMESTAMPDIFF(SECOND, other, column)`                         |
| MariaDB    | `diffInSeconds(other)` | `TIMESTAMPDIFF(SECOND, other, column)`                         |
| SQLite     | `diffInSeconds(other)` | `CAST(STRFTIME('%s', column) - STRFTIME('%s', other) AS INTEGER)` |

Note: A positive result means the column timestamp is after the other timestamp. PostgreSQL's `EXTRACT(EPOCH FROM interval)` returns the total number of seconds including fractional seconds. MySQL/MariaDB's `TIMESTAMPDIFF` also supports other units (`MINUTE`, `HOUR`, `DAY`, `MONTH`, `YEAR`) for computing differences in those units directly. SQLite's `STRFTIME('%s', ...)` returns the Unix timestamp as text, but the arithmetic expression performs implicit numeric conversion. SQLite 3.38+ also supports `UNIXEPOCH(column)` as a cleaner alternative.

---

## Date/Time Truncation

Truncates a timestamp to the specified precision, setting smaller components to their minimum values (zero for time parts, 1 for month/day).

### Truncate to Year

Truncates to the start of the year (January 1st, 00:00:00).

| Dialect    | Method Name        | Generated SQL                                    |
|------------|--------------------|--------------------------------------------------|
| PostgreSQL | `truncateToYear()`  | `DATE_TRUNC('year', column)`                    |
| MySQL      | `truncateToYear()`  | `DATE_FORMAT(column, '%Y-01-01 00:00:00')`      |
| MariaDB    | `truncateToYear()`  | `DATE_FORMAT(column, '%Y-01-01 00:00:00')`      |
| SQLite     | `truncateToYear()`  | `STRFTIME('%Y-01-01 00:00:00', column)`          |

### Truncate to Month

Truncates to the start of the month (1st day, 00:00:00).

| Dialect    | Method Name         | Generated SQL                                    |
|------------|---------------------|--------------------------------------------------|
| PostgreSQL | `truncateToMonth()`  | `DATE_TRUNC('month', column)`                   |
| MySQL      | `truncateToMonth()`  | `DATE_FORMAT(column, '%Y-%m-01 00:00:00')`      |
| MariaDB    | `truncateToMonth()`  | `DATE_FORMAT(column, '%Y-%m-01 00:00:00')`      |
| SQLite     | `truncateToMonth()`  | `STRFTIME('%Y-%m-01 00:00:00', column)`          |

### Truncate to Day

Truncates to the start of the day (00:00:00).

| Dialect    | Method Name       | Generated SQL                                    |
|------------|-------------------|--------------------------------------------------|
| PostgreSQL | `truncateToDay()`  | `DATE_TRUNC('day', column)`                     |
| MySQL      | `truncateToDay()`  | `DATE(column)`                                   |
| MariaDB    | `truncateToDay()`  | `DATE(column)`                                   |
| SQLite     | `truncateToDay()`  | `DATE(column)`                                   |

Note: MySQL, MariaDB, and SQLite's `DATE()` returns a date-only value (no time component). PostgreSQL's `DATE_TRUNC('day', ...)` returns a timestamp with the time set to midnight. To get a date-only value in PostgreSQL, use `CAST(column AS DATE)` instead.

### Truncate to Hour

Truncates to the start of the hour (minutes and seconds set to 00).

| Dialect    | Method Name        | Generated SQL                                    |
|------------|--------------------|--------------------------------------------------|
| PostgreSQL | `truncateToHour()`  | `DATE_TRUNC('hour', column)`                    |
| MySQL      | `truncateToHour()`  | `DATE_FORMAT(column, '%Y-%m-%d %H:00:00')`      |
| MariaDB    | `truncateToHour()`  | `DATE_FORMAT(column, '%Y-%m-%d %H:00:00')`      |
| SQLite     | `truncateToHour()`  | `STRFTIME('%Y-%m-%d %H:00:00', column)`          |

### Truncate to Minute

Truncates to the start of the minute (seconds set to 00).

| Dialect    | Method Name          | Generated SQL                                    |
|------------|----------------------|--------------------------------------------------|
| PostgreSQL | `truncateToMinute()`  | `DATE_TRUNC('minute', column)`                  |
| MySQL      | `truncateToMinute()`  | `DATE_FORMAT(column, '%Y-%m-%d %H:%i:00')`      |
| MariaDB    | `truncateToMinute()`  | `DATE_FORMAT(column, '%Y-%m-%d %H:%i:00')`      |
| SQLite     | `truncateToMinute()`  | `STRFTIME('%Y-%m-%d %H:%M:00', column)`          |

Note: PostgreSQL is the only dialect with a native `DATE_TRUNC` function that accepts an arbitrary precision unit. MySQL and MariaDB use `DATE_FORMAT` to reconstruct the truncated timestamp as a formatted string. SQLite uses `STRFTIME` similarly. An important format difference: MySQL/MariaDB use `%i` for minutes in `DATE_FORMAT`, while SQLite uses `%M` in `STRFTIME`.

---

## Date/Time Formatting

### Format Date/Time

Converts a date or timestamp to a formatted string using a dialect-specific pattern.

| Dialect    | Method Name           | Generated SQL                        |
|------------|-----------------------|--------------------------------------|
| PostgreSQL | `formatDate(pattern)` | `TO_CHAR(column, 'pattern')`         |
| MySQL      | `formatDate(pattern)` | `DATE_FORMAT(column, 'pattern')`     |
| MariaDB    | `formatDate(pattern)` | `DATE_FORMAT(column, 'pattern')`     |
| SQLite     | `formatDate(pattern)` | `STRFTIME('pattern', column)`        |

Note: Each dialect uses a different pattern syntax. The method must translate the abstract pattern to the appropriate dialect-specific format string. The table below maps common format elements across dialects:

| Component            | PostgreSQL (`TO_CHAR`)  | MySQL/MariaDB (`DATE_FORMAT`) | SQLite (`STRFTIME`) |
|----------------------|-------------------------|-------------------------------|---------------------|
| 4-digit year         | `YYYY`                  | `%Y`                          | `%Y`                |
| 2-digit year         | `YY`                    | `%y`                          | —                   |
| Month (01–12)        | `MM`                    | `%m`                          | `%m`                |
| Day (01–31)          | `DD`                    | `%d`                          | `%d`                |
| Hour 24h (00–23)     | `HH24`                  | `%H`                          | `%H`                |
| Hour 12h (01–12)     | `HH12`                  | `%h`                          | —                   |
| Minute (00–59)       | `MI`                    | `%i`                          | `%M`                |
| Second (00–59)       | `SS`                    | `%s`                          | `%S`                |
| AM/PM                | `AM` or `PM`            | `%p`                          | —                   |
| Full month name      | `Month`                 | `%M`                          | —                   |
| Abbreviated month    | `Mon`                   | `%b`                          | —                   |
| Full day name        | `Day`                   | `%W`                          | —                   |
| Abbreviated day      | `Dy`                    | `%a`                          | —                   |
| Day of year          | `DDD`                   | `%j`                          | `%j`                |
| Unix timestamp       | —                       | —                             | `%s`                |

Note: SQLite's `STRFTIME` has a limited set of format specifiers compared to the other dialects. It does not support named months, named days, 12-hour time, or AM/PM formatting. PostgreSQL's `TO_CHAR` is the most flexible, supporting ordinal suffixes (`DDth`), fill mode (`FM` prefix to suppress padding), and locale-aware names.

---

## Epoch Conversion

### To Unix Epoch

Converts a date or timestamp to a Unix timestamp (seconds since 1970-01-01 00:00:00 UTC).

| Dialect    | Method Name  | Generated SQL                        |
|------------|--------------|--------------------------------------|
| PostgreSQL | `toEpoch()`  | `EXTRACT(EPOCH FROM column)`         |
| MySQL      | `toEpoch()`  | `UNIX_TIMESTAMP(column)`             |
| MariaDB    | `toEpoch()`  | `UNIX_TIMESTAMP(column)`             |
| SQLite     | `toEpoch()`  | `UNIXEPOCH(column)`                  |

Note: PostgreSQL's `EXTRACT(EPOCH FROM ...)` returns fractional seconds as `double precision`. MySQL/MariaDB's `UNIX_TIMESTAMP` returns an integer (or decimal if the column has fractional seconds). SQLite's `UNIXEPOCH` was added in version 3.38.0 (2022); for older versions, use `CAST(STRFTIME('%s', column) AS INTEGER)`. All dialects return `NULL` for dates before 1970-01-01 or `NULL` input, except PostgreSQL which can handle dates before the epoch (returning negative values).

### From Unix Epoch

Converts a Unix timestamp (integer or expression) to a datetime value.

| Dialect    | Method Name       | Generated SQL                       |
|------------|-------------------|-------------------------------------|
| PostgreSQL | `fromEpoch(value)` | `TO_TIMESTAMP(value)`              |
| MySQL      | `fromEpoch(value)` | `FROM_UNIXTIME(value)`             |
| MariaDB    | `fromEpoch(value)` | `FROM_UNIXTIME(value)`             |
| SQLite     | `fromEpoch(value)` | `DATETIME(value, 'unixepoch')`     |

Note: PostgreSQL's `TO_TIMESTAMP` returns `TIMESTAMP WITH TIME ZONE`. MySQL/MariaDB's `FROM_UNIXTIME` returns `DATETIME` in the session time zone. SQLite returns a `TEXT` string in UTC. To get a local-time result in SQLite, append `'localtime'`: `DATETIME(value, 'unixepoch', 'localtime')`.

---

## Date/Time Construction & Conversion

### Extract Date

Extracts the date portion from a timestamp column, discarding the time component.

| Dialect    | Method Name  | Generated SQL              |
|------------|--------------|----------------------------|
| PostgreSQL | `toDate()`   | `CAST(column AS DATE)`     |
| MySQL      | `toDate()`   | `DATE(column)`             |
| MariaDB    | `toDate()`   | `DATE(column)`             |
| SQLite     | `toDate()`   | `DATE(column)`             |

Note: PostgreSQL does not have a `DATE()` function; use `CAST(column AS DATE)` or the PostgreSQL-specific shorthand `column::date`. MySQL, MariaDB, and SQLite all support `DATE(column)` to extract the date portion.

### Extract Time

Extracts the time portion from a timestamp column, discarding the date component.

| Dialect    | Method Name  | Generated SQL              |
|------------|--------------|----------------------------|
| PostgreSQL | `toTime()`   | `CAST(column AS TIME)`     |
| MySQL      | `toTime()`   | `TIME(column)`             |
| MariaDB    | `toTime()`   | `TIME(column)`             |
| SQLite     | `toTime()`   | `TIME(column)`             |

Note: PostgreSQL does not have a `TIME()` function; use `CAST(column AS TIME)` or `column::time`.

### Last Day of Month

Returns the last day of the month for the given date or timestamp.

| Dialect    | Method Name  | Generated SQL                                                       |
|------------|--------------|---------------------------------------------------------------------|
| PostgreSQL | `lastDay()`  | `CAST(DATE_TRUNC('month', column) + INTERVAL '1 month - 1 day' AS DATE)` |
| MySQL      | `lastDay()`  | `LAST_DAY(column)`                                                  |
| MariaDB    | `lastDay()`  | `LAST_DAY(column)`                                                  |
| SQLite     | `lastDay()`  | `DATE(column, 'start of month', '+1 month', '-1 day')`             |

Note: MySQL and MariaDB have a dedicated `LAST_DAY()` function. PostgreSQL requires truncating to the start of the month, adding one month, and subtracting one day. SQLite's `DATE()` function accepts multiple modifier arguments that are applied sequentially: `'start of month'` resets to the 1st, `'+1 month'` advances to the next month's 1st, and `'-1 day'` steps back to the last day of the original month.

### Make Date

Constructs a date from individual year, month, and day components.

| Dialect    | Method Name               | Generated SQL                       |
|------------|---------------------------|-------------------------------------|
| PostgreSQL | `makeDate(y, m, d)`       | `MAKE_DATE(y, m, d)`               |
| MySQL      | `makeDate(y, m, d)`       | `STR_TO_DATE(CONCAT(y, '-', m, '-', d), '%Y-%m-%d')` |
| MariaDB    | `makeDate(y, m, d)`       | `STR_TO_DATE(CONCAT(y, '-', m, '-', d), '%Y-%m-%d')` |
| SQLite     | `makeDate(y, m, d)`       | `DATE(PRINTF('%04d-%02d-%02d', y, m, d))`             |

Note: Only PostgreSQL has a dedicated `MAKE_DATE` function (available since 9.4). MySQL and MariaDB construct a formatted string and parse it with `STR_TO_DATE`. SQLite uses `PRINTF` to format the components into an ISO 8601 date string. All dialects will produce invalid results or errors if the components do not form a valid date.

### Make Time

Constructs a time from individual hour, minute, and second components.

| Dialect    | Method Name               | Generated SQL                       |
|------------|---------------------------|-------------------------------------|
| PostgreSQL | `makeTime(h, m, s)`       | `MAKE_TIME(h, m, s)`               |
| MySQL      | `makeTime(h, m, s)`       | `MAKETIME(h, m, s)`                |
| MariaDB    | `makeTime(h, m, s)`       | `MAKETIME(h, m, s)`                |
| SQLite     | `makeTime(h, m, s)`       | `TIME(PRINTF('%02d:%02d:%02d', h, m, s))` |

Note: PostgreSQL's `MAKE_TIME` (9.4+) and MySQL/MariaDB's `MAKETIME` are dedicated functions. PostgreSQL's `MAKE_TIME` accepts a fractional seconds parameter (`double precision`), while MySQL/MariaDB's `MAKETIME` truncates to whole seconds. SQLite constructs a time string using `PRINTF` formatting.

---

## Key Differences & Limitations

### SQLite Date/Time Storage

SQLite has no dedicated date/time column types. Dates are stored as one of three representations:

| Storage Type | Format                          | Example                  |
|--------------|---------------------------------|--------------------------|
| `TEXT`       | ISO 8601 string                 | `'2024-03-15 14:30:00'`  |
| `REAL`       | Julian day number               | `2460384.104166667`       |
| `INTEGER`    | Unix timestamp (seconds)        | `1710513000`              |

All SQLite date/time functions accept any of these formats as input. When a column stores dates as `INTEGER` (Unix timestamps), pass through `DATETIME(column, 'unixepoch')` first to use with other date functions.

### Version Requirements

| Dialect    | Feature                  | Minimum Version                                       |
|------------|--------------------------|-------------------------------------------------------|
| PostgreSQL | `EXTRACT`, `DATE_TRUNC`  | 7.1+ (available since early versions)                 |
| PostgreSQL | `MAKE_DATE`, `MAKE_TIME` | 9.4 (2014)                                            |
| PostgreSQL | `EXTRACT` returns numeric | 14 (2021); earlier versions return `double precision` |
| MySQL      | `DATE_ADD`, `EXTRACT`    | 4.0+ (available since early versions)                 |
| MySQL      | `TIMESTAMPDIFF`          | 5.0 (2005)                                            |
| MariaDB    | All date/time functions  | 5.1+ (available since early versions)                 |
| SQLite     | Core date/time functions | 3.0+ (built-in since the beginning)                   |
| SQLite     | `UNIXEPOCH()`            | 3.38.0 (2022)                                         |

### Time Zone Handling

- **PostgreSQL**: Distinguishes between `TIMESTAMP` (no time zone) and `TIMESTAMPTZ` (with time zone). `CURRENT_TIMESTAMP` returns `TIMESTAMPTZ`. Use `AT TIME ZONE 'zone'` to convert between zones.
- **MySQL / MariaDB**: `DATETIME` and `TIMESTAMP` types exist. `TIMESTAMP` is stored as UTC and converted to/from the session time zone on read/write. `DATETIME` is stored as-is without zone conversion. Use `CONVERT_TZ(column, from_tz, to_tz)` for explicit conversion.
- **SQLite**: No time zone awareness. All date/time functions operate in UTC by default. Append the `'localtime'` modifier to convert to the system's local time zone: `DATETIME(column, 'localtime')`.

### Null Handling

All dialects return `NULL` if the input column value is `NULL` for all date/time functions. No special null handling is needed.

### Fractional Seconds

| Dialect    | Support                                         | Maximum Precision  |
|------------|-------------------------------------------------|--------------------|
| PostgreSQL | Native in `TIMESTAMP` and `TIME` types          | Microseconds (6)   |
| MySQL      | `DATETIME(fsp)` and `TIMESTAMP(fsp)` since 5.6  | Microseconds (6)   |
| MariaDB    | `DATETIME(fsp)` and `TIMESTAMP(fsp)` since 5.3  | Microseconds (6)   |
| SQLite     | Supported in `TEXT` storage and `STRFTIME('%f')` | Milliseconds (3)   |

Note: MySQL and MariaDB require explicitly specifying the fractional seconds precision (fsp) when creating the column: `DATETIME(3)` for milliseconds, `DATETIME(6)` for microseconds. Without `(fsp)`, fractional seconds are truncated silently.

### Month Arithmetic Edge Cases

Adding months to end-of-month dates produces different results across dialects:

| Expression                        | PostgreSQL   | MySQL / MariaDB | SQLite       |
|-----------------------------------|--------------|-----------------|--------------|
| `'2024-01-31' + 1 month`         | `2024-02-29`  | `2024-02-29`   | `2024-03-02`  |
| `'2024-03-31' + 1 month`         | `2024-04-30`  | `2024-04-30`   | `2024-05-01`  |
| `'2023-01-31' + 1 month`         | `2023-02-28`  | `2023-02-28`   | `2023-03-03`  |

PostgreSQL and MySQL/MariaDB clamp the day to the last valid day of the target month. SQLite simply increments the month number and lets the day overflow into the next month. This is a critical behavioral difference to be aware of when performing month arithmetic.
