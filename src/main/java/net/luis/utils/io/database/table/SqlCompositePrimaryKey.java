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
 * Represents a composite primary key of a sql table that spans multiple columns.<br>
 * A composite primary key combines two or more columns into a single primary key constraint.<br>
 *
 * @see SqlColumn
 * @see SqlTable
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the owning table maps to
 */
public record SqlCompositePrimaryKey<E>(
	@NonNull @Unmodifiable List<SqlColumn<E, ?>> columns
) {
	
	/**
	 * Constructs a new composite primary key with the given columns.<br>
	 * The columns are copied into an unmodifiable list.<br>
	 *
	 * @param columns The columns that make up the composite primary key
	 * @throws NullPointerException If the columns list is null
	 * @throws IllegalArgumentException If the columns list contains less than 2 columns
	 */
	public SqlCompositePrimaryKey {
		Objects.requireNonNull(columns, "Sql referenced columns must not be null");
		
		if (columns.size() < 2) {
			throw new IllegalArgumentException("Sql referenced columns must contain at least 2 columns");
		}
		
		columns = List.copyOf(columns);
	}
	
	/**
	 * Returns the columns that make up this composite primary key.<br>
	 * @return The unmodifiable list of columns
	 */
	public @NonNull @Unmodifiable List<SqlColumn<E, ?>> referenceTarget() {
		return this.columns;
	}
}
