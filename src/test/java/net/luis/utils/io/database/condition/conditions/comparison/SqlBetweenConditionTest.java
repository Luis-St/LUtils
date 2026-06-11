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

package net.luis.utils.io.database.condition.conditions.comparison;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlBetweenCondition}.<br>
 *
 * @author Luis-St
 */
class SqlBetweenConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlExpression<?> lower = SqlTestFixtures.integerExpression();
		SqlExpression<?> upper = SqlTestFixtures.integerExpression();
		SqlBetweenCondition condition = new SqlBetweenCondition(value, lower, upper);
		assertSame(value, condition.value());
		assertSame(lower, condition.lower());
		assertSame(upper, condition.upper());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlBetweenCondition(null, SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullLower() {
		assertThrows(NullPointerException.class, () -> new SqlBetweenCondition(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullUpper() {
		assertThrows(NullPointerException.class, () -> new SqlBetweenCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlBetweenCondition condition = new SqlBetweenCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersBetween() throws SqlException {
		SqlBetweenCondition condition = new SqlBetweenCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("BETWEEN"));
		assertTrue(sql.contains("AND"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlBetweenCondition first = new SqlBetweenCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlBetweenCondition second = new SqlBetweenCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlBetweenCondition different = new SqlBetweenCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
