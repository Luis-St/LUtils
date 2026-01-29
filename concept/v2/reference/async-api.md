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
| `fetchOneOrNull()` | `fetchOneOrNullAsync()` |
| `count()` | `countAsync()` |
| `exists()` | `existsAsync()` |
| `execute()` | `executeAsync()` |
| `stream()` | `streamAsync()` |

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

// Async fetch one
CompletableFuture<User> futureUser = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync();

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
            .where(OrderTable.CUSTOMER_ID.equalTo(user.id()))
            .fetchAsync();
    })
    .thenApply(orders -> OrderSummary.from(orders));

// Combine independent operations
CompletableFuture<Long> userCountFuture = UserTable.TABLE.select().countAsync();
CompletableFuture<Long> orderCountFuture = OrderTable.TABLE.select()
    .where(OrderTable.STATUS.equalTo(OrderStatus.PENDING))
    .countAsync();
CompletableFuture<List<Product>> lowStockFuture = ProductTable.TABLE.select()
    .where(ProductTable.STOCK.lessThan(10))
    .fetchAsync();

CompletableFuture<Dashboard> dashboard = CompletableFuture.allOf(
    userCountFuture, orderCountFuture, lowStockFuture
).thenApply(v -> new Dashboard(
    userCountFuture.join(),
    orderCountFuture.join(),
    lowStockFuture.join()
));
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

// Async update with RETURNING
CompletableFuture<List<User>> updatedUsers = UserTable.TABLE.update()
    .set(UserTable.STATUS, UserStatus.ACTIVE)
    .where(UserTable.STATUS.equalTo(UserStatus.PENDING))
    .returning()
    .fetchAsync();
```

## Async Transactions

```java
// Async transaction with functional style
CompletableFuture<User> result = database.inTransactionAsync(tx -> {
    return UserTable.TABLE.insertAsync(user)
        .thenCompose(saved -> {
            AuditEntry entry = AuditEntry.forCreate(saved);
            return AuditTable.TABLE.insertAsync(entry)
                .thenApply(audit -> saved);
        });
});
// Commits on success, rolls back on exception

// Manual async transaction control
CompletableFuture<User> result = database.beginTransactionAsync()
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

// Use virtual threads (Java 21+)
DatabaseConfig config = DatabaseConfig.builder()
    .asyncExecutor(Executors.newVirtualThreadPerTaskExecutor())
    .build();

// Custom executor with bounded queue
ExecutorService executor = new ThreadPoolExecutor(
    10, 50,
    60L, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1000),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
DatabaseConfig config = DatabaseConfig.builder()
    .asyncExecutor(executor)
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

// Combined with other operations
CompletableFuture<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .orTimeout(2, TimeUnit.SECONDS)
    .exceptionally(e -> {
        if (e.getCause() instanceof TimeoutException) {
            throw new ServiceUnavailableException("Database timeout");
        }
        throw new CompletionException(e);
    });
```

## Error Handling

```java
// Handle specific exceptions
CompletableFuture<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .exceptionally(e -> {
        if (e.getCause() instanceof EntityNotFoundException) {
            return User.anonymous();
        }
        throw new CompletionException(e);
    });

// Handle with Result type
CompletableFuture<Result<User>> result = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .handle((user, error) -> {
        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(user);
    });

// Recover from specific errors
CompletableFuture<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .exceptionallyCompose(e -> {
        if (e.getCause() instanceof EntityNotFoundException) {
            // Try fallback source
            return fallbackService.getUserAsync(userId);
        }
        return CompletableFuture.failedFuture(e);
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

// With timeout and cancellation
CompletableFuture<List<User>> future = UserTable.TABLE.select()
    .fetchAsync()
    .orTimeout(5, TimeUnit.SECONDS);

// Cancel on external signal
shutdownSignal.thenAccept(v -> future.cancel(true));
```

## Streaming with Async

For large result sets:

```java
// Returns a stream that can be consumed asynchronously
CompletableFuture<Stream<User>> streamFuture = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .streamAsync();

streamFuture.thenAccept(stream -> {
    stream.forEach(user -> processUser(user));
});

// Process items as they arrive with callback
UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .forEachAsync(user -> {
        // Called for each row as it's fetched
        processUser(user);
    })
    .thenRun(() -> log.info("All users processed"));

// With parallelism control
UserTable.TABLE.select()
    .forEachAsync(user -> processUser(user), 10)  // Max 10 concurrent
    .join();
```

## Async Builder Interface

```java
public interface SelectBuilder<E> {

    // Sync methods
    List<E> fetch();
    Optional<E> fetchFirst();
    E fetchOne();
    E fetchOneOrNull();
    long count();
    boolean exists();
    Stream<E> stream();

    // Async counterparts
    CompletableFuture<List<E>> fetchAsync();
    CompletableFuture<Optional<E>> fetchFirstAsync();
    CompletableFuture<E> fetchOneAsync();
    CompletableFuture<E> fetchOneOrNullAsync();
    CompletableFuture<Long> countAsync();
    CompletableFuture<Boolean> existsAsync();
    CompletableFuture<Stream<E>> streamAsync();

    // Async iteration
    CompletableFuture<Void> forEachAsync(Consumer<E> action);
    CompletableFuture<Void> forEachAsync(Consumer<E> action, int maxConcurrency);
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

## Virtual Threads (Java 21+)

With virtual threads, async becomes simpler:

```java
// Configure with virtual threads
DatabaseConfig config = DatabaseConfig.builder()
    .asyncExecutor(Executors.newVirtualThreadPerTaskExecutor())
    .build();

// Each async operation gets its own virtual thread
// Can have millions of concurrent operations
List<CompletableFuture<User>> futures = userIds.stream()
    .map(id -> UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(id))
        .fetchOneAsync())
    .toList();

List<User> users = futures.stream()
    .map(CompletableFuture::join)
    .toList();
```

## Best Practices

1. **Use async for I/O-bound operations**: Database queries, network calls
2. **Compose futures**: Use `thenCompose` for dependent operations, `thenCombine` for parallel
3. **Handle errors properly**: Always handle exceptions to avoid silent failures
4. **Set timeouts**: Prevent unbounded waits with `orTimeout`
5. **Consider virtual threads**: On Java 21+, virtual threads simplify async code
6. **Avoid blocking in callbacks**: Don't call `.join()` or `.get()` inside async callbacks

## Related Documents

- [../core/03-query-api.md](../core/03-query-api.md) - Sync query API
- [transaction-api.md](transaction-api.md) - Async transactions
- [error-handling.md](error-handling.md) - Async exception handling
