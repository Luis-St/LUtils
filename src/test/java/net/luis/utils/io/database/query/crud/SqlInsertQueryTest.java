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

import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.query.util.SqlSetClause;
import net.luis.utils.io.database.query.util.SqlSetType;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlInsertQuery}.<br>
 *
 * @author Luis-St
 */
class SqlInsertQueryTest {
	
	private static SqlTable<Object> oneColumnTable() {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		table.column("id", INTEGER_TYPE, object -> 0);
		return table;
	}
	
	private static SqlTable<Object> twoColumnTable() {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		table.column("id", INTEGER_TYPE, object -> 0);
		table.column("name", STRING_TYPE, object -> "x");
		return table;
	}
	
	private static long markerCount(String sql) {
		return sql.chars().filter(character -> character == '?').count();
	}
	
	private static SqlTable<Object> auditedOneColumnTable() {
		SqlTable<Object> table = auditedTable();
		table.column("id", INTEGER_TYPE, object -> 0);
		return table;
	}
	
	private static boolean bindsUser(SqlRendered rendered, String user) {
		return rendered.parameters().stream().anyMatch(parameter -> user.equals(parameter.getSecond()));
	}
	
	@Test
	void constructWithEntities() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertNotNull(query);
		assertTrue(query.toSql(DIALECT).sql().contains("INSERT INTO"));
	}
	
	@Test
	void constructWithAuditUserProvider() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		assertNotNull(new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), provider));
	}
	
	@Test
	void insertOrIgnoreFactoryCreatesQuery() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> id = table.column("conflict", INTEGER_TYPE, object -> 0);
		assertNotNull(SqlInsertQuery.insertOrIgnore(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(id)));
	}
	
	@Test
	void upsertFactoryCreatesQuery() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> id = table.column("conflict", INTEGER_TYPE, object -> 0);
		assertNotNull(SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), id));
	}
	
	@Test
	void insertFromSelectFactoryCreatesQuery() throws SqlException {
		SqlInsertQuery<Object> query = SqlInsertQuery.insertFromSelect(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, sampleSelect());
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("INSERT INTO"));
		assertFalse(sql.contains("VALUES"));
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQuery<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object())));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQuery<>(oneColumnTable(), null, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object())));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQuery<>(oneColumnTable(), DIALECT, null, TIMEOUT, resultSet -> null, List.of(new Object())));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, null, resultSet -> null, List.of(new Object())));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, null, List.of(new Object())));
	}
	
	@Test
	void constructWithNullEntities() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null));
	}
	
	@Test
	void constructWithEmptyEntities() {
		assertThrows(IllegalArgumentException.class, () -> new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of()));
	}
	
	@Test
	void insertOrIgnoreWithNullEntities() {
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.insertOrIgnore(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of(integerColumn())));
	}
	
	@Test
	void insertOrIgnoreWithNullConflictColumns() {
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.insertOrIgnore(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), null));
	}
	
	@Test
	void insertOrIgnoreWithEmptyConflictColumns() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQuery.insertOrIgnore(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of()));
	}
	
	@Test
	void upsertWithNullEntities() {
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.upsert(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, integerColumn()));
	}
	
	@Test
	void omittingWithNullColumnsThrows() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertThrows(NullPointerException.class, () -> query.omitting((SqlColumn<Object, ?>[]) null));
	}
	
	@Test
	void overrideWithNullColumnThrows() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertThrows(NullPointerException.class, () -> query.override(null, integerExpression()));
	}
	
	@Test
	void overrideWithNullExpressionThrows() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertThrows(NullPointerException.class, () -> query.override(integerColumn(), null));
	}
	
	@Test
	void upsertWithCompositeConflictColumnsCreatesQuery() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> a = table.column("a", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> b = table.column("b", INTEGER_TYPE, object -> 0);
		assertNotNull(SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(a, b)));
	}
	
	@Test
	void upsertWithEmptyConflictColumnsThrows() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQuery.upsert(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of()));
	}
	
	@Test
	void upsertWithNullConflictColumnsThrows() {
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.upsert(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), (List<SqlColumn<Object, ?>>) null));
	}
	
	@Test
	void upsertSingleColumnOverloadWithNullConflictColumnThrows() {
		SqlTable<Object> table = oneColumnTable();
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), (SqlColumn<Object, ?>) null));
	}
	
	@Test
	void upsertWithUpdateClausesCreatesQuery() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> conflict = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(conflict, integerExpression(), SqlSetType.EXPRESSION);
		assertNotNull(SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(conflict), List.of(clause)));
	}
	
	@Test
	void upsertWithNullUpdateClausesThrows() {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> conflict = table.column("conflict", INTEGER_TYPE, object -> 0);
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(conflict), null));
	}
	
	@Test
	void upsertWithUpdateClausesNullEntitiesThrows() {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> conflict = table.column("conflict", INTEGER_TYPE, object -> 0);
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, List.of(conflict), List.of()));
	}
	
	@Test
	void upsertWithUpdateClausesNullConflictColumnsThrows() {
		SqlTable<Object> table = oneColumnTable();
		assertThrows(NullPointerException.class, () -> SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), null, List.of()));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQuery<>(null));
	}
	
	@Test
	void toSqlWithNullDialect() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertThrows(NullPointerException.class, () -> query.toSql(null));
	}
	
	@Test
	void toSqlStandardInsertSingleRow() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("INSERT INTO"));
		assertTrue(sql.contains("VALUES"));
		assertEquals(1, markerCount(sql));
	}
	
	@Test
	void toSqlStandardInsertMultipleColumns() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(twoColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains(","));
		assertEquals(2, markerCount(sql));
	}
	
	@Test
	void toSqlMultipleRowsRenderMultipleTuples() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object(), new Object()));
		assertEquals(2, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void toSqlInsertFromSelectRendersSelect() throws SqlException {
		SqlInsertQuery<Object> query = SqlInsertQuery.insertFromSelect(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, sampleSelect());
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("SELECT"));
		assertFalse(sql.contains("VALUES"));
	}
	
	@Test
	void toSqlInsertOrIgnoreRendersConflictSuffix() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> id = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = SqlInsertQuery.insertOrIgnore(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(id));
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("ON CONFLICT"));
		assertTrue(sql.contains("DO NOTHING"));
	}
	
	@Test
	void toSqlInsertOrIgnoreRendersModifierForMySql() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> id = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = SqlInsertQuery.insertOrIgnore(table, SqlDialects.MYSQL, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(id));
		assertTrue(query.toSql(SqlDialects.MYSQL).sql().contains("IGNORE"));
	}
	
	@Test
	void toSqlUpsertWithUpsertSuffixSupported() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> id = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), id);
		assertTrue(query.toSql(SqlDialects.POSTGRESQL).sql().contains("ON CONFLICT"));
	}
	
	@Test
	void toSqlUpsertWithoutUpsertSuffixSupported() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> id = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object(), new Object()), id);
		assertTrue(query.toSql(SqlDialects.SQL_SERVER).sql().contains("MERGE"));
	}
	
	@Test
	void omittingExcludesColumnFromRenderedInsert() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, String> name = table.column("name", STRING_TYPE, object -> "x");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object())).omitting(name);
		String sql = query.toSql(DIALECT).sql();
		assertFalse(sql.contains("\"name\""));
		assertEquals(1, markerCount(sql));
	}
	
	@Test
	void omittingDoesNotExcludeConflictColumn() throws SqlException {
		SqlTable<Object> table = oneColumnTable();
		SqlColumn<Object, Integer> conflict = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), conflict).omitting(conflict);
		String sql = query.toSql(SqlDialects.POSTGRESQL).sql();
		assertTrue(sql.contains("\"conflict\""));
		assertEquals(2, markerCount(sql));
	}
	
	@Test
	void overrideRendersExpressionInsteadOfBindParameter() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> id = table.column("id", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object())).override(id, id.of(SqlAlias.EXCLUDED));
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("excluded"));
		assertEquals(0, markerCount(sql));
	}
	
	@Test
	void overrideCalledTwiceForSameColumnReplacesFirst() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> id = table.column("id", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()))
			.override(id, id.of(SqlAlias.of("FIRST")))
			.override(id, id.of(SqlAlias.of("SECOND")));
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("SECOND"));
		assertFalse(sql.contains("FIRST"));
	}
	
	@Test
	void toSqlUpsertCompositeConflictColumnsRendersAllColumns() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> a = table.column("a", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> b = table.column("b", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(a, b));
		String sql = query.toSql(SqlDialects.POSTGRESQL).sql();
		assertTrue(sql.contains("ON CONFLICT"));
		assertTrue(sql.contains("\"a\""));
		assertTrue(sql.contains("\"b\""));
	}
	
	@Test
	void toSqlUpsertAutoIncrementConflictColumnStillRenderedInValueTuple() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> id = table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.autoIncrement());
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), id);
		String sql = query.toSql(SqlDialects.POSTGRESQL).sql();
		assertTrue(sql.contains("\"id\""));
		assertEquals(1, markerCount(sql));
	}
	
	@Test
	void toSqlUpsertWithCustomUpdateClauseRendersCustomExpression() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> conflict = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> value = table.column("value", INTEGER_TYPE, object -> 0);
		SqlSetClause<Object, Integer> clause = new SqlSetClause<>(value, value.of(SqlAlias.EXCLUDED), SqlSetType.INCREMENT);
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), List.of(conflict), List.of(clause));
		String sql = query.toSql(SqlDialects.POSTGRESQL).sql();
		assertTrue(sql.contains("+"));
		assertTrue(sql.contains("excluded"));
	}
	
	@Test
	void toSqlUpsertWithoutUpdateClausesUsesDefaultPerColumnAssignment() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> id = table.column("id", INTEGER_TYPE, object -> 0);
		table.column("name", STRING_TYPE, object -> "x");
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), id);
		String sql = query.toSql(SqlDialects.POSTGRESQL).sql();
		assertTrue(sql.contains("\"name\" = \"excluded\".\"name\""));
		// The conflict column itself is not excluded from the default SET-clause loop, so it is also assigned from excluded.
		assertTrue(sql.contains("\"id\" = \"excluded\".\"id\""));
	}
	
	@Test
	void toSqlPlainInsertOmitsAutoIncrementColumn() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.autoIncrement());
		table.column("name", STRING_TYPE, object -> "x");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		String sql = query.toSql(DIALECT).sql();
		assertFalse(sql.contains("\"id\""));
		assertTrue(sql.contains("\"name\""));
		assertEquals(1, markerCount(sql));
	}
	
	@Test
	void toSqlWithAuditApplicationSourceRendersParameters() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(auditedTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), provider);
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains("INSERT INTO"));
		assertTrue(markerCount(sql) > 0);
	}
	
	@Test
	void toSqlWithAuditCreatedByNullUser() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(auditedTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertTrue(query.toSql(DIALECT).sql().contains("NULL"));
	}
	
	@Test
	void auditedByWithNullProvider() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		SqlInsertQuery<Object> audited = assertDoesNotThrow(() -> query.auditedBy(null));
		assertNotNull(audited);
		assertTrue(audited.toSql(DIALECT).sql().contains("INSERT INTO"));
	}
	
	@Test
	void auditedByReturnsNewInstance() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertNotSame(query, query.auditedBy(provider));
	}
	
	@Test
	void auditedByBindsUserOnAuditedTable() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(auditedOneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		assertTrue(bindsUser(query.auditedBy(provider).toSql(DIALECT), "tester"));
	}
	
	@Test
	void auditedByOnNonAuditedTableHasNoEffect() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(oneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		SqlRendered plain = query.toSql(DIALECT);
		SqlRendered audited = query.auditedBy(provider).toSql(DIALECT);
		assertEquals(plain.sql(), audited.sql());
		assertEquals(plain.parameters(), audited.parameters());
	}
	
	@Test
	void auditedByWithNullProviderBindsNullUser() throws SqlException {
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(auditedOneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		SqlRendered rendered = query.auditedBy(null).toSql(DIALECT);
		assertFalse(bindsUser(rendered, "tester"));
		assertTrue(rendered.sql().contains("NULL"));
	}
	
	@Test
	void auditedByLeavesOriginalQueryUnchanged() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(auditedOneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		SqlInsertQuery<Object> copy = query.auditedBy(provider);
		assertFalse(bindsUser(query.toSql(DIALECT), "tester"));
		assertTrue(bindsUser(copy.toSql(DIALECT), "tester"));
	}
	
	@Test
	void auditedByOverwritesExistingProvider() throws SqlException {
		SqlAuditUserProvider first = () -> Optional.of("first");
		SqlAuditUserProvider second = () -> Optional.of("second");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(auditedOneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), first);
		SqlRendered rendered = query.auditedBy(second).toSql(DIALECT);
		assertTrue(bindsUser(rendered, "second"));
		assertFalse(bindsUser(rendered, "first"));
	}
	
	@Test
	void auditedByWithEmptyProviderResult() throws SqlException {
		SqlAuditUserProvider provider = Optional::empty;
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(auditedOneColumnTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		SqlRendered rendered = query.auditedBy(provider).toSql(DIALECT);
		assertFalse(bindsUser(rendered, "tester"));
		assertTrue(rendered.sql().contains("NULL"));
	}
	
	@Test
	void auditedByChainedWithOmitting() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlTable<Object> table = auditedOneColumnTable();
		SqlColumn<Object, String> name = table.column("name", STRING_TYPE, object -> "x");
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		SqlRendered rendered = query.auditedBy(provider).omitting(name).toSql(DIALECT);
		assertFalse(rendered.sql().contains("\"name\""));
		assertTrue(bindsUser(rendered, "tester"));
		assertEquals(rendered.sql(), query.omitting(name).auditedBy(provider).toSql(DIALECT).sql());
	}
	
	@Test
	void auditedByChainedWithOverride() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlTable<Object> table = auditedTable();
		SqlColumn<Object, Integer> id = table.column("id", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = new SqlInsertQuery<>(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()));
		SqlRendered rendered = query.auditedBy(provider).override(id, id.of(SqlAlias.EXCLUDED)).toSql(DIALECT);
		assertTrue(rendered.sql().contains("excluded"));
		assertTrue(bindsUser(rendered, "tester"));
	}
	
	@Test
	void auditedByOnUpsertQueryBindsUser() throws SqlException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlTable<Object> table = auditedTable();
		SqlColumn<Object, Integer> conflict = table.column("conflict", INTEGER_TYPE, object -> 0);
		SqlInsertQuery<Object> query = SqlInsertQuery.upsert(table, DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(new Object()), conflict);
		SqlRendered rendered = query.auditedBy(provider).toSql(SqlDialects.POSTGRESQL);
		assertTrue(rendered.sql().contains("ON CONFLICT"));
		assertTrue(bindsUser(rendered, "tester"));
	}
}
