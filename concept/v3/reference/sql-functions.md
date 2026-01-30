# SQL Functions

## Overview

SQL functions are organized into typed, grouped classes for common operations:

1. **SqlString** - String manipulation functions (LOWER, UPPER, TRIM, etc.)
2. **SqlMath** - Numeric functions (ABS, ROUND, FLOOR, etc.)
3. **SqlDate** - Date/time functions (NOW, EXTRACT, DATE_TRUNC, etc.)
4. **SqlFunction** - General functions (COALESCE, CAST, NULLIF, etc.) and generic fallback

All functions return `SqlExpression<T>` which can be used in SELECT, WHERE, ORDER BY, and other clauses.

## SqlExpression Interface

```java
/**
 * Represents a SQL expression (function call, column, literal, etc.).<br>
 *
 * @param <T> The return type of the expression
 */
public interface SqlExpression<T> {

    /**
     * Returns the return type.<br>
     */
    Class<T> getReturnType();

    /**
     * Creates an alias for this expression in SELECT.<br>
     */
    SqlSelectExpression<T> as(String alias);

    // Condition building (for WHERE/HAVING)
    SqlCondition<?> equalTo(T value);
    SqlCondition<?> notEqualTo(T value);
    SqlCondition<?> greaterThan(T value);
    SqlCondition<?> lessThan(T value);
    SqlCondition<?> greaterThanOrEqual(T value);
    SqlCondition<?> lessThanOrEqual(T value);
    SqlCondition<?> between(T lower, T upper);
    SqlCondition<?> in(T... values);
    SqlCondition<?> in(Collection<T> values);
    SqlCondition<?> isNull();
    SqlCondition<?> isNotNull();
}
```

---

## SqlString - String Functions

```java
public final class SqlString {

    private SqlString() {}

    // === Case Conversion ===

    /** LOWER - converts to lowercase. */
    public static SqlExpression<String> lower(SqlColumn<?, String> column) { ... }

    /** UPPER - converts to uppercase. */
    public static SqlExpression<String> upper(SqlColumn<?, String> column) { ... }

    /** INITCAP - capitalizes first letter of each word. */
    public static SqlExpression<String> initCap(SqlColumn<?, String> column) { ... }

    // === Trimming ===

    /** TRIM - removes leading and trailing whitespace. */
    public static SqlExpression<String> trim(SqlColumn<?, String> column) { ... }

    /** LTRIM - removes leading whitespace. */
    public static SqlExpression<String> ltrim(SqlColumn<?, String> column) { ... }

    /** RTRIM - removes trailing whitespace. */
    public static SqlExpression<String> rtrim(SqlColumn<?, String> column) { ... }

    /** TRIM - removes specified characters from both ends. */
    public static SqlExpression<String> trim(SqlColumn<?, String> column, String characters) { ... }

    // === Length & Position ===

    /** LENGTH - returns string length. */
    public static SqlExpression<Integer> length(SqlColumn<?, String> column) { ... }

    /** CHAR_LENGTH - returns character length (alias for LENGTH). */
    public static SqlExpression<Integer> charLength(SqlColumn<?, String> column) { ... }

    /** OCTET_LENGTH - returns byte length. */
    public static SqlExpression<Integer> octetLength(SqlColumn<?, String> column) { ... }

    /** POSITION - returns position of substring (1-indexed, 0 if not found). */
    public static SqlExpression<Integer> position(String substring, SqlColumn<?, String> column) { ... }

    /** LOCATE - returns position of substring with optional start position. */
    public static SqlExpression<Integer> locate(String substring, SqlColumn<?, String> column) { ... }
    public static SqlExpression<Integer> locate(String substring, SqlColumn<?, String> column, int start) { ... }

    // === Substring & Extraction ===

    /** SUBSTRING - extracts substring from start position. */
    public static SqlExpression<String> substring(SqlColumn<?, String> column, int start) { ... }

    /** SUBSTRING - extracts substring from start position with length. */
    public static SqlExpression<String> substring(SqlColumn<?, String> column, int start, int length) { ... }

    /** LEFT - extracts leftmost characters. */
    public static SqlExpression<String> left(SqlColumn<?, String> column, int length) { ... }

    /** RIGHT - extracts rightmost characters. */
    public static SqlExpression<String> right(SqlColumn<?, String> column, int length) { ... }

    // === Concatenation ===

    /** CONCAT - concatenates strings. */
    public static SqlExpression<String> concat(Object... values) { ... }

    /** CONCAT_WS - concatenates with separator. */
    public static SqlExpression<String> concatWs(String separator, Object... values) { ... }

    // === Replacement ===

    /** REPLACE - replaces all occurrences. */
    public static SqlExpression<String> replace(SqlColumn<?, String> column, String from, String to) { ... }

    /** TRANSLATE - replaces characters (PostgreSQL). */
    public static SqlExpression<String> translate(SqlColumn<?, String> column, String from, String to) { ... }

    /** OVERLAY - replaces substring at position. */
    public static SqlExpression<String> overlay(SqlColumn<?, String> column, String replacement, int start, int length) { ... }

    // === Padding ===

    /** LPAD - left-pads to specified length. */
    public static SqlExpression<String> lpad(SqlColumn<?, String> column, int length, String padString) { ... }

    /** RPAD - right-pads to specified length. */
    public static SqlExpression<String> rpad(SqlColumn<?, String> column, int length, String padString) { ... }

    // === Other ===

    /** REVERSE - reverses the string. */
    public static SqlExpression<String> reverse(SqlColumn<?, String> column) { ... }

    /** REPEAT - repeats string n times. */
    public static SqlExpression<String> repeat(SqlColumn<?, String> column, int count) { ... }

    /** SPACE - returns string of n spaces. */
    public static SqlExpression<String> space(int count) { ... }

    /** ASCII - returns ASCII code of first character. */
    public static SqlExpression<Integer> ascii(SqlColumn<?, String> column) { ... }

    /** CHR/CHAR - returns character for ASCII code. */
    public static SqlExpression<String> chr(int code) { ... }

    /** FORMAT - formats string with arguments (PostgreSQL). */
    public static SqlExpression<String> format(String pattern, Object... args) { ... }

    /** MD5 - returns MD5 hash. */
    public static SqlExpression<String> md5(SqlColumn<?, String> column) { ... }

    /** ENCODE - encodes binary to text (base64, hex, escape). */
    public static SqlExpression<String> encode(SqlColumn<?, byte[]> column, String format) { ... }

    /** DECODE - decodes text to binary. */
    public static SqlExpression<byte[]> decode(SqlColumn<?, String> column, String format) { ... }
}
```

### SqlString Usage Examples

```java
// Case conversion
UserTable.TABLE.select()
    .where(SqlString.lower(UserTable.EMAIL).equalTo("test@example.com"))
    .fetch();

// In SELECT clause
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        SqlString.upper(UserTable.NAME).as("upper_name")
    )
    .fetch();

// Length check
UserTable.TABLE.select()
    .where(SqlString.length(UserTable.NAME).greaterThan(10))
    .fetch();

// Concatenation
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        SqlString.concat(UserTable.FIRST_NAME, " ", UserTable.LAST_NAME).as("full_name")
    )
    .fetch();

// Concatenation with separator
SqlString.concatWs(", ", UserTable.CITY, UserTable.STATE, UserTable.COUNTRY).as("location")

// Trimming in update
UserTable.TABLE.update()
    .set(UserTable.NAME, SqlString.trim(UserTable.NAME))
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Substring extraction
List<Row2<UUID, String>> results = UserTable.TABLE
    .select(
        UserTable.ID,
        SqlString.substring(UserTable.EMAIL, 1, 10).as("email_prefix")
    )
    .fetch();

// Find position
UserTable.TABLE.select()
    .where(SqlString.position("@", UserTable.EMAIL).greaterThan(0))
    .fetch();

// Padding
SqlString.lpad(UserTable.CODE, 10, "0").as("padded_code")

// Replace
UserTable.TABLE.update()
    .set(UserTable.DESCRIPTION, SqlString.replace(UserTable.DESCRIPTION, "old", "new"))
    .execute();
```

---

## SqlMath - Numeric Functions

```java
public final class SqlMath {

    private SqlMath() {}

    // === Basic Operations ===

    /** ABS - absolute value. */
    public static <T extends Number> SqlExpression<T> abs(SqlColumn<?, T> column) { ... }

    /** SIGN - returns -1, 0, or 1. */
    public static SqlExpression<Integer> sign(SqlColumn<?, ? extends Number> column) { ... }

    /** MOD - modulo/remainder. */
    public static <T extends Number> SqlExpression<T> mod(SqlColumn<?, T> column, T divisor) { ... }

    // === Rounding ===

    /** ROUND - rounds to nearest integer. */
    public static <T extends Number> SqlExpression<T> round(SqlColumn<?, T> column) { ... }

    /** ROUND - rounds to specified decimal places. */
    public static <T extends Number> SqlExpression<T> round(SqlColumn<?, T> column, int decimals) { ... }

    /** CEIL/CEILING - rounds up to nearest integer. */
    public static <T extends Number> SqlExpression<T> ceil(SqlColumn<?, T> column) { ... }

    /** FLOOR - rounds down to nearest integer. */
    public static <T extends Number> SqlExpression<T> floor(SqlColumn<?, T> column) { ... }

    /** TRUNC/TRUNCATE - truncates to specified decimal places. */
    public static <T extends Number> SqlExpression<T> trunc(SqlColumn<?, T> column) { ... }
    public static <T extends Number> SqlExpression<T> trunc(SqlColumn<?, T> column, int decimals) { ... }

    // === Powers & Roots ===

    /** POWER - raises to power. */
    public static SqlExpression<Double> power(SqlColumn<?, ? extends Number> column, double exponent) { ... }

    /** SQRT - square root. */
    public static SqlExpression<Double> sqrt(SqlColumn<?, ? extends Number> column) { ... }

    /** CBRT - cube root (PostgreSQL). */
    public static SqlExpression<Double> cbrt(SqlColumn<?, ? extends Number> column) { ... }

    /** EXP - e raised to power. */
    public static SqlExpression<Double> exp(SqlColumn<?, ? extends Number> column) { ... }

    // === Logarithms ===

    /** LN - natural logarithm. */
    public static SqlExpression<Double> ln(SqlColumn<?, ? extends Number> column) { ... }

    /** LOG - logarithm base 10. */
    public static SqlExpression<Double> log(SqlColumn<?, ? extends Number> column) { ... }

    /** LOG - logarithm with specified base. */
    public static SqlExpression<Double> log(double base, SqlColumn<?, ? extends Number> column) { ... }

    // === Trigonometric ===

    /** SIN - sine (radians). */
    public static SqlExpression<Double> sin(SqlColumn<?, ? extends Number> column) { ... }

    /** COS - cosine (radians). */
    public static SqlExpression<Double> cos(SqlColumn<?, ? extends Number> column) { ... }

    /** TAN - tangent (radians). */
    public static SqlExpression<Double> tan(SqlColumn<?, ? extends Number> column) { ... }

    /** COT - cotangent (radians). */
    public static SqlExpression<Double> cot(SqlColumn<?, ? extends Number> column) { ... }

    /** ASIN - arc sine. */
    public static SqlExpression<Double> asin(SqlColumn<?, ? extends Number> column) { ... }

    /** ACOS - arc cosine. */
    public static SqlExpression<Double> acos(SqlColumn<?, ? extends Number> column) { ... }

    /** ATAN - arc tangent. */
    public static SqlExpression<Double> atan(SqlColumn<?, ? extends Number> column) { ... }

    /** ATAN2 - arc tangent of y/x. */
    public static SqlExpression<Double> atan2(SqlColumn<?, ? extends Number> y, SqlColumn<?, ? extends Number> x) { ... }

    // === Angle Conversion ===

    /** DEGREES - radians to degrees. */
    public static SqlExpression<Double> degrees(SqlColumn<?, ? extends Number> column) { ... }

    /** RADIANS - degrees to radians. */
    public static SqlExpression<Double> radians(SqlColumn<?, ? extends Number> column) { ... }

    // === Constants & Random ===

    /** PI - returns π. */
    public static SqlExpression<Double> pi() { ... }

    /** RANDOM/RAND - returns random value between 0 and 1. */
    public static SqlExpression<Double> random() { ... }

    // === Bitwise Operations ===

    /** Bitwise AND. */
    public static SqlExpression<Long> bitAnd(SqlColumn<?, ? extends Number> column, long value) { ... }

    /** Bitwise OR. */
    public static SqlExpression<Long> bitOr(SqlColumn<?, ? extends Number> column, long value) { ... }

    /** Bitwise XOR. */
    public static SqlExpression<Long> bitXor(SqlColumn<?, ? extends Number> column, long value) { ... }

    /** Bitwise NOT. */
    public static SqlExpression<Long> bitNot(SqlColumn<?, ? extends Number> column) { ... }

    /** Bit shift left. */
    public static SqlExpression<Long> shiftLeft(SqlColumn<?, ? extends Number> column, int bits) { ... }

    /** Bit shift right. */
    public static SqlExpression<Long> shiftRight(SqlColumn<?, ? extends Number> column, int bits) { ... }
}
```

### SqlMath Usage Examples

```java
// Absolute value
OrderTable.TABLE.select()
    .where(SqlMath.abs(OrderTable.DISCOUNT).lessThan(new BigDecimal("50")))
    .fetch();

// Rounding
List<Row2<Order, BigDecimal>> results = OrderTable.TABLE
    .select(
        OrderTable.ALL_COLUMNS,
        SqlMath.round(OrderTable.TOTAL, 2).as("rounded_total")
    )
    .fetch();

// Ceiling/Floor
OrderTable.TABLE.select()
    .where(SqlMath.ceil(OrderTable.SHIPPING_COST).equalTo(new BigDecimal("10")))
    .fetch();

// Modulo for even/odd check
ProductTable.TABLE.select()
    .where(SqlMath.mod(ProductTable.QUANTITY, 2).equalTo(0))  // Even quantities
    .fetch();

// Power and sqrt
SqlMath.power(ProductTable.RATING, 2).as("rating_squared")
SqlMath.sqrt(ProductTable.VARIANCE).as("std_dev")

// Random ordering
UserTable.TABLE.select()
    .orderBy(SqlMath.random())
    .limit(10)
    .fetch();

// Logarithm
SqlMath.log(SensorTable.VALUE).as("log_value")
SqlMath.ln(SensorTable.VALUE).as("natural_log")

// Trigonometry
SqlMath.sin(SqlMath.radians(LocationTable.LATITUDE)).as("sin_lat")
```

---

## SqlDate - Date/Time Functions

```java
public final class SqlDate {

    private SqlDate() {}

    // === Current Date/Time ===

    /** NOW/CURRENT_TIMESTAMP - current timestamp. */
    public static SqlExpression<Instant> now() { ... }

    /** CURRENT_DATE - current date. */
    public static SqlExpression<LocalDate> currentDate() { ... }

    /** CURRENT_TIME - current time. */
    public static SqlExpression<LocalTime> currentTime() { ... }

    /** CURRENT_TIMESTAMP - current timestamp (alias for now). */
    public static SqlExpression<Instant> currentTimestamp() { ... }

    /** LOCALTIME - current time without timezone. */
    public static SqlExpression<LocalTime> localTime() { ... }

    /** LOCALTIMESTAMP - current timestamp without timezone. */
    public static SqlExpression<LocalDateTime> localTimestamp() { ... }

    // === Extraction ===

    /** EXTRACT - extracts date part. */
    public static SqlExpression<Integer> extract(DatePart part, SqlColumn<?, ?> column) { ... }

    /** YEAR - extracts year. */
    public static SqlExpression<Integer> year(SqlColumn<?, ?> column) { ... }

    /** MONTH - extracts month (1-12). */
    public static SqlExpression<Integer> month(SqlColumn<?, ?> column) { ... }

    /** DAY - extracts day of month. */
    public static SqlExpression<Integer> day(SqlColumn<?, ?> column) { ... }

    /** HOUR - extracts hour. */
    public static SqlExpression<Integer> hour(SqlColumn<?, ?> column) { ... }

    /** MINUTE - extracts minute. */
    public static SqlExpression<Integer> minute(SqlColumn<?, ?> column) { ... }

    /** SECOND - extracts second. */
    public static SqlExpression<Integer> second(SqlColumn<?, ?> column) { ... }

    /** MILLISECOND - extracts milliseconds. */
    public static SqlExpression<Integer> millisecond(SqlColumn<?, ?> column) { ... }

    /** DAY_OF_WEEK - extracts day of week (1=Sunday or 1=Monday depending on dialect). */
    public static SqlExpression<Integer> dayOfWeek(SqlColumn<?, ?> column) { ... }

    /** DAY_OF_YEAR - extracts day of year (1-366). */
    public static SqlExpression<Integer> dayOfYear(SqlColumn<?, ?> column) { ... }

    /** WEEK - extracts week of year. */
    public static SqlExpression<Integer> week(SqlColumn<?, ?> column) { ... }

    /** QUARTER - extracts quarter (1-4). */
    public static SqlExpression<Integer> quarter(SqlColumn<?, ?> column) { ... }

    /** EPOCH - extracts Unix timestamp. */
    public static SqlExpression<Long> epoch(SqlColumn<?, ?> column) { ... }

    // === Truncation ===

    /** DATE_TRUNC - truncates to specified precision (PostgreSQL). */
    public static SqlExpression<Instant> dateTrunc(String precision, SqlColumn<?, ?> column) { ... }

    /** DATE - extracts date part from timestamp. */
    public static SqlExpression<LocalDate> date(SqlColumn<?, ?> column) { ... }

    /** TIME - extracts time part from timestamp. */
    public static SqlExpression<LocalTime> time(SqlColumn<?, ?> column) { ... }

    // === Arithmetic ===

    /** DATE_ADD/DATEADD - adds interval to date. */
    public static SqlExpression<Instant> dateAdd(SqlColumn<?, ?> column, long amount, DatePart unit) { ... }

    /** DATE_SUB/DATESUB - subtracts interval from date. */
    public static SqlExpression<Instant> dateSub(SqlColumn<?, ?> column, long amount, DatePart unit) { ... }

    /** Adds days to date. */
    public static SqlExpression<Instant> addDays(SqlColumn<?, ?> column, int days) { ... }

    /** Adds months to date. */
    public static SqlExpression<Instant> addMonths(SqlColumn<?, ?> column, int months) { ... }

    /** Adds years to date. */
    public static SqlExpression<Instant> addYears(SqlColumn<?, ?> column, int years) { ... }

    /** Adds hours to timestamp. */
    public static SqlExpression<Instant> addHours(SqlColumn<?, ?> column, int hours) { ... }

    /** Adds minutes to timestamp. */
    public static SqlExpression<Instant> addMinutes(SqlColumn<?, ?> column, int minutes) { ... }

    /** Adds seconds to timestamp. */
    public static SqlExpression<Instant> addSeconds(SqlColumn<?, ?> column, int seconds) { ... }

    // === Difference ===

    /** DATEDIFF - difference between dates. */
    public static SqlExpression<Long> dateDiff(DatePart unit, SqlColumn<?, ?> start, SqlColumn<?, ?> end) { ... }

    /** AGE - interval between dates (PostgreSQL). */
    public static SqlExpression<String> age(SqlColumn<?, ?> column) { ... }
    public static SqlExpression<String> age(SqlColumn<?, ?> end, SqlColumn<?, ?> start) { ... }

    // === Construction ===

    /** MAKE_DATE - creates date from parts (PostgreSQL). */
    public static SqlExpression<LocalDate> makeDate(int year, int month, int day) { ... }

    /** MAKE_TIME - creates time from parts (PostgreSQL). */
    public static SqlExpression<LocalTime> makeTime(int hour, int minute, double second) { ... }

    /** MAKE_TIMESTAMP - creates timestamp from parts (PostgreSQL). */
    public static SqlExpression<Instant> makeTimestamp(int year, int month, int day, int hour, int minute, double second) { ... }

    // === Formatting ===

    /** TO_CHAR - formats date to string (PostgreSQL/Oracle). */
    public static SqlExpression<String> toChar(SqlColumn<?, ?> column, String format) { ... }

    /** DATE_FORMAT - formats date to string (MySQL). */
    public static SqlExpression<String> dateFormat(SqlColumn<?, ?> column, String format) { ... }

    /** STRFTIME - formats date to string (SQLite). */
    public static SqlExpression<String> strftime(String format, SqlColumn<?, ?> column) { ... }

    // === Parsing ===

    /** TO_DATE - parses string to date (PostgreSQL/Oracle). */
    public static SqlExpression<LocalDate> toDate(SqlColumn<?, String> column, String format) { ... }

    /** TO_TIMESTAMP - parses string to timestamp (PostgreSQL/Oracle). */
    public static SqlExpression<Instant> toTimestamp(SqlColumn<?, String> column, String format) { ... }

    /** STR_TO_DATE - parses string to date (MySQL). */
    public static SqlExpression<LocalDate> strToDate(SqlColumn<?, String> column, String format) { ... }

    // === Timezone ===

    /** AT TIME ZONE - converts timestamp to timezone. */
    public static SqlExpression<Instant> atTimeZone(SqlColumn<?, ?> column, String timezone) { ... }

    /** TIMEZONE - returns timezone of timestamp. */
    public static SqlExpression<String> timezone(SqlColumn<?, ?> column) { ... }

    // === Special ===

    /** LAST_DAY - returns last day of month. */
    public static SqlExpression<LocalDate> lastDay(SqlColumn<?, ?> column) { ... }

    /** NEXT_DAY - returns next occurrence of weekday (Oracle). */
    public static SqlExpression<LocalDate> nextDay(SqlColumn<?, ?> column, String weekday) { ... }

    /** MONTHS_BETWEEN - months between two dates (Oracle). */
    public static SqlExpression<Double> monthsBetween(SqlColumn<?, ?> date1, SqlColumn<?, ?> date2) { ... }
}

/** Date parts for EXTRACT and date arithmetic. */
public enum DatePart {
    YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND,
    WEEK, QUARTER, DAY_OF_WEEK, DAY_OF_YEAR, EPOCH
}
```

### SqlDate Usage Examples

```java
// Extract parts
List<Row2<Order, Integer>> results = OrderTable.TABLE
    .select(
        OrderTable.ALL_COLUMNS,
        SqlDate.year(OrderTable.CREATED_AT).as("year")
    )
    .fetch();

// Group by month
List<Row2<Integer, Long>> monthlyCounts = OrderTable.TABLE
    .select(SqlDate.month(OrderTable.CREATED_AT), SqlAgg.count())
    .groupBy(SqlDate.month(OrderTable.CREATED_AT))
    .fetch();

// Date truncation (PostgreSQL)
OrderTable.TABLE.select()
    .where(SqlDate.dateTrunc("day", OrderTable.CREATED_AT).equalTo(today))
    .fetch();

// Date arithmetic
UserTable.TABLE.select()
    .where(UserTable.SUBSCRIPTION_END.lessThan(SqlDate.addDays(SqlDate.now(), 7)))
    .fetch();  // Subscriptions expiring within 7 days

// Age calculation (PostgreSQL)
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        SqlDate.age(UserTable.BIRTH_DATE).as("age")
    )
    .fetch();

// Formatting
SqlDate.toChar(OrderTable.CREATED_AT, "YYYY-MM-DD").as("formatted_date")
SqlDate.dateFormat(OrderTable.CREATED_AT, "%Y-%m-%d").as("formatted_date")  // MySQL

// Filter by day of week (weekends)
OrderTable.TABLE.select()
    .where(SqlDate.dayOfWeek(OrderTable.CREATED_AT).in(1, 7))  // Sunday=1, Saturday=7
    .fetch();

// Date difference
SqlDate.dateDiff(DatePart.DAY, OrderTable.CREATED_AT, OrderTable.SHIPPED_AT).as("days_to_ship")

// Timezone conversion
SqlDate.atTimeZone(EventTable.START_TIME, "America/New_York").as("local_time")
```

---

## SqlFunction - General Functions & Fallback

```java
public final class SqlFunction {

    private SqlFunction() {}

    // === Null Handling ===

    /** COALESCE - returns first non-null value. */
    public static <T> SqlExpression<T> coalesce(SqlColumn<?, T> column, T defaultValue) { ... }

    /** COALESCE - returns first non-null from multiple values. */
    @SafeVarargs
    public static <T> SqlExpression<T> coalesce(Object... values) { ... }

    /** NULLIF - returns null if values are equal. */
    public static <T> SqlExpression<T> nullif(SqlColumn<?, T> column, T compareValue) { ... }

    /** IFNULL/NVL - returns second value if first is null. */
    public static <T> SqlExpression<T> ifNull(SqlColumn<?, T> column, T defaultValue) { ... }

    // === Type Conversion ===

    /** CAST - converts to specified type. */
    public static <T> SqlExpression<T> cast(Object value, Class<T> targetType) { ... }

    /** CAST - converts column to specified SQL type. */
    public static <T> SqlExpression<T> cast(SqlColumn<?, ?> column, String sqlType, Class<T> javaType) { ... }

    /** CONVERT - type conversion (SQL Server style). */
    public static <T> SqlExpression<T> convert(Object value, Class<T> targetType) { ... }

    // === Comparison ===

    /** GREATEST - returns largest value. */
    @SafeVarargs
    public static <T extends Comparable<T>> SqlExpression<T> greatest(Object... values) { ... }

    /** LEAST - returns smallest value. */
    @SafeVarargs
    public static <T extends Comparable<T>> SqlExpression<T> least(Object... values) { ... }

    // === Conditional ===

    /** CASE WHEN builder. */
    public static <T> SqlCaseBuilder<T> caseWhen(SqlCondition<?> condition, T result) { ... }

    /** CASE column WHEN value builder. */
    public static <T, R> SqlCaseBuilder<R> caseOf(SqlColumn<?, T> column) { ... }

    /** IIF - inline if (SQL Server). */
    public static <T> SqlExpression<T> iif(SqlCondition<?> condition, T trueValue, T falseValue) { ... }

    /** DECODE - Oracle-style case expression. */
    public static <T, R> SqlExpression<R> decode(SqlColumn<?, T> column, Object... whenThenPairs) { ... }

    // === Existence ===

    /** EXISTS - returns true if subquery has rows. */
    public static SqlExpression<Boolean> exists(SqlSubquery<?> subquery) { ... }

    // === Row Value ===

    /** ROW - constructs row value. */
    public static SqlExpression<Object[]> row(Object... values) { ... }

    // === Generic Fallback ===

    /**
     * Creates a generic function call for functions not covered by specific classes.<br>
     *
     * @param name The function name
     * @param args The function arguments (columns or values)
     * @return A SqlExpression representing the call
     */
    public static SqlExpression<Object> of(String name, Object... args) { ... }

    /**
     * Creates a typed generic function call.<br>
     *
     * @param name The function name
     * @param returnType The expected return type
     * @param args The function arguments
     * @return A typed SqlExpression
     */
    public static <T> SqlExpression<T> of(String name, Class<T> returnType, Object... args) { ... }
}

/** Builder for CASE WHEN expressions. */
public interface SqlCaseBuilder<T> {
    SqlCaseBuilder<T> when(SqlCondition<?> condition, T result);
    SqlExpression<T> otherwise(T defaultResult);
    SqlExpression<T> end();  // NULL for unmatched
}
```

### SqlFunction Usage Examples

```java
// Coalesce
UserTable.TABLE.select()
    .where(SqlFunction.coalesce(UserTable.NICKNAME, UserTable.NAME).equalTo("John"))
    .fetch();

// With default value
OrderTable.TABLE.select(
    OrderTable.ID,
    SqlFunction.coalesce(OrderTable.DISCOUNT, BigDecimal.ZERO).as("discount")
).fetch();

// Nullif
SqlFunction.nullif(UserTable.STATUS, UserStatus.UNKNOWN).as("status_or_null")

// Cast
SqlFunction.cast(UserTable.AGE, String.class).as("age_string")
SqlFunction.cast(OrderTable.TOTAL, "NUMERIC(10,2)", BigDecimal.class)

// Greatest/Least
SqlFunction.greatest(OrderTable.SUBTOTAL, OrderTable.MINIMUM_ORDER).as("final_subtotal")
SqlFunction.least(OrderTable.DISCOUNT, OrderTable.MAX_DISCOUNT).as("applied_discount")

// Case When
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        SqlFunction.caseWhen(UserTable.AGE.lessThan(18), "Minor")
            .when(UserTable.AGE.lessThan(65), "Adult")
            .otherwise("Senior")
            .as("age_group")
    )
    .fetch();

// Case Of (simple case)
SqlFunction.caseOf(UserTable.STATUS)
    .when(UserStatus.ACTIVE, "Active User")
    .when(UserStatus.PENDING, "Pending Approval")
    .otherwise("Inactive")
    .as("status_label")

// IIF (SQL Server style)
SqlFunction.iif(OrderTable.TOTAL.greaterThan(100), "Large", "Small").as("order_size")

// Generic fallback for uncommon functions
SqlFunction.of("CUSTOM_FUNCTION", String.class, UserTable.NAME, 42).as("custom_result")
```

---

## Dialect-Specific Functions via .dialect() API

For database-specific features, use the `.dialect()` API on tables and columns:

### PostgreSQL ILIKE

```java
// Via .dialect() on column
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.EMAIL.dialect(SqlDialect.POSTGRES).ilike("%@GMAIL.COM"))
    .fetch();

// Generated SQL:
// SELECT * FROM users WHERE email ILIKE ?
```

### PostgreSQL Array Operations

```java
// Array contains element
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.ROLES.dialect(SqlDialect.POSTGRES).arrayContains("admin"))
    .fetch();

// Generated SQL:
// SELECT * FROM users WHERE 'admin' = ANY(roles)

// Array overlaps
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.ROLES.dialect(SqlDialect.POSTGRES)
        .arrayOverlaps(List.of("admin", "moderator")))
    .fetch();

// Array aggregation in SELECT
List<Row2<UserStatus, List<String>>> results = UserTable.TABLE
    .dialect(SqlDialect.POSTGRES)
    .select(
        UserTable.STATUS,
        UserTable.EMAIL.dialect(SqlDialect.POSTGRES).arrayAgg().as("emails")
    )
    .groupBy(UserTable.STATUS)
    .fetch();
```

### PostgreSQL JSONB Operations

```java
// JSONB contains path
ProductTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(ProductTable.METADATA.dialect(SqlDialect.POSTGRES)
        .jsonExists("$.specs.weight"))
    .fetch();

// JSONB extract value
List<Row2<Product, String>> results = ProductTable.TABLE
    .dialect(SqlDialect.POSTGRES)
    .select(
        ProductTable.ALL_COLUMNS,
        ProductTable.METADATA.dialect(SqlDialect.POSTGRES)
            .jsonExtract("$.brand", String.class).as("brand")
    )
    .fetch();
```

### PostgreSQL Full-Text Search

```java
// Full-text search
ArticleTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(ArticleTable.CONTENT.dialect(SqlDialect.POSTGRES)
        .fullTextSearch("database & optimization"))
    .fetch();
```

### TimescaleDB Time Bucket

```java
// Time bucket aggregation
List<Row3<Instant, Double, Long>> hourlyStats = SensorTable.TABLE
    .dialect(SqlDialect.TIMESCALE)
    .select(
        SensorTable.RECORDED_AT.dialect(SqlDialect.TIMESCALE).timeBucket("1 hour"),
        SqlAgg.avg(SensorTable.VALUE),
        SqlAgg.count()
    )
    .where(SensorTable.RECORDED_AT.greaterThan(oneDayAgo))
    .groupBy(1)
    .orderBy(1)
    .fetch();

// First/last value per time bucket
List<Row3<Instant, Double, Double>> results = SensorTable.TABLE
    .dialect(SqlDialect.TIMESCALE)
    .select(
        SensorTable.RECORDED_AT.dialect(SqlDialect.TIMESCALE).timeBucket("1 hour"),
        SensorTable.VALUE.dialect(SqlDialect.TIMESCALE).first(SensorTable.RECORDED_AT),
        SensorTable.VALUE.dialect(SqlDialect.TIMESCALE).last(SensorTable.RECORDED_AT)
    )
    .groupBy(1)
    .fetch();

// Gap filling
List<Row2<Instant, Double>> filledData = SensorTable.TABLE
    .dialect(SqlDialect.TIMESCALE)
    .select(
        SensorTable.RECORDED_AT.dialect(SqlDialect.TIMESCALE)
            .timeBucketGapfill("1 hour", startTime, endTime),
        SensorTable.VALUE.dialect(SqlDialect.TIMESCALE).locf()  // Last observation carried forward
    )
    .where(SensorTable.SENSOR_ID.equalTo(sensorId))
    .groupBy(1)
    .fetch();
```

### MySQL Full-Text Search

```java
// MATCH AGAINST
ProductTable.TABLE.dialect(SqlDialect.MYSQL).select()
    .where(ProductTable.NAME.dialect(SqlDialect.MYSQL)
        .matchAgainst("wireless bluetooth"))
    .fetch();

// GROUP_CONCAT
List<Row2<UUID, String>> results = OrderTable.TABLE
    .dialect(SqlDialect.MYSQL)
    .select(
        OrderTable.CUSTOMER_ID,
        OrderTable.ORDER_NUMBER.dialect(SqlDialect.MYSQL)
            .groupConcat(", ").as("orders")
    )
    .groupBy(OrderTable.CUSTOMER_ID)
    .fetch();
```

## Using Functions in Different Contexts

### In WHERE Clause

```java
UserTable.TABLE.select()
    .where(SqlString.length(UserTable.NAME).greaterThan(5))
    .fetch();
```

### In SELECT Clause

```java
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        SqlString.upper(UserTable.NAME).as("upper_name")
    )
    .fetch();
```

### In ORDER BY

```java
UserTable.TABLE.select()
    .orderBy(SqlString.length(UserTable.NAME).asc())
    .fetch();
```

### In GROUP BY / HAVING

```java
List<Row2<Integer, Long>> results = UserTable.TABLE
    .select(
        SqlDate.year(UserTable.CREATED_AT).as("year"),
        SqlAgg.count()
    )
    .groupBy(SqlDate.year(UserTable.CREATED_AT))
    .having(SqlAgg.count().greaterThan(10))
    .fetch();
```

### In UPDATE SET

```java
UserTable.TABLE.update()
    .set(UserTable.NAME, SqlString.trim(UserTable.NAME))
    .set(UserTable.EMAIL, SqlString.lower(UserTable.EMAIL))
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
        SqlRaw.of("(SELECT COUNT(*) FROM orders WHERE orders.customer_id = users.id)").as("order_count")
    )
    .fetch();
```

## Related Documents

- [dialect-system.md](dialect-system.md) - Dialect architecture
- [dialect-comparison.md](dialect-comparison.md) - Feature availability per dialect
- [../core/03-query-api.md](../core/03-query-api.md) - Using functions in queries
