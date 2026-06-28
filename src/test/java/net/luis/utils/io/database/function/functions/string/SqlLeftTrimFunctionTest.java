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
 * Test class for {@link SqlLeftTrimFunction}.<br>
 *
 * @author Luis-St
 */
class SqlLeftTrimFunctionTest {
	
	@Test
	void constructWithExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlLeftTrimFunction<String> function = new SqlLeftTrimFunction<>(expression);
		assertSame(expression, function.expression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlLeftTrimFunction<>(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlLeftTrimFunction<String> function = new SqlLeftTrimFunction<>(SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlLeftTrimFunction<String> function = new SqlLeftTrimFunction<>(expression);
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlLeftTrimFunction<String> function = new SqlLeftTrimFunction<>(SqlTestFixtures.stringExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlLeftTrimFunction<String> first = new SqlLeftTrimFunction<>(SqlTestFixtures.stringExpression());
		SqlLeftTrimFunction<String> second = new SqlLeftTrimFunction<>(SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlLeftTrimFunction<String> function = new SqlLeftTrimFunction<>(SqlTestFixtures.stringExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("LTRIM("));
	}
}
