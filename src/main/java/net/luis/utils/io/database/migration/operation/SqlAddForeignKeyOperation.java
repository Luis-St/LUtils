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

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Migration operation that adds a foreign key constraint to an existing table.<br>
 *
 * @author Luis-St
 *
 * @param table The table to add the constraint to
 * @param name The name of the constraint
 * @param columns The columns of the foreign key
 * @param referencedTable The table referenced by the foreign key
 * @param referencedColumns The columns referenced in the referenced table
 * @param onDelete The referential action applied on delete
 * @param onUpdate The referential action applied on update
 */
public record SqlAddForeignKeyOperation(
	@NonNull SqlTable<?> table,
	@NonNull String name,
	@NonNull List<SqlColumn<?, ?>> columns,
	@NonNull SqlTable<?> referencedTable,
	@NonNull List<SqlColumn<?, ?>> referencedColumns,
	@NonNull SqlReferentialAction onDelete,
	@NonNull SqlReferentialAction onUpdate
) implements SqlMigrationOperation {
	
	/**
	 * Constructs a new add foreign key operation with the given table, name, columns, referenced table, referenced columns and referential actions.<br>
	 * @throws NullPointerException If the table, name, columns, referenced table, referenced columns, on delete action or on update action are null
	 */
	public SqlAddForeignKeyOperation {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(name, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(referencedTable, "Sql referenced table must not be null");
		Objects.requireNonNull(referencedColumns, "Sql referenced columns must not be null");
		Objects.requireNonNull(onDelete, "On sql delete action must not be null");
		Objects.requireNonNull(onUpdate, "On sql update action must not be null");
	}
}
