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

package net.luis.utils.io.database.index;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Describes a database index over one or more columns of a table.<br>
 * An index may be unique, may use a partial {@link SqlCondition where condition} and a specific {@link SqlIndexMethod index method}.<br>
 *
 * @author Luis-St
 *
 * @param name The name of the index
 * @param columns The columns covered by the index
 * @param unique Whether the index enforces uniqueness
 * @param whereCondition The optional condition for a partial index, or null for a full index
 * @param method The index method used by the index
 */
public record SqlIndex(
	@NonNull String name,
	@NonNull List<SqlColumn<?, ?>> columns,
	boolean unique,
	@Nullable SqlCondition whereCondition,
	@NonNull SqlIndexMethod method
) {
	
	/**
	 * Constructs a new index with the given name, columns, uniqueness, where condition and method.<br>
	 *
	 * @throws NullPointerException If the name, columns or method is null
	 * @throws IllegalArgumentException If the name is blank or the columns are empty
	 */
	public SqlIndex {
		Objects.requireNonNull(name, "Sql index name must not be null");
		Objects.requireNonNull(columns, "Sql index columns must not be null");
		Objects.requireNonNull(method, "Sql index method must not be null");
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Sql index name must not be blank");
		}
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Sql index columns must not be empty");
		}
	}
	
	/**
	 * Constructs a new full index with the given name, columns, uniqueness and method.<br>
	 *
	 * @param name The name of the index
	 * @param columns The columns covered by the index
	 * @param unique Whether the index enforces uniqueness
	 * @param method The index method used by the index
	 * @throws NullPointerException If the name, columns or method is null
	 * @throws IllegalArgumentException If the name is blank or the columns are empty
	 */
	public SqlIndex(@NonNull String name, @NonNull List<SqlColumn<?, ?>> columns, boolean unique, @NonNull SqlIndexMethod method) {
		this(name, columns, unique, null, method);
	}
}
