# ORM Features

## Overview

This document describes the Object-Relational Mapping features including relationships, loading strategies, caching, and entity lifecycle management.

## Relationships

### Relationship Types

#### One-to-One

```yaml
# user_profile.yaml
entity: UserProfile
table: user_profiles

fields:
  id:
    type: UUID
    primary: true
  bio:
    type: String
    columnType: TEXT
  avatarUrl:
    type: String
    column: avatar_url

relationships:
  user:
    type: one-to-one
    target: User
    foreignKey: user_id
    optional: false
    cascade: [all]
```

Generated code:

```java
@NullMarked
public record UserProfile(
    UUID id,
    String bio,
    @Nullable String avatarUrl,
    @ToOne(fetch = FetchType.EAGER) User user
) {
    // ...
}

// Repository with relationship loading
public interface UserProfileRepository extends Repository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId(UUID userId);

    // Query with eager loading
    UserProfileQuery query();
}

// Usage
Optional<UserProfile> profile = profileRepository.query()
    .whereUserId(userId)
    .withUser()  // Eager load user
    .fetchFirst();
```

#### One-to-Many

```yaml
# post.yaml
entity: Post
table: posts

fields:
  id:
    type: Long
    primary: true
    generated: true
  title:
    type: String

relationships:
  comments:
    type: one-to-many
    target: Comment
    mappedBy: post
    fetch: lazy
    cascade: [all]
    orphanRemoval: true
    orderBy:
      - field: createdAt
        direction: desc
```

Generated code:

```java
@NullMarked
public record Post(
    Long id,
    String title,
    @ToMany(fetch = FetchType.LAZY, mappedBy = "post") List<Comment> comments
) {
    // Lazy-loaded comments
    public LazyList<Comment> comments() {
        return LazyList.of(comments, this::loadComments);
    }

    private List<Comment> loadComments() {
        // Loaded on first access
        return commentRepository.findByPostId(id);
    }
}
```

#### Many-to-One

```yaml
# comment.yaml
entity: Comment
table: comments

fields:
  id:
    type: Long
    primary: true
    generated: true
  content:
    type: String
    columnType: TEXT

relationships:
  post:
    type: many-to-one
    target: Post
    foreignKey: post_id
    fetch: eager
    optional: false

  author:
    type: many-to-one
    target: User
    foreignKey: author_id
    fetch: lazy
    optional: false
```

#### Many-to-Many

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
      columns:
        - name: assigned_at
          type: Instant
          default: now()
    fetch: lazy
    cascade: [persist, merge]
```

Generated code:

```java
@NullMarked
public record Post(
    Long id,
    String title,
    @ManyToMany(joinTable = "post_tags") List<Tag> tags
) {
    // Add tag with join table data
    public void addTag(Tag tag, Instant assignedAt) {
        postTagRepository.insert(new PostTag(this.id, tag.id(), assignedAt));
    }
}

// Join table entity (generated)
@NullMarked
public record PostTag(
    Long postId,
    Long tagId,
    Instant assignedAt
) {}
```

## Loading Strategies

### Fetch Types

```java
public enum FetchType {
    /**
     * Load relationship immediately with parent entity.
     */
    EAGER,

    /**
     * Load relationship on first access.
     */
    LAZY,

    /**
     * Never automatically load; must be explicitly fetched.
     */
    MANUAL
}
```

### Eager Loading

Eager loading uses JOIN queries to load relationships with the parent:

```java
// Single relationship
List<Post> posts = postRepository.query()
    .withAuthor()  // JOIN users
    .fetch();

// Multiple relationships
List<Post> posts = postRepository.query()
    .withAuthor()
    .withTags()
    .fetch();

// Nested relationships
List<Post> posts = postRepository.query()
    .withComments(comments -> comments
        .withAuthor())
    .fetch();

// Generated SQL
// SELECT p.*, u.*, c.*, cu.*
// FROM posts p
// LEFT JOIN users u ON p.author_id = u.id
// LEFT JOIN comments c ON c.post_id = p.id
// LEFT JOIN users cu ON c.author_id = cu.id
```

### Lazy Loading

Lazy loading defers relationship loading until first access:

```java
// Lazy proxy wrapper
public final class LazyProxy<T> implements Supplier<T> {

    private final Supplier<T> loader;
    private volatile T value;
    private volatile boolean loaded = false;

    public static <T> LazyProxy<T> of(Supplier<T> loader) {
        return new LazyProxy<>(loader);
    }

    @Override
    public T get() {
        if (!loaded) {
            synchronized (this) {
                if (!loaded) {
                    value = loader.get();
                    loaded = true;
                }
            }
        }
        return value;
    }

    public boolean isLoaded() {
        return loaded;
    }
}

// Lazy collection wrapper
public final class LazyList<T> implements List<T> {

    private final List<T> delegate;
    private final Supplier<List<T>> loader;
    private volatile boolean loaded = false;

    @Override
    public T get(int index) {
        ensureLoaded();
        return delegate.get(index);
    }

    private void ensureLoaded() {
        if (!loaded) {
            synchronized (this) {
                if (!loaded) {
                    delegate.clear();
                    delegate.addAll(loader.get());
                    loaded = true;
                }
            }
        }
    }

    // ... delegate other List methods
}
```

### Batch Loading (N+1 Prevention)

```java
// Configure batch loading
DatabaseConfig config = DatabaseConfig.builder()
    .batchFetchSize(50)  // Load up to 50 related entities at once
    .build();

// Batch loader implementation
public final class BatchLoader<K, V> {

    private final int batchSize;
    private final Function<Collection<K>, Map<K, V>> loader;
    private final Map<K, CompletableFuture<V>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<V> load(K key) {
        return pending.computeIfAbsent(key, k -> {
            // Schedule batch load when batch size reached
            if (pending.size() >= batchSize) {
                executeBatch();
            }
            return new CompletableFuture<>();
        });
    }

    private void executeBatch() {
        Set<K> keys = new HashSet<>(pending.keySet());
        Map<K, V> results = loader.apply(keys);

        for (K key : keys) {
            CompletableFuture<V> future = pending.remove(key);
            if (future != null) {
                future.complete(results.get(key));
            }
        }
    }
}

// Usage - automatically batches queries
List<Post> posts = postRepository.findAll();
for (Post post : posts) {
    // These are batched automatically
    User author = post.author().get();  // Batched query for all authors
}

// Generated SQL (single query for all authors):
// SELECT * FROM users WHERE id IN (?, ?, ?, ...)
```

## Cascade Operations

### Cascade Types

```java
public enum CascadeType {
    /**
     * Cascade persist operations.
     */
    PERSIST,

    /**
     * Cascade merge operations.
     */
    MERGE,

    /**
     * Cascade remove operations.
     */
    REMOVE,

    /**
     * Cascade refresh operations.
     */
    REFRESH,

    /**
     * Cascade detach operations.
     */
    DETACH,

    /**
     * All cascade operations.
     */
    ALL
}
```

### Cascade Persist

```java
// Post with comments - cascade: [persist]
Post post = Post.builder()
    .title("New Post")
    .comments(List.of(
        Comment.builder().content("First comment").build(),
        Comment.builder().content("Second comment").build()
    ))
    .build();

// Single save cascades to comments
Post saved = postRepository.save(post);
// Inserts: 1 post + 2 comments
```

### Cascade Remove

```java
// Post with cascade: [remove] on comments
postRepository.delete(post);
// Deletes: all comments first, then post
```

### Orphan Removal

```java
// Remove comment from collection - orphanRemoval: true
Post post = postRepository.findById(postId);
List<Comment> comments = new ArrayList<>(post.comments());
comments.remove(0);  // Remove first comment

Post updated = post.toBuilder()
    .comments(comments)
    .build();

Post saved = postRepository.save(updated);
// Removed comment is deleted from database
```

## Caching

### Cache Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Application                       │
└─────────────────────────┬───────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────┐
│              First-Level Cache (Session)             │
│         • Per-transaction/session                    │
│         • Identity map (prevents duplicate objects)  │
│         • Automatically cleared on commit/rollback   │
└─────────────────────────┬───────────────────────────┘
                          │ miss
┌─────────────────────────▼───────────────────────────┐
│            Second-Level Cache (Shared)               │
│         • Application-wide                           │
│         • Configurable per entity                    │
│         • TTL and size limits                        │
└─────────────────────────┬───────────────────────────┘
                          │ miss
┌─────────────────────────▼───────────────────────────┐
│                      Database                        │
└─────────────────────────────────────────────────────┘
```

### First-Level Cache (Session Cache)

```java
public final class SessionCache {

    private final Map<EntityKey, Object> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Class<T> type, Object id) {
        EntityKey key = new EntityKey(type, id);
        return Optional.ofNullable((T) cache.get(key));
    }

    public <T> void put(Class<T> type, Object id, T entity) {
        EntityKey key = new EntityKey(type, id);
        cache.put(key, entity);
    }

    public void evict(Class<?> type, Object id) {
        EntityKey key = new EntityKey(type, id);
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private record EntityKey(Class<?> type, Object id) {}
}

// Usage - automatic identity mapping
try (Session session = database.openSession()) {
    User user1 = session.find(User.class, userId);
    User user2 = session.find(User.class, userId);

    // Same object reference
    assert user1 == user2;
}
```

### Second-Level Cache

```java
// Cache configuration
@NullMarked
public record CacheConfig(
    boolean enabled,
    Duration timeToLive,
    Duration timeToIdle,
    int maxSize,
    CacheEvictionPolicy evictionPolicy
) {
    public static CacheConfigBuilder builder() {
        return new CacheConfigBuilder();
    }
}

// Cache provider interface
public interface CacheProvider {

    <T> Optional<T> get(Class<T> type, Object id);
    <T> void put(Class<T> type, Object id, T entity, CacheConfig config);
    void evict(Class<?> type, Object id);
    void evictAll(Class<?> type);
    void clear();
    CacheStatistics getStatistics();
}

// Caffeine implementation
public final class CaffeineCache implements CacheProvider {

    private final Map<Class<?>, Cache<Object, Object>> caches = new ConcurrentHashMap<>();

    private Cache<Object, Object> getCache(Class<?> type, CacheConfig config) {
        return caches.computeIfAbsent(type, t ->
            Caffeine.newBuilder()
                .maximumSize(config.maxSize())
                .expireAfterWrite(config.timeToLive())
                .expireAfterAccess(config.timeToIdle())
                .recordStats()
                .build()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Class<T> type, Object id) {
        Cache<Object, Object> cache = caches.get(type);
        if (cache == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((T) cache.getIfPresent(id));
    }

    @Override
    public <T> void put(Class<T> type, Object id, T entity, CacheConfig config) {
        Cache<Object, Object> cache = getCache(type, config);
        cache.put(id, entity);
    }
}
```

### Cache Configuration per Entity

```yaml
# user.yaml
entity: User
table: users

cache:
  enabled: true
  region: users
  timeToLive: 1h
  timeToIdle: 15m
  maxSize: 10000

fields:
  # ...
```

Generated annotation:

```java
@NullMarked
@Entity(table = "users")
@Cacheable(region = "users", ttl = 3600, tti = 900, maxSize = 10000)
public record User(
    // ...
) {}
```

### Query Cache

```java
// Enable query caching
List<User> users = userRepository.query()
    .whereStatus(UserStatus.ACTIVE)
    .cacheable()  // Cache this query result
    .cacheRegion("active-users")
    .fetch();

// Query cache implementation
public final class QueryCache {

    private final Cache<QueryKey, Object> cache;

    public <T> Optional<List<T>> get(Query<T> query) {
        QueryKey key = QueryKey.from(query);
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    public <T> void put(Query<T> query, List<T> results) {
        QueryKey key = QueryKey.from(query);
        cache.put(key, results);
    }

    // Invalidate when entity changes
    public void invalidate(Class<?> entityType) {
        // Remove all queries involving this entity type
    }

    private record QueryKey(String sql, List<Object> parameters) {
        static QueryKey from(Query<?> query) {
            return new QueryKey(query.toSql(), query.getParameters());
        }
    }
}
```

## Entity Lifecycle

### Lifecycle States

```
           ┌──────────────────┐
           │       NEW        │  Created with new/builder
           └────────┬─────────┘
                    │ persist()
                    ▼
           ┌──────────────────┐
           │     MANAGED      │  Tracked by session
           └────────┬─────────┘
        ┌───────────┼───────────┐
        │           │           │
   detach()     remove()     close()
        │           │           │
        ▼           ▼           ▼
┌───────────┐ ┌───────────┐ ┌───────────┐
│  DETACHED │ │  REMOVED  │ │  DETACHED │
└─────┬─────┘ └───────────┘ └─────┬─────┘
      │                           │
      │         merge()           │
      └───────────────────────────┘
                    │
                    ▼
           ┌──────────────────┐
           │     MANAGED      │
           └──────────────────┘
```

### Lifecycle Callbacks

```yaml
# user.yaml
entity: User
table: users

lifecycle:
  prePersist: onPrePersist
  postPersist: onPostPersist
  preUpdate: onPreUpdate
  postUpdate: onPostUpdate
  preRemove: onPreRemove
  postRemove: onPostRemove
  postLoad: onPostLoad

fields:
  # ...
```

Generated code:

```java
@NullMarked
@Entity(table = "users")
public record User(
    UUID id,
    String email,
    Instant createdAt,
    Instant updatedAt
) {

    @PrePersist
    void onPrePersist() {
        // Called before INSERT
    }

    @PostPersist
    void onPostPersist() {
        // Called after INSERT
    }

    @PreUpdate
    void onPreUpdate() {
        // Called before UPDATE
    }

    @PostUpdate
    void onPostUpdate() {
        // Called after UPDATE
    }

    @PreRemove
    void onPreRemove() {
        // Called before DELETE
    }

    @PostRemove
    void onPostRemove() {
        // Called after DELETE
    }

    @PostLoad
    void onPostLoad() {
        // Called after SELECT
    }
}
```

### Entity Listeners

```java
// Separate listener class
@NullMarked
public class AuditListener {

    @PrePersist
    public void setCreatedAt(Object entity) {
        if (entity instanceof Auditable auditable) {
            auditable.setCreatedAt(Instant.now());
        }
    }

    @PreUpdate
    public void setUpdatedAt(Object entity) {
        if (entity instanceof Auditable auditable) {
            auditable.setUpdatedAt(Instant.now());
        }
    }
}

// Apply listener to entity
@EntityListeners(AuditListener.class)
public record User(...) {}

// Or configure globally
DatabaseConfig config = DatabaseConfig.builder()
    .addEntityListener(new AuditListener())
    .build();
```

## Transactions

### Transaction API

```java
// Programmatic transaction
try (Transaction tx = database.beginTransaction()) {
    User user = userRepository.findById(userId);
    user = user.toBuilder().status(UserStatus.ACTIVE).build();
    userRepository.save(user);

    tx.commit();
} catch (Exception e) {
    // Automatic rollback on exception
}

// Declarative transaction (via annotation)
@Transactional
public User activateUser(UUID userId) {
    User user = userRepository.findById(userId);
    user = user.toBuilder().status(UserStatus.ACTIVE).build();
    return userRepository.save(user);
}

// Read-only transaction
@Transactional(readOnly = true)
public List<User> getActiveUsers() {
    return userRepository.query()
        .whereStatus(UserStatus.ACTIVE)
        .fetch();
}
```

### Transaction Isolation Levels

```java
public enum IsolationLevel {
    READ_UNCOMMITTED,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE
}

// Specify isolation level
try (Transaction tx = database.beginTransaction(IsolationLevel.SERIALIZABLE)) {
    // Critical section
    tx.commit();
}

// Or via annotation
@Transactional(isolation = IsolationLevel.REPEATABLE_READ)
public void transferFunds(UUID from, UUID to, BigDecimal amount) {
    // ...
}
```

### Savepoints

```java
try (Transaction tx = database.beginTransaction()) {
    userRepository.save(user1);

    Savepoint sp = tx.savepoint("after_user1");

    try {
        userRepository.save(user2);
    } catch (Exception e) {
        tx.rollback(sp);  // Rollback to savepoint
    }

    tx.commit();  // user1 is saved, user2 might not be
}
```

## Optimistic Locking

### Version Field

```yaml
# user.yaml
entity: User
table: users

versioning:
  enabled: true
  field: version
  column: version

fields:
  id:
    type: UUID
    primary: true
  version:
    type: Long
  # ...
```

Generated code:

```java
@NullMarked
@Entity(table = "users")
public record User(
    UUID id,
    @Version Long version,
    String email
    // ...
) {}

// Update includes version check
// UPDATE users SET email = ?, version = version + 1
// WHERE id = ? AND version = ?

// Throws OptimisticLockException if version mismatch
```

### Handling Conflicts

```java
public User updateUserWithRetry(UUID userId, Consumer<UserBuilder> updater) {
    int maxRetries = 3;

    for (int i = 0; i < maxRetries; i++) {
        try {
            User user = userRepository.findById(userId);
            UserBuilder builder = user.toBuilder();
            updater.accept(builder);
            return userRepository.save(builder.build());
        } catch (OptimisticLockException e) {
            if (i == maxRetries - 1) {
                throw new ConcurrentModificationException(
                    "Failed after " + maxRetries + " retries", e);
            }
            // Retry with fresh data
        }
    }
    throw new IllegalStateException("Unreachable");
}
```

## Soft Delete

### Configuration

```yaml
# user.yaml
entity: User
table: users

softDelete:
  enabled: true
  column: deleted_at
  type: Instant

fields:
  # ...
  deletedAt:
    type: Instant
    column: deleted_at
    nullable: true
```

### Generated Behavior

```java
// Delete sets timestamp instead of removing
userRepository.delete(user);
// UPDATE users SET deleted_at = NOW() WHERE id = ?

// Queries automatically filter deleted records
List<User> users = userRepository.findAll();
// SELECT * FROM users WHERE deleted_at IS NULL

// Include deleted records
List<User> allUsers = userRepository.query()
    .includeDeleted()
    .fetch();
// SELECT * FROM users

// Find only deleted records
List<User> deletedUsers = userRepository.query()
    .onlyDeleted()
    .fetch();
// SELECT * FROM users WHERE deleted_at IS NOT NULL

// Restore soft-deleted record
User restored = userRepository.restore(userId);
// UPDATE users SET deleted_at = NULL WHERE id = ?

// Permanently delete
userRepository.purge(userId);
// DELETE FROM users WHERE id = ?
```

## Related Documents

- [00-overview.md](00-overview.md) - Architecture overview
- [01-yaml-schema.md](01-yaml-schema.md) - Relationship definitions
- [03-query-api.md](03-query-api.md) - Query with relationships
