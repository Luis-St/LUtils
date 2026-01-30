# Relationship Loading

## Overview

In v3, relationships are handled through three entity variants:
- **Base entities** - Contain raw FK values only (no loaded entities)
- **FullJoined entities** - FK replaced with loaded entity instances
- **PartialJoined entities** - FK wrapped in `SqlForeignKey<K, V>` for optional lazy loading

There is no lazy loading or proxy magic - you choose which entity variant to use based on your needs.

## Entity Variants

### Base Entity

Generated for every entity. Contains raw FK values only:

```java
// Base entity - raw FK values, no relationships loaded
public record User(
    UUID id,
    String name,
    String email,
    UUID addressId          // Raw FK value
) {}

public record Order(
    UUID id,
    String orderNumber,
    BigDecimal total,
    UUID customerId,        // Raw FK value
    UUID agentId            // Raw FK value (nullable)
) {}
```

### FullJoined Entity

Generated for entities with relationships. FK replaced with loaded entity:

```java
// FullJoined - relationships loaded via JOIN
public record UserFullJoined(
    UUID id,
    String name,
    String email,
    Address address,        // Loaded entity (from many-to-one)
    List<Order> orders      // Loaded list (from one-to-many)
) {}

public record OrderFullJoined(
    UUID id,
    String orderNumber,
    BigDecimal total,
    User customer,          // Loaded entity
    User agent              // Loaded entity (nullable)
) {}
```

### PartialJoined Entity

Generated for entities with relationships. FK wrapped in `SqlForeignKey`:

```java
// PartialJoined - SqlForeignKey wrapper for lazy access
public record UserPartialJoined(
    UUID id,
    String name,
    String email,
    SqlForeignKey<UUID, Address> addressKey,
    SqlForeignKey<UUID, List<Order>> ordersKey
) {}

public record OrderPartialJoined(
    UUID id,
    String orderNumber,
    BigDecimal total,
    SqlForeignKey<UUID, User> customerKey,
    SqlForeignKey<UUID, User> agentKey
) {}
```

## SqlForeignKey Class

The `SqlForeignKey<K, V>` class encapsulates a foreign key value and optionally the loaded entity:

```java
/**
 * Represents a foreign key reference with optional loaded value.<br>
 *
 * @param <K> The key type (e.g., UUID, Long)
 * @param <V> The value type (the related entity or List for one-to-many)
 */
public record SqlForeignKey<K, V>(
    K key,           // The foreign key value (always available)
    V value          // The loaded entity (null if not loaded)
) {

    /**
     * Creates a key without a loaded value.<br>
     */
    public static <K, V> SqlForeignKey<K, V> of(K key) {
        return new SqlForeignKey<>(key, null);
    }

    /**
     * Creates a key with a loaded value.<br>
     */
    public static <K, V> SqlForeignKey<K, V> of(K key, V value) {
        return new SqlForeignKey<>(key, value);
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
            throw new SqlRelationshipNotLoadedException("Relationship not loaded. Use FullJoined entity or explicit join.");
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

## Query Patterns

### Using Base Entities (Raw FK)

Best for: Simple queries, when you don't need related data:

```java
// Query base entities - no relationship loading
List<Order> orders = OrderTable.TABLE.select()
    .where(OrderTable.STATUS.equalTo(OrderStatus.PENDING))
    .fetch();

// Access raw FK values
for (Order order : orders) {
    UUID customerId = order.customerId();  // Just the FK value
}
```

### Using FullJoined Entities

Best for: When you always need the related data loaded:

```java
// Query with JOIN, map to FullJoined using selectAs()
List<OrderFullJoined> orders = OrderTable.TABLE
    .joinCustomer()  // Generated from customerId relationship
    .selectAs(OrderFullJoined.class)
    .where(OrderTable.STATUS.equalTo(OrderStatus.PENDING))
    .fetch();

// Access loaded relationships
for (OrderFullJoined order : orders) {
    User customer = order.customer();  // Already loaded from JOIN
    System.out.println(customer.name());
}
```

### Using PartialJoined Entities

Best for: When you need FK always available, with optional loaded entity:

```java
// Query with LEFT JOIN, map to PartialJoined using selectAs()
List<OrderPartialJoined> orders = OrderTable.TABLE
    .leftJoinCustomer()  // Generated from customerId relationship
    .selectAs(OrderPartialJoined.class)
    .where(OrderTable.STATUS.equalTo(OrderStatus.PENDING))
    .fetch();

// Access via SqlForeignKey
for (OrderPartialJoined order : orders) {
    // Key is always available
    UUID customerId = order.customerKey().key();

    // Value may or may not be loaded
    if (order.customerKey().isLoaded()) {
        User customer = order.customerKey().value();
        System.out.println(customer.name());
    }
}
```

## Loading Strategies

### Strategy 1: Row-Based Join Results

Returns separate objects in Row types:

```java
// Inner join - select() returns Row2 by default
List<Row2<Order, User>> results = OrderTable.TABLE
    .joinCustomer()
    .select()
    .fetch();

// Access the data
for (Row2<Order, User> row : results) {
    Order order = row.first();
    User customer = row.second();
}

// Left join - second element is Optional
List<Row2<Order, Optional<User>>> results = OrderTable.TABLE
    .leftJoinCustomer()
    .select()
    .fetch();

for (Row2<Order, Optional<User>> row : results) {
    Order order = row.first();
    Optional<User> customer = row.second();
}
```

### Strategy 2: Batch Loading (Separate Queries)

Best for: One-to-many relationships, avoiding cartesian products:

```java
// Load orders
List<Order> orders = OrderTable.TABLE.select()
    .where(OrderTable.STATUS.equalTo(OrderStatus.PENDING))
    .fetch();

// Batch load their customers (single query for all orders)
Set<UUID> customerIds = orders.stream()
    .map(Order::customerId)
    .filter(Objects::nonNull)
    .collect(Collectors.toSet());

Map<UUID, User> customers = UserTable.TABLE.select()
    .where(UserTable.ID.in(customerIds))
    .fetch()
    .stream()
    .collect(Collectors.toMap(User::id, u -> u));

// Associate
for (Order order : orders) {
    User customer = customers.get(order.customerId());
    // Process order with its customer
}
```

### Strategy 3: One-to-Many Loading

For loading collections, use separate queries to avoid cartesian products:

```java
// Load users
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Batch load their orders
List<UUID> userIds = users.stream().map(User::id).toList();
List<Order> allOrders = OrderTable.TABLE.select()
    .where(OrderTable.CUSTOMER_ID.in(userIds))
    .orderBy(OrderTable.CREATED_AT.desc())
    .fetch();

// Group by user
Map<UUID, List<Order>> ordersByUser = allOrders.stream()
    .collect(Collectors.groupingBy(Order::customerId));

// Create FullJoined entities manually
List<UserFullJoined> usersWithOrders = users.stream()
    .map(user -> new UserFullJoined(
        user.id(),
        user.name(),
        user.email(),
        null,  // Address not loaded in this example
        ordersByUser.getOrDefault(user.id(), List.of())
    ))
    .toList();
```

## Multi-Level Joins

```java
// User -> Order -> OrderItem (3-level join using chained join methods)
List<Row3<User, Order, OrderItem>> results = UserTable.TABLE
    .joinOrders()        // User -> Order via orders relationship
    .joinOrderItems()    // Order -> OrderItem via orderItems relationship
    .select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// With column selection
List<Row3<String, String, BigDecimal>> results = UserTable.TABLE
    .joinOrders()
    .joinOrderItems()
    .select(UserTable.NAME, OrderTable.ORDER_NUMBER, OrderItemTable.PRICE)
    .fetch();
```

## Avoiding N+1 Queries

The N+1 problem occurs when you load N entities, then make N separate queries to load their relationships:

```java
// BAD: N+1 queries
List<Order> orders = OrderTable.TABLE.select().fetch();  // 1 query
for (Order order : orders) {
    // This is N additional queries!
    User customer = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(order.customerId()))
        .fetchOne();
}

// GOOD: 1 query with JOIN using pre-generated join method
List<OrderFullJoined> orders = OrderTable.TABLE
    .joinCustomer()
    .selectAs(OrderFullJoined.class)
    .fetch();

// GOOD: 2 queries with batch loading
List<Order> orders = OrderTable.TABLE.select().fetch();
Set<UUID> customerIds = orders.stream()
    .map(Order::customerId)
    .filter(Objects::nonNull)
    .collect(Collectors.toSet());
Map<UUID, User> customers = UserTable.TABLE.select()
    .where(UserTable.ID.in(customerIds))
    .fetch()
    .stream()
    .collect(Collectors.toMap(User::id, u -> u));
```

## Converting Between Variants

```java
// PartialJoined -> Base
UserPartialJoined partialUser = ...;
User baseUser = partialUser.toBase();  // Extracts FK values from SqlForeignKey

// PartialJoined -> FullJoined (requires all relationships loaded)
try {
    UserFullJoined fullUser = partialUser.toFullJoined();
} catch (SqlRelationshipNotLoadedException e) {
    // Some relationship wasn't loaded
}

// FullJoined -> Base
UserFullJoined fullUser = ...;
User baseUser = fullUser.toBase();  // Extracts FK values from loaded entities
```

## Self-Referencing Relationships

```java
// Employee -> Manager (self-referencing)
public record Employee(
    UUID id,
    String name,
    UUID managerId       // Self-reference FK
) {}

public record EmployeeFullJoined(
    UUID id,
    String name,
    Employee manager     // Loaded manager
) {}

// Load with hierarchy using pre-generated join method
List<Row2<Employee, Optional<Employee>>> withManagers = EmployeeTable.TABLE
    .leftJoinManager()   // Generated from managerId -> Employee relationship
    .select()
    .fetch();

// Load managers with their direct reports
List<EmployeeFullJoined> managersWithReports = EmployeeTable.TABLE
    .joinDirectReports()  // Generated from directReports one-to-many relationship
    .selectAs(EmployeeFullJoined.class)
    .fetch();

// Recursive CTE for full hierarchy
List<Employee> hierarchy = SqlCte.withRecursive("emp_tree",
    // Base case: top-level employees
    EmployeeTable.TABLE.select().where(EmployeeTable.MANAGER_ID.isNull()),
    // Recursive case: employees under those
    (cte) -> EmployeeTable.TABLE
        .joinCte(cte, EmployeeTable.MANAGER_ID)  // Join with CTE
        .select()
)
.select()
.fetch();
```

## Best Practices

1. **Use Base entities for simple queries** - When you don't need related data
2. **Use FullJoined for eager loading** - When you always need the relationship
3. **Use PartialJoined for flexible loading** - When you need FK always, entity sometimes
4. **Batch load for collections** - Use separate queries for one-to-many to avoid cartesian products
5. **Check isLoaded() on SqlForeignKey** - Before accessing value() to avoid exceptions
6. **Use requireValue() when confident** - It fails fast with clear error message

## Related Documents

- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Relationship YAML syntax
- [../core/03-query-api.md](../core/03-query-api.md) - Join syntax
- [entity-mapping.md](entity-mapping.md) - How relationships are mapped
