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
 * Test class for {@link SqlCumulativeDistributionFunction}.<br>
 *
 * @author Luis-St
 */
class SqlCumulativeDistributionFunctionTest {
	
	@Test
	void constructWithOverAndType() {
		SqlWindowClause over = SqlWindowClause.of();
		SqlCumulativeDistributionFunction<Integer> function = new SqlCumulativeDistributionFunction<>(over, SqlTestFixtures.INTEGER_TYPE);
		assertSame(over, function.over());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlCumulativeDistributionFunction<>(null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlCumulativeDistributionFunction<>(SqlWindowClause.of(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlCumulativeDistributionFunction<Integer> function = new SqlCumulativeDistributionFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlCumulativeDistributionFunction<Integer> function = new SqlCumulativeDistributionFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlCumulativeDistributionFunction<Integer> first = new SqlCumulativeDistributionFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		SqlCumulativeDistributionFunction<Integer> second = new SqlCumulativeDistributionFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlCumulativeDistributionFunction<Integer> function = new SqlCumulativeDistributionFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		String sql = function.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("CUME_DIST("));
		assertTrue(sql.contains("OVER("));
	}
}
