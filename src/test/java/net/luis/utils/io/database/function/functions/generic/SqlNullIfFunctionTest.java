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

package net.luis.utils.io.database.function.functions.generic;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNullIfFunction}.<br>
 *
 * @author Luis-St
 */
class SqlNullIfFunctionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlExpression<String> compareValue = SqlTestFixtures.stringExpression();
		SqlNullIfFunction<String> function = new SqlNullIfFunction<>(expression, compareValue);
		assertSame(expression, function.expression());
		assertSame(compareValue, function.compareValue());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlNullIfFunction<>(null, SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullCompareValue() {
		assertThrows(NullPointerException.class, () -> new SqlNullIfFunction<>(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void typeReturnsExpressionType() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlNullIfFunction<String> function = new SqlNullIfFunction<>(expression, SqlTestFixtures.stringExpression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlNullIfFunction<String> function = new SqlNullIfFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlNullIfFunction<String> function = new SqlNullIfFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlNullIfFunction<String> first = new SqlNullIfFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlNullIfFunction<String> second = new SqlNullIfFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlNullIfFunction<String> function = new SqlNullIfFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("NULLIF("));
	}
}
