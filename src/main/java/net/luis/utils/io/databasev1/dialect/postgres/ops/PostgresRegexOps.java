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

package net.luis.utils.io.databasev1.dialect.postgres.ops;

import net.luis.utils.io.databasev1.condition.SqlCondition;
import net.luis.utils.io.databasev1.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing PostgreSQL-specific regular expression operations for column conditions.<br>
 * <p>
 *     These operations generate SQL conditions and expressions for PostgreSQL POSIX regular expressions,<br>
 *     including case-sensitive and case-insensitive matching, and regex-based replacement.
 * </p>
 *
 * @author Luis-St
 */
public interface PostgresRegexOps {
	
	/**
	 * Creates a condition that checks if the column matches the given regex pattern.<br>
	 * Generates SQL: {@code column ~ pattern}.<br>
	 *
	 * @param pattern The regex pattern
	 * @return The regex match condition
	 */
	@NonNull SqlCondition matches(@NonNull String pattern);
	
	/**
	 * Creates a condition that checks if the column matches the given regex pattern, ignoring case.<br>
	 * Generates SQL: {@code column ~* pattern}.<br>
	 *
	 * @param pattern The regex pattern
	 * @return The case-insensitive regex match condition
	 */
	@NonNull SqlCondition matchesIgnoreCase(@NonNull String pattern);
	
	/**
	 * Replaces all occurrences of a regex pattern in the column with the given replacement.<br>
	 * Generates SQL: {@code regexp_replace(column, pattern, replacement, 'g')}.<br>
	 *
	 * @param pattern The regex pattern to match
	 * @param replacement The replacement string
	 * @return The replaced expression
	 */
	@NonNull SqlExpression<String> replaceAll(@NonNull String pattern, @NonNull String replacement);
}
