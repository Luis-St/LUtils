# Database Concept Overview

## Introduction

LUtils-DB is a type-safe, compile-time database access layer for Java. It generates query builders, entity mappers, and repository interfaces from YAML schema definitions, providing full type safety without runtime reflection or magic.

## Design Principles

### 1. Type-Safe at Compile Time
All queries are validated at compile time. Column references are typed, conditions are type-checked, and result mappings are generated - no runtime surprises.

### 2. Explicit Over Implicit
There is no lazy loading, no proxy objects, no annotation-driven behavior. You control exactly when queries execute, when transactions begin, and how relationships are loaded.

### 3. SQL-Like API
The query API mirrors SQL syntax for familiarity while providing type safety. Developers who know SQL can immediately understand the generated code.

### 4. Dialect-Aware
Write once, run on multiple databases. The dialect system handles SQL syntax differences, type mappings, and database-specific features transparently.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Build Time                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌──────────────┐     ┌──────────────┐     ┌──────────────────────────┐   │
│   │ YAML Schema  │────▶│   Gradle     │────▶│    Generated Code        │   │
│   │   Files      │     │   Plugin     │     │  - Entity Records        │   │
│   │              │     │              │     │  - Table References      │   │
│   │ - user.yaml  │     │ - Parse      │     │  - Query Builders        │   │
│   │ - post.yaml  │     │ - Validate   │     │  - Entity Mappers        │   │
│   │ - _config    │     │ - Generate   │     │  - Repositories          │   │
│   └──────────────┘     └──────────────┘     └──────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              Runtime                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌──────────────┐     ┌──────────────┐     ┌──────────────────────────┐   │
│   │ Application  │────▶│ Query API    │────▶│      Database            │   │
│   │    Code      │     │              │     │                          │   │
│   │              │     │ - Select     │     │  PostgreSQL / MySQL /    │   │
│   │              │     │ - Insert     │     │  SQLite / H2             │   │
│   │              │     │ - Update     │     │                          │   │
│   │              │     │ - Delete     │     │                          │   │
│   └──────────────┘     └──────────────┘     └──────────────────────────┘   │
│                              │                                              │
│                              ▼                                              │
│                    ┌──────────────────┐                                     │
│                    │  Dialect System  │                                     │
│                    │  - TypeMapping   │                                     │
│                    │  - SqlGenerator  │                                     │
│                    └──────────────────┘                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Component Overview

### Schema Layer
YAML files define your database entities, fields, relationships, and constraints. Each entity lives in its own file for clarity and version control.

```yaml
# user.yaml
entity: User
table: users

fields:
  id:
    type: UUID
    primary: true
    generated: true
  email:
    type: String
    unique: true
  status:
    type: UserStatus
    enum: true
```

### Code Generation
The Gradle plugin processes YAML schemas at compile time:

1. **Parse** - Read and validate YAML syntax
2. **Validate** - Check type consistency, relationships, constraints
3. **Resolve** - Map Java types to SQL types per dialect
4. **Generate** - Output type-safe Java code

### Runtime Components

**Table References** - Static column references with typed conditions:
```java
UserTable.EMAIL.equalTo("test@example.com")  // Condition<User>
UserTable.AGE.greaterThan(18)                // Condition<User>
UserTable.STATUS.in(ACTIVE, PENDING)         // Condition<User>
```

**Query Builders** - Fluent API mirroring SQL:
```java
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .orderBy(UserTable.CREATED_AT.desc())
    .limit(10)
    .fetch();
```

**Dialect System** - Database-agnostic SQL generation:
- `Dialect` - Database identifier and feature flags
- `TypeMapping` - Java to SQL type conversion
- `SqlGenerator` - Statement generation

## Quick Start Example

### 1. Define Schema

```yaml
# src/main/resources/schema/user.yaml
entity: User
table: users

fields:
  id:
    type: UUID
    primary: true
    generated: true
    generator: uuid

  email:
    type: String
    unique: true
    constraints:
      notBlank: true
      maxLength: 255

  name:
    type: String
    constraints:
      notBlank: true

  status:
    type: UserStatus
    enum:
      values: [PENDING, ACTIVE, SUSPENDED]
      default: PENDING

  createdAt:
    type: Instant
    column: created_at
    insertable: true
    updatable: false
```

### 2. Configure Plugin

```kotlin
// build.gradle.kts
plugins {
    id("net.luis.db") version "1.0.0"
}

lutilsDb {
    schemaDir = file("src/main/resources/schema")
    basePackage = "com.example.db"
    dialect = "postgresql"
}
```

### 3. Use Generated Code

```java
// Insert
User user = User.builder()
    .email("john@example.com")
    .name("John Doe")
    .build();

User saved = UserTable.TABLE.insert(user);
// saved.id() is now populated with generated UUID

// Query
List<User> activeUsers = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .orderBy(UserTable.NAME.asc())
    .fetch();

// Update
int updated = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.SUSPENDED)
    .where(UserTable.ID.equalTo(userId))
    .execute();

// Delete
int deleted = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.SUSPENDED))
    .execute();
```

### 4. Transactions

```java
// Connection-first pattern
try (Connection conn = dataSource.getConnection()) {
    Transaction tx = conn.beginTransaction();
    try {
        UserTable.TABLE.insert(user);
        OrderTable.TABLE.insert(order);
        tx.commit();
    } catch (Exception e) {
        tx.rollback();
        throw e;
    }
}

// Transaction-first pattern (connection managed internally)
try (Transaction tx = database.beginTransaction()) {
    UserTable.TABLE.insert(user);
    OrderTable.TABLE.insert(order);
    tx.commit();
}

// Auto-commit mode (no explicit transaction)
User user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOne();
```

## Key Concepts

### Row Types for Projections

When selecting specific columns, results are returned as `Row2` through `Row8` (configurable):

```java
List<Row2<UUID, String>> results = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL)
    .fetch();

for (Row2<UUID, String> row : results) {
    UUID id = row.first();
    String email = row.second();
}
```

### SqlKey for Relationships

Foreign key references use `SqlKey<K, V>` to separate the key from the loaded entity:

```java
public record Order(
    UUID id,
    BigDecimal total,
    SqlKey<UUID, User> customer  // Key is always available, User loaded on demand
) {}

// Access the key (always available)
UUID customerId = order.customer().key();

// Access the loaded entity (only if loaded via join)
User customer = order.customer().value();  // May be null if not loaded
```

### SQL Functions

Generic functions work across dialects:
```java
UserTable.TABLE.select()
    .where(Sql.function("LOWER", UserTable.EMAIL).equalTo("test@example.com"))
    .fetch();
```

Dialect-specific functions:
```java
// PostgreSQL TimescaleDB
SensorTable.TABLE.select()
    .where(DialectFunctions.postgres()
        .timeBucket("1 hour", SensorTable.RECORDED_AT)
        .equalTo(targetBucket))
    .fetch();
```

## Related Documents

- [02-yaml-schema.md](02-yaml-schema.md) - Schema definition reference
- [03-query-api.md](03-query-api.md) - Query builder API
- [04-code-generation.md](04-code-generation.md) - Generation pipeline details
- [../reference/dialect-system.md](../reference/dialect-system.md) - Dialect architecture
- [../reference/transaction-api.md](../reference/transaction-api.md) - Transaction management
