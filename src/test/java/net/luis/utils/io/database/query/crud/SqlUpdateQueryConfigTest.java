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
import net.luis.utils.io.database.query.util.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlUpdateQueryConfig}.<br>
 *
 * @author Luis-St
 */
class SqlUpdateQueryConfigTest {
	
	private static SqlUpdateQueryConfig<Object> config() {
		return new SqlUpdateQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null);
	}
	
	private static List<SqlSetClause<Object, ?>> setClauses() {
		return List.of(new SqlSetClause<>(integerColumn(), integerExpression(), SqlSetType.EXPRESSION));
	}
	
	@Test
	void constructWithConvenienceConstructor() {
		SqlUpdateQueryConfig<Object> config = config();
		assertTrue(config.setClauses().isEmpty());
		assertTrue(config.joins().isEmpty());
		assertNull(config.whereCondition());
		assertFalse(config.allowAll());
	}
	
	@Test
	void constructWithAllArguments() {
		List<SqlSetClause<Object, ?>> clauses = setClauses();
		SqlCondition condition = alwaysCondition();
		SqlUpdateQueryConfig<Object> config = new SqlUpdateQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, clauses, List.of(), condition, true);
		assertEquals(clauses, config.setClauses());
		assertSame(condition, config.whereCondition());
		assertTrue(config.allowAll());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQueryConfig<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQueryConfig<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQueryConfig<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQueryConfig<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null));
	}
	
	@Test
	void constructWithNullSetClauses() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of(), null, false));
	}
	
	@Test
	void constructWithNullJoins() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, null, false));
	}
	
	@Test
	void withSetClausesReturnsCopy() {
		SqlUpdateQueryConfig<Object> original = config();
		List<SqlSetClause<Object, ?>> clauses = setClauses();
		SqlUpdateQueryConfig<Object> result = original.withSetClauses(clauses);
		assertNotSame(original, result);
		assertEquals(clauses, result.setClauses());
		assertTrue(original.setClauses().isEmpty());
	}
	
	@Test
	void withJoinsReturnsCopy() {
		SqlUpdateQueryConfig<Object> original = config();
		List<SqlJoinClause> joins = List.of(new SqlJoinClause(SqlJoinType.CROSS, sampleTable(), null));
		SqlUpdateQueryConfig<Object> result = original.withJoins(joins);
		assertEquals(joins, result.joins());
		assertSame(original.dialect(), result.dialect());
	}
	
	@Test
	void withWhereConditionReturnsCopy() {
		SqlUpdateQueryConfig<Object> original = config();
		SqlCondition condition = alwaysCondition();
		SqlUpdateQueryConfig<Object> result = original.withWhereCondition(condition);
		assertSame(condition, result.whereCondition());
		assertNull(original.whereCondition());
	}
	
	@Test
	void withWhereConditionAcceptsNull() {
		SqlUpdateQueryConfig<Object> result = assertDoesNotThrow(() -> config().withWhereCondition(null));
		assertNull(result.whereCondition());
	}
	
	@Test
	void withAllowAllSetsFlag() {
		SqlUpdateQueryConfig<Object> original = config();
		SqlUpdateQueryConfig<Object> result = original.withAllowAll();
		assertTrue(result.allowAll());
		assertFalse(original.allowAll());
	}
}
