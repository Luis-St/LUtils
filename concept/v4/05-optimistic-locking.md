# Optimistic Locking / Version Column

## Overview

Optimistic locking prevents silent data loss from concurrent updates by tracking a version on each entity.
On every update/delete, the framework automatically checks the version and increments it.
If the version in the database no longer matches the entity being written, `SqlStaleEntityException` is thrown.

This is a per-table, opt-in feature configured in the YAML schema, following the same pattern as the audit system.

---

## YAML Configuration

Top-level `version` block on the entity definition (like `audit`):

```yaml
entity: User
table: users

audit:
  createdAt: { column: created_at, type: Instant }
  updatedAt: { column: updated_at, type: Instant }

version:
  column: version
  type: Long          # Long (default) or Integer

fields:
  id:
    type: UUID
    primary: true
    generated: uuid

  email:
    type: String
    unique: true

  name:
    type: String
```

The `version` block is independent of `audit` — either, both, or neither can be present.

### Configuration Options

| Key | Required | Default | Description |
|-----|----------|---------|-------------|
| `column` | no | `version` | SQL column name |
| `type` | no | `Long` | Java type (`Long` or `Integer`) |

Minimal form:

```yaml
version: {}            # Defaults: column = "version", type = Long
```

---

## Framework Interfaces

The framework uses two interfaces to interact with managed entity fields without reflection.
Generated entities implement these interfaces when versioning or auditing is enabled.

### SqlVersioned

Implemented by entities with a `version` block in the YAML schema:

```java
public interface SqlVersioned<T extends SqlVersioned<T>> {

    /**
     * Returns the current version of this entity.
     */
    long version();

    /**
     * Returns a copy of this entity with the given version.
     */
    @NonNull T withVersion(long version);
}
```

### SqlAuditable

Implemented by entities with an `audit` block in the YAML schema.
All methods have defaults so that entities only override the fields they actually configure:

```java
public interface SqlAuditable<T extends SqlAuditable<T>> {

    default @Nullable Instant createdAt() { return null; }
    default @Nullable Instant updatedAt() { return null; }
    default @Nullable String createdBy() { return null; }
    default @Nullable String updatedBy() { return null; }

    default @NonNull T withCreatedAt(@NonNull Instant createdAt) { throw new UnsupportedOperationException("createdAt not configured"); }
    default @NonNull T withUpdatedAt(@NonNull Instant updatedAt) { throw new UnsupportedOperationException("updatedAt not configured"); }
    default @NonNull T withCreatedBy(@NonNull String createdBy) { throw new UnsupportedOperationException("createdBy not configured"); }
    default @NonNull T withUpdatedBy(@NonNull String updatedBy) { throw new UnsupportedOperationException("updatedBy not configured"); }
}
```

Getters default to `null` for unconfigured fields. `with*` methods throw for unconfigured fields — the framework
only calls `with*` for fields it knows are configured (from table metadata), so the exception is a safety net, not a normal path.

For record components that match a getter name (e.g. `Instant createdAt` component), the record accessor
automatically overrides the default — no explicit `@Override` needed for the getter.

### Framework Usage

The framework uses these interfaces internally for all managed-field operations:

```java
// After a successful UPDATE (version increment in memory when RETURNING is not available)
if (entity instanceof SqlVersioned<?> versioned) {
    entity = ((SqlVersioned<T>) versioned).withVersion(versioned.version() + 1);
}

// Before INSERT (set audit timestamps)
if (entity instanceof SqlAuditable<?> auditable) {
    Instant now = timestampSource.now();
    entity = ((SqlAuditable<T>) auditable).withCreatedAt(now);
    entity = ((SqlAuditable<T>) auditable).withUpdatedAt(now);
    String user = SqlAuditContext.getCurrentUser();
    if (user != null) {
        entity = ((SqlAuditable<T>) auditable).withCreatedBy(user);
        entity = ((SqlAuditable<T>) auditable).withUpdatedBy(user);
    }
}

// Before UPDATE (set audit timestamp)
if (entity instanceof SqlAuditable<?> auditable) {
    entity = ((SqlAuditable<T>) auditable).withUpdatedAt(timestampSource.now());
    String user = SqlAuditContext.getCurrentUser();
    if (user != null) {
        entity = ((SqlAuditable<T>) auditable).withUpdatedBy(user);
    }
}
```

---

## Generated Code

### Entity Record

The generated entity implements `SqlVersioned` and/or `SqlAuditable` based on the YAML configuration.
A `copy` method with a generated `Mutator` class provides a single entry point for modifying entities:

```java
public record User(
    UUID id,
    String email,
    String name,
    long version,
    Instant createdAt,
    Instant updatedAt
) implements SqlVersioned<User>, SqlAuditable<User> {

    /**
     * Convenience constructor — only takes user-defined fields.<br>
     * Version defaults to {@code 0}, audit fields default to {@code null}.<br>
     * The framework populates managed fields during INSERT/UPDATE.<br>
     */
    public User(UUID id, String email, String name) {
        this(id, email, name, 0L, null, null);
    }

    // SqlVersioned
    @Override
    public @NonNull User withVersion(long version) {
        return new User(id, email, name, version, createdAt, updatedAt);
    }

    // SqlAuditable — only configured fields are overridden
    // createdAt() and updatedAt() are record accessors, automatically override the defaults

    @Override
    public @NonNull User withCreatedAt(@NonNull Instant createdAt) {
        return new User(id, email, name, version, createdAt, updatedAt);
    }

    @Override
    public @NonNull User withUpdatedAt(@NonNull Instant updatedAt) {
        return new User(id, email, name, version, createdAt, updatedAt);
    }

    // createdBy() / updatedBy() not configured — defaults apply (return null / throw)
}
```

The `with*` methods from `SqlVersioned` and `SqlAuditable` remain for the framework's internal use.

The convenience constructor only takes user-defined fields (`id`, `email`, `name`) and sets framework-managed
fields to their defaults — version to `0`, audit fields to `null`. The canonical constructor (all fields) is
still available and used internally by the mapper and `with*` methods.

The version field is readable via `user.version()` but its value is fully managed by the framework:

- On INSERT, the framework always writes `0` regardless of the value in the record.
- On UPDATE, the framework reads the value to build the `WHERE version = ?` clause, then increments it in the database.
- After a successful operation, the framework calls `withVersion()` to return the entity with the updated version.

### Table Class

A dedicated `SqlVersionColumn` marks the version column, similar to how `SqlAuditColumn` works for audit fields:

```java
public final class UserTable {
    public static final SqlTable<User> TABLE = SqlTable.of("users", User.class);

    public static final SqlColumn<User, UUID> ID = TABLE.column("id", UUID.class);
    public static final SqlColumn<User, String> EMAIL = TABLE.column("email", String.class);
    public static final SqlColumn<User, String> NAME = TABLE.column("name", String.class);

    // Version column - special type recognized by query builders
    public static final SqlVersionColumn<User, Long> VERSION = TABLE.versionColumn("version", Long.class);
}
```

### Mapper

The mapper reads/writes the version field like any other column:

```java
@Override
public User mapRow(ResultSet rs) throws SQLException {
	return new User(
		rs.getObject("id", UUID.class),
		rs.getString("email"),
		rs.getString("name"),
		rs.getLong("version"),
		rs.getObject("created_at", Instant.class),
		rs.getObject("updated_at", Instant.class)
	);
}
```

---

## Automatic Behavior

### INSERT

New entities are inserted with version `0`:

```java
User user = new User(UUID.randomUUID(), "john@example.com", "John");
User saved = UserTable.TABLE.insert(user);
// saved.version() == 0
```

Generated SQL:

```sql
INSERT INTO users (id, email, name, version, created_at, updated_at)
VALUES (?, ?, ?, 0, NOW(), NOW())
```

The initial version value `0` is always set by the framework, regardless of what value the entity holds.

### UPDATE (Entity-Level)

Entity-level updates automatically add a version check and increment:

```java
User user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOne();
// user.version() == 3

User modified = user.copy(m -> m.name("Jane"));
UserTable.TABLE.update(modified);
```

Generated SQL:

```sql
UPDATE users
SET name = ?, version = version + 1, updated_at = NOW()
WHERE id = ? AND version = 3
```

**If 0 rows are affected** (version mismatch), `SqlStaleEntityException` is thrown:

```java
try {
    UserTable.TABLE.update(modified);
} catch (SqlStaleEntityException e) {
    // Entity was modified by another transaction since it was read
    e.getEntityType();     // User.class
    e.getEntityId();       // the primary key value
    e.getExpectedVersion(); // 3 (what we had)
}
```

### UPDATE (Query-Level)

Query-level updates do **not** inject automatic version checks — the caller has full control:

```java
// No version check, no version increment
UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .execute();
```

To manually include version handling in a query-level update, use the version column explicitly:

```java
UserTable.TABLE.update()
    .set(UserTable.NAME, "Jane")
    .increment(UserTable.VERSION, 1)
    .where(UserTable.ID.equalTo(userId).and(UserTable.VERSION.equalTo(3L)))
    .execute();
```

### DELETE (Entity-Level)

Entity-level deletes include a version check:

```java
UserTable.TABLE.delete(user);
```

Generated SQL:

```sql
DELETE FROM users WHERE id = ? AND version = 3
```

If 0 rows are affected, `SqlStaleEntityException` is thrown.

### DELETE (Query-Level)

Query-level deletes do **not** inject automatic version checks:

```java
UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.DELETED))
    .execute();
```

---

## Returned Entity Version

When a versioned entity is updated via entity-level operations, the returned entity reflects the new version:

```java
User saved = UserTable.TABLE.insert(user);
// saved.version() == 0

UserTable.TABLE.update(saved);
// After update, the entity in the database has version == 1
```

If the dialect supports `RETURNING`, the framework uses it to return the updated entity with the new version in a single round-trip:

```sql
UPDATE users SET name = ?, version = version + 1 WHERE id = ? AND version = 0 RETURNING *
```

If `RETURNING` is not supported, the framework calls `entity.withVersion(entity.version() + 1)` after a successful update (0 rows check passes) to return the entity with the correct version.

---

## Skipping Version Check

For cases where the version check must be bypassed (admin corrections, data migrations, imports):

```java
UserTable.TABLE.skipVersionCheck().update(entity);
UserTable.TABLE.skipVersionCheck().delete(entity);
```

`skipVersionCheck()` returns a view of the table that suppresses the automatic `WHERE version = ?`
condition and the automatic `version = version + 1` increment for entity-level operations.

This follows the same pattern as `from()` and `withIn()` — it returns a scoped table view, not a global setting.

---

## Integration with Transactions & Retry

Optimistic locking pairs naturally with the retry mechanism for handling contention:

```java
User result = SqlRetry.withBackoff(3, Duration.ofMillis(100))
    .retryOn(SqlStaleEntityException.class)
    .execute(() -> {
        // Always re-fetch inside retry to get the latest version
        User current = UserTable.TABLE.select()
            .where(UserTable.ID.equalTo(userId))
            .fetchOne();

        User modified = current.copy(m -> m.name("Updated"));
        UserTable.TABLE.update(modified);
        return modified;
    });
```

Within a transaction:

```java
User result = SqlRetry.withBackoff(3, Duration.ofMillis(100))
    .retryOn(SqlStaleEntityException.class)
    .execute(() -> database.inTransaction(tx -> {
        User current = UserTable.TABLE.select()
            .where(UserTable.ID.equalTo(userId))
            .fetchOne();

        User modified = current.copy(m -> m.name("Updated"));
        UserTable.TABLE.update(modified);
        return modified;
    }));
```

### Optimistic vs. Pessimistic Locking

Both strategies are available and can be chosen per use case:

```java
// Optimistic (version check on write, no lock held during read)
User user = UserTable.TABLE.select().where(...).fetchOne();
User modified = user.copy(m -> m.name("New"));
UserTable.TABLE.update(modified);

// Pessimistic (row lock held for duration of transaction)
database.inTransaction(tx -> {
    User user = UserTable.TABLE.select().where(...).forUpdate().fetchOne();
    User modified = user.copy(m -> m.name("New"));
    UserTable.TABLE.update(modified);
});
```

---

## SqlStaleEntityException

Extended to carry version context:

```java
public class SqlStaleEntityException extends SqlEntityException {

    public SqlStaleEntityException(Class<?> entityType, Object entityId, long expectedVersion) { ... }

    /**
     * Returns the entity type that was stale.
     */
    public Class<?> getEntityType();

    /**
     * Returns the primary key of the stale entity.
     */
    public Object getEntityId();

    /**
     * Returns the version the application expected (the version it read).
     */
    public long getExpectedVersion();
}
```

Example message: `"Stale entity User[id=550e8400-...]: expected version 3 but row was modified or deleted by another transaction"`

---

## DDL

### CREATE TABLE

The version column is generated as a `NOT NULL` column with a default of `0`:

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
```

### Migration

Adding optimistic locking to an existing table generates:

```sql
ALTER TABLE users ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
```

All existing rows start at version `0`.

---

## Interaction with Other Features

### Audit

Version increment and audit timestamp update happen in the same statement:

```sql
UPDATE users
SET name = ?, version = version + 1, updated_at = NOW(), updated_by = ?
WHERE id = ? AND version = 3
```

### Entity Listeners

Listeners fire normally. The version check happens at SQL execution time, after `beforeUpdate`/`beforeDelete`:

```
beforeUpdate(entity) → execute SQL with version check → afterUpdate(entity)
                        ↘ SqlStaleEntityException (no afterUpdate)
```

If `SqlStaleEntityException` is thrown, `afterUpdate`/`afterDelete` is **not** called.

### Upsert

On upsert with a version column, the insert path starts at version `0` and the update path increments the version:

```java
UserTable.TABLE.upsert(user, UserTable.EMAIL,
    updater -> updater.set(UserTable.NAME, user.name()));
```

```sql
INSERT INTO users (id, email, name, version) VALUES (?, ?, ?, 0)
ON CONFLICT (email)
DO UPDATE SET name = ?, version = users.version + 1
```

No stale check on upsert — the conflict resolution is inherently last-write-wins by design.

### Batch Operations

Batch entity-level updates check the version per entity. If any entity in the batch is stale, the entire batch fails with `SqlStaleEntityException` for the first stale entity detected:

```java
UserTable.TABLE.update(user1, user2, user3);
// Each generates: UPDATE ... SET version = version + 1 WHERE id = ? AND version = ?
// Executed within a single transaction — rolls back on first stale entity
```
