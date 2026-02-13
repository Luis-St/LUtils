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

package net.luis.utils.io.database.function;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL regular expression functions.<br>
 *
 * @author Luis-St
 */
public class SqlRegex {
	
	/**
	 * Creates a condition that checks if the expression matches the given regex pattern.<br>
	 * Generates SQL: {@code expression ~ pattern} or dialect equivalent.<br>
	 *
	 * @param expr The expression to match
	 * @param pattern The regex pattern
	 * @return The regex match condition
	 */
	public static @NonNull SqlCondition matches(@NonNull SqlExpression<String> expr, @NonNull String pattern) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a condition that checks if the expression does not match the given regex pattern.<br>
	 * Generates SQL: {@code expression !~ pattern} or dialect equivalent.<br>
	 *
	 * @param expr The expression to match
	 * @param pattern The regex pattern
	 * @return The negated regex match condition
	 */
	public static @NonNull SqlCondition notMatches(@NonNull SqlExpression<String> expr, @NonNull String pattern) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a condition that checks if the expression matches the given regex pattern, ignoring case.<br>
	 * Generates SQL: {@code expression ~* pattern} or dialect equivalent.<br>
	 *
	 * @param expr The expression to match
	 * @param pattern The regex pattern
	 * @return The case-insensitive regex match condition
	 */
	public static @NonNull SqlCondition matchesIgnoreCase(@NonNull SqlExpression<String> expr, @NonNull String pattern) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Replaces all occurrences of a regex pattern in the expression with the given replacement.<br>
	 * Generates SQL: {@code REGEXP_REPLACE(expression, pattern, replacement)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to search in
	 * @param pattern The regex pattern to match
	 * @param replacement The replacement string
	 * @return The replaced expression
	 */
	public static @NonNull SqlExpression<String> replaceAll(@NonNull SqlExpression<String> expr, @NonNull String pattern, @NonNull String replacement) {
		throw new UnsupportedOperationException();
	}
}
