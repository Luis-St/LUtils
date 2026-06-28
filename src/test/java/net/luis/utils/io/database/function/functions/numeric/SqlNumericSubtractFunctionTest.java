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
 * Test class for {@link SqlNumericSubtractFunction}.<br>
 *
 * @author Luis-St
 */
class SqlNumericSubtractFunctionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> subtrahend = SqlTestFixtures.integerExpression();
		SqlNumericSubtractFunction<Integer> function = new SqlNumericSubtractFunction<>(expression, subtrahend);
		assertSame(expression, function.expression());
		assertSame(subtrahend, function.subtrahend());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlNumericSubtractFunction<>(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullSubtrahend() {
		assertThrows(NullPointerException.class, () -> new SqlNumericSubtractFunction<>(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlNumericSubtractFunction<Integer> function = new SqlNumericSubtractFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlNumericSubtractFunction<Integer> function = new SqlNumericSubtractFunction<>(expression, SqlTestFixtures.integerExpression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlNumericSubtractFunction<Integer> function = new SqlNumericSubtractFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlNumericSubtractFunction<Integer> first = new SqlNumericSubtractFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlNumericSubtractFunction<Integer> second = new SqlNumericSubtractFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlNumericSubtractFunction<Integer> function = new SqlNumericSubtractFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("-"));
	}
}
