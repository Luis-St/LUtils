# Schema & Code Generation

## File Structure

```
src/main/resources/schema/
├── _config.yaml      # Global configuration
├── user.yaml         # One file per entity
├── order.yaml
└── audit/
    └── audit_log.yaml
```

## Entity Definition

```yaml
entity: User
table: users
schema: public

audit:                          # Per-table audit (optional)
  createdAt: { column: created_at, type: Instant }
  updatedAt: { column: updated_at, type: Instant }
  createdBy: { column: created_by, type: String }

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
    constraints: { notBlank: true, maxLength: 255 }

  status:
    type: UserStatus
    enum: [PENDING, ACTIVE, SUSPENDED]
    default: PENDING

  addressId:                    # Many-to-One relationship
    type: UUID
    column: address_id
    nullable: true
    relationship:
      type: many-to-one
      target: Address
      optional: true
      onDelete: set-null

  orders:                       # One-to-Many (inverse side)
    relationship:
      type: one-to-many
      target: Order
      mappedBy: customerId
      orderBy: [{ field: createdAt, direction: desc }]

indexes:
  - name: idx_users_status
    columns: [status]
    where: "status = 'ACTIVE'"  # Partial index
```

## Relationship Types

| Type | Description | Generated |
|------|-------------|-----------|
| `many-to-one` | FK column on this entity | Join method, FK column |
| `one-to-many` | Inverse of many-to-one | Collection in FullJoined |
| `one-to-one` | Like many-to-one + unique | Same as many-to-one |
| Many-to-Many | Not supported | Use manual join table |

**FK Options:** `onDelete` / `onUpdate`: `cascade`, `set-null`, `restrict`, `no-action`

## Field Types

| YAML | Java | PostgreSQL | MySQL |
|------|------|------------|-------|
| `Boolean` | `boolean` | `BOOLEAN` | `TINYINT(1)` |
| `Integer` | `int` | `INTEGER` | `INT` |
| `Long` | `long` | `BIGINT` | `BIGINT` |
| `BigDecimal` | `BigDecimal` | `NUMERIC(p,s)` | `DECIMAL(p,s)` |
| `String` | `String` | `VARCHAR(n)` | `VARCHAR(n)` |
| `UUID` | `UUID` | `UUID` | `CHAR(36)` |
| `Instant` | `Instant` | `TIMESTAMPTZ` | `DATETIME(6)` |
| `LocalDate` | `LocalDate` | `DATE` | `DATE` |
| `Json` | `JsonElement` | `JSONB` | `JSON` |
| `List<T>` | `List<T>` | `T[]` | `JSON` |

## Field Options

```yaml
fieldName:
  type: String
  column: column_name       # SQL column name
  columnType: TEXT          # Override SQL type
  nullable: false           # NOT NULL
  unique: true              # Unique constraint
  index: true               # Create index
  insertable: true          # Include in INSERT
  updatable: true           # Include in UPDATE
  default: "value"          # Default value or now()
  constraints:
    notNull: true
    notBlank: true
    minLength: 1
    maxLength: 255
    min: 0
    max: 100
    positive: true
    pattern: "^[a-z]+$"
```

## Generated Entity Variants

For entities with relationships, three variants are generated:

```java
// Base - raw FK values only
public record User(UUID id, String name, UUID addressId) {}

// FullJoined - loaded relationships
public record UserFullJoined(UUID id, String name, Address address, List<Order> orders) {}

// PartialJoined - SqlForeignKey wrappers
public record UserPartialJoined(
    UUID id, String name,
    SqlForeignKey<UUID, Address> addressKey,
    SqlForeignKey<UUID, List<Order>> ordersKey
) {}
```

## SqlForeignKey

```java
SqlForeignKey<UUID, User> customerKey = ...;

customerKey.key();           // UUID - always available
customerKey.isLoaded();      // boolean - true if value loaded
customerKey.value();         // User - loaded entity (null if not loaded)
customerKey.requireValue();  // User - throws if not loaded
customerKey.valueOptional(); // Optional<User>
```

## Generated Table Class

```java
public final class UserTable {
    public static final SqlTable<User> TABLE = SqlTable.of("users", User.class);

    public static final SqlColumn<User, UUID> ID = TABLE.column("id", UUID.class);
    public static final SqlColumn<User, String> EMAIL = TABLE.column("email", String.class);
    public static final SqlForeignColumn<User, Address, UUID> ADDRESS_ID = ...;

    // Generated join methods
    public static SqlJoinBuilder<User, Address> joinAddress() { ... }
    public static SqlJoinBuilder<User, Address> leftJoinAddress() { ... }
    public static SqlJoinBuilder<User, Order> joinOrders() { ... }
}
```

## Gradle Plugin

```kotlin
plugins { id("net.luis.db") version "1.0.0" }

lutilsDb {
    schemaDir = file("src/main/resources/schema")
    outputDir = file("build/generated/sources/lutils-db/java")
    basePackage = "com.example.db"
    dialect = "postgresql"

    generation {
        entityStyle = EntityStyle.RECORD
        generateBuilders = true
        generateFullJoined = true
        generatePartialJoined = true
    }
}
```

**Tasks:** `generateDbSources`, `validateSchema`, `cleanDbSources`
