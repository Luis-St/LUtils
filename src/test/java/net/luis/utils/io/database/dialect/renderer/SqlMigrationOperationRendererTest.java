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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.comparison.SqlEqualToCondition;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
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
 * Test class for {@link SqlMigrationOperationRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationOperationRendererTest {
	
	private static final SqlMigrationOperationRenderer RENDERER = new SqlMigrationOperationRenderer(SqlDialects.DEFAULT);
	
	private static SqlTable<Object> usersTable() {
		return SqlTable.create(Object.class, "users");
	}
	
	private static SqlColumn<Object, Integer> idColumn(SqlTable<Object> table) {
		return table.column("id", SqlTypes.INTEGER, object -> 0);
	}
	
	private static SqlCondition condition() {
		return new SqlEqualToCondition(new SqlValueExpression<>(1, SqlTypes.INTEGER), new SqlValueExpression<>(1, SqlTypes.INTEGER));
	}
	
	private static long countCommas(String sql) {
		return sql.chars().filter(c -> c == ',').count();
	}
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlMigrationOperationRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationOperationRenderer(null));
	}
	
	@Test
	void renderRenameTableWithNullFromTable() {
		SqlTable<Object> to = SqlTable.create(Object.class, "customers");
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameTable(null, to));
	}
	
	@Test
	void renderRenameTableWithNullToTable() {
		SqlTable<Object> from = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameTable(from, null));
	}
	
	@Test
	void renderAddColumnWithNullTable() {
		SqlColumn<Object, Integer> id = idColumn(usersTable());
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(null, id, SqlTypes.INTEGER, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void renderAddColumnWithNullColumn() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(users, null, SqlTypes.INTEGER, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void renderAddColumnWithNullType() {
		SqlTable<Object> users = usersTable();
		SqlColumn<Object, Integer> id = idColumn(users);
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(users, id, null, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void renderAddColumnWithNullOptions() {
		SqlTable<Object> users = usersTable();
		SqlColumn<Object, Integer> id = idColumn(users);
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddColumn(users, id, SqlTypes.INTEGER, null));
	}
	
	@Test
	void renderDropColumnWithNullTable() {
		SqlColumn<Object, Integer> id = idColumn(usersTable());
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropColumn(null, id));
	}
	
	@Test
	void renderDropColumnWithNullColumn() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropColumn(users, null));
	}
	
	@Test
	void renderRenameColumnWithNullTable() {
		SqlTable<Object> users = usersTable();
		SqlColumn<Object, Integer> id = idColumn(users);
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(null, id, id));
	}
	
	@Test
	void renderRenameColumnWithNullFromColumn() {
		SqlTable<Object> users = usersTable();
		SqlColumn<Object, Integer> id = idColumn(users);
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(users, null, id));
	}
	
	@Test
	void renderRenameColumnWithNullToColumn() {
		SqlTable<Object> users = usersTable();
		SqlColumn<Object, Integer> id = idColumn(users);
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(users, id, null));
	}
	
	@Test
	void renderAddUniqueConstraintWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddUniqueConstraint(null, "uq", List.of()));
	}
	
	@Test
	void renderAddUniqueConstraintWithNullConstraintName() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddUniqueConstraint(users, null, List.of()));
	}
	
	@Test
	void renderAddUniqueConstraintWithNullColumns() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddUniqueConstraint(users, "uq", null));
	}
	
	@Test
	void renderAddForeignKeyWithNullTable() {
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddForeignKey(null, "fk", List.of(), other, List.of(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void renderAddForeignKeyWithNullConstraintName() {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddForeignKey(users, null, List.of(), other, List.of(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void renderAddForeignKeyWithNullColumns() {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddForeignKey(users, "fk", null, other, List.of(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void renderAddForeignKeyWithNullReferencedTable() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddForeignKey(users, "fk", List.of(), null, List.of(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void renderAddForeignKeyWithNullReferencedColumns() {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddForeignKey(users, "fk", List.of(), other, null, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void renderAddForeignKeyWithNullOnDelete() {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddForeignKey(users, "fk", List.of(), other, List.of(), null, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void renderAddForeignKeyWithNullOnUpdate() {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddForeignKey(users, "fk", List.of(), other, List.of(), SqlReferentialAction.NO_ACTION, null));
	}
	
	@Test
	void renderAddCheckConstraintWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddCheckConstraint(null, "ck", condition()));
	}
	
	@Test
	void renderAddCheckConstraintWithNullConstraintName() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddCheckConstraint(users, null, condition()));
	}
	
	@Test
	void renderAddCheckConstraintWithNullCondition() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddCheckConstraint(users, "ck", null));
	}
	
	@Test
	void renderAddCompositePrimaryKeyWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddCompositePrimaryKey(null, "pk", List.of()));
	}
	
	@Test
	void renderAddCompositePrimaryKeyWithNullConstraintName() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddCompositePrimaryKey(users, null, List.of()));
	}
	
	@Test
	void renderAddCompositePrimaryKeyWithNullColumns() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAddCompositePrimaryKey(users, "pk", null));
	}
	
	@Test
	void renderEnableAuditingWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderEnableAuditing(null, SqlAuditConfig.DEFAULT));
	}
	
	@Test
	void renderEnableAuditingWithNullConfig() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderEnableAuditing(users, null));
	}
	
	@Test
	void renderDisableAuditingWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDisableAuditing(null, SqlAuditConfig.DEFAULT));
	}
	
	@Test
	void renderDisableAuditingWithNullConfig() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderDisableAuditing(users, null));
	}
	
	@Test
	void renderDropConstraintWithNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropConstraint(null, "ck"));
	}
	
	@Test
	void renderDropConstraintWithNullConstraintName() {
		SqlTable<Object> users = usersTable();
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropConstraint(users, null));
	}
	
	@Test
	void renderAddColumnWithEmptyOptions() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, SqlColumnOptions.EMPTY);
		String sql = rendered.sql();
		assertTrue(sql.contains("ALTER TABLE \"users\" ADD COLUMN \"id\""));
		assertTrue(sql.contains(SqlDialects.DEFAULT.getTypeName(SqlTypes.INTEGER)));
		assertFalse(sql.contains("NOT NULL"));
		assertFalse(sql.contains("UNIQUE"));
		assertFalse(sql.contains("IDENTITY"));
		assertFalse(sql.contains("DEFAULT"));
		assertFalse(sql.contains("REFERENCES"));
		assertFalse(sql.contains("CHECK"));
	}
	
	@Test
	void renderAddColumnNotNullOption() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlColumnOptions options = new SqlColumnOptions(true, false, false, Optional.empty(), null, null);
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, options);
		assertTrue(rendered.sql().contains("NOT NULL"));
	}
	
	@Test
	void renderAddColumnUniqueOption() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlColumnOptions options = new SqlColumnOptions(false, true, false, Optional.empty(), null, null);
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, options);
		assertTrue(rendered.sql().contains("UNIQUE"));
	}
	
	@Test
	void renderAddColumnAutoIncrementOption() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlColumnOptions options = new SqlColumnOptions(false, false, true, Optional.empty(), null, null);
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, options);
		assertTrue(rendered.sql().contains("GENERATED"));
		assertTrue(rendered.sql().contains("IDENTITY"));
	}
	
	@Test
	void renderAddColumnDefaultValueOption() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlColumnOptions options = new SqlColumnOptions(false, false, false, Optional.of(0), null, null);
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, options);
		assertTrue(rendered.sql().contains("DEFAULT"));
		assertTrue(rendered.sql().contains("0"));
	}
	
	@Test
	void renderAddColumnReferencesTableOption() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumnOptions options = new SqlColumnOptions(false, false, false, Optional.empty(), other, null);
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, options);
		assertTrue(rendered.sql().contains("REFERENCES"));
		assertTrue(rendered.sql().contains("\"other\""));
	}
	
	@Test
	void renderAddColumnCheckOption() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlColumnOptions options = new SqlColumnOptions(false, false, false, Optional.empty(), null, condition());
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, options);
		assertTrue(rendered.sql().contains("CHECK"));
		assertTrue(rendered.sql().contains("("));
		assertTrue(rendered.sql().contains(")"));
	}
	
	@Test
	void renderEnableAuditingWithVersionColumn() throws SqlException {
		List<SqlRendered> results = RENDERER.renderEnableAuditing(usersTable(), SqlAuditConfig.DEFAULT);
		String versionStatement = results.getFirst().sql();
		assertTrue(versionStatement.contains("DEFAULT"));
		assertTrue(versionStatement.contains("0"));
		assertTrue(versionStatement.contains("NOT NULL"));
	}
	
	@Test
	void renderEnableAuditingWithNonVersionColumn() throws SqlException {
		List<SqlRendered> results = RENDERER.renderEnableAuditing(usersTable(), SqlAuditConfig.DEFAULT);
		String nonVersionStatement = results.get(1).sql();
		assertFalse(nonVersionStatement.contains("DEFAULT 0"));
	}
	
	@Test
	void renderDisableAuditingWithAuditColumns() throws SqlException {
		List<SqlRendered> results = RENDERER.renderDisableAuditing(usersTable(), SqlAuditConfig.DEFAULT);
		assertEquals(SqlAuditConfig.DEFAULT.auditColumns().size(), results.size());
		for (SqlRendered rendered : results) {
			assertTrue(rendered.sql().contains("ALTER TABLE \"users\" DROP COLUMN"));
		}
	}
	
	@Test
	void renderAddUniqueConstraintWithSingleColumn() throws SqlException {
		SqlTable<Object> users = usersTable();
		List<SqlColumn<?, ?>> columns = List.of(idColumn(users));
		SqlRendered rendered = RENDERER.renderAddUniqueConstraint(users, "uq", columns);
		assertTrue(rendered.sql().contains("UNIQUE("));
		assertTrue(rendered.sql().contains("\"id\""));
		assertEquals(0, countCommas(rendered.sql()));
	}
	
	@Test
	void renderAddUniqueConstraintWithMultipleColumns() throws SqlException {
		SqlTable<Object> users = usersTable();
		List<SqlColumn<?, ?>> columns = List.of(idColumn(users), users.column("name", SqlTypes.TEXT, object -> ""));
		SqlRendered rendered = RENDERER.renderAddUniqueConstraint(users, "uq", columns);
		assertEquals(1, countCommas(rendered.sql()));
	}
	
	@Test
	void renderAddCompositePrimaryKeyWithSingleColumn() throws SqlException {
		SqlTable<Object> users = usersTable();
		List<SqlColumn<?, ?>> columns = List.of(idColumn(users));
		SqlRendered rendered = RENDERER.renderAddCompositePrimaryKey(users, "pk", columns);
		assertTrue(rendered.sql().contains("PRIMARY KEY("));
		assertTrue(rendered.sql().contains("\"id\""));
	}
	
	@Test
	void renderAddCompositePrimaryKeyWithMultipleColumns() throws SqlException {
		SqlTable<Object> users = usersTable();
		List<SqlColumn<?, ?>> columns = List.of(idColumn(users), users.column("name", SqlTypes.TEXT, object -> ""));
		SqlRendered rendered = RENDERER.renderAddCompositePrimaryKey(users, "pk", columns);
		assertEquals(1, countCommas(rendered.sql()));
	}
	
	@Test
	void renderAddUniqueConstraintWithEmptyColumns() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlRendered rendered = RENDERER.renderAddUniqueConstraint(users, "uq", List.of());
		assertTrue(rendered.sql().contains("UNIQUE()"));
	}
	
	@Test
	void renderAddCompositePrimaryKeyWithEmptyColumns() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlRendered rendered = RENDERER.renderAddCompositePrimaryKey(users, "pk", List.of());
		assertTrue(rendered.sql().contains("PRIMARY KEY()"));
	}
	
	@Test
	void renderAddForeignKeyWithNoActionActions() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		List<SqlColumn<?, ?>> columns = List.of(idColumn(users));
		List<SqlColumn<?, ?>> referenced = List.of(other.column("oid", SqlTypes.INTEGER, object -> 0));
		SqlRendered rendered = RENDERER.renderAddForeignKey(users, "fk", columns, other, referenced, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		String sql = rendered.sql();
		assertTrue(sql.contains("FOREIGN KEY"));
		assertTrue(sql.contains("REFERENCES"));
		assertTrue(sql.contains("ON DELETE"));
		assertTrue(sql.contains("ON UPDATE"));
		assertTrue(sql.contains("NO ACTION"));
	}
	
	@Test
	void renderAddForeignKeyWithCascadeActions() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		List<SqlColumn<?, ?>> columns = List.of(idColumn(users));
		List<SqlColumn<?, ?>> referenced = List.of(other.column("oid", SqlTypes.INTEGER, object -> 0));
		SqlRendered rendered = RENDERER.renderAddForeignKey(users, "fk", columns, other, referenced, SqlReferentialAction.CASCADE, SqlReferentialAction.SET_NULL);
		assertTrue(rendered.sql().contains("CASCADE"));
		assertTrue(rendered.sql().contains("SET NULL"));
	}
	
	@Test
	void renderRenameTableProducesAlterRename() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> customers = SqlTable.create(Object.class, "customers");
		SqlRendered rendered = RENDERER.renderRenameTable(users, customers);
		String sql = rendered.sql();
		assertTrue(sql.contains("ALTER TABLE"));
		assertTrue(sql.contains("\"users\""));
		assertTrue(sql.contains("RENAME"));
		assertTrue(sql.contains("TO"));
		assertTrue(sql.contains("\"customers\""));
	}
	
	@Test
	void renderDropColumnProducesDropColumn() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlRendered rendered = RENDERER.renderDropColumn(users, idColumn(users));
		assertTrue(rendered.sql().contains("ALTER TABLE \"users\" DROP COLUMN \"id\""));
	}
	
	@Test
	void renderRenameColumnProducesRenameColumn() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlColumn<Object, Integer> id = idColumn(users);
		SqlColumn<Object, Integer> identifier = users.column("identifier", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderRenameColumn(users, id, identifier);
		String sql = rendered.sql();
		assertTrue(sql.contains("RENAME"));
		assertTrue(sql.contains("COLUMN"));
		assertTrue(sql.contains("\"id\""));
		assertTrue(sql.contains("TO"));
		assertTrue(sql.contains("\"identifier\""));
	}
	
	@Test
	void renderAddCheckConstraintProducesCheckClause() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlRendered rendered = RENDERER.renderAddCheckConstraint(users, "ck", condition());
		String sql = rendered.sql();
		assertTrue(sql.contains("ADD CONSTRAINT"));
		assertTrue(sql.contains("\"ck\""));
		assertTrue(sql.contains("CHECK"));
		assertTrue(sql.contains("("));
		assertTrue(sql.contains(")"));
	}
	
	@Test
	void renderDropConstraintProducesDropConstraint() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlRendered rendered = RENDERER.renderDropConstraint(users, "ck");
		assertTrue(rendered.sql().contains("ALTER TABLE \"users\" DROP CONSTRAINT"));
		assertTrue(rendered.sql().contains("\"ck\""));
	}
	
	@Test
	void renderAddColumnWithAllOptionsCombined() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumnOptions options = new SqlColumnOptions(true, true, true, Optional.of(0), other, condition());
		SqlRendered rendered = RENDERER.renderAddColumn(users, idColumn(users), SqlTypes.INTEGER, options);
		String sql = rendered.sql();
		assertTrue(sql.contains("NOT NULL"));
		assertTrue(sql.contains("UNIQUE"));
		assertTrue(sql.contains("IDENTITY"));
		assertTrue(sql.contains("DEFAULT"));
		assertTrue(sql.contains("REFERENCES"));
		assertTrue(sql.contains("CHECK"));
		assertTrue(sql.indexOf("NOT NULL") < sql.indexOf("UNIQUE"));
		assertTrue(sql.indexOf("UNIQUE") < sql.indexOf("IDENTITY"));
		assertTrue(sql.indexOf("IDENTITY") < sql.indexOf("DEFAULT"));
		assertTrue(sql.indexOf("DEFAULT") < sql.indexOf("REFERENCES"));
		assertTrue(sql.indexOf("REFERENCES") < sql.indexOf("CHECK"));
	}
	
	@Test
	void renderAddForeignKeyWithMultipleColumnsAndReferencedColumns() throws SqlException {
		SqlTable<Object> users = usersTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		List<SqlColumn<?, ?>> columns = List.of(idColumn(users), users.column("name", SqlTypes.TEXT, object -> ""));
		List<SqlColumn<?, ?>> referenced = List.of(other.column("oid", SqlTypes.INTEGER, object -> 0), other.column("oname", SqlTypes.TEXT, object -> ""));
		SqlRendered rendered = RENDERER.renderAddForeignKey(users, "fk", columns, other, referenced, SqlReferentialAction.CASCADE, SqlReferentialAction.CASCADE);
		String sql = rendered.sql();
		assertEquals(2, countCommas(sql));
		assertTrue(sql.contains("REFERENCES"));
		assertTrue(sql.contains("\"other\""));
		assertTrue(sql.contains("ON DELETE"));
		assertTrue(sql.contains("ON UPDATE"));
		assertTrue(sql.contains("CASCADE"));
	}
	
	@Test
	void renderEnableAuditingWithFullDefaultConfig() throws SqlException {
		List<SqlRendered> results = RENDERER.renderEnableAuditing(usersTable(), SqlAuditConfig.DEFAULT);
		assertEquals(SqlAuditConfig.DEFAULT.auditColumns().size(), results.size());
		List<SqlAuditColumn> auditColumns = SqlAuditConfig.DEFAULT.auditColumns();
		for (int i = 0; i < auditColumns.size(); i++) {
			String sql = results.get(i).sql();
			assertTrue(sql.contains("ALTER TABLE \"users\" ADD COLUMN"));
			if (auditColumns.get(i).role() == SqlAuditRole.VERSION) {
				assertTrue(sql.contains("DEFAULT"));
				assertTrue(sql.contains("0"));
				assertTrue(sql.contains("NOT NULL"));
			} else {
				assertFalse(sql.contains("DEFAULT 0"));
			}
		}
	}
}
