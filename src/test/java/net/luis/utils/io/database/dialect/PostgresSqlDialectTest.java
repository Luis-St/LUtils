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
import net.luis.utils.io.database.condition.conditions.comparison.SqlInListCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.SqlNumericTruncateFunction;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PostgresSqlDialect}.<br>
 *
 * @author Luis-St
 */
class PostgresSqlDialectTest {
	
	private static final PostgresSqlDialect DIALECT = new PostgresSqlDialect();
	
	@Test
	void isTypeSupportedNullType() {
		assertThrows(NullPointerException.class, () -> DIALECT.isTypeSupported(null));
	}
	
	@Test
	void getTypeNameNullType() {
		assertThrows(NullPointerException.class, () -> DIALECT.getTypeName(null));
	}
	
	@Test
	void isFeatureSupportedNullFeature() {
		assertThrows(NullPointerException.class, () -> DIALECT.isFeatureSupported(null));
	}
	
	@Test
	void isIndexMethodSupportedNullMethod() {
		assertThrows(NullPointerException.class, () -> DIALECT.isIndexMethodSupported(null));
	}
	
	@Test
	void renderReturningNullColumns() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderReturning(null));
	}
	
	@Test
	void isTypeSupportedAlwaysTrue() {
		assertTrue(DIALECT.isTypeSupported(SqlTypes.INTEGER.array()));
	}
	
	@Test
	void getTypeNameArrayTypeAppendsBrackets() throws SqlException {
		assertEquals("INTEGER[]", DIALECT.getTypeName(SqlTypes.INTEGER.array()));
	}
	
	@Test
	void getTypeNameNonArrayFallsBackToSuper() throws SqlException {
		assertEquals("INTEGER", DIALECT.getTypeName(SqlTypes.INTEGER));
	}
	
	@Test
	void getScalarTypeNameByteaFamily() {
		assertEquals("BYTEA", DIALECT.getScalarTypeName(Types.LONGVARBINARY).orElseThrow());
		assertEquals("BYTEA", DIALECT.getScalarTypeName(Types.BLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameTextFamily() {
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.LONGNVARCHAR).orElseThrow());
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.NCLOB).orElseThrow());
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.CLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameFallsBackToSuper() {
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.INTEGER).orElseThrow());
	}
	
	@Test
	void getLengthParameterizedTypeNameNchar() {
		assertEquals("CHAR(64)", DIALECT.getLengthParameterizedTypeName(Types.NCHAR, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void getLengthParameterizedTypeNameNvarchar() {
		assertEquals("VARCHAR(64)", DIALECT.getLengthParameterizedTypeName(Types.NVARCHAR, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void getLengthParameterizedTypeNameFallsBackToSuper() {
		assertEquals("VARCHAR(64)", DIALECT.getLengthParameterizedTypeName(Types.VARCHAR, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void isFeatureSupportedForSupportedFeature() {
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPDATE_RETURNING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.LATERAL_JOIN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RECURSIVE_CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.NULLS_ORDERING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.SCHEMAS));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.WINDOW_FUNCTIONS));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.FOR_UPDATE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.FOR_SHARE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.SKIP_LOCKED));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.NO_WAIT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.TRUNCATE_CASCADE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.IS_DISTINCT_FROM));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPSERT_SUFFIX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ROW_LOCKING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.INSERT_OR_IGNORE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RENAME_INDEX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ALTER_COLUMN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ADD_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.DROP_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ARRAY_TYPE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT));
	}
	
	@Test
	void isFeatureSupportedForUnsupportedFeature() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TABLE_REBUILD));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.JOINED_DML));
	}
	
	@Test
	void isIndexMethodSupportedForSupportedMethods() {
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.GIN));
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.GIST));
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.BRIN));
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.SPGIST));
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.BTREE));
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.HASH));
	}
	
	@Test
	void isIndexMethodSupportedForUnsupportedMethod() {
		assertFalse(DIALECT.isIndexMethodSupported(SqlIndexMethod.CLUSTERED));
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
	void nameReturnsPostgreSql() {
		assertEquals("PostgreSQL", DIALECT.name());
	}
	
	@Test
	void renderLateralJoinReturnsLateral() throws SqlException {
		assertEquals("LATERAL", DIALECT.renderLateralJoin().sql());
	}
	
	@Test
	void getCheckConstraintsQueryStringContainsPgConstraint() {
		String query = DIALECT.getCheckConstraintsQueryString();
		assertTrue(query.contains("pg_constraint"));
		assertTrue(query.contains("nspname = ?"));
		assertTrue(query.contains("relname = ?"));
	}
	
	@Test
	void jsonTypeMapsToJsonb() throws SqlException {
		assertEquals("JSONB", DIALECT.getTypeName(SqlTypes.JSON));
	}
	
	@Test
	void ipAddressMapsToInet() throws SqlException {
		assertEquals("INET", DIALECT.getTypeName(SqlTypes.IP_ADDRESS));
	}
	
	@Test
	void ipNetworkMapsToCidr() throws SqlException {
		assertEquals("CIDR", DIALECT.getTypeName(SqlTypes.IP_NETWORK));
	}
	
	@Test
	void uuidMapsToUuid() throws SqlException {
		assertEquals("UUID", DIALECT.getTypeName(SqlTypes.UUID));
	}
	
	@Test
	void xmlMapsToXml() throws SqlException {
		assertEquals("XML", DIALECT.getTypeName(SqlTypes.XML));
	}
	
	@Test
	void inListRoutesThroughPostgresRenderer() throws SqlException {
		List<SqlExpression<?>> options = List.of(new SqlValueExpression<>(1), new SqlValueExpression<>(2));
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options);
		assertTrue(DIALECT.renderCondition(condition).sql().contains("= ANY("));
	}
	
	@Test
	void truncateRoutesThroughPostgresNumericRenderer() throws SqlException {
		SqlNumericTruncateFunction<?> function = new SqlNumericTruncateFunction<>(new SqlValueExpression<>(5));
		assertTrue(DIALECT.renderFunction(function).sql().contains("TRUNC("));
	}
}
