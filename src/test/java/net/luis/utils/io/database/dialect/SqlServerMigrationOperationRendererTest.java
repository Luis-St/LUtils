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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.audit.SqlAuditColumn;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlServerMigrationOperationRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlServerMigrationOperationRendererTest {
	
	private static final SqlServerMigrationOperationRenderer RENDERER = new SqlServerMigrationOperationRenderer(SqlDialects.SQL_SERVER);
	
	private static SqlTable<Object> table(String name) {
		return SqlTable.create(Object.class, name);
	}
	
	private static SqlColumn<Object, Integer> column(SqlTable<Object> table, String name) {
		return table.column(name, SqlTypes.INTEGER, object -> 0);
	}
	
	/**
	 * Builds column options for the branch under test, keeping the six independent flags readable at the call site.
	 *
	 * @param notNull Whether the column is declared {@code NOT NULL}
	 * @param autoIncrement Whether the column is auto-incremented
	 * @param unique Whether the column carries a unique constraint
	 * @param defaultValue The default value or {@code null} for none
	 * @param references Whether the column references a {@code roles} table
	 * @param check The check condition or {@code null} for none
	 * @return The built column options
	 */
	private static SqlColumnOptions options(boolean notNull, boolean autoIncrement, boolean unique, Object defaultValue, boolean references, SqlCondition check) {
		return new SqlColumnOptions(notNull, unique, autoIncrement, Optional.ofNullable(defaultValue), references ? table("roles") : null, check);
	}
	
	@Test
	void renderRenameTableNullFromTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameTable(null, table("new")));
	}
	
	@Test
	void renderRenameTableNullToTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameTable(table("old"), null));
	}
	
	@Test
	void renderRenameColumnNullTable() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(null, column(table, "old"), column(table, "new")));
	}
	
	@Test
	void renderRenameColumnNullFromColumn() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(table, null, column(table, "new")));
	}
	
	@Test
	void renderRenameColumnNullToColumn() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(table, column(table, "old"), null));
	}
	
	@Test
	void renderRenameTableUsesSpRename() throws SqlException {
		String sql = RENDERER.renderRenameTable(table("old"), table("new")).sql();
		assertTrue(sql.contains("EXEC"));
		assertTrue(sql.contains("sp_rename"));
		assertTrue(sql.contains("'old'"));
		assertTrue(sql.contains("'new'"));
	}
	
	@Test
	void renderRenameColumnUsesSpRenameWithColumn() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderRenameColumn(table, column(table, "old"), column(table, "new")).sql();
		assertTrue(sql.contains("sp_rename"));
		assertTrue(sql.contains("'COLUMN'"));
		assertTrue(sql.contains("'users.old'"));
	}
	
	@Test
	void renderAddColumnWithNullTable() {
		SqlTable<Object> table = table("users");
		SqlColumn<Object, Integer> age = column(table, "age");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(null, age, SqlTypes.INTEGER, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void renderAddColumnWithNullColumn() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(table, null, SqlTypes.INTEGER, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void renderAddColumnWithNullType() {
		SqlTable<Object> table = table("users");
		SqlColumn<Object, Integer> age = column(table, "age");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(table, age, null, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void renderAddColumnWithNullOptions() {
		SqlTable<Object> table = table("users");
		SqlColumn<Object, Integer> age = column(table, "age");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(table, age, SqlTypes.INTEGER, null));
	}
	
	@Test
	void renderEnableAuditingWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderEnableAuditing(null, SqlAuditConfig.DEFAULT));
	}
	
	@Test
	void renderEnableAuditingWithNullConfig() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderEnableAuditing(table, null));
	}
	
	@Test
	void renderDisableAuditingWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDisableAuditing(null, SqlAuditConfig.DEFAULT));
	}
	
	@Test
	void renderDisableAuditingWithNullConfig() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderDisableAuditing(table, null));
	}
	
	@Test
	void renderAddColumnWithUnsupportedTypeThrows() {
		SqlTable<Object> table = table("users");
		SqlColumn<Object, Integer> age = column(table, "age");
		assertThrows(SqlException.class, () -> RENDERER.renderAddColumn(table, age, SqlTypes.INTEGER.array(), SqlColumnOptions.EMPTY));
	}
	
	@Test
	void renderAddColumnWithDefaultOptionsOmitsAllClauses() throws SqlException {
		SqlTable<Object> table = table("users");
		SqlRendered rendered = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, SqlColumnOptions.EMPTY);
		assertEquals("ALTER TABLE [users] ADD [age] " + SqlDialects.SQL_SERVER.getTypeName(SqlTypes.INTEGER), rendered.sql());
		assertFalse(rendered.sql().contains("COLUMN"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderAddColumnWithNotNullOption() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, options(true, false, false, null, false, null)).sql();
		assertTrue(sql.endsWith("NOT NULL"), sql);
	}
	
	@Test
	void renderAddColumnWithAutoIncrementOption() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, options(false, true, false, null, false, null)).sql();
		assertTrue(sql.contains(SqlDialects.SQL_SERVER.tableRenderer().renderAutoIncrementKeyword().sql()), sql);
	}
	
	@Test
	void renderAddColumnWithDefaultValueAddsNamedConstraint() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, options(false, false, false, 0, false, null)).sql();
		assertTrue(sql.contains("CONSTRAINT [DF_users_age] DEFAULT 0"), sql);
		assertFalse(sql.contains("?"), sql);
	}
	
	@Test
	void renderAddColumnWithoutDefaultValueOmitsConstraint() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, SqlColumnOptions.EMPTY).sql();
		assertFalse(sql.contains("CONSTRAINT"), sql);
	}
	
	@Test
	void renderAddColumnWithUniqueOption() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, options(false, false, true, null, false, null)).sql();
		assertTrue(sql.contains("UNIQUE"), sql);
	}
	
	@Test
	void renderAddColumnWithReferencesTable() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "role_id"), SqlTypes.INTEGER, options(false, false, false, null, true, null)).sql();
		assertTrue(sql.contains("REFERENCES [roles]"), sql);
	}
	
	@Test
	void renderAddColumnWithoutReferencesTableOmitsReferences() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, SqlColumnOptions.EMPTY).sql();
		assertFalse(sql.contains("REFERENCES"), sql);
	}
	
	@Test
	void renderAddColumnWithCheckRendersUnqualifiedLiteral() throws SqlException {
		SqlTable<Object> table = table("users");
		SqlColumn<Object, Integer> age = column(table, "age");
		SqlRendered rendered = RENDERER.renderAddColumn(table, age, SqlTypes.INTEGER, options(false, false, false, null, false, Sql.greaterThan(age, 0)));
		assertTrue(rendered.sql().contains("CHECK([age] > 0)"), rendered.sql());
		assertFalse(rendered.sql().contains("[users].[age]"), rendered.sql());
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderAddColumnWithoutCheckOmitsCheck() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, SqlColumnOptions.EMPTY).sql();
		assertFalse(sql.contains("CHECK"), sql);
	}
	
	@Test
	void renderEnableAuditingWithVersionColumnAddsDefaultConstraint() throws SqlException {
		List<SqlRendered> statements = RENDERER.renderEnableAuditing(table("users"), SqlAuditConfig.DEFAULT);
		String versionStatement = statements.getFirst().sql();
		assertTrue(versionStatement.contains("CONSTRAINT [DF_users_" + SqlAuditConfig.DEFAULT.versionColumn() + "] DEFAULT 0 NOT NULL"), versionStatement);
	}
	
	@Test
	void renderEnableAuditingWithNonVersionColumnOmitsDefaultConstraint() throws SqlException {
		List<SqlRendered> statements = RENDERER.renderEnableAuditing(table("users"), SqlAuditConfig.DEFAULT);
		SqlAuditColumn createdAt = SqlAuditConfig.DEFAULT.auditColumns().get(1);
		String statement = statements.get(1).sql();
		assertEquals("ALTER TABLE [users] ADD [" + createdAt.name() + "] " + SqlDialects.SQL_SERVER.getTypeName(createdAt.type()), statement);
		assertFalse(statement.contains("CONSTRAINT"), statement);
	}
	
	@Test
	void renderDisableAuditingDropsConstraintBeforeColumn() throws SqlException {
		List<SqlRendered> statements = RENDERER.renderDisableAuditing(table("users"), SqlAuditConfig.DEFAULT);
		String versionColumn = SqlAuditConfig.DEFAULT.versionColumn();
		assertTrue(statements.getFirst().sql().startsWith("DECLARE @constraint sysname"), statements.getFirst().sql());
		assertTrue(statements.getFirst().sql().contains("c.name = N'" + versionColumn + "'"), statements.getFirst().sql());
		assertEquals("ALTER TABLE [users] DROP COLUMN [" + versionColumn + "]", statements.get(1).sql());
	}
	
	@Test
	void renderAddColumnQuotesIdentifiers() throws SqlException {
		SqlTable<Object> table = table("us]ers");
		String sql = RENDERER.renderAddColumn(table, column(table, "a]ge"), SqlTypes.INTEGER, SqlColumnOptions.EMPTY).sql();
		assertTrue(sql.contains("ALTER TABLE [us]]ers]"), sql);
		assertTrue(sql.contains("ADD [a]]ge]"), sql);
	}
	
	@Test
	void renderEnableAuditingWithMultipleColumnsKeepsConfigOrder() throws SqlException {
		List<SqlAuditColumn> columns = SqlAuditConfig.DEFAULT.auditColumns();
		List<SqlRendered> statements = RENDERER.renderEnableAuditing(table("users"), SqlAuditConfig.DEFAULT);
		assertEquals(columns.size(), statements.size());
		for (int index = 0; index < columns.size(); index++) {
			assertTrue(statements.get(index).sql().contains("[" + columns.get(index).name() + "]"), statements.get(index).sql());
		}
	}
	
	@Test
	void renderDisableAuditingWithMultipleColumnsProducesTwoStatementsEach() throws SqlException {
		List<SqlAuditColumn> columns = SqlAuditConfig.DEFAULT.auditColumns();
		List<SqlRendered> statements = RENDERER.renderDisableAuditing(table("users"), SqlAuditConfig.DEFAULT);
		assertEquals(2 * columns.size(), statements.size());
		for (int index = 0; index < columns.size(); index++) {
			assertTrue(statements.get(2 * index).sql().contains("N'" + columns.get(index).name() + "'"), statements.get(2 * index).sql());
			assertTrue(statements.get(2 * index + 1).sql().endsWith("DROP COLUMN [" + columns.get(index).name() + "]"), statements.get(2 * index + 1).sql());
		}
	}
	
	@Test
	void renderAddColumnProducesNoParameters() throws SqlException {
		SqlTable<Object> table = table("users");
		SqlColumn<Object, Integer> age = column(table, "age");
		assertTrue(RENDERER.renderAddColumn(table, age, SqlTypes.INTEGER, SqlColumnOptions.EMPTY).parameters().isEmpty());
		assertTrue(RENDERER.renderAddColumn(table, age, SqlTypes.INTEGER, options(true, true, true, 0, true, Sql.greaterThan(age, 0))).parameters().isEmpty());
	}
	
	@Test
	void renderAddColumnWithAllOptionsRendersClausesInOrder() throws SqlException {
		SqlTable<Object> table = table("users");
		SqlColumn<Object, Integer> age = column(table, "age");
		String sql = RENDERER.renderAddColumn(table, age, SqlTypes.INTEGER, options(true, true, true, 0, true, Sql.greaterThan(age, 0))).sql();
		
		assertTrue(sql.indexOf("NOT NULL") < sql.indexOf("IDENTITY"), sql);
		assertTrue(sql.indexOf("IDENTITY") < sql.indexOf("CONSTRAINT"), sql);
		assertTrue(sql.indexOf("CONSTRAINT") < sql.indexOf("UNIQUE"), sql);
		assertTrue(sql.indexOf("UNIQUE") < sql.indexOf("REFERENCES"), sql);
		assertTrue(sql.indexOf("REFERENCES") < sql.indexOf("CHECK"), sql);
	}
	
	@Test
	void renderAddColumnWithNotNullAndDefaultValue() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderAddColumn(table, column(table, "age"), SqlTypes.INTEGER, options(true, false, false, 0, false, null)).sql();
		assertTrue(sql.contains("NOT NULL"), sql);
		assertTrue(sql.contains("CONSTRAINT [DF_users_age] DEFAULT 0"), sql);
		assertTrue(sql.indexOf("NOT NULL") < sql.indexOf("CONSTRAINT"), sql);
	}
	
	@Test
	void renderEnableThenDisableAuditingIsSymmetric() throws SqlException {
		SqlTable<Object> table = table("users");
		List<SqlRendered> enabled = RENDERER.renderEnableAuditing(table, SqlAuditConfig.DEFAULT);
		List<SqlRendered> disabled = RENDERER.renderDisableAuditing(table, SqlAuditConfig.DEFAULT);
		String versionColumn = SqlAuditConfig.DEFAULT.versionColumn();
		
		assertEquals(2 * enabled.size(), disabled.size());
		assertTrue(enabled.getFirst().sql().contains("CONSTRAINT [DF_users_" + versionColumn + "]"), enabled.getFirst().sql());
		assertTrue(disabled.getFirst().sql().contains("OBJECT_ID(N'users')"), disabled.getFirst().sql());
		for (SqlAuditColumn column : SqlAuditConfig.DEFAULT.auditColumns()) {
			assertTrue(disabled.stream().anyMatch(statement -> statement.sql().endsWith("DROP COLUMN [" + column.name() + "]")), column.name());
		}
	}
}
