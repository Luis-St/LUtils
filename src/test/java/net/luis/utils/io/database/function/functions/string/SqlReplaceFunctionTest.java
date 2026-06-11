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
 * Test class for {@link SqlReplaceFunction}.<br>
 *
 * @author Luis-St
 */
class SqlReplaceFunctionTest {
	
	@Test
	void constructWithExpressionSearchAndReplacement() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlExpression<String> search = SqlTestFixtures.stringExpression();
		SqlExpression<String> replacement = SqlTestFixtures.stringExpression();
		SqlReplaceFunction<String> function = new SqlReplaceFunction<>(expression, search, replacement);
		assertSame(expression, function.expression());
		assertSame(search, function.search());
		assertSame(replacement, function.replacement());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlReplaceFunction<>(null, SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullSearch() {
		assertThrows(NullPointerException.class, () -> new SqlReplaceFunction<>(SqlTestFixtures.stringExpression(), null, SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void constructWithNullReplacement() {
		assertThrows(NullPointerException.class, () -> new SqlReplaceFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlReplaceFunction<String> function = new SqlReplaceFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlReplaceFunction<String> function = new SqlReplaceFunction<>(expression, SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlReplaceFunction<String> function = new SqlReplaceFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlReplaceFunction<String> first = new SqlReplaceFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		SqlReplaceFunction<String> second = new SqlReplaceFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlReplaceFunction<String> function = new SqlReplaceFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("REPLACE("));
	}
}
