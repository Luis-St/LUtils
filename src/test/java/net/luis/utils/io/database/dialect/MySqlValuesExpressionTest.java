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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlValuesExpression}.<br>
 *
 * @author Luis-St
 */
class MySqlValuesExpressionTest {
	
	@Test
	void constructWithValidColumn() {
		SqlColumn<Object, Integer> column = integerColumn();
		MySqlValuesExpression<Integer> expression = new MySqlValuesExpression<>(column);
		assertSame(column, expression.column());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new MySqlValuesExpression<>(null));
	}
	
	@Test
	void toSqlWithNullDialectThrows() {
		MySqlValuesExpression<Integer> expression = new MySqlValuesExpression<>(integerColumn());
		assertThrows(NullPointerException.class, () -> expression.toSql(null));
	}
	
	@Test
	void typeReturnsColumnType() {
		SqlColumn<Object, Integer> column = integerColumn();
		MySqlValuesExpression<Integer> expression = new MySqlValuesExpression<>(column);
		assertEquals(column.type(), expression.type());
	}
	
	@Test
	void toSqlRendersValuesWrappedIdentifier() throws SqlException {
		SqlColumn<Object, Integer> column = integerColumn();
		MySqlValuesExpression<Integer> expression = new MySqlValuesExpression<>(column);
		String sql = expression.toSql(DIALECT).sql();
		assertEquals("VALUES(" + DIALECT.quoteIdentifier(column.name()) + ")", sql);
	}
	
	@Test
	void toSqlUsesGivenDialectsQuoting() throws SqlException {
		SqlColumn<Object, Integer> column = integerColumn();
		MySqlValuesExpression<Integer> expression = new MySqlValuesExpression<>(column);
		String defaultSql = expression.toSql(SqlDialects.DEFAULT).sql();
		String mysqlSql = expression.toSql(SqlDialects.MYSQL).sql();
		assertNotEquals(defaultSql, mysqlSql);
		assertTrue(defaultSql.contains("\"" + column.name() + "\""));
		assertTrue(mysqlSql.contains("`" + column.name() + "`"));
	}
	
	@Test
	void toSqlWithStringTypedColumn() throws SqlException {
		SqlColumn<Object, String> column = stringColumn();
		MySqlValuesExpression<String> expression = new MySqlValuesExpression<>(column);
		String sql = expression.toSql(DIALECT).sql();
		assertEquals("VALUES(" + DIALECT.quoteIdentifier(column.name()) + ")", sql);
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlColumn<Object, Integer> column = integerColumn();
		MySqlValuesExpression<Integer> first = new MySqlValuesExpression<>(column);
		MySqlValuesExpression<Integer> second = new MySqlValuesExpression<>(column);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void inheritedOrderingMethodsAreReachable() {
		MySqlValuesExpression<Integer> expression = new MySqlValuesExpression<>(integerColumn());
		assertNotNull(expression.ascending());
		assertNotNull(expression.descending());
		assertNotNull(expression.nullsFirst());
		assertNotNull(expression.nullsLast());
		assertNotNull(expression.as(SqlAlias.of("x")));
	}
}
