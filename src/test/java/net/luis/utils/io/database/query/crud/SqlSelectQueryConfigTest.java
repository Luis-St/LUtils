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
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.SqlOrderable;
import net.luis.utils.io.database.query.*;
import net.luis.utils.io.database.query.util.*;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSelectQueryConfig}.<br>
 *
 * @author Luis-St
 */
class SqlSelectQueryConfigTest {
	
	private static SqlSelectQueryConfig<Object> config() {
		return new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of());
	}
	
	@Test
	void constructWithConvenienceConstructor() {
		SqlSelectQueryConfig<Object> config = config();
		assertTrue(config.selectedExpressions().isEmpty());
		assertTrue(config.joins().isEmpty());
		assertTrue(config.groupByColumns().isEmpty());
		assertEquals(-1, config.limit());
		assertEquals(-1, config.offset());
		assertFalse(config.isDistinct());
		assertFalse(config.skipLocked());
		assertFalse(config.noWait());
		assertNull(config.whereCondition());
		assertNull(config.lockMode());
	}
	
	@Test
	void constructWithAllArguments() {
		List<SqlExpression<?>> selected = List.of(integerExpression());
		SqlSelectQueryConfig<Object> config = new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, selected, List.of(), List.of(), List.of(), List.of(), List.of(), 0, 10, false, null, null, null, null, false, false);
		assertEquals(selected, config.selectedExpressions());
		assertEquals(0, config.limit());
		assertEquals(10, config.offset());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of()));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null, List.of()));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null, List.of()));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null, List.of()));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null, List.of()));
	}
	
	@Test
	void constructWithNullSelectedExpressions() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null));
	}
	
	@Test
	void constructWithNullJoins() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, List.of(), List.of(), List.of(), List.of(), -1, -1, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithNullGroupByColumns() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), null, List.of(), List.of(), List.of(), -1, -1, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithNullOrderByClauses() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), null, List.of(), List.of(), -1, -1, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithNullSetOperations() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), List.of(), null, List.of(), -1, -1, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithNullCommonTableExpressions() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), List.of(), List.of(), null, -1, -1, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithLimitBelowNegativeOne() {
		assertThrows(IllegalArgumentException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), -2, -1, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithOffsetBelowNegativeOne() {
		assertThrows(IllegalArgumentException.class, () -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), -1, -2, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithLimitNegativeOneAllowed() {
		assertDoesNotThrow(() -> new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), -1, -1, false, null, null, null, null, false, false));
	}
	
	@Test
	void constructWithLimitZeroAllowed() {
		SqlSelectQueryConfig<Object> config = new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), 0, -1, false, null, null, null, null, false, false);
		assertEquals(0, config.limit());
	}
	
	@Test
	void constructWithOffsetNegativeOneAllowed() {
		SqlSelectQueryConfig<Object> config = new SqlSelectQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), -1, -1, false, null, null, null, null, false, false);
		assertEquals(-1, config.offset());
	}
	
	@Test
	void withJoinsReturnsCopy() {
		SqlSelectQueryConfig<Object> original = config();
		List<SqlJoinClause> joins = List.of(new SqlJoinClause(SqlJoinType.CROSS, sampleTable(), null));
		SqlSelectQueryConfig<Object> result = original.withJoins(joins);
		assertNotSame(original, result);
		assertEquals(joins, result.joins());
		assertSame(original.dialect(), result.dialect());
	}
	
	@Test
	void withGroupByColumnsReturnsCopy() {
		List<SqlColumn<?, ?>> columns = List.of(integerColumn());
		SqlSelectQueryConfig<Object> result = config().withGroupByColumns(columns);
		assertEquals(columns, result.groupByColumns());
	}
	
	@Test
	void withOrderByClausesReturnsCopy() {
		List<SqlOrderable<?>> orderables = List.of(integerExpression());
		SqlSelectQueryConfig<Object> result = config().withOrderByClauses(orderables);
		assertEquals(orderables, result.orderByClauses());
	}
	
	@Test
	void withSetOperationsReturnsCopy() {
		List<SqlSetOperationEntry<Object>> operations = List.of(new SqlSetOperationEntry<>(SqlSetOperation.UNION, sampleSelect()));
		SqlSelectQueryConfig<Object> result = config().withSetOperations(operations);
		assertEquals(operations, result.setOperations());
	}
	
	@Test
	void withCommonTableExpressionsReturnsCopy() {
		List<SqlCommonTableExpression> ctes = List.of(new SqlCommonTableExpression(SqlAlias.of("cte"), sampleSelect(), false));
		SqlSelectQueryConfig<Object> result = config().withCommonTableExpressions(ctes);
		assertEquals(ctes, result.commonTableExpressions());
	}
	
	@Test
	void withLimitReturnsCopy() {
		SqlSelectQueryConfig<Object> original = config();
		SqlSelectQueryConfig<Object> result = original.withLimit(5);
		assertEquals(5, result.limit());
		assertEquals(-1, original.limit());
	}
	
	@Test
	void withOffsetReturnsCopy() {
		SqlSelectQueryConfig<Object> result = config().withOffset(5);
		assertEquals(5, result.offset());
	}
	
	@Test
	void withDistinctSetsFlag() {
		SqlSelectQueryConfig<Object> result = config().withDistinct();
		assertTrue(result.isDistinct());
	}
	
	@Test
	void withWhereConditionReturnsCopy() {
		SqlCondition condition = alwaysCondition();
		SqlSelectQueryConfig<Object> result = config().withWhereCondition(condition);
		assertSame(condition, result.whereCondition());
	}
	
	@Test
	void withWhereExistsSubqueryReturnsCopy() {
		SqlSelectQueryConfig<Object> result = config().withWhereExistsSubquery(sampleSelect());
		assertNotNull(result.whereExistsSubquery());
	}
	
	@Test
	void withHavingConditionReturnsCopy() {
		SqlCondition condition = alwaysCondition();
		SqlSelectQueryConfig<Object> result = config().withHavingCondition(condition);
		assertSame(condition, result.havingCondition());
	}
	
	@Test
	void withLockModeReturnsCopy() {
		SqlSelectQueryConfig<Object> result = config().withLockMode(SqlLockMode.FOR_UPDATE);
		assertEquals(SqlLockMode.FOR_UPDATE, result.lockMode());
	}
	
	@Test
	void withSkipLockedSetsFlag() {
		SqlSelectQueryConfig<Object> result = config().withSkipLocked();
		assertTrue(result.skipLocked());
	}
	
	@Test
	void withNoWaitSetsFlag() {
		SqlSelectQueryConfig<Object> result = config().withNoWait();
		assertTrue(result.noWait());
	}
	
	@Test
	void withWhereConditionAcceptsNull() {
		SqlSelectQueryConfig<Object> result = assertDoesNotThrow(() -> config().withWhereCondition(null));
		assertNull(result.whereCondition());
	}
	
	@Test
	void withLockModeAcceptsNull() {
		SqlSelectQueryConfig<Object> result = assertDoesNotThrow(() -> config().withLockMode(null));
		assertNull(result.lockMode());
	}
}
