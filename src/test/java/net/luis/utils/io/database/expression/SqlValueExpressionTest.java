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

package net.luis.utils.io.database.expression;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlValueExpression}.<br>
 *
 * @author Luis-St
 */
class SqlValueExpressionTest {
	
	@Test
	void constructWithValueAndType() {
		SqlValueExpression<String> expression = new SqlValueExpression<>("test", SqlTestFixtures.STRING_TYPE);
		assertEquals("test", expression.value());
		assertSame(SqlTestFixtures.STRING_TYPE, expression.type());
	}
	
	@Test
	void constructWithValueOnly() throws SqlException {
		SqlValueExpression<Integer> expression = new SqlValueExpression<>(5);
		assertEquals(5, expression.value());
		assertNotNull(expression.type());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlValueExpression<>(null, SqlTestFixtures.STRING_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlValueExpression<>("test", null));
	}
	
	@Test
	void constructWithValueOnlyNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlValueExpression<>(null));
	}
	
	@Test
	void constructWithValueOnlyNonInferrableType() {
		assertThrows(SqlTypeNotFoundException.class, () -> new SqlValueExpression<>(new Object()));
	}
	
	@Test
	void toSqlWithNullDialect() throws SqlException {
		SqlValueExpression<Integer> expression = new SqlValueExpression<>(5);
		assertThrows(NullPointerException.class, () -> expression.toSql(null));
	}
	
	@Test
	void toSqlRendersPlaceholder() throws SqlException {
		SqlRendered rendered = new SqlValueExpression<>(5, SqlTestFixtures.INTEGER_TYPE).toSql(SqlTestFixtures.DIALECT);
		assertEquals("?", rendered.sql());
		assertEquals(1, rendered.parameters().size());
	}
	
	@Test
	void accessorsReturnConstructorArguments() {
		SqlValueExpression<String> expression = new SqlValueExpression<>("name", SqlTestFixtures.STRING_TYPE);
		assertEquals("name", expression.value());
		assertSame(SqlTestFixtures.STRING_TYPE, expression.type());
	}
	
	@Test
	void inheritedOrderingMethodsAreReachable() {
		SqlValueExpression<Integer> expression = new SqlValueExpression<>(5, SqlTestFixtures.INTEGER_TYPE);
		assertNotNull(expression.ascending());
		assertNotNull(expression.descending());
		assertNotNull(expression.nullsFirst());
		assertNotNull(expression.nullsLast());
		assertNotNull(expression.as(SqlAlias.of("x")));
	}
	
	@Test
	void equalValueExpressionsAreEqual() {
		SqlValueExpression<Integer> first = new SqlValueExpression<>(5, SqlTestFixtures.INTEGER_TYPE);
		SqlValueExpression<Integer> second = new SqlValueExpression<>(5, SqlTestFixtures.INTEGER_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlOfStringValueRendersPlaceholder() throws SqlException {
		SqlRendered rendered = SqlTestFixtures.stringExpression().toSql(SqlTestFixtures.DIALECT);
		assertEquals("?", rendered.sql());
		assertEquals(1, rendered.parameters().size());
	}
}
