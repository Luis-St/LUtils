# Common String Functions Across Dialects

String manipulation functions shared across PostgreSQL, MySQL, MariaDB, and SQLite.
Most functions follow SQL standard conventions with identical or near-identical SQL syntax.
Where differences exist, they are noted per dialect.

The base `SqlColumn<T>` interface already provides pattern matching (`like`, `startsWith`, `contains`, `endsWith`) and case-insensitive comparison (`equalToIgnoreCase`). The functions below cover string transformation, extraction, and inspection.

---

## Case Conversion

### Upper Case

Converts all characters in the string to upper case.

| Dialect    | Method Name | Generated SQL      |
|------------|-------------|--------------------|
| PostgreSQL | `upper()`   | `UPPER(column)`    |
| MySQL      | `upper()`   | `UPPER(column)`    |
| MariaDB    | `upper()`   | `UPPER(column)`    |
| SQLite     | `upper()`   | `UPPER(column)`    |

Note: SQLite's `UPPER` only handles ASCII characters by default. For full Unicode support, the ICU extension must be loaded.

### Lower Case

Converts all characters in the string to lower case.

| Dialect    | Method Name | Generated SQL      |
|------------|-------------|--------------------|
| PostgreSQL | `lower()`   | `LOWER(column)`    |
| MySQL      | `lower()`   | `LOWER(column)`    |
| MariaDB    | `lower()`   | `LOWER(column)`    |
| SQLite     | `lower()`   | `LOWER(column)`    |

Note: Same Unicode limitation as `UPPER` in SQLite.

---

## Trimming

### Trim

Removes leading and trailing whitespace from the string.

| Dialect    | Method Name | Generated SQL     |
|------------|-------------|-------------------|
| PostgreSQL | `trim()`    | `TRIM(column)`    |
| MySQL      | `trim()`    | `TRIM(column)`    |
| MariaDB    | `trim()`    | `TRIM(column)`    |
| SQLite     | `trim()`    | `TRIM(column)`    |

### Left Trim

Removes leading whitespace from the string.

| Dialect    | Method Name | Generated SQL      |
|------------|-------------|--------------------|
| PostgreSQL | `ltrim()`   | `LTRIM(column)`    |
| MySQL      | `ltrim()`   | `LTRIM(column)`    |
| MariaDB    | `ltrim()`   | `LTRIM(column)`    |
| SQLite     | `ltrim()`   | `LTRIM(column)`    |

### Right Trim

Removes trailing whitespace from the string.

| Dialect    | Method Name | Generated SQL      |
|------------|-------------|--------------------|
| PostgreSQL | `rtrim()`   | `RTRIM(column)`    |
| MySQL      | `rtrim()`   | `RTRIM(column)`    |
| MariaDB    | `rtrim()`   | `RTRIM(column)`    |
| SQLite     | `rtrim()`   | `RTRIM(column)`    |

### Trim Characters

Removes leading and trailing occurrences of specified characters from the string.

| Dialect    | Method Name          | Generated SQL                    |
|------------|----------------------|----------------------------------|
| PostgreSQL | `trimChars(chars)`   | `TRIM(BOTH 'chars' FROM column)` |
| MySQL      | `trimChars(chars)`   | `TRIM(BOTH 'chars' FROM column)` |
| MariaDB    | `trimChars(chars)`   | `TRIM(BOTH 'chars' FROM column)` |
| SQLite     | `trimChars(chars)`   | `TRIM(column, 'chars')`          |

Note: PostgreSQL, MySQL, and MariaDB use the SQL standard `TRIM(BOTH 'chars' FROM column)` syntax. SQLite uses a simpler two-argument form. The same pattern applies to `LTRIM(column, 'chars')` and `RTRIM(column, 'chars')` for directional trimming in SQLite, while the others use `TRIM(LEADING/TRAILING 'chars' FROM column)`.

---

## Length & Position

### Character Length

Returns the number of characters in the string.

| Dialect    | Method Name    | Generated SQL            |
|------------|----------------|--------------------------|
| PostgreSQL | `charLength()` | `CHAR_LENGTH(column)`    |
| MySQL      | `charLength()` | `CHAR_LENGTH(column)`    |
| MariaDB    | `charLength()` | `CHAR_LENGTH(column)`    |
| SQLite     | `charLength()` | `LENGTH(column)`         |

Note: All dialects also accept `CHARACTER_LENGTH(column)` except SQLite. In MySQL and MariaDB, `LENGTH(column)` returns the byte length (different from character length for multi-byte encodings), while in PostgreSQL and SQLite, `LENGTH(column)` returns the character length. Use `CHAR_LENGTH` for portable character counting.

### Find Substring Position

Returns the 1-based position of the first occurrence of the substring, or 0 if not found.

| Dialect    | Method Name          | Generated SQL                          |
|------------|----------------------|----------------------------------------|
| PostgreSQL | `position(substr)`   | `POSITION('substr' IN column)`         |
| MySQL      | `position(substr)`   | `LOCATE('substr', column)`             |
| MariaDB    | `position(substr)`   | `LOCATE('substr', column)`             |
| SQLite     | `position(substr)`   | `INSTR(column, 'substr')`              |

Note: PostgreSQL uses the SQL standard `POSITION(substr IN column)` syntax but also supports `STRPOS(column, substr)`. MySQL and MariaDB support both `LOCATE(substr, column)` and `INSTR(column, substr)`. SQLite only supports `INSTR(column, substr)`.

---

## Extraction

### Substring

Returns a substring starting at position `start` (1-based) with the given length.

| Dialect    | Method Name              | Generated SQL                     |
|------------|--------------------------|-----------------------------------|
| PostgreSQL | `substring(start, len)`  | `SUBSTR(column, start, len)`      |
| MySQL      | `substring(start, len)`  | `SUBSTR(column, start, len)`      |
| MariaDB    | `substring(start, len)`  | `SUBSTR(column, start, len)`      |
| SQLite     | `substring(start, len)`  | `SUBSTR(column, start, len)`      |

Note: All dialects accept the `SUBSTR(column, start, length)` form. PostgreSQL, MySQL, and MariaDB also accept the SQL standard form `SUBSTRING(column FROM start FOR length)`. A negative `start` counts from the end of the string in all dialects.

### Left

Returns the leftmost `n` characters of the string.

| Dialect    | Method Name | Generated SQL       |
|------------|-------------|---------------------|
| PostgreSQL | `left(n)`   | `LEFT(column, n)`   |
| MySQL      | `left(n)`   | `LEFT(column, n)`   |
| MariaDB    | `left(n)`   | `LEFT(column, n)`   |

Note: SQLite does not have a `LEFT` function. Use `SUBSTR(column, 1, n)` instead.

### Right

Returns the rightmost `n` characters of the string.

| Dialect    | Method Name | Generated SQL        |
|------------|-------------|----------------------|
| PostgreSQL | `right(n)`  | `RIGHT(column, n)`   |
| MySQL      | `right(n)`  | `RIGHT(column, n)`   |
| MariaDB    | `right(n)`  | `RIGHT(column, n)`   |

Note: SQLite does not have a `RIGHT` function. Use `SUBSTR(column, -n)` instead.

---

## Modification

### Replace

Replaces all occurrences of the search string with the replacement string.

| Dialect    | Method Name                      | Generated SQL                                |
|------------|----------------------------------|----------------------------------------------|
| PostgreSQL | `replace(search, replacement)`   | `REPLACE(column, 'search', 'replacement')`   |
| MySQL      | `replace(search, replacement)`   | `REPLACE(column, 'search', 'replacement')`   |
| MariaDB    | `replace(search, replacement)`   | `REPLACE(column, 'search', 'replacement')`   |
| SQLite     | `replace(search, replacement)`   | `REPLACE(column, 'search', 'replacement')`   |

### Concatenate

Concatenates the column value with one or more other values.

| Dialect    | Method Name         | Generated SQL                       |
|------------|---------------------|-------------------------------------|
| PostgreSQL | `concat(values...)` | `column \|\| value1 \|\| value2`    |
| MySQL      | `concat(values...)` | `CONCAT(column, value1, value2)`    |
| MariaDB    | `concat(values...)` | `CONCAT(column, value1, value2)`    |
| SQLite     | `concat(values...)` | `column \|\| value1 \|\| value2`    |

Note: PostgreSQL and SQLite use the `||` operator for concatenation. MySQL and MariaDB use the `CONCAT()` function. An important difference: the `||` operator returns `NULL` if any operand is `NULL`, while MySQL/MariaDB's `CONCAT()` also returns `NULL` if any argument is `NULL`. All dialects support `CONCAT_WS(separator, val1, val2, ...)` for separator-based concatenation except SQLite.

### Pad Left

Pads the string on the left with the fill character to reach the specified length.

| Dialect    | Method Name         | Generated SQL                    |
|------------|---------------------|----------------------------------|
| PostgreSQL | `lpad(length, fill)` | `LPAD(column, length, 'fill')`  |
| MySQL      | `lpad(length, fill)` | `LPAD(column, length, 'fill')`  |
| MariaDB    | `lpad(length, fill)` | `LPAD(column, length, 'fill')`  |

Note: SQLite does not have a `LPAD` function. A `PRINTF`-based workaround is possible for zero-padding: `PRINTF('%0Nd', column)`.

### Pad Right

Pads the string on the right with the fill character to reach the specified length.

| Dialect    | Method Name          | Generated SQL                    |
|------------|----------------------|----------------------------------|
| PostgreSQL | `rpad(length, fill)` | `RPAD(column, length, 'fill')`   |
| MySQL      | `rpad(length, fill)` | `RPAD(column, length, 'fill')`   |
| MariaDB    | `rpad(length, fill)` | `RPAD(column, length, 'fill')`   |

Note: SQLite does not have an `RPAD` function.

### Repeat

Repeats the string the specified number of times.

| Dialect    | Method Name  | Generated SQL          |
|------------|--------------|------------------------|
| PostgreSQL | `repeat(n)`  | `REPEAT(column, n)`    |
| MySQL      | `repeat(n)`  | `REPEAT(column, n)`    |
| MariaDB    | `repeat(n)`  | `REPEAT(column, n)`    |

Note: SQLite does not have a `REPEAT` function. A workaround using `REPLACE(ZEROBLOB(n), X'00', column)` is possible but not recommended for general use.

### Reverse

Reverses the characters in the string.

| Dialect    | Method Name  | Generated SQL         |
|------------|--------------|-----------------------|
| PostgreSQL | `reverse()`  | `REVERSE(column)`     |
| MySQL      | `reverse()`  | `REVERSE(column)`     |
| MariaDB    | `reverse()`  | `REVERSE(column)`     |

Note: SQLite does not have a built-in `REVERSE` function.

---

## Character Functions

### Character Code

Returns the numeric code of the first character.

| Dialect    | Method Name    | Generated SQL        |
|------------|----------------|----------------------|
| PostgreSQL | `ascii()`      | `ASCII(column)`      |
| MySQL      | `ascii()`      | `ASCII(column)`      |
| MariaDB    | `ascii()`      | `ASCII(column)`      |
| SQLite     | `unicode()`    | `UNICODE(column)`    |

Note: PostgreSQL, MySQL, and MariaDB use `ASCII()` which returns the ASCII code of the first character (or the Unicode code point for multi-byte characters in PostgreSQL). SQLite uses `UNICODE()` which returns the Unicode code point of the first character.

### Character From Code

Returns a character from one or more Unicode code points.

| Dialect    | Method Name              | Generated SQL                 |
|------------|--------------------------|-------------------------------|
| PostgreSQL | `chr(codePoint)`         | `CHR(codePoint)`              |
| MySQL      | `charFunc(codePoints..)` | `CHAR(cp1, cp2, ...)`        |
| MariaDB    | `charFunc(codePoints..)` | `CHAR(cp1, cp2, ...)`        |
| SQLite     | `charFunc(codePoints..)` | `CHAR(cp1, cp2, ...)`        |

Note: PostgreSQL uses `CHR()` which takes a single code point. MySQL, MariaDB, and SQLite use `CHAR()` which accepts multiple code points and returns the concatenated characters. MySQL and MariaDB also support `CHAR(... USING charset)` for character set conversion.

---

## Encoding

### Hexadecimal Encode

Returns the hexadecimal representation of the column value.

| Dialect    | Method Name | Generated SQL               |
|------------|-------------|-----------------------------|
| PostgreSQL | `hex()`     | `ENCODE(column, 'hex')`     |
| MySQL      | `hex()`     | `HEX(column)`               |
| MariaDB    | `hex()`     | `HEX(column)`               |
| SQLite     | `hex()`     | `HEX(column)`               |

Note: PostgreSQL uses the `ENCODE` function with `'hex'` format. MySQL, MariaDB, and SQLite use the `HEX()` function directly. For string inputs, MySQL/MariaDB return the hex encoding of each byte; SQLite returns the hex encoding of the UTF-8 representation.

### Hexadecimal Decode

Converts a hexadecimal string back to its original representation.

| Dialect    | Method Name | Generated SQL               |
|------------|-------------|-----------------------------|
| PostgreSQL | `unhex()`   | `DECODE(column, 'hex')`     |
| MySQL      | `unhex()`   | `UNHEX(column)`             |
| MariaDB    | `unhex()`   | `UNHEX(column)`             |
| SQLite     | `unhex()`   | `UNHEX(column)`             |

Note: PostgreSQL uses `DECODE` with `'hex'` format. SQLite's `UNHEX` is available since version 3.38.0.

---

## String Aggregation

### Group Concat

Concatenates values from grouped rows into a single string with the specified separator.

| Dialect    | Method Name                | Generated SQL                                        |
|------------|----------------------------|------------------------------------------------------|
| PostgreSQL | `groupConcat(separator)`   | `STRING_AGG(column, 'separator')`                    |
| MySQL      | `groupConcat(separator)`   | `GROUP_CONCAT(column SEPARATOR 'separator')`         |
| MariaDB    | `groupConcat(separator)`   | `GROUP_CONCAT(column SEPARATOR 'separator')`         |
| SQLite     | `groupConcat(separator)`   | `GROUP_CONCAT(column, 'separator')`                  |

Note: PostgreSQL uses the `STRING_AGG` function. MySQL and MariaDB use `GROUP_CONCAT` with a `SEPARATOR` clause. SQLite uses `GROUP_CONCAT` with the separator as a second argument. Without explicit ordering, the concatenation order is non-deterministic across all dialects.

### Group Concat Distinct

Concatenates only distinct values from grouped rows into a single string with the specified separator.

| Dialect    | Method Name                        | Generated SQL                                                |
|------------|------------------------------------|--------------------------------------------------------------|
| PostgreSQL | `groupConcatDistinct(separator)`   | `STRING_AGG(DISTINCT column, 'separator')`                   |
| MySQL      | `groupConcatDistinct(separator)`   | `GROUP_CONCAT(DISTINCT column SEPARATOR 'separator')`        |
| MariaDB    | `groupConcatDistinct(separator)`   | `GROUP_CONCAT(DISTINCT column SEPARATOR 'separator')`        |
| SQLite     | `groupConcatDistinct(separator)`   | `GROUP_CONCAT(DISTINCT column, 'separator')`                 |

### Group Concat Ordered

Concatenates values from grouped rows with explicit ordering of the aggregated elements.

| Dialect    | Method Name                               | Generated SQL                                                     |
|------------|-------------------------------------------|-------------------------------------------------------------------|
| PostgreSQL | `groupConcatOrdered(separator, orderBy)`  | `STRING_AGG(column, 'separator' ORDER BY orderBy)`                |
| MySQL      | `groupConcatOrdered(separator, orderBy)`  | `GROUP_CONCAT(column ORDER BY orderBy SEPARATOR 'separator')`     |
| MariaDB    | `groupConcatOrdered(separator, orderBy)`  | `GROUP_CONCAT(column ORDER BY orderBy SEPARATOR 'separator')`     |
| SQLite     | —                                         | Not available (`GROUP_CONCAT` has no `ORDER BY` support)          |

Note: Without explicit ordering, the concatenation order depends on the internal row processing order, which is non-deterministic. SQLite does not support ordering within `GROUP_CONCAT`; use a subquery with `ORDER BY` as a workaround.

---

## Key Differences & Limitations

### SQLite Limitations

SQLite is missing several string functions that the other three dialects share:

| Function   | Workaround in SQLite                               |
|------------|-----------------------------------------------------|
| `LEFT`     | `SUBSTR(column, 1, n)`                              |
| `RIGHT`    | `SUBSTR(column, -n)`                                |
| `LPAD`     | `PRINTF('%0Nd', column)` (zero-padding only)         |
| `RPAD`     | No simple workaround                                |
| `REPEAT`   | `REPLACE(ZEROBLOB(n), X'00', column)` (hacky)       |
| `REVERSE`  | No built-in solution                                |

### Unicode Support

- **PostgreSQL**: Full Unicode support for all string functions.
- **MySQL / MariaDB**: Full Unicode support. `LENGTH()` returns byte length; use `CHAR_LENGTH()` for character count.
- **SQLite**: `UPPER()` and `LOWER()` only handle ASCII by default. Loading the ICU extension enables full Unicode case conversion.

### Null Handling

All string functions across all dialects return `NULL` if the column value is `NULL`. For concatenation, `NULL` propagates: any `NULL` operand causes the entire result to be `NULL` (use `COALESCE` to substitute defaults).

### Collation Sensitivity

- **PostgreSQL**: String comparisons and sorting respect the column's collation. `POSITION` and `REPLACE` are always case-sensitive.
- **MySQL / MariaDB**: Case sensitivity depends on the column's collation (e.g., `utf8mb4_general_ci` is case-insensitive, `utf8mb4_bin` is case-sensitive).
- **SQLite**: By default uses `BINARY` collation (case-sensitive). `NOCASE` collation is available for case-insensitive comparisons but only covers ASCII.
