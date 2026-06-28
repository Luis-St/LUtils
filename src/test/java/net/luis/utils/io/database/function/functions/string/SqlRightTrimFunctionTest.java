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
 * Test class for {@link SqlRightTrimFunction}.<br>
 *
 * @author Luis-St
 */
class SqlRightTrimFunctionTest {
	
	@Test
	void constructWithExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlRightTrimFunction<String> function = new SqlRightTrimFunction<>(expression);
		assertSame(expression, function.expression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlRightTrimFunction<>(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlRightTrimFunction<String> function = new SqlRightTrimFunction<>(SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlRightTrimFunction<String> function = new SqlRightTrimFunction<>(expression);
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlRightTrimFunction<String> function = new SqlRightTrimFunction<>(SqlTestFixtures.stringExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlRightTrimFunction<String> first = new SqlRightTrimFunction<>(SqlTestFixtures.stringExpression());
		SqlRightTrimFunction<String> second = new SqlRightTrimFunction<>(SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlRightTrimFunction<String> function = new SqlRightTrimFunction<>(SqlTestFixtures.stringExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("RTRIM("));
	}
}
