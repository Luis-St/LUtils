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

import net.luis.utils.io.database.*;
import net.luis.utils.io.database.SqlTestFixtures.RecordingDataSource;
import net.luis.utils.io.database.dialect.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlMigrationConflictException;
import net.luis.utils.io.database.exception.database.SqlMigrationExecutionException;
import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.migration.store.SqlMigrationStore;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.util.Pair;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationRunner}.<br>
 * <p>
 *     The runner is built through the real {@code of(...)} factories and driven by
 *     {@link SqlTestFixtures#recordingDataSource()} (a working, no-op fake connection), so the real
 *     {@code SqlDatabaseMigrationContext}, {@code SqlMigrationSchemaStore} and renderer run without a live database.
 *     A deterministic {@link FakeMigrationStore} controls migration history. Because the recording connection does
 *     not persist anything, paths that read back a previously written schema snapshot (multi-migration applies and
 *     the snapshot-present rollback/dry-run paths) surface the source-correct {@code "Schema snapshot not found"}
 *     failure rather than succeeding; those success paths require a live database and are integration-tier.
 * </p>
 *
 * @author Luis-St
 */
class SqlMigrationRunnerTest {
	
	private static final Version V1 = Version.of(1, 0, 0);
	private static final Version V2 = Version.of(2, 0, 0);
	private static final Version V3 = Version.of(3, 0, 0);
	
	private static @NonNull SqlMigrationRunner runner(@NonNull RecordingDataSource source, @NonNull SqlDialect dialect, @NonNull FakeMigrationStore store) throws SqlException {
		return SqlMigrationRunner.of(SqlDatabase.builder(source, dialect).build(), store);
	}
	
	private static @NonNull TestMigration migration(@NonNull Version version) {
		return new TestMigration(version, false, false, null, null);
	}
	
	private static @NonNull SqlMigrationInfo applied(@NonNull Version version, @Nullable String checksum) {
		return new SqlMigrationInfo(version, "Migration " + version, SqlMigrationStatus.APPLIED, null, checksum);
	}
	
	private static @NonNull SqlMigrationInfo rolledBack(@NonNull Version version) {
		return new SqlMigrationInfo(version, "Migration " + version, SqlMigrationStatus.ROLLED_BACK, null, null);
	}
	
	private static @NonNull SqlMigrationInfo appliedWithStatements(@NonNull Version version, @Nullable String checksum, @Nullable String statements) {
		return new SqlMigrationInfo(version, "Migration " + version, SqlMigrationStatus.APPLIED, null, checksum, statements);
	}
	
	private static @NonNull SqlSchemaColumnInfo columnInfo(int jdbcType, boolean nullable, boolean autoIncrement, boolean primaryKey, boolean unique) {
		return new SqlSchemaColumnInfo("users", "id", jdbcType, null, nullable, autoIncrement, primaryKey, unique, 1, null);
	}
	
	private static @Nullable Object invokeStatic(@NonNull String name, @NonNull Class<?> type, @Nullable Object argument) throws Throwable {
		Method method = SqlMigrationRunner.class.getDeclaredMethod(name, type);
		method.setAccessible(true);
		try {
			return method.invoke(null, argument);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
	
	private static @NonNull String invokeJoinStatements(@Nullable List<SqlRendered> rendered) throws Throwable {
		return (String) Objects.requireNonNull(invokeStatic("joinStatements", List.class, rendered));
	}
	
	private static @NonNull String invokeAppliedStatementsHint(@NonNull SqlMigrationInfo info) throws Throwable {
		return (String) Objects.requireNonNull(invokeStatic("appliedStatementsHint", SqlMigrationInfo.class, info));
	}
	
	private static boolean invokeIsBookkeepingTable(@NonNull String tableName) throws Throwable {
		return (Boolean) Objects.requireNonNull(invokeStatic("isBookkeepingTable", String.class, tableName));
	}
	
	@SuppressWarnings("unchecked")
	private static @NonNull Set<String> invokeConstraintNames(@Nullable List<SqlCheckConstraintInfo> constraints) throws Throwable {
		return (Set<String>) Objects.requireNonNull(invokeStatic("constraintNames", List.class, constraints));
	}
	
	private static @Nullable String invokeDescribeDifference(@NonNull SqlSchemaColumnInfo expected, @NonNull SqlSchemaColumnInfo actual) throws Throwable {
		Method method = SqlMigrationRunner.class.getDeclaredMethod("describeDifference", SqlSchemaColumnInfo.class, SqlSchemaColumnInfo.class);
		method.setAccessible(true);
		try {
			return (String) method.invoke(null, expected, actual);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
	
	/**
	 * Wraps the connections of the given source so they report the given schema and catalog, which is what
	 * {@code SqlDialect#defaultSchema(Connection)} resolves from; every other call still reaches the recording source.
	 *
	 * @param delegate The recording source handing out the real fake connections
	 * @param schema The schema {@code getSchema()} reports
	 * @param catalog The catalog {@code getCatalog()} reports
	 * @return The wrapping data source
	 */
	private static @NonNull DataSource schemaOverriding(@NonNull RecordingDataSource delegate, @Nullable String schema, @Nullable String catalog) {
		return (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class<?>[] { DataSource.class }, (proxy, method, args) -> {
			if (!"getConnection".equals(method.getName())) {
				return null;
			}
			Connection connection = delegate.getConnection();
			return Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, (connectionProxy, connectionMethod, connectionArgs) -> switch (connectionMethod.getName()) {
				case "getSchema" -> schema;
				case "getCatalog" -> catalog;
				default -> connectionMethod.invoke(connection, connectionArgs);
			});
		});
	}
	
	/**
	 * Invokes the private {@code executeStatements} method of the runner's migration context.
	 *
	 * @param runner The runner holding the context
	 * @param source The data source to obtain the connection from
	 * @param statements The statements to execute
	 * @throws Throwable The cause thrown by the invoked method
	 */
	private static void invokeExecuteStatements(@NonNull SqlMigrationRunner runner, @NonNull RecordingDataSource source, @NonNull List<SqlRendered> statements) throws Throwable {
		Field contextField = SqlMigrationRunner.class.getDeclaredField("context");
		contextField.setAccessible(true);
		Object context = contextField.get(runner);
		
		Method method = context.getClass().getDeclaredMethod("executeStatements", Connection.class, List.class);
		method.setAccessible(true);
		try (Connection connection = source.getConnection()) {
			method.invoke(context, connection, statements);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
	
	@Test
	void ofWithDatabaseAndStoreReturnsRunner() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		
		assertNotNull(runner);
		assertTrue(store.initialized);
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("_sql_schema_columns")));
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("_sql_schema_check_constraints")));
	}
	
	@Test
	void ofWithDatabaseReturnsRunner() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		
		SqlMigrationRunner runner = SqlMigrationRunner.of(SqlDatabase.builder(source, SqlDialects.POSTGRESQL).build());
		
		assertNotNull(runner);
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("_sql_migrations")));
	}
	
	@Test
	void ofWithNullDatabase() {
		assertThrows(NullPointerException.class, () -> SqlMigrationRunner.of(null));
	}
	
	@Test
	void ofWithDatabaseAndNullDatabase() {
		assertThrows(NullPointerException.class, () -> SqlMigrationRunner.of(null, new FakeMigrationStore()));
	}
	
	@Test
	void ofWithNullStore() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL).build();
		assertThrows(NullPointerException.class, () -> SqlMigrationRunner.of(database, null));
	}
	
	@Test
	void ofWithFailingDataSourceWrapsSchemaStoreInit() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(SqlTestFixtures.failingDataSource(), SqlDialects.POSTGRESQL).build();
		assertThrows(SqlMigrationExecutionException.class, () -> SqlMigrationRunner.of(database, new FakeMigrationStore()));
	}
	
	@Test
	void ofSingleArgWithFailingDataSourceWrapsTableStoreInit() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(SqlTestFixtures.failingDataSource(), SqlDialects.POSTGRESQL).build();
		assertThrows(SqlMigrationExecutionException.class, () -> SqlMigrationRunner.of(database));
	}
	
	@Test
	void registerWithNullMigration() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertThrows(NullPointerException.class, () -> runner.register((SqlMigration) null));
	}
	
	@Test
	void registerWithNullList() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertThrows(NullPointerException.class, () -> runner.register((List<SqlMigration>) null));
	}
	
	@Test
	void registerListWithNullElement() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		List<SqlMigration> migrations = Arrays.asList(migration(V1), null);
		assertThrows(NullPointerException.class, () -> runner.register(migrations));
	}
	
	@Test
	void registerDuplicateVersionThrows() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(migration(V1));
		assertThrows(SqlMigrationConflictException.class, () -> runner.register(migration(V1)));
	}
	
	@Test
	void registerListWithDuplicateVersionThrows() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		List<SqlMigration> migrations = List.of(migration(V1), migration(V1));
		assertThrows(SqlMigrationConflictException.class, () -> runner.register(migrations));
	}
	
	@Test
	void migrateToWithNullVersion() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertThrows(NullPointerException.class, () -> runner.migrateTo(null));
	}
	
	@Test
	void rollbackToWithNullVersion() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertThrows(NullPointerException.class, () -> runner.rollbackTo(null));
	}
	
	@Test
	void rollbackWithCountBelowOne() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertThrows(IllegalArgumentException.class, () -> runner.rollback(0));
	}
	
	@Test
	void rollbackWithNegativeCount() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertThrows(IllegalArgumentException.class, () -> runner.rollback(-1));
	}
	
	@Test
	void rollbackWithUnregisteredAppliedVersionThrows() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "abcd"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::rollback);
		assertTrue(exception.getMessage().contains("No registered migration found"));
	}
	
	@Test
	void validateWithChecksumMismatchThrows() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "s1:0000"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::validate);
		assertTrue(exception.getMessage().contains("checksum mismatch"));
	}
	
	@Test
	void validateWithLegacyChecksumSkipsComparison() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "0".repeat(64)));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		assertDoesNotThrow(runner::validate);
	}
	
	@Test
	void validateMismatchMessageWithoutStatements() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "s1:0000"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::validate);
		assertTrue(exception.getMessage().endsWith("(checksum mismatch)"));
		assertFalse(exception.getMessage().contains("was applied:"));
	}
	
	@Test
	void validateMismatchMessageIncludesStatements() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(appliedWithStatements(V1, "s1:0000", "CREATE TABLE recorded"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::validate);
		assertTrue(exception.getMessage().contains("checksum mismatch"));
		assertTrue(exception.getMessage().contains("CREATE TABLE recorded"));
	}
	
	@Test
	void validateWithoutSnapshotSkipsDriftCheck() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, null));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		assertDoesNotThrow(runner::validate);
	}
	
	@Test
	void migrateRejectsNonAtomicMigrationOnNonTransactionalDialect() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlTestFixtures.DIALECT, store);
		runner.register(new TestMigration(V1, false, false, null, null));
		
		assertThrows(SqlMigrationConflictException.class, runner::migrate);
		assertTrue(store.saved.isEmpty());
		assertEquals(0, source.commitCount());
	}
	
	@Test
	void latestAppliedSchemaThrowsWhenSnapshotMissing() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "abcd"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::rollback);
		assertTrue(exception.getMessage().contains("Schema snapshot not found"));
	}
	
	@Test
	void registerSingleMigrationAddsIt() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(migration(V1));
		List<SqlMigrationInfo> status = runner.status();
		assertEquals(1, status.size());
		assertEquals(V1, status.getFirst().version());
	}
	
	@Test
	void registerSortsByVersion() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(migration(V3));
		runner.register(migration(V1));
		runner.register(migration(V2));
		List<SqlMigrationInfo> status = runner.status();
		assertEquals(List.of(V1, V2, V3), status.stream().map(SqlMigrationInfo::version).toList());
	}
	
	@Test
	void registerEmptyListIsNoOp() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(List.of());
		assertTrue(runner.status().isEmpty());
	}
	
	@Test
	void registerListAddsAll() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(List.of(migration(V2), migration(V1)));
		List<SqlMigrationInfo> status = runner.status();
		assertEquals(List.of(V1, V2), status.stream().map(SqlMigrationInfo::version).toList());
	}
	
	@Test
	void migrateAppliesPendingMigration() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrate();
		
		assertEquals(1, store.saved.size());
		assertEquals(V1, store.saved.getFirst().version());
		assertEquals(SqlMigrationStatus.APPLIED, store.saved.getFirst().status());
		assertNotNull(store.saved.getFirst().checksum());
		assertTrue(source.commitCount() >= 1);
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("CREATE TABLE") && sql.contains("test_table")));
	}
	
	@Test
	void migrateRecordsStructuralChecksum() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrate();
		
		String checksum = store.saved.getFirst().checksum();
		assertNotNull(checksum);
		assertTrue(checksum.startsWith("s1:"));
		assertEquals(64, checksum.length());
	}
	
	@Test
	void migrateRecordsExecutedStatements() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrate();
		
		String statements = store.saved.getFirst().statements();
		assertNotNull(statements);
		assertTrue(statements.contains("CREATE TABLE"));
		assertTrue(statements.contains("test_table"));
	}
	
	@Test
	void migrateRecordsNoStatementsForDataOnlyMigration() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(new TestMigration(V1, false, true, null, null));
		
		runner.migrate();
		
		assertEquals("", store.saved.getFirst().statements());
	}
	
	@Test
	void migrateChecksumIsStableAcrossRunnerInstances() throws SqlException {
		FakeMigrationStore first = new FakeMigrationStore();
		runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, first).register(migration(V1));
		SqlMigrationRunner firstRunner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, first);
		firstRunner.register(migration(V1));
		firstRunner.migrate();
		
		FakeMigrationStore second = new FakeMigrationStore();
		SqlMigrationRunner secondRunner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, second);
		secondRunner.register(migration(V1));
		secondRunner.migrate();
		
		assertEquals(first.saved.getFirst().checksum(), second.saved.getFirst().checksum());
	}
	
	@Test
	void migrateSkipsAlreadyAppliedMigration() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "abcd"));
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrate();
		
		assertTrue(store.saved.isEmpty());
		assertEquals(0, source.commitCount());
	}
	
	@Test
	void migrateWithEmptyRegistryIsNoOp() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		
		runner.migrate();
		
		assertTrue(store.saved.isEmpty());
		assertEquals(0, source.commitCount());
	}
	
	@Test
	void migrateReappliesNonAppliedMigration() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore(rolledBack(V1));
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrate();
		
		assertEquals(1, store.saved.size());
		assertEquals(V1, store.saved.getFirst().version());
		assertEquals(SqlMigrationStatus.APPLIED, store.saved.getFirst().status());
		assertTrue(source.commitCount() >= 1);
	}
	
	@Test
	void migrateToAppliesUpToTargetAndBreaksAbove() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		runner.register(migration(V2));
		
		runner.migrateTo(V1);
		
		assertEquals(1, store.saved.size());
		assertEquals(V1, store.saved.getFirst().version());
	}
	
	@Test
	void migrateToSkipsAppliedWithinRange() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "abcd"));
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrateTo(V1);
		
		assertTrue(store.saved.isEmpty());
		assertEquals(0, source.commitCount());
	}
	
	@Test
	void rollbackDefaultDelegatesToRollbackOne() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "a"), applied(V2, "b"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		runner.register(migration(V2));
		
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::rollback);
		assertTrue(exception.getMessage().contains("Schema snapshot not found"));
		assertTrue(exception.getMessage().contains(V2.toString()));
	}
	
	@Test
	void rollbackCountGreaterThanAppliedSelectsAll() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "a"), applied(V2, "b"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		runner.register(migration(V2));
		
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, () -> runner.rollback(5));
		assertTrue(exception.getMessage().contains(V2.toString()));
	}
	
	@Test
	void rollbackWithNoAppliedMigrationsIsNoOp() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		assertDoesNotThrow(() -> runner.rollback(1));
		assertTrue(store.updated.isEmpty());
	}
	
	@Test
	void rollbackToWithNoAppliedIsNoOp() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		assertDoesNotThrow(() -> runner.rollbackTo(V1));
		assertTrue(store.updated.isEmpty());
	}
	
	@Test
	void rollbackToKeepsVersionsAtOrBelowTarget() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "a"), applied(V2, "b"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		assertDoesNotThrow(() -> runner.rollbackTo(V2));
		assertTrue(store.updated.isEmpty());
	}
	
	@Test
	void rollbackToAboveTargetReachesRollbackWithoutSnapshot() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "a"), applied(V2, "b"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		runner.register(migration(V2));
		
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, () -> runner.rollbackTo(V1));
		assertTrue(exception.getMessage().contains("Schema snapshot not found"));
		assertTrue(exception.getMessage().contains(V2.toString()));
	}
	
	@Test
	void validateWithNullChecksumSkipsComparison() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, null));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		assertDoesNotThrow(runner::validate);
	}
	
	@Test
	void validateWithNoAppliedMigrationsIsNoOp() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		assertDoesNotThrow(runner::validate);
	}
	
	@Test
	void validateWithUnregisteredAppliedVersionThrows() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, null));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::validate);
		assertTrue(exception.getMessage().contains("No registered migration found"));
	}
	
	@Test
	void validateSkipsNonAppliedMigration() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(rolledBack(V1));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		assertDoesNotThrow(runner::validate);
	}
	
	@Test
	void statusReturnsStoredInfoWhenPresent() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "abcd"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		List<SqlMigrationInfo> status = runner.status();
		assertEquals(1, status.size());
		assertEquals(SqlMigrationStatus.APPLIED, status.getFirst().status());
	}
	
	@Test
	void statusSynthesizesPendingWhenAbsent() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		SqlMigrationInfo info = runner.status().getFirst();
		assertEquals(SqlMigrationStatus.PENDING, info.status());
		assertNull(info.appliedAt());
		assertNull(info.checksum());
		assertEquals("Migration " + V1, info.description());
	}
	
	@Test
	void statusWithEmptyRegistryReturnsEmptyList() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertTrue(runner.status().isEmpty());
	}
	
	@Test
	void statusResultIsUnmodifiable() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(migration(V1));
		List<SqlMigrationInfo> status = runner.status();
		assertThrows(UnsupportedOperationException.class, () -> status.add(applied(V2, null)));
	}
	
	@Test
	void dryRunRendersPendingMigrations() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		List<SqlRendered> rendered = runner.dryRun();
		
		assertFalse(rendered.isEmpty());
		assertTrue(store.saved.isEmpty());
		assertEquals(0, source.commitCount());
	}
	
	@Test
	void dryRunWithEmptyRegistryReturnsEmptyList() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertTrue(runner.dryRun().isEmpty());
	}
	
	@Test
	void dryRunResultIsUnmodifiable() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(migration(V1));
		List<SqlRendered> rendered = runner.dryRun();
		assertThrows(UnsupportedOperationException.class, () -> rendered.add(SqlRendered.of("SELECT 1")));
	}
	
	@Test
	void dryRunRollbackWithNoAppliedReturnsEmpty() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		assertTrue(runner.dryRunRollback().isEmpty());
	}
	
	@Test
	void dryRunRollbackNonEmptyReachesRenderWithoutSnapshot() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "abcd"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::dryRunRollback);
		assertTrue(exception.getMessage().contains("Schema snapshot not found"));
	}
	
	@Test
	void migrateAllowsAtomicMigrationOnTransactionalDialect() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(new TestMigration(V1, false, false, null, null));
		runner.migrate();
		assertEquals(1, store.saved.size());
	}
	
	@Test
	void migrateAllowsNonAtomicMigrationViaOverride() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlTestFixtures.DIALECT, store);
		runner.register(new TestMigration(V1, true, false, null, null));
		runner.migrate();
		assertEquals(1, store.saved.size());
	}
	
	@Test
	void dryRunInvokesMigrationUp() throws SqlException {
		AtomicBoolean upCalled = new AtomicBoolean(false);
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(new TestMigration(V1, false, false, upCalled, null));
		runner.dryRun();
		assertTrue(upCalled.get());
	}
	
	@Test
	void executeAndSaveCommitsAndPersistsViaRealContext() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrate();
		
		assertEquals(1, source.commitCount());
		assertEquals(0, source.rollbackCount());
		assertEquals(1, store.saved.size());
		assertEquals(V1, store.saved.getFirst().version());
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("CREATE TABLE") && sql.contains("test_table")));
	}
	
	@Test
	void executeStatementsSkipsEmptySql() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(new TestMigration(V1, false, true, null, null));
		
		runner.migrate();
		
		assertEquals(1, source.commitCount());
		assertFalse(source.executedSql().contains(""));
	}
	
	@Test
	void joinStatementsWithNullList() {
		assertThrows(NullPointerException.class, () -> invokeJoinStatements(null));
	}
	
	@Test
	void joinStatementsWithEmptyList() throws Throwable {
		assertEquals("", invokeJoinStatements(List.of()));
	}
	
	@Test
	void joinStatementsWithSingleStatement() throws Throwable {
		assertEquals("CREATE TABLE a", invokeJoinStatements(List.of(SqlRendered.of("CREATE TABLE a"))));
	}
	
	@Test
	void joinStatementsWithMultipleStatements() throws Throwable {
		assertEquals("CREATE TABLE a;\nCREATE TABLE b", invokeJoinStatements(List.of(SqlRendered.of("CREATE TABLE a"), SqlRendered.of("CREATE TABLE b"))));
	}
	
	@Test
	void joinStatementsSkipsEmptyStatements() throws Throwable {
		List<SqlRendered> rendered = List.of(SqlRendered.of("CREATE TABLE a"), SqlRendered.of(""), SqlRendered.of("CREATE TABLE b"));
		assertEquals("CREATE TABLE a;\nCREATE TABLE b", invokeJoinStatements(rendered));
	}
	
	@Test
	void joinStatementsWithOnlyEmptyStatements() throws Throwable {
		assertEquals("", invokeJoinStatements(List.of(SqlRendered.of(""), SqlRendered.of(""))));
	}
	
	@Test
	void appliedStatementsHintWithNullStatements() throws Throwable {
		assertEquals("", invokeAppliedStatementsHint(applied(V1, "s1:abc")));
	}
	
	@Test
	void appliedStatementsHintWithEmptyStatements() throws Throwable {
		assertEquals("", invokeAppliedStatementsHint(appliedWithStatements(V1, "s1:abc", "")));
	}
	
	@Test
	void appliedStatementsHintWithStatements() throws Throwable {
		String hint = invokeAppliedStatementsHint(appliedWithStatements(V1, "s1:abc", "CREATE TABLE a"));
		assertTrue(hint.startsWith(", the following sql"));
		assertTrue(hint.contains("CREATE TABLE a"));
	}
	
	@Test
	void isBookkeepingTableWithMigrationTable() throws Throwable {
		assertTrue(invokeIsBookkeepingTable("_sql_migrations"));
	}
	
	@Test
	void isBookkeepingTableWithSchemaColumnsTable() throws Throwable {
		assertTrue(invokeIsBookkeepingTable(SqlDialect.SCHEMA_COLUMNS_TABLE));
		assertTrue(invokeIsBookkeepingTable(SqlDialect.SCHEMA_CHECK_CONSTRAINTS_TABLE));
	}
	
	@Test
	void isBookkeepingTableWithUpperCaseName() throws Throwable {
		assertTrue(invokeIsBookkeepingTable("_SQL_MIGRATIONS"));
	}
	
	@Test
	void isBookkeepingTableWithUserTable() throws Throwable {
		assertFalse(invokeIsBookkeepingTable("users"));
	}
	
	@Test
	void isBookkeepingTableWithSimilarPrefix() throws Throwable {
		assertFalse(invokeIsBookkeepingTable("_sqlusers"));
		assertFalse(invokeIsBookkeepingTable("sql_users"));
	}
	
	@Test
	void describeDifferenceWithChangedJdbcType() throws Throwable {
		String difference = invokeDescribeDifference(columnInfo(Types.INTEGER, true, false, false, false), columnInfo(Types.BIGINT, true, false, false, false));
		assertNotNull(difference);
		assertTrue(difference.contains("jdbc type"));
	}
	
	@Test
	void describeDifferenceWithChangedNullable() throws Throwable {
		String difference = invokeDescribeDifference(columnInfo(Types.INTEGER, true, false, false, false), columnInfo(Types.INTEGER, false, false, false, false));
		assertNotNull(difference);
		assertTrue(difference.contains("nullable"));
	}
	
	@Test
	void describeDifferenceWithChangedAutoIncrement() throws Throwable {
		String difference = invokeDescribeDifference(columnInfo(Types.INTEGER, true, false, false, false), columnInfo(Types.INTEGER, true, true, false, false));
		assertNotNull(difference);
		assertTrue(difference.contains("auto-increment"));
	}
	
	@Test
	void describeDifferenceWithChangedPrimaryKey() throws Throwable {
		String difference = invokeDescribeDifference(columnInfo(Types.INTEGER, true, false, false, false), columnInfo(Types.INTEGER, true, false, true, false));
		assertNotNull(difference);
		assertTrue(difference.contains("primary key"));
	}
	
	@Test
	void describeDifferenceWithChangedUnique() throws Throwable {
		String difference = invokeDescribeDifference(columnInfo(Types.INTEGER, true, false, false, false), columnInfo(Types.INTEGER, true, false, false, true));
		assertNotNull(difference);
		assertTrue(difference.contains("unique"));
	}
	
	@Test
	void describeDifferenceWithIdenticalColumns() throws Throwable {
		assertNull(invokeDescribeDifference(columnInfo(Types.INTEGER, true, false, false, false), columnInfo(Types.INTEGER, true, false, false, false)));
	}
	
	@Test
	void describeDifferenceIgnoresOrdinalPosition() throws Throwable {
		SqlSchemaColumnInfo expected = new SqlSchemaColumnInfo("users", "id", Types.INTEGER, null, true, false, false, false, 1, null);
		SqlSchemaColumnInfo actual = new SqlSchemaColumnInfo("users", "id", Types.INTEGER, null, true, false, false, false, 7, null);
		assertNull(invokeDescribeDifference(expected, actual));
	}
	
	@Test
	void describeDifferenceIgnoresTypeParameter() throws Throwable {
		SqlSchemaColumnInfo expected = new SqlSchemaColumnInfo("users", "id", Types.INTEGER, SqlParameter.length(10), true, false, false, false, 1, "int");
		SqlSchemaColumnInfo actual = new SqlSchemaColumnInfo("users", "id", Types.INTEGER, SqlParameter.length(20), true, false, false, false, 1, "integer");
		assertNull(invokeDescribeDifference(expected, actual));
	}
	
	@Test
	void describeDifferenceReportsFirstDifferenceOnly() throws Throwable {
		String difference = invokeDescribeDifference(columnInfo(Types.INTEGER, true, false, false, false), columnInfo(Types.BIGINT, false, false, false, false));
		assertNotNull(difference);
		assertTrue(difference.contains("jdbc type"));
		assertFalse(difference.contains("nullable"));
	}
	
	@Test
	void constraintNamesWithNullList() throws Throwable {
		assertEquals(Set.of(), invokeConstraintNames(null));
	}
	
	@Test
	void constraintNamesWithEmptyList() throws Throwable {
		assertEquals(Set.of(), invokeConstraintNames(List.of()));
	}
	
	@Test
	void constraintNamesWithConstraints() throws Throwable {
		List<SqlCheckConstraintInfo> constraints = List.of(new SqlCheckConstraintInfo("b_check", "b > 0"), new SqlCheckConstraintInfo("a_check", "a > 0"));
		assertEquals(Set.of("a_check", "b_check"), invokeConstraintNames(constraints));
	}
	
	@Test
	void constraintNamesIgnoresCheckClause() throws Throwable {
		List<SqlCheckConstraintInfo> constraints = List.of(new SqlCheckConstraintInfo("a_check", "a > 0"), new SqlCheckConstraintInfo("a_check", "a > 100"));
		assertEquals(Set.of("a_check"), invokeConstraintNames(constraints));
	}
	
	@Test
	void migrateThenStatusReflectsApplied() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		
		runner.migrate();
		List<SqlMigrationInfo> status = runner.status();
		
		assertEquals(1, status.size());
		assertEquals(SqlMigrationStatus.APPLIED, status.getFirst().status());
	}
	
	@Test
	void registerManyMigrationsKeepsSortedStatusOrder() throws SqlException {
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(migration(Version.of(1, 0, 0)));
		runner.register(migration(Version.of(1, 2, 0)));
		runner.register(migration(Version.of(2, 0, 0)));
		runner.register(migration(Version.of(10, 0, 0)));
		
		List<Version> versions = runner.status().stream().map(SqlMigrationInfo::version).toList();
		List<Version> sorted = new ArrayList<>(versions);
		sorted.sort(Comparator.naturalOrder());
		assertEquals(sorted, versions);
	}
	
	@Test
	void validateWithMatchingChecksumPasses() throws SqlException {
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		runner.migrate();
		
		assertNotNull(store.saved.getFirst().checksum());
		assertDoesNotThrow(runner::validate);
	}
	
	@Test
	void executeAndSaveWrapsFailingDefaultSchema() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SchemaRecordingDialect dialect = new SchemaRecordingDialect(null, true, false);
		SqlMigrationRunner runner = SqlMigrationRunner.of(SqlDatabase.builder(source, dialect).build(), new FakeMigrationStore());
		runner.register(migration(V1));
		
		assertThrows(SqlException.class, runner::migrate);
		assertEquals(0, source.commitCount());
		assertEquals(0, source.rollbackCount());
	}
	
	@Test
	void executeAndSaveResolvesSchemaThroughDialect() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SchemaRecordingDialect dialect = new SchemaRecordingDialect("resolved_schema", false, false);
		SqlMigrationRunner runner = SqlMigrationRunner.of(SqlDatabase.builder(schemaOverriding(source, "connection_schema", null), dialect).build(), new FakeMigrationStore());
		runner.register(migration(V1));
		
		runner.migrate();
		assertFalse(dialect.introspectionSchemas.isEmpty());
		assertTrue(dialect.introspectionSchemas.stream().allMatch("resolved_schema"::equals), String.valueOf(dialect.introspectionSchemas));
	}
	
	@Test
	void executeAndSaveUsesConnectionSchemaWithDefaultDialect() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SchemaRecordingDialect dialect = new SchemaRecordingDialect(null, false, false);
		SqlMigrationRunner runner = SqlMigrationRunner.of(SqlDatabase.builder(schemaOverriding(source, "app", "catalog"), dialect).build(), new FakeMigrationStore());
		runner.register(migration(V1));
		
		runner.migrate();
		assertTrue(dialect.introspectionSchemas.stream().allMatch("app"::equals), String.valueOf(dialect.introspectionSchemas));
	}
	
	@Test
	void executeAndSaveFallsBackToPublicWhenConnectionReportsNoSchema() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SchemaRecordingDialect dialect = new SchemaRecordingDialect(null, false, false);
		SqlMigrationRunner runner = SqlMigrationRunner.of(SqlDatabase.builder(schemaOverriding(source, null, null), dialect).build(), new FakeMigrationStore());
		runner.register(migration(V1));
		
		runner.migrate();
		assertTrue(dialect.introspectionSchemas.stream().allMatch("public"::equals), String.valueOf(dialect.introspectionSchemas));
	}
	
	@Test
	void executeAndSaveWithCatalogScopedDialectResolvesCatalogName() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SchemaRecordingDialect dialect = new SchemaRecordingDialect(null, false, true);
		SqlMigrationRunner runner = SqlMigrationRunner.of(SqlDatabase.builder(schemaOverriding(source, null, "app_db"), dialect).build(), new FakeMigrationStore());
		runner.register(migration(V1));
		
		runner.migrate();
		assertTrue(dialect.introspectionCatalogs.stream().allMatch("app_db"::equals), String.valueOf(dialect.introspectionCatalogs));
		assertTrue(dialect.introspectionSchemas.stream().allMatch(Objects::isNull), String.valueOf(dialect.introspectionSchemas));
	}
	
	@Test
	void migrateWithParameterizedStatementThrowsConflict() throws Throwable {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, new FakeMigrationStore());
		List<SqlRendered> statements = List.of(new SqlRendered(List.of("ALTER TABLE t ADD CONSTRAINT c CHECK(x >= ?)"), List.of(Pair.of(SqlTestFixtures.INTEGER_TYPE, 0))));
		
		SqlMigrationConflictException thrown = assertThrows(SqlMigrationConflictException.class, () -> invokeExecuteStatements(runner, source, statements));
		assertTrue(thrown.getMessage().contains("CHECK(x >= ?)"));
		assertTrue(thrown.getMessage().contains("1 bind parameter"));
	}
	
	@Test
	void migrateWithParameterizedStatementDoesNotExecuteIt() throws Throwable {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, new FakeMigrationStore());
		List<SqlRendered> statements = List.of(new SqlRendered(List.of("CREATE INDEX i ON t(x) WHERE x = ?"), List.of(Pair.of(SqlTestFixtures.INTEGER_TYPE, 1))));
		
		assertThrows(SqlMigrationConflictException.class, () -> invokeExecuteStatements(runner, source, statements));
		assertFalse(source.executedSql().stream().anyMatch(sql -> sql.contains("CREATE INDEX i")));
	}
	
	@Test
	void migrateWithParameterizedStatementAfterValidOneStopsAtFirstOffender() throws Throwable {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, new FakeMigrationStore());
		List<SqlRendered> statements = List.of(
			SqlRendered.of("CREATE TABLE first_table(id INTEGER)"),
			new SqlRendered(List.of("ALTER TABLE first_table ADD CONSTRAINT c CHECK(id >= ?)"), List.of(Pair.of(SqlTestFixtures.INTEGER_TYPE, 0)))
		);
		
		assertThrows(SqlMigrationConflictException.class, () -> invokeExecuteStatements(runner, source, statements));
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("CREATE TABLE first_table")));
		assertFalse(source.executedSql().stream().anyMatch(sql -> sql.contains("ADD CONSTRAINT")));
	}
	
	@Test
	void migrateWithMultipleParametersReportsCount() throws Throwable {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, new FakeMigrationStore());
		List<SqlRendered> statements = List.of(new SqlRendered(
			List.of("ALTER TABLE t ADD CONSTRAINT c CHECK(x BETWEEN ? AND ?)"),
			List.of(Pair.of(SqlTestFixtures.INTEGER_TYPE, 1), Pair.of(SqlTestFixtures.INTEGER_TYPE, 5))
		));
		
		SqlMigrationConflictException thrown = assertThrows(SqlMigrationConflictException.class, () -> invokeExecuteStatements(runner, source, statements));
		assertTrue(thrown.getMessage().contains("2 bind parameter"));
	}
	
	@Test
	void migrateWithParameterlessStatementExecutes() throws Throwable {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, new FakeMigrationStore());
		
		invokeExecuteStatements(runner, source, List.of(SqlRendered.of("CREATE TABLE plain_table(id INTEGER)")));
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("CREATE TABLE plain_table")));
	}
	
	@Test
	void migrateWithCheckConstraintProducesNoParameters() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		FakeMigrationStore store = new FakeMigrationStore();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, store);
		runner.register(new CheckMigration(V1));
		
		assertDoesNotThrow(runner::migrate);
		assertEquals(1, store.saved.size());
		assertTrue(source.executedSql().stream().anyMatch(sql -> sql.contains("CHECK") && sql.contains(">= 0")));
		assertTrue(source.executedSql().stream().filter(sql -> sql.startsWith("CREATE TABLE") || sql.startsWith("ALTER TABLE")).noneMatch(sql -> sql.contains("?")));
	}
	
	@Test
	void dryRunProducesNoParameterizedStatements() throws SqlException {
		RecordingDataSource source = SqlTestFixtures.recordingDataSource();
		SqlMigrationRunner runner = runner(source, SqlDialects.POSTGRESQL, new FakeMigrationStore());
		runner.register(new CheckMigration(V1));
		
		List<SqlRendered> rendered = runner.dryRun();
		assertFalse(rendered.isEmpty());
		for (SqlRendered statement : rendered) {
			assertTrue(statement.parameters().isEmpty(), "Statement carries parameters: " + statement.sql());
			assertFalse(statement.sql().contains("?"), "Statement carries a placeholder: " + statement.sql());
		}
	}
	
	/**
	 * A PostgreSQL dialect that records the schema and catalog the runner routes into metadata introspection, which is
	 * the only observable trace of the schema {@code defaultSchema(Connection)} resolved for the post-migration snapshot.<br>
	 */
	private static final class SchemaRecordingDialect extends PostgresSqlDialect {
		
		private final List<String> introspectionSchemas = new ArrayList<>();
		private final List<String> introspectionCatalogs = new ArrayList<>();
		private final String forcedSchema;
		private final boolean failOnDefaultSchema;
		private final boolean schemaAsCatalog;
		
		private SchemaRecordingDialect(@Nullable String forcedSchema, boolean failOnDefaultSchema, boolean schemaAsCatalog) {
			this.forcedSchema = forcedSchema;
			this.failOnDefaultSchema = failOnDefaultSchema;
			this.schemaAsCatalog = schemaAsCatalog;
		}
		
		@Override
		public @NonNull String defaultSchema(@NonNull Connection connection) throws SqlException {
			if (this.failOnDefaultSchema) {
				throw new SqlSchemaIntrospectionException("Failed to resolve the default schema in tests", new SQLException("Schema lookup failed in tests"));
			}
			return this.forcedSchema != null ? this.forcedSchema : super.defaultSchema(connection);
		}
		
		@Override
		public @Nullable String introspectionCatalog(@NonNull String schema) {
			String catalog = this.schemaAsCatalog ? schema : super.introspectionCatalog(schema);
			this.introspectionCatalogs.add(catalog);
			return catalog;
		}
		
		@Override
		public @Nullable String introspectionSchema(@NonNull String schema) {
			String resolved = this.schemaAsCatalog ? null : super.introspectionSchema(schema);
			this.introspectionSchemas.add(resolved);
			return resolved;
		}
	}
	
	private static final class FakeMigrationStore implements SqlMigrationStore {
		
		private final List<SqlMigrationInfo> infos = new ArrayList<>();
		private final List<SqlMigrationInfo> saved = new ArrayList<>();
		private final List<Pair<Version, SqlMigrationStatus>> updated = new ArrayList<>();
		private boolean initialized;
		
		private FakeMigrationStore(SqlMigrationInfo @NonNull ... seed) {
			this.infos.addAll(Arrays.asList(seed));
		}
		
		@Override
		public void initialize() {
			this.initialized = true;
		}
		
		@Override
		public @NonNull List<SqlMigrationInfo> loadAll() {
			return List.copyOf(this.infos);
		}
		
		@Override
		public void save(@NonNull SqlMigrationInfo info) {
			this.saved.add(info);
			this.infos.add(info);
		}
		
		@Override
		public void update(@NonNull Version version, @NonNull SqlMigrationStatus status) {
			this.updated.add(Pair.of(version, status));
			this.infos.removeIf(info -> info.version().equals(version));
			this.infos.add(new SqlMigrationInfo(version, "Migration " + version, status, null, null));
		}
	}
	
	private record TestMigration(Version version, boolean nonAtomic, boolean dataOperation, @Nullable AtomicBoolean upCalled, @Nullable AtomicBoolean downCalled) implements SqlMigration {
		
		private TestMigration(@NonNull Version version, boolean nonAtomic, boolean dataOperation, @Nullable AtomicBoolean upCalled, @Nullable AtomicBoolean downCalled) {
			this.version = version;
			this.nonAtomic = nonAtomic;
			this.dataOperation = dataOperation;
			this.upCalled = upCalled;
			this.downCalled = downCalled;
		}
		
		@Override
		public @NonNull String description() {
			return "Migration " + this.version;
		}
		
		@Override
		public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException {
			if (this.upCalled != null) {
				this.upCalled.set(true);
			}
			if (this.dataOperation) {
				builder.data(SqlTestFixtures.sampleTable(), _ -> {});
			} else {
				builder.createTable(SqlTestFixtures.sampleTable(), table -> table.column(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE));
			}
		}
		
		@Override
		public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
			if (this.downCalled != null) {
				this.downCalled.set(true);
			}
			builder.dropTable(SqlTestFixtures.sampleTable());
		}
		
		@Override
		public boolean allowsNonAtomicExecution() {
			return this.nonAtomic;
		}
	}
	
	/**
	 * Migration that creates a table carrying a value check constraint and a partial index, the operations
	 * whose values must be rendered as literals rather than bind parameters.
	 *
	 * @param version The version of this migration
	 */
	private record CheckMigration(Version version) implements SqlMigration {
		
		@Override
		public @NonNull String description() {
			return "Check migration " + this.version;
		}
		
		@Override
		public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
			SqlTable<Object> table = SqlTestFixtures.sampleTable();
			SqlColumn<Object, Integer> id = table.column("id", SqlTestFixtures.INTEGER_TYPE, _ -> 0);
			builder.createTable(table, definition -> definition.column(id, SqlTestFixtures.INTEGER_TYPE));
			builder.addCheckConstraint(table, "sample_id_check", Sql.greaterThanOrEqualTo(id, 0));
		}
		
		@Override
		public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {
			builder.dropTable(SqlTestFixtures.sampleTable());
		}
		
		@Override
		public boolean allowsNonAtomicExecution() {
			return true;
		}
	}
}
