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
 * Test class for {@link SqlLastValueFunction}.<br>
 *
 * @author Luis-St
 */
class SqlLastValueFunctionTest {
	
	@Test
	void constructWithColumnAndOver() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlWindowClause over = SqlWindowClause.of();
		SqlLastValueFunction<String> function = new SqlLastValueFunction<>(column, over);
		assertSame(column, function.column());
		assertSame(over, function.over());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlLastValueFunction<>(null, SqlWindowClause.of()));
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlLastValueFunction<>(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlLastValueFunction<String> function = new SqlLastValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToColumn() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlLastValueFunction<String> function = new SqlLastValueFunction<>(column, SqlWindowClause.of());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlLastValueFunction<String> function = new SqlLastValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlLastValueFunction<String> first = new SqlLastValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		SqlLastValueFunction<String> second = new SqlLastValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlLastValueFunction<String> function = new SqlLastValueFunction<>(SqlTestFixtures.stringExpression(), SqlWindowClause.of());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("LAST_VALUE("));
	}
}
