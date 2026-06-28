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
 * Test class for {@link SqlIsZeroCondition}.<br>
 *
 * @author Luis-St
 */
class SqlIsZeroConditionTest {
	
	@Test
	void constructWithValue() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlIsZeroCondition condition = new SqlIsZeroCondition(value);
		assertSame(value, condition.value());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlIsZeroCondition(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlIsZeroCondition condition = new SqlIsZeroCondition(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersEqualsZero() throws SqlException {
		SqlIsZeroCondition condition = new SqlIsZeroCondition(SqlTestFixtures.integerExpression());
		assertTrue(condition.toSql(SqlTestFixtures.DIALECT).sql().contains("= 0"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlIsZeroCondition first = new SqlIsZeroCondition(SqlTestFixtures.integerExpression());
		SqlIsZeroCondition second = new SqlIsZeroCondition(SqlTestFixtures.integerExpression());
		SqlIsZeroCondition different = new SqlIsZeroCondition(SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
