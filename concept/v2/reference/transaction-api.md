# Transaction API

## Overview

Transactions are managed explicitly - there is no annotation-based magic. You control exactly when transactions begin, commit, and rollback. The API supports three patterns:

1. **Connection-first**: Get a connection, then start a transaction
2. **Transaction-first**: Start a transaction (connection managed internally)
3. **Auto-commit**: Execute queries without an explicit transaction

## Connection-First Pattern

The connection-first pattern gives you explicit control over the connection lifecycle:

```java
try (Connection conn = dataSource.getConnection()) {
    Transaction tx = conn.beginTransaction();
    try {
        UserTable.TABLE.insert(user);
        OrderTable.TABLE.insert(order);
        tx.commit();
    } catch (Exception e) {
        tx.rollback();
        throw e;
    }
}
```

This pattern is useful when you need:
- Connection pooling control
- Connection-level settings (timeouts, read-only mode)
- Multiple sequential transactions on the same connection

## Transaction-First Pattern

The transaction-first pattern manages the connection internally:

```java
// Using try-with-resources
try (Transaction tx = database.beginTransaction()) {
    UserTable.TABLE.insert(user);
    OrderTable.TABLE.insert(order);
    tx.commit();
}
// Connection is returned to pool, auto-rollback if commit() not called

// Using functional style
User savedUser = database.inTransaction(tx -> {
    User user = UserTable.TABLE.insert(newUser);
    AuditTable.TABLE.insert(AuditEntry.forCreate(user));
    return user;
});
// Commits on success, rolls back on exception
```

## Queries Without Transaction (Auto-Commit)

For read-only queries or simple operations, you can skip explicit transactions:

```java
// Auto-commit mode - each statement is its own transaction
List<User> users = UserTable.TABLE.select()
    .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
    .fetch();

// Single insert in auto-commit
User saved = UserTable.TABLE.insert(user);
```

## Transaction Interface

```java
public interface Transaction extends AutoCloseable {

    /**
     * Commits all changes made in this transaction.<br>
     * After commit, the transaction is completed and cannot be reused.
     */
    void commit();

    /**
     * Rolls back all changes made in this transaction.<br>
     * After rollback, the transaction is completed and cannot be reused.
     */
    void rollback();

    /**
     * Returns whether this transaction is still active.<br>
     *
     * @return True if the transaction can still be committed or rolled back
     */
    boolean isActive();

    /**
     * Creates a savepoint that can be rolled back to.<br>
     *
     * @param name The savepoint name
     * @return The savepoint
     */
    Savepoint savepoint(String name);

    /**
     * Rolls back to a previously created savepoint.<br>
     *
     * @param savepoint The savepoint to roll back to
     */
    void rollbackTo(Savepoint savepoint);

    /**
     * Releases a savepoint, making it no longer available for rollback.<br>
     *
     * @param savepoint The savepoint to release
     */
    void release(Savepoint savepoint);

    /**
     * Sets the transaction isolation level.<br>
     * Must be called before any queries are executed.
     *
     * @param level The isolation level
     * @return This transaction for chaining
     */
    Transaction isolation(IsolationLevel level);

    /**
     * Marks this transaction as read-only.<br>
     * Some databases can optimize read-only transactions.
     *
     * @return This transaction for chaining
     */
    Transaction readOnly();

    /**
     * Sets a timeout for this transaction.<br>
     * If the transaction exceeds this time, it will be rolled back.
     *
     * @param duration The timeout duration
     * @return This transaction for chaining
     */
    Transaction timeout(Duration duration);

    /**
     * Returns the underlying JDBC connection.<br>
     * Use with caution - prefer using the provided API.
     *
     * @return The JDBC connection
     */
    Connection getConnection();

    /**
     * Close is called automatically by try-with-resources.<br>
     * If commit() was not called, the transaction is rolled back.
     */
    @Override
    void close();
}
```

## Database Entry Points

```java
public interface Database {

    /**
     * Begins a new transaction with default settings.<br>
     * The connection is obtained from the pool internally.
     *
     * @return A new transaction
     */
    Transaction beginTransaction();

    /**
     * Begins a transaction with the specified isolation level.<br>
     *
     * @param level The isolation level
     * @return A new transaction
     */
    Transaction beginTransaction(IsolationLevel level);

    /**
     * Executes the given function within a transaction.<br>
     * Commits on success, rolls back on exception.
     *
     * @param action The action to execute
     * @return The result of the action
     */
    <T> T inTransaction(Function<Transaction, T> action);

    /**
     * Executes the given action within a transaction.<br>
     * Commits on success, rolls back on exception.
     *
     * @param action The action to execute
     */
    void inTransaction(Consumer<Transaction> action);
}
```

## Isolation Levels

```java
public enum IsolationLevel {

    /**
     * Lowest isolation. Dirty reads, non-repeatable reads, and phantom reads possible.
     */
    READ_UNCOMMITTED,

    /**
     * Prevents dirty reads. Non-repeatable reads and phantom reads possible.
     */
    READ_COMMITTED,

    /**
     * Prevents dirty and non-repeatable reads. Phantom reads possible.
     */
    REPEATABLE_READ,

    /**
     * Highest isolation. Prevents all anomalies but may have performance impact.
     */
    SERIALIZABLE
}
```

Usage:
```java
try (Transaction tx = database.beginTransaction(IsolationLevel.SERIALIZABLE)) {
    // Fully isolated transaction
    BigDecimal balance = AccountTable.TABLE.select(AccountTable.BALANCE)
        .where(AccountTable.ID.equalTo(accountId))
        .forUpdate()
        .fetchOne();

    AccountTable.TABLE.update()
        .set(AccountTable.BALANCE, balance.subtract(amount))
        .where(AccountTable.ID.equalTo(accountId))
        .execute();

    tx.commit();
}
```

## Savepoints

Savepoints allow partial rollback within a transaction:

```java
try (Transaction tx = database.beginTransaction()) {
    UserTable.TABLE.insert(user1);

    Savepoint sp = tx.savepoint("before_user2");

    try {
        UserTable.TABLE.insert(user2);
        // Something goes wrong
        if (someCondition) {
            throw new BusinessException("Invalid state");
        }
    } catch (BusinessException e) {
        tx.rollbackTo(sp);
        // user1 is still inserted, user2 is rolled back
        UserTable.TABLE.insert(alternativeUser);
    }

    tx.commit();
}
```

## Nested Transaction Support

Nested transaction calls participate in the outer transaction. The inner code can check for and work with the existing transaction:

```java
void outerMethod() {
    try (Transaction tx = database.beginTransaction()) {
        UserTable.TABLE.insert(user);
        innerMethod();  // Uses the same transaction
        tx.commit();
    }
}

void innerMethod() {
    // Check if we're already in a transaction
    if (Transaction.isInTransaction()) {
        // Get the current transaction
        Transaction current = Transaction.current()
            .orElseThrow(() -> new IllegalStateException("Expected active transaction"));

        // Or require it explicitly
        Transaction.requireActive();  // Throws if no active transaction

        OrderTable.TABLE.insert(order);  // Uses current transaction
    } else {
        // No transaction, start our own
        try (Transaction tx = database.beginTransaction()) {
            OrderTable.TABLE.insert(order);
            tx.commit();
        }
    }
}
```

## Transaction Context

Static methods for working with the current transaction:

```java
public interface Transaction {

    /**
     * Returns the currently active transaction for this thread, if any.<br>
     *
     * @return Optional containing the current transaction, empty if none active
     */
    static Optional<Transaction> current();

    /**
     * Returns the current transaction, throwing if none is active.<br>
     *
     * @return The current transaction
     * @throws IllegalStateException If no transaction is active
     */
    static Transaction requireActive();

    /**
     * Returns true if a transaction is currently active for this thread.<br>
     *
     * @return True if in a transaction
     */
    static boolean isInTransaction();
}
```

## Read-Only Transactions

```java
// Read-only for performance optimization
try (Transaction tx = database.beginTransaction().readOnly()) {
    List<User> users = UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
        .fetch();

    // Any insert/update/delete would throw ReadOnlyTransactionException
}
```

Read-only transactions may be optimized by the database (e.g., PostgreSQL can skip WAL writes).

## Transaction Timeout

```java
try (Transaction tx = database.beginTransaction().timeout(Duration.ofSeconds(30))) {
    // Long-running operations
    processLargeDataset();
    tx.commit();
}
// TransactionTimeoutException is thrown if timeout is exceeded
```

## Error Handling

```java
try (Transaction tx = database.beginTransaction()) {
    try {
        UserTable.TABLE.insert(user);
        OrderTable.TABLE.insert(order);
        tx.commit();
    } catch (UniqueConstraintViolationException e) {
        // Specific handling for duplicate key
        tx.rollback();
        throw new DuplicateUserException(e);
    } catch (ForeignKeyViolationException e) {
        // Specific handling for FK violation
        tx.rollback();
        throw new InvalidReferenceException(e);
    }
    // General DatabaseException is also rolled back by try-with-resources
}
```

## Transaction Listeners

For cross-cutting concerns like auditing or cache invalidation:

```java
database.addTransactionListener(new TransactionListener() {
    @Override
    public void beforeCommit(Transaction tx) {
        // Called before commit
        log.info("About to commit transaction");
    }

    @Override
    public void afterCommit(Transaction tx) {
        // Called after successful commit
        cacheInvalidator.invalidate(tx.getChangedEntities());
    }

    @Override
    public void afterRollback(Transaction tx) {
        // Called after rollback
        log.warn("Transaction rolled back");
    }
});
```

## Async Transactions

```java
// Async transaction with callback
CompletableFuture<User> result = database.inTransactionAsync(tx -> {
    return UserTable.TABLE.insertAsync(user)
        .thenCompose(saved -> {
            AuditEntry entry = AuditEntry.forCreate(saved);
            return AuditTable.TABLE.insertAsync(entry)
                .thenApply(audit -> saved);
        });
});

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

## Best Practices

### Keep Transactions Short
Long-running transactions hold locks and connections. Keep them focused:

```java
// BAD: Long transaction with external call
try (Transaction tx = database.beginTransaction()) {
    User user = UserTable.TABLE.select()...;
    String result = externalApi.call(user);  // Network call in transaction!
    UserTable.TABLE.update()...;
    tx.commit();
}

// GOOD: External call outside transaction
User user = UserTable.TABLE.select()...;
String result = externalApi.call(user);  // Outside transaction
try (Transaction tx = database.beginTransaction()) {
    UserTable.TABLE.update()...;
    tx.commit();
}
```

### Use Appropriate Isolation Level
Default (READ_COMMITTED) is suitable for most cases. Use higher levels only when needed:

```java
// Most queries: default isolation
List<User> users = UserTable.TABLE.select().fetch();

// Financial operations: serializable
try (Transaction tx = database.beginTransaction(IsolationLevel.SERIALIZABLE)) {
    // Money transfer logic
    tx.commit();
}
```

### Handle Retries for Deadlocks
```java
User user = Retry.withBackoff(3, Duration.ofMillis(100))
    .retryOn(DeadlockException.class)
    .execute(() -> {
        try (Transaction tx = database.beginTransaction()) {
            User u = UserTable.TABLE.select()
                .where(UserTable.ID.equalTo(userId))
                .forUpdate()
                .fetchOne();
            u = u.withLoginCount(u.loginCount() + 1);
            UserTable.TABLE.update(u);
            tx.commit();
            return u;
        }
    });
```

## Related Documents

- [error-handling.md](error-handling.md) - Transaction exceptions
- [async-api.md](async-api.md) - Async transaction support
- [../core/03-query-api.md](../core/03-query-api.md) - Queries within transactions
