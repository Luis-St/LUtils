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
 * Test class for {@link SqlStartsWithCondition}.<br>
 *
 * @author Luis-St
 */
class SqlStartsWithConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> value = SqlTestFixtures.stringExpression();
		SqlExpression<?> prefix = SqlTestFixtures.stringExpression();
		SqlStartsWithCondition condition = new SqlStartsWithCondition(value, prefix);
		assertSame(value, condition.value());
		assertSame(prefix, condition.prefix());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlStartsWithCondition(null, SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullPrefix() {
		assertThrows(NullPointerException.class, () -> new SqlStartsWithCondition(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlStartsWithCondition condition = new SqlStartsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersLikeWithTrailingWildcard() throws SqlException {
		SqlStartsWithCondition condition = new SqlStartsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("LIKE"));
		assertTrue(sql.contains("|| '%'"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlStartsWithCondition first = new SqlStartsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlStartsWithCondition second = new SqlStartsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlStartsWithCondition different = new SqlStartsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
