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
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.regex.Pattern;

/**
 * Interface representing a MySQL-specific column.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface MysqlColumn<T> extends SqlColumn<T> {
	
	/**
	 * Returns MySQL-specific JSON operations for this column.<br>
	 * @return JSON operations accessor
	 */
	@NonNull MysqlJsonOps json();
	
	/**
	 * Creates a condition that matches the column against a regular expression pattern.<br>
	 * Generates SQL: {@code column REGEXP 'pattern'}.<br>
	 *
	 * @param pattern The regular expression pattern to match
	 * @return The regexp condition
	 */
	@NonNull SqlCondition regexp(@NonNull Pattern pattern);
	
	/**
	 * Creates a fulltext search condition using {@code MATCH(...) AGAINST(...)}.<br>
	 * Generates SQL: {@code MATCH(column) AGAINST('searchTerms')}.<br>
	 *
	 * @param searchTerms The search terms to match against
	 * @return The fulltext match condition
	 */
	// Can be configured, this could be improved with a builder for the search terms to allow for more complex queries (e.g. boolean mode, natural language mode, etc.)
	@NonNull SqlCondition matchAgainst(@NonNull String searchTerms);
	
	/**
	 * Creates a {@code GROUP_CONCAT} expression that concatenates grouped values with the given separator.<br>
	 * Generates SQL: {@code GROUP_CONCAT(column SEPARATOR 'separator')}.<br>
	 *
	 * @param separator The separator used between concatenated values
	 * @return The group concat expression
	 */
	// Can be configured, this could be improved with a builder to allow for more complex group concat expressions (e.g. ordering, distinct values, etc.)
	@NonNull SqlExpression<?> groupConcat(@NonNull String separator);
	
	/**
	 * Creates a {@code FIND_IN_SET} expression that searches for a value in a comma-separated list.<br>
	 * Generates SQL: {@code FIND_IN_SET('value', column)}.<br>
	 *
	 * @param value The value to search for in the set
	 * @return The find-in-set expression
	 */
	@NonNull SqlExpression<?> findInSet(@NonNull String value);
}
