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
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.query.SqlCommonTableExpression;
import net.luis.utils.io.database.query.util.SqlJoinType;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSelectQuery}.<br>
 *
 * @author Luis-St
 */
class SqlSelectQueryTest {
	
	private static final SqlDialect PG = SqlDialects.POSTGRESQL;
	private static final SqlDialect H2 = SqlDialects.H2;
	
	private static SqlTable<Object> table() {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		table.column("id", INTEGER_TYPE, object -> 0);
		table.column("name", STRING_TYPE, object -> "x");
		return table;
	}
	
	private static SqlSelectQuery<Object> select() {
		return new SqlSelectQuery<>(table(), DIALECT, SOURCE, TIMEOUT, resultSet -> null);
	}
	
	private static SqlSelectQuery<Object> selectOn(SqlDialect dialect) {
		return new SqlSelectQuery<>(table(), dialect, SOURCE, TIMEOUT, resultSet -> null);
	}
	
	private static SqlSelectQuery<Object> selectExpr(SqlExpression<?>... expressions) {
		return new SqlSelectQuery<>(table(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(expressions));
	}
	
	private static long markerCount(String sql) {
		return sql.chars().filter(character -> character == '?').count();
	}
	
	@Test
	void constructFullEntitySelect() throws SqlException {
		SqlSelectQuery<Object> query = select();
		assertNotNull(query);
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("SELECT"));
		assertTrue(sql.contains("FROM"));
	}
	
	@Test
	void constructProjectionSelectWithExpressions() throws SqlException {
		SqlSelectQuery<Object> query = selectExpr(integerExpression());
		assertEquals(1, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQuery<>(null));
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQuery<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQuery<>(table(), null, SOURCE, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQuery<>(table(), DIALECT, null, TIMEOUT, resultSet -> null));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQuery<>(table(), DIALECT, SOURCE, null, resultSet -> null));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQuery<>(table(), DIALECT, SOURCE, TIMEOUT, null));
	}
	
	@Test
	void constructWithNullSelectedExpressions() {
		assertThrows(NullPointerException.class, () -> new SqlSelectQuery<>(table(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null));
	}
	
	@Test
	void whereWithNullCondition() {
		assertThrows(NullPointerException.class, () -> select().where(null));
	}
	
	@Test
	void whereExistsWithNullSubquery() {
		assertThrows(NullPointerException.class, () -> select().whereExists(null));
	}
	
	@Test
	void havingWithNullCondition() {
		assertThrows(NullPointerException.class, () -> select().having(null));
	}
	
	@Test
	void groupByWithNullColumns() {
		assertThrows(NullPointerException.class, () -> select().groupBy((SqlColumn<?, ?>[]) null));
	}
	
	@Test
	void orderByWithNullOrderables() {
		assertThrows(NullPointerException.class, () -> select().orderBy((net.luis.utils.io.database.expression.orderable.SqlOrderable<?>[]) null));
	}
	
	@Test
	void withWithNullCte() {
		assertThrows(NullPointerException.class, () -> select().with(null));
	}
	
	@Test
	void projectIntoWithNullType() {
		assertThrows(NullPointerException.class, () -> selectExpr(integerExpression()).projectInto(null));
	}
	
	@Test
	void limitWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> select().limit(-1));
	}
	
	@Test
	void offsetWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> select().offset(-1));
	}
	
	@Test
	void forUpdateOnUnsupportedDialect() {
		assertThrows(SqlDialectFeatureException.class, () -> select().forUpdate());
	}
	
	@Test
	void forShareOnUnsupportedDialect() {
		assertThrows(SqlDialectFeatureException.class, () -> select().forShare());
	}
	
	@Test
	void skipLockedWithoutLockMode() {
		assertThrows(SqlStatementBuilderException.class, () -> selectOn(PG).skipLocked());
	}
	
	@Test
	void skipLockedOnUnsupportedDialect() {
		assertThrows(SqlDialectFeatureException.class, () -> selectOn(H2).forUpdate().skipLocked());
	}
	
	@Test
	void noWaitWithoutLockMode() {
		assertThrows(SqlStatementBuilderException.class, () -> selectOn(PG).noWait());
	}
	
	@Test
	void noWaitOnUnsupportedDialect() {
		assertThrows(SqlDialectFeatureException.class, () -> selectOn(H2).forUpdate().noWait());
	}
	
	@Test
	void lateralJoinOnUnsupportedDialect() {
		assertThrows(SqlDialectFeatureException.class, () -> select().lateralJoin(sampleSelect(), SqlAlias.of("x")));
	}
	
	@Test
	void projectIntoWithoutSelectedExpressions() {
		assertThrows(SqlStatementBuilderException.class, () -> select().projectInto(Projection.class));
	}
	
	@Test
	void withAuditOnNonAuditedTable() {
		assertThrows(SqlStatementBuilderException.class, () -> select().withAudit());
	}
	
	@Test
	void withAuditOnProjectionSelect() {
		SqlSelectQuery<Object> query = new SqlSelectQuery<>(auditedTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(integerExpression()));
		assertThrows(SqlStatementBuilderException.class, query::withAudit);
	}
	
	@Test
	void fetchPageWithNegativePage() {
		assertThrows(IllegalArgumentException.class, () -> select().fetchPage(-1, 10));
	}
	
	@Test
	void fetchPageWithNonPositivePageSize() {
		assertThrows(IllegalArgumentException.class, () -> select().fetchPage(0, 0));
	}
	
	@Test
	void fetchPageWithoutOrderBy() {
		assertThrows(SqlStatementBuilderException.class, () -> select().fetchPage(0, 10));
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> select().toSql(null));
	}
	
	@Test
	void renderSkipLockedWithForShareThrows() throws SqlException {
		SqlSelectQuery<Object> query = selectOn(PG).forShare().skipLocked();
		assertThrows(SqlStatementBuilderException.class, () -> query.toSql(PG));
	}
	
	@Test
	void forUpdateSetsLockMode() throws SqlException {
		SqlSelectQuery<Object> query = selectOn(PG).forUpdate();
		assertTrue(query.toSql(PG).sql().contains("FOR UPDATE"));
	}
	
	@Test
	void forShareSetsLockMode() throws SqlException {
		SqlSelectQuery<Object> query = selectOn(PG).forShare();
		assertTrue(query.toSql(PG).sql().contains("FOR SHARE"));
	}
	
	@Test
	void skipLockedAfterForUpdate() throws SqlException {
		SqlSelectQuery<Object> query = selectOn(PG).forUpdate().skipLocked();
		assertTrue(query.toSql(PG).sql().contains("SKIP LOCKED"));
	}
	
	@Test
	void noWaitAfterForUpdate() throws SqlException {
		SqlSelectQuery<Object> query = selectOn(PG).forUpdate().noWait();
		assertTrue(query.toSql(PG).sql().toLowerCase().contains("wait"));
	}
	
	@Test
	void innerJoinAppendsClause() throws SqlException {
		assertTrue(select().innerJoin(table(), alwaysCondition()).toSql(DIALECT).sql().contains("INNER JOIN"));
	}
	
	@Test
	void leftJoinAppendsClause() throws SqlException {
		assertTrue(select().leftJoin(table(), alwaysCondition()).toSql(DIALECT).sql().contains("LEFT JOIN"));
	}
	
	@Test
	void rightJoinAppendsClause() throws SqlException {
		assertTrue(select().rightJoin(table(), alwaysCondition()).toSql(DIALECT).sql().contains("RIGHT JOIN"));
	}
	
	@Test
	void fullJoinAppendsClause() throws SqlException {
		assertTrue(select().fullJoin(table(), alwaysCondition()).toSql(DIALECT).sql().contains("FULL OUTER JOIN"));
	}
	
	@Test
	void crossJoinAppendsClause() throws SqlException {
		assertTrue(select().crossJoin(table()).toSql(DIALECT).sql().contains("CROSS JOIN"));
	}
	
	@Test
	void lateralJoinDefaultsToCross() throws SqlException {
		SqlSelectQuery<Object> query = selectOn(PG).lateralJoin(sampleSelect(), SqlAlias.of("sub"));
		assertTrue(query.toSql(PG).sql().contains("LATERAL"));
	}
	
	@Test
	void lateralJoinWithExplicitType() throws SqlException {
		SqlSelectQuery<Object> query = selectOn(PG).lateralJoin(SqlJoinType.LEFT, sampleSelect(), SqlAlias.of("sub"));
		assertTrue(query.toSql(PG).sql().contains("LATERAL"));
	}
	
	@Test
	void whereWithNoExistingCondition() throws SqlException {
		assertTrue(select().where(alwaysCondition()).toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void whereCombinesWithExistingCondition() throws SqlException {
		assertTrue(select().where(alwaysCondition()).where(neverCondition()).toSql(DIALECT).sql().contains("WHERE"));
	}
	
	@Test
	void groupByAppendsColumns() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> id = table.column("grp", INTEGER_TYPE, object -> 0);
		SqlSelectQuery<Object> query = new SqlSelectQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null).groupBy(id);
		assertTrue(query.toSql(DIALECT).sql().contains("GROUP BY"));
	}
	
	@Test
	void havingSetsCondition() throws SqlException {
		assertTrue(select().having(alwaysCondition()).toSql(DIALECT).sql().contains("HAVING"));
	}
	
	@Test
	void orderByAppendsClauses() throws SqlException {
		assertTrue(select().orderBy(integerExpression()).toSql(DIALECT).sql().contains("ORDER BY"));
	}
	
	@Test
	void distinctSetsFlag() throws SqlException {
		assertTrue(select().distinct().toSql(DIALECT).sql().contains("DISTINCT"));
	}
	
	@Test
	void limitRendersInSql() throws SqlException {
		assertTrue(select().limit(5).toSql(DIALECT).sql().contains("LIMIT"));
	}
	
	@Test
	void offsetRendersInSql() throws SqlException {
		assertTrue(select().limit(10).offset(5).toSql(DIALECT).sql().contains("OFFSET"));
	}
	
	@Test
	void unionAppendsUnionOperation() throws SqlException {
		assertTrue(select().union(select()).toSql(DIALECT).sql().contains("UNION"));
	}
	
	@Test
	void unionAllAppendsUnionAllOperation() throws SqlException {
		assertTrue(select().unionAll(select()).toSql(DIALECT).sql().contains("UNION"));
	}
	
	@Test
	void intersectAppendsIntersectOperation() throws SqlException {
		assertTrue(select().intersect(select()).toSql(DIALECT).sql().contains("INTERSECT"));
	}
	
	@Test
	void exceptAppendsExceptOperation() throws SqlException {
		assertTrue(select().except(select()).toSql(DIALECT).sql().contains("EXCEPT"));
	}
	
	@Test
	void withAppendsCommonTableExpression() throws SqlException {
		SqlSelectQuery<Object> query = select().with(select().asCommonExpression(SqlAlias.of("cte"), false));
		assertTrue(query.toSql(DIALECT).sql().startsWith("WITH"));
	}
	
	@Test
	void asCommonExpressionWrapsQuery() {
		SqlSelectQuery<Object> query = select();
		SqlCommonTableExpression recursive = query.asCommonExpression(SqlAlias.of("cte"), true);
		assertSame(query, recursive.query());
		assertTrue(recursive.recursive());
		assertFalse(query.asCommonExpression(SqlAlias.of("cte"), false).recursive());
	}
	
	@Test
	void projectIntoBuildsProjectionQuery() throws SqlException {
		SqlSelectQuery<Projection> query = selectExpr(integerExpression()).projectInto(Projection.class);
		assertNotNull(query);
		assertEquals(1, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void builderMethodsReturnNewInstances() {
		SqlSelectQuery<Object> original = select();
		assertNotSame(original, original.where(alwaysCondition()));
		assertNotSame(original, original.limit(5));
		assertNotSame(original, original.distinct());
	}
	
	@Test
	void withAuditOnAuditedEntitySelect() throws SqlException {
		SqlSelectQuery<Object> query = new SqlSelectQuery<>(auditedTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null);
		assertNotNull(query.withAudit());
		assertDoesNotThrow(() -> query.withAudit().toSql(DIALECT));
	}
	
	@Test
	void toSqlFullEntitySelectListsColumns() throws SqlException {
		String sql = select().toSql(DIALECT).sql();
		assertTrue(sql.contains(DIALECT.quoteIdentifier("id")));
		assertTrue(sql.contains(DIALECT.quoteIdentifier("name")));
	}
	
	@Test
	void toSqlFullEntitySelectOnAuditedTableIncludesAuditColumns() throws SqlException {
		SqlSelectQuery<Object> query = new SqlSelectQuery<>(auditedTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null);
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("SELECT"));
		assertTrue(sql.contains(DIALECT.quoteIdentifier("audited_table")));
	}
	
	@Test
	void toSqlProjectionSelectListsExpressions() throws SqlException {
		assertEquals(2, markerCount(selectExpr(integerExpression(), integerExpression()).toSql(DIALECT).sql()));
	}
	
	@Test
	void toSqlWithJoins() throws SqlException {
		assertTrue(select().innerJoin(table(), alwaysCondition()).toSql(DIALECT).sql().contains("INNER JOIN"));
	}
	
	@Test
	void toSqlWithWhereOnly() throws SqlException {
		String sql = select().where(alwaysCondition()).toSql(DIALECT).sql();
		assertTrue(sql.contains("WHERE"));
		assertFalse(sql.contains("EXISTS"));
	}
	
	@Test
	void toSqlWithWhereExistsOnly() throws SqlException {
		String sql = select().whereExists(sampleSelect()).toSql(DIALECT).sql();
		assertTrue(sql.contains("WHERE"));
		assertTrue(sql.contains("EXISTS"));
	}
	
	@Test
	void toSqlWithWhereAndWhereExists() throws SqlException {
		String sql = select().where(alwaysCondition()).whereExists(sampleSelect()).toSql(DIALECT).sql();
		assertTrue(sql.contains("AND"));
		assertTrue(sql.contains("EXISTS"));
	}
	
	@Test
	void toSqlWithGroupByAndHaving() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> id = table.column("grp", INTEGER_TYPE, object -> 0);
		SqlSelectQuery<Object> query = new SqlSelectQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null).groupBy(id).having(alwaysCondition());
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("GROUP BY"));
		assertTrue(sql.contains("HAVING"));
	}
	
	@Test
	void toSqlWithSetOperations() throws SqlException {
		assertTrue(select().union(select()).toSql(DIALECT).sql().contains("UNION"));
	}
	
	@Test
	void toSqlWithOrderedExpression() throws SqlException {
		String sql = select().orderBy(integerExpression().ascending()).toSql(DIALECT).sql();
		assertTrue(sql.contains("ORDER BY"));
		assertTrue(sql.contains("ASC"));
	}
	
	@Test
	void toSqlWithPlainExpressionOrderBy() throws SqlException {
		String sql = select().orderBy(integerExpression()).toSql(DIALECT).sql();
		assertTrue(sql.contains("ORDER BY"));
		assertFalse(sql.contains("ASC"));
	}
	
	@Test
	void toSqlWithLimitAndOffset() throws SqlException {
		String sql = select().limit(10).offset(5).toSql(DIALECT).sql();
		assertTrue(sql.contains("LIMIT"));
		assertTrue(sql.contains("OFFSET"));
	}
	
	@Test
	void toSqlWithRecursiveCte() throws SqlException {
		SqlSelectQuery<Object> query = select().with(select().asCommonExpression(SqlAlias.of("cte"), true));
		assertTrue(query.toSql(DIALECT).sql().contains("RECURSIVE"));
	}
	
	@Test
	void toSqlWithNonRecursiveCte() throws SqlException {
		SqlSelectQuery<Object> query = select()
			.with(select().asCommonExpression(SqlAlias.of("a"), false))
			.with(select().asCommonExpression(SqlAlias.of("b"), false));
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.startsWith("WITH"));
		assertFalse(sql.contains("RECURSIVE"));
	}
	
	@Test
	void fullQueryRendersAllClausesInOrder() throws SqlException {
		SqlTable<Object> table = table();
		SqlColumn<Object, Integer> id = table.column("grp", INTEGER_TYPE, object -> 0);
		SqlSelectQuery<Object> query = new SqlSelectQuery<>(table, PG, SOURCE, TIMEOUT, resultSet -> null)
			.distinct()
			.innerJoin(table(), alwaysCondition())
			.where(alwaysCondition())
			.groupBy(id)
			.having(alwaysCondition())
			.orderBy(integerExpression())
			.limit(10)
			.forUpdate();
		String sql = query.toSql(PG).sql();
		assertTrue(sql.contains("SELECT"));
		assertTrue(sql.contains("DISTINCT"));
		assertTrue(sql.contains("INNER JOIN"));
		assertTrue(sql.contains("WHERE"));
		assertTrue(sql.contains("GROUP BY"));
		assertTrue(sql.contains("HAVING"));
		assertTrue(sql.contains("ORDER BY"));
		assertTrue(sql.contains("LIMIT"));
		assertTrue(sql.contains("FOR UPDATE"));
		assertTrue(sql.indexOf("WHERE") < sql.indexOf("GROUP BY"));
		assertTrue(sql.indexOf("GROUP BY") < sql.indexOf("ORDER BY"));
	}
	
	private record Projection(int value) {}
}
