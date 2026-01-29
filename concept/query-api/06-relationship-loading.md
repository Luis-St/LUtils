# Relationship Loading

## Overview

Relationships are loaded explicitly through joins or separate queries. There is no lazy loading or proxy magic - you control exactly when and how related data is fetched.

## Relationship Types

### One-to-One / Many-to-One

```yaml
# YAML schema
tables:
  users:
    columns:
      id: uuid
      name: string
      address_id:
        type: uuid
        references: addresses.id
```

```java
// Generated table with foreign column
public final class UserTable {
    public static final SqlForeignColumn<User, Address, UUID> ADDRESS =
        TABLE.foreignColumn("address_id", Address.class, UUID.class, AddressTable.TABLE);
}

// User entity has the address reference
public record User(
    UUID id,
    String name,
    UUID addressId,         // The FK value (always available)
    Address address         // The loaded entity (null if not joined)
) {}
```

### One-to-Many

```yaml
tables:
  users:
    columns:
      id: uuid
      name: string

  orders:
    columns:
      id: uuid
      user_id:
        type: uuid
        references: users.id
      total: decimal
```

```java
// The "many" side has the foreign column
public final class OrderTable {
    public static final SqlForeignColumn<Order, User, UUID> USER =
        TABLE.foreignColumn("user_id", User.class, UUID.class, UserTable.TABLE);
}
```

## Loading Strategies

### Strategy 1: Join in Single Query

Best for: Loading a few related entities, avoiding N+1.

```java
// Inner join - only users with addresses
List<Tuple2<User, Address>> results = UserTable.TABLE
    .innerJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS)
    .select()
    .fetch();

// Left join - all users, address may be null
List<Tuple2<User, Optional<Address>>> results = UserTable.TABLE
    .leftJoin(AddressTable.TABLE)
    .on(UserTable.ADDRESS)
    .select()
    .fetch();

// Access the data
for (var result : results) {
    User user = result.first();
    Optional<Address> address = result.second();
    // ...
}
```

### Strategy 2: Populate Entity Relationship

Load joined data directly into the entity field:

```java
// User.address() will be populated with the joined Address
List<User> users = UserTable.TABLE
    .leftJoin(AddressTable.TABLE).on(UserTable.ADDRESS)
    .selectEntity(UserTable.TABLE)
    .withLoaded(UserTable.ADDRESS)
    .fetch();

for (User user : users) {
    Address address = user.address();  // Populated from join, or null
}
```

### Strategy 3: Separate Queries (Batch Loading)

Best for: Loading one-to-many relationships, avoiding cartesian products.

```java
// Load users
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Batch load their orders (single query for all users)
List<UUID> userIds = users.stream().map(User::id).toList();
List<Order> allOrders = OrderTable.TABLE.select()
    .where(OrderTable.USER_ID.in(userIds))
    .fetch();

// Group by user
Map<UUID, List<Order>> ordersByUser = allOrders.stream()
    .collect(Collectors.groupingBy(Order::userId));

// Associate
for (User user : users) {
    List<Order> userOrders = ordersByUser.getOrDefault(user.id(), List.of());
    // Process user with their orders
}
```

### Strategy 4: DataLoader Pattern

For complex graphs, use a DataLoader to batch and deduplicate:

```java
// Define loaders
DataLoader<UUID, Address> addressLoader = DataLoader.create(
    ids -> AddressTable.TABLE.select()
        .where(AddressTable.ID.in(ids))
        .fetch()
        .stream()
        .collect(Collectors.toMap(Address::id, a -> a))
);

DataLoader<UUID, List<Order>> ordersLoader = DataLoader.createGrouped(
    userIds -> OrderTable.TABLE.select()
        .where(OrderTable.USER_ID.in(userIds))
        .fetch()
        .stream()
        .collect(Collectors.groupingBy(Order::userId))
);

// Use in a loading context
try (LoadingContext ctx = LoadingContext.begin()) {
    for (User user : users) {
        // Queue loads (not executed yet)
        CompletableFuture<Address> addressFuture = addressLoader.load(user.addressId());
        CompletableFuture<List<Order>> ordersFuture = ordersLoader.load(user.id());
    }
    // Execute all queued loads in batch
    ctx.dispatch();
}
```

## Loading Builder API

```java
public interface LoadingBuilder<E> {

    /**
     * Specifies that the given relationship should be populated from the join.
     */
    <R> LoadingBuilder<E> withLoaded(SqlForeignColumn<E, R, ?> relationship);

    /**
     * Specifies columns to select from the relationship.
     */
    <R> LoadingBuilder<E> withLoaded(SqlForeignColumn<E, R, ?> relationship,
                                      SqlColumn<R, ?>... columns);

    /**
     * Fetches the results with relationships populated.
     */
    List<E> fetch();

    CompletableFuture<List<E>> fetchAsync();
}
```

## Multi-Level Joins

```java
// User -> Order -> Product (3-level join)
List<Tuple3<User, Order, Product>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.USER_ID))
    .innerJoin(ProductTable.TABLE).on(OrderTable.PRODUCT_ID.equalTo(ProductTable.ID))
    .select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// With column selection
List<Tuple3<String, String, BigDecimal>> results = UserTable.TABLE
    .innerJoin(OrderTable.TABLE).on(UserTable.ID.equalTo(OrderTable.USER_ID))
    .innerJoin(ProductTable.TABLE).on(OrderTable.PRODUCT_ID.equalTo(ProductTable.ID))
    .select(UserTable.NAME, ProductTable.NAME, OrderTable.TOTAL)
    .fetch();
```

## Avoiding N+1 Queries

The N+1 problem occurs when you load N entities, then make N separate queries to load their relationships:

```java
// BAD: N+1 queries
List<User> users = UserTable.TABLE.select().fetch();  // 1 query
for (User user : users) {
    // This would be N additional queries if using lazy loading
    Address address = AddressTable.TABLE.select()
        .where(AddressTable.ID.equalTo(user.addressId()))
        .fetchOne();
}

// GOOD: 1 query with join
List<Tuple2<User, Address>> usersWithAddresses = UserTable.TABLE
    .innerJoin(AddressTable.TABLE).on(UserTable.ADDRESS)
    .select()
    .fetch();

// GOOD: 2 queries with batch loading
List<User> users = UserTable.TABLE.select().fetch();
Set<UUID> addressIds = users.stream()
    .map(User::addressId)
    .filter(Objects::nonNull)
    .collect(Collectors.toSet());
Map<UUID, Address> addresses = AddressTable.TABLE.select()
    .where(AddressTable.ID.in(addressIds))
    .fetch()
    .stream()
    .collect(Collectors.toMap(Address::id, a -> a));
```

## Relationship Helpers

Generated helper methods for common patterns:

```java
public final class UserTable {

    /**
     * Loads users with their addresses in a single query.
     */
    public static List<Tuple2<User, Optional<Address>>> selectWithAddress() {
        return TABLE
            .leftJoin(AddressTable.TABLE).on(ADDRESS)
            .select()
            .fetch();
    }

    /**
     * Loads a user by ID with their address.
     */
    public static Optional<Tuple2<User, Optional<Address>>> findByIdWithAddress(UUID id) {
        return TABLE
            .leftJoin(AddressTable.TABLE).on(ADDRESS)
            .select()
            .where(ID.equalTo(id))
            .fetchFirst();
    }
}

// Usage
var result = UserTable.findByIdWithAddress(userId);
```

## Circular References

For entities that reference each other:

```java
// Employee -> Manager (self-referencing)
public final class EmployeeTable {
    public static final SqlForeignColumn<Employee, Employee, UUID> MANAGER =
        TABLE.foreignColumn("manager_id", Employee.class, UUID.class, TABLE);
}

// Load with hierarchy
List<Tuple2<Employee, Optional<Employee>>> withManagers = EmployeeTable.TABLE
    .leftJoin(EmployeeTable.TABLE.as("manager")).on(EmployeeTable.MANAGER)
    .select()
    .fetch();
```

## Open Questions

- **Graph loading**: How should deep relationship graphs be loaded efficiently?
- **Relationship hints**: Should there be hints in the YAML schema about preferred loading strategies?
- **Identity map**: Should there be request-scoped identity map to avoid duplicate entity instances?
- **Prefetch API**: Should there be a declarative prefetch API for common loading patterns?
