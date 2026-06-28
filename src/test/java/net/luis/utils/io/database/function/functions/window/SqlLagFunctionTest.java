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
 * Test class for {@link SqlLagFunction}.<br>
 *
 * @author Luis-St
 */
class SqlLagFunctionTest {
	
	@Test
	void constructWithAllArguments() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlExpression<Integer> offset = SqlTestFixtures.integerExpression();
		SqlExpression<String> defaultValue = SqlTestFixtures.stringExpression();
		SqlWindowClause over = SqlWindowClause.of();
		SqlLagFunction<String> function = new SqlLagFunction<>(column, offset, defaultValue, over);
		assertSame(column, function.column());
		assertSame(offset, function.offset());
		assertSame(defaultValue, function.defaultValue());
		assertSame(over, function.over());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void constructWithNullOffsetAndDefault() {
		SqlLagFunction<String> function = new SqlLagFunction<>(SqlTestFixtures.stringExpression(), null, null, SqlWindowClause.of());
		assertNull(function.offset());
		assertNull(function.defaultValue());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlLagFunction<String>(null, null, null, SqlWindowClause.of()));
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlLagFunction<>(SqlTestFixtures.stringExpression(), null, null, null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlLagFunction<String> function = new SqlLagFunction<>(SqlTestFixtures.stringExpression(), null, null, SqlWindowClause.of());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToColumn() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlLagFunction<String> function = new SqlLagFunction<>(column, null, null, SqlWindowClause.of());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlLagFunction<String> function = new SqlLagFunction<>(SqlTestFixtures.stringExpression(), null, null, SqlWindowClause.of());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlLagFunction<String> first = new SqlLagFunction<>(SqlTestFixtures.stringExpression(), null, null, SqlWindowClause.of());
		SqlLagFunction<String> second = new SqlLagFunction<>(SqlTestFixtures.stringExpression(), null, null, SqlWindowClause.of());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlLagFunction<String> function = new SqlLagFunction<>(SqlTestFixtures.stringExpression(), null, null, SqlWindowClause.of());
		String sql = function.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("LAG("));
		assertTrue(sql.contains("OVER("));
	}
}
