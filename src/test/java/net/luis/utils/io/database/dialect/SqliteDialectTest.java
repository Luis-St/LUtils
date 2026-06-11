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
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.SqlNumericTruncateFunction;
import net.luis.utils.io.database.function.functions.temporal.SqlNowFunction;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqliteDialect}.<br>
 *
 * @author Luis-St
 */
class SqliteDialectTest {
	
	private static final SqliteDialect DIALECT = new SqliteDialect();
	
	@Test
	void isFeatureSupportedNullFeature() {
		assertThrows(NullPointerException.class, () -> DIALECT.isFeatureSupported(null));
	}
	
	@Test
	void renderReturningNullColumns() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderReturning(null));
	}
	
	@Test
	void renderLockClauseAlwaysUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderLockClause(SqlLockMode.FOR_UPDATE, false, false));
	}
	
	@Test
	void renderLockClauseUnsupportedEvenWithNullMode() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderLockClause(null, false, false));
	}
	
	@Test
	void getScalarTypeNameIntegerFamily() {
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.BOOLEAN).orElseThrow());
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.TINYINT).orElseThrow());
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.SMALLINT).orElseThrow());
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.INTEGER).orElseThrow());
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.BIGINT).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameRealFamily() {
		assertEquals("REAL", DIALECT.getScalarTypeName(Types.REAL).orElseThrow());
		assertEquals("REAL", DIALECT.getScalarTypeName(Types.FLOAT).orElseThrow());
		assertEquals("REAL", DIALECT.getScalarTypeName(Types.DOUBLE).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameTextFamily() {
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.LONGVARCHAR).orElseThrow());
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.CLOB).orElseThrow());
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.NCLOB).orElseThrow());
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.DATE).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameBlobFamily() {
		assertEquals("BLOB", DIALECT.getScalarTypeName(Types.LONGVARBINARY).orElseThrow());
		assertEquals("BLOB", DIALECT.getScalarTypeName(Types.BLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameUnknownEmpty() {
		assertTrue(DIALECT.getScalarTypeName(Types.OTHER).isEmpty());
	}
	
	@Test
	void getParameterizedTypeNameTextFamily() {
		assertEquals("TEXT", DIALECT.getParameterizedTypeName(Types.CHAR, SqlParameter.length(1)).orElseThrow());
		assertEquals("TEXT", DIALECT.getParameterizedTypeName(Types.VARCHAR, SqlParameter.length(1)).orElseThrow());
		assertEquals("TEXT", DIALECT.getParameterizedTypeName(Types.TIMESTAMP, SqlParameter.fractional(3)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameBlobFamily() {
		assertEquals("BLOB", DIALECT.getParameterizedTypeName(Types.BINARY, SqlParameter.length(1)).orElseThrow());
		assertEquals("BLOB", DIALECT.getParameterizedTypeName(Types.VARBINARY, SqlParameter.length(1)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameNumericFamily() {
		assertEquals("NUMERIC", DIALECT.getParameterizedTypeName(Types.NUMERIC, SqlParameter.precision(10, 2)).orElseThrow());
		assertEquals("NUMERIC", DIALECT.getParameterizedTypeName(Types.DECIMAL, SqlParameter.precision(10, 2)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameUnknownEmpty() {
		assertTrue(DIALECT.getParameterizedTypeName(Types.OTHER, SqlParameter.length(1)).isEmpty());
	}
	
	@Test
	void getParameterizedTypeNameNullParameterDoesNotThrow() {
		assertEquals("TEXT", DIALECT.getParameterizedTypeName(Types.VARCHAR, null).orElseThrow());
	}
	
	@Test
	void isFeatureSupportedForSupportedFeature() {
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPDATE_RETURNING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RECURSIVE_CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.NULLS_ORDERING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPSERT_SUFFIX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.INSERT_OR_IGNORE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ALTER_COLUMN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ADD_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.TABLE_REBUILD));
	}
	
	@Test
	void isFeatureSupportedForUnsupportedFeature() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.LATERAL_JOIN));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SCHEMAS));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.WINDOW_FUNCTIONS));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.FOR_UPDATE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.FOR_SHARE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SKIP_LOCKED));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.NO_WAIT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRUNCATE_CASCADE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.IS_DISTINCT_FROM));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ROW_LOCKING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RENAME_INDEX));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.DROP_CONSTRAINT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ARRAY_TYPE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.JOINED_DML));
	}
	
	@Test
	void renderReturningEmptyColumns() throws SqlException {
		String sql = DIALECT.renderReturning(List.of()).sql();
		assertTrue(sql.contains("RETURNING"));
		assertTrue(sql.contains("*"));
	}
	
	@Test
	void renderReturningWithColumns() throws SqlException {
		String sql = DIALECT.renderReturning(List.of(SqlTestFixtures.integerColumn())).sql();
		assertTrue(sql.contains("RETURNING"));
		assertTrue(sql.contains("\"id\""));
	}
	
	@Test
	void nameReturnsSqlite() {
		assertEquals("SQLite", DIALECT.name());
	}
	
	@Test
	void maxBindParametersIs999() {
		assertEquals(999, DIALECT.maxBindParameters());
	}
	
	@Test
	void getCheckConstraintsQueryStringIsNull() {
		assertNull(DIALECT.getCheckConstraintsQueryString());
	}
	
	@Test
	void numericTruncateRoutesThroughSqliteRenderer() throws SqlException {
		SqlNumericTruncateFunction<?> function = new SqlNumericTruncateFunction<>(new SqlValueExpression<>(5));
		assertTrue(DIALECT.renderFunction(function).sql().contains("TRUNC("));
	}
	
	@Test
	void temporalNowRoutesThroughSqliteRenderer() throws SqlException {
		assertEquals("datetime('now')", DIALECT.renderFunction(new SqlNowFunction<>(SqlTypes.LOCAL_DATE)).sql());
	}
}
