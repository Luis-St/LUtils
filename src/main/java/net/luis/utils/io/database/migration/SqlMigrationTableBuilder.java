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
import net.luis.utils.io.database.migration.operation.SqlColumnDefinition;
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fluent builder used to define the columns and primary key of a table during a migration.<br>
 * It accumulates {@link SqlColumnDefinition}s that describe the table to be created.<br>
 *
 * @author Luis-St
 */

public class SqlMigrationTableBuilder {
	
	/**
	 * The column definitions of the table.
	 */
	private final List<SqlColumnDefinition> columns = Lists.newArrayList();
	/**
	 * The columns that make up the primary key of the table.
	 */
	private final List<SqlColumn<?, ?>> primaryKeyColumns = Lists.newArrayList();
	
	/**
	 * Constructs a new sql migration table builder.<br>
	 */
	SqlMigrationTableBuilder() {}
	
	/**
	 * Adds a column with the given type to the table using the default column options.<br>
	 *
	 * @param column The column to add
	 * @param type The sql type of the column
	 * @return This builder
	 * @throws NullPointerException If the column or type is null
	 */
	public @NonNull SqlMigrationTableBuilder column(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> type) {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		
		this.columns.add(new SqlColumnDefinition(column, type, SqlColumnOptions.EMPTY));
		return this;
	}
	
	/**
	 * Adds a column with the given type to the table using the given options.<br>
	 * The options consumer is used to configure the column definition.<br>
	 *
	 * @param column The column to add
	 * @param type The sql type of the column
	 * @param options The consumer used to configure the column options
	 * @return This builder
	 * @throws NullPointerException If the column, type or options consumer is null
	 * @param <C> The value type of the column
	 */
	public <C> @NonNull SqlMigrationTableBuilder column(@NonNull SqlColumn<?, C> column, @NonNull SqlType<C> type, @NonNull Consumer<SqlMigrationColumnBuilder<C>> options) {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(options, "Sql column options must not be null");
		
		SqlMigrationColumnBuilder<C> builder = new SqlMigrationColumnBuilder<>();
		options.accept(builder);
		this.columns.add(new SqlColumnDefinition(column, type, builder.build()));
		return this;
	}
	
	/**
	 * Sets the primary key of the table to the given columns.<br>
	 * Any previously set primary key columns are replaced.<br>
	 *
	 * @param columns The columns that make up the primary key
	 * @return This builder
	 * @throws NullPointerException If the columns array is null
	 * @throws IllegalArgumentException If no columns are given
	 */
	public @NonNull SqlMigrationTableBuilder primaryKey(SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Sql primary key columns must not be null");
		if (columns.length == 0) {
			throw new IllegalArgumentException("Primary key must have at least one column");
		}
		
		this.primaryKeyColumns.clear();
		this.primaryKeyColumns.addAll(List.of(columns));
		return this;
	}
	
	/**
	 * Returns the column definitions of the table.<br>
	 * @return An immutable copy of the column definitions
	 */
	@NonNull List<SqlColumnDefinition> getColumns() {
		return List.copyOf(this.columns);
	}
	
	/**
	 * Returns the columns that make up the primary key of the table.<br>
	 * @return An immutable copy of the primary key columns
	 */
	@NonNull List<SqlColumn<?, ?>> getPrimaryKeyColumns() {
		return List.copyOf(this.primaryKeyColumns);
	}
}
