# Code Generation System

## Overview

The code generation system is implemented as a Gradle plugin that processes YAML schema files at compile-time to generate type-safe Java code. The generator produces entity classes (base, FullJoined, PartialJoined variants), table references, query builders, and mappers.

## Gradle Plugin

### Plugin Configuration

```kotlin
// build.gradle.kts
plugins {
    id("net.luis.db") version "1.0.0"
}

lutilsDb {
    // Schema configuration
    schemaDir = file("src/main/resources/schema")

    // Output configuration
    outputDir = file("build/generated/sources/lutils-db/java")
    resourcesDir = file("build/generated/sources/lutils-db/resources")

    // Package configuration
    basePackage = "com.example.db"
    entityPackage = "entity"           // -> com.example.db.entity
    tablePackage = "table"             // -> com.example.db.table
    queryPackage = "query"             // -> com.example.db.query

    // Dialect configuration
    dialect = "postgresql"             // postgresql, mysql, sqlite, h2

    // Generation options
    generation {
        // Entity generation
        entityStyle = EntityStyle.RECORD  // RECORD or POJO
        generateBuilders = true
        generateWithers = true
        generateValidation = true

        // Joined variant generation (always generated for entities with relationships)
        generateFullJoined = true
        generatePartialJoined = true

        // Table reference generation
        generateTables = true
        generateQueryBuilders = true

        // Row types
        maxRowCount = 8                   // Generate Row2 through Row8
    }

    // Validation options
    validation {
        strict = true
        warnUnindexedForeignKeys = true
        failOnWarning = false
    }
}
```

### Gradle Tasks

```
./gradlew tasks --group="lutils-db"

lutils-db tasks
---------------
generateDbSources - Generate all database source files
generateEntities - Generate entity classes only
generateTables - Generate table reference classes only
generateQueries - Generate query builder interfaces only
validateSchema - Validate YAML schema files
cleanDbSources - Clean generated database sources
```

## Generation Pipeline

```
+-----------+    +-----------+    +-----------+    +-----------+
|   Parse   |--->|  Validate |--->|   Resolve |--->|  Generate |
|   YAML    |    |   Schema  |    |   Types   |    |   Code    |
+-----------+    +-----------+    +-----------+    +-----------+
      |                |                |                |
      v                v                v                v
  SqlSchemaAST   SqlValidation    SqlResolved      SqlGenerated
                   Result          Schema           Files
```

### 1. Parse Stage

Reads YAML files and produces an Abstract Syntax Tree:

```java
public final class SqlSchemaParser {

    public SqlSchemaAST parse(Path schemaFile) throws SqlSchemaParseException {
        String content = Files.readString(schemaFile);
        RawSchema raw = yamlMapper.readValue(content, RawSchema.class);
        return SqlSchemaAST.from(raw, schemaFile);
    }

    public List<SqlSchemaAST> parseAll(Path schemaDir) throws SqlSchemaParseException {
        try (Stream<Path> paths = Files.walk(schemaDir)) {
            return paths
                .filter(path -> path.toString().endsWith(".yaml"))
                .filter(path -> !path.getFileName().toString().startsWith("_"))
                .map(this::parse)
                .toList();
        }
    }
}
```

### 2. Validate Stage

Validates schemas for consistency:

```java
public final class SqlSchemaValidator {

    private final List<SqlValidationRule> rules = List.of(
        new SqlTypeValidationRule(),
        new SqlRelationshipValidationRule(),
        new SqlConstraintValidationRule(),
        new SqlNamingConflictRule(),
        new SqlCircularDependencyRule()
    );

    public SqlValidationResult validate(SqlSchemaAST schema) {
        List<SqlValidationError> errors = new ArrayList<>();
        List<SqlValidationWarning> warnings = new ArrayList<>();

        for (SqlValidationRule rule : rules) {
            rule.validate(schema, errors::add, warnings::add);
        }

        return new SqlValidationResult(errors, warnings);
    }
}
```

### 3. Resolve Stage

Maps Java types to SQL types based on dialect:

```java
public final class SqlTypeResolver {

    private final SqlTypeRegistry typeRegistry;
    private final SqlDialect dialect;

    public SqlResolvedSchema resolve(SqlSchemaAST schema) {
        List<SqlResolvedField> fields = schema.fields().stream()
            .map(this::resolveField)
            .toList();

        List<SqlResolvedRelationship> relationships = schema.relationships().stream()
            .map(this::resolveRelationship)
            .toList();

        return new SqlResolvedSchema(
            schema.entityName(),
            schema.tableName(),
            fields,
            relationships
        );
    }

    private SqlResolvedField resolveField(SqlFieldAST field) {
        SqlJavaType javaType = typeRegistry.resolve(field.type());
        String sqlType = dialect.getTypeMapping().toSqlType(javaType.rawClass(), field.typeOptions());

        return new SqlResolvedField(
            field.name(),
            field.columnName(),
            javaType,
            sqlType,
            field.constraints(),
            field.options()
        );
    }
}
```

### 4. Generate Stage

Produces Java source files:

```java
public final class SqlCodeGenerator {

    public SqlGenerationResult generate(SqlResolvedSchema schema, SqlGenerationConfig config) {
        List<SqlGeneratedFile> files = new ArrayList<>();

        // Generate base entity
        files.add(entityGenerator.generate(schema, config));

        // Generate builder (if entity style is RECORD)
        if (config.entityStyle() == EntityStyle.RECORD && config.generateBuilders()) {
            files.add(entityGenerator.generateBuilder(schema, config));
        }

        // Generate FullJoined variant (for entities with relationships)
        if (config.generateFullJoined() && schema.hasRelationships()) {
            files.add(entityGenerator.generateFullJoined(schema, config));
        }

        // Generate PartialJoined variant (for entities with relationships)
        if (config.generatePartialJoined() && schema.hasRelationships()) {
            files.add(entityGenerator.generatePartialJoined(schema, config));
        }

        // Generate table reference class
        if (config.generateTables()) {
            files.add(tableGenerator.generate(schema, config));
        }

        return new SqlGenerationResult(files);
    }
}
```

## Generated Base Entity (Record Style)

```java
// Generated from user.yaml
package com.example.db.entity;

import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Generated entity for the {@code users} table.<br>
 * <p>
 *     Base entity with raw FK values. For loaded relationships,
 *     use {@link UserFullJoined} or {@link UserPartialJoined}.
 * </p>
 *
 * @param id The unique identifier
 * @param email The user's email address
 * @param name The user's name
 * @param status The user's account status
 * @param createdAt When the user was created
 * @param addressId FK reference to Address (raw value)
 * @author LUtils-DB Generator
 */
@NullMarked
@SqlEntity(table = "users", schema = "public")
public record User(
    @SqlId @SqlGenerated UUID id,
    @SqlColumn("email") @SqlUnique String email,
    @SqlColumn("name") String name,
    @SqlEnumerated UserStatus status,
    @SqlColumn("created_at") @SqlInsertable @SqlReadOnly Instant createdAt,
    @SqlColumn("address_id") @Nullable UUID addressId
) {

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder toBuilder() {
        return new UserBuilder(this);
    }

    public void validate() throws SqlConstraintViolationException {
        UserValidator.INSTANCE.validate(this);
    }
}
```

## Generated FullJoined Entity

```java
// Generated from user.yaml - FullJoined variant
package com.example.db.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * FullJoined variant of {@link User} with loaded relationships.<br>
 * <p>
 *     Use this entity when fetching with JOINs to get fully populated relationships.
 * </p>
 *
 * @param id The unique identifier
 * @param email The user's email address
 * @param name The user's name
 * @param status The user's account status
 * @param createdAt When the user was created
 * @param address Loaded Address entity (from many-to-one)
 * @param orders Loaded Order list (from one-to-many)
 * @author LUtils-DB Generator
 */
@NullMarked
@SqlEntity(table = "users", schema = "public", variant = SqlEntityVariant.FULL_JOINED)
public record UserFullJoined(
    UUID id,
    String email,
    String name,
    UserStatus status,
    Instant createdAt,
    @Nullable Address address,       // Loaded from join
    List<Order> orders               // Loaded from join
) {

    public static UserFullJoinedBuilder builder() {
        return new UserFullJoinedBuilder();
    }

    /**
     * Converts to base entity, extracting FK values from loaded relationships.
     */
    public User toBase() {
        return new User(
            id,
            email,
            name,
            status,
            createdAt,
            address != null ? address.id() : null
        );
    }
}
```

## Generated PartialJoined Entity

```java
// Generated from user.yaml - PartialJoined variant
package com.example.db.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;

/**
 * PartialJoined variant of {@link User} with SqlForeignKey wrappers.<br>
 * <p>
 *     Use this entity when you need FK values always available,
 *     with optional lazy loading of related entities.
 * </p>
 *
 * @param id The unique identifier
 * @param email The user's email address
 * @param name The user's name
 * @param status The user's account status
 * @param createdAt When the user was created
 * @param addressKey SqlForeignKey wrapper for Address relationship
 * @param ordersKey SqlForeignKey wrapper for Orders relationship
 * @author LUtils-DB Generator
 */
@NullMarked
@SqlEntity(table = "users", schema = "public", variant = SqlEntityVariant.PARTIAL_JOINED)
public record UserPartialJoined(
    UUID id,
    String email,
    String name,
    UserStatus status,
    Instant createdAt,
    SqlForeignKey<UUID, Address> addressKey,
    SqlForeignKey<UUID, List<Order>> ordersKey
) {

    public static UserPartialJoinedBuilder builder() {
        return new UserPartialJoinedBuilder();
    }

    /**
     * Converts to base entity, extracting FK values from SqlForeignKey wrappers.
     */
    public User toBase() {
        return new User(
            id,
            email,
            name,
            status,
            createdAt,
            addressKey != null ? addressKey.key() : null
        );
    }

    /**
     * Converts to FullJoined if all relationships are loaded.
     * @throws SqlRelationshipNotLoadedException if any relationship is not loaded
     */
    public UserFullJoined toFullJoined() {
        return new UserFullJoined(
            id,
            email,
            name,
            status,
            createdAt,
            addressKey != null ? addressKey.requireValue() : null,
            ordersKey != null ? ordersKey.requireValue() : List.of()
        );
    }
}
```

## Generated Builder

```java
package com.example.db.entity;

@NullMarked
public final class UserBuilder {

    private @Nullable UUID id;
    private @Nullable String email;
    private @Nullable String name;
    private UserStatus status = UserStatus.PENDING;
    private @Nullable Instant createdAt;
    private @Nullable UUID addressId;

    UserBuilder() {}

    UserBuilder(User user) {
        this.id = user.id();
        this.email = user.email();
        this.name = user.name();
        this.status = user.status();
        this.createdAt = user.createdAt();
        this.addressId = user.addressId();
    }

    public UserBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder status(UserStatus status) {
        this.status = status;
        return this;
    }

    public UserBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder addressId(UUID addressId) {
        this.addressId = addressId;
        return this;
    }

    public User build() {
        if (email == null) {
            throw new IllegalStateException("email is required");
        }
        if (name == null) {
            throw new IllegalStateException("name is required");
        }
        return new User(id, email, name, status, createdAt, addressId);
    }

    public User buildValidated() throws SqlConstraintViolationException {
        User user = build();
        user.validate();
        return user;
    }
}
```

## Generated Table Reference

```java
package com.example.db.table;

import com.example.db.entity.User;
import com.example.db.entity.UserStatus;
import com.example.db.entity.Address;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * SqlTable reference for {@code users}.<br>
 *
 * @author LUtils-DB Generator
 */
public final class UserTable {

    private UserTable() {}

    public static final SqlTable<User> TABLE = SqlTable.of("users", User.class);

    // Primary key
    public static final SqlColumn<User, UUID> ID = TABLE.column("id", UUID.class);

    // Regular columns
    public static final SqlColumn<User, String> EMAIL = TABLE.column("email", String.class);
    public static final SqlColumn<User, String> NAME = TABLE.column("name", String.class);
    public static final SqlColumn<User, UserStatus> STATUS = TABLE.column("status", UserStatus.class);
    public static final SqlColumn<User, Instant> CREATED_AT = TABLE.column("created_at", Instant.class);

    // Foreign key columns (using SqlForeignColumn for relationship metadata)
    public static final SqlForeignColumn<User, Address, UUID> ADDRESS_ID = TABLE.foreignColumn("address_id", UUID.class, AddressTable.TABLE, SqlLoadingStrategy.LAZY);

    // All columns
    public static final List<SqlColumn<User, ?>> ALL_COLUMNS = List.of(
        ID, EMAIL, NAME, STATUS, CREATED_AT, ADDRESS_ID
    );
}
```

## Generated Row Types

Based on `maxRowCount` configuration:

```java
package com.example.db.query;

/**
 * Row type for 2-column projections.<br>
 */
public record Row2<T1, T2>(T1 first, T2 second) {}

/**
 * Row type for 3-column projections.<br>
 */
public record Row3<T1, T2, T3>(T1 first, T2 second, T3 third) {}

/**
 * Row type for 4-column projections.<br>
 */
public record Row4<T1, T2, T3, T4>(T1 first, T2 second, T3 third, T4 fourth) {}

// ... up to Row8 (or configured max)
```

## Generated Mapper

```java
package com.example.db.mapper;

/**
 * Maps ResultSet rows to {@link User} entities.<br>
 *
 * @author LUtils-DB Generator
 */
public final class UserMapper implements SqlEntityMapper<User> {

    public static final UserMapper INSTANCE = new UserMapper();

    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
            .id(rs.getObject("id", UUID.class))
            .email(rs.getString("email"))
            .name(rs.getString("name"))
            .status(UserStatus.valueOf(rs.getString("status")))
            .createdAt(rs.getObject("created_at", Instant.class))
            .addressId(rs.getObject("address_id", UUID.class))
            .build();
    }

    @Override
    public void bindParameters(PreparedStatement ps, User entity, int startIndex) throws SQLException {
        ps.setObject(startIndex, entity.id());
        ps.setString(startIndex + 1, entity.email());
        ps.setString(startIndex + 2, entity.name());
        ps.setString(startIndex + 3, entity.status().name());
        ps.setObject(startIndex + 4, entity.createdAt());
        ps.setObject(startIndex + 5, entity.addressId());
    }

    @Override
    public List<String> getColumnNames() {
        return List.of("id", "email", "name", "status", "created_at", "address_id");
    }
}
```

## Annotations

Generated annotations for runtime reflection:

```java
// Entity marker
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SqlEntity {
    String table();
    String schema() default "";
    SqlEntityVariant variant() default SqlEntityVariant.BASE;
}

public enum SqlEntityVariant {
    BASE,
    FULL_JOINED,
    PARTIAL_JOINED
}

// Primary key
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface SqlId {}

// Generated value
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface SqlGenerated {
    SqlGenerationType strategy() default SqlGenerationType.AUTO;
}

// Column mapping
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface SqlColumn {
    String value();
}
```

## Incremental Build Support

The plugin supports incremental builds for faster compilation:

```kotlin
abstract class GenerateSqlSourcesTask : DefaultTask() {

    @get:InputDirectory
    abstract val schemaDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val basePackage: Property<String>

    @TaskAction
    fun generate() {
        // Only regenerate changed files
        val changedFiles = inputs.inputChanges
            .getFileChanges(schemaDir)
            .filter { it.changeType != ChangeType.REMOVED }
            .map { it.file }

        changedFiles.forEach { schemaFile ->
            generateForSchema(schemaFile)
        }
    }
}
```

## Related Documents

- [02-yaml-schema.md](02-yaml-schema.md) - YAML schema specification
- [03-query-api.md](03-query-api.md) - Using generated code
