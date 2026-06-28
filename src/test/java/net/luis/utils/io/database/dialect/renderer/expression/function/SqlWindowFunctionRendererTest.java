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

package net.luis.utils.io.database.dialect.renderer.expression.function;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlWindowFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlSumFunction;
import net.luis.utils.io.database.function.functions.window.*;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlWindowFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlWindowFunctionRendererTest {
	
	private static final SqlDialect DIALECT = SqlDialects.DEFAULT;
	private static final SqlAggregateFunctionRenderer AGGREGATE = new SqlAggregateFunctionRenderer(DIALECT);
	private static final SqlWindowFunctionRenderer RENDERER = new SqlWindowFunctionRenderer(DIALECT, AGGREGATE);
	
	private static int countOccurrences(String text, String needle) {
		int count = 0;
		int index = 0;
		while ((index = text.indexOf(needle, index)) != -1) {
			count++;
			index += needle.length();
		}
		return count;
	}
	
	@Test
	void constructWithDialectAndAggregateRenderer() {
		assertNotNull(new SqlWindowFunctionRenderer(DIALECT, AGGREGATE));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlWindowFunctionRenderer(null, AGGREGATE));
	}
	
	@Test
	void constructWithNullAggregateRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlWindowFunctionRenderer(DIALECT, null));
	}
	
	@Test
	void renderNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownWindowType() {
		SqlWindowFunction<Object> unknown = new UnknownWindowFunction();
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderWindowCallWithNullFunctionName() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderWindowCall(null, SqlWindowClause.of()));
	}
	
	@Test
	void renderWindowCallWithNullOver() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderWindowCall("ROW_NUMBER", null));
	}
	
	@Test
	void renderWindowCallWithNullArgumentsArray() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderWindowCall("ROW_NUMBER", SqlWindowClause.of(), (SqlExpression<?>[]) null));
	}
	
	@Test
	void renderCumulativeDistributionNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCumulativeDistribution(null));
	}
	
	@Test
	void renderDenseRankNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDenseRank(null));
	}
	
	@Test
	void renderFirstValueNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderFirstValue(null));
	}
	
	@Test
	void renderLagNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLag(null));
	}
	
	@Test
	void renderLastValueNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLastValue(null));
	}
	
	@Test
	void renderLeadNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLead(null));
	}
	
	@Test
	void renderPercentRankNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderPercentRank(null));
	}
	
	@Test
	void renderRankNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRank(null));
	}
	
	@Test
	void renderRowNumberNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRowNumber(null));
	}
	
	@Test
	void renderTileBucketNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTileBucket(null));
	}
	
	@Test
	void renderValueAtNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderValueAt(null));
	}
	
	@Test
	void renderWindowedAggregateNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderWindowedAggregate(null));
	}
	
	@Test
	void renderCumulativeDistributionFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlCumulativeDistributionFunction<>(SqlWindowClause.of(), SqlTypes.DOUBLE));
		assertTrue(rendered.sql().contains("CUME_DIST("));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	@Test
	void renderDenseRankFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlDenseRankFunction<>(SqlWindowClause.of(), SqlTypes.LONG));
		assertTrue(rendered.sql().contains("DENSE_RANK("));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	@Test
	void renderFirstValueFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlFirstValueFunction<>(new SqlValueExpression<>(5), SqlWindowClause.of()));
		assertTrue(rendered.sql().contains("FIRST_VALUE("));
	}
	
	@Test
	void renderLagFunctionAllArgs() throws SqlException {
		SqlLagFunction<Integer> function = new SqlLagFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(1), new SqlValueExpression<>(0), SqlWindowClause.of());
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("LAG("));
		assertEquals(2, countOccurrences(rendered.sql(), ","));
	}
	
	@Test
	void renderLagFunctionNullArgsFiltered() throws SqlException {
		SqlLagFunction<Integer> function = new SqlLagFunction<>(new SqlValueExpression<>(5), null, null, SqlWindowClause.of());
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("LAG("));
		assertFalse(rendered.sql().contains(","));
	}
	
	@Test
	void renderLastValueFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlLastValueFunction<>(new SqlValueExpression<>(5), SqlWindowClause.of()));
		assertTrue(rendered.sql().contains("LAST_VALUE("));
	}
	
	@Test
	void renderLeadFunctionAllArgs() throws SqlException {
		SqlLeadFunction<Integer> function = new SqlLeadFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(1), new SqlValueExpression<>(0), SqlWindowClause.of());
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("LEAD("));
		assertEquals(2, countOccurrences(rendered.sql(), ","));
	}
	
	@Test
	void renderPercentRankFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlPercentRankFunction<>(SqlWindowClause.of(), SqlTypes.DOUBLE));
		assertTrue(rendered.sql().contains("PERCENT_RANK("));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	@Test
	void renderRankFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlRankFunction<>(SqlWindowClause.of(), SqlTypes.LONG));
		assertTrue(rendered.sql().contains("RANK("));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	@Test
	void renderRowNumberFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlRowNumberFunction<>(SqlWindowClause.of(), SqlTypes.LONG));
		assertTrue(rendered.sql().contains("ROW_NUMBER("));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	@Test
	void renderTileBucketFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlTileBucketFunction<>(new SqlValueExpression<>(4), SqlWindowClause.of(), SqlTypes.INTEGER));
		assertTrue(rendered.sql().contains("NTILE("));
	}
	
	@Test
	void renderValueAtFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlValueAtFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(2), SqlWindowClause.of()));
		assertTrue(rendered.sql().contains("NTH_VALUE("));
	}
	
	@Test
	void renderWindowedAggregateFunction() throws SqlException {
		SqlWindowedAggregate<Integer> function = new SqlWindowedAggregate<>(new SqlSumFunction<>(new SqlValueExpression<>(5)), SqlWindowClause.of());
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("SUM("));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	@Test
	void renderRowNumberNoArguments() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlRowNumberFunction<>(SqlWindowClause.of(), SqlTypes.LONG));
		assertTrue(rendered.sql().contains("ROW_NUMBER()"));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	@Test
	void renderLagWithDefaultValueExpression() throws SqlException {
		SqlExpression<Integer> defaultValue = new SqlValueExpression<>(99);
		SqlLagFunction<Integer> function = new SqlLagFunction<>(new SqlValueExpression<>(5), new SqlValueExpression<>(1), defaultValue, SqlWindowClause.of());
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("LAG("));
		assertEquals(2, countOccurrences(rendered.sql(), ","));
		assertTrue(rendered.sql().contains("OVER("));
	}
	
	private static final class UnknownWindowFunction implements SqlWindowFunction<Object> {
		
		@Override
		public @NonNull SqlType<Object> type() {
			throw new UnsupportedOperationException();
		}
	}
}
