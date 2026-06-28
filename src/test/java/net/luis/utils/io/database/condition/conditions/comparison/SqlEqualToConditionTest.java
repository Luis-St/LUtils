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
 * Test class for {@link SqlEqualToCondition}.<br>
 *
 * @author Luis-St
 */
class SqlEqualToConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> first = SqlTestFixtures.integerExpression();
		SqlExpression<?> second = SqlTestFixtures.integerExpression();
		SqlEqualToCondition condition = new SqlEqualToCondition(first, second);
		assertSame(first, condition.first());
		assertSame(second, condition.second());
	}
	
	@Test
	void constructWithNullFirst() {
		assertThrows(NullPointerException.class, () -> new SqlEqualToCondition(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new SqlEqualToCondition(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlEqualToCondition condition = new SqlEqualToCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersEquals() throws SqlException {
		SqlEqualToCondition condition = new SqlEqualToCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertTrue(condition.toSql(SqlTestFixtures.DIALECT).sql().contains("="));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlEqualToCondition first = new SqlEqualToCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlEqualToCondition second = new SqlEqualToCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlEqualToCondition different = new SqlEqualToCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
