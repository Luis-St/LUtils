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

package net.luis.utils.io.database.query.util;

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlJoinClause}.<br>
 *
 * @author Luis-St
 */
class SqlJoinClauseTest {
	
	@Test
	void constructTableJoinWithCondition() {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.INNER, sampleTable(), alwaysCondition());
		assertEquals(SqlJoinType.INNER, clause.type());
		assertNotNull(clause.table());
		assertNotNull(clause.on());
		assertFalse(clause.isLateral());
	}
	
	@Test
	void constructCrossJoinWithNullCondition() {
		SqlJoinClause clause = assertDoesNotThrow(() -> new SqlJoinClause(SqlJoinType.CROSS, sampleTable(), null));
		assertEquals(SqlJoinType.CROSS, clause.type());
		assertNull(clause.on());
	}
	
	@Test
	void constructLateralJoinTwoArg() {
		SqlSelectQuery<Object> subquery = sampleSelect();
		SqlJoinClause clause = new SqlJoinClause(subquery, SqlAlias.of("sub"));
		assertTrue(clause.isLateral());
		assertEquals(SqlJoinType.CROSS, clause.type());
		assertSame(subquery, clause.lateralSubquery());
		assertNotNull(clause.lateralAlias());
		assertNull(clause.table());
		assertNull(clause.on());
	}
	
	@Test
	void constructLateralJoinThreeArg() {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.LEFT, sampleSelect(), SqlAlias.of("sub"));
		assertEquals(SqlJoinType.LEFT, clause.type());
		assertTrue(clause.isLateral());
	}
	
	@Test
	void constructTableJoinWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlJoinClause(null, sampleTable(), alwaysCondition()));
	}
	
	@Test
	void constructTableJoinWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlJoinClause(SqlJoinType.INNER, null, alwaysCondition()));
	}
	
	@Test
	void constructNonCrossJoinWithNullCondition() {
		assertThrows(NullPointerException.class, () -> new SqlJoinClause(SqlJoinType.INNER, sampleTable(), null));
	}
	
	@Test
	void constructLateralJoinWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlJoinClause(null, sampleSelect(), SqlAlias.of("sub")));
	}
	
	@Test
	void constructLateralJoinWithNullSubquery() {
		assertThrows(NullPointerException.class, () -> new SqlJoinClause(SqlJoinType.LEFT, null, SqlAlias.of("sub")));
	}
	
	@Test
	void constructLateralJoinWithNullAlias() {
		assertThrows(NullPointerException.class, () -> new SqlJoinClause(SqlJoinType.LEFT, sampleSelect(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.INNER, sampleTable(), alwaysCondition());
		assertThrows(NullPointerException.class, () -> clause.toSql(null));
	}
	
	@Test
	void isLateralTrueForLateralClause() {
		SqlJoinClause clause = new SqlJoinClause(sampleSelect(), SqlAlias.of("sub"));
		assertTrue(clause.isLateral());
	}
	
	@Test
	void isLateralFalseForTableClause() {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.INNER, sampleTable(), alwaysCondition());
		assertFalse(clause.isLateral());
	}
	
	@Test
	void toSqlInnerJoin() throws SqlException {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.INNER, sampleTable(), alwaysCondition());
		String sql = clause.toSql(DIALECT).sql();
		assertTrue(sql.contains("INNER JOIN"));
		assertTrue(sql.contains("ON"));
	}
	
	@Test
	void toSqlLeftJoin() throws SqlException {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.LEFT, sampleTable(), alwaysCondition());
		assertTrue(clause.toSql(DIALECT).sql().contains("LEFT JOIN"));
	}
	
	@Test
	void toSqlRightJoin() throws SqlException {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.RIGHT, sampleTable(), alwaysCondition());
		assertTrue(clause.toSql(DIALECT).sql().contains("RIGHT JOIN"));
	}
	
	@Test
	void toSqlFullJoin() throws SqlException {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.FULL, sampleTable(), alwaysCondition());
		assertTrue(clause.toSql(DIALECT).sql().contains("FULL OUTER JOIN"));
	}
	
	@Test
	void toSqlCrossJoinWithoutCondition() throws SqlException {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.CROSS, sampleTable(), null);
		String sql = clause.toSql(DIALECT).sql();
		assertTrue(sql.contains("CROSS JOIN"));
		assertFalse(sql.contains(" ON "));
	}
	
	@Test
	void toSqlLateralJoin() throws SqlException {
		SqlJoinClause clause = new SqlJoinClause(SqlJoinType.CROSS, sampleSelect(), SqlAlias.of("sub"));
		String sql = clause.toSql(DIALECT).sql();
		assertTrue(sql.contains("LATERAL"));
		assertTrue(sql.contains("AS"));
	}
}
