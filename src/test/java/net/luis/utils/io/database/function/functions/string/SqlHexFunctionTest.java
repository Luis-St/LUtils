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
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlHexFunction}.<br>
 *
 * @author Luis-St
 */
class SqlHexFunctionTest {
	
	@Test
	void constructWithExpressionAndType() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlHexFunction function = new SqlHexFunction(expression, SqlTestFixtures.STRING_TYPE);
		assertSame(expression, function.expression());
		assertEquals(SqlTestFixtures.STRING_TYPE, function.type());
	}
	
	@Test
	void constructWithDefaultType() {
		SqlHexFunction function = new SqlHexFunction(SqlTestFixtures.integerExpression());
		assertEquals(SqlTypes.TEXT, function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlHexFunction(null, SqlTestFixtures.STRING_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlHexFunction(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructConvenienceWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlHexFunction(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlHexFunction function = new SqlHexFunction(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlHexFunction function = new SqlHexFunction(SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlHexFunction first = new SqlHexFunction(SqlTestFixtures.integerExpression(), SqlTestFixtures.STRING_TYPE);
		SqlHexFunction second = new SqlHexFunction(SqlTestFixtures.integerExpression(), SqlTestFixtures.STRING_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlHexFunction function = new SqlHexFunction(SqlTestFixtures.integerExpression(), SqlTestFixtures.STRING_TYPE);
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("HEX("));
	}
}
