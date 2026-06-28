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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationRendererTest {
	
	private static @NonNull SqlTable<Object> twoColumnTable() {
		SqlTable<Object> table = SqlTable.create(Object.class, "test_table");
		table.column("id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		table.column("name", SqlTestFixtures.STRING_TYPE, object -> "test");
		return table;
	}
	
	private static @NonNull SqlColumn<?, ?> column(@NonNull SqlTable<Object> table, @NonNull String name) {
		return java.util.Objects.requireNonNull(table.columnForName(name));
	}
	
	private static @NonNull String allSql(@NonNull List<SqlRendered> rendered) {
		return rendered.stream().map(SqlRendered::sql).collect(Collectors.joining(" "));
	}
	
	private static @NonNull SqlTable<Object> referencedTable() {
		SqlTable<Object> table = SqlTable.create(Object.class, "ref_table");
		table.column("ref_id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		return table;
	}
	
	@Test
	void constructWithDialect() throws SqlException {
		assertDoesNotThrow(() -> new SqlMigrationRenderer(SqlTestFixtures.DIALECT));
	}
	
	@Test
	void constructWithNullDialect() throws SqlException {
		assertThrows(NullPointerException.class, () -> new SqlMigrationRenderer(null));
	}
	
	@Test
	void renderWithNullOperationsList() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		assertThrows(NullPointerException.class, () -> renderer.render(null));
	}
	
	@Test
	void renderEmptyOperationListReturnsEmpty() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		assertTrue(renderer.render(List.of()).isEmpty());
	}
	
	@Test
	void renderAlterColumnOperationDispatch() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "name"), List.of(new SqlSetNullableAlteration(true)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertFalse(rendered.isEmpty());
		assertTrue(allSql(rendered).contains("ALTER"));
	}
	
	@Test
	void renderEnableAuditingOperationDispatch() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = SqlTestFixtures.auditedTable();
		SqlEnableAuditingOperation operation = new SqlEnableAuditingOperation(table, SqlAuditConfig.DEFAULT);
		
		List<SqlRendered> rendered = assertDoesNotThrow(() -> renderer.render(List.of(operation)));
		
		assertNotNull(rendered);
	}
	
	@Test
	void renderDisableAuditingOperationDispatch() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = SqlTestFixtures.auditedTable();
		SqlDisableAuditingOperation operation = new SqlDisableAuditingOperation(table, SqlAuditConfig.DEFAULT);
		
		List<SqlRendered> rendered = assertDoesNotThrow(() -> renderer.render(List.of(operation)));
		
		assertNotNull(rendered);
	}
	
	@Test
	void renderConstraintWithRebuildDialectUsesRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAddUniqueConstraintOperation operation = new SqlAddUniqueConstraintOperation(table, "uq_name", List.of(column(table, "name")));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
	}
	
	@Test
	void renderConstraintWithoutRebuildDialectUsesRenderOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAddUniqueConstraintOperation operation = new SqlAddUniqueConstraintOperation(table, "uq_name", List.of(column(table, "name")));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
		assertFalse(allSql(rendered).contains("CREATE TABLE"));
		assertTrue(allSql(rendered).contains("UNIQUE"));
	}
	
	@Test
	void renderNonConstraintWithRebuildDialectUsesRenderOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlCreateTableOperation operation = new SqlCreateTableOperation(table, List.of(), List.of());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
	}
	
	@Test
	void renderCreateTableOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlCreateTableOperation operation = new SqlCreateTableOperation(table, List.of(), List.of());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
	}
	
	@Test
	void renderDropTableOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlDropTableOperation operation = new SqlDropTableOperation(twoColumnTable());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("DROP TABLE"));
	}
	
	@Test
	void renderRenameTableOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> from = SqlTable.create(Object.class, "old_table");
		SqlTable<Object> to = SqlTable.create(Object.class, "new_table");
		SqlRenameTableOperation operation = new SqlRenameTableOperation(from, to);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("new_table"));
	}
	
	@Test
	void renderAddColumnOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAddColumnOperation operation = new SqlAddColumnOperation(column(table, "name"), SqlTestFixtures.STRING_TYPE, SqlColumnOptions.EMPTY);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("ADD"));
	}
	
	@Test
	void renderDropColumnOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlDropColumnOperation operation = new SqlDropColumnOperation(column(table, "name"));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("DROP"));
	}
	
	@Test
	void renderRenameColumnOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlRenameColumnOperation operation = new SqlRenameColumnOperation(column(table, "id"), column(table, "name"));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertFalse(rendered.isEmpty());
		assertTrue(allSql(rendered).contains("name"));
	}
	
	@Test
	void renderCreateIndexOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlIndex index = new SqlIndex("idx_id", List.of(column(table, "id")), false, SqlIndexMethod.BTREE);
		SqlCreateIndexOperation operation = new SqlCreateIndexOperation(index, table);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE") && allSql(rendered).contains("INDEX"));
	}
	
	@Test
	void renderDropIndexOperationWithTable() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlDropIndexOperation operation = new SqlDropIndexOperation(table, "idx_id");
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("DROP") && allSql(rendered).contains("INDEX"));
	}
	
	@Test
	void renderDropIndexOperationWithoutTable() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlDropIndexOperation operation = new SqlDropIndexOperation(null, "idx_id");
		
		List<SqlRendered> rendered = assertDoesNotThrow(() -> renderer.render(List.of(operation)));
		
		assertTrue(allSql(rendered).contains("INDEX"));
	}
	
	@Test
	void renderRenameIndexOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlRenameIndexOperation operation = new SqlRenameIndexOperation(table, "idx_old", "idx_new");
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("idx_new"));
	}
	
	@Test
	void renderAddUniqueConstraintOperationNonRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAddUniqueConstraintOperation operation = new SqlAddUniqueConstraintOperation(table, "uq_name", List.of(column(table, "name")));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("UNIQUE"));
	}
	
	@Test
	void renderAddForeignKeyOperationNonRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlTable<Object> referenced = referencedTable();
		SqlAddForeignKeyOperation operation = new SqlAddForeignKeyOperation(
			table, "fk_id", List.of(column(table, "id")), referenced, List.of(column(referenced, "ref_id")), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION
		);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("REFERENCES"));
	}
	
	@Test
	void renderAddCheckConstraintOperationNonRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAddCheckConstraintOperation operation = new SqlAddCheckConstraintOperation(table, "ck_name", SqlTestFixtures.alwaysCondition());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CHECK"));
	}
	
	@Test
	void renderAddCompositePrimaryKeyOperationNonRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAddCompositePrimaryKeyOperation operation = new SqlAddCompositePrimaryKeyOperation(table, "pk_name", List.of(column(table, "id"), column(table, "name")));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("PRIMARY") && allSql(rendered).contains("KEY"));
	}
	
	@Test
	void renderDropConstraintOperation() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlDropConstraintOperation operation = new SqlDropConstraintOperation(table, "uq_name");
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("DROP") && allSql(rendered).contains("CONSTRAINT"));
	}
	
	@Test
	void renderExecuteDataOperationProducesEmpty() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlExecuteDataOperation operation = new SqlExecuteDataOperation(twoColumnTable());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
		assertEquals("", rendered.getFirst().sql());
	}
	
	@Test
	void renderAlterColumnSetTypeAlteration() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlSetTypeAlteration(SqlTypes.LONG)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
		assertTrue(allSql(rendered).contains("ALTER"));
	}
	
	@Test
	void renderAlterColumnSetNullableAlteration() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "name"), List.of(new SqlSetNullableAlteration(false)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
		assertTrue(allSql(rendered).contains("ALTER"));
	}
	
	@Test
	void renderAlterColumnSetDefaultAlteration() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlSetDefaultAlteration(42)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
		assertTrue(allSql(rendered).contains("DEFAULT"));
	}
	
	@Test
	void renderAlterColumnDropDefaultAlteration() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlDropDefaultAlteration()));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
		assertTrue(allSql(rendered).contains("DEFAULT"));
	}
	
	@Test
	void renderAlterColumnWithNoAlterations() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(rendered.isEmpty());
	}
	
	@Test
	void renderAlterColumnWithMultipleAlterations() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(
			column(table, "id"), List.of(new SqlSetTypeAlteration(SqlTypes.LONG), new SqlSetNullableAlteration(true), new SqlDropDefaultAlteration())
		);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(3, rendered.size());
	}
	
	@Test
	void renderAlterColumnViaRebuildSetType() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlSetTypeAlteration(SqlTypes.LONG)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
		assertTrue(allSql(rendered).contains("name"));
	}
	
	@Test
	void renderAlterColumnViaRebuildSetNullable() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlSetNullableAlteration(true)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
	}
	
	@Test
	void renderAlterColumnViaRebuildSetDefault() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlSetDefaultAlteration(42)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
	}
	
	@Test
	void renderAlterColumnViaRebuildDropDefault() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlDropDefaultAlteration()));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
	}
	
	@Test
	void renderAddUniqueConstraintViaRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAddUniqueConstraintOperation operation = new SqlAddUniqueConstraintOperation(table, "uq_name", List.of(column(table, "name")));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
		assertTrue(allSql(rendered).contains("UNIQUE"));
	}
	
	@Test
	void renderAddCompositePrimaryKeyViaRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAddCompositePrimaryKeyOperation operation = new SqlAddCompositePrimaryKeyOperation(table, "pk_name", List.of(column(table, "id"), column(table, "name")));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
		assertTrue(allSql(rendered).contains("PRIMARY") && allSql(rendered).contains("KEY"));
	}
	
	@Test
	void renderAddCheckConstraintViaRebuild() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAddCheckConstraintOperation operation = new SqlAddCheckConstraintOperation(table, "ck_name", SqlTestFixtures.alwaysCondition());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
		assertTrue(allSql(rendered).contains("CHECK"));
	}
	
	@Test
	void renderAddForeignKeyViaRebuildNoReferentialActions() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlTable<Object> referenced = referencedTable();
		SqlAddForeignKeyOperation operation = new SqlAddForeignKeyOperation(
			table, "fk_id", List.of(column(table, "id")), referenced, List.of(column(referenced, "ref_id")), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION
		);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("REFERENCES"));
		assertFalse(allSql(rendered).contains("ON DELETE"));
		assertFalse(allSql(rendered).contains("ON UPDATE"));
	}
	
	@Test
	void renderAddForeignKeyViaRebuildWithOnDeleteAction() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlTable<Object> referenced = referencedTable();
		SqlAddForeignKeyOperation operation = new SqlAddForeignKeyOperation(
			table, "fk_id", List.of(column(table, "id")), referenced, List.of(column(referenced, "ref_id")), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION
		);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("ON DELETE"));
		assertFalse(allSql(rendered).contains("ON UPDATE"));
	}
	
	@Test
	void renderAddForeignKeyViaRebuildWithOnUpdateAction() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlTable<Object> referenced = referencedTable();
		SqlAddForeignKeyOperation operation = new SqlAddForeignKeyOperation(
			table, "fk_id", List.of(column(table, "id")), referenced, List.of(column(referenced, "ref_id")), SqlReferentialAction.NO_ACTION, SqlReferentialAction.CASCADE
		);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("ON UPDATE"));
		assertFalse(allSql(rendered).contains("ON DELETE"));
	}
	
	@Test
	void renderNonConstraintOperationIsNotRebuilt() {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlDropConstraintOperation operation = new SqlDropConstraintOperation(table, "uq_name");
		
		// SqlDropConstraintOperation is not in the rebuilt-constraint set, so even under SQLite it bypasses the table
		// rebuild and reaches renderOperation -> SQLite's renderDropConstraint, which rejects the unsupported feature.
		assertThrows(SqlDialectFeatureException.class, () -> renderer.render(List.of(operation)));
	}
	
	@Test
	void renderSingleCreateTableReturnsSingletonList() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlCreateTableOperation operation = new SqlCreateTableOperation(twoColumnTable(), List.of(), List.of());
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertEquals(1, rendered.size());
	}
	
	@Test
	void renderAlterColumnViaRebuildPreservesUnrelatedColumns() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column(table, "id"), List.of(new SqlSetNullableAlteration(true)));
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("id"));
		assertTrue(allSql(rendered).contains("name"));
	}
	
	@Test
	void renderMultipleMixedOperationsAccumulatesInOrder() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlTestFixtures.DIALECT);
		SqlTable<Object> table = twoColumnTable();
		List<SqlMigrationOperation> operations = List.of(
			new SqlCreateTableOperation(table, List.of(), List.of()),
			new SqlAddColumnOperation(column(table, "name"), SqlTestFixtures.STRING_TYPE, SqlColumnOptions.EMPTY),
			new SqlDropConstraintOperation(table, "uq_name")
		);
		
		List<SqlRendered> rendered = renderer.render(operations);
		
		assertTrue(rendered.size() >= 3);
		String first = rendered.getFirst().sql();
		assertTrue(first.contains("CREATE TABLE"));
		assertTrue(allSql(rendered).contains("CONSTRAINT"));
	}
	
	@Test
	void renderAlterColumnViaRebuildWithMultipleAlterations() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(
			column(table, "id"), List.of(new SqlSetTypeAlteration(SqlTypes.LONG), new SqlSetNullableAlteration(true), new SqlSetDefaultAlteration(42))
		);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("CREATE TABLE"));
		assertTrue(allSql(rendered).contains("DEFAULT"));
	}
	
	@Test
	void renderForeignKeyViaRebuildWithBothReferentialActions() throws SqlException {
		SqlMigrationRenderer renderer = new SqlMigrationRenderer(SqlDialects.SQLITE);
		SqlTable<Object> table = twoColumnTable();
		SqlTable<Object> referenced = referencedTable();
		SqlAddForeignKeyOperation operation = new SqlAddForeignKeyOperation(
			table, "fk_id", List.of(column(table, "id")), referenced, List.of(column(referenced, "ref_id")), SqlReferentialAction.CASCADE, SqlReferentialAction.SET_NULL
		);
		
		List<SqlRendered> rendered = renderer.render(List.of(operation));
		
		assertTrue(allSql(rendered).contains("ON DELETE"));
		assertTrue(allSql(rendered).contains("ON UPDATE"));
	}
}
