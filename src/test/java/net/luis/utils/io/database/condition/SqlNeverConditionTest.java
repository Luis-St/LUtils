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

package net.luis.utils.io.database.condition;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNeverCondition}.<br>
 *
 * @author Luis-St
 */
class SqlNeverConditionTest {
	
	@Test
	void constructNever() {
		assertNotNull(new SqlNeverCondition());
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlNeverCondition().toSql(null));
	}
	
	@Test
	void notReturnsAlwaysCondition() {
		assertInstanceOf(SqlAlwaysCondition.class, new SqlNeverCondition().not());
	}
	
	@Test
	void toSqlRendersFalse() throws SqlException {
		assertEquals("FALSE", new SqlNeverCondition().toSql(SqlTestFixtures.DIALECT).sql());
	}
	
	@Test
	void equalsAndHashCode() {
		SqlNeverCondition first = new SqlNeverCondition();
		SqlNeverCondition second = new SqlNeverCondition();
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
