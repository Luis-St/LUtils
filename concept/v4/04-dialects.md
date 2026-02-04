# Dialects

## Supported Dialects

| Dialect | Database | Version |
|---------|----------|---------|
| `postgresql` | PostgreSQL | 12+ |
| `timescaledb` | TimescaleDB | 2.0+ |
| `mysql` | MySQL | 8.0+ |
| `mariadb` | MariaDB | 10.5+ |
| `sqlite` | SQLite | 3.35+ |
| `h2` | H2 | 2.0+ |

## Feature Matrix

| Feature | PostgreSQL | MySQL | SQLite | H2 |
|---------|------------|-------|--------|-----|
| Native UUID | Yes | No | No | Yes |
| Native Arrays | Yes | No | No | Yes |
| JSONB/JSON | Yes | Yes | No | Yes |
| RETURNING | Yes | No* | Yes | Yes |
| SKIP LOCKED | Yes | Yes | No | No |
| ILIKE | Yes | No | No | No |
| Partial Indexes | Yes | No | Yes | No |
| Window Functions | Yes | Yes | Yes | Yes |
| CTEs | Yes | Yes | Yes | Yes |

*MySQL 8.0.21+ has INSERT RETURNING only

## Dialect-Specific API

Access via `.dialect()` on tables and columns:

```java
// PostgreSQL
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.EMAIL.dialect(SqlDialect.POSTGRES).ilike("%@GMAIL.COM"))
    .fetch();

// TimescaleDB
SensorTable.TABLE.dialect(SqlDialect.TIMESCALE).select(
    SensorTable.TIME.dialect(SqlDialect.TIMESCALE).timeBucket("1 hour"),
    SqlAgg.avg(SensorTable.VALUE)
).groupBy(1).fetch();

// Standard (no dialect)
UserTable.TABLE.select().where(UserTable.EMAIL.like("%@gmail.com")).fetch();
```

### PostgreSQL Features

```java
// ILIKE
col.dialect(SqlDialect.POSTGRES).ilike("%pattern%")
col.dialect(SqlDialect.POSTGRES).notIlike("%pattern%")

// Arrays
col.dialect(SqlDialect.POSTGRES).arrayContains(element)
col.dialect(SqlDialect.POSTGRES).arrayOverlaps(List.of(a, b))
col.dialect(SqlDialect.POSTGRES).arrayAgg()

// JSONB
col.dialect(SqlDialect.POSTGRES).jsonExtract("$.path", Type.class)
col.dialect(SqlDialect.POSTGRES).jsonExists("$.path")

// Full-text search
col.dialect(SqlDialect.POSTGRES).fullTextSearch("query & terms")
```

### TimescaleDB Features

```java
// Time bucket
col.dialect(SqlDialect.TIMESCALE).timeBucket("1 hour")
col.dialect(SqlDialect.TIMESCALE).timeBucketGapfill("1 hour", start, end)

// First/last
col.dialect(SqlDialect.TIMESCALE).first(timeCol)
col.dialect(SqlDialect.TIMESCALE).last(timeCol)
col.dialect(SqlDialect.TIMESCALE).locf()  // Last observation carried forward
```

### MySQL Features

```java
col.dialect(SqlDialect.MYSQL).matchAgainst("search terms")
col.dialect(SqlDialect.MYSQL).groupConcat(", ")
```

## Type Mappings

| Java Type | PostgreSQL | MySQL | SQLite |
|-----------|------------|-------|--------|
| `boolean` | `BOOLEAN` | `TINYINT(1)` | `INTEGER` |
| `int` | `INTEGER` | `INT` | `INTEGER` |
| `long` | `BIGINT` | `BIGINT` | `INTEGER` |
| `BigDecimal` | `NUMERIC(p,s)` | `DECIMAL(p,s)` | `TEXT` |
| `String` | `VARCHAR(n)` | `VARCHAR(n)` | `TEXT` |
| `UUID` | `UUID` | `CHAR(36)` | `TEXT` |
| `Instant` | `TIMESTAMPTZ` | `DATETIME(6)` | `TEXT` |
| `byte[]` | `BYTEA` | `BLOB` | `BLOB` |
| `List<T>` | `T[]` | `JSON` | `TEXT` |

## Syntax Differences

| | PostgreSQL | MySQL | SQLite |
|---|------------|-------|--------|
| **Quoting** | `"name"` | `` `name` `` | `"name"` |
| **Boolean** | `TRUE/FALSE` | `1/0` | `1/0` |
| **Concat** | `a \|\| b` | `CONCAT(a,b)` | `a \|\| b` |
| **NOW()** | `NOW()` | `NOW(6)` | `datetime('now')` |
| **UUID** | `gen_random_uuid()` | `UUID()` | N/A |

## Upsert Syntax

**PostgreSQL/SQLite:**
```sql
INSERT ... ON CONFLICT (col) DO UPDATE SET ...
```

**MySQL:**
```sql
INSERT ... ON DUPLICATE KEY UPDATE ...
```

## Feature Detection

```java
SqlDialect dialect = SqlDialectRegistry.detect(jdbcUrl);  // Auto-detect
SqlDialectFeatures features = dialect.getFeatures();

if (features.supportsReturning()) {
    User saved = UserTable.TABLE.insert(user).returning().fetchOne();
} else {
    UserTable.TABLE.insert(user).execute();
    // Separate SELECT
}

// Capability-safe method (handles automatically)
User saved = UserTable.TABLE.insert(user).fetchInserted();
```

## SqlDialect Interface

```java
SqlDialect.POSTGRES      // PostgreSQL
SqlDialect.TIMESCALE     // TimescaleDB
SqlDialect.MYSQL         // MySQL/MariaDB
SqlDialect.SQLITE        // SQLite
SqlDialect.H2            // H2
SqlDialect.DEFAULT       // Common SQL

dialect.getId()              // "postgresql"
dialect.getName()            // "PostgreSQL"
dialect.getFeatures()        // SqlDialectFeatures
dialect.quoteIdentifier(s)   // "\"name\""
dialect.nowFunction()        // "NOW()"
dialect.uuidFunction()       // "gen_random_uuid()"
```
