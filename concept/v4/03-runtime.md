# Runtime: Transactions, Async, Errors, DDL, Migrations

## Transactions

### Functional Style (Recommended)

```java
User saved = database.inTransaction(tx -> {
    User user = UserTable.TABLE.insert(newUser);
    AuditTable.TABLE.insert(SqlAuditEntry.forCreate(user));
    return user;
});
// Auto-commit on success, auto-rollback on exception
```

### Try-With-Resources

```java
try (SqlTransaction tx = database.beginTransaction()) {
    UserTable.TABLE.insert(user);
    OrderTable.TABLE.insert(order);
    tx.commit();
}
// Auto-rollback if commit() not called
```

### Without Transaction (Auto-Commit)

```java
List<User> users = UserTable.TABLE.select().fetch();  // Each statement is atomic
```

### Isolation Levels

```java
try (SqlTransaction tx = database.beginTransaction(SqlIsolationLevel.SERIALIZABLE)) {
    // SERIALIZABLE, REPEATABLE_READ, READ_COMMITTED, READ_UNCOMMITTED
}
```

### Savepoints

```java
try (SqlTransaction tx = database.beginTransaction()) {
    UserTable.TABLE.insert(user1);
    SqlSavepoint sp = tx.savepoint("before_user2");
    try {
        UserTable.TABLE.insert(user2);
    } catch (Exception e) {
        tx.rollbackTo(sp);  // Rollback only user2
        UserTable.TABLE.insert(alternativeUser);
    }
    tx.commit();
}
```

### Options

```java
tx.readOnly();                    // Read-only optimization
tx.timeout(Duration.ofSeconds(30));
tx.isolation(SqlIsolationLevel.SERIALIZABLE);
```

### Transaction Context

```java
SqlTransaction.isInTransaction();   // boolean
SqlTransaction.current();           // Optional<SqlTransaction>
SqlTransaction.requireActive();     // throws if none active
```

---

## Async API

All sync methods have async counterparts with `Async` suffix:

```java
CompletableFuture<List<User>> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetchAsync();

CompletableFuture<Optional<User>> user = query.fetchFirstAsync();
CompletableFuture<User> user = query.fetchOneAsync();
CompletableFuture<Long> count = query.countAsync();
CompletableFuture<Integer> affected = updateQuery.executeAsync();
```

### Chaining

```java
CompletableFuture<OrderSummary> summary = UserTable.TABLE.select()
    .where(UserTable.EMAIL.equalTo(email))
    .fetchFirstAsync()
    .thenCompose(user -> OrderTable.TABLE.select()
        .where(OrderTable.CUSTOMER_ID.equalTo(user.orElseThrow().id()))
        .fetchAsync())
    .thenApply(OrderSummary::from);
```

### Async Transactions

```java
CompletableFuture<User> result = database.inTransactionAsync(tx -> {
    return UserTable.TABLE.insertAsync(user)
        .thenCompose(saved -> AuditTable.TABLE.insertAsync(entry).thenApply(a -> saved));
});
```

### Configuration

```java
SqlDatabaseConfig.builder()
    .asyncExecutor(Executors.newVirtualThreadPerTaskExecutor())  // Java 21+
    .asyncConnectionPoolSize(20)
    .build();
```

### Timeout & Error Handling

```java
future.orTimeout(5, TimeUnit.SECONDS)
      .completeOnTimeout(List.of(), 5, TimeUnit.SECONDS)
      .exceptionally(e -> handleError(e.getCause()));
```

---

## Error Handling

### Exception Hierarchy

```
SqlDatabaseException (base, unchecked)
├── SqlConnectionException
├── SqlTransactionException
├── SqlQueryException, SqlQueryTimeoutException
├── SqlConstraintViolationException
│   ├── SqlUniqueConstraintViolationException
│   ├── SqlForeignKeyViolationException
│   ├── SqlNotNullViolationException
│   └── SqlCheckConstraintViolationException
├── SqlEntityException
│   ├── SqlEntityNotFoundException
│   ├── SqlRelationshipNotLoadedException
│   └── SqlStaleEntityException
└── SqlLockingException
    ├── SqlDeadlockException
    └── SqlLockNotAvailableException
```

### Constraint Violation Details

```java
try {
    UserTable.TABLE.insert(user);
} catch (SqlUniqueConstraintViolationException e) {
    e.getConstraintName();    // "users_email_key"
    e.getColumnNames();       // ["email"]
    e.getDuplicateValue();    // Optional<Object>
} catch (SqlForeignKeyViolationException e) {
    e.getReferencedTableName();
    e.getMissingKeyValue();
}
```

### Base Exception Info

```java
catch (SqlDatabaseException e) {
    e.getSqlState();      // "23505"
    e.getVendorCode();    // 1
    e.getSql();           // Optional<String>
    e.getParameters();    // List<Object>
}
```

### Retry Support

```java
User user = SqlRetry.withBackoff(3, Duration.ofMillis(100))
    .retryOn(SqlDeadlockException.class, SqlQueryTimeoutException.class)
    .execute(() -> {
        try (SqlTransaction tx = database.beginTransaction()) {
            User u = UserTable.TABLE.select().where(...).forUpdate().fetchOne();
            UserTable.TABLE.update(u.withCount(u.count() + 1));
            tx.commit();
            return u;
        }
    });
```

---

## DDL Operations

**Note:** Runtime table modifications (`addColumn`, etc.) are not supported. Schema changes require YAML update + regeneration.

### Table Operations

```java
UserTable.TABLE.create();
UserTable.TABLE.createIfNotExists();
UserTable.TABLE.drop();
UserTable.TABLE.dropIfExists();
UserTable.TABLE.truncate();
boolean exists = UserTable.TABLE.exists();
```

### Index Operations

```java
UserTable.TABLE.createIndex("idx_users_email", UserTable.EMAIL);
UserTable.TABLE.createIndex(SqlIndexDefinition.builder()
    .name("idx_active_users")
    .columns("email")
    .unique(true)
    .where("status = 'ACTIVE'")  // Partial index
    .method(SqlIndexMethod.GIN)  // PostgreSQL
    .build());

UserTable.TABLE.dropIndex("idx_users_email");
List<SqlIndexInfo> indexes = UserTable.TABLE.listIndexes();
```

### Sequence Operations

```java
UserTable.TABLE.createSequence(SqlSequenceDefinition.builder()
    .name("users_id_seq").startWith(1).incrementBy(1).cache(50).build());
long next = UserTable.TABLE.nextSequenceValue("users_id_seq");
```

### DDL Without Execution

```java
String sql = UserTable.TABLE.generateCreateSql();
String sql = UserTable.TABLE.generateDropSql();
```

---

## Migrations

Schema changes are managed through versioned migration files generated from YAML schema diffs.

### Migration Workflow

```
1. Modify YAML schema
2. Run: ./gradlew generateMigration
3. Review generated SQL in migrations/
4. Run: ./gradlew migrate
```

### Gradle Configuration

```kotlin
lutilsDb {
    migrations {
        enabled = true
        directory = file("src/main/resources/db/migrations")
        naming = "V{version}__{description}.sql"
        baseline = "V1__initial_schema.sql"
    }
}
```

### Generated Migration Example

When adding a `phone` field to `user.yaml`:

```sql
-- V2__add_phone_to_users.sql
-- Generated by LUtils-DB at 2024-01-15T10:30:00Z
-- Schema change: user.yaml

ALTER TABLE users ADD COLUMN phone VARCHAR(50);
CREATE INDEX idx_users_phone ON users (phone);
```

### Migration Tasks

| Task | Description |
|------|-------------|
| `generateMigration` | Generate SQL from schema changes |
| `migrate` | Apply pending migrations |
| `migrateInfo` | Show migration status |
| `migrateValidate` | Validate migrations against schema |
| `migrateBaseline` | Create baseline from current schema |
| `migrateUndo` | Rollback last migration (if supported) |

### Schema Diff Detection

```java
// Programmatic diff
SqlSchemaDiff diff = SqlMigration.diff(
    SqlSchema.fromYaml(oldSchemaDir),
    SqlSchema.fromYaml(newSchemaDir)
);

diff.getAddedTables();      // List<SqlTableDiff>
diff.getRemovedTables();    // List<SqlTableDiff>
diff.getModifiedTables();   // List<SqlTableDiff>

// Generate SQL
String sql = diff.toSql(SqlDialect.POSTGRES);
```

### Change Types Detected

| Change | Generated SQL |
|--------|---------------|
| Add table | `CREATE TABLE ...` |
| Drop table | `DROP TABLE ...` |
| Add column | `ALTER TABLE ... ADD COLUMN ...` |
| Drop column | `ALTER TABLE ... DROP COLUMN ...` |
| Modify column type | `ALTER TABLE ... ALTER COLUMN ... TYPE ...` |
| Add/drop constraint | `ALTER TABLE ... ADD/DROP CONSTRAINT ...` |
| Add/drop index | `CREATE/DROP INDEX ...` |
| Rename (with hint) | `ALTER TABLE ... RENAME ...` |

### Rename Detection

Renames require hints in YAML to avoid drop+create:

```yaml
fields:
  userName:           # Was: name
    type: String
    _renamedFrom: name  # Hint for migration generator
```

Generates:
```sql
ALTER TABLE users RENAME COLUMN name TO user_name;
```

### Manual Migration

For complex changes, create manual migrations:

```sql
-- V3__migrate_status_enum.sql
-- Manual migration: convert status from string to enum

ALTER TABLE users ADD COLUMN status_new user_status;
UPDATE users SET status_new = status::user_status;
ALTER TABLE users DROP COLUMN status;
ALTER TABLE users RENAME COLUMN status_new TO status;
```

### Migration History Table

Migrations are tracked in `_lutils_migrations`:

```sql
CREATE TABLE _lutils_migrations (
    version VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255),
    script VARCHAR(255),
    checksum VARCHAR(64),
    applied_at TIMESTAMPTZ DEFAULT NOW(),
    applied_by VARCHAR(100),
    execution_time_ms INTEGER
);
```

### Dry Run

Preview migrations without applying:

```bash
./gradlew migrate --dry-run
```

```java
List<SqlMigration> pending = SqlMigration.pending(dataSource, migrationsDir);
for (SqlMigration m : pending) {
    System.out.println(m.version() + ": " + m.description());
    System.out.println(m.sql());
}
```

---

## Auditing

Per-table audit configuration (no global defaults):

```yaml
entity: User
table: users

audit:
  createdAt: { column: created_at, type: Instant }
  updatedAt: { column: updated_at, type: Instant }
  createdBy: { column: created_by, type: String }
  updatedBy: { column: updated_by, type: String }
```

### Automatic Handling

- On INSERT: `createdAt`, `updatedAt` set to now; `createdBy`, `updatedBy` from context
- On UPDATE: `updatedAt` set to now; `updatedBy` from context

### User Context

```java
SqlAuditContext.setCurrentUser("user-123");
SqlAuditContext.getCurrentUser();  // Optional<String>
SqlAuditContext.clear();

// Scoped
SqlAuditContext.withUser("user-123", () -> {
    UserTable.TABLE.insert(user);
    return result;
});
```

### Options

```java
// Skip audit
UserTable.TABLE.insert(user).skipAudit().execute();

// Override timestamp
User user = User.builder().createdAt(specificTimestamp).build();  // Used instead of auto

// Timestamp source
SqlDatabaseConfig.builder()
    .auditTimestampSource(SqlTimestampSource.APPLICATION)  // vs DATABASE (default)
    .auditClock(Clock.fixed(...))  // For testing
    .build();
```

### Querying Audit Data

```java
UserTable.TABLE.select()
    .where(UserTable.CREATED_AT.withinLast(Duration.ofDays(7)))
    .where(UserTable.CREATED_BY.equalTo("admin-1"))
    .fetch();
```
