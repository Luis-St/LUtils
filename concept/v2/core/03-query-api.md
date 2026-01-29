# Type-Safe Query API

## Overview

The query API provides SQL-like, column-reference based queries with full type safety. Queries are built using static column references and method chaining, providing familiar SQL structure while catching errors at compile time.

## Design Principles

1. **Column references as first-class objects** - Columns are typed objects with condition methods
2. **Consistent method chaining** - All query operations use the same builder pattern
3. **Table-centric operations** - Operations are performed on table objects
4. **SQL-like structure** - The API mirrors SQL syntax for familiarity
5. **Explicit logical operators** - AND/OR/NOT are explicit method calls

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

    // Relationship columns using SqlKey pattern
    public static final SqlRelationship<User, Address> ADDRESS =
        TABLE.relationship("address_id", Address.class, UUID.class, AddressTable.TABLE);

    // All columns for SELECT *
    public static final List<SqlColumn<User, ?>> ALL_COLUMNS = List.of(
        ID, NAME, EMAIL, AGE, STATUS, CREATED_AT
    );
}
```

## Condition Building

### Base Column Conditions

All columns support these conditions:

```java
// Equality
UserTable.EMAIL.equalTo("test@example.com")
UserTable.STATUS.notEqualTo(UserStatus.BANNED)

// Null checks
UserTable.LAST_LOGIN.isNull()
UserTable.EMAIL.isNotNull()

// IN clauses
UserTable.STATUS.in(UserStatus.ACTIVE, UserStatus.PENDING)
UserTable.ID.in(idList)
UserTable.STATUS.notIn(UserStatus.BANNED, UserStatus.DELETED)

// Subquery conditions
UserTable.ID.in(OrderTable.TABLE.subquery(OrderTable.USER_ID))
```

### Comparable Column Conditions

For numeric and temporal types:

```java
// Comparison
UserTable.AGE.greaterThan(18)
UserTable.AGE.greaterThanOrEqual(21)
UserTable.AGE.lessThan(65)
UserTable.AGE.lessThanOrEqual(30)
UserTable.AGE.between(18, 65)
UserTable.CREATED_AT.between(startDate, endDate)
```

### String Column Conditions

```java
// Pattern matching
UserTable.EMAIL.like("%@gmail.com")
UserTable.NAME.notLike("Test%")
UserTable.EMAIL.ilike("%@GMAIL.COM")  // Case-insensitive (PostgreSQL)

// Convenience patterns
UserTable.EMAIL.startsWith("admin")   // LIKE 'admin%'
UserTable.EMAIL.endsWith("@test.com") // LIKE '%@test.com'
UserTable.NAME.contains("john")       // LIKE '%john%'

// Case-insensitive equality
UserTable.EMAIL.equalToIgnoreCase("TEST@EXAMPLE.COM")

// Length conditions
UserTable.NAME.lengthEquals(10)
UserTable.NAME.lengthGreaterThan(5)
UserTable.NAME.isEmpty()
UserTable.NAME.isNotEmpty()
```

### Temporal Column Conditions

```java
// Relative time
UserTable.CREATED_AT.withinLast(Duration.ofDays(7))
UserTable.LAST_LOGIN.olderThan(Duration.ofDays(30))

// Date part extraction
UserTable.CREATED_AT.yearEquals(2024)
UserTable.CREATED_AT.monthEquals(1)
UserTable.CREATED_AT.dateEquals(LocalDate.of(2024, 1, 15))
```

## Logical Operators

### Combining Conditions

```java
// AND
UserTable.AGE.greaterThan(18).and(UserTable.STATUS.equalTo(UserStatus.ACTIVE))

// OR
UserTable.STATUS.equalTo(UserStatus.ACTIVE).or(UserTable.STATUS.equalTo(UserStatus.PENDING))

// NOT
UserTable.STATUS.in(UserStatus.BANNED, UserStatus.DELETED).not()

// Complex nested conditions
UserTable.AGE.greaterThan(18)
    .and(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .or(
        UserTable.EMAIL.endsWith("@admin.com")
            .and(UserTable.STATUS.notEqualTo(UserStatus.BANNED))
    )

// Static helpers for multiple conditions
Condition.and(
    UserTable.AGE.greaterThan(18),
    UserTable.STATUS.equalTo(UserStatus.ACTIVE),
    UserTable.EMAIL.isNotNull()
)

Condition.or(
    UserTable.STATUS.equalTo(UserStatus.ACTIVE),
    UserTable.STATUS.equalTo(UserStatus.PENDING),
    UserTable.EMAIL.endsWith("@vip.com")
)
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

When selecting specific columns, results are returned as `Row2` through `Row8`:

```java
// Select specific columns - returns Row2
List<Row2<UUID, String>> results = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL)
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Access values
for (Row2<UUID, String> row : results) {
    UUID id = row.first();
    String email = row.second();
}

// Select three columns - returns Row3
List<Row3<UUID, String, Integer>> results = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL, UserTable.AGE)
    .fetch();

// Select single column - returns single type
List<String> emails = UserTable.TABLE
    .select(UserTable.EMAIL)
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();
```

### Row Types

Row types are generated based on configuration (default: `Row2` through `Row8`):

```java
public record Row2<T1, T2>(T1 first, T2 second) {}
public record Row3<T1, T2, T3>(T1 first, T2 second, T3 third) {}
public record Row4<T1, T2, T3, T4>(T1 first, T2 second, T3 third, T4 fourth) {}
// ... up to Row8 by default

// Configure max in _config.yaml:
// rows:
//   maxCount: 10  # Generate Row2 through Row10
```

### Projection Interfaces

For named access, define a projection interface:

```java
public interface UserSummary {
    UUID id();
    String email();
    int age();
}

List<UserSummary> summaries = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL, UserTable.AGE)
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetchAs(UserSummary.class);
```

### Ordering

```java
// Single column
List<User> users = UserTable.TABLE.select()
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
```

### Pagination

```java
// Basic pagination
List<User> page = UserTable.TABLE.select()
    .orderBy(UserTable.CREATED_AT.desc())
    .limit(20)
    .offset(40)  // Page 3
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

### Fetch Methods

```java
// Fetch all results
List<User> users = query.fetch();

// Fetch as stream (for large result sets)
Stream<User> stream = query.stream();

// Fetch first result
Optional<User> user = query.fetchFirst();

// Fetch exactly one (throws if not found or multiple)
User user = query.fetchOne();

// Fetch one or null
User user = query.fetchOneOrNull();

// Count
long count = query.count();

// Exists check
boolean exists = query.exists();
```

## Aggregate Functions

```java
// COUNT
long count = UserTable.TABLE.select().count();
long activeCount = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .count();

// SUM
Long totalAge = UserTable.TABLE.select(Agg.sum(UserTable.AGE)).fetchOne();

// AVG
Double averageAge = UserTable.TABLE.select(Agg.avg(UserTable.AGE)).fetchOne();

// MIN / MAX
Integer minAge = UserTable.TABLE.select(Agg.min(UserTable.AGE)).fetchOne();
Integer maxAge = UserTable.TABLE.select(Agg.max(UserTable.AGE)).fetchOne();

// COUNT DISTINCT
long uniqueStatuses = UserTable.TABLE.select(Agg.countDistinct(UserTable.STATUS)).fetchOne();
```

## GROUP BY and HAVING

```java
// Group users by status, count each group
List<Row2<UserStatus, Long>> statusCounts = UserTable.TABLE
    .select(UserTable.STATUS, Agg.count())
    .groupBy(UserTable.STATUS)
    .fetch();

// Group with HAVING
List<Row2<UserStatus, Long>> popularStatuses = UserTable.TABLE
    .select(UserTable.STATUS, Agg.count())
    .groupBy(UserTable.STATUS)
    .having(Agg.count().greaterThan(10))
    .fetch();

// WHERE + GROUP BY + HAVING
List<Row2<UserStatus, Long>> results = UserTable.TABLE
    .select(UserTable.STATUS, Agg.count())
    .where(UserTable.AGE.greaterThan(18))
    .groupBy(UserTable.STATUS)
    .having(Agg.count().greaterThan(10))
    .orderBy(Agg.count().desc())
    .fetch();
```

## JOIN Operations

```java
// Inner join
List<Row2<User, Address>> results = UserTable.TABLE
    .innerJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS.equalTo(AddressTable.ID))
    .select()
    .fetch();

// Left join
List<Row2<User, Optional<Address>>> results = UserTable.TABLE
    .leftJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS)
    .select()
    .fetch();

// Using relationship shorthand
List<Row2<User, Address>> results = UserTable.TABLE
    .innerJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS)  // Uses defined relationship
    .select()
    .fetch();

// Multiple joins
List<Row3<User, Order, Product>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.USER_ID))
    .innerJoin(ProductTable.TABLE).on(OrderTable.PRODUCT_ID.equalTo(ProductTable.ID))
    .select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Load relationship into entity
List<User> users = UserTable.TABLE
    .leftJoin(AddressTable.TABLE).on(UserTable.ADDRESS)
    .selectEntity(UserTable.TABLE)
    .withLoaded(UserTable.ADDRESS)
    .fetch();
```

## Subqueries

```java
// IN subquery
List<User> usersWithOrders = UserTable.TABLE.select()
    .where(UserTable.ID.in(
        OrderTable.TABLE.subquery(OrderTable.USER_ID)
            .where(OrderTable.STATUS.equalTo(OrderStatus.COMPLETED))
    ))
    .fetch();

// EXISTS
List<User> usersWithRecentOrders = UserTable.TABLE.select()
    .whereExists(
        OrderTable.TABLE.subquery()
            .where(OrderTable.USER_ID.equalTo(UserTable.ID)
                .and(OrderTable.CREATED_AT.withinLast(Duration.ofDays(30))))
    )
    .fetch();

// Scalar subquery
List<Order> largeOrders = OrderTable.TABLE.select()
    .where(OrderTable.TOTAL.greaterThan(
        OrderTable.TABLE.subquery(Agg.avg(OrderTable.TOTAL))
    ))
    .fetch();
```

## UPDATE Queries

```java
// Update single entity
UserTable.TABLE.update(user);

// Conditional update
int updatedCount = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .set(UserTable.LAST_LOGIN, Instant.now())
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Update with expressions
int updatedCount = UserTable.TABLE.update()
    .increment(UserTable.LOGIN_COUNT, 1)
    .setNow(UserTable.LAST_LOGIN)
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Update with RETURNING (PostgreSQL, SQLite 3.35+)
List<User> updatedUsers = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .returning()
    .fetch();
```

## DELETE Queries

```java
// Delete single entity
UserTable.TABLE.delete(user);

// Conditional delete
int deletedCount = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.DELETED))
    .execute();

// Delete with RETURNING
List<User> deletedUsers = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.EXPIRED))
    .returning()
    .fetch();

// Soft delete (if configured)
UserTable.TABLE.delete()
    .where(UserTable.ID.equalTo(userId))
    .execute();
// Executes: UPDATE users SET deleted_at = NOW() WHERE id = ?

// Hard delete (bypass soft delete)
UserTable.TABLE.hardDelete()
    .where(UserTable.ID.equalTo(userId))
    .execute();
```

## INSERT Operations

```java
// Insert single entity
User saved = UserTable.TABLE.insert(user);

// Insert multiple entities
List<User> saved = UserTable.TABLE.insert(user1, user2, user3);

// Upsert (INSERT ON CONFLICT)
User user = UserTable.TABLE.upsert(
    user,
    UserTable.EMAIL,  // Conflict column
    updater -> updater
        .set(UserTable.NAME, user.name())
        .set(UserTable.LAST_LOGIN, Instant.now())
);

// Insert or ignore
UserTable.TABLE.insertOrIgnore(user, UserTable.EMAIL);

// Bulk insert
int insertedCount = UserTable.TABLE.bulkInsert(largeUserList);
```

## SQL Functions

### Generic Functions

```java
// Use in WHERE
UserTable.TABLE.select()
    .where(Sql.function("LOWER", UserTable.EMAIL).equalTo("test@example.com"))
    .fetch();

// Use in SELECT
List<Row2<User, String>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Sql.function("UPPER", UserTable.NAME).as("upper_name")
    )
    .fetch();

// Common functions
Sql.coalesce(UserTable.NICKNAME, "Anonymous")
Sql.cast(UserTable.AGE, String.class)
Sql.now()
Sql.currentDate()
```

### Dialect-Specific Functions

```java
// PostgreSQL
UserTable.TABLE.select()
    .where(DialectFunctions.postgres().ilike(UserTable.EMAIL, "%@GMAIL.COM"))
    .fetch();

// TimescaleDB
SensorTable.TABLE.select(
    DialectFunctions.timescale().timeBucket("1 hour", SensorTable.RECORDED_AT),
    Agg.avg(SensorTable.VALUE)
)
.groupBy(Sql.raw("1"))
.fetch();
```

## Window Functions

```java
// ROW_NUMBER
List<Row2<User, Long>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Window.rowNumber()
            .over(Window.orderBy(UserTable.CREATED_AT.desc()))
            .as("row_num")
    )
    .fetch();

// RANK with PARTITION BY
List<Row2<User, Long>> results = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        Window.rank()
            .over(Window
                .partitionBy(UserTable.STATUS)
                .orderBy(UserTable.CREATED_AT.desc()))
            .as("rank")
    )
    .fetch();
```

## Locking

```java
// SELECT FOR UPDATE
User user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .forUpdate()
    .fetchOne();

// SKIP LOCKED (for job queue pattern)
List<Task> tasks = TaskTable.TABLE.select()
    .where(TaskTable.STATUS.equalTo(TaskStatus.PENDING))
    .forUpdate()
    .skipLocked()
    .limit(10)
    .fetch();

// NOWAIT
try {
    User user = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(userId))
        .forUpdate()
        .noWait()
        .fetchOne();
} catch (LockNotAvailableException e) {
    // Row is locked by another transaction
}
```

## Query Debugging

```java
// Get generated SQL
String sql = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .limit(10)
    .toSql();
// SELECT * FROM users WHERE status = ? LIMIT 10

// Get parameters
List<Object> params = query.getParameters();
// [ACTIVE]

// Explain query plan
String plan = query.explain();
String analyzedPlan = query.explainAnalyze();
```

## Related Documents

- [01-overview.md](01-overview.md) - Architecture overview
- [02-yaml-schema.md](02-yaml-schema.md) - Schema definitions
- [04-code-generation.md](04-code-generation.md) - Generated code details
- [../reference/sql-functions.md](../reference/sql-functions.md) - Function reference
- [../reference/relationship-loading.md](../reference/relationship-loading.md) - Loading strategies
