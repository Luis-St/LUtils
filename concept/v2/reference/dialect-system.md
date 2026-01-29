# Dialect System

## Overview

The dialect system abstracts database-specific SQL syntax, type mappings, and features. It uses separated concerns: `Dialect` provides identity and features, `TypeMapping` handles type conversion, and `SqlGenerator` produces SQL statements.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Dialect                                  │
│  - getId(): String                                              │
│  - getName(): String                                            │
│  - getFeatures(): DialectFeatures                               │
├─────────────────────────────────────────────────────────────────┤
│                    │                         │                  │
│                    ▼                         ▼                  │
│  ┌─────────────────────────┐   ┌─────────────────────────────┐ │
│  │      TypeMapping        │   │       SqlGenerator          │ │
│  │  - toSqlType()          │   │  - createTable()            │ │
│  │  - toJdbcType()         │   │  - insert()                 │ │
│  │  - toJdbcValue()        │   │  - select()                 │ │
│  │  - fromJdbcValue()      │   │  - update()                 │ │
│  └─────────────────────────┘   │  - delete()                 │ │
│                                │  - upsert()                 │ │
│                                └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Dialect Interface

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
public interface Dialect {

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
    TypeMapping getTypeMapping();

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
    DialectFeatures getFeatures();

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

## Dialect Features

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
 * @param maxIdentifierLength Maximum length of identifiers
 * @author Luis-St
 */
public record DialectFeatures(
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
    int maxIdentifierLength
) {

    public static DialectFeaturesBuilder builder() {
        return new DialectFeaturesBuilder();
    }

    public DialectFeaturesBuilder toBuilder() {
        return new DialectFeaturesBuilder(this);
    }
}
```

## Type Mapping Interface

```java
package net.luis.utils.db.mapping;

import org.jspecify.annotations.NullMarked;

/**
 * Maps Java types to SQL types for a specific dialect.<br>
 *
 * @author Luis-St
 */
@NullMarked
public interface TypeMapping {

    /**
     * Maps a Java type to SQL type declaration.<br>
     *
     * @param javaType The Java type
     * @param options Additional type options (length, precision, etc.)
     * @return The SQL type declaration
     */
    String toSqlType(Class<?> javaType, TypeOptions options);

    /**
     * Returns the JDBC type code for a Java type.<br>
     *
     * @param javaType The Java type
     * @return The JDBC Types constant
     */
    int toJdbcType(Class<?> javaType);

    /**
     * Converts a Java value for JDBC binding.<br>
     *
     * @param value The Java value
     * @return The JDBC-compatible value
     */
    Object toJdbcValue(Object value);

    /**
     * Converts a JDBC value to Java type.<br>
     *
     * @param value The JDBC value
     * @param targetType The target Java type
     * @return The Java value
     */
    <T> T fromJdbcValue(Object value, Class<T> targetType);
}

/**
 * Options for type mapping (length, precision, scale, etc.).<br>
 */
public record TypeOptions(
    Integer length,
    Integer precision,
    Integer scale,
    String columnType   // Override from YAML
) {
    public static TypeOptions defaults() {
        return new TypeOptions(null, null, null, null);
    }

    public static TypeOptions withLength(int length) {
        return new TypeOptions(length, null, null, null);
    }

    public static TypeOptions withPrecision(int precision, int scale) {
        return new TypeOptions(null, precision, scale, null);
    }
}
```

## SQL Generator Interface

```java
package net.luis.utils.db.dialect;

import org.jspecify.annotations.NullMarked;
import java.util.List;

/**
 * Generates SQL statements for a specific dialect.<br>
 *
 * @author Luis-St
 */
@NullMarked
public interface SqlGenerator {

    // DDL generation
    String createTable(TableDefinition table);
    String alterTable(TableDefinition table, List<SchemaChange> changes);
    String dropTable(String tableName, boolean ifExists, boolean cascade);

    String createIndex(IndexDefinition index);
    String dropIndex(String indexName, String tableName);

    String createSequence(SequenceDefinition sequence);
    String dropSequence(String sequenceName);

    // DML generation
    String insert(String table, List<String> columns);
    String insertReturning(String table, List<String> columns, List<String> returning);
    String update(String table, List<String> columns, String whereClause);
    String updateReturning(String table, List<String> columns, String whereClause, List<String> returning);
    String delete(String table, String whereClause);
    String deleteReturning(String table, String whereClause, List<String> returning);
    String upsert(String table, List<String> columns, List<String> conflictColumns,
                  List<String> updateColumns);

    // Query generation
    String select(SelectDefinition select);
}
```

## PostgreSQL Dialect Implementation

```java
package net.luis.utils.db.dialect.impl;

import net.luis.utils.db.dialect.*;
import net.luis.utils.db.mapping.TypeMapping;
import org.jspecify.annotations.NullMarked;
import java.util.Optional;

/**
 * PostgreSQL dialect implementation.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class PostgresDialect implements Dialect {

    public static final PostgresDialect INSTANCE = new PostgresDialect();

    private final PostgresTypeMapping typeMapping = new PostgresTypeMapping();
    private final PostgresSqlGenerator sqlGenerator = new PostgresSqlGenerator(this);

    private PostgresDialect() {}

    @Override
    public String getId() {
        return "postgresql";
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }

    @Override
    public TypeMapping getTypeMapping() {
        return typeMapping;
    }

    @Override
    public SqlGenerator getSqlGenerator() {
        return sqlGenerator;
    }

    @Override
    public DialectFeatures getFeatures() {
        return DialectFeatures.builder()
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
public final class TimescaleDialect extends PostgresDialect {

    public static final TimescaleDialect INSTANCE = new TimescaleDialect();

    private TimescaleDialect() {}

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

    // Time bucket function
    public String timeBucket(String interval, String column) {
        return String.format("time_bucket('%s', %s)", interval, column);
    }
}
```

## MySQL Dialect Implementation

```java
package net.luis.utils.db.dialect.impl;

import net.luis.utils.db.dialect.*;
import net.luis.utils.db.mapping.TypeMapping;
import org.jspecify.annotations.NullMarked;
import java.util.Optional;

/**
 * MySQL dialect implementation.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class MySqlDialect implements Dialect {

    public static final MySqlDialect INSTANCE = new MySqlDialect();

    private final MySqlTypeMapping typeMapping = new MySqlTypeMapping();
    private final MySqlSqlGenerator sqlGenerator = new MySqlSqlGenerator(this);

    private MySqlDialect() {}

    @Override
    public String getId() {
        return "mysql";
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public TypeMapping getTypeMapping() {
        return typeMapping;
    }

    @Override
    public SqlGenerator getSqlGenerator() {
        return sqlGenerator;
    }

    @Override
    public DialectFeatures getFeatures() {
        return DialectFeatures.builder()
            .supportsArrays(false)
            .supportsJson(true)
            .supportsUuid(false)
            .supportsInterval(false)
            .supportsReturning(false)    // Limited in 8.0.21+
            .supportsUpsert(true)        // ON DUPLICATE KEY UPDATE
            .supportsWindowFunctions(true)
            .supportsCte(true)
            .supportsRecursiveCte(true)
            .supportsSkipLocked(true)    // MySQL 8.0+
            .supportsPartialIndexes(false)
            .supportsGeneratedColumns(true)
            .supportsDistinctOn(false)
            .maxIdentifierLength(64)
            .build();
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return "`" + identifier.replace("`", "``") + "`";
    }

    @Override
    public String limitOffsetClause(int limit, int offset) {
        if (offset > 0) {
            return "LIMIT " + offset + ", " + limit;
        }
        return "LIMIT " + limit;
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
        return "NOW(6)";  // Microsecond precision
    }

    @Override
    public String uuidFunction() {
        return "UUID()";
    }

    @Override
    public String nextValExpression(String sequenceName) {
        throw new UnsupportedOperationException("MySQL doesn't support sequences, use AUTO_INCREMENT");
    }

    @Override
    public String currentValExpression(String sequenceName) {
        throw new UnsupportedOperationException("MySQL doesn't support sequences");
    }
}
```

## SQLite Dialect Implementation

```java
package net.luis.utils.db.dialect.impl;

import net.luis.utils.db.dialect.*;
import org.jspecify.annotations.NullMarked;
import java.util.Optional;

/**
 * SQLite dialect implementation.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class SqliteDialect implements Dialect {

    public static final SqliteDialect INSTANCE = new SqliteDialect();

    private SqliteDialect() {}

    @Override
    public String getId() {
        return "sqlite";
    }

    @Override
    public String getName() {
        return "SQLite";
    }

    @Override
    public DialectFeatures getFeatures() {
        return DialectFeatures.builder()
            .supportsArrays(false)
            .supportsJson(false)         // Extension required
            .supportsUuid(false)
            .supportsInterval(false)
            .supportsReturning(true)     // SQLite 3.35+
            .supportsUpsert(true)        // SQLite 3.24+
            .supportsWindowFunctions(true) // SQLite 3.25+
            .supportsCte(true)
            .supportsRecursiveCte(true)
            .supportsSkipLocked(false)
            .supportsPartialIndexes(true)
            .supportsGeneratedColumns(true)
            .supportsDistinctOn(false)
            .maxIdentifierLength(Integer.MAX_VALUE)
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
    public String nowFunction() {
        return "datetime('now')";
    }

    @Override
    public String uuidFunction() {
        // SQLite has no native UUID function
        throw new UnsupportedOperationException("SQLite doesn't support UUID generation, generate in application");
    }

    @Override
    public Optional<String> skipLockedClause() {
        return Optional.empty();
    }
}
```

## Dialect Registry

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
public final class DialectRegistry {

    private static final Map<String, Dialect> DIALECTS = new ConcurrentHashMap<>();

    static {
        // Register built-in dialects
        register(PostgresDialect.INSTANCE);
        register(TimescaleDialect.INSTANCE);
        register(MySqlDialect.INSTANCE);
        register(SqliteDialect.INSTANCE);
        register(H2Dialect.INSTANCE);

        // Load additional dialects via ServiceLoader
        ServiceLoader.load(Dialect.class).forEach(DialectRegistry::register);
    }

    private DialectRegistry() {}

    public static void register(Dialect dialect) {
        DIALECTS.put(dialect.getId().toLowerCase(), dialect);
    }

    public static Dialect get(String id) {
        Dialect dialect = DIALECTS.get(id.toLowerCase());
        if (dialect == null) {
            throw new IllegalArgumentException("Unknown dialect: " + id);
        }
        return dialect;
    }

    public static Optional<Dialect> find(String id) {
        return Optional.ofNullable(DIALECTS.get(id.toLowerCase()));
    }

    public static Dialect detect(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return PostgresDialect.INSTANCE;
        } else if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return MySqlDialect.INSTANCE;
        } else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            return MySqlDialect.INSTANCE;  // MariaDB uses MySQL dialect
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            return SqliteDialect.INSTANCE;
        } else if (jdbcUrl.startsWith("jdbc:h2:")) {
            return H2Dialect.INSTANCE;
        }
        throw new IllegalArgumentException("Cannot detect dialect from JDBC URL: " + jdbcUrl);
    }

    public static Collection<Dialect> all() {
        return Collections.unmodifiableCollection(DIALECTS.values());
    }
}
```

## Custom Dialect Implementation

For unsupported databases or custom behavior:

```java
// Custom dialect extending an existing one
public final class CockroachDialect extends PostgresDialect {

    public static final CockroachDialect INSTANCE = new CockroachDialect();

    @Override
    public String getId() {
        return "cockroachdb";
    }

    @Override
    public String getName() {
        return "CockroachDB";
    }

    @Override
    public String uuidFunction() {
        return "gen_random_uuid()";
    }

    @Override
    public DialectFeatures getFeatures() {
        return super.getFeatures().toBuilder()
            .supportsSkipLocked(false)  // Not supported in CockroachDB
            .build();
    }
}

// Register via ServiceLoader
// META-INF/services/net.luis.utils.db.dialect.Dialect contains:
// com.example.db.CockroachDialect

// Or register programmatically
DialectRegistry.register(CockroachDialect.INSTANCE);
```

## Feature Detection at Runtime

```java
// Check dialect capabilities
Dialect dialect = DialectRegistry.get("postgresql");

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
