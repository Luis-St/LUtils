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

package net.luis.utils.io.database.function.functions.numeric;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlLogFunction}.<br>
 *
 * @author Luis-St
 */
class SqlLogFunctionTest {
	
	@Test
	void constructWithExpressionAndBase() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> base = SqlTestFixtures.integerExpression();
		SqlLogFunction function = new SqlLogFunction(expression, base);
		assertSame(expression, function.expression());
		assertSame(base, function.base());
		assertEquals(SqlTypes.DOUBLE, function.type());
	}
	
	@Test
	void constructWithNullBase() {
		SqlLogFunction function = new SqlLogFunction(SqlTestFixtures.integerExpression(), null);
		assertNull(function.base());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlLogFunction(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlLogFunction function = new SqlLogFunction(SqlTestFixtures.integerExpression(), null);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeAlwaysDouble() {
		SqlLogFunction function = new SqlLogFunction(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertEquals(SqlTypes.DOUBLE, function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlLogFunction function = new SqlLogFunction(SqlTestFixtures.integerExpression(), null);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlLogFunction first = new SqlLogFunction(SqlTestFixtures.integerExpression(), null);
		SqlLogFunction second = new SqlLogFunction(SqlTestFixtures.integerExpression(), null);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlLogFunction function = new SqlLogFunction(SqlTestFixtures.integerExpression(), null);
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("LN("));
	}
}
