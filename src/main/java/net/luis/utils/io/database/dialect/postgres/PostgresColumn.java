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

package net.luis.utils.io.database.dialect.postgres;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a PostgreSQL-specific column.<br>
 * Extends {@link SqlColumn} with additional conditions that are only available in PostgreSQL.<br>
 *
 * @see SqlColumn
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface PostgresColumn<T> extends SqlColumn<T> {

	/**
	 * Creates a condition using a case-insensitive {@code ILIKE} pattern.<br>
	 * Generates SQL: {@code column ILIKE pattern}.<br>
	 *
	 * @param pattern The ilike pattern
	 * @return The ilike condition
	 */
	@NonNull SqlCondition ilike(@NonNull String pattern);

	/**
	 * Creates a condition using a PostgreSQL {@code SIMILAR TO} regular expression pattern.<br>
	 * Generates SQL: {@code column SIMILAR TO pattern}.<br>
	 *
	 * @param pattern The similar-to pattern
	 * @return The similar-to condition
	 */
	@NonNull SqlCondition similarTo(@NonNull String pattern);

	/**
	 * Creates a condition that accesses a JSON field by key.<br>
	 * Generates SQL: {@code column -> 'field'}.<br>
	 *
	 * @param field The JSON field name
	 * @return The json field condition
	 */
	@NonNull SqlCondition jsonField(@NonNull String field);

	/**
	 * Creates a condition that accesses a JSON value using a path expression.<br>
	 * Generates SQL: {@code column #>> 'path'}.<br>
	 *
	 * @param path The JSON path expression
	 * @return The json path condition
	 */
	@NonNull SqlCondition jsonPath(@NonNull String path);

	/**
	 * Creates a condition that checks if the array column contains the given element.<br>
	 * Generates SQL: {@code value = ANY(column)}.<br>
	 *
	 * @param element The element to check for
	 * @return The array contains condition
	 */
	@NonNull SqlCondition arrayContains(@NonNull T element);

	/**
	 * Creates a condition that checks if the array column overlaps with the given list of values.<br>
	 * Generates SQL: {@code column && ARRAY[values]}.<br>
	 *
	 * @param values The values to check for overlap
	 * @return The array overlaps condition
	 */
	@NonNull SqlCondition arrayOverlaps(@NonNull List<T> values);

	/**
	 * Creates a full-text search condition using PostgreSQL {@code @@} operator.<br>
	 * Generates SQL: {@code column @@ to_tsquery('query')}.<br>
	 *
	 * @param query The full-text search query
	 * @return The text search match condition
	 */
	@NonNull SqlCondition tsMatch(@NonNull String query);
}
