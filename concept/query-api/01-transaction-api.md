# Transaction API

## Overview

Transactions are managed explicitly using a try-with-resources pattern. There is no annotation-based magic - you control exactly when transactions begin, commit, and rollback.

## Basic Transaction Usage

```java
// Simple transaction
try (Transaction tx = Database.beginTransaction()) {
    UserTable.TABLE.insert(user);
    OrderTable.TABLE.insert(order);
    tx.commit();
}
// Auto-rollback if commit() not called

// With return value
User savedUser = Database.inTransaction(tx -> {
    User user = UserTable.TABLE.insert(newUser);
    AuditTable.TABLE.insert(AuditEntry.forCreate(user));
    return user;
});
```

## Transaction Interface

```java
public interface Transaction extends AutoCloseable {

    /**
     * Commits all changes made in this transaction.
     * After commit, the transaction is completed and cannot be reused.
     */
    void commit();

    /**
     * Rolls back all changes made in this transaction.
     * After rollback, the transaction is completed and cannot be reused.
     */
    void rollback();

    /**
     * Returns whether this transaction is still active.
     */
    boolean isActive();

    /**
     * Creates a savepoint that can be rolled back to.
     */
    Savepoint savepoint(String name);

    /**
     * Rolls back to a previously created savepoint.
     */
    void rollbackTo(Savepoint savepoint);

    /**
     * Releases a savepoint, making it no longer available for rollback.
     */
    void release(Savepoint savepoint);

    /**
     * Sets the transaction isolation level.
     * Must be called before any queries are executed.
     */
    Transaction isolation(IsolationLevel level);

    /**
     * Marks this transaction as read-only.
     * Some databases can optimize read-only transactions.
     */
    Transaction readOnly();

    /**
     * Sets a timeout for this transaction in seconds.
     * If the transaction exceeds this time, it will be rolled back.
     */
    Transaction timeout(int seconds);

    /**
     * Close is called automatically by try-with-resources.
     * If commit() was not called, the transaction is rolled back.
     */
    @Override
    void close();
}
```

## Database Entry Points

```java
public final class Database {

    /**
     * Begins a new transaction.
     * Must be used with try-with-resources.
     */
    public static Transaction beginTransaction() { ... }

    /**
     * Begins a transaction with the specified isolation level.
     */
    public static Transaction beginTransaction(IsolationLevel level) { ... }

    /**
     * Executes the given function within a transaction.
     * Commits on success, rolls back on exception.
     */
    public static <T> T inTransaction(Function<Transaction, T> action) { ... }

    /**
     * Executes the given action within a transaction.
     * Commits on success, rolls back on exception.
     */
    public static void inTransaction(Consumer<Transaction> action) { ... }

    /**
     * Executes the given action without an explicit transaction.
     * Each statement runs in its own auto-commit transaction.
     */
    public static <T> T withAutoCommit(Supplier<T> action) { ... }
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

## Savepoints

```java
try (Transaction tx = Database.beginTransaction()) {
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

## Nested Transactions

Nested transaction calls participate in the outer transaction (no true nested transactions):

```java
void outerMethod() {
    try (Transaction tx = Database.beginTransaction()) {
        UserTable.TABLE.insert(user);
        innerMethod();  // Uses the same transaction
        tx.commit();
    }
}

void innerMethod() {
    // This gets the current transaction, doesn't start a new one
    Transaction current = Transaction.current()
        .orElseThrow(() -> new IllegalStateException("No active transaction"));

    // Or require a transaction to exist
    Transaction.requireActive();
    OrderTable.TABLE.insert(order);
}
```

## Transaction Context

```java
public interface Transaction {

    /**
     * Returns the currently active transaction, if any.
     */
    static Optional<Transaction> current() { ... }

    /**
     * Throws if no transaction is active.
     */
    static Transaction requireActive() { ... }

    /**
     * Returns true if a transaction is currently active.
     */
    static boolean isInTransaction() { ... }
}
```

## Read-Only Transactions

```java
// Read-only for performance optimization
try (Transaction tx = Database.beginTransaction().readOnly()) {
    List<User> users = UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
        .fetch();
    // Any insert/update/delete would throw ReadOnlyTransactionException
}
```

## Transaction Timeout

```java
try (Transaction tx = Database.beginTransaction().timeout(30)) {
    // Long-running operations
    // If total time exceeds 30 seconds, TransactionTimeoutException is thrown
    processLargeDataset();
    tx.commit();
}
```

## Error Handling

```java
try (Transaction tx = Database.beginTransaction()) {
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
    // General SQLException is also rolled back by try-with-resources
}
```

## Open Questions

- **Connection binding**: Should transactions be bound to a specific connection or retrieved from a pool per-statement?
- **Distributed transactions**: Is XA/2PC support needed for multi-database transactions?
- **Transaction listeners**: Should there be hooks for before-commit/after-commit/after-rollback?
