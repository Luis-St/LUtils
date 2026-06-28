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
 * Test class for {@link SqlIsNullCondition}.<br>
 *
 * @author Luis-St
 */
class SqlIsNullConditionTest {
	
	@Test
	void constructWithValue() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlIsNullCondition condition = new SqlIsNullCondition(value);
		assertSame(value, condition.value());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlIsNullCondition(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlIsNullCondition condition = new SqlIsNullCondition(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersIsNull() throws SqlException {
		SqlIsNullCondition condition = new SqlIsNullCondition(SqlTestFixtures.integerExpression());
		assertTrue(condition.toSql(SqlTestFixtures.DIALECT).sql().contains("IS NULL"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlIsNullCondition first = new SqlIsNullCondition(SqlTestFixtures.integerExpression());
		SqlIsNullCondition second = new SqlIsNullCondition(SqlTestFixtures.integerExpression());
		SqlIsNullCondition different = new SqlIsNullCondition(SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
