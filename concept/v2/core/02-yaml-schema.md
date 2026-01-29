# YAML Schema Specification

## Overview

The YAML schema defines database entities, their fields, relationships, and constraints. Schema files are processed at compile-time to generate type-safe Java code.

## File Organization

Each entity is defined in its own YAML file:

```
src/main/resources/schema/
├── _config.yaml          # Global configuration
├── user.yaml             # User entity
├── address.yaml          # Address entity
├── order.yaml            # Order entity
├── order_item.yaml       # OrderItem entity
├── tag.yaml              # Tag entity
└── audit/
    └── audit_log.yaml    # Namespaced entity
```

**Important:** Each file contains exactly one entity definition.

## Global Configuration

The `_config.yaml` file defines project-wide settings:

```yaml
# _config.yaml
config:
  # Default schema for all entities
  defaultSchema: public

  # Naming conventions
  naming:
    table: snake_case      # user_profile -> user_profile
    column: snake_case     # createdAt -> created_at
    entity: PascalCase     # user_profile -> UserProfile
    field: camelCase       # created_at -> createdAt

  # Default field options
  defaults:
    nullable: false
    updatable: true
    insertable: true

  # Row type configuration
  rows:
    maxCount: 8            # Generate Row2 through Row8 (default: 8)

  # Soft delete configuration
  softDelete:
    enabled: false
    column: deleted_at
    type: Instant
```

## Entity Definition

### Basic Entity

```yaml
# user.yaml
entity: User
table: users
schema: public

fields:
  id:
    type: UUID
    primary: true
    generated: uuid

  email:
    type: String
    column: email_address
    unique: true
    index: true
    constraints:
      notBlank: true
      maxLength: 255

  name:
    type: String
    constraints:
      notBlank: true
      maxLength: 100

  status:
    type: UserStatus
    enum: [PENDING, ACTIVE, SUSPENDED, DELETED]
    default: PENDING

  createdAt:
    type: Instant
    column: created_at
    insertable: true
    updatable: false
    default: now()
```

### Entity with Relationships

Relationships are defined as fields with a `relationship` block containing all relationship options:

```yaml
# order.yaml
entity: Order
table: orders

fields:
  id:
    type: UUID
    primary: true
    generated: uuid

  orderNumber:
    type: String
    column: order_number
    unique: true

  total:
    type: BigDecimal
    constraints:
      positive: true

  # ============================================
  # RELATIONSHIP FIELDS
  # ============================================

  # Many-to-One relationship
  customer:
    type: UUID                        # FK column type
    column: customer_id               # FK column name
    relationship:
      type: many-to-one
      target: User                    # Target entity
      fetch: lazy                     # lazy | eager
      optional: false                 # Creates NOT NULL on FK
      cascade: [persist, merge]       # Cascade operations
      onDelete: restrict              # cascade | set-null | restrict | no-action
      onUpdate: no-action             # cascade | set-null | restrict | no-action

  # Many-to-One with minimal config (uses defaults)
  assignedAgent:
    type: UUID
    column: agent_id
    nullable: true
    relationship:
      type: many-to-one
      target: User
      optional: true
      onDelete: set-null

  # One-to-Many relationship
  items:
    relationship:
      type: one-to-many
      target: OrderItem
      mappedBy: order                 # Field in OrderItem that owns the FK
      fetch: lazy
      cascade: [all]
      orphanRemoval: true
      orderBy:
        - field: createdAt
          direction: desc

  # Many-to-Many relationship
  tags:
    relationship:
      type: many-to-many
      target: Tag
      fetch: lazy
      cascade: [persist, merge]
      joinTable:
        name: order_tags
        schema: public
        joinColumn: order_id
        inverseJoinColumn: tag_id
        columns:                      # Extra columns on join table
          - name: assigned_at
            type: Instant
            default: now()
          - name: assigned_by
            type: String

  createdAt:
    type: Instant
    column: created_at
    insertable: true
    updatable: false
    default: now()
```

```yaml
# order_item.yaml
entity: OrderItem
table: order_items

fields:
  id:
    type: UUID
    primary: true
    generated: uuid

  # Many-to-One back to Order (owns the relationship)
  order:
    type: UUID
    column: order_id
    relationship:
      type: many-to-one
      target: Order
      fetch: lazy
      optional: false
      cascade: [persist, merge]
      onDelete: cascade

  # Many-to-One to Product
  product:
    type: UUID
    column: product_id
    relationship:
      type: many-to-one
      target: Product
      fetch: lazy
      optional: false
      onDelete: restrict

  quantity:
    type: Integer
    constraints:
      min: 1

  price:
    type: BigDecimal
    constraints:
      positive: true
```

## Relationship Types

### Many-to-One

The most common relationship. This entity holds the foreign key.

```yaml
fields:
  author:
    type: UUID                        # FK column type (matches target PK)
    column: author_id                 # FK column name (optional, defaults to {field}_id)
    nullable: false                   # NOT NULL on the FK column
    index: true                       # Index the FK column
    relationship:
      type: many-to-one
      target: User                    # Target entity name
      fetch: lazy                     # lazy (default) | eager
      optional: false                 # Whether null FK is allowed
      cascade: [persist, merge]       # Operations to cascade
      onDelete: cascade               # FK constraint action
      onUpdate: no-action             # FK constraint action
```

**All Many-to-One Options:**

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `type` | string | required | Must be `many-to-one` |
| `target` | string | required | Target entity name |
| `fetch` | enum | `lazy` | `lazy` or `eager` loading strategy |
| `optional` | boolean | `true` | Whether the relationship is optional |
| `cascade` | list | `[]` | `persist`, `merge`, `remove`, `refresh`, `detach`, `all` |
| `onDelete` | enum | `no-action` | `cascade`, `set-null`, `restrict`, `no-action` |
| `onUpdate` | enum | `no-action` | `cascade`, `set-null`, `restrict`, `no-action` |

**Generated Code:**
```java
public record Post(
    UUID id,
    String title,
    SqlKey<UUID, User> author     // FK always available, entity loaded on demand
) {}

// Access FK (always available)
UUID authorId = post.author().key();

// Access loaded entity (only if fetched)
User author = post.author().value();
```

### One-to-Many

The inverse of many-to-one. This entity does NOT hold the FK.

```yaml
fields:
  comments:
    relationship:
      type: one-to-many
      target: Comment                 # Target entity name
      mappedBy: post                  # Field in Comment that holds the FK
      fetch: lazy                     # lazy (default) | eager
      cascade: [all]                  # Operations to cascade
      orphanRemoval: true             # Delete orphans when removed from collection
      orderBy:                        # Default ordering
        - field: createdAt
          direction: desc
        - field: id
          direction: asc
```

**All One-to-Many Options:**

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `type` | string | required | Must be `one-to-many` |
| `target` | string | required | Target entity name |
| `mappedBy` | string | required | Field in target entity that owns the relationship |
| `fetch` | enum | `lazy` | `lazy` or `eager` loading strategy |
| `cascade` | list | `[]` | `persist`, `merge`, `remove`, `refresh`, `detach`, `all` |
| `orphanRemoval` | boolean | `false` | Delete children removed from collection |
| `orderBy` | list | `[]` | Default ordering when collection is loaded |

**Generated Code:**
```java
public record Post(
    UUID id,
    String title,
    SqlKey<UUID, List<Comment>> comments  // Loaded via separate query or join
) {}
```

### One-to-One

Like many-to-one but with unique constraint on FK.

```yaml
fields:
  # Owning side (has the FK column)
  profile:
    type: UUID
    column: profile_id
    unique: true                      # Enforces one-to-one
    nullable: true
    relationship:
      type: one-to-one
      target: UserProfile
      fetch: eager                    # Often eager for one-to-one
      optional: true
      cascade: [all]
      onDelete: cascade

  # OR inverse side (no FK column)
  profile:
    relationship:
      type: one-to-one
      target: UserProfile
      mappedBy: user                  # FK is on UserProfile.user
      fetch: eager
      cascade: [all]
      orphanRemoval: true
```

**All One-to-One Options:**

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `type` | string | required | Must be `one-to-one` |
| `target` | string | required | Target entity name |
| `mappedBy` | string | - | Field in target (for inverse side only) |
| `fetch` | enum | `lazy` | `lazy` or `eager` loading strategy |
| `optional` | boolean | `true` | Whether the relationship is optional |
| `cascade` | list | `[]` | `persist`, `merge`, `remove`, `refresh`, `detach`, `all` |
| `orphanRemoval` | boolean | `false` | Delete orphan when relationship cleared |
| `onDelete` | enum | `no-action` | FK constraint action (owning side only) |
| `onUpdate` | enum | `no-action` | FK constraint action (owning side only) |

### Many-to-Many

Requires a join/junction table.

```yaml
fields:
  tags:
    relationship:
      type: many-to-many
      target: Tag
      fetch: lazy
      cascade: [persist, merge]
      joinTable:
        name: post_tags               # Junction table name
        schema: public                # Optional schema
        joinColumn: post_id           # This entity's FK column
        inverseJoinColumn: tag_id     # Target entity's FK column
        columns:                      # Additional columns on junction table
          - name: assigned_at
            type: Instant
            default: now()
          - name: position
            type: Integer
            default: 0
      orderBy:
        - field: name
          direction: asc
```

**All Many-to-Many Options:**

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `type` | string | required | Must be `many-to-many` |
| `target` | string | required | Target entity name |
| `fetch` | enum | `lazy` | `lazy` or `eager` loading strategy |
| `cascade` | list | `[]` | `persist`, `merge`, `remove`, `refresh`, `detach`, `all` |
| `joinTable` | object | required | Junction table configuration |
| `joinTable.name` | string | required | Junction table name |
| `joinTable.schema` | string | - | Junction table schema |
| `joinTable.joinColumn` | string | required | This entity's FK column |
| `joinTable.inverseJoinColumn` | string | required | Target entity's FK column |
| `joinTable.columns` | list | `[]` | Additional columns on junction table |
| `orderBy` | list | `[]` | Default ordering when collection is loaded |

**Generated Code:**
```java
public record Post(
    UUID id,
    String title,
    SqlKey<UUID, List<Tag>> tags      // Loaded via junction table
) {}
```

### Self-Referencing Relationships

```yaml
# employee.yaml
entity: Employee
table: employees

fields:
  id:
    type: UUID
    primary: true
    generated: uuid

  name:
    type: String

  # Many-to-one to same entity (employee has one manager)
  manager:
    type: UUID
    column: manager_id
    nullable: true
    relationship:
      type: many-to-one
      target: Employee                # Self-reference
      fetch: lazy
      optional: true
      onDelete: set-null

  # One-to-many inverse (manager has many direct reports)
  directReports:
    relationship:
      type: one-to-many
      target: Employee
      mappedBy: manager
      fetch: lazy
      orderBy:
        - field: name
          direction: asc
```

## Complete Example

```yaml
# blog_post.yaml
entity: BlogPost
table: blog_posts

audit:
  createdAt:
    column: created_at
    type: Instant
  updatedAt:
    column: updated_at
    type: Instant
  createdBy:
    column: created_by
    type: String

fields:
  # Primary key
  id:
    type: UUID
    primary: true
    generated: uuid

  # Regular fields
  title:
    type: String
    constraints:
      notBlank: true
      maxLength: 200

  slug:
    type: String
    unique: true
    constraints:
      pattern: "^[a-z0-9-]+$"

  content:
    type: String
    columnType: TEXT

  status:
    type: PostStatus
    enum: [DRAFT, PUBLISHED, ARCHIVED]
    default: DRAFT

  publishedAt:
    type: Instant
    column: published_at
    nullable: true

  viewCount:
    type: Integer
    column: view_count
    default: 0

  # Many-to-One: Post belongs to User (author)
  author:
    type: UUID
    column: author_id
    nullable: false
    index: true
    relationship:
      type: many-to-one
      target: User
      fetch: lazy
      optional: false
      cascade: [persist, merge]
      onDelete: restrict

  # Many-to-One: Post optionally belongs to Category
  category:
    type: UUID
    column: category_id
    nullable: true
    index: true
    relationship:
      type: many-to-one
      target: Category
      fetch: lazy
      optional: true
      onDelete: set-null

  # One-to-Many: Post has many Comments
  comments:
    relationship:
      type: one-to-many
      target: Comment
      mappedBy: post
      fetch: lazy
      cascade: [all]
      orphanRemoval: true
      orderBy:
        - field: createdAt
          direction: desc

  # Many-to-Many: Post has many Tags
  tags:
    relationship:
      type: many-to-many
      target: Tag
      fetch: lazy
      cascade: [persist, merge]
      joinTable:
        name: post_tags
        joinColumn: post_id
        inverseJoinColumn: tag_id

  # Many-to-Many: Self-referencing (related posts)
  relatedPosts:
    relationship:
      type: many-to-many
      target: BlogPost
      fetch: lazy
      joinTable:
        name: related_posts
        joinColumn: post_id
        inverseJoinColumn: related_post_id

# Indexes
indexes:
  - name: idx_posts_author_status
    columns: [author_id, status]

  - name: idx_posts_published
    columns: [published_at]
    where: "status = 'PUBLISHED'"
```

## Field Types

### Primitive Types

| YAML Type | Java Type | PostgreSQL | MySQL | SQLite |
|-----------|-----------|------------|-------|--------|
| `Boolean` | `boolean` / `Boolean` | `BOOLEAN` | `TINYINT(1)` | `INTEGER` |
| `Byte` | `byte` / `Byte` | `SMALLINT` | `TINYINT` | `INTEGER` |
| `Short` | `short` / `Short` | `SMALLINT` | `SMALLINT` | `INTEGER` |
| `Integer` | `int` / `Integer` | `INTEGER` | `INT` | `INTEGER` |
| `Long` | `long` / `Long` | `BIGINT` | `BIGINT` | `INTEGER` |
| `Float` | `float` / `Float` | `REAL` | `FLOAT` | `REAL` |
| `Double` | `double` / `Double` | `DOUBLE PRECISION` | `DOUBLE` | `REAL` |
| `BigInteger` | `BigInteger` | `NUMERIC` | `DECIMAL(65,0)` | `TEXT` |
| `BigDecimal` | `BigDecimal` | `NUMERIC(p,s)` | `DECIMAL(p,s)` | `TEXT` |

### String Types

| YAML Type | Java Type | PostgreSQL | MySQL | SQLite |
|-----------|-----------|------------|-------|--------|
| `String` | `String` | `VARCHAR(n)` | `VARCHAR(n)` | `TEXT` |
| `Text` | `String` | `TEXT` | `TEXT` | `TEXT` |
| `Char` | `char` / `Character` | `CHAR(1)` | `CHAR(1)` | `TEXT` |

### Temporal Types

| YAML Type | Java Type | PostgreSQL | MySQL | SQLite |
|-----------|-----------|------------|-------|--------|
| `Instant` | `Instant` | `TIMESTAMPTZ` | `DATETIME(6)` | `TEXT` |
| `LocalDate` | `LocalDate` | `DATE` | `DATE` | `TEXT` |
| `LocalTime` | `LocalTime` | `TIME` | `TIME(6)` | `TEXT` |
| `LocalDateTime` | `LocalDateTime` | `TIMESTAMP` | `DATETIME` | `TEXT` |
| `OffsetDateTime` | `OffsetDateTime` | `TIMESTAMPTZ` | `DATETIME(6)` | `TEXT` |
| `ZonedDateTime` | `ZonedDateTime` | `TIMESTAMPTZ` | `DATETIME(6)` | `TEXT` |
| `Duration` | `Duration` | `INTERVAL` | `BIGINT` | `INTEGER` |
| `Period` | `Period` | `INTERVAL` | `VARCHAR(50)` | `TEXT` |

### Other Types

| YAML Type | Java Type | PostgreSQL | MySQL | SQLite |
|-----------|-----------|------------|-------|--------|
| `UUID` | `UUID` | `UUID` | `CHAR(36)` | `TEXT` |
| `byte[]` | `byte[]` | `BYTEA` | `BLOB` | `BLOB` |
| `Json` | `JsonElement` | `JSONB` | `JSON` | `TEXT` |
| `Enum` | Generated enum | `VARCHAR` | `VARCHAR` | `TEXT` |

### Collection Types

```yaml
fields:
  tags:
    type: List<String>
    columnType: "TEXT[]"       # PostgreSQL array

  metadata:
    type: Map<String, String>
    columnType: JSONB          # Stored as JSON
```

## Field Options

### Common Options

```yaml
fields:
  example:
    type: String

    # Column mapping
    column: example_column     # SQL column name (default: field name in snake_case)
    columnType: VARCHAR(100)   # Override SQL type

    # Nullability
    nullable: false            # NOT NULL constraint (default: false)

    # Mutability
    insertable: true           # Include in INSERT (default: true)
    updatable: true            # Include in UPDATE (default: true)

    # Query options
    select: true               # Include in default SELECT (default: true)

    # Indexing
    index: true                # Create single-column index
    unique: true               # Unique constraint
```

### Primary Key Options

```yaml
fields:
  id:
    type: Long
    primary: true              # Mark as primary key
    generated: identity        # identity, sequence, uuid, or omit for manual

  # With sequence
  id:
    type: Long
    primary: true
    generated: sequence
    sequence:
      name: user_id_seq
      startWith: 1
      incrementBy: 50
```

### Default Values

```yaml
fields:
  status:
    type: String
    default: "PENDING"         # Static value

  createdAt:
    type: Instant
    default: now()             # Database function

  sortOrder:
    type: Integer
    default: "(SELECT COALESCE(MAX(sort_order), 0) + 1 FROM items)"
```

## Constraints

### String Constraints

```yaml
fields:
  email:
    type: String
    constraints:
      notNull: true
      notBlank: true
      minLength: 5
      maxLength: 255
      pattern: "^[\\w.-]+@[\\w.-]+\\.\\w+$"
```

### Numeric Constraints

```yaml
fields:
  age:
    type: Integer
    constraints:
      min: 0
      max: 150

  price:
    type: BigDecimal
    constraints:
      positive: true
      scale: 2
      precision: 10
```

### Collection Constraints

```yaml
fields:
  tags:
    type: List<String>
    constraints:
      notEmpty: true
      minSize: 1
      maxSize: 10
      elements:
        notBlank: true
        maxLength: 50
```

## Enums

### Inline Definition

```yaml
fields:
  status:
    type: UserStatus
    enum: [PENDING, ACTIVE, SUSPENDED, DELETED]
    default: PENDING
```

### With Storage Type

```yaml
fields:
  status:
    type: UserStatus
    enum:
      values: [PENDING, ACTIVE, SUSPENDED, DELETED]
      storage: ordinal         # ordinal or string (default: string)
    default: PENDING
```

## Audit Configuration

Per-table audit (no global defaults):

```yaml
entity: User
table: users

audit:
  createdAt:
    column: created_at
    type: Instant
  updatedAt:
    column: updated_at
    type: Instant
  createdBy:
    column: created_by
    type: String
  updatedBy:
    column: updated_by
    type: String

fields:
  # ... regular fields
```

## Cascade Operations

| Value | Description |
|-------|-------------|
| `persist` | Cascade persist (insert) operations |
| `merge` | Cascade merge (update) operations |
| `remove` | Cascade remove (delete) operations |
| `refresh` | Cascade refresh operations |
| `detach` | Cascade detach operations |
| `all` | Cascade all operations (persist, merge, remove, refresh, detach) |

## Foreign Key Actions

| Value | Description |
|-------|-------------|
| `cascade` | Delete/update child rows when parent is deleted/updated |
| `set-null` | Set FK to NULL when parent is deleted/updated |
| `restrict` | Prevent delete/update if children exist (checked immediately) |
| `no-action` | Prevent delete/update if children exist (checked at end of transaction) |

## Schema Validation

The generator validates:

- **Type consistency** - Field types match Java types
- **Relationship validity** - Target entities exist
- **FK column types** - Match referenced PK types
- **mappedBy validity** - Referenced field exists in target
- **Circular dependencies** - Detected and reported
- **Naming conflicts** - No duplicate names
- **Index coverage** - Warning for unindexed FKs

```
Schema validation failed:

order.yaml:15:5 - Error: Relationship target 'Users' not found (did you mean 'User'?)
order.yaml:23:5 - Error: mappedBy 'orders' not found in OrderItem (available: order, product)
post.yaml:8:7 - Warning: Foreign key 'author_id' is not indexed
```

## Related Documents

- [01-overview.md](01-overview.md) - Architecture overview
- [03-query-api.md](03-query-api.md) - Query builder API
- [04-code-generation.md](04-code-generation.md) - Code generation details
- [../reference/relationship-loading.md](../reference/relationship-loading.md) - Loading strategies
- [../reference/ddl-operations.md](../reference/ddl-operations.md) - DDL operations
