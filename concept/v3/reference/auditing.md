# Auditing

## Overview

Auditing automatically tracks creation and modification timestamps, and optionally the user who made changes. In v3, audit configuration is **per-table only** - there are no global defaults.

## Per-Table Configuration

Each table defines its own audit columns in the YAML schema:

```yaml
# user.yaml
entity: User
table: users

# Audit configuration for this table only
audit:
  createdAt:
    column: created_at        # Column name (required)
    type: Instant             # Type: Instant, LocalDateTime, OffsetDateTime
  updatedAt:
    column: updated_at
    type: Instant
  createdBy:
    column: created_by
    type: String              # Type: String, UUID, Long (user identifier type)
  updatedBy:
    column: updated_by
    type: String

fields:
  id:
    type: UUID
    primary: true
    generated: true
  email:
    type: String
    unique: true
  name:
    type: String
```

### Minimal Audit Configuration

You can include only the columns you need:

```yaml
# post.yaml - only track timestamps, not users
entity: Post
table: posts

audit:
  createdAt:
    column: created_at
    type: Instant
  updatedAt:
    column: updated_at
    type: Instant

fields:
  # ...
```

```yaml
# log_entry.yaml - only track creation, not updates
entity: LogEntry
table: log_entries

audit:
  createdAt:
    column: logged_at
    type: Instant

fields:
  # ...
```

### Custom Column Names

Column names are fully configurable per table:

```yaml
# legacy_table.yaml - using existing column names
entity: LegacyRecord
table: legacy_records

audit:
  createdAt:
    column: date_created      # Legacy column name
    type: LocalDateTime       # Legacy type without timezone
  updatedAt:
    column: date_modified
    type: LocalDateTime
  createdBy:
    column: creator_id
    type: Long                # Legacy user ID type
  updatedBy:
    column: modifier_id
    type: Long

fields:
  # ...
```

## Generated Table Structure

```java
public final class UserTable {

    public static final SqlTable<User> TABLE = SqlTable.of("users", User.class);

    // Regular columns
    public static final SqlColumn<User, UUID> ID = TABLE.column("id", UUID.class);
    public static final SqlColumn<User, String> EMAIL = TABLE.column("email", String.class);
    public static final SqlColumn<User, String> NAME = TABLE.column("name", String.class);

    // Audit columns
    public static final SqlAuditColumn<User, Instant> CREATED_AT = TABLE.auditColumn("created_at", Instant.class, SqlAuditType.CREATED_AT);
    public static final SqlAuditColumn<User, Instant> UPDATED_AT = TABLE.auditColumn("updated_at", Instant.class, SqlAuditType.UPDATED_AT);
    public static final SqlAuditColumn<User, String> CREATED_BY = TABLE.auditColumn("created_by", String.class, SqlAuditType.CREATED_BY);
    public static final SqlAuditColumn<User, String> UPDATED_BY = TABLE.auditColumn("updated_by", String.class, SqlAuditType.UPDATED_BY);
}
```

## Automatic Timestamp Handling

### On Insert

```java
User user = User.builder()
    .email("john@example.com")
    .name("John")
    .build();

// createdAt and updatedAt are NOT set on the builder

User saved = UserTable.TABLE.insert(user);

// After insert:
// saved.createdAt() = current timestamp (e.g., 2024-01-15T10:30:00Z)
// saved.updatedAt() = same as createdAt
// saved.createdBy() = current user from SqlAuditContext (if configured)
// saved.updatedBy() = same as createdBy
```

### On Update

```java
User updated = saved.withName("John Doe");

User result = UserTable.TABLE.update(updated);

// After update:
// result.createdAt() = unchanged (2024-01-15T10:30:00Z)
// result.updatedAt() = new timestamp (2024-01-15T11:45:00Z)
// result.createdBy() = unchanged
// result.updatedBy() = current user from SqlAuditContext
```

### Conditional Update

```java
int count = UserTable.TABLE.update()
    .set(UserTable.NAME, "New Name")
    .where(UserTable.ID.equalTo(userId))
    .execute();

// updatedAt is automatically included:
// UPDATE users SET name = ?, updated_at = NOW() WHERE id = ?
```

## User Tracking

### Setting Current User

```java
// Set the current user context (typically in a filter/interceptor)
SqlAuditContext.setCurrentUser("user-123");

// Or with more detail
SqlAuditContext.setCurrentUser(SqlAuditUser.of("user-123", "john@example.com"));

// Now insert/update operations will set created_by/updated_by
User saved = UserTable.TABLE.insert(user);
// saved.createdBy() = "user-123"
```

### SqlAuditContext Implementation

```java
public final class SqlAuditContext {

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    /**
     * Sets the current user for audit tracking.<br>
     *
     * @param userId The user identifier
     */
    public static void setCurrentUser(String userId) {
        CURRENT_USER.set(userId);
    }

    /**
     * Returns the current user, if set.<br>
     *
     * @return Optional containing the user ID
     */
    public static Optional<String> getCurrentUser() {
        return Optional.ofNullable(CURRENT_USER.get());
    }

    /**
     * Clears the current user context.<br>
     */
    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * Executes an action with a specific user context.<br>
     *
     * @param userId The user ID for this context
     * @param action The action to execute
     * @return The result of the action
     */
    public static <T> T withUser(String userId, Supplier<T> action) {
        String previous = CURRENT_USER.get();
        try {
            CURRENT_USER.set(userId);
            return action.get();
        } finally {
            if (previous != null) {
                CURRENT_USER.set(previous);
            } else {
                CURRENT_USER.remove();
            }
        }
    }
}
```

## Timestamp Source

### Default: Database Time

By default, timestamps use database time (NOW()):

```sql
INSERT INTO users (id, name, created_at, updated_at)
VALUES (?, ?, NOW(), NOW());
```

This ensures consistency when multiple application servers are involved.

### Application Time

Configure to use application time instead:

```java
SqlDatabaseConfig.builder()
    .auditTimestampSource(SqlTimestampSource.APPLICATION)
    .build();

// Now uses Java's Instant.now()
// INSERT INTO users (id, name, created_at, updated_at) VALUES (?, ?, ?, ?);
// With application-side timestamp bound as parameter
```

### Custom Clock

For testing or special requirements:

```java
// Fixed clock for testing
SqlDatabaseConfig.builder()
    .auditClock(Clock.fixed(fixedInstant, ZoneOffset.UTC))
    .build();

// Offset clock
SqlDatabaseConfig.builder()
    .auditClock(Clock.offset(Clock.systemUTC(), Duration.ofHours(2)))
    .build();
```

## Disabling Auditing

### Per-Operation

```java
// Skip audit column updates (for data migration, etc.)
UserTable.TABLE.insert(user)
    .skipAudit()
    .execute();

UserTable.TABLE.update()
    .set(UserTable.NAME, "New Name")
    .where(UserTable.ID.equalTo(userId))
    .skipAudit()
    .execute();
```

### Manual Timestamp Override

```java
// Set timestamp explicitly (overrides auto-generation)
User user = User.builder()
    .email("john@example.com")
    .name("John")
    .createdAt(specificTimestamp)  // Explicit value used
    .build();

User saved = UserTable.TABLE.insert(user);
// saved.createdAt() = specificTimestamp (not auto-generated)
```

This is useful for data migration or importing historical data.

## Querying Audit Data

```java
// Find recently created users
List<User> recentUsers = UserTable.TABLE.select()
    .where(UserTable.CREATED_AT.withinLast(Duration.ofDays(7)))
    .orderBy(UserTable.CREATED_AT.desc())
    .fetch();

// Find users created by specific admin
List<User> adminCreated = UserTable.TABLE.select()
    .where(UserTable.CREATED_BY.equalTo("admin-1"))
    .fetch();

// Find recently modified
List<User> recentlyModified = UserTable.TABLE.select()
    .where(UserTable.UPDATED_AT.greaterThan(oneHourAgo))
    .fetch();

// Users not modified since creation (never updated)
List<User> neverUpdated = UserTable.TABLE.select()
    .where(UserTable.UPDATED_AT.equalTo(UserTable.CREATED_AT))
    .fetch();

// Users modified by different person than creator
List<User> modifiedByOther = UserTable.TABLE.select()
    .where(UserTable.UPDATED_BY.notEqualTo(UserTable.CREATED_BY))
    .fetch();
```

## SqlAuditable Interface

Generated entities with audit columns implement the SqlAuditable interface:

```java
public interface SqlAuditable {

    /**
     * Returns when this entity was created.<br>
     */
    Instant createdAt();

    /**
     * Returns when this entity was last updated.<br>
     */
    Instant updatedAt();

    /**
     * Returns who created this entity, if tracked.<br>
     */
    default Optional<String> createdBy() {
        return Optional.empty();
    }

    /**
     * Returns who last updated this entity, if tracked.<br>
     */
    default Optional<String> updatedBy() {
        return Optional.empty();
    }
}

// Generated entity implements SqlAuditable
public record User(
    UUID id,
    String email,
    String name,
    Instant createdAt,
    Instant updatedAt,
    @Nullable String createdBy,
    @Nullable String updatedBy
) implements SqlAuditable {

    @Override
    public Optional<String> createdBy() {
        return Optional.ofNullable(createdBy);
    }

    @Override
    public Optional<String> updatedBy() {
        return Optional.ofNullable(updatedBy);
    }
}
```

## Audit Trail Table (Full History)

For full audit history, create a separate audit log table:

```yaml
# user_audit_log.yaml
entity: UserAuditLog
table: user_audit_log

fields:
  id:
    type: UUID
    primary: true
    generated: true

  userId:
    type: UUID
    column: user_id
    index: true

  action:
    type: SqlAuditAction
    enum:
      values: [INSERT, UPDATE, DELETE]

  oldValues:
    type: Json
    column: old_values
    nullable: true

  newValues:
    type: Json
    column: new_values
    nullable: true

  changedAt:
    type: Instant
    column: changed_at

  changedBy:
    type: String
    column: changed_by
```

### Manual Audit Logging

```java
// Create audit entry on insert
User saved = UserTable.TABLE.insert(user);
UserAuditLogTable.TABLE.insert(UserAuditLog.builder()
    .userId(saved.id())
    .action(SqlAuditAction.INSERT)
    .newValues(toJson(saved))
    .changedAt(Instant.now())
    .changedBy(SqlAuditContext.getCurrentUser().orElse("system"))
    .build());

// Create audit entry on update
User original = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOne();
User updated = UserTable.TABLE.update(modified);
UserAuditLogTable.TABLE.insert(UserAuditLog.builder()
    .userId(updated.id())
    .action(SqlAuditAction.UPDATE)
    .oldValues(toJson(original))
    .newValues(toJson(updated))
    .changedAt(Instant.now())
    .changedBy(SqlAuditContext.getCurrentUser().orElse("system"))
    .build());
```

## Related Documents

- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Audit YAML syntax
- [transaction-api.md](transaction-api.md) - Audit within transactions
- [error-handling.md](error-handling.md) - Audit error handling
