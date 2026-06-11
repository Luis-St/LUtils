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
 * Test class for {@link SqlMakeTimeFunction}.<br>
 *
 * @author Luis-St
 */
class SqlMakeTimeFunctionTest {
	
	@Test
	void constructWithHourMinuteSecondAndType() {
		SqlExpression<Integer> hour = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> minute = SqlTestFixtures.integerExpression();
		SqlExpression<Integer> second = SqlTestFixtures.integerExpression();
		SqlMakeTimeFunction<?> function = new SqlMakeTimeFunction<>(hour, minute, second, SqlTypes.LOCAL_DATE);
		assertSame(hour, function.hour());
		assertSame(minute, function.minute());
		assertSame(second, function.second());
		assertEquals(SqlTypes.LOCAL_DATE, function.type());
	}
	
	@Test
	void constructWithNullHour() {
		assertThrows(NullPointerException.class, () -> new SqlMakeTimeFunction<>(null, SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullMinute() {
		assertThrows(NullPointerException.class, () -> new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), null, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlMakeTimeFunction<?> function = new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlMakeTimeFunction<?> function = new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlMakeTimeFunction<?> first = new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		SqlMakeTimeFunction<?> second = new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithPostgresqlDialect() throws SqlException {
		SqlMakeTimeFunction<?> function = new SqlMakeTimeFunction<>(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), SqlTypes.LOCAL_DATE);
		assertTrue(function.toSql(SqlDialects.POSTGRESQL).sql().contains("MAKE_TIME("));
	}
}
