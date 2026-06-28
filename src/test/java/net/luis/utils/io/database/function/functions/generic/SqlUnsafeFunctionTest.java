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
 * Test class for {@link SqlUnsafeFunction}.<br>
 *
 * @author Luis-St
 */
class SqlUnsafeFunctionTest {
	
	@Test
	void constructWithExpressionArgumentsAndType() {
		SqlExpression<Integer> argument = SqlTestFixtures.integerExpression();
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(argument), SqlTestFixtures.INTEGER_TYPE);
		assertEquals("MY_FUNC", function.expression());
		assertEquals(1, function.arguments().size());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithEmptyArguments() {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(), SqlTestFixtures.INTEGER_TYPE);
		assertTrue(function.arguments().isEmpty());
	}
	
	@Test
	void constructWithNullExpressionString() {
		assertThrows(NullPointerException.class, () -> new SqlUnsafeFunction<>(null, List.of(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullArguments() {
		assertThrows(NullPointerException.class, () -> new SqlUnsafeFunction<>("MY_FUNC", null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlUnsafeFunction<>("MY_FUNC", List.of(), null));
	}
	
	@Test
	void constructWithBlankExpression() {
		assertThrows(IllegalArgumentException.class, () -> new SqlUnsafeFunction<>("   ", List.of(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullArgumentInList() {
		List<SqlExpression<?>> arguments = Arrays.asList(SqlTestFixtures.integerExpression(), null);
		assertThrows(IllegalArgumentException.class, () -> new SqlUnsafeFunction<>("MY_FUNC", arguments, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNonBlankExpression() {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(), SqlTestFixtures.INTEGER_TYPE);
		assertEquals("MY_FUNC", function.expression());
	}
	
	@Test
	void nullArgumentCheckSkippedForEmptyList() {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(), SqlTestFixtures.INTEGER_TYPE);
		assertTrue(function.arguments().isEmpty());
	}
	
	@Test
	void argumentsListIsUnmodifiable() {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(SqlTestFixtures.integerExpression()), SqlTestFixtures.INTEGER_TYPE);
		assertThrows(UnsupportedOperationException.class, () -> function.arguments().add(SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(), SqlTestFixtures.INTEGER_TYPE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(), SqlTestFixtures.INTEGER_TYPE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void argumentsCopyIsolatedFromSource() {
		List<SqlExpression<?>> source = new ArrayList<>(List.of(SqlTestFixtures.integerExpression()));
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", source, SqlTestFixtures.INTEGER_TYPE);
		source.add(SqlTestFixtures.integerExpression());
		assertEquals(1, function.arguments().size());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlUnsafeFunction<Integer> function = new SqlUnsafeFunction<>("MY_FUNC", List.of(SqlTestFixtures.integerExpression()), SqlTestFixtures.INTEGER_TYPE);
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("MY_FUNC"));
	}
}
