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

import net.luis.utils.io.database.dialect.SqlColumnType;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlPrimaryKeyColumn;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * Builder for table definitions in migrations.<br>
 * Provides a fluent API for defining columns and primary keys when creating a new table.<br>
 *
 * @author Luis-St
 */
public interface SqlTableBuilder {
	
	/**
	 * Adds a column with the specified column reference and type.<br>
	 *
	 * @param column The column reference
	 * @param type The column type
	 * @return This builder for chaining
	 */
	@NonNull SqlTableBuilder column(@NonNull SqlColumn<?> column, @NonNull SqlColumnType type);
	
	/**
	 * Adds a column with the specified column reference, type, and additional options.<br>
	 *
	 * @param column The column reference
	 * @param type The column type
	 * @param options A consumer to configure column options
	 * @param <V> The column value type
	 * @return This builder for chaining
	 */
	<V> @NonNull SqlTableBuilder column(@NonNull SqlColumn<V> column, @NonNull SqlColumnType type, @NonNull Consumer<SqlColumnBuilder<V>> options);
	
	/**
	 * Sets the primary key for the table.<br>
	 *
	 * @param columns The primary key columns
	 * @return This builder for chaining
	 */
	@NonNull SqlTableBuilder primaryKey(SqlPrimaryKeyColumn<?, ?> @NonNull ... columns);
	
	/**
	 * Sets a composite primary key for the table using any column types.<br>
	 * Use this when the composite key includes columns that are not individually declared as primary key columns,
	 * such as foreign key columns in a junction table.<br>
	 *
	 * @param columns The columns forming the composite primary key
	 * @return This builder for chaining
	 */
	@NonNull SqlTableBuilder compositePrimaryKey(SqlColumn<?> @NonNull ... columns);
}
