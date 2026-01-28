# YAML Schema Specification

## Overview

The YAML schema defines database entities, their fields, relationships, and constraints. The schema files are processed at compile-time to generate type-safe Java code.

## File Organization

Schema files are organized by entity:

```
src/main/resources/schema/
├── _config.yaml          # Global configuration
├── user.yaml             # User entity
├── post.yaml             # Post entity
├── comment.yaml          # Comment entity
└── audit/
    └── audit_log.yaml    # Namespaced entity
```

## Global Configuration

The `_config.yaml` file defines project-wide settings:

```yaml
# _config.yaml
config:
  # Default schema for all entities
  defaultSchema: public

  # Naming conventions
  naming:
    table: snake_case      # user_profile
    column: snake_case     # created_at
    entity: PascalCase     # UserProfile
    field: camelCase       # createdAt

  # Default field options
  defaults:
    nullable: false
    updatable: true
    insertable: true

  # Audit columns added to all entities
  audit:
    enabled: true
    createdAt: created_at
    updatedAt: updated_at
    createdBy: created_by
    updatedBy: updated_by

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
    generated: true
    generator: uuid

  email:
    type: String
    column: email_address
    constraints:
      notBlank: true
      maxLength: 255
      pattern: "^[\\w.-]+@[\\w.-]+\\.\\w+$"
    unique: true
    index: true

  passwordHash:
    type: String
    column: password_hash
    constraints:
      notNull: true
      minLength: 60
    select: false  # Excluded from default SELECT

  status:
    type: UserStatus
    enum: true
    default: PENDING

  createdAt:
    type: Instant
    column: created_at
    insertable: true
    updatable: false
    default: now()

  lastLoginAt:
    type: Instant
    column: last_login_at
    nullable: true

# Composite indexes
indexes:
  - name: idx_user_status_created
    columns: [status, created_at]
    unique: false

  - name: idx_user_email_lower
    expression: "lower(email_address)"
    unique: true

# Table constraints
constraints:
  - type: check
    name: chk_email_format
    expression: "email_address ~ '^[\\w.-]+@[\\w.-]+\\.\\w+$'"
```

### Entity with Relationships

```yaml
# post.yaml
entity: Post
table: posts

fields:
  id:
    type: Long
    primary: true
    generated: true
    generator: identity

  title:
    type: String
    constraints:
      notBlank: true
      maxLength: 200

  content:
    type: String
    constraints:
      notNull: true
    columnType: TEXT

  authorId:
    type: UUID
    column: author_id

relationships:
  author:
    type: many-to-one
    target: User
    foreignKey: author_id
    fetch: eager
    optional: false
    cascade: [persist, merge]

  comments:
    type: one-to-many
    target: Comment
    mappedBy: post
    fetch: lazy
    cascade: [all]
    orphanRemoval: true

  tags:
    type: many-to-many
    target: Tag
    joinTable:
      name: post_tags
      joinColumn: post_id
      inverseJoinColumn: tag_id
    fetch: lazy
    cascade: [persist, merge]
```

## Field Types

### Primitive Types

| YAML Type | Java Type | SQL Type (PostgreSQL) |
|-----------|-----------|----------------------|
| `Boolean` | `boolean` / `Boolean` | `BOOLEAN` |
| `Byte` | `byte` / `Byte` | `SMALLINT` |
| `Short` | `short` / `Short` | `SMALLINT` |
| `Integer` | `int` / `Integer` | `INTEGER` |
| `Long` | `long` / `Long` | `BIGINT` |
| `Float` | `float` / `Float` | `REAL` |
| `Double` | `double` / `Double` | `DOUBLE PRECISION` |
| `BigInteger` | `BigInteger` | `NUMERIC` |
| `BigDecimal` | `BigDecimal` | `NUMERIC(p,s)` |

### String Types

| YAML Type | Java Type | SQL Type (PostgreSQL) |
|-----------|-----------|----------------------|
| `String` | `String` | `VARCHAR(n)` |
| `Text` | `String` | `TEXT` |
| `Char` | `char` / `Character` | `CHAR(1)` |

### Temporal Types

| YAML Type | Java Type | SQL Type (PostgreSQL) |
|-----------|-----------|----------------------|
| `Instant` | `Instant` | `TIMESTAMP WITH TIME ZONE` |
| `LocalDate` | `LocalDate` | `DATE` |
| `LocalTime` | `LocalTime` | `TIME` |
| `LocalDateTime` | `LocalDateTime` | `TIMESTAMP` |
| `OffsetDateTime` | `OffsetDateTime` | `TIMESTAMP WITH TIME ZONE` |
| `ZonedDateTime` | `ZonedDateTime` | `TIMESTAMP WITH TIME ZONE` |
| `Duration` | `Duration` | `INTERVAL` |
| `Period` | `Period` | `INTERVAL` |

### Other Types

| YAML Type | Java Type | SQL Type (PostgreSQL) |
|-----------|-----------|----------------------|
| `UUID` | `UUID` | `UUID` |
| `byte[]` | `byte[]` | `BYTEA` |
| `Json` | `JsonElement` | `JSONB` |
| `Enum` | Generated enum | `VARCHAR` or `ENUM` |

### Collection Types

```yaml
fields:
  tags:
    type: List<String>
    columnType: "TEXT[]"

  metadata:
    type: Map<String, String>
    columnType: JSONB

  scores:
    type: int[]
    columnType: "INTEGER[]"
```

### Custom Types

```yaml
fields:
  address:
    type: Address
    converter: AddressConverter
    columnType: JSONB

  money:
    type: Money
    embeddable: true
    # Expands to: amount DECIMAL, currency VARCHAR
```

## Field Options

### Common Options

```yaml
fields:
  example:
    type: String

    # Column mapping
    column: example_column     # SQL column name
    columnType: VARCHAR(100)   # Override SQL type

    # Nullability
    nullable: false            # NOT NULL constraint

    # Mutability
    insertable: true           # Include in INSERT
    updatable: true            # Include in UPDATE

    # Query options
    select: true               # Include in default SELECT

    # Indexing
    index: true                # Create index
    unique: true               # Unique constraint
```

### Primary Key Options

```yaml
fields:
  id:
    type: Long
    primary: true
    generated: true
    generator: identity        # identity, sequence, uuid, custom

    # For sequence generator
    sequence:
      name: user_id_seq
      initialValue: 1
      allocationSize: 50
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

Constraints reuse the existing LUtils constraint system:

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
      # Advanced options
      startsWith: "user_"
      endsWith: "@example.com"
      contains: "@"
```

### Numeric Constraints

```yaml
fields:
  age:
    type: Integer
    constraints:
      notNull: true
      min: 0
      max: 150
      # Or using range
      range: [0, 150]

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
      # Element constraints
      elements:
        notBlank: true
        maxLength: 50
```

### Custom Constraints

```yaml
fields:
  username:
    type: String
    constraints:
      custom:
        validator: UsernameValidator
        message: "Username must be alphanumeric"
```

## Relationships

### Many-to-One

```yaml
relationships:
  author:
    type: many-to-one
    target: User
    foreignKey: author_id

    # Fetch strategy
    fetch: eager               # eager or lazy

    # Optional relationship
    optional: false            # Generates NOT NULL FK

    # Cascade operations
    cascade: [persist, merge]

    # On delete behavior
    onDelete: cascade          # cascade, set-null, restrict, no-action
```

### One-to-Many

```yaml
relationships:
  comments:
    type: one-to-many
    target: Comment
    mappedBy: post             # Field in Comment that owns relationship

    fetch: lazy
    cascade: [all]
    orphanRemoval: true        # Delete orphaned children

    # Ordering
    orderBy:
      - field: createdAt
        direction: desc
```

### Many-to-Many

```yaml
relationships:
  tags:
    type: many-to-many
    target: Tag

    # Join table configuration
    joinTable:
      name: post_tags
      schema: public
      joinColumn: post_id
      inverseJoinColumn: tag_id

      # Additional columns on join table
      columns:
        - name: assigned_at
          type: Instant
          default: now()

    fetch: lazy
    cascade: [persist, merge]
```

### One-to-One

```yaml
relationships:
  profile:
    type: one-to-one
    target: UserProfile
    foreignKey: profile_id

    # Or mapped by the other side
    # mappedBy: user

    fetch: eager
    cascade: [all]
    optional: true
```

## Inheritance

### Single Table Inheritance

```yaml
# base_content.yaml
entity: Content
table: content
abstract: true

inheritance:
  strategy: single-table
  discriminator:
    column: content_type
    type: String

fields:
  id:
    type: Long
    primary: true
    generated: true
  title:
    type: String
---
# article.yaml
entity: Article
extends: Content
discriminatorValue: ARTICLE

fields:
  body:
    type: String
    columnType: TEXT
---
# video.yaml
entity: Video
extends: Content
discriminatorValue: VIDEO

fields:
  url:
    type: String
  duration:
    type: Duration
```

### Joined Table Inheritance

```yaml
# base_payment.yaml
entity: Payment
table: payments
abstract: true

inheritance:
  strategy: joined
  discriminator:
    column: payment_type
    type: String

fields:
  id:
    type: Long
    primary: true
  amount:
    type: BigDecimal
---
# credit_card_payment.yaml
entity: CreditCardPayment
table: credit_card_payments
extends: Payment
discriminatorValue: CREDIT_CARD

fields:
  cardNumber:
    type: String
  expiryDate:
    type: String
```

## Embeddables

Embeddables are value objects that map to multiple columns:

```yaml
# _embeddables/address.yaml
embeddable: Address

fields:
  street:
    type: String
    constraints:
      maxLength: 200
  city:
    type: String
    constraints:
      maxLength: 100
  postalCode:
    type: String
    column: postal_code
  country:
    type: String
    constraints:
      maxLength: 2
```

Usage in entity:

```yaml
# customer.yaml
entity: Customer
table: customers

fields:
  id:
    type: Long
    primary: true
    generated: true
  name:
    type: String

embedded:
  billingAddress:
    type: Address
    prefix: billing_      # billing_street, billing_city, etc.

  shippingAddress:
    type: Address
    prefix: shipping_
    nullable: true        # All columns nullable
```

## Enums

### Inline Enum Definition

```yaml
fields:
  status:
    type: UserStatus
    enum:
      values:
        - PENDING
        - ACTIVE
        - SUSPENDED
        - DELETED
      default: PENDING
      storage: string     # string or ordinal
```

### External Enum Definition

```yaml
# _enums/order_status.yaml
enum: OrderStatus

values:
  - name: PENDING
    description: Order is awaiting processing
  - name: PROCESSING
    description: Order is being prepared
  - name: SHIPPED
    description: Order has been shipped
  - name: DELIVERED
    description: Order has been delivered
  - name: CANCELLED
    description: Order was cancelled

storage: string
```

## TimescaleDB Extensions

For TimescaleDB hypertables:

```yaml
# sensor_reading.yaml
entity: SensorReading
table: sensor_readings

timescale:
  hypertable: true
  timeColumn: recorded_at
  chunkInterval: "7 days"

  # Compression policy
  compression:
    enabled: true
    after: "30 days"
    segmentBy: [sensor_id]
    orderBy: [recorded_at]

  # Retention policy
  retention:
    enabled: true
    after: "365 days"

  # Continuous aggregates
  aggregates:
    - name: hourly_readings
      interval: "1 hour"
      columns:
        - name: avg_value
          expression: "AVG(value)"
        - name: max_value
          expression: "MAX(value)"
        - name: reading_count
          expression: "COUNT(*)"

fields:
  id:
    type: Long
    primary: true
    generated: true

  sensorId:
    type: UUID
    column: sensor_id
    index: true

  recordedAt:
    type: Instant
    column: recorded_at

  value:
    type: Double
```

## Schema Validation

The generator validates schemas for:

- **Type Consistency**: Field types match Java types
- **Relationship Validity**: Foreign keys reference valid entities
- **Constraint Compatibility**: Constraints match field types
- **Naming Conflicts**: No duplicate field/column names
- **Circular Dependencies**: Detect and report circular relationships
- **Index Coverage**: Warning for unindexed foreign keys

Validation errors include file location and suggested fixes:

```
Schema validation failed:

user.yaml:15:5 - Error: Unknown field type 'Strings' (did you mean 'String'?)
post.yaml:23:3 - Error: Relationship 'author' references unknown entity 'Users'
comment.yaml:8:7 - Warning: Foreign key 'post_id' is not indexed
```

## Related Documents

- [00-overview.md](00-overview.md) - Architecture overview
- [02-code-generation.md](02-code-generation.md) - Code generation details
- [04-dialect-system.md](04-dialect-system.md) - SQL type mappings per dialect
