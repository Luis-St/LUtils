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

package net.luis.utils.io.database.function.functions.numeric;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlPowFunction}.<br>
 *
 * @author Luis-St
 */
class SqlPowFunctionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> exponent = SqlTestFixtures.integerExpression();
		SqlPowFunction<Integer> function = new SqlPowFunction<>(expression, exponent);
		assertSame(expression, function.expression());
		assertSame(exponent, function.exponent());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlPowFunction<>(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullExponent() {
		assertThrows(NullPointerException.class, () -> new SqlPowFunction<>(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlPowFunction<Integer> function = new SqlPowFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlPowFunction<Integer> function = new SqlPowFunction<>(expression, SqlTestFixtures.integerExpression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlPowFunction<Integer> function = new SqlPowFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlPowFunction<Integer> first = new SqlPowFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlPowFunction<Integer> second = new SqlPowFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlPowFunction<Integer> function = new SqlPowFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("POWER("));
	}
}
