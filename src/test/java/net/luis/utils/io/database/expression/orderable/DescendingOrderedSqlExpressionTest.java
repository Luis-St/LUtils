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

package net.luis.utils.io.database.expression.orderable;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlAliasedExpression;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DescendingOrderedSqlExpression}.<br>
 *
 * @author Luis-St
 */
class DescendingOrderedSqlExpressionTest {
	
	@Test
	void constructWithExpressionAndNullOrdering() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(expression, SqlNullOrdering.NULLS_LAST);
		assertSame(expression, descending.expression());
		assertEquals(SqlNullOrdering.NULLS_LAST, descending.nullOrdering());
		assertEquals(SqlOrdering.DESCENDING, descending.ordering());
	}
	
	@Test
	void constructWithExpressionOnly() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertEquals(SqlNullOrdering.DEFAULT, descending.nullOrdering());
		assertEquals(SqlOrdering.DESCENDING, descending.ordering());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new DescendingOrderedSqlExpression<>(null, SqlNullOrdering.DEFAULT));
	}
	
	@Test
	void constructWithNullNullOrdering() {
		assertThrows(NullPointerException.class, () -> new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructWithExpressionOnlyNullExpression() {
		assertThrows(NullPointerException.class, () -> new DescendingOrderedSqlExpression<>(null));
	}
	
	@Test
	void asWithNullAlias() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> descending.as(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> descending.toSql(null));
	}
	
	@Test
	void orderingIsDescending() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertEquals(SqlOrdering.DESCENDING, descending.ordering());
	}
	
	@Test
	void descendingReturnsSameInstance() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertSame(descending, descending.descending());
	}
	
	@Test
	void ascendingReturnsAscendingPreservingNullOrdering() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(expression, SqlNullOrdering.NULLS_FIRST);
		OrderedSqlExpression<Integer> result = descending.ascending();
		assertInstanceOf(AscendingOrderedSqlExpression.class, result);
		assertEquals(SqlNullOrdering.NULLS_FIRST, result.nullOrdering());
		assertSame(expression, result.expression());
	}
	
	@Test
	void nullsFirstWhenAlreadyNullsFirstReturnsSame() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_FIRST);
		assertSame(descending, descending.nullsFirst());
	}
	
	@Test
	void nullsFirstWhenNotNullsFirstReturnsNew() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		OrderedSqlExpression<Integer> result = descending.nullsFirst();
		assertNotSame(descending, result);
		assertEquals(SqlNullOrdering.NULLS_FIRST, result.nullOrdering());
		assertEquals(SqlOrdering.DESCENDING, result.ordering());
	}
	
	@Test
	void nullsLastWhenAlreadyNullsLastReturnsSame() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_LAST);
		assertSame(descending, descending.nullsLast());
	}
	
	@Test
	void nullsLastWhenNotNullsLastReturnsNew() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		OrderedSqlExpression<Integer> result = descending.nullsLast();
		assertNotSame(descending, result);
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
	}
	
	@Test
	void toSqlRendersDescending() throws SqlException {
		SqlRendered rendered = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression()).toSql(SqlTestFixtures.DIALECT);
		assertTrue(rendered.sql().contains("DESC"));
		assertTrue(rendered.sql().contains("?"));
	}
	
	@Test
	void typeDelegatesToWrappedExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(expression);
		assertSame(expression.type(), descending.type());
	}
	
	@Test
	void asWrapsInAliasedExpression() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		SqlAliasedExpression<?> aliased = assertInstanceOf(SqlAliasedExpression.class, descending.as(SqlAlias.of("x")));
		assertSame(descending, aliased.expression());
	}
	
	@Test
	void equalDescendingExpressionsAreEqual() {
		DescendingOrderedSqlExpression<Integer> first = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_LAST);
		DescendingOrderedSqlExpression<Integer> second = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_LAST);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void ascendingThenDescendingRoundTripPreservesNullOrdering() {
		DescendingOrderedSqlExpression<Integer> descending = new DescendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_LAST);
		OrderedSqlExpression<Integer> result = descending.ascending().descending();
		assertEquals(SqlOrdering.DESCENDING, result.ordering());
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
	}
}
