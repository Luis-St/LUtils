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

package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.query.util.SqlJoinClause;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDeleteQueryConfig}.<br>
 *
 * @author Luis-St
 */
class SqlDeleteQueryConfigTest {
	
	private static SqlDeleteQueryConfig<Object> config() {
		return new SqlDeleteQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, false);
	}
	
	@Test
	void constructWithValidArguments() {
		SqlDeleteQueryConfig<Object> config = config();
		assertNotNull(config.table());
		assertSame(DIALECT, config.dialect());
		assertTrue(config.joins().isEmpty());
		assertNull(config.whereCondition());
		assertFalse(config.allowAll());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQueryConfig<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, false));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQueryConfig<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, false));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQueryConfig<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null, List.of(), null, false));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQueryConfig<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null, List.of(), null, false));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null, List.of(), null, false));
	}
	
	@Test
	void constructWithNullJoins() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, null, false));
	}
	
	@Test
	void withJoinsReturnsCopyWithNewJoins() {
		SqlDeleteQueryConfig<Object> original = config();
		List<SqlJoinClause> joins = List.of(new SqlJoinClause(net.luis.utils.io.database.query.util.SqlJoinType.CROSS, sampleTable(), null));
		SqlDeleteQueryConfig<Object> result = original.withJoins(joins);
		assertNotSame(original, result);
		assertEquals(joins, result.joins());
		assertSame(original.table(), result.table());
	}
	
	@Test
	void withWhereConditionReturnsCopy() {
		SqlDeleteQueryConfig<Object> original = config();
		SqlCondition condition = alwaysCondition();
		SqlDeleteQueryConfig<Object> result = original.withWhereCondition(condition);
		assertSame(condition, result.whereCondition());
		assertNull(original.whereCondition());
	}
	
	@Test
	void withWhereConditionAcceptsNull() {
		SqlDeleteQueryConfig<Object> result = assertDoesNotThrow(() -> config().withWhereCondition(null));
		assertNull(result.whereCondition());
	}
	
	@Test
	void withAllowAllSetsFlag() {
		SqlDeleteQueryConfig<Object> original = config();
		SqlDeleteQueryConfig<Object> result = original.withAllowAll();
		assertTrue(result.allowAll());
		assertFalse(original.allowAll());
	}
}
