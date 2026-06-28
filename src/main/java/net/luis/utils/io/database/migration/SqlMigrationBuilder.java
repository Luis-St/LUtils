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

import com.google.common.collect.Lists;
import net.luis.utils.function.throwable.ThrowableConsumer;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Top-level fluent builder used inside a migration's up and down methods to describe schema changes.<br>
 * It accumulates {@link SqlMigrationOperation}s for tables, columns, indices, constraints and data changes.<br>
 * This builder is the entry point passed to a {@link SqlMigration}.<br>
 *
 * @author Luis-St
 */

public class SqlMigrationBuilder {
	
	/**
	 * The accumulated migration operations described by this builder.
	 */
	private final List<SqlMigrationOperation> operations = Lists.newArrayList();
	/**
	 * The migration context used to access tables and execute data changes.
	 */
	private final SqlMigrationContext context;
	/**
	 * Whether this builder runs in dry-run mode, in which case data actions are skipped.
	 */
	private final boolean dryRun;
	
	/**
	 * Constructs a new sql migration builder with the given context.<br>
	 * The builder is created in normal (non dry-run) mode.<br>
	 *
	 * @param context The migration context
	 * @throws NullPointerException If the context is null
	 */
	SqlMigrationBuilder(@NonNull SqlMigrationContext context) {
		this(context, false);
	}
	
	/**
	 * Constructs a new sql migration builder with the given context and dry-run flag.<br>
	 *
	 * @param context The migration context
	 * @param dryRun Whether the builder should run in dry-run mode
	 * @throws NullPointerException If the context is null
	 */
	SqlMigrationBuilder(@NonNull SqlMigrationContext context, boolean dryRun) {
		this.context = Objects.requireNonNull(context, "Sql migration context must not be null");
		this.dryRun = dryRun;
	}
	
	/**
	 * Adds a create-table operation for the given table.<br>
	 * The definition consumer is used to declare the columns and primary key of the table.<br>
	 *
	 * @param table The table to create
	 * @param definition The consumer used to define the table
	 * @throws NullPointerException If the definition consumer is null
	 */
	public void createTable(@NonNull SqlTable<?> table, @NonNull Consumer<SqlMigrationTableBuilder> definition) {
		Objects.requireNonNull(definition, "Sql table definition consumer must not be null");
		
		SqlMigrationTableBuilder builder = new SqlMigrationTableBuilder();
		definition.accept(builder);
		this.operations.add(new SqlCreateTableOperation(table, builder.getColumns(), builder.getPrimaryKeyColumns()));
	}
	
	/**
	 * Adds a drop-table operation for the given table.<br>
	 * @param table The table to drop
	 */
	public void dropTable(@NonNull SqlTable<?> table) {
		this.operations.add(new SqlDropTableOperation(table));
	}
	
	/**
	 * Adds a rename-table operation that renames the given table.<br>
	 *
	 * @param from The table to rename
	 * @param to The renamed table
	 */
	public void renameTable(@NonNull SqlTable<?> from, @NonNull SqlTable<?> to) {
		this.operations.add(new SqlRenameTableOperation(from, to));
	}
	
	/**
	 * Adds an add-column operation for the given column using the default column options.<br>
	 *
	 * @param column The column to add
	 * @param type The sql type of the column
	 */
	public void addColumn(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> type) {
		this.operations.add(new SqlAddColumnOperation(column, type, SqlColumnOptions.EMPTY));
	}
	
	/**
	 * Adds an add-column operation for the given column with the given options.<br>
	 * The options consumer is used to configure the column definition.<br>
	 *
	 * @param column The column to add
	 * @param type The sql type of the column
	 * @param options The consumer used to configure the column options
	 * @throws NullPointerException If the options consumer is null
	 * @param <C> The value type of the column
	 */
	public <C> void addColumn(@NonNull SqlColumn<?, C> column, @NonNull SqlType<C> type, @NonNull Consumer<SqlMigrationColumnBuilder<C>> options) {
		Objects.requireNonNull(options, "Sql column options consumer must not be null");
		
		SqlMigrationColumnBuilder<C> builder = new SqlMigrationColumnBuilder<>();
		options.accept(builder);
		this.operations.add(new SqlAddColumnOperation(column, type, builder.build()));
	}
	
	/**
	 * Adds a drop-column operation for the given column.<br>
	 * @param column The column to drop
	 */
	public void dropColumn(@NonNull SqlColumn<?, ?> column) {
		this.operations.add(new SqlDropColumnOperation(column));
	}
	
	/**
	 * Adds a rename-column operation that renames the given column.<br>
	 *
	 * @param from The column to rename
	 * @param to The renamed column
	 */
	public void renameColumn(@NonNull SqlColumn<?, ?> from, @NonNull SqlColumn<?, ?> to) {
		this.operations.add(new SqlRenameColumnOperation(from, to));
	}
	
	/**
	 * Adds an enable-auditing operation for the given table.<br>
	 * The audit config of the table is used, or the default config if the table has none.<br>
	 *
	 * @param table The table to enable auditing for
	 * @throws NullPointerException If the table is null
	 */
	public void enableAuditing(@NonNull SqlTable<?> table) {
		Objects.requireNonNull(table, "Sql table must not be null");
		this.enableAuditing(table, table.auditConfig().orElse(SqlAuditConfig.DEFAULT));
	}
	
	/**
	 * Adds an enable-auditing operation for the given table using the given config.<br>
	 *
	 * @param table The table to enable auditing for
	 * @param config The audit config to use
	 * @throws NullPointerException If the table or config is null
	 */
	public void enableAuditing(@NonNull SqlTable<?> table, @NonNull SqlAuditConfig config) {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(config, "Sql audit config must not be null");
		this.operations.add(new SqlEnableAuditingOperation(table, config));
	}
	
	/**
	 * Adds a disable-auditing operation for the given table.<br>
	 * The audit config of the table is used, or the default config if the table has none.<br>
	 *
	 * @param table The table to disable auditing for
	 * @throws NullPointerException If the table is null
	 */
	public void disableAuditing(@NonNull SqlTable<?> table) {
		Objects.requireNonNull(table, "Sql table must not be null");
		this.operations.add(new SqlDisableAuditingOperation(table, table.auditConfig().orElse(SqlAuditConfig.DEFAULT)));
	}
	
	/**
	 * Adds an alter-column operation for the given column.<br>
	 * The changes consumer is used to declare the alterations applied to the column.<br>
	 *
	 * @param column The column to alter
	 * @param changes The consumer used to define the alterations
	 * @throws NullPointerException If the changes consumer is null
	 * @param <C> The value type of the column
	 */
	public <C> void alterColumn(@NonNull SqlColumn<?, C> column, @NonNull Consumer<SqlMigrationColumnAlter<C>> changes) {
		Objects.requireNonNull(changes, "Sql column alterations consumer must not be null");
		
		SqlMigrationColumnAlter<C> alter = new SqlMigrationColumnAlter<>();
		changes.accept(alter);
		this.operations.add(new SqlAlterColumnOperation(column, alter.getAlterations()));
	}
	
	/**
	 * Adds a create-index operation for the given table.<br>
	 * The definition consumer is used to declare the columns and options of the index.<br>
	 *
	 * @param table The table to create the index on
	 * @param name The name of the index
	 * @param definition The consumer used to define the index
	 * @throws NullPointerException If the definition consumer is null
	 */
	public void createIndex(@NonNull SqlTable<?> table, @NonNull String name, @NonNull Consumer<SqlMigrationIndexBuilder> definition) {
		Objects.requireNonNull(definition, "Sql index definition consumer must not be null");
		
		SqlMigrationIndexBuilder builder = new SqlMigrationIndexBuilder(name);
		definition.accept(builder);
		SqlIndex index = builder.build();
		this.operations.add(new SqlCreateIndexOperation(index, table));
	}
	
	/**
	 * Adds a drop-index operation for the given index on the given table.<br>
	 *
	 * @param table The table the index belongs to
	 * @param index The name of the index to drop
	 */
	public void dropIndex(@NonNull SqlTable<?> table, @NonNull String index) {
		this.operations.add(new SqlDropIndexOperation(table, index));
	}
	
	/**
	 * Adds a rename-index operation that renames an index on the given table.<br>
	 *
	 * @param table The table the index belongs to
	 * @param from The current name of the index
	 * @param to The new name of the index
	 */
	public void renameIndex(@NonNull SqlTable<?> table, @NonNull String from, @NonNull String to) {
		this.operations.add(new SqlRenameIndexOperation(table, from, to));
	}
	
	/**
	 * Adds an add-unique-constraint operation for the given columns on the given table.<br>
	 *
	 * @param table The table to add the constraint to
	 * @param name The name of the constraint
	 * @param columns The columns covered by the unique constraint
	 * @throws NullPointerException If the columns array is null
	 */
	public void addUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String name, SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		this.operations.add(new SqlAddUniqueConstraintOperation(table, name, List.of(columns)));
	}
	
	/**
	 * Adds an add-foreign-key operation for the given columns on the given table.<br>
	 * The referenced columns of the referenced table are linked using the given referential actions.<br>
	 *
	 * @param table The table to add the foreign key to
	 * @param name The name of the foreign key
	 * @param columns The referencing columns
	 * @param referencedTable The referenced table
	 * @param referencedColumns The referenced columns
	 * @param onDelete The referential action applied on delete
	 * @param onUpdate The referential action applied on update
	 * @throws NullPointerException If the columns array or referenced columns array is null
	 */
	public void addForeignKey(
		@NonNull SqlTable<?> table,
		@NonNull String name,
		@NonNull SqlColumn<?, ?> @NonNull [] columns,
		@NonNull SqlTable<?> referencedTable,
		@NonNull SqlColumn<?, ?> @NonNull [] referencedColumns,
		@NonNull SqlReferentialAction onDelete,
		@NonNull SqlReferentialAction onUpdate
	) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(referencedColumns, "Sql referenced columns must not be null");
		
		this.operations.add(new SqlAddForeignKeyOperation(table, name, List.of(columns), referencedTable, List.of(referencedColumns), onDelete, onUpdate));
	}
	
	/**
	 * Adds an add-check-constraint operation for the given condition on the given table.<br>
	 *
	 * @param table The table to add the constraint to
	 * @param name The name of the constraint
	 * @param condition The check condition
	 */
	public void addCheckConstraint(@NonNull SqlTable<?> table, @NonNull String name, @NonNull SqlCondition condition) {
		this.operations.add(new SqlAddCheckConstraintOperation(table, name, condition));
	}
	
	/**
	 * Adds an add-composite-primary-key operation for the given columns on the given table.<br>
	 *
	 * @param table The table to add the composite primary key to
	 * @param name The name of the composite primary key
	 * @param columns The columns that make up the composite primary key
	 * @throws NullPointerException If the columns array is null
	 */
	public void addCompositePrimaryKey(@NonNull SqlTable<?> table, @NonNull String name, SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		this.operations.add(new SqlAddCompositePrimaryKeyOperation(table, name, List.of(columns)));
	}
	
	/**
	 * Adds a drop-constraint operation for the given constraint on the given table.<br>
	 *
	 * @param table The table the constraint belongs to
	 * @param name The name of the constraint to drop
	 */
	public void dropConstraint(@NonNull SqlTable<?> table, @NonNull String name) {
		this.operations.add(new SqlDropConstraintOperation(table, name));
	}
	
	/**
	 * Adds a data operation for the given table and executes the given action.<br>
	 * The action is provided with a query provider for the table to perform data changes.<br>
	 * If this builder runs in dry-run mode, the action is not executed.<br>
	 *
	 * @param table The table to perform data changes on
	 * @param action The action that performs the data changes
	 * @throws NullPointerException If the table or action is null
	 * @throws SqlException If the action fails to execute
	 * @param <E> The entity type of the table
	 */
	public <E> void data(@NonNull SqlTable<E> table, @NonNull ThrowableConsumer<SqlQueryProvider<E>, SqlException> action) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(action, "Sql data action must not be null");
		
		this.operations.add(new SqlExecuteDataOperation(table));
		if (this.dryRun) {
			return;
		}
		
		action.accept(this.context.from(table));
	}
	
	/**
	 * Returns the operations accumulated by this builder.<br>
	 * @return An immutable copy of the migration operations
	 */
	@NonNull List<SqlMigrationOperation> getOperations() {
		return List.copyOf(this.operations);
	}
}
