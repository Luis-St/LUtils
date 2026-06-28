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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCoalesceFunction}.<br>
 *
 * @author Luis-St
 */
class SqlCoalesceFunctionTest {
	
	@Test
	void constructWithSingleExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(List.of(expression));
		assertEquals(1, function.expressions().size());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new SqlCoalesceFunction<String>(null));
	}
	
	@Test
	void constructWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new SqlCoalesceFunction<>(List.of()));
	}
	
	@Test
	void constructWithNullExpressionInList() {
		List<SqlExpression<String>> expressions = Arrays.asList(SqlTestFixtures.stringExpression(), null);
		assertThrows(IllegalArgumentException.class, () -> new SqlCoalesceFunction<>(expressions));
	}
	
	@Test
	void constructWithMismatchedTypes() {
		@SuppressWarnings("unchecked")
		SqlExpression<String> mismatched = (SqlExpression<String>) (SqlExpression<?>) SqlTestFixtures.integerExpression();
		List<SqlExpression<String>> expressions = List.of(SqlTestFixtures.stringExpression(), mismatched);
		assertThrows(IllegalArgumentException.class, () -> new SqlCoalesceFunction<>(expressions));
	}
	
	@Test
	void constructWithMatchingMultipleExpressions() {
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression()));
		assertEquals(3, function.expressions().size());
	}
	
	@Test
	void typeReturnsFirstExpressionType() {
		SqlExpression<String> first = SqlTestFixtures.stringExpression();
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(List.of(first, SqlTestFixtures.stringExpression()));
		assertEquals(first.type(), function.type());
	}
	
	@Test
	void expressionsListIsUnmodifiable() {
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(List.of(SqlTestFixtures.stringExpression()));
		assertThrows(UnsupportedOperationException.class, () -> function.expressions().add(SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(List.of(SqlTestFixtures.stringExpression()));
		assertFalse(function.requiresCast());
	}
	
	@Test
	void expressionsCopyIsolatedFromSource() {
		List<SqlExpression<String>> source = new ArrayList<>(List.of(SqlTestFixtures.stringExpression()));
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(source);
		source.add(SqlTestFixtures.stringExpression());
		assertEquals(1, function.expressions().size());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(List.of(SqlTestFixtures.stringExpression()));
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlCoalesceFunction<String> function = new SqlCoalesceFunction<>(List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression()));
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("COALESCE("));
	}
}
