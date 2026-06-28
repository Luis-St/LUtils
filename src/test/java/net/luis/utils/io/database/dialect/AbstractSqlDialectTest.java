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

import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.*;
import net.luis.utils.io.database.expression.orderable.*;
import net.luis.utils.io.database.function.window.*;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AbstractSqlDialect}.<br>
 *
 * @author Luis-St
 */
class AbstractSqlDialectTest {
	
	private static final TestDialect DIALECT = new TestDialect();
	private static final SqlType<?> UNSUPPORTED_TYPE = SqlTypes.INTEGER.array();
	
	private static String sql(SqlRendered rendered) {
		return rendered.sql();
	}
	
	private static String referentialAction(SqlReferentialAction action) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		DIALECT.renderReferentialAction(renderer, action);
		return sql(renderer.toSql());
	}
	//endregion
	
	//region Tier 1 - Constructors
	@Test
	void constructInitializesRegistryAndRenderer() {
		TestDialect dialect = new TestDialect();
		assertNotNull(dialect.tableRenderer());
		assertNotNull(dialect.indexRenderer());
		assertNotNull(dialect.columnRenderer());
		assertNotNull(dialect.migrationRenderer());
		assertNotNull(dialect.schemaRenderer());
	}
	
	//region Tier 2 - Exceptions
	@Test
	void isTypeSupportedNullType() {
		assertThrows(NullPointerException.class, () -> DIALECT.isTypeSupported(null));
	}
	
	@Test
	void getTypeNameNullType() {
		assertThrows(NullPointerException.class, () -> DIALECT.getTypeName(null));
	}
	
	@Test
	void getTypeNameUnsupportedTypeThrows() {
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> DIALECT.getTypeName(UNSUPPORTED_TYPE));
	}
	
	@Test
	void bindingOverrideNullType() {
		assertThrows(NullPointerException.class, () -> DIALECT.bindingOverride(null));
	}
	
	@Test
	void readingOverrideNullType() {
		assertThrows(NullPointerException.class, () -> DIALECT.readingOverride(null));
	}
	
	@Test
	void getParameterizedTypeNameNullParameter() {
		assertThrows(NullPointerException.class, () -> DIALECT.getParameterizedTypeName(Types.VARCHAR, null));
	}
	
	@Test
	void getLengthParameterizedTypeNameNullParameter() {
		assertThrows(NullPointerException.class, () -> DIALECT.getLengthParameterizedTypeName(Types.VARCHAR, null));
	}
	
	@Test
	void getPrecisionParameterizedTypeNameNullParameter() {
		assertThrows(NullPointerException.class, () -> DIALECT.getPrecisionParameterizedTypeName(Types.NUMERIC, null));
	}
	
	@Test
	void getFractionalParameterizedTypeNameNullParameter() {
		assertThrows(NullPointerException.class, () -> DIALECT.getFractionalParameterizedTypeName(Types.TIME, null));
	}
	
	@Test
	void renderWindowClauseNullClause() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderWindowClause(null));
	}
	
	@Test
	void renderOrderingItemNullRenderer() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderOrderingItem(null, SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void renderOrderingItemNullOrderable() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderOrderingItem(SqlRenderer.empty(), null));
	}
	
	@Test
	void renderWindowFrameNullFrame() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderWindowFrame(null));
	}
	
	@Test
	void renderFrameBoundNullBound() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderFrameBound(null));
	}
	
	@Test
	void renderWindowFrameUnknownTypeThrows() {
		SqlWindowFrame frame = new DummyWindowFrame();
		assertThrows(SqlDialectUnknownConstructException.class, () -> DIALECT.renderWindowFrame(frame));
	}
	
	@Test
	void renderFrameBoundUnknownTypeThrows() {
		SqlFrameBound bound = new DummyFrameBound();
		assertThrows(SqlDialectUnknownConstructException.class, () -> DIALECT.renderFrameBound(bound));
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
	void getIndexMethodNameNullMethod() {
		assertThrows(NullPointerException.class, () -> DIALECT.getIndexMethodName(null));
	}
	
	@Test
	void getIndexMethodNameUnsupportedThrows() {
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> DIALECT.getIndexMethodName(SqlIndexMethod.HASH));
	}
	
	@Test
	void renderValueLiteralNullValue() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderValueLiteral(null));
	}
	
	@Test
	void quoteIdentifierNullIdentifier() {
		assertThrows(NullPointerException.class, () -> DIALECT.quoteIdentifier(null));
	}
	
	@Test
	void renderReferentialActionNullRenderer() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderReferentialAction(null, SqlReferentialAction.CASCADE));
	}
	
	@Test
	void renderReferentialActionNullAction() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderReferentialAction(SqlRenderer.empty(), null));
	}
	
	@Test
	void renderLimitOffsetLimitBelowNegativeOne() {
		assertThrows(IllegalArgumentException.class, () -> DIALECT.renderLimitOffset(-2, 0, false));
	}
	
	@Test
	void renderLimitOffsetNegativeOffset() {
		assertThrows(IllegalArgumentException.class, () -> DIALECT.renderLimitOffset(10, -1, false));
	}
	
	@Test
	void renderLimitOffsetOffsetWithoutLimitUnsupported() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderLimitOffset(-1, 5, false));
	}
	
	@Test
	void renderStandardReturningNullColumns() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderStandardReturning(null));
	}
	
	@Test
	void renderLockClauseNullMode() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderLockClause(null, false, false));
	}
	
	@Test
	void renderSetOperationNullOperation() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderSetOperation(null));
	}
	
	@Test
	void renderOrderingNullOrdering() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderOrdering(null, SqlNullOrdering.DEFAULT));
	}
	
	@Test
	void renderOrderingNullNullOrdering() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderOrdering(SqlOrdering.ASCENDING, null));
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
	void renderInsertOrIgnoreSuffixNullColumns() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderInsertOrIgnoreSuffix(null));
	}
	
	@Test
	void getCheckConstraintsNullConnection() {
		assertThrows(NullPointerException.class, () -> DIALECT.getCheckConstraints(null, "public", "test_table"));
	}
	
	@Test
	void getCheckConstraintsNullSchema() {
		assertThrows(NullPointerException.class, () -> DIALECT.getCheckConstraints(SqlTestFixtures.placeholderConnection(), null, "test_table"));
	}
	
	@Test
	void getCheckConstraintsNullTableName() {
		assertThrows(NullPointerException.class, () -> DIALECT.getCheckConstraints(SqlTestFixtures.placeholderConnection(), "public", null));
	}
	
	@Test
	void renderReturningUnsupportedByDefault() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderReturning(List.of()));
	}
	
	@Test
	void renderLateralJoinUnsupportedByDefault() {
		assertThrows(SqlDialectFeatureException.class, DIALECT::renderLateralJoin);
	}
	
	@Test
	void renderUpsertStatementUnsupportedByDefault() {
		assertThrows(SqlDialectFeatureException.class, () -> DIALECT.renderUpsertStatement(SqlTestFixtures.sampleTable(), List.of(), SqlTestFixtures.integerColumn(), SqlRendered.of("")));
	}
	
	@Test
	void renderInsertOrIgnoreModifierUnsupportedByDefault() {
		assertThrows(SqlDialectFeatureException.class, DIALECT::renderInsertOrIgnoreModifier);
	}
	//endregion
	
	@Test
	void renderOrderingItemUnknownOrderableThrows() {
		assertThrows(SqlDialectException.class, () -> DIALECT.renderOrderingItem(SqlRenderer.empty(), new DummyOrderable()));
	}
	
	//region Tier 3 - Branch coverage
	@Test
	void isTypeSupportedResolvableType() {
		assertTrue(DIALECT.isTypeSupported(SqlTypes.INTEGER));
	}
	
	@Test
	void isTypeSupportedUnresolvableTypeFalse() {
		assertFalse(DIALECT.isTypeSupported(UNSUPPORTED_TYPE));
	}
	
	@Test
	void getScalarTypeNameKnownTypes() {
		assertEquals("BOOLEAN", DIALECT.getScalarTypeName(Types.BOOLEAN).orElseThrow());
		assertEquals("INTEGER", DIALECT.getScalarTypeName(Types.INTEGER).orElseThrow());
		assertEquals("BIGINT", DIALECT.getScalarTypeName(Types.BIGINT).orElseThrow());
		assertEquals("DOUBLE PRECISION", DIALECT.getScalarTypeName(Types.DOUBLE).orElseThrow());
		assertEquals("TEXT", DIALECT.getScalarTypeName(Types.LONGVARCHAR).orElseThrow());
		assertEquals("BLOB", DIALECT.getScalarTypeName(Types.BLOB).orElseThrow());
		assertEquals("DATE", DIALECT.getScalarTypeName(Types.DATE).orElseThrow());
	}
	
	@Test
	void getScalarTypeNameUnknownTypeEmpty() {
		assertTrue(DIALECT.getScalarTypeName(Types.OTHER).isEmpty());
	}
	
	@Test
	void getScalarTypeNameRemainingKnownTypes() {
		assertEquals("TINYINT", DIALECT.getScalarTypeName(Types.TINYINT).orElseThrow());
		assertEquals("SMALLINT", DIALECT.getScalarTypeName(Types.SMALLINT).orElseThrow());
		assertEquals("REAL", DIALECT.getScalarTypeName(Types.REAL).orElseThrow());
		assertEquals("FLOAT", DIALECT.getScalarTypeName(Types.FLOAT).orElseThrow());
		assertEquals("NCLOB", DIALECT.getScalarTypeName(Types.LONGNVARCHAR).orElseThrow());
		assertEquals("NCLOB", DIALECT.getScalarTypeName(Types.NCLOB).orElseThrow());
		assertEquals("CLOB", DIALECT.getScalarTypeName(Types.CLOB).orElseThrow());
	}
	
	@Test
	void getLengthParameterizedTypeNameKnownTypes() {
		assertEquals("CHAR(64)", DIALECT.getLengthParameterizedTypeName(Types.CHAR, SqlParameter.length(64)).orElseThrow());
		assertEquals("VARCHAR(64)", DIALECT.getLengthParameterizedTypeName(Types.VARCHAR, SqlParameter.length(64)).orElseThrow());
		assertEquals("NCHAR(64)", DIALECT.getLengthParameterizedTypeName(Types.NCHAR, SqlParameter.length(64)).orElseThrow());
		assertEquals("NVARCHAR(64)", DIALECT.getLengthParameterizedTypeName(Types.NVARCHAR, SqlParameter.length(64)).orElseThrow());
		assertEquals("BINARY(64)", DIALECT.getLengthParameterizedTypeName(Types.BINARY, SqlParameter.length(64)).orElseThrow());
		assertEquals("VARBINARY(64)", DIALECT.getLengthParameterizedTypeName(Types.VARBINARY, SqlParameter.length(64)).orElseThrow());
	}
	
	@Test
	void getLengthParameterizedTypeNameUnknownEmpty() {
		assertTrue(DIALECT.getLengthParameterizedTypeName(Types.OTHER, SqlParameter.length(64)).isEmpty());
	}
	
	@Test
	void getPrecisionParameterizedTypeNameNumericAndDecimal() {
		assertEquals("NUMERIC(10, 2)", DIALECT.getPrecisionParameterizedTypeName(Types.NUMERIC, SqlParameter.precision(10, 2)).orElseThrow());
		assertEquals("DECIMAL(10, 2)", DIALECT.getPrecisionParameterizedTypeName(Types.DECIMAL, SqlParameter.precision(10, 2)).orElseThrow());
	}
	
	@Test
	void getPrecisionParameterizedTypeNameUnknownEmpty() {
		assertTrue(DIALECT.getPrecisionParameterizedTypeName(Types.OTHER, SqlParameter.precision(10, 2)).isEmpty());
	}
	
	@Test
	void getFractionalParameterizedTypeNameKnownTypes() {
		assertEquals("TIME(3)", DIALECT.getFractionalParameterizedTypeName(Types.TIME, SqlParameter.fractional(3)).orElseThrow());
		assertEquals("TIMESTAMP(3)", DIALECT.getFractionalParameterizedTypeName(Types.TIMESTAMP, SqlParameter.fractional(3)).orElseThrow());
		assertEquals("TIME(3) WITH TIME ZONE", DIALECT.getFractionalParameterizedTypeName(Types.TIME_WITH_TIMEZONE, SqlParameter.fractional(3)).orElseThrow());
		assertEquals("TIMESTAMP(3) WITH TIME ZONE", DIALECT.getFractionalParameterizedTypeName(Types.TIMESTAMP_WITH_TIMEZONE, SqlParameter.fractional(3)).orElseThrow());
	}
	
	@Test
	void getFractionalParameterizedTypeNameUnknownEmpty() {
		assertTrue(DIALECT.getFractionalParameterizedTypeName(Types.OTHER, SqlParameter.fractional(3)).isEmpty());
	}
	
	@Test
	void renderWindowClauseEmptyClause() throws SqlException {
		assertTrue(sql(DIALECT.renderWindowClause(SqlWindowClause.of())).isBlank());
	}
	
	@Test
	void renderWindowClauseWithPartitions() throws SqlException {
		SqlWindowClause clause = SqlWindowClause.partitionBy(SqlTestFixtures.integerColumn());
		String sql = sql(DIALECT.renderWindowClause(clause));
		assertTrue(sql.contains("PARTITION"));
		assertTrue(sql.contains("\"id\""));
	}
	
	@Test
	void renderWindowClauseWithOrderings() throws SqlException {
		SqlWindowClause clause = SqlWindowClause.of().orderBy(SqlTestFixtures.stringExpression());
		String sql = sql(DIALECT.renderWindowClause(clause));
		assertTrue(sql.contains("ORDER BY"));
	}
	
	@Test
	void renderWindowClauseWithFrame() throws SqlException {
		SqlWindowFrame frame = SqlWindowFrame.rows(SqlFrameBound.unboundedPreceding(), SqlFrameBound.currentRow());
		SqlWindowClause clause = new SqlWindowClause(List.of(), List.of(), frame);
		String sql = sql(DIALECT.renderWindowClause(clause));
		assertTrue(sql.contains("ROWS"));
		assertTrue(sql.contains("BETWEEN"));
	}
	
	@Test
	void renderOrderingItemOrderedExpression() throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		DIALECT.renderOrderingItem(renderer, SqlTestFixtures.stringExpression().ascending());
		assertTrue(sql(renderer.toSql()).contains("ASC"));
	}
	
	@Test
	void renderOrderingItemPlainExpression() throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		DIALECT.renderOrderingItem(renderer, SqlTestFixtures.stringExpression());
		assertFalse(sql(renderer.toSql()).isBlank());
	}
	
	@Test
	void renderWindowFrameRows() throws SqlException {
		String sql = sql(DIALECT.renderWindowFrame(SqlWindowFrame.rows(SqlFrameBound.unboundedPreceding(), SqlFrameBound.currentRow())));
		assertTrue(sql.startsWith("ROWS"));
		assertTrue(sql.contains("BETWEEN"));
		assertTrue(sql.contains("AND"));
	}
	
	@Test
	void renderWindowFrameRange() throws SqlException {
		String sql = sql(DIALECT.renderWindowFrame(SqlWindowFrame.range(SqlFrameBound.unboundedPreceding(), SqlFrameBound.currentRow())));
		assertTrue(sql.startsWith("RANGE"));
		assertTrue(sql.contains("BETWEEN"));
	}
	
	@Test
	void renderWindowFrameGroups() throws SqlException {
		String sql = sql(DIALECT.renderWindowFrame(SqlWindowFrame.groups(SqlFrameBound.unboundedPreceding(), SqlFrameBound.currentRow())));
		assertTrue(sql.startsWith("GROUPS"));
		assertTrue(sql.contains("BETWEEN"));
	}
	
	@Test
	void renderFrameBoundUnboundedPreceding() throws SqlException {
		assertEquals("UNBOUNDED PRECEDING", sql(DIALECT.renderFrameBound(SqlFrameBound.unboundedPreceding())));
	}
	
	@Test
	void renderFrameBoundPreceding() throws SqlException {
		assertTrue(sql(DIALECT.renderFrameBound(SqlFrameBound.preceding(3))).contains("PRECEDING"));
	}
	
	@Test
	void renderFrameBoundCurrentRow() throws SqlException {
		assertEquals("CURRENT ROW", sql(DIALECT.renderFrameBound(SqlFrameBound.currentRow())));
	}
	
	@Test
	void renderFrameBoundFollowing() throws SqlException {
		assertTrue(sql(DIALECT.renderFrameBound(SqlFrameBound.following(3))).contains("FOLLOWING"));
	}
	
	@Test
	void renderFrameBoundUnboundedFollowing() throws SqlException {
		assertEquals("UNBOUNDED FOLLOWING", sql(DIALECT.renderFrameBound(SqlFrameBound.unboundedFollowing())));
	}
	
	@Test
	void isFeatureSupportedAlwaysFalseInBase() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.CTE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
	}
	
	@Test
	void isIndexMethodSupportedBtreeOnly() {
		assertTrue(DIALECT.isIndexMethodSupported(SqlIndexMethod.BTREE));
		assertFalse(DIALECT.isIndexMethodSupported(SqlIndexMethod.HASH));
	}
	
	@Test
	void getIndexMethodNameSupported() throws SqlException {
		assertEquals("BTREE", DIALECT.getIndexMethodName(SqlIndexMethod.BTREE));
	}
	
	@Test
	void renderValueLiteralNumber() throws SqlException {
		assertEquals("42", DIALECT.renderValueLiteral(42));
	}
	
	@Test
	void renderValueLiteralBoolean() throws SqlException {
		assertEquals("TRUE", DIALECT.renderValueLiteral(true));
	}
	
	@Test
	void renderValueLiteralStringEscapesQuotes() throws SqlException {
		assertEquals("'O''Brien'", DIALECT.renderValueLiteral("O'Brien"));
	}
	
	@Test
	void quoteIdentifierWrapsAndEscapes() {
		assertEquals("\"na\"\"me\"", DIALECT.quoteIdentifier("na\"me"));
	}
	
	@Test
	void renderReferentialActionAllVariants() throws SqlException {
		assertTrue(referentialAction(SqlReferentialAction.NO_ACTION).contains("NO ACTION"));
		assertTrue(referentialAction(SqlReferentialAction.RESTRICT).contains("RESTRICT"));
		assertTrue(referentialAction(SqlReferentialAction.CASCADE).contains("CASCADE"));
		assertTrue(referentialAction(SqlReferentialAction.SET_NULL).contains("SET NULL"));
		assertTrue(referentialAction(SqlReferentialAction.SET_DEFAULT).contains("SET DEFAULT"));
	}
	
	@Test
	void renderLimitOffsetLimitOnly() throws SqlException {
		String sql = sql(DIALECT.renderLimitOffset(10, 0, false));
		assertTrue(sql.contains("LIMIT 10"));
		assertFalse(sql.contains("OFFSET"));
	}
	
	@Test
	void renderLimitOffsetOffsetOnly() throws SqlException {
		String sql = sql(DIALECT.renderLimitOffset(5, 3, false));
		assertTrue(sql.contains("LIMIT 5"));
		assertTrue(sql.contains("OFFSET 3"));
	}
	
	@Test
	void renderLimitOffsetZeroOffsetSkipsOffset() throws SqlException {
		String sql = sql(DIALECT.renderLimitOffset(0, 0, false));
		assertTrue(sql.contains("LIMIT 0"));
		assertFalse(sql.contains("OFFSET"));
	}
	
	@Test
	void renderLimitOffsetNoLimitNoOffset() throws SqlException {
		String sql = sql(DIALECT.renderLimitOffset(-1, 0, false));
		assertFalse(sql.contains("LIMIT"));
		assertFalse(sql.contains("OFFSET"));
		assertTrue(sql.isBlank());
	}
	
	@Test
	void renderStandardReturningEmptyColumns() throws SqlException {
		String sql = sql(DIALECT.renderStandardReturning(List.of()));
		assertTrue(sql.contains("RETURNING"));
		assertTrue(sql.contains("*"));
	}
	
	@Test
	void renderStandardReturningWithColumns() throws SqlException {
		String sql = sql(DIALECT.renderStandardReturning(List.of(SqlTestFixtures.integerColumn())));
		assertTrue(sql.contains("RETURNING"));
		assertTrue(sql.contains("\"id\""));
	}
	
	@Test
	void renderLockClauseForUpdate() throws SqlException {
		assertTrue(sql(DIALECT.renderLockClause(SqlLockMode.FOR_UPDATE, false, false)).contains("FOR UPDATE"));
	}
	
	@Test
	void renderLockClauseForShare() throws SqlException {
		assertTrue(sql(DIALECT.renderLockClause(SqlLockMode.FOR_SHARE, false, false)).contains("FOR SHARE"));
	}
	
	@Test
	void renderLockClauseSkipLocked() throws SqlException {
		assertTrue(sql(DIALECT.renderLockClause(SqlLockMode.FOR_UPDATE, true, false)).contains("SKIP LOCKED"));
	}
	
	@Test
	void renderLockClauseNoWait() throws SqlException {
		assertTrue(sql(DIALECT.renderLockClause(SqlLockMode.FOR_UPDATE, false, true)).contains("NOWAIT"));
	}
	
	@Test
	void renderSetOperationAllVariants() throws SqlException {
		assertEquals("UNION", sql(DIALECT.renderSetOperation(SqlSetOperation.UNION)));
		assertEquals("UNION ALL", sql(DIALECT.renderSetOperation(SqlSetOperation.UNION_ALL)));
		assertEquals("INTERSECT", sql(DIALECT.renderSetOperation(SqlSetOperation.INTERSECT)));
		assertEquals("EXCEPT", sql(DIALECT.renderSetOperation(SqlSetOperation.EXCEPT)));
	}
	
	@Test
	void renderBooleanLiteralTrue() throws SqlException {
		assertEquals("TRUE", sql(DIALECT.renderBooleanLiteral(true)));
	}
	
	@Test
	void renderBooleanLiteralFalse() throws SqlException {
		assertEquals("FALSE", sql(DIALECT.renderBooleanLiteral(false)));
	}
	
	@Test
	void renderOrderingAscending() throws SqlException {
		assertTrue(sql(DIALECT.renderOrdering(SqlOrdering.ASCENDING, SqlNullOrdering.DEFAULT)).contains("ASC"));
	}
	
	@Test
	void renderOrderingDescending() throws SqlException {
		assertTrue(sql(DIALECT.renderOrdering(SqlOrdering.DESCENDING, SqlNullOrdering.DEFAULT)).contains("DESC"));
	}
	
	@Test
	void renderOrderingDefaultOrdering() throws SqlException {
		String sql = sql(DIALECT.renderOrdering(SqlOrdering.DEFAULT, SqlNullOrdering.DEFAULT));
		assertFalse(sql.contains("ASC"));
		assertFalse(sql.contains("DESC"));
	}
	
	@Test
	void renderOrderingNullsFirst() throws SqlException {
		assertTrue(sql(DIALECT.renderOrdering(SqlOrdering.DEFAULT, SqlNullOrdering.NULLS_FIRST)).contains("FIRST"));
	}
	
	@Test
	void renderOrderingNullsLast() throws SqlException {
		assertTrue(sql(DIALECT.renderOrdering(SqlOrdering.DEFAULT, SqlNullOrdering.NULLS_LAST)).contains("LAST"));
	}
	
	@Test
	void renderOrderingDefaultNullOrdering() throws SqlException {
		String sql = sql(DIALECT.renderOrdering(SqlOrdering.DEFAULT, SqlNullOrdering.DEFAULT));
		assertFalse(sql.contains("NULLS"));
	}
	
	@Test
	void renderUpsertClauseSingleColumn() throws SqlException {
		String sql = sql(DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), List.of(SqlTestFixtures.stringColumn())));
		assertTrue(sql.contains("CONFLICT"));
		assertTrue(sql.contains("EXCLUDED.\"name\""));
		assertFalse(sql.contains(","));
	}
	
	@Test
	void renderUpsertClauseMultipleColumns() throws SqlException {
		List<SqlColumn<?, ?>> updates = List.of(SqlTestFixtures.integerColumn(), SqlTestFixtures.stringColumn());
		String sql = sql(DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), updates));
		assertTrue(sql.contains(","));
		assertTrue(sql.contains("EXCLUDED.\"id\""));
		assertTrue(sql.contains("EXCLUDED.\"name\""));
	}
	
	@Test
	void renderUpsertClauseEmptyColumns() throws SqlException {
		String sql = sql(DIALECT.renderUpsertClause(SqlTestFixtures.integerColumn(), List.of()));
		assertTrue(sql.contains("DO"));
		assertFalse(sql.contains("EXCLUDED."));
	}
	
	@Test
	void renderInsertOrIgnoreSuffixSingleColumn() throws SqlException {
		String sql = sql(DIALECT.renderInsertOrIgnoreSuffix(List.of(SqlTestFixtures.integerColumn())));
		assertTrue(sql.contains("\"id\""));
		assertTrue(sql.contains("NOTHING"));
	}
	
	@Test
	void renderInsertOrIgnoreSuffixMultipleColumns() throws SqlException {
		List<SqlColumn<?, ?>> columns = List.of(SqlTestFixtures.integerColumn(), SqlTestFixtures.stringColumn());
		String sql = sql(DIALECT.renderInsertOrIgnoreSuffix(columns));
		assertTrue(sql.contains(","));
		assertTrue(sql.contains("\"id\""));
		assertTrue(sql.contains("\"name\""));
	}
	
	@Test
	void renderInsertOrIgnoreSuffixEmptyColumns() throws SqlException {
		String sql = sql(DIALECT.renderInsertOrIgnoreSuffix(List.of()));
		assertTrue(sql.contains("CONFLICT"));
		assertTrue(sql.contains("NOTHING"));
	}
	
	@Test
	void getCheckConstraintsNullQueryReturnsEmpty() throws SqlException {
		assertTrue(DIALECT.getCheckConstraints(SqlTestFixtures.placeholderConnection(), "public", "test_table").isEmpty());
	}
	//endregion
	
	//region Tier 4 - Simple inputs
	@Test
	void readXmlElementNullReturnsNull() throws SQLException {
		assertNull(AbstractSqlDialect.readXmlElement(null));
	}
	
	@Test
	void readXmlElementWithDeclaration() throws SQLException {
		XmlElement element = AbstractSqlDialect.readXmlElement(new StringSqlXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>"));
		assertNotNull(element);
		assertEquals("root", element.getName());
	}
	
	@Test
	void readXmlElementWithoutDeclaration() throws SQLException {
		XmlElement element = AbstractSqlDialect.readXmlElement(new StringSqlXml("<root/>"));
		assertNotNull(element);
		assertEquals("root", element.getName());
	}
	
	@Test
	void getTypeNameResolvableScalar() throws SqlException {
		assertEquals("INTEGER", DIALECT.getTypeName(SqlTypes.INTEGER));
	}
	
	@Test
	void getTypeNameResolvableParameterized() throws SqlException {
		assertEquals("VARCHAR(64)", DIALECT.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(64))));
	}
	
	@Test
	void renderExpressionDelegates() throws SqlException {
		SqlRendered rendered = DIALECT.renderExpression(SqlTestFixtures.stringExpression());
		assertNotNull(rendered);
		assertFalse(rendered.sql().isBlank());
	}
	
	@Test
	void renderConditionDelegates() throws SqlException {
		SqlRendered rendered = DIALECT.renderCondition(SqlTestFixtures.alwaysCondition());
		assertNotNull(rendered);
	}
	//endregion
	
	//region Tier 5 - Complex inputs
	@Test
	void getCreateSchemaColumnsTableSqlStructure() throws SqlException {
		String sql = DIALECT.getCreateSchemaColumnsTableSql();
		assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS"));
		assertTrue(sql.contains("\"_sql_schema_columns\""));
		assertTrue(sql.contains("PRIMARY KEY("));
		assertTrue(sql.contains("VARCHAR(64)"));
		assertTrue(sql.contains("INTEGER"));
		assertTrue(sql.contains("BOOLEAN"));
	}
	
	@Test
	void getCreateSchemaCheckConstraintsTableSqlStructure() throws SqlException {
		String sql = DIALECT.getCreateSchemaCheckConstraintsTableSql();
		assertTrue(sql.contains("\"_sql_schema_check_constraints\""));
		assertTrue(sql.contains("\"check_clause\""));
		assertTrue(sql.contains("TEXT"));
		assertTrue(sql.contains("PRIMARY KEY("));
	}
	
	@Test
	void getInsertSchemaColumnSqlPlaceholders() {
		String sql = DIALECT.getInsertSchemaColumnSql();
		assertTrue(sql.startsWith("INSERT INTO"));
		assertTrue(sql.endsWith("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"));
	}
	
	@Test
	void getInsertSchemaCheckConstraintSqlPlaceholders() {
		assertTrue(DIALECT.getInsertSchemaCheckConstraintSql().endsWith("VALUES (?, ?, ?, ?)"));
	}
	
	@Test
	void getSelectSchemaColumnsSqlStructure() {
		String sql = DIALECT.getSelectSchemaColumnsSql();
		assertTrue(sql.startsWith("SELECT * FROM"));
		assertTrue(sql.contains("WHERE \"version\" = ?"));
		assertTrue(sql.contains("ORDER BY \"table_name\", \"ordinal_position\""));
	}
	
	@Test
	void getSelectSchemaCheckConstraintsSqlStructure() {
		String sql = DIALECT.getSelectSchemaCheckConstraintsSql();
		assertTrue(sql.startsWith("SELECT * FROM"));
		assertTrue(sql.contains("WHERE \"version\" = ?"));
	}
	
	@Test
	void getDeleteSchemaColumnsSqlStructure() {
		String sql = DIALECT.getDeleteSchemaColumnsSql();
		assertTrue(sql.startsWith("DELETE FROM"));
		assertTrue(sql.contains("WHERE \"version\" = ?"));
	}
	
	@Test
	void getDeleteSchemaCheckConstraintsSqlStructure() {
		String sql = DIALECT.getDeleteSchemaCheckConstraintsSql();
		assertTrue(sql.startsWith("DELETE FROM"));
		assertTrue(sql.contains("WHERE \"version\" = ?"));
	}
	//endregion
	
	//region Test fixtures
	private static final class TestDialect extends AbstractSqlDialect {
		
		private final String checkQuery;
		
		private TestDialect() {
			this(null);
		}
		
		private TestDialect(@Nullable String checkQuery) {
			this.checkQuery = checkQuery;
		}
		
		@Override
		public @NonNull String name() {
			return "Test";
		}
		
		@Override
		protected @Nullable String getCheckConstraintsQueryString() {
			return this.checkQuery;
		}
	}
	
	private static final class DummyWindowFrame implements SqlWindowFrame {
		
		@Override
		public @NonNull SqlFrameBound start() {
			return SqlFrameBound.currentRow();
		}
		
		@Override
		public @NonNull SqlFrameBound end() {
			return SqlFrameBound.currentRow();
		}
	}
	
	private static final class DummyFrameBound implements SqlFrameBound {}
	
	private static final class DummyOrderable implements SqlOrderable<Object> {
		
		@Override
		public @NonNull OrderedSqlExpression<Object> ascending() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull OrderedSqlExpression<Object> descending() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull OrderedSqlExpression<Object> nullsFirst() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull OrderedSqlExpression<Object> nullsLast() {
			throw new UnsupportedOperationException();
		}
	}
	
	private record StringSqlXml(@NonNull String content) implements SQLXML {
		
		@Override
		public String getString() {
			return this.content;
		}
		
		@Override
		public void setString(String value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void free() {}
		
		@Override
		public InputStream getBinaryStream() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public OutputStream setBinaryStream() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Reader getCharacterStream() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Writer setCharacterStream() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public <T extends javax.xml.transform.Source> T getSource(Class<T> sourceClass) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public <T extends javax.xml.transform.Result> T setResult(Class<T> resultClass) {
			throw new UnsupportedOperationException();
		}
	}
	//endregion
}
