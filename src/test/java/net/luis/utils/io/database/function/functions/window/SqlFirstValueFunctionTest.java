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

package net.luis.utils.io.database.function.functions.window;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlFirstValueFunction}.<br>
 *
 * @author Luis-St
 */
class SqlFirstValueFunctionTest {
	
	@Test
	void constructWithColumnAndOver() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlWindowClause over = SqlWindowClause.of();
		SqlFirstValueFunction<String> function = new SqlFirstValueFunction<>(column, over);
		assertSame(column, function.column());
		assertSame(over, function.over());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlFirstValueFunction<>(null, SqlWindowClause.of()));
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlFirstValueFunction<>(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlFirstValueFunction<String> function = new SqlFirstValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToColumn() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlFirstValueFunction<String> function = new SqlFirstValueFunction<>(column, SqlWindowClause.of());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlFirstValueFunction<String> function = new SqlFirstValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlFirstValueFunction<String> first = new SqlFirstValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		SqlFirstValueFunction<String> second = new SqlFirstValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlFirstValueFunction<String> function = new SqlFirstValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("FIRST_VALUE("));
	}
}
