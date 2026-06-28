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

package net.luis.utils.io.database.function.window;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.orderable.SqlOrderable;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlWindowClause}.<br>
 *
 * @author Luis-St
 */
class SqlWindowClauseTest {
	
	private static SqlWindowFrame sampleFrame() {
		return SqlWindowFrame.rows(SqlFrameBound.unboundedPreceding(), SqlFrameBound.currentRow());
	}
	
	@Test
	void constructCanonicalWithComponents() {
		List<SqlColumn<?, ?>> partitions = List.of(SqlTestFixtures.integerColumn());
		List<SqlOrderable<?>> orderings = List.of(SqlTestFixtures.integerExpression());
		SqlWindowFrame frame = sampleFrame();
		SqlWindowClause clause = new SqlWindowClause(partitions, orderings, frame);
		assertEquals(partitions, clause.partitions());
		assertEquals(orderings, clause.orderings());
		assertSame(frame, clause.frame());
	}
	
	@Test
	void ofCreatesEmptyClause() {
		SqlWindowClause clause = SqlWindowClause.of();
		assertTrue(clause.partitions().isEmpty());
		assertTrue(clause.orderings().isEmpty());
		assertNull(clause.frame());
	}
	
	@Test
	void partitionByWithNullArray() {
		assertThrows(NullPointerException.class, () -> SqlWindowClause.partitionBy((SqlColumn<?, ?>[]) null));
	}
	
	@Test
	void partitionByWithNullColumnElement() {
		assertThrows(NullPointerException.class, () -> SqlWindowClause.partitionBy(SqlTestFixtures.integerColumn(), null));
	}
	
	@Test
	void orderByWithNullArray() {
		assertThrows(NullPointerException.class, () -> SqlWindowClause.of().orderBy((SqlOrderable<?>[]) null));
	}
	
	@Test
	void orderByWithNullOrderableElement() {
		assertThrows(NullPointerException.class, () -> SqlWindowClause.of().orderBy(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void frameWithNullFrame() {
		assertThrows(NullPointerException.class, () -> SqlWindowClause.of().frame(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlWindowClause.of().toSql(null));
	}
	
	@Test
	void partitionByStoresColumns() {
		SqlWindowClause clause = SqlWindowClause.partitionBy(SqlTestFixtures.integerColumn());
		assertEquals(1, clause.partitions().size());
		assertTrue(clause.orderings().isEmpty());
		assertNull(clause.frame());
	}
	
	@Test
	void orderByPreservesPartitionsAndFrame() {
		SqlWindowClause base = SqlWindowClause.partitionBy(SqlTestFixtures.integerColumn()).frame(sampleFrame());
		SqlWindowClause clause = base.orderBy(SqlTestFixtures.integerExpression());
		assertEquals(1, clause.partitions().size());
		assertEquals(1, clause.orderings().size());
		assertNotNull(clause.frame());
	}
	
	@Test
	void framePreservesPartitionsAndOrderings() {
		SqlWindowClause base = SqlWindowClause.partitionBy(SqlTestFixtures.integerColumn()).orderBy(SqlTestFixtures.integerExpression());
		SqlWindowClause clause = base.frame(sampleFrame());
		assertEquals(1, clause.partitions().size());
		assertEquals(1, clause.orderings().size());
		assertNotNull(clause.frame());
	}
	
	@Test
	void partitionByWithEmptyVarargs() {
		SqlWindowClause clause = SqlWindowClause.partitionBy();
		assertTrue(clause.partitions().isEmpty());
	}
	
	@Test
	void orderByWithEmptyVarargs() {
		SqlWindowClause clause = SqlWindowClause.of().orderBy();
		assertTrue(clause.orderings().isEmpty());
	}
	
	@Test
	void builderChainPartitionOrderFrame() {
		SqlWindowClause partitioned = SqlWindowClause.partitionBy(SqlTestFixtures.integerColumn());
		SqlWindowClause ordered = partitioned.orderBy(SqlTestFixtures.integerExpression());
		SqlWindowClause framed = ordered.frame(sampleFrame());
		assertEquals(1, framed.partitions().size());
		assertEquals(1, framed.orderings().size());
		assertNotNull(framed.frame());
		assertTrue(partitioned.orderings().isEmpty());
		assertNull(partitioned.frame());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlWindowClause first = SqlWindowClause.of();
		SqlWindowClause second = SqlWindowClause.of();
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlWindowClause clause = SqlWindowClause.partitionBy(SqlTestFixtures.integerColumn());
		String sql = clause.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("PARTITION BY"));
		assertTrue(sql.contains("\"id\""));
	}
}
