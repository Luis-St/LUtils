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

import net.luis.utils.io.database.condition.conditions.comparison.SqlIsDistinctFromCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqliteComparisonConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqliteComparisonConditionRendererTest {
	
	private static final SqliteComparisonConditionRenderer RENDERER = new SqliteComparisonConditionRenderer(SqlDialects.SQLITE);
	
	@Test
	void renderIsDistinctFromNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderIsDistinctFrom(null));
	}
	
	@Test
	void renderIsDistinctFromUsesIsNot() throws SqlException {
		SqlIsDistinctFromCondition condition = new SqlIsDistinctFromCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(9));
		assertTrue(RENDERER.renderIsDistinctFrom(condition).sql().contains("IS NOT"));
	}
}
