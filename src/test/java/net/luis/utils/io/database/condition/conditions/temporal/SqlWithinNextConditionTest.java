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
 * Test class for {@link SqlWithinNextCondition}.<br>
 *
 * @author Luis-St
 */
class SqlWithinNextConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlExpression<?> duration = SqlTestFixtures.integerExpression();
		SqlWithinNextCondition condition = new SqlWithinNextCondition(value, duration);
		assertSame(value, condition.value());
		assertSame(duration, condition.duration());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlWithinNextCondition(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullDuration() {
		assertThrows(NullPointerException.class, () -> new SqlWithinNextCondition(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlWithinNextCondition condition = new SqlWithinNextCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersWithinNextInterval() throws SqlException {
		SqlWithinNextCondition condition = new SqlWithinNextCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("<="));
		assertTrue(sql.contains("CURRENT_TIMESTAMP"));
		assertTrue(sql.contains("+"));
		assertTrue(sql.contains("INTERVAL"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlWithinNextCondition first = new SqlWithinNextCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlWithinNextCondition second = new SqlWithinNextCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlWithinNextCondition different = new SqlWithinNextCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
