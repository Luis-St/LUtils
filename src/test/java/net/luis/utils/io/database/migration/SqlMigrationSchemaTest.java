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
import net.luis.utils.io.database.SqlTestFixtures.RecordingDataSource;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlSchemaObjectNotFoundException;
import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationSchema}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationSchemaTest {
	
	private static final SqlColumnOptions NOT_NULL = new SqlColumnOptions(true, false, false, Optional.empty(), null, null);
	
	private static SqlMigrationSchema apply(SqlMigrationSchema base, SqlMigrationOperation operation) throws SqlException {
		return SqlMigrationSchema.applyOperations(base, List.of(operation));
	}
	
	private static Fixture baseTable() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> id = table.column("id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlColumn<Object, String> name = table.column("name", SqlTestFixtures.STRING_TYPE, object -> "x");
		SqlCreateTableOperation create = new SqlCreateTableOperation(table, List.of(new SqlColumnDefinition(id, SqlTestFixtures.INTEGER_TYPE, NOT_NULL), new SqlColumnDefinition(name, SqlTestFixtures.STRING_TYPE, NOT_NULL)), List.of());
		return new Fixture(table, id, name, SqlMigrationSchema.applyOperations(SqlMigrationSchema.empty(), List.of(create)));
	}
	
	private static SqlSchemaColumnInfo columnInfo(String table, String column, int jdbcType, boolean nullable, boolean primaryKey, int ordinal) {
		return new SqlSchemaColumnInfo(table, column, jdbcType, null, nullable, false, primaryKey, false, ordinal);
	}
	
	private static void enqueueSchema(@NonNull RecordingDataSource dataSource, @NonNull Map<String, ?> column) {
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(Map.of("TABLE_NAME", "it_table"))));
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of()));
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of()));
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(column)));
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of()));
	}
	
	@Test
	void emptyHasNoTablesOrConstraints() {
		SqlMigrationSchema schema = SqlMigrationSchema.empty();
		assertTrue(schema.tableNames().isEmpty());
		assertTrue(schema.extractColumnInfos().isEmpty());
		assertTrue(schema.extractCheckConstraints().isEmpty());
	}
	
	@Test
	void emptyWithSchemaName() {
		assertDoesNotThrow(() -> SqlMigrationSchema.empty("custom"));
		assertTrue(SqlMigrationSchema.empty("custom").tableNames().isEmpty());
	}
	
	@Test
	void fromSnapshotSingleTableSingleColumn() throws SqlException {
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(columnInfo("t", "id", Types.INTEGER, false, true, 0)), Map.of());
		assertTrue(schema.hasTable("t"));
		assertTrue(schema.column("t", "id").primaryKey());
		assertEquals(1, schema.extractColumnInfos().size());
		assertEquals("id", schema.extractColumnInfos().getFirst().columnName());
	}
	
	@Test
	void emptyWithNullSchema() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.empty(null));
	}
	
	@Test
	void fromSnapshotWithNullSchema() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.fromSnapshot(null, List.of(), Map.of()));
	}
	
	@Test
	void fromSnapshotWithNullColumnInfos() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.fromSnapshot("public", null, Map.of()));
	}
	
	@Test
	void fromSnapshotWithNullCheckConstraints() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.fromSnapshot("public", List.of(), null));
	}
	
	@Test
	void applyOperationsWithNullBase() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.applyOperations(null, List.of()));
	}
	
	@Test
	void applyOperationsWithNullOperations() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.applyOperations(SqlMigrationSchema.empty(), null));
	}
	
	@Test
	void tableMissingThrows() {
		assertThrows(SqlSchemaObjectNotFoundException.class, () -> SqlMigrationSchema.empty().table("nope"));
	}
	
	@Test
	void hasTableWithNullName() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.empty().hasTable(null));
	}
	
	@Test
	void columnMissingTableThrows() {
		assertThrows(SqlSchemaObjectNotFoundException.class, () -> SqlMigrationSchema.empty().column("nope", "x"));
	}
	
	@Test
	void columnMissingColumnThrows() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		assertThrows(SqlSchemaObjectNotFoundException.class, () -> schema.column("t", "missing"));
	}
	
	@Test
	void columnWithNullTableName() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.empty().column(null, "x"));
	}
	
	@Test
	void columnWithNullColumnName() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.empty().column("t", null));
	}
	
	@Test
	void typedColumnWithNullType() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		assertThrows(NullPointerException.class, () -> schema.column("t", "id", null));
	}
	
	@Test
	void typedColumnWrongTypeThrows() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		assertThrows(SqlSchemaObjectNotFoundException.class, () -> schema.column("t", "id", String.class));
	}
	
	@Test
	void hasColumnWithNullTableName() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.empty().hasColumn(null, "x"));
	}
	
	@Test
	void hasColumnWithNullColumnName() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.empty().hasColumn("t", null));
	}
	
	@Test
	void checkConstraintsMissingTableThrows() {
		assertThrows(SqlSchemaObjectNotFoundException.class, () -> SqlMigrationSchema.empty().checkConstraints("nope"));
	}
	
	@Test
	void checkConstraintsWithNullTableName() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.empty().checkConstraints(null));
	}
	
	@Test
	void fromSnapshotSortsColumnsByOrdinal() throws SqlException {
		List<SqlSchemaColumnInfo> infos = List.of(columnInfo("t", "second", Types.INTEGER, true, false, 1), columnInfo("t", "first", Types.INTEGER, true, false, 0));
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(infos, Map.of());
		List<SqlSchemaColumnInfo> extracted = schema.extractColumnInfos();
		assertEquals("first", extracted.get(0).columnName());
		assertEquals("second", extracted.get(1).columnName());
	}
	
	@Test
	void fromSnapshotSinglePrimaryKeyNoComposite() throws SqlException {
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(columnInfo("t", "id", Types.INTEGER, false, true, 0)), Map.of());
		assertTrue(schema.column("t", "id").primaryKey());
	}
	
	@Test
	void fromSnapshotMultiplePrimaryKeysGeneratesComposite() throws SqlException {
		List<SqlSchemaColumnInfo> infos = List.of(columnInfo("t", "a", Types.INTEGER, false, true, 0), columnInfo("t", "b", Types.INTEGER, false, true, 1));
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(infos, Map.of());
		assertTrue(schema.column("t", "a").primaryKey());
		assertTrue(schema.column("t", "b").primaryKey());
	}
	
	@Test
	void fromSnapshotCopiesCheckConstraints() throws SqlException {
		List<SqlCheckConstraintInfo> source = new ArrayList<>(List.of(new SqlCheckConstraintInfo("chk", "x > 0")));
		Map<String, List<SqlCheckConstraintInfo>> checks = new HashMap<>();
		checks.put("t", source);
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(columnInfo("t", "id", Types.INTEGER, false, false, 0)), checks);
		source.clear();
		assertEquals(1, schema.checkConstraints("t").size());
	}
	
	@Test
	void fromSnapshotWithParameterizedType() throws SqlException {
		SqlSchemaColumnInfo info = new SqlSchemaColumnInfo("t", "name", Types.VARCHAR, net.luis.utils.io.database.type.parameter.SqlParameter.length(64), true, false, false, false, 0);
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(info), Map.of());
		assertNotNull(schema.extractColumnInfos().getFirst().parameter());
	}
	
	@Test
	void fromSnapshotBuildsNotNullOnlyAndPlainColumns() throws SqlException {
		List<SqlSchemaColumnInfo> infos = List.of(columnInfo("t", "a", Types.INTEGER, false, false, 0), columnInfo("t", "b", Types.INTEGER, true, false, 1));
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(infos, Map.of());
		assertFalse(schema.column("t", "a").nullable());
		assertTrue(schema.column("t", "b").nullable());
		assertFalse(schema.column("t", "a").primaryKey());
		assertFalse(schema.column("t", "a").autoIncrement());
		assertFalse(schema.column("t", "a").unique());
	}
	
	@Test
	void extractColumnInfosParameterizedAndScalar() throws SqlException {
		SqlSchemaColumnInfo parameterized = new SqlSchemaColumnInfo("t", "name", Types.VARCHAR, net.luis.utils.io.database.type.parameter.SqlParameter.length(64), true, false, false, false, 0);
		SqlSchemaColumnInfo scalar = columnInfo("t", "id", Types.INTEGER, true, false, 1);
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(parameterized, scalar), Map.of());
		Map<String, SqlSchemaColumnInfo> byName = new HashMap<>();
		schema.extractColumnInfos().forEach(info -> byName.put(info.columnName(), info));
		assertNotNull(byName.get("name").parameter());
		assertNull(byName.get("id").parameter());
	}
	
	@Test
	void applyCreateTableAddsColumnsAndChecks() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		assertTrue(schema.hasTable("t"));
		assertTrue(schema.hasColumn("t", "id"));
		assertTrue(schema.hasColumn("t", "name"));
		assertTrue(schema.checkConstraints("t").isEmpty());
	}
	
	@Test
	void applyDropTableRemovesTableAndChecks() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema withCheck = apply(fixture.base(), new SqlAddCheckConstraintOperation(fixture.table(), "chk", SqlTestFixtures.alwaysCondition()));
		SqlMigrationSchema schema = apply(withCheck, new SqlDropTableOperation(fixture.table()));
		assertFalse(schema.hasTable("t"));
		assertThrows(SqlSchemaObjectNotFoundException.class, () -> schema.checkConstraints("t"));
	}
	
	@Test
	void applyRenameTableMovesColumnsAndChecks() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema withCheck = apply(fixture.base(), new SqlAddCheckConstraintOperation(fixture.table(), "chk", SqlTestFixtures.alwaysCondition()));
		SqlTable<Object> renamed = SqlTable.create(Object.class, "t2");
		SqlMigrationSchema schema = apply(withCheck, new SqlRenameTableOperation(fixture.table(), renamed));
		assertFalse(schema.hasTable("t"));
		assertTrue(schema.hasTable("t2"));
		assertTrue(schema.hasColumn("t2", "id"));
		assertEquals(1, schema.checkConstraints("t2").size());
	}
	
	@Test
	void applyRenameTableMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> from = SqlTable.create(Object.class, "other");
		SqlTable<Object> to = SqlTable.create(Object.class, "other2");
		SqlMigrationSchema schema = apply(fixture.base(), new SqlRenameTableOperation(from, to));
		assertEquals(Set.of("t"), schema.tableNames());
	}
	
	@Test
	void applyAddColumnAppendsColumn() throws SqlException {
		Fixture fixture = baseTable();
		SqlColumn<Object, Integer> age = fixture.table().column("age", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAddColumnOperation(age, SqlTestFixtures.INTEGER_TYPE, SqlColumnOptions.EMPTY));
		assertTrue(schema.hasColumn("t", "age"));
		assertEquals(3, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyAddColumnToMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> age = other.column("age", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAddColumnOperation(age, SqlTestFixtures.INTEGER_TYPE, SqlColumnOptions.EMPTY));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyDropColumnRemovesColumn() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlDropColumnOperation(fixture.name()));
		assertFalse(schema.hasColumn("t", "name"));
		assertEquals(1, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyDropColumnMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> column = other.column("id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlDropColumnOperation(column));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyRenameColumnRenamesMatch() throws SqlException {
		Fixture fixture = baseTable();
		SqlColumn<Object, Integer> renamed = fixture.table().column("identifier", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlRenameColumnOperation(fixture.id(), renamed));
		assertTrue(schema.hasColumn("t", "identifier"));
		assertFalse(schema.hasColumn("t", "id"));
	}
	
	@Test
	void applyRenameColumnMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> from = other.column("id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> to = other.column("ident", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlRenameColumnOperation(from, to));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyRenameColumnNoMatchIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlColumn<Object, Integer> ghost = fixture.table().column("ghost", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> ghost2 = fixture.table().column("ghost2", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlRenameColumnOperation(ghost, ghost2));
		assertFalse(schema.hasColumn("t", "ghost"));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyAlterColumnAppliesAlterations() throws SqlException {
		Fixture fixture = baseTable();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(fixture.id(), List.of(new SqlSetTypeAlteration(SqlTestFixtures.STRING_TYPE), new SqlSetNullableAlteration(true)));
		SqlMigrationSchema schema = apply(fixture.base(), operation);
		assertEquals(Types.VARCHAR, schema.column("t", "id").type().jdbcType());
		assertTrue(schema.column("t", "id").nullable());
	}
	
	@Test
	void applyAlterColumnSetTypeParameterized() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAlterColumnOperation(fixture.id(), List.of(new SqlSetTypeAlteration(SqlTestFixtures.STRING_TYPE))));
		Map<String, SqlSchemaColumnInfo> byName = new HashMap<>();
		schema.extractColumnInfos().forEach(info -> byName.put(info.columnName(), info));
		assertEquals(Types.VARCHAR, byName.get("id").jdbcType());
		assertNotNull(byName.get("id").parameter());
	}
	
	@Test
	void applyAlterColumnSetTypeScalar() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAlterColumnOperation(fixture.name(), List.of(new SqlSetTypeAlteration(SqlTestFixtures.INTEGER_TYPE))));
		Map<String, SqlSchemaColumnInfo> byName = new HashMap<>();
		schema.extractColumnInfos().forEach(info -> byName.put(info.columnName(), info));
		assertEquals(Types.INTEGER, byName.get("name").jdbcType());
		assertNull(byName.get("name").parameter());
	}
	
	@Test
	void applyAlterColumnDefaultAlterationsAreNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAlterColumnOperation(fixture.id(), List.of(new SqlSetDefaultAlteration("d"), new SqlDropDefaultAlteration())));
		assertEquals(Types.INTEGER, schema.column("t", "id").type().jdbcType());
		assertFalse(schema.column("t", "id").nullable());
	}
	
	@Test
	void applyAlterColumnMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> column = other.column("id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAlterColumnOperation(column, List.of(new SqlSetNullableAlteration(true))));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyAddCheckConstraintAppends() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAddCheckConstraintOperation(fixture.table(), "chk", SqlTestFixtures.alwaysCondition()));
		assertEquals(1, schema.checkConstraints("t").size());
		assertEquals("chk", schema.checkConstraints("t").getFirst().constraintName());
	}
	
	@Test
	void applyDropConstraintRemovesCheck() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema withCheck = apply(fixture.base(), new SqlAddCheckConstraintOperation(fixture.table(), "chk", SqlTestFixtures.alwaysCondition()));
		SqlMigrationSchema schema = apply(withCheck, new SqlDropConstraintOperation(fixture.table(), "chk"));
		assertTrue(schema.checkConstraints("t").isEmpty());
	}
	
	@Test
	void applyDropConstraintMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlMigrationSchema schema = apply(fixture.base(), new SqlDropConstraintOperation(other, "chk"));
		assertTrue(schema.checkConstraints("t").isEmpty());
	}
	
	@Test
	void applyEnableAuditingAddsAuditColumns() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlEnableAuditingOperation(fixture.table(), SqlAuditConfig.DEFAULT));
		assertEquals(2 + SqlAuditConfig.DEFAULT.auditColumns().size(), schema.extractColumnInfos().size());
		for (String name : SqlAuditConfig.DEFAULT.columnNames()) {
			assertTrue(schema.hasColumn("t", name));
		}
	}
	
	@Test
	void applyEnableAuditingMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlMigrationSchema schema = apply(fixture.base(), new SqlEnableAuditingOperation(other, SqlAuditConfig.DEFAULT));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyDisableAuditingRemovesAuditColumns() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema enabled = apply(fixture.base(), new SqlEnableAuditingOperation(fixture.table(), SqlAuditConfig.DEFAULT));
		SqlMigrationSchema schema = apply(enabled, new SqlDisableAuditingOperation(fixture.table(), SqlAuditConfig.DEFAULT));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyDisableAuditingMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlMigrationSchema schema = apply(fixture.base(), new SqlDisableAuditingOperation(other, SqlAuditConfig.DEFAULT));
		assertEquals(2, schema.extractColumnInfos().size());
	}
	
	@Test
	void applyAddCompositePrimaryKeySetsFlags() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAddCompositePrimaryKeyOperation(fixture.table(), "pk", List.of(fixture.id(), fixture.name())));
		assertTrue(schema.column("t", "id").primaryKey());
		assertTrue(schema.column("t", "name").primaryKey());
	}
	
	@Test
	void applyAddUniqueConstraintSetsFlags() throws SqlException {
		Fixture fixture = baseTable();
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAddUniqueConstraintOperation(fixture.table(), "uq", List.of(fixture.id())));
		assertTrue(schema.column("t", "id").unique());
	}
	
	@Test
	void applyConstraintFlagsMissingTableIsNoop() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> column = other.column("id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlMigrationSchema schema = apply(fixture.base(), new SqlAddUniqueConstraintOperation(other, "uq", List.of(column)));
		assertFalse(schema.column("t", "id").unique());
	}
	
	@Test
	void applyNoopOperationsLeaveSchemaUnchanged() throws SqlException {
		Fixture fixture = baseTable();
		SqlTable<Object> other = SqlTable.create(Object.class, "other");
		SqlColumn<Object, Integer> otherColumn = other.column("oid", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		List<SqlMigrationOperation> operations = List.of(
			new SqlAddForeignKeyOperation(fixture.table(), "fk", List.of(fixture.id()), other, List.of(otherColumn), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION),
			new SqlCreateIndexOperation(new SqlIndex("idx", List.of(fixture.id()), false, SqlIndexMethod.BTREE), fixture.table()),
			new SqlDropIndexOperation(fixture.table(), "idx"),
			new SqlRenameIndexOperation(fixture.table(), "a", "b"),
			new SqlExecuteDataOperation(fixture.table())
		);
		List<SqlSchemaColumnInfo> before = fixture.base().extractColumnInfos();
		SqlMigrationSchema schema = SqlMigrationSchema.applyOperations(fixture.base(), operations);
		assertEquals(before, schema.extractColumnInfos());
	}
	
	@Test
	void hasTableTrueAndFalse() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		assertTrue(schema.hasTable("t"));
		assertFalse(schema.hasTable("nope"));
	}
	
	@Test
	void hasColumnTrueAndFalse() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		assertTrue(schema.hasColumn("t", "id"));
		assertFalse(schema.hasColumn("t", "missing"));
		assertFalse(schema.hasColumn("nope", "id"));
	}
	
	@Test
	void typedColumnAssignableReturnsColumn() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		SqlColumn<Void, Integer> column = schema.column("t", "id", Integer.class);
		assertEquals("id", column.name());
	}
	
	@Test
	void tableNamesIsUnmodifiable() throws SqlException {
		SqlMigrationSchema schema = baseTable().base();
		assertThrows(UnsupportedOperationException.class, () -> schema.tableNames().add("x"));
	}
	
	@Test
	void applyOperationsSequenceReindexesOrdinals() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> c0 = table.column("c0", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> c1 = table.column("c1", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> c2 = table.column("c2", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> c3 = table.column("c3", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlCreateTableOperation create = new SqlCreateTableOperation(table, List.of(new SqlColumnDefinition(c0, SqlTestFixtures.INTEGER_TYPE, NOT_NULL), new SqlColumnDefinition(c1, SqlTestFixtures.INTEGER_TYPE, NOT_NULL), new SqlColumnDefinition(c2, SqlTestFixtures.INTEGER_TYPE, NOT_NULL)), List.of());
		SqlMigrationSchema schema = SqlMigrationSchema.applyOperations(SqlMigrationSchema.empty(), List.of(create, new SqlDropColumnOperation(c1), new SqlAddColumnOperation(c3, SqlTestFixtures.INTEGER_TYPE, SqlColumnOptions.EMPTY)));
		List<SqlSchemaColumnInfo> infos = schema.extractColumnInfos();
		assertEquals(3, infos.size());
		for (int i = 0; i < infos.size(); i++) {
			assertEquals(i, infos.get(i).ordinalPosition());
		}
	}
	
	@Test
	void applyOperationsRoundTripThroughFromSnapshot() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		SqlColumn<Object, Integer> id = table.column("id", SqlTestFixtures.INTEGER_TYPE, object -> 0);
		SqlColumn<Object, String> name = table.column("name", SqlTestFixtures.STRING_TYPE, object -> "x");
		SqlCreateTableOperation create = new SqlCreateTableOperation(table, List.of(new SqlColumnDefinition(id, SqlTestFixtures.INTEGER_TYPE, NOT_NULL)), List.of());
		SqlMigrationSchema schema = SqlMigrationSchema.applyOperations(SqlMigrationSchema.empty(), List.of(create, new SqlAddColumnOperation(name, SqlTestFixtures.STRING_TYPE, SqlColumnOptions.EMPTY), new SqlAddCheckConstraintOperation(table, "chk", SqlTestFixtures.alwaysCondition())));
		assertTrue(schema.hasColumn("t", "id"));
		assertTrue(schema.hasColumn("t", "name"));
		assertEquals(1, schema.checkConstraints("t").size());
	}
	
	@Test
	void loadWithNullDataSource() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.load(null, SqlTestFixtures.DIALECT));
	}
	
	@Test
	void loadWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.load(SqlTestFixtures.failingDataSource(), null));
	}
	
	@Test
	void loadWithNullSchema() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.load(SqlTestFixtures.failingDataSource(), SqlTestFixtures.DIALECT, null));
	}
	
	@Test
	void loadWithNullConnection() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.load((Connection) null, SqlTestFixtures.DIALECT, "public"));
	}
	
	@Test
	void loadWithNullDatabase() {
		assertThrows(NullPointerException.class, () -> SqlMigrationSchema.load(null));
	}
	
	@Test
	void loadWithUnsupportedColumnTypeThrows() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		enqueueSchema(dataSource, Map.of("COLUMN_NAME", "id", "DATA_TYPE", 1111, "TYPE_NAME", "geometry", "COLUMN_SIZE", 0, "DECIMAL_DIGITS", 0, "NULLABLE", 1, "IS_AUTOINCREMENT", "NO"));
		
		try (Connection connection = dataSource.getConnection()) {
			SqlSchemaIntrospectionException exception = assertThrows(SqlSchemaIntrospectionException.class,
				() -> SqlMigrationSchema.load(connection, SqlTestFixtures.DIALECT, "public"));
			assertTrue(exception.getMessage().contains("geometry"));
			assertTrue(exception.getMessage().contains("1111"));
			assertTrue(exception.getMessage().contains("id"));
			assertTrue(exception.getMessage().contains("it_table"));
		}
	}
	
	@Test
	void loadResolvesColumnTypeThroughDialect() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		enqueueSchema(dataSource, Map.of("COLUMN_NAME", "id", "DATA_TYPE", 1111, "TYPE_NAME", "uuid", "COLUMN_SIZE", 0, "DECIMAL_DIGITS", 0, "NULLABLE", 1, "IS_AUTOINCREMENT", "NO"));
		
		try (Connection connection = dataSource.getConnection()) {
			SqlMigrationSchema schema = SqlMigrationSchema.load(connection, SqlDialects.POSTGRESQL, "public");
			assertSame(SqlTypes.UUID, schema.column("it_table", "id").type());
		}
	}
	
	@Test
	void loadResolvesPortableColumnType() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		enqueueSchema(dataSource, Map.of("COLUMN_NAME", "id", "DATA_TYPE", Types.INTEGER, "TYPE_NAME", "int4", "COLUMN_SIZE", 10, "DECIMAL_DIGITS", 0, "NULLABLE", 1, "IS_AUTOINCREMENT", "NO"));
		
		try (Connection connection = dataSource.getConnection()) {
			SqlMigrationSchema schema = SqlMigrationSchema.load(connection, SqlTestFixtures.DIALECT, "public");
			assertSame(SqlTypes.INTEGER, schema.column("it_table", "id").type());
		}
	}
	
	@Test
	void loadWithNullTypeNameFromDriver() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		Map<String, Object> column = new HashMap<>();
		column.put("COLUMN_NAME", "name");
		column.put("DATA_TYPE", Types.VARCHAR);
		column.put("TYPE_NAME", null);
		column.put("COLUMN_SIZE", 64);
		column.put("DECIMAL_DIGITS", 0);
		column.put("NULLABLE", 1);
		column.put("IS_AUTOINCREMENT", "NO");
		enqueueSchema(dataSource, column);
		
		try (Connection connection = dataSource.getConnection()) {
			SqlMigrationSchema schema = assertDoesNotThrow(() -> SqlMigrationSchema.load(connection, SqlTestFixtures.DIALECT, "public"));
			assertEquals(SqlTypes.STRING.configure(SqlParameter.length(64)), schema.column("it_table", "name").type());
		}
	}
	
	@Test
	void loadPreservesColumnFlags() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		enqueueSchema(dataSource, Map.of("COLUMN_NAME", "id", "DATA_TYPE", Types.INTEGER, "TYPE_NAME", "int4", "COLUMN_SIZE", 10, "DECIMAL_DIGITS", 0, "NULLABLE", 0, "IS_AUTOINCREMENT", "YES"));
		
		try (Connection connection = dataSource.getConnection()) {
			SqlColumn<?, ?> column = SqlMigrationSchema.load(connection, SqlTestFixtures.DIALECT, "public").column("it_table", "id");
			assertFalse(column.nullable());
			assertTrue(column.autoIncrement());
		}
	}
	
	@Test
	void loadMultipleTablesWithDialectSpecificTypes() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(Map.of("TABLE_NAME", "first"), Map.of("TABLE_NAME", "second"))));
		for (String typeName : List.of("uuid", "jsonb")) {
			dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of()));
			dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of()));
			dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of(
				Map.of("COLUMN_NAME", "value", "DATA_TYPE", 1111, "TYPE_NAME", typeName, "COLUMN_SIZE", 0, "DECIMAL_DIGITS", 0, "NULLABLE", 1, "IS_AUTOINCREMENT", "NO")
			)));
			dataSource.enqueueResultSet(SqlTestFixtures.labeledResultSet(List.of()));
		}
		
		try (Connection connection = dataSource.getConnection()) {
			SqlMigrationSchema schema = SqlMigrationSchema.load(connection, SqlDialects.POSTGRESQL, "public");
			assertEquals(List.of("first", "second"), List.copyOf(schema.tableNames()));
			assertSame(SqlTypes.UUID, schema.column("first", "value").type());
			assertSame(SqlTypes.JSON, schema.column("second", "value").type());
		}
	}
	
	@Test
	void loadThenExtractProducesResolvableIdentifiers() throws Exception {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		enqueueSchema(dataSource, Map.of("COLUMN_NAME", "id", "DATA_TYPE", 1111, "TYPE_NAME", "uuid", "COLUMN_SIZE", 0, "DECIMAL_DIGITS", 0, "NULLABLE", 1, "IS_AUTOINCREMENT", "NO"));
		
		try (Connection connection = dataSource.getConnection()) {
			SqlMigrationSchema schema = SqlMigrationSchema.load(connection, SqlDialects.POSTGRESQL, "public");
			SqlSchemaColumnInfo info = schema.extractColumnInfos().getFirst();
			assertEquals("uuid", info.typeIdentifier());
			assertSame(SqlTypes.UUID, SqlTypes.byIdentifier(info.typeIdentifier()).orElseThrow());
		}
	}
	
	@Test
	void extractColumnInfosStoresTypeIdentifier() throws Exception {
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(
			new SqlSchemaColumnInfo("it_table", "id", SqlTypes.UUID.jdbcType(), SqlParameter.length(36), true, false, false, false, 0, "uuid")
		), Map.of());
		assertEquals("uuid", schema.extractColumnInfos().getFirst().typeIdentifier());
	}
	
	@Test
	void extractColumnInfosWithoutTypeIdentifier() throws Exception {
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(
			new SqlSchemaColumnInfo("it_table", "id", Types.INTEGER, null, true, false, false, false, 0),
			new SqlSchemaColumnInfo("it_table", "name", Types.VARCHAR, SqlParameter.length(64), true, false, false, false, 1)
		), Map.of());
		for (SqlSchemaColumnInfo info : schema.extractColumnInfos()) {
			assertNull(info.typeIdentifier());
		}
	}
	
	@Test
	void extractColumnInfosForAnonymousMappedType() throws Exception {
		SqlTable<Void> table = SqlTable.create(Void.class, "it_table");
		table.column("id", SqlTypes.INTEGER.map(String.class, Integer::parseInt, value -> Integer.toString(value)), value -> null);
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(
			new SqlSchemaColumnInfo("it_table", "id", Types.INTEGER, null, true, false, false, false, 0, null)
		), Map.of());
		assertNull(schema.extractColumnInfos().getFirst().typeIdentifier());
	}
	
	@Test
	void fromSnapshotRestoresIdentifiedType() throws Exception {
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(
			new SqlSchemaColumnInfo("it_table", "id", Types.CHAR, SqlParameter.length(36), true, false, false, false, 0, "uuid")
		), Map.of());
		assertSame(SqlTypes.UUID, schema.column("it_table", "id").type());
	}
	
	@Test
	void fromSnapshotRestoresUnidentifiedType() throws Exception {
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(List.of(
			new SqlSchemaColumnInfo("it_table", "id", Types.CHAR, SqlParameter.length(36), true, false, false, false, 0, null)
		), Map.of());
		assertEquals(SqlTypes.FIXED_STRING.configure(SqlParameter.length(36)), schema.column("it_table", "id").type());
	}
	
	@Test
	void extractAndRebuildRoundTripsIdentifiedColumns() throws Exception {
		List<SqlSchemaColumnInfo> infos = List.of(
			new SqlSchemaColumnInfo("it_table", "id", Types.CHAR, SqlParameter.length(36), false, false, true, false, 0, "uuid"),
			new SqlSchemaColumnInfo("it_table", "payload", Types.LONGVARCHAR, null, true, false, false, false, 1, "json"),
			new SqlSchemaColumnInfo("it_table", "name", Types.VARCHAR, SqlParameter.length(64), true, false, false, false, 2, null),
			new SqlSchemaColumnInfo("it_table", "amount", Types.DECIMAL, SqlParameter.precision(10, 2), true, false, false, false, 3, null),
			new SqlSchemaColumnInfo("it_table", "data", Types.LONGVARBINARY, null, true, false, false, false, 4, null)
		);
		SqlMigrationSchema schema = SqlMigrationSchema.fromSnapshot(infos, Map.of());
		SqlMigrationSchema rebuilt = SqlMigrationSchema.fromSnapshot(schema.extractColumnInfos(), Map.of());
		
		assertSame(SqlTypes.UUID, rebuilt.column("it_table", "id").type());
		assertSame(SqlTypes.JSON, rebuilt.column("it_table", "payload").type());
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(64)), rebuilt.column("it_table", "name").type());
		assertEquals(SqlTypes.DECIMAL.configure(SqlParameter.precision(10, 2)), rebuilt.column("it_table", "amount").type());
		assertSame(SqlTypes.LARGE_BYTES, rebuilt.column("it_table", "data").type());
	}
	
	@Test
	void loadWithDataSourceAndDialectFailingConnection() {
		SqlSchemaIntrospectionException thrown = assertThrows(SqlSchemaIntrospectionException.class, () -> SqlMigrationSchema.load(SqlTestFixtures.failingDataSource(), SqlTestFixtures.DIALECT));
		assertTrue(thrown.getMessage().contains("default schema"), thrown.getMessage());
		assertInstanceOf(SQLException.class, thrown.getCause());
	}
	
	@Test
	void loadWithDataSourceAndDialectFailingDefaultSchema() {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", true);
		assertThrows(SqlSchemaIntrospectionException.class, () -> SqlMigrationSchema.load(recorder.dataSource(), SqlTestFixtures.DIALECT));
	}
	
	@Test
	void loadWithDataSourceUsesDialectDefaultSchema() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", false);
		recorder.enqueueTable("it_table");
		
		SqlMigrationSchema schema = SqlMigrationSchema.load(recorder.dataSource(), SqlTestFixtures.DIALECT);
		assertEquals(List.of("it_table"), List.copyOf(schema.tableNames()));
		assertFalse(recorder.schemaPatterns.isEmpty());
		assertTrue(recorder.schemaPatterns.stream().allMatch("app"::equals), String.valueOf(recorder.schemaPatterns));
	}
	
	@Test
	void loadWithDataSourceFallsBackToPublicWhenConnectionReportsNoSchema() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder(null, null, false);
		recorder.enqueueTable("it_table");
		
		SqlMigrationSchema.load(recorder.dataSource(), SqlTestFixtures.DIALECT);
		assertTrue(recorder.schemaPatterns.stream().allMatch("public"::equals), String.valueOf(recorder.schemaPatterns));
	}
	
	@Test
	void loadWithDatabaseDelegatesToDialectDefaultSchema() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", false);
		recorder.enqueueTable("it_table");
		
		SqlMigrationSchema.load(net.luis.utils.io.database.SqlDatabase.builder(recorder.dataSource(), SqlTestFixtures.DIALECT).build());
		assertTrue(recorder.schemaPatterns.stream().allMatch("app"::equals), String.valueOf(recorder.schemaPatterns));
	}
	
	@Test
	void loadPassesSchemaAsSchemaPatternForDefaultDialect() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", false);
		recorder.enqueueTable("it_table");
		
		SqlMigrationSchema.load(recorder.dataSource(), SqlTestFixtures.DIALECT);
		assertEquals(List.of("getTables", "getPrimaryKeys", "getIndexInfo", "getColumns"), recorder.calls);
		assertTrue(recorder.catalogs.stream().allMatch(Objects::isNull), String.valueOf(recorder.catalogs));
		assertTrue(recorder.schemaPatterns.stream().allMatch("app"::equals), String.valueOf(recorder.schemaPatterns));
	}
	
	@Test
	void loadPassesSchemaAsCatalogForMySqlDialect() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", false);
		recorder.enqueueTable("it_table");
		
		SqlMigrationSchema.load(recorder.dataSource(), SqlDialects.MYSQL);
		assertTrue(recorder.catalogs.stream().allMatch("app"::equals), String.valueOf(recorder.catalogs));
		assertTrue(recorder.schemaPatterns.stream().allMatch(Objects::isNull), String.valueOf(recorder.schemaPatterns));
	}
	
	@Test
	void loadClosesConnectionAfterLoading() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", false);
		recorder.enqueueTable("it_table");
		
		SqlMigrationSchema.load(recorder.dataSource(), SqlTestFixtures.DIALECT);
		assertTrue(recorder.closed);
	}
	
	@Test
	void loadWithDataSourceAndDialectReturnsSameResultAsExplicitSchema() throws Exception {
		IntrospectionRecorder resolved = new IntrospectionRecorder("app", "catalog", false);
		IntrospectionRecorder explicit = new IntrospectionRecorder("app", "catalog", false);
		resolved.enqueueTable("it_table");
		explicit.enqueueTable("it_table");
		
		SqlMigrationSchema fromDefault = SqlMigrationSchema.load(resolved.dataSource(), SqlTestFixtures.DIALECT);
		SqlMigrationSchema fromExplicit = SqlMigrationSchema.load(explicit.dataSource(), SqlTestFixtures.DIALECT, "app");
		assertEquals(List.copyOf(fromExplicit.tableNames()), List.copyOf(fromDefault.tableNames()));
		assertEquals(fromExplicit.column("it_table", "id").type(), fromDefault.column("it_table", "id").type());
	}
	
	@Test
	void loadWithEmptyMetadataProducesEmptySnapshot() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", false);
		SqlMigrationSchema schema = SqlMigrationSchema.load(recorder.dataSource(), SqlTestFixtures.DIALECT);
		assertTrue(schema.tableNames().isEmpty());
		assertEquals(List.of("getTables"), recorder.calls);
	}
	
	@Test
	void loadWithCatalogScopedDialectStillResolvesColumnsAndKeys() throws Exception {
		IntrospectionRecorder recorder = new IntrospectionRecorder("app", "catalog", false);
		recorder.enqueue(SqlTestFixtures.labeledResultSet(List.of(Map.of("TABLE_NAME", "it_table"))));
		recorder.enqueue(SqlTestFixtures.labeledResultSet(List.of(Map.of("COLUMN_NAME", "id"))));
		recorder.enqueue(SqlTestFixtures.labeledResultSet(List.of(Map.of("INDEX_NAME", "ux_code", "COLUMN_NAME", "code"))));
		recorder.enqueue(SqlTestFixtures.labeledResultSet(List.of(
			Map.of("COLUMN_NAME", "id", "DATA_TYPE", Types.INTEGER, "TYPE_NAME", "int", "COLUMN_SIZE", 10, "DECIMAL_DIGITS", 0, "NULLABLE", 0, "IS_AUTOINCREMENT", "NO"),
			Map.of("COLUMN_NAME", "code", "DATA_TYPE", Types.VARCHAR, "TYPE_NAME", "varchar", "COLUMN_SIZE", 64, "DECIMAL_DIGITS", 0, "NULLABLE", 1, "IS_AUTOINCREMENT", "NO")
		)));
		
		SqlMigrationSchema schema = SqlMigrationSchema.load(recorder.dataSource(), SqlDialects.MYSQL);
		assertTrue(recorder.catalogs.stream().allMatch("app"::equals), String.valueOf(recorder.catalogs));
		assertTrue(schema.column("it_table", "id").primaryKey());
		assertFalse(schema.column("it_table", "id").nullable());
		assertTrue(schema.column("it_table", "code").unique());
	}
	
	private record Fixture(SqlTable<Object> table, SqlColumn<Object, Integer> id, SqlColumn<Object, String> name, SqlMigrationSchema base) {}
	
	/**
	 * Hand-written {@link DataSource} whose connection reports a configurable schema and catalog and whose
	 * {@link java.sql.DatabaseMetaData} records the {@code (catalog, schemaPattern)} pair of every lookup, which is what
	 * the dialect routing through {@code introspectionCatalog} / {@code introspectionSchema} has to get right.<br>
	 */
	private static final class IntrospectionRecorder {
		
		private final List<String> calls = new ArrayList<>();
		private final List<String> catalogs = new ArrayList<>();
		private final List<String> schemaPatterns = new ArrayList<>();
		private final Deque<ResultSet> resultSets = new ArrayDeque<>();
		private final String schema;
		private final String catalog;
		private final boolean failOnSchema;
		private boolean closed;
		
		private IntrospectionRecorder(String schema, String catalog, boolean failOnSchema) {
			this.schema = schema;
			this.catalog = catalog;
			this.failOnSchema = failOnSchema;
		}
		
		private void enqueue(ResultSet resultSet) {
			this.resultSets.add(resultSet);
		}
		
		private void enqueueTable(String tableName) {
			this.enqueue(SqlTestFixtures.labeledResultSet(List.of(Map.of("TABLE_NAME", tableName))));
			this.enqueue(SqlTestFixtures.labeledResultSet(List.of()));
			this.enqueue(SqlTestFixtures.labeledResultSet(List.of()));
			this.enqueue(SqlTestFixtures.labeledResultSet(List.of(
				Map.of("COLUMN_NAME", "id", "DATA_TYPE", Types.INTEGER, "TYPE_NAME", "int4", "COLUMN_SIZE", 10, "DECIMAL_DIGITS", 0, "NULLABLE", 1, "IS_AUTOINCREMENT", "NO")
			)));
		}
		
		private ResultSet next() {
			ResultSet queued = this.resultSets.poll();
			return queued != null ? queued : SqlTestFixtures.labeledResultSet(List.of());
		}
		
		private DataSource dataSource() {
			return (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class<?>[] { DataSource.class }, (proxy, method, args) ->
				"getConnection".equals(method.getName()) ? this.connection() : null
			);
		}
		
		private Connection connection() {
			return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class }, (proxy, method, args) -> switch (method.getName()) {
				case "getSchema" -> {
					if (this.failOnSchema) {
						throw new SQLException("Schema lookup failed in tests");
					}
					yield this.schema;
				}
				case "getCatalog" -> this.catalog;
				case "getMetaData" -> this.metaData();
				case "close" -> {
					this.closed = true;
					yield null;
				}
				case "isClosed" -> this.closed;
				case "isValid", "getAutoCommit", "isReadOnly", "isWrapperFor" -> false;
				case "prepareStatement" -> this.statement();
				case "toString" -> "IntrospectionConnection";
				default -> null;
			});
		}
		
		private Object metaData() {
			return Proxy.newProxyInstance(DatabaseMetaData.class.getClassLoader(), new Class<?>[] { DatabaseMetaData.class }, (proxy, method, args) -> {
				String name = method.getName();
				if (List.of("getTables", "getColumns", "getPrimaryKeys", "getIndexInfo").contains(name)) {
					this.calls.add(name);
					this.catalogs.add((String) args[0]);
					this.schemaPatterns.add((String) args[1]);
					return this.next();
				}
				if (method.getReturnType() == ResultSet.class) {
					return this.next();
				}
				return switch (name) {
					case "getIdentifierQuoteString" -> "\"";
					case "getDatabaseProductName" -> "RecordingDB";
					case "getURL" -> "jdbc:recording:test";
					case "isWrapperFor" -> false;
					case "toString" -> "IntrospectionMetaData";
					default -> method.getReturnType().isPrimitive() ? false : null;
				};
			});
		}
		
		private Object statement() {
			return Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(), new Class<?>[] { PreparedStatement.class }, (proxy, method, args) -> switch (method.getName()) {
				case "executeQuery" -> this.next();
				case "executeUpdate" -> 0;
				case "execute", "isClosed", "isWrapperFor" -> false;
				case "toString" -> "IntrospectionStatement";
				default -> null;
			});
		}
	}
}
