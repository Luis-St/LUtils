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

package net.luis.utils.io.database.function.functions.temporal;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTemporalAddFunction}.<br>
 *
 * @author Luis-St
 */
class SqlTemporalAddFunctionTest {
	
	@Test
	void constructWithSummandsPartAndType() {
		SqlExpression<Integer> first = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> second = SqlTestFixtures.integerExpression();
		SqlTemporalAddFunction<Integer> function = new SqlTemporalAddFunction<>(first, SqlTemporalPart.DAY, second, SqlTestFixtures.INTEGER_TYPE);
		assertSame(first, function.firstSummand());
		assertEquals(SqlTemporalPart.DAY, function.part());
		assertSame(second, function.secondSummand());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithNullFirstSummand() {
		assertThrows(NullPointerException.class, () -> new SqlTemporalAddFunction<>(null, SqlTemporalPart.DAY, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullPart() {
		assertThrows(NullPointerException.class, () -> new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullSecondSummand() {
		assertThrows(NullPointerException.class, () -> new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.DAY, null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.DAY, SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlTemporalAddFunction<Integer> function = new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.DAY, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlTemporalAddFunction<Integer> function = new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.DAY, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlTemporalAddFunction<Integer> first = new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.DAY, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE);
		SqlTemporalAddFunction<Integer> second = new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.DAY, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlTemporalAddFunction<Integer> function = new SqlTemporalAddFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.DAY, SqlTestFixtures.integerExpression(), SqlTestFixtures.INTEGER_TYPE);
		String sql = function.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("+"));
		assertTrue(sql.contains("INTERVAL"));
	}
}
