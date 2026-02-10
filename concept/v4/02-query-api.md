# Query API

## SELECT

```java
// Basic select
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .orderBy(UserTable.CREATED_AT.desc())
    .limit(10).offset(20)
    .fetch();

// Column projection - returns Row types
List<Row2<UUID, String>> rows = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL)
    .fetch();

// Single column
List<String> emails = UserTable.TABLE.select(UserTable.EMAIL).fetch();

// Fetch methods
List<User> all = query.fetch();
Optional<User> first = query.fetchFirst();
User one = query.fetchOne();           // throws if not found
User oneOrNull = query.fetchOneOrNull();
long count = query.count();
boolean exists = query.exists();
Stream<User> stream = query.stream();

// Pagination
SqlPage<User> page = query.fetchPage(pageNumber, pageSize);
// page.content(), page.totalElements(), page.hasNext(), etc.
```

## Conditions

```java
// Equality
UserTable.EMAIL.equalTo("test@example.com")
UserTable.STATUS.notEqualTo(UserStatus.BANNED)
UserTable.EMAIL.equalToIgnoreCase("TEST@EXAMPLE.COM")

// Null
UserTable.LAST_LOGIN.isNull()
UserTable.EMAIL.isNotNull()

// Comparison (numeric/temporal)
UserTable.AGE.greaterThan(18)
UserTable.AGE.between(18, 65)
UserTable.CREATED_AT.withinLast(Duration.ofDays(7))

// IN
UserTable.STATUS.in(UserStatus.ACTIVE, UserStatus.PENDING)
UserTable.ID.in(idList)
UserTable.ID.in(OrderTable.TABLE.subquery(OrderTable.CUSTOMER_ID))

// String
UserTable.EMAIL.like("%@gmail.com")
UserTable.NAME.startsWith("John")
UserTable.NAME.contains("doe")
UserTable.NAME.lengthGreaterThan(5)

// Logical
condition1.and(condition2)
condition1.or(condition2)
condition.not()
SqlCondition.and(c1, c2, c3)
SqlCondition.or(c1, c2, c3)
```

## JOIN

Joins are pre-generated from YAML relationships - no manual ON clauses:

```java
// Inner join - returns Row2
List<Row2<User, Address>> rows = UserTable.TABLE.joinAddress().select().fetch();

// Left join - second element is Optional
List<Row2<User, Optional<Address>>> rows = UserTable.TABLE.leftJoinAddress().select().fetch();

// Map to FullJoined entity
List<UserFullJoined> users = UserTable.TABLE
    .joinAddress()
    .selectAs(UserFullJoined.class)
    .fetch();

// Chained joins
List<Row3<User, Order, OrderItem>> rows = UserTable.TABLE
    .joinOrders()
    .joinOrderItems()
    .select()
    .fetch();

// With conditions
UserTable.TABLE.joinAddress().select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .where(AddressTable.COUNTRY.equalTo("US"))
    .fetch();
```

## Aggregates & GROUP BY

```java
// Count
long count = UserTable.TABLE.select().count();

// Aggregates
Long sum = UserTable.TABLE.select(SqlAgg.sum(UserTable.AGE)).fetchOne();
Double avg = UserTable.TABLE.select(SqlAgg.avg(UserTable.AGE)).fetchOne();
Integer min = UserTable.TABLE.select(SqlAgg.min(UserTable.AGE)).fetchOne();

// GROUP BY with HAVING
List<Row2<UserStatus, Long>> counts = UserTable.TABLE
    .select(UserTable.STATUS, SqlAgg.count())
    .groupBy(UserTable.STATUS)
    .having(SqlAgg.count().greaterThan(10))
    .orderBy(SqlAgg.count().desc())
    .fetch();
```

## Window Functions

```java
List<Row2<User, Long>> ranked = UserTable.TABLE
    .select(
        UserTable.ALL_COLUMNS,
        SqlWindow.rowNumber()
            .over(SqlWindow.partitionBy(UserTable.STATUS)
                          .orderBy(UserTable.CREATED_AT.desc()))
            .as("rank")
    )
    .fetch();
```

## INSERT

```java
User saved = UserTable.TABLE.insert(user);
List<User> saved = UserTable.TABLE.insert(user1, user2, user3);

// Upsert
User user = UserTable.TABLE.upsert(user, UserTable.EMAIL,
    updater -> updater.set(UserTable.NAME, user.name()));

// Insert or ignore
UserTable.TABLE.insertOrIgnore(user, UserTable.EMAIL);
```

## UPDATE

```java
// Update entity
UserTable.TABLE.update(user);

// Conditional update
int count = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .set(UserTable.LAST_LOGIN, Instant.now())
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Update with expressions
UserTable.TABLE.update()
    .increment(UserTable.LOGIN_COUNT, 1)
    .setNow(UserTable.LAST_LOGIN)
    .where(UserTable.ID.equalTo(userId))
    .execute();

// With RETURNING (PostgreSQL, SQLite 3.35+)
List<User> updated = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .returning()
    .fetch();
```

## DELETE

```java
UserTable.TABLE.delete(user);

int count = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.DELETED))
    .execute();
```

## Locking

```java
User user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .forUpdate()
    .fetchOne();

// Job queue pattern
List<Task> tasks = TaskTable.TABLE.select()
    .where(TaskTable.STATUS.equalTo(TaskStatus.PENDING))
    .forUpdate().skipLocked()
    .limit(10)
    .fetch();

// NOWAIT - throws SqlLockNotAvailableException if locked
UserTable.TABLE.select().where(...).forUpdate().noWait().fetchOne();
```

## SQL Functions

### String (SqlString)

```java
SqlString.lower(col)           SqlString.upper(col)
SqlString.trim(col)            SqlString.length(col)
SqlString.substring(col, 1, 5) SqlString.concat(a, " ", b)
SqlString.replace(col, "a", "b")
```

### Math (SqlMath)

```java
SqlMath.abs(col)    SqlMath.round(col, 2)   SqlMath.ceil(col)
SqlMath.floor(col)  SqlMath.mod(col, 2)     SqlMath.power(col, 2)
SqlMath.sqrt(col)   SqlMath.random()
```

### Date (SqlDate)

```java
SqlDate.now()                  SqlDate.currentDate()
SqlDate.year(col)              SqlDate.month(col)
SqlDate.day(col)               SqlDate.dateTrunc("day", col)
SqlDate.addDays(col, 7)        SqlDate.dateDiff(DatePart.DAY, start, end)
SqlDate.toChar(col, "YYYY-MM-DD")
```

### General (SqlFunction)

```java
SqlFunction.coalesce(col, defaultValue)
SqlFunction.nullif(col, value)
SqlFunction.cast(col, String.class)
SqlFunction.greatest(a, b, c)
SqlFunction.least(a, b, c)
SqlFunction.caseWhen(cond, "A").when(cond2, "B").otherwise("C")
SqlFunction.of("CUSTOM_FUNC", ReturnType.class, args...)  // Fallback
```

### Dialect-Specific

```java
// PostgreSQL ILIKE
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.EMAIL.dialect(SqlDialect.POSTGRES).ilike("%@GMAIL.COM"))
    .fetch();

// PostgreSQL arrays
UserTable.ROLES.dialect(SqlDialect.POSTGRES).arrayContains("admin")

// TimescaleDB time bucket
SensorTable.RECORDED_AT.dialect(SqlDialect.TIMESCALE).timeBucket("1 hour")
```

## Subqueries

```java
// IN subquery
UserTable.TABLE.select()
    .where(UserTable.ID.in(
        OrderTable.TABLE.subquery(OrderTable.CUSTOMER_ID)
            .where(OrderTable.STATUS.equalTo(OrderStatus.COMPLETED))
    ))
    .fetch();

// EXISTS
UserTable.TABLE.select()
    .whereExists(OrderTable.TABLE.subquery()
        .where(OrderTable.CUSTOMER_ID.equalTo(UserTable.ID)))
    .fetch();
```

## Query Debugging

```java
String sql = query.toSql();
List<Object> params = query.getParameters();
String plan = query.explain();
```
