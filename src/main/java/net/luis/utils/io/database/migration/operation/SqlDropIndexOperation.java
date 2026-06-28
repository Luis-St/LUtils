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
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Migration operation that drops an index by its name.<br>
 *
 * @author Luis-St
 *
 * @param table The table the index belongs to, or {@code null} if not bound to a table
 * @param index The name of the index to drop
 */
public record SqlDropIndexOperation(
	@Nullable SqlTable<?> table,
	@NonNull String index
) implements SqlMigrationOperation {
	
	/**
	 * Constructs a new drop index operation with the given table and index name.<br>
	 * @throws NullPointerException If the index name is null
	 */
	public SqlDropIndexOperation {
		Objects.requireNonNull(index, "Sql index name must not be null");
	}
}
