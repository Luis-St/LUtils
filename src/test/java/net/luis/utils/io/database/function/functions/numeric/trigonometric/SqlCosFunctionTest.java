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

package net.luis.utils.io.database.function.functions.numeric.trigonometric;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCosFunction}.<br>
 *
 * @author Luis-St
 */
class SqlCosFunctionTest {
	
	@Test
	void constructWithExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlCosFunction function = new SqlCosFunction(expression);
		assertSame(expression, function.expression());
		assertEquals(SqlTypes.DOUBLE, function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlCosFunction(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlCosFunction function = new SqlCosFunction(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeAlwaysDouble() {
		SqlCosFunction integerFunction = new SqlCosFunction(SqlTestFixtures.integerExpression());
		SqlCosFunction doubleFunction = new SqlCosFunction(SqlTestFixtures.integerExpression());
		assertEquals(SqlTypes.DOUBLE, integerFunction.type());
		assertEquals(SqlTypes.DOUBLE, doubleFunction.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlCosFunction function = new SqlCosFunction(SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlCosFunction first = new SqlCosFunction(SqlTestFixtures.integerExpression());
		SqlCosFunction second = new SqlCosFunction(SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlCosFunction function = new SqlCosFunction(SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("COS("));
	}
}
