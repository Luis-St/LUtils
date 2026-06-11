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

package net.luis.utils.io.database.function.functions.aggregate;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAverageFunction}.<br>
 *
 * @author Luis-St
 */
class SqlAverageFunctionTest {
	
	@Test
	void constructWithExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlAverageFunction<Integer> function = new SqlAverageFunction<>(expression);
		assertSame(expression, function.expression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlAverageFunction<>(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlAverageFunction<Integer> function = new SqlAverageFunction<>(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlAverageFunction<Integer> function = new SqlAverageFunction<>(expression);
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastReturnsTrue() {
		SqlAverageFunction<Integer> function = new SqlAverageFunction<>(SqlTestFixtures.integerExpression());
		assertTrue(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlAverageFunction<Integer> first = new SqlAverageFunction<>(SqlTestFixtures.integerExpression());
		SqlAverageFunction<Integer> second = new SqlAverageFunction<>(SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlAverageFunction<Integer> function = new SqlAverageFunction<>(SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("AVG("));
	}
}
