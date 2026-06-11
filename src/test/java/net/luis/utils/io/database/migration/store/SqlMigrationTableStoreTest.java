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
		assertEquals(1, executed.size());
		assertTrue(executed.get(0).contains("CREATE TABLE IF NOT EXISTS"));
		assertTrue(executed.get(0).contains("_sql_migrations"));
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
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(migrationRow("1.0.0", "APPLIED", APPLIED_AT))));
		
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
			migrationRow("1.0.0", "APPLIED", APPLIED_AT),
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
		row.put("applied_at", APPLIED_AT);
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
			migrationRow("1.0.0", "APPLIED", APPLIED_AT),
			migrationRow("2.0.0", "PENDING", null)
		)));
		
		List<SqlMigrationInfo> result = store(dataSource).loadAll();
		assertEquals(Version.parse("1.0.0"), result.get(0).version());
		assertEquals(APPLIED_AT, result.get(0).appliedAt());
		assertNull(result.get(1).appliedAt());
		assertNull(result.get(2).appliedAt());
	}
}
