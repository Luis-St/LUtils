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

package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.SqlTestFixtures.*;
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.query.util.SqlColumnValue;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlInsertColumnsBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlInsertColumnsBuilderTest {
	
	@Test
	void constructWithValidArguments() {
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		assertNotNull(builder);
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlInsertColumnsBuilder<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of()));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlInsertColumnsBuilder<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null, null, List.of()));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null, null, List.of()));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null, null, List.of()));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null, null, List.of()));
	}
	
	@Test
	void constructWithNullRows() {
		assertThrows(NullPointerException.class, () -> new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, null));
	}
	
	@Test
	void constructWithNullAuditUserProviderAllowed() {
		assertDoesNotThrow(() -> new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of()));
	}
	
	@Test
	void rowVarargsWithNullValuesThrows() {
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		assertThrows(NullPointerException.class, () -> builder.row((SqlColumnValue<Object, ?>[]) null));
	}
	
	@Test
	void rowListWithNullValuesThrows() {
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		assertThrows(NullPointerException.class, () -> builder.row((List<SqlColumnValue<Object, ?>>) null));
	}
	
	@Test
	void rowWithEmptyValuesThrows() {
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		assertThrows(SqlStatementBuilderException.class, () -> builder.row(List.of()));
	}
	
	@Test
	void rowWithMismatchedColumnsThrows() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> columnA = table.column("column_a", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, String> columnB = table.column("column_b", STRING_TYPE, o -> "test");
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(columnA, 1)));
		assertThrows(SqlStatementBuilderException.class, () -> builder.row(List.of(SqlColumnValue.of(columnB, "x"))));
	}
	
	@Test
	void toSqlWithNullDialectThrows() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		assertThrows(NullPointerException.class, () -> builder.toSql(null));
	}
	
	@Test
	void executeWithNoRowsThrows() {
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		assertThrows(SqlStatementBuilderException.class, builder::execute);
	}
	
	@Test
	void toSqlWithNoRowsThrows() {
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		assertThrows(SqlStatementBuilderException.class, () -> builder.toSql(DIALECT));
	}
	
	@Test
	void rowAsFirstRowSkipsColumnComparison() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		assertDoesNotThrow(() -> builder.row(List.of(SqlColumnValue.of(column, 1))));
	}
	
	@Test
	void rowWithMatchingColumnsOnSubsequentRowSucceeds() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		SqlInsertColumnsBuilder<Object> withSecondRow = assertDoesNotThrow(() -> builder.row(List.of(SqlColumnValue.of(column, 2))));
		assertNotNull(withSecondRow);
	}
	
	@Test
	void rowVarargsDelegatesToListOverload() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> viaVarargs = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(SqlColumnValue.of(column, 1));
		SqlInsertColumnsBuilder<Object> viaList = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		assertEquals(viaList.toSql(DIALECT), viaVarargs.toSql(DIALECT));
	}
	
	@Test
	void executeWithSingleChunkUsesSingleUpdate() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		int affected = builder.execute();
		assertEquals(1, affected);
		assertEquals(1, source.executedSql().size());
	}
	
	@Test
	void executeWithMultipleChunksUsesBatchedUpdate() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource();
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, SqlDialects.SQLITE, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> null, null, List.of());
		for (int i = 0; i < 1000; i++) {
			builder = builder.row(List.of(SqlColumnValue.of(column, i)));
		}
		builder.execute();
		assertTrue(source.executedSql().size() > 1);
	}
	
	@Test
	void executeReturningKeysWithSingleChunk() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(generatedKeysResultSet(42L));
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		List<Long> keys = builder.executeReturningKeys();
		assertEquals(List.of(42L), keys);
	}
	
	@Test
	void executeReturningKeysWithMultipleChunks() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(generatedKeysResultSet(1L));
		source.enqueueResultSet(generatedKeysResultSet(2L));
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, SqlDialects.SQLITE, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> null, null, List.of());
		for (int i = 0; i < 1000; i++) {
			builder = builder.row(List.of(SqlColumnValue.of(column, i)));
		}
		List<Long> keys = builder.executeReturningKeys();
		assertEquals(List.of(1L, 2L), keys);
	}
	
	@Test
	void returningWithSingleChunkUsesSingleReturningQuery() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of())));
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, SqlDialects.SQLITE, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> new Object(), null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		List<Object> entities = builder.returning();
		assertEquals(1, entities.size());
	}
	
	@Test
	void returningWithMultipleChunksUsesBatchedReturningQuery() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of())));
		source.enqueueResultSet(labeledResultSet(List.of(Map.of())));
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, SqlDialects.SQLITE, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> new Object(), null, List.of());
		for (int i = 0; i < 1000; i++) {
			builder = builder.row(List.of(SqlColumnValue.of(column, i)));
		}
		List<Object> entities = builder.returning();
		assertEquals(2, entities.size());
	}
	
	@Test
	void renderInsertWithAuditedTableAppendsAuditColumns() throws SqlException {
		SqlTable<Object> table = auditedTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		String sql = builder.toSql(DIALECT).sql();
		for (SqlAuditColumn auditColumn : table.auditConfig().orElseThrow().auditColumns()) {
			assertTrue(sql.contains(DIALECT.quoteIdentifier(auditColumn.name())));
		}
	}
	
	@Test
	void renderInsertWithNonAuditedTableOmitsAuditColumns() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		String sql = builder.toSql(DIALECT).sql();
		assertTrue(table.auditConfig().isEmpty());
		for (SqlAuditColumn auditColumn : SqlAuditConfig.DEFAULT.auditColumns()) {
			assertFalse(sql.contains(DIALECT.quoteIdentifier(auditColumn.name())));
		}
	}
	
	@Test
	void renderChunksAuditedTableReducesMaxRowsPerChunk() throws SqlException {
		SqlTable<Object> auditedTbl = auditedTable();
		SqlColumn<Object, Integer> auditedColumn = auditedTbl.column("id", INTEGER_TYPE, o -> 0);
		SqlTable<Object> plainTbl = sampleTable();
		SqlColumn<Object, Integer> plainColumn = plainTbl.column("id", INTEGER_TYPE, o -> 0);
		
		RecordingDataSource auditedSource = recordingDataSource();
		RecordingDataSource plainSource = recordingDataSource();
		SqlInsertColumnsBuilder<Object> auditedBuilder = new SqlInsertColumnsBuilder<>(auditedTbl, SqlDialects.SQLITE, SqlConnectionSource.pooled(auditedSource), TIMEOUT, resultSet -> null, null, List.of());
		SqlInsertColumnsBuilder<Object> plainBuilder = new SqlInsertColumnsBuilder<>(plainTbl, SqlDialects.SQLITE, SqlConnectionSource.pooled(plainSource), TIMEOUT, resultSet -> null, null, List.of());
		for (int i = 0; i < 200; i++) {
			auditedBuilder = auditedBuilder.row(List.of(SqlColumnValue.of(auditedColumn, i)));
			plainBuilder = plainBuilder.row(List.of(SqlColumnValue.of(plainColumn, i)));
		}
		
		auditedBuilder.execute();
		plainBuilder.execute();
		
		assertTrue(auditedSource.executedSql().size() > plainSource.executedSql().size());
	}
	
	@Test
	void renderInsertWithMultiColumnRowInsertsCommasBetweenColumns() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "multi_column_table");
		SqlColumn<Object, Integer> columnA = table.column("column_a", INTEGER_TYPE, o -> 0);
		SqlColumn<Object, String> columnB = table.column("column_b", STRING_TYPE, o -> "test");
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(columnA, 1), SqlColumnValue.of(columnB, "x")));
		String sql = builder.toSql(DIALECT).sql();
		int indexA = sql.indexOf(DIALECT.quoteIdentifier("column_a"));
		int indexB = sql.indexOf(DIALECT.quoteIdentifier("column_b"));
		assertTrue(indexA >= 0);
		assertTrue(indexB >= 0);
		String betweenColumns = sql.substring(Math.min(indexA, indexB), Math.max(indexA, indexB));
		assertTrue(betweenColumns.contains(","));
	}
	
	@Test
	void withAuditUserReturnsNewInstanceWithProvider() throws SqlException {
		SqlTable<Object> table = auditedTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlAuditUserProvider provider = () -> Optional.of("alice");
		SqlRendered withProvider = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.withAuditUser(provider)
			.row(List.of(SqlColumnValue.of(column, 1)))
			.toSql(DIALECT);
		SqlRendered withoutProvider = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)))
			.toSql(DIALECT);
		assertTrue(withProvider.parameters().stream().anyMatch(pair -> "alice".equals(pair.getSecond())));
		assertFalse(withoutProvider.parameters().stream().anyMatch(pair -> "alice".equals(pair.getSecond())));
	}
	
	@Test
	void withAuditUserWithNullClearsProvider() {
		SqlTable<Object> table = auditedTable();
		SqlAuditUserProvider provider = () -> Optional.of("alice");
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.withAuditUser(provider);
		assertDoesNotThrow(() -> builder.withAuditUser(null));
	}
	
	@Test
	void toSqlRendersInsertStatementShape() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		String sql = builder.toSql(DIALECT).sql();
		assertTrue(sql.contains("INSERT"));
		assertTrue(sql.contains("INTO"));
		assertTrue(sql.contains(DIALECT.quoteIdentifier(table.name())));
		assertTrue(sql.contains(DIALECT.quoteIdentifier("id")));
		assertTrue(sql.contains("VALUES"));
	}
	
	@Test
	void rowsAreImmutableAcrossBuilderInstances() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> original = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		SqlInsertColumnsBuilder<Object> withRow = original.row(List.of(SqlColumnValue.of(column, 1)));
		assertThrows(SqlStatementBuilderException.class, original::execute);
		assertDoesNotThrow(() -> withRow.toSql(DIALECT));
	}
	
	@Test
	void builderReuseAddingMultipleRowsSequentially() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of());
		builder = builder.row(List.of(SqlColumnValue.of(column, 1)));
		builder = builder.row(List.of(SqlColumnValue.of(column, 2)));
		builder = builder.row(List.of(SqlColumnValue.of(column, 3)));
		SqlRendered rendered = builder.toSql(DIALECT);
		long openParens = rendered.sql().chars().filter(c -> c == '(').count();
		assertEquals(4, openParens);
		assertEquals(3, rendered.parameters().size());
	}
	
	@Test
	void executeAndReturningShareRenderedRowsConsistently() throws SqlException {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, Integer> column = table.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource source = recordingDataSource().rowsAffected(1);
		source.enqueueResultSet(labeledResultSet(List.of(Map.of())));
		SqlInsertColumnsBuilder<Object> builder = new SqlInsertColumnsBuilder<>(table, SqlDialects.SQLITE, SqlConnectionSource.pooled(source), TIMEOUT, resultSet -> new Object(), null, List.of())
			.row(List.of(SqlColumnValue.of(column, 1)));
		
		builder.execute();
		builder.returning();
		
		List<String> executed = source.executedSql();
		assertEquals(2, executed.size());
		for (String sql : executed) {
			assertTrue(sql.contains(SqlDialects.SQLITE.quoteIdentifier(table.name())));
			assertTrue(sql.contains(SqlDialects.SQLITE.quoteIdentifier("id")));
		}
	}
	
	@Test
	void chunkingBoundaryAtExactMaxRows() throws SqlException {
		SqlTable<Object> exactTable = sampleTable();
		SqlColumn<Object, Integer> exactColumn = exactTable.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource exactSource = recordingDataSource();
		SqlInsertColumnsBuilder<Object> exactBuilder = new SqlInsertColumnsBuilder<>(exactTable, SqlDialects.SQLITE, SqlConnectionSource.pooled(exactSource), TIMEOUT, resultSet -> null, null, List.of());
		for (int i = 0; i < 999; i++) {
			exactBuilder = exactBuilder.row(List.of(SqlColumnValue.of(exactColumn, i)));
		}
		exactBuilder.execute();
		assertEquals(1, exactSource.executedSql().size());
		
		SqlTable<Object> overTable = sampleTable();
		SqlColumn<Object, Integer> overColumn = overTable.column("id", INTEGER_TYPE, o -> 0);
		RecordingDataSource overSource = recordingDataSource();
		SqlInsertColumnsBuilder<Object> overBuilder = new SqlInsertColumnsBuilder<>(overTable, SqlDialects.SQLITE, SqlConnectionSource.pooled(overSource), TIMEOUT, resultSet -> null, null, List.of());
		for (int i = 0; i < 1000; i++) {
			overBuilder = overBuilder.row(List.of(SqlColumnValue.of(overColumn, i)));
		}
		overBuilder.execute();
		assertEquals(2, overSource.executedSql().size());
	}
}
