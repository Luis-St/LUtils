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
 *
 * @author Luis-St
 *
 */

public class SqlMigrationBuilder {
	
	private final List<SqlMigrationOperation> operations = Lists.newArrayList();
	private final SqlMigrationContext context;
	private final boolean dryRun;
	
	SqlMigrationBuilder(@NonNull SqlMigrationContext context) {
		this(context, false);
	}
	
	SqlMigrationBuilder(@NonNull SqlMigrationContext context, boolean dryRun) {
		this.context = Objects.requireNonNull(context, "Sql migration context must not be null");
		this.dryRun = dryRun;
	}
	
	public void createTable(@NonNull SqlTable<?> table, @NonNull Consumer<SqlMigrationTableBuilder> definition) {
		Objects.requireNonNull(definition, "Sql table definition consumer must not be null");
		
		SqlMigrationTableBuilder builder = new SqlMigrationTableBuilder();
		definition.accept(builder);
		this.operations.add(new SqlCreateTableOperation(table, builder.getColumns(), builder.getPrimaryKeyColumns()));
	}
	
	public void dropTable(@NonNull SqlTable<?> table) {
		this.operations.add(new SqlDropTableOperation(table));
	}
	
	public void renameTable(@NonNull SqlTable<?> from, @NonNull SqlTable<?> to) {
		this.operations.add(new SqlRenameTableOperation(from, to));
	}
	
	public void addColumn(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> type) {
		this.operations.add(new SqlAddColumnOperation(column, type, SqlColumnOptions.EMPTY));
	}
	
	public <C> void addColumn(@NonNull SqlColumn<?, C> column, @NonNull SqlType<C> type, @NonNull Consumer<SqlMigrationColumnBuilder<C>> options) {
		Objects.requireNonNull(options, "Sql column options consumer must not be null");
		
		SqlMigrationColumnBuilder<C> builder = new SqlMigrationColumnBuilder<>();
		options.accept(builder);
		this.operations.add(new SqlAddColumnOperation(column, type, builder.build()));
	}
	
	public void dropColumn(@NonNull SqlColumn<?, ?> column) {
		this.operations.add(new SqlDropColumnOperation(column));
	}
	
	public void renameColumn(@NonNull SqlColumn<?, ?> from, @NonNull SqlColumn<?, ?> to) {
		this.operations.add(new SqlRenameColumnOperation(from, to));
	}
	
	public <C> void alterColumn(@NonNull SqlColumn<?, C> column, @NonNull Consumer<SqlMigrationColumnAlter<C>> changes) {
		Objects.requireNonNull(changes, "Sql column alterations consumer must not be null");
		
		SqlMigrationColumnAlter<C> alter = new SqlMigrationColumnAlter<>();
		changes.accept(alter);
		this.operations.add(new SqlAlterColumnOperation(column, alter.getAlterations()));
	}
	
	public void createIndex(@NonNull SqlTable<?> table, @NonNull String name, @NonNull Consumer<SqlMigrationIndexBuilder> definition) {
		Objects.requireNonNull(definition, "Sql index definition consumer must not be null");
		
		SqlMigrationIndexBuilder builder = new SqlMigrationIndexBuilder(name);
		definition.accept(builder);
		SqlIndex index = builder.build();
		this.operations.add(new SqlCreateIndexOperation(index, table));
	}
	
	public void dropIndex(@NonNull SqlTable<?> table, @NonNull String name) {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		this.operations.add(new SqlDropIndexOperation(table.getName(), name));
	}
	
	public void renameIndex(@NonNull SqlTable<?> table, @NonNull String from, @NonNull String to) {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		this.operations.add(new SqlRenameIndexOperation(table.getName(), from, to));
	}
	
	public void addUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String name, SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		this.operations.add(new SqlAddUniqueConstraintOperation(table, name, List.of(columns)));
	}
	
	public void addForeignKey(
		@NonNull SqlTable<?> table,
		@NonNull String name,
		@NonNull SqlColumn<?, ?> @NonNull [] columns,
		@NonNull SqlTable<?> referencedTable,
		@NonNull SqlColumn<?, ?> @NonNull [] referencedColumns,
		@NonNull SqlReferentialAction onDelete,
		@NonNull SqlReferentialAction onUpdate
	) {
		Objects.requireNonNull(referencedColumns, "Sql referenced columns must not be null");
		
		this.operations.add(new SqlAddForeignKeyOperation(table, name, List.of(columns), referencedTable, List.of(referencedColumns), onDelete, onUpdate));
	}
	
	public void addCheckConstraint(@NonNull SqlTable<?> table, @NonNull String name, @NonNull SqlCondition condition) {
		this.operations.add(new SqlAddCheckConstraintOperation(table, name, condition));
	}
	
	public void addCompositePrimaryKey(@NonNull SqlTable<?> table, @NonNull String name, SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		this.operations.add(new SqlAddCompositePrimaryKeyOperation(table, name, List.of(columns)));
	}
	
	public void dropConstraint(@NonNull SqlTable<?> table, @NonNull String name) {
		this.operations.add(new SqlDropConstraintOperation(table, name));
	}
	
	public <E> void data(@NonNull SqlTable<E> table, @NonNull ThrowableConsumer<SqlQueryProvider<E>, SqlException> action) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(action, "Sql data action must not be null");
		
		this.operations.add(new SqlExecuteDataOperation(table));
		if (this.dryRun) {
			return;
		}
		try (SqlQueryProvider<E> provider = this.context.from(table)) {
			action.accept(provider);
		}
	}
	
	@NonNull List<SqlMigrationOperation> getOperations() {
		return List.copyOf(this.operations);
	}
}
