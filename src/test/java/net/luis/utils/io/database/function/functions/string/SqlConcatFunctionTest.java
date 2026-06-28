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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlConcatFunction}.<br>
 *
 * @author Luis-St
 */
class SqlConcatFunctionTest {
	
	@Test
	void constructWithSingleExpressionNoSeparator() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(expression), Optional.empty(), false, false);
		assertEquals(1, function.expressions().size());
		assertTrue(function.separator().isEmpty());
		assertFalse(function.distinct());
		assertFalse(function.ordered());
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithSeparatorAndFlags() {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression()), Optional.of(", "), true, true);
		assertEquals(", ", function.separator().orElseThrow());
		assertTrue(function.distinct());
		assertTrue(function.ordered());
	}
	
	@Test
	void constructWithNullExpressions() {
		assertThrows(NullPointerException.class, () -> new SqlConcatFunction<String>(null, Optional.empty(), false, false));
	}
	
	@Test
	void constructWithNullSeparator() {
		assertThrows(NullPointerException.class, () -> new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression()), null, false, false));
	}
	
	@Test
	void constructWithEmptyExpressions() {
		assertThrows(IllegalArgumentException.class, () -> new SqlConcatFunction<>(List.of(), Optional.empty(), false, false));
	}
	
	@Test
	void constructWithNullExpressionInList() {
		List<SqlExpression<String>> expressions = Arrays.asList(SqlTestFixtures.stringExpression(), null);
		assertThrows(IllegalArgumentException.class, () -> new SqlConcatFunction<>(expressions, Optional.empty(), false, false));
	}
	
	@Test
	void constructWithEmptySeparator() {
		assertThrows(IllegalArgumentException.class, () -> new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression()), Optional.of(""), false, false));
	}
	
	@Test
	void constructWithIncompatibleTypes() {
		@SuppressWarnings("unchecked")
		SqlExpression<String> incompatible = (SqlExpression<String>) (SqlExpression<?>) SqlTestFixtures.integerExpression();
		List<SqlExpression<String>> expressions = List.of(SqlTestFixtures.stringExpression(), incompatible);
		assertThrows(IllegalArgumentException.class, () -> new SqlConcatFunction<>(expressions, Optional.empty(), false, false));
	}
	
	@Test
	void separatorEmptyCheckSkippedWhenAbsent() {
		assertDoesNotThrow(() -> new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression()), Optional.empty(), false, false));
	}
	
	@Test
	void constructWithCompatibleMultipleExpressions() {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression()), Optional.empty(), false, false);
		assertEquals(3, function.expressions().size());
	}
	
	@Test
	void typeReturnsFirstExpressionType() {
		SqlExpression<String> first = SqlTestFixtures.stringExpression();
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(first, SqlTestFixtures.stringExpression()), Optional.empty(), false, false);
		assertEquals(first.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression()), Optional.empty(), false, false);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void expressionsListIsUnmodifiable() {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression()), Optional.empty(), false, false);
		assertThrows(UnsupportedOperationException.class, () -> function.expressions().add(SqlTestFixtures.stringExpression()));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression()), Optional.empty(), false, false);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void expressionsCopyIsolatedFromSource() {
		List<SqlExpression<String>> source = new ArrayList<>(List.of(SqlTestFixtures.stringExpression()));
		SqlConcatFunction<String> function = new SqlConcatFunction<>(source, Optional.empty(), false, false);
		source.add(SqlTestFixtures.stringExpression());
		assertEquals(1, function.expressions().size());
	}
	
	@Test
	void distinctAndOrderedFlagCombinations() {
		List<SqlExpression<String>> expressions = List.of(SqlTestFixtures.stringExpression());
		assertFalse(new SqlConcatFunction<>(expressions, Optional.empty(), false, false).distinct());
		assertTrue(new SqlConcatFunction<>(expressions, Optional.empty(), true, false).distinct());
		assertTrue(new SqlConcatFunction<>(expressions, Optional.empty(), false, true).ordered());
		SqlConcatFunction<String> both = new SqlConcatFunction<>(expressions, Optional.empty(), true, true);
		assertTrue(both.distinct());
		assertTrue(both.ordered());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlConcatFunction<String> function = new SqlConcatFunction<>(List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression()), Optional.empty(), false, false);
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("||"));
	}
}
