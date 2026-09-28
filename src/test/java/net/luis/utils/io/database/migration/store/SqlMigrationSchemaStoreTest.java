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
import net.luis.utils.io.database.migration.*;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import javax.sql.rowset.*;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationSchemaStore}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationSchemaStoreTest {
	
	/**
	 * Marker recorded by {@link CapturingDataSource} when a parameter is bound as sql null.
	 */
	private static final Object NULL_MARKER = new Object();
	
	private static SqlMigrationSchemaStore store(DataSource dataSource) {
		return new SqlMigrationSchemaStore(dataSource, SqlTestFixtures.DIALECT);
	}
	
	private static SqlSchemaColumnInfo column(SqlParameter parameter) {
		return new SqlSchemaColumnInfo("users", "id", Types.INTEGER, parameter, true, false, false, false, 1);
	}
	
	private static SqlCheckConstraintInfo constraint() {
		return new SqlCheckConstraintInfo("ck_users", "id > 0");
	}
	
	private static Map<String, Object> baseColumnRow() {
		Map<String, Object> row = new HashMap<>();
		row.put("table_name", "users");
		row.put("column_name", "id");
		row.put("jdbc_type", Types.INTEGER);
		row.put("is_nullable", true);
		row.put("is_auto_increment", false);
		row.put("is_primary_key", false);
		row.put("is_unique", false);
		row.put("ordinal_position", 1);
		return row;
	}
	
	private static @NonNull SqlSchemaColumnInfo identifiedColumn(@Nullable String typeIdentifier) {
		return new SqlSchemaColumnInfo("it_table", "it_column", Types.CHAR, SqlParameter.length(36), false, false, true, false, 0, typeIdentifier);
	}
	
	private static @NonNull Map<String, Object> labeledColumnRow(@Nullable String typeIdentifier) {
		Map<String, Object> row = new HashMap<>();
		row.put("table_name", "it_table");
		row.put("column_name", "it_column");
		row.put("jdbc_type", Types.CHAR);
		row.put("length", 36);
		row.put("is_nullable", false);
		row.put("is_auto_increment", false);
		row.put("is_primary_key", true);
		row.put("is_unique", false);
		row.put("ordinal_position", 0);
		row.put("type_identifier", typeIdentifier);
		return row;
	}
	
	private static @NonNull ResultSet schemaColumnsRowSet(boolean withTypeIdentifier, @Nullable String typeIdentifier) {
		return schemaColumnsRowSet(withTypeIdentifier, typeIdentifier, false);
	}
	
	private static @NonNull ResultSet schemaColumnsRowSet(boolean withTypeIdentifier, @Nullable String typeIdentifier, boolean upperCaseLabels) {
		try {
			List<String> labels = new ArrayList<>(List.of("table_name", "column_name", "jdbc_type", "length", "precision", "scale", "fractional", "is_nullable", "is_auto_increment", "is_primary_key", "is_unique", "ordinal_position"));
			if (withTypeIdentifier) {
				labels.add("type_identifier");
			}
			
			RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
			metaData.setColumnCount(labels.size());
			for (int i = 0; i < labels.size(); i++) {
				String label = upperCaseLabels ? labels.get(i).toUpperCase(Locale.ROOT) : labels.get(i);
				metaData.setColumnName(i + 1, label);
				metaData.setColumnLabel(i + 1, label);
				metaData.setColumnType(i + 1, columnType(labels.get(i)));
			}
			
			CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
			rowSet.setMetaData(metaData);
			rowSet.moveToInsertRow();
			rowSet.updateString(1, "it_table");
			rowSet.updateString(2, "it_column");
			rowSet.updateInt(3, Types.CHAR);
			rowSet.updateInt(4, 36);
			rowSet.updateNull(5);
			rowSet.updateNull(6);
			rowSet.updateNull(7);
			rowSet.updateBoolean(8, false);
			rowSet.updateBoolean(9, false);
			rowSet.updateBoolean(10, true);
			rowSet.updateBoolean(11, false);
			rowSet.updateInt(12, 0);
			if (withTypeIdentifier) {
				if (typeIdentifier == null) {
					rowSet.updateNull(13);
				} else {
					rowSet.updateString(13, typeIdentifier);
				}
			}
			rowSet.insertRow();
			rowSet.moveToCurrentRow();
			rowSet.beforeFirst();
			return rowSet;
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to build schema columns row set fixture", e);
		}
	}
	
	private static int columnType(@NonNull String label) {
		return switch (label) {
			case "table_name", "column_name", "type_identifier" -> Types.VARCHAR;
			case "is_nullable", "is_auto_increment", "is_primary_key", "is_unique" -> Types.BOOLEAN;
			default -> Types.INTEGER;
		};
	}
	
	@Test
	void constructWithDataSourceAndDialect() {
		assertDoesNotThrow(() -> new SqlMigrationSchemaStore(SqlTestFixtures.failingDataSource(), SqlTestFixtures.DIALECT));
	}
	
	@Test
	void constructWithNullDataSource() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationSchemaStore(null, SqlTestFixtures.DIALECT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationSchemaStore(SqlTestFixtures.failingDataSource(), null));
	}
	
	@Test
	void saveWithNullVersion() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(null, List.of(), Map.of()));
	}
	
	@Test
	void saveWithNullColumnInfos() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(Version.of(1, 0, 0), null, Map.of()));
	}
	
	@Test
	void saveWithNullCheckConstraints() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(Version.of(1, 0, 0), List.of(), null));
	}
	
	@Test
	void saveWithConnectionNullConnection() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(null, Version.of(1, 0, 0), List.of(), Map.of()));
	}
	
	@Test
	void saveWithConnectionNullVersion() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(SqlTestFixtures.placeholderConnection(), null, List.of(), Map.of()));
	}
	
	@Test
	void saveWithConnectionNullColumnInfos() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(SqlTestFixtures.placeholderConnection(), Version.of(1, 0, 0), null, Map.of()));
	}
	
	@Test
	void saveWithConnectionNullCheckConstraints() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).save(SqlTestFixtures.placeholderConnection(), Version.of(1, 0, 0), List.of(), null));
	}
	
	@Test
	void loadWithNullVersion() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).load(null));
	}
	
	@Test
	void deleteWithNullVersion() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).delete(null));
	}
	
	@Test
	void deleteWithConnectionNullConnection() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).delete(null, Version.of(1, 0, 0)));
	}
	
	@Test
	void deleteWithConnectionNullVersion() {
		assertThrows(NullPointerException.class, () -> store(SqlTestFixtures.failingDataSource()).delete(SqlTestFixtures.placeholderConnection(), null));
	}
	
	@Test
	void initializeWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).initialize());
	}
	
	@Test
	void saveWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).save(Version.of(1, 0, 0), List.of(column(null)), Map.of()));
	}
	
	@Test
	void saveWithConnectionWrapsColumnSqlException() {
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class,
			() -> store(SqlTestFixtures.failingDataSource()).save(SqlTestFixtures.throwingConnection(), Version.of(1, 0, 0), List.of(column(null)), Map.of()));
		assertTrue(exception.getMessage().contains("column infos"));
	}
	
	@Test
	void deleteWrapsSqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).delete(Version.of(1, 0, 0)));
	}
	
	@Test
	void deleteWithConnectionWrapsSqlException() {
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class,
			() -> store(SqlTestFixtures.failingDataSource()).delete(SqlTestFixtures.throwingConnection(), Version.of(1, 0, 0)));
		assertTrue(exception.getMessage().contains("column infos"));
	}
	
	@Test
	void saveWithConnectionWrapsCheckConstraintSqlException() {
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class,
			() -> store(SqlTestFixtures.failingDataSource()).save(SqlTestFixtures.connectionFailingOnStatement(2), Version.of(1, 0, 0), List.of(column(null)), Map.of("users", List.of(constraint()))));
		assertTrue(exception.getMessage().contains("check constraints"));
	}
	
	@Test
	void deleteWithConnectionWrapsCheckConstraintSqlException() {
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class,
			() -> store(SqlTestFixtures.failingDataSource()).delete(SqlTestFixtures.connectionFailingOnStatement(2), Version.of(1, 0, 0)));
		assertTrue(exception.getMessage().contains("check constraints"));
	}
	
	@Test
	void loadWrapsCheckConstraintQuerySqlException() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> row = baseColumnRow();
		row.put("length", 64);
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(row)));
		dataSource.enqueueResultSet(SqlTestFixtures.throwingResultSet());
		
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class, () -> store(dataSource).load(Version.of(1, 0, 0)));
		assertTrue(exception.getMessage().contains("check constraints"));
	}
	
	@Test
	void initializeExecutesBothSchemaTables() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).initialize();
		
		List<String> executed = dataSource.executedSql();
		assertEquals(3, executed.size());
		assertTrue(executed.contains(SqlTestFixtures.DIALECT.getCreateSchemaColumnsTableSql()));
		assertTrue(executed.contains(SqlTestFixtures.DIALECT.getCreateSchemaCheckConstraintsTableSql()));
		assertTrue(executed.contains(SqlTestFixtures.DIALECT.getSelectSchemaColumnsSql()));
	}
	
	@Test
	void saveCommitsTransaction() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).save(Version.of(1, 0, 0), List.of(column(SqlParameter.length(64))), Map.of());
		
		assertEquals(1, dataSource.commitCount());
		assertEquals(0, dataSource.rollbackCount());
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaColumnSql()));
	}
	
	@Test
	void saveRollsBackOnCommitFailure() {
		RecordingDataSource dataSource = SqlTestFixtures.commitFailingDataSource();
		
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class,
			() -> store(dataSource).save(Version.of(1, 0, 0), List.of(column(SqlParameter.length(64))), Map.of()));
		assertTrue(exception.getMessage().contains("schema snapshot"));
		assertEquals(1, dataSource.rollbackCount());
		assertEquals(0, dataSource.commitCount());
	}
	
	@Test
	void deleteRollsBackOnCommitFailure() {
		RecordingDataSource dataSource = SqlTestFixtures.commitFailingDataSource();
		
		SqlMigrationExecutionException exception = assertThrows(SqlMigrationExecutionException.class, () -> store(dataSource).delete(Version.of(1, 0, 0)));
		assertTrue(exception.getMessage().contains("schema snapshot"));
		assertEquals(1, dataSource.rollbackCount());
		assertEquals(0, dataSource.commitCount());
	}
	
	@Test
	void saveWithEmptyColumnInfosSkipsColumnLoop() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(Version.of(1, 0, 0), List.of(), Map.of()));
		assertTrue(dataSource.executedSql().isEmpty());
	}
	
	@Test
	void saveWithConnectionRecordsColumnAndConstraintInserts() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(SqlParameter.length(64))), Map.of("users", List.of(constraint())));
		
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaColumnSql()));
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaCheckConstraintSql()));
	}
	
	@Test
	void saveWithLengthParameterColumn() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(SqlParameter.length(64))), Map.of()));
	}
	
	@Test
	void saveWithPrecisionParameterColumn() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(SqlParameter.precision(10, 2))), Map.of()));
	}
	
	@Test
	void saveWithFractionalParameterColumn() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(SqlParameter.fractional(6))), Map.of()));
	}
	
	@Test
	void saveWithNullParameterColumn() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(null)), Map.of()));
	}
	
	@Test
	void saveWithEmptyCheckConstraintsSkipsConstraintLoop() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(null)), Map.of()));
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaColumnSql()));
		assertFalse(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaCheckConstraintSql()));
	}
	
	@Test
	void saveWithConnectionTableWithoutConstraints() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(null)), Map.of("t", List.of())));
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaColumnSql()));
		assertFalse(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaCheckConstraintSql()));
	}
	
	@Test
	void loadReturnsNullWhenNoColumns() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertNull(store(dataSource).load(Version.of(1, 0, 0)));
	}
	
	@Test
	void loadReconstructsColumnWithLengthParameter() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> row = baseColumnRow();
		row.put("length", 64);
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(row)));
		
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		assertNotNull(snapshot);
		assertEquals(1, snapshot.columns().size());
		assertEquals("users", snapshot.columns().get(0).tableName());
		assertEquals(SqlParameter.length(64), snapshot.columns().get(0).parameter());
	}
	
	@Test
	void loadReconstructsColumnWithPrecisionParameter() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> row = baseColumnRow();
		row.put("precision", 10);
		row.put("scale", 2);
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(row)));
		
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		assertNotNull(snapshot);
		assertEquals(SqlParameter.precision(10, 2), snapshot.columns().get(0).parameter());
	}
	
	@Test
	void loadReconstructsColumnWithFractionalParameter() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> row = baseColumnRow();
		row.put("fractional", 6);
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(row)));
		
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		assertNotNull(snapshot);
		assertEquals(SqlParameter.fractional(6), snapshot.columns().get(0).parameter());
	}
	
	@Test
	void loadReconstructsColumnWithNoParameter() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(baseColumnRow())));
		
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		assertNotNull(snapshot);
		assertNull(snapshot.columns().get(0).parameter());
	}
	
	@Test
	void loadGroupsCheckConstraintsByTable() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(baseColumnRow())));
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(
			Map.of("table_name", "users", "constraint_name", "c1", "check_clause", "id > 0"),
			Map.of("table_name", "orders", "constraint_name", "c2", "check_clause", "total > 0")
		)));
		
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		assertNotNull(snapshot);
		assertEquals(2, snapshot.checkConstraints().size());
		assertEquals(1, snapshot.checkConstraints().get("users").size());
		assertEquals(1, snapshot.checkConstraints().get("orders").size());
	}
	
	@Test
	void loadAppendsCheckConstraintsForSameTable() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(baseColumnRow())));
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(
			Map.of("table_name", "users", "constraint_name", "c1", "check_clause", "id > 0"),
			Map.of("table_name", "users", "constraint_name", "c2", "check_clause", "id < 100")
		)));
		
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		assertNotNull(snapshot);
		assertEquals(1, snapshot.checkConstraints().size());
		assertEquals(2, snapshot.checkConstraints().get("users").size());
	}
	
	@Test
	void deleteCommitsTransaction() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).delete(Version.of(1, 0, 0));
		
		assertEquals(1, dataSource.commitCount());
		assertEquals(0, dataSource.rollbackCount());
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getDeleteSchemaColumnsSql()));
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getDeleteSchemaCheckConstraintsSql()));
	}
	
	@Test
	void deleteWithConnectionExecutesBothDeletes() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).delete(dataSource.getConnection(), Version.of(1, 0, 0));
		
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getDeleteSchemaColumnsSql()));
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getDeleteSchemaCheckConstraintsSql()));
	}
	
	@Test
	void saveWithMultipleColumnInfos() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		List<SqlSchemaColumnInfo> columns = List.of(column(SqlParameter.length(64)), column(SqlParameter.precision(10, 2)), column(SqlParameter.fractional(6)));
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), columns, Map.of()));
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaColumnSql()));
	}
	
	@Test
	void saveWithConnectionMultipleConstraintsPerTable() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, List<SqlCheckConstraintInfo>> constraints = Map.of("users", List.of(constraint(), new SqlCheckConstraintInfo("ck_users_2", "id < 100")));
		assertDoesNotThrow(() -> store(dataSource).save(dataSource.getConnection(), Version.of(1, 0, 0), List.of(column(null)), constraints));
		assertTrue(dataSource.executedSql().contains(SqlTestFixtures.DIALECT.getInsertSchemaCheckConstraintSql()));
	}
	
	@Test
	void saveThenLoadRoundTripSnapshot() throws Exception {
		RecordingDataSource saveSource = SqlTestFixtures.recordingDataSource();
		List<SqlSchemaColumnInfo> columns = List.of(column(SqlParameter.length(64)), column(SqlParameter.precision(10, 2)), column(SqlParameter.fractional(6)), column(null));
		assertDoesNotThrow(() -> store(saveSource).save(saveSource.getConnection(), Version.of(2, 0, 0), columns, Map.of("users", List.of(constraint()))));
		
		RecordingDataSource loadSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> lengthRow = baseColumnRow();
		lengthRow.put("length", 64);
		Map<String, Object> precisionRow = baseColumnRow();
		precisionRow.put("column_name", "amount");
		precisionRow.put("ordinal_position", 2);
		precisionRow.put("precision", 10);
		precisionRow.put("scale", 2);
		loadSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(lengthRow, precisionRow)));
		loadSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(
			Map.of("table_name", "users", "constraint_name", "c1", "check_clause", "id > 0"),
			Map.of("table_name", "orders", "constraint_name", "c2", "check_clause", "total > 0")
		)));
		
		SqlSchemaSnapshot snapshot = store(loadSource).load(Version.of(2, 0, 0));
		assertNotNull(snapshot);
		assertEquals(2, snapshot.columns().size());
		assertEquals(SqlParameter.length(64), snapshot.columns().get(0).parameter());
		assertEquals(SqlParameter.precision(10, 2), snapshot.columns().get(1).parameter());
		assertEquals(2, snapshot.checkConstraints().size());
	}
	
	@Test
	void loadWrapsColumnQuerySqlException() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).load(Version.of(1, 0, 0)));
	}
	
	@Test
	void initializeWrapsUpgradeFailure() {
		assertThrows(SqlMigrationExecutionException.class, () -> store(SqlTestFixtures.failingDataSource()).initialize());
	}
	
	@Test
	void initializeSkipsUpgradeWhenColumnPresent() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(schemaColumnsRowSet(true, "uuid"));
		store(dataSource).initialize();
		
		assertTrue(dataSource.executedSql().stream().noneMatch(sql -> sql.startsWith("ALTER TABLE")));
	}
	
	@Test
	void initializeAddsColumnWhenMissing() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(schemaColumnsRowSet(false, null));
		store(dataSource).initialize();
		
		List<String> altered = dataSource.executedSql().stream().filter(sql -> sql.startsWith("ALTER TABLE")).toList();
		assertEquals(1, altered.size());
		assertTrue(altered.getFirst().contains("type_identifier"));
	}
	
	@Test
	void initializeSkipsUpgradeWhenMetadataUnavailable() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> store(dataSource).initialize());
		assertTrue(dataSource.executedSql().stream().noneMatch(sql -> sql.startsWith("ALTER TABLE")));
	}
	
	@Test
	void initializeIsIdempotent() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(schemaColumnsRowSet(false, null));
		dataSource.enqueueResultSet(schemaColumnsRowSet(true, "uuid"));
		SqlMigrationSchemaStore store = store(dataSource);
		store.initialize();
		store.initialize();
		
		assertEquals(1, dataSource.executedSql().stream().filter(sql -> sql.startsWith("ALTER TABLE")).count());
	}
	
	@Test
	void saveBindsTypeIdentifier() throws Exception {
		CapturingDataSource dataSource = new CapturingDataSource();
		new SqlMigrationSchemaStore(dataSource, SqlTestFixtures.DIALECT)
			.save(Version.of(1, 0, 0), List.of(identifiedColumn("uuid")), Map.of());
		assertEquals("uuid", dataSource.parameter(14));
	}
	
	@Test
	void saveBindsNullTypeIdentifier() throws Exception {
		CapturingDataSource dataSource = new CapturingDataSource();
		new SqlMigrationSchemaStore(dataSource, SqlTestFixtures.DIALECT)
			.save(Version.of(1, 0, 0), List.of(identifiedColumn(null)), Map.of());
		assertEquals(NULL_MARKER, dataSource.parameter(14));
	}
	
	@Test
	void loadReadsTypeIdentifierWhenPresent() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(schemaColumnsRowSet(true, "uuid"));
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		
		assertNotNull(snapshot);
		assertEquals(1, snapshot.columns().size());
		assertEquals("uuid", snapshot.columns().getFirst().typeIdentifier());
	}
	
	@Test
	void loadWithoutTypeIdentifierColumn() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(schemaColumnsRowSet(false, null));
		SqlSchemaSnapshot snapshot = assertDoesNotThrow(() -> store(dataSource).load(Version.of(1, 0, 0)));
		
		assertNotNull(snapshot);
		assertNull(snapshot.columns().getFirst().typeIdentifier());
	}
	
	@Test
	void loadWithUnavailableMetadata() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(labeledColumnRow(null))));
		SqlSchemaSnapshot snapshot = assertDoesNotThrow(() -> store(dataSource).load(Version.of(1, 0, 0)));
		
		assertNotNull(snapshot);
		assertNull(snapshot.columns().getFirst().typeIdentifier());
	}
	
	@Test
	void containsColumnIgnoresCase() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(schemaColumnsRowSet(true, "uuid", true));
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		
		assertNotNull(snapshot);
		assertEquals("uuid", snapshot.columns().getFirst().typeIdentifier());
	}
	
	@Test
	void saveThenLoadRoundTripsTypeIdentifier() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		store(dataSource).save(Version.of(1, 0, 0), List.of(identifiedColumn("uuid"), identifiedColumn(null)), Map.of());
		dataSource.enqueueResultSet(schemaColumnsRowSet(true, "uuid"));
		
		SqlSchemaSnapshot snapshot = store(dataSource).load(Version.of(1, 0, 0));
		assertNotNull(snapshot);
		assertEquals("uuid", snapshot.columns().getFirst().typeIdentifier());
	}
	
	@Test
	void upgradedTableAcceptsInsertStatement() {
		String insertSql = SqlTestFixtures.DIALECT.getInsertSchemaColumnSql();
		String columns = insertSql.substring(insertSql.indexOf('(') + 1, insertSql.indexOf(')'));
		String createSql = assertDoesNotThrow(SqlTestFixtures.DIALECT::getCreateSchemaColumnsTableSql);
		for (String column : columns.split(",")) {
			assertTrue(createSql.contains(column.trim()), "Created table is missing " + column.trim());
		}
	}
	
	/**
	 * Data source whose prepared statements record the bound parameter values so a binding can be asserted.<br>
	 * Everything else behaves as a no-op, mirroring the recording fixture.
	 */
	private static final class CapturingDataSource implements DataSource {
		
		private final Map<Integer, Object> parameters = new HashMap<>();
		
		private static @Nullable Object defaultValue(@NonNull Class<?> type) {
			if (!type.isPrimitive()) {
				return null;
			}
			if (type == boolean.class) {
				return false;
			}
			if (type == int.class) {
				return 0;
			}
			if (type == long.class) {
				return 0L;
			}
			return null;
		}
		
		private @Nullable Object parameter(int index) {
			return this.parameters.get(index);
		}
		
		@Override
		public @NonNull Connection getConnection() {
			return (Connection) Proxy.newProxyInstance(
				Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class },
				(proxy, method, args) -> switch (method.getName()) {
					case "prepareStatement" -> this.statement();
					case "isClosed", "getAutoCommit" -> false;
					case "toString" -> "CapturingConnection";
					default -> defaultValue(method.getReturnType());
				}
			);
		}
		
		private @NonNull Object statement() {
			return Proxy.newProxyInstance(
				PreparedStatement.class.getClassLoader(),
				new Class<?>[] { PreparedStatement.class },
				(proxy, method, args) -> {
					String name = method.getName();
					if (name.startsWith("set") && args != null && args.length >= 2 && args[0] instanceof Integer index) {
						this.parameters.put(index, "setNull".equals(name) ? NULL_MARKER : args[1]);
						return null;
					}
					return switch (name) {
						case "executeBatch" -> new int[0];
						case "executeLargeBatch" -> new long[0];
						case "toString" -> "CapturingStatement";
						default -> defaultValue(method.getReturnType());
					};
				}
			);
		}
		
		@Override
		public @NonNull Connection getConnection(String username, String password) {
			return this.getConnection();
		}
		
		@Override
		public java.io.PrintWriter getLogWriter() {
			return null;
		}
		
		@Override
		public void setLogWriter(java.io.PrintWriter out) {}
		
		@Override
		public int getLoginTimeout() {
			return 0;
		}
		
		@Override
		public void setLoginTimeout(int seconds) {}
		
		@Override
		public java.util.logging.Logger getParentLogger() {
			return java.util.logging.Logger.getGlobal();
		}
		
		@Override
		public <T> T unwrap(Class<T> iface) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isWrapperFor(Class<?> iface) {
			return false;
		}
	}
}
