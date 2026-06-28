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
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTileBucketFunction}.<br>
 *
 * @author Luis-St
 */
class SqlTileBucketFunctionTest {
	
	@Test
	void constructWithBucketsOverAndType() {
		SqlExpression<Integer> buckets = SqlTestFixtures.integerExpression();
		SqlWindowClause over = SqlWindowClause.of();
		SqlTileBucketFunction<Integer> function = new SqlTileBucketFunction<>(buckets, over, SqlTestFixtures.INTEGER_TYPE);
		assertSame(buckets, function.buckets());
		assertSame(over, function.over());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithNullBuckets() {
		assertThrows(NullPointerException.class, () -> new SqlTileBucketFunction<>(null, SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullOver() {
		assertThrows(NullPointerException.class, () -> new SqlTileBucketFunction<>(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlTileBucketFunction<>(SqlTestFixtures.integerExpression(), SqlWindowClause.of(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlTileBucketFunction<Integer> function = new SqlTileBucketFunction<>(SqlTestFixtures.integerExpression(), SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlTileBucketFunction<Integer> function = new SqlTileBucketFunction<>(SqlTestFixtures.integerExpression(), SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlTileBucketFunction<Integer> first = new SqlTileBucketFunction<>(SqlTestFixtures.integerExpression(), SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		SqlTileBucketFunction<Integer> second = new SqlTileBucketFunction<>(SqlTestFixtures.integerExpression(), SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlTileBucketFunction<Integer> function = new SqlTileBucketFunction<>(SqlTestFixtures.integerExpression(), SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE);
		assertTrue(function.toSql(SqlTestFixtures.DIALECT).sql().contains("NTILE("));
	}
}
