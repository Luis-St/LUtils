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
 * Test class for {@link DefaultOrderedSqlExpression}.<br>
 *
 * @author Luis-St
 */
class DefaultOrderedSqlExpressionTest {
	
	@Test
	void constructWithExpressionAndNullOrdering() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(expression, SqlNullOrdering.NULLS_FIRST);
		assertSame(expression, ordered.expression());
		assertEquals(SqlNullOrdering.NULLS_FIRST, ordered.nullOrdering());
		assertEquals(SqlOrdering.DEFAULT, ordered.ordering());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new DefaultOrderedSqlExpression<>(null, SqlNullOrdering.DEFAULT));
	}
	
	@Test
	void constructWithNullNullOrdering() {
		assertThrows(NullPointerException.class, () -> new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void asWithNullAlias() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		assertThrows(NullPointerException.class, () -> ordered.as(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		assertThrows(NullPointerException.class, () -> ordered.toSql(null));
	}
	
	@Test
	void orderingIsDefault() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		assertEquals(SqlOrdering.DEFAULT, ordered.ordering());
	}
	
	@Test
	void ascendingReturnsAscendingPreservingNullOrdering() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(expression, SqlNullOrdering.NULLS_FIRST);
		OrderedSqlExpression<Integer> result = ordered.ascending();
		assertInstanceOf(AscendingOrderedSqlExpression.class, result);
		assertEquals(SqlNullOrdering.NULLS_FIRST, result.nullOrdering());
		assertSame(expression, result.expression());
	}
	
	@Test
	void descendingReturnsDescendingPreservingNullOrdering() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(expression, SqlNullOrdering.NULLS_LAST);
		OrderedSqlExpression<Integer> result = ordered.descending();
		assertInstanceOf(DescendingOrderedSqlExpression.class, result);
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
		assertSame(expression, result.expression());
	}
	
	@Test
	void nullsFirstWhenAlreadyNullsFirstReturnsSame() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_FIRST);
		assertSame(ordered, ordered.nullsFirst());
	}
	
	@Test
	void nullsFirstWhenNotNullsFirstReturnsNew() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		OrderedSqlExpression<Integer> result = ordered.nullsFirst();
		assertNotSame(ordered, result);
		assertEquals(SqlNullOrdering.NULLS_FIRST, result.nullOrdering());
		assertEquals(SqlOrdering.DEFAULT, result.ordering());
	}
	
	@Test
	void nullsLastWhenAlreadyNullsLastReturnsSame() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_LAST);
		assertSame(ordered, ordered.nullsLast());
	}
	
	@Test
	void nullsLastWhenNotNullsLastReturnsNew() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		OrderedSqlExpression<Integer> result = ordered.nullsLast();
		assertNotSame(ordered, result);
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
	}
	
	@Test
	void toSqlRendersWrappedExpression() throws SqlException {
		SqlRendered rendered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT).toSql(SqlTestFixtures.DIALECT);
		assertTrue(rendered.sql().contains("?"));
		assertEquals(1, rendered.parameters().size());
	}
	
	@Test
	void typeDelegatesToWrappedExpression() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(expression, SqlNullOrdering.DEFAULT);
		assertSame(expression.type(), ordered.type());
	}
	
	@Test
	void asWrapsInAliasedExpression() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		SqlAliasedExpression<?> aliased = assertInstanceOf(SqlAliasedExpression.class, ordered.as(SqlAlias.of("x")));
		assertSame(ordered, aliased.expression());
	}
	
	@Test
	void equalDefaultExpressionsAreEqual() {
		DefaultOrderedSqlExpression<Integer> first = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_FIRST);
		DefaultOrderedSqlExpression<Integer> second = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.NULLS_FIRST);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void nullsFirstThenNullsLastSwitchesNullOrdering() {
		DefaultOrderedSqlExpression<Integer> ordered = new DefaultOrderedSqlExpression<>(SqlTestFixtures.integerExpression(), SqlNullOrdering.DEFAULT);
		OrderedSqlExpression<Integer> result = ordered.nullsFirst().nullsLast();
		assertEquals(SqlNullOrdering.NULLS_LAST, result.nullOrdering());
		assertEquals(SqlOrdering.DEFAULT, result.ordering());
	}
}
