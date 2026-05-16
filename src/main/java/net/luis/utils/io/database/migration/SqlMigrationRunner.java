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

package net.luis.utils.io.database.migration;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.store.SqlMigrationStore;
import net.luis.utils.io.database.migration.store.SqlMigrationTableStore;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlMigrationRunner {
	
	private final SqlMigrationContext context;
	private final SqlMigrationStore store;
	private final SqlMigrationRenderer renderer;
	private final List<RegisteredSqlMigration> registeredMigrations = new java.util.concurrent.CopyOnWriteArrayList<>();
	
	private SqlMigrationRunner(@NonNull SqlMigrationContext context, @NonNull SqlMigrationStore store) {
		this.context = Objects.requireNonNull(context, "Sql migration context must not be null");
		this.store = Objects.requireNonNull(store, "Sql migration store must not be null");
		this.renderer = new SqlMigrationRenderer(context.dialect());
	}
	
	public static @NonNull SqlMigrationRunner of(@NonNull SqlDatabase database) throws SqlException {
		return of(database, new SqlMigrationTableStore(database.getDataSource(), database.getDialect()));
	}
	
	public static @NonNull SqlMigrationRunner of(@NonNull SqlDatabase database, @NonNull SqlMigrationStore store) throws SqlException {
		Objects.requireNonNull(database, "Sql database must not be null");
		Objects.requireNonNull(store, "Sql migration store must not be null");
		
		SqlMigrationContext context = new SqlDatabaseMigrationContext(database);
		store.initialize();
		return new SqlMigrationRunner(context, store);
	}
	
	public void register(@NonNull SqlMigration migration) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		
		for (RegisteredSqlMigration existing : this.registeredMigrations) {
			if (existing.migration().version().equals(migration.version())) {
				throw new SqlException("Duplicate migration version: " + migration.version());
			}
		}
		
		long checksum = this.computeChecksum(migration);
		this.registeredMigrations.add(new RegisteredSqlMigration(migration, checksum));
		this.registeredMigrations.sort(Comparator.comparing(m -> m.migration().version()));
	}
	
	public void register(@NonNull List<SqlMigration> migrations) throws SqlException {
		Objects.requireNonNull(migrations, "Sql migrations must not be null");
		
		for (SqlMigration migration : migrations) {
			this.register(migration);
		}
	}
	
	public void migrate() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll();
		Set<Version> appliedVersions = applied.stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.map(SqlMigrationInfo::version)
			.collect(Collectors.toSet());
		
		for (RegisteredSqlMigration registered : this.registeredMigrations) {
			if (!appliedVersions.contains(registered.migration().version())) {
				this.applyMigration(registered);
			}
		}
	}
	
	public void migrateTo(@NonNull Version targetVersion) throws SqlException {
		Objects.requireNonNull(targetVersion, "Sql target version must not be null");
		List<SqlMigrationInfo> applied = this.store.loadAll();
		Set<Version> appliedVersions = applied.stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.map(SqlMigrationInfo::version)
			.collect(Collectors.toSet());
		
		for (RegisteredSqlMigration registered : this.registeredMigrations) {
			if (registered.migration().version().compareTo(targetVersion) > 0) {
				break;
			}
			
			if (!appliedVersions.contains(registered.migration().version())) {
				this.applyMigration(registered);
			}
		}
	}
	
	public void rollback() throws SqlException {
		this.rollback(1);
	}
	
	public void rollback(int count) throws SqlException {
		if (count < 1) {
			throw new IllegalArgumentException("Rollback count must be at least 1");
		}
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.sorted(Comparator.comparing(SqlMigrationInfo::appliedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
			.toList();
		
		List<SqlMigrationInfo> toRollback = applied.subList(Math.max(0, applied.size() - count), applied.size());
		List<SqlMigrationInfo> reversed = Lists.newArrayList(toRollback);
		Collections.reverse(reversed);
		
		for (SqlMigrationInfo info : reversed) {
			RegisteredSqlMigration registered = this.findRegistered(info.version());
			this.rollbackMigration(registered);
		}
	}
	
	public void rollbackTo(@NonNull Version targetVersion) throws SqlException {
		Objects.requireNonNull(targetVersion, "Sql target version must not be null");
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.filter(info -> info.version().compareTo(targetVersion) > 0)
			.sorted(Comparator.comparing(SqlMigrationInfo::appliedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
			.toList();
		
		List<SqlMigrationInfo> reversed = Lists.newArrayList(applied);
		Collections.reverse(reversed);
		
		for (SqlMigrationInfo info : reversed) {
			RegisteredSqlMigration registered = this.findRegistered(info.version());
			this.rollbackMigration(registered);
		}
	}
	
	public void validate() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.toList();
		
		for (SqlMigrationInfo info : applied) {
			RegisteredSqlMigration registered = this.findRegistered(info.version());
			if (registered.checksum() != info.checksum()) {
				throw new SqlException("Checksum mismatch for migration " + info.version() + ": stored=" + info.checksum() + ", computed=" + registered.checksum());
			}
		}
	}
	
	public @NonNull List<SqlMigrationInfo> status() throws SqlException {
		List<SqlMigrationInfo> stored = this.store.loadAll();
		Map<Version, SqlMigrationInfo> storedMap = stored.stream()
			.collect(Collectors.toMap(SqlMigrationInfo::version, Function.identity()));
		
		List<SqlMigrationInfo> result = Lists.newArrayList();
		for (RegisteredSqlMigration registered : this.registeredMigrations) {
			SqlMigrationInfo existing = storedMap.get(registered.migration().version());
			
			if (existing != null) {
				result.add(existing);
			} else {
				result.add(new SqlMigrationInfo(
					registered.migration().version(),
					registered.migration().description(),
					SqlMigrationStatus.PENDING,
					null,
					registered.checksum()
				));
			}
		}
		return List.copyOf(result);
	}
	
	public @NonNull List<SqlRendered> dryRun() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll();
		Set<Version> appliedVersions = applied.stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.map(SqlMigrationInfo::version)
			.collect(Collectors.toSet());
		
		List<SqlRendered> results = Lists.newArrayList();
		for (RegisteredSqlMigration registered : this.registeredMigrations) {
			if (!appliedVersions.contains(registered.migration().version())) {
				results.addAll(this.renderMigration(registered.migration(), true, true));
			}
		}
		return List.copyOf(results);
	}
	
	public @NonNull List<SqlRendered> dryRunRollback() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.toList();
		
		if (applied.isEmpty()) {
			return List.of();
		}
		
		SqlMigrationInfo last = applied.getLast();
		RegisteredSqlMigration registered = this.findRegistered(last.version());
		return this.renderMigration(registered.migration(), false, true);
	}
	
	private void applyMigration(@NonNull RegisteredSqlMigration registered) throws SqlException {
		Objects.requireNonNull(registered, "Registered sql migration must not be null");
		
		SqlMigrationInfo info = new SqlMigrationInfo(
			registered.migration().version(),
			registered.migration().description(),
			SqlMigrationStatus.APPLIED,
			Instant.now(),
			registered.checksum()
		);
		this.context.executeAndSave(this.renderMigrationUp(registered.migration()), this.store, info);
	}
	
	private void rollbackMigration(@NonNull RegisteredSqlMigration registered) throws SqlException {
		Objects.requireNonNull(registered, "Registered sql migration must not be null");
		
		this.context.executeAndUpdate(this.renderMigrationDown(registered.migration()), this.store, registered.migration().version(), SqlMigrationStatus.ROLLED_BACK);
	}
	
	private @NonNull List<SqlRendered> renderMigrationUp(@NonNull SqlMigration migration) throws SqlException {
		return this.renderMigration(migration, true, false);
	}
	
	private @NonNull List<SqlRendered> renderMigrationDown(@NonNull SqlMigration migration) throws SqlException {
		return this.renderMigration(migration, false, false);
	}
	
	private @NonNull List<SqlRendered> renderMigration(@NonNull SqlMigration migration, boolean up, boolean dryRun) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		
		SqlMigrationBuilder builder = new SqlMigrationBuilder(this.context, dryRun);
		if (up) {
			migration.up(builder);
		} else {
			migration.down(builder);
		}
		return this.renderer.render(builder.getOperations());
	}
	
	private long computeChecksum(@NonNull SqlMigration migration) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		
		long hash = 0;
		for (SqlRendered statement : this.renderMigration(migration, true, true)) {
			hash = 31 * hash + statement.sql().hashCode();
			for (Object param : statement.parameters()) {
				hash = 31 * hash + Objects.hashCode(param);
			}
		}
		return hash;
	}
	
	private @NonNull RegisteredSqlMigration findRegistered(@NonNull Version version) throws SqlException {
		Objects.requireNonNull(version, "Sql migration version must not be null");
		
		for (RegisteredSqlMigration registered : this.registeredMigrations) {
			if (registered.migration().version().equals(version)) {
				return registered;
			}
		}
		throw new SqlException("No registered migration found for version " + version);
	}
	
	private record RegisteredSqlMigration(@NonNull SqlMigration migration, long checksum) {}
	
	private record SqlDatabaseMigrationContext(@NonNull SqlDatabase database) implements SqlMigrationContext {
		
		private SqlDatabaseMigrationContext {
			Objects.requireNonNull(database, "Sql database must not be null");
		}
		
		@Override
		public @NonNull SqlDialect dialect() {
			return this.database.getDialect();
		}
		
		@Override
		public <E> @NonNull SqlQueryProvider<E> from(@NonNull SqlTable<E> table) throws SqlException {
			Objects.requireNonNull(table, "Sql table must not be null");
			
			return this.database.from(table);
		}
		
		private void executeStatements(@NonNull Connection connection, @NonNull List<SqlRendered> statements) throws SQLException, SqlException {
			Objects.requireNonNull(connection, "Sql connection must not be null");
			Objects.requireNonNull(statements, "Sql rendered statements must not be null");
			
			for (SqlRendered rendered : statements) {
				String sql = rendered.sql();
				if (sql.isEmpty()) {
					continue;
				}
				
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					List<Pair<SqlType<?>, Object>> params = rendered.parameters();
					for (int i = 0; i < params.size(); i++) {
						Pair<SqlType<?>, Object> pair = params.get(i);
						SqlType.setUnsafe(pair.getFirst(), this.database.getDialect(), statement, i + 1, pair.getSecond());
					}
					statement.execute();
				}
			}
		}
		
		@Override
		public void executeAndSave(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull SqlMigrationInfo info) throws SqlException {
			Objects.requireNonNull(statements, "Sql rendered statements must not be null");
			Objects.requireNonNull(store, "Sql migration store must not be null");
			Objects.requireNonNull(info, "Sql migration info must not be null");
			
			try (Connection connection = this.database.getDataSource().getConnection()) {
				connection.setAutoCommit(false);
				try {
					this.executeStatements(connection, statements);
					store.save(connection, info);
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					throw e;
				}
			} catch (SQLException e) {
				throw new SqlException("Failed to execute migration with store update", e);
			}
		}
		
		@Override
		public void executeAndUpdate(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
			Objects.requireNonNull(statements, "Sql rendered statements must not be null");
			Objects.requireNonNull(store, "Sql migration store must not be null");
			Objects.requireNonNull(version, "Sql migration version must not be null");
			Objects.requireNonNull(status, "Sql migration status must not be null");
			
			try (Connection connection = this.database.getDataSource().getConnection()) {
				connection.setAutoCommit(false);
				try {
					this.executeStatements(connection, statements);
					store.update(connection, version, status);
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					throw e;
				}
			} catch (SQLException e) {
				throw new SqlException("Failed to execute migration with store update", e);
			}
		}
	}
}
