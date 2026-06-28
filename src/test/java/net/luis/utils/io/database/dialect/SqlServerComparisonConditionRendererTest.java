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
 * Test class for {@link SqlServerComparisonConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlServerComparisonConditionRendererTest {
	
	private static final SqlServerComparisonConditionRenderer RENDERER = new SqlServerComparisonConditionRenderer(SqlDialects.SQL_SERVER);
	
	@Test
	void renderIsDistinctFromNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderIsDistinctFrom(null));
	}
	
	@Test
	void renderIsDistinctFromUsesCaseExpression() throws SqlException {
		SqlIsDistinctFromCondition condition = new SqlIsDistinctFromCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(9));
		String sql = RENDERER.renderIsDistinctFrom(condition).sql();
		assertTrue(sql.contains("CASE"));
		assertTrue(sql.contains("WHEN"));
		assertTrue(sql.contains("IS NULL"));
		assertTrue(sql.contains("THEN"));
		assertTrue(sql.contains("ELSE"));
		assertTrue(sql.contains("END"));
		assertTrue(sql.contains("= 1"));
	}
}
