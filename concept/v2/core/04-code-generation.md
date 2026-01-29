# Code Generation System

## Overview

The code generation system is implemented as a Gradle plugin that processes YAML schema files at compile-time to generate type-safe Java code. The generator produces entity classes, table references, query builders, and mappers.

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

        // Table reference generation
        generateTables = true
        generateQueryBuilders = true

        // Codec generation
        generateCodecs = true
        codecRegistryClass = "Codecs"

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
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Parse     │───▶│  Validate   │───▶│   Resolve   │───▶│  Generate   │
│   YAML      │    │   Schema    │    │   Types     │    │   Code      │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       │                  │                  │                  │
       ▼                  ▼                  ▼                  ▼
   SchemaAST       ValidationResult    ResolvedSchema     GeneratedFiles
```

### 1. Parse Stage

Reads YAML files and produces an Abstract Syntax Tree:

```java
public final class SchemaParser {

    public SchemaAST parse(Path schemaFile) throws SchemaParseException {
        String content = Files.readString(schemaFile);
        RawSchema raw = yamlMapper.readValue(content, RawSchema.class);
        return SchemaAST.from(raw, schemaFile);
    }

    public List<SchemaAST> parseAll(Path schemaDir) throws SchemaParseException {
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
public final class SchemaValidator {

    private final List<ValidationRule> rules = List.of(
        new TypeValidationRule(),
        new RelationshipValidationRule(),
        new ConstraintValidationRule(),
        new NamingConflictRule(),
        new CircularDependencyRule()
    );

    public ValidationResult validate(SchemaAST schema) {
        List<ValidationError> errors = new ArrayList<>();
        List<ValidationWarning> warnings = new ArrayList<>();

        for (ValidationRule rule : rules) {
            rule.validate(schema, errors::add, warnings::add);
        }

        return new ValidationResult(errors, warnings);
    }
}
```

### 3. Resolve Stage

Maps Java types to SQL types based on dialect:

```java
public final class TypeResolver {

    private final TypeRegistry typeRegistry;
    private final Dialect dialect;

    public ResolvedSchema resolve(SchemaAST schema) {
        List<ResolvedField> fields = schema.fields().stream()
            .map(this::resolveField)
            .toList();

        List<ResolvedRelationship> relationships = schema.relationships().stream()
            .map(this::resolveRelationship)
            .toList();

        return new ResolvedSchema(
            schema.entityName(),
            schema.tableName(),
            fields,
            relationships
        );
    }

    private ResolvedField resolveField(FieldAST field) {
        JavaType javaType = typeRegistry.resolve(field.type());
        String sqlType = dialect.getTypeMapping().toSqlType(javaType.rawClass(), field.typeOptions());

        return new ResolvedField(
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
public final class CodeGenerator {

    public GenerationResult generate(ResolvedSchema schema, GenerationConfig config) {
        List<GeneratedFile> files = new ArrayList<>();

        // Generate entity
        files.add(entityGenerator.generate(schema, config));

        // Generate builder (if entity style is RECORD)
        if (config.entityStyle() == EntityStyle.RECORD && config.generateBuilders()) {
            files.add(entityGenerator.generateBuilder(schema, config));
        }

        // Generate table reference class
        if (config.generateTables()) {
            files.add(tableGenerator.generate(schema, config));
        }

        // Generate codec
        if (config.generateCodecs()) {
            files.add(codecGenerator.generate(schema, config));
        }

        return new GenerationResult(files);
    }
}
```

## Generated Entity (Record Style)

```java
// Generated from user.yaml
package com.example.db.entity;

import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Generated entity for the {@code users} table.<br>
 *
 * @param id The unique identifier
 * @param email The user's email address
 * @param name The user's name
 * @param status The user's account status
 * @param createdAt When the user was created
 * @param address The user's address (relationship)
 * @author LUtils-DB Generator
 */
@NullMarked
@Entity(table = "users", schema = "public")
public record User(
    @Id @Generated UUID id,
    @Column("email") @Unique String email,
    @Column("name") String name,
    @Enumerated UserStatus status,
    @Column("created_at") @Insertable @ReadOnly Instant createdAt,
    @Relationship(loading = LoadingStrategy.LAZY)
    SqlKey<UUID, Address> address
) {

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder toBuilder() {
        return new UserBuilder(this);
    }

    public void validate() throws ConstraintViolationException {
        UserValidator.INSTANCE.validate(this);
    }

    public static Codec<User> codec() {
        return UserCodec.INSTANCE;
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
    private @Nullable SqlKey<UUID, Address> address;

    UserBuilder() {}

    UserBuilder(User user) {
        this.id = user.id();
        this.email = user.email();
        this.name = user.name();
        this.status = user.status();
        this.createdAt = user.createdAt();
        this.address = user.address();
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

    public UserBuilder address(SqlKey<UUID, Address> address) {
        this.address = address;
        return this;
    }

    public UserBuilder addressId(UUID addressId) {
        this.address = SqlKey.of(addressId);
        return this;
    }

    public User build() {
        if (email == null) {
            throw new IllegalStateException("email is required");
        }
        if (name == null) {
            throw new IllegalStateException("name is required");
        }
        return new User(id, email, name, status, createdAt, address);
    }

    public User buildValidated() throws ConstraintViolationException {
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
 * Table reference for {@code users}.<br>
 *
 * @author LUtils-DB Generator
 */
public final class UserTable {

    private UserTable() {}

    public static final SqlTable<User> TABLE = SqlTable.of("users", User.class);

    // Primary key
    public static final SqlColumn<User, UUID> ID =
        TABLE.column("id", UUID.class);

    // Regular columns
    public static final SqlColumn<User, String> EMAIL =
        TABLE.column("email", String.class);

    public static final SqlColumn<User, String> NAME =
        TABLE.column("name", String.class);

    public static final SqlColumn<User, UserStatus> STATUS =
        TABLE.column("status", UserStatus.class);

    public static final SqlColumn<User, Instant> CREATED_AT =
        TABLE.column("created_at", Instant.class);

    // Relationships (using SqlKey pattern)
    public static final SqlRelationship<User, Address> ADDRESS =
        TABLE.relationship("address_id", Address.class, UUID.class,
            AddressTable.TABLE, LoadingStrategy.LAZY);

    // All columns
    public static final List<SqlColumn<User, ?>> ALL_COLUMNS = List.of(
        ID, EMAIL, NAME, STATUS, CREATED_AT
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

## Generated Codec

```java
package com.example.db.entity;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.CodecBuilder;
import net.luis.utils.io.codec.Codecs;

/**
 * Codec for serializing/deserializing {@link User}.<br>
 *
 * @author LUtils-DB Generator
 */
public final class UserCodec {

    public static final Codec<User> INSTANCE = CodecBuilder.of(
        Codecs.UUID.fieldOf("id").optional(),
        Codecs.STRING.fieldOf("email"),
        Codecs.STRING.fieldOf("name"),
        UserStatusCodec.INSTANCE.fieldOf("status").withDefault(UserStatus.PENDING),
        Codecs.INSTANT.fieldOf("createdAt").optional(),
        SqlKeyCodec.of(Codecs.UUID, AddressCodec.INSTANCE).fieldOf("address").nullable(),
        User::new
    );

    private UserCodec() {}
}
```

## Generated Mapper

```java
package com.example.db.mapper;

/**
 * Maps ResultSet rows to {@link User} entities.<br>
 *
 * @author LUtils-DB Generator
 */
public final class UserMapper implements EntityMapper<User> {

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
    public void bindParameters(PreparedStatement ps, User entity, int startIndex)
            throws SQLException {
        ps.setObject(startIndex, entity.id());
        ps.setString(startIndex + 1, entity.email());
        ps.setString(startIndex + 2, entity.name());
        ps.setString(startIndex + 3, entity.status().name());
        ps.setObject(startIndex + 4, entity.createdAt());
        ps.setObject(startIndex + 5, entity.address() != null ? entity.address().key() : null);
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
public @interface Entity {
    String table();
    String schema() default "";
}

// Primary key
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Id {}

// Generated value
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Generated {
    GenerationType strategy() default GenerationType.AUTO;
}

// Column mapping
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Column {
    String value();
}

// Relationship with loading strategy
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Relationship {
    LoadingStrategy loading() default LoadingStrategy.LAZY;
}

// Loading strategies
public enum LoadingStrategy {
    LAZY,   // Load on demand via explicit join or separate query
    EAGER   // Always load via join
}
```

## Incremental Build Support

The plugin supports incremental builds for faster compilation:

```kotlin
abstract class GenerateDbSourcesTask : DefaultTask() {

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

- [01-overview.md](01-overview.md) - Architecture overview
- [02-yaml-schema.md](02-yaml-schema.md) - YAML schema specification
- [03-query-api.md](03-query-api.md) - Using generated code
