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
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMakeDateFunction}.<br>
 *
 * @author Luis-St
 */
class SqlMakeDateFunctionTest {
	
	@Test
	void constructWithYearMonthDayAndType() {
		SqlExpression<Integer> year = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> month = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> day = SqlTestFixtures.integerExpression();
		SqlMakeDateFunction<?> function = new SqlMakeDateFunction<>(year, month, day, SqlTypes.LOCAL_DATE);
		assertSame(year, function.year());
		assertSame(month, function.month());
		assertSame(day, function.day());
		assertEquals(SqlTypes.LOCAL_DATE, function.type());
	}
	
	@Test
	void constructWithNullYear() {
		assertThrows(NullPointerException.class, () -> new SqlMakeDateFunction<>(null, SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullMonth() {
		assertThrows(NullPointerException.class, () -> new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullDay() {
		assertThrows(NullPointerException.class, () -> new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), null, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlMakeDateFunction<?> function = new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlMakeDateFunction<?> function = new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlMakeDateFunction<?> first = new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		SqlMakeDateFunction<?> second = new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithPostgresqlDialect() throws SqlException {
		SqlMakeDateFunction<?> function = new SqlMakeDateFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertTrue(function.toSql(SqlDialects.POSTGRESQL).sql().contains("MAKE_DATE("));
	}
}
