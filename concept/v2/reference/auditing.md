# Auditing

## Overview

Auditing automatically tracks creation and modification timestamps, and optionally the user who made changes. In v2, audit configuration is **per-table only** - there are no global defaults.

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
    .email("john@example.com")
    .name("John")
    .build();

// createdAt and updatedAt are NOT set on the builder

User saved = UserTable.TABLE.insert(user);

// After insert:
// saved.createdAt() = current timestamp (e.g., 2024-01-15T10:30:00Z)
// saved.updatedAt() = same as createdAt
// saved.createdBy() = current user from AuditContext (if configured)
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
// result.updatedBy() = current user from AuditContext
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

### AuditContext Implementation

```java
public final class AuditContext {

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

### Web Filter Integration

```java
// Servlet filter
public void doFilter(HttpServletRequest request, HttpServletResponse response,
                     FilterChain chain) throws IOException, ServletException {
    String userId = extractUserId(request);
    AuditContext.setCurrentUser(userId);
    try {
        chain.doFilter(request, response);
    } finally {
        AuditContext.clear();
    }
}

// Spring interceptor
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                         Object handler) {
    String userId = extractUserId(request);
    AuditContext.setCurrentUser(userId);
    return true;
}

@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                            Object handler, Exception ex) {
    AuditContext.clear();
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
DatabaseConfig.builder()
    .auditTimestampSource(TimestampSource.APPLICATION)
    .build();

// Now uses Java's Instant.now()
// INSERT INTO users (id, name, created_at, updated_at) VALUES (?, ?, ?, ?);
// With application-side timestamp bound as parameter
```

### Custom Clock

For testing or special requirements:

```java
// Fixed clock for testing
DatabaseConfig.builder()
    .auditClock(Clock.fixed(fixedInstant, ZoneOffset.UTC))
    .build();

// Offset clock
DatabaseConfig.builder()
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

## Auditable Interface

Generated entities with audit columns implement the Auditable interface:

```java
public interface Auditable {

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

// Generated entity implements Auditable
public record User(
    UUID id,
    String email,
    String name,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy
) implements Auditable {

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
    type: AuditAction
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
    .action(AuditAction.INSERT)
    .newValues(toJson(saved))
    .changedAt(Instant.now())
    .changedBy(AuditContext.getCurrentUser().orElse("system"))
    .build());

// Create audit entry on update
User original = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOne();
User updated = UserTable.TABLE.update(modified);
UserAuditLogTable.TABLE.insert(UserAuditLog.builder()
    .userId(updated.id())
    .action(AuditAction.UPDATE)
    .oldValues(toJson(original))
    .newValues(toJson(updated))
    .changedAt(Instant.now())
    .changedBy(AuditContext.getCurrentUser().orElse("system"))
    .build());
```

### Database Triggers (Alternative)

For automatic audit logging, use database triggers:

```sql
-- PostgreSQL trigger for audit logging
CREATE OR REPLACE FUNCTION audit_user_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO user_audit_log (user_id, action, new_values, changed_at, changed_by)
        VALUES (NEW.id, 'INSERT', row_to_json(NEW), NOW(), current_setting('app.current_user', true));
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO user_audit_log (user_id, action, old_values, new_values, changed_at, changed_by)
        VALUES (NEW.id, 'UPDATE', row_to_json(OLD), row_to_json(NEW), NOW(), current_setting('app.current_user', true));
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO user_audit_log (user_id, action, old_values, changed_at, changed_by)
        VALUES (OLD.id, 'DELETE', row_to_json(OLD), NOW(), current_setting('app.current_user', true));
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER user_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW EXECUTE FUNCTION audit_user_changes();
```

## Related Documents

- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Audit YAML syntax
- [transaction-api.md](transaction-api.md) - Audit within transactions
- [error-handling.md](error-handling.md) - Audit error handling
