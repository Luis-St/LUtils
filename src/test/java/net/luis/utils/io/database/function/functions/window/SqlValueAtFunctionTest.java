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
 * Test class for {@link SqlValueAtFunction}.<br>
 *
 * @author Luis-St
 */
class SqlValueAtFunctionTest {
	
	@Test
	void constructWithColumnPositionAndOver() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlExpression<Integer> position = SqlTestFixtures.integerExpression();
		SqlWindowClause over = SqlWindowClause.of();
		SqlValueAtFunction<String> function = new SqlValueAtFunction<>(column, position, over);
		assertSame(column, function.column());
		assertSame(position, function.position());
		assertSame(over, function.over());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlValueAtFunction<>(null, SqlTestFixtures.integerExpression(), SqlWindowClause.of()));
	}
	
	@Test
	void constructWithNullPosition() {
		assertThrows(NullPointerException.class, () -> new SqlValueAtFunction<>(SqlTestFixtures.stringExpression(), null, SqlWindowClause.of()));
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlValueAtFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlValueAtFunction<String> function = new SqlValueAtFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlWindowClause.of());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToColumn() {
		SqlExpression<String> column = SqlTestFixtures.stringExpression();
		SqlValueAtFunction<String> function = new SqlValueAtFunction<>(column, SqlTestFixtures.integerExpression(), SqlWindowClause.of());
		assertEquals(column.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlValueAtFunction<String> function = new SqlValueAtFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlWindowClause.of());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlValueAtFunction<String> first = new SqlValueAtFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlWindowClause.of());
		SqlValueAtFunction<String> second = new SqlValueAtFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlWindowClause.of());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlValueAtFunction<String> function = new SqlValueAtFunction<>(SqlTestFixtures.stringExpression(), SqlTestFixtures.integerExpression(), SqlWindowClause.of());
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("NTH_VALUE("));
	}
}
