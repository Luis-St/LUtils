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

import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDateInZoneFunction}.<br>
 *
 * @author Luis-St
 */
class SqlDateInZoneFunctionTest {
	
	@Test
	void constructWithValidArguments() {
		SqlExpression<String> expression = stringExpression();
		SqlExpression<String> zoneId = stringExpression();
		SqlDateInZoneFunction<LocalDate> function = new SqlDateInZoneFunction<>(expression, zoneId, SqlTypes.LOCAL_DATE);
		assertSame(expression, function.expression());
		assertSame(zoneId, function.zoneId());
		assertEquals(SqlTypes.LOCAL_DATE, function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlDateInZoneFunction<>(null, stringExpression(), SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullZoneId() {
		assertThrows(NullPointerException.class, () -> new SqlDateInZoneFunction<>(stringExpression(), null, SqlTypes.LOCAL_DATE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlDateInZoneFunction<>(stringExpression(), stringExpression(), null));
	}
	
	@Test
	void toSqlWithNullDialectThrows() {
		SqlDateInZoneFunction<LocalDate> function = new SqlDateInZoneFunction<>(stringExpression(), stringExpression(), SqlTypes.LOCAL_DATE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void toSqlWithDefaultDialectThrowsUnsupported() {
		SqlDateInZoneFunction<LocalDate> function = new SqlDateInZoneFunction<>(stringExpression(), stringExpression(), SqlTypes.LOCAL_DATE);
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> function.toSql(DIALECT));
	}
	
	@Test
	void requiresCastAlwaysTrue() {
		SqlDateInZoneFunction<LocalDate> function = new SqlDateInZoneFunction<>(stringExpression(), stringExpression(), SqlTypes.LOCAL_DATE);
		assertTrue(function.requiresCast());
	}
	
	@Test
	void componentsAreStoredVerbatim() {
		SqlExpression<String> expression = stringExpression();
		SqlExpression<String> zoneId = stringExpression();
		SqlDateInZoneFunction<LocalDate> function = new SqlDateInZoneFunction<>(expression, zoneId, SqlTypes.LOCAL_DATE);
		assertSame(expression, function.expression());
		assertSame(zoneId, function.zoneId());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlExpression<String> expression = stringExpression();
		SqlExpression<String> zoneId = stringExpression();
		SqlDateInZoneFunction<LocalDate> first = new SqlDateInZoneFunction<>(expression, zoneId, SqlTypes.LOCAL_DATE);
		SqlDateInZoneFunction<LocalDate> second = new SqlDateInZoneFunction<>(expression, zoneId, SqlTypes.LOCAL_DATE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithPostgresqlDialect() throws SqlException {
		SqlDateInZoneFunction<LocalDate> function = new SqlDateInZoneFunction<>(stringExpression(), stringExpression(), SqlTypes.LOCAL_DATE);
		String sql = function.toSql(SqlDialects.POSTGRESQL).sql();
		assertTrue(sql.contains("CAST("));
		assertTrue(sql.contains("AT"));
		assertTrue(sql.contains("TIME"));
		assertTrue(sql.contains("ZONE"));
		assertTrue(sql.contains("AS"));
		assertTrue(sql.contains("DATE"));
	}
	
	@Test
	void constructWithDifferentTemporalTypeParameterizations() {
		SqlDateInZoneFunction<LocalDate> dateFunction = new SqlDateInZoneFunction<>(stringExpression(), stringExpression(), SqlTypes.LOCAL_DATE);
		SqlDateInZoneFunction<java.time.Year> yearFunction = new SqlDateInZoneFunction<>(stringExpression(), stringExpression(), SqlTypes.YEAR);
		assertEquals(SqlTypes.LOCAL_DATE, dateFunction.type());
		assertEquals(SqlTypes.YEAR, yearFunction.type());
	}
}
