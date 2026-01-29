# SQL Functions

## Overview

SQL functions are supported through two mechanisms:

1. **Generic functions** - Work across all dialects using `Sql.function()`
2. **Dialect-specific functions** - Access database-specific features via `DialectFunctions`

## Generic Function Builder

For standard SQL functions that work across dialects:

```java
// Generic function call
UserTable.TABLE.select()
    .where(Sql.function("LOWER", UserTable.EMAIL).equalTo("test@example.com"))
    .fetch();

// Generated SQL: SELECT * FROM users WHERE LOWER(email) = ?

// With multiple arguments
OrderTable.TABLE.select()
    .where(Sql.function("COALESCE", OrderTable.DISCOUNT, 0).greaterThan(10))
    .fetch();

// In SELECT clause
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Sql.function("UPPER", UserTable.NAME).as("upper_name")
    )
    .fetch();
```

## SqlFunction Interface

```java
/**
 * Represents a SQL function call in a query.<br>
 *
 * @param <T> The return type of the function
 */
public interface SqlFunction<T> {

    /**
     * Returns the function name.<br>
     */
    String getName();

    /**
     * Returns the function arguments.<br>
     */
    List<Object> getArguments();

    /**
     * Returns the return type.<br>
     */
    Class<T> getReturnType();

    /**
     * Creates an alias for this function in SELECT.<br>
     */
    SelectExpression<T> as(String alias);

    // Condition building (for WHERE/HAVING)
    Condition<?> equalTo(T value);
    Condition<?> notEqualTo(T value);
    Condition<?> greaterThan(T value);
    Condition<?> lessThan(T value);
    Condition<?> greaterThanOrEqual(T value);
    Condition<?> lessThanOrEqual(T value);
    Condition<?> in(T... values);
    Condition<?> in(Collection<T> values);
    Condition<?> isNull();
    Condition<?> isNotNull();
}
```

## Sql Function Factory

```java
public final class Sql {

    private Sql() {}

    /**
     * Creates a generic function call.<br>
     *
     * @param name The function name
     * @param args The function arguments (columns or values)
     * @return A SqlFunction representing the call
     */
    public static SqlFunction<Object> function(String name, Object... args) {
        return new GenericSqlFunction<>(name, Object.class, args);
    }

    /**
     * Creates a typed function call.<br>
     *
     * @param name The function name
     * @param returnType The expected return type
     * @param args The function arguments
     * @return A typed SqlFunction
     */
    public static <T> SqlFunction<T> function(String name, Class<T> returnType, Object... args) {
        return new GenericSqlFunction<>(name, returnType, args);
    }

    // Common functions with proper typing

    /**
     * COALESCE - returns first non-null value.<br>
     */
    public static <T> SqlFunction<T> coalesce(SqlColumn<?, T> column, T defaultValue) {
        return new GenericSqlFunction<>("COALESCE", column.getType(), column, defaultValue);
    }

    /**
     * NULLIF - returns null if values are equal.<br>
     */
    public static <T> SqlFunction<T> nullif(SqlColumn<?, T> column, T compareValue) {
        return new GenericSqlFunction<>("NULLIF", column.getType(), column, compareValue);
    }

    /**
     * CAST - type conversion.<br>
     */
    public static <T> SqlFunction<T> cast(Object value, Class<T> targetType) {
        return new CastFunction<>(value, targetType);
    }

    /**
     * NOW - current timestamp.<br>
     */
    public static SqlFunction<Instant> now() {
        return new GenericSqlFunction<>("NOW", Instant.class);
    }

    /**
     * CURRENT_DATE - current date.<br>
     */
    public static SqlFunction<LocalDate> currentDate() {
        return new GenericSqlFunction<>("CURRENT_DATE", LocalDate.class);
    }

    /**
     * CURRENT_TIME - current time.<br>
     */
    public static SqlFunction<LocalTime> currentTime() {
        return new GenericSqlFunction<>("CURRENT_TIME", LocalTime.class);
    }
}
```

## String Functions

```java
// UPPER / LOWER
UserTable.TABLE.select()
    .where(Sql.function("LOWER", UserTable.EMAIL).equalTo("test@example.com"))
    .fetch();

// LENGTH
UserTable.TABLE.select()
    .where(Sql.function("LENGTH", UserTable.NAME, Integer.class).greaterThan(10))
    .fetch();

// CONCAT
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Sql.function("CONCAT", String.class, UserTable.FIRST_NAME, " ", UserTable.LAST_NAME)
            .as("full_name")
    )
    .fetch();

// TRIM / LTRIM / RTRIM
UserTable.TABLE.update()
    .set(UserTable.NAME, Sql.function("TRIM", UserTable.NAME))
    .where(UserTable.ID.equalTo(userId))
    .execute();

// SUBSTRING
List<Row2<UUID, String>> results = UserTable.TABLE
    .select(
        UserTable.ID,
        Sql.function("SUBSTRING", String.class, UserTable.EMAIL, 1, 10).as("email_prefix")
    )
    .fetch();

// REPLACE
UserTable.TABLE.update()
    .set(UserTable.DESCRIPTION, Sql.function("REPLACE", UserTable.DESCRIPTION, "old", "new"))
    .execute();
```

## Numeric Functions

```java
// ABS
OrderTable.TABLE.select()
    .where(Sql.function("ABS", OrderTable.DISCOUNT, BigDecimal.class).lessThan(new BigDecimal("50")))
    .fetch();

// ROUND
List<Row2<Order, BigDecimal>> results = OrderTable.TABLE
    .select(
        OrderTable.ALL_COLUMNS,
        Sql.function("ROUND", BigDecimal.class, OrderTable.TOTAL, 2).as("rounded_total")
    )
    .fetch();

// CEIL / FLOOR
OrderTable.TABLE.select()
    .where(Sql.function("CEIL", OrderTable.SHIPPING_COST, BigDecimal.class).equalTo(new BigDecimal("10")))
    .fetch();

// MOD
ProductTable.TABLE.select()
    .where(Sql.function("MOD", ProductTable.QUANTITY, Integer.class, 2).equalTo(0))  // Even quantities
    .fetch();
```

## Date/Time Functions

```java
// EXTRACT
List<Row2<Order, Integer>> results = OrderTable.TABLE
    .select(
        OrderTable.ALL_COLUMNS,
        Sql.function("EXTRACT", Integer.class, "YEAR FROM", OrderTable.CREATED_AT).as("year")
    )
    .fetch();

// DATE_TRUNC (PostgreSQL)
OrderTable.TABLE.select()
    .where(Sql.function("DATE_TRUNC", Instant.class, "day", OrderTable.CREATED_AT)
        .equalTo(today))
    .fetch();

// AGE (PostgreSQL)
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Sql.function("AGE", String.class, UserTable.BIRTH_DATE).as("age")
    )
    .fetch();
```

## Dialect-Specific Functions

For database-specific features, use `DialectFunctions`:

```java
/**
 * Factory for dialect-specific SQL functions.<br>
 */
public final class DialectFunctions {

    /**
     * Returns PostgreSQL-specific functions.<br>
     */
    public static PostgresFunctions postgres() {
        return PostgresFunctions.INSTANCE;
    }

    /**
     * Returns MySQL-specific functions.<br>
     */
    public static MySqlFunctions mysql() {
        return MySqlFunctions.INSTANCE;
    }

    /**
     * Returns SQLite-specific functions.<br>
     */
    public static SqliteFunctions sqlite() {
        return SqliteFunctions.INSTANCE;
    }

    /**
     * Returns TimescaleDB-specific functions.<br>
     */
    public static TimescaleFunctions timescale() {
        return TimescaleFunctions.INSTANCE;
    }
}
```

### PostgreSQL Functions

```java
public final class PostgresFunctions {

    public static final PostgresFunctions INSTANCE = new PostgresFunctions();

    /**
     * ILIKE - case-insensitive LIKE.<br>
     */
    public Condition<?> ilike(SqlColumn<?, String> column, String pattern) {
        return new IlikeCondition(column, pattern);
    }

    /**
     * Array contains element.<br>
     */
    public <T> Condition<?> arrayContains(SqlColumn<?, List<T>> column, T element) {
        return new ArrayContainsCondition<>(column, element);
    }

    /**
     * Array overlaps with collection.<br>
     */
    public <T> Condition<?> arrayOverlaps(SqlColumn<?, List<T>> column, Collection<T> elements) {
        return new ArrayOverlapsCondition<>(column, elements);
    }

    /**
     * JSONB contains path.<br>
     */
    public Condition<?> jsonContains(SqlColumn<?, ?> column, String path, Object value) {
        return new JsonContainsCondition(column, path, value);
    }

    /**
     * JSONB path exists.<br>
     */
    public Condition<?> jsonExists(SqlColumn<?, ?> column, String path) {
        return new JsonExistsCondition(column, path);
    }

    /**
     * Extract JSON value at path.<br>
     */
    public <T> SqlFunction<T> jsonExtract(SqlColumn<?, ?> column, String path, Class<T> type) {
        return new JsonExtractFunction<>(column, path, type);
    }

    /**
     * Full-text search.<br>
     */
    public Condition<?> fullTextSearch(SqlColumn<?, String> column, String query) {
        return new FullTextSearchCondition(column, query, null);
    }

    /**
     * Full-text search with language.<br>
     */
    public Condition<?> fullTextSearch(SqlColumn<?, String> column, String query, String language) {
        return new FullTextSearchCondition(column, query, language);
    }

    /**
     * Array aggregation.<br>
     */
    public <T> SqlFunction<List<T>> arrayAgg(SqlColumn<?, T> column) {
        return new ArrayAggFunction<>(column);
    }

    /**
     * String aggregation.<br>
     */
    public SqlFunction<String> stringAgg(SqlColumn<?, String> column, String delimiter) {
        return new StringAggFunction(column, delimiter);
    }

    /**
     * Generate series of numbers.<br>
     */
    public SqlFunction<Long> generateSeries(long start, long end) {
        return new GenerateSeriesFunction(start, end);
    }

    /**
     * Generate series of timestamps.<br>
     */
    public SqlFunction<Instant> generateSeries(Instant start, Instant end, String interval) {
        return new GenerateSeriesFunction(start, end, interval);
    }
}
```

Usage:
```java
// ILIKE
UserTable.TABLE.select()
    .where(DialectFunctions.postgres().ilike(UserTable.EMAIL, "%@GMAIL.COM"))
    .fetch();

// Array operations
UserTable.TABLE.select()
    .where(DialectFunctions.postgres().arrayContains(UserTable.ROLES, "admin"))
    .fetch();

// JSONB operations
ProductTable.TABLE.select()
    .where(DialectFunctions.postgres().jsonExists(ProductTable.METADATA, "$.specs.weight"))
    .fetch();

// Array aggregation
List<Row2<UserStatus, List<String>>> results = UserTable.TABLE
    .select(
        UserTable.STATUS,
        DialectFunctions.postgres().arrayAgg(UserTable.EMAIL).as("emails")
    )
    .groupBy(UserTable.STATUS)
    .fetch();
```

### TimescaleDB Functions

```java
public final class TimescaleFunctions {

    public static final TimescaleFunctions INSTANCE = new TimescaleFunctions();

    /**
     * time_bucket - aggregate by time interval.<br>
     *
     * @param interval The bucket interval (e.g., "1 hour", "5 minutes")
     * @param column The timestamp column
     * @return A SqlFunction representing time_bucket
     */
    public SqlFunction<Instant> timeBucket(String interval, SqlColumn<?, Instant> column) {
        return new TimeBucketFunction(interval, column);
    }

    /**
     * time_bucket with origin.<br>
     */
    public SqlFunction<Instant> timeBucket(String interval, SqlColumn<?, Instant> column, Instant origin) {
        return new TimeBucketFunction(interval, column, origin);
    }

    /**
     * first() - get first value in time order.<br>
     */
    public <T> SqlFunction<T> first(SqlColumn<?, T> column, SqlColumn<?, Instant> timeColumn) {
        return new FirstFunction<>(column, timeColumn);
    }

    /**
     * last() - get last value in time order.<br>
     */
    public <T> SqlFunction<T> last(SqlColumn<?, T> column, SqlColumn<?, Instant> timeColumn) {
        return new LastFunction<>(column, timeColumn);
    }

    /**
     * time_bucket_gapfill - fill gaps in time series.<br>
     */
    public SqlFunction<Instant> timeBucketGapfill(String interval, SqlColumn<?, Instant> column,
                                                    Instant start, Instant end) {
        return new TimeBucketGapfillFunction(interval, column, start, end);
    }

    /**
     * locf - last observation carried forward (for gap filling).<br>
     */
    public <T> SqlFunction<T> locf(SqlColumn<?, T> column) {
        return new LocfFunction<>(column);
    }

    /**
     * interpolate - linear interpolation (for gap filling).<br>
     */
    public SqlFunction<Double> interpolate(SqlColumn<?, ? extends Number> column) {
        return new InterpolateFunction(column);
    }
}
```

Usage:
```java
// Time bucket aggregation
List<Row3<Instant, Double, Long>> hourlyStats = SensorTable.TABLE
    .select(
        DialectFunctions.timescale().timeBucket("1 hour", SensorTable.RECORDED_AT).as("bucket"),
        Agg.avg(SensorTable.VALUE),
        Agg.count()
    )
    .where(SensorTable.RECORDED_AT.greaterThan(oneDayAgo))
    .groupBy(Sql.raw("1"))  // Group by bucket
    .orderBy(Sql.raw("1").asc())
    .fetch();

// First/last value per time bucket
List<Row3<Instant, Double, Double>> results = SensorTable.TABLE
    .select(
        DialectFunctions.timescale().timeBucket("1 hour", SensorTable.RECORDED_AT).as("bucket"),
        DialectFunctions.timescale().first(SensorTable.VALUE, SensorTable.RECORDED_AT).as("first_value"),
        DialectFunctions.timescale().last(SensorTable.VALUE, SensorTable.RECORDED_AT).as("last_value")
    )
    .groupBy(Sql.raw("1"))
    .fetch();

// Gap filling
List<Row2<Instant, Double>> filledData = SensorTable.TABLE
    .select(
        DialectFunctions.timescale().timeBucketGapfill("1 hour", SensorTable.RECORDED_AT,
            startTime, endTime).as("bucket"),
        DialectFunctions.timescale().locf(SensorTable.VALUE).as("value")
    )
    .where(SensorTable.SENSOR_ID.equalTo(sensorId))
    .groupBy(Sql.raw("1"))
    .fetch();
```

### MySQL Functions

```java
public final class MySqlFunctions {

    public static final MySqlFunctions INSTANCE = new MySqlFunctions();

    /**
     * MATCH AGAINST - full-text search.<br>
     */
    public Condition<?> matchAgainst(List<SqlColumn<?, String>> columns, String searchTerms) {
        return new MatchAgainstCondition(columns, searchTerms, MatchMode.NATURAL);
    }

    /**
     * MATCH AGAINST with mode.<br>
     */
    public Condition<?> matchAgainst(List<SqlColumn<?, String>> columns, String searchTerms, MatchMode mode) {
        return new MatchAgainstCondition(columns, searchTerms, mode);
    }

    /**
     * JSON_EXTRACT.<br>
     */
    public <T> SqlFunction<T> jsonExtract(SqlColumn<?, ?> column, String path, Class<T> type) {
        return new JsonExtractFunction<>(column, path, type);
    }

    /**
     * JSON_CONTAINS.<br>
     */
    public Condition<?> jsonContains(SqlColumn<?, ?> column, Object value, String path) {
        return new JsonContainsCondition(column, value, path);
    }

    /**
     * GROUP_CONCAT.<br>
     */
    public SqlFunction<String> groupConcat(SqlColumn<?, String> column, String separator) {
        return new GroupConcatFunction(column, separator);
    }

    public enum MatchMode {
        NATURAL,
        BOOLEAN,
        NATURAL_WITH_QUERY_EXPANSION
    }
}
```

Usage:
```java
// Full-text search
ProductTable.TABLE.select()
    .where(DialectFunctions.mysql().matchAgainst(
        List.of(ProductTable.NAME, ProductTable.DESCRIPTION),
        "wireless bluetooth"
    ))
    .fetch();

// GROUP_CONCAT
List<Row2<UUID, String>> results = OrderTable.TABLE
    .select(
        OrderTable.CUSTOMER_ID,
        DialectFunctions.mysql().groupConcat(OrderTable.ORDER_NUMBER, ", ").as("orders")
    )
    .groupBy(OrderTable.CUSTOMER_ID)
    .fetch();
```

## Using Functions in Different Contexts

### In WHERE Clause

```java
UserTable.TABLE.select()
    .where(Sql.function("LENGTH", UserTable.NAME, Integer.class).greaterThan(5))
    .fetch();
```

### In SELECT Clause

```java
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Sql.function("UPPER", UserTable.NAME).as("upper_name")
    )
    .fetch();
```

### In ORDER BY

```java
UserTable.TABLE.select()
    .orderBy(Sql.function("LENGTH", UserTable.NAME).asc())
    .fetch();
```

### In GROUP BY / HAVING

```java
List<Row2<Integer, Long>> results = UserTable.TABLE
    .select(
        Sql.function("EXTRACT", Integer.class, "YEAR FROM", UserTable.CREATED_AT).as("year"),
        Agg.count()
    )
    .groupBy(Sql.function("EXTRACT", Integer.class, "YEAR FROM", UserTable.CREATED_AT))
    .having(Agg.count().greaterThan(10))
    .fetch();
```

### In UPDATE SET

```java
UserTable.TABLE.update()
    .set(UserTable.NAME, Sql.function("TRIM", UserTable.NAME))
    .set(UserTable.EMAIL, Sql.function("LOWER", UserTable.EMAIL))
    .where(UserTable.ID.equalTo(userId))
    .execute();
```

## Raw SQL Escape Hatch

For complex expressions not covered by the function API:

```java
// Raw expression in WHERE
UserTable.TABLE.select()
    .whereRaw("email ~ ? AND created_at > ?", pattern, timestamp)
    .fetch();

// Raw expression in SELECT
List<Row2<User, Object>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Sql.raw("(SELECT COUNT(*) FROM orders WHERE orders.user_id = users.id)").as("order_count")
    )
    .fetch();
```

## Related Documents

- [dialect-system.md](dialect-system.md) - Dialect architecture
- [dialect-comparison.md](dialect-comparison.md) - Feature availability per dialect
- [../core/03-query-api.md](../core/03-query-api.md) - Using functions in queries
