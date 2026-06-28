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

package net.luis.utils.io.database.function.functions.numeric;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlPiFunction}.<br>
 *
 * @author Luis-St
 */
class SqlPiFunctionTest {
	
	@Test
	void constructDefault() {
		SqlPiFunction function = new SqlPiFunction();
		assertEquals(SqlTypes.DOUBLE, function.type());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlPiFunction function = new SqlPiFunction();
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		assertFalse(new SqlPiFunction().requiresCast());
	}
	
	@Test
	void recordEqualityForNoArgInstances() {
		SqlPiFunction first = new SqlPiFunction();
		SqlPiFunction second = new SqlPiFunction();
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		assertEquals("PI()", new SqlPiFunction().toSql(SqlTestFixtures.DIALECT).sql());
	}
}
