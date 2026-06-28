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
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAliasedExpression}.<br>
 *
 * @author Luis-St
 */
class SqlAliasedExpressionTest {
	
	@Test
	void constructWithExpressionAndAlias() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlAliasedExpression<String> aliased = new SqlAliasedExpression<>(expression, SqlAlias.of("x"));
		assertSame(expression, aliased.expression());
		assertEquals("x", aliased.alias().get());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlAliasedExpression<>(null, SqlAlias.of("x")));
	}
	
	@Test
	void constructWithNullAlias() {
		assertThrows(NullPointerException.class, () -> new SqlAliasedExpression<>(SqlTestFixtures.stringExpression(), null));
	}
	
	@Test
	void asWithNullAlias() {
		SqlAliasedExpression<String> aliased = new SqlAliasedExpression<>(SqlTestFixtures.stringExpression(), SqlAlias.of("x"));
		assertThrows(NullPointerException.class, () -> aliased.as(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlAliasedExpression<String> aliased = new SqlAliasedExpression<>(SqlTestFixtures.stringExpression(), SqlAlias.of("x"));
		assertThrows(NullPointerException.class, () -> aliased.toSql(null));
	}
	
	@Test
	void asReplacesAliasWithoutNesting() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlAliasedExpression<String> a = new SqlAliasedExpression<>(expression, SqlAlias.of("x"));
		SqlAliasedExpression<?> b = assertInstanceOf(SqlAliasedExpression.class, a.as(SqlAlias.of("y")));
		assertEquals("y", b.alias().get());
		assertSame(expression, b.expression());
	}
	
	@Test
	void toSqlRendersAlias() throws SqlException {
		SqlRendered rendered = new SqlAliasedExpression<>(SqlTestFixtures.integerExpression(), SqlAlias.of("x")).toSql(SqlTestFixtures.DIALECT);
		assertTrue(rendered.sql().contains("AS"));
		assertTrue(rendered.sql().contains("\"x\""));
		assertEquals(1, rendered.parameters().size());
	}
	
	@Test
	void typeDelegatesToWrappedExpression() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlAliasedExpression<String> aliased = new SqlAliasedExpression<>(expression, SqlAlias.of("x"));
		assertSame(expression.type(), aliased.type());
	}
	
	@Test
	void accessorsReturnConstructorArguments() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlAlias alias = SqlAlias.of("x");
		SqlAliasedExpression<String> aliased = new SqlAliasedExpression<>(expression, alias);
		assertSame(expression, aliased.expression());
		assertSame(alias, aliased.alias());
	}
	
	@Test
	void equalAliasedExpressionsAreEqual() {
		SqlAliasedExpression<String> first = new SqlAliasedExpression<>(SqlTestFixtures.stringExpression(), SqlAlias.of("x"));
		SqlAliasedExpression<String> second = new SqlAliasedExpression<>(SqlTestFixtures.stringExpression(), SqlAlias.of("x"));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void aliasThenAscendingRendersBoth() throws SqlException {
		SqlAliasedExpression<Integer> aliased = new SqlAliasedExpression<>(SqlTestFixtures.integerExpression(), SqlAlias.of("x"));
		SqlRendered rendered = aliased.ascending().toSql(SqlTestFixtures.DIALECT);
		assertTrue(rendered.sql().contains("AS"));
		assertTrue(rendered.sql().contains("\"x\""));
		assertTrue(rendered.sql().contains("ASC"));
	}
}
