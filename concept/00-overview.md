# Database Code Generation System - Architecture Overview

## Introduction

This document describes the architecture of the LUtils Database module, a YAML-driven code generation system for type-safe database access. The system generates Java models and query APIs from YAML schema definitions at compile-time via a Gradle plugin.

## Design Philosophy

The database module follows the same design principles as the existing LUtils codebase:

- **Type Safety**: Leverage Java's type system for compile-time query validation
- **Fluent API**: Method chaining for intuitive query building
- **Immutability**: Generated models support immutable record patterns
- **Codec Integration**: Seamless serialization via the existing Codec system

## Module Structure

The database system consists of four modules:

```
lutils-db/
├── lutils-db-core/          # Core runtime library
├── lutils-db-generator/     # Code generation engine
├── lutils-db-gradle/        # Gradle plugin
└── lutils-db-dialects/      # SQL dialect implementations
```

### Module Dependencies

```
┌─────────────────────┐
│  lutils-db-gradle   │  Gradle Plugin (compile-time only)
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│ lutils-db-generator │  Code Generation Engine
└──────────┬──────────┘
           │ generates code using
           ▼
┌─────────────────────┐     ┌─────────────────────┐
│   lutils-db-core    │◄────│ lutils-db-dialects  │
│   (runtime)         │     │ (runtime)           │
└──────────┬──────────┘     └─────────────────────┘
           │ depends on
           ▼
┌─────────────────────┐
│     lutils-core     │  Existing LUtils Library
│  (Codec,            │
│   Constraints)      │
└─────────────────────┘
```

## Core Components

### 1. Schema Definition Layer

YAML files define the database schema:

```yaml
# schema/user.yaml
entity: User
table: users
fields:
  id:
    type: UUID
    primary: true
    generated: true
  email:
    type: String
    constraints:
      notBlank: true
      maxLength: 255
      unique: true
  createdAt:
    type: Instant
    column: created_at
    default: now()
```

### 2. Code Generation Layer

The Gradle plugin processes YAML schemas and generates:

- **Entity Classes**: Immutable records or mutable POJOs
- **Repository Interfaces**: Type-safe CRUD operations
- **Query Builders**: Fluent API for complex queries

### 3. Runtime Layer

The core runtime provides:

- **Connection Management**: Pluggable connection pooling
- **Query Execution**: Prepared statement handling
- **Transaction Support**: Declarative and programmatic transactions
- **Caching**: First-level (session) and second-level (shared) caching

### 4. Dialect Layer

SQL dialect implementations for:

- PostgreSQL (primary target)
- TimescaleDB (PostgreSQL extension)
- MySQL / MariaDB
- SQLite
- H2 (for testing)

## Generated Code Example

From the YAML schema above, the system generates:

```java
// Generated entity
public record User(
    UUID id,
    String email,
    Instant createdAt
) {
    // Builder for mutations
    public static UserBuilder builder() { ... }
    public UserBuilder toBuilder() { ... }
}

// Generated repository
public interface UserRepository extends Repository<User, UUID> {

    User findById(UUID id) throws EntityNotFoundException;
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User save(User user);
    void delete(User user);

    // Custom query builder
    UserQuery query();
}

// Generated query builder
public interface UserQuery extends Query<User> {

    UserQuery whereId(UUID id);
    UserQuery whereEmail(String email);
    UserQuery whereEmailLike(String pattern);
    UserQuery whereCreatedAtAfter(Instant instant);
    UserQuery orderByCreatedAt(Order order);
    UserQuery limit(int limit);
    UserQuery offset(int offset);

    List<User> fetch();
    Optional<User> fetchFirst();
    long count();
}
```

## Key Features

### Type-Safe Queries

All queries are validated at compile-time:

```java
// Compile-time error: User has no 'name' field
userRepository.query()
    .whereName("John")  // Error: method does not exist
    .fetch();

// Correct usage - fully type-checked
userRepository.query()
    .whereEmail("john@example.com")
    .whereCreatedAtAfter(Instant.now().minus(Duration.ofDays(30)))
    .orderByCreatedAt(Order.DESC)
    .limit(10)
    .fetch();
```

### Constraint Integration

The existing constraint system validates data before persistence:

```java
// Constraints defined in YAML are enforced at runtime
User user = User.builder()
    .email("")  // Fails: notBlank constraint
    .build();

// Validation throws ConstraintViolationException
user.validate();  // throws ConstraintViolationException
```

### Codec Integration

Generated models integrate with the Codec system:

```java
// Automatic codec registration
Codec<User> userCodec = Codecs.get(User.class);

// Serialize to JSON
JsonElement json = userCodec.encodeStart(JsonOps.INSTANCE, user);

// Deserialize from JSON
User user = userCodec.decodeStart(JsonOps.INSTANCE, jsonElement);
```

## Configuration

### Gradle Plugin Configuration

```kotlin
// build.gradle.kts
plugins {
    id("net.luis.db") version "1.0.0"
}

lutilsDb {
    // Schema location
    schemaDir = file("src/main/resources/schema")

    // Generation output
    outputDir = file("build/generated/sources/lutils-db")

    // Package for generated code
    basePackage = "com.example.db"

    // Target dialect
    dialect = "postgresql"

    // Generation options
    generateRepositories = true
    generateCodecs = true

    // Entity style
    entityStyle = "record"  // or "pojo"
}
```

### Runtime Configuration

```java
// Database configuration
DatabaseConfig config = DatabaseConfig.builder()
    .dialect(PostgresDialect.INSTANCE)
    .connectionPool(HikariPoolProvider.create(
        "jdbc:postgresql://localhost:5432/mydb",
        "user",
        "password"
    ))
    .cacheProvider(CaffeineCache.create())
    .build();

// Create database instance
Database db = Database.create(config);

// Get repositories
UserRepository users = db.repository(UserRepository.class);
```

## Package Structure

```
net.luis.utils.db/
├── core/
│   ├── Database.java
│   ├── DatabaseConfig.java
│   ├── Repository.java
│   ├── Query.java
│   └── Transaction.java
├── connection/
│   ├── ConnectionPool.java
│   ├── ConnectionProvider.java
│   └── PoolConfig.java
├── dialect/
│   ├── Dialect.java
│   ├── DialectRegistry.java
│   └── impl/
│       ├── PostgresDialect.java
│       ├── TimescaleDialect.java
│       ├── MySqlDialect.java
│       └── SqliteDialect.java
├── query/
│   ├── QueryBuilder.java
│   ├── Condition.java
│   ├── Order.java
│   └── Join.java
├── mapping/
│   ├── EntityMapper.java
│   ├── FieldMapper.java
│   └── TypeMapper.java
├── cache/
│   ├── CacheProvider.java
│   ├── SessionCache.java
│   └── SharedCache.java
└── generator/
    ├── SchemaParser.java
    ├── EntityGenerator.java
    ├── RepositoryGenerator.java
    └── QueryGenerator.java
```

## Related Documents

- [01-yaml-schema.md](01-yaml-schema.md) - YAML schema specification
- [02-code-generation.md](02-code-generation.md) - Gradle plugin and generation process
- [03-query-api.md](03-query-api.md) - Type-safe query builder design
- [04-dialect-system.md](04-dialect-system.md) - SQL dialect abstraction
- [05-orm-features.md](05-orm-features.md) - Relationships, loading, caching
- [07-codec-integration.md](07-codec-integration.md) - Codec system integration
