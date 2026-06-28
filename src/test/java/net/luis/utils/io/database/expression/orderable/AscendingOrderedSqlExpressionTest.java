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
 * Test class for {@link AscendingOrderedSqlExpression}.<br>
 *
 * @author Luis-St
 */
class AscendingOrderedSqlExpressionTest {
	
	@Test
	void constructWithExpressionAndNullOrdering() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(expression, SqlNullOrdering.NULLS_FIRST);
		assertSame(expression, ascending.expression());
		assertEquals(SqlNullOrdering.NULLS_FIRST, ascending.nullOrdering());
		assertEquals(SqlOrdering.ASCENDING, ascending.ordering());
	}
	
	@Test
	void constructWithExpressionOnly() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertEquals(SqlNullOrdering.DEFAULT, ascending.nullOrdering());
		assertEquals(SqlOrdering.ASCENDING, ascending.ordering());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new AscendingOrderedSqlExpression<>(null, SqlNullOrdering.DEFAULT));
	}
	
	@Test
	void constructWithNullNullOrdering() {
		assertThrows(NullPointerException.class, () -> new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructWithExpressionOnlyNullExpression() {
		assertThrows(NullPointerException.class, () -> new AscendingOrderedSqlExpression<>(null));
	}
	
	@Test
	void asWithNullAlias() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> ascending.as(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertThrows(NullPointerException.class, () -> ascending.toSql(null));
	}
	
	@Test
	void orderingIsAscending() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertEquals(SqlOrdering.ASCENDING, ascending.ordering());
	}
	
	@Test
	void ascendingReturnsSameInstance() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		assertSame(ascending, ascending.ascending());
	}
	
	@Test
	void descendingReturnsDescendingPreservingNullOrdering() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(expression, SqlNullOrdering.NULLS_LAST);
		OrderedSqlExpression<Integer> result = ascending.descending();
		assertInstanceOf(DescendingOrderedSqlExpression.class, result);
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
		assertSame(expression, result.expression());
	}
	
	@Test
	void nullsFirstWhenAlreadyNullsFirstReturnsSame() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_FIRST);
		assertSame(ascending, ascending.nullsFirst());
	}
	
	@Test
	void nullsFirstWhenNotNullsFirstReturnsNew() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		OrderedSqlExpression<Integer> result = ascending.nullsFirst();
		assertNotSame(ascending, result);
		assertEquals(SqlNullOrdering.NULLS_FIRST, result.nullOrdering());
		assertEquals(SqlOrdering.ASCENDING, result.ordering());
	}
	
	@Test
	void nullsLastWhenAlreadyNullsLastReturnsSame() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_LAST);
		assertSame(ascending, ascending.nullsLast());
	}
	
	@Test
	void nullsLastWhenNotNullsLastReturnsNew() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		OrderedSqlExpression<Integer> result = ascending.nullsLast();
		assertNotSame(ascending, result);
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
	}
	
	@Test
	void toSqlRendersAscending() throws SqlException {
		SqlRendered rendered = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression()).toSql(SqlTestFixtures.DIALECT);
		assertTrue(rendered.sql().contains("ASC"));
		assertTrue(rendered.sql().contains("?"));
	}
	
	@Test
	void typeDelegatesToWrappedExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(expression);
		assertSame(expression.type(), ascending.type());
	}
	
	@Test
	void asWrapsInAliasedExpression() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		SqlAliasedExpression<?> aliased = assertInstanceOf(SqlAliasedExpression.class, ascending.as(SqlAlias.of("x")));
		assertSame(ascending, aliased.expression());
	}
	
	@Test
	void equalAscendingExpressionsAreEqual() {
		AscendingOrderedSqlExpression<Integer> first = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_FIRST);
		AscendingOrderedSqlExpression<Integer> second = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_FIRST);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void nullsFirstThenAscendingChainStaysAscending() {
		AscendingOrderedSqlExpression<Integer> ascending = new AscendingOrderedSqlExpression<>(SqlTestFixtures.integerExpression());
		OrderedSqlExpression<Integer> result = ascending.nullsFirst().ascending();
		assertEquals(SqlOrdering.ASCENDING, result.ordering());
		assertEquals(SqlNullOrdering.NULLS_FIRST, result.nullOrdering());
	}
}
