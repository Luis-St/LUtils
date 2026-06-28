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

package net.luis.utils.io.database.dialect.renderer.expression.condition;

import net.luis.utils.io.database.condition.conditions.SqlNumericCondition;
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNumericConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlNumericConditionRendererTest {
	
	private static final SqlNumericConditionRenderer RENDERER = new SqlNumericConditionRenderer(SqlDialects.DEFAULT);
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlNumericConditionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlNumericConditionRenderer(null));
	}
	
	@Test
	void renderNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownNumericType() {
		SqlNumericCondition unknown = new SqlNumericCondition() {};
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderIsNegativeNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderIsNegative(null));
	}
	
	@Test
	void renderIsPositiveNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderIsPositive(null));
	}
	
	@Test
	void renderIsZeroNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderIsZero(null));
	}
	
	@Test
	void renderModEqualsNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderModEquals(null));
	}
	
	@Test
	void renderIsNegativeCondition() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlIsNegativeCondition(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("< 0"));
	}
	
	@Test
	void renderIsPositiveCondition() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlIsPositiveCondition(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("> 0"));
	}
	
	@Test
	void renderIsZeroCondition() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlIsZeroCondition(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("= 0"));
	}
	
	@Test
	void renderModEqualsCondition() throws SqlException {
		SqlModEqualsCondition condition = new SqlModEqualsCondition(new SqlValueExpression<>(10), new SqlValueExpression<>(3), new SqlValueExpression<>(1));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("MOD("));
		assertTrue(rendered.sql().contains(","));
		assertTrue(rendered.sql().contains("="));
	}
	
	@Test
	void renderModEqualsWithColumnValue() throws SqlException {
		SqlModEqualsCondition condition = new SqlModEqualsCondition(new SqlValueExpression<>(100), new SqlValueExpression<>(7), new SqlValueExpression<>(2));
		SqlRendered rendered = RENDERER.render(condition);
		assertNotNull(rendered);
		assertTrue(rendered.sql().startsWith("MOD("));
		assertEquals(3, rendered.parameters().size());
	}
}
