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

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.SqlNullOrdering;
import net.luis.utils.io.database.expression.orderable.SqlOrdering;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.window.*;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.migration.SqlCheckConstraintInfo;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.query.util.SqlSetClause;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */
class SqlDialectTest {
	
	/**
	 * Builds a {@link Connection} stub reporting the given schema and catalog, every other method is rejected.
	 *
	 * @param schema The schema {@code getSchema()} reports
	 * @param catalog The catalog {@code getCatalog()} reports
	 * @return The connection stub
	 */
	private static @NonNull Connection stubConnection(@Nullable String schema, @Nullable String catalog) {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, (proxy, method, args) -> switch (method.getName()) {
			case "getSchema" -> schema;
			case "getCatalog" -> catalog;
			case "toString" -> "StubConnection";
			default -> throw new UnsupportedOperationException(method.getName());
		});
	}
	
	/**
	 * Builds a {@link Connection} stub that throws a {@link SQLException} from one of the two schema getters.
	 *
	 * @param failOnSchema {@code true} to fail in {@code getSchema()}, {@code false} to report no schema and fail in {@code getCatalog()}
	 * @return The connection stub
	 */
	private static @NonNull Connection failingConnection(boolean failOnSchema) {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, (proxy, method, args) -> switch (method.getName()) {
			case "getSchema" -> {
				if (failOnSchema) {
					throw new SQLException("Schema lookup failed in tests");
				}
				yield null;
			}
			case "getCatalog" -> throw new SQLException("Catalog lookup failed in tests");
			case "toString" -> "FailingStubConnection";
			default -> throw new UnsupportedOperationException(method.getName());
		});
	}
	
	/**
	 * Builds a rendered fragment carrying a single integer parameter, used to source parameter lists.
	 *
	 * @param value The parameter value
	 * @return The rendered fragment
	 */
	private static @NonNull SqlRendered single(int value) {
		return SqlRenderer.empty().parameter(SqlTypes.INTEGER, value).toSql();
	}
	
	/**
	 * Builds a rendered fragment carrying two integer parameters, used to source parameter lists.
	 *
	 * @param first The first parameter value
	 * @param second The second parameter value
	 * @return The rendered fragment
	 */
	private static @NonNull SqlRendered pair(int first, int second) {
		return SqlRenderer.empty().parameter(SqlTypes.INTEGER, first).parameter(SqlTypes.INTEGER, second).toSql();
	}
	
	@Test
	void bindingOverrideDefaultReturnsEmpty() {
		assertTrue(new StubDialect().bindingOverride(SqlTypes.INTEGER).isEmpty());
	}
	
	@Test
	void readingOverrideDefaultReturnsEmpty() {
		assertTrue(new StubDialect().readingOverride(SqlTypes.INTEGER).isEmpty());
	}
	
	@Test
	void maxBindParametersDefaultValue() {
		assertEquals(65535, new StubDialect().maxBindParameters());
	}
	
	@Test
	void resolveTypeWithNullNativeType() {
		StubDialect dialect = new StubDialect();
		assertThrows(NullPointerException.class, () -> dialect.resolveType(null));
	}
	
	@Test
	void resolveTypeReturnsPortableType() {
		assertEquals(Optional.of(SqlTypes.INTEGER), new StubDialect().resolveType(new SqlNativeType(Types.INTEGER, "int4", 10, 0)));
	}
	
	@Test
	void resolveTypeReturnsEmptyForUnsupportedType() {
		assertEquals(Optional.empty(), new StubDialect().resolveType(new SqlNativeType(Types.OTHER, "uuid", 0, 0)));
	}
	
	@Test
	void resolveTypeIgnoresTypeNameByDefault() {
		StubDialect dialect = new StubDialect();
		Optional<SqlType<?>> first = dialect.resolveType(new SqlNativeType(Types.VARCHAR, "text", 64, 0));
		Optional<SqlType<?>> second = dialect.resolveType(new SqlNativeType(Types.VARCHAR, "varchar", 64, 0));
		assertEquals(Optional.of(SqlTypes.STRING.configure(SqlParameter.length(64))), first);
		assertEquals(first, second);
	}
	
	@Test
	void schemaTableConstantsHaveExpectedNames() {
		assertEquals("_sql_schema_columns", SqlDialect.SCHEMA_COLUMNS_TABLE);
		assertEquals("_sql_schema_check_constraints", SqlDialect.SCHEMA_CHECK_CONSTRAINTS_TABLE);
	}
	
	@Test
	void resolveTypeMatchesAbstractDialectForPortableTypes() {
		StubDialect stub = new StubDialect();
		List<SqlNativeType> nativeTypes = List.of(
			new SqlNativeType(Types.INTEGER, "int", 10, 0),
			new SqlNativeType(Types.VARCHAR, "varchar", 64, 0),
			new SqlNativeType(Types.TIMESTAMP, "timestamp", 26, 6),
			new SqlNativeType(Types.LONGVARBINARY, "longblob", 0, 0)
		);
		for (SqlNativeType nativeType : nativeTypes) {
			assertEquals(stub.resolveType(nativeType), SqlDialects.H2.resolveType(nativeType), "Mismatch for " + nativeType);
		}
	}
	
	@Test
	void renderConditionInlineWithNullCondition() {
		StubDialect dialect = new StubDialect();
		assertThrows(NullPointerException.class, () -> dialect.renderConditionInline(null));
	}
	
	@Test
	void renderConditionInlineWithMorePlaceholdersThanParameters() {
		SqlRendered rendered = new SqlRendered(List.of("\"count\"", ">=", "?", "?"), single(0).parameters());
		StubDialect dialect = new StubDialect(rendered);
		SqlDialectUnsupportedRenderingException thrown = assertThrows(SqlDialectUnsupportedRenderingException.class, () -> dialect.renderConditionInline(SqlCondition.always()));
		assertTrue(thrown.getMessage().contains("Stub"));
		assertTrue(thrown.getMessage().contains("more placeholders than parameters"));
	}
	
	@Test
	void renderConditionInlineWithMoreParametersThanPlaceholders() {
		SqlRendered rendered = new SqlRendered(List.of("\"count\"", ">=", "?"), pair(1, 2).parameters());
		StubDialect dialect = new StubDialect(rendered);
		SqlDialectUnsupportedRenderingException thrown = assertThrows(SqlDialectUnsupportedRenderingException.class, () -> dialect.renderConditionInline(SqlCondition.always()));
		assertTrue(thrown.getMessage().contains("more parameters than placeholders"));
	}
	
	@Test
	void renderConditionInlineWithUnrenderableValue() {
		SqlRendered rendered = SqlRenderer.empty().literal("\"kind\"").literal("=").parameter(SqlTypes.LARGE_BYTES, new byte[] { 1, 2 }).toSql();
		StubDialect dialect = new StubDialect(rendered);
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> dialect.renderConditionInline(SqlCondition.always()));
	}
	
	@Test
	void renderConditionInlineWithoutParametersReturnsRenderedUnchanged() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"active\"").literal("IS NOT NULL").toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertSame(rendered, result);
		assertTrue(result.parameters().isEmpty());
	}
	
	@Test
	void renderConditionInlineReplacesPlaceholderWithLiteral() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"count\"").literal(">=").parameter(SqlTypes.INTEGER, 0).toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertEquals("\"count\" >= 0", result.sql());
		assertFalse(result.sql().contains("?"));
		assertTrue(result.parameters().isEmpty());
	}
	
	@Test
	void renderConditionInlineKeepsNonPlaceholderTokens() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"count\"").literal(">=").parameter(SqlTypes.INTEGER, 7).toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertEquals(List.of("\"count\"", ">=", "7"), result.statements());
	}
	
	@Test
	void renderConditionInlineWithNullParameterValue() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"kind\"").literal("=").parameter(SqlTypes.INTEGER, null).toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertEquals("\"kind\" = NULL", result.sql());
		assertFalse(result.sql().contains("'null'"));
	}
	
	@Test
	void renderConditionInlineWithEmptyTokenList() {
		SqlRendered rendered = new SqlRendered(List.of(), single(0).parameters());
		StubDialect dialect = new StubDialect(rendered);
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> dialect.renderConditionInline(SqlCondition.always()));
	}
	
	@Test
	void renderConditionInlineWithMultiplePlaceholdersPreservesOrder() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty()
			.literal("\"a\"").literal("=").parameter(SqlTypes.INTEGER, 1)
			.literal("AND").literal("\"b\"").literal("=").parameter(SqlTypes.INTEGER, 2)
			.toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertEquals("\"a\" = 1 AND \"b\" = 2", result.sql());
	}
	
	@Test
	void renderConditionInlineResultHasNoParameters() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"count\"").literal(">=").parameter(SqlTypes.INTEGER, 0).toSql();
		assertEquals(1, rendered.parameters().size());
		assertTrue(new StubDialect(rendered).renderConditionInline(SqlCondition.always()).parameters().isEmpty());
	}
	
	@Test
	void renderConditionInlineWithStringValueUsesDialectQuoting() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"kind\"").literal("=").parameter(SqlTypes.TEXT, "O'Brien").toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertEquals("\"kind\" = 'O''Brien'", result.sql());
	}
	
	@Test
	void renderConditionInlineWithBooleanValue() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"active\"").literal("=").parameter(SqlTypes.BOOLEAN, true).toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertEquals("\"active\" = TRUE", result.sql());
		assertFalse(result.sql().contains("'true'"));
	}
	
	@Test
	void renderConditionInlineWithMixedNullAndValueParameters() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty()
			.parameter(SqlTypes.INTEGER, 1)
			.parameter(SqlTypes.INTEGER, null)
			.parameter(SqlTypes.TEXT, "A")
			.toSql();
		SqlRendered result = new StubDialect(rendered).renderConditionInline(SqlCondition.always());
		assertEquals(List.of("1", "NULL", "'A'"), result.statements());
		assertTrue(result.parameters().isEmpty());
	}
	
	@Test
	void renderConditionInlineOnRealDialectRendersComparisonWithoutParameters() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		SqlColumn<Object, Integer> count = table.column("count", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = SqlDialects.H2.renderConditionInline(Sql.greaterThanOrEqualTo(count, 0));
		assertTrue(rendered.sql().contains(">= 0"));
		assertFalse(rendered.sql().contains("?"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderConditionInlineWithNestedConditionCombinators() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		SqlColumn<Object, Integer> count = table.column("count", SqlTypes.INTEGER, object -> 0);
		SqlColumn<Object, String> kind = table.column("kind", SqlTypes.TEXT, object -> "");
		SqlRendered rendered = SqlDialects.H2.renderConditionInline(SqlCondition.allOf(Sql.greaterThanOrEqualTo(count, 0), Sql.equalTo(kind, "A")));
		assertTrue(rendered.sql().contains(">= 0"));
		assertTrue(rendered.sql().contains("'A'"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderCheckConditionWithNullCondition() {
		StubDialect dialect = new StubDialect();
		assertThrows(NullPointerException.class, () -> dialect.renderCheckCondition(null));
	}
	
	@Test
	void renderCheckConditionWithMorePlaceholdersThanParameters() {
		SqlRendered rendered = new SqlRendered(List.of("\"count\"", ">=", "?", "?"), single(0).parameters());
		StubDialect dialect = new StubDialect(rendered);
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> dialect.renderCheckCondition(SqlCondition.always()));
	}
	
	@Test
	void defaultSchemaWithNullConnection() {
		StubDialect dialect = new StubDialect();
		assertThrows(NullPointerException.class, () -> dialect.defaultSchema(null));
	}
	
	@Test
	void defaultSchemaWithFailingConnection() {
		StubDialect dialect = new StubDialect();
		Connection connection = failingConnection(true);
		SqlSchemaIntrospectionException thrown = assertThrows(SqlSchemaIntrospectionException.class, () -> dialect.defaultSchema(connection));
		assertInstanceOf(SQLException.class, thrown.getCause());
		assertTrue(thrown.getMessage().contains("Stub"));
	}
	
	@Test
	void defaultSchemaWithConnectionFailingOnCatalog() {
		StubDialect dialect = new StubDialect();
		Connection connection = failingConnection(false);
		SqlSchemaIntrospectionException thrown = assertThrows(SqlSchemaIntrospectionException.class, () -> dialect.defaultSchema(connection));
		assertInstanceOf(SQLException.class, thrown.getCause());
		assertTrue(thrown.getMessage().contains("Stub"));
	}
	
	@Test
	void introspectionCatalogWithNullSchema() {
		StubDialect dialect = new StubDialect();
		assertThrows(NullPointerException.class, () -> dialect.introspectionCatalog(null));
	}
	
	@Test
	void introspectionSchemaWithNullSchema() {
		StubDialect dialect = new StubDialect();
		assertThrows(NullPointerException.class, () -> dialect.introspectionSchema(null));
	}
	
	@Test
	void renderCheckConditionStripsTableQualifier() throws SqlException {
		SqlRendered rendered = new SqlRendered(List.of("\"users\".\"age\"", ">=", "18"), List.of());
		SqlRendered result = new StubDialect(rendered).renderCheckCondition(SqlCondition.always());
		assertEquals(List.of("\"age\"", ">=", "18"), result.statements());
		assertFalse(result.sql().contains("\"users\"."));
		assertTrue(result.parameters().isEmpty());
	}
	
	@Test
	void renderCheckConditionKeepsUnqualifiedIdentifier() throws SqlException {
		SqlRendered rendered = new SqlRendered(List.of("\"age\"", ">=", "18"), List.of());
		SqlRendered result = new StubDialect(rendered).renderCheckCondition(SqlCondition.always());
		assertEquals(List.of("\"age\"", ">=", "18"), result.statements());
	}
	
	@Test
	void renderCheckConditionKeepsKeywordsAndOperatorTokens() throws SqlException {
		SqlRendered rendered = new SqlRendered(List.of(">", "AND", "'A'", "NULL"), List.of());
		SqlRendered result = new StubDialect(rendered).renderCheckCondition(SqlCondition.always());
		assertEquals(List.of(">", "AND", "'A'", "NULL"), result.statements());
	}
	
	@Test
	void renderCheckConditionKeepsTokenWithDotButNoClosingQuote() throws SqlException {
		SqlRendered rendered = new SqlRendered(List.of("\"a\".\"b"), List.of());
		SqlRendered result = new StubDialect(rendered).renderCheckCondition(SqlCondition.always());
		assertEquals(List.of("\"a\".\"b"), result.statements());
	}
	
	@Test
	void renderCheckConditionWithDialectWithoutQuoting() throws SqlException {
		SqlRendered rendered = new SqlRendered(List.of("\"users\".\"age\"", ">=", "18"), List.of());
		SqlRendered result = new StubDialect(rendered, false).renderCheckCondition(SqlCondition.always());
		assertEquals(List.of("\"users\".\"age\"", ">=", "18"), result.statements());
	}
	
	@Test
	void renderCheckConditionWithoutParametersReturnsUnqualifiedTokens() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"users\".\"active\"").literal("IS NOT NULL").toSql();
		SqlRendered result = new StubDialect(rendered).renderCheckCondition(SqlCondition.always());
		assertEquals(List.of("\"active\"", "IS NOT NULL"), result.statements());
		assertTrue(result.parameters().isEmpty());
	}
	
	@Test
	void renderCheckConditionWithParametersRendersLiterals() throws SqlException {
		SqlRendered rendered = SqlRenderer.empty().literal("\"users\".\"count\"").literal(">=").parameter(SqlTypes.INTEGER, 0).toSql();
		SqlRendered result = new StubDialect(rendered).renderCheckCondition(SqlCondition.always());
		assertEquals("\"count\" >= 0", result.sql());
		assertFalse(result.sql().contains("?"));
		assertTrue(result.parameters().isEmpty());
	}
	
	@Test
	void renderCheckConditionWithEmptyTokenList() throws SqlException {
		SqlRendered rendered = new SqlRendered(List.of(), List.of());
		SqlRendered result = new StubDialect(rendered).renderCheckCondition(SqlCondition.always());
		assertTrue(result.statements().isEmpty());
		assertTrue(result.parameters().isEmpty());
	}
	
	@Test
	void defaultSchemaReturnsConnectionSchema() throws SqlException {
		assertEquals("app", new StubDialect().defaultSchema(stubConnection("app", "catalog")));
	}
	
	@Test
	void defaultSchemaFallsBackToCatalogWhenSchemaNull() throws SqlException {
		assertEquals("catalog", new StubDialect().defaultSchema(stubConnection(null, "catalog")));
	}
	
	@Test
	void defaultSchemaFallsBackToCatalogWhenSchemaBlank() throws SqlException {
		assertEquals("catalog", new StubDialect().defaultSchema(stubConnection("   ", "catalog")));
	}
	
	@Test
	void defaultSchemaFallsBackToPublicWhenSchemaAndCatalogNull() throws SqlException {
		assertEquals("public", new StubDialect().defaultSchema(stubConnection(null, null)));
	}
	
	@Test
	void defaultSchemaFallsBackToPublicWhenCatalogBlank() throws SqlException {
		assertEquals("public", new StubDialect().defaultSchema(stubConnection(null, "   ")));
	}
	
	@Test
	void supportsOffsetTemporalTypesDefaultsToTrue() {
		assertTrue(new StubDialect().supportsOffsetTemporalTypes());
	}
	
	@Test
	void usesRecursiveCteKeywordDefaultsToTrue() {
		assertTrue(new StubDialect().usesRecursiveCteKeyword());
	}
	
	@Test
	void requiresRecursiveCteColumnListDefaultsToFalse() {
		assertFalse(new StubDialect().requiresRecursiveCteColumnList());
	}
	
	@Test
	void requiresJoinedDeleteTargetDefaultsToFalse() {
		assertFalse(new StubDialect().requiresJoinedDeleteTarget());
	}
	
	@Test
	void introspectionCatalogDefaultsToNull() {
		assertNull(new StubDialect().introspectionCatalog("public"));
	}
	
	@Test
	void introspectionSchemaDefaultsToGivenSchema() {
		assertEquals("public", new StubDialect().introspectionSchema("public"));
	}
	
	@Test
	void renderCheckConditionWithCombinedQualifiedConditions() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		SqlColumn<Object, Integer> count = table.column("count", SqlTypes.INTEGER, object -> 0);
		SqlColumn<Object, String> kind = table.column("kind", SqlTypes.TEXT, object -> "");
		SqlRendered rendered = SqlDialects.H2.renderCheckCondition(SqlCondition.allOf(Sql.greaterThanOrEqualTo(count, 0), Sql.equalTo(kind, "A")));
		
		assertFalse(rendered.sql().contains("\"items\"."));
		assertFalse(rendered.sql().contains("?"));
		assertTrue(rendered.parameters().isEmpty());
		assertTrue(rendered.sql().indexOf("\"count\"") < rendered.sql().indexOf("\"kind\""));
	}
	
	@Test
	void renderCheckConditionIsUsableAsCreateTableCheckBody() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		SqlColumn<Object, Integer> count = table.column("count", SqlTypes.INTEGER, object -> 0);
		assertNotNull(table.column("limit", SqlTypes.INTEGER, object -> 0, builder -> builder.addConstraint(Sql.greaterThanOrEqualTo(count, 0))));
		SqlRendered body = SqlDialects.H2.renderCheckCondition(Sql.greaterThanOrEqualTo(count, 0));
		SqlRendered createTable = SqlDialects.H2.tableRenderer().renderCreateTable(table, false);
		
		assertTrue(createTable.sql().contains("CHECK"), createTable.sql());
		assertTrue(createTable.sql().contains(body.sql()), createTable.sql());
		assertFalse(createTable.sql().contains("\"items\".\"count\" >="));
	}
	
	private record StubDialect(SqlRendered conditionRendered, boolean quoting) implements SqlDialect {
		
		private StubDialect() {
			this(null);
		}
		
		private StubDialect(@Nullable SqlRendered conditionRendered) {
			this(conditionRendered, true);
		}
		
		private StubDialect(@Nullable SqlRendered conditionRendered, boolean quoting) {
			this.conditionRendered = conditionRendered;
			this.quoting = quoting;
		}
		
		@Override
		public @NonNull String name() {
			return "Stub";
		}
		
		@Override
		public @NonNull SqlTableRenderer tableRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlIndexRenderer indexRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlColumnRenderer columnRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlMigrationOperationRenderer migrationRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlSchemaRenderer schemaRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isTypeSupported(@NonNull SqlType<?> type) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getTypeName(@NonNull SqlType<?> type) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) {
			Objects.requireNonNull(condition, "Sql condition must not be null");
			if (this.conditionRendered == null) {
				throw new UnsupportedOperationException();
			}
			return this.conditionRendered;
		}
		
		@Override
		public @NonNull SqlRendered renderWindowClause(@NonNull SqlWindowClause clause) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderWindowFrame(@NonNull SqlWindowFrame frame) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderFrameBound(@NonNull SqlFrameBound bound) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isFeatureSupported(@NonNull SqlFeature feature) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String renderValueLiteral(@NonNull Object value) throws SqlException {
			Objects.requireNonNull(value, "Value must not be null");
			if (value instanceof Number) {
				return value.toString();
			}
			if (value instanceof Boolean bool) {
				return bool ? "TRUE" : "FALSE";
			}
			if (value.getClass().isArray()) {
				throw new SqlDialectUnsupportedRenderingException("Array literals are not supported by dialect " + this.name());
			}
			return "'" + value.toString().replace("'", "''") + "'";
		}
		
		@Override
		public @NonNull String quoteIdentifier(@NonNull String identifier) {
			return this.quoting ? "\"" + identifier + "\"" : identifier;
		}
		
		@Override
		public void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderLimitOffset(long limit, long offset, boolean hasOrdering) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderSetOperation(@NonNull SqlSetOperation operation) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderLateralJoin() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderBooleanLiteral(boolean value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderOrdering(@NonNull SqlOrdering ordering, @NonNull SqlNullOrdering nullOrdering) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderUpsertClause(@NonNull List<SqlColumn<?, ?>> conflictColumns, @NonNull List<SqlSetClause<?, ?>> updateClauses) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderUpsertStatement(@NonNull SqlTable<?> table, @NonNull List<SqlColumn<?, ?>> columns, @NonNull List<SqlColumn<?, ?>> conflictColumns, @NonNull SqlRendered valueTuples) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlExpression<?> upsertExcludedValue(@NonNull SqlColumn<?, ?> column) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderInsertOrIgnoreModifier() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull List<SqlCheckConstraintInfo> getCheckConstraints(@NonNull Connection connection, @NonNull String schema, @NonNull String tableName) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getCreateSchemaColumnsTableSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getCreateSchemaCheckConstraintsTableSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getInsertSchemaColumnSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getInsertSchemaCheckConstraintSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getSelectSchemaColumnsSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getSelectSchemaCheckConstraintsSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getDeleteSchemaColumnsSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getDeleteSchemaCheckConstraintsSql() {
			throw new UnsupportedOperationException();
		}
	}
}
