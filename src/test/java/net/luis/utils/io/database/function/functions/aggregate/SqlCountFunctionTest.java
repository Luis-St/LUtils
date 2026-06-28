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

package net.luis.utils.io.database.function.functions.aggregate;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCountFunction}.<br>
 *
 * @author Luis-St
 */
class SqlCountFunctionTest {
	
	@Test
	void constructWithExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlCountFunction function = new SqlCountFunction(expression);
		assertSame(expression, function.expression());
		assertEquals(SqlTypes.LONG, function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		SqlCountFunction function = new SqlCountFunction(null);
		assertNull(function.expression());
		assertEquals(SqlTypes.LONG, function.type());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlCountFunction function = new SqlCountFunction(null);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeAlwaysLong() {
		SqlCountFunction function = new SqlCountFunction(SqlTestFixtures.integerExpression());
		assertEquals(SqlTypes.LONG, function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		assertFalse(new SqlCountFunction(null).requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlCountFunction first = new SqlCountFunction(SqlTestFixtures.integerExpression());
		SqlCountFunction second = new SqlCountFunction(SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertEquals(new SqlCountFunction(null), new SqlCountFunction(null));
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlCountFunction function = new SqlCountFunction(SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("COUNT("));
	}
}
