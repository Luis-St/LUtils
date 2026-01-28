# Type-Safe Query API

## Overview

This document defines a SQL-like, column-reference based query API. Queries are built using static column references and method chaining, providing a familiar SQL structure while maintaining full type safety.

## Core Design Principles

1. **Column references as first-class objects** - Columns are typed objects with condition methods
2. **Consistent method chaining** - All query operations use the same builder pattern
3. **Table-centric operations** - Operations are performed on table objects, not repositories
4. **SQL-like structure** - The API mirrors SQL syntax for familiarity
5. **Explicit logical operators** - AND/OR/NOT are explicit method calls, not implicit chaining
6. **Optional WHERE clause** - `.where()` is always a separate chained method, never embedded in `select()`

## Generated Table Structure

```java
// Generated from YAML schema
public final class UserTable {

    private UserTable() {}

    public static final SqlTable<User> TABLE = SqlTable.of("users", User.class);

    // Simple columns: SqlColumn<Entity, JavaType>
    public static final SqlColumn<User, UUID> ID = TABLE.column("id", UUID.class);
    public static final SqlColumn<User, String> NAME = TABLE.column("name", String.class);
    public static final SqlColumn<User, String> EMAIL = TABLE.column("email", String.class);
    public static final SqlColumn<User, Integer> AGE = TABLE.column("age", Integer.class);
    public static final SqlColumn<User, UserStatus> STATUS = TABLE.column("status", UserStatus.class);
    public static final SqlColumn<User, Instant> CREATED_AT = TABLE.column("created_at", Instant.class);
    public static final SqlColumn<User, Instant> LAST_LOGIN = TABLE.column("last_login", Instant.class);

    // Foreign key columns: SqlColumn<Entity, JavaType, ReferenceKeyType>
    public static final SqlForeignColumn<User, Address, UUID> ADDRESS =
        TABLE.foreignColumn("address_id", Address.class, UUID.class, AddressTable.TABLE);

    // All columns for SELECT *
    public static final List<SqlColumn<User, ?>> ALL_COLUMNS = List.of(
        ID, NAME, EMAIL, AGE, STATUS, CREATED_AT, LAST_LOGIN, ADDRESS
    );
}
```

## Column Interface Hierarchy

```
                    ┌──────────────────────┐
                    │   SqlColumn<E, T>    │  Base column interface
                    └──────────┬───────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         │                     │                     │
┌────────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
│ SqlStringColumn │  │ SqlNumericColumn│  │SqlTemporalColumn│
└─────────────────┘  └─────────────────┘  └─────────────────┘
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               │
                    ┌──────────▼───────────┐
                    │  SqlForeignColumn    │  For relationships
                    │    <E, R, K>         │
                    └──────────────────────┘
```

## Condition Building

### Base Column Conditions

All columns support these conditions:

```java
public interface SqlColumn<E, T> {

    // Equality
    Condition<E> equalTo(T value);
    Condition<E> notEqualTo(T value);

    // Null checks
    Condition<E> isNull();
    Condition<E> isNotNull();

    // IN clauses
    Condition<E> in(T... values);
    Condition<E> in(Collection<T> values);
    Condition<E> notIn(T... values);
    Condition<E> notIn(Collection<T> values);

    // Subquery conditions
    Condition<E> in(Subquery<T> subquery);
    Condition<E> notIn(Subquery<T> subquery);
    Condition<E> equalTo(Subquery<T> subquery);

    // Column comparison
    Condition<E> equalTo(SqlColumn<?, T> other);
    Condition<E> notEqualTo(SqlColumn<?, T> other);
}
```

### Comparable Column Conditions

For numeric, temporal, and other comparable types:

```java
public interface SqlComparableColumn<E, T extends Comparable<T>> extends SqlColumn<E, T> {

    // Comparison
    Condition<E> greaterThan(T value);
    Condition<E> greaterThanOrEqual(T value);
    Condition<E> lessThan(T value);
    Condition<E> lessThanOrEqual(T value);
    Condition<E> between(T min, T max);
    Condition<E> notBetween(T min, T max);

    // Column comparison
    Condition<E> greaterThan(SqlColumn<?, T> other);
    Condition<E> lessThan(SqlColumn<?, T> other);
}
```

### String Column Conditions

```java
public interface SqlStringColumn<E> extends SqlColumn<E, String> {

    // Pattern matching
    Condition<E> like(String pattern);
    Condition<E> notLike(String pattern);
    Condition<E> ilike(String pattern);        // Case-insensitive (PostgreSQL)
    Condition<E> notIlike(String pattern);

    // Convenience patterns
    Condition<E> startsWith(String prefix);    // LIKE 'prefix%'
    Condition<E> endsWith(String suffix);      // LIKE '%suffix'
    Condition<E> contains(String substring);   // LIKE '%substring%'

    // Case-insensitive equality
    Condition<E> equalToIgnoreCase(String value);

    // Regex (dialect-dependent)
    Condition<E> matches(String regex);

    // Length conditions
    Condition<E> lengthEquals(int length);
    Condition<E> lengthGreaterThan(int length);
    Condition<E> lengthLessThan(int length);
    Condition<E> lengthBetween(int min, int max);
    Condition<E> isEmpty();
    Condition<E> isNotEmpty();
}
```

### Temporal Column Conditions

```java
public interface SqlTemporalColumn<E, T> extends SqlComparableColumn<E, T> {

    // Relative time
    Condition<E> withinLast(Duration duration);
    Condition<E> olderThan(Duration duration);
    Condition<E> withinNext(Duration duration);  // For future dates

    // Date part extraction
    Condition<E> yearEquals(int year);
    Condition<E> monthEquals(int month);
    Condition<E> dayEquals(int day);
    Condition<E> hourEquals(int hour);

    // Date truncation comparison
    Condition<E> dateEquals(LocalDate date);     // Truncate to date and compare
}
```

### Collection Column Conditions

```java
public interface SqlCollectionColumn<E, T> extends SqlColumn<E, List<T>> {

    // Size conditions
    Condition<E> sizeEquals(int size);
    Condition<E> sizeGreaterThan(int size);
    Condition<E> sizeLessThan(int size);
    Condition<E> isEmpty();
    Condition<E> isNotEmpty();

    // Contains
    Condition<E> contains(T element);
    Condition<E> containsAll(Collection<T> elements);
    Condition<E> containsAny(Collection<T> elements);
    Condition<E> overlaps(Collection<T> elements);  // PostgreSQL array overlap
}
```

## Logical Operators

### Condition Composition

```java
public interface Condition<E> {

    // Combine conditions
    Condition<E> and(Condition<E> other);
    Condition<E> or(Condition<E> other);
    Condition<E> not();

    // SQL generation
    String toSql(SqlDialect dialect);
    List<Object> getParameters();
}

// Query builder provides static factory methods
public interface QueryBuilder<E> {

    // Combine multiple conditions
    Condition<E> and(Condition<E>... conditions);
    Condition<E> and(Collection<Condition<E>> conditions);
    Condition<E> or(Condition<E>... conditions);
    Condition<E> or(Collection<Condition<E>> conditions);
    Condition<E> not(Condition<E> condition);

    // Always true/false
    Condition<E> alwaysTrue();
    Condition<E> alwaysFalse();
}
```

### Usage Examples

```java
// Simple AND
UserTable.TABLE.select()
    .where(UserTable.AGE.greaterThan(18)
        .and(UserTable.STATUS.equalTo(UserStatus.ACTIVE)))
    .fetch();

// Simple OR
UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE)
        .or(UserTable.STATUS.equalTo(UserStatus.PENDING)))
    .fetch();

// Complex nested conditions
// WHERE (age > 18 AND status = 'ACTIVE') OR (email LIKE '%@admin.com' AND NOT status = 'BANNED')
UserTable.TABLE.select()
    .where(
        UserTable.AGE.greaterThan(18)
            .and(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
        .or(
            UserTable.EMAIL.endsWith("@admin.com")
                .and(UserTable.STATUS.notEqualTo(UserStatus.BANNED))
        )
    )
    .fetch();

// NOT condition
UserTable.TABLE.select()
    .where(UserTable.STATUS.in(UserStatus.BANNED, UserStatus.DELETED).not())
    .fetch();

// Using static helper for complex AND/OR
UserTable.TABLE.select()
    .where(Condition.and(
        UserTable.AGE.greaterThan(18),
        UserTable.STATUS.equalTo(UserStatus.ACTIVE),
        UserTable.EMAIL.isNotNull()
    ))
    .fetch();

UserTable.TABLE.select()
    .where(Condition.or(
        UserTable.STATUS.equalTo(UserStatus.ACTIVE),
        UserTable.STATUS.equalTo(UserStatus.PENDING),
        UserTable.EMAIL.endsWith("@vip.com")
    ))
    .fetch();
```

## SELECT Queries

### Basic Select

```java
// Select all columns, all rows
List<User> users = UserTable.TABLE.select().fetch();

// Select with condition
List<User> activeUsers = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Method chaining for modifiers
List<User> users = UserTable.TABLE.select()
    .where(UserTable.AGE.greaterThan(18))
    .distinct()
    .orderBy(UserTable.CREATED_AT.desc())
    .limit(10)
    .offset(20)
    .fetch();
```

### Column Selection (Projections)

```java
// Select specific columns - returns tuples
List<Tuple2<UUID, String>> results = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL)
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Select into projection interface
public interface UserSummary {
    UUID id();
    String email();
    int age();
}

List<UserSummary> summaries = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL, UserTable.AGE)
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetchAs(UserSummary.class);

// Select single column
List<String> emails = UserTable.TABLE
    .select(UserTable.EMAIL)
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();
```

### Ordering

```java
// Single column
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .orderBy(UserTable.CREATED_AT.desc())
    .fetch();

// Multiple columns
List<User> users = UserTable.TABLE.select()
    .orderBy(UserTable.STATUS.asc(), UserTable.CREATED_AT.desc())
    .fetch();

// Null ordering
List<User> users = UserTable.TABLE.select()
    .orderBy(UserTable.LAST_LOGIN.desc().nullsLast())
    .fetch();

// Order direction interface
public interface SqlColumn<E, T> {
    OrderSpec<E> asc();
    OrderSpec<E> desc();
}

public interface OrderSpec<E> {
    OrderSpec<E> nullsFirst();
    OrderSpec<E> nullsLast();
}
```

### Pagination

```java
// Basic pagination
List<User> page = UserTable.TABLE.select()
    .orderBy(UserTable.CREATED_AT.desc())
    .limit(20)
    .offset(40)  // Page 3
    .fetch();

// Cursor-based pagination (more efficient for large datasets)
List<User> nextPage = UserTable.TABLE.select()
    .where(UserTable.CREATED_AT.lessThan(lastSeenCreatedAt))
    .orderBy(UserTable.CREATED_AT.desc())
    .limit(20)
    .fetch();

// Page helper
Page<User> page = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .orderBy(UserTable.CREATED_AT.desc())
    .fetchPage(pageNumber, pageSize);

public record Page<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {}
```

### Terminal Operations

```java
public interface SelectBuilder<E> {

    // Fetch all results
    List<E> fetch();

    // Fetch as stream (for large result sets)
    Stream<E> stream();

    // Fetch single result
    Optional<E> fetchFirst();
    E fetchOne() throws EntityNotFoundException;
    E fetchOneOrNull();

    // Aggregates
    long count();
    boolean exists();

    // Pagination
    Page<E> fetchPage(int pageNumber, int pageSize);
}
```

## Aggregate Functions

### Basic Aggregates

```java
// COUNT - without condition
long count = UserTable.TABLE.select().count();

// COUNT - with condition
long activeCount = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .count();

// COUNT DISTINCT
long uniqueAges = UserTable.TABLE.select(UserTable.AGE)
    .distinct()
    .count();

// SUM
Long totalAge = UserTable.TABLE.select(Agg.sum(UserTable.AGE)).fetchOne();

Long totalActiveAge = UserTable.TABLE.select(Agg.sum(UserTable.AGE))
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetchOne();

// AVG
Double averageAge = UserTable.TABLE.select(Agg.avg(UserTable.AGE)).fetchOne();

// MIN / MAX
Integer minAge = UserTable.TABLE.select(Agg.min(UserTable.AGE)).fetchOne();
Integer maxAge = UserTable.TABLE.select(Agg.max(UserTable.AGE)).fetchOne();
Instant firstCreated = UserTable.TABLE.select(Agg.min(UserTable.CREATED_AT)).fetchOne();
Instant lastCreated = UserTable.TABLE.select(Agg.max(UserTable.CREATED_AT)).fetchOne();
```

### Aggregate Interface

```java
public interface SelectBuilder<E> {

    // Terminal aggregate operations
    long count();
    boolean exists();

    // ... other terminal operations
}
```

## GROUP BY and HAVING

### Group By

```java
// Group users by status, count each group
List<Tuple2<UserStatus, Long>> statusCounts = UserTable.TABLE
    .select(UserTable.STATUS, Agg.count())
    .groupBy(UserTable.STATUS)
    .fetch();

// Group by multiple columns
List<Tuple3<UserStatus, Integer, Long>> results = UserTable.TABLE
    .select(UserTable.STATUS, UserTable.AGE, Agg.count())
    .groupBy(UserTable.STATUS, UserTable.AGE)
    .fetch();

// Group with aggregates
List<Tuple3<UserStatus, Double, Integer>> results = UserTable.TABLE
    .select(UserTable.STATUS, Agg.avg(UserTable.AGE), Agg.max(UserTable.AGE))
    .groupBy(UserTable.STATUS)
    .fetch();
```

### HAVING Clause

```java
// Filter groups with HAVING
List<Tuple2<UserStatus, Long>> popularStatuses = UserTable.TABLE
    .select(UserTable.STATUS, Agg.count())
    .groupBy(UserTable.STATUS)
    .having(Agg.count().greaterThan(10))
    .fetch();

// Complex HAVING
List<Tuple3<UserStatus, Double, Long>> results = UserTable.TABLE
    .select(UserTable.STATUS, Agg.avg(UserTable.AGE), Agg.count())
    .groupBy(UserTable.STATUS)
    .having(Agg.count().greaterThan(5)
        .and(Agg.avg(UserTable.AGE).greaterThan(25.0)))
    .fetch();

// WHERE + GROUP BY + HAVING
List<Tuple2<UserStatus, Long>> results = UserTable.TABLE
    .select(UserTable.STATUS, Agg.count())
    .where(UserTable.AGE.greaterThan(18))
    .groupBy(UserTable.STATUS)
    .having(Agg.count().greaterThan(10))
    .orderBy(Agg.count().desc())
    .fetch();
```

### Aggregate Functions Class

```java
public final class Agg {

    private Agg() {}

    // Count
    public static AggregateExpression<Long> count() {
        return new CountExpression();
    }

    public static <E, T> AggregateExpression<Long> count(SqlColumn<E, T> column) {
        return new CountColumnExpression<>(column);
    }

    public static <E, T> AggregateExpression<Long> countDistinct(SqlColumn<E, T> column) {
        return new CountDistinctExpression<>(column);
    }

    // Sum
    public static <E, T extends Number> AggregateExpression<T> sum(SqlColumn<E, T> column) {
        return new SumExpression<>(column);
    }

    // Average
    public static <E, T extends Number> AggregateExpression<Double> avg(SqlColumn<E, T> column) {
        return new AvgExpression<>(column);
    }

    // Min/Max
    public static <E, T extends Comparable<T>> AggregateExpression<T> min(SqlColumn<E, T> column) {
        return new MinExpression<>(column);
    }

    public static <E, T extends Comparable<T>> AggregateExpression<T> max(SqlColumn<E, T> column) {
        return new MaxExpression<>(column);
    }

    // String aggregates
    public static <E> AggregateExpression<String> stringAgg(SqlColumn<E, String> column, String delimiter) {
        return new StringAggExpression<>(column, delimiter);
    }

    // Array aggregates (PostgreSQL)
    public static <E, T> AggregateExpression<List<T>> arrayAgg(SqlColumn<E, T> column) {
        return new ArrayAggExpression<>(column);
    }
}

public interface AggregateExpression<T> {

    // For HAVING conditions
    Condition<?> greaterThan(T value);
    Condition<?> lessThan(T value);
    Condition<?> equalTo(T value);
    Condition<?> between(T min, T max);

    // For ORDER BY
    OrderSpec<?> asc();
    OrderSpec<?> desc();

    // SQL generation
    String toSql(SqlDialect dialect);
}
```

## JOIN Operations

### Join Types

```java
// Inner join
List<Tuple2<User, Address>> results = UserTable.TABLE
    .innerJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS.equalTo(AddressTable.ID))
    .select()
    .fetch();

// Left join
List<Tuple2<User, Optional<Address>>> results = UserTable.TABLE
    .leftJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS.equalTo(AddressTable.ID))
    .select()
    .fetch();

// Right join
List<Tuple2<Optional<User>, Address>> results = UserTable.TABLE
    .rightJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS.equalTo(AddressTable.ID))
    .select()
    .fetch();

// Cross join
List<Tuple2<User, Role>> results = UserTable.TABLE
    .crossJoin(RoleTable.TABLE)
    .select()
    .fetch();
```

### Join Conditions

```java
// Simple foreign key join
List<Tuple2<User, Address>> results = UserTable.TABLE
    .innerJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS)  // Uses foreign key relationship
    .select()
    .fetch();

// Complex join condition
List<Tuple2<Order, Product>> results = OrderTable.TABLE
    .innerJoin(ProductTable.TABLE)
    .on(join -> join.and(
        OrderTable.PRODUCT_ID.equalTo(ProductTable.ID),
        ProductTable.STATUS.equalTo(ProductStatus.ACTIVE)
    ))
    .select()
    .fetch();

// Multiple joins
List<Tuple3<User, Order, Product>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.USER_ID))
    .innerJoin(ProductTable.TABLE).on(OrderTable.PRODUCT_ID.equalTo(ProductTable.ID))
    .select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();
```

### Join with Column Selection

```java
// Select specific columns from joined tables
List<Tuple3<String, String, BigDecimal>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.USER_ID))
    .select(UserTable.EMAIL, OrderTable.ORDER_NUMBER, OrderTable.TOTAL)
    .where(OrderTable.STATUS.equalTo(OrderStatus.COMPLETED))
    .fetch();

// Map to entity with relationship loaded
List<User> usersWithAddresses = UserTable.TABLE
    .leftJoin(AddressTable.TABLE).on(UserTable.ADDRESS)
    .selectEntity(UserTable.TABLE)
    .withLoaded(UserTable.ADDRESS)  // Populate the address relationship
    .fetch();
```

## Subqueries

### Subquery in WHERE

```java
// IN subquery
List<User> usersWithOrders = UserTable.TABLE.select()
    .where(UserTable.ID.in(
        OrderTable.TABLE.subquery(OrderTable.USER_ID)
            .where(OrderTable.STATUS.equalTo(OrderStatus.COMPLETED))
    ))
    .fetch();

// NOT IN subquery
List<User> usersWithoutOrders = UserTable.TABLE.select()
    .where(UserTable.ID.notIn(
        OrderTable.TABLE.subquery(OrderTable.USER_ID)
    ))
    .fetch();

// Scalar subquery comparison
List<Order> largeOrders = OrderTable.TABLE.select()
    .where(OrderTable.TOTAL.greaterThan(
        OrderTable.TABLE.subquery(Agg.avg(OrderTable.TOTAL))
    ))
    .fetch();
```

### EXISTS / NOT EXISTS

```java
// EXISTS
List<User> usersWithRecentOrders = UserTable.TABLE.select()
    .whereExists(
        OrderTable.TABLE.subquery()
            .where(OrderTable.USER_ID.equalTo(UserTable.ID)
                .and(OrderTable.CREATED_AT.withinLast(Duration.ofDays(30))))
    )
    .fetch();

// NOT EXISTS
List<User> inactiveUsers = UserTable.TABLE.select()
    .whereNotExists(
        OrderTable.TABLE.subquery()
            .where(OrderTable.USER_ID.equalTo(UserTable.ID))
    )
    .fetch();
```

### Correlated Subqueries

```java
// Select with correlated subquery in column list
List<Tuple2<User, Long>> usersWithOrderCount = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        OrderTable.TABLE.subquery(Agg.count())
            .where(OrderTable.USER_ID.equalTo(UserTable.ID))
            .as("order_count")
    )
    .fetch();
```

## UPDATE Queries

### Basic Update

```java
// Update single entity
UserTable.TABLE.update(user);

// Update multiple entities
UserTable.TABLE.update(users);
UserTable.TABLE.update(List.of(user1, user2, user3));
```

### Conditional Update

```java
// Update with condition
int updatedCount = UserTable.TABLE.update()
    .set(UserTable.EMAIL, newEmail)
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Update multiple columns
int updatedCount = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .set(UserTable.LAST_LOGIN, Instant.now())
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .execute();

// Update all matching rows
int updatedCount = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.EXPIRED)
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING)
        .and(UserTable.CREATED_AT.olderThan(Duration.ofDays(30))))
    .execute();
```

### Update with Expressions

```java
// Increment numeric value
int updatedCount = UserTable.TABLE.update()
    .increment(UserTable.LOGIN_COUNT, 1)
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Decrement
int updatedCount = ProductTable.TABLE.update()
    .decrement(ProductTable.STOCK, quantity)
    .where(ProductTable.ID.equalTo(productId))
    .execute();

// Set to NULL
int updatedCount = UserTable.TABLE.update()
    .setNull(UserTable.LAST_LOGIN)
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Set from another column
int updatedCount = UserTable.TABLE.update()
    .setFrom(UserTable.BACKUP_EMAIL, UserTable.EMAIL)
    .where(UserTable.BACKUP_EMAIL.isNull())
    .execute();

// Conditional set (CASE WHEN)
int updatedCount = UserTable.TABLE.update()
    .setCase(UserTable.STATUS,
        Case.when(UserTable.AGE.lessThan(18), UserStatus.RESTRICTED)
            .when(UserTable.AGE.greaterThan(65), UserStatus.SENIOR)
            .otherwise(UserStatus.ACTIVE))
    .where(UserTable.STATUS.notEqualTo(UserStatus.DELETED))
    .execute();
```

### Updater Interface

```java
public interface Updater<E> {

    // Set value
    <T> Updater<E> set(SqlColumn<E, T> column, T value);
    <T> Updater<E> setNull(SqlColumn<E, T> column);
    <T> Updater<E> setFrom(SqlColumn<E, T> target, SqlColumn<E, T> source);

    // Numeric operations
    <T extends Number> Updater<E> increment(SqlColumn<E, T> column, T amount);
    <T extends Number> Updater<E> decrement(SqlColumn<E, T> column, T amount);
    <T extends Number> Updater<E> multiply(SqlColumn<E, T> column, T factor);

    // String operations
    Updater<E> append(SqlStringColumn<E> column, String suffix);
    Updater<E> prepend(SqlStringColumn<E> column, String prefix);
    Updater<E> trim(SqlStringColumn<E> column);
    Updater<E> upper(SqlStringColumn<E> column);
    Updater<E> lower(SqlStringColumn<E> column);

    // Temporal operations
    <T> Updater<E> setNow(SqlTemporalColumn<E, T> column);

    // Conditional set
    <T> Updater<E> setCase(SqlColumn<E, T> column, CaseExpression<T> caseExpr);

    // Array operations (PostgreSQL)
    <T> Updater<E> arrayAppend(SqlCollectionColumn<E, T> column, T element);
    <T> Updater<E> arrayRemove(SqlCollectionColumn<E, T> column, T element);
}
```

### Update with RETURNING

```java
// Return updated entities
List<User> updatedUsers = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .returning()
    .fetch();

// Return specific columns
List<Tuple2<UUID, UserStatus>> results = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .returning(UserTable.ID, UserTable.STATUS)
    .fetch();
```

## DELETE Queries

### Basic Delete

```java
// Delete single entity
UserTable.TABLE.delete(user);

// Delete multiple entities
UserTable.TABLE.delete(users);
UserTable.TABLE.delete(List.of(user1, user2, user3));
```

### Conditional Delete

```java
// Delete with condition
int deletedCount = UserTable.TABLE.delete()
    .where(UserTable.AGE.lessThan(13))
    .execute();

// Delete with complex condition
int deletedCount = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.DELETED)
        .and(UserTable.CREATED_AT.olderThan(Duration.ofDays(365))))
    .execute();

// Delete all (be careful!)
int deletedCount = UserTable.TABLE.delete().execute();
```

### Delete with RETURNING

```java
// Return deleted entities
List<User> deletedUsers = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.EXPIRED))
    .returning()
    .fetch();

// Return specific columns
List<UUID> deletedIds = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.EXPIRED))
    .returning(UserTable.ID)
    .fetch();
```

### Soft Delete

```java
// If entity has soft delete configured, delete() performs soft delete
UserTable.TABLE.delete()
    .where(UserTable.ID.equalTo(userId))
    .execute();
// Executes: UPDATE users SET deleted_at = NOW() WHERE id = ?

// Force hard delete
UserTable.TABLE.hardDelete()
    .where(UserTable.ID.equalTo(userId))
    .execute();
// Executes: DELETE FROM users WHERE id = ?

// Queries automatically filter soft-deleted by default
List<User> users = UserTable.TABLE.select().fetch();
// Executes: SELECT * FROM users WHERE deleted_at IS NULL

// Include soft-deleted
List<User> allUsers = UserTable.TABLE.select().includeSoftDeleted().fetch();
// Executes: SELECT * FROM users

// Only soft-deleted
List<User> deletedUsers = UserTable.TABLE.select().onlySoftDeleted().fetch();
// Executes: SELECT * FROM users WHERE deleted_at IS NOT NULL

// Restore soft-deleted
int restoredCount = UserTable.TABLE.restore()
    .where(UserTable.ID.equalTo(userId))
    .execute();
// Executes: UPDATE users SET deleted_at = NULL WHERE id = ?
```

## INSERT Operations

### Basic Insert

```java
// Insert single entity
User user = UserTable.TABLE.insert(user);

// Insert multiple entities
List<User> users = UserTable.TABLE.insert(user1, user2, user3);
List<User> users = UserTable.TABLE.insert(userList);
```

### Insert with Generated Values

```java
// Insert returns entity with generated values (ID, timestamps, etc.)
User user = User.builder()
    .name("John")
    .email("john@example.com")
    .build();

User saved = UserTable.TABLE.insert(user);
// saved.getId() now contains generated UUID
// saved.getCreatedAt() now contains generated timestamp
```

### Upsert (INSERT ON CONFLICT)

```java
// Upsert - update on conflict
User user = UserTable.TABLE.upsert(
    user,
    UserTable.EMAIL,  // Conflict column(s)
    updater -> updater
        .set(UserTable.NAME, user.getName())
        .set(UserTable.LAST_LOGIN, Instant.now())
);

// Upsert - do nothing on conflict
UserTable.TABLE.insertOrIgnore(user, UserTable.EMAIL);

// Upsert with conflict target
User user = UserTable.TABLE.upsert(
    user,
    conflict -> conflict
        .onColumns(UserTable.EMAIL)
        .doUpdate(updater -> updater
            .set(UserTable.NAME, user.getName())
            .increment(UserTable.LOGIN_COUNT, 1))
);

// Upsert with WHERE on conflict
User user = UserTable.TABLE.upsert(
    user,
    conflict -> conflict
        .onColumns(UserTable.EMAIL)
        .doUpdate(updater -> updater.set(UserTable.NAME, user.getName()))
        .where(UserTable.STATUS.notEqualTo(UserStatus.BANNED))
);
```

### Bulk Insert

```java
// Efficient bulk insert
int insertedCount = UserTable.TABLE.bulkInsert(largeUserList);

// Bulk insert with batch size
int insertedCount = UserTable.TABLE.bulkInsert(largeUserList, 1000);

// Bulk upsert
int upsertedCount = UserTable.TABLE.bulkUpsert(
    userList,
    UserTable.EMAIL,
    updater -> updater.set(UserTable.NAME, Expr.excluded(UserTable.NAME))
);
```

## Table DDL Operations

```java
// Create table
UserTable.TABLE.create();
UserTable.TABLE.createIfNotExists();

// Drop table
UserTable.TABLE.drop();
UserTable.TABLE.dropIfExists();
UserTable.TABLE.dropCascade();  // Drop with dependencies

// Truncate
UserTable.TABLE.truncate();
UserTable.TABLE.truncateCascade();
UserTable.TABLE.truncateRestart();  // Reset sequences

// Check existence
boolean exists = UserTable.TABLE.exists();
```

## UNION, INTERSECT, EXCEPT

```java
// UNION (removes duplicates)
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .union(
        UserTable.TABLE.select().where(UserTable.EMAIL.endsWith("@vip.com"))
    )
    .fetch();

// UNION ALL (keeps duplicates)
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .unionAll(
        UserTable.TABLE.select().where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    )
    .fetch();

// INTERSECT
List<User> users = UserTable.TABLE.select()
    .where(UserTable.AGE.greaterThan(18))
    .intersect(
        UserTable.TABLE.select().where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    )
    .fetch();

// EXCEPT
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .except(
        UserTable.TABLE.select().where(UserTable.EMAIL.endsWith("@banned.com"))
    )
    .fetch();
```

## Common Table Expressions (CTE)

```java
// Simple CTE
List<User> results = Cte.with("active_users",
        UserTable.TABLE.select().where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    )
    .select()
    .from("active_users")
    .where(UserTable.AGE.greaterThan(25))
    .fetch();

// Recursive CTE (for hierarchical data)
List<Category> allCategories = Cte.withRecursive("category_tree",
        // Base case
        CategoryTable.TABLE.select().where(CategoryTable.PARENT_ID.isNull()),
        // Recursive case
        (cte) -> CategoryTable.TABLE
            .innerJoin(cte).on(CategoryTable.PARENT_ID.equalTo(cte.column(CategoryTable.ID)))
            .select()
    )
    .select()
    .fetch();

// Multiple CTEs
List<Report> reports = Cte
    .with("active_users",
        UserTable.TABLE.select().where(UserTable.STATUS.equalTo(UserStatus.ACTIVE)))
    .with("recent_orders",
        OrderTable.TABLE.select().where(OrderTable.CREATED_AT.withinLast(Duration.ofDays(30))))
    .select()
    .from("active_users")
    .innerJoin("recent_orders").on(...)
    .fetch();
```

## Window Functions

```java
// ROW_NUMBER
List<Tuple2<User, Long>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Window.rowNumber()
            .over(Window.orderBy(UserTable.CREATED_AT.desc()))
            .as("row_num")
    )
    .fetch();

// RANK with PARTITION BY
List<Tuple2<User, Long>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Window.rank()
            .over(Window
                .partitionBy(UserTable.STATUS)
                .orderBy(UserTable.CREATED_AT.desc()))
            .as("rank")
    )
    .fetch();

// Running total
List<Tuple2<Order, BigDecimal>> results = OrderTable.TABLE
    .select(
        OrderTable.ALL_COLUMNS,
        Window.sum(OrderTable.TOTAL)
            .over(Window
                .partitionBy(OrderTable.USER_ID)
                .orderBy(OrderTable.CREATED_AT.asc())
                .rowsBetween(Window.UNBOUNDED_PRECEDING, Window.CURRENT_ROW))
            .as("running_total")
    )
    .fetch();

// LAG / LEAD
List<Tuple3<Order, BigDecimal, BigDecimal>> results = OrderTable.TABLE
    .select(
        OrderTable.ALL_COLUMNS,
        Window.lag(OrderTable.TOTAL, 1).over(Window.orderBy(OrderTable.CREATED_AT)).as("prev_total"),
        Window.lead(OrderTable.TOTAL, 1).over(Window.orderBy(OrderTable.CREATED_AT)).as("next_total")
    )
    .fetch();
```

### Window Function Interface

```java
public final class Window {

    // Ranking functions
    public static WindowFunction<Long> rowNumber();
    public static WindowFunction<Long> rank();
    public static WindowFunction<Long> denseRank();
    public static WindowFunction<Double> percentRank();
    public static WindowFunction<Long> ntile(int buckets);

    // Aggregate functions as window functions
    public static <T extends Number> WindowFunction<T> sum(SqlColumn<?, T> column);
    public static <T extends Number> WindowFunction<Double> avg(SqlColumn<?, T> column);
    public static <T extends Comparable<T>> WindowFunction<T> min(SqlColumn<?, T> column);
    public static <T extends Comparable<T>> WindowFunction<T> max(SqlColumn<?, T> column);
    public static WindowFunction<Long> count();

    // Offset functions
    public static <T> WindowFunction<T> lag(SqlColumn<?, T> column, int offset);
    public static <T> WindowFunction<T> lag(SqlColumn<?, T> column, int offset, T defaultValue);
    public static <T> WindowFunction<T> lead(SqlColumn<?, T> column, int offset);
    public static <T> WindowFunction<T> lead(SqlColumn<?, T> column, int offset, T defaultValue);
    public static <T> WindowFunction<T> firstValue(SqlColumn<?, T> column);
    public static <T> WindowFunction<T> lastValue(SqlColumn<?, T> column);
    public static <T> WindowFunction<T> nthValue(SqlColumn<?, T> column, int n);

    // Window specification builders
    public static WindowSpec partitionBy(SqlColumn<?, ?>... columns);
    public static WindowSpec orderBy(OrderSpec<?>... orders);

    // Frame bounds
    public static final FrameBound UNBOUNDED_PRECEDING = ...;
    public static final FrameBound UNBOUNDED_FOLLOWING = ...;
    public static final FrameBound CURRENT_ROW = ...;
    public static FrameBound preceding(int offset);
    public static FrameBound following(int offset);
}

public interface WindowFunction<T> {
    WindowFunction<T> over(WindowSpec spec);
    SelectExpression<T> as(String alias);
}

public interface WindowSpec {
    WindowSpec partitionBy(SqlColumn<?, ?>... columns);
    WindowSpec orderBy(OrderSpec<?>... orders);
    WindowSpec rowsBetween(FrameBound start, FrameBound end);
    WindowSpec rangeBetween(FrameBound start, FrameBound end);
}
```

## Locking

```java
// SELECT FOR UPDATE
Optional<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .forUpdate()
    .fetchFirst();

// SELECT FOR SHARE
Optional<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .forShare()
    .fetchFirst();

// SKIP LOCKED (for job queue pattern)
List<Task> tasks = TaskTable.TABLE.select()
    .where(TaskTable.STATUS.equalTo(TaskStatus.PENDING))
    .forUpdate()
    .skipLocked()
    .limit(10)
    .fetch();

// NOWAIT (fail immediately if locked)
try {
    User user = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(userId))
        .forUpdate()
        .noWait()
        .fetchOne();
} catch (LockNotAvailableException e) {
    // Handle lock contention
}

// Lock specific tables in a join
List<Tuple2<User, Order>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.USER_ID))
    .select()
    .forUpdate(UserTable.TABLE)  // Only lock users table
    .fetch();
```

## Raw SQL Escape Hatch

```java
// Raw WHERE clause
List<User> users = UserTable.TABLE
    .select()
    .whereRaw("email ~ ? AND created_at > ?", pattern, timestamp)
    .fetch();

// Named parameters
List<User> users = UserTable.TABLE
    .select()
    .whereRaw("email ~ :pattern AND created_at > :timestamp",
        Map.of("pattern", pattern, "timestamp", timestamp))
    .fetch();

// Full raw query with entity mapping
List<User> users = UserTable.TABLE.rawQuery("""
    SELECT u.* FROM users u
    INNER JOIN user_roles ur ON u.id = ur.user_id
    WHERE ur.role = :role
    ORDER BY u.created_at DESC
    """, Map.of("role", "ADMIN"));

// Raw query with custom mapping
List<Report> reports = SqlQuery.raw("""
    SELECT u.email, COUNT(o.id) as order_count, SUM(o.total) as total_spent
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    GROUP BY u.email
    """)
    .map(row -> new Report(
        row.getString("email"),
        row.getLong("order_count"),
        row.getBigDecimal("total_spent")
    ))
    .fetch();
```

## Query Inspection and Debugging

```java
// Get generated SQL
String sql = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .orderBy(UserTable.CREATED_AT.desc())
    .limit(10)
    .toSql();
// SELECT * FROM users WHERE status = ? ORDER BY created_at DESC LIMIT 10

// Get parameters
List<Object> params = query.getParameters();
// [ACTIVE]

// Explain query plan
String plan = UserTable.TABLE.select()
    .where(UserTable.EMAIL.like("%@example.com"))
    .explain();

// Explain analyze (actually runs query)
String analyzedPlan = UserTable.TABLE.select()
    .where(UserTable.EMAIL.like("%@example.com"))
    .explainAnalyze();
```

## Type Mapping

### Column Type Inference

The column type is inferred from the entity field:

| Java Type | SQL Type (PostgreSQL) | SQL Type (MySQL) |
|-----------|----------------------|------------------|
| `UUID` | `uuid` | `CHAR(36)` |
| `String` | `text` | `VARCHAR(255)` |
| `Integer` | `integer` | `INT` |
| `Long` | `bigint` | `BIGINT` |
| `BigDecimal` | `numeric` | `DECIMAL` |
| `Boolean` | `boolean` | `TINYINT(1)` |
| `Instant` | `timestamptz` | `DATETIME` |
| `LocalDate` | `date` | `DATE` |
| `LocalDateTime` | `timestamp` | `DATETIME` |
| `byte[]` | `bytea` | `BLOB` |
| `List<T>` | `T[]` (array) | `JSON` |
| `Map<K,V>` | `jsonb` | `JSON` |
| `Enum` | `text` | `VARCHAR` |

### Custom Type Converters

```java
// Register custom converter
TypeConverters.register(Money.class, new MoneyConverter());

public class MoneyConverter implements TypeConverter<Money, BigDecimal> {
    @Override
    public BigDecimal toDatabase(Money value) {
        return value.getAmount();
    }

    @Override
    public Money fromDatabase(BigDecimal value) {
        return Money.of(value);
    }
}
```

## Related Documents

- [00-overview.md](00-overview.md) - Architecture overview
- [01-yaml-schema.md](01-yaml-schema.md) - Schema definitions
- [02-code-generation.md](02-code-generation.md) - Code generation pipeline
- [04-dialect-system.md](04-dialect-system.md) - Dialect-specific features
- [05-orm-features.md](05-orm-features.md) - ORM and relationship handling
