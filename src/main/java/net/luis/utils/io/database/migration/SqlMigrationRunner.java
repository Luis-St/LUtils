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

import com.google.common.collect.*;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlMigrationConflictException;
import net.luis.utils.io.database.exception.database.SqlMigrationExecutionException;
import net.luis.utils.io.database.migration.operation.SqlMigrationOperation;
import net.luis.utils.io.database.migration.store.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orchestrates the execution of schema migrations against a database.<br>
 * Migrations are registered with this runner and ordered by their {@link Version}, the runner then discovers which
 * migrations are still pending, applies them through their {@code up} step or rolls them back through their {@code down} step,
 * and records the resulting status in a {@link SqlMigrationStore}.<br>
 * <p>
 *     Each migration is executed atomically inside a single transaction together with the corresponding store and schema
 *     snapshot update, so that a failure leaves neither the schema nor the migration history half-applied. Dialects which
 *     implicitly commit on DDL are rejected unless the migration explicitly opts into non-atomic execution.
 * </p>
 *
 * @author Luis-St
 */
public final class SqlMigrationRunner {
	
	/**
	 * The migration context providing access to the dialect, data source and transactional execution.
	 */
	private final SqlMigrationContext context;
	/**
	 * The store which holds the applied migration history.
	 */
	private final SqlMigrationStore store;
	/**
	 * The store which holds the schema snapshots captured after each applied migration.
	 */
	private final SqlMigrationSchemaStore schemaStore;
	/**
	 * The renderer used to turn migration operations into executable sql.
	 */
	private final SqlMigrationRenderer renderer;
	/**
	 * The list of registered migrations, kept sorted by version.
	 */
	private final List<RegisteredSqlMigration> registeredMigrations = Lists.newCopyOnWriteArrayList();
	
	/**
	 * Constructs a new sql migration runner with the given context and stores.<br>
	 *
	 * @param context The migration context to use
	 * @param store The store holding the applied migration history
	 * @param schemaStore The store holding the schema snapshots
	 * @throws NullPointerException If the context, store or schema store is null
	 */
	private SqlMigrationRunner(@NonNull SqlMigrationContext context, @NonNull SqlMigrationStore store, @NonNull SqlMigrationSchemaStore schemaStore) {
		this.context = Objects.requireNonNull(context, "Sql migration context must not be null");
		this.store = Objects.requireNonNull(store, "Sql migration store must not be null");
		this.schemaStore = Objects.requireNonNull(schemaStore, "Sql migration schema store must not be null");
		this.renderer = new SqlMigrationRenderer(context.dialect());
	}
	
	/**
	 * Creates a new sql migration runner for the given database.<br>
	 * The migration history is stored in a {@link SqlMigrationTableStore} backed by the database's data source and dialect.<br>
	 *
	 * @param database The database to run migrations against
	 * @return The created migration runner
	 * @throws NullPointerException If the database is null
	 * @throws SqlException If the migration store or schema store could not be initialized
	 */
	public static @NonNull SqlMigrationRunner of(@NonNull SqlDatabase database) throws SqlException {
		return of(database, new SqlMigrationTableStore(database.getDataSource(), database.getDialect()));
	}
	
	/**
	 * Creates a new sql migration runner for the given database using the given migration store.<br>
	 * Both the given store and the internally created schema store are initialized before the runner is returned.<br>
	 *
	 * @param database The database to run migrations against
	 * @param store The store holding the applied migration history
	 * @return The created migration runner
	 * @throws NullPointerException If the database or store is null
	 * @throws SqlException If the migration store or schema store could not be initialized
	 */
	public static @NonNull SqlMigrationRunner of(@NonNull SqlDatabase database, @NonNull SqlMigrationStore store) throws SqlException {
		Objects.requireNonNull(database, "Sql database must not be null");
		Objects.requireNonNull(store, "Sql migration store must not be null");
		
		SqlMigrationContext context = new SqlDatabaseMigrationContext(database);
		store.initialize();
		SqlMigrationSchemaStore schemaStore = new SqlMigrationSchemaStore(database.getDataSource(), database.getDialect());
		schemaStore.initialize();
		return new SqlMigrationRunner(context, store, schemaStore);
	}
	
	/**
	 * Joins the given rendered sql statements into the text that is recorded alongside an applied migration.<br>
	 * Empty statements are skipped and the remaining ones are separated by {@code ";\n"}.<br>
	 *
	 * @param rendered The rendered sql statements to join
	 * @return The recorded statement text
	 * @throws NullPointerException If the rendered list is null
	 */
	private static @NonNull String joinStatements(@NonNull List<SqlRendered> rendered) {
		Objects.requireNonNull(rendered, "Sql rendered list must not be null");
		
		return rendered.stream()
			.map(SqlRendered::sql)
			.filter(sql -> !sql.isEmpty())
			.collect(Collectors.joining(";\n"));
	}
	
	/**
	 * Validates that the check constraints of the recorded tables still match the schema snapshot.<br>
	 * Only the names of the constraints are compared, their clauses are normalized differently by every engine and carry
	 * no information the names do not already carry.<br>
	 *
	 * @param version The version of the schema snapshot the live schema is compared against
	 * @param snapshot The schema snapshot recorded after the migration with the given version was applied
	 * @param live The live schema of the database
	 * @param recordedTables The names of the tables the snapshot records columns for
	 * @throws SqlMigrationConflictException If the check constraints of a recorded table have drifted
	 */
	private static void validateCheckConstraintDrift(@NonNull Version version, @NonNull SqlSchemaSnapshot snapshot, @NonNull SqlMigrationSchema live, @NonNull Set<String> recordedTables) throws SqlException {
		Map<String, List<SqlCheckConstraintInfo>> liveConstraints = live.extractCheckConstraints();
		
		for (String table : recordedTables) {
			Set<String> expected = constraintNames(snapshot.checkConstraints().get(table));
			Set<String> actual = constraintNames(liveConstraints.get(table));
			if (expected.equals(actual)) {
				continue;
			}
			
			throw new SqlMigrationConflictException(
				"The check constraints of table '" + table + "' have drifted from the schema snapshot of version " + version + ", expected " + expected + " but found " + actual
			);
		}
	}
	
	/**
	 * Returns the names of the given check constraints.<br>
	 *
	 * @param constraints The check constraints to collect the names of or {@code null} if there are none
	 * @return The names of the given check constraints
	 */
	private static @NonNull Set<String> constraintNames(@Nullable List<SqlCheckConstraintInfo> constraints) {
		if (constraints == null) {
			return Set.of();
		}
		return constraints.stream().map(SqlCheckConstraintInfo::constraintName).collect(Collectors.toCollection(TreeSet::new));
	}
	
	/**
	 * Returns the key that identifies the given column within a schema.<br>
	 *
	 * @param column The column to build the key for
	 * @return The key that identifies the column
	 */
	private static @NonNull String columnKey(@NonNull SqlSchemaColumnInfo column) {
		return column.tableName() + '.' + column.columnName();
	}
	
	/**
	 * Describes the first difference between the recorded and the live state of a column.<br>
	 * The ordinal position and the resolved type parameter are not compared, both are reported inconsistently across
	 * driver versions and would turn harmless upgrades into validation failures.<br>
	 *
	 * @param expected The column as recorded in the schema snapshot
	 * @param actual The column as found in the live database
	 * @return The description of the first difference or {@code null} if the column matches the recorded state
	 */
	private static @Nullable String describeDifference(@NonNull SqlSchemaColumnInfo expected, @NonNull SqlSchemaColumnInfo actual) {
		if (expected.jdbcType() != actual.jdbcType()) {
			return "expected jdbc type " + expected.jdbcType() + " but found " + actual.jdbcType();
		}
		if (expected.nullable() != actual.nullable()) {
			return "expected nullable " + expected.nullable() + " but found " + actual.nullable();
		}
		if (expected.autoIncrement() != actual.autoIncrement()) {
			return "expected auto-increment " + expected.autoIncrement() + " but found " + actual.autoIncrement();
		}
		if (expected.primaryKey() != actual.primaryKey()) {
			return "expected primary key " + expected.primaryKey() + " but found " + actual.primaryKey();
		}
		if (expected.unique() != actual.unique()) {
			return "expected unique " + expected.unique() + " but found " + actual.unique();
		}
		return null;
	}
	
	/**
	 * Checks whether the given table belongs to the bookkeeping of the migration framework itself.<br>
	 * Those tables are upgraded by the framework rather than by a migration, so they are not covered by any schema
	 * snapshot and must be excluded from the drift check.<br>
	 *
	 * @param tableName The name of the table to check
	 * @return True if the table belongs to the bookkeeping of the framework, false otherwise
	 */
	private static boolean isBookkeepingTable(@NonNull String tableName) {
		return tableName.toLowerCase(Locale.ROOT).startsWith("_sql_");
	}
	
	/**
	 * Returns the hint that lists the sql which was executed when the given migration was applied.<br>
	 * The hint is empty if no statements were recorded, which is the case for every migration that was applied before the
	 * framework started recording them.<br>
	 *
	 * @param info The recorded info of the applied migration
	 * @return The hint listing the executed sql or an empty string if none was recorded
	 */
	private static @NonNull String appliedStatementsHint(@NonNull SqlMigrationInfo info) {
		String statements = info.statements();
		if (statements == null || statements.isEmpty()) {
			return "";
		}
		return ", the following sql was executed when it was applied:\n" + statements;
	}
	
	/**
	 * Registers the given migration with this runner.<br>
	 * The registered migrations are kept sorted by their version after insertion.<br>
	 *
	 * @param migration The migration to register
	 * @throws NullPointerException If the migration is null
	 * @throws SqlMigrationConflictException If a migration with the same version is already registered
	 */
	public void register(@NonNull SqlMigration migration) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		
		for (RegisteredSqlMigration existing : this.registeredMigrations) {
			if (existing.migration().version().equals(migration.version())) {
				throw new SqlMigrationConflictException("Duplicate migration version: " + migration.version());
			}
		}
		
		this.registeredMigrations.add(new RegisteredSqlMigration(migration));
		this.registeredMigrations.sort(Comparator.comparing(m -> m.migration().version()));
	}
	
	/**
	 * Registers all migrations in the given list with this runner.<br>
	 * The migrations are registered in iteration order, the registered migrations remain sorted by their version.<br>
	 *
	 * @param migrations The migrations to register
	 * @throws NullPointerException If the list of migrations is null
	 * @throws SqlMigrationConflictException If a migration with a version that is already registered is encountered
	 */
	public void register(@NonNull List<SqlMigration> migrations) throws SqlException {
		Objects.requireNonNull(migrations, "Sql migrations must not be null");
		
		for (SqlMigration migration : migrations) {
			this.register(migration);
		}
	}
	
	/**
	 * Applies all registered migrations which have not yet been applied.<br>
	 * The migrations are applied in version order, already applied versions are skipped.<br>
	 *
	 * @throws SqlException If a migration could not be applied
	 */
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
	
	/**
	 * Applies all pending migrations up to and including the given target version.<br>
	 * Migrations are applied in version order, already applied versions are skipped and migrations with a version greater
	 * than the target version are not applied.<br>
	 *
	 * @param targetVersion The highest version to migrate to, inclusive
	 * @throws NullPointerException If the target version is null
	 * @throws SqlException If a migration could not be applied
	 */
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
	
	/**
	 * Rolls back the most recently applied migration.<br>
	 * This is equivalent to calling {@link #rollback(int)} with a count of {@code 1}.<br>
	 *
	 * @throws SqlException If the migration could not be rolled back
	 */
	public void rollback() throws SqlException {
		this.rollback(1);
	}
	
	/**
	 * Rolls back the given number of most recently applied migrations.<br>
	 * The migrations are rolled back in reverse version order, starting with the most recently applied one.<br>
	 *
	 * @param count The number of applied migrations to roll back
	 * @throws IllegalArgumentException If the count is less than {@code 1}
	 * @throws SqlException If a migration could not be rolled back
	 */
	public void rollback(int count) throws SqlException {
		if (count < 1) {
			throw new IllegalArgumentException("Rollback count must be at least 1");
		}
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.sorted(Comparator.comparing(SqlMigrationInfo::version))
			.toList();
		
		List<SqlMigrationInfo> toRollback = applied.subList(Math.max(0, applied.size() - count), applied.size());
		List<SqlMigrationInfo> reversed = Lists.newArrayList(toRollback);
		Collections.reverse(reversed);
		
		for (SqlMigrationInfo info : reversed) {
			RegisteredSqlMigration registered = this.findRegistered(info.version());
			this.rollbackMigration(registered);
		}
	}
	
	/**
	 * Rolls back all applied migrations with a version greater than the given target version.<br>
	 * The migrations are rolled back in reverse version order, leaving the given target version as the most recent applied
	 * migration.<br>
	 *
	 * @param targetVersion The version to roll back to, exclusive
	 * @throws NullPointerException If the target version is null
	 * @throws SqlException If a migration could not be rolled back
	 */
	public void rollbackTo(@NonNull Version targetVersion) throws SqlException {
		Objects.requireNonNull(targetVersion, "Sql target version must not be null");
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.filter(info -> info.version().compareTo(targetVersion) > 0)
			.sorted(Comparator.comparing(SqlMigrationInfo::version))
			.toList();
		
		List<SqlMigrationInfo> reversed = Lists.newArrayList(applied);
		Collections.reverse(reversed);
		
		for (SqlMigrationInfo info : reversed) {
			RegisteredSqlMigration registered = this.findRegistered(info.version());
			this.rollbackMigration(registered);
		}
	}
	
	/**
	 * Validates that all applied migrations still match their recorded state.<br>
	 * <p>
	 *     Two independent checks are performed. For every applied migration the operations are rebuilt and their structural
	 *     checksum is compared against the checksum that was stored when the migration was applied, which detects a
	 *     migration whose body has been edited. Afterwards the live database schema is compared against the schema snapshot
	 *     that was recorded after the most recently applied migration, which detects a database that has drifted away from
	 *     what the migrations produced.
	 * </p>
	 * <p>
	 *     The checksum deliberately ignores the names and types of the tables and columns a migration references, because
	 *     those are read from the live schema definitions and change whenever a later migration renames or retypes them.
	 *     Such a change is detected by the drift check instead, which compares against the database rather than against the
	 *     schema definitions.
	 * </p>
	 * <p>
	 *     A migration whose recorded checksum was written by an earlier version of the library is skipped by the checksum
	 *     check, because that checksum was computed over the rendered sql and cannot be compared against a structural one.
	 *     Such a migration is validated again as soon as it is applied to a fresh database.
	 * </p>
	 *
	 * @throws SqlMigrationConflictException If an applied migration has been modified since it was applied or the database has drifted from the recorded schema
	 * @throws SqlException If the applied migrations could not be validated
	 */
	public void validate() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.sorted(Comparator.comparing(SqlMigrationInfo::version))
			.toList();
		
		SqlMigrationSchema schema = SqlMigrationSchema.empty();
		for (SqlMigrationInfo info : applied) {
			RegisteredSqlMigration registered = this.findRegistered(info.version());
			List<SqlMigrationOperation> operations = this.buildOperations(registered.migration(), true, true, schema);
			
			if (info.checksum() != null && SqlMigrationChecksum.isComparable(info.checksum())) {
				String checksum = SqlMigrationChecksum.compute(operations, this.context.dialect());
				if (!checksum.equals(info.checksum())) {
					throw new SqlMigrationConflictException(
						"Migration " + info.version() + " has been modified since it was applied (checksum mismatch)" + appliedStatementsHint(info)
					);
				}
			}
			schema = SqlMigrationSchema.applyOperations(schema, operations);
		}
		this.validateSchemaDrift(applied);
	}
	
	/**
	 * Returns the status of all registered migrations.<br>
	 * For each registered migration the stored info is returned if it exists, otherwise an info with the status
	 * {@link SqlMigrationStatus#PENDING} is returned. The result is ordered by version.<br>
	 *
	 * @return An immutable list with the status of every registered migration
	 * @throws SqlException If the stored migration infos could not be loaded
	 */
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
					null
				));
			}
		}
		return List.copyOf(result);
	}
	
	/**
	 * Renders the sql that would be executed by {@link #migrate()} without applying it.<br>
	 * The pending migrations are rendered in version order against the schema that would result from applying the preceding
	 * pending migrations, the database is not modified.<br>
	 *
	 * @return An immutable list with the rendered sql for all pending migrations
	 * @throws SqlException If the pending migrations could not be rendered
	 */
	public @NonNull List<SqlRendered> dryRun() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll();
		Set<Version> appliedVersions = applied.stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.map(SqlMigrationInfo::version)
			.collect(Collectors.toSet());
		
		SqlMigrationSchema schema = this.latestAppliedSchema();
		List<SqlRendered> results = Lists.newArrayList();
		for (RegisteredSqlMigration registered : this.registeredMigrations) {
			if (!appliedVersions.contains(registered.migration().version())) {
				List<SqlMigrationOperation> operations = this.buildOperations(registered.migration(), true, true, schema);
				results.addAll(this.renderer.render(operations));
				schema = SqlMigrationSchema.applyOperations(schema, operations);
			}
		}
		return List.copyOf(results);
	}
	
	/**
	 * Renders the sql that would be executed when rolling back the most recently applied migration without applying it.<br>
	 * If no migration has been applied an empty list is returned, the database is not modified.<br>
	 *
	 * @return An immutable list with the rendered rollback sql for the most recently applied migration
	 * @throws SqlException If the rollback could not be rendered
	 */
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
	
	/**
	 * Applies the given registered migration.<br>
	 * The migration's {@code up} step is rendered and executed together with the corresponding store and schema snapshot
	 * update inside a single transaction, the recorded info is marked as {@link SqlMigrationStatus#APPLIED}.<br>
	 *
	 * @param registered The registered migration to apply
	 * @throws NullPointerException If the registered migration is null
	 * @throws SqlException If the migration could not be applied
	 */
	private void applyMigration(@NonNull RegisteredSqlMigration registered) throws SqlException {
		Objects.requireNonNull(registered, "Registered sql migration must not be null");
		this.ensureAtomicExecutionAllowed(registered.migration());
		
		List<SqlMigrationOperation> operations = this.buildOperations(registered.migration(), true, false, this.latestAppliedSchema());
		List<SqlRendered> rendered = this.renderer.render(operations);
		SqlMigrationInfo info = new SqlMigrationInfo(
			registered.migration().version(),
			registered.migration().description(),
			SqlMigrationStatus.APPLIED,
			Instant.now(),
			SqlMigrationChecksum.compute(operations, this.context.dialect()),
			joinStatements(rendered)
		);
		this.context.executeAndSave(rendered, this.store, info, this.schemaStore);
	}
	
	/**
	 * Rolls back the given registered migration.<br>
	 * The migration's {@code down} step is rendered and executed together with the corresponding store and schema snapshot
	 * update inside a single transaction, the recorded info is marked as {@link SqlMigrationStatus#ROLLED_BACK}.<br>
	 *
	 * @param registered The registered migration to roll back
	 * @throws NullPointerException If the registered migration is null
	 * @throws SqlException If the migration could not be rolled back
	 */
	private void rollbackMigration(@NonNull RegisteredSqlMigration registered) throws SqlException {
		Objects.requireNonNull(registered, "Registered sql migration must not be null");
		this.ensureAtomicExecutionAllowed(registered.migration());
		
		List<SqlRendered> rendered = this.renderMigrationDown(registered.migration());
		this.context.executeAndUpdate(rendered, this.store, registered.migration().version(), SqlMigrationStatus.ROLLED_BACK, this.schemaStore);
	}
	
	/**
	 * Ensures that the given migration may be executed atomically on the current dialect.<br>
	 * The check passes if the dialect supports transactional ddl or the migration explicitly opts into non-atomic
	 * execution, otherwise an exception is thrown to prevent a half-applied schema on a mid-migration failure.<br>
	 *
	 * @param migration The migration to check
	 * @throws NullPointerException If the migration is null
	 * @throws SqlMigrationConflictException If the migration cannot be executed atomically on the current dialect
	 */
	private void ensureAtomicExecutionAllowed(@NonNull SqlMigration migration) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		
		if (this.context.dialect().isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL) || migration.allowsNonAtomicExecution()) {
			return;
		}
		
		throw new SqlMigrationConflictException(
			"Migration " + migration.version() + " cannot be executed atomically on dialect '" + this.context.dialect().name() + "' because it implicitly commits on DDL, " +
				"a mid-migration failure may leave the schema half-applied without the possibility of a rollback. Override SqlMigration.allowsNonAtomicExecution() to accept this risk."
		);
	}
	
	/**
	 * Renders the sql for the {@code down} step of the given migration.<br>
	 * @param migration The migration to render the down step for
	 * @return The rendered sql statements for the down step
	 * @throws SqlException If the down step could not be rendered
	 */
	private @NonNull List<SqlRendered> renderMigrationDown(@NonNull SqlMigration migration) throws SqlException {
		return this.renderMigration(migration, false, false);
	}
	
	/**
	 * Renders the sql for the given migration against the latest applied schema.<br>
	 * The operations are built for either the {@code up} or {@code down} step depending on the given flag and then
	 * rendered into executable sql statements.<br>
	 *
	 * @param migration The migration to render
	 * @param up Whether to render the up step ({@code true}) or the down step ({@code false})
	 * @param dryRun Whether the operations are built for a dry run without modifying the database
	 * @return The rendered sql statements for the migration
	 * @throws NullPointerException If the migration is null
	 * @throws SqlException If the migration could not be rendered
	 */
	private @NonNull List<SqlRendered> renderMigration(@NonNull SqlMigration migration, boolean up, boolean dryRun) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		
		SqlMigrationSchema schema = this.latestAppliedSchema();
		return this.renderer.render(this.buildOperations(migration, up, dryRun, schema));
	}
	
	/**
	 * Returns the schema as it exists after the most recently applied migration.<br>
	 * If no migration has been applied an empty schema is returned, otherwise the schema snapshot stored for the latest
	 * applied version is loaded and reconstructed.<br>
	 *
	 * @return The schema after the most recently applied migration
	 * @throws SqlMigrationConflictException If no schema snapshot exists for the latest applied version
	 * @throws SqlException If the applied migrations or the schema snapshot could not be loaded
	 */
	private @NonNull SqlMigrationSchema latestAppliedSchema() throws SqlException {
		List<SqlMigrationInfo> applied = this.store.loadAll().stream()
			.filter(info -> info.status() == SqlMigrationStatus.APPLIED)
			.sorted(Comparator.comparing(SqlMigrationInfo::version))
			.toList();
		
		if (applied.isEmpty()) {
			return SqlMigrationSchema.empty();
		}
		
		Version latestVersion = applied.getLast().version();
		SqlSchemaSnapshot snapshot = this.schemaStore.load(latestVersion);
		if (snapshot == null) {
			throw new SqlMigrationConflictException("Schema snapshot not found for applied version " + latestVersion);
		}
		return SqlMigrationSchema.fromSnapshot(snapshot.columns(), snapshot.checkConstraints());
	}
	
	/**
	 * Validates that the live database schema still matches the schema snapshot of the most recently applied migration.<br>
	 * <p>
	 *     The snapshot was introspected from the database right after the migration was applied and is therefore ground
	 *     truth, unlike anything re-derived from the schema definitions. Only the tables the snapshot records are checked,
	 *     tables created outside of the migrations are ignored, as is the bookkeeping of the framework itself.
	 * </p>
	 *
	 * @param applied The applied migration infos ordered by version
	 * @throws NullPointerException If the applied migration infos are null
	 * @throws SqlMigrationConflictException If the database has drifted from the recorded schema snapshot
	 * @throws SqlException If the schema snapshot or the live schema could not be loaded
	 */
	private void validateSchemaDrift(@NonNull List<SqlMigrationInfo> applied) throws SqlException {
		Objects.requireNonNull(applied, "Applied sql migration infos must not be null");
		
		if (applied.isEmpty()) {
			return;
		}
		
		Version version = applied.getLast().version();
		SqlSchemaSnapshot snapshot = this.schemaStore.load(version);
		if (snapshot == null) {
			return;
		}
		
		SqlMigrationSchema live = SqlMigrationSchema.load(this.context.dataSource(), this.context.dialect());
		List<SqlSchemaColumnInfo> liveColumns = live.extractColumnInfos();
		Map<String, SqlSchemaColumnInfo> liveColumnsByKey = Maps.newLinkedHashMap();
		for (SqlSchemaColumnInfo column : liveColumns) {
			liveColumnsByKey.put(columnKey(column), column);
		}
		
		Set<String> recordedTables = Sets.newLinkedHashSet();
		Set<String> recordedColumns = Sets.newLinkedHashSet();
		for (SqlSchemaColumnInfo expected : snapshot.columns()) {
			if (isBookkeepingTable(expected.tableName())) {
				continue;
			}
			recordedTables.add(expected.tableName());
			recordedColumns.add(columnKey(expected));
			
			SqlSchemaColumnInfo actual = liveColumnsByKey.get(columnKey(expected));
			if (actual == null) {
				throw new SqlMigrationConflictException(
					"Column '" + expected.columnName() + "' of table '" + expected.tableName() + "' is recorded in the schema snapshot of version " + version + " but is missing from the database"
				);
			}
			
			String difference = describeDifference(expected, actual);
			if (difference != null) {
				throw new SqlMigrationConflictException(
					"Column '" + expected.columnName() + "' of table '" + expected.tableName() + "' has drifted from the schema snapshot of version " + version + ": " + difference
				);
			}
		}
		
		for (SqlSchemaColumnInfo actual : liveColumns) {
			if (!recordedTables.contains(actual.tableName()) || recordedColumns.contains(columnKey(actual))) {
				continue;
			}
			
			throw new SqlMigrationConflictException(
				"Table '" + actual.tableName() + "' has the additional column '" + actual.columnName() + "' which the schema snapshot of version " + version + " does not record"
			);
		}
		validateCheckConstraintDrift(version, snapshot, live, recordedTables);
	}
	
	/**
	 * Builds the migration operations for the given migration against the given schema.<br>
	 * Depending on the given flag the migration's {@code up} or {@code down} step is invoked on a fresh builder, the
	 * collected operations are then returned.<br>
	 *
	 * @param migration The migration to build the operations for
	 * @param up Whether to build the up step ({@code true}) or the down step ({@code false})
	 * @param dryRun Whether the operations are built for a dry run without modifying the database
	 * @param schema The current schema the operations are built against
	 * @return The operations collected from the migration step
	 * @throws NullPointerException If the migration or schema is null
	 * @throws SqlException If the operations could not be built
	 */
	private @NonNull List<SqlMigrationOperation> buildOperations(@NonNull SqlMigration migration, boolean up, boolean dryRun, @NonNull SqlMigrationSchema schema) throws SqlException {
		Objects.requireNonNull(migration, "Sql migration must not be null");
		Objects.requireNonNull(schema, "Sql migration schema must not be null");
		
		SqlMigrationBuilder builder = new SqlMigrationBuilder(this.context, dryRun);
		if (up) {
			migration.up(builder, schema);
		} else {
			migration.down(builder, schema);
		}
		return builder.getOperations();
	}
	
	/**
	 * Finds the registered migration with the given version.<br>
	 * @param version The version to look up
	 * @return The registered migration with the given version
	 * @throws NullPointerException If the version is null
	 * @throws SqlMigrationConflictException If no registered migration with the given version exists
	 */
	private @NonNull RegisteredSqlMigration findRegistered(@NonNull Version version) throws SqlException {
		Objects.requireNonNull(version, "Sql migration version must not be null");
		
		for (RegisteredSqlMigration registered : this.registeredMigrations) {
			if (registered.migration().version().equals(version)) {
				return registered;
			}
		}
		throw new SqlMigrationConflictException("No registered migration found for version " + version);
	}
	
	/**
	 * Internal record holding a migration that has been registered with the runner.<br>
	 *
	 * @param migration The registered migration
	 */
	private record RegisteredSqlMigration(@NonNull SqlMigration migration) {}
	
	/**
	 * Internal migration context backed by a {@link SqlDatabase}.<br>
	 * Provides the dialect, data source and transactional execution used by the runner from the underlying database.<br>
	 *
	 * @param database The database backing this migration context
	 */
	private record SqlDatabaseMigrationContext(@NonNull SqlDatabase database) implements SqlMigrationContext {
		
		/**
		 * Constructs a new database migration context with the given database.<br>
		 *
		 * @param database The database backing this migration context
		 * @throws NullPointerException If the database is null
		 */
		private SqlDatabaseMigrationContext {
			Objects.requireNonNull(database, "Sql database must not be null");
		}
		
		@Override
		public @NonNull DataSource dataSource() {
			return this.database.getDataSource();
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
		
		/**
		 * Executes the given rendered sql statements on the given connection.<br>
		 * Empty statements are skipped, every other statement is prepared, its parameters are bound in order and the
		 * statement is executed.<br>
		 *
		 * @param connection The connection to execute the statements on
		 * @param statements The rendered sql statements to execute
		 * @throws NullPointerException If the connection or statements list is null
		 * @throws SQLException If a statement could not be prepared or executed
		 * @throws SqlException If a parameter value could not be bound
		 */
		private void executeStatements(@NonNull Connection connection, @NonNull List<SqlRendered> statements) throws SQLException, SqlException {
			Objects.requireNonNull(connection, "Connection must not be null");
			Objects.requireNonNull(statements, "Sql rendered statements must not be null");
			
			for (SqlRendered rendered : statements) {
				String sql = rendered.sql();
				if (sql.isEmpty()) {
					continue;
				}
				
				if (!rendered.parameters().isEmpty()) {
					throw new SqlMigrationConflictException(
						"Migration statement '" + sql + "' carries " + rendered.parameters().size() + " bind parameter(s), but ddl is part of the schema definition and no engine accepts placeholders there. " +
							"Every value of a migration statement must be rendered as a literal."
					);
				}
				
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					List<Pair<SqlType<?>, Object>> params = rendered.parameters();
					for (int i = 0; i < params.size(); i++) {
						Pair<SqlType<?>, Object> pair = params.get(i);
						SqlType.setValue(pair.getFirst(), this.database.getDialect(), statement, i + 1, pair.getSecond());
					}
					statement.execute();
				}
			}
		}
		
		@Override
		public void executeAndSave(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull SqlMigrationInfo info, @NonNull SqlMigrationSchemaStore schemaStore) throws SqlException {
			Objects.requireNonNull(statements, "Sql rendered statements must not be null");
			Objects.requireNonNull(store, "Sql migration store must not be null");
			Objects.requireNonNull(info, "Sql migration info must not be null");
			Objects.requireNonNull(schemaStore, "Sql migration schema store must not be null");
			
			try (Connection connection = this.database.getDataSource().getConnection()) {
				connection.setAutoCommit(false);
				try {
					this.executeStatements(connection, statements);
					store.save(connection, info);
					
					String introspectionSchema = this.database.getDialect().defaultSchema(connection);
					SqlMigrationSchema schema = SqlMigrationSchema.load(connection, this.database.getDialect(), introspectionSchema);
					schemaStore.save(connection, info.version(), schema.extractColumnInfos(), schema.extractCheckConstraints());
					
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					throw e;
				}
			} catch (SQLException e) {
				throw new SqlMigrationExecutionException("Failed to execute migration with store update", e);
			}
		}
		
		@Override
		public void executeAndUpdate(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull Version version, @NonNull SqlMigrationStatus status, @NonNull SqlMigrationSchemaStore schemaStore) throws SqlException {
			Objects.requireNonNull(statements, "Sql rendered statements must not be null");
			Objects.requireNonNull(store, "Sql migration store must not be null");
			Objects.requireNonNull(version, "Sql migration version must not be null");
			Objects.requireNonNull(status, "Sql migration status must not be null");
			Objects.requireNonNull(schemaStore, "Sql migration schema store must not be null");
			
			try (Connection connection = this.database.getDataSource().getConnection()) {
				connection.setAutoCommit(false);
				try {
					this.executeStatements(connection, statements);
					
					store.update(connection, version, status);
					schemaStore.delete(connection, version);
					
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					throw e;
				}
			} catch (SQLException e) {
				throw new SqlMigrationExecutionException("Failed to execute migration with store update", e);
			}
		}
	}
}
