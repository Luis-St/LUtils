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

package net.luis.utils.io.database.condition.conditions.string;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlContainsCondition}.<br>
 *
 * @author Luis-St
 */
class SqlContainsConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> value = SqlTestFixtures.stringExpression();
		SqlExpression<?> substring = SqlTestFixtures.stringExpression();
		SqlContainsCondition condition = new SqlContainsCondition(value, substring);
		assertSame(value, condition.value());
		assertSame(substring, condition.substring());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlContainsCondition(null, SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullSubstring() {
		assertThrows(NullPointerException.class, () -> new SqlContainsCondition(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlContainsCondition condition = new SqlContainsCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersLikeWithWildcards() throws SqlException {
		SqlContainsCondition condition = new SqlContainsCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("LIKE"));
		assertTrue(sql.contains("||"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlContainsCondition first = new SqlContainsCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlContainsCondition second = new SqlContainsCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlContainsCondition different = new SqlContainsCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
