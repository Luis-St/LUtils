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
import net.luis.utils.io.database.function.functions.aggregate.SqlCountFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlSumFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlWindowedAggregate}.<br>
 *
 * @author Luis-St
 */
class SqlWindowedAggregateTest {
	
	@Test
	void constructWithAggregateAndOver() {
		SqlSumFunction<Integer> aggregate = new SqlSumFunction<>(SqlTestFixtures.integerExpression());
		SqlWindowClause over = SqlWindowClause.of();
		SqlWindowedAggregate<Integer> function = new SqlWindowedAggregate<>(aggregate, over);
		assertSame(aggregate, function.aggregate());
		assertSame(over, function.over());
		assertEquals(aggregate.type(), function.type());
	}
	
	@Test
	void constructWithNullAggregate() {
		assertThrows(NullPointerException.class, () -> new SqlWindowedAggregate<Object>(null, SqlWindowClause.of()));
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlWindowedAggregate<>(new SqlSumFunction<>(SqlTestFixtures.integerExpression()), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlWindowedAggregate<Integer> function = new SqlWindowedAggregate<>(new SqlSumFunction<>(SqlTestFixtures.integerExpression()), SqlWindowClause.of());
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void typeDelegatesToAggregate() {
		SqlSumFunction<Integer> aggregate = new SqlSumFunction<>(SqlTestFixtures.integerExpression());
		SqlWindowedAggregate<Integer> function = new SqlWindowedAggregate<>(aggregate, SqlWindowClause.of());
		assertEquals(aggregate.type(), function.type());
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlWindowedAggregate<Integer> function = new SqlWindowedAggregate<>(new SqlSumFunction<>(SqlTestFixtures.integerExpression()), SqlWindowClause.of());
		assertFalse(function.requiresCast());
	}
	
	@Test
	void wrapsDifferentAggregateTypes() {
		SqlWindowedAggregate<Long> count = new SqlWindowedAggregate<>(new SqlCountFunction(SqlTestFixtures.integerExpression()), SqlWindowClause.of());
		assertEquals(SqlTypes.LONG, count.type());
		SqlSumFunction<Integer> sum = new SqlSumFunction<>(SqlTestFixtures.integerExpression());
		SqlWindowedAggregate<Integer> sumAggregate = new SqlWindowedAggregate<>(sum, SqlWindowClause.of());
		assertEquals(sum.type(), sumAggregate.type());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlWindowedAggregate<Integer> first = new SqlWindowedAggregate<>(new SqlSumFunction<>(SqlTestFixtures.integerExpression()), SqlWindowClause.of());
		SqlWindowedAggregate<Integer> second = new SqlWindowedAggregate<>(new SqlSumFunction<>(SqlTestFixtures.integerExpression()), SqlWindowClause.of());
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlWindowedAggregate<Integer> function = new SqlWindowedAggregate<>(new SqlSumFunction<>(SqlTestFixtures.integerExpression()), SqlWindowClause.of());
		String sql = function.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("SUM("));
		assertTrue(sql.contains("OVER("));
	}
}
