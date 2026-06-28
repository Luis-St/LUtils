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
 * Test class for {@link SqlUnhexFunction}.<br>
 *
 * @author Luis-St
 */
class SqlUnhexFunctionTest {
	
	@Test
	void constructWithExpressionAndType() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlUnhexFunction<Integer> function = new SqlUnhexFunction<>(expression, SqlTestFixtures.INTEGER_TYPE);
		assertSame(expression, function.expression());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlUnhexFunction<>(null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlUnhexFunction<Integer> function = new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeReturnsExplicitType() {
		SqlUnhexFunction<Integer> function = new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlUnhexFunction<Integer> function = new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlUnhexFunction<Integer> first = new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.INTEGER_TYPE);
		SqlUnhexFunction<Integer> second = new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlUnhexFunction<Integer> function = new SqlUnhexFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("UNHEX("));
	}
}
