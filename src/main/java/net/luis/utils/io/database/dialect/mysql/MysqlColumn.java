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

package net.luis.utils.io.database.dialect.mysql;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.mysql.operation.MysqlJsonOps;
import net.luis.utils.io.database.dialect.mysql.operation.MysqlStringOps;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a MySQL-specific column.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface MysqlColumn<T> extends SqlColumn<T> {

	@Override
	@NonNull MysqlStringOps string();

	/**
	 * Returns MySQL-specific JSON operations for this column.<br>
	 * @return JSON operations accessor
	 */
	@NonNull MysqlJsonOps json();

	/**
	 * Creates a fulltext search condition using {@code MATCH(...) AGAINST(...)}.<br>
	 * Generates SQL: {@code MATCH(column) AGAINST('searchTerms')}.<br>
	 *
	 * @param searchTerms The search terms to match against
	 * @return The fulltext match condition
	 */
	@NonNull SqlCondition matchAgainst(@NonNull String searchTerms);

	/**
	 * Creates a {@code GROUP_CONCAT} expression that concatenates grouped values with the given separator.<br>
	 * Generates SQL: {@code GROUP_CONCAT(column SEPARATOR 'separator')}.<br>
	 *
	 * @param separator The separator used between concatenated values
	 * @return The group concat expression
	 */
	@NonNull SqlExpression<?> groupConcat(@NonNull String separator);

	/**
	 * Creates an {@code IFNULL} expression that returns the column value or a default if null.<br>
	 * Generates SQL: {@code IFNULL(column, defaultValue)}.<br>
	 *
	 * @param defaultValue The default value to use when the column is null
	 * @return The ifnull expression
	 */
	@NonNull SqlExpression<?> ifNull(@NonNull T defaultValue);

	/**
	 * Creates a {@code FIND_IN_SET} expression that searches for a value in a comma-separated list.<br>
	 * Generates SQL: {@code FIND_IN_SET('value', column)}.<br>
	 *
	 * @param value The value to search for in the set
	 * @return The find-in-set expression
	 */
	@NonNull SqlExpression<?> findInSet(@NonNull String value);
}
