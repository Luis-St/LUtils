# Codec System Integration

## Overview

The database module integrates with the existing LUtils Codec system for serialization, constraint validation, and type mapping. This enables seamless conversion between database records, Java objects, and various serialization formats (JSON, XML, etc.).

## Integration Points

### Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      Database Module                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐  │
│  │ YAML Schema  │───▶│  Generator   │───▶│ Generated Entity │  │
│  └──────────────┘    └──────────────┘    └────────┬─────────┘  │
│                                                    │             │
│                           ┌────────────────────────┤             │
│                           │                        │             │
│                           ▼                        ▼             │
│                   ┌──────────────┐        ┌──────────────┐      │
│                   │Generated     │        │ Generated    │      │
│                   │ Codec        │        │ Validator    │      │
│                   └──────┬───────┘        └──────┬───────┘      │
│                          │                        │              │
└──────────────────────────┼────────────────────────┼──────────────┘
                           │                        │
                           ▼                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                    LUtils Core Library                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────┐         ┌─────────────────────────────┐   │
│  │   Codec System   │         │    Constraint System        │   │
│  │                  │         │                             │   │
│  │ • Encoder        │         │ • ConstraintConfig          │   │
│  │ • Decoder        │         │ • Constraint interfaces     │   │
│  │ • FieldCodec     │         │ • Constraint builders       │   │
│  │ • CodecBuilder   │         │                             │   │
│  └──────────────────┘         └─────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Codec Generation

### Generated Entity Codec

From YAML schema:

```yaml
# user.yaml
entity: User
table: users

fields:
  id:
    type: UUID
    primary: true
  email:
    type: String
    constraints:
      notBlank: true
      maxLength: 255
  status:
    type: UserStatus
    enum: true
  createdAt:
    type: Instant
    column: created_at
  metadata:
    type: Map<String, String>
    columnType: JSONB
    nullable: true
```

Generated codec:

```java
package com.example.db.entity;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.CodecBuilder;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.FieldCodec;
import org.jspecify.annotations.NullMarked;

/**
 * Codec for serializing and deserializing {@link User} entities.<br>
 * <p>
 *     Supports JSON, XML, and other formats via the LUtils Codec system.<br>
 *     Includes constraint validation during encoding/decoding.<br>
 * </p>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public final class UserCodec {

    /**
     * The singleton codec instance for {@link User} entities.<br>
     */
    public static final Codec<User> INSTANCE = createCodec();

    private UserCodec() {}

    private static Codec<User> createCodec() {
        // UUID field codec
        FieldCodec<UUID, User> idCodec = Codecs.UUID
            .fieldOf("id")
            .getter(User::id);

        // String field codec with constraints
        FieldCodec<String, User> emailCodec = Codecs.STRING
            .notBlank()
            .maxLength(255)
            .fieldOf("email")
            .getter(User::email);

        // Enum field codec
        FieldCodec<UserStatus, User> statusCodec = UserStatusCodec.INSTANCE
            .fieldOf("status")
            .getter(User::status);

        // Instant field codec
        FieldCodec<Instant, User> createdAtCodec = Codecs.INSTANT
            .fieldOf("createdAt")
            .getter(User::createdAt);

        // Nullable Map field codec
        FieldCodec<Map<String, String>, User> metadataCodec = Codecs.STRING
            .mapOf(Codecs.STRING)
            .nullable()
            .fieldOf("metadata")
            .getter(User::metadata);

        // Build composite codec
        return CodecBuilder.of(
            idCodec,
            emailCodec,
            statusCodec,
            createdAtCodec,
            metadataCodec,
            User::new
        );
    }

    /**
     * Creates a codec with custom field names for database column mapping.<br>
     *
     * @return A codec using database column names
     */
    public static Codec<User> databaseCodec() {
        return CodecBuilder.of(
            Codecs.UUID.fieldOf("id").getter(User::id),
            Codecs.STRING.notBlank().maxLength(255).fieldOf("email_address").getter(User::email),
            UserStatusCodec.INSTANCE.fieldOf("status").getter(User::status),
            Codecs.INSTANT.fieldOf("created_at").getter(User::createdAt),
            Codecs.STRING.mapOf(Codecs.STRING).nullable().fieldOf("metadata").getter(User::metadata),
            User::new
        );
    }
}
```

### Enum Codec Generation

```yaml
# _enums/user_status.yaml
enum: UserStatus
values:
  - PENDING
  - ACTIVE
  - SUSPENDED
  - DELETED
```

Generated code:

```java
// Enum definition
package com.example.db.entity;

/**
 * User account status enumeration.<br>
 *
 * @author LUtils-DB Generator
 */
public enum UserStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    DELETED
}

// Codec for enum
package com.example.db.entity;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import org.jspecify.annotations.NullMarked;

/**
 * Codec for {@link UserStatus} enumeration.<br>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public final class UserStatusCodec {

    public static final Codec<UserStatus> INSTANCE = Codecs.STRING
        .xmap(
            UserStatus::valueOf,
            UserStatus::name
        )
        .named("UserStatus");

    private UserStatusCodec() {}
}
```

## Constraint Integration

### YAML Constraints to Codec Constraints

| YAML Constraint | Codec Method | Constraint Type |
|-----------------|--------------|-----------------|
| `notNull: true` | `.notNull()` | `BaseConstraint` |
| `notBlank: true` | `.notBlank()` | `CharSequenceConstraint` |
| `minLength: n` | `.minLength(n)` | `LengthConstraint` |
| `maxLength: n` | `.maxLength(n)` | `LengthConstraint` |
| `pattern: regex` | `.matches(regex)` | `CharSequenceConstraint` |
| `min: n` | `.min(n)` | `ComparableConstraint` |
| `max: n` | `.max(n)` | `ComparableConstraint` |
| `range: [a, b]` | `.between(a, b)` | `ComparableConstraint` |
| `positive: true` | `.positive()` | `SignedConstraint` |
| `negative: true` | `.negative()` | `SignedConstraint` |
| `minSize: n` | `.minSize(n)` | `SizeConstraint` |
| `maxSize: n` | `.maxSize(n)` | `SizeConstraint` |
| `notEmpty: true` | `.notEmpty()` | `SizeConstraint` |

### Generated Validator

```java
package com.example.db.entity;

import net.luis.utils.io.codec.constraint.config.*;
import org.jspecify.annotations.NullMarked;

/**
 * Validator for {@link User} entities using constraint configs.<br>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public final class UserValidator {

    public static final UserValidator INSTANCE = new UserValidator();

    private final StringConstraintConfig<String> emailConstraints;

    private UserValidator() {
        this.emailConstraints = StringConstraintConfig.<String>unconstrained()
            .withNotBlank(true)
            .withMaxLength(255);
    }

    /**
     * Validates a user entity against all defined constraints.<br>
     *
     * @param user The user to validate
     * @throws ConstraintViolationException If validation fails
     */
    public void validate(User user) throws ConstraintViolationException {
        // Validate email
        if (!emailConstraints.matches(user.email())) {
            throw new ConstraintViolationException("email", "Email validation failed");
        }

        // Validate other fields...
    }

    /**
     * Validates a single field.<br>
     *
     * @param fieldName The field name
     * @param value The field value
     * @throws ConstraintViolationException If validation fails
     */
    public void validateField(String fieldName, Object value) throws ConstraintViolationException {
        switch (fieldName) {
            case "email" -> {
                if (!emailConstraints.matches((String) value)) {
                    throw new ConstraintViolationException(fieldName, "Email validation failed");
                }
            }
            default -> { /* No constraints */ }
        }
    }
}
```

## Database Type Codecs

### JDBC Result Set Codec

```java
package net.luis.utils.db.codec;

import net.luis.utils.io.codec.provider.TypeProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jspecify.annotations.NullMarked;

/**
 * Type provider for JDBC ResultSet operations.<br>
 *
 * @author Luis-St
 */
@NullMarked
public final class ResultSetOps implements TypeProvider<ResultSetValue> {

    public static final ResultSetOps INSTANCE = new ResultSetOps();

    private ResultSetOps() {}

    @Override
    public ResultSetValue createMap() {
        return new ResultSetMapValue();
    }

    @Override
    public ResultSetValue createList() {
        return new ResultSetListValue();
    }

    @Override
    public String getStringValue(ResultSetValue value) throws CodecException {
        return value.getString();
    }

    @Override
    public Number getNumberValue(ResultSetValue value) throws CodecException {
        return value.getNumber();
    }

    @Override
    public Boolean getBooleanValue(ResultSetValue value) throws CodecException {
        return value.getBoolean();
    }

    // ... other methods
}

/**
 * Wrapper for ResultSet values supporting codec operations.<br>
 */
@NullMarked
public sealed interface ResultSetValue {

    String getString() throws CodecException;
    Number getNumber() throws CodecException;
    Boolean getBoolean() throws CodecException;
    byte[] getBytes() throws CodecException;

    record ColumnValue(ResultSet rs, String column) implements ResultSetValue {
        @Override
        public String getString() throws CodecException {
            try {
                return rs.getString(column);
            } catch (SQLException e) {
                throw new CodecException("Failed to get string from column: " + column, e);
            }
        }

        @Override
        public Number getNumber() throws CodecException {
            try {
                return rs.getObject(column, Number.class);
            } catch (SQLException e) {
                throw new CodecException("Failed to get number from column: " + column, e);
            }
        }

        // ... other methods
    }
}
```

### Entity Mapping with Codecs

```java
package net.luis.utils.db.mapping;

import net.luis.utils.io.codec.Codec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NullMarked;

/**
 * Maps database rows to entity objects using codecs.<br>
 *
 * @param <T> The entity type
 * @author Luis-St
 */
@NullMarked
public final class CodecEntityMapper<T> implements EntityMapper<T> {

    private final Codec<T> codec;
    private final ResultSetOps ops;

    public CodecEntityMapper(Codec<T> codec) {
        this.codec = codec;
        this.ops = ResultSetOps.INSTANCE;
    }

    @Override
    public T map(ResultSet rs) throws MappingException {
        ResultSetValue value = new ResultSetValue.RowValue(rs);
        return codec.decodeStart(ops, value);
    }

    @Override
    public List<T> mapAll(ResultSet rs) throws MappingException {
        List<T> results = new ArrayList<>();
        try {
            while (rs.next()) {
                results.add(map(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new MappingException("Failed to iterate result set", e);
        }
    }
}
```

## JSON Column Handling

### JSONB Field Codec

```java
package net.luis.utils.db.codec;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonOps;
import org.jspecify.annotations.NullMarked;

/**
 * Codec for JSONB database columns.<br>
 *
 * @param <T> The Java type stored in JSONB
 * @author Luis-St
 */
@NullMarked
public final class JsonbCodec<T> implements Codec<T> {

    private final Codec<T> valueCodec;

    public JsonbCodec(Codec<T> valueCodec) {
        this.valueCodec = valueCodec;
    }

    /**
     * Encodes value to JSON string for database storage.<br>
     *
     * @param value The value to encode
     * @return JSON string
     */
    public String encodeToJson(T value) {
        JsonElement element = valueCodec.encodeStart(JsonOps.INSTANCE, value);
        return element.toString();
    }

    /**
     * Decodes value from JSON string retrieved from database.<br>
     *
     * @param json The JSON string from database
     * @return Decoded value
     * @throws CodecException If decoding fails
     */
    public T decodeFromJson(String json) throws CodecException {
        JsonElement element = JsonParser.parseString(json);
        return valueCodec.decodeStart(JsonOps.INSTANCE, element);
    }
}

// Usage in generated entity
public record User(
    UUID id,
    String email,
    @JsonColumn Map<String, Object> settings
) {

    // Custom codec for settings field
    private static final JsonbCodec<Map<String, Object>> SETTINGS_CODEC =
        new JsonbCodec<>(Codecs.STRING.mapOf(Codecs.DYNAMIC));
}
```

### Complex JSONB Types

```yaml
# user.yaml
fields:
  preferences:
    type: UserPreferences
    columnType: JSONB
    codec: UserPreferencesCodec
```

```java
// Embedded type
public record UserPreferences(
    String theme,
    boolean notificationsEnabled,
    List<String> favoriteCategories,
    Map<String, String> customSettings
) {}

// Generated codec for embedded type
public final class UserPreferencesCodec {

    public static final Codec<UserPreferences> INSTANCE = CodecBuilder.of(
        Codecs.STRING.fieldOf("theme").withDefault("light").getter(UserPreferences::theme),
        Codecs.BOOLEAN.fieldOf("notificationsEnabled").withDefault(true)
            .getter(UserPreferences::notificationsEnabled),
        Codecs.STRING.listOf().fieldOf("favoriteCategories").withDefault(List.of())
            .getter(UserPreferences::favoriteCategories),
        Codecs.STRING.mapOf(Codecs.STRING).fieldOf("customSettings").withDefault(Map.of())
            .getter(UserPreferences::customSettings),
        UserPreferences::new
    );
}
```

## Codec Registry Integration

### Auto-Registration

```java
package com.example.db;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import org.jspecify.annotations.NullMarked;

/**
 * Registry of all generated database entity codecs.<br>
 *
 * @author LUtils-DB Generator
 */
@NullMarked
public final class DatabaseCodecs {

    static {
        // Register all generated codecs
        Codecs.register(User.class, UserCodec.INSTANCE);
        Codecs.register(Post.class, PostCodec.INSTANCE);
        Codecs.register(Comment.class, CommentCodec.INSTANCE);
        Codecs.register(UserStatus.class, UserStatusCodec.INSTANCE);
        Codecs.register(UserPreferences.class, UserPreferencesCodec.INSTANCE);
    }

    /**
     * Initializes the codec registry with all database codecs.<br>
     * <p>
     *     Call this method at application startup to ensure<br>
     *     all codecs are registered before use.<br>
     * </p>
     */
    public static void initialize() {
        // Static initializer does the work
    }

    private DatabaseCodecs() {}
}
```

### Usage with Codec System

```java
// Serialize entity to JSON
User user = userRepository.findById(userId);
JsonElement json = Codecs.get(User.class).encodeStart(JsonOps.INSTANCE, user);

// Deserialize from JSON
String jsonString = "{ \"id\": \"...\", \"email\": \"...\", ... }";
JsonElement element = JsonParser.parseString(jsonString);
User user = Codecs.get(User.class).decodeStart(JsonOps.INSTANCE, element);

// Serialize to different formats
XmlElement xml = Codecs.get(User.class).encodeStart(XmlOps.INSTANCE, user);
TomlElement toml = Codecs.get(User.class).encodeStart(TomlOps.INSTANCE, user);
```

## Partial Updates with Codecs

### Patch Support

```java
package net.luis.utils.db.codec;

import net.luis.utils.io.codec.Codec;
import org.jspecify.annotations.NullMarked;

/**
 * Codec for partial entity updates (PATCH operations).<br>
 *
 * @param <T> The entity type
 * @author Luis-St
 */
@NullMarked
public final class PatchCodec<T> {

    private final Codec<T> entityCodec;
    private final Map<String, FieldCodec<?, T>> fieldCodecs;

    /**
     * Applies a partial update to an entity.<br>
     *
     * @param entity The original entity
     * @param patch The patch data (partial JSON)
     * @return The updated entity
     * @throws CodecException If patching fails
     */
    public T applyPatch(T entity, JsonObject patch) throws CodecException {
        // Start with entity values
        JsonElement encoded = entityCodec.encodeStart(JsonOps.INSTANCE, entity);
        JsonObject merged = encoded.getAsJsonObject();

        // Apply patch fields
        for (Map.Entry<String, JsonElement> entry : patch.entrySet()) {
            merged.add(entry.getKey(), entry.getValue());
        }

        // Decode merged object
        return entityCodec.decodeStart(JsonOps.INSTANCE, merged);
    }

    /**
     * Validates patch data without applying it.<br>
     *
     * @param patch The patch data
     * @throws ConstraintViolationException If validation fails
     */
    public void validatePatch(JsonObject patch) throws ConstraintViolationException {
        for (String fieldName : patch.keySet()) {
            FieldCodec<?, T> fieldCodec = fieldCodecs.get(fieldName);
            if (fieldCodec == null) {
                throw new ConstraintViolationException(fieldName, "Unknown field: " + fieldName);
            }

            try {
                fieldCodec.codec().decodeStart(JsonOps.INSTANCE, patch.get(fieldName));
            } catch (CodecException e) {
                throw new ConstraintViolationException(fieldName, e.getMessage());
            }
        }
    }
}
```

## API Serialization

### REST API Integration

```java
// Controller using codecs
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final Codec<User> userCodec = UserCodec.INSTANCE;

    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable UUID id) {
        try {
            User user = userRepository.findById(id);
            JsonElement json = userCodec.encodeStart(JsonOps.INSTANCE, user);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json.toString());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody String body) {
        try {
            JsonElement json = JsonParser.parseString(body);
            User user = userCodec.decodeStart(JsonOps.INSTANCE, json);
            user.validate();
            User saved = userRepository.save(user);
            JsonElement savedJson = userCodec.encodeStart(JsonOps.INSTANCE, saved);
            return ResponseEntity.created(URI.create("/api/users/" + saved.id()))
                .body(savedJson.toString());
        } catch (ConstraintViolationException | CodecException e) {
            return ResponseEntity.badRequest()
                .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable UUID id,
            @RequestBody String body) {

        PatchCodec<User> patchCodec = new PatchCodec<>(userCodec);

        try {
            User user = userRepository.findById(id);
            JsonObject patch = JsonParser.parseString(body).getAsJsonObject();
            User updated = patchCodec.applyPatch(user, patch);
            updated.validate();
            User saved = userRepository.save(updated);
            JsonElement savedJson = userCodec.encodeStart(JsonOps.INSTANCE, saved);
            return ResponseEntity.ok(savedJson.toString());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ConstraintViolationException | CodecException e) {
            return ResponseEntity.badRequest()
                .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
```

## Related Documents

- [00-overview.md](00-overview.md) - Architecture overview
- [01-yaml-schema.md](01-yaml-schema.md) - Schema constraints
- [02-code-generation.md](02-code-generation.md) - Codec generation
