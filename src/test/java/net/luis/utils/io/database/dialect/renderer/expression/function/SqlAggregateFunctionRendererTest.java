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

import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.aggregate.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAggregateFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlAggregateFunctionRendererTest {
	
	private static final SqlAggregateFunctionRenderer RENDERER = new SqlAggregateFunctionRenderer(SqlDialects.DEFAULT);
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlAggregateFunctionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlAggregateFunctionRenderer(null));
	}
	
	@Test
	void renderNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownAggregateType() {
		SqlAggregateFunction<Object> unknown = new UnknownAggregateFunction();
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderAverageNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAverage(null));
	}
	
	@Test
	void renderCountNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCount(null));
	}
	
	@Test
	void renderCountDistinctNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCountDistinct(null));
	}
	
	@Test
	void renderMaxNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMax(null));
	}
	
	@Test
	void renderMinNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMin(null));
	}
	
	@Test
	void renderSumNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderSum(null));
	}
	
	@Test
	void renderAverageFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlAverageFunction<>(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("AVG("));
	}
	
	@Test
	void renderCountWithExpression() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlCountFunction(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("COUNT("));
		assertFalse(rendered.sql().contains("COUNT(*)"));
	}
	
	@Test
	void renderCountStar() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlCountFunction(null));
		assertEquals("COUNT(*)", rendered.sql());
	}
	
	@Test
	void renderCountDistinctFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlCountDistinctFunction(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("COUNT("));
		assertTrue(rendered.sql().contains("DISTINCT"));
	}
	
	@Test
	void renderMaxFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlMaxFunction<>(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("MAX("));
	}
	
	@Test
	void renderMinFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlMinFunction<>(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("MIN("));
	}
	
	@Test
	void renderSumFunction() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlSumFunction<>(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("SUM("));
	}
	
	@Test
	void renderSumOfColumn() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "stats");
		SqlColumn<Object, Integer> column = table.column("amount", SqlTypes.INTEGER, object -> 0);
		
		SqlRendered rendered = RENDERER.render(new SqlSumFunction<>(column));
		assertTrue(rendered.sql().contains("SUM("));
		assertTrue(rendered.sql().contains("\"amount\""));
	}
	
	private static final class UnknownAggregateFunction implements SqlAggregateFunction<Object> {
		
		@Override
		public @NonNull SqlType<Object> type() {
			throw new UnsupportedOperationException();
		}
	}
}
