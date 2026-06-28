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

package net.luis.utils.io.database.condition;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNegatedCondition}.<br>
 *
 * @author Luis-St
 */
class SqlNegatedConditionTest {
	
	@Test
	void constructWithCondition() {
		SqlCondition inner = SqlCondition.always();
		SqlNegatedCondition negated = new SqlNegatedCondition(inner);
		assertSame(inner, negated.condition());
	}
	
	@Test
	void constructWithNullCondition() {
		assertThrows(NullPointerException.class, () -> new SqlNegatedCondition(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlNegatedCondition(SqlCondition.always()).toSql(null));
	}
	
	@Test
	void notReturnsInnerCondition() {
		SqlCondition inner = SqlCondition.always();
		assertSame(inner, new SqlNegatedCondition(inner).not());
	}
	
	@Test
	void toSqlRendersNotGroup() throws SqlException {
		String sql = new SqlNegatedCondition(SqlCondition.always()).toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("NOT"));
		assertTrue(sql.contains("(TRUE)"));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlNegatedCondition first = new SqlNegatedCondition(SqlCondition.always());
		SqlNegatedCondition second = new SqlNegatedCondition(SqlCondition.always());
		SqlNegatedCondition different = new SqlNegatedCondition(SqlCondition.never());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
