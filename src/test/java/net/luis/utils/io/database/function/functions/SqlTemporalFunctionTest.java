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
import net.luis.utils.io.database.function.functions.temporal.SqlCurrentDateFunction;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTemporalFunction}.<br>
 *
 * @author Luis-St
 */
class SqlTemporalFunctionTest {
	
	@Test
	void toSqlWithNullDialect() {
		SqlTemporalFunction<LocalDate> function = () -> SqlTypes.LOCAL_DATE;
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsToFalse() {
		SqlTemporalFunction<LocalDate> function = () -> SqlTypes.LOCAL_DATE;
		assertFalse(function.requiresCast());
	}
	
	@Test
	void toSqlRendersConcreteTemporalFunctionWithDefaultDialect() throws SqlException {
		assertEquals("CURRENT_DATE", new SqlCurrentDateFunction<>(SqlTypes.LOCAL_DATE).toSql(SqlTestFixtures.DIALECT).sql());
	}
}
