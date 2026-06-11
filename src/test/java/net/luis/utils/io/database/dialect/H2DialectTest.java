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

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.temporal.SqlFromEpochFunction;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link H2Dialect}.<br>
 *
 * @author Luis-St
 */
class H2DialectTest {
	
	private static final H2Dialect DIALECT = new H2Dialect();
	
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
		assertThrows(NullPointerException.class, () -> DIALECT.getParameterizedTypeName(Types.VARCHAR, null));
	}
	
	@Test
	void renderLockClauseNullMode() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderLockClause(null, false, false));
	}
	
	@Test
	void renderLockClauseForShareUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderLockClause(SqlLockMode.FOR_SHARE, false, false));
	}
	
	@Test
	void renderLockClauseSkipLockedUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderLockClause(SqlLockMode.FOR_UPDATE, true, false));
	}
	
	@Test
	void renderLockClauseNoWaitUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderLockClause(SqlLockMode.FOR_UPDATE, false, true));
	}
	
	@Test
	void getScalarTypeNameClob() {
		assertEquals("CHARACTER LARGE OBJECT", DIALECT.getScalarTypeName(Types.CLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameBlobAndLongVarBinary() {
		assertEquals("BINARY LARGE OBJECT", DIALECT.getScalarTypeName(Types.BLOB).orElseThrow());
		assertEquals("BINARY LARGE OBJECT", DIALECT.getScalarTypeName(Types.LONGVARBINARY).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameFallsBackToSuper() {
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.INTEGER).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameVarcharLength() {
		assertEquals("CHARACTER VARYING(64)", DIALECT.getParameterizedTypeName(Types.VARCHAR, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameVarbinaryLength() {
		assertEquals("BINARY VARYING(64)", DIALECT.getParameterizedTypeName(Types.VARBINARY, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameLengthFallsBackToSuper() {
		assertEquals("CHAR(64)", DIALECT.getParameterizedTypeName(Types.CHAR, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameNonLengthFallsBackToSuper() {
		assertEquals("NUMERIC(10, 2)", DIALECT.getParameterizedTypeName(Types.NUMERIC, SqlParameter.precision(10, 2)).orElseThrow());
	}
	
	@Test
	void isFeatureSupportedForSupportedFeature() {
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RECURSIVE_CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.NULLS_ORDERING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.SCHEMAS));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.WINDOW_FUNCTIONS));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.FOR_UPDATE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.IS_DISTINCT_FROM));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPSERT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ROW_LOCKING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RENAME_INDEX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ALTER_COLUMN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ADD_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.DROP_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT));
	}
	
	@Test
	void isFeatureSupportedForUnsupportedFeature() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.LATERAL_JOIN));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.FOR_SHARE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SKIP_LOCKED));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.NO_WAIT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRUNCATE_CASCADE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT_SUFFIX));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.INSERT_OR_IGNORE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ARRAY_TYPE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TABLE_REBUILD));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.JOINED_DML));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPDATE_RETURNING));
	}
	
	@Test
	void isIndexMethodSupportedForSupportedMethods() {
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.BTREE));
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.HASH));
	}
	
	@Test
	void isIndexMethodSupportedForUnsupportedMethod() {
		assertFalse(DIALECT.isIndexMethodSupported(SqlIndexMethod.GIN));
	}
	
	@Test
	void renderLockClauseForUpdateSucceeds() throws SqlException {
		assertTrue(DIALECT.renderLockClause(SqlLockMode.FOR_UPDATE, false, false).sql().contains("FOR UPDATE"));
	}
	
	@Test
	void nameReturnsH2() {
		assertEquals("H2", DIALECT.name());
	}
	
	@Test
	void getCheckConstraintsQueryStringContainsInformationSchema() {
		String query = DIALECT.getCheckConstraintsQueryString();
		assertTrue(query.contains("INFORMATION_SCHEMA.CHECK_CONSTRAINTS"));
		assertTrue(query.contains("TABLE_SCHEMA = ?"));
		assertTrue(query.contains("TABLE_NAME = ?"));
	}
	
	@Test
	void uuidTypeIsSupportedViaRegistry() throws SqlException {
		assertTrue(DIALECT.isTypeSupported(SqlTypes.UUID));
		assertEquals("UUID", DIALECT.getTypeName(SqlTypes.UUID));
	}
	
	@Test
	void jsonTypeIsSupportedViaRegistry() throws SqlException {
		assertEquals("JSON", DIALECT.getTypeName(SqlTypes.JSON));
	}
	
	@Test
	void temporalFunctionRoutesThroughH2Renderer() throws SqlException {
		SqlFromEpochFunction<?> function = new SqlFromEpochFunction<>(new SqlValueExpression<>(5), SqlTypes.LOCAL_DATE);
		String sql = DIALECT.renderFunction(function).sql();
		assertTrue(sql.contains("DATEADD"));
		assertTrue(sql.contains("TIMESTAMP '1970-01-01 00:00:00'"));
	}
}
