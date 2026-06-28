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

package net.luis.utils.io.database.function.functions.string;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRightFunction}.<br>
 *
 * @author Luis-St
 */
class SqlRightFunctionTest {
	
	@Test
	void constructWithExpressionAndLength() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlExpression<Integer> length = SqlTestFixtures.integerExpression();
		SqlRightFunction<String> function = new SqlRightFunction<>(expression, length);
		assertSame(expression, function.expression());
		assertSame(length, function.length());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlRightFunction<>(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullLength() {
		assertThrows(NullPointerException.class, () -> new SqlRightFunction<>(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlRightFunction<String> function = new SqlRightFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlRightFunction<String> function = new SqlRightFunction<>(expression, SqlTestFixtures.integerExpression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlRightFunction<String> function = new SqlRightFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlRightFunction<String> first = new SqlRightFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		SqlRightFunction<String> second = new SqlRightFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlRightFunction<String> function = new SqlRightFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("RIGHT("));
	}
}
