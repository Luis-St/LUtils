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

import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.SqlTestFixtures.RecordingDataSource;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlMigrationConflictException;
import net.luis.utils.io.database.exception.database.SqlMigrationExecutionException;
import net.luis.utils.io.database.migration.store.SqlMigrationStore;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.util.Pair;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	
	private static @NonNull String invokeStableParameterValue(@Nullable Object value) throws Throwable {
		Method method = SqlMigrationRunner.class.getDeclaredMethod("stableParameterValue", Object.class);
		method.setAccessible(true);
		try {
			return (String) method.invoke(null, value);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
	
	private static @NonNull String invokeArrayToString(@Nullable Object array) throws Throwable {
		Method method = SqlMigrationRunner.class.getDeclaredMethod("arrayToString", Object.class);
		method.setAccessible(true);
		try {
			return (String) method.invoke(null, array);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
	
	private static @NonNull String invokeComputeChecksum(@NonNull List<SqlRendered> rendered) throws Throwable {
		Method method = SqlMigrationRunner.class.getDeclaredMethod("computeChecksum", List.class);
		method.setAccessible(true);
		try {
			return (String) method.invoke(null, rendered);
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
		FakeMigrationStore store = new FakeMigrationStore(applied(V1, "0000"));
		SqlMigrationRunner runner = runner(SqlTestFixtures.recordingDataSource(), SqlDialects.POSTGRESQL, store);
		runner.register(migration(V1));
		SqlMigrationConflictException exception = assertThrows(SqlMigrationConflictException.class, runner::validate);
		assertTrue(exception.getMessage().contains("checksum mismatch"));
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
	void stableParameterValueWithNull() throws Throwable {
		assertEquals("null", invokeStableParameterValue(null));
	}
	
	@Test
	void stableParameterValueWithByteArray() throws Throwable {
		assertEquals("0a1b", invokeStableParameterValue(new byte[] { 0x0a, 0x1b }));
	}
	
	@Test
	void stableParameterValueWithNestedArray() throws Throwable {
		assertEquals("[1, 2]", invokeStableParameterValue(new int[] { 1, 2 }));
	}
	
	@Test
	void stableParameterValueWithScalar() throws Throwable {
		assertEquals("42", invokeStableParameterValue(42));
	}
	
	@Test
	void arrayToStringWithEmptyArray() throws Throwable {
		assertEquals("[]", invokeArrayToString(new int[0]));
	}
	
	@Test
	void arrayToStringWithNullArray() {
		assertThrows(NullPointerException.class, () -> invokeArrayToString(null));
	}
	
	@Test
	void computeChecksumIsDeterministicAndHex() throws Throwable {
		List<SqlRendered> rendered = List.of(new SqlRendered(List.of("INSERT INTO t VALUES (?)"), List.of(Pair.of(SqlTestFixtures.INTEGER_TYPE, 42))));
		String first = invokeComputeChecksum(rendered);
		String second = invokeComputeChecksum(rendered);
		assertEquals(64, first.length());
		assertTrue(first.matches("[0-9a-f]{64}"));
		assertEquals(first, second);
		assertNotEquals(first, invokeComputeChecksum(List.of(SqlRendered.of("INSERT INTO other VALUES (1)"))));
	}
	
	@Test
	void computeChecksumWithEmptyParameters() throws Throwable {
		String checksum = invokeComputeChecksum(List.of(SqlRendered.of("CREATE TABLE t (id INTEGER)")));
		assertEquals(64, checksum.length());
		assertTrue(checksum.matches("[0-9a-f]{64}"));
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
				builder.data(SqlTestFixtures.sampleTable(), query -> {});
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
}
