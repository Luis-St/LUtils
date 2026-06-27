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

import net.luis.utils.io.database.condition.conditions.numeric.SqlModEqualsCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link H2NumericConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class H2NumericConditionRendererTest {
	
	private static final H2NumericConditionRenderer RENDERER = new H2NumericConditionRenderer(SqlDialects.H2);
	
	@Test
	void renderModEqualsNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderModEquals(null));
	}
	
	@Test
	void renderModEqualsProducesModComparison() throws SqlException {
		SqlModEqualsCondition condition = new SqlModEqualsCondition(new SqlValueExpression<>(7), new SqlValueExpression<>(3), new SqlValueExpression<>(1));
		String sql = RENDERER.renderModEquals(condition).sql();
		assertTrue(sql.contains("MOD("));
		assertTrue(sql.contains("="));
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains(","));
	}
}
