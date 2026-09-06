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

package net.luis.utils.io.database.migration.store;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.SqlTestFixtures.RecordingDataSource;
import net.luis.utils.io.database.exception.database.SqlMigrationExecutionException;
import net.luis.utils.io.database.migration.SqlMigrationInfo;
import net.luis.utils.io.database.migration.SqlMigrationStatus;
import net.luis.utils.util.Version;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationTableStore}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationTableStoreTest {
	
	private static final Instant APPLIED_AT = Instant.ofEpochSecond(1_700_000_000L);
	private static final long APPLIED_AT_MILLIS = APPLIED_AT.toEpochMilli();
	
	private static SqlMigrationTableStore store(DataSource dataSource) {
		return new SqlMigrationTableStore(dataSource, SqlTestFixtures.DIALECT);
	}
	
	private static SqlMigrationInfo info(SqlMigrationStatus status, Instant appliedAt, String checksum) {
		return new SqlMigrationInfo(Version.of(1, 0, 0), "init", status, appliedAt, checksum);
	}
	
	private static Map<String, Object> migrationRow(String version, String status, Object appliedAt) {
		Map<String, Object> row = new HashMap<>();
		row.put("version", version);
		row.put("description", "desc");
		row.put("status", status);
		if (appliedAt != null) {
			row.put("applied_at", appliedAt);
		}
		return row;
	}
	
	private static boolean recorded(RecordingDataSource dataSource, String fragment) {
		return dataSource.executedSql().stream().anyMatch(sql -> sql.contains(fragment) && sql.contains("_sql_migrations"));
	}
	
	@Test
	void constructWithDataSourceAndDialect() {
		assertDoesNotThrow(() -> new SqlMigrationTableStore(SqlTestFixtures.failingDataSource(), SqlTestFixtures.DIALECT));
	}
	
	@Test
	void constructWithNullDataSource() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableStore(null, SqlTestFixtures.DIALECT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableStore(SqlTestFixtures.failingDataSource(), null));
	}
	
	@Test
	void saveWithNullInfo() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(null));
	}
	
	@Test
	void saveWithConnectionNullConnection() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(null, info(SqlMigrationStatus.PENDING, null, "abc")));
	}
	
	@Test
	void saveWithConnectionNullInfo() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(SqlTestFixtures.placeholderConnection(), null));
	}
	
	@Test
	void updateWithNullVersion() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).update(null, SqlMigrationStatus.APPLIED));
	}
	
	@Test
	void updateWithNullStatus() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).update(Version.of(1, 0, 0), null));
	}
	
	@Test
	void updateWithConnectionNullConnection() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).update(null, Version.of(1, 0, 0), SqlMigrationStatus.APPLIED));
	}
	
	@Test
	void updateWithConnectionNullVersion() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).update(SqlTestFixtures.placeholderConnection(), null, SqlMigrationStatus.APPLIED));
	}
	
	@Test
	void updateWithConnectionNullStatus() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).update(SqlTestFixtures.placeholderConnection(), Version.of(1, 0, 0), null));
	}
	
	@Test
	void initializeWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).initialize());
	}
	
	@Test
	void loadAllWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).loadAll());
	}
	
	@Test
	void saveWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).save(info(SqlMigrationStatus.PENDING, null, "abc")));
	}
	
	@Test
	void saveWithConnectionWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).save(SqlTestFixtures.throwingConnection(), info(SqlMigrationStatus.PENDING, null, "abc")));
	}
	
	@Test
	void updateWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).update(Version.of(1, 0, 0), SqlMigrationStatus.APPLIED));
	}
	
	@Test
	void updateWithConnectionWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).update(SqlTestFixtures.throwingConnection(), Version.of(1, 0, 0), SqlMigrationStatus.APPLIED));
	}
	
	@Test
	void loadAllPropagatesInvalidStatus() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(migrationRow("1.0.0", "BOGUS", null))));
		assertThrows(IllegalArgumentException.class, () -> store(dataSource).loadAll());
	}
	
	@Test
	void initializeExecutesCreateTable() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).initialize();
		
		List<String> executed = dataSource.executedSql();
		assertEquals(2, executed.size());
		assertTrue(executed.get(0).contains("CREATE TABLE IF NOT EXISTS"));
		assertTrue(executed.get(0).contains("_sql_migrations"));
		assertTrue(executed.get(1).contains("WHERE 1 = 0"));
	}
	
	@Test
	void initializeExecutesUpgradeProbe() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).initialize();
		
		assertTrue(recorded(dataSource, "WHERE 1 = 0"));
		assertTrue(recorded(dataSource, "SELECT *"));
	}
	
	@Test
	void initializeCreateTableDeclaresStatementsColumn() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).initialize();
		
		assertTrue(dataSource.executedSql().getFirst().contains("statements"));
	}
	
	@Test
	void initializeSkipsAddColumnWhenMetadataUnavailable() throws Exception {
		ProbingDataSource probe = new ProbingDataSource(null);
		new SqlMigrationTableStore(probe.dataSource(), SqlTestFixtures.DIALECT).initialize();
		
		assertTrue(probe.executed.stream().noneMatch(sql -> sql.contains("ADD COLUMN")));
	}
	
	@Test
	void initializeSkipsAddColumnWhenStatementsPresent() throws Exception {
		ProbingDataSource probe = new ProbingDataSource(new ArrayList<>(List.of("version", "description", "status", "applied_at", "checksum", "statements")));
		new SqlMigrationTableStore(probe.dataSource(), SqlTestFixtures.DIALECT).initialize();
		
		assertTrue(probe.executed.stream().noneMatch(sql -> sql.contains("ADD COLUMN")));
	}
	
	@Test
	void initializeAddsStatementsColumnWhenMissing() throws Exception {
		ProbingDataSource probe = new ProbingDataSource(new ArrayList<>(List.of("version", "description", "status", "applied_at", "checksum")));
		new SqlMigrationTableStore(probe.dataSource(), SqlTestFixtures.DIALECT).initialize();
		
		String altered = probe.executed.stream().filter(sql -> sql.contains("ADD COLUMN")).findFirst().orElseThrow();
		assertTrue(altered.contains("ALTER TABLE"));
		assertTrue(altered.contains("_sql_migrations"));
		assertTrue(altered.contains("statements"));
	}
	
	@Test
	void initializeAddsStatementsColumnWithEmptyMetadata() throws Exception {
		ProbingDataSource probe = new ProbingDataSource(new ArrayList<>());
		new SqlMigrationTableStore(probe.dataSource(), SqlTestFixtures.DIALECT).initialize();
		
		assertTrue(probe.executed.stream().anyMatch(sql -> sql.contains("ADD COLUMN")));
	}
	
	@Test
	void initializeMatchesStatementsColumnCaseInsensitively() throws Exception {
		ProbingDataSource probe = new ProbingDataSource(new ArrayList<>(List.of("VERSION", "STATEMENTS")));
		new SqlMigrationTableStore(probe.dataSource(), SqlTestFixtures.DIALECT).initialize();
		
		assertTrue(probe.executed.stream().noneMatch(sql -> sql.contains("ADD COLUMN")));
	}
	
	@Test
	void initializeIsIdempotent() throws Exception {
		ProbingDataSource probe = new ProbingDataSource(new ArrayList<>(List.of("version", "description", "status", "applied_at", "checksum")));
		SqlMigrationTableStore store = new SqlMigrationTableStore(probe.dataSource(), SqlTestFixtures.DIALECT);
		
		store.initialize();
		store.initialize();
		
		assertEquals(1, probe.executed.stream().filter(sql -> sql.contains("ADD COLUMN")).count());
	}
	
	@Test
	void initializeWithUpgradeProbeFailure() {
		ProbingDataSource probe = new ProbingDataSource(new ArrayList<>());
		probe.failProbe = true;
		
		SqlMigrationTableStore store = new SqlMigrationTableStore(probe.dataSource(), SqlTestFixtures.DIALECT);
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class, store::initialize);
		assertTrue(exception.getMessage().contains("upgrade the migration table"));
	}
	
	@Test
	void loadAllSelectsStatementsColumn() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).loadAll();
		
		assertTrue(recorded(dataSource, "statements"));
		assertTrue(recorded(dataSource, "SELECT"));
	}
	
	@Test
	void loadAllReadsStatements() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> row = migrationRow("1.0.0", "APPLIED", APPLIED_AT_MILLIS);
		row.put("statements", "CREATE TABLE a;\nCREATE TABLE b");
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(row)));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(1, result.size());
		assertEquals("CREATE TABLE a;\nCREATE TABLE b", result.getFirst().statements());
	}
	
	@Test
	void loadAllWithNullStatements() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(migrationRow("1.0.0", "APPLIED", APPLIED_AT_MILLIS))));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(1, result.size());
		assertNull(result.getFirst().statements());
		assertEquals(SqlMigrationStatus.APPLIED, result.getFirst().status());
	}
	
	@Test
	void saveInsertBindsSixParameters() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 0, 0 }, null, null);
		SqlMigrationInfo info = new SqlMigrationInfo(Version.of(1, 0, 0), "init", SqlMigrationStatus.APPLIED, APPLIED_AT, "s1:abc", "CREATE TABLE a");
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info);
		
		assertTrue(recorder.prepared.get(1).startsWith("INSERT INTO"));
		assertEquals(6, recorder.prepared.get(1).chars().filter(c -> c == '?').count());
		assertEquals(Arrays.asList(Version.of(1, 0, 0).toString(), "init", "APPLIED", APPLIED_AT_MILLIS, "s1:abc", "CREATE TABLE a"), recorder.parameters.get(1));
	}
	
	@Test
	void saveOverwriteBindsStatementsBeforeVersion() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		SqlMigrationInfo info = new SqlMigrationInfo(Version.of(1, 0, 0), "init", SqlMigrationStatus.APPLIED, APPLIED_AT, "s1:abc", "CREATE TABLE a");
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info);
		
		assertEquals(Arrays.asList("init", "APPLIED", APPLIED_AT_MILLIS, "s1:abc", "CREATE TABLE a", Version.of(1, 0, 0).toString()), recorder.parameters.getFirst());
	}
	
	@Test
	void saveWithNullStatements() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		assertDoesNotThrow(() -> store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "s1:abc")));
		
		assertNull(recorder.parameters.getFirst().get(4));
	}
	
	@Test
	void saveWithMultiStatementText() throws Exception {
		String statements = "ALTER TABLE \"a\" ADD COLUMN \"b\" TEXT;\nCREATE INDEX \"i\" ON \"a\" (\"b\")";
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		SqlMigrationInfo info = new SqlMigrationInfo(Version.of(1, 0, 0), "init", SqlMigrationStatus.APPLIED, APPLIED_AT, "s1:abc", statements);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info);
		
		assertEquals(statements, recorder.parameters.getFirst().get(4));
	}
	
	@Test
	void saveRoundTripWithStatements() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 0, 0 }, null, null);
		SqlMigrationInfo saved = new SqlMigrationInfo(Version.of(1, 0, 0), "desc", SqlMigrationStatus.APPLIED, APPLIED_AT, "s1:abc", "CREATE TABLE a");
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), saved);
		
		List<Object> bound = recorder.parameters.get(1);
		Map<String, Object> row = migrationRow((String) bound.get(0), (String) bound.get(2), bound.get(3));
		row.put("checksum", bound.get(4));
		row.put("statements", bound.get(5));
		
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(row)));
		SqlMigrationInfo loaded = store(dataSource).loadAll().getFirst();
		assertEquals(saved.version(), loaded.version());
		assertEquals(saved.checksum(), loaded.checksum());
		assertEquals(saved.statements(), loaded.statements());
	}
	
	@Test
	void loadAllReturnsEmptyWhenNoRows() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void loadAllReadsSingleAppliedRow() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(migrationRow("1.0.0", "APPLIED", APPLIED_AT_MILLIS))));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(1, result.size());
		assertEquals(SqlMigrationStatus.APPLIED, result.get(0).status());
		assertEquals(APPLIED_AT, result.get(0).appliedAt());
	}
	
	@Test
	void loadAllReadsRowWithNullAppliedAt() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(migrationRow("1.0.0", "PENDING", null))));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(1, result.size());
		assertNull(result.get(0).appliedAt());
	}
	
	@Test
	void loadAllSortsRowsByVersion() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(
			migrationRow("2.0.0", "APPLIED", null),
			migrationRow("1.0.0", "APPLIED", null),
			migrationRow("1.5.0", "APPLIED", null)
		)));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(Version.parse("1.0.0"), result.get(0).version());
		assertEquals(Version.parse("1.5.0"), result.get(1).version());
		assertEquals(Version.parse("2.0.0"), result.get(2).version());
	}
	
	@Test
	void saveWithConnectionRecordsInsert() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).save(dataSource.getConnection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		assertTrue(recorded(dataSource, "INSERT INTO"));
	}
	
	@Test
	void saveWithConnectionNullAppliedAt() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), info(SqlMigrationStatus.PENDING, null, "abc")));
		assertTrue(recorded(dataSource, "INSERT INTO"));
	}
	
	@Test
	void updateWithConnectionAppliedSetsTimestamp() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).update(dataSource.getConnection(), Version.of(1, 0, 0), SqlMigrationStatus.APPLIED));
		assertTrue(recorded(dataSource, "UPDATE"));
	}
	
	@Test
	void updateWithConnectionPendingLeavesTimestampNull() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).update(dataSource.getConnection(), Version.of(1, 0, 0), SqlMigrationStatus.PENDING));
		assertTrue(recorded(dataSource, "UPDATE"));
	}
	
	@Test
	void updateWithConnectionRolledBackLeavesTimestampNull() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).update(dataSource.getConnection(), Version.of(1, 0, 0), SqlMigrationStatus.ROLLED_BACK));
		assertTrue(recorded(dataSource, "UPDATE"));
	}
	
	@Test
	void saveOpensConnectionAndDelegates() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).save(info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		assertTrue(recorded(dataSource, "INSERT INTO"));
	}
	
	@Test
	void updateOpensConnectionAndDelegates() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).update(Version.of(1, 0, 0), SqlMigrationStatus.APPLIED);
		assertTrue(recorded(dataSource, "UPDATE"));
	}
	
	@Test
	void loadAllReadsMultipleRows() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(
			migrationRow("1.0.0", "APPLIED", APPLIED_AT_MILLIS),
			migrationRow("1.1.0", "PENDING", null),
			migrationRow("1.2.0", "ROLLED_BACK", null)
		)));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(3, result.size());
		assertEquals(SqlMigrationStatus.APPLIED, result.get(0).status());
		assertEquals(SqlMigrationStatus.PENDING, result.get(1).status());
		assertEquals(SqlMigrationStatus.ROLLED_BACK, result.get(2).status());
	}
	
	@Test
	void saveWithConnectionWrapsOverwriteSqlException() {
		RecordingConnection recorder = new RecordingConnection(new int[0], "UPDATE", null);
		SqlMigrationExecutionException thrown = assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc")));
		assertTrue(thrown.getMessage().contains(Version.of(1, 0, 0).toString()));
		assertInstanceOf(SQLException.class, thrown.getCause());
		assertTrue(recorder.prepared.stream().noneMatch(sql -> sql.startsWith("INSERT")));
	}
	
	@Test
	void saveWithConnectionWrapsOverwriteExecuteException() {
		RecordingConnection recorder = new RecordingConnection(new int[0], null, "UPDATE");
		SqlMigrationExecutionException thrown = assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc")));
		assertTrue(thrown.getMessage().contains(Version.of(1, 0, 0).toString()));
		assertTrue(recorder.prepared.stream().noneMatch(sql -> sql.startsWith("INSERT")));
	}
	
	@Test
	void saveWithConnectionOverwritesExistingRow() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		
		assertEquals(1, recorder.prepared.size());
		assertTrue(recorder.prepared.getFirst().startsWith("UPDATE"), recorder.prepared.getFirst());
		assertTrue(recorder.prepared.getFirst().contains("WHERE \"version\" = ?"), recorder.prepared.getFirst());
	}
	
	@Test
	void saveWithConnectionInsertsWhenNoRowUpdated() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 0, 0 }, null, null);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		
		assertEquals(2, recorder.prepared.size());
		assertTrue(recorder.prepared.get(0).startsWith("UPDATE"), recorder.prepared.get(0));
		assertTrue(recorder.prepared.get(1).startsWith("INSERT INTO"), recorder.prepared.get(1));
		assertEquals(Arrays.asList(Version.of(1, 0, 0).toString(), "init", "APPLIED", APPLIED_AT_MILLIS, "abc", null), recorder.parameters.get(1));
	}
	
	@Test
	void saveWithConnectionOverwriteWithNullAppliedAt() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.PENDING, null, "abc"));
		assertNull(recorder.parameters.getFirst().get(2));
	}
	
	@Test
	void saveWithConnectionOverwriteWithAppliedAt() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		assertEquals(APPLIED_AT_MILLIS, recorder.parameters.getFirst().get(2));
	}
	
	@Test
	void saveWithConnectionOverwriteBindsParametersInOrder() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		assertEquals(Arrays.asList("init", "APPLIED", APPLIED_AT_MILLIS, "abc", null, Version.of(1, 0, 0).toString()), recorder.parameters.getFirst());
	}
	
	@Test
	void saveWithConnectionOverwriteQuotesIdentifiers() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		
		String sql = recorder.prepared.getFirst();
		for (String identifier : List.of("_sql_migrations", "description", "status", "applied_at", "checksum", "version")) {
			assertTrue(sql.contains("\"" + identifier + "\""), sql);
		}
	}
	
	@Test
	void saveWithConnectionOverwriteWithNullChecksum() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		assertDoesNotThrow(() -> store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.PENDING, null, null)));
		assertNull(recorder.parameters.getFirst().get(3));
	}
	
	@Test
	void saveTwiceForSameVersionUpdatesSecondTime() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 0, 0, 1 }, null, null);
		SqlMigrationTableStore store = store(SqlTestFixtures.failingDataSource());
		store.save(recorder.connection(), info(SqlMigrationStatus.PENDING, null, "abc"));
		store.save(recorder.connection(), info(SqlMigrationStatus.APPLIED, APPLIED_AT, "abc"));
		
		assertEquals(3, recorder.prepared.size());
		assertEquals(1, recorder.prepared.stream().filter(sql -> sql.startsWith("INSERT INTO")).count());
		assertTrue(recorder.prepared.get(2).startsWith("UPDATE"), recorder.prepared.get(2));
	}
	
	@Test
	void saveWithChangedStatusOverwritesStatusAndAppliedAt() throws Exception {
		RecordingConnection recorder = new RecordingConnection(new int[] { 1 }, null, null);
		store(SqlTestFixtures.failingDataSource()).save(recorder.connection(), info(SqlMigrationStatus.ROLLED_BACK, null, "abc"));
		
		List<Object> parameters = recorder.parameters.getFirst();
		assertEquals("ROLLED_BACK", parameters.get(1));
		assertNull(parameters.get(2));
		assertEquals(Version.of(1, 0, 0).toString(), parameters.get(5));
	}
	
	@Test
	void saveWithNullChecksum() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), info(SqlMigrationStatus.PENDING, null, null)));
		assertTrue(recorded(dataSource, "INSERT INTO"));
	}
	
	@Test
	void loadAllRoundTripsAllFields() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> row = new HashMap<>();
		row.put("version", "1.2.3");
		row.put("description", "create users");
		row.put("status", "APPLIED");
		row.put("applied_at", APPLIED_AT_MILLIS);
		row.put("checksum", "deadbeef");
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(row)));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(1, result.size());
		assertEquals(new SqlMigrationInfo(Version.parse("1.2.3"), "create users", SqlMigrationStatus.APPLIED, APPLIED_AT, "deadbeef"), result.get(0));
	}
	
	@Test
	void loadAllSortsAndReadsTimestampsTogether() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(
			migrationRow("3.0.0", "PENDING", null),
			migrationRow("1.0.0", "APPLIED", APPLIED_AT_MILLIS),
			migrationRow("2.0.0", "PENDING", null)
		)));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(Version.parse("1.0.0"), result.get(0).version());
		assertEquals(APPLIED_AT, result.get(0).appliedAt());
		assertNull(result.get(1).appliedAt());
		assertNull(result.get(2).appliedAt());
	}
	
	/**
	 * Hand-written {@link Connection} stub that records every prepared statement with its bound parameters and lets a
	 * test decide, per statement, how many rows {@code executeUpdate} reports — which is what selects the overwrite
	 * branch of {@code save(Connection, SqlMigrationInfo)}.<br>
	 */
	/**
	 * A data source whose upgrade probe reports the given column labels, which is what drives the conditional
	 * {@code ADD COLUMN}. A null column list makes {@code getMetaData()} return null, the case in which the store must
	 * assume the column is present rather than altering blindly.
	 */
	private static final class ProbingDataSource {
		
		private final List<String> executed = new ArrayList<>();
		private final List<String> columns;
		private boolean failProbe;
		
		private ProbingDataSource(List<String> columns) {
			this.columns = columns;
		}
		
		private DataSource dataSource() {
			return (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class<?>[] { DataSource.class }, (proxy, method, args) ->
				"getConnection".equals(method.getName()) ? this.connection() : null
			);
		}
		
		private Connection connection() {
			return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, (proxy, method, args) ->
				"createStatement".equals(method.getName()) ? this.statement() : null
			);
		}
		
		private Object statement() {
			return Proxy.newProxyInstance(Statement.class.getClassLoader(), new Class<?>[] { Statement.class }, (proxy, method, args) -> switch (method.getName()) {
				case "execute" -> {
					String sql = (String) args[0];
					this.executed.add(sql);
					if (sql.contains("ADD COLUMN") && this.columns != null) {
						this.columns.add("statements");
					}
					yield true;
				}
				case "executeQuery" -> {
					if (this.failProbe) {
						throw new SQLException("Probe failed in tests");
					}
					this.executed.add((String) args[0]);
					yield this.resultSet();
				}
				default -> null;
			});
		}
		
		private Object resultSet() {
			return Proxy.newProxyInstance(ResultSet.class.getClassLoader(), new Class<?>[] { ResultSet.class }, (proxy, method, args) -> switch (method.getName()) {
				case "getMetaData" -> this.columns == null ? null : this.metaData();
				case "next" -> false;
				default -> null;
			});
		}
		
		private Object metaData() {
			return Proxy.newProxyInstance(ResultSetMetaData.class.getClassLoader(), new Class<?>[] { ResultSetMetaData.class }, (proxy, method, args) -> switch (method.getName()) {
				case "getColumnCount" -> this.columns.size();
				case "getColumnLabel" -> this.columns.get((Integer) args[0] - 1);
				default -> null;
			});
		}
	}
	
	private static final class RecordingConnection {
		
		private final List<String> prepared = new ArrayList<>();
		private final List<List<Object>> parameters = new ArrayList<>();
		private final int[] updateCounts;
		private final String failingPrepareFragment;
		private final String failingExecuteFragment;
		
		private RecordingConnection(int[] updateCounts, String failingPrepareFragment, String failingExecuteFragment) {
			this.updateCounts = updateCounts.clone();
			this.failingPrepareFragment = failingPrepareFragment;
			this.failingExecuteFragment = failingExecuteFragment;
		}
		
		private Connection connection() {
			return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, (proxy, method, args) -> {
				if (!"prepareStatement".equals(method.getName())) {
					return "toString".equals(method.getName()) ? "RecordingConnection" : null;
				}
				String sql = (String) args[0];
				if (this.failingPrepareFragment != null && sql.startsWith(this.failingPrepareFragment)) {
					throw new SQLException("Prepare failed in tests");
				}
				this.prepared.add(sql);
				this.parameters.add(new ArrayList<>());
				return this.statement(sql, this.prepared.size() - 1);
			});
		}
		
		private Object statement(String sql, int index) {
			return Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(), new Class<?>[] { PreparedStatement.class }, (proxy, method, args) -> switch (method.getName()) {
				case "setString", "setObject" -> {
					List<Object> bound = this.parameters.get(index);
					int position = (Integer) args[0] - 1;
					while (bound.size() <= position) {
						bound.add(null);
					}
					bound.set(position, args[1]);
					yield null;
				}
				case "executeUpdate" -> {
					if (this.failingExecuteFragment != null && sql.startsWith(this.failingExecuteFragment)) {
						throw new SQLException("Execute failed in tests");
					}
					yield index < this.updateCounts.length ? this.updateCounts[index] : 0;
				}
				case "toString" -> "RecordingStatement";
				default -> null;
			});
		}
	}
}
