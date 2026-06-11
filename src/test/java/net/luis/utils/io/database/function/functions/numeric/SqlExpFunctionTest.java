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
 * Test class for {@link SqlExpFunction}.<br>
 *
 * @author Luis-St
 */
class SqlExpFunctionTest {
	
	@Test
	void constructWithExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlExpFunction function = new SqlExpFunction(expression);
		assertSame(expression, function.expression());
		assertEquals(SqlTypes.DOUBLE, function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlExpFunction(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlExpFunction function = new SqlExpFunction(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeAlwaysDouble() {
		SqlExpFunction integerFunction = new SqlExpFunction(SqlTestFixtures.integerExpression());
		SqlExpFunction doubleFunction = new SqlExpFunction(SqlTestFixtures.integerExpression());
		assertEquals(SqlTypes.DOUBLE, integerFunction.type());
		assertEquals(SqlTypes.DOUBLE, doubleFunction.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlExpFunction function = new SqlExpFunction(SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlExpFunction first = new SqlExpFunction(SqlTestFixtures.integerExpression());
		SqlExpFunction second = new SqlExpFunction(SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlExpFunction function = new SqlExpFunction(SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("EXP("));
	}
}
