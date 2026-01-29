# Async API

## Overview

All synchronous query operations have async counterparts returning `CompletableFuture<T>`. The async API uses a separate executor/connection pool optimized for non-blocking operations.

## Naming Convention

Async methods use the `Async` suffix:

| Sync Method | Async Method |
|-------------|--------------|
| `fetch()` | `fetchAsync()` |
| `fetchFirst()` | `fetchFirstAsync()` |
| `fetchOne()` | `fetchOneAsync()` |
| `count()` | `countAsync()` |
| `exists()` | `existsAsync()` |
| `execute()` | `executeAsync()` |

## SELECT Queries

```java
// Async fetch all
CompletableFuture<List<User>> futureUsers = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetchAsync();

// Async fetch first
CompletableFuture<Optional<User>> futureUser = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchFirstAsync();

// Async count
CompletableFuture<Long> futureCount = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .countAsync();

// Async exists
CompletableFuture<Boolean> futureExists = UserTable.TABLE.select()
    .where(UserTable.EMAIL.equalTo(email))
    .existsAsync();
```

## Chaining Async Operations

```java
// Chain dependent operations
CompletableFuture<OrderSummary> summary = UserTable.TABLE.select()
    .where(UserTable.EMAIL.equalTo(email))
    .fetchFirstAsync()
    .thenCompose(userOpt -> {
        User user = userOpt.orElseThrow(() -> new UserNotFoundException(email));
        return OrderTable.TABLE.select()
            .where(OrderTable.USER_ID.equalTo(user.getId()))
            .fetchAsync();
    })
    .thenApply(orders -> OrderSummary.from(orders));

// Combine independent operations
CompletableFuture<Dashboard> dashboard = CompletableFuture.allOf(
    UserTable.TABLE.select().countAsync(),
    OrderTable.TABLE.select().where(OrderTable.STATUS.equalTo(OrderStatus.PENDING)).countAsync(),
    ProductTable.TABLE.select().where(ProductTable.STOCK.lessThan(10)).fetchAsync()
).thenApply(v -> new Dashboard(userCount, pendingOrders, lowStockProducts));
```

## INSERT/UPDATE/DELETE

```java
// Async insert
CompletableFuture<User> savedUser = UserTable.TABLE.insertAsync(user);

// Async batch insert
CompletableFuture<List<User>> savedUsers = UserTable.TABLE.insertAsync(userList);

// Async update
CompletableFuture<Integer> updatedCount = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.ID.equalTo(userId))
    .executeAsync();

// Async delete
CompletableFuture<Integer> deletedCount = UserTable.TABLE.delete()
    .where(UserTable.STATUS.equalTo(UserStatus.EXPIRED))
    .executeAsync();
```

## Async Transactions

```java
// Async transaction with callback
CompletableFuture<User> result = Database.inTransactionAsync(tx -> {
    return UserTable.TABLE.insertAsync(user)
        .thenCompose(saved -> {
            AuditEntry entry = AuditEntry.forCreate(saved);
            return AuditTable.TABLE.insertAsync(entry)
                .thenApply(audit -> saved);
        });
});

// Manual async transaction control
CompletableFuture<User> result = Database.beginTransactionAsync()
    .thenCompose(tx -> {
        return UserTable.TABLE.insertAsync(user)
            .thenCompose(saved -> tx.commitAsync().thenApply(v -> saved))
            .exceptionally(e -> {
                tx.rollbackAsync();
                throw new CompletionException(e);
            });
    });
```

## Executor Configuration

```java
// Configure the async executor
DatabaseConfig config = DatabaseConfig.builder()
    .asyncExecutor(Executors.newFixedThreadPool(20))
    .asyncConnectionPoolSize(20)
    .build();

// Or use virtual threads (Java 21+)
DatabaseConfig config = DatabaseConfig.builder()
    .asyncExecutor(Executors.newVirtualThreadPerTaskExecutor())
    .build();
```

## Timeout Handling

```java
// With timeout
CompletableFuture<List<User>> users = UserTable.TABLE.select()
    .fetchAsync()
    .orTimeout(5, TimeUnit.SECONDS);

// With fallback on timeout
CompletableFuture<List<User>> users = UserTable.TABLE.select()
    .fetchAsync()
    .completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
```

## Error Handling

```java
CompletableFuture<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .exceptionally(e -> {
        if (e.getCause() instanceof EntityNotFoundException) {
            return User.anonymous();
        }
        throw new CompletionException(e);
    });

// With handle for both success and failure
CompletableFuture<Result<User>> result = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .handle((user, error) -> {
        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(user);
    });
```

## Cancellation

```java
CompletableFuture<List<User>> future = UserTable.TABLE.select().fetchAsync();

// Cancel if no longer needed
boolean cancelled = future.cancel(true);

// Check if cancelled
if (future.isCancelled()) {
    log.info("Query was cancelled");
}
```

## Async Builder Interface

```java
public interface SelectBuilder<E> {

    // Sync methods
    List<E> fetch();
    Optional<E> fetchFirst();
    E fetchOne();
    long count();
    boolean exists();

    // Async counterparts
    CompletableFuture<List<E>> fetchAsync();
    CompletableFuture<Optional<E>> fetchFirstAsync();
    CompletableFuture<E> fetchOneAsync();
    CompletableFuture<Long> countAsync();
    CompletableFuture<Boolean> existsAsync();
}

public interface UpdateBuilder<E> {

    int execute();
    CompletableFuture<Integer> executeAsync();
}

public interface DeleteBuilder<E> {

    int execute();
    CompletableFuture<Integer> executeAsync();
}
```

## Streaming with Async

For large result sets, async streaming returns results as they arrive:

```java
// Returns a stream that can be consumed asynchronously
CompletableFuture<Stream<User>> streamFuture = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .streamAsync();

// Process items as they arrive
UserTable.TABLE.select()
    .forEachAsync(user -> {
        // Called for each row as it's fetched
        processUser(user);
    })
    .thenRun(() -> log.info("All users processed"));
```

## Open Questions

- **Backpressure**: How to handle backpressure when consuming large result sets asynchronously?
- **Connection management**: Should async operations use a separate connection pool?
- **Virtual threads**: Should virtual threads be the recommended approach for Java 21+?
- **Reactive interop**: Should there be adapters for Project Reactor (Mono/Flux) or RxJava?
