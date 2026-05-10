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
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

import java.sql.*;
import java.time.Instant;
import java.util.*;
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
	private final List<RegisteredMigration> registeredMigrations = Lists.newArrayList();
	
	private SqlMigrationRunner(@NonNull SqlMigrationContext context, @NonNull SqlMigrationStore store) {
		this.context = Objects.requireNonNull(context, "Sql migration context must not be null");
		this.store = Objects.requireNonNull(store, "Sql migration store must not be null");
		this.renderer = new SqlMigrationRenderer(context.dialect());
	}
	
	public static @NonNull SqlMigrationRunner of(@NonNull SqlDatabase database, @NonNull SqlMigrationStore store) throws SqlException {
		Objects.requireNonNull(database, "Sql database must not be null");
		Objects.requireNonNull(store, "Sql migration store must not be null");
		SqlMigrationContext context = new DatabaseMigrationContext(database);
		store.initialize();
		return new SqlMigrationRunner(context, store);
	}
	
	public void register(@NonNull SqlMigration migration) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		long checksum = this.computeChecksum(migration);
		this.registeredMigrations.add(new RegisteredMigration(migration, checksum));
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
		Set<String> appliedVersions = applied.stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.map(info -> info.version().toString())
			.collect(Collectors.toSet());
		
		for (RegisteredMigration registered : this.registeredMigrations) {
			if (!appliedVersions.contains(registered.migration().version().toString())) {
				this.applyMigration(registered);
			}
		}
	}
	
	public void migrateTo(@NonNull Version targetVersion) throws SqlException {
		Objects.requireNonNull(targetVersion, "Sql target version must not be null");
		List<SqlMigrationInfo> applied = this.store.loadAll();
		Set<String> appliedVersions = applied.stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.map(info -> info.version().toString())
			.collect(Collectors.toSet());
		
		for (RegisteredMigration registered : this.registeredMigrations) {
			if (registered.migration().version().compareTo(targetVersion) > 0) {
				break;
			}
			if (!appliedVersions.contains(registered.migration().version().toString())) {
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
			.toList();
		
		List<SqlMigrationInfo> toRollback = applied.subList(Math.max(0, applied.size() - count), applied.size());
		List<SqlMigrationInfo> reversed = Lists.newArrayList(toRollback);
		Collections.reverse(reversed);
		
		for (SqlMigrationInfo info : reversed) {
			RegisteredMigration registered = this.findRegistered(info.version());
			this.rollbackMigration(registered);
		}
	}
	
	public void rollbackTo(@NonNull Version targetVersion) throws SqlException {
		Objects.requireNonNull(targetVersion, "Sql target version must not be null");
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.filter(info -> info.version().compareTo(targetVersion) >= 0)
			.toList();
		
		List<SqlMigrationInfo> reversed = Lists.newArrayList(applied);
		Collections.reverse(reversed);
		
		for (SqlMigrationInfo info : reversed) {
			RegisteredMigration registered = this.findRegistered(info.version());
			this.rollbackMigration(registered);
		}
	}
	
	public void validate() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.toList();
		
		for (SqlMigrationInfo info : applied) {
			RegisteredMigration registered = this.findRegistered(info.version());
			if (registered.checksum() != info.checksum()) {
				throw new SqlException("Checksum mismatch for migration " + info.version() +
					": stored=" + info.checksum() + ", computed=" + registered.checksum());
			}
		}
	}
	
	public @NonNull List<SqlMigrationInfo> status() throws SqlException {
		List<SqlMigrationInfo> stored = this.store.loadAll();
		Map<String, SqlMigrationInfo> storedMap = stored.stream()
			.collect(Collectors.toMap(info -> info.version().toString(), info -> info));
		
		List<SqlMigrationInfo> result = Lists.newArrayList();
		for (RegisteredMigration registered : this.registeredMigrations) {
			String versionStr = registered.migration().version().toString();
			SqlMigrationInfo existing = storedMap.get(versionStr);
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
		Set<String> appliedVersions = applied.stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.map(info -> info.version().toString())
			.collect(Collectors.toSet());
		
		List<SqlRendered> results = Lists.newArrayList();
		for (RegisteredMigration registered : this.registeredMigrations) {
			if (!appliedVersions.contains(registered.migration().version().toString())) {
				results.addAll(this.renderMigrationUp(registered.migration()));
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
		RegisteredMigration registered = this.findRegistered(last.version());
		return this.renderMigrationDown(registered.migration());
	}
	
	private void applyMigration(@NonNull RegisteredMigration registered) throws SqlException {
		List<SqlRendered> statements = this.renderMigrationUp(registered.migration());
		this.context.execute(statements);
		SqlMigrationInfo info = new SqlMigrationInfo(
			registered.migration().version(),
			registered.migration().description(),
			SqlMigrationStatus.APPLIED,
			Instant.now(),
			registered.checksum()
		);
		this.store.save(info);
	}
	
	private void rollbackMigration(@NonNull RegisteredMigration registered) throws SqlException {
		List<SqlRendered> statements = this.renderMigrationDown(registered.migration());
		this.context.execute(statements);
		this.store.update(registered.migration().version(), SqlMigrationStatus.ROLLED_BACK);
	}
	
	private @NonNull List<SqlRendered> renderMigrationUp(@NonNull SqlMigration migration) throws SqlException {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(this.context);
		migration.up(builder);
		return this.renderer.render(builder.getOperations());
	}
	
	private @NonNull List<SqlRendered> renderMigrationDown(@NonNull SqlMigration migration) throws SqlException {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(this.context);
		migration.down(builder);
		return this.renderer.render(builder.getOperations());
	}
	
	private long computeChecksum(@NonNull SqlMigration migration) throws SqlException {
		List<SqlRendered> rendered = this.renderMigrationUp(migration);
		long hash = 0;
		for (SqlRendered statement : rendered) {
			hash = 31 * hash + statement.sql().hashCode();
			for (Object param : statement.parameters()) {
				hash = 31 * hash + Objects.hashCode(param);
			}
		}
		return hash;
	}
	
	private @NonNull RegisteredMigration findRegistered(@NonNull Version version) throws SqlException {
		for (RegisteredMigration registered : this.registeredMigrations) {
			if (registered.migration().version().equals(version)) {
				return registered;
			}
		}
		throw new SqlException("No registered migration found for version " + version);
	}
	
	private record RegisteredMigration(@NonNull SqlMigration migration, long checksum) {}
	
	private record DatabaseMigrationContext(SqlDatabase database) implements SqlMigrationContext {
		
		private DatabaseMigrationContext(@NonNull SqlDatabase database) {
			this.database = Objects.requireNonNull(database, "Sql database must not be null");
		}
		
		@Override
		public @NonNull SqlDialect dialect() {
			return this.database.getDialect();
		}
		
		@Override
		public <E> @NonNull SqlQueryProvider<E> queryProvider(@NonNull SqlTable<E> table) {
			return this.database.from(table);
		}
		
		@Override
		public void execute(@NonNull List<SqlRendered> statements) throws SqlException {
			Objects.requireNonNull(statements, "Sql rendered statements must not be null");
			try (Connection connection = this.database.getDataSource().getConnection()) {
				connection.setAutoCommit(false);
				try {
					for (SqlRendered rendered : statements) {
						for (String sql : rendered.statements()) {
							if (sql.isEmpty()) {
								continue;
							}
							try (PreparedStatement stmt = connection.prepareStatement(sql)) {
								List<Object> params = rendered.parameters();
								for (int i = 0; i < params.size(); i++) {
									stmt.setObject(i + 1, params.get(i));
								}
								stmt.execute();
							}
						}
					}
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					throw e;
				}
			} catch (SQLException e) {
				throw new SqlException("Failed to execute migration statements", e);
			}
		}
	}
}
