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

package net.luis.utils.io.database.function.functions.numeric.bitwise;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlBitwiseXorFunction}.<br>
 *
 * @author Luis-St
 */
class SqlBitwiseXorFunctionTest {
	
	@Test
	void constructWithExplicitType() {
		SqlExpression<Integer> first = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> second = SqlTestFixtures.integerExpression();
		SqlBitwiseXorFunction<Integer> function = new SqlBitwiseXorFunction<>(first, second, SqlTestFixtures.INTEGER_TYPE);
		assertSame(first, function.firstOperand());
		assertSame(second, function.secondOperand());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithMatchingOperandTypes() {
		SqlExpression<Integer> first = SqlTestFixtures.integerExpression();
		SqlBitwiseXorFunction<Integer> function = new SqlBitwiseXorFunction<>(first, SqlTestFixtures.integerExpression());
		assertEquals(first.type(), function.type());
	}
	
	@Test
	void constructWithNullFirstOperand() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseXorFunction<>(null, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullSecondOperand() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructConvenienceWithNullFirstOperand() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseXorFunction<>(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructConvenienceWithNullSecondOperand() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructConvenienceWithMismatchedTypes() {
		SqlExpression<Integer> first = SqlTestFixtures.integerExpression();
		@SuppressWarnings("unchecked")
		SqlExpression<Integer> mismatched = (SqlExpression<Integer>) (SqlExpression<?>) SqlTestFixtures.stringExpression();
		assertThrows(IllegalArgumentException.class, () -> new SqlBitwiseXorFunction<>(first, mismatched));
	}
	
	@Test
	void convenienceDerivesTypeFromFirstOperand() {
		SqlExpression<Integer> first = SqlTestFixtures.integerExpression();
		SqlBitwiseXorFunction<Integer> function = new SqlBitwiseXorFunction<>(first, SqlTestFixtures.integerExpression());
		assertEquals(first.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlBitwiseXorFunction<Integer> function = new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlBitwiseXorFunction<Integer> function = new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlBitwiseXorFunction<Integer> first = new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlBitwiseXorFunction<Integer> second = new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlBitwiseXorFunction<Integer> function = new SqlBitwiseXorFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("^"));
	}
}
