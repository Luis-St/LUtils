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
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Defines a column together with its sql type and options for use in a migration operation.<br>
 *
 * @author Luis-St
 *
 * @param column The column being defined
 * @param type The sql type of the column
 * @param options The options describing constraints and the default value of the column
 */
public record SqlColumnDefinition(
	@NonNull SqlColumn<?, ?> column,
	@NonNull SqlType<?> type,
	@NonNull SqlColumnOptions options
) {
	
	/**
	 * Constructs a new column definition with the given column, type and options.<br>
	 * @throws NullPointerException If the column, type or options are null
	 */
	public SqlColumnDefinition {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(options, "Sql column options must not be null");
	}
}
