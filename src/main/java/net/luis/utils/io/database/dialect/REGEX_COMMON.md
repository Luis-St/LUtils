# Common Regular Expression Operations Across Dialects

Regular expression operations shared across PostgreSQL, MySQL (8.0+), MariaDB (10.0.5+), and SQLite (via extension).
Each dialect supports regex matching and most support regex replacement, though the underlying SQL syntax and regex flavor differ.

---

## Matching

### Regex Match

Tests whether the column value matches a regular expression pattern. Returns a condition (boolean).

| Dialect    | Method Name              | Generated SQL                          |
|------------|--------------------------|----------------------------------------|
| PostgreSQL | `posixRegex(pattern)`    | `column ~ 'pattern'`                  |
| MySQL      | `regexpLike(pattern)`    | `REGEXP_LIKE(column, 'pattern')`      |
| MariaDB    | `regexpLike(pattern)`    | `REGEXP_LIKE(column, 'pattern')`      |
| SQLite     | `regexp(pattern)`        | `column REGEXP 'pattern'`             |

Note: PostgreSQL uses the POSIX `~` operator (case-sensitive). MySQL and MariaDB use the `REGEXP_LIKE` function (case-insensitive by default in MySQL, depends on collation in MariaDB). SQLite's `REGEXP` operator requires a user-defined function to be loaded (not built-in).

### Negated Regex Match

Tests whether the column value does not match a regular expression pattern.

| Dialect    | Method Name                 | Generated SQL                              |
|------------|-----------------------------|--------------------------------------------|
| PostgreSQL | `notPosixRegex(pattern)`    | `column !~ 'pattern'`                     |
| MySQL      | `notRegexpLike(pattern)`    | `NOT REGEXP_LIKE(column, 'pattern')`      |
| MariaDB    | `notRegexpLike(pattern)`    | `NOT REGEXP_LIKE(column, 'pattern')`      |
| SQLite     | —                           | Not available as a dedicated method        |

Note: PostgreSQL has a dedicated `!~` operator for negation. MySQL and MariaDB negate the `REGEXP_LIKE` function with `NOT`. SQLite has no built-in negated regex operator.

---

## Replacement

### Regex Replace All

Replaces all substrings matching the regular expression with the replacement string.

| Dialect    | Method Name                                 | Generated SQL                                              |
|------------|---------------------------------------------|------------------------------------------------------------|
| PostgreSQL | `regexpReplaceAll(pattern, replacement)`     | `regexp_replace(column, 'pattern', 'replacement', 'g')`   |
| MySQL      | `regexpReplace(pattern, replacement)`        | `REGEXP_REPLACE(column, 'pattern', 'replacement')`        |
| MariaDB    | `regexpReplace(pattern, replacement)`        | `REGEXP_REPLACE(column, 'pattern', 'replacement')`        |

Note: PostgreSQL requires the `'g'` (global) flag to replace all matches. MySQL and MariaDB replace all matches by default.

---

## Regex Flavor Differences

Each dialect uses a different regular expression engine, which affects pattern syntax and behavior:

| Feature                    | PostgreSQL (POSIX)          | MySQL / MariaDB (ICU)        | SQLite (user-defined)        |
|----------------------------|-----------------------------|------------------------------|------------------------------|
| Engine                     | POSIX Extended              | ICU (8.0+) / Henry Spencer   | Depends on loaded extension  |
| Case sensitivity (default) | Case-sensitive              | Depends on collation          | Depends on implementation    |
| Backreferences             | `\1`, `\2`, ...            | `$1`, `$2`, ... (ICU)        | Depends on implementation    |
| Lookahead / lookbehind     | No                          | No                            | Depends on implementation    |
| Character classes           | `[:alpha:]`, `[:digit:]`   | `[:alpha:]`, `[:digit:]`     | Depends on implementation    |
| Unicode support            | Yes                         | Yes (ICU)                     | Depends on implementation    |

### Case-Insensitive Matching

| Dialect    | Method / Approach                                  | Generated SQL                            |
|------------|----------------------------------------------------|------------------------------------------|
| PostgreSQL | `posixRegexInsensitive(pattern)`                   | `column ~* 'pattern'`                   |
| MySQL      | `regexpLike(pattern)` with case-insensitive collation | `REGEXP_LIKE(column, 'pattern', 'i')` |
| MariaDB    | `regexpLike(pattern)` with case-insensitive collation | `REGEXP_LIKE(column, 'pattern', 'i')` |
| SQLite     | —                                                  | Not available as a dedicated method       |

Note: PostgreSQL has a dedicated `~*` operator for case-insensitive matching. MySQL and MariaDB support an optional match-type parameter (`'i'` for case-insensitive, `'c'` for case-sensitive). SQLite depends on the loaded regex extension.

---

## Key Differences & Limitations

### Available Operations per Dialect

| Operation              | PostgreSQL | MySQL | MariaDB | SQLite |
|------------------------|-----------|-------|---------|--------|
| Regex match            | `~`       | Yes   | Yes     | Yes    |
| Negated regex match    | `!~`      | Yes   | Yes     | No     |
| Case-insensitive match | `~*`      | Yes   | Yes     | No     |
| Regex replace (first)  | Yes       | No    | No      | No     |
| Regex replace (all)    | Yes       | Yes   | Yes     | No     |
| Regex substring        | Yes       | Yes   | Yes     | No     |
| Regex position         | No        | Yes   | Yes     | No     |

### SQLite Limitations

SQLite's `REGEXP` operator is not built-in. It requires a user-defined function (e.g., via `sqlite3_create_function`). Many SQLite distributions and wrappers include a PCRE-based implementation, but availability is not guaranteed. The `GLOB` operator (documented in `SQLITE_EXTENSIONS.md`) is a built-in alternative for simpler pattern matching.

### Version Requirements

| Dialect    | Minimum Version   | Notes                                                            |
|------------|-------------------|------------------------------------------------------------------|
| PostgreSQL | 7.4 (2003)        | POSIX regex has been available since early versions               |
| MySQL      | 8.0 (2018)        | `REGEXP_LIKE`, `REGEXP_REPLACE`, `REGEXP_INSTR`, `REGEXP_SUBSTR` |
| MariaDB    | 10.0.5 (2014)     | PCRE-based regex; `REGEXP_REPLACE` since 10.0.11                 |
| SQLite     | Any               | Requires user-defined `REGEXP` function                          |
