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
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.aggregate.SqlCountFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlSumFunction;
import net.luis.utils.io.database.function.functions.generic.SqlCastFunction;
import net.luis.utils.io.database.function.functions.generic.SqlCoalesceFunction;
import net.luis.utils.io.database.function.functions.numeric.SqlAbsFunction;
import net.luis.utils.io.database.function.functions.string.SqlUpperFunction;
import net.luis.utils.io.database.function.functions.temporal.SqlCurrentDateFunction;
import net.luis.utils.io.database.function.functions.window.SqlRowNumberFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlFunctionRendererTest {
	
	private static final SqlDialect DIALECT = SqlDialects.DEFAULT;
	private static final SqlAggregateFunctionRenderer AGGREGATE = new SqlAggregateFunctionRenderer(DIALECT);
	private static final SqlFunctionRenderer RENDERER = new SqlFunctionRenderer(
		DIALECT,
		AGGREGATE,
		new SqlNumericFunctionRenderer(DIALECT),
		new SqlStringFunctionRenderer(DIALECT),
		new SqlTemporalFunctionRenderer(DIALECT),
		new SqlWindowFunctionRenderer(DIALECT, AGGREGATE),
		new SqlGenericFunctionRenderer(DIALECT)
	);
	
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
	void constructWithRenderers() {
		assertNotNull(RENDERER);
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlFunctionRenderer(
			null, AGGREGATE, new SqlNumericFunctionRenderer(DIALECT), new SqlStringFunctionRenderer(DIALECT),
			new SqlTemporalFunctionRenderer(DIALECT), new SqlWindowFunctionRenderer(DIALECT, AGGREGATE), new SqlGenericFunctionRenderer(DIALECT)
		));
	}
	
	@Test
	void constructWithNullAggregateRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlFunctionRenderer(
			DIALECT, null, new SqlNumericFunctionRenderer(DIALECT), new SqlStringFunctionRenderer(DIALECT),
			new SqlTemporalFunctionRenderer(DIALECT), new SqlWindowFunctionRenderer(DIALECT, AGGREGATE), new SqlGenericFunctionRenderer(DIALECT)
		));
	}
	
	@Test
	void constructWithNullNumericRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlFunctionRenderer(
			DIALECT, AGGREGATE, null, new SqlStringFunctionRenderer(DIALECT),
			new SqlTemporalFunctionRenderer(DIALECT), new SqlWindowFunctionRenderer(DIALECT, AGGREGATE), new SqlGenericFunctionRenderer(DIALECT)
		));
	}
	
	@Test
	void constructWithNullStringRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlFunctionRenderer(
			DIALECT, AGGREGATE, new SqlNumericFunctionRenderer(DIALECT), null,
			new SqlTemporalFunctionRenderer(DIALECT), new SqlWindowFunctionRenderer(DIALECT, AGGREGATE), new SqlGenericFunctionRenderer(DIALECT)
		));
	}
	
	@Test
	void constructWithNullTemporalRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlFunctionRenderer(
			DIALECT, AGGREGATE, new SqlNumericFunctionRenderer(DIALECT), new SqlStringFunctionRenderer(DIALECT),
			null, new SqlWindowFunctionRenderer(DIALECT, AGGREGATE), new SqlGenericFunctionRenderer(DIALECT)
		));
	}
	
	@Test
	void constructWithNullWindowRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlFunctionRenderer(
			DIALECT, AGGREGATE, new SqlNumericFunctionRenderer(DIALECT), new SqlStringFunctionRenderer(DIALECT),
			new SqlTemporalFunctionRenderer(DIALECT), null, new SqlGenericFunctionRenderer(DIALECT)
		));
	}
	
	@Test
	void constructWithNullGenericRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlFunctionRenderer(
			DIALECT, AGGREGATE, new SqlNumericFunctionRenderer(DIALECT), new SqlStringFunctionRenderer(DIALECT),
			new SqlTemporalFunctionRenderer(DIALECT), new SqlWindowFunctionRenderer(DIALECT, AGGREGATE), null
		));
	}
	
	@Test
	void renderNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderCastFunction() throws SqlException {
		SqlCastFunction<Integer> cast = new SqlCastFunction<>(new SqlValueExpression<>(5), SqlTypes.INTEGER);
		SqlRendered rendered = RENDERER.render(cast);
		assertTrue(rendered.sql().contains("CAST("));
		assertTrue(rendered.sql().contains("AS"));
		assertTrue(rendered.sql().contains(")"));
	}
	
	@Test
	void renderAggregateFunctionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlSumFunction<>(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("SUM("));
	}
	
	@Test
	void renderNumericFunctionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlAbsFunction<>(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("ABS("));
	}
	
	@Test
	void renderStringFunctionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlUpperFunction<>(new SqlValueExpression<>("name")));
		assertTrue(rendered.sql().contains("UPPER("));
	}
	
	@Test
	void renderTemporalFunctionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlCurrentDateFunction<>(SqlTypes.LOCAL_DATE));
		assertTrue(rendered.sql().contains("CURRENT_DATE"));
	}
	
	@Test
	void renderWindowFunctionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlRowNumberFunction<>(SqlWindowClause.of(), SqlTypes.LONG));
		assertTrue(rendered.sql().contains("ROW_NUMBER("));
		assertTrue(rendered.sql().contains("OVER"));
	}
	
	@Test
	void renderGenericFunctionViaDefault() throws SqlException {
		SqlCoalesceFunction<Integer> coalesce = new SqlCoalesceFunction<>(Arrays.asList(new SqlValueExpression<>(5), new SqlValueExpression<>(6)));
		SqlRendered rendered = RENDERER.render(coalesce);
		assertTrue(rendered.sql().contains("COALESCE("));
	}
	
	@Test
	void renderFunctionWithoutCast() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlAbsFunction<>(new SqlValueExpression<>(5)));
		assertFalse(rendered.sql().contains("CAST("));
	}
	
	@Test
	void renderFunctionRequiringCast() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlSumFunction<>(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("CAST("));
		assertTrue(rendered.sql().contains("SUM("));
	}
	
	@Test
	void renderAggregateCountStar() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlCountFunction(null));
		assertTrue(rendered.sql().contains("COUNT(*)"));
	}
	
	@Test
	void renderCastTakesPrecedenceOverRequiresCast() throws SqlException {
		SqlCastFunction<Integer> cast = new SqlCastFunction<>(new SqlValueExpression<>(5), SqlTypes.INTEGER);
		SqlRendered rendered = RENDERER.render(cast);
		assertEquals(1, countOccurrences(rendered.sql(), "CAST("));
	}
}
