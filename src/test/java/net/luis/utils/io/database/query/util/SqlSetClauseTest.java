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

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSetClause}.<br>
 *
 * @author Luis-St
 */
class SqlSetClauseTest {
	
	@Test
	void constructExpressionTypeWithExpression() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlExpression<Integer> expression = integerExpression();
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(column, expression, SqlSetType.EXPRESSION);
		assertSame(column, clause.column());
		assertSame(expression, clause.expression());
		assertEquals(SqlSetType.EXPRESSION, clause.type());
	}
	
	@Test
	void constructNullTypeWithoutExpression() {
		SqlSetClause<Object, Integer> clause = assertDoesNotThrow(() -> new SqlSetClause<>(integerColumn(), null, SqlSetType.NULL));
		assertNull(clause.expression());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlSetClause<>(null, integerExpression(), SqlSetType.EXPRESSION));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlSetClause<>(integerColumn(), integerExpression(), null));
	}
	
	@Test
	void constructExpressionTypeWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlSetClause<>(integerColumn(), null, SqlSetType.EXPRESSION));
	}
	
	@Test
	void constructIncrementTypeWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlSetClause<>(integerColumn(), null, SqlSetType.INCREMENT));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(integerColumn(), integerExpression(), SqlSetType.EXPRESSION);
		assertThrows(NullPointerException.class, () -> clause.toSql(null));
	}
	
	@Test
	void toSqlExpressionType() throws SqlException {
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(integerColumn(), integerExpression(), SqlSetType.EXPRESSION);
		String sql = clause.toSql(DIALECT).sql();
		assertTrue(sql.contains(DIALECT.quoteIdentifier("id")));
		assertTrue(sql.contains("="));
	}
	
	@Test
	void toSqlIncrementType() throws SqlException {
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(integerColumn(), integerExpression(), SqlSetType.INCREMENT);
		String sql = clause.toSql(DIALECT).sql();
		assertTrue(sql.contains("+"));
		assertTrue(sql.contains(DIALECT.quoteIdentifier("test_table") + "." + DIALECT.quoteIdentifier("id")));
	}
	
	@Test
	void toSqlDecrementType() throws SqlException {
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(integerColumn(), integerExpression(), SqlSetType.DECREMENT);
		String sql = clause.toSql(DIALECT).sql();
		assertTrue(sql.contains("-"));
		assertTrue(sql.contains(DIALECT.quoteIdentifier("test_table") + "." + DIALECT.quoteIdentifier("id")));
	}
	
	@Test
	void toSqlNullType() throws SqlException {
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(integerColumn(), null, SqlSetType.NULL);
		String sql = clause.toSql(DIALECT).sql();
		assertTrue(sql.contains("NULL"));
		assertTrue(sql.contains(DIALECT.quoteIdentifier("id")));
	}
}
