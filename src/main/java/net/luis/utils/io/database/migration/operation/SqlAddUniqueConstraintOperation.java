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
 *
 * @author Luis-St
 *
 */

public record SqlAddUniqueConstraintOperation(
	@NonNull SqlTable<?> table,
	@NonNull String name,
	@NonNull List<SqlColumn<?, ?>> columns
) implements SqlMigrationOperation {
	
	public SqlAddUniqueConstraintOperation {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(name, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Unique constraint must have at least one column");
		}
	}
}
