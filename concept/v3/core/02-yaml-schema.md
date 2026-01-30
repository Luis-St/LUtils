# YAML Schema Specification

## Overview

The YAML schema defines database entities, their fields, relationships, and constraints. Schema files are processed at compile-time to generate type-safe Java code.

## File Organization

Each entity is defined in its own YAML file:

```
src/main/resources/schema/
+-- _config.yaml          # Global configuration
+-- user.yaml             # User entity
+-- address.yaml          # Address entity
+-- order.yaml            # Order entity
+-- order_item.yaml       # OrderItem entity
+-- tag.yaml              # Tag entity
+-- audit/
    +-- audit_log.yaml    # Namespaced entity
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

Relationships are defined as fields with a `relationship` block.

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

  # Many-to-One relationship - base entity gets raw FK value
  customerId:
    type: UUID                        # FK column type
    column: customer_id               # FK column name
    relationship:
      type: many-to-one
      target: User                    # Target entity
      optional: false                 # Creates NOT NULL on FK
      onDelete: restrict              # cascade | set-null | restrict | no-action
      onUpdate: no-action             # cascade | set-null | restrict | no-action

  # Many-to-One with optional reference
  agentId:
    type: UUID
    column: agent_id
    nullable: true
    relationship:
      type: many-to-one
      target: User
      optional: true
      onDelete: set-null

  createdAt:
    type: Instant
    column: created_at
    insertable: true
    updatable: false
    default: now()
```

**Generated Entity Variants:**

```java
// Base entity - raw FK values only
public record Order(
    UUID id,
    String orderNumber,
    BigDecimal total,
    UUID customerId,           // Raw FK value
    UUID agentId,              // Raw FK value (nullable)
    Instant createdAt
) {}

// FullJoined - FK replaced with loaded entities
public record OrderFullJoined(
    UUID id,
    String orderNumber,
    BigDecimal total,
    User customer,             // Loaded entity
    User agent,                // Loaded entity (nullable)
    Instant createdAt
) {}

// PartialJoined - FK wrapped in SqlForeignKey
public record OrderPartialJoined(
    UUID id,
    String orderNumber,
    BigDecimal total,
    SqlForeignKey<UUID, User> customerKey,  // Key with optional value
    SqlForeignKey<UUID, User> agentKey,     // Key with optional value
    Instant createdAt
) {}
```

### One-to-Many Relationships

One-to-many relationships are **omitted from base entities** and only appear in FullJoined/PartialJoined variants:

```yaml
# user.yaml
entity: User
table: users

fields:
  id:
    type: UUID
    primary: true
    generated: uuid

  name:
    type: String

  addressId:
    type: UUID
    column: address_id
    relationship:
      type: many-to-one
      target: Address

  # One-to-Many: User has many Orders
  # Note: This creates NO column in the users table
  # It's the inverse of Order.customerId
  orders:
    relationship:
      type: one-to-many
      target: Order
      mappedBy: customerId      # Field in Order that owns the FK
      orderBy:
        - field: createdAt
          direction: desc
```

**Generated Entity Variants:**

```java
// Base entity
public record User(
    UUID id,
    String name,
    UUID addressId,
    List<UUID> orderIds
) {}

// FullJoined - includes loaded collections
public record UserFullJoined(
    UUID id,
    String name,
    Address address,
    List<Order> orders         // One-to-many loaded via join
) {}

// PartialJoined - collections wrapped in SqlForeignKey
public record UserPartialJoined(
    UUID id,
    String name,
    SqlForeignKey<UUID, Address> addressKey,
    SqlForeignKey<UUID, List<Order>> ordersKey   // Lazy-loadable
) {}
```

## Relationship Types

### Many-to-One

The most common relationship. This entity holds the foreign key.

```yaml
fields:
  authorId:
    type: UUID                        # FK column type (matches target PK)
    column: author_id                 # FK column name
    nullable: false                   # NOT NULL on the FK column
    index: true                       # Index the FK column
    relationship:
      type: many-to-one
      target: User                    # Target entity name
      optional: false                 # Whether null FK is allowed
      onDelete: cascade               # FK constraint action
      onUpdate: no-action             # FK constraint action
```

**All Many-to-One Options:**

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `type` | string | required | Must be `many-to-one` |
| `target` | string | required | Target entity name |
| `optional` | boolean | `true` | Whether the relationship is optional |
| `onDelete` | enum | `no-action` | `cascade`, `set-null`, `restrict`, `no-action` |
| `onUpdate` | enum | `no-action` | `cascade`, `set-null`, `restrict`, `no-action` |

**Generated Code:**

```java
// Base entity - raw FK
public record Post(
    UUID id,
    String title,
    UUID authorId         // Raw FK value
) {}

// PartialJoined - SqlForeignKey wrapper
public record PostPartialJoined(
    UUID id,
    String title,
    SqlForeignKey<UUID, User> authorKey
) {}

// Access FK (always available on base)
UUID authorId = post.authorId();

// Access loaded entity (on PartialJoined)
User author = postPartialJoined.authorKey().value();
```

### One-to-Many

The inverse of many-to-one. This entity does NOT hold the FK.

```yaml
fields:
  comments:
    relationship:
      type: one-to-many
      target: Comment                 # Target entity name
      mappedBy: postId                # Field in Comment that holds the FK
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
| `orderBy` | list | `[]` | Default ordering when collection is loaded |

**Generated Code:**

```java
// Base entity
public record Post(
    UUID id,
    String title,
    List<UUID> commentIds        // List of FK values (not loaded)
) {}

// FullJoined - includes loaded collection
public record PostFullJoined(
    UUID id,
    String title,
    List<Comment> comments    // Loaded via join
) {}

// PartialJoined - lazy-loadable collection
public record PostPartialJoined(
    UUID id,
    String title,
    SqlForeignKey<UUID, List<Comment>> commentsKey
) {}
```

### One-to-One

Like many-to-one but with unique constraint on FK.

```yaml
fields:
  # Owning side (has the FK column)
  profileId:
    type: UUID
    column: profile_id
    unique: true                      # Enforces one-to-one
    nullable: true
    relationship:
      type: one-to-one
      target: UserProfile
      optional: true
      onDelete: cascade
```

**All One-to-One Options:**

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `type` | string | required | Must be `one-to-one` |
| `target` | string | required | Target entity name |
| `mappedBy` | string | - | Field in target (for inverse side only) |
| `optional` | boolean | `true` | Whether the relationship is optional |
| `onDelete` | enum | `no-action` | FK constraint action (owning side only) |
| `onUpdate` | enum | `no-action` | FK constraint action (owning side only) |

### Many-to-Many Relationships (Manual Handling)

**Many-to-many relationships are NOT supported directly.** Users should handle them manually using join tables:

```yaml
# post.yaml
entity: Post
table: posts

fields:
  id:
    type: UUID
    primary: true
    generated: uuid
  title:
    type: String

# tag.yaml
entity: Tag
table: tags

fields:
  id:
    type: UUID
    primary: true
    generated: uuid
  name:
    type: String

# post_tag.yaml - Manual join table entity
entity: PostTag
table: post_tags

fields:
  id:
    type: UUID
    primary: true
    generated: uuid

  postId:
    type: UUID
    column: post_id
    relationship:
      type: many-to-one
      target: Post
      onDelete: cascade

  tagId:
    type: UUID
    column: tag_id
    relationship:
      type: many-to-one
      target: Tag
      onDelete: cascade

  assignedAt:
    type: Instant
    column: assigned_at
    default: now()

indexes:
  - name: idx_post_tags_unique
    columns: [post_id, tag_id]
    unique: true
```

**Usage:**

```java
// Get all tags for a post using pre-generated join method
List<Tag> tags = PostTagTable.TABLE
    .joinTag()  // Generated from tagId -> Tag relationship
    .select()
    .where(PostTagTable.POST_ID.equalTo(postId))
    .map(row -> row.second())  // Extract Tag from Row2<PostTag, Tag>
    .fetch();

// Get all posts for a tag
List<Post> posts = PostTagTable.TABLE
    .joinPost()  // Generated from postId -> Post relationship
    .select()
    .where(PostTagTable.TAG_ID.equalTo(tagId))
    .map(row -> row.second())
    .fetch();

// Add tag to post
PostTagTable.TABLE.insert(PostTag.builder()
    .postId(postId)
    .tagId(tagId)
    .build());

// Remove tag from post
PostTagTable.TABLE.delete()
    .where(PostTagTable.POST_ID.equalTo(postId)
        .and(PostTagTable.TAG_ID.equalTo(tagId)))
    .execute();
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
  managerId:
    type: UUID
    column: manager_id
    nullable: true
    relationship:
      type: many-to-one
      target: Employee                # Self-reference
      optional: true
      onDelete: set-null

  # One-to-many inverse (manager has many direct reports)
  directReports:
    relationship:
      type: one-to-many
      target: Employee
      mappedBy: managerId
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
  authorId:
    type: UUID
    column: author_id
    nullable: false
    index: true
    relationship:
      type: many-to-one
      target: User
      optional: false
      onDelete: restrict

  # Many-to-One: Post optionally belongs to Category
  categoryId:
    type: UUID
    column: category_id
    nullable: true
    index: true
    relationship:
      type: many-to-one
      target: Category
      optional: true
      onDelete: set-null

  # One-to-Many: Post has many Comments
  comments:
    relationship:
      type: one-to-many
      target: Comment
      mappedBy: postId
      orderBy:
        - field: createdAt
          direction: desc

# Indexes
indexes:
  - name: idx_posts_author_status
    columns: [author_id, status]

  - name: idx_posts_published
    columns: [published_at]
    where: "status = 'PUBLISHED'"
```

**Generated Entities:**

```java
// Base entity
public record BlogPost(
    UUID id,
    String title,
    String slug,
    String content,
    PostStatus status,
    Instant publishedAt,
    int viewCount,
    UUID authorId,
    UUID categoryId,
	List<UUID> commentIds,
    Instant createdAt,
    Instant updatedAt,
    String createdBy
) {}

// FullJoined
public record BlogPostFullJoined(
    UUID id,
    String title,
    String slug,
    String content,
    PostStatus status,
    Instant publishedAt,
    int viewCount,
    User author,
    Category category,
    List<Comment> comments,
    Instant createdAt,
    Instant updatedAt,
    String createdBy
) {}

// PartialJoined
public record BlogPostPartialJoined(
    UUID id,
    String title,
    String slug,
    String content,
    PostStatus status,
    Instant publishedAt,
    int viewCount,
    SqlForeignKey<UUID, User> authorKey,
    SqlForeignKey<UUID, Category> categoryKey,
    SqlForeignKey<UUID, List<Comment>> commentsKey,
    Instant createdAt,
    Instant updatedAt,
    String createdBy
) {}
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
order.yaml:23:5 - Error: mappedBy 'orders' not found in OrderItem (available: orderId, productId)
post.yaml:8:7 - Warning: Foreign key 'author_id' is not indexed
```

## Related Documents

- [03-query-api.md](03-query-api.md) - Query builder API
- [04-code-generation.md](04-code-generation.md) - Code generation details
- [../reference/relationship-loading.md](../reference/relationship-loading.md) - Loading strategies
