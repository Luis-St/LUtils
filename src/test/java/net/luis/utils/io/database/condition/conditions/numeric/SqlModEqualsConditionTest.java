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

package net.luis.utils.io.database.condition.conditions.numeric;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlModEqualsCondition}.<br>
 *
 * @author Luis-St
 */
class SqlModEqualsConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlExpression<?> divisor = SqlTestFixtures.integerExpression();
		SqlExpression<?> remainder = SqlTestFixtures.integerExpression();
		SqlModEqualsCondition condition = new SqlModEqualsCondition(value, divisor, remainder);
		assertSame(value, condition.value());
		assertSame(divisor, condition.divisor());
		assertSame(remainder, condition.remainder());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlModEqualsCondition(null, SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullDivisor() {
		assertThrows(NullPointerException.class, () -> new SqlModEqualsCondition(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullRemainder() {
		assertThrows(NullPointerException.class, () -> new SqlModEqualsCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlModEqualsCondition condition = new SqlModEqualsCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersMod() throws SqlException {
		SqlModEqualsCondition condition = new SqlModEqualsCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("MOD("));
		assertTrue(sql.contains(","));
		assertTrue(sql.contains("="));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlModEqualsCondition first = new SqlModEqualsCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlModEqualsCondition second = new SqlModEqualsCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlModEqualsCondition different = new SqlModEqualsCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
