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

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.function.functions.numeric.SqlAbsFunction;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlGenericFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlGenericFunctionRendererTest {
	
	private static final SqlGenericFunctionRenderer RENDERER = new SqlGenericFunctionRenderer(SqlDialects.DEFAULT);
	
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
	void constructWithDialect() {
		assertNotNull(new SqlGenericFunctionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlGenericFunctionRenderer(null));
	}
	
	@Test
	void renderNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownFunctionType() {
		SqlFunction<Object> unknown = new UnknownFunction();
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderCaseWhenNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCaseWhen(null));
	}
	
	@Test
	void renderCoalesceNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCoalesce(null));
	}
	
	@Test
	void renderGreatestNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderGreatest(null));
	}
	
	@Test
	void renderLeastNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLeast(null));
	}
	
	@Test
	void renderNullIfNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderNullIf(null));
	}
	
	@Test
	void renderUnsafeNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderUnsafe(null));
	}
	
	@Test
	void renderCaseWhenWithElse() throws SqlException {
		SqlCaseWhenFunction<Integer> function = new SqlCaseWhenFunction<>(
			List.of(new SqlCaseWhenBranch<>(SqlCondition.always(), new SqlValueExpression<>(1))),
			new SqlValueExpression<>(0)
		);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("CASE"));
		assertTrue(rendered.sql().contains("WHEN"));
		assertTrue(rendered.sql().contains("THEN"));
		assertTrue(rendered.sql().contains("ELSE"));
		assertTrue(rendered.sql().contains("END"));
	}
	
	@Test
	void renderCaseWhenWithoutElse() throws SqlException {
		SqlCaseWhenFunction<Integer> function = new SqlCaseWhenFunction<>(
			List.of(new SqlCaseWhenBranch<>(SqlCondition.always(), new SqlValueExpression<>(1))),
			null
		);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("CASE"));
		assertTrue(rendered.sql().contains("WHEN"));
		assertTrue(rendered.sql().contains("THEN"));
		assertTrue(rendered.sql().contains("END"));
		assertFalse(rendered.sql().contains("ELSE"));
	}
	
	@Test
	void renderCaseWhenMultipleBranches() throws SqlException {
		SqlCaseWhenFunction<Integer> function = new SqlCaseWhenFunction<>(
			Arrays.asList(
				new SqlCaseWhenBranch<>(SqlCondition.always(), new SqlValueExpression<>(1)),
				new SqlCaseWhenBranch<>(SqlCondition.never(), new SqlValueExpression<>(2))
			),
			null
		);
		SqlRendered rendered = RENDERER.render(function);
		assertEquals(2, countOccurrences(rendered.sql(), "WHEN"));
		assertEquals(2, countOccurrences(rendered.sql(), "THEN"));
	}
	
	@Test
	void renderCoalesceFunction() throws SqlException {
		SqlCoalesceFunction<Integer> function = new SqlCoalesceFunction<>(Arrays.asList(new SqlValueExpression<>(1), new SqlValueExpression<>(2)));
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("COALESCE("));
	}
	
	@Test
	void renderGreatestFunction() throws SqlException {
		SqlGreatestFunction<Integer> function = new SqlGreatestFunction<>(Arrays.asList(new SqlValueExpression<>(1), new SqlValueExpression<>(2)));
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("GREATEST("));
	}
	
	@Test
	void renderLeastFunction() throws SqlException {
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(Arrays.asList(new SqlValueExpression<>(1), new SqlValueExpression<>(2)));
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("LEAST("));
	}
	
	@Test
	void renderNullIfFunction() throws SqlException {
		SqlNullIfFunction<Integer> function = new SqlNullIfFunction<>(new SqlValueExpression<>(1), new SqlValueExpression<>(2));
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("NULLIF("));
		assertTrue(rendered.sql().contains(","));
	}
	
	@Test
	void renderUnsafeFunction() throws SqlException {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(new SqlValueExpression<>(1)), SqlTypes.INTEGER);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("MY_FUNC"));
		assertTrue(rendered.sql().contains("("));
	}
	
	@Test
	void renderCoalesceMultipleExpressions() throws SqlException {
		SqlCoalesceFunction<Integer> function = new SqlCoalesceFunction<>(Arrays.asList(
			new SqlValueExpression<>(1), new SqlValueExpression<>(2), new SqlValueExpression<>(3)
		));
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("COALESCE("));
		assertEquals(2, countOccurrences(rendered.sql(), ","));
	}
	
	@Test
	void renderCaseWhenNestedExpression() throws SqlException {
		SqlExpression<Integer> nested = new SqlAbsFunction<>(new SqlValueExpression<>(5));
		SqlCaseWhenFunction<Integer> function = new SqlCaseWhenFunction<>(
			List.of(new SqlCaseWhenBranch<>(SqlCondition.always(), nested)),
			new SqlValueExpression<>(0)
		);
		SqlRendered rendered = RENDERER.render(function);
		assertTrue(rendered.sql().contains("CASE"));
		assertTrue(rendered.sql().contains("ABS("));
		assertTrue(rendered.sql().contains("ELSE"));
		assertTrue(rendered.sql().contains("END"));
	}
	
	private static final class UnknownFunction implements SqlFunction<Object> {
		
		@Override
		public @NonNull SqlType<Object> type() {
			throw new UnsupportedOperationException();
		}
	}
}
