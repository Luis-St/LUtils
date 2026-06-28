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
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlServerNumericFunctionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlServerNumericFunctionRendererTest {
	
	private static final SqlServerNumericFunctionRenderer RENDERER = new SqlServerNumericFunctionRenderer(SqlDialects.SQL_SERVER);
	
	private static SqlValueExpression<Integer> value() throws SqlException {
		return new SqlValueExpression<>(5);
	}
	
	@Test
	void renderLogNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLog(null));
	}
	
	@Test
	void renderModNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderMod(null));
	}
	
	@Test
	void renderTruncateNullFunction() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTruncate(null));
	}
	
	@Test
	void renderLogWithBase() throws SqlException {
		SqlRendered rendered = RENDERER.renderLog(new SqlLogFunction(value(), value()));
		assertTrue(rendered.sql().contains("LOG("));
		assertEquals(2, rendered.parameters().size());
	}
	
	@Test
	void renderLogWithoutBase() throws SqlException {
		SqlRendered rendered = RENDERER.renderLog(new SqlLogFunction(value(), null));
		assertTrue(rendered.sql().contains("LOG("));
		assertEquals(1, rendered.parameters().size());
	}
	
	@Test
	void renderRandomProducesRandCall() throws SqlException {
		assertEquals("RAND()", RENDERER.renderRandom(new SqlRandomFunction()).sql());
	}
	
	@Test
	void renderModProducesPercentInfix() throws SqlException {
		assertTrue(RENDERER.renderMod(new SqlModFunction<>(value(), value())).sql().contains("%"));
	}
	
	@Test
	void renderTruncateProducesRoundWithTruncateFlag() throws SqlException {
		SqlRendered rendered = RENDERER.renderTruncate(new SqlNumericTruncateFunction<>(value()));
		assertTrue(rendered.sql().contains("ROUND("));
		assertEquals(3, rendered.parameters().size());
	}
}
