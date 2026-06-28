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

package net.luis.utils.io.database.expression.orderable;

import org.jspecify.annotations.NonNull;

/**
 * Represents something that can be turned into an ordered sql expression.<br>
 * Provides factory methods for the supported value and null orderings.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the value the expression evaluates to
 */
public interface SqlOrderable<T> {
	
	/**
	 * Creates an ordered expression that sorts in ascending order.<br>
	 * @return An ascending ordered expression
	 */
	@NonNull OrderedSqlExpression<T> ascending();
	
	/**
	 * Creates an ordered expression that sorts in descending order.<br>
	 * @return A descending ordered expression
	 */
	@NonNull OrderedSqlExpression<T> descending();
	
	/**
	 * Creates an ordered expression that sorts null values first.<br>
	 * @return An ordered expression with nulls ordered first
	 */
	@NonNull OrderedSqlExpression<T> nullsFirst();
	
	/**
	 * Creates an ordered expression that sorts null values last.<br>
	 * @return An ordered expression with nulls ordered last
	 */
	@NonNull OrderedSqlExpression<T> nullsLast();
}
