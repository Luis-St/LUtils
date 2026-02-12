# Common JSON Operations Across Dialects

Operations that share the same functionality in 3 or more dialects.
The base `SqlJsonOps` interface (`get`, `hasKey`, `getAsText`) is inherited by all dialects and not listed here.

---

## Mutation

### Set Value at Path

Creates the key if it does not exist, replaces the value if it does.

| Dialect    | Method Name              | Generated SQL                                  |
|------------|--------------------------|------------------------------------------------|
| PostgreSQL | `jsonbSet(path, value)`  | `jsonb_set(column, '{path}', 'value')`         |
| MySQL      | `jsonSet(path, value)`   | `JSON_SET(column, 'path', 'value')`            |
| MariaDB    | `jsonSet(path, value)`   | `JSON_SET(column, 'path', 'value')`            |
| SQLite     | `jsonSet(path, value)`   | `json_set(column, 'path', 'value')`            |

### Insert Value at Path

Inserts a value at the specified path only if the path does not already exist. Does not replace existing values.

| Dialect    | Method Name                | Generated SQL                                  |
|------------|----------------------------|------------------------------------------------|
| PostgreSQL | `jsonbInsert(path, value)` | `jsonb_insert(column, '{path}', 'value')`      |
| MySQL      | `jsonInsert(path, value)`  | `JSON_INSERT(column, 'path', 'value')`         |
| MariaDB    | `jsonInsert(path, value)`  | `JSON_INSERT(column, 'path', 'value')`         |
| SQLite     | `jsonInsert(path, value)`  | `json_insert(column, 'path', 'value')`         |

### Replace Value at Path

Replaces the value at the specified path only if the path already exists. Does nothing if the path is absent.

| Dialect    | Method Name                  | Generated SQL                                  |
|------------|------------------------------|------------------------------------------------|
| MySQL      | `jsonReplace(path, value)`   | `JSON_REPLACE(column, 'path', 'value')`        |
| MariaDB    | `jsonReplace(path, value)`   | `JSON_REPLACE(column, 'path', 'value')`        |
| SQLite     | `jsonReplace(path, value)`   | `json_replace(column, 'path', 'value')`        |

### Remove Value at Path

Removes the value at the specified path from the JSON document.

| Dialect    | Method Name              | Generated SQL                                  |
|------------|--------------------------|------------------------------------------------|
| PostgreSQL | `jsonbDeletePath(path)`  | `column #- '{path}'`                           |
| MySQL      | `jsonRemove(path)`       | `JSON_REMOVE(column, 'path')`                  |
| MariaDB    | `jsonRemove(path)`       | `JSON_REMOVE(column, 'path')`                  |
| SQLite     | `jsonRemove(path)`       | `json_remove(column, 'path')`                  |

### Merge / Patch JSON

Merges a JSON document into the column value. For objects, keys in the patch take precedence over existing keys.

| Dialect    | Method Name              | Generated SQL                                  |
|------------|--------------------------|------------------------------------------------|
| PostgreSQL | `jsonbConcat(json)`      | `column \|\| 'json'::jsonb`                    |
| MySQL      | `jsonMergePatch(json)`   | `JSON_MERGE_PATCH(column, 'json')`             |
| MariaDB    | `jsonMergePatch(json)`   | `JSON_MERGE_PATCH(column, 'json')`             |
| SQLite     | `jsonPatch(patch)`       | `json_patch(column, 'patch')`                  |

Note: MySQL, MariaDB, and SQLite follow RFC 7396 merge-patch semantics. PostgreSQL's `||` concatenation behaves similarly for objects but does not remove keys when the patch value is `null`.

---

## Introspection

### Get JSON Value Type

Returns the type of the top-level JSON value as a string (e.g. `object`, `array`, `string`, `number`, `boolean`, `null`).

| Dialect    | Method Name       | Generated SQL                                  |
|------------|-------------------|------------------------------------------------|
| PostgreSQL | `jsonbTypeof()`   | `jsonb_typeof(column)`                         |
| MySQL      | `jsonType()`      | `JSON_TYPE(column)`                            |
| MariaDB    | `jsonType()`      | `JSON_TYPE(column)`                            |
| SQLite     | `jsonType()`      | `json_type(column)`                            |

### Get Array Length

Returns the number of elements in a JSON array.

| Dialect    | Method Name           | Generated SQL                                  |
|------------|-----------------------|------------------------------------------------|
| PostgreSQL | `jsonbArrayLength()`  | `jsonb_array_length(column)`                   |
| MySQL      | `jsonLength()`        | `JSON_LENGTH(column)`                          |
| MariaDB    | `jsonLength()`        | `JSON_LENGTH(column)`                          |
| SQLite     | `jsonArrayLength()`   | `json_array_length(column)`                    |

Note: MySQL and MariaDB's `JSON_LENGTH` also returns the number of keys for objects, making it a general-purpose JSON size function. PostgreSQL and SQLite have array-specific variants.

### Get Object Keys

Returns the set of keys in the top-level JSON object.

| Dialect    | Method Name           | Generated SQL                                  |
|------------|-----------------------|------------------------------------------------|
| PostgreSQL | `jsonbObjectKeys()`   | `jsonb_object_keys(column)`                    |
| MySQL      | `jsonKeys()`          | `JSON_KEYS(column)`                            |
| MariaDB    | `jsonKeys()`          | `JSON_KEYS(column)`                            |
