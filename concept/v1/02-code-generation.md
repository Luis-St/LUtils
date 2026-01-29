# Code Generation System

## Overview

The code generation system is implemented as a Gradle plugin that processes YAML schema files at compile-time to generate type-safe Java code. The generator produces entity classes, repositories, and query builders.

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
    repositoryPackage = "repository"   // -> com.example.db.repository
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

        // Repository generation
        generateRepositories = true
        generateQueryBuilders = true

        // Codec generation
        generateCodecs = true
        codecRegistryClass = "Codecs"
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
generateRepositories - Generate repository interfaces only
generateQueries - Generate query builder interfaces only
validateSchema - Validate YAML schema files
cleanDbSources - Clean generated database sources
```

### Incremental Build Support

The plugin supports incremental builds:

```kotlin
// Plugin implementation
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

        // Process changed schemas
        changedFiles.forEach { schemaFile ->
            generateForSchema(schemaFile)
        }
    }
}
```

## Generation Pipeline

### Pipeline Stages

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

```java
public final class SchemaParser {

    private final YamlMapper yamlMapper;

    public SchemaAST parse(Path schemaFile) throws SchemaParseException {
        try {
            String content = Files.readString(schemaFile);
            RawSchema raw = yamlMapper.readValue(content, RawSchema.class);
            return SchemaAST.from(raw, schemaFile);
        } catch (IOException e) {
            throw new SchemaParseException("Failed to parse schema: " + schemaFile, e);
        }
    }

    public List<SchemaAST> parseAll(Path schemaDir) throws SchemaParseException {
        try (Stream<Path> paths = Files.walk(schemaDir)) {
            return paths
                .filter(path -> path.toString().endsWith(".yaml"))
                .filter(path -> !path.getFileName().toString().startsWith("_"))
                .map(this::parse)
                .toList();
        } catch (IOException e) {
            throw new SchemaParseException("Failed to scan schema directory", e);
        }
    }
}
```

### 2. Validate Stage

```java
public final class SchemaValidator {

    private final List<ValidationRule> rules;

    public ValidationResult validate(SchemaAST schema) {
        List<ValidationError> errors = new ArrayList<>();
        List<ValidationWarning> warnings = new ArrayList<>();

        for (ValidationRule rule : rules) {
            rule.validate(schema, errors::add, warnings::add);
        }

        return new ValidationResult(errors, warnings);
    }
}

// Validation rules
public sealed interface ValidationRule {
    void validate(SchemaAST schema,
                  Consumer<ValidationError> errors,
                  Consumer<ValidationWarning> warnings);
}

public final class TypeValidationRule implements ValidationRule {
    @Override
    public void validate(SchemaAST schema,
                        Consumer<ValidationError> errors,
                        Consumer<ValidationWarning> warnings) {
        for (FieldAST field : schema.fields()) {
            if (!TypeRegistry.isKnownType(field.type())) {
                errors.accept(new ValidationError(
                    field.location(),
                    "Unknown field type: " + field.type()
                ));
            }
        }
    }
}
```

### 3. Resolve Stage

```java
public final class TypeResolver {

    private final TypeRegistry typeRegistry;
    private final DialectTypeMapper dialectMapper;

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
        SqlType sqlType = dialectMapper.mapToSql(javaType, field.columnType());
        ConstraintConfig<?> constraints = resolveConstraints(field);

        return new ResolvedField(
            field.name(),
            field.columnName(),
            javaType,
            sqlType,
            constraints,
            field.options()
        );
    }
}
```

### 4. Generate Stage

```java
public final class CodeGenerator {

    private final EntityGenerator entityGenerator;
    private final RepositoryGenerator repositoryGenerator;
    private final QueryGenerator queryGenerator;
    private final CodecGenerator codecGenerator;

    public GenerationResult generate(ResolvedSchema schema, GenerationConfig config) {
        List<GeneratedFile> files = new ArrayList<>();

        // Generate entity
        files.add(entityGenerator.generate(schema, config));

        // Generate builder (if entity style is RECORD)
        if (config.entityStyle() == EntityStyle.RECORD && config.generateBuilders()) {
            files.add(entityGenerator.generateBuilder(schema, config));
        }

        // Generate repository
        if (config.generateRepositories()) {
            files.add(repositoryGenerator.generate(schema, config));
        }

        // Generate query builder
        if (config.generateQueryBuilders()) {
            files.add(queryGenerator.generate(schema, config));
        }

        // Generate codec
        if (config.generateCodecs()) {
            files.add(codecGenerator.generate(schema, config));
        }

        return new GenerationResult(files);
    }
}
```

## Entity Generation

### Record-Style Entity

```java
// Generated from user.yaml
package com.example.db.entity;

import net.luis.utils.io.codec.Codec;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Generated entity for the {@code users} table.<br>
 *
 * @param id The unique identifier
 * @param email The user's email address
 * @param status The user's account status
 * @param createdAt The timestamp when the user was created
 * @param lastLoginAt The timestamp of the last login (nullable)
 * @author LUtils-DB Generator
 */
@NullMarked
@Entity(table = "users", schema = "public")
public record User(
    @Id @Generated UUID id,
    @Column("email_address") @Unique String email,
    @Column("password_hash") @Select(false) String passwordHash,
    @Enumerated UserStatus status,
    @Column("created_at") @Insertable @ReadOnly Instant createdAt,
    @Column("last_login_at") @Nullable Instant lastLoginAt
) {

    /**
     * Creates a new builder for constructing {@link User} instances.<br>
     *
     * @return A new user builder
     */
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    /**
     * Creates a builder initialized with this user's values.<br>
     *
     * @return A builder with current values
     */
    public UserBuilder toBuilder() {
        return new UserBuilder(this);
    }

    /**
     * Validates this user against defined constraints.<br>
     *
     * @throws ConstraintViolationException If validation fails
     */
    public void validate() throws ConstraintViolationException {
        UserValidator.INSTANCE.validate(this);
    }

    /**
     * Returns the codec for serializing/deserializing users.<br>
     *
     * @return The user codec
     */
    public static Codec<User> codec() {
        return UserCodec.INSTANCE;
    }
}
```

### Generated Builder

```java
package com.example.db.entity;

import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Builder for constructing {@link User} instances.<br>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public final class UserBuilder {

    private @Nullable UUID id;
    private @Nullable String email;
    private @Nullable String passwordHash;
    private UserStatus status = UserStatus.PENDING;
    private @Nullable Instant createdAt;
    private @Nullable Instant lastLoginAt;

    UserBuilder() {}

    UserBuilder(User user) {
        this.id = user.id();
        this.email = user.email();
        this.passwordHash = user.passwordHash();
        this.status = user.status();
        this.createdAt = user.createdAt();
        this.lastLoginAt = user.lastLoginAt();
    }

    public UserBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder passwordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public UserBuilder lastLoginAt(@Nullable Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }

    /**
     * Builds the user instance.<br>
     *
     * @return The constructed user
     * @throws IllegalStateException If required fields are missing
     */
    public User build() {
        if (email == null) {
            throw new IllegalStateException("email is required");
        }
        if (passwordHash == null) {
            throw new IllegalStateException("passwordHash is required");
        }
        return new User(id, email, passwordHash, status, createdAt, lastLoginAt);
    }

    /**
     * Builds and validates the user instance.<br>
     *
     * @return The constructed and validated user
     * @throws IllegalStateException If required fields are missing
     * @throws ConstraintViolationException If validation fails
     */
    public User buildValidated() throws ConstraintViolationException {
        User user = build();
        user.validate();
        return user;
    }
}
```

### POJO-Style Entity

```java
// Generated with entityStyle = POJO
package com.example.db.entity;

@NullMarked
@Entity(table = "users", schema = "public")
public final class User {

    @Id @Generated
    private UUID id;

    @Column("email_address") @Unique
    private String email;

    @Column("password_hash") @Select(false)
    private String passwordHash;

    @Enumerated
    private UserStatus status = UserStatus.PENDING;

    @Column("created_at") @Insertable @ReadOnly
    private Instant createdAt;

    @Column("last_login_at") @Nullable
    private Instant lastLoginAt;

    public User() {}

    // All-args constructor
    public User(UUID id, String email, String passwordHash,
                UserStatus status, Instant createdAt, @Nullable Instant lastLoginAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public @Nullable Instant getLastLoginAt() { return lastLoginAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setLastLoginAt(@Nullable Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    // equals, hashCode, toString...
}
```

## Repository Generation

```java
package com.example.db.repository;

import com.example.db.entity.User;
import com.example.db.query.UserQuery;
import net.luis.utils.db.core.Repository;
import net.luis.utils.db.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;

/**
 * Repository interface for {@link User} entities.<br>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public interface UserRepository extends Repository<User, UUID> {

    // Primary key operations

    /**
     * Finds a user by their unique identifier.<br>
     *
     * @param id The user ID
     * @return The user
     * @throws EntityNotFoundException If no user exists with the given ID
     */
    User findById(UUID id) throws EntityNotFoundException;

    /**
     * Finds a user by their unique identifier, if it exists.<br>
     *
     * @param id The user ID
     * @return An optional containing the user, or empty if not found
     */
    Optional<User> findByIdOptional(UUID id);

    /**
     * Checks if a user with the given ID exists.<br>
     *
     * @param id The user ID
     * @return True if the user exists
     */
    boolean existsById(UUID id);

    // Unique field operations (generated from unique constraints)

    /**
     * Finds a user by their email address.<br>
     *
     * @param email The email address
     * @return An optional containing the user, or empty if not found
     */
    Optional<User> findByEmail(String email);

    // Collection operations

    /**
     * Finds all users.<br>
     *
     * @return All users
     */
    List<User> findAll();

    /**
     * Counts all users.<br>
     *
     * @return The count
     */
    long count();

    // Persistence operations

    /**
     * Saves a user (insert or update).<br>
     *
     * @param user The user to save
     * @return The saved user with generated fields populated
     */
    User save(User user);

    /**
     * Saves multiple users.<br>
     *
     * @param users The users to save
     * @return The saved users
     */
    List<User> saveAll(Iterable<User> users);

    /**
     * Deletes a user.<br>
     *
     * @param user The user to delete
     */
    void delete(User user);

    /**
     * Deletes a user by ID.<br>
     *
     * @param id The user ID
     */
    void deleteById(UUID id);

    // Query builder

    /**
     * Creates a new query builder for users.<br>
     *
     * @return A user query builder
     */
    UserQuery query();
}
```

## Query Builder Generation

```java
package com.example.db.query;

import com.example.db.entity.User;
import com.example.db.entity.UserStatus;
import net.luis.utils.db.query.Query;
import net.luis.utils.db.query.Order;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;

/**
 * Type-safe query builder for {@link User} entities.<br>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public interface UserQuery extends Query<User> {

    // ID conditions
    UserQuery whereId(UUID id);
    UserQuery whereIdIn(Collection<UUID> ids);
    UserQuery whereIdNot(UUID id);
    UserQuery whereIdNotIn(Collection<UUID> ids);

    // Email conditions (String field)
    UserQuery whereEmail(String email);
    UserQuery whereEmailIn(Collection<String> emails);
    UserQuery whereEmailNot(String email);
    UserQuery whereEmailLike(String pattern);
    UserQuery whereEmailNotLike(String pattern);
    UserQuery whereEmailStartsWith(String prefix);
    UserQuery whereEmailEndsWith(String suffix);
    UserQuery whereEmailContains(String substring);
    UserQuery whereEmailIsNull();
    UserQuery whereEmailIsNotNull();

    // Status conditions (Enum field)
    UserQuery whereStatus(UserStatus status);
    UserQuery whereStatusIn(Collection<UserStatus> statuses);
    UserQuery whereStatusNot(UserStatus status);
    UserQuery whereStatusNotIn(Collection<UserStatus> statuses);

    // CreatedAt conditions (Temporal field)
    UserQuery whereCreatedAt(Instant instant);
    UserQuery whereCreatedAtBefore(Instant instant);
    UserQuery whereCreatedAtAfter(Instant instant);
    UserQuery whereCreatedAtBetween(Instant start, Instant end);
    UserQuery whereCreatedAtIsNull();
    UserQuery whereCreatedAtIsNotNull();

    // LastLoginAt conditions (Nullable temporal field)
    UserQuery whereLastLoginAt(Instant instant);
    UserQuery whereLastLoginAtBefore(Instant instant);
    UserQuery whereLastLoginAtAfter(Instant instant);
    UserQuery whereLastLoginAtBetween(Instant start, Instant end);
    UserQuery whereLastLoginAtIsNull();
    UserQuery whereLastLoginAtIsNotNull();

    // Logical operators
    UserQuery and();
    UserQuery or();
    UserQuery not();

    // Grouping
    UserQuery beginGroup();
    UserQuery endGroup();

    // Ordering
    UserQuery orderById(Order order);
    UserQuery orderByEmail(Order order);
    UserQuery orderByStatus(Order order);
    UserQuery orderByCreatedAt(Order order);
    UserQuery orderByLastLoginAt(Order order);

    // Pagination
    UserQuery limit(int limit);
    UserQuery offset(int offset);

    // Distinct
    UserQuery distinct();

    // Locking
    UserQuery forUpdate();
    UserQuery forShare();

    // Terminal operations
    List<User> fetch();
    Optional<User> fetchFirst();
    User fetchOne() throws EntityNotFoundException;
    long count();
    boolean exists();

    // Batch operations
    int delete();
    int update(User template);
}
```

## Codec Generation

```java
package com.example.db.entity;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.CodecBuilder;
import net.luis.utils.io.codec.Codecs;
import org.jspecify.annotations.NullMarked;

/**
 * Codec for serializing and deserializing {@link User} entities.<br>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public final class UserCodec {

    public static final Codec<User> INSTANCE = CodecBuilder.of(
        Codecs.UUID.fieldOf("id").optional(),
        Codecs.STRING.fieldOf("email"),
        Codecs.STRING.fieldOf("passwordHash"),
        UserStatusCodec.INSTANCE.fieldOf("status").withDefault(UserStatus.PENDING),
        Codecs.INSTANT.fieldOf("createdAt").optional(),
        Codecs.INSTANT.fieldOf("lastLoginAt").nullable(),
        User::new
    );

    private UserCodec() {}
}
```

## Annotation Processing

The generated annotations support runtime reflection:

```java
package net.luis.utils.db.annotation;

// Entity marker
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    String table();
    String schema() default "";
}

// Primary key marker
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {}

// Generated value marker
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Generated {
    GenerationType strategy() default GenerationType.AUTO;
}

// Column mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String value();  // Column name
}

// Unique constraint
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Unique {}

// Exclude from SELECT
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Select {
    boolean value() default true;
}

// Insert-only field
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Insertable {}

// Read-only field
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReadOnly {}

// Enum field
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Enumerated {
    EnumType value() default EnumType.STRING;
}
```

## Template Engine

The generator uses a simple template engine for code generation:

```java
public final class CodeTemplate {

    private final String template;
    private final Map<String, Object> context;

    public CodeTemplate(String template) {
        this.template = template;
        this.context = new HashMap<>();
    }

    public CodeTemplate with(String key, Object value) {
        context.put(key, value);
        return this;
    }

    public String render() {
        String result = template;

        // Simple variable substitution: {{name}}
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}",
                                    String.valueOf(entry.getValue()));
        }

        // Conditional blocks: {{#if condition}}...{{/if}}
        result = processConditionals(result);

        // Loops: {{#each items}}...{{/each}}
        result = processLoops(result);

        return result;
    }
}

// Example template
private static final String ENTITY_TEMPLATE = """
    package {{package}};

    {{#each imports}}
    import {{this}};
    {{/each}}

    /**
     * Generated entity for the {@code {{tableName}}} table.<br>
     *
     {{#each fields}}
     * @param {{name}} {{description}}
     {{/each}}
     * @author LUtils-DB Generator
     */
    @NullMarked
    @Entity(table = "{{tableName}}"{{#if schema}}, schema = "{{schema}}"{{/if}})
    public record {{entityName}}(
        {{#each fields}}
        {{annotations}} {{type}} {{name}}{{#unless @last}},{{/unless}}
        {{/each}}
    ) {
        // Builder and validation methods...
    }
    """;
```

## Related Documents

- [00-overview.md](00-overview.md) - Architecture overview
- [01-yaml-schema.md](01-yaml-schema.md) - YAML schema specification
- [03-query-api.md](03-query-api.md) - Query builder details
