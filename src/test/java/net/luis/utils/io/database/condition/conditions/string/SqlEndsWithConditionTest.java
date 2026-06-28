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
 * Test class for {@link SqlEndsWithCondition}.<br>
 *
 * @author Luis-St
 */
class SqlEndsWithConditionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<?> value = SqlTestFixtures.stringExpression();
		SqlExpression<?> suffix = SqlTestFixtures.stringExpression();
		SqlEndsWithCondition condition = new SqlEndsWithCondition(value, suffix);
		assertSame(value, condition.value());
		assertSame(suffix, condition.suffix());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlEndsWithCondition(null, SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullSuffix() {
		assertThrows(NullPointerException.class, () -> new SqlEndsWithCondition(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlEndsWithCondition condition = new SqlEndsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlRendersLikeWithLeadingWildcard() throws SqlException {
		SqlEndsWithCondition condition = new SqlEndsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertTrue(condition.toSql(SqlTestFixtures.DIALECT).sql().contains("LIKE '%' ||"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlEndsWithCondition first = new SqlEndsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlEndsWithCondition second = new SqlEndsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlEndsWithCondition different = new SqlEndsWithCondition(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
