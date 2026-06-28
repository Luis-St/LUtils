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
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNowFunction}.<br>
 *
 * @author Luis-St
 */
class SqlNowFunctionTest {
	
	@Test
	void constructWithType() {
		SqlNowFunction<?> function = new SqlNowFunction<>(SqlTypes.LOCAL_DATE);
		assertEquals(SqlTypes.LOCAL_DATE, function.type());
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlNowFunction<>(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlNowFunction<?> function = new SqlNowFunction<>(SqlTypes.LOCAL_DATE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		assertFalse(new SqlNowFunction<>(SqlTypes.LOCAL_DATE).requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlNowFunction<?> first = new SqlNowFunction<>(SqlTypes.LOCAL_DATE);
		SqlNowFunction<?> second = new SqlNowFunction<>(SqlTypes.LOCAL_DATE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		assertEquals("CURRENT_TIMESTAMP", new SqlNowFunction<>(SqlTypes.LOCAL_DATE).toSql(SqlTestFixtures.DIALECT).sql());
	}
}
