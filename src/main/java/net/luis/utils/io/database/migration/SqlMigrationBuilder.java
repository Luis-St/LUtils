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
	
	SqlMigrationBuilder(@NonNull SqlMigrationContext context) {
		this.context = Objects.requireNonNull(context, "Sql migration context must not be null");
	}
	
	public void createTable(@NonNull SqlTable<?> table, @NonNull Consumer<SqlMigrationTableBuilder> definition) {
		Objects.requireNonNull(definition, "Sql table definition consumer must not be null");
		
		SqlMigrationTableBuilder builder = new SqlMigrationTableBuilder();
		definition.accept(builder);
		this.operations.add(new CreateTableOperation(table, builder.getColumns(), builder.getPrimaryKeyColumns()));
	}
	
	public void dropTable(@NonNull SqlTable<?> table) {
		this.operations.add(new DropTableOperation(table));
	}
	
	public void renameTable(@NonNull SqlTable<?> from, @NonNull SqlTable<?> to) {
		this.operations.add(new RenameTableOperation(from, to));
	}
	
	public void addColumn(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> type) {
		this.operations.add(new AddColumnOperation(column, type, SqlColumnOptions.EMPTY));
	}
	
	public <C> void addColumn(@NonNull SqlColumn<?, C> column, @NonNull SqlType<C> type, @NonNull Consumer<SqlMigrationColumnBuilder<C>> options) {
		Objects.requireNonNull(options, "Sql column options consumer must not be null");
		
		SqlMigrationColumnBuilder<C> builder = new SqlMigrationColumnBuilder<>();
		options.accept(builder);
		this.operations.add(new AddColumnOperation(column, type, builder.build()));
	}
	
	public void dropColumn(@NonNull SqlColumn<?, ?> column) {
		this.operations.add(new DropColumnOperation(column));
	}
	
	public void renameColumn(@NonNull SqlColumn<?, ?> from, @NonNull SqlColumn<?, ?> to) {
		this.operations.add(new RenameColumnOperation(from, to));
	}
	
	public <C> void alterColumn(@NonNull SqlColumn<?, C> column, @NonNull Consumer<SqlMigrationColumnAlter<C>> changes) {
		Objects.requireNonNull(changes, "Sql column alterations consumer must not be null");
		
		SqlMigrationColumnAlter<C> alter = new SqlMigrationColumnAlter<>();
		changes.accept(alter);
		this.operations.add(new AlterColumnOperation(column, alter.getAlterations()));
	}
	
	public void createIndex(@NonNull SqlTable<?> table, @NonNull String name, @NonNull Consumer<SqlMigrationIndexBuilder> definition) {
		Objects.requireNonNull(definition, "Sql index definition consumer must not be null");
		
		SqlMigrationIndexBuilder builder = new SqlMigrationIndexBuilder(name);
		definition.accept(builder);
		SqlIndex index = builder.build();
		this.operations.add(new CreateIndexOperation(index, table));
	}
	
	public void dropIndex(@NonNull String name) {
		this.operations.add(new DropIndexOperation(name));
	}
	
	public void renameIndex(@NonNull String from, @NonNull String to) {
		this.operations.add(new RenameIndexOperation(from, to));
	}
	
	public void addUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String name, SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		this.operations.add(new AddUniqueConstraintOperation(table, name, List.of(columns)));
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
		
		this.operations.add(new AddForeignKeyOperation(table, name, List.of(columns), referencedTable, List.of(referencedColumns), onDelete, onUpdate));
	}
	
	public void addCheckConstraint(@NonNull SqlTable<?> table, @NonNull String name, @NonNull SqlCondition condition) {
		this.operations.add(new AddCheckConstraintOperation(table, name, condition));
	}
	
	public void addCompositePrimaryKey(@NonNull SqlTable<?> table, @NonNull String name, SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		this.operations.add(new AddCompositePrimaryKeyOperation(table, name, List.of(columns)));
	}
	
	public void dropConstraint(@NonNull SqlTable<?> table, @NonNull String name) {
		this.operations.add(new DropConstraintOperation(table, name));
	}
	
	public <E> @NonNull SqlQueryProvider<E> data(@NonNull SqlTable<E> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		this.operations.add(new ExecuteDataOperation(table));
		return this.context.from(table);
	}
	
	@NonNull List<SqlMigrationOperation> getOperations() {
		return List.copyOf(this.operations);
	}
}
