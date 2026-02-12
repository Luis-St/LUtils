# Common Array Operations Across Dialects

Operations that provide array functionality across all supported dialects.
PostgreSQL uses native array types (`type[]`) and built-in array functions.
MySQL, MariaDB, and SQLite do not have native array column types; they store arrays as JSON arrays in `JSON` / `TEXT` columns and use JSON functions as workarounds.

The unified `SqlArrayOps` interface exposes these methods with a single API. The generated SQL differs per dialect.

---

## Storage

| Dialect    | Column Type               | Array Literal Example                        |
|------------|---------------------------|----------------------------------------------|
| PostgreSQL | `integer[]`, `text[]`, …  | `ARRAY[1, 2, 3]` or `'{1,2,3}'`             |
| MySQL      | `JSON`                    | `JSON_ARRAY(1, 2, 3)` or `'[1, 2, 3]'`      |
| MariaDB    | `JSON` (alias `LONGTEXT`) | `JSON_ARRAY(1, 2, 3)` or `'[1, 2, 3]'`      |
| SQLite     | `TEXT`                    | `json_array(1, 2, 3)` or `'[1, 2, 3]'`      |

Note: PostgreSQL arrays are 1-based. JSON arrays are 0-based internally, but the API normalizes indices to 0-based for consistency with java arrays.

---

## Mutation

### Append Element

Appends an element to the end of the array.

| Dialect    | Method Name         | Generated SQL                                     |
|------------|---------------------|----------------------------------------------------|
| PostgreSQL | `append(element)`   | `array_append(column, element)`                    |
| MySQL      | `append(element)`   | `JSON_ARRAY_APPEND(column, '$', element)`          |
| MariaDB    | `append(element)`   | `JSON_ARRAY_APPEND(column, '$', element)`          |
| SQLite     | `append(element)`   | `json_insert(column, '$[#]', element)`             |

Note: SQLite's `$[#]` path (available since 3.38.0) refers to one past the last element, making it equivalent to an append.

### Prepend Element

Prepends an element to the beginning of the array.

| Dialect    | Method Name          | Generated SQL                                                                                         |
|------------|----------------------|--------------------------------------------------------------------------------------------------------|
| PostgreSQL | `prepend(element)`   | `array_prepend(element, column)`                                                                       |
| MySQL      | `prepend(element)`   | `JSON_ARRAY_INSERT(column, '$[0]', element)`                                                           |
| MariaDB    | `prepend(element)`   | `JSON_ARRAY_INSERT(column, '$[0]', element)`                                                           |
| SQLite     | `prepend(element)`   | `(SELECT json_group_array(v) FROM (SELECT element AS v UNION ALL SELECT je.value FROM json_each(column) je))` |

Note: MySQL and MariaDB's `JSON_ARRAY_INSERT` at `$[0]` shifts existing elements to the right. SQLite has no equivalent single-function call, so a subquery that rebuilds the array is required.

### Remove Element

Removes all occurrences of the specified element from the array.

| Dialect    | Method Name         | Generated SQL                                                                                                       |
|------------|---------------------|----------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `remove(element)`   | `array_remove(column, element)`                                                                                      |
| MySQL      | `remove(element)`   | `(SELECT JSON_ARRAYAGG(jt.val) FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) AS jt WHERE jt.val != CAST(element AS JSON))` |
| MariaDB    | `remove(element)`   | `(SELECT JSON_ARRAYAGG(jt.val) FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) AS jt WHERE jt.val != CAST(element AS JSON))` |
| SQLite     | `remove(element)`   | `(SELECT json_group_array(je.value) FROM json_each(column) je WHERE je.value != element)`                            |

Note: The JSON workarounds expand the array into rows, filter out matching elements, and re-aggregate into an array. This removes *all* occurrences, matching PostgreSQL's `array_remove` semantics.

### Replace Element

Replaces all occurrences of `oldElement` with `newElement`.

| Dialect    | Method Name                       | Generated SQL                                                                                                                               |
|------------|-----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `replace(oldElement, newElement)`  | `array_replace(column, oldElement, newElement)`                                                                                              |
| MySQL      | `replace(oldElement, newElement)`  | `(SELECT JSON_ARRAYAGG(CASE WHEN jt.val = CAST(oldElement AS JSON) THEN newElement ELSE jt.val END) FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) AS jt)` |
| MariaDB    | `replace(oldElement, newElement)`  | `(SELECT JSON_ARRAYAGG(CASE WHEN jt.val = CAST(oldElement AS JSON) THEN newElement ELSE jt.val END) FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) AS jt)` |
| SQLite     | `replace(oldElement, newElement)`  | `(SELECT json_group_array(CASE WHEN je.value = oldElement THEN newElement ELSE je.value END) FROM json_each(column) je)`                     |

### Concatenate Arrays

Concatenates two arrays into one.

| Dialect    | Method Name     | Generated SQL                                                                                                                               |
|------------|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `cat(other)`    | `array_cat(column, other)`                                                                                                                   |
| MySQL      | `cat(other)`    | `JSON_MERGE_PRESERVE(column, other)`                                                                                                         |
| MariaDB    | `cat(other)`    | `JSON_MERGE_PRESERVE(column, other)`                                                                                                         |
| SQLite     | `cat(other)`    | `(SELECT json_group_array(j.value) FROM (SELECT je.value FROM json_each(column) je UNION ALL SELECT je2.value FROM json_each(other) je2) j)` |

Note: MySQL and MariaDB's `JSON_MERGE_PRESERVE` concatenates JSON arrays (unlike `JSON_MERGE_PATCH` which replaces). SQLite requires expanding both arrays and re-aggregating.

---

## Inspection

### Array Length

Returns the number of elements in the array.

| Dialect    | Method Name   | Generated SQL                    |
|------------|---------------|----------------------------------|
| PostgreSQL | `length()`    | `cardinality(column)`            |
| MySQL      | `length()`    | `JSON_LENGTH(column)`            |
| MariaDB    | `length()`    | `JSON_LENGTH(column)`            |
| SQLite     | `length()`    | `json_array_length(column)`      |

Note: PostgreSQL's `cardinality()` returns the total number of elements across all dimensions. For one-dimensional arrays, `array_length(column, 1)` is equivalent. MySQL and MariaDB's `JSON_LENGTH` also works on objects (returning the number of keys), so the column must contain a JSON array.

### Find Element Position

Returns the 1-based index of the first occurrence of the element, or null if not found.

| Dialect    | Method Name           | Generated SQL                                                                                                                                    |
|------------|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `position(element)`   | `array_position(column, element)`                                                                                                                 |
| MySQL      | `position(element)`   | `(SELECT jt.ord FROM JSON_TABLE(column, '$[*]' COLUMNS(ord FOR ORDINALITY, val JSON PATH '$')) AS jt WHERE jt.val = CAST(element AS JSON) LIMIT 1)` |
| MariaDB    | `position(element)`   | `(SELECT jt.ord FROM JSON_TABLE(column, '$[*]' COLUMNS(ord FOR ORDINALITY, val JSON PATH '$')) AS jt WHERE jt.val = CAST(element AS JSON) LIMIT 1)` |
| SQLite     | `position(element)`   | `(SELECT je.key + 1 FROM json_each(column) je WHERE je.value = element LIMIT 1)`                                                                 |

Note: `JSON_TABLE` with `FOR ORDINALITY` produces a 1-based row number. SQLite's `json_each` returns a 0-based `key` for array elements, so `+1` normalizes to 1-based indexing.

### Contains Element

Checks if the array contains the given element.

| Dialect    | Method Name           | Generated SQL                                                                            |
|------------|-----------------------|------------------------------------------------------------------------------------------|
| PostgreSQL | `contains(element)`   | `element = ANY(column)`                                                                  |
| MySQL      | `contains(element)`   | `JSON_CONTAINS(column, CAST(element AS JSON))`                                           |
| MariaDB    | `contains(element)`   | `JSON_CONTAINS(column, CAST(element AS JSON))`                                           |
| SQLite     | `contains(element)`   | `EXISTS(SELECT 1 FROM json_each(column) je WHERE je.value = element)`                    |

Note: MySQL and MariaDB's `JSON_CONTAINS` checks whether a candidate value is found within the target JSON document. For string elements, the value must be properly JSON-quoted (e.g. `'"text"'`).

### Contains All Elements

Checks if the array contains all of the given elements (array superset).

| Dialect    | Method Name              | Generated SQL                                                                                                                                                                                    |
|------------|--------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `containsAll(elements)`  | `column @> ARRAY[e1, e2, ...]`                                                                                                                                                                   |
| MySQL      | `containsAll(elements)`  | `JSON_CONTAINS(column, JSON_ARRAY(e1, e2, ...))`                                                                                                                                                 |
| MariaDB    | `containsAll(elements)`  | `JSON_CONTAINS(column, JSON_ARRAY(e1, e2, ...))`                                                                                                                                                 |
| SQLite     | `containsAll(elements)`  | `(SELECT COUNT(DISTINCT t.value) FROM json_each(json_array(e1, e2, ...)) t WHERE t.value IN (SELECT je.value FROM json_each(column) je)) = N`                                                    |

Note: MySQL and MariaDB's `JSON_CONTAINS(target, candidate)` returns true when all elements of the candidate are present in the target, which directly implements "contains all" semantics. `N` in the SQLite variant is the number of elements to check.

### Array Overlaps

Checks if two arrays share at least one common element.

| Dialect    | Method Name               | Generated SQL                                                                                                                                      |
|------------|---------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `overlaps(elements)`      | `column && ARRAY[e1, e2, ...]`                                                                                                                     |
| MySQL      | `overlaps(elements)`      | `JSON_OVERLAPS(column, JSON_ARRAY(e1, e2, ...))`                                                                                                   |
| MariaDB    | `overlaps(elements)`      | `EXISTS(SELECT 1 FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) jt WHERE jt.val IN (CAST(e1 AS JSON), CAST(e2 AS JSON), ...))`          |
| SQLite     | `overlaps(elements)`      | `EXISTS(SELECT 1 FROM json_each(column) je WHERE je.value IN (e1, e2, ...))`                                                                       |

Note: MySQL's `JSON_OVERLAPS` is available since 8.0.17. MariaDB does not have `JSON_OVERLAPS`, so a `JSON_TABLE`-based check is used instead.

### Is Contained By

Checks if the array is a subset of the given elements.

| Dialect    | Method Name                  | Generated SQL                                                                                                                                                                                  |
|------------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `isContainedBy(elements)`    | `column <@ ARRAY[e1, e2, ...]`                                                                                                                                                                 |
| MySQL      | `isContainedBy(elements)`    | `JSON_CONTAINS(JSON_ARRAY(e1, e2, ...), column)`                                                                                                                                               |
| MariaDB    | `isContainedBy(elements)`    | `JSON_CONTAINS(JSON_ARRAY(e1, e2, ...), column)`                                                                                                                                               |
| SQLite     | `isContainedBy(elements)`    | `NOT EXISTS(SELECT 1 FROM json_each(column) je WHERE je.value NOT IN (e1, e2, ...))`                                                                                                           |

Note: This is the inverse of `containsAll`. MySQL and MariaDB achieve this by swapping the arguments of `JSON_CONTAINS`. SQLite checks that no element in the column array exists outside the given set.

---

## Transformation

### Remove Duplicates

Returns the array with duplicate elements removed. Element order may not be preserved.

| Dialect    | Method Name    | Generated SQL                                                                                                                                 |
|------------|----------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `distinct()`   | `ARRAY(SELECT DISTINCT unnest(column))`                                                                                                       |
| MySQL      | `distinct()`   | `(SELECT JSON_ARRAYAGG(d.val) FROM (SELECT DISTINCT jt.val FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) jt) d)`                 |
| MariaDB    | `distinct()`   | `(SELECT JSON_ARRAYAGG(d.val) FROM (SELECT DISTINCT jt.val FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) jt) d)`                 |
| SQLite     | `distinct()`   | `(SELECT json_group_array(d.value) FROM (SELECT DISTINCT je.value FROM json_each(column) je) d)`                                              |

### Sort

Returns the array with elements sorted in ascending order.

| Dialect    | Method Name | Generated SQL                                                                                                                                    |
|------------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `sort()`    | `ARRAY(SELECT unnest(column) ORDER BY 1)`                                                                                                        |
| MySQL      | `sort()`    | `(SELECT JSON_ARRAYAGG(s.val) FROM (SELECT jt.val FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) jt ORDER BY jt.val) s)`              |
| MariaDB    | `sort()`    | `(SELECT JSON_ARRAYAGG(s.val) FROM (SELECT jt.val FROM JSON_TABLE(column, '$[*]' COLUMNS(val JSON PATH '$')) jt ORDER BY jt.val) s)`              |
| SQLite     | `sort()`    | `(SELECT json_group_array(s.value) FROM (SELECT je.value FROM json_each(column) je ORDER BY je.value) s)`                                         |

Note: The subquery-based approach wraps the ordered result in an outer aggregation to guarantee element order is preserved in the final array.

### Array to String

Converts the array elements into a single string separated by the given delimiter.

| Dialect    | Method Name              | Generated SQL                                                                                                                               |
|------------|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL | `toString(delimiter)`    | `array_to_string(column, 'delimiter')`                                                                                                      |
| MySQL      | `toString(delimiter)`    | `(SELECT GROUP_CONCAT(jt.val ORDER BY jt.ord SEPARATOR 'delimiter') FROM JSON_TABLE(column, '$[*]' COLUMNS(ord FOR ORDINALITY, val VARCHAR(512) PATH '$')) jt)` |
| MariaDB    | `toString(delimiter)`    | `(SELECT GROUP_CONCAT(jt.val ORDER BY jt.ord SEPARATOR 'delimiter') FROM JSON_TABLE(column, '$[*]' COLUMNS(ord FOR ORDINALITY, val VARCHAR(512) PATH '$')) jt)` |
| SQLite     | `toString(delimiter)`    | `(SELECT GROUP_CONCAT(je.value, 'delimiter') FROM json_each(column) je)`                                                                    |

Note: PostgreSQL's `array_to_string` has an optional third argument for null replacement. The JSON workarounds use `GROUP_CONCAT` with an explicit `ORDER BY` to preserve element order.

---

## Key Differences & Limitations

### Tooling per Dialect

The JSON workarounds rely on two core mechanisms per dialect:

| Purpose                        | MySQL / MariaDB                          | SQLite                     |
|--------------------------------|------------------------------------------|----------------------------|
| Expand array into rows         | `JSON_TABLE(col, '$[*]' COLUMNS(...))`   | `json_each(col)`           |
| Re-aggregate rows into array   | `JSON_ARRAYAGG(expr)`                    | `json_group_array(expr)`   |

These two primitives (expand + re-aggregate) form the basis for most workaround operations (remove, replace, distinct, sort, slice).

### Type Handling

- **PostgreSQL**: Arrays are strongly typed. An `integer[]` column rejects non-integer elements.
- **MySQL / MariaDB / SQLite**: JSON arrays are heterogeneous. Any JSON value type can be mixed within a single array. Application-level validation is recommended.

### Performance

- **PostgreSQL**: Native array operations are index-friendly (GIN indexes on array columns support `@>`, `&&`, and `=` operators).
- **MySQL / MariaDB**: `JSON_TABLE` materializes a derived table per call. For large arrays or frequent mutations, consider a normalized join table.
- **SQLite**: `json_each` is a virtual table. Performance is acceptable for small to moderate arrays but degrades with very large arrays.

### Null Handling

- **PostgreSQL**: Arrays can contain SQL `NULL` elements. `array_remove(column, NULL)` removes all nulls.
- **JSON dialects**: JSON arrays can contain `null` (JSON null). Comparisons with SQL `NULL` vs JSON `null` require care (use `json_type()` checks or `IS NULL` conditions as appropriate).
