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
 * Represents a foreign key constraint of a sql table.<br>
 * It binds the local columns of the owning table to a {@link SqlForeignKey} that describes the referenced table and columns.<br>
 *
 * @see SqlColumn
 * @see SqlForeignKey
 * @see SqlTable
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the owning table maps to
 * @param <T> The type of the entity the referenced table maps to
 */
public record SqlTableForeignKey<E, T>(
	@NonNull @Unmodifiable List<SqlColumn<E, ?>> getReferencingColumns,
	@NonNull SqlForeignKey<T> getForeignKey
) {
	
	/**
	 * Constructs a new table foreign key with the given referencing columns and foreign key.<br>
	 * The referencing columns are copied into an unmodifiable list.<br>
	 *
	 * @param getReferencingColumns The local columns of the owning table that reference the foreign key
	 * @param getForeignKey The foreign key describing the referenced table and columns
	 * @throws NullPointerException If the referencing columns or the foreign key is null
	 * @throws IllegalArgumentException If the referencing columns are empty
	 */
	public SqlTableForeignKey {
		Objects.requireNonNull(getReferencingColumns, "Sql referencing columns must not be null");
		Objects.requireNonNull(getForeignKey, "Sql foreign key must not be null");
		
		if (getReferencingColumns.isEmpty()) {
			throw new IllegalArgumentException("Sql referencing columns must not be empty");
		}
		
		getReferencingColumns = List.copyOf(getReferencingColumns);
	}
}
