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
 * Test class for {@link SqlSubstringFunction}.<br>
 *
 * @author Luis-St
 */
class SqlSubstringFunctionTest {
	
	@Test
	void constructWithExpressionStartAndLength() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlExpression<Integer> start = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> length = SqlTestFixtures.integerExpression();
		SqlSubstringFunction<String> function = new SqlSubstringFunction<>(expression, start, length);
		assertSame(expression, function.expression());
		assertSame(start, function.start());
		assertSame(length, function.length());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullLength() {
		SqlSubstringFunction<String> function = new SqlSubstringFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null);
		assertNull(function.length());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlSubstringFunction<>(null, SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructWithNullStart() {
		assertThrows(NullPointerException.class, () -> new SqlSubstringFunction<>(SqlTestFixtures.stringExpression(), null, null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlSubstringFunction<String> function = new SqlSubstringFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlSubstringFunction<String> function = new SqlSubstringFunction<>(expression, SqlTestFixtures.integerExpression(), null);
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlSubstringFunction<String> function = new SqlSubstringFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlSubstringFunction<String> first = new SqlSubstringFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null);
		SqlSubstringFunction<String> second = new SqlSubstringFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlSubstringFunction<String> function = new SqlSubstringFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null);
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("SUBSTRING("));
	}
}
