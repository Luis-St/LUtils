# SQL Dialect System

## Overview

The dialect system abstracts database-specific SQL syntax, type mappings, and features. This allows the same YAML schema to generate correct SQL for different database systems while taking advantage of database-specific features when available.

## Dialect Architecture

### Dialect Interface

```java
package net.luis.utils.db.dialect;

import net.luis.utils.db.query.Query;
import net.luis.utils.db.mapping.TypeMapping;
import org.jspecify.annotations.NullMarked;
import java.util.List;
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

    // Type mapping
    TypeMapping getTypeMapping();

    // SQL generation
    SqlGenerator getSqlGenerator();

    // Feature support
    DialectFeatures getFeatures();

    // Identifier quoting
    String quoteIdentifier(String identifier);

    // Pagination
    String limitOffsetClause(int limit, int offset);

    // Locking
    String forUpdateClause();
    String forShareClause();
    Optional<String> skipLockedClause();

    // Default value expressions
    String nowFunction();
    String uuidFunction();

    // Sequence operations
    String nextValExpression(String sequenceName);
    String currentValExpression(String sequenceName);
}
```

### Dialect Features

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
 * @param supportsSkipLocked Whether SKIP LOCKED is supported
 * @param supportsPartialIndexes Whether partial indexes are supported
 * @param supportsGeneratedColumns Whether generated columns are supported
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
    boolean supportsSkipLocked,
    boolean supportsPartialIndexes,
    boolean supportsGeneratedColumns,
    int maxIdentifierLength
) {

    public static DialectFeaturesBuilder builder() {
        return new DialectFeaturesBuilder();
    }
}
```

## Type Mappings

### Type Mapping Interface

```java
package net.luis.utils.db.mapping;

import java.sql.Types;
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
```

### PostgreSQL Type Mapping

| Java Type | PostgreSQL Type | Notes |
|-----------|-----------------|-------|
| `boolean` / `Boolean` | `BOOLEAN` | |
| `byte` / `Byte` | `SMALLINT` | No TINYINT in PostgreSQL |
| `short` / `Short` | `SMALLINT` | |
| `int` / `Integer` | `INTEGER` | |
| `long` / `Long` | `BIGINT` | |
| `float` / `Float` | `REAL` | |
| `double` / `Double` | `DOUBLE PRECISION` | |
| `BigInteger` | `NUMERIC` | |
| `BigDecimal` | `NUMERIC(p,s)` | |
| `String` | `VARCHAR(n)` / `TEXT` | |
| `char` / `Character` | `CHAR(1)` | |
| `UUID` | `UUID` | Native type |
| `byte[]` | `BYTEA` | |
| `Instant` | `TIMESTAMP WITH TIME ZONE` | |
| `LocalDate` | `DATE` | |
| `LocalTime` | `TIME` | |
| `LocalDateTime` | `TIMESTAMP` | |
| `OffsetDateTime` | `TIMESTAMP WITH TIME ZONE` | |
| `ZonedDateTime` | `TIMESTAMP WITH TIME ZONE` | Zone stored separately |
| `Duration` | `INTERVAL` | |
| `Period` | `INTERVAL` | |
| `JsonElement` | `JSONB` | |
| `List<T>` | `T[]` | Native arrays |
| `Map<String, V>` | `JSONB` | |

### MySQL Type Mapping

| Java Type | MySQL Type | Notes |
|-----------|------------|-------|
| `boolean` / `Boolean` | `TINYINT(1)` | |
| `byte` / `Byte` | `TINYINT` | |
| `short` / `Short` | `SMALLINT` | |
| `int` / `Integer` | `INT` | |
| `long` / `Long` | `BIGINT` | |
| `float` / `Float` | `FLOAT` | |
| `double` / `Double` | `DOUBLE` | |
| `BigInteger` | `DECIMAL(65,0)` | |
| `BigDecimal` | `DECIMAL(p,s)` | |
| `String` | `VARCHAR(n)` / `TEXT` | |
| `UUID` | `CHAR(36)` / `BINARY(16)` | No native UUID |
| `byte[]` | `BLOB` | |
| `Instant` | `TIMESTAMP(6)` | |
| `LocalDate` | `DATE` | |
| `LocalTime` | `TIME(6)` | |
| `LocalDateTime` | `DATETIME(6)` | |
| `Duration` | `BIGINT` | Stored as milliseconds |
| `JsonElement` | `JSON` | |
| `List<T>` | `JSON` | No native arrays |

### SQLite Type Mapping

| Java Type | SQLite Type | Notes |
|-----------|-------------|-------|
| `boolean` / `Boolean` | `INTEGER` | 0/1 |
| All integers | `INTEGER` | |
| All decimals | `REAL` | |
| `String` | `TEXT` | |
| `UUID` | `TEXT` | |
| `byte[]` | `BLOB` | |
| All temporal | `TEXT` | ISO 8601 format |
| `JsonElement` | `TEXT` | |
| Collections | `TEXT` | JSON serialized |

## Dialect Implementations

### PostgreSQL Dialect

```java
package net.luis.utils.db.dialect.impl;

import net.luis.utils.db.dialect.Dialect;
import net.luis.utils.db.dialect.DialectFeatures;
import org.jspecify.annotations.NullMarked;

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
            .supportsSkipLocked(true)
            .supportsPartialIndexes(true)
            .supportsGeneratedColumns(true)
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
            if (sb.length() > 0) sb.append(" ");
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

### TimescaleDB Dialect

TimescaleDB extends PostgreSQL with time-series features:

```java
package net.luis.utils.db.dialect.impl;

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

### MySQL Dialect

```java
package net.luis.utils.db.dialect.impl;

/**
 * MySQL dialect implementation.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class MySqlDialect implements Dialect {

    public static final MySqlDialect INSTANCE = new MySqlDialect();

    @Override
    public String getId() {
        return "mysql";
    }

    @Override
    public DialectFeatures getFeatures() {
        return DialectFeatures.builder()
            .supportsArrays(false)
            .supportsJson(true)
            .supportsUuid(false)
            .supportsInterval(false)
            .supportsReturning(false)  // MySQL 8.0.21+ has limited support
            .supportsUpsert(true)      // ON DUPLICATE KEY UPDATE
            .supportsWindowFunctions(true)
            .supportsCte(true)
            .supportsSkipLocked(true)  // MySQL 8.0+
            .supportsPartialIndexes(false)
            .supportsGeneratedColumns(true)
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
    public String nowFunction() {
        return "NOW(6)";  // Microsecond precision
    }

    @Override
    public String uuidFunction() {
        return "UUID()";
    }

    @Override
    public String nextValExpression(String sequenceName) {
        // MySQL doesn't have sequences, use AUTO_INCREMENT
        throw new UnsupportedOperationException("MySQL doesn't support sequences");
    }
}
```

## SQL Generation

### SQL Generator Interface

```java
package net.luis.utils.db.dialect;

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
    String dropTable(String tableName);

    String createIndex(IndexDefinition index);
    String dropIndex(String indexName, String tableName);

    String createSequence(SequenceDefinition sequence);
    String dropSequence(String sequenceName);

    // DML generation
    String insert(String table, List<String> columns);
    String insertReturning(String table, List<String> columns, List<String> returning);
    String update(String table, List<String> columns, String whereClause);
    String delete(String table, String whereClause);
    String upsert(String table, List<String> columns, List<String> conflictColumns,
                  List<String> updateColumns);

    // Query generation
    String select(SelectBuilder builder);
}
```

### PostgreSQL SQL Generator

```java
package net.luis.utils.db.dialect.impl;

/**
 * SQL generator for PostgreSQL.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class PostgresSqlGenerator implements SqlGenerator {

    private final PostgresDialect dialect;

    public PostgresSqlGenerator(PostgresDialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String createTable(TableDefinition table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");

        if (table.schema() != null) {
            sb.append(dialect.quoteIdentifier(table.schema())).append(".");
        }
        sb.append(dialect.quoteIdentifier(table.name())).append(" (\n");

        // Columns
        List<String> columnDefs = table.columns().stream()
            .map(this::columnDefinition)
            .toList();
        sb.append("    ").append(String.join(",\n    ", columnDefs));

        // Primary key
        if (table.primaryKey() != null) {
            sb.append(",\n    PRIMARY KEY (");
            sb.append(table.primaryKey().columns().stream()
                .map(dialect::quoteIdentifier)
                .collect(Collectors.joining(", ")));
            sb.append(")");
        }

        // Foreign keys
        for (ForeignKeyDefinition fk : table.foreignKeys()) {
            sb.append(",\n    ").append(foreignKeyDefinition(fk));
        }

        // Check constraints
        for (CheckConstraint check : table.checkConstraints()) {
            sb.append(",\n    CONSTRAINT ")
              .append(dialect.quoteIdentifier(check.name()))
              .append(" CHECK (").append(check.expression()).append(")");
        }

        sb.append("\n)");

        return sb.toString();
    }

    @Override
    public String insertReturning(String table, List<String> columns, List<String> returning) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(dialect.quoteIdentifier(table));
        sb.append(" (");
        sb.append(columns.stream()
            .map(dialect::quoteIdentifier)
            .collect(Collectors.joining(", ")));
        sb.append(") VALUES (");
        sb.append(columns.stream()
            .map(c -> "?")
            .collect(Collectors.joining(", ")));
        sb.append(")");

        if (!returning.isEmpty()) {
            sb.append(" RETURNING ");
            sb.append(returning.stream()
                .map(dialect::quoteIdentifier)
                .collect(Collectors.joining(", ")));
        }

        return sb.toString();
    }

    @Override
    public String upsert(String table, List<String> columns, List<String> conflictColumns,
                         List<String> updateColumns) {
        StringBuilder sb = new StringBuilder();
        sb.append(insert(table, columns));
        sb.append(" ON CONFLICT (");
        sb.append(conflictColumns.stream()
            .map(dialect::quoteIdentifier)
            .collect(Collectors.joining(", ")));
        sb.append(") DO UPDATE SET ");
        sb.append(updateColumns.stream()
            .map(c -> dialect.quoteIdentifier(c) + " = EXCLUDED." + dialect.quoteIdentifier(c))
            .collect(Collectors.joining(", ")));
        return sb.toString();
    }

    private String columnDefinition(ColumnDefinition column) {
        StringBuilder sb = new StringBuilder();
        sb.append(dialect.quoteIdentifier(column.name()));
        sb.append(" ").append(column.sqlType());

        if (column.generated()) {
            switch (column.generationStrategy()) {
                case IDENTITY -> sb.append(" GENERATED ALWAYS AS IDENTITY");
                case SEQUENCE -> {
                    // Handled separately with sequence
                }
                case UUID -> sb.append(" DEFAULT ").append(dialect.uuidFunction());
            }
        } else if (column.defaultValue() != null) {
            sb.append(" DEFAULT ").append(column.defaultValue());
        }

        if (!column.nullable()) {
            sb.append(" NOT NULL");
        }

        if (column.unique()) {
            sb.append(" UNIQUE");
        }

        return sb.toString();
    }
}
```

## Dialect Registry

```java
package net.luis.utils.db.dialect;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NullMarked;

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
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            return SqliteDialect.INSTANCE;
        } else if (jdbcUrl.startsWith("jdbc:h2:")) {
            return H2Dialect.INSTANCE;
        }
        throw new IllegalArgumentException("Cannot detect dialect from JDBC URL: " + jdbcUrl);
    }
}
```

## Extending Dialects

### Custom Dialect Implementation

```java
// Custom dialect for a specific database
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

// Register via ServiceLoader (META-INF/services/net.luis.utils.db.dialect.Dialect)
// or programmatically
DialectRegistry.register(CockroachDialect.INSTANCE);
```

## Related Documents

- [00-overview.md](00-overview.md) - Architecture overview
- [01-yaml-schema.md](01-yaml-schema.md) - Type definitions in YAML
- [03-query-api.md](03-query-api.md) - Query builder design
