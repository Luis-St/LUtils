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
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("SqlSourceToSinkFlow")
public class SqlSession implements SqlProvider, AutoCloseable {
	
	private final SqlDatabase database;
	private final SqlConnectionSource connectionSource;
	private final SqlDialect dialect;
	private final Duration queryTimeout;
	private final @Nullable SqlAuditUserProvider override;
	private final @Nullable SqlTransaction transaction;
	private final Map<SqlEntityKey, SqlAuditMetadata> tracked = Maps.newHashMap();
	private final Map<SqlEntityKey, Optional<SqlAuditMetadata>> snapshots = Maps.newHashMap();
	private boolean closed;
	
	SqlSession(@NonNull SqlDatabase database, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout, @Nullable SqlAuditUserProvider override) {
		this(database, connectionSource, queryTimeout, override, null);
	}
	
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
			conditions.add(Sql.equalTo(typed, Sql.of(value, typed.type())));
		}
		return conditions.size() == 1 ? conditions.getFirst() : SqlCondition.allOf(conditions);
	}
	
	private static <E, V> @NonNull SqlUpdateQuery<E> applyColumn(@NonNull SqlUpdateQuery<E> query, @NonNull SqlColumn<E, V> column, @NonNull E entity) {
		Objects.requireNonNull(query, "Sql update query must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		return query.set(column, column.getter().apply(entity));
	}
	
	@SuppressWarnings("ReturnOfNull")
	private static  <E, V> @NonNull SqlColumn<E, V> auditColumn(@NonNull SqlTable<E> table, @NonNull String name, @NonNull SqlType<V> type) {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(name, "Audit column name must not be null");
		Objects.requireNonNull(type, "Audit column type must not be null");
		
		return new SqlColumn<>(table, name, 1, type, entity -> null, true, Optional.empty(), false, false, false, Optional.empty(), List.of());
	}
	//endregion
	
	//region Helper methods
	
	private void ensureActive() throws SqlTransactionStateException {
		if (this.closed) {
			throw new SqlTransactionStateException("Sql session is closed");
		}
		
		if (this.transaction != null && !this.transaction.isActive()) {
			throw new SqlTransactionStateException("The sql transaction bound to this session is no longer active");
		}
	}
	
	private <E> @NonNull SqlQueryProvider<E> provider(@NonNull SqlTable<E> table) {
		SqlAuditUserProvider userProvider = this.override != null ? this.override : this.database.getAuditUserProvider();
		return new SqlQueryProvider<>(table, this.dialect, this.connectionSource, this.queryTimeout, userProvider, this);
	}
	
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
	
	public <E> void track(@NonNull SqlTable<E> table, @NonNull E entity, @NonNull SqlAuditMetadata metadata) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		Objects.requireNonNull(metadata, "Sql audit metadata must not be null");
		
		this.tracked.put(this.extractKey(table, entity), metadata);
	}
	
	public <E> void attach(@NonNull SqlTable<E> table, @NonNull SqlAudited<E> audited) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(audited, "Audited entity must not be null");
		
		this.tracked.put(this.extractKey(table, audited.entity()), audited.audit());
	}
	
	public <E> void evict(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		this.tracked.remove(this.extractKey(table, entity));
	}
	
	public void evict(@Nullable SqlEntityKey key) {
		this.tracked.remove(key);
	}
	
	public void clear() {
		this.tracked.clear();
	}
	
	public <E> boolean isTracked(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlIncompletePrimaryKeyException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		
		return this.tracked.containsKey(this.extractKey(table, entity));
	}
	
	public int trackedCount() {
		return this.tracked.size();
	}
	
	public <E> @NonNull SqlAuditMetadata reload(@NonNull SqlTable<E> table, @NonNull E entity) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(entity, "Entity must not be null");
		this.ensureActive();
		
		SqlAudited<E> reloaded = this.provider(table).select().where(primaryKeyCondition(table, entity)).withAudit().fetchOne();
		SqlEntityKey key = this.extractKey(table, reloaded.entity());
		this.tracked.put(key, reloaded.audit());
		return reloaded.audit();
	}
	
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
	
	public <E> @NonNull SqlAuditMetadata update(@NonNull SqlTable<E> table, @NonNull SqlAudited<E> audited) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(audited, "Audited entity must not be null");
		this.ensureActive();
		
		long expectedVersion = audited.version().orElseThrow(() -> new SqlUntrackedEntityException("Supplied audited entity of sql table '" + table.name() + "' has no version to lock against"));
		SqlEntityKey key = this.extractKey(table, audited.entity());
		return this.update(table, audited.entity(), key, audited.audit(), expectedVersion);
	}
	
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
	
	public record SqlEntityKey(@NonNull Class<?> entityType, @NonNull Object primaryKey) {
		
		public SqlEntityKey {
			Objects.requireNonNull(entityType, "Entity type must not be null");
			Objects.requireNonNull(primaryKey, "Sql primary key must not be null");
		}
	}
}
