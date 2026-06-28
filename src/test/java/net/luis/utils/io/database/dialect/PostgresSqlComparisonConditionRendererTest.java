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

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.conditions.comparison.SqlInListCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PostgresSqlComparisonConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class PostgresSqlComparisonConditionRendererTest {
	
	private static final PostgresSqlComparisonConditionRenderer RENDERER = new PostgresSqlComparisonConditionRenderer(SqlDialects.POSTGRESQL);
	
	@Test
	void renderInListNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderInList(null));
	}
	
	@Test
	void renderInListUniformValuesUsesAnyArray() throws SqlException {
		List<SqlExpression<?>> options = List.of(new SqlValueExpression<>(1), new SqlValueExpression<>(2));
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options);
		assertTrue(RENDERER.renderInList(condition).sql().contains("= ANY("));
	}
	
	@Test
	void renderInListMixedTypesFallsBackToIn() throws SqlException {
		List<SqlExpression<?>> options = List.of(new SqlValueExpression<>(1), new SqlValueExpression<>("a"));
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options);
		String sql = RENDERER.renderInList(condition).sql();
		assertTrue(sql.contains("IN("));
		assertFalse(sql.contains("ANY("));
	}
	
	@Test
	void renderInListNonValueExpressionFallsBackToIn() throws SqlException {
		List<SqlExpression<?>> options = List.of(new SqlValueExpression<>(1), SqlTestFixtures.integerColumn());
		SqlInListCondition condition = new SqlInListCondition(new SqlValueExpression<>(5), options);
		String sql = RENDERER.renderInList(condition).sql();
		assertTrue(sql.contains("IN("));
		assertFalse(sql.contains("ANY("));
	}
}
