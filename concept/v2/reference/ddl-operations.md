# DDL Operations

## Overview

DDL (Data Definition Language) operations manage database schema: creating, altering, and dropping tables, indexes, sequences, and constraints. These operations are available on generated table references.

## Table Operations

### Create Table

```java
// Create table with all columns and constraints
UserTable.TABLE.create();

// Create only if it doesn't exist
UserTable.TABLE.createIfNotExists();

// Create with options
UserTable.TABLE.create(CreateOptions.builder()
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
UserTable.TABLE.drop(DropOptions.builder()
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
UserTable.TABLE.truncate(TruncateOptions.builder()
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

## Column Operations

### Add Column

```java
// Add a new column
UserTable.TABLE.addColumn(
    ColumnDefinition.builder()
        .name("phone")
        .type(String.class)
        .nullable(true)
        .build()
);

// Add column with default value
UserTable.TABLE.addColumn(
    ColumnDefinition.builder()
        .name("verified")
        .type(Boolean.class)
        .nullable(false)
        .defaultValue("false")
        .build()
);
```

Generated SQL:
```sql
ALTER TABLE "users" ADD COLUMN "phone" VARCHAR(255);
ALTER TABLE "users" ADD COLUMN "verified" BOOLEAN NOT NULL DEFAULT false;
```

### Drop Column

```java
// Drop a column
UserTable.TABLE.dropColumn("phone");

// Drop with CASCADE (removes dependent constraints)
UserTable.TABLE.dropColumn("phone", DropOptions.cascade());
```

Generated SQL:
```sql
ALTER TABLE "users" DROP COLUMN "phone" CASCADE;
```

### Alter Column

```java
// Change column type
UserTable.TABLE.alterColumn("name")
    .setType(String.class, TypeOptions.withLength(500))
    .execute();

// Change nullability
UserTable.TABLE.alterColumn("phone")
    .setNullable(false)
    .setDefault("'unknown'")
    .execute();

// Rename column
UserTable.TABLE.alterColumn("name")
    .renameTo("full_name")
    .execute();
```

Generated SQL:
```sql
ALTER TABLE "users" ALTER COLUMN "name" TYPE VARCHAR(500);
ALTER TABLE "users" ALTER COLUMN "phone" SET NOT NULL;
ALTER TABLE "users" ALTER COLUMN "phone" SET DEFAULT 'unknown';
ALTER TABLE "users" RENAME COLUMN "name" TO "full_name";
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
UserTable.TABLE.createIndex(IndexDefinition.builder()
    .name("idx_users_email_unique")
    .columns("email")
    .unique(true)
    .build());

// Partial index (PostgreSQL)
UserTable.TABLE.createIndex(IndexDefinition.builder()
    .name("idx_active_users")
    .columns("email")
    .where("status = 'ACTIVE'")
    .build());

// Expression index
UserTable.TABLE.createIndex(IndexDefinition.builder()
    .name("idx_users_email_lower")
    .expression("LOWER(email)")
    .unique(true)
    .build());

// Index with specific method (PostgreSQL)
UserTable.TABLE.createIndex(IndexDefinition.builder()
    .name("idx_users_metadata")
    .columns("metadata")
    .method(IndexMethod.GIN)      // For JSONB columns
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
UserTable.TABLE.dropIndex("idx_users_email", DropOptions.ifExists());
```

Generated SQL:
```sql
DROP INDEX IF EXISTS "idx_users_email";
```

### List Indexes

```java
// Get all indexes on table
List<IndexInfo> indexes = UserTable.TABLE.listIndexes();

for (IndexInfo idx : indexes) {
    System.out.println(idx.name() + ": " + idx.columns() +
        (idx.isUnique() ? " (UNIQUE)" : ""));
}
```

## Constraint Operations

### Add Constraint

```java
// Add unique constraint
UserTable.TABLE.addConstraint(
    Constraint.unique("uq_users_email", "email")
);

// Add check constraint
UserTable.TABLE.addConstraint(
    Constraint.check("chk_users_age", "age >= 0 AND age <= 150")
);

// Add foreign key constraint
OrderTable.TABLE.addConstraint(
    Constraint.foreignKey("fk_orders_customer")
        .column("customer_id")
        .references("users", "id")
        .onDelete(ForeignKeyAction.CASCADE)
        .onUpdate(ForeignKeyAction.CASCADE)
);

// Add primary key (rare - usually defined at creation)
UserTable.TABLE.addConstraint(
    Constraint.primaryKey("pk_users", "id")
);
```

Generated SQL:
```sql
ALTER TABLE "users" ADD CONSTRAINT "uq_users_email" UNIQUE ("email");
ALTER TABLE "users" ADD CONSTRAINT "chk_users_age" CHECK (age >= 0 AND age <= 150);
ALTER TABLE "orders" ADD CONSTRAINT "fk_orders_customer"
    FOREIGN KEY ("customer_id") REFERENCES "users"("id")
    ON DELETE CASCADE ON UPDATE CASCADE;
```

### Drop Constraint

```java
// Drop constraint by name
UserTable.TABLE.dropConstraint("uq_users_email");

// Drop with CASCADE
UserTable.TABLE.dropConstraint("fk_orders_customer", DropOptions.cascade());
```

Generated SQL:
```sql
ALTER TABLE "users" DROP CONSTRAINT "uq_users_email";
ALTER TABLE "orders" DROP CONSTRAINT "fk_orders_customer" CASCADE;
```

### List Constraints

```java
// Get all constraints on table
List<ConstraintInfo> constraints = UserTable.TABLE.listConstraints();

for (ConstraintInfo c : constraints) {
    System.out.println(c.type() + ": " + c.name());
}
```

## Sequence Operations

```java
// Create sequence
UserTable.TABLE.createSequence(SequenceDefinition.builder()
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
Schema.create("audit");
Schema.createIfNotExists("audit");

// Drop schema
Schema.drop("audit");
Schema.drop("audit", DropOptions.cascade());

// Check if schema exists
boolean exists = Schema.exists("audit");

// Set search path (PostgreSQL)
Schema.setSearchPath("public", "audit");
```

## Batch DDL Operations

For multiple DDL operations in a transaction:

```java
try (Transaction tx = database.beginTransaction()) {
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

## Schema Migration Support

```java
// Check current schema version
int version = SchemaVersion.current();

// Apply migration
if (version < 2) {
    UserTable.TABLE.addColumn(
        ColumnDefinition.builder()
            .name("phone")
            .type(String.class)
            .nullable(true)
            .build()
    );
    SchemaVersion.update(2);
}

if (version < 3) {
    UserTable.TABLE.createIndex("idx_users_phone", "phone");
    SchemaVersion.update(3);
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

## Related Documents

- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Schema definitions
- [dialect-comparison.md](dialect-comparison.md) - DDL differences per dialect
- [transaction-api.md](transaction-api.md) - DDL in transactions
