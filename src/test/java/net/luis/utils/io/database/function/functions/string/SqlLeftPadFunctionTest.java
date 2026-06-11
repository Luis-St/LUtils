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
 * Test class for {@link SqlLeftPadFunction}.<br>
 *
 * @author Luis-St
 */
class SqlLeftPadFunctionTest {
	
	@Test
	void constructWithExpressionLengthAndFill() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlExpression<Integer> length = SqlTestFixtures.integerExpression();
		SqlExpression<String> fill = SqlTestFixtures.stringExpression();
		SqlLeftPadFunction<String> function = new SqlLeftPadFunction<>(expression, length, fill);
		assertSame(expression, function.expression());
		assertSame(length, function.length());
		assertSame(fill, function.fill());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlLeftPadFunction<>(null, SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullLength() {
		assertThrows(NullPointerException.class, () -> new SqlLeftPadFunction<>(SqlTestFixtures.stringExpression(), null, SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullFill() {
		assertThrows(NullPointerException.class, () -> new SqlLeftPadFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlLeftPadFunction<String> function = new SqlLeftPadFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlLeftPadFunction<String> function = new SqlLeftPadFunction<>(expression, SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlLeftPadFunction<String> function = new SqlLeftPadFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlLeftPadFunction<String> first = new SqlLeftPadFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		SqlLeftPadFunction<String> second = new SqlLeftPadFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlLeftPadFunction<String> function = new SqlLeftPadFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.stringExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("LPAD("));
	}
}
