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

package net.luis.utils.io.database.table;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a unique constraint of a sql table.<br>
 * The constraint enforces that the combination of values of the given columns is unique across all rows of the table.<br>
 *
 * @see SqlColumn
 * @see SqlTable
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the owning table maps to
 */
public record SqlUniqueConstraint<E>(
	@NonNull @Unmodifiable List<SqlColumn<E, ?>> columns
) {
	
	/**
	 * Constructs a new unique constraint with the given columns.<br>
	 * The columns are copied into an unmodifiable list.<br>
	 *
	 * @param columns The columns that the unique constraint spans
	 * @throws NullPointerException If the columns list is null
	 * @throws IllegalArgumentException If the columns list is empty
	 */
	public SqlUniqueConstraint {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Sql unique constraint must contain at least one column");
		}
		
		columns = List.copyOf(columns);
	}
}
