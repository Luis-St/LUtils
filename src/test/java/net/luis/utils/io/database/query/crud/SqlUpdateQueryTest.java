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
 * Test class for {@link SqlUpdateQuery}.<br>
 *
 * @author Luis-St
 */
class SqlUpdateQueryTest {
	
	private static final SqlDialect JOINED_DML_DIALECT = SqlDialects.MYSQL;
	
	private static SqlUpdateQuery<Object> update() {
		return new SqlUpdateQuery<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null);
	}
	
	@Test
	void constructWithValidArguments() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).allowAll();
		assertNotNull(query);
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("UPDATE"));
		assertTrue(sql.contains("SET"));
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQuery<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQuery<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQuery<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQuery<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQuery<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SqlUpdateQuery<>(null));
	}
	
	@Test
	void setNullWithNullColumn() {
		assertThrows(NullPointerException.class, () -> update().setNull(null));
	}
	
	@Test
	void setValueWithNullColumn() {
		assertThrows(NullPointerException.class, () -> update().set(null, 5));
	}
	
	@Test
	void incrementValueWithNullColumn() {
		assertThrows(NullPointerException.class, () -> update().increment(null, 5));
	}
	
	@Test
	void decrementValueWithNullColumn() {
		assertThrows(NullPointerException.class, () -> update().decrement(null, 5));
	}
	
	@Test
	void whereWithNullCondition() {
		assertThrows(NullPointerException.class, () -> update().where(null));
	}
	
	@Test
	void executeWithoutWhereOrAllowAll() {
		assertThrows(SqlStatementBuilderException.class, () -> update().set(integerColumn(), 5).execute());
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> update().set(integerColumn(), 5).toSql(null));
	}
	
	@Test
	void toSqlWithoutSetClauses() {
		assertThrows(SqlStatementBuilderException.class, () -> update().toSql(DIALECT));
	}
	
	@Test
	void toSqlWithJoinsOnDialectWithoutJoinedDml() {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).crossJoin(sampleTable());
		assertThrows(SqlDialectFeatureException.class, () -> query.toSql(DIALECT));
	}
	
	@Test
	void innerJoinAppendsClause() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).innerJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("INNER JOIN"));
	}
	
	@Test
	void leftJoinAppendsClause() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).leftJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("LEFT JOIN"));
	}
	
	@Test
	void rightJoinAppendsClause() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).rightJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("RIGHT JOIN"));
	}
	
	@Test
	void fullJoinAppendsClause() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).fullJoin(sampleTable(), alwaysCondition());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("FULL OUTER JOIN"));
	}
	
	@Test
	void crossJoinAppendsClause() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).crossJoin(sampleTable());
		assertTrue(query.toSql(JOINED_DML_DIALECT).sql().contains("CROSS JOIN"));
	}
	
	@Test
	void setNullAddsNullClause() throws SqlException {
		String sql = update().setNull(integerColumn()).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("NULL"));
	}
	
	@Test
	void setValueWithNonNullDelegatesToExpression() throws SqlException {
		String sql = update().set(integerColumn(), 5).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("?"));
	}
	
	@Test
	void setValueWithNullDelegatesToSetNull() throws SqlException {
		String sql = update().set(integerColumn(), (Integer) null).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("NULL"));
	}
	
	@Test
	void setExpressionAddsExpressionClause() throws SqlException {
		String sql = update().set(integerColumn(), integerExpression()).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("SET"));
		assertTrue(sql.contains("?"));
	}
	
	@Test
	void incrementWithExpressionAddsIncrementClause() throws SqlException {
		String sql = update().increment(integerColumn(), integerExpression()).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("+"));
	}
	
	@Test
	void incrementWithValueWrapsLiteral() throws SqlException {
		String sql = update().increment(integerColumn(), 5).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("+"));
		assertTrue(sql.contains("?"));
	}
	
	@Test
	void decrementWithExpressionAddsDecrementClause() throws SqlException {
		String sql = update().decrement(integerColumn(), integerExpression()).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("-"));
	}
	
	@Test
	void decrementWithValueWrapsLiteral() throws SqlException {
		String sql = update().decrement(integerColumn(), 5).allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains("-"));
		assertTrue(sql.contains("?"));
	}
	
	@Test
	void whereWithNoExistingCondition() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).where(alwaysCondition());
		assertTrue(query.toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void whereCombinesWithExistingCondition() throws SqlException {
		SqlUpdateQuery<Object> query = update().set(integerColumn(), 5).where(alwaysCondition()).where(neverCondition());
		assertTrue(query.toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void toSqlWithoutWhere() throws SqlException {
		String sql = update().set(integerColumn(), 5).allowAll().toSql(DIALECT).sql();
		assertFalse(sql.contains("WHERE"));
	}
	
	@Test
	void toSqlWithWhere() throws SqlException {
		assertTrue(update().set(integerColumn(), 5).where(alwaysCondition()).toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void executeWithSetClauseAndAllowAllPassesGuard() {
		SqlException exception = assertThrows(SqlException.class, () -> update().set(integerColumn(), 5).allowAll().execute());
		assertFalse(exception instanceof SqlStatementBuilderException);
	}
	
	@Test
	void executeWithSetClauseAndWhereConditionPassesGuard() {
		SqlException exception = assertThrows(SqlException.class, () -> update().set(integerColumn(), 5).where(alwaysCondition()).execute());
		assertFalse(exception instanceof SqlStatementBuilderException);
	}
	
	@Test
	void multipleSetClausesRenderCommaSeparated() throws SqlException {
		String sql = update().set(integerColumn(), 1).set(stringColumn(), "x").allowAll().toSql(DIALECT).sql();
		assertTrue(sql.contains(","));
	}
	
	@Test
	void chainedBuildersAreImmutable() {
		SqlUpdateQuery<Object> base = update();
		SqlUpdateQuery<Object> withSet = base.set(integerColumn(), 5);
		SqlUpdateQuery<Object> withWhere = base.where(alwaysCondition());
		assertNotSame(base, withSet);
		assertNotSame(base, withWhere);
		assertNotSame(withSet, withWhere);
		assertThrows(SqlStatementBuilderException.class, () -> base.toSql(DIALECT));
	}
}
