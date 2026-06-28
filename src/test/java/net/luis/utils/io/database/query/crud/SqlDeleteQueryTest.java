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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDeleteQuery}.<br>
 *
 * @author Luis-St
 */
class SqlDeleteQueryTest {
	
	private static final SqlDialect JOINED_DML_DIALECT = SqlDialects.MYSQL;
	
	private static SqlDeleteQuery<Object> delete() {
		return new SqlDeleteQuery<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null);
	}
	
	@Test
	void constructWithValidArguments() throws SqlException {
		SqlDeleteQuery<Object> query = delete();
		assertNotNull(query);
		assertTrue(query.allowAll().toSql(DIALECT).sql().contains("DELETE FROM"));
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQuery<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQuery<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQuery<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQuery<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQuery<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SqlDeleteQuery<>(null));
	}
	
	@Test
	void whereWithNullCondition() {
		assertThrows(NullPointerException.class, () -> delete().where(null));
	}
	
	@Test
	void executeWithoutWhereOrAllowAll() {
		assertThrows(SqlStatementBuilderException.class, () -> delete().execute());
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> delete().toSql(null));
	}
	
	@Test
	void toSqlWithJoinsOnDialectWithoutJoinedDml() {
		SqlDeleteQuery<Object> query = delete().crossJoin(sampleTable());
		assertThrows(SqlDialectFeatureException.class, () -> query.toSql(DIALECT));
	}
	
	@Test
	void innerJoinAppendsClause() throws SqlException {
		SqlDeleteQuery<Object> query = delete().innerJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("INNER JOIN"));
	}
	
	@Test
	void leftJoinAppendsClause() throws SqlException {
		SqlDeleteQuery<Object> query = delete().leftJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("LEFT JOIN"));
	}
	
	@Test
	void rightJoinAppendsClause() throws SqlException {
		SqlDeleteQuery<Object> query = delete().rightJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("RIGHT JOIN"));
	}
	
	@Test
	void fullJoinAppendsClause() throws SqlException {
		SqlDeleteQuery<Object> query = delete().fullJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("FULL OUTER JOIN"));
	}
	
	@Test
	void crossJoinAppendsClause() throws SqlException {
		SqlDeleteQuery<Object> query = delete().crossJoin(sampleTable());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("CROSS JOIN"));
	}
	
	@Test
	void joinReturnsNewInstance() throws SqlException {
		SqlDeleteQuery<Object> original = delete();
		SqlDeleteQuery<Object> joined = original.innerJoin(sampleTable(), alwaysCondition());
		assertNotSame(original, joined);
		assertFalse(original.allowAll().toSql(DIALECT).sql().contains("JOIN"));
	}
	
	@Test
	void whereWithNoExistingCondition() throws SqlException {
		SqlDeleteQuery<Object> query = delete().where(alwaysCondition());
		assertTrue(query.toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void whereCombinesWithExistingCondition() throws SqlException {
		SqlDeleteQuery<Object> query = delete().where(alwaysCondition()).where(neverCondition());
		assertTrue(query.toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void toSqlWithoutWhere() throws SqlException {
		String sql = delete().allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("DELETE FROM"));
		assertFalse(sql.contains("WHERE"));
	}
	
	@Test
	void toSqlWithWhere() throws SqlException {
		assertTrue(delete().where(alwaysCondition()).toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void toSqlWithJoins() throws SqlException {
		SqlDeleteQuery<Object> query = delete().innerJoin(sampleTable(), alwaysCondition());
		String sql = query.toSql(JOINED_DML_DIALECT).sql();
		assertTrue(sql.contains("DELETE FROM"));
		assertTrue(sql.contains("INNER JOIN"));
	}
	
	@Test
	void executeWithAllowAllPassesGuard() {
		SqlException exception = assertThrows(SqlException.class, () -> delete().allowAll().execute());
		assertFalse(exception instanceof SqlStatementBuilderException);
	}
	
	@Test
	void executeWithWhereConditionPassesGuard() {
		SqlException exception = assertThrows(SqlException.class, () -> delete().where(alwaysCondition()).execute());
		assertFalse(exception instanceof SqlStatementBuilderException);
	}
	
	@Test
	void allowAllEnablesDeleteAllToSql() throws SqlException {
		SqlDeleteQuery<Object> original = delete();
		SqlDeleteQuery<Object> all = original.allowAll();
		assertNotSame(original, all);
		assertTrue(all.toSql(DIALECT).sql().contains("DELETE FROM"));
	}
}
