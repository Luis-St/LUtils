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
import net.luis.utils.io.database.function.functions.numeric.SqlRandomFunction;
import net.luis.utils.io.database.function.functions.string.SqlConcatFunction;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlDialect}.<br>
 *
 * @author Luis-St
 */
class MySqlDialectTest {
	
	private static final MySqlDialect DIALECT = new MySqlDialect();
	
	@Test
	void isFeatureSupportedNullFeature() {
		assertThrows(NullPointerException.class, () -> DIALECT.isFeatureSupported(null));
	}
	
	@Test
	void isIndexMethodSupportedNullMethod() {
		assertThrows(NullPointerException.class, () -> DIALECT.isIndexMethodSupported(null));
	}
	
	@Test
	void getCastTypeNameNullType() {
		assertThrows(NullPointerException.class, () -> DIALECT.getCastTypeName(null));
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
	void renderUpsertClauseNullConflictColumn() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderUpsertClause(null, List.of()));
	}
	
	@Test
	void renderUpsertClauseNullUpdateColumns() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), null));
	}
	
	@Test
	void renderInsertOrIgnoreSuffixUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderInsertOrIgnoreSuffix(List.of()));
	}
	
	@Test
	void getCastTypeNameIntegralTypesUseSigned() throws SqlException {
		assertEquals("SIGNED", DIALECT.getCastTypeName(SqlTypes.INTEGER));
		assertEquals("SIGNED", DIALECT.getCastTypeName(SqlTypes.LONG));
		assertEquals("SIGNED", DIALECT.getCastTypeName(SqlTypes.BOOLEAN));
	}
	
	@Test
	void getCastTypeNameDoubleUsesDouble() throws SqlException {
		assertEquals("DOUBLE", DIALECT.getCastTypeName(SqlTypes.DOUBLE));
	}
	
	@Test
	void getCastTypeNameFloatUsesFloat() throws SqlException {
		assertEquals("FLOAT", DIALECT.getCastTypeName(SqlTypes.FLOAT));
	}
	
	@Test
	void getCastTypeNameOtherScalarFallsBackToTypeName() throws SqlException {
		assertEquals(DIALECT.getTypeName(SqlTypes.TEXT), DIALECT.getCastTypeName(SqlTypes.TEXT));
		assertNotEquals("SIGNED", DIALECT.getCastTypeName(SqlTypes.TEXT));
	}
	
	@Test
	void getCastTypeNameNonScalarFallsBackToTypeName() throws SqlException {
		assertEquals(DIALECT.getTypeName(SqlTestFixtures.STRING_TYPE), DIALECT.getCastTypeName(SqlTestFixtures.STRING_TYPE));
	}
	
	@Test
	void getScalarTypeNameBoolean() {
		assertEquals("TINYINT(1)", DIALECT.getScalarTypeName(Types.BOOLEAN).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameTinyint() {
		assertEquals("TINYINT", DIALECT.getScalarTypeName(Types.TINYINT).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameTextFamily() {
		assertEquals("LONGTEXT", DIALECT.getScalarTypeName(Types.LONGVARCHAR).orElseThrow());
		assertEquals("LONGTEXT", DIALECT.getScalarTypeName(Types.NCLOB).orElseThrow());
		assertEquals("LONGTEXT", DIALECT.getScalarTypeName(Types.CLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameBlobFamily() {
		assertEquals("LONGBLOB", DIALECT.getScalarTypeName(Types.LONGVARBINARY).orElseThrow());
		assertEquals("LONGBLOB", DIALECT.getScalarTypeName(Types.BLOB).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameFallsBackToSuper() {
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.INTEGER).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameTimestampWithTimezone() {
		assertEquals("DATETIME(3)", DIALECT.getParameterizedTypeName(Types.TIMESTAMP_WITH_TIMEZONE, SqlParameter.fractional(3)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameTimeWithTimezone() {
		assertEquals("TIME(3)", DIALECT.getParameterizedTypeName(Types.TIME_WITH_TIMEZONE, SqlParameter.fractional(3)).orElseThrow());
	}
	
	@Test
	void getParameterizedTypeNameFractionalFallsBackToSuper() {
		assertEquals("TIMESTAMP(3)", DIALECT.getParameterizedTypeName(Types.TIMESTAMP, SqlParameter.fractional(3)).orElseThrow());
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
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.FOR_SHARE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.SKIP_LOCKED));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.NO_WAIT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPSERT_SUFFIX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ROW_LOCKING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.INSERT_OR_IGNORE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RENAME_INDEX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ALTER_COLUMN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ADD_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.DROP_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.JOINED_DML));
	}
	
	@Test
	void isFeatureSupportedForUnsupportedFeature() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.LATERAL_JOIN));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.NULLS_ORDERING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRUNCATE_CASCADE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.IS_DISTINCT_FROM));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ARRAY_TYPE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TABLE_REBUILD));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT));
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
	void renderUpsertClauseSingleColumn() throws SqlException {
		String sql = DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), List.of(SqlTestFixtures.stringColumn())).sql();
		assertTrue(sql.contains("ON DUPLICATE KEY UPDATE"));
		assertTrue(sql.contains("VALUES(`name`)"));
		assertFalse(sql.contains(","));
	}
	
	@Test
	void renderUpsertClauseMultipleColumns() throws SqlException {
		List<SqlColumn<?, ?>> updates = List.of(SqlTestFixtures.integerColumn(), SqlTestFixtures.stringColumn());
		String sql = DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), updates).sql();
		assertTrue(sql.contains(","));
		assertTrue(sql.contains("VALUES(`id`)"));
		assertTrue(sql.contains("VALUES(`name`)"));
	}
	
	@Test
	void renderUpsertClauseEmptyColumns() throws SqlException {
		String sql = DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), List.of()).sql();
		assertTrue(sql.contains("ON DUPLICATE KEY UPDATE"));
		assertFalse(sql.contains("VALUES("));
	}
	
	@Test
	void usesInsertOrIgnoreModifierReturnsTrue() {
		assertTrue(DIALECT.usesInsertOrIgnoreModifier());
	}
	
	@Test
	void nameReturnsMySql() {
		assertEquals("MySQL", DIALECT.name());
	}
	
	@Test
	void quoteIdentifierUsesBackticks() {
		assertEquals("`col`", DIALECT.quoteIdentifier("col"));
	}
	
	@Test
	void quoteIdentifierEscapesBackticks() {
		assertEquals("`a``b`", DIALECT.quoteIdentifier("a`b"));
	}
	
	@Test
	void renderInsertOrIgnoreModifierReturnsIgnore() throws SqlException {
		assertEquals("IGNORE", DIALECT.renderInsertOrIgnoreModifier().sql());
	}
	
	@Test
	void getCheckConstraintsQueryStringContainsCheckConstraints() {
		String query = DIALECT.getCheckConstraintsQueryString();
		assertTrue(query.contains("CHECK_CONSTRAINTS"));
		assertTrue(query.contains("CONSTRAINT_SCHEMA = ?"));
		assertTrue(query.contains("TABLE_NAME = ?"));
	}
	
	@Test
	void getCheckConstraintsQueryStringJoinsTableConstraints() {
		String query = DIALECT.getCheckConstraintsQueryString();
		assertTrue(query.contains("TABLE_CONSTRAINTS"));
		assertTrue(query.contains("JOIN"));
	}
	
	@Test
	void jsonTypeSupportedViaRegistry() throws SqlException {
		assertEquals("JSON", DIALECT.getTypeName(SqlTypes.JSON));
	}
	
	@Test
	void stringConcatRoutesThroughMySqlRenderer() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(new SqlValueExpression<>("a"), new SqlValueExpression<>("b")), Optional.empty(), false, false);
		assertTrue(DIALECT.renderFunction(function).sql().contains("CONCAT("));
	}
	
	@Test
	void randomFunctionRoutesThroughMySqlRenderer() throws SqlException {
		assertEquals("RAND()", DIALECT.renderFunction(new SqlRandomFunction()).sql());
	}
}
