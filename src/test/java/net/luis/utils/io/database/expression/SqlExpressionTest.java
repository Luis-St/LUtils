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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.expression.orderable.*;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlExpression}.<br>
 *
 * @author Luis-St
 */
class SqlExpressionTest {
	
	private static final SqlExpression<String> EXPRESSION = new TestExpression(SqlTestFixtures.STRING_TYPE);
	
	@Test
	void asWithNullAlias() {
		assertThrows(NullPointerException.class, () -> EXPRESSION.as(null));
	}
	
	@Test
	void asWrapsExpressionWithAlias() {
		SqlExpression<String> result = EXPRESSION.as(SqlAlias.of("x"));
		SqlAliasedExpression<?> aliased = assertInstanceOf(SqlAliasedExpression.class, result);
		assertSame(EXPRESSION, aliased.expression());
		assertEquals("x", aliased.alias().get());
	}
	
	@Test
	void ascendingReturnsAscendingExpression() {
		OrderedSqlExpression<String> result = EXPRESSION.ascending();
		assertInstanceOf(AscendingOrderedSqlExpression.class, result);
		assertEquals(SqlOrdering.ASCENDING, result.ordering());
		assertEquals(SqlNullOrdering.DEFAULT, result.nullOrdering());
		assertSame(EXPRESSION, result.expression());
	}
	
	@Test
	void descendingReturnsDescendingExpression() {
		OrderedSqlExpression<String> result = EXPRESSION.descending();
		assertInstanceOf(DescendingOrderedSqlExpression.class, result);
		assertEquals(SqlOrdering.DESCENDING, result.ordering());
		assertEquals(SqlNullOrdering.DEFAULT, result.nullOrdering());
	}
	
	@Test
	void nullsFirstReturnsDefaultOrderingWithNullsFirst() {
		OrderedSqlExpression<String> result = EXPRESSION.nullsFirst();
		assertInstanceOf(DefaultOrderedSqlExpression.class, result);
		assertEquals(SqlOrdering.DEFAULT, result.ordering());
		assertEquals(SqlNullOrdering.NULLS_FIRST, result.nullOrdering());
	}
	
	@Test
	void nullsLastReturnsDefaultOrderingWithNullsLast() {
		OrderedSqlExpression<String> result = EXPRESSION.nullsLast();
		assertInstanceOf(DefaultOrderedSqlExpression.class, result);
		assertEquals(SqlOrdering.DEFAULT, result.ordering());
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
	}
	
	@Test
	void ascendingWrapsSameUnderlyingExpression() {
		assertSame(EXPRESSION, EXPRESSION.ascending().expression());
	}
	
	private record TestExpression(SqlType<String> type) implements SqlExpression<String> {
		
		@Override
		public SqlRendered toSql(SqlDialect dialect) {
			return SqlRendered.of("test");
		}
	}
}
