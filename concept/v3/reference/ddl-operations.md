# DDL Operations

## Overview

DDL (Data Definition Language) operations manage database schema: creating and dropping tables, indexes, and sequences. In v3, **runtime table modifications are not supported** - schema changes happen only through YAML and regeneration.

**Supported operations:**
- `create()`, `createIfNotExists()` - Create tables
- `drop()`, `dropIfExists()` - Drop tables
- `exists()` - Check table existence
- `truncate()` - Clear table data
- Index operations
- Sequence operations

To modify table structure, update the YAML schema and regenerate the code.

## Table Operations

### Create Table

```java
// Create table with all columns and constraints
UserTable.TABLE.create();

// Create only if it doesn't exist
UserTable.TABLE.createIfNotExists();

// Create with options
UserTable.TABLE.create(SqlCreateOptions.builder()
    .ifNotExists(true)
    .withComments(true)      // Add column comments from YAML descriptions
    .build());
```

Generated SQL (PostgreSQL):
```sql
CREATE TABLE IF NOT EXISTS "users" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "email" VARCHAR(255) NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "status" VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    "address_id" UUID,
    PRIMARY KEY ("id"),
    UNIQUE ("email"),
    FOREIGN KEY ("address_id") REFERENCES "addresses"("id")
);

COMMENT ON COLUMN "users"."email" IS 'The user''s email address';
```

### Drop Table

```java
// Drop table (fails if doesn't exist)
UserTable.TABLE.drop();

// Drop only if exists
UserTable.TABLE.dropIfExists();

// Drop with CASCADE (removes dependent objects)
UserTable.TABLE.drop(SqlDropOptions.builder()
    .ifExists(true)
    .cascade(true)           // Drop dependent views, FKs, etc.
    .build());
```

Generated SQL:
```sql
DROP TABLE IF EXISTS "users" CASCADE;
```

### Truncate Table

```java
// Remove all rows (faster than DELETE)
UserTable.TABLE.truncate();

// Truncate with options
UserTable.TABLE.truncate(SqlTruncateOptions.builder()
    .cascade(true)           // Also truncate referencing tables
    .restartIdentity(true)   // Reset sequences to initial value
    .build());
```

Generated SQL:
```sql
TRUNCATE TABLE "users" RESTART IDENTITY CASCADE;
```

### Check Table Existence

```java
// Check if table exists in database
boolean exists = UserTable.TABLE.exists();

if (!UserTable.TABLE.exists()) {
    UserTable.TABLE.create();
}
```

## Index Operations

### Create Index

```java
// Simple index on single column
UserTable.TABLE.createIndex("idx_users_email", UserTable.EMAIL);

// Composite index
UserTable.TABLE.createIndex("idx_users_status_created",
    UserTable.STATUS, UserTable.CREATED_AT);

// Unique index
UserTable.TABLE.createIndex(SqlIndexDefinition.builder()
    .name("idx_users_email_unique")
    .columns("email")
    .unique(true)
    .build());

// Partial index (PostgreSQL)
UserTable.TABLE.createIndex(SqlIndexDefinition.builder()
    .name("idx_active_users")
    .columns("email")
    .where("status = 'ACTIVE'")
    .build());

// Expression index
UserTable.TABLE.createIndex(SqlIndexDefinition.builder()
    .name("idx_users_email_lower")
    .expression("LOWER(email)")
    .unique(true)
    .build());

// Index with specific method (PostgreSQL)
UserTable.TABLE.createIndex(SqlIndexDefinition.builder()
    .name("idx_users_metadata")
    .columns("metadata")
    .method(SqlIndexMethod.GIN)      // For JSONB columns
    .build());
```

Generated SQL:
```sql
CREATE INDEX "idx_users_email" ON "users" ("email");
CREATE INDEX "idx_users_status_created" ON "users" ("status", "created_at");
CREATE UNIQUE INDEX "idx_users_email_unique" ON "users" ("email");
CREATE INDEX "idx_active_users" ON "users" ("email") WHERE status = 'ACTIVE';
CREATE UNIQUE INDEX "idx_users_email_lower" ON "users" (LOWER(email));
CREATE INDEX "idx_users_metadata" ON "users" USING GIN ("metadata");
```

### Drop Index

```java
// Drop index
UserTable.TABLE.dropIndex("idx_users_email");

// Drop if exists
UserTable.TABLE.dropIndex("idx_users_email", SqlDropOptions.ifExists());
```

Generated SQL:
```sql
DROP INDEX IF EXISTS "idx_users_email";
```

### List Indexes

```java
// Get all indexes on table
List<SqlIndexInfo> indexes = UserTable.TABLE.listIndexes();

for (SqlIndexInfo idx : indexes) {
    System.out.println(idx.name() + ": " + idx.columns() +
        (idx.isUnique() ? " (UNIQUE)" : ""));
}
```

## Sequence Operations

```java
// Create sequence
UserTable.TABLE.createSequence(SqlSequenceDefinition.builder()
    .name("users_id_seq")
    .startWith(1)
    .incrementBy(1)
    .minValue(1)
    .maxValue(Long.MAX_VALUE)
    .cache(50)
    .build());

// Drop sequence
UserTable.TABLE.dropSequence("users_id_seq");

// Get next value
long nextId = UserTable.TABLE.nextSequenceValue("users_id_seq");

// Get current value
long currentId = UserTable.TABLE.currentSequenceValue("users_id_seq");

// Reset sequence
UserTable.TABLE.resetSequence("users_id_seq", 1);
```

Generated SQL:
```sql
CREATE SEQUENCE "users_id_seq" START WITH 1 INCREMENT BY 1 MINVALUE 1 CACHE 50;
DROP SEQUENCE "users_id_seq";
SELECT nextval('users_id_seq');
SELECT currval('users_id_seq');
ALTER SEQUENCE "users_id_seq" RESTART WITH 1;
```

## Schema Operations

```java
// Create schema
SqlSchema.create("audit");
SqlSchema.createIfNotExists("audit");

// Drop schema
SqlSchema.drop("audit");
SqlSchema.drop("audit", SqlDropOptions.cascade());

// Check if schema exists
boolean exists = SqlSchema.exists("audit");

// Set search path (PostgreSQL)
SqlSchema.setSearchPath("public", "audit");
```

## Batch DDL Operations

For multiple DDL operations in a transaction:

```java
try (SqlTransaction tx = database.beginTransaction()) {
    // Create tables in dependency order
    AddressTable.TABLE.createIfNotExists();
    UserTable.TABLE.createIfNotExists();
    OrderTable.TABLE.createIfNotExists();

    // Add indexes
    UserTable.TABLE.createIndex("idx_users_email", UserTable.EMAIL);
    OrderTable.TABLE.createIndex("idx_orders_customer", OrderTable.CUSTOMER_ID);

    tx.commit();
}
```

## DDL Generation Without Execution

```java
// Get DDL SQL without executing
String createSql = UserTable.TABLE.generateCreateSql();
String dropSql = UserTable.TABLE.generateDropSql();

// Useful for:
// - Review before execution
// - Export for DBA review
// - Migration scripts
System.out.println(createSql);
```

## Schema Changes Workflow

Since runtime table modifications are not supported in v3, follow this workflow for schema changes:

### 1. Update YAML Schema

```yaml
# user.yaml - add a new field
entity: User
table: users

fields:
  id:
    type: UUID
    primary: true
  email:
    type: String
    unique: true
  name:
    type: String
  phone:                    # New field added
    type: String
    nullable: true
```

### 2. Regenerate Code

```bash
./gradlew generateDbSources
```

### 3. Create Migration Script

For production environments, create a migration script:

```sql
-- V2__add_phone_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(255);
```

### 4. Apply Migration

Use your preferred migration tool (Flyway, Liquibase, etc.) to apply the migration in production.

## Why No Runtime Modifications?

V3 removes runtime table modification operations (`addColumn`, `dropColumn`, `alterColumn`, etc.) for several reasons:

1. **Schema as Code** - The YAML schema is the single source of truth for database structure
2. **Type Safety** - Generated code always matches the schema definition
3. **Migration Control** - Schema changes go through a proper review process
4. **Production Safety** - Prevents accidental schema modifications in production
5. **Consistency** - All environments use the same schema from code generation

For development, you can:
- Use `drop()` + `create()` to recreate tables
- Use database-specific tools for ad-hoc modifications
- Use migration tools for version-controlled schema changes

## Related Documents

- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Schema definitions
- [dialect-comparison.md](dialect-comparison.md) - DDL differences per dialect
- [transaction-api.md](transaction-api.md) - DDL in transactions
