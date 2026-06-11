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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlServerDialect}.<br>
 *
 * @author Luis-St
 */
class SqlServerDialectTest {
	
	private static final SqlServerDialect DIALECT = new SqlServerDialect();
	
	@Test
	void isFeatureSupportedNullFeature() {
		assertThrows(NullPointerException.class, () -> DIALECT.isFeatureSupported(null));
	}
	
	@Test
	void isIndexMethodSupportedNullMethod() {
		assertThrows(NullPointerException.class, () -> DIALECT.isIndexMethodSupported(null));
	}
	
	@Test
	void getParameterizedTypeNameNullParameter() {
		assertThrows(NullPointerException.class, () -> DIALECT.getParameterizedTypeName(Types.TIMESTAMP, null));
	}
	
	@Test
	void quoteIdentifierNullIdentifier() {
		assertThrows(NullPointerException.class, () -> DIALECT.quoteIdentifier(null));
	}
	
	@Test
	void renderLimitOffsetLimitBelowNegativeOne() {
		assertThrows(IllegalArgumentException.class, () -> DIALECT.renderLimitOffset(-2, 0, true));
	}
	
	@Test
	void renderLimitOffsetNegativeOffset() {
		assertThrows(IllegalArgumentException.class, () -> DIALECT.renderLimitOffset(10, -1, true));
	}
	
	@Test
	void renderUpsertClauseUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), List.of()));
	}
	
	@Test
	void renderInsertOrIgnoreSuffixUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderInsertOrIgnoreSuffix(List.of()));
	}
	
	@Test
	void renderUpsertStatementNullTable() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderUpsertStatement(null, List.of(), SqlTestFixtures.integerColumn(), SqlRendered.of("")));
	}
	
	@Test
	void renderUpsertStatementNullColumns() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderUpsertStatement(SqlTestFixtures.sampleTable(), null, SqlTestFixtures.integerColumn(), SqlRendered.of("")));
	}
	
	@Test
	void renderUpsertStatementNullConflictColumn() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderUpsertStatement(SqlTestFixtures.sampleTable(), List.of(), null, SqlRendered.of("")));
	}
	
	@Test
	void renderUpsertStatementNullValueTuples() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderUpsertStatement(SqlTestFixtures.sampleTable(), List.of(), SqlTestFixtures.integerColumn(), null));
	}
	
	@Test
	void getScalarTypeNameBoolean() {
		assertEquals("BIT", DIALECT.getScalarTypeName(Types.BOOLEAN).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameTinyint() {
		assertEquals("TINYINT", DIALECT.getScalarTypeName(Types.TINYINT).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameNvarcharMaxFamily() {
		assertEquals("NVARCHAR(MAX)", DIALECT.getScalarTypeName(Types.LONGVARCHAR).orElseThrow());
		assertEquals("NVARCHAR(MAX)", DIALECT.getScalarTypeName(Types.LONGNVARCHAR).orElseThrow());
		assertEquals("NVARCHAR(MAX)", DIALECT.getScalarTypeName(Types.NCLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameClobToVarcharMax() {
		assertEquals("VARCHAR(MAX)", DIALECT.getScalarTypeName(Types.CLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameVarbinaryMaxFamily() {
		assertEquals("VARBINARY(MAX)", DIALECT.getScalarTypeName(Types.LONGVARBINARY).orElseThrow());
		assertEquals("VARBINARY(MAX)", DIALECT.getScalarTypeName(Types.BLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameFallsBackToSuper() {
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.INTEGER).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameTimestamp() {
		assertEquals("DATETIME2(3)", DIALECT.getParameterizedTypeName(Types.TIMESTAMP, SqlParameter.fractional(3)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameTimestampWithTimezone() {
		assertEquals("DATETIMEOFFSET(3)", DIALECT.getParameterizedTypeName(Types.TIMESTAMP_WITH_TIMEZONE, SqlParameter.fractional(3)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameFractionalFallsBackToSuper() {
		assertEquals("TIME(3)", DIALECT.getParameterizedTypeName(Types.TIME, SqlParameter.fractional(3)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameNonFractionalFallsBackToSuper() {
		assertEquals("VARCHAR(64)", DIALECT.getParameterizedTypeName(Types.VARCHAR, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void isFeatureSupportedForSupportedFeature() {
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RECURSIVE_CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.SCHEMAS));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.WINDOW_FUNCTIONS));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.FOR_UPDATE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.NO_WAIT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ROW_LOCKING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPSERT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ALTER_COLUMN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ADD_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.DROP_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT));
	}
	
	@Test
	void isFeatureSupportedForUnsupportedFeature() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.LATERAL_JOIN));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.NULLS_ORDERING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.FOR_SHARE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SKIP_LOCKED));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRUNCATE_CASCADE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.IS_DISTINCT_FROM));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT_SUFFIX));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.INSERT_OR_IGNORE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RENAME_INDEX));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ARRAY_TYPE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TABLE_REBUILD));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.JOINED_DML));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPDATE_RETURNING));
	}
	
	@Test
	void isIndexMethodSupportedForSupportedMethods() {
		assertTrue(DIALECT.isIndexMethodSupported(net.luis.utils.io.database.index.SqlIndexMethod.CLUSTERED));
		assertTrue(DIALECT.isIndexMethodSupported(net.luis.utils.io.database.index.SqlIndexMethod.NONCLUSTERED));
		assertTrue(DIALECT.isIndexMethodSupported(net.luis.utils.io.database.index.SqlIndexMethod.COLUMNSTORE));
		assertTrue(DIALECT.isIndexMethodSupported(net.luis.utils.io.database.index.SqlIndexMethod.BTREE));
		assertTrue(DIALECT.isIndexMethodSupported(net.luis.utils.io.database.index.SqlIndexMethod.HASH));
	}
	
	@Test
	void isIndexMethodSupportedForUnsupportedMethod() {
		assertFalse(DIALECT.isIndexMethodSupported(net.luis.utils.io.database.index.SqlIndexMethod.GIN));
	}
	
	@Test
	void renderBooleanLiteralTrueIsOne() throws SqlException {
		assertEquals("1", DIALECT.renderBooleanLiteral(true).sql());
	}
	
	@Test
	void renderBooleanLiteralFalseIsZero() throws SqlException {
		assertEquals("0", DIALECT.renderBooleanLiteral(false).sql());
	}
	
	@Test
	void renderLimitOffsetWithoutOrderingPrependsOrderByNull() throws SqlException {
		String sql = DIALECT.renderLimitOffset(10, 0, false).sql();
		assertTrue(sql.contains("ORDER BY"));
		assertTrue(sql.contains("SELECT NULL"));
		assertTrue(sql.contains("OFFSET 0 ROWS"));
		assertTrue(sql.contains("FETCH NEXT 10 ROWS ONLY"));
	}
	
	@Test
	void renderLimitOffsetWithOrderingOmitsOrderByNull() throws SqlException {
		String sql = DIALECT.renderLimitOffset(10, 5, true).sql();
		assertTrue(sql.contains("OFFSET 5 ROWS"));
		assertTrue(sql.contains("FETCH NEXT 10 ROWS ONLY"));
		assertFalse(sql.contains("SELECT NULL"));
	}
	
	@Test
	void renderLimitOffsetNoLimitOmitsFetch() throws SqlException {
		String sql = DIALECT.renderLimitOffset(-1, 5, true).sql();
		assertTrue(sql.contains("OFFSET 5 ROWS"));
		assertFalse(sql.contains("FETCH NEXT"));
	}
	
	@Test
	void nameReturnsSqlServer() {
		assertEquals("SQL Server", DIALECT.name());
	}
	
	@Test
	void maxBindParametersIs2100() {
		assertEquals(2100, DIALECT.maxBindParameters());
	}
	
	@Test
	void quoteIdentifierUsesBrackets() {
		assertEquals("[col]", DIALECT.quoteIdentifier("col"));
	}
	
	@Test
	void quoteIdentifierEscapesClosingBracket() {
		assertEquals("[a]]b]", DIALECT.quoteIdentifier("a]b"));
	}
	
	@Test
	void getCheckConstraintsQueryStringContainsInformationSchema() {
		String query = DIALECT.getCheckConstraintsQueryString();
		assertTrue(query.contains("INFORMATION_SCHEMA.CHECK_CONSTRAINTS"));
		assertTrue(query.contains("TABLE_SCHEMA = ?"));
		assertTrue(query.contains("TABLE_NAME = ?"));
	}
	
	@Test
	void uuidMapsToUniqueidentifier() throws SqlException {
		assertEquals("UNIQUEIDENTIFIER", DIALECT.getTypeName(SqlTypes.UUID));
	}
	
	@Test
	void renderUpsertStatementBuildsMerge() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlColumn<Object, String> name = table.column("name", SqlTestFixtures.STRING_TYPE, object -> "test");
		SqlColumn<Object, String> email = table.column("email", SqlTestFixtures.STRING_TYPE, object -> "test");
		List<SqlColumn<?, ?>> columns = List.of(id, name, email);
		
		String sql = DIALECT.renderUpsertStatement(table, columns, id, SqlRendered.of("(?, ?, ?)")).sql();
		assertTrue(sql.contains("MERGE"));
		assertTrue(sql.contains("INTO"));
		assertTrue(sql.contains("USING"));
		assertTrue(sql.contains("AS source"));
		assertTrue(sql.contains("ON target.[id]"));
		assertTrue(sql.contains("WHEN MATCHED THEN UPDATE SET"));
		assertTrue(sql.contains("WHEN NOT MATCHED THEN INSERT"));
		assertTrue(sql.contains("VALUES"));
	}
	
	@Test
	void renderUpsertStatementSkipsConflictColumnInSetClause() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlColumn<Object, String> name = table.column("name", SqlTestFixtures.STRING_TYPE, object -> "test");
		List<SqlColumn<?, ?>> columns = List.of(id, name);
		
		String sql = DIALECT.renderUpsertStatement(table, columns, id, SqlRendered.of("(?, ?)")).sql();
		String setClause = sql.substring(sql.indexOf("UPDATE SET") + "UPDATE SET".length(), sql.indexOf("WHEN NOT MATCHED"));
		assertTrue(setClause.contains("target.[name]"));
		assertFalse(setClause.contains("[id]"));
	}
	
	@Test
	void renderUpsertStatementOnlyConflictColumnUsesFallbackSet() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		List<SqlColumn<?, ?>> columns = List.of(id);
		
		String sql = DIALECT.renderUpsertStatement(table, columns, id, SqlRendered.of("(?)")).sql();
		String setClause = sql.substring(sql.indexOf("UPDATE SET") + "UPDATE SET".length(), sql.indexOf("WHEN NOT MATCHED"));
		assertTrue(setClause.contains("target.[id]"));
		assertTrue(setClause.contains("source.[id]"));
	}
	
	@Test
	void renderUpsertStatementWithAuditColumns() throws SqlException {
		SqlTable<Object> table = SqlTable.audited(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlColumn<Object, String> name = table.column("name", SqlTestFixtures.STRING_TYPE, object -> "test");
		List<SqlColumn<?, ?>> columns = List.of(id, name);
		
		String sql = DIALECT.renderUpsertStatement(table, columns, id, SqlRendered.of("(?, ?)")).sql();
		String auditColumn = table.auditConfig().orElseThrow().columnNames().getFirst();
		assertTrue(sql.contains(auditColumn));
	}
	
	@Test
	void upsertRoutesThroughDialect() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> id = table.column("id", SqlTypes.INTEGER, object -> 0);
		SqlColumn<Object, String> name = table.column("name", SqlTestFixtures.STRING_TYPE, object -> "test");
		List<SqlColumn<?, ?>> columns = List.of(id, name);
		
		assertTrue(DIALECT.renderUpsertStatement(table, columns, id, SqlRendered.of("(?, ?)")).sql().contains("MERGE"));
	}
}
