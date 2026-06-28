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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.function.throwable.ThrowableBiConsumer;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRenderingHelper}.<br>
 *
 * @author Luis-St
 */
class SqlRenderingHelperTest {
	
	private static long countCommas(String sql) {
		return sql.chars().filter(c -> c == ',').count();
	}
	
	@Test
	void renderInfixWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderInfix(null, new SqlValueExpression<>(1), "+", new SqlValueExpression<>(2)));
	}
	
	@Test
	void renderInfixWithNullFirstOperand() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderInfix(SqlDialects.DEFAULT, null, "+", new SqlValueExpression<>(2)));
	}
	
	@Test
	void renderInfixWithNullOperator() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderInfix(SqlDialects.DEFAULT, new SqlValueExpression<>(1), null, new SqlValueExpression<>(2)));
	}
	
	@Test
	void renderInfixWithNullSecondOperand() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderInfix(SqlDialects.DEFAULT, new SqlValueExpression<>(1), "+", null));
	}
	
	@Test
	void renderFunctionCallWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderFunctionCall(null, "NOW"));
	}
	
	@Test
	void renderFunctionCallWithNullFunctionName() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderFunctionCall(SqlDialects.DEFAULT, null));
	}
	
	@Test
	void renderFunctionCallWithNullArgumentsArray() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderFunctionCall(SqlDialects.DEFAULT, "F", (SqlExpression<?>[]) null));
	}
	
	@Test
	void renderFunctionCallWithListNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderFunctionCallWithList(null, "COALESCE", List.of()));
	}
	
	@Test
	void renderFunctionCallWithListNullFunctionName() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderFunctionCallWithList(SqlDialects.DEFAULT, null, List.of()));
	}
	
	@Test
	void renderFunctionCallWithListNullValues() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderFunctionCallWithList(SqlDialects.DEFAULT, "COALESCE", null));
	}
	
	@Test
	void renderListWithNullRenderer() {
		ThrowableBiConsumer<SqlRenderer, String, SqlException> itemRenderer = (renderer, item) -> renderer.literal(item);
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderList(null, List.of("a"), itemRenderer));
	}
	
	@Test
	void renderListWithNullValues() {
		ThrowableBiConsumer<SqlRenderer, String, SqlException> itemRenderer = (renderer, item) -> renderer.literal(item);
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderList(SqlRenderer.empty(), null, itemRenderer));
	}
	
	@Test
	void renderListWithNullItemRenderer() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderList(SqlRenderer.empty(), List.<String>of(), null));
	}
	
	@Test
	void renderCastWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderCast(null, SqlRendered.of("\"x\""), SqlTypes.INTEGER));
	}
	
	@Test
	void renderCastWithNullInner() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderCast(SqlDialects.DEFAULT, null, SqlTypes.INTEGER));
	}
	
	@Test
	void renderCastWithNullType() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderCast(SqlDialects.DEFAULT, SqlRendered.of("\"x\""), null));
	}
	
	@Test
	void renderFunctionCallWithNoArguments() throws SqlException {
		SqlRendered rendered = SqlRenderingHelper.renderFunctionCall(SqlDialects.DEFAULT, "NOW");
		assertEquals("NOW()", rendered.sql());
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderFunctionCallWithSingleArgument() throws SqlException {
		SqlRendered rendered = SqlRenderingHelper.renderFunctionCall(SqlDialects.DEFAULT, "ABS", new SqlValueExpression<>(5));
		assertTrue(rendered.sql().contains("ABS("));
		assertTrue(rendered.sql().contains("?"));
		assertEquals(0, countCommas(rendered.sql()));
		assertEquals(1, rendered.parameters().size());
	}
	
	@Test
	void renderFunctionCallWithMultipleArguments() throws SqlException {
		SqlRendered rendered = SqlRenderingHelper.renderFunctionCall(SqlDialects.DEFAULT, "GREATEST", new SqlValueExpression<>(1), new SqlValueExpression<>(2));
		assertEquals(1, countCommas(rendered.sql()));
		assertEquals(2, rendered.parameters().size());
	}
	
	@Test
	void renderFunctionCallWithListEmpty() throws SqlException {
		SqlRendered rendered = SqlRenderingHelper.renderFunctionCallWithList(SqlDialects.DEFAULT, "COALESCE", List.of());
		assertTrue(rendered.sql().contains("COALESCE()"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderFunctionCallWithListMultipleValues() throws SqlException {
		List<SqlValueExpression<Integer>> values = List.of(new SqlValueExpression<>(1), new SqlValueExpression<>(2));
		SqlRendered rendered = SqlRenderingHelper.renderFunctionCallWithList(SqlDialects.DEFAULT, "COALESCE", values);
		assertEquals(1, countCommas(rendered.sql()));
		assertEquals(2, rendered.parameters().size());
	}
	
	@Test
	void renderListEmpty() {
		SqlRenderer renderer = SqlRenderer.empty();
		assertDoesNotThrow(() -> SqlRenderingHelper.renderList(renderer, List.<String>of(), (r, item) -> r.literal(item)));
		assertTrue(renderer.toSql().sql().isEmpty());
	}
	
	@Test
	void renderListSingleElement() throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		SqlRenderingHelper.renderList(renderer, List.of("a"), (r, item) -> r.literal(item));
		assertEquals("a", renderer.toSql().sql());
		assertEquals(0, countCommas(renderer.toSql().sql()));
	}
	
	@Test
	void renderListMultipleElements() throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		SqlRenderingHelper.renderList(renderer, List.of("a", "b", "c"), (r, item) -> r.literal(item));
		assertEquals(2, countCommas(renderer.toSql().sql()));
	}
	
	@Test
	void renderInfixSimpleExpression() throws SqlException {
		SqlRendered rendered = SqlRenderingHelper.renderInfix(SqlDialects.DEFAULT, new SqlValueExpression<>(1), "+", new SqlValueExpression<>(2));
		assertTrue(rendered.sql().contains("?"));
		assertTrue(rendered.sql().contains("+"));
		assertEquals(2, rendered.parameters().size());
	}
	
	@Test
	void renderCastSimpleType() throws SqlException {
		String typeName = SqlDialects.DEFAULT.getTypeName(SqlTypes.INTEGER);
		SqlRendered rendered = SqlRenderingHelper.renderCast(SqlDialects.DEFAULT, SqlRendered.of("\"x\""), SqlTypes.INTEGER);
		assertTrue(rendered.sql().contains("CAST("));
		assertTrue(rendered.sql().contains("AS"));
		assertTrue(rendered.sql().contains(typeName));
		assertTrue(rendered.sql().contains(")"));
	}
	
	@Test
	void renderFunctionCallWithNullArgumentElement() {
		assertThrows(NullPointerException.class, () -> SqlRenderingHelper.renderFunctionCall(SqlDialects.DEFAULT, "F", (SqlExpression<?>) null));
	}
	
	@Test
	void renderFunctionCallWithListContainingMultipleNestedExpressions() throws SqlException {
		List<SqlValueExpression<Integer>> values = List.of(new SqlValueExpression<>(1), new SqlValueExpression<>(2), new SqlValueExpression<>(3));
		SqlRendered rendered = SqlRenderingHelper.renderFunctionCallWithList(SqlDialects.DEFAULT, "COALESCE", values);
		assertEquals(2, countCommas(rendered.sql()));
		assertEquals(3, rendered.parameters().size());
	}
}
