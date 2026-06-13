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
}
