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

package net.luis.utils.io.database.function.functions;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.function.functions.window.SqlRowNumberFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlWindowFunction}.<br>
 *
 * @author Luis-St
 */
class SqlWindowFunctionTest {
	
	@Test
	void toSqlWithNullDialect() {
		SqlWindowFunction<Integer> function = () -> SqlTestFixtures.INTEGER_TYPE;
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsToFalse() {
		SqlWindowFunction<Integer> function = () -> SqlTestFixtures.INTEGER_TYPE;
		assertFalse(function.requiresCast());
	}
	
	@Test
	void toSqlRendersConcreteWindowFunctionWithDefaultDialect() throws SqlException {
		String sql = new SqlRowNumberFunction<>(SqlWindowClause.of(), SqlTestFixtures.INTEGER_TYPE).toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("ROW_NUMBER("));
		assertTrue(sql.contains("OVER("));
	}
}
