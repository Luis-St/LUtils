# Dialect System

## Overview

The dialect system handles differences between SQL databases. Each dialect generates appropriate SQL syntax and handles type mappings specific to that database.

## Supported Dialects

| Dialect | Database | Status |
|---------|----------|--------|
| `POSTGRESQL` | PostgreSQL 12+ | Primary |
| `MYSQL` | MySQL 8.0+ | Supported |
| `MARIADB` | MariaDB 10.5+ | Supported |
| `H2` | H2 (testing) | Supported |
| `SQLITE` | SQLite 3.35+ | Planned |
| `ORACLE` | Oracle 19c+ | Planned |
| `SQLSERVER` | SQL Server 2019+ | Planned |

## Dialect Configuration

```java
// Explicit dialect
DatabaseConfig.builder()
    .url("jdbc:postgresql://localhost:5432/myapp")
    .dialect(SqlDialect.POSTGRESQL)
    .build();

// Auto-detect from URL
DatabaseConfig.builder()
    .url("jdbc:postgresql://localhost:5432/myapp")
    // Dialect is inferred: POSTGRESQL
    .build();
```

## SqlDialect Interface

```java
public interface SqlDialect {

    // Dialect instances
    SqlDialect POSTGRESQL = new PostgreSqlDialect();
    SqlDialect MYSQL = new MySqlDialect();
    SqlDialect H2 = new H2Dialect();

    /**
     * Dialect identifier.
     */
    String getName();

    /**
     * Generates SELECT statement.
     */
    String generateSelect(SelectQuery<?> query);

    /**
     * Generates INSERT statement.
     */
    String generateInsert(InsertQuery<?> query);

    /**
     * Generates UPDATE statement.
     */
    String generateUpdate(UpdateQuery<?> query);

    /**
     * Generates DELETE statement.
     */
    String generateDelete(DeleteQuery<?> query);

    /**
     * Quotes an identifier (table/column name).
     */
    String quoteIdentifier(String identifier);

    /**
     * Returns the parameter placeholder for position n.
     */
    String parameterPlaceholder(int position);

    /**
     * Maps Java type to SQL type.
     */
    String getSqlType(Class<?> javaType);

    /**
     * Generates LIMIT/OFFSET clause.
     */
    String generateLimitOffset(Long limit, Long offset);

    /**
     * Whether this dialect supports RETURNING clause.
     */
    boolean supportsReturning();

    /**
     * Whether this dialect supports upsert (INSERT ON CONFLICT).
     */
    boolean supportsUpsert();

    /**
     * Generates upsert statement.
     */
    String generateUpsert(UpsertQuery<?> query);

    /**
     * Translates SQLException to DatabaseException.
     */
    DatabaseException translateException(SQLException e, String sql, List<Object> params);
}
```

## Dialect-Specific SQL Generation

### LIMIT/OFFSET

```java
// PostgreSQL / MySQL / H2
SELECT * FROM users LIMIT 10 OFFSET 20

// Oracle (pre-12c style)
SELECT * FROM (
    SELECT a.*, ROWNUM rnum FROM (
        SELECT * FROM users
    ) a WHERE ROWNUM <= 30
) WHERE rnum > 20

// SQL Server
SELECT * FROM users
ORDER BY id
OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY
```

### Identifier Quoting

```java
// PostgreSQL: double quotes
SELECT "user"."name" FROM "user"

// MySQL: backticks
SELECT `user`.`name` FROM `user`

// SQL Server: square brackets
SELECT [user].[name] FROM [user]
```

### Boolean Literals

```java
// PostgreSQL
WHERE active = true

// MySQL
WHERE active = 1

// Oracle
WHERE active = 1
```

### String Concatenation

```java
// PostgreSQL
first_name || ' ' || last_name

// MySQL
CONCAT(first_name, ' ', last_name)

// SQL Server
first_name + ' ' + last_name
```

## Dialect-Specific Features

### PostgreSQL-Specific

```java
public interface PostgreSqlFeatures {

    // ILIKE (case-insensitive LIKE)
    Condition<E> ilike(String pattern);

    // Array operations
    Condition<E> arrayContains(T element);
    Condition<E> arrayOverlaps(Collection<T> elements);

    // JSONB operations
    Condition<E> jsonContains(String path, Object value);
    Condition<E> jsonExists(String path);

    // Full-text search
    Condition<E> fullTextSearch(String query);
    Condition<E> fullTextSearch(String query, String language);

    // DISTINCT ON
    SelectBuilder<E> distinctOn(SqlColumn<E, ?>... columns);

    // Array aggregation
    AggregateExpression<List<T>> arrayAgg(SqlColumn<E, T> column);

    // Generate series
    static SqlTable<Long> generateSeries(long start, long end);
}

// Usage
List<User> users = UserTable.TABLE.select()
    .where(UserTable.EMAIL.ilike("%@GMAIL.COM"))  // PostgreSQL-specific
    .fetch();
```

### MySQL-Specific

```java
public interface MySqlFeatures {

    // REPLACE INTO
    User replaceInto(User entity);

    // INSERT IGNORE
    User insertIgnore(User entity);

    // ON DUPLICATE KEY UPDATE
    User insertOnDuplicateKeyUpdate(User entity, Consumer<Updater<User>> onDuplicate);

    // Full-text search with MATCH AGAINST
    Condition<E> matchAgainst(String searchTerms);
    Condition<E> matchAgainst(String searchTerms, MatchMode mode);

    // Index hints
    SelectBuilder<E> useIndex(String indexName);
    SelectBuilder<E> forceIndex(String indexName);
    SelectBuilder<E> ignoreIndex(String indexName);
}
```

## Type Mapping by Dialect

```java
public class PostgreSqlDialect implements SqlDialect {

    @Override
    public String getSqlType(Class<?> javaType) {
        if (javaType == UUID.class) return "uuid";
        if (javaType == String.class) return "text";
        if (javaType == Integer.class) return "integer";
        if (javaType == Long.class) return "bigint";
        if (javaType == BigDecimal.class) return "numeric";
        if (javaType == Boolean.class) return "boolean";
        if (javaType == Instant.class) return "timestamptz";
        if (javaType == LocalDate.class) return "date";
        if (javaType == LocalDateTime.class) return "timestamp";
        if (javaType == byte[].class) return "bytea";
        if (List.class.isAssignableFrom(javaType)) return "array";
        if (Map.class.isAssignableFrom(javaType)) return "jsonb";
        throw new UnsupportedTypeException(javaType);
    }
}

public class MySqlDialect implements SqlDialect {

    @Override
    public String getSqlType(Class<?> javaType) {
        if (javaType == UUID.class) return "CHAR(36)";
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == Integer.class) return "INT";
        if (javaType == Long.class) return "BIGINT";
        if (javaType == BigDecimal.class) return "DECIMAL(19,4)";
        if (javaType == Boolean.class) return "TINYINT(1)";
        if (javaType == Instant.class) return "DATETIME(6)";
        if (javaType == LocalDate.class) return "DATE";
        if (javaType == LocalDateTime.class) return "DATETIME";
        if (javaType == byte[].class) return "LONGBLOB";
        if (List.class.isAssignableFrom(javaType)) return "JSON";
        if (Map.class.isAssignableFrom(javaType)) return "JSON";
        throw new UnsupportedTypeException(javaType);
    }
}
```

## Feature Detection

```java
// Check dialect capabilities at runtime
SqlDialect dialect = Database.getDialect();

if (dialect.supportsReturning()) {
    User saved = UserTable.TABLE.insert(user)
        .returning()
        .fetchOne();
} else {
    UserTable.TABLE.insert(user).execute();
    User saved = UserTable.TABLE.select()
        .where(UserTable.EMAIL.equalTo(user.email()))
        .fetchOne();
}

// Or use capability-safe methods
User saved = UserTable.TABLE.insert(user).fetchInserted();
// Internally uses RETURNING if supported, or separate SELECT otherwise
```

## Dialect Registry

```java
public final class SqlDialects {

    private static final Map<String, SqlDialect> DIALECTS = new ConcurrentHashMap<>();

    static {
        register(new PostgreSqlDialect());
        register(new MySqlDialect());
        register(new MariaDbDialect());
        register(new H2Dialect());
    }

    public static void register(SqlDialect dialect) {
        DIALECTS.put(dialect.getName().toLowerCase(), dialect);
    }

    public static SqlDialect forName(String name) {
        SqlDialect dialect = DIALECTS.get(name.toLowerCase());
        if (dialect == null) {
            throw new UnknownDialectException(name);
        }
        return dialect;
    }

    public static SqlDialect fromJdbcUrl(String url) {
        if (url.startsWith("jdbc:postgresql:")) return POSTGRESQL;
        if (url.startsWith("jdbc:mysql:")) return MYSQL;
        if (url.startsWith("jdbc:mariadb:")) return MARIADB;
        if (url.startsWith("jdbc:h2:")) return H2;
        throw new UnknownDialectException("Cannot detect dialect from URL: " + url);
    }
}
```

## Custom Dialect

For unsupported databases or custom behavior:

```java
public class CustomDialect extends PostgreSqlDialect {

    @Override
    public String getName() {
        return "custom";
    }

    @Override
    public String generateLimitOffset(Long limit, Long offset) {
        // Custom LIMIT/OFFSET syntax
        return String.format("FETCH FIRST %d ROWS ONLY SKIP %d", limit, offset);
    }

    @Override
    public String getSqlType(Class<?> javaType) {
        if (javaType == Money.class) {
            return "DECIMAL(19,4)";  // Custom type mapping
        }
        return super.getSqlType(javaType);
    }
}

// Register and use
SqlDialects.register(new CustomDialect());

DatabaseConfig.builder()
    .url("jdbc:custom://...")
    .dialect(SqlDialects.forName("custom"))
    .build();
```

## Dialect Testing

```java
@ParameterizedTest
@EnumSource(SqlDialect.class)
void shouldGenerateValidSqlForAllDialects(SqlDialect dialect) {
    SelectQuery<User> query = UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
        .limit(10);

    String sql = query.toSql(dialect);

    // Verify SQL is valid for the dialect
    assertThat(sql).isNotEmpty();
    assertThat(sql).contains("SELECT");
    assertThat(sql).contains("FROM");
    assertThat(sql).contains("WHERE");
}
```

## Open Questions

- **Dialect negotiation**: Should dialect be auto-detected from database metadata after connection?
- **Version-specific features**: How to handle features available only in newer versions (e.g., PostgreSQL 15)?
- **Dialect extensions**: Should users be able to add methods to existing dialects?
- **Cross-dialect compatibility**: Should there be a "compatibility mode" that only uses common SQL?
- **Stored procedures**: How should dialect differences in stored procedure calls be handled?
