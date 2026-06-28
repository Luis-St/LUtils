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
 * Test class for {@link SqlAtan2Function}.<br>
 *
 * @author Luis-St
 */
class SqlAtan2FunctionTest {
	
	@Test
	void constructWithExpressions() {
		SqlExpression<Integer> y = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> x = SqlTestFixtures.integerExpression();
		SqlAtan2Function function = new SqlAtan2Function(y, x);
		assertSame(y, function.y());
		assertSame(x, function.x());
		assertEquals(SqlTypes.DOUBLE, function.type());
	}
	
	@Test
	void constructWithNullY() {
		assertThrows(NullPointerException.class, () -> new SqlAtan2Function(null, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructWithNullX() {
		assertThrows(NullPointerException.class, () -> new SqlAtan2Function(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlAtan2Function function = new SqlAtan2Function(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeAlwaysDouble() {
		SqlAtan2Function function = new SqlAtan2Function(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertEquals(SqlTypes.DOUBLE, function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlAtan2Function function = new SqlAtan2Function(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlAtan2Function first = new SqlAtan2Function(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlAtan2Function second = new SqlAtan2Function(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlAtan2Function function = new SqlAtan2Function(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("ATAN2("));
	}
}
