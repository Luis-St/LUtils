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
 * Test class for {@link SqlBitwiseNotFunction}.<br>
 *
 * @author Luis-St
 */
class SqlBitwiseNotFunctionTest {
	
	@Test
	void constructWithExplicitType() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlBitwiseNotFunction<Integer> function = new SqlBitwiseNotFunction<>(expression, SqlTestFixtures.INTEGER_TYPE);
		assertSame(expression, function.expression());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithDerivedType() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlBitwiseNotFunction<Integer> function = new SqlBitwiseNotFunction<>(expression);
		assertEquals(expression.type(), function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseNotFunction<>(null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseNotFunction<>(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructConvenienceWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlBitwiseNotFunction<>(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlBitwiseNotFunction<Integer> function = new SqlBitwiseNotFunction<>(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlBitwiseNotFunction<Integer> function = new SqlBitwiseNotFunction<>(SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlBitwiseNotFunction<Integer> first = new SqlBitwiseNotFunction<>(SqlTestFixtures.integerExpression());
		SqlBitwiseNotFunction<Integer> second = new SqlBitwiseNotFunction<>(SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlBitwiseNotFunction<Integer> function = new SqlBitwiseNotFunction<>(SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("~("));
	}
}
