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

package net.luis.utils.io.database.query.util;

import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a single entry of a sql set operation.<br>
 * An entry combines a set operation with the select query it applies to.<br>
 *
 * @see SqlSetOperation
 * @see SqlSelectQuery
 *
 * @author Luis-St
 *
 * @param <E> The type of the elements returned by the query
 * @param operation The set operation used to combine the query with the preceding queries
 * @param query The select query that is combined using the set operation
 */
public record SqlSetOperationEntry<E>(
	@NonNull SqlSetOperation operation,
	@NonNull SqlSelectQuery<E> query
) {
	
	/**
	 * Constructs a new sql set operation entry with the given set operation and query.<br>
	 *
	 * @param operation The set operation used to combine the query with the preceding queries
	 * @param query The select query that is combined using the set operation
	 * @throws NullPointerException If the operation or the query is null
	 */
	public SqlSetOperationEntry {
		Objects.requireNonNull(operation, "Sql set operation must not be null");
		Objects.requireNonNull(query, "Sql query must not be null");
	}
}
