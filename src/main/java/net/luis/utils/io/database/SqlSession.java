/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.io.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.*;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionStateException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.query.crud.SqlUpdateQuery;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.transaction.SqlTransaction;
import net.luis.utils.io.database.transaction.SqlTransactionListener;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an active session against a {@link SqlDatabase}.<br>
 * A session is the central entry point for working with the database, it provides access to
 * schema operations, table-level operations through {@link #table(SqlTable)} and query building
 * through {@link #from(SqlTable)}.<br>
 * <p>
 *     In addition to plain queries, a session maintains an identity map of tracked
 *     {@link SqlAuditMetadata audit metadata} keyed by {@link SqlEntityKey}, which is used to
 *     perform optimistic-locking aware {@link #update(SqlTable, Object) updates} of audited entities.<br>
 *     A session may be bound to a {@link SqlTransaction}, in which case the tracked state is
 *     snapshotted before mutations and restored on rollback.
 * </p>
 * Sessions are short-lived and must be {@link #close() closed} after use to release their state.<br>
 *
 * @see SqlDatabase
 * @see SqlProvider
 * @see SqlTransaction
 *
 * @author Luis-St
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class SqlSession implements SqlProvider, AutoCloseable {
	
	/**
	 * The database this session belongs to.<br>
	 */
	private final SqlDatabase database;
	/**
	 * The source used to open connections to the database.<br>
	 */
	private final SqlConnectionSource connectionSource;
	/**
	 * The dialect used to render sql statements, taken from the database.<br>
	 */
	private final SqlDialect dialect;
	/**
	 * The timeout applied to queries executed through this session.<br>
	 */
	private final Duration queryTimeout;
	/**
	 * The audit user provider overriding the database default, or {@code null} to use the database default.<br>
	 */
	private final @Nullable SqlAuditUserProvider override;
	/**
	 * The transaction this session is bound to, or {@code null} if the session is not transactional.<br>
	 */
	private final @Nullable SqlTransaction transaction;
	/**
	 * The identity map of tracked audit metadata keyed by entity key.<br>
	 */
	private final Map<SqlEntityKey, SqlAuditMetadata> tracked = Maps.newHashMap();
	/**
	 * The snapshots of tracked audit metadata taken before mutations, used to restore state on rollback.<br>
	 */
	private final Map<SqlEntityKey, Optional<SqlAuditMetadata>> snapshots = Maps.newHashMap();
	/**
	 * Whether this session has been closed.<br>
	 */
	private boolean closed;
	
	/**
	 * Constructs a new non-transactional sql session with the given database, connection source, query timeout and audit user override.<br>
	 *
	 * @param database The database this session belongs to
	 * @param connectionSource The source used to open connections
	 * @param queryTimeout The timeout applied to queries
	 * @param override The audit user provider overriding the database default, or null to use the database default
	 * @throws NullPointerException If the database, connection source or query timeout is null
	 */
	SqlSession(@NonNull SqlDatabase database, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout, @Nullable SqlAuditUserProvider override) {
		this(database, connectionSource, queryTimeout, override, null);
	}
	
	/**
	 * Constructs a new sql session with the given database, connection source, query timeout, audit user override and transaction.<br>
	 * If a transaction is given, the session registers a listener that clears its snapshots on commit and
	 * restores the tracked state from its snapshots on rollback.<br>
	 *
	 * @param database The database this session belongs to
	 * @param connectionSource The source used to open connections
	 * @param queryTimeout The timeout applied to queries
	 * @param override The audit user provider overriding the database default, or null to use the database default
	 * @param transaction The transaction this session is bound to, or null for a non-transactional session
	 * @throws NullPointerException If the database, connection source or query timeout is null
	 */
	SqlSession(
		@NonNull SqlDatabase database,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@Nullable SqlAuditUserProvider override,
		@Nullable SqlTransaction transaction
	) {
		this.database = Objects.requireNonNull(database, "Sql database must not be null");
		this.connectionSource = Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		this.dialect = database.getDialect();
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.override = override;
		this.transaction = transaction;
		
		if (transaction != null) {
			transaction.addListener(new SqlTransactionListener() {
				
				@Override
				public void afterCommit() {
					SqlSession.this.snapshots.clear();
				}
				
				@Override
				public void afterRollback() {
					for (Map.Entry<SqlEntityKey, Optional<SqlAuditMetadata>> entry : SqlSession.this.snapshots.entrySet()) {
						if (entry.getValue().isPresent()) {
							SqlSession.this.tracked.put(entry.getKey(), entry.getValue().get());
						} else {
							SqlSession.this.tracked.remove(entry.getKey());
						}
					}
					SqlSession.this.snapshots.clear();
				}
			});
		}
	}
	
	//region Static helper methods
	
	/**
	 * Builds a condition matching the primary key of the given entity in the given table.<br>
	 * If the primary key consists of multiple columns, the individual conditions are combined with a logical conjunction.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity whose primary key should be matched
	 * @param <E> The type of the entity
	 * @return The condition matching the primary key of the entity
	 * @throws NullPointerException If the table or entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 */
	@SuppressWarnings("unchecked")
	private static <E> @NonNull SqlCondition primaryKeyCondition(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		List<SqlColumn<E, ?>> primaryKey = table.primaryKeyColumns();
		if (primaryKey.isEmpty()) {
			throw new SqlIncompletePrimaryKeyException("Table '" + table.name() + "' has no primary key columns");
		}
		
		List<SqlCondition> conditions = Lists.newArrayListWithCapacity(primaryKey.size());
		for (SqlColumn<E, ?> column : primaryKey) {
			Object value = column.getter().apply(entity);
			if (value == null) {
				throw new SqlIncompletePrimaryKeyException("Primary key column '" + column.name() + "' of table '" + table.name() + "' is null");
			}
			
			SqlColumn<E, Object> typed = (SqlColumn<E, Object>) column;
			conditions.add(Sql.equalTo(typed, value));
		}
		return conditions.size() == 1 ? conditions.getFirst() : SqlCondition.allOf(conditions);
	}
	
	/**
	 * Applies the value of the given column read from the given entity to the given update query.<br>
	 *
	 * @param query The update query to apply the column value to
	 * @param column The column whose value should be applied
	 * @param entity The entity to read the column value from
	 * @param <E> The type of the entity
	 * @param <V> The type of the column value
	 * @return The update query with the column value applied
	 * @throws NullPointerException If the query, column or entity is null
	 */
	private static <E, V> @NonNull SqlUpdateQuery<E> applyColumn(@NonNull SqlUpdateQuery<E> query, @NonNull SqlColumn<E, V> column, @NonNull E entity) {
		Objects.requireNonNull(query, "Sql update query must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		return query.set(column, column.getter().apply(entity));
	}
	
	/**
	 * Creates a synthetic audit column with the given name and type for the given table.<br>
	 * The column is only used to render audit related update assignments and never reads a value from an entity.<br>
	 *
	 * @param table The table the column belongs to
	 * @param name The name of the audit column
	 * @param type The type of the audit column
	 * @param <E> The type of the entity
	 * @param <V> The type of the column value
	 * @return The newly created audit column
	 * @throws NullPointerException If the table, name or type is null
	 */
	@SuppressWarnings("ReturnOfNull")
	private static <E, V> @NonNull SqlColumn<E, V> auditColumn(@NonNull SqlTable<E> table, @NonNull String name, @NonNull SqlType<V> type) {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(name, "Audit column name must not be null");
		Objects.requireNonNull(type, "Audit column type must not be null");
		
		return new SqlColumn<>(table, name, 1, type, entity -> null, true, Optional.empty(), false, false, false, Optional.empty(), List.of());
	}
	//endregion
	
	//region Helper methods
	
	/**
	 * Ensures that this session is still usable for operations.<br>
	 * @throws SqlTransactionStateException If the session is closed or the bound transaction is no longer active
	 */
	private void ensureActive() throws SqlTransactionStateException {
		if (this.closed) {
			throw new SqlTransactionStateException("Sql session is closed");
		}
		
		if (this.transaction != null && !this.transaction.isActive()) {
			throw new SqlTransactionStateException("The sql transaction bound to this session is no longer active");
		}
	}
	
	/**
	 * Creates a query provider for the given table bound to this session.<br>
	 * The audit user override of this session is used if present, otherwise the database default is used.<br>
	 *
	 * @param table The table to create the query provider for
	 * @param <E> The type of the entity
	 * @return The newly created query provider
	 * @throws NullPointerException If the table is null
	 */
	private <E> @NonNull SqlQueryProvider<E> provider(@NonNull SqlTable<E> table) {
		SqlAuditUserProvider userProvider = this.override != null ? this.override : this.database.getAuditUserProvider();
		return new SqlQueryProvider<>(table, this.dialect, this.connectionSource, this.queryTimeout, userProvider, this);
	}
	
	/**
	 * Snapshots the currently tracked audit metadata for the given key before it is mutated.<br>
	 * The snapshot is only taken if this session is transactional and no snapshot exists yet for the key,
	 * so that the tracked state can be restored if the transaction is rolled back.<br>
	 *
	 * @param key The entity key whose tracked metadata should be snapshotted
	 * @throws NullPointerException If the key is null
	 */
	private void snapshotBeforeMutate(@NonNull SqlEntityKey key) {
		Objects.requireNonNull(key, "Sql entity key must not be null");
		
		if (this.transaction != null && !this.snapshots.containsKey(key)) {
			this.snapshots.put(key, Optional.ofNullable(this.tracked.get(key)));
		}
	}
	//endregion
	
	@Override
	public void createSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		this.ensureActive();
		
		try (SqlConnectionHandle handle = this.connectionSource.open(); Statement statement = handle.connection().createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, false).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public void createSchemaIfNotExists(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		this.ensureActive();
		
		try (SqlConnectionHandle handle = this.connectionSource.open(); Statement statement = handle.connection().createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, true).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public boolean existsSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		this.ensureActive();
		
		try (SqlConnectionHandle handle = this.connectionSource.open(); ResultSet resultSet = handle.connection().getMetaData().getSchemas()) {
			while (resultSet.next()) {
				if (name.equals(resultSet.getString("TABLE_SCHEM"))) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			throw new SqlException("Failed to check if sql schema '" + name + "' exists", e);
		}
	}
	
	@Override
	public void dropSchema(@NonNull String name, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		this.ensureActive();
		
		try (SqlConnectionHandle handle = this.connectionSource.open(); Statement statement = handle.connection().createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderDropSchema(name, false, cascade).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to drop sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public @NonNull <T> SqlTableProvider<T> table(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		this.ensureActive();
		
		return new SqlTableProvider<>(table, this.dialect, this.connectionSource, this.queryTimeout);
	}
	
	@Override
	public @NonNull <T> SqlQueryProvider<T> from(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		this.ensureActive();
		return this.provider(table);
	}
	
	/**
	 * Extracts the entity key identifying the given entity within the given table.<br>
	 * The key is built from the entity type and either the single primary key value or the list of
	 * primary key values for composite primary keys.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity whose key should be extracted
	 * @param <E> The type of the entity
	 * @return The entity key identifying the entity
	 * @throws NullPointerException If the table or entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 */
	private <E> @NonNull SqlEntityKey extractKey(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		List<SqlColumn<E, ?>> primaryKey = table.primaryKeyColumns();
		if (primaryKey.isEmpty()) {
			throw new SqlIncompletePrimaryKeyException("Sql table '" + table.name() + "' has no primary key columns");
		}
		
		if (primaryKey.size() == 1) {
			Object value = primaryKey.getFirst().getter().apply(entity);
			if (value == null) {
				throw new SqlIncompletePrimaryKeyException("Sql primary key column '" + primaryKey.getFirst().name() + "' of table '" + table.name() + "' is null");
			}
			
			return new SqlEntityKey(table.type(), value);
		}
		
		List<Object> values = Lists.newArrayListWithCapacity(primaryKey.size());
		for (SqlColumn<E, ?> column : primaryKey) {
			Object value = column.getter().apply(entity);
			if (value == null) {
				throw new SqlIncompletePrimaryKeyException("Sql primary key column '" + column.name() + "' of table '" + table.name() + "' is null");
			}
			
			values.add(value);
		}
		return new SqlEntityKey(table.type(), List.copyOf(values));
	}
	
	/**
	 * Tracks the given entity in this session using the given audit metadata.<br>
	 * Tracking an entity is a prerequisite for performing optimistic-locking aware updates through {@link #update(SqlTable, Object)}.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity to track
	 * @param metadata The audit metadata to associate with the entity
	 * @param <E> The type of the entity
	 * @throws NullPointerException If the table, entity or metadata is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 */
	public <E> void track(@NonNull SqlTable<E> table, @NonNull E entity, @NonNull SqlAuditMetadata metadata) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		Objects.requireNonNull(metadata, "Sql audit metadata must not be null");
		
		this.tracked.put(this.extractKey(table, entity), metadata);
	}
	
	/**
	 * Attaches the given audited entity to this session for tracking.<br>
	 * The wrapped entity is tracked together with its audit metadata, this is a convenience method for {@link #track(SqlTable, Object, SqlAuditMetadata)}.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param audited The audited entity to attach
	 * @param <E> The type of the entity
	 * @throws NullPointerException If the table or audited entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 */
	public <E> void attach(@NonNull SqlTable<E> table, @NonNull SqlAudited<E> audited) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(audited, "Audited entity must not be null");
		
		this.tracked.put(this.extractKey(table, audited.entity()), audited.audit());
	}
	
	/**
	 * Removes the given entity from the tracked state of this session.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity to evict
	 * @param <E> The type of the entity
	 * @throws NullPointerException If the table or entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 */
	public <E> void evict(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		this.tracked.remove(this.extractKey(table, entity));
	}
	
	/**
	 * Removes the entity identified by the given key from the tracked state of this session.<br>
	 * @param key The entity key to evict, a {@code null} key is silently ignored
	 */
	public void evict(@Nullable SqlEntityKey key) {
		this.tracked.remove(key);
	}
	
	/**
	 * Clears the entire tracked state of this session.<br>
	 */
	public void clear() {
		this.tracked.clear();
	}
	
	/**
	 * Checks whether the given entity is currently tracked by this session.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity to check
	 * @param <E> The type of the entity
	 * @return {@code true} if the entity is tracked, {@code false} otherwise
	 * @throws NullPointerException If the table or entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 */
	public <E> boolean isTracked(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		return this.tracked.containsKey(this.extractKey(table, entity));
	}
	
	/**
	 * Returns the number of entities currently tracked by this session.<br>
	 * @return The number of tracked entities
	 */
	public int trackedCount() {
		return this.tracked.size();
	}
	
	/**
	 * Reloads the audit metadata of the given entity from the database and refreshes the tracked state.<br>
	 * The entity is selected by its primary key with its audit metadata, and the freshly loaded metadata
	 * replaces any previously tracked metadata for the entity.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity whose audit metadata should be reloaded
	 * @param <E> The type of the entity
	 * @return The reloaded audit metadata
	 * @throws NullPointerException If the table or entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 * @throws SqlException If the session is closed, the bound transaction is no longer active or the reload fails
	 */
	public <E> @NonNull SqlAuditMetadata reload(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		this.ensureActive();
		
		SqlAudited<E> reloaded = this.provider(table).select().where(primaryKeyCondition(table, entity)).withAudit().fetchOne();
		SqlEntityKey key = this.extractKey(table, reloaded.entity());
		this.tracked.put(key, reloaded.audit());
		return reloaded.audit();
	}
	
	/**
	 * Performs an optimistic-locking aware update of the given entity using the metadata tracked by this session.<br>
	 * The version of the tracked metadata is used to lock the row, so the entity must have been loaded through
	 * this session or attached via {@link #attach(SqlTable, SqlAudited)} beforehand.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity to update
	 * @param <E> The type of the entity
	 * @return The refreshed audit metadata after the update
	 * @throws NullPointerException If the table or entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 * @throws SqlUntrackedEntityException If the entity is not tracked by this session or its tracked metadata has no version
	 * @throws SqlException If the session is closed, the bound transaction is no longer active, the table is not audited, the version no longer matches or the update fails
	 */
	public <E> @NonNull SqlAuditMetadata update(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		this.ensureActive();
		
		SqlEntityKey key = this.extractKey(table, entity);
		SqlAuditMetadata trackedMetadata = this.tracked.get(key);
		if (trackedMetadata == null || trackedMetadata.version().isEmpty()) {
			throw new SqlUntrackedEntityException("Entity of sql table '" + table.name() + "' is not tracked by this session, load it through the session or attach() it first");
		}
		return this.update(table, entity, key, trackedMetadata, trackedMetadata.version().getAsLong());
	}
	
	/**
	 * Performs an optimistic-locking aware update of the given audited entity.<br>
	 * The version carried by the audited entity is used to lock the row, so the entity does not need to be tracked
	 * by this session beforehand.<br>
	 *
	 * @param table The table the entity belongs to
	 * @param audited The audited entity to update
	 * @param <E> The type of the entity
	 * @return The refreshed audit metadata after the update
	 * @throws NullPointerException If the table or audited entity is null
	 * @throws SqlIncompletePrimaryKeyException If the table has no primary key columns or a primary key value is null
	 * @throws SqlUntrackedEntityException If the audited entity has no version to lock against
	 * @throws SqlException If the session is closed, the bound transaction is no longer active, the table is not audited, the version no longer matches or the update fails
	 */
	public <E> @NonNull SqlAuditMetadata update(@NonNull SqlTable<E> table, @NonNull SqlAudited<E> audited) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(audited, "Audited entity must not be null");
		this.ensureActive();
		
		long expectedVersion = audited.version().orElseThrow(() -> new SqlUntrackedEntityException("Supplied audited entity of sql table '" + table.name() + "' has no version to lock against"));
		SqlEntityKey key = this.extractKey(table, audited.entity());
		return this.update(table, audited.entity(), key, audited.audit(), expectedVersion);
	}
	
	/**
	 * Performs the actual optimistic-locking aware update of the given entity.<br>
	 * <p>
	 *     All non primary key columns are written from the entity, the version column is incremented and the
	 *     updated-at and updated-by audit columns are set, all guarded by a condition matching the primary key
	 *     and the expected version.<br>
	 *     If the audit value source is the database, the resulting metadata is reloaded, otherwise the base
	 *     metadata is bumped locally and stored in the tracked state.
	 * </p>
	 *
	 * @param table The table the entity belongs to
	 * @param entity The entity to update
	 * @param key The entity key identifying the entity
	 * @param baseMetadata The audit metadata to bump if the values are sourced locally
	 * @param expectedVersion The version expected to be present in the database
	 * @param <E> The type of the entity
	 * @return The refreshed audit metadata after the update
	 * @throws NullPointerException If the table, entity, key or base metadata is null
	 * @throws IllegalArgumentException If the expected version is negative
	 * @throws SqlUntrackedEntityException If the table is not audited
	 * @throws SqlOptimisticLockException If the update matched no rows for the expected version
	 * @throws SqlException If the update fails
	 */
	private <E> @NonNull SqlAuditMetadata update(@NonNull SqlTable<E> table, @NonNull E entity, @NonNull SqlEntityKey key, @NonNull SqlAuditMetadata baseMetadata, long expectedVersion) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		Objects.requireNonNull(key, "Sql entity key must not be null");
		Objects.requireNonNull(baseMetadata, "Base sql audit metadata must not be null");
		if (expectedVersion < 0) {
			throw new IllegalArgumentException("Expected version must not be negative");
		}
		
		SqlAuditConfig config = table.auditConfig().orElseThrow(() -> new SqlUntrackedEntityException("Sql table '" + table.name() + "' is not audited"));
		this.snapshotBeforeMutate(key);
		
		LocalDateTime now = LocalDateTime.now(config.clock());
		String user = (this.override != null ? this.override : this.database.getAuditUserProvider()).get().orElse(null);
		
		SqlColumn<E, Long> versionColumn = auditColumn(table, config.versionColumn(), config.versionType());
		SqlColumn<E, LocalDateTime> updatedAtColumn = auditColumn(table, config.updatedAtColumn(), config.timestampType());
		SqlColumn<E, String> updatedByColumn = auditColumn(table, config.updatedByColumn(), config.userType());
		
		SqlUpdateQuery<E> query = this.provider(table).update().where(primaryKeyCondition(table, entity).and(Sql.equalTo(versionColumn, Sql.of(expectedVersion, config.versionType()))));
		for (SqlColumn<E, ?> column : table.columns()) {
			if (!column.primaryKey()) {
				query = applyColumn(query, column, entity);
			}
		}
		query = query
			.increment(versionColumn, Sql.of(1L, config.versionType()))
			.set(updatedAtColumn, config.valueSource() == SqlAuditValueSource.DATABASE ? Sql.now(config.timestampType()) : Sql.of(now, config.timestampType()))
			.set(updatedByColumn, user);
		
		if (query.execute() == 0) {
			throw new SqlOptimisticLockException("Audited update of sql table '" + table.name() + "' matched no rows for expected version " + expectedVersion);
		}
		
		if (config.valueSource() == SqlAuditValueSource.DATABASE) {
			return this.reload(table, entity);
		}
		
		SqlAuditMetadata refreshed = baseMetadata.bumped(now, user);
		this.tracked.put(key, refreshed);
		return refreshed;
	}
	
	@Override
	public void close() {
		this.closed = true;
		this.tracked.clear();
		this.snapshots.clear();
	}
	
	/**
	 * Identifies an entity within the tracked state of a session by its type and primary key.<br>
	 * The primary key is either the single primary key value or a list of values for composite primary keys.<br>
	 *
	 * @author Luis-St
	 *
	 * @param entityType The type of the entity
	 * @param primaryKey The primary key value or list of values identifying the entity
	 */
	public record SqlEntityKey(@NonNull Class<?> entityType, @NonNull Object primaryKey) {
		
		/**
		 * Constructs a new entity key with the given entity type and primary key.<br>
		 * @throws NullPointerException If the entity type or primary key is null
		 */
		public SqlEntityKey {
			Objects.requireNonNull(entityType, "Entity type must not be null");
			Objects.requireNonNull(primaryKey, "Sql primary key must not be null");
		}
	}
}
