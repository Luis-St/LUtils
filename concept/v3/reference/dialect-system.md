# Dialect System

## Overview

The dialect system abstracts database-specific SQL syntax, type mappings, and features. In v3, dialect-specific methods are accessed through a fluent `.dialect()` API that returns dialect-specific builders enabling type-safe access to database-specific features.

## Architecture

```
+---------------------------------------------------------------+
|                      SqlDialect                                |
|  - getId(): String                                            |
|  - getName(): String                                          |
|  - getFeatures(): SqlDialectFeatures                          |
+---------------------------------------------------------------+
|                    |                         |                |
|                    v                         v                |
|  +-----------------------+   +---------------------------+    |
|  |    SqlTypeMapping     |   |     SqlGenerator          |    |
|  |  - toSqlType()        |   |  - createTable()          |    |
|  |  - toJdbcType()       |   |  - insert()               |    |
|  |  - toJdbcValue()      |   |  - select()               |    |
|  |  - fromJdbcValue()    |   |  - update()               |    |
|  +-----------------------+   |  - delete()               |    |
|                              |  - upsert()               |    |
|                              +---------------------------+    |
+---------------------------------------------------------------+
```

## Dialect Fluent API

### Basic Usage

```java
// Dialect specified on table - returns dialect-specific builder
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.EMAIL.dialect(SqlDialect.POSTGRES).ilike("%@gmail.com"))
    .fetch();

// Without dialect - uses default dialect (common methods only)
UserTable.TABLE.select()
    .where(UserTable.EMAIL.like("%@gmail.com"))  // Standard LIKE
    .fetch();
```

### How .dialect() Works

When calling `.dialect()` on a table or column, it returns a dialect-specific wrapper:

```java
// SqlTable.dialect() returns a dialect-specific table wrapper
SqlDialectTable<User, PostgresDialect> pgTable = UserTable.TABLE.dialect(SqlDialect.POSTGRES);

// The wrapper's select() returns a dialect-specific query builder
PostgresSelectBuilder<User> query = pgTable.select();

// SqlColumn.dialect() returns a dialect-specific column wrapper
SqlDialectColumn<User, String, PostgresDialect> pgEmail =
    UserTable.EMAIL.dialect(SqlDialect.POSTGRES);

// The wrapper has dialect-specific condition methods
SqlCondition<User> condition = pgEmail.ilike("%@GMAIL.COM");
```

### Dialect Hierarchy

```java
// Base dialect - common methods for all databases
public interface SqlDialect {
    String getId();
    String getName();
    SqlTypeMapping getTypeMapping();
    SqlGenerator getSqlGenerator();
    SqlDialectFeatures getFeatures();
}

// PostgreSQL dialect with specific methods
public interface PostgresDialect extends SqlDialect {
    // Enabled via .dialect(SqlDialect.POSTGRES)
}

// TimescaleDB extends PostgreSQL
public interface TimescaleDialect extends PostgresDialect {
    // Enabled via .dialect(SqlDialect.TIMESCALE)
}

// MySQL dialect
public interface MySqlDialect extends SqlDialect {
    // Enabled via .dialect(SqlDialect.MYSQL)
}
```

## SqlDialect Interface

```java
package net.luis.utils.db.dialect;

import org.jspecify.annotations.NullMarked;
import java.util.Optional;

/**
 * Interface for database dialect implementations.<br>
 * <p>
 *     Each dialect provides database-specific SQL generation,<br>
 *     type mappings, and feature support information.<br>
 * </p>
 *
 * @author Luis-St
 */
@NullMarked
public interface SqlDialect {

    // Predefined dialect constants
    SqlDialect DEFAULT = DefaultSqlDialect.INSTANCE;
    PostgresDialect POSTGRES = PostgresSqlDialect.INSTANCE;
    TimescaleDialect TIMESCALE = TimescaleSqlDialect.INSTANCE;
    MySqlDialect MYSQL = MySqlSqlDialect.INSTANCE;
    SqliteDialect SQLITE = SqliteSqlDialect.INSTANCE;
    H2Dialect H2 = H2SqlDialect.INSTANCE;

    /**
     * Returns the unique identifier for this dialect.<br>
     *
     * @return The dialect identifier (e.g., "postgresql", "mysql")
     */
    String getId();

    /**
     * Returns the display name for this dialect.<br>
     *
     * @return The dialect name (e.g., "PostgreSQL", "MySQL")
     */
    String getName();

    /**
     * Returns the type mapping for this dialect.<br>
     *
     * @return The type mapping implementation
     */
    SqlTypeMapping getTypeMapping();

    /**
     * Returns the SQL generator for this dialect.<br>
     *
     * @return The SQL generator implementation
     */
    SqlGenerator getSqlGenerator();

    /**
     * Returns the feature flags for this dialect.<br>
     *
     * @return The dialect features
     */
    SqlDialectFeatures getFeatures();

    /**
     * Quotes an identifier (table/column name) for this dialect.<br>
     *
     * @param identifier The identifier to quote
     * @return The quoted identifier
     */
    String quoteIdentifier(String identifier);

    /**
     * Generates LIMIT/OFFSET clause for this dialect.<br>
     *
     * @param limit Maximum rows to return
     * @param offset Rows to skip
     * @return The LIMIT/OFFSET clause
     */
    String limitOffsetClause(int limit, int offset);

    /**
     * Returns the FOR UPDATE locking clause.<br>
     *
     * @return The FOR UPDATE clause
     */
    String forUpdateClause();

    /**
     * Returns the FOR SHARE locking clause.<br>
     *
     * @return The FOR SHARE clause
     */
    String forShareClause();

    /**
     * Returns the SKIP LOCKED clause if supported.<br>
     *
     * @return Optional containing the clause, empty if not supported
     */
    Optional<String> skipLockedClause();

    /**
     * Returns the current timestamp function.<br>
     *
     * @return The NOW() equivalent function
     */
    String nowFunction();

    /**
     * Returns the UUID generation function.<br>
     *
     * @return The UUID generation function
     */
    String uuidFunction();

    /**
     * Returns the next value expression for a sequence.<br>
     *
     * @param sequenceName The sequence name
     * @return The nextval expression
     */
    String nextValExpression(String sequenceName);

    /**
     * Returns the current value expression for a sequence.<br>
     *
     * @param sequenceName The sequence name
     * @return The currval expression
     */
    String currentValExpression(String sequenceName);
}
```

## Dialect-Specific Column Wrappers

```java
/**
 * PostgreSQL-specific column methods.
 */
public interface PostgresColumn<E, T> {

    /**
     * Case-insensitive LIKE (PostgreSQL only).
     */
    SqlCondition<E> ilike(String pattern);

    /**
     * Case-insensitive NOT LIKE.
     */
    SqlCondition<E> notIlike(String pattern);
}

/**
 * PostgreSQL array column methods.
 */
public interface PostgresArrayColumn<E, T> extends PostgresColumn<E, List<T>> {

    /**
     * Array contains element.
     */
    SqlCondition<E> arrayContains(T element);

    /**
     * Array overlaps with collection.
     */
    SqlCondition<E> arrayOverlaps(Collection<T> elements);

    /**
     * Array contains all elements.
     */
    SqlCondition<E> arrayContainsAll(Collection<T> elements);
}

/**
 * TimescaleDB-specific column methods for timestamp columns.
 */
public interface TimescaleColumn<E> extends PostgresColumn<E, Instant> {

    /**
     * TimescaleDB time_bucket function.
     */
    SqlFunction<Instant> timeBucket(String interval);

    /**
     * time_bucket with origin.
     */
    SqlFunction<Instant> timeBucket(String interval, Instant origin);
}
```

## PostgreSQL Dialect Implementation

```java
package net.luis.utils.db.dialect.impl;

import net.luis.utils.db.dialect.*;
import net.luis.utils.db.mapping.SqlTypeMapping;
import org.jspecify.annotations.NullMarked;
import java.util.Optional;

/**
 * PostgreSQL dialect implementation.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class PostgresSqlDialect implements PostgresDialect {

    public static final PostgresSqlDialect INSTANCE = new PostgresSqlDialect();

    private final PostgresTypeMapping typeMapping = new PostgresTypeMapping();
    private final PostgresSqlGenerator sqlGenerator = new PostgresSqlGenerator(this);

    private PostgresSqlDialect() {}

    @Override
    public String getId() {
        return "postgresql";
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }

    @Override
    public SqlTypeMapping getTypeMapping() {
        return typeMapping;
    }

    @Override
    public SqlGenerator getSqlGenerator() {
        return sqlGenerator;
    }

    @Override
    public SqlDialectFeatures getFeatures() {
        return SqlDialectFeatures.builder()
            .supportsArrays(true)
            .supportsJson(true)
            .supportsUuid(true)
            .supportsInterval(true)
            .supportsReturning(true)
            .supportsUpsert(true)
            .supportsWindowFunctions(true)
            .supportsCte(true)
            .supportsRecursiveCte(true)
            .supportsSkipLocked(true)
            .supportsPartialIndexes(true)
            .supportsGeneratedColumns(true)
            .supportsDistinctOn(true)
            .supportsIlike(true)
            .maxIdentifierLength(63)
            .build();
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    @Override
    public String limitOffsetClause(int limit, int offset) {
        StringBuilder sb = new StringBuilder();
        if (limit > 0) {
            sb.append("LIMIT ").append(limit);
        }
        if (offset > 0) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append("OFFSET ").append(offset);
        }
        return sb.toString();
    }

    @Override
    public String forUpdateClause() {
        return "FOR UPDATE";
    }

    @Override
    public String forShareClause() {
        return "FOR SHARE";
    }

    @Override
    public Optional<String> skipLockedClause() {
        return Optional.of("SKIP LOCKED");
    }

    @Override
    public String nowFunction() {
        return "NOW()";
    }

    @Override
    public String uuidFunction() {
        return "gen_random_uuid()";
    }

    @Override
    public String nextValExpression(String sequenceName) {
        return "nextval('" + sequenceName + "')";
    }

    @Override
    public String currentValExpression(String sequenceName) {
        return "currval('" + sequenceName + "')";
    }
}
```

## TimescaleDB Dialect (Extension)

TimescaleDB extends PostgreSQL with time-series features:

```java
package net.luis.utils.db.dialect.impl;

import org.jspecify.annotations.NullMarked;

/**
 * TimescaleDB dialect extending PostgreSQL.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class TimescaleSqlDialect extends PostgresSqlDialect implements TimescaleDialect {

    public static final TimescaleSqlDialect INSTANCE = new TimescaleSqlDialect();

    private TimescaleSqlDialect() {}

    @Override
    public String getId() {
        return "timescaledb";
    }

    @Override
    public String getName() {
        return "TimescaleDB";
    }

    // Hypertable creation
    public String createHypertable(String table, String timeColumn, String chunkInterval) {
        return String.format(
            "SELECT create_hypertable('%s', '%s', chunk_time_interval => INTERVAL '%s')",
            table, timeColumn, chunkInterval
        );
    }

    // Compression policy
    public String addCompressionPolicy(String table, String interval) {
        return String.format(
            "SELECT add_compression_policy('%s', INTERVAL '%s')",
            table, interval
        );
    }

    // Retention policy
    public String addRetentionPolicy(String table, String interval) {
        return String.format(
            "SELECT add_retention_policy('%s', INTERVAL '%s')",
            table, interval
        );
    }

    // Continuous aggregate
    public String createContinuousAggregate(String name, String query, String interval) {
        return String.format("""
            CREATE MATERIALIZED VIEW %s
            WITH (timescaledb.continuous) AS
            %s
            WITH NO DATA;

            SELECT add_continuous_aggregate_policy('%s',
                start_offset => NULL,
                end_offset => INTERVAL '%s',
                schedule_interval => INTERVAL '%s');
            """, name, query, name, interval, interval);
    }
}
```

## Using Dialect-Specific Features

### PostgreSQL ILIKE

```java
// Via .dialect() API
List<User> users = UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.EMAIL.dialect(SqlDialect.POSTGRES).ilike("%@GMAIL.COM"))
    .fetch();

// Generated SQL:
// SELECT * FROM users WHERE email ILIKE ?
```

### PostgreSQL Array Operations

```java
// Array contains element
List<User> admins = UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.ROLES.dialect(SqlDialect.POSTGRES).arrayContains("admin"))
    .fetch();

// Generated SQL:
// SELECT * FROM users WHERE 'admin' = ANY(roles)

// Array overlaps
List<User> users = UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.ROLES.dialect(SqlDialect.POSTGRES)
        .arrayOverlaps(List.of("admin", "moderator")))
    .fetch();

// Generated SQL:
// SELECT * FROM users WHERE roles && ARRAY['admin', 'moderator']
```

### TimescaleDB Time Bucket

```java
// Time bucket aggregation
List<Row2<Instant, Double>> hourlyStats = SensorTable.TABLE
    .dialect(SqlDialect.TIMESCALE).select(
        SensorTable.RECORDED_AT.dialect(SqlDialect.TIMESCALE).timeBucket("1 hour"),
        SqlAgg.avg(SensorTable.VALUE)
    )
    .where(SensorTable.RECORDED_AT.greaterThan(oneDayAgo))
    .groupBy(1)
    .orderBy(1)
    .fetch();

// Generated SQL:
// SELECT time_bucket('1 hour', recorded_at), AVG(value)
// FROM sensors
// WHERE recorded_at > ?
// GROUP BY 1 ORDER BY 1

// First/last value per time bucket
List<Row3<Instant, Double, Double>> results = SensorTable.TABLE
    .dialect(SqlDialect.TIMESCALE).select(
        SensorTable.RECORDED_AT.dialect(SqlDialect.TIMESCALE).timeBucket("1 hour"),
        SensorTable.VALUE.dialect(SqlDialect.TIMESCALE).first(SensorTable.RECORDED_AT),
        SensorTable.VALUE.dialect(SqlDialect.TIMESCALE).last(SensorTable.RECORDED_AT)
    )
    .groupBy(1)
    .fetch();
```

## SqlDialectFeatures

```java
package net.luis.utils.db.dialect;

/**
 * Describes features supported by a specific dialect.<br>
 *
 * @param supportsArrays Whether native array types are supported
 * @param supportsJson Whether JSON/JSONB types are supported
 * @param supportsUuid Whether native UUID type is supported
 * @param supportsInterval Whether INTERVAL type is supported
 * @param supportsReturning Whether RETURNING clause is supported
 * @param supportsUpsert Whether native UPSERT is supported
 * @param supportsWindowFunctions Whether window functions are supported
 * @param supportsCte Whether Common Table Expressions are supported
 * @param supportsRecursiveCte Whether recursive CTEs are supported
 * @param supportsSkipLocked Whether SKIP LOCKED is supported
 * @param supportsPartialIndexes Whether partial indexes are supported
 * @param supportsGeneratedColumns Whether generated columns are supported
 * @param supportsDistinctOn Whether DISTINCT ON is supported
 * @param supportsIlike Whether ILIKE is supported
 * @param maxIdentifierLength Maximum length of identifiers
 * @author Luis-St
 */
public record SqlDialectFeatures(
    boolean supportsArrays,
    boolean supportsJson,
    boolean supportsUuid,
    boolean supportsInterval,
    boolean supportsReturning,
    boolean supportsUpsert,
    boolean supportsWindowFunctions,
    boolean supportsCte,
    boolean supportsRecursiveCte,
    boolean supportsSkipLocked,
    boolean supportsPartialIndexes,
    boolean supportsGeneratedColumns,
    boolean supportsDistinctOn,
    boolean supportsIlike,
    int maxIdentifierLength
) {

    public static SqlDialectFeaturesBuilder builder() {
        return new SqlDialectFeaturesBuilder();
    }

    public SqlDialectFeaturesBuilder toBuilder() {
        return new SqlDialectFeaturesBuilder(this);
    }
}
```

## SqlDialectRegistry

```java
package net.luis.utils.db.dialect;

import net.luis.utils.db.dialect.impl.*;
import org.jspecify.annotations.NullMarked;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for database dialects.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class SqlDialectRegistry {

    private static final Map<String, SqlDialect> DIALECTS = new ConcurrentHashMap<>();

    static {
        // Register built-in dialects
        register(PostgresSqlDialect.INSTANCE);
        register(TimescaleSqlDialect.INSTANCE);
        register(MySqlSqlDialect.INSTANCE);
        register(SqliteSqlDialect.INSTANCE);
        register(H2SqlDialect.INSTANCE);

        // Load additional dialects via ServiceLoader
        ServiceLoader.load(SqlDialect.class).forEach(SqlDialectRegistry::register);
    }

    private SqlDialectRegistry() {}

    public static void register(SqlDialect dialect) {
        DIALECTS.put(dialect.getId().toLowerCase(), dialect);
    }

    public static SqlDialect get(String id) {
        SqlDialect dialect = DIALECTS.get(id.toLowerCase());
        if (dialect == null) {
            throw new IllegalArgumentException("Unknown dialect: " + id);
        }
        return dialect;
    }

    public static Optional<SqlDialect> find(String id) {
        return Optional.ofNullable(DIALECTS.get(id.toLowerCase()));
    }

    public static SqlDialect detect(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return PostgresSqlDialect.INSTANCE;
        } else if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return MySqlSqlDialect.INSTANCE;
        } else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            return MySqlSqlDialect.INSTANCE;  // MariaDB uses MySQL dialect
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            return SqliteSqlDialect.INSTANCE;
        } else if (jdbcUrl.startsWith("jdbc:h2:")) {
            return H2SqlDialect.INSTANCE;
        }
        throw new IllegalArgumentException("Cannot detect dialect from JDBC URL: " + jdbcUrl);
    }

    public static Collection<SqlDialect> all() {
        return Collections.unmodifiableCollection(DIALECTS.values());
    }
}
```

## Feature Detection at Runtime

```java
// Check dialect capabilities
SqlDialect dialect = SqlDialectRegistry.get("postgresql");

if (dialect.getFeatures().supportsReturning()) {
    User saved = UserTable.TABLE.insert(user)
        .returning()
        .fetchOne();
} else {
    UserTable.TABLE.insert(user).execute();
    User saved = UserTable.TABLE.select()
        .where(UserTable.EMAIL.equalTo(user.email()))
        .fetchOne();
}

// Capability-safe methods handle this automatically
User saved = UserTable.TABLE.insert(user).fetchInserted();
// Uses RETURNING if supported, separate SELECT otherwise
```

## Related Documents

- [dialect-comparison.md](dialect-comparison.md) - Feature and type comparison
- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Schema definitions
- [sql-functions.md](sql-functions.md) - Dialect-specific functions
