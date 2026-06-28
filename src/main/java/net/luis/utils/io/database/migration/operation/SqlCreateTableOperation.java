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

package net.luis.utils.io.database.migration.operation;

import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Migration operation that creates a new table with the given columns and primary key columns.<br>
 *
 * @author Luis-St
 *
 * @param table The table to create
 * @param columns The definitions of the columns to create
 * @param primaryKeyColumns The columns that form the primary key
 */
public record SqlCreateTableOperation(
	@NonNull SqlTable<?> table,
	@NonNull List<SqlColumnDefinition> columns,
	@NonNull List<SqlColumn<?, ?>> primaryKeyColumns
) implements SqlMigrationOperation {
	
	/**
	 * Constructs a new create table operation with the given table, columns and primary key columns.<br>
	 * @throws NullPointerException If the table, columns or primary key columns are null
	 */
	public SqlCreateTableOperation {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(columns, "Sql column definitions must not be null");
		Objects.requireNonNull(primaryKeyColumns, "Sql primary key columns must not be null");
	}
}
