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

package net.luis.utils.io.database.condition.conditions.temporal;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlBeforeCondition}.<br>
 *
 * @author Luis-St
 */
class SqlBeforeConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlExpression<?> laterBound = SqlTestFixtures.integerExpression();
		SqlBeforeCondition condition = new SqlBeforeCondition(value, laterBound);
		assertSame(value, condition.value());
		assertSame(laterBound, condition.laterBound());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlBeforeCondition(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullLaterBound() {
		assertThrows(NullPointerException.class, () -> new SqlBeforeCondition(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlBeforeCondition condition = new SqlBeforeCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersLessThan() throws SqlException {
		SqlBeforeCondition condition = new SqlBeforeCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertTrue(condition.toSql(SqlTestFixtures.DIALECT).sql().contains("<"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlBeforeCondition first = new SqlBeforeCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlBeforeCondition second = new SqlBeforeCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlBeforeCondition different = new SqlBeforeCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
