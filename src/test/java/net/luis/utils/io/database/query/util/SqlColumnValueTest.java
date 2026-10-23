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

package net.luis.utils.io.database.query.util;

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlColumnValue}.<br>
 *
 * @author Luis-St
 */
class SqlColumnValueTest {
	
	@Test
	void constructWithValidColumnAndExpression() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlExpression<Integer> expression = integerExpression();
		SqlColumnValue<Object, Integer> columnValue = new SqlColumnValue<>(column, expression);
		assertSame(column, columnValue.column());
		assertSame(expression, columnValue.expression());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlColumnValue<>(null, integerExpression()));
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlColumnValue<>(integerColumn(), null));
	}
	
	@Test
	void ofValueWithNullColumnThrows() {
		assertThrows(NullPointerException.class, () -> SqlColumnValue.of(null, 1));
	}
	
	@Test
	void ofValueWithNullValueThrows() {
		SqlColumn<Object, Integer> column = integerColumn();
		assertThrows(NullPointerException.class, () -> SqlColumnValue.of(column, (Integer) null));
	}
	
	@Test
	void ofExpressionWithNullColumnThrows() {
		assertThrows(NullPointerException.class, () -> SqlColumnValue.of((SqlColumn<Object, Integer>) null, integerExpression()));
	}
	
	@Test
	void ofExpressionWithNullExpressionThrows() {
		SqlColumn<Object, Integer> column = integerColumn();
		assertThrows(NullPointerException.class, () -> SqlColumnValue.of(column, (SqlExpression<Integer>) null));
	}
	
	@Test
	void ofValueWrapsValueAsExpression() throws SqlException {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlColumnValue<Object, Integer> result = SqlColumnValue.of(column, 42);
		assertSame(column, result.column());
		assertEquals(Sql.of(42, column.type()).toSql(DIALECT), result.expression().toSql(DIALECT));
	}
	
	@Test
	void ofExpressionStoresGivenExpressionVerbatim() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlExpression<Integer> expression = integerExpression();
		SqlColumnValue<Object, Integer> result = SqlColumnValue.of(column, expression);
		assertSame(column, result.column());
		assertSame(expression, result.expression());
	}
	
	@Test
	void ofValueAndOfExpressionProduceEquivalentRendering() throws SqlException {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlColumnValue<Object, Integer> viaValue = SqlColumnValue.of(column, 7);
		SqlColumnValue<Object, Integer> viaExpression = SqlColumnValue.of(column, Sql.of(7, column.type()));
		assertEquals(viaExpression.expression().toSql(DIALECT), viaValue.expression().toSql(DIALECT));
	}
}
