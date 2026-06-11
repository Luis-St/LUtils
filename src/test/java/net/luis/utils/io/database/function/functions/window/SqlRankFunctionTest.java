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

package net.luis.utils.io.database.function.functions.window;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRankFunction}.<br>
 *
 * @author Luis-St
 */
class SqlRankFunctionTest {
	
	@Test
	void constructWithOverAndType() {
		SqlWindowClause over = SqlWindowClause.of();
		SqlRankFunction<Integer> function = new SqlRankFunction<>(over, SqlTestFixtures.INTEGER_TYPE);
		assertSame(over, function.over());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlRankFunction<>(null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlRankFunction<>(SqlWindowClause.of(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlRankFunction<Integer> function = new SqlRankFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlRankFunction<Integer> function = new SqlRankFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlRankFunction<Integer> first = new SqlRankFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		SqlRankFunction<Integer> second = new SqlRankFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlRankFunction<Integer> function = new SqlRankFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		String sql = function.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("RANK("));
		assertTrue(sql.contains("OVER("));
	}
}
