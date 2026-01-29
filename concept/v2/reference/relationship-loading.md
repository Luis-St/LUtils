# Relationship Loading

## Overview

Relationships are loaded explicitly through joins or separate queries. There is no lazy loading or proxy magic - you control exactly when and how related data is fetched.

The key concept is `SqlKey<K, V>`: a record that separates the foreign key value (always available) from the loaded entity (populated on demand).

## SqlKey Pattern

### Basic Structure

```java
/**
 * Represents a foreign key reference with optional loaded value.<br>
 *
 * @param <K> The key type (e.g., UUID, Long)
 * @param <V> The value type (the related entity or List for one-to-many)
 */
public record SqlKey<K, V>(
    K key,           // The foreign key value (always available)
    V value          // The loaded entity (null if not loaded)
) {

    /**
     * Creates a key without a loaded value.<br>
     */
    public static <K, V> SqlKey<K, V> of(K key) {
        return new SqlKey<>(key, null);
    }

    /**
     * Creates a key with a loaded value.<br>
     */
    public static <K, V> SqlKey<K, V> of(K key, V value) {
        return new SqlKey<>(key, value);
    }

    /**
     * Returns true if the value has been loaded.<br>
     */
    public boolean isLoaded() {
        return value != null;
    }

    /**
     * Returns the value, throwing if not loaded.<br>
     */
    public V requireValue() {
        if (value == null) {
            throw new RelationshipNotLoadedException(
                "Relationship not loaded. Use withLoaded() in query or join explicitly.");
        }
        return value;
    }

    /**
     * Returns the value as Optional.<br>
     */
    public Optional<V> valueOptional() {
        return Optional.ofNullable(value);
    }
}
```

### Usage in Entities

```java
// Many-to-one: Order has one User (customer)
public record Order(
    UUID id,
    String orderNumber,
    BigDecimal total,
    SqlKey<UUID, User> customer    // FK is UUID, loaded value is User
) {}

// Access the key (always works)
UUID customerId = order.customer().key();

// Check if loaded
if (order.customer().isLoaded()) {
    User customer = order.customer().value();
}

// Access loaded value (throws if not loaded)
User customer = order.customer().requireValue();
```

### One-to-Many Relationships

```java
// One-to-many: User has many Orders
public record User(
    UUID id,
    String name,
    SqlKey<UUID, List<Order>> orders   // FK is user's ID, loaded value is List<Order>
) {}

// The key is the user's own ID (for one-to-many)
// The value is the list of related orders
List<Order> userOrders = user.orders().value();  // Empty list if not loaded, not null
```

## YAML Configuration

Configure default loading strategy in YAML:

```yaml
# order.yaml
entity: Order
table: orders

fields:
  id:
    type: UUID
    primary: true
  orderNumber:
    type: String
    column: order_number
  total:
    type: BigDecimal

relationships:
  customer:
    type: many-to-one
    target: User
    foreignKey: customer_id
    loading: LAZY              # Default loading strategy
    optional: false

  items:
    type: one-to-many
    target: OrderItem
    mappedBy: order
    loading: LAZY
```

### Loading Strategies

| Strategy | Behavior |
|----------|----------|
| `LAZY` (default) | Only the key is populated; value is null unless explicitly loaded |
| `EAGER` | Always load via join when fetching the parent entity |

**Note:** `EAGER` should be used sparingly as it can cause performance issues with large result sets.

## Loading at Query Time

### Override Loading Strategy

```java
// Default query: customer not loaded
List<Order> orders = OrderTable.TABLE.select().fetch();
orders.get(0).customer().value();  // null

// With explicit loading
List<Order> orders = OrderTable.TABLE.select()
    .withLoaded(OrderTable.CUSTOMER)   // Override: load customer
    .fetch();
orders.get(0).customer().value();  // User object

// Disable eager loading
List<Order> orders = OrderTable.TABLE.select()
    .withoutLoading(OrderTable.CUSTOMER)  // Override: don't load even if EAGER
    .fetch();
```

### Annotation on Records

Generated records include loading strategy annotation:

```java
@Entity(table = "orders")
public record Order(
    @Id UUID id,
    String orderNumber,
    BigDecimal total,
    @Relationship(loading = LoadingStrategy.LAZY)
    SqlKey<UUID, User> customer
) {}
```

## Loading Strategies

### Strategy 1: Join in Single Query

Best for: Loading a few related entities, avoiding N+1.

```java
// Inner join - only orders with customers
List<Row2<Order, User>> results = OrderTable.TABLE
    .innerJoin(UserTable.TABLE)
    .on(OrderTable.CUSTOMER)
    .select()
    .fetch();

// Left join - all orders, customer may be null
List<Row2<Order, Optional<User>>> results = OrderTable.TABLE
    .leftJoin(UserTable.TABLE)
    .on(OrderTable.CUSTOMER)
    .select()
    .fetch();

// Access the data
for (Row2<Order, User> row : results) {
    Order order = row.first();
    User customer = row.second();
}
```

### Strategy 2: Populate Entity Relationship

Load joined data directly into the `SqlKey`:

```java
// Order.customer() will have the User populated
List<Order> orders = OrderTable.TABLE
    .leftJoin(UserTable.TABLE).on(OrderTable.CUSTOMER)
    .selectEntity(OrderTable.TABLE)
    .withLoaded(OrderTable.CUSTOMER)
    .fetch();

for (Order order : orders) {
    User customer = order.customer().value();  // Populated from join, or null
}
```

### Strategy 3: Separate Queries (Batch Loading)

Best for: Loading one-to-many relationships, avoiding cartesian products.

```java
// Load orders
List<Order> orders = OrderTable.TABLE.select()
    .where(OrderTable.STATUS.equalTo(OrderStatus.PENDING))
    .fetch();

// Batch load their items (single query for all orders)
List<UUID> orderIds = orders.stream().map(Order::id).toList();
List<OrderItem> allItems = OrderItemTable.TABLE.select()
    .where(OrderItemTable.ORDER_ID.in(orderIds))
    .fetch();

// Group by order
Map<UUID, List<OrderItem>> itemsByOrder = allItems.stream()
    .collect(Collectors.groupingBy(OrderItem::orderId));

// Associate (or use stream processing)
for (Order order : orders) {
    List<OrderItem> items = itemsByOrder.getOrDefault(order.id(), List.of());
    // Process order with its items
}
```

### Strategy 4: DataLoader Pattern

For complex graphs, use a DataLoader to batch and deduplicate:

```java
// Define loaders
DataLoader<UUID, User> customerLoader = DataLoader.create(
    ids -> UserTable.TABLE.select()
        .where(UserTable.ID.in(ids))
        .fetch()
        .stream()
        .collect(Collectors.toMap(User::id, u -> u))
);

DataLoader<UUID, List<Order>> ordersLoader = DataLoader.createGrouped(
    userIds -> OrderTable.TABLE.select()
        .where(OrderTable.CUSTOMER_ID.in(userIds))
        .fetch()
        .stream()
        .collect(Collectors.groupingBy(o -> o.customer().key()))
);

// Use in a loading context
try (LoadingContext ctx = LoadingContext.begin()) {
    for (Order order : orders) {
        // Queue loads (not executed yet)
        CompletableFuture<User> customerFuture = customerLoader.load(order.customer().key());
    }
    // Execute all queued loads in batch
    ctx.dispatch();
}
```

## Multi-Level Joins

```java
// User -> Order -> OrderItem (3-level join)
List<Row3<User, Order, OrderItem>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.CUSTOMER_ID))
    .innerJoin(OrderItemTable.TABLE).on(OrderTable.ID.equalTo(OrderItemTable.ORDER_ID))
    .select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// With column selection
List<Row3<String, String, BigDecimal>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.CUSTOMER_ID))
    .innerJoin(OrderItemTable.TABLE).on(OrderTable.ID.equalTo(OrderItemTable.ORDER_ID))
    .select(UserTable.NAME, OrderTable.ORDER_NUMBER, OrderItemTable.PRICE)
    .fetch();
```

## Many-to-Many Relationships

Many-to-many uses a junction table:

```yaml
# post.yaml
relationships:
  tags:
    type: many-to-many
    target: Tag
    joinTable:
      name: post_tags
      joinColumn: post_id
      inverseJoinColumn: tag_id
    loading: LAZY
```

```java
// Load posts with their tags
List<Post> posts = PostTable.TABLE.select()
    .withLoaded(PostTable.TAGS)
    .fetch();

for (Post post : posts) {
    List<Tag> tags = post.tags().value();  // Loaded via junction table
}

// Or explicit join through junction table
List<Row2<Post, Tag>> results = PostTable.TABLE
    .innerJoin(PostTagTable.TABLE).on(PostTable.ID.equalTo(PostTagTable.POST_ID))
    .innerJoin(TagTable.TABLE).on(PostTagTable.TAG_ID.equalTo(TagTable.ID))
    .select()
    .fetch();
```

## Avoiding N+1 Queries

The N+1 problem occurs when you load N entities, then make N separate queries to load their relationships:

```java
// BAD: N+1 queries
List<Order> orders = OrderTable.TABLE.select().fetch();  // 1 query
for (Order order : orders) {
    // This would be N additional queries if using lazy loading
    User customer = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(order.customer().key()))
        .fetchOne();
}

// GOOD: 1 query with join
List<Order> orders = OrderTable.TABLE.select()
    .withLoaded(OrderTable.CUSTOMER)
    .fetch();

// GOOD: 2 queries with batch loading
List<Order> orders = OrderTable.TABLE.select().fetch();
Set<UUID> customerIds = orders.stream()
    .map(o -> o.customer().key())
    .filter(Objects::nonNull)
    .collect(Collectors.toSet());
Map<UUID, User> customers = UserTable.TABLE.select()
    .where(UserTable.ID.in(customerIds))
    .fetch()
    .stream()
    .collect(Collectors.toMap(User::id, u -> u));
```

## Circular References

For entities that reference each other:

```java
// Employee -> Manager (self-referencing)
public record Employee(
    UUID id,
    String name,
    SqlKey<UUID, Employee> manager  // Self-reference
) {}

// Load with hierarchy
List<Row2<Employee, Optional<Employee>>> withManagers = EmployeeTable.TABLE
    .leftJoin(EmployeeTable.TABLE.as("manager"))
    .on(EmployeeTable.MANAGER)
    .select()
    .fetch();

// Recursive CTE for full hierarchy
List<Employee> hierarchy = Cte.withRecursive("emp_tree",
    // Base case: top-level employees
    EmployeeTable.TABLE.select().where(EmployeeTable.MANAGER.isNull()),
    // Recursive case: employees under those
    (cte) -> EmployeeTable.TABLE
        .innerJoin(cte).on(EmployeeTable.MANAGER.equalTo(cte.column(EmployeeTable.ID)))
        .select()
)
.select()
.fetch();
```

## Loading Builder Interface

```java
public interface LoadingBuilder<E> {

    /**
     * Specifies that the given relationship should be loaded.<br>
     * Overrides the default loading strategy from YAML.
     */
    <R> LoadingBuilder<E> withLoaded(SqlRelationship<E, R> relationship);

    /**
     * Specifies that the given relationship should NOT be loaded.<br>
     * Overrides EAGER loading from YAML.
     */
    <R> LoadingBuilder<E> withoutLoading(SqlRelationship<E, R> relationship);

    /**
     * Specifies columns to select from the relationship.<br>
     */
    <R> LoadingBuilder<E> withLoaded(SqlRelationship<E, R> relationship,
                                      SqlColumn<R, ?>... columns);

    /**
     * Fetches the results with relationships populated.<br>
     */
    List<E> fetch();

    CompletableFuture<List<E>> fetchAsync();
}
```

## Generated Relationship Helpers

Common loading patterns are generated as helper methods:

```java
public final class OrderTable {

    /**
     * Loads orders with their customers in a single query.<br>
     */
    public static List<Row2<Order, Optional<User>>> selectWithCustomer() {
        return TABLE
            .leftJoin(UserTable.TABLE).on(CUSTOMER)
            .select()
            .fetch();
    }

    /**
     * Loads an order by ID with its customer.<br>
     */
    public static Optional<Row2<Order, Optional<User>>> findByIdWithCustomer(UUID id) {
        return TABLE
            .leftJoin(UserTable.TABLE).on(CUSTOMER)
            .select()
            .where(ID.equalTo(id))
            .fetchFirst();
    }
}

// Usage
var result = OrderTable.findByIdWithCustomer(orderId);
```

## Best Practices

1. **Default to LAZY**: Only use EAGER when you always need the relationship
2. **Use withLoaded() at query time**: Override loading when needed
3. **Batch load for collections**: Use separate queries for one-to-many to avoid cartesian products
4. **Check isLoaded()**: Before accessing value() to avoid NullPointerException
5. **Use requireValue()**: When you expect the relationship to be loaded (fails fast)

## Related Documents

- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Relationship YAML syntax
- [../core/03-query-api.md](../core/03-query-api.md) - Join syntax
- [entity-mapping.md](entity-mapping.md) - How relationships are mapped
