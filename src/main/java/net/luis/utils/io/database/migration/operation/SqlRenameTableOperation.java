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

import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Migration operation that renames an existing table.<br>
 *
 * @author Luis-St
 *
 * @param from The table to rename
 * @param to The table with the new name
 */
public record SqlRenameTableOperation(
	@NonNull SqlTable<?> from,
	@NonNull SqlTable<?> to
) implements SqlMigrationOperation {
	
	/**
	 * Constructs a new rename table operation with the given source and target table.<br>
	 * @throws NullPointerException If the source or target table is null
	 */
	public SqlRenameTableOperation {
		Objects.requireNonNull(from, "Sql source table must not be null");
		Objects.requireNonNull(to, "Sql target table must not be null");
	}
}
