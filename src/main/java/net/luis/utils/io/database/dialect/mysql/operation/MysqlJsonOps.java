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

package net.luis.utils.io.database.dialect.mysql.operation;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.operation.SqlJsonOps;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing MySQL-specific JSON column operations.<br>
 *
 * @author Luis-St
 */
public interface MysqlJsonOps extends SqlJsonOps {

	/**
	 * Creates a condition that checks if the JSON column contains the given value.<br>
	 * Generates SQL: {@code JSON_CONTAINS(column, 'value')}.<br>
	 *
	 * @param value The JSON value to check for
	 * @return The json-contains condition
	 */
	@NonNull SqlCondition jsonContains(@NonNull String value);

	/**
	 * Extracts a value from the JSON column at the given path.<br>
	 * Generates SQL: {@code JSON_EXTRACT(column, 'path')}.<br>
	 *
	 * @param path The JSON path expression
	 * @return The extracted JSON value expression
	 */
	@NonNull SqlExpression<?> jsonExtract(@NonNull String path);
}
