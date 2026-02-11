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
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.operation.SqlJsonOps;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing PostgreSQL-specific JSON column operations.<br>
 *
 * @author Luis-St
 */
public interface PostgresJsonOps extends SqlJsonOps {
	
	/**
	 * Creates a condition that checks if the JSON column contains another JSON value.<br>
	 * Generates SQL: {@code column @> value}.<br>
	 *
	 * @return The JSON containment condition
	 */
	@NonNull SqlCondition containsJson();
	
	/**
	 * Creates a condition that checks if the JSON column is contained by another JSON value.<br>
	 * Generates SQL: {@code column <@ value}.<br>
	 *
	 * @return The JSON contained-by condition
	 */
	@NonNull SqlCondition containedBy();
	
	/**
	 * Queries the JSON column using a path expression.<br>
	 * Generates SQL: {@code jsonb_path_query(column, 'path')}.<br>
	 *
	 * @param path The JSON path expression
	 * @return The JSON path query expression
	 */
	@NonNull SqlExpression<?> getPath(@NonNull String path);
}
