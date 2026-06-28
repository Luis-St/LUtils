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
 * Test class for {@link SqlAlwaysCondition}.<br>
 *
 * @author Luis-St
 */
class SqlAlwaysConditionTest {
	
	@Test
	void constructAlways() {
		assertNotNull(new SqlAlwaysCondition());
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlAlwaysCondition().toSql(null));
	}
	
	@Test
	void notReturnsNeverCondition() {
		assertInstanceOf(SqlNeverCondition.class, new SqlAlwaysCondition().not());
	}
	
	@Test
	void toSqlRendersTrue() throws SqlException {
		assertEquals("TRUE", new SqlAlwaysCondition().toSql(SqlTestFixtures.DIALECT).sql());
	}
	
	@Test
	void equalsAndHashCode() {
		SqlAlwaysCondition first = new SqlAlwaysCondition();
		SqlAlwaysCondition second = new SqlAlwaysCondition();
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
