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
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.generic.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link H2GenericFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class H2GenericFunctionRendererTest {
	
	private static final H2GenericFunctionRenderer RENDERER = new H2GenericFunctionRenderer(SqlDialects.H2);
	
	@Test
	void renderGreatestNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderGreatest(null));
	}
	
	@Test
	void renderLeastNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLeast(null));
	}
	
	@Test
	void renderCoalesceNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCoalesce(null));
	}
	
	@Test
	void renderGreatestSingleExpression() throws SqlException {
		List<SqlExpression<Integer>> expressions = List.of(new SqlValueExpression<>(5));
		String sql = RENDERER.renderGreatest(new SqlGreatestFunction<>(expressions)).sql();
		assertTrue(sql.contains("GREATEST("));
		assertTrue(sql.contains("CAST("));
		assertFalse(sql.contains(","));
	}
	
	@Test
	void renderGreatestMultipleExpressions() throws SqlException {
		List<SqlExpression<Integer>> expressions = List.of(new SqlValueExpression<>(5), new SqlValueExpression<>(7), new SqlValueExpression<>(9));
		String sql = RENDERER.renderGreatest(new SqlGreatestFunction<>(expressions)).sql();
		assertTrue(sql.contains("GREATEST("));
		assertTrue(sql.contains("CAST("));
		assertEquals(2, sql.chars().filter(c -> c == ',').count());
	}
	
	@Test
	void renderGreatestCastsOperands() throws SqlException {
		List<SqlExpression<Integer>> expressions = List.of(new SqlValueExpression<>(5));
		String sql = RENDERER.renderGreatest(new SqlGreatestFunction<>(expressions)).sql();
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains(" AS "));
	}
	
	@Test
	void renderLeastProducesLeastCall() throws SqlException {
		List<SqlExpression<Integer>> expressions = List.of(new SqlValueExpression<>(5), new SqlValueExpression<>(7));
		String sql = RENDERER.renderLeast(new SqlLeastFunction<>(expressions)).sql();
		assertTrue(sql.contains("LEAST("));
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains(","));
	}
	
	@Test
	void renderCoalesceProducesCoalesceCall() throws SqlException {
		List<SqlExpression<Integer>> expressions = List.of(new SqlValueExpression<>(5), new SqlValueExpression<>(7));
		String sql = RENDERER.renderCoalesce(new SqlCoalesceFunction<>(expressions)).sql();
		assertTrue(sql.contains("COALESCE("));
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains(","));
	}
}
