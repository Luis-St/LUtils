# Auditing

## Overview

Auditing automatically tracks creation and modification timestamps, and optionally the user who made changes. This is configured in the YAML schema and handled transparently during insert/update operations.

## YAML Schema Configuration

```yaml
tables:
  users:
    auditing:
      created_at: timestamp    # Auto-set on insert
      updated_at: timestamp    # Auto-set on insert and update
      created_by: string       # Optional: who created
      updated_by: string       # Optional: who modified

    columns:
      id: uuid
      name: string
      email: string
```

## Generated Table Structure

```java
public final class UserTable {

    public static final SqlTable<User> TABLE = SqlTable.of("users", User.class);

    // Regular columns
    public static final SqlColumn<User, UUID> ID = TABLE.column("id", UUID.class);
    public static final SqlColumn<User, String> NAME = TABLE.column("name", String.class);
    public static final SqlColumn<User, String> EMAIL = TABLE.column("email", String.class);

    // Audit columns
    public static final SqlAuditColumn<User, Instant> CREATED_AT =
        TABLE.auditColumn("created_at", Instant.class, AuditType.CREATED_AT);
    public static final SqlAuditColumn<User, Instant> UPDATED_AT =
        TABLE.auditColumn("updated_at", Instant.class, AuditType.UPDATED_AT);
    public static final SqlAuditColumn<User, String> CREATED_BY =
        TABLE.auditColumn("created_by", String.class, AuditType.CREATED_BY);
    public static final SqlAuditColumn<User, String> UPDATED_BY =
        TABLE.auditColumn("updated_by", String.class, AuditType.UPDATED_BY);
}
```

## Automatic Timestamp Handling

### On Insert

```java
User user = User.builder()
    .name("John")
    .email("john@example.com")
    .build();

// createdAt and updatedAt are NOT set on the builder

User saved = UserTable.TABLE.insert(user);

// After insert:
// saved.createdAt() = current timestamp (e.g., 2024-01-15T10:30:00Z)
// saved.updatedAt() = same as createdAt
```

### On Update

```java
User updated = saved.withName("John Doe");

User result = UserTable.TABLE.update(updated);

// After update:
// result.createdAt() = unchanged (2024-01-15T10:30:00Z)
// result.updatedAt() = new timestamp (2024-01-15T11:45:00Z)
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
AuditContext.setCurrentUser("user-123");

// Or with more detail
AuditContext.setCurrentUser(AuditUser.of("user-123", "john@example.com"));

// Now insert/update operations will set created_by/updated_by
User saved = UserTable.TABLE.insert(user);
// saved.createdBy() = "user-123"
```

### Thread-Local Context

```java
public class AuditContext {

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    public static void setCurrentUser(String userId) {
        CURRENT_USER.set(userId);
    }

    public static Optional<String> getCurrentUser() {
        return Optional.ofNullable(CURRENT_USER.get());
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * Execute with a specific user context.
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

// Usage in web filter
public void doFilter(Request request, Response response, FilterChain chain) {
    String userId = extractUserId(request);
    AuditContext.setCurrentUser(userId);
    try {
        chain.doFilter(request, response);
    } finally {
        AuditContext.clear();
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

### Application Time

Configure to use application time instead:

```java
DatabaseConfig.builder()
    .auditTimestampSource(TimestampSource.APPLICATION)
    .build();

// Now uses Java's Instant.now()
INSERT INTO users (id, name, created_at, updated_at)
VALUES (?, ?, ?, ?);
// With application-side timestamp bound as parameter
```

### Custom Clock

For testing or special requirements:

```java
DatabaseConfig.builder()
    .auditClock(Clock.fixed(fixedInstant, ZoneOffset.UTC))
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

### Manual Timestamp

```java
// Set timestamp explicitly (overrides auto-generation)
User user = User.builder()
    .name("John")
    .createdAt(specificTimestamp)  // Explicit value used
    .build();

User saved = UserTable.TABLE.insert(user);
// saved.createdAt() = specificTimestamp (not auto-generated)
```

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
```

## Audit Trail Table

For full audit history, configure a separate audit trail:

```yaml
tables:
  users:
    auditing:
      created_at: timestamp
      updated_at: timestamp
      audit_trail: user_audit_log  # Reference to audit log table

  user_audit_log:
    columns:
      id: uuid
      user_id:
        type: uuid
        references: users.id
      action:
        type: string
        enum: [INSERT, UPDATE, DELETE]
      old_values: jsonb
      new_values: jsonb
      changed_at: timestamp
      changed_by: string
```

```java
// Query audit history
List<UserAuditLog> history = UserAuditLogTable.TABLE.select()
    .where(UserAuditLogTable.USER_ID.equalTo(userId))
    .orderBy(UserAuditLogTable.CHANGED_AT.desc())
    .fetch();

// Audit log entries are automatically created on insert/update/delete
User saved = UserTable.TABLE.insert(user);
// Creates audit log entry with action=INSERT, new_values={...}

UserTable.TABLE.update(modified);
// Creates audit log entry with action=UPDATE, old_values={...}, new_values={...}

UserTable.TABLE.delete(user);
// Creates audit log entry with action=DELETE, old_values={...}
```

## Entity Interface

```java
public interface Auditable {

    Instant createdAt();
    Instant updatedAt();

    default Optional<String> createdBy() {
        return Optional.empty();
    }

    default Optional<String> updatedBy() {
        return Optional.empty();
    }
}

// Generated entity implements Auditable
public record User(
    UUID id,
    String name,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy
) implements Auditable {}
```

## Open Questions

- **Audit log storage**: Should audit logs be in the same database or a separate audit database?
- **Soft delete auditing**: How should audit interact with soft delete (deleted_at, deleted_by)?
- **Field-level auditing**: Should we track which specific fields changed, not just that a row changed?
- **Async audit logging**: Should audit log writes be async to avoid impacting main operation latency?
- **Audit retention**: Should there be built-in audit log rotation/archival support?
