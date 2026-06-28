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
 * Test class for {@link SqlLeastFunction}.<br>
 *
 * @author Luis-St
 */
class SqlLeastFunctionTest {
	
	@Test
	void constructWithSingleExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(List.of(expression));
		assertEquals(1, function.expressions().size());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new SqlLeastFunction<Integer>(null));
	}
	
	@Test
	void constructWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new SqlLeastFunction<>(List.of()));
	}
	
	@Test
	void constructWithNullExpressionInList() {
		List<SqlExpression<Integer>> expressions = Arrays.asList(SqlTestFixtures.integerExpression(), null);
		assertThrows(IllegalArgumentException.class, () -> new SqlLeastFunction<>(expressions));
	}
	
	@Test
	void constructWithMismatchedTypes() {
		@SuppressWarnings("unchecked")
		SqlExpression<Integer> mismatched = (SqlExpression<Integer>) (SqlExpression<?>) SqlTestFixtures.stringExpression();
		List<SqlExpression<Integer>> expressions = List.of(SqlTestFixtures.integerExpression(), mismatched);
		assertThrows(IllegalArgumentException.class, () -> new SqlLeastFunction<>(expressions));
	}
	
	@Test
	void constructWithMatchingMultipleExpressions() {
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(List.of(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression()));
		assertEquals(3, function.expressions().size());
	}
	
	@Test
	void typeReturnsFirstExpressionType() {
		SqlExpression<Integer> first = SqlTestFixtures.integerExpression();
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(List.of(first, SqlTestFixtures.integerExpression()));
		assertEquals(first.type(), function.type());
	}
	
	@Test
	void expressionsListIsUnmodifiable() {
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(List.of(SqlTestFixtures.integerExpression()));
		assertThrows(UnsupportedOperationException.class, () -> function.expressions().add(SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(List.of(SqlTestFixtures.integerExpression()));
		assertFalse(function.requiresCast());
	}
	
	@Test
	void expressionsCopyIsolatedFromSource() {
		List<SqlExpression<Integer>> source = new ArrayList<>(List.of(SqlTestFixtures.integerExpression()));
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(source);
		source.add(SqlTestFixtures.integerExpression());
		assertEquals(1, function.expressions().size());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(List.of(SqlTestFixtures.integerExpression()));
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlLeastFunction<Integer> function = new SqlLeastFunction<>(List.of(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression()));
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("LEAST("));
	}
}
