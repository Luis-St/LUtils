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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.migration.store.SqlMigrationSchemaStore;
import net.luis.utils.io.database.migration.store.SqlMigrationStore;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationBuilderTest {
	
	@Test
	void constructWithContext() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		assertTrue(builder.getOperations().isEmpty());
	}
	
	@Test
	void constructWithContextAndDryRun() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext(), true);
		assertTrue(builder.getOperations().isEmpty());
	}
	
	@Test
	void constructWithNullContext() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(null, false));
	}
	
	@Test
	void createTableWithNullDefinition() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).createTable(SqlTestFixtures.sampleTable(), null));
	}
	
	@Test
	void addColumnWithNullOptionsConsumer() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).addColumn(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE, null));
	}
	
	@Test
	void enableAuditingWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).enableAuditing(null));
	}
	
	@Test
	void enableAuditingWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).enableAuditing(SqlTestFixtures.sampleTable(), null));
	}
	
	@Test
	void disableAuditingWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).disableAuditing(null));
	}
	
	@Test
	void alterColumnWithNullChanges() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).alterColumn(SqlTestFixtures.integerColumn(), null));
	}
	
	@Test
	void createIndexWithNullDefinition() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).createIndex(SqlTestFixtures.sampleTable(), "idx", null));
	}
	
	@Test
	void addUniqueConstraintWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).addUniqueConstraint(SqlTestFixtures.sampleTable(), "uq", (SqlColumn<?, ?>[]) null));
	}
	
	@Test
	void addForeignKeyWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).addForeignKey(SqlTestFixtures.sampleTable(), "fk", null, SqlTestFixtures.sampleTable(), new SqlColumn<?, ?>[] { SqlTestFixtures.integerColumn() }, SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void addForeignKeyWithNullReferencedColumns() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).addForeignKey(SqlTestFixtures.sampleTable(), "fk", new SqlColumn<?, ?>[] { SqlTestFixtures.integerColumn() }, SqlTestFixtures.sampleTable(), null, SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void addCompositePrimaryKeyWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).addCompositePrimaryKey(SqlTestFixtures.sampleTable(), "pk", (SqlColumn<?, ?>[]) null));
	}
	
	@Test
	void dataWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).data(null, provider -> {}));
	}
	
	@Test
	void dataWithNullAction() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationBuilder(new FakeContext()).data(SqlTestFixtures.sampleTable(), null));
	}
	
	@Test
	void createTableAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.createTable(SqlTestFixtures.sampleTable(), table -> table.column(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE).primaryKey(SqlTestFixtures.integerColumn()));
		SqlCreateTableOperation operation = assertInstanceOf(SqlCreateTableOperation.class, builder.getOperations().getFirst());
		assertEquals(1, operation.columns().size());
		assertEquals(1, operation.primaryKeyColumns().size());
	}
	
	@Test
	void dropTableAppendsOperation() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.dropTable(table);
		SqlDropTableOperation operation = assertInstanceOf(SqlDropTableOperation.class, builder.getOperations().getFirst());
		assertSame(table, operation.table());
	}
	
	@Test
	void renameTableAppendsOperation() {
		SqlTable<Object> from = SqlTable.create(Object.class, "old");
		SqlTable<Object> to = SqlTable.create(Object.class, "new");
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.renameTable(from, to);
		SqlRenameTableOperation operation = assertInstanceOf(SqlRenameTableOperation.class, builder.getOperations().getFirst());
		assertSame(from, operation.from());
		assertSame(to, operation.to());
	}
	
	@Test
	void addColumnWithDefaultOptionsAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.addColumn(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE);
		SqlAddColumnOperation operation = assertInstanceOf(SqlAddColumnOperation.class, builder.getOperations().getFirst());
		assertSame(SqlColumnOptions.EMPTY, operation.options());
	}
	
	@Test
	void addColumnWithOptionsAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.addColumn(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE, options -> options.notNull());
		SqlAddColumnOperation operation = assertInstanceOf(SqlAddColumnOperation.class, builder.getOperations().getFirst());
		assertTrue(operation.options().notNull());
	}
	
	@Test
	void dropColumnAppendsOperation() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.dropColumn(column);
		SqlDropColumnOperation operation = assertInstanceOf(SqlDropColumnOperation.class, builder.getOperations().getFirst());
		assertSame(column, operation.column());
	}
	
	@Test
	void renameColumnAppendsOperation() {
		SqlColumn<?, ?> from = SqlTestFixtures.integerColumn();
		SqlColumn<?, ?> to = SqlTestFixtures.stringColumn();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.renameColumn(from, to);
		SqlRenameColumnOperation operation = assertInstanceOf(SqlRenameColumnOperation.class, builder.getOperations().getFirst());
		assertSame(from, operation.from());
		assertSame(to, operation.to());
	}
	
	@Test
	void enableAuditingResolvesTableConfig() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.enableAuditing(SqlTestFixtures.sampleTable());
		SqlEnableAuditingOperation operation = assertInstanceOf(SqlEnableAuditingOperation.class, builder.getOperations().getFirst());
		assertSame(SqlAuditConfig.DEFAULT, operation.config());
	}
	
	@Test
	void enableAuditingUsesAuditedTableConfig() {
		SqlTable<?> table = SqlTestFixtures.auditedTable();
		SqlAuditConfig config = table.auditConfig().orElseThrow();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.enableAuditing(table);
		SqlEnableAuditingOperation operation = assertInstanceOf(SqlEnableAuditingOperation.class, builder.getOperations().getFirst());
		assertEquals(config, operation.config());
	}
	
	@Test
	void enableAuditingWithExplicitConfigAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.enableAuditing(SqlTestFixtures.sampleTable(), SqlAuditConfig.DEFAULT);
		SqlEnableAuditingOperation operation = assertInstanceOf(SqlEnableAuditingOperation.class, builder.getOperations().getFirst());
		assertSame(SqlAuditConfig.DEFAULT, operation.config());
	}
	
	@Test
	void disableAuditingAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.disableAuditing(SqlTestFixtures.sampleTable());
		SqlDisableAuditingOperation operation = assertInstanceOf(SqlDisableAuditingOperation.class, builder.getOperations().getFirst());
		assertSame(SqlAuditConfig.DEFAULT, operation.config());
	}
	
	@Test
	void alterColumnAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.alterColumn(SqlTestFixtures.integerColumn(), changes -> changes.setNullable(true));
		SqlAlterColumnOperation operation = assertInstanceOf(SqlAlterColumnOperation.class, builder.getOperations().getFirst());
		assertEquals(1, operation.alterations().size());
		assertInstanceOf(SqlSetNullableAlteration.class, operation.alterations().getFirst());
	}
	
	@Test
	void createIndexAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.createIndex(SqlTestFixtures.sampleTable(), "idx", index -> index.columns(SqlTestFixtures.integerColumn()));
		SqlCreateIndexOperation operation = assertInstanceOf(SqlCreateIndexOperation.class, builder.getOperations().getFirst());
		assertEquals("idx", operation.index().name());
	}
	
	@Test
	void dropIndexAppendsOperation() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.dropIndex(table, "idx");
		SqlDropIndexOperation operation = assertInstanceOf(SqlDropIndexOperation.class, builder.getOperations().getFirst());
		assertSame(table, operation.table());
		assertEquals("idx", operation.index());
	}
	
	@Test
	void renameIndexAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.renameIndex(SqlTestFixtures.sampleTable(), "old", "new");
		SqlRenameIndexOperation operation = assertInstanceOf(SqlRenameIndexOperation.class, builder.getOperations().getFirst());
		assertEquals("old", operation.from());
		assertEquals("new", operation.to());
	}
	
	@Test
	void addUniqueConstraintAppendsOperation() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.addUniqueConstraint(SqlTestFixtures.sampleTable(), "uq", column);
		SqlAddUniqueConstraintOperation operation = assertInstanceOf(SqlAddUniqueConstraintOperation.class, builder.getOperations().getFirst());
		assertEquals("uq", operation.name());
		assertEquals(List.of(column), operation.columns());
	}
	
	@Test
	void addForeignKeyAppendsOperation() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		SqlColumn<?, ?> referencedColumn = SqlTestFixtures.integerColumn();
		SqlTable<?> referenced = SqlTable.create(Object.class, "parent");
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.addForeignKey(SqlTestFixtures.sampleTable(), "fk", new SqlColumn<?, ?>[] { column }, referenced, new SqlColumn<?, ?>[] { referencedColumn }, SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION);
		SqlAddForeignKeyOperation operation = assertInstanceOf(SqlAddForeignKeyOperation.class, builder.getOperations().getFirst());
		assertEquals("fk", operation.name());
		assertEquals(List.of(column), operation.columns());
		assertEquals(List.of(referencedColumn), operation.referencedColumns());
		assertSame(referenced, operation.referencedTable());
		assertEquals(SqlReferentialAction.CASCADE, operation.onDelete());
		assertEquals(SqlReferentialAction.NO_ACTION, operation.onUpdate());
	}
	
	@Test
	void addCheckConstraintAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.addCheckConstraint(SqlTestFixtures.sampleTable(), "chk", SqlTestFixtures.alwaysCondition());
		SqlAddCheckConstraintOperation operation = assertInstanceOf(SqlAddCheckConstraintOperation.class, builder.getOperations().getFirst());
		assertEquals("chk", operation.name());
	}
	
	@Test
	void addCompositePrimaryKeyAppendsOperation() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.addCompositePrimaryKey(SqlTestFixtures.sampleTable(), "pk", column);
		SqlAddCompositePrimaryKeyOperation operation = assertInstanceOf(SqlAddCompositePrimaryKeyOperation.class, builder.getOperations().getFirst());
		assertEquals("pk", operation.name());
		assertEquals(List.of(column), operation.columns());
	}
	
	@Test
	void dropConstraintAppendsOperation() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.dropConstraint(SqlTestFixtures.sampleTable(), "constraint");
		SqlDropConstraintOperation operation = assertInstanceOf(SqlDropConstraintOperation.class, builder.getOperations().getFirst());
		assertEquals("constraint", operation.name());
	}
	
	@Test
	void dataInDryRunSkipsAction() throws SqlException {
		FakeContext context = new FakeContext();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(context, true);
		AtomicBoolean executed = new AtomicBoolean(false);
		builder.data(SqlTestFixtures.sampleTable(), provider -> executed.set(true));
		assertInstanceOf(SqlExecuteDataOperation.class, builder.getOperations().getFirst());
		assertFalse(executed.get());
		assertEquals(0, context.fromCalls.get());
	}
	
	@Test
	void dataNotDryRunRunsAction() throws SqlException {
		FakeContext context = new FakeContext();
		SqlMigrationBuilder builder = new SqlMigrationBuilder(context, false);
		AtomicBoolean executed = new AtomicBoolean(false);
		builder.data(SqlTestFixtures.sampleTable(), provider -> executed.set(true));
		assertInstanceOf(SqlExecuteDataOperation.class, builder.getOperations().getFirst());
		assertTrue(executed.get());
		assertEquals(1, context.fromCalls.get());
	}
	
	@Test
	void multipleOperationsPreserveOrder() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.createTable(SqlTestFixtures.sampleTable(), table -> table.column(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE));
		builder.addColumn(SqlTestFixtures.stringColumn(), SqlTestFixtures.STRING_TYPE);
		builder.createIndex(SqlTestFixtures.sampleTable(), "idx", index -> index.columns(SqlTestFixtures.integerColumn()));
		builder.dropTable(SqlTestFixtures.sampleTable());
		List<SqlMigrationOperation> operations = builder.getOperations();
		assertEquals(4, operations.size());
		assertInstanceOf(SqlCreateTableOperation.class, operations.get(0));
		assertInstanceOf(SqlAddColumnOperation.class, operations.get(1));
		assertInstanceOf(SqlCreateIndexOperation.class, operations.get(2));
		assertInstanceOf(SqlDropTableOperation.class, operations.get(3));
	}
	
	@Test
	void getOperationsReturnsImmutableCopy() {
		SqlMigrationBuilder builder = new SqlMigrationBuilder(new FakeContext());
		builder.dropTable(SqlTestFixtures.sampleTable());
		List<SqlMigrationOperation> first = builder.getOperations();
		assertThrows(UnsupportedOperationException.class, () -> first.add(first.getFirst()));
		builder.dropTable(SqlTestFixtures.sampleTable());
		assertEquals(1, first.size());
		assertEquals(2, builder.getOperations().size());
	}
	
	private static final class FakeContext implements SqlMigrationContext {
		
		private final AtomicInteger fromCalls = new AtomicInteger(0);
		
		@Override
		public @NonNull DataSource dataSource() {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public @NonNull SqlDialect dialect() {
			return SqlTestFixtures.DIALECT;
		}
		
		@Override
		public <E> @NonNull SqlQueryProvider<E> from(@NonNull SqlTable<E> table) {
			this.fromCalls.incrementAndGet();
			return null;
		}
		
		@Override
		public void executeAndSave(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull SqlMigrationInfo info, @NonNull SqlMigrationSchemaStore schemaStore) {
			throw new UnsupportedOperationException("Not used in tests");
		}
		
		@Override
		public void executeAndUpdate(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull Version version, @NonNull SqlMigrationStatus status, @NonNull SqlMigrationSchemaStore schemaStore) {
			throw new UnsupportedOperationException("Not used in tests");
		}
	}
}
