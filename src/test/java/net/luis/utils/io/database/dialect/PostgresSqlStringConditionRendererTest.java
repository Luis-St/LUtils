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

import net.luis.utils.io.database.condition.conditions.string.SqlEqualsIgnoreCaseCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PostgresSqlStringConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class PostgresSqlStringConditionRendererTest {
	
	private static final PostgresSqlStringConditionRenderer RENDERER = new PostgresSqlStringConditionRenderer(SqlDialects.POSTGRESQL);
	
	@Test
	void renderEqualsIgnoreCaseNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderEqualsIgnoreCase(null));
	}
	
	@Test
	void renderEqualsIgnoreCaseUsesIlike() throws SqlException {
		SqlEqualsIgnoreCaseCondition condition = new SqlEqualsIgnoreCaseCondition(new SqlValueExpression<>("a"), new SqlValueExpression<>("b"));
		assertTrue(RENDERER.renderEqualsIgnoreCase(condition).sql().contains("ILIKE"));
	}
}
