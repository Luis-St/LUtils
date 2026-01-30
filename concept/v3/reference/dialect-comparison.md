# Dialect Comparison

## Overview

This document provides a comprehensive comparison of supported database dialects, including feature support, type mappings, and syntax differences.

## Supported Dialects

| Dialect | Database | Version | Status |
|---------|----------|---------|--------|
| `postgresql` | PostgreSQL | 12+ | Primary |
| `timescaledb` | TimescaleDB | 2.0+ | Extension of PostgreSQL |
| `mysql` | MySQL | 8.0+ | Supported |
| `mariadb` | MariaDB | 10.5+ | Supported |
| `h2` | H2 | 2.0+ | Testing |
| `sqlite` | SQLite | 3.35+ | Supported |

## Feature Matrix

| Feature | PostgreSQL | MySQL | MariaDB | SQLite | H2 |
|---------|------------|-------|---------|--------|-----|
| Native UUID | Yes | No | No | No | Yes |
| Native Arrays | Yes | No | No | No | Yes |
| JSON/JSONB | Yes (JSONB) | Yes | Yes | No | Yes |
| RETURNING Clause | Yes | No* | Yes | Yes (3.35+) | Yes |
| Upsert | Yes | Yes | Yes | Yes | Yes |
| Window Functions | Yes | Yes | Yes | Yes | Yes |
| CTEs | Yes | Yes | Yes | Yes | Yes |
| Recursive CTEs | Yes | Yes | Yes | Yes | Yes |
| SKIP LOCKED | Yes | Yes (8.0+) | Yes | No | No |
| Partial Indexes | Yes | No | No | Yes | No |
| Generated Columns | Yes | Yes | Yes | Yes | Yes |
| Full-Text Search | Yes | Yes | Yes | Yes (FTS5) | Yes |
| INTERVAL Type | Yes | No | No | No | No |
| DISTINCT ON | Yes | No | No | No | No |
| Array Aggregation | Yes | No | No | No | No |
| ILIKE | Yes | No | No | No | No |

*MySQL 8.0.21+ has limited RETURNING support for INSERT only

## Type Mapping Comparison

### Boolean Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `boolean` | `BOOLEAN` | `TINYINT(1)` | `INTEGER` | `BOOLEAN` |
| `Boolean` | `BOOLEAN` | `TINYINT(1)` | `INTEGER` | `BOOLEAN` |

### Integer Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `byte` / `Byte` | `SMALLINT` | `TINYINT` | `INTEGER` | `TINYINT` |
| `short` / `Short` | `SMALLINT` | `SMALLINT` | `INTEGER` | `SMALLINT` |
| `int` / `Integer` | `INTEGER` | `INT` | `INTEGER` | `INTEGER` |
| `long` / `Long` | `BIGINT` | `BIGINT` | `INTEGER` | `BIGINT` |
| `BigInteger` | `NUMERIC` | `DECIMAL(65,0)` | `TEXT` | `NUMERIC` |

### Decimal Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `float` / `Float` | `REAL` | `FLOAT` | `REAL` | `REAL` |
| `double` / `Double` | `DOUBLE PRECISION` | `DOUBLE` | `REAL` | `DOUBLE` |
| `BigDecimal` | `NUMERIC(p,s)` | `DECIMAL(p,s)` | `TEXT` | `NUMERIC(p,s)` |

### String Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `String` (short) | `VARCHAR(n)` | `VARCHAR(n)` | `TEXT` | `VARCHAR(n)` |
| `String` (long) | `TEXT` | `TEXT` | `TEXT` | `TEXT` |
| `char` | `CHAR(1)` | `CHAR(1)` | `TEXT` | `CHAR(1)` |

### Temporal Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `Instant` | `TIMESTAMPTZ` | `DATETIME(6)` | `TEXT` | `TIMESTAMP WITH TIME ZONE` |
| `LocalDate` | `DATE` | `DATE` | `TEXT` | `DATE` |
| `LocalTime` | `TIME` | `TIME(6)` | `TEXT` | `TIME` |
| `LocalDateTime` | `TIMESTAMP` | `DATETIME` | `TEXT` | `TIMESTAMP` |
| `OffsetDateTime` | `TIMESTAMPTZ` | `DATETIME(6)` | `TEXT` | `TIMESTAMP WITH TIME ZONE` |
| `ZonedDateTime` | `TIMESTAMPTZ` | `DATETIME(6)` | `TEXT` | `TIMESTAMP WITH TIME ZONE` |
| `Duration` | `INTERVAL` | `BIGINT` (ms) | `INTEGER` (ms) | `INTERVAL` |
| `Period` | `INTERVAL` | `VARCHAR(50)` | `TEXT` | `INTERVAL` |

### UUID Type

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `UUID` | `UUID` | `CHAR(36)` or `BINARY(16)` | `TEXT` | `UUID` |

### Binary Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `byte[]` | `BYTEA` | `BLOB` / `LONGBLOB` | `BLOB` | `BLOB` |

### JSON Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `JsonElement` | `JSONB` | `JSON` | `TEXT` | `JSON` |
| `Map<String, ?>` | `JSONB` | `JSON` | `TEXT` | `JSON` |

### Collection Types

| Java Type | PostgreSQL | MySQL | SQLite | H2 |
|-----------|------------|-------|--------|-----|
| `List<T>` | `T[]` (native) | `JSON` | `TEXT` (JSON) | `T[]` |
| `Set<T>` | `T[]` (native) | `JSON` | `TEXT` (JSON) | `T[]` |

## Syntax Differences

### Identifier Quoting

| Dialect | Style | Example |
|---------|-------|---------|
| PostgreSQL | Double quotes | `"user"."name"` |
| MySQL | Backticks | `` `user`.`name` `` |
| MariaDB | Backticks or double quotes | `` `user`.`name` `` |
| SQLite | Double quotes or backticks | `"user"."name"` |
| H2 | Double quotes | `"user"."name"` |

### LIMIT/OFFSET

**PostgreSQL, MySQL, SQLite, H2:**
```sql
SELECT * FROM users LIMIT 10 OFFSET 20
```

**MySQL alternative:**
```sql
SELECT * FROM users LIMIT 20, 10  -- offset, limit
```

### Boolean Literals

| Dialect | True | False |
|---------|------|-------|
| PostgreSQL | `TRUE` or `true` | `FALSE` or `false` |
| MySQL | `1` or `TRUE` | `0` or `FALSE` |
| SQLite | `1` | `0` |
| H2 | `TRUE` | `FALSE` |

### String Concatenation

| Dialect | Syntax |
|---------|--------|
| PostgreSQL | `first_name \|\| ' ' \|\| last_name` |
| MySQL | `CONCAT(first_name, ' ', last_name)` |
| SQLite | `first_name \|\| ' ' \|\| last_name` |
| H2 | `first_name \|\| ' ' \|\| last_name` or `CONCAT()` |

### Current Timestamp

| Dialect | Function |
|---------|----------|
| PostgreSQL | `NOW()` or `CURRENT_TIMESTAMP` |
| MySQL | `NOW(6)` (with microseconds) |
| SQLite | `datetime('now')` |
| H2 | `CURRENT_TIMESTAMP` |

### UUID Generation

| Dialect | Function |
|---------|----------|
| PostgreSQL | `gen_random_uuid()` |
| MySQL | `UUID()` |
| SQLite | N/A (generate in app) |
| H2 | `RANDOM_UUID()` |

### Upsert Syntax

**PostgreSQL:**
```sql
INSERT INTO users (email, name) VALUES (?, ?)
ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name
```

**MySQL:**
```sql
INSERT INTO users (email, name) VALUES (?, ?)
ON DUPLICATE KEY UPDATE name = VALUES(name)
```

**SQLite:**
```sql
INSERT INTO users (email, name) VALUES (?, ?)
ON CONFLICT (email) DO UPDATE SET name = excluded.name
```

### RETURNING Clause

**PostgreSQL, SQLite (3.35+), MariaDB:**
```sql
INSERT INTO users (name) VALUES (?) RETURNING id, created_at
UPDATE users SET name = ? WHERE id = ? RETURNING *
DELETE FROM users WHERE id = ? RETURNING *
```

**MySQL (8.0.21+):** Limited support, INSERT only
```sql
-- Not directly supported, use LAST_INSERT_ID() or separate SELECT
```

### Case-Insensitive Search

**PostgreSQL (via .dialect() API):**
```sql
SELECT * FROM users WHERE email ILIKE '%@gmail.com'
```

**MySQL (default collation is case-insensitive):**
```sql
SELECT * FROM users WHERE email LIKE '%@gmail.com'
```

**SQLite:**
```sql
SELECT * FROM users WHERE email LIKE '%@gmail.com' COLLATE NOCASE
```

## Database-Specific Features

### PostgreSQL

- **Native Arrays**: `SELECT * FROM users WHERE 'admin' = ANY(roles)`
- **JSONB Operators**: `->`, `->>`, `@>`, `?`, `?|`, `?&`
- **Full-Text Search**: `to_tsvector()`, `to_tsquery()`, `@@`
- **DISTINCT ON**: `SELECT DISTINCT ON (user_id) * FROM orders`
- **Array Aggregation**: `array_agg()`, `string_agg()`
- **Range Types**: `int4range`, `tstzrange`, etc.
- **ILIKE**: Case-insensitive LIKE (accessed via `.dialect(SqlDialect.POSTGRES)`)

### TimescaleDB (PostgreSQL Extension)

- **Hypertables**: `create_hypertable()`
- **Time Buckets**: `time_bucket('1 hour', time_column)` (via `.dialect(SqlDialect.TIMESCALE)`)
- **Compression**: `add_compression_policy()`
- **Retention**: `add_retention_policy()`
- **Continuous Aggregates**: Materialized views with automatic refresh

### MySQL

- **Full-Text Search**: `MATCH (col) AGAINST ('term')`
- **JSON Functions**: `JSON_EXTRACT()`, `JSON_CONTAINS()`
- **Index Hints**: `USE INDEX`, `FORCE INDEX`, `IGNORE INDEX`
- **INSERT IGNORE**: Skip on duplicate
- **REPLACE INTO**: Delete and re-insert on duplicate

### SQLite

- **FTS5**: Full-text search extension
- **JSON1**: JSON functions extension
- **Window Functions**: Full support since 3.25
- **UPSERT**: Since 3.24
- **RETURNING**: Since 3.35

## Dialect Limitations

### PostgreSQL
- Maximum identifier length: 63 characters
- No native TINYINT (use SMALLINT)

### MySQL
- No native UUID type (use CHAR(36) or BINARY(16))
- No native arrays (use JSON)
- No INTERVAL arithmetic (use date functions)
- No RETURNING for UPDATE/DELETE
- Case sensitivity depends on collation

### SQLite
- No native types (everything is TEXT, INTEGER, REAL, BLOB)
- No native UUID, timestamp, or boolean types
- Limited concurrent write access
- No ALTER COLUMN support

### H2
- Some PostgreSQL compatibility mode limitations
- Not suitable for production

## Recommended Practices

### UUID Storage
- **PostgreSQL**: Use native `UUID` type
- **MySQL**: Use `BINARY(16)` for efficiency, `CHAR(36)` for readability
- **SQLite**: Use `TEXT` with application-side validation

### Timestamps
- **PostgreSQL**: Always use `TIMESTAMPTZ` for timezone-aware timestamps
- **MySQL**: Use `DATETIME(6)` for microsecond precision
- **SQLite**: Store as ISO 8601 `TEXT`, parse in application

### JSON Data
- **PostgreSQL**: Prefer `JSONB` over `JSON` for indexing and querying
- **MySQL**: Use `JSON` with appropriate indexes
- **SQLite**: Store as `TEXT`, use JSON1 functions

### Arrays
- **PostgreSQL**: Use native arrays for simple cases, JSONB for complex
- **MySQL/SQLite**: Use JSON arrays

## Related Documents

- [dialect-system.md](dialect-system.md) - Dialect architecture and implementation
- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Type definitions in YAML
